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
         <div class="" v-if="reports">
            <div class="mb-2"  v-for="adj in reports" :key="adj.id">
                <div class="card">
                    <div class="card-body p-2">
                        <div class="row g-2 mb-2">
                            <div class="col-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">
                                        <tr>
                                            <td class="text-end col-4 p-0 fw-bold">Remark :</td>
                                            <td class="col-8 p-0 ps-2">
                                                {{adj.remark}}
                                            </td>                                                             
                                        </tr>
                                                                     
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">
                                        <tr>
                                            <td class="text-end col-4 p-0 fw-bold">Voucher No. :</td>
                                            <td class="col-8 p-0 ps-2">
                                                {{adj.voucher_no}}
                                            </td>                                                             
                                        </tr>
                                        <tr>
                                            <td class="text-end  p-0 fw-bold">Date :</td>
                                            <td class=" p-0  ps-2">
                                               <span v-if="adj.created_at"> {{showTime(adj.created_at)}}</span>
                                            </td>
                                        </tr>                               
                                    </tbody>
                              </table>
                            </div>
                        </div>         
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">Packing Size</td>
                                    <td class="col-1">Before Adj</td>
                                    <td class="col-1">Adj type</td>
                                    <td class="col-1">Qty</td>      
                                    <td class="col-1">After Adj</td>   
                                    <td class="col-1">RTS/RFS</td>                          
                                    <td class="col-1">Remark</td>                                      
                                </tr>
                                <tr class=""v-if="adj.products.length > 0" v-for="(st, i) in adj.products" :key="st.id" >
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
                                                                                                                 
                            </tbody>
                        </table>                                     
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
import * as ss from "@/lib/snapshot/adjustments"
export default {
    props:['fromDate', 'toDate', 'voucher_no'],
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
                this.isLoading=true;
                this.fetchadjs();  
            
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
        fM(amount){
            return hp.formattedAmount(amount)
        },
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
        async fetchadjs(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            let query_url="";
            if(this.voucher_no){
                query_url = query(collection(db, "adjustments"),  where("voucher_no", "==", Number(this.voucher_no)))
            }else{
             query_url = query(collection(db, "adjustments"),  
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            }
            onSnapshot(query_url, (snapShot)=>{
                const items=ss.adjustments(snapShot)    
                this.reports=items;
                this.isLoading=false;
           })
        }
    }
}
</script>
<style lang="">
    
</style>
