<template lang="">
    <div class="">
        <table class="table table-bordered mb-0 border-dark table-hover">
            <tbody class="" >    
                    <tr class="fw-bold text-center">
                        <td class="col-1">Code</td>
                        <td class="col-2">Description</td>
                        <td class="col-1">Packing size</td>
                        <td class="col-1">Before Adj</td>
                        <td class="col-1">Adj type</td>
                        <td class="col-1">Qty</td>      
                        <td class="col-1">After Adj</td>   
                        <td class="col-1">RTS/RFS</td>                          
                        <td class="col-1">Remark</td>   
                        
                    </tr>  
                </tbody>  
                <tbody  v-if="reports.length > 0">                    
                    <tr v-for="st in reports" :key="st.code_no">
                        <td class="text-center">
                            {{st.code_no}}                                           
                        </td>
                        <td class="">{{st.item_name}}</td>
                        <td class="">
                            {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                        </td>
                        <td class=" text-end">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.before_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-center">
                          {{st.adj_type}}
                        </td>
                        <td class="text-center" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>     
                        <td class=" text-end">
                            {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                        </td>      
                        <td class="text-center">
                            {{st.rts_rfs}}
                        </td>                                                         
                        <td class="">
                            {{st.remark}}
                        </td>
                    </tr>
                    <tr class="text-center fw-bold">
                        <td colspan="6"></td>
                        <td>Pur amount</td>
                        <td>Retail amount</td>
                        <td>WS amount</td>
                    </tr>
                    <tr>
                        <td colspan="6" class="text-end fw-bold">Total</td>
                        <td class="text-end">{{total_retail_amount}}</td>
                        <td class="text-end">{{total_whole_sale_amount}}</td>
                        <td class="text-end">{{total_pur_amount}}</td>
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
import {Timestamp,onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/adjustments"

export default {
    props:['fromDate', 'toDate', 'code_no',  'adj_type', 'rts_rfs'],
    components: {
    },
  
    data(){
        return{                
            reports:[],
            adjustmentsData:[],
            total_pur_amount:0,
            total_retail_amount:0,
            total_whole_sale_amount:0,
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
                this.fetchAdjustments();  
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
                this.fetchAdjustments();  
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
            this.adjustmentsData.map((res)=>{
                    res.products.map((data)=>{
                        stocks.push(data)
                    })
            })          
            if(this.code_no){
                stocks=stocks.filter((f)=>f.code_no === Number(this.code_no))
            }
            stocks=stocks.filter((f)=>{
                 if(this.adj_type) {
                  return  f.adj_type== this.adj_type
                }
                return f;
            })
            stocks=stocks.filter((f)=>{
                 if(this.rts_rfs) {
                  return  f.rts_rfs== this.rts_rfs
                }
                return f;
            }).sort((a,b)=>a.code_no - b.code_no).reverse();                    
                     
               stocks.map((ren)=>{
                        this.total_retail_amount += ren.retail_price.smallest_unit * ren.adj_qty;
                         this.total_whole_sale_amount += ren.whole_sale_price.smallest_unit * ren.adj_qty
                         this.total_pur_amount += ren.pur_price.smallest_unit * ren.adj_qty
               })
              this.reports=[...stocks];        
        },      
        async fetchAdjustments(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            const query_url = query(collection(db, "adjustments"),
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
           onSnapshot(query_url, (snapShot)=>{
                const items=ss.adjustments(snapShot)    
                this.adjustmentsData=items;
                this.displayReports();
                this.isLoading=false;
           })
        }
    }
}
</script>
<style lang="">
    
</style>
