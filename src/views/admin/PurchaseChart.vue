<template lang="">
    <div>
        <div class="card shadow">
            <div class="card-body">
                <div class="card-text">{{t('pur_amount_by_month')}} ({{currentMonth}})</div>
                <div class="row">
                    <div class="col-12">
                        <div v-if="isLoading">
                            <div>
                                <div class="d-flex justify-content-center my-5">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            </div>
                        </div>   
                        <div v-if="monthlyPurchase.length > 1" id="chart">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="PieChart"
                            :data="monthlyPurchase"
                            :options="monthlyChartOptions"
                            @ready="onChartReady"
                        />
                        </div>
                        <div class="my-5 text-center" v-if="monthlyPurchase.length <=1 && !isLoading" >
                            <span  @click="fetchPurchaseMonthly" class="change-cursor"><i class="fa-solid fa-rotate-right"></i></span>
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
import * as ss from '@/lib/snapshot/purchases'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import {lang} from '@/lib/language'

export default {
    components:{
        GChart
    },
    data(){
        return{
            isLoading:false,
            purchaseMonthlyData:[],
            monthlyPurchase:[],
            monthlyChartOptions: {              
                 colors : [
                "#003333", "#004d4d", "#006666", "#007f7f", "#009999", 
                "#00b2b2", "#00cccc", "#1ad1d1", "#33d6d6", "#4ddada",
                "#66e0e0", "#80e5e5", "#99ebeb", "#b3f0f0", "#ccf5f5",
                "#e6fafa", "#cce6e6", "#99cccc", "#66b3b3", "#339999",
                "#008080", "#009999", "#00b3b3", "#00cccc", "#00e6e6",
                "#33f0f0", "#66fafa", "#99ffff", "#b3ffff", "#ccffff"
                ],

                is3D: true,
                responsive:true,
            }
        }
    },
    mounted() {
      //  this.isLoading=true;
        this.fetchPurchaseMonthly();
            
    },
  
    computed:{
        user(){
            return this.$store.getters.user;
        },
         t() {
             return (key) => this.$store.getters.t(key);
        },
        currentMonth() {
            const date = new Date();
            const engMonths = [
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            ];
            const mmMonths = [
                "ဇန်နဝါရီ", "ဖေဖော်ဝါရီ", "မတ်", "ဧပြီ", "မေ", "ဇွန်",
                "ဇူလိုင်", "ဩဂုတ်", "စက်တင်ဘာ", "အောက်တိုဘာ", "နိုဝင်ဘာ", "ဒီဇင်ဘာ"
            ];

            const monthIndex = date.getMonth(); // 0-11
             const sl=this.$store.getters.currentLang;
             if(sl=="eng"){
                return engMonths[monthIndex]
             }else{
                return  mmMonths[monthIndex]
             }           
        }
    },
    methods:{
        onChartReady(){
            this.isLoading=false;
        },
        monthlyReports(){
            let purchase=[]
            this.purchaseMonthlyData.map((res)=>{                  
                        purchase.push({...res, group_month : this.monthlyShowTime(res.pur_date), group_date: this.showTime(res.pur_date)})
            })
            const gbm=Object.entries(hp.gb_date(purchase))             
            const data= gbm.map((res)=>{
                    const date=res[0];
                    let total_price=0;
                    res[1].map((r)=>{
                            total_price +=r.total_amount;
                    })
                   return [date, total_price]
            })          
            const title=["Date", "Purchase Amt"]
            data.unshift(title)
            this.monthlyPurchase=data;
           // this.isLoading=false;
        },
        async fetchPurchaseMonthly(){
            this.isLoading=true;
            const currentDate = new Date();
            // First day of month at 00:00:00.000
            const theFirst = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1, 0, 0, 0, 0);

            // Last day of month at 23:59:59.999
            const theLast = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0, 23, 59, 59, 999);
            const from_date=Timestamp.fromDate(new Date(theFirst))
            const to_date=Timestamp.fromDate(new Date(theLast))
            const query_url= query(collection(db, "purchases"),  
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date"));            
            onSnapshot(query_url,(snapShot)=>{
                let purchaseItems=ss.purchases(snapShot);      
                this.purchaseMonthlyData=purchaseItems;
                this.monthlyReports();
                if(purchaseItems.length<=0){
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
        showTime(t){
            return hp.showTime(t);
        }
    }
}
</script>
<style>
    #chart {
  width: 100%; /* Make sure the chart container takes full width */
}
</style>