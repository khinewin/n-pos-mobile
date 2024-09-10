<template lang="">
    <div class="container-fluid">  
        <div v-if="isLoading">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary"  role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
        </div>  
         <div class="row" v-if="reports" v-for="sale in reports">
            <div class="col-12 mb-2">
                <div class="card">
                    <div class="card-body p-2">
                        <div class="row justify-content-end" >   
                            <div class="col-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">  
                                        <tr>
                                            <td class="col-4 pb-0 fw-bold text-end">Voucher no. <span class="">:</span></td>
                                            <td class="col-8  ps-2 pb-0" style="border-bottom: 1px dotted;">
                                                {{sale.voucher_no}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="pb-0 fw-bold text-end">Date <span class="">:</span></td>
                                            <td class=" ps-2 pb-0" style="border-bottom: 1px dotted;">
                                               <span>{{showTime(sale.sale_date)}}</span>
                                            </td>
                                        </tr>       
                                    </tbody>
                                </table>
                            </div>        
                            <div class="col-6">                    
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">                                     
                                        <tr>
                                            <td class="  col-4 pb-0 fw-bold text-end">Customer <span class="">:</span></td>
                                            <td class=" ps-2 col-8 pb-0" style="border-bottom: 1px dotted;">
                                                {{sale.customer.name}}
                                            </td>
                                        </tr>      
                                        <tr>
                                            <td class="pb-0 fw-bold text-end">Cashier <span class="">:</span></td>
                                            <td class=" ps-2 pb-0" style="border-bottom: 1px dotted;">
                                                {{sale.user.displayName}}
                                            </td>
                                        </tr>                                               
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-12">
                                <table class="table table-bordered table-hover border-dark mt-2 mb-0">
                                    <tbody class="">
                                        <tr class="text-center fw-bold">
                                            <td class="col-1">No.</td>
                                            <td class="col-2">Description</td>
                                            <td class="col-1 text-center">Packing size</td>                                                                                    
                                            <td class="col-1 text-center">Qty</td>
                                            <td class="col-1 text-center">Unit price</td>
                                            <td class="col-1 text-center">Amount</td>
                                            <td class="col-1 text-center">Discount</td>
                                            <td class="col-1 text-center">Final amount</td>
                                        </tr>
                                        <tr class="" v-if="sale.products.length > 0" v-for="(s, i) in sale.products" :key="s.id" >     
                                            <td class="text-center">{{i+1}}</td>                   
                                            <td class="">{{s.item_name}}</td>
                                            <td class="">
                                                {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                            </td>                                        
                                            <td class="text-center" >
                                                {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.sale_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                                            </td>                                            
                                            <td class="text-center">
                                                {{s.qty_sale_price_unit}}' {{s.qty_sale_price}}
                                            </td>                                         
                                           
                                            <td class=" text-end">{{s.amount}}</td>
                                            <td class=" text-end">{{s.discount}}</td>
                                            <td class=" text-end">{{s.final_amount}}</td>
                                           
                                        </tr>                        
                                    </tbody>
                                    <tbody>
                                        <tr>
                                            <td colspan="7" class="text-end pe-2 " style="border-bottom-style: hidden;">Total amount</td>
                                            <td class="text-end ">{{sale.total_amount}}</td>
                                        </tr>                     
                                        <tr>
                                            <td colspan="7" class="text-end  pe-2 " style="border-bottom-style: hidden;">Extra discount</td>
                                            <td class="text-end ">
                                                {{sale.extra_discount}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="7" class="text-end   pe-2 " style="border-bottom-style: hidden;">Net amount</td>
                                            <td class="text-end ">{{sale.net_amount}}</td>
                                        </tr>
                                        <tr >
                                            <td colspan="7" class="text-end   pe-2 " style="border-bottom-style: hidden;">Paid</td>
                                            <td class="text-end">
                                                {{sale.paid}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="7" class="text-end   pe-2 " style="border-bottom-style: hidden;" >Change</td>
                                            <td class="text-end ">
                                                {{sale.change}}
                                            </td>
                                        </tr> 
                                        <tr>
                                            <td colspan="7" class="text-end   pe-2 " >Credit</td>
                                            <td class="text-end ">
                                                {{sale.credit}}
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
    </div>
</template>
<script>
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/sales"
export default {
    props:['fromDate', 'toDate', 'voucher_no','customer_code'],   
    data(){
        return{                  
            reports:[],
            salesData:[],          
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
                this.fetchSales();  
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
                this.fetchSales();  
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
        displayPrice(l_unit, m_unit, s_unit){
            return hp.displayPrice(l_unit, m_unit, s_unit);
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        showTime(t){
            return hp.showTimeHour(t);
        },
        goBack(){
            this.$router.go(-1)
        },            
        async fetchSales(){
            const date=new Date();         
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)

           let query_url="";
           if(this.voucher_no){
             query_url = query(collection(db, "creditSales"), 
            where("voucher_no", "==", Number(this.voucher_no)));
            }else 
            if(this.customer_code){
                query_url = query(collection(db, "creditSales"), 
                where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
            }else{
                query_url = query(collection(db, "creditSales"), 
                where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
            }           
            onSnapshot(query_url, (snapShot)=>{
                let saleItems=ss.sales(snapShot);    
                saleItems=saleItems.filter((f)=>f.credit > 0)            
                this.reports=saleItems;
                this.isLoading=false;
            })
           
        }
    }
}
</script>
<style lang="">
    
</style>
