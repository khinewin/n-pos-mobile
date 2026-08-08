<template>
    <!-- Sales Records Modal -->
    <div class="modal fade" id="salesModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="salesModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
            <div class="modal-content bg-light">               
                <div class="container-fluid p-2">              
                    <div class="row g-2">
                        <!-- Left Sidebar: Search Filters -->
                        <div class="col-lg-3">
                            <div class="card border-0 shadow-sm rounded-3">
                                <div class="card-body p-3">
                                    <h6 class="fw-bold text-muted text-uppercase small mb-3">
                                        <i class="fa-solid fa-filter me-2"></i>Search Filters
                                    </h6>
                                    
                                    <!-- Date Range -->
                                    <div class="row g-2 mb-2">
                                        <div class="col-6">
                                            <label class="small text-muted text-uppercase fw-bold mb-1">{{t('from_date')}}</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light border-0">
                                                    <i class="fa-solid fa-calendar-day text-secondary"></i>
                                                </span>
                                                <flat-pickr 
                                                    class="form-control bg-light border-0 small" 
                                                    v-model="search_by_from_sale_date" 
                                                    id="from_date" 
                                                    :config="configFromDate" />
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <label class="small text-muted text-uppercase fw-bold mb-1">{{t('to_date')}}</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light border-0">
                                                    <i class="fa-solid fa-calendar-day text-secondary"></i>
                                                </span>
                                                <flat-pickr 
                                                    class="form-control bg-light border-0 small" 
                                                    v-model="search_by_to_sale_date" 
                                                    id="to_date" 
                                                    :config="configToDate" />
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Invoice No Input -->
                                    <div class="mb-2">
                                        <label class="small text-muted text-uppercase fw-bold mb-1">{{t('invoice_no')}}</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-0">
                                                <i class="fa-solid fa-file-invoice text-secondary"></i>
                                            </span>
                                            <input 
                                                type="search" 
                                                id="search_by_voucher_no" 
                                                v-model="search_by_voucher_no" 
                                                @keypress.enter="searchSales" 
                                                @paste="searchSales"
                                                placeholder="Voucher no." 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Customer Input -->
                                    <div class="mb-3">
                                        <label class="small text-muted text-uppercase fw-bold mb-1">{{t('customer')}}</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-0">
                                                <i class="fa-solid fa-user-tag text-secondary"></i>
                                            </span>
                                            <input 
                                                type="search" 
                                                v-model="search_by_customer" 
                                                @input="inputSearchCustomer" 
                                                placeholder="Search customer" 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Action Buttons -->
                                    <div class="d-flex flex-column gap-2">
                                        <button class="btn btn-primary shadow-sm fw-medium" @click="fetchSales">
                                            <i class="fa-solid fa-search me-2"></i> {{t('search_link')}}
                                        </button>
                                        <div class="d-flex gap-2">
                                            <button class="btn btn-light flex-fill shadow-sm fw-medium" @click="clearSearch">
                                                <i class="fa-solid fa-filter-circle-xmark me-2"></i> {{t('clear_link')}}
                                            </button>
                                            <button class="btn btn-light flex-fill shadow-sm fw-medium text-danger" data-bs-dismiss="modal" aria-label="Close">
                                                <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Right: Sales Table -->
                        <div class="col-lg-9">
                            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                                <div class="card-body p-0">
                                    <div class="vh-95 overflow-y-auto position-relative">
                                        <table class="table table-hover mb-0 align-middle">
                                            <thead class="position-sticky top-0 z-3">
                                                <tr class="table-primary-subtle fw-bold">
                                                    <th class="col-2 text-center border-0 py-3">
                                                        <i class="fa-solid fa-hashtag me-1"></i>{{t('invoice_no')}}
                                                    </th>
                                                    <th class="col-3 border-0 py-3">
                                                        <i class="fa-solid fa-user me-1"></i>{{t('customer')}}
                                                    </th>
                                                    <th class="col-2 text-center border-0 py-3">
                                                        <i class="fa-solid fa-phone me-1"></i>{{t('phone')}}
                                                    </th>
                                                    <th class="col-2 text-center border-0 py-3">
                                                        <i class="fa-solid fa-clock me-1"></i>{{t('date')}}
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-user-tie me-1"></i>{{t('cashier')}}
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">
                                                <tr 
                                                    class="cursor-pointer" 
                                                    v-if="sales.length > 0" 
                                                    v-for="s in sales" 
                                                    :key="s.id" 
                                                    @click="setCode(s)" 
                                                    data-bs-dismiss="modal"
                                                    style="transition: all 0.2s;">
                                                    <td class="text-center">
                                                        <span class="badge bg-light text-dark border font-monospace px-3">{{s.voucher_no}}</span>
                                                    </td>
                                                    <td>
                                                        <div class="fw-bold text-dark">{{s.customer.name}}</div>
                                                    </td>
                                                    <td class="text-center text-muted">
                                                        <i class="fa-solid fa-mobile-screen small opacity-50 me-1"></i>
                                                        {{s.customer.phone || '-'}}
                                                    </td>
                                                    <td class="text-center text-muted fw-medium">{{showTime(s.sale_date)}}</td>
                                                    <td class="text-center">
                                                        <span class="badge bg-secondary-subtle text-secondary py-2 px-3">
                                                            <i class="fa-solid fa-circle-user me-1 text-secondary opacity-75"></i>
                                                            {{s.user.displayName}}
                                                        </span>
                                                    </td>
                                                </tr>

                                                <!-- Empty State -->
                                                <tr v-if="!isLoading && sales.length === 0">
                                                    <td colspan="5" class="text-center text-muted py-5">
                                                        <i class="fa-solid fa-receipt fa-3x mb-3 opacity-25"></i>
                                                        <div class="h6">No sales records found</div>
                                                        <small>Try adjusting your search filters or date range</small>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>

                                        <!-- Loading State Overlay -->
                                        <div v-if="isLoading" class="position-absolute top-50 start-50 translate-middle text-center py-5 bg-white bg-opacity-75 w-100 h-100 d-flex flex-column justify-content-center align-items-center" style="z-index: 10;" >
                                            <div class="spinner-border text-primary mb-3" role="status">
                                                <span class="visually-hidden">Loading...</span>
                                            </div>
                                            <div class="text-muted fw-medium">Loading records...</div>
                                        </div>
                                    </div>
                                </div>
                            </div>                       
                        </div>
                    </div>                                                                                     
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import {Timestamp, collection, getDocs, orderBy, query, where} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/sales"
import FlatPickr from 'vue-flatpickr-component';

export default {
   components:{
    FlatPickr,
   },
   data(){
       return{
           sales:[],
           salesData: [],
           search_by_voucher_no:"N-",
           search_by_from_sale_date:"",
           search_by_to_sale_date:"",
           search_by_customer:"",
           isLoading:false,
       }
   },
  
   mounted() {    
       this.setDate();
       this.fetchSales();         
   },
  
   computed:{
        config(){
            return this.$store.getters.flat_pickr_config;
        },
        configFromDate() {
            return { ...this.$store.getters.flat_pickr_config };
        },
        configToDate() {
            return { ...this.$store.getters.flat_pickr_config };
        },
        user(){
            return this.$store.getters.user;
        },       
        t() {
            return (key) => this.$store.getters.t(key);
        },
   },
  
   methods:{
    setDate(){
        this.search_by_from_sale_date=hp.setToday();
        this.search_by_to_sale_date=hp.setToday();
    },
    setCode(s){
        this.$emit("setCode", s)
        this.clearSearch();
    },
    inputSearchCustomer(event){
        this.search_by_customer = event.target.value;
        this.searchSales();
    },
    clearSearch(){
        this.search_by_voucher_no="N-";
        this.search_by_customer="";
        this.setDate();
        this.fetchSales();
    },
    searchSales(){   
        let filtered = this.salesData;

        if (this.search_by_customer) {
            filtered = filtered.filter(f => 
                f.customer.name.toLowerCase().includes(this.search_by_customer.toLowerCase()) || 
                (f.customer.phone && f.customer.phone.includes(this.search_by_customer))
            );
        }

        if (this.search_by_voucher_no.length > 2) {
            filtered = filtered.filter(f => f.voucher_no.includes(this.search_by_voucher_no));
        }

        this.sales = filtered;
    },
    async fetchSales() {
        this.sales = []
        this.isLoading = true;
        try {
            let salesQuery;
            if (this.search_by_from_sale_date && this.search_by_to_sale_date) {
                const from_date = Timestamp.fromDate(hp.searchFromDate(this.search_by_from_sale_date));
                const to_date = Timestamp.fromDate(hp.searchToDate(this.search_by_to_sale_date));

                salesQuery = query(
                    collection(db, "sales"),
                    where("sale_date", ">=", from_date),
                    where("sale_date", "<=", to_date),
                    orderBy("sale_date", "desc")
                );
            } else {
                salesQuery = query(
                    collection(db, "sales"),
                    orderBy("sale_date", "desc")
                );
            }
            const snapShot = await getDocs(salesQuery);
            const sales_res = ss.sales(snapShot);
            this.salesData = sales_res;
            this.sales = sales_res;
        } catch (error) {
            console.error("Error fetching sales:", error);
        } finally {
            this.isLoading = false;
        }
    },
    showTime(t){
        return hp.showTimeHourShort(t)
    }
   }
}
</script>

<style scoped>
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
.cursor-pointer {
    cursor: pointer;
}
.cursor-pointer:hover {
    background-color: rgba(0,0,0,0.03);
    transform: scale(1.001);
}
.vh-95 {
    height: 95vh;
}
.font-monospace {
    font-family: 'Courier New', Courier, monospace;
}
</style>
