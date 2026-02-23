<template>
    <!-- Purchase Records Modal -->
    <div class="modal fade" id="purchaseModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="purchaseModalLabel" aria-hidden="true">
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
                                                    v-model="search_by_from_pur_date" 
                                                    id="from_date" 
                                                    :config="config" />
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
                                                    v-model="search_by_to_pur_date" 
                                                    id="to_date" 
                                                    :config="config" />
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
                                                @keypress.enter="searchPurchase" 
                                                placeholder="Voucher no." 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Supplier Input -->
                                    <div class="mb-3">
                                        <label class="small text-muted text-uppercase fw-bold mb-1">{{t('supplier')}}</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-0">
                                                <i class="fa-solid fa-truck-field text-secondary"></i>
                                            </span>
                                            <input 
                                                type="search" 
                                                v-model="search_by_supplier" 
                                                @input="inputSearchSupplier" 
                                                placeholder="Search supplier" 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Action Buttons -->
                                    <div class="d-flex flex-column gap-2">
                                        <button class="btn btn-primary shadow-sm fw-medium" @click="fetchPurchase">
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

                        <!-- Right: Purchase Table -->
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
                                                    <th class="col-2 text-center border-0 py-3">
                                                        <i class="fa-solid fa-calendar me-1"></i>{{t('date')}}
                                                    </th>                                    
                                                    <th class="col-5 border-0 py-3">
                                                        <i class="fa-solid fa-truck-field me-1"></i>{{t('supplier')}}
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-user-tie me-1"></i>{{t('cashier')}}
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">
                                                <tr 
                                                    class="cursor-pointer" 
                                                    v-if="purchases.length > 0" 
                                                    v-for="p in purchases" 
                                                    :key="p.id" 
                                                    @click="setCode(p)" 
                                                    data-bs-dismiss="modal"
                                                    style="transition: all 0.2s;">
                                                    <td class="text-center">
                                                        <span class="badge bg-light text-dark border font-monospace px-3">{{p.voucher_no}}</span>
                                                    </td>
                                                    <td class="text-center text-muted fw-medium">{{showTime(p.pur_date)}}</td>
                                                    <td>
                                                        <div class="fw-bold text-dark">{{p.supplier.name}}</div>
                                                        <div v-if="p.supplier.company" class="small text-muted">
                                                            <i class="fa-solid fa-building me-1 tiny-icon"></i>{{p.supplier.company}}
                                                        </div>
                                                    </td>
                                                    <td class="text-center">
                                                        <span class="badge bg-secondary-subtle text-secondary py-2 px-3">
                                                            <i class="fa-solid fa-circle-user me-1 text-secondary opacity-75"></i>
                                                            {{p.user.displayName}}
                                                        </span>
                                                    </td>
                                                </tr>

                                                <!-- Empty State -->
                                                <tr v-if="!isLoading && purchases.length === 0">
                                                    <td colspan="4" class="text-center text-muted py-5">
                                                        <i class="fa-solid fa-receipt fa-3x mb-3 opacity-25"></i>
                                                        <div class="h6">No purchase records found</div>
                                                        <small>Try adjusting your search filters or date range</small>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>

                                        <!-- Loading State Overlay -->
                                        <div v-if="isLoading" class="position-absolute top-50 start-50 translate-middle text-center py-5 bg-white bg-opacity-75 w-100 h-100 d-flex flex-column justify-content-center align-items-center" style="z-index: 10;">
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
import {Timestamp, collection, onSnapshot, getDocs, orderBy, query, where} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/purchases"
import FlatPickr from 'vue-flatpickr-component';

export default {
   components:{
    FlatPickr,
   },
   data(){
       return{
           purchases:[],
           purchasesData: [],
           search_by_voucher_no:"N-",
           search_by_from_pur_date:"",
           search_by_to_pur_date:"",
           search_by_supplier:"",
           search_by_location:"",
           isLoading:false,
           config: {
                dateFormat: 'd/m/Y',
                enableTime: false,
                wrap: false, // Changed to false as we are not using input-group wrapping for flatpickr directly
                allowInput: true
            },  
       }
   },
  
   mounted() {    
       this.setDate();
       this.fetchPurchase();         
   },
  
   computed:{
       user(){
           return this.$store.getters.user;
       },      
       t() {
            return (key) => this.$store.getters.t(key);
        },
   },
  
   methods:{
    setDate(){
        this.search_by_from_pur_date=hp.setToday();
        this.search_by_to_pur_date=hp.setToday();
    },
    setCode(p){
        this.$emit("setCode", p)
        this.clearSearch();
    },
    inputSearchSupplier(event){
        this.search_by_supplier = event.target.value;
        this.searchPurchase();
    },
    clearSearch(){
        this.search_by_voucher_no="N-";
        this.search_by_supplier="";
        this.setDate();
        this.search_by_location="";
        this.fetchPurchase();
    },
    searchPurchase(){   
        let filtered = this.purchasesData;

        if (this.search_by_supplier) {
            filtered = filtered.filter(f => 
                f.supplier.name.toLowerCase().includes(this.search_by_supplier.toLowerCase()) || 
                f.supplier.company.toLowerCase().includes(this.search_by_supplier.toLowerCase())
            );
        }

        if (this.search_by_voucher_no.length > 2) {
            filtered = filtered.filter(f => f.voucher_no.includes(this.search_by_voucher_no));
        }

        if (this.search_by_location) {
            filtered = filtered.filter(f => f.location == this.search_by_location);
        }

        this.purchases = filtered;
    },
    async fetchPurchase() {
        this.purchases = []
        this.isLoading = true;
        try {
            let purchaseQuery;
            if (this.search_by_from_pur_date && this.search_by_to_pur_date) {
                const from_date = Timestamp.fromDate(hp.searchFromDate(this.search_by_from_pur_date));
                const to_date = Timestamp.fromDate(hp.searchToDate(this.search_by_to_pur_date));

                purchaseQuery = query(
                    collection(db, "purchases"),
                    where("pur_date", ">=", from_date),
                    where("pur_date", "<=", to_date),
                    orderBy("pur_date", "desc")
                );
            } else {
                purchaseQuery = query(
                    collection(db, "purchases"),
                    orderBy("pur_date", "desc")
                );
            }
            const snapShot = await getDocs(purchaseQuery);
            const pur = ss.purchases(snapShot);
            this.purchasesData = pur;
            this.purchases = pur;
        } catch (error) {
            console.error("Error fetching purchases:", error);
        } finally {
            this.isLoading = false;
        }
    },
    showTime(t){
        return hp.showTime(t)
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
.tiny-icon {
    font-size: 0.7rem;
    opacity: 0.6;
}
.vh-95 {
    height: 95vh;
}
.font-monospace {
    font-family: 'Courier New', Courier, monospace;
}
</style>
