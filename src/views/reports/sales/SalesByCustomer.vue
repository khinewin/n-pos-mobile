<template lang="">
    <div class="container-fluid">  
             
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="" v-if="reports.length > 0">                            
                            <tr v-for="r in reports" :key="r[0]">
                                <td class="col-1 text-center">{{r[0]}}</td>
                                <td colspan="5" class="p-0 col-11">
                                    <table class="table table-bordered mb-0 border-dark table-hover " style="border-style: hidden;">
                                        <thead>
                                            <tr class="fw-bold text-center">
                                                <td>Code</td>
                                                <td>Description</td>
                                                <td colspan="2">Qty</td>
                                                <td>Sale price</td>
                                                <td>Pur price</td>
                                                <td>Sale amount</td>
                                                <td>Pur amount</td>
                                                <td>Profit</td>
                                            </tr>
                                        </thead>
                                        <tbody>                                            
                                            <tr v-for="item in r[1]" :key="item.code_no">
                                                <td class="col-2 text-center">{{item.code_no}}</td>
                                                <td class="col-2">{{item.item_name}}</td>
                                                <td class="col-1 text-center">{{item.sale_qty}}</td>
                                                <td class="col-1 text-center">
                                                    {{Math.floor(item.sale_qty / item.item_per_package)}} x {{Math.floor(item.sale_qty % item.item_per_package)}}  
                                                </td>
                                                <td class="col-1 text-end">{{item.sale_price}}</td>
                                                <td class="col-1 text-end">{{item.pur_price}}</td>
                                                <td class="col-1 text-end">{{item.sale_amount}}</td>
                                                <td class="col-1 text-end">{{item.pur_amount}}</td>
                                                <td class="col-1 text-end">{{item.profit}}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="6" class="text-end fw-bold">Total</td>
                                                <td class="text-end">{{r[2].sale_amount}}</td>
                                                <td class="text-end">{{r[2].pur_amount}}</td>
                                                <td class="text-end">{{r[2].profit_amount}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Amount</td>
                                <td class="text-start">{{total_amount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Discount</td>
                                <td class="text-start">{{total_discount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total amount</td>
                                <td class="text-start">{{total_charges}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Gross profit</td>
                                <td class="text-start">{{total_profit}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total pay</td>
                                <td class="text-start">{{total_pay}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total refunds</td>
                                <td class="text-start">{{total_refunds}}</td>
                            </tr>
                        </tbody>
                    </table>
                    <div v-if="isLoading">
                        <div colspan="5">
                            <div class="d-flex justify-content-center my-5">
                                <div class="spinner-border text-primary"  role="status">
                                  <span class="visually-hidden">Loading...</span>
                                </div>
                            </div>
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
import * as ss from "@/lib/snapshot/sales"
export default {
    props:['fromDate', 'toDate', 'customer_code'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            salesData:[],
            total_amount:0,
            total_discount:0,
            total_charges:0,
            total_pur_amount:0,
            total_profit:0,
            total_pay:0,
            total_refunds:0,
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
        goBack(){
            this.$router.go(-1)
        },
       
        displayReports(){       
            let sales=[]
            this.salesData.map((res)=>{
                    this.total_amount += res.total_amount
                    this.total_discount +=res.discount
                    this.total_charges += res.total_amount - res.discount
                    this.total_pay += res.pay;
                    this.total_refunds += res.refunds
                    res.products.map((data)=>{
                        sales.push({
                            ...data,
                            group_customer:  `${res.customer.name}(${res.customer.phone})`,
                            customer_code: res.customer.code,
                        })
                    })
            })
            sales.sort((a,b)=>Number(a.customer_code) - Number(b.customer_code))
            const gbd=Object.entries(hp.gb_customer(sales))
            let total_profit=0;
            let total_pur_amount=0;
           const gbc=gbd.map((res)=>{
            const res1=Object.entries(hp.gb_item_name_sale_price(res[1]))
                const res2=res1.map((res)=>{
                let item={
                    item_name: res[0],
                    sale_qty : 0,
                    code_no:"",
                    sale_price:0,
                    pur_price:0,
                    sale_amount:0,
                    pur_amount:0,
                    profit: 0,
                    item_per_package:0
                }                
                res[1].map((ren)=>{
                    item.item_per_package=ren.item_per_package;
                    item.sale_qty += ren.sale_qty;
                    item.code_no=ren.code_no;
                    item.sale_price=ren.sale_price;
                    item.pur_price=ren.pur_price;
                    item.pur_amount +=ren.pur_price * ren.sale_qty;
                    item.sale_amount += ren.sale_amount;
                    item.profit += ren.sale_amount - (ren.pur_price * ren.sale_qty);
                })
                return item;
           }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)
           let sale_amount=0;
                let pur_amount=0;
                let profit_amount=0;
                res2.map((res)=>{
                    sale_amount += res.sale_amount;
                    pur_amount += res.pur_amount;
                    profit_amount += res.sale_amount - res.pur_amount;
                })
                total_profit += profit_amount;
                total_pur_amount += pur_amount;
                const res4={sale_amount, pur_amount, profit_amount}

                const newData=[res[0],  [...res2],res4]               
                return newData;                             
           })           
           this.total_pur_amount=total_pur_amount;
           this.total_profit=total_profit;
           this.reports=gbc;
        
        },   
        async fetchSales(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            let query_url=""
            if(this.customer_code){
                query_url = query(collection(db, "sales"), 
                where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
            }else{
                 query_url = query(collection(db, "sales"), 
                 where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
            }
            const snapShot=await getDocs(query_url)
            const saleItems=ss.sales(snapShot);           
            this.salesData=saleItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
