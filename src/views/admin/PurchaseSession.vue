<template lang="">
        <div class="row g-2">         
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-danger">
                    <div class="card-body">
                            <div class="row">
                                <div class="col-3 text-center h1"><i class="fa-solid fa-basket-shopping"></i></div>
                                <div class="col-9">
                                    <div class="small fw-lighter mb-2">Purchase (Today)</div>
                                    <div class="card-text">{{purchase_today}} MMK</div>
                                </div>
                            </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-warning">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-3 text-center h1"><i class="fa-solid fa-basket-shopping"></i></div>
                            <div class="col-9">
                                <div class="small fw-lighter mb-2">Purchase (This week)</div>
                                <div class="card-text">{{purchase_this_week}} MMK</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-info">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-3 text-center h1"><i class="fa-solid fa-basket-shopping"></i></div>
                            <div class="col-9">
                                <div class="small fw-lighter mb-2">Purchase (This month)</div>
                                <div class="card-text">{{purchase_this_month}} MMK</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-md-12 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-success">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-3 text-center h1"><i class="fa-solid fa-basket-shopping"></i></div>
                            <div class="col-9">
                                <div class="small fw-lighter mb-2">Purchase (This year)</div>
                                <div class="card-text">{{purchase_this_year}} MMK</div>
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
    import * as ss from "@/lib/snapshot/purchases"
    import * as hp from "@/lib/helper"
    export default {
        components: {
            
        },
        data(){
            return{
                purchaseYearlyData:[],
                purchase_this_month: 0,
                purchase_this_week:0,
                purchase_this_year:0,
                purchase_today:0
         
            }
        },
        computed:{
            user(){
                return this.$store.getters.user ? this.$store.getters.user : null;
            }
        },
        mounted() {
        
        },
        created(){
            if(this.user !== null){
                this.fetchPurchaseYearly();
            }
        },
        watch:{
            user(data){
                if(data){
                    this.fetchPurchaseYearly();
                }
            }
        },
        methods:{
            showTime(t){
                return hp.showTime(t)
            },
            monthlyPurchaseReport(){
                // for this month sales
                this.purchaseYearlyData.map((res)=>{
                    this.purchase_this_year += res.total_amount;
                })
                //for totay sales
                const today=this.showTime(Timestamp.fromDate(new Date()))
                this.purchaseYearlyData.filter((f)=>{
                       const pur_date=this.showTime(f.pur_date)
                        if(pur_date === today){
                                this.purchase_today += f.total_amount;
                        }
                })
                // for this week
                function getCurrentWeekDates() {
                    const currentDate = new Date();
                    const firstDayOfWeek = new Date(currentDate);
                    const dayOfWeek = currentDate.getDay();
                    const diffToMonday = (dayOfWeek + 6) % 7; // 0 = Sunday, 1 = Monday, etc.
                    firstDayOfWeek.setDate(currentDate.getDate() - diffToMonday);
                    const lastDayOfWeek = new Date(firstDayOfWeek);
                    lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);
                    return {
                        firstDay: firstDayOfWeek,
                        lastDay: lastDayOfWeek
                    };
                }
                const w=getCurrentWeekDates();
                const first_day_week=Timestamp.fromDate(new Date(this.editShowTime(w.firstDay)))
                const last_day_week=Timestamp.fromDate(new Date(this.editShowTime(w.lastDay)))
                this.purchaseYearlyData.filter((f)=>{
                        if(f.pur_date >= first_day_week && f.pur_date <= last_day_week){
                                this.purchase_this_week += f.total_amount;
                        }
                })
                //for this month
                let date = new Date();
                let first_day = new Date(date.getFullYear(), date.getMonth(),1+1).toISOString().substr(0, 10);
                let last_day= new Date(date.getFullYear(), date.getMonth() + 1, 1).toISOString().substr(0, 10);
                const first_day_month=Timestamp.fromDate(new Date(first_day))
                const last_day_month=Timestamp.fromDate(new Date(last_day))
                this.purchaseYearlyData.filter((f)=>{
                        if(f.pur_date >= first_day_month && f.pur_date <= last_day_month){
                                this.purchase_this_month += f.total_amount;
                        }
                })
    
            },
           async fetchPurchaseYearly(){
                const currentDate = new Date();
                const theFirst = new Date(currentDate.getFullYear(), 0, 1);
                const theLast = new Date(currentDate.getFullYear(), 11, 31);
                const first_day_month=Timestamp.fromDate(new Date(theFirst))
                const last_day_month=Timestamp.fromDate(new Date(theLast))
                const query_url=query(collection(db, "purchases"),  
                where("pur_date", ">=", first_day_month), where("pur_date", "<=", last_day_month), orderBy("pur_date"));            
                const snapShot=await getDocs(query_url)
                let saleItems=ss.purchases(snapShot);           
                this.purchaseYearlyData=saleItems;
                this.monthlyPurchaseReport();
           },
           editShowTime(t){
                const formattedDate = new Date(t).toISOString().substr(0, 10)
                return formattedDate;
            },
        }
    }
    </script>