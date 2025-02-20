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
                                    <td class="text-end pb-0">Voucher No. :</td>
                                    <td class="pb-0" >
                                        {{p.voucher_no}}
                                    </td>                              
                                    <td class="text-end pb-0">Location :</td>
                                    <td class="pb-0"  >
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
                                                                
                                    <td class="text-end pb-0">Pur Date :</td>
                                    <td class="pb-0" v-if="p.pur_date" >
                                        {{showTime(p.pur_date)}}
                                    </td>                              
                                    <td class="text-end pb-0">Supplier :</td>
                                    <td class="pb-0" >
                                        {{p.supplier.name}} ({{p.supplier.company}})
                                    </td>
                                </tr>
                            </tbody>
                      </table>
                    </div>
                </div>      
                <div class="overflow-x-auto">           
                   <table class="table table-bordered mb-0 table-hover inside-table">
                        <tbody class="table-group-divider">
                            <tr class="small text-center fw-bold table-secondary">
                                <td class="col-1">Code</td>
                                <td class="col-2">Product Name</td>
                                <!--<td class="col-1">Exp. Date</td>-->
                                <td class="col-1">Packing Size</td>
                                <td class="col-1">Qty</td>
                                <td class="col-1">Unit Price</td>
                                <td class="col-1">Amount</td>
                                <td class="col-1">Discount</td>                                              
                                <td class="col-1">Unit Cost</td>
                            </tr>
                        </tbody>
                        <tbody>
                            <tr class="" v-if="p.products.length > 0" v-for="st in p.products" :key="st.id">
                                <td class="text-center">{{st.code_no}}</td>
                                <td class="">{{st.item_name}}</td>
                                <!--<td class="text-center">                                   
                                     <span v-if="st.exp_date">{{showTime(st.exp_date)}}</span>
                                </td>-->
                                <td class="col-1">
                                    {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                 </td>
                                <td class="text-end">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                </td>
                                <td class="text-center">
                                    {{st.qty_pur_price}}' {{st.qty_pur_price_unit}}
                                </td>                   
                                <td class="text-end">{{fM(st.amount)}}</td>
                                <td class="text-end">{{st.discount ? fM(st.discount) : 0}}</td>
                                <td class="text-end">{{fM(st.unit_cost)}}</td>                               
                            </tr>                                                    
                        </tbody>
                   </table>          
                   <table class="table table-bordered table-hover mb-0">
                    <tbody class="table-group-divider" >                      
                        <tr class="small">
                            <td  class="text-end col-3 pe-2 fw-bold">Total</td>
                            <td class="text-end col-3">{{fM(p.total)}}</td>
                            <td  class="text-end col-3 pe-2 fw-bold">Extra Discount</td>
                            <td class="text-end col-3">
                                {{fM(p.extra_discount)}}
                            </td>
                        </tr>                            
                        <tr class="small">
                            <td  class="text-end pe-2 fw-bold">Paid</td>
                            <td class="text-end">{{fM(p.paid)}}</td>
                            <td  class="text-end pe-2 fw-bold">Banalce</td>
                            <td class="text-end">{{fM(p.balance)}}</td>
                        </tr>
                        <tr>
                            <td  class="text-end pe-2 fw-bold">Remark</td>
                            <td colspan="3">{{p.remark}}</td>
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
                this.isLoading=true;
                this.fetchPurchase();  
            
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
        async fetchPurchase(){
            const date=new Date();         
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)
            
           let query_url="";
           const loc=this.location=="All" ? null : this.location;

           if(this.voucher_no.length>2){
             query_url = query(collection(db, "purchases"), 
            where("voucher_no", "==", this.voucher_no));
           }else if(this.supplier && loc !==null){
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
           onSnapshot(query_url, (snapShot)=>{
                const purchaseItems=ss.purchases(snapShot)
                this.reports=purchaseItems;
                this.isLoading=false;
           })
        }
    }
}
</script>
<style lang="">
    
</style>
