<template lang="">        
    <div>           
        <PageHeader page-title="Dashboard" page-code="dashboard"/>
        <div class="container-fluid my-2">
            <div class="row mb-2">
                <div class="col-12">
                    <div class="card shadow border-0 border-bottom border-3 border-primary">
                        <div class="card-body">
                                <div class="row">
                                    <div class="col-3 text-center h1"><i class="fa-solid fa-user-check"></i></div>
                                    <div class="col-9">
                                        <div class="small fw-lighter mb-2">Welcome</div>
                                        <div class="card-text" v-if="user !==null">{{user.email}}</div>
                                    </div>
                                </div>
                        </div>
                    </div>
                </div>
            </div>
           <SalesSession />
           <PurchaseSession />
           <div class="row g-0">           
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Sale'}" ><i class="fa-solid fa-cash-register"></i> Sale</router-link>
            </div>
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping"></i> Purchase</router-link>
            </div>
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'OpeningStocks'}"><i class="fa-solid fa-money-bill-trend-up"></i> Stocks</router-link>
            </div>
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Products'}"><i class="fa-solid fa-list-check"></i> Products code</router-link>
            </div>
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Suppliers'}"><i class="fa-solid fa-person-walking-luggage"></i> Suppliers</router-link>
            </div>
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Customers'}"><i class="fa-solid fa-users"></i> Customers</router-link>
            </div>
            <div class="col-sm-2 col-lg-3 d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Transfers'}"><i class="fa-solid fa-right-left"></i> Transfers</router-link>
            </div>
            <div class="col-sm-2 col-lg-3  d-grid">
                <router-link :to="{name: 'Reports'}" class="btn btn-sm"><i class="fa-solid fa-chart-pie"></i> Reports</router-link>
            </div>
           </div>
           <div class="row g-2">
            <div class="col-lg-6">
                <SalesChart />
            </div>
            <div class="col-lg-6">
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
import SalesChart from "@/views/admin/SalesChart.vue"
import PurchaseChart from "@/views/admin/PurchaseChart.vue"

 export default{
     components:{
        SalesSession,PageHeader,PurchaseSession,SalesChart, PurchaseChart
     },
     computed:{
            user(){
                return this.$store.getters.user ? this.$store.getters.user : null;
            }
        },
}
</script>
<style lang="">
    
</style>