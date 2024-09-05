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
         <div class="row" v-if="reports" v-for="p in reports">
            <div class="mb-2 col-12">
                <div class="card">
                    <div class="card-body p-2">
                    <div class="row  mb-2">
                    <div class="col-lg-6">
                        <table class="table mb-0 table-borderless">
                            <tbody>
                                <tr class="">
                                    <td class="text-end pb-0">Voucher no. :</td>
                                    <td class="pb-0" style="border-bottom: 1px dotted;">
                                        {{p.voucher_no}}
                                    </td>                              
                                    <td class="text-end pb-0">Location :</td>
                                    <td class="pb-0"  style="border-bottom: 1px dotted;">
                                        {{p.location}}
                                    </td>                            
                                   
                                </tr>
                            </tbody>
                      </table>
                    </div>
                    <div class="col-lg-6">
                        <table class="table mb-0 table-borderless">
                            <tbody>
                                <tr class="">
                                                                
                                    <td class="text-end pb-0">Pur date :</td>
                                    <td class="pb-0" v-if="p.pur_date" style="border-bottom: 1px dotted;">
                                        {{showTime(p.pur_date)}}
                                    </td>                              
                                    <td class="text-end pb-0">Supplier :</td>
                                    <td class="pb-0" style="border-bottom: 1px dotted;">
                                        {{p.supplier.name}} ({{p.supplier.company}})
                                    </td>
                                </tr>
                            </tbody>
                      </table>
                    </div>
                </div>                 
                   <table class="table table-bordered mb-0 table-hover">
                        <tbody>
                            <tr class="small text-center fw-bold table-secondary">
                                <td class="col-1">Code</td>
                                <td class="col-2">Description</td>
                                <td class="col-1">Exp. date</td>
                                <td class="col-1">Packing size</td>
                                <td class="col-1">Qty</td>
                                <td class="col-1">Unit price</td>
                                <td class="col-1">Amount</td>
                                <td class="col-1">Discount</td>                                              
                                <td class="col-1">Price</td>
                            </tr>
                        </tbody>
                        <tbody>
                            <tr class="small text-center" v-if="p.products.length > 0" v-for="st in p.products" :key="st.id">
                                <td class="">{{st.code_no}}</td>
                                <td class="">{{st.item_name}}</td>
                                <td class="">                                   
                                     <span v-if="st.exp_date">{{showTime(st.exp_date)}}</span>
                                </td>
                                <td class="col-1">
                                    {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                 </td>
                                <td class="text-end">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                </td>
                                <td class="text-center">
                                    {{st.qty_pur_price}}' {{st.qty_pur_price_unit}}
                                </td>                   
                                <td class="text-end">{{st.amount}}</td>
                                <td class="text-end">{{st.discount}}</td>
                                <td class="text-end">{{st.price}}</td>                               
                            </tr>
                            <tr class="small">
                                <td colspan="8" class="text-end">Total amount</td>
                                <td class="text-end">{{p.total_amount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="8" class="text-end">Total discount</td>
                                <td class="text-end ">{{p.total_discount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="8" class="text-end">Extra discount</td>
                                <td class="text-end">{{p.extra_discount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="8" class="text-end">Total price</td>
                                <td class="text-end">{{p.total_price}}</td>
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
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/purchases"
export default {
    props:['fromDate', 'toDate', 'voucher_no', 'supplier', 'location'],
    components: {
    },
  
    data(){
        return{                 
            reports:[],
            purchaseData:[],
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
                this.fetchPurchase();  
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
                this.fetchPurchase();  
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
        async fetchPurchase(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           let query_url="";
           const loc=this.location=="All" ? null : this.location;

           if(this.voucher_no){
             query_url = query(collection(db, "purchases"), 
            where("voucher_no", "==", Number(this.voucher_no)));
           } if(this.supplier && loc !==null){
            query_url = query(collection(db, "purchases"),
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
            where('supplier.code',"==", Number(this.supplier)), where("location","==", loc),
             orderBy("pur_date", "desc"));                                         
            }else if(this.supplier){
                query_url = query(collection(db, "purchases"),
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
                where('supplier.code',"==", Number(this.supplier)),
                orderBy("pur_date", "desc"));             
            }else if(loc !==null){
                query_url = query(collection(db, "purchases"),
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
                where("location","==", loc),
                orderBy("pur_date", "desc"));        
            }else{
             query_url = query(collection(db, "purchases"), 
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date", "desc"));
           }           
            const snapShot=await getDocs(query_url)
            const purchaseItems=ss.purchases(snapShot);
           
            this.reports=purchaseItems;
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
