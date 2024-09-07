<template lang="">
    <div>
        <div class="card shadow">
            <div class="card-body">
                <div class="card-text">Purchase amount by month</div>
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
                        <div v-if="monthlyPurchase.length > 1">
                            <GChart
                            :settings="{ packages: ['corechart', 'table', 'map'] }"
                            type="PieChart"
                            :data="monthlyPurchase"
                            :options="monthlyChartOptions"
                        />
                        </div>
                        <div class="my-5 text-center" v-else >No data.</div>
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
                colors : ["purple", "brown","green", "orange","gray", "blue","orange",]
            }
        }
    },
    mounted() {
        if(this.user !==null){
               this.isLoading=true;
                this.fetchPurchaseMonthly();
            }
    },
    watch:{   
       user(data){
           if(data){
               this.isLoading=true;
               this.fetchPurchaseMonthly();
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
            let purchase=[]
            this.purchaseMonthlyData.map((res)=>{                  
                        purchase.push({...res, group_month : this.monthlyShowTime(res.pur_date)})
            })
            const gbm=Object.entries(hp.gb_month(purchase))             
            const data= gbm.map((res)=>{
                    const date=res[0];
                    let total_price=0;
                    res[1].map((r)=>{
                            total_price +=r.total_amount;
                    })
                   return [date, total_price]
            })          
            const title=["Month", "Purchase Amt"]
            data.unshift(title)
            this.monthlyPurchase=data;
            this.isLoading=false;
        },
        async fetchPurchaseMonthly(){
            const currentDate = new Date();
            const theFirst = new Date(currentDate.getFullYear(), 0, 1);
            const theLast = new Date(currentDate.getFullYear(), 11, 31);
            const from_date=Timestamp.fromDate(new Date(theFirst))
            const to_date=Timestamp.fromDate(new Date(theLast))
            const query_url= query(collection(db, "purchases"),  
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date"));            
            onSnapshot(query_url,(snapShot)=>{
                let purchaseItems=ss.purchases(snapShot);           
                this.purchaseMonthlyData=purchaseItems;
                this.monthlyReports();
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