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
                                    <td  class="col-2">Description</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1 " v-if="location=='Main' || location=='All'">Main Qty</td>         
                                    <td class="col-1" v-if="location=='Shop' || location=='All'">Shop Qty</td>                          
                                    <td class="col-1 ">RT price</td>
                                    <td class="col-1 ">WS price</td>
                                    <td class="col-1" v-if="location=='Main' || location=='All'">Main RT Amt</td>                                    
                                    <td class="col-1" v-if="location=='Main' || location=='All'">Main WS Am</td>                                    
                                    <td class="col-1" v-if="location=='Shop' || location=='All'">Shop RT Amt</td>                                    
                                    <td class="col-1" v-if="location=='Shop' || location=='All'">Shop WS Am</td>      
                                </tr>
                                <tr class=""v-if="stock.products.length > 0" v-for="(st, i) in stock.products" :key="st.id">
                                    <td class="text-center">                                       
                                            {{st.code_no}}                                                                                                          
                                    </td>                                   
                                    <td class="">{{st.item_name}}</td>
                                    <td class="">                 
                                        1{{st.parent_unit}} * {{st.item_per_package}}{{st.child_unit}}
                                    </td>
                                    <td class="text-end" v-if="location=='Main' || location=='All'">
                                        <span v-if="calQtyL(st.main_qty, st.item_per_package) > 0">{{calQtyL(st.main_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                         <span v-if="calQtyL(st.main_qty, st.item_per_package) >0 && calQtyS(st.main_qty, st.item_per_package) > 0">, </span>
                                         <span v-if="calQtyS(st.main_qty, st.item_per_package)> 0">{{calQtyS(st.main_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                     </td>
                                    <td class="text-end" v-if="location=='Shop' || location=='All'">
                                       <span v-if="calQtyL(st.qty, st.item_per_package) > 0">{{calQtyL(st.qty, st.item_per_package)}}{{st.parent_unit}}</span>
                                        <span v-if="calQtyL(st.qty, st.item_per_package) >0 && calQtyS(st.qty, st.item_per_package) > 0">, </span>
                                        <span v-if="calQtyS(st.qty, st.item_per_package)> 0">{{calQtyS(st.qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                    </td>                            
                                    <td class="text-end">{{st.retail_price}}</td>
                                    <td class="text-end">{{st.whole_sale_price}}</td>
                                    <td class="text-end" v-if="location=='Main' || location=='All'">{{st.main_retail_amount}}</td>
                                    <td class="text-end" v-if="location=='Main' || location=='All'">{{st.main_whole_sale_amount}}</td>
                                    <td class="text-end" v-if="location=='Shop' || location=='All'">{{st.retail_amount}}</td>
                                    <td class="text-end" v-if="location=='Shop' || location=='All'">{{st.whole_sale_amount}}</td>                                                
                                </tr>
                                <tr class="small">
                                    <td :colspan="[location== 'All' ? 7 : 4]" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{stock.total_main_retail_amount}}</td>
                                    <td class="text-end">{{stock.total_main_whole_sale_amount}}</td>
                                    <td class="text-end">{{stock.total_main_retail_amount}}</td>
                                    <td class="text-end">{{stock.total_main_whole_sale_amount}}</td>
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
    props:['fromDate', 'toDate', 'voucher_no', 'location'],
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
        calQtyL(qty, ipp){
            return hp.calQtyL(qty, ipp);
        },
        calQtyS(qty, ipp){
            return hp.calQtyS(qty, ipp)
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
