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
                        <div class="mb-2">
                            <table class="table mb-0 table-borderless">
                                <tbody class="small">
                                    <tr>
                                        <td class="text-end col-2 p-0 fw-bold">Voucher no. :</td>
                                        <td class="col-2 p-0 ps-2" style="border-bottom: 1px dotted;">
                                            {{adj.voucher_no}}
                                        </td>                                                             
                                
                                        <td class="text-end col-2 p-0 fw-bold">Date :</td>
                                        <td class="col-2 p-0  ps-2" style="border-bottom: 1px dotted;">
                                           <span v-if="adj.created_at"> {{showTime(adj.created_at)}}</span>
                                        </td>
                                    </tr>                               
                                </tbody>
                          </table>
                        </div>         
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">Before Adj</td>
                                    <td class="col-1">Adj type</td>
                                    <td class="col-1">Qty</td>      
                                    <td class="col-1">After Adj</td>   
                                    <td class="col-1">RTS/RFS</td>                          
                                    <td class="col-1">Remark</td>   
                                    <td class="col-1">Retail Amt</td>     
                                    <td class="col-1">WS Amt</td>     
                                    <td class="col-1">Pur Amt</td>     
                                </tr>
                                <tr class=""v-if="adj.products.length > 0" v-for="(st, i) in adj.products" :key="st.id">
                                    <td class="text-center">
                                        {{st.code_no}}                                           
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="text-center">                                      
                                        <span>{{st.before_adj_qty}}</span>
                                    </td>
                                    <td class="">
                                        {{st.adj_type}}
                                    </td>
                                    <td class="text-center">                                      
                                        <span>{{st.adj_qty}}</span>
                                    </td>
                                    <td class="text-center">                                      
                                        <span>{{st.after_adj_qty}}</span>
                                    </td>
                                        <td class="text-center">                                      
                                            <span>{{st.rts_rfs}}</span>
                                    </td>
                                    <td class="">                                      
                                        <span>{{st.remark}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.retail_amount}}</span>
                                    </td> 
                                    <td class="text-end">                                      
                                        <span>{{st.whole_sale_amount}}</span>
                                    </td>                                   
                                    <td class="text-end">                                      
                                        <span>{{st.pur_amount}}</span>
                                    </td>                                                   
                                </tr>
                                <tr class="small">
                                    <td colspan="8" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{adj.total_retail_amount}}</td>
                                    <td class="text-end">{{adj.total_whole_sale_amount}}</td>
                                    <td class="text-end">{{adj.total_pur_amount}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
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
            if(this.user !==null){
                this.isLoading=true;
                this.fetchadjs();  
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
                this.fetchadjs();  
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
            const snapShot=await getDocs(query_url)
            const adjItems=ss.adjustments(snapShot);                     
            this.reports=adjItems;
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
