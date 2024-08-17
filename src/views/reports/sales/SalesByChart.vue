<template>
  <div class="container-fluid">
    <div class="row my-2">
        <div class="col-12">
            <div class="card">
                <div class="card-header text-dark">Top 20 sales</div>
                <div class="card-body">
                    <div v-if="isLoading">
                        <div colspan="5">
                            <div class="d-flex justify-content-center my-5">
                                <div class="spinner-border text-primary"  role="status">
                                  <span class="visually-hidden">Loading...</span>
                                </div>
                            </div>
                        </div>
                    </div>   
                    <div v-if="topSalesQty.length >0">
                        <GChart
                        :settings="{ packages: ['corechart', 'table', 'map'] }"
                        type="ColumnChart"
                        :data="topSalesQty"
                        :options="qtyOptions"
                    />
                    </div>
                </div>
            </div>
        </div>
    </div>
        <div class="row my-2">
            <div class="col-12">
                <div class="card">
                    <div class="card-header text-dark">Purchase , sales and profit amount by Date</div>
                    <div class="card-body">
                        <div v-if="isLoading">
                            <div colspan="5">
                                <div class="d-flex justify-content-center my-5">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            </div>
                        </div>   
                        <div v-if="products.length >0">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="ColumnChart"
                            :data="products"
                            :options="chartOptions"
                        />
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row gx-2">
            <div class="col-lg-6">
                <div class="card">
                    <div class="card-header">Purchase, sales and profit amount by Month</div>
                    <div class="card-body">
                        <div v-if="isLoading">
                            <div colspan="5">
                                <div class="d-flex justify-content-center my-5">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            </div>
                        </div>   
                        <div v-if="monthlyProducts.length > 0">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="ColumnChart"
                            :data="monthlyProducts"
                            :options="monthlyChartOptions"
                        />
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="card">
                    <div class="card-header">Sales amount by Month</div>
                    <div class="card-body">
                        <div v-if="isLoading">
                            <div colspan="5">
                                <div class="d-flex justify-content-center my-5">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            </div>
                        </div>   
                        <div v-if="monthlySalesProducts.length > 0">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="PieChart"
                            :data="monthlySalesProducts"
                            :options="monthlyChartOptions"
                        />
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
import { GChart } from 'vue-google-charts'
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
            qtyOptions: {              
                colors : ["teal"]
            },        
            chartOptions: {              
                colors : ["orange", "blue","green"]
            },
            monthlyChartOptions: {              
                colors : ["orange", "blue","green"]
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
                        sales.push({...data, month : this.monthlyShowTime(res.sale_date)})
                    })
            })
            const gbm=Object.entries(Object.groupBy(sales, res=>{
                return res.month
            }))             
            const data= gbm.map((res)=>{
                    const date=res[0];
                    let sale_amount=0;
                    let pur_amount=0;
                    let profit=0;
                    res[1].map((r)=>{
                            sale_amount += r.sale_amount
                            pur_amount +=r.pur_amount;
                            profit += r.sale_amount - r.pur_amount; 
                        
                    })
                   return [date, pur_amount, sale_amount, profit]
            })
            const salesData= gbm.map((res)=>{
                    const date=res[0];
                    let sale_amount=0;
                    res[1].map((r)=>{
                        sale_amount += r.sale_amount                     
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
            query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date"));
           }else{
             query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date"));
            }
            const shapShot=await getDocs(query_url)
            let saleItems=[];
            shapShot.forEach((doc) => {         
                let sale = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    sale_date : doc.data().sale_date,
                    products : doc.data().products,    
                    charges: doc.data().charges,
                    pay:doc.data().pay,
                    discount: doc.data().discount,
                    refunds: doc.data().refunds,
                    total_amount:doc.data().total_amount,
                    sale_type: doc.data().sale_type,
                    customer : doc.data().customer,
                };               
                                
                saleItems.push(sale);      
            });
            this.salesMonthlyData=saleItems;
            this.monthlyReports();
            this.isLoading=false;
        },
        dailyReports(){
            let sales=[]
            this.salesData.map((res)=>{                  
                    res.products.map((data)=>{
                        sales.push({...data, date_group : this.topQtyShowTime(res.sale_date)})
                    })
            })
            const gbd=Object.entries(Object.groupBy(sales, res=>{
                return res.date_group
            }))
            //cal by date
           const data= gbd.map((res)=>{
                    const date=res[0];
                    let sale_amount=0;
                    let pur_amount=0;
                    let profit=0;               
                    res[1].map((r)=>{                     
                            sale_amount += r.sale_amount
                            pur_amount +=r.pur_amount;
                            profit += r.sale_amount - r.pur_amount;                       
                    })
                   return [date, pur_amount, sale_amount, profit]
            })
            const title=["Date", "Pur amount", "Sales amount", "Profit"]
            data.unshift(title)
            this.products=data;            
            //cal qty
            const gbc=Object.entries(Object.groupBy(sales, res=>{
                return res.code_no
            }))
            let result=[]
            gbc.map((res)=>{
                    const data=res[1].reduce((accu, cur)=>{
                            return {
                                    item_name : accu.item_name,
                                    sale_qty : accu.sale_qty + cur.sale_qty
                            }
                    })  
                    result.push(data)
                  //  return [data.item_name, data.sale_qty]
            })
            result.sort((a,b)=>a.sale_qty - b.sale_qty).reverse()
            const filterResult=result.filter((res, i)=>{
                if(i<20) return res;
            })
          //  console.log(filterRerult)
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
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           // console.log(from_date, to_date)
           let query_url=""
           if(this.customer_code){
            query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date"));
           }else{
             query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date"));
            }
            const shapShot=await getDocs(query_url)
            let saleItems=[];
            shapShot.forEach((doc) => {         
                let sale = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    sale_date : doc.data().sale_date,
                    products : doc.data().products,    
                    charges: doc.data().charges,
                    pay:doc.data().pay,
                    discount: doc.data().discount,
                    refunds: doc.data().refunds,
                    total_amount:doc.data().total_amount,
                    sale_type: doc.data().sale_type,
                    customer : doc.data().customer,
                };               
                                
                saleItems.push(sale);      
            });
        this.salesData=saleItems;
        this.dailyReports();
        this.isLoading=false;
        },
        showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
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
<style lang="">
    
</style>