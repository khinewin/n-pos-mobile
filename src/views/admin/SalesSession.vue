<template lang="">
        <div class="row g-2">           
            <div class=" col-sm-12 col-lg-6">
                <div class="card shadow border-0 border-start border-5 border-primary">
                    <div class="card-body p-2">
                            <div class="small">{{t('sales_amount')}}</div>
                            <div class="row">
                                <div class="col-2 text-center h1">
                                    <i class="fa-solid fa-chart-line"></i>
                                </div>
                                <div class="col-5">
                                    <div class="small fw-lighter mb-1 text-center">{{t('today')}}</div>
                                    <div class="card-text text-center small">                                     
                                        <span v-if="salesYearlyData.length"> {{fM(sales_today)}}</span>
                                        <span v-else @click="fetchSalesYearly" class="change-cursor"><i class="fa-solid fa-rotate-right"></i></span>
                                    </div>
                                </div>
                                <div class="col-5">
                                    <div class="small fw-lighter mb-1 text-center">{{t('this_week')}}</div>
                                    <div class="card-text text-center small">     
                                        <span v-if="salesYearlyData.length">{{fM(sales_this_week)}} </span>
                                        <span v-else @click="fetchSalesYearly" class="change-cursor"><i class="fa-solid fa-rotate-right"></i></span>                       
                                    </div>
                                </div>
                            </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-12 col-lg-6">
                <div class="card shadow border-0 border-start border-5 border-success">
                    <div class="card-body p-2">
                            <div class="small">{{t('sales_amount')}}</div>
                            <div class="row">
                                <div class="col-2 text-center h1">
                                    <i class="fa-solid fa-chart-line"></i>
                                </div>
                                <div class="col-5">
                                    <div class="small fw-lighter mb-1 text-center">{{t('this_week')}}</div>
                                    <div class="card-text text-center small">                                     
                                        <span v-if="salesYearlyData.length"> {{fM(sales_this_month)}}</span>
                                        <span v-else @click="fetchSalesYearly" class="change-cursor"><i class="fa-solid fa-rotate-right"></i></span>
                                     </div>
                                </div>
                                <div class="col-5">
                                    <div class="small fw-lighter mb-1 text-center">{{t('this_year')}}</div>
                                    <div class="card-text text-center small">     
                                        <span v-if="salesYearlyData.length">{{fM(sales_this_year)}}</span> 
                                        <span v-else @click="fetchSalesYearly" class="change-cursor"><i class="fa-solid fa-rotate-right"></i></span>                           
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
    import * as ss from "@/lib/snapshot/sales"
    import * as hp from "@/lib/helper"
    import {lang} from '@/lib/language'

    export default {
        components: {
            
        },
        data(){
            return{
                salesYearlyData:[],
                sales_this_year: 0,
                sales_this_week:0,
                sales_this_month:0,
                sales_today:0,
                isLoading:false,
            }
        },
        computed:{
            user(){
                return this.$store.getters.user ? this.$store.getters.user : null;
            },
            t() {
                return (key) => this.$store.getters.t(key);
                },
        },
        mounted() {
            //this.isLoading=true;
           // this.fetchSalesYearly();
        },
     
      
        methods:{
            fM(amount){
                return hp.formattedAmountCurrency(amount)
            },
            showTime(t){
                return hp.showTime(t)
            },
            yearlyReports(){
                // for this year sales
                this.salesYearlyData.map((res)=>{
                    this.sales_this_year += res.total_amount;
                })
                //for totay sales
                let startToday=new Date();
                startToday.setHours("0", "0", "0")
                let endToday=new Date();
                endToday.setHours("23", "59", "59")
                const start_today=Timestamp.fromDate(startToday)
                const end_today=Timestamp.fromDate(endToday)
                this.salesYearlyData.filter((f)=>{                 
                       const sale_date=f.sale_date;
                        if(sale_date >= start_today && sale_date <= end_today){
                                this.sales_today += f.total_amount;
                     }                                
                })
                // for this week
                function getCurrentWeekDates() {
                    const currentDate = new Date();
                    const firstDayOfWeek = new Date(currentDate);
                    const dayOfWeek = currentDate.getDay();
                    const diffToMonday = (dayOfWeek + 6) % 7; // 0 = Sunday, 1 = Monday, etc.
                    firstDayOfWeek.setDate(currentDate.getDate() - diffToMonday);
                    firstDayOfWeek.setHours("0", "0", "0")
                    const lastDayOfWeek = new Date(firstDayOfWeek);
                    lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);
                    lastDayOfWeek.setHours("23", "59", "59")
                    return {
                        firstDay: firstDayOfWeek,
                        lastDay: lastDayOfWeek
                    };
                }
                const w=getCurrentWeekDates();              
                
                this.salesYearlyData.filter((f)=>{             
                        const sd=f.sale_date.toDate();
                        if(sd >= w.firstDay && sd <= w.lastDay){
                            this.sales_this_week += f.total_amount;
                        }
                    
                })
                //for this month
                let date = new Date();
                let first_day = new Date(date.getFullYear(), date.getMonth(),1+1).toISOString().substr(0, 10);
                let fDay=new Date(first_day)
                fDay.setHours("0", "0", "0")
                let last_day= new Date(date.getFullYear(), date.getMonth() + 1, 1).toISOString().substr(0, 10);
                let lDay=new Date(last_day)
                lDay.setHours("23", "59", "59")
                const first_day_month=Timestamp.fromDate(new Date(fDay))
                const last_day_month=Timestamp.fromDate(new Date(last_day))

                this.salesYearlyData.filter((f)=>{
                        if(f.sale_date >= first_day_month && f.sale_date <= last_day_month){
                                this.sales_this_month += f.total_amount;
                        }
                })
            },
           async fetchSalesYearly(){
                this.isLoading=true;
                const currentDate = new Date();
                const theFirst = new Date(currentDate.getFullYear(), 0, 1);
                const theLast = new Date(currentDate.getFullYear(), 11, 31);
                theLast.setHours("23", "59", "59")
                const first_day_month=Timestamp.fromDate(new Date(theFirst))
                const last_day_month=Timestamp.fromDate(new Date(theLast))
                const query_url=query(collection(db, "sales"),  
                where("sale_date", ">=", first_day_month), where("sale_date", "<=", last_day_month), orderBy("sale_date"));            
                onSnapshot(query_url,(snapShot)=>{
                    let saleItems=ss.sales(snapShot);           
                    this.salesYearlyData=saleItems;
                    this.yearlyReports();
                })               
           },
           editShowTime(t){
                const formattedDate = new Date(t).toISOString().substr(0, 10)
                return formattedDate;
            },
        }
    }
    </script>