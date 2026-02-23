<template>
    <div class="dashboard-page bg-light min-vh-100">             
        <PageHeader :page-title="t('dashboard')" page-code="dashboard"/>
        
        <div class="container-fluid py-2 vh-85 overflow-y-auto">
            <!-- Welcome Section -->
            <div class="row mb-3">
                <div class="col-12">
                    <div class="card border-0 shadow-sm welcome-card text-white overflow-hidden">
                        <div class="card-body p-4 position-relative z-1">
                             <div class="d-flex align-items-center">
                                <div class="avatar-circle bg-white text-primary me-3 shadow-sm">
                                    <i class="fa-solid fa-user-circle fa-2x"></i>
                                </div>
                                <div>
                                    <h4 class="fw-bold mb-1">{{getGreeting}}!</h4>
                                    <p class="mb-0 opacity-75" v-if="user">{{user.displayName}} ({{user.email}})</p>
                                </div>
                             </div>
                        </div>
                        <!-- Abstract Background Decoration -->
                        <div class="bg-decoration"></div>
                    </div>
                </div>
            </div>

            <!-- Stats/Metrics Row -->
            <div class="row g-3 mb-3">
                <div class="col-md-6">
                    <div class="h-100">
                        <SalesSession />
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="h-100">
                        <PurchaseSession />
                    </div>
                </div>
            </div>

            <!-- Quick Actions Grid -->
            <div class="row g-2 mb-3">           
                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Sale'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                            <div class="icon-box bg-success-subtle text-success mb-2 rounded-circle">
                                <i class="fa-solid fa-cash-register"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('sale')}}</span>
                        </div>
                    </router-link>
                </div>

                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Purchase'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                            <div class="icon-box bg-danger-subtle text-danger mb-2 rounded-circle">
                                <i class="fa-solid fa-cart-shopping"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('purchase')}}</span>
                        </div>
                    </router-link>
                </div>
                
                 <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Stocks'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                            <div class="icon-box bg-primary-subtle text-primary mb-2 rounded-circle">
                                <i class="fa-solid fa-money-bill-trend-up"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('stocks')}}</span>
                        </div>
                    </router-link>
                </div>

                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Products'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                             <div class="icon-box bg-warning-subtle text-warning mb-2 rounded-circle">
                                <i class="fa-solid fa-barcode"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('product_code')}}</span>
                        </div>
                    </router-link>
                </div>

                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Suppliers'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                             <div class="icon-box bg-info-subtle text-info mb-2 rounded-circle">
                                <i class="fa-solid fa-truck-field"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('suppliers')}}</span>
                        </div>
                    </router-link>
                </div>

                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Customers'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                         <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                             <div class="icon-box bg-success-subtle text-success mb-2 rounded-circle">
                                <i class="fa-solid fa-users"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('customers')}}</span>
                        </div>
                    </router-link>
                </div>

                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Adjustments'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                             <div class="icon-box bg-secondary-subtle text-secondary mb-2 rounded-circle">
                                <i class="fa-solid fa-wrench"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('adjustment')}}</span>
                        </div>
                    </router-link>
                </div>           
                
                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name : 'Soc'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                             <div class="icon-box bg-warning-subtle text-warning mb-2 rounded-circle">
                                <i class="fa-solid fa-file-invoice"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('soc')}}</span>
                        </div>
                    </router-link>
                </div>

                <div class="col-6 col-sm-4 col-md-3 col-lg-2">
                    <router-link :to="{name: 'Reports'}" class="quick-action-card card border-0 shadow-sm text-decoration-none h-100">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                             <div class="icon-box bg-info-subtle text-primary mb-2 rounded-circle">
                                <i class="fa-solid fa-chart-pie"></i>
                            </div>
                            <span class="text-dark fw-semibold small">{{t('reports')}}</span>
                        </div>
                    </router-link>
                </div>
           </div>

           <!-- Charts Section -->
           <div class="row g-2">
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body p-3">
                        <SalesChart />
                    </div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="card border-0 shadow-sm h-100">
                    <div class="card-body p-3">
                         <PurchaseChart />
                    </div>
                </div>
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
import {lang} from "@/lib/language"

 export default{
     components:{
        SalesSession,PageHeader,PurchaseSession,SalesChart, PurchaseChart
     },
     computed:{
            t() {
                return (key) => this.$store.getters.t(key);
                },
            user(){
                return this.$store.getters.user ? this.$store.getters.user : null;
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
    .dashboard-page {
        font-family: 'Inter', sans-serif;
    }

    /* Welcome Card Styling */
    .welcome-card {
        background: linear-gradient(135deg, #6c6a89 0%, #fafcff 100%);
        border-radius: 16px;
    }
    .avatar-circle {
        width: 50px;
        height: 50px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
    }
    .bg-decoration {
        position: absolute;
        top: 0;
        right: 0;
        width: 100%;
        height: 100%;
        background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.1' fill-rule='evenodd'/%3E%3C/svg%3E");
        opacity: 0.6;
    }

    /* Quick Action Cards */
    .quick-action-card {
        border-radius: 12px;
        transition: transform 0.25s cubic-bezier(0.25, 0.8, 0.25, 1), box-shadow 0.25s ease;
        background-color: #fff;
    }
    .quick-action-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0,0,0,0.08) !important;
    }
    .quick-action-card:hover .icon-box {
        transform: scale(1.1);
    }
    
    .icon-box {
        width: 48px;
        height: 48px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.25rem;
        transition: transform 0.3s ease;
    }

    /* Scrollbar override if needed */
    .overflow-y-auto::-webkit-scrollbar {
        width: 6px;
    }
    .overflow-y-auto::-webkit-scrollbar-thumb {
        background-color: #dee2e6;
        border-radius: 3px;
    }
</style>