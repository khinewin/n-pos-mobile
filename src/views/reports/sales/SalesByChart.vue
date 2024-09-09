<template>
  <div class="container-fluid">
    <div class="row my-2">
        <div class="col-12">
            <div class="card">
                <div class="card-body p-0">
                    <div class="ms-4 mt-2">Top 10 sales item</div>
                   <div class="row">
                    <div class="col-12">
                        <div v-if="isLoading">
                            <div colspan="5">
                                <div class="d-flex justify-content-center my-5">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            </div>
                        </div>   
                        <div v-if="topSalesQty.length >1" class="pe-5">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="ColumnChart"
                            :data="topSalesQty"
                            :options="qtyOptions"
                        />
                        </div>
                        <div class="my-5 text-center" v-else >No data.</div>
                    </div>
                    </div>
                   </div>
            </div>
        </div>
    </div>
        <div class="row my-2">
            <div class="col-12">
                <div class="card">
                    <div class="card-body p-0">
                    <div class="ms-4 mt-2">Sales with purchase amount and profit by Date</div>
                      <div class="row">
                        <div class="col-12">
                            <div v-if="isLoading">
                                <div colspan="5">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                    </div>
                                </div>
                            </div>   
                            <div v-if="products.length >1" class="pe-5">
                                <GChart
                                :settings="{ packages: ['corechart', 'table', 'map'] }"
                                type="ColumnChart"
                                :data="products"
                                :options="chartOptions"
                            />
                            </div>
                            <div class="my-5 text-center" v-else >No data.</div>
                        </div>
                        </div>
                      </div>
                </div>
            </div>
        </div>
        <div class="row g-2">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body p-0">
                        <div class="ms-4 mt-2">Sales with purchase amount and profit by Month</div>
                       <div class="row">
                        <div class="col-12">
                            <div v-if="isLoading">
                                <div colspan="5">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                    </div>
                                </div>
                            </div>   
                            <div v-if="monthlyProducts.length > 1" class="pe-5">
                                <GChart
                                :settings="{ packages: ['corechart', 'table', 'map'] }"
                                type="ColumnChart"
                                :data="monthlyProducts"
                                :options="monthlyChartOptions"
                            />
                            </div>
                            <div class="my-5 text-center" v-else >No data.</div>
                        </div>
                       </div>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body p-0">
                    <div class="ms-4 mt-2">Sales amount by Month</div>
                      <div class="row">
                        <div class="col-12">
                            <div v-if="isLoading">
                                <div colspan="5">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                    </div>
                                </div>
                            </div>   
                            <div v-if="monthlySalesProducts.length > 1">
                                <GChart
                                :settings="{ packages: ['corechart', 'table', 'map'] }"
                                type="PieChart"
                                :data="monthlySalesProducts"
                                :options="monthlySalesAmountOptions"

                            />
                            </div>
                            <div class="my-5 text-center" v-else >No data.</div>
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
import { GChart } from 'vue-google-charts'
import * as hp from "@/lib/helper"
import * as ss from '@/lib/snapshot/sales'
export default {
    props:['fromDate', 'toDate', 'customer_code'],
    components: {
    GChart
  },
    data(){
        return{           
            monthlyProducts:[],
            monthlySalesProducts:[],
            products:[],
            topSalesQty:[],
            salesMonthlyData:[],
            salesData:[],
            isLoading:false,   
            discount:0,
            qtyOptions: {
                colors : ["teal"],
                width: '100%'
            },        
            chartOptions: {              
                colors : ["orange", "blue","green", "brown"]
            },
            monthlyChartOptions: {              
                colors : ["orange", "blue","green"]
            },
            monthlySalesAmountOptions: {              
                colors : ["red", "orange","yellow", "green","teal","blue"]
            }
        }
    },
    mounted() {
        if(this.user !==null){
                this.isLoading=true;
                this.fetchSalesDaily();  
                this.fetchSalesMonthly();
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
               this.fetchSalesDaily();  
               this.fetchSalesMonthly();
           }
       }   
  },
    computed:{
        user(){
            return this.$store.getters.user;
        },
    },
    methods:{
        monthlyReports(){
            let sales=[]
            this.salesMonthlyData.map((res)=>{                  
                    res.products.map((data)=>{
                        let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                             sales.push({...data, group_month : this.monthlyShowTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: res.extra_discount})
                        }else{
                            sales.push({...data, group_month : this.monthlyShowTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: 0})
                        }
                    })
            })
            const gbm=Object.entries(hp.gb_month(sales))             
            const data= gbm.map((res)=>{
                    const date=res[0];
                    let sale_amount=0;
                    let pur_amount=0;
                    let profit=0;

                    res[1].map((r)=>{
                            sale_amount += r.final_amount
                            pur_amount +=r.pur_price.smallest_unit * r.sale_qty;
                            profit += (r.final_amount - (r.pur_price.smallest_unit * r.sale_qty)) - r.extra_discount;                         
                    })
                   return [date, pur_amount, sale_amount, profit]
            })
            const salesData= gbm.map((res)=>{
                    const date=res[0];
                    let sale_amount=0;
                    res[1].map((r)=>{
                        sale_amount += r.final_amount                     
                    })
                   return [date,sale_amount]
            })
            const title=["Month","Pur amount", "Sales amount", "Profit"]
            const salesTitle=["Month", "Sales amount"]
            data.unshift(title)
            salesData.unshift(salesTitle)
            this.monthlyProducts=data;
            this.monthlySalesProducts=salesData;
        },
        async fetchSalesMonthly(){
            const currentDate = new Date();
            const theFirst = new Date(currentDate.getFullYear(), 0, 1);
            const theLast = new Date(currentDate.getFullYear(), 11, 31);
            const from_date=Timestamp.fromDate(new Date(theFirst))
            const to_date=Timestamp.fromDate(new Date(theLast))
            let query_url=""
           if(this.customer_code){
            query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date"));
           }else{
             query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date"));
            }
            onSnapshot(query_url, (snapShot)=>{
                let saleItems=ss.sales(snapShot);           
                this.salesMonthlyData=saleItems;
                this.monthlyReports();
                this.isLoading=false;
            })
           
        },
        dailyReports(){
            let sales=[]
            this.salesData.map((res)=>{              
                    res.products.map((data)=>{
                        let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                             sales.push({...data, group_date : this.topQtyShowTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: res.extra_discount})
                        }else{
                            sales.push({...data, group_date : this.topQtyShowTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: 0})
                        }
                    })
            })
            const gbd=Object.entries(hp.gb_date(sales))
           const data= gbd.map((res)=>{
                    let item={
                        date : res[0],
                        sale_amount : 0,
                        pur_amount :0,
                        profit: 0,
                    }                            
                    res[1].map((r)=>{  
                            item.sale_amount += r.final_amount
                            item.pur_amount +=r.pur_price.smallest_unit * r.sale_qty;
                           item.profit += (r.final_amount - (r.pur_price.smallest_unit * r.sale_qty)) - r.extra_discount;  
                    })                    
                   return [item.date, item.pur_amount, item.sale_amount, item.profit]
            })
            const title=["Date", "Pur Amt", "Sales Amt", "Profit"]
            data.unshift(title)
            this.products=data;            
            //cal qty
            const gbc=Object.entries(hp.gb_code(sales))
            let result=[]
            gbc.map((res)=>{                
                   let data={item_name:"", sale_qty: 0}
                    res[1].map((ren)=>{
                            data.item_name=ren.item_name;
                            data.sale_qty += ren.packing_size.largest_unit ?  ren.sale_qty / (ren.packing_size.medium_qty * ren.packing_size.smallest_qty) : ren.sale_qty / ren.packing_size.smallest_qty;
                    })
                    result.push(data)
            })
            result.sort((a,b)=>a.sale_qty - b.sale_qty).reverse()
            const filterResult=result.filter((res, i)=>{
                if(i<10) return res;
            })
            const newResult=filterResult.map((res, i)=>{     
                    return [res.item_name, res.sale_qty]                          
            })            
            const qtyTitle=["Description", "Qty"]
           newResult.unshift(qtyTitle)
           // result.unshift(qtyTitle)
            this.topSalesQty=newResult;
            this.isLoading=false   
        },
        async fetchSalesDaily(){
            const date=new Date();         
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)
            
           let query_url=""
           if(this.customer_code){
            query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date"));
           }else{
             query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date"));
            }
            onSnapshot(query_url, (snapShot)=>{
                const saleItems=ss.sales(snapShot)
                this.salesData=saleItems;
                this.dailyReports();
                this.isLoading=false;
            })           
        },
        showTime(t){
            return hp.showTime(t)
        },
        topQtyShowTime(t){
            const date=t.toDate()
            const options = { year: '2-digit', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
        monthlyShowTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'short'};
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
    }
    
}
</script>
<style scoped>
   
</style>