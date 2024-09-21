<template lang="">
    <div class="">  
        <div v-if="isLoading">
            <div colspan="5">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary"  role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        </div>  
         <div class="row" v-if="reports" v-for="stock in reports">
            <div class="col-12 mb-2">
                <div class="card">
                    <div class="card-body p-2">
                        <div class="row g-2 mb-2">
                            <div class="col-6">
                                <table class="table mb-0 table-borderless ">
                                    <tbody class="small">
                                        <tr>
                                            <td class="text-end col-4 fw-bold">From Location :</td>
                                            <td class="col-8  ps-2">
                                                {{from_location}}
                                            </td>                                                            
                                        </tr>                                     
                                        <tr>
                                            <td class="text-end   fw-bold">To Location :</td>
                                            <td class="ps-2 ">
                                               <span > {{to_location}}</span>
                                            </td>                                                                                                        
                                        </tr>                                                             
                                    </tbody>
                              </table>
                            </div>   
                            <div class="col-6">
                                <table class="table mb-0 table-borderless ">
                                    <tbody class="small">
                                        <tr>                                           
                                            <td class="text-end col-4   fw-bold">Date :</td>
                                            <td class="ps-2 col-8">
                                               <span v-if="stock.created_at"> {{showTime(stock.created_at)}}</span>
                                            </td>                                                        
                                        </tr>
                                        <tr>
                                            <td class="text-end fw-bold">Voucher No. :</td>
                                            <td class="ps-2">
                                                {{stock.voucher_no}}
                                            </td>
                                        </tr>            
                                        <tr>
                                            <td class="text-end fw-bold">Remark :</td>
                                            <td class="ps-2">
                                                {{stock.remark}}
                                            </td>
                                        </tr>                                                              
                                    </tbody>
                              </table>
                            </div>      
                        </div>   
                        <div class="overflow-x-auto">
                        <table class="table table-bordered  table-hover mb-0 inside-table">
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">Packing Size</td>
                                    <td class="col-1">From Loc Bal</td> 
                                    <td class="col-1">To Loc Bal</td> 
                                    <td class="col-1">Transfer Bal</td>
                                    <td class="col-1">After From Loc Bal</td> 
                                    <td class="col-1">After To Loc Bal</td>                    
                                </tr>
                                <tr class=""v-if="stock.products.length > 0" v-for="(st, i) in stock.products" :key="st.id">
                                    <td class="text-center">                                       
                                            {{st.code_no}}                                                                                                          
                                    </td>    
                                    <td>{{st.item_name}}</td>                               
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
                        </div>                               
                    </div>
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
    props:['fromDate', 'toDate', 'voucher_no', 'from_location', 'to_location'],
    components: {
    },  
    data(){
        return{                 
            reports:[],
            salesData:[],
            grand_total:"",
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
        showTime(t){
            return hp.showTime(t)
        },
        async fetchStocks(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            let query_url="";
            if(this.voucher_no){
                query_url = query(collection(db, "transfers"),  where("voucher_no", "==", Number(this.voucher_no)))
            }else{
             query_url = query(collection(db, "transfers"),  
            where("created_at", ">=", from_date), where("created_at", "<=", to_date),
            where("from_location", "==", this.from_location), where("to_location", "==", this.to_location), 
            orderBy("created_at", "desc"));
            }
            onSnapshot(query_url, (snapShot)=>{
                const items=ss.transfers(snapShot);           
                this.reports=items;
                this.isLoading=false;
            })
          
        }
    }
}
</script>
<style lang="">
    
</style>
