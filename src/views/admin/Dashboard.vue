<template lang="">        
    <div>           
        <PageHeader page-title="Dashboard" page-code="dashboard"/>
        <div class="container-fluid my-2 vh-80 overflow-y-auto">
            <div class="row mb-2">
                <div class="col-12">
                    <div class="card shadow border-0 border-start border-5 border-danger">
                        <div class="card-body p-2">
                                <div class="row">
                                    <div class="col-3 text-center h1"><i class="fa-solid fa-user-check"></i></div>
                                    <div class="col-9">
                                        <div class="small fw-lighter mb-2">Welcome</div>
                                        <div class="card-text" v-if="user !==null">{{user.displayName}} ({{user.email}})</div>
                                    </div>
                                </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row g-2">
                <div class="col-sm-6">
                    <SalesSession />
                </div>
                <div class="col-sm-6">
                    <PurchaseSession />
                </div>
            </div>
           <div class="row g-1 my-1 n-link">           
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm " :to="{name : 'Sale'}" ><i class="fa-solid fa-cash-register"></i> Sale</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping"></i> Purchase</router-link>
            </div>
            <div class=" col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Stocks'}"><i class="fa-solid fa-money-bill-trend-up"></i> Stocks</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Products'}"><i class="fa-solid fa-list-check"></i> Medicine code</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Suppliers'}"><i class="fa-solid fa-person-walking-luggage"></i> Suppliers</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Customers'}"><i class="fa-solid fa-users"></i> Customers</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Transfers'}"><i class="fa-solid fa-right-left"></i> Transfer</router-link>
            </div>  
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Adjustments'}"><i class="fa-solid fa-sliders"></i> Adjustment</router-link>
            </div>           
            <div class="col-6 col-sm-4 col-md-3 col-lg-2  d-grid">
                <router-link class="btn btn-sm" :to="{name : 'Soc'}"><i class="fa-solid fa-file-invoice-dollar"></i> SOC</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2   d-grid">
                <router-link :to="{name: 'Reports'}" class="btn btn-sm"><i class="fa-solid fa-chart-pie"></i> Reports</router-link>
            </div>
            <div class="col-6 col-sm-4 col-md-3 col-lg-2   d-grid">
                <router-link class="btn btn-sm" :to="{name : 'BackupRestore'}"><i class="fa-solid fa-file-export"></i> Backup / Restore</router-link>
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
<style scoped>
    .n-link .btn:hover{
        text-shadow: 1px 1px 10px rgba(255, 255, 0, 0.6);
    }
</style>