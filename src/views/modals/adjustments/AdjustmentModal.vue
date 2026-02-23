<template>
    <!-- Adjustments History Modal -->
    <div class="modal fade" id="adjustmentModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="adjustmentModalLabel" aria-hidden="true">
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
                                            <label class="small text-muted text-uppercase fw-bold mb-1">{{from_date_link}}</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light border-0">
                                                    <i class="fa-solid fa-calendar-day text-secondary"></i>
                                                </span>
                                                <flat-pickr 
                                                    class="form-control bg-light border-0 small" 
                                                    v-model="search_by_from_date" 
                                                    id="from_date" 
                                                    :config="config" 
                                                    @change="searchAdjustments" />
                                            </div>
                                        </div>
                                        <div class="col-6">
                                            <label class="small text-muted text-uppercase fw-bold mb-1">{{to_date_link}}</label>
                                            <div class="input-group">
                                                <span class="input-group-text bg-light border-0">
                                                    <i class="fa-solid fa-calendar-day text-secondary"></i>
                                                </span>
                                                <flat-pickr 
                                                    class="form-control bg-light border-0 small" 
                                                    v-model="search_by_to_date" 
                                                    id="to_date" 
                                                    :config="config"
                                                    @change="searchAdjustments" />
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Voucher No Input -->
                                    <div class="mb-3">
                                        <label class="small text-muted text-uppercase fw-bold mb-1">{{voucher_no_link}}</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-0">
                                                <i class="fa-solid fa-hashtag text-secondary"></i>
                                            </span>
                                            <input 
                                                type="search" 
                                                id="search_by_voucher_no" 
                                                v-model="search_by_voucher_no" 
                                                @keypress.enter="searchAdjustments" 
                                                placeholder="Voucher no." 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Action Buttons -->
                                    <div class="d-flex flex-column gap-2">
                                        <button class="btn btn-primary shadow-sm fw-medium" @click="fetchAdjustments">
                                            <i class="fa-solid fa-clock-rotate-left me-2"></i> Fetch History
                                        </button>
                                        <div class="d-flex gap-2">
                                            <button class="btn btn-light flex-fill shadow-sm fw-medium" @click="clearSearch">
                                                <i class="fa-solid fa-filter-circle-xmark me-2"></i> {{clear_link}}
                                            </button>
                                            <button class="btn btn-light flex-fill shadow-sm fw-medium text-danger" data-bs-dismiss="modal" aria-label="Close">
                                                <i class="fa-solid fa-circle-xmark me-2"></i> {{close_link}}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Right: Adjustments Table -->
                        <div class="col-lg-9">
                            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                                <div class="card-body p-0">
                                    <div class="vh-95 overflow-y-auto position-relative dataContainer">
                                        <table class="table table-hover mb-0 align-middle">
                                            <thead class="position-sticky top-0 z-3">
                                                <tr class="table-primary-subtle fw-bold">
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-file-invoice me-1"></i>Voucher No.
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-calendar me-1"></i>Adjustment Date
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-location-dot me-1"></i>Location
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-user-circle me-1"></i>Staff
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">
                                                <tr 
                                                    class="cursor-pointer" 
                                                    v-if="adjustments.length > 0" 
                                                    v-for="p in adjustments" 
                                                    :key="p.id" 
                                                    @click="setCode(p)" 
                                                    data-bs-dismiss="modal"
                                                    style="transition: all 0.2s;">
                                                    <td class="text-center">
                                                        <span class="badge bg-light text-dark border font-monospace px-3">{{p.voucher_no}}</span>
                                                    </td>
                                                    <td class="text-center text-muted fw-medium">{{showTime(p.created_at)}}</td>
                                                    <td class="text-center">
                                                        <span class="badge" :class="p.location === 'Main' ? 'bg-info-subtle text-info' : 'bg-warning-subtle text-warning'">
                                                            {{p.location}}
                                                        </span>
                                                    </td>
                                                    <td class="text-center">
                                                        <span class="badge bg-secondary-subtle text-secondary py-2 px-3">
                                                            <i class="fa-solid fa-circle-user me-1 text-secondary opacity-75"></i>
                                                            {{p.user.displayName}}
                                                        </span>
                                                    </td>
                                                </tr>

                                                <!-- Empty State -->
                                                <tr v-if="!isLoading && adjustments.length === 0">
                                                    <td colspan="4" class="text-center text-muted py-5">
                                                        <i class="fa-solid fa-folder-open fa-3x mb-3 opacity-25"></i>
                                                        <div class="h6">No adjustment records found</div>
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
                                            <div class="text-muted fw-medium">Loading history...</div>
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
import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/adjustments"
import FlatPickr from 'vue-flatpickr-component';
import * as hp from '@/lib/helper'
import {lang} from '@/lib/language'

export default {
    components:{
        FlatPickr,
    },
   data(){
       return{
           adjustments:[],
           adjustmentsData: [],
           search_by_voucher_no:"N-",
           search_by_from_date:"",
           search_by_to_date:"",
           search_by_location: "",
           isLoading:false,
           isUpdating:false,
       }
   },
   created() {
     this.setDate();
   },
   mounted() {    
       const myModalEl = document.getElementById('adjustmentModal')
       myModalEl.addEventListener('show.bs.modal', event => {
               if(this.adjustmentsData.length === 0){
                  this.fetchAdjustments();                
               }
       })       
   },
   computed:{
       user(){
           return this.$store.getters.user;
       },
       config(){
        return this.$store.getters.flat_pickr_config;
       },
        clear_link(){
            const sl=this.$store.getters.setLanguage;
            return sl=="mm" ? lang.clear_link.mm : lang.clear_link.eng ;
        },
        close_link(){
            const sl=this.$store.getters.setLanguage;
            return sl=="mm" ? lang.close_link.mm : lang.close_link.eng ;
        },
        from_date_link(){
            const sl=this.$store.getters.setLanguage;
            return sl=="mm" ? lang.from_date.mm : lang.from_date.eng ;
        },
        to_date_link(){
            const sl=this.$store.getters.setLanguage;
            return sl=="mm" ? lang.to_date.mm : lang.to_date.eng ;
        },
        voucher_no_link(){
            const sl=this.$store.getters.setLanguage;
            return sl=="mm" ? lang.voucher_no.mm : lang.voucher_no.eng ;
        },
   },
   methods:{
    setDate(){
        this.search_by_from_date=hp.setToday();
        this.search_by_to_date=hp.setToday();
    },
    setCode(p){
        this.$emit("setCode", p)
        this.clearSearch();
    },
    clearSearch(){
        this.search_by_voucher_no="N-";
        this.search_by_location="";
        this.setDate();
        this.fetchAdjustments();
    },
    searchAdjustments(){
        let filtered = this.adjustmentsData;

        if (this.search_by_voucher_no.length > 2) {
            filtered = filtered.filter(f => f.voucher_no.includes(this.search_by_voucher_no));
        }

        if (this.search_by_location) {
            filtered = filtered.filter(f => f.location == this.search_by_location);
        }

        this.adjustments = filtered;
    },
    async fetchAdjustments(){
        this.isLoading = true;
        const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_by_from_date))
        const to_date=Timestamp.fromDate(hp.searchToDate(this.search_by_to_date))        
        let query_url="";         
        if(this.search_by_from_date && this.search_by_to_date){
            query_url = query(collection(db, "adjustments"), where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
        }

        onSnapshot(query_url, (snapShot)=>{
            const adjustments=ss.adjustments(snapShot);      
            this.adjustmentsData=adjustments;
            this.isLoading=false;
            this.isUpdating=false;
            this.searchAdjustments();
        })
    },
    showTime(t){
        return hp.showTime(t);
    },
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
.dataContainer::-webkit-scrollbar {
    width: 6px;
}
.dataContainer::-webkit-scrollbar-track {
    background: transparent;
}
.dataContainer::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 10px;
}
.dataContainer::-webkit-scrollbar-thumb:hover {
    background: #cccccc;
}
</style>


  <!--
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                    <div class="row">
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter ">Voucher no.</div>
                                        </div>
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter ">Pur Date</div>
                                        </div>
                                        <div class="col-lg-6">
                                                <div class="small fw-lighter">Supplier</div>
                                        </div>                                          
                                        
                                </div>
                                </li>
                             
                                <li class="list-group-item" v-if="isLoading">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                      </div>
                                </li>
                         
                                <li v-if="adjustments.length > 0" class="list-group-item list-group-item-action" v-for="p in adjustments" :key="p.id" >
                                        <div class="row" @click="setCode(p)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                <div class="col-lg-3 small">
                                                        <div>{{p.voucher_no}}</div>
                                                </div>
                                                <div class="col-lg-3 small">
                                                 <div>{{showTime(p.pur_date)}}</div>
                                                </div>
                                                <div class="col-lg-6 small">
                                                        <div>{{p.supplier.name}} ({{p.supplier.company}})</div>
                                                </div>
                                            
                                        </div>
                                </li>
                        </ul>
                    -->