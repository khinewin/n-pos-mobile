<template lang="">
    <div class="container-fluid">  
        <div v-if="isLoading">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary"  role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
        </div>  
         <div class="row" v-if="reports" v-for="stock in reports">
            <div class="col-12 mb-2">
                <div class="card">
                    <div class="card-body p-2">
                        <div class="mb-2">
                            <table class="table mb-2  table-borderless">
                                <tbody class="small">
                                    <tr>
                                        <td class="text-end col-2 p-0 fw-bold">Voucher no. :</td>
                                        <td class="col-2 p-0 ps-2">
                                            {{stock.voucher_no}}
                                        </td>                                                             
                                
                                        <td class="text-end col-2 p-0 fw-bold">Date :</td>
                                        <td class="col-2 p-0  ps-2">
                                           <span v-if="stock.created_at"> {{showTime(stock.created_at)}}</span>
                                        </td>
                                    </tr>                               
                                </tbody>
                          </table>
                        </div>         
                        <table class="table table-bordered mb-0  table-hover">
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td  class="col-2">Description</td>
                                    <td class="col-1">Packing Size</td>
                                    <td class="col-1 " v-if="location=='Main' || location=='All'">Main Inv Bal</td>         
                                    <td class="col-1" v-if="location=='Shop' || location=='All'">Shop Inv Bal</td>                          
                                    <td class="col-1">Pur Price</td>
                                    <td class="col-1">Retail Price</td>
                                    <td class="col-1">Wholesale Price</td>  
                                </tr>
                                <tr class=""v-if="stock.products.length > 0" v-for="(p, i) in stock.products" :key="p.id">
                                    <td class="text-center">                                       
                                            {{p.code_no}}                                                                                                          
                                    </td>                                   
                                    <td class="">{{p.item_name}}</td>
                                    <td class="">
                                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                    </td>
                                    <td class="text-end"  v-if="location =='All' || location=='Main'">
                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.main_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end" v-if="location =='All' || location=='Pharmacy'">
                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end" >
                                            {{displayPrice(p.pur_price.largest_unit, p.pur_price.medium_unit, p.pur_price.smallest_unit)}}
                                    </td>
                                    <td class="text-end">
                                             {{displayPrice(p.retail_price.largest_unit, p.retail_price.medium_unit, p.retail_price.smallest_unit)}}
                                    </td>
                                    <td class="text-end">
                                            {{displayPrice(p.whole_sale_price.largest_unit, p.whole_sale_price.medium_unit, p.whole_sale_price.smallest_unit)}}
                                    </td>                                   
                                </tr>    
                                <tr>
                                    <td :colspan="[location=='All' ? 6 : 5]" class="text-end fw-bold pe-2">Total Amount (Ks)</td>
                                    <td class="text-end ">{{fM(stock.retail_amount)}}</td>
                                    <td class="text-end ">{{fM(stock.whole_sale_amount)}}</td>
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
import * as ss from "@/lib/snapshot/stocks"
export default {
    props:['fromDate', 'toDate', 'voucher_no', 'location', 'code_no'],
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
                this.fetchStocks();  
            
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
            return hp.formattedAmount(amount);
        },
        displayPrice(l_unit, m_unit, s_unit){
            return hp.displayPrice(l_unit, m_unit, s_unit);
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
        async fetchStocks(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            let query_url="";
            if(this.voucher_no){
                query_url = query(collection(db, "stocks"),  where("voucher_no", "==", Number(this.voucher_no)))
            }else{
             query_url = query(collection(db, "stocks"),  
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            }
            onSnapshot(query_url, (snapShot)=>{ //all on snapshot
                let stockItems=ss.stocks(snapShot);                          
                if(this.code_no){            
                    stockItems=stockItems.map((st)=>{
                        const data=st.products.filter((f)=>{
                            return f.code_no==this.code_no
                        })
                        return {...st, products : data}
                    })
                }
                stockItems=stockItems.map((i)=>{
                    let retail_amount=0;
                    let whole_sale_amount=0;
                    i.products.map((rep)=>{
                        retail_amount +=this.location=="All" ? (rep.qty + rep.main_qty) * rep.retail_price.smallest_unit : this.location=="Main" ? rep.main_qty * rep.retail_price.smallest_unit : rep.qty * rep.retail_price.smallest_unit ;
                        whole_sale_amount += this.location=="All" ? rep.whole_sale_price.smallest_unit * (rep.qty + rep.main_qty) : this.location=="Main" ? rep.whole_sale_price.smallest_unit *rep.main_qty : rep.whole_sale_price.smallest_unit *rep.qty;

                    })
                    return {...i, retail_amount: retail_amount, whole_sale_amount: whole_sale_amount}
                })           
                this.reports=stockItems; 
                this.isLoading=false;
            })//all on snapshot
        }
    }
}
</script>
<style lang="">
    
</style>
