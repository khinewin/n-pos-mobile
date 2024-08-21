<template lang="">
        <div class="row g-2 mb-2">           
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-danger">
                    <div class="card-body">
                            <div class="row">
                                <div class="col-3 text-center h1"><i class="fa-solid fa-cart-plus"></i></div>
                                <div class="col-9">
                                    <div class="small fw-lighter mb-2">Sales (Today)</div>
                                    <div class="card-text">                                     
                                        {{sales_today}} MMK
                                    </div>
                                </div>
                            </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-warning">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-3 text-center h1"><i class="fa-solid fa-cart-plus"></i></div>
                            <div class="col-9">
                                <div class="small fw-lighter mb-2">Sales (This week)</div>
                                <div class="card-text">{{sales_this_week}} MMK</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-md-4 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-info">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-3 text-center h1"><i class="fa-solid fa-cart-plus"></i></div>
                            <div class="col-9">
                                <div class="small fw-lighter mb-2">Sales (This month)</div>
                                <div class="card-text">{{sales_this_month}} MMK</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 col-md-12 col-lg-3">
                <div class="card shadow border-0 border-bottom border-3 border-success">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-3 text-center h1"><i class="fa-solid fa-cart-plus"></i></div>
                            <div class="col-9">
                                <div class="small fw-lighter mb-2">Sales (This year)</div>
                                <div class="card-text">{{sales_this_year}} MMK</div>
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
    import * as ss from "@/lib/snapshot/sales"
    import * as hp from "@/lib/helper"
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
            }
        },
        mounted() {
        
        },
        created(){
            if(this.user !== null){
                this.isLoading=true;
                this.fetchSalesYearly();
            }
        },
        watch:{
            user(data){
                if(data){
                    this.isLoading=true;
                    this.fetchSalesYearly();
                }
            }
        },
        methods:{
            showTime(t){
                return hp.showTime(t)
            },
            yearlyReports(){
                // for this year sales
                this.salesYearlyData.map((res)=>{
                    this.sales_this_year += res.charges;
                })
                //for totay sales
                const today=this.showTime(Timestamp.fromDate(new Date()))
                this.salesYearlyData.filter((f)=>{
                       const sale_date=this.showTime(f.sale_date)
                        if(sale_date === today){
                                this.sales_today += f.charges;
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
                this.salesYearlyData.filter((f)=>{
                        if(f.sale_date >= first_day_week && f.sale_date <= last_day_week){
                                this.sales_this_week += f.charges;
                        }
                })
                //for this month
                let date = new Date();
                let first_day = new Date(date.getFullYear(), date.getMonth(),1+1).toISOString().substr(0, 10);
                let last_day= new Date(date.getFullYear(), date.getMonth() + 1, 1).toISOString().substr(0, 10);
                const first_day_month=Timestamp.fromDate(new Date(first_day))
                const last_day_month=Timestamp.fromDate(new Date(last_day))
                this.salesYearlyData.filter((f)=>{
                        if(f.sale_date >= first_day_month && f.sale_date <= last_day_month){
                                this.sales_this_month += f.charges;
                        }
                })
            },
           async fetchSalesYearly(){
                const currentDate = new Date();
                const theFirst = new Date(currentDate.getFullYear(), 0, 1);
                const theLast = new Date(currentDate.getFullYear(), 11, 31);
                const first_day_month=Timestamp.fromDate(new Date(theFirst))
                const last_day_month=Timestamp.fromDate(new Date(theLast))
                const query_url=query(collection(db, "sales"),  
                where("sale_date", ">=", first_day_month), where("sale_date", "<=", last_day_month), orderBy("sale_date"));            
                const snapShot=await getDocs(query_url)
                let saleItems=ss.sales(snapShot);           
                this.salesYearlyData=saleItems;
                this.yearlyReports();
           },
           editShowTime(t){
                const formattedDate = new Date(t).toISOString().substr(0, 10)
                return formattedDate;
            },
        }
    }
    </script>