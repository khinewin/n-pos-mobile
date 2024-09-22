<template lang="">
    <div class="overflow-x-auto">            
        <table class="table table-bordered border-dark table-hover inside-table">
            <tbody class="" v-if="reports.length > 0">    
                <tr class="fw-bold text-center">
                    <td class="col-1">Code</td>
                    <td class="col-2">Product Name</td>
                    <td class="col-1">Packing Size</td>
                    <td class="col-1">From Loc Bal</td> 
                    <td class="col-1">To Loc Bal</td> 
                    <td class="col-1">Transfer Bal</td>
                    <td class="col-1">After From Loc Bal</td> 
                    <td class="col-1">After To Loc Bal</td>                                         
                </tr>                        
                    <tr v-for="st in reports" :key="st.code_no">
                        <td class="text-center">{{st.code_no}}</td>
                        <td class="">{{st.item_name}}</td>
                        <td class="">
                            {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                        </td>
                        <td class="text-end">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.from_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-end">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.to_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-center" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.transfer_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-end">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_from_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-end">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_to_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>  
                    </tr>
               
            </tbody>
        </table>
        <div v-if="isLoading">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary"  role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/transfers"

export default {
    props:['fromDate', 'toDate', 'from_location', 'to_location', 'code_no'],
    components: {
    },
  
    data(){
        return{                
            reports:[],
            transfersData:[],
            total_pur_amount:0,
            total_retail_amount:0,
            total_whole_sale_amount:0,
            total_main_retail_amount:0,
            total_main_whole_sale_amount:0,
            isLoading:false,
            msg : {
                type :"",
                info :"",
            },       
        }
    },
   
    mounted() {    
            if(this.user !==null){
                this.isLoading=true;
                this.fetchStocks();  
            }
            this.displayFromDate=Timestamp.fromDate(new Date(this.fromDate))
            this.displayToDate=Timestamp.fromDate(new Date(this.toDate))
            if(this.reportType="by_date"){
                this.displayReportType="by Date"
            }
    },
   watch:{
   
    msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                        this.msg.info="";
                    }, 5000)
                }
            },deep:true
        },
        user(data){
            if(data){
                this.isLoading=true;
                this.fetchStocks();  
            }
        }   
   },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        report_type(){
            return this.$store.getters.reportType;
        },      
    },
    methods:{
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        goBack(){
            this.$router.go(-1)
        },
       
        displayReports(){       
            let stocks=[]
            this.transfersData.map((res)=>{
                    res.products.map((data)=>{
                        stocks.push(data)
                    })
            })          
            if(this.code_no){
                stocks=stocks.filter((f)=>f.code_no === Number(this.code_no))
            }                          
            stocks=stocks.sort((a,b)=>a.code_no - b.code_no).reverse();
            this.reports=stocks;        
        },      
        async fetchStocks(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            const query_url = query(collection(db, "transfers"),
            where("created_at", ">=", from_date), where("created_at", "<=", to_date),
            where("from_location", "==", this.from_location), where("to_location", "==", this.to_location), 
            orderBy("created_at", "desc"));
            onSnapshot(query_url, (snapShot)=>{
                const items=ss.transfers(snapShot);           
                this.transfersData=items;
                this.displayReports();
                this.isLoading=false;
            })
        }
    }
}
</script>
<style lang="">
    
</style>
