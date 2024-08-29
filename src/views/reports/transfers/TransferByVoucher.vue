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
                        <div class="mb-2">
                            <table class="table mb-0 table-borderless ">
                                <tbody class="small">
                                    <tr>
                                        <td class="text-end p-0 fw-bold">From location :</td>
                                        <td class="col-2 p-0 ps-2" style="border-bottom: 1px dotted;">
                                            {{from_location}}
                                        </td>                                                             
                                
                                        <td class="text-end p-0 fw-bold">To location :</td>
                                        <td class="col-2 p-0  ps-2" style="border-bottom: 1px dotted;">
                                           <span > {{to_location}}</span>
                                        </td>
                                        <td class="text-end p-0 fw-bold">Voucher no. :</td>
                                        <td class="col-2 p-0 ps-2" style="border-bottom: 1px dotted;">
                                            {{stock.voucher_no}}
                                        </td>                                                             
                                
                                        <td class="text-end  p-0 fw-bold">Date :</td>
                                        <td class="col-2 p-0  ps-2" style="border-bottom: 1px dotted;">
                                           <span v-if="stock.created_at"> {{showTime(stock.created_at)}}</span>
                                        </td>
                                    </tr>                               
                                </tbody>
                          </table>
                        </div>         
                        <table class="table table-bordered  table-hover mb-0">
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">Packing size</td>
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
                                    <td class="">{{st.item_name}}</td>
                                    <td class="">                 
                                        1{{st.parent_unit}} * {{st.item_per_package}}{{st.child_unit}}
                                    </td>
                                    <td class="text-end">
                                        <span  v-if="from_location=='Main'">
                                         <span v-if="calQtyL(st.main_qty, st.item_per_package) > 0">{{calQtyL(st.main_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                         <span v-if="calQtyL(st.main_qty, st.item_per_package) >0 && calQtyS(st.main_qty, st.item_per_package) > 0">, </span>
                                         <span v-if="calQtyS(st.main_qty, st.item_per_package)> 0">{{calQtyS(st.main_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                        </span>
                                        <span v-if="from_location=='Shop'">
                                         <span v-if="calQtyL(st.qty, st.item_per_package) > 0">{{calQtyL(st.qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                         <span v-if="calQtyL(st.qty, st.item_per_package) >0 && calQtyS(st.qty, st.item_per_package) > 0">, </span>
                                         <span v-if="calQtyS(st.qty, st.item_per_package)> 0">{{calQtyS(st.qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                        </span>
                                      </td>
                                      <td class="text-end">
                                         <span  v-if="to_location=='Main'">
                                          <span v-if="calQtyL(st.main_qty, st.item_per_package) > 0">{{calQtyL(st.main_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                          <span v-if="calQtyL(st.main_qty, st.item_per_package) >0 && calQtyS(st.main_qty, st.item_per_package) > 0">, </span>
                                          <span v-if="calQtyS(st.main_qty, st.item_per_package)> 0">{{calQtyS(st.main_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                         </span>
                                         <span v-if="to_location=='Shop'">
                                          <span v-if="calQtyL(st.qty, st.item_per_package) > 0">{{calQtyL(st.qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                          <span v-if="calQtyL(st.qty, st.item_per_package) >0 && calQtyS(st.qty, st.item_per_package) > 0">, </span>
                                          <span v-if="calQtyS(st.qty, st.item_per_package)> 0">{{calQtyS(st.qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                         </span>
                                       </td>
                                       <td class="text-end">
                                         <span v-if="calQtyL(st.transfer_qty, st.item_per_package) > 0">{{calQtyL(st.transfer_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                         <span v-if="calQtyL(st.transfer_qty, st.item_per_package) >0 && calQtyS(st.transfer_qty, st.item_per_package) > 0">, </span>
                                         <span v-if="calQtyS(st.transfer_qty, st.item_per_package)> 0">{{calQtyS(st.transfer_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                       </td>
                                       <td class="text-end">
                                         <span  v-if="from_location=='Main'">
                                          <span v-if="calQtyL(st.main_qty -st.transfer_qty, st.item_per_package) > 0">{{calQtyL(st.main_qty-st.transfer_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                          <span v-if="calQtyL(st.main_qty -st.transfer_qty, st.item_per_package) >0 && calQtyS(st.main_qty-st.transfer_qty, st.item_per_package) > 0">, </span>
                                          <span v-if="calQtyS(st.main_qty-st.transfer_qty, st.item_per_package)> 0">{{calQtyS(st.main_qty-st.transfer_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                         </span>
                                         <span v-if="from_location=='Shop'">
                                          <span v-if="calQtyL(st.qty-st.transfer_qty, st.item_per_package) > 0">{{calQtyL(st.qty-st.transfer_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                          <span v-if="calQtyL(st.qty-st.transfer_qty, st.item_per_package) >0 && calQtyS(st.qty-st.transfer_qty, st.item_per_package) > 0">, </span>
                                          <span v-if="calQtyS(st.qty-st.transfer_qty, st.item_per_package)> 0">{{calQtyS(st.qty-st.transfer_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                         </span>
                                       </td>
                                       <td class="text-end">
                                         <span  v-if="to_location=='Main'">
                                          <span v-if="calQtyL(st.main_qty +st.transfer_qty, st.item_per_package) > 0">{{calQtyL(st.main_qty+st.transfer_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                          <span v-if="calQtyL(st.main_qty +st.transfer_qty, st.item_per_package) >0 && calQtyS(st.main_qty+st.transfer_qty, st.item_per_package) > 0">, </span>
                                          <span v-if="calQtyS(st.main_qty+st.transfer_qty, st.item_per_package)> 0">{{calQtyS(st.main_qty+st.transfer_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                         </span>
                                         <span v-if="to_location=='Shop'">
                                          <span v-if="calQtyL(st.qty+st.transfer_qty, st.item_per_package) > 0">{{calQtyL(st.qty+st.transfer_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                          <span v-if="calQtyL(st.qty+st.transfer_qty, st.item_per_package) >0 && calQtyS(st.qty+st.transfer_qty, st.item_per_package) > 0">, </span>
                                          <span v-if="calQtyS(st.qty+st.transfer_qty, st.item_per_package)> 0">{{calQtyS(st.qty+st.transfer_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                         </span>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/stocks"
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
                query_url = query(collection(db, "transfers"),  where("voucher_no", "==", Number(this.voucher_no)))
            }else{
             query_url = query(collection(db, "transfers"),  
            where("created_at", ">=", from_date), where("created_at", "<=", to_date),
            where("from_location", "==", this.from_location), where("to_location", "==", this.to_location), 
            orderBy("created_at", "desc"));
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
