<template lang="">
    <div>
        <div class="card shadow">
            <div class="card-body">
                <div class="card-text">Sales Amount By Month</div>
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
                        <div v-if="monthlySales.length>1">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="ColumnChart"
                            :data="monthlySales"
                            :options="monthlyChartOptions"
                            @ready="onChartReady"
                        />
                        </div>
                        <div class="my-5 text-center" v-if="monthlySales.length <=1 && isLoading==false">
                            <span @click="fetchSalesMonthly" class="change-cursor"><i class="fa-solid fa-rotate-right"></i></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { GChart } from 'vue-google-charts'
import * as hp from "@/lib/helper"
import * as ss from '@/lib/snapshot/sales'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
export default {
    components:{
        GChart
    },
    data(){
        return{
            isLoading:false,
            salesMonthlyData:[],
            monthlySales:[],
            monthlyChartOptions: {              
                colors : ["blue"],
                responsive:true,
            }
        }
    },
    mounted() {
       // this.isLoading=true;
       //  this.fetchSalesMonthly();
            
    },
   
    computed:{
        user(){
            return this.$store.getters.user;
        },
    },
    methods:{
        onChartReady() {
      // Once the chart is ready, hide the loading message
        this.isLoading = false;
        },
        monthlyReports(){
            let sales=[]
            this.salesMonthlyData.map((res)=>{                  
                        sales.push({...res, group_month : this.monthlyShowTime(res.sale_date)})
            })
            const gbm=Object.entries(hp.gb_month(sales))             
            const data= gbm.map((res)=>{
                    const date=res[0];
                    let total_amount=0;
                    res[1].map((r)=>{
                            total_amount += r.total_amount
                    })
                   return [date, total_amount]
            })          
            const title=["Month","Sales Amt"]
            data.unshift(title)
            this.monthlySales=data;
           // this.isLoading=false;
        },
        async fetchSalesMonthly(){
            this.isLoading=true;
            const currentDate = new Date();
            const theFirst = new Date(currentDate.getFullYear(), 0, 1);
            const theLast = new Date(currentDate.getFullYear(), 11, 31);
            theLast.setHours("23", "59", "59")
            const from_date=Timestamp.fromDate(new Date(theFirst))
            const to_date=Timestamp.fromDate(new Date(theLast))

            const query_url= query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date"));            
            onSnapshot(query_url,(snapShot)=>{
                let saleItems=ss.sales(snapShot);        
               this.salesMonthlyData=saleItems;
              this.monthlyReports();
              if(saleItems.length<=0){
                    this.isLoading=false;
              }
               
            })
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