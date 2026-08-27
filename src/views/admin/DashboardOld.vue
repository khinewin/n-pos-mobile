<template lang="">        
    <div>             
        <PageHeader :page-title="t('dashboard')" page-code="dashboard"/>
        <div class="container-fluid my-2 vh-80 overflow-y-auto">
            <div class="row mb-2">
                <div class="col-12">
                    <div class="card shadow border-0 border-start border-5 border-danger">
                        <div class="card-body p-2">
                                <div class="row align-items-center">
                                    <div class="col-2 text-center h1 mb-0"><i class="fa-solid fa-user-check"></i></div>
                                    <div class="col-5">
                                        <div class="fw-bold text-dark fs-5 mb-1">{{getGreeting}}!</div>
                                        <div class="card-text text-muted small" v-if="user !==null">{{user.displayName}} ({{user.email}})</div>
                                    </div>
                                    <div class="col-5 text-end pe-3">
                                        <div class="fw-bold text-primary small"><i class="fa-solid fa-calendar-days me-1"></i> {{ currentDayAndDate }}</div>
                                        <div class="small text-muted"><i class="fa-solid fa-clock me-1"></i> {{ currentTime }}</div>
                                    </div>
                                </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row g-2">
                <div class="col-md-4">
                    <SalesSession />
                </div>
                <div class="col-md-4">
                    <PurchaseSession />
                </div>
                <div class="col-md-4">
                    <StockValuationSession />
                </div>
            </div>
           <div class="row g-1 my-1 n-link">           
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm  " :to="{name : 'Sale'}" ><i class="fa-solid fa-cash-register text-success"></i>  {{t('sale')}}</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping text-danger"></i> {{t('purchase')}}</router-link>
            </div>
            <div class=" col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Stocks'}"><i class="fa-solid fa-money-bill-trend-up text-primary"></i> {{t('stocks')}}</router-link>
            </div>
            <div class=" col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'StockValuation'}"><i class="fa-solid fa-coins text-warning"></i> {{t('stock_valuation')}}</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Products'}"><i class="fa-solid fa-list-check text-warning"></i> {{t('product_code')}}</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Suppliers'}"><i class="fa-solid fa-person-walking-luggage text-info"></i> {{t('suppliers')}}</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Customers'}"><i class="fa-solid fa-users text-success"></i> {{t('customers')}}</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Adjustments'}"><i class="fa-solid fa-sliders text-primary"></i> {{t('adjustment')}}</router-link>
            </div>           
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Soc'}"><i class="fa-solid fa-file-invoice-dollar text-warning"></i> {{t('soc')}}</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2   d-grid">
                <router-link :to="{name: 'Reports'}" class="btn btn-sm "><i class="fa-solid fa-chart-pie text-info"></i> {{t('reports')}}</router-link>
            </div>
           </div>
           <div class="row g-2">
            <div class="col-lg-7">
                <SalesChart />
            </div>
            <div class="col-lg-5">
                <PurchaseChart />
            </div>
           </div>
        </div>
    </div>    
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import SalesSession from "@/views/admin/SalesSession.vue"
import PurchaseSession from "@/views/admin/PurchaseSession.vue"
import StockValuationSession from "@/views/admin/StockValuationSession.vue"
import SalesChart from "@/views/admin/SalesChart.vue"
import PurchaseChart from "@/views/admin/PurchaseChart.vue"
import {lang} from "@/lib/language"

 export default{
     components:{
        SalesSession,PageHeader,PurchaseSession,StockValuationSession,SalesChart, PurchaseChart
     },
     data() {
       return {
         currentDateTime: new Date(),
         timer: null
       }
     },
     mounted() {
       this.timer = setInterval(() => {
         this.currentDateTime = new Date();
       }, 1000);
     },
     beforeUnmount() {
       if (this.timer) clearInterval(this.timer);
     },
     computed:{
            t() {
                return (key) => this.$store.getters.t(key);
                },
            user(){
                return this.$store.getters.user ? this.$store.getters.user : null;
            },
            currentDayAndDate() {
              return this.currentDateTime.toLocaleDateString('en-US', {
                weekday: 'long',
                month: 'short',
                day: 'numeric',
                year: 'numeric'
              });
            },
            currentTime() {
              return this.currentDateTime.toLocaleTimeString('en-US', {
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: true
              });
            },
        getGreeting() {
            const now = new Date();
            const hours = now.getHours();
            const sl=this.$store.getters.currentLang;
            if (hours < 12) {
                return sl=="mm" ? "မင်္ဂလာနံနက်ခင်းပါ" :  "Good morning";
            } else if (hours < 18) {
                return sl=="mm" ? "မင်္ဂလာနေ့လည်ခင်းပါ" : "Good afternoon";
            } else {
                return sl=="mm" ? "မင်္ဂလာညနေခင်းပါ" : "Good evening";
            }
        }
    },
}
</script>
<style scoped>
    .n-link .btn:hover{
        text-shadow: 1px 1px 10px rgba(255, 255, 0, 0.6);
    }
</style>