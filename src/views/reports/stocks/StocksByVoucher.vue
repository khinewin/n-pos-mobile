<template lang="">
    <div class="container-fluid">  
        <div v-if="isLoading">
            <div colspan="5">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary"  role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        </div>  
         <div class="row mb-2" v-if="reports" v-for="stock in reports">
            <div class="col-12 mb-2">
                <div class="card shadow">
                    <div class="card-body">
                        <div class="mb-2">
                            <table class="table mb-2 table-borderless">
                                <tbody class="small">
                                    <tr>
                                        <td class="text-end col-2 p-0 fw-bold">Voucher no. :</td>
                                        <td class="col-2 p-0 ps-2" style="border-bottom: 1px dotted;">
                                            {{stock.voucher_no}}
                                        </td>                                                             
                                
                                        <td class="text-end col-2 p-0 fw-bold">Date :</td>
                                        <td class="col-2 p-0  ps-2" style="border-bottom: 1px dotted;">
                                           <span v-if="stock.created_at"> {{showTime(stock.created_at)}}</span>
                                        </td>
                                    </tr>                               
                                </tbody>
                          </table>
                        </div>         
                        <table class="table table-bordered  table-hover">
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Desc</td>
                                    <td class="col-1" colspan="2">Qty</td>                                   
                                    <td class="col-1">Retail price</td>
                                    <td class="col-1">Wholesale price</td>
                                    <td class="col-1">Pur price</td>
                                    <td class="col-1">Retail amount</td>                                    
                                    <td class="col-1">Wholesale amount</td>
                                    <td class="col-1">Pur amount</td>
                                </tr>
                                <tr class=""v-if="stock.products.length > 0" v-for="(st, i) in stock.products" :key="st.id">
                                    <td class="text-center">                                       
                                            {{st.code_no}}                                                                                                          
                                    </td>                                   
                                    <td class="">{{st.item_name}}</td>
                                    <td class="text-center">                                      
                                        <span>{{st.qty}}</span>
                                    </td>
                                    <td class="text-center">
                                        {{Math.floor(st.qty / st.item_per_package)}} x   {{Math.floor(st.qty % st.item_per_package)}}
                                    </td>                                
                                    <td class="text-end">                                      
                                        <span>{{st.retail_price}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.whole_sale_price}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.pur_price}}</span>
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
                                    <td colspan="7" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{stock.total_retail_amount}}</td>
                                    <td class="text-end">{{stock.total_whole_sale_amount}}</td>
                                    <td class="text-end">{{stock.total_pur_amount}}</td>
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
import * as ss from "@/lib/snapshot/stocks"
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
                query_url = query(collection(db, "openingStocks"),  where("voucher_no", "==", Number(this.voucher_no)))
            }else{
             query_url = query(collection(db, "openingStocks"),  
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            }
            const snapShot=await getDocs(query_url)
            const stockItems=ss.stocks(snapShot);                     
            this.reports=stockItems;
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
