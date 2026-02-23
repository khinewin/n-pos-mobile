<template>
    <div>           
        <PageHeader :page-title="pageTitle" page-code="adjustments" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Modern Sticky Toolbar -->
            <div class="mb-2 sticky-top z-3 bg-white rounded-3" style="top: 0;">
                <div class="d-flex justify-content-end align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                    <div class="d-flex gap-2">
                        <button @click="print" class="btn btn-light shadow-sm text-dark fw-medium border-0 px-3" :disabled="!voucher_no">
                            <i class="fa-solid fa-print me-2 text-primary"></i> {{t('print_link')}}
                        </button>
                        <button class="btn btn-light shadow-sm text-dark fw-medium border-0 px-3 open" data-bs-toggle="modal" data-bs-target="#adjustmentModal">
                            <i class="fa-solid fa-folder-tree me-2 text-primary"></i> {{t('open_link')}}
                        </button>
                    </div>

                

                    <div class="d-flex gap-2">
                        <router-link :to="{name : 'NewAdjustment'}" class="btn btn-light text-primary shadow-sm px-4 fw-bold border-0 new">
                            <i class="fa-solid fa-square-plus me-2"></i> {{t('new_link')}}
                        </router-link>
                        <button @click="closeAdjustment" class="btn btn-light shadow-sm text-danger fw-medium border-0 px-3" :disabled="!voucher_no">
                            <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                        </button>
                    </div>
                </div>
            </div>

            <div class="card border-0 shadow-sm rounded-3 overflow-hidden" id="printMe">
                <!-- Print Header -->
                <div class="d-none d-print-block">
                    <LargePrintHeader />
                    <div class="text-center h5 fw-bold mb-4 text-uppercase border-bottom pb-2">Inventory Adjustments</div>
                </div>

                <div class="card-body p-0">
                    <!-- Integrated Header Info -->
                    <div class="p-3 bg-light-subtle border-bottom">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <small class="text-muted text-uppercase fw-bold d-block mb-1">{{t('invoice_no')}}</small>
                                <span class="fw-bold fs-5 text-dark">{{voucher_no || '---'}}</span>
                            </div>
                            <div class="col-md-3">
                                <small class="text-muted text-uppercase fw-bold d-block mb-1">{{t('date')}}</small>
                                <span class="fw-medium text-dark">
                                    <i class="fa-regular fa-calendar me-1 opacity-50"></i>
                                    <span v-if="created_at">{{showTime(created_at)}}</span>
                                    <span v-else>---</span>
                                </span>
                            </div>
                            <div class="col-md-6">
                                <small class="text-muted text-uppercase fw-bold d-block mb-1">{{t('notes')}}</small>
                                <span class="fw-medium text-dark">{{remark || 'No extra notes'}}</span>
                            </div>
                        </div>
                    </div>

                    <!-- Product Table -->
                    <div class="vh-65 overflow-y-auto dataContainer">
                        <table class="table table-hover align-middle mb-0 text-nowrap">
                            <thead class="sticky-top z-1 text-nowrap">
                                <tr class="table-primary fw-bold small text-center">
                                    <th class="py-2 border-0 text-start ps-3" style="width: 150px;">{{t('barcode')}}</th>
                                    <th class="py-2 border-0 text-start" style="min-width: 250px;">{{t('item_name')}}</th>
                                    <th class="py-2 border-0">{{t('packing_size')}}</th>
                                    <th class="py-2 border-0">{{t('before_adj')}}</th>
                                    <th class="py-2 border-0">{{t('adj_type')}}</th>
                                    <th class="py-2 border-0 table-primary-subtle">{{t('qty')}}</th>
                                    <th class="py-2 border-0">{{t('after_adj')}}</th>
                                    <th class="py-2 border-0">{{t('rts_rfs')}}</th>
                                    <th class="py-2 border-0 text-start pe-3" style="min-width: 200px;">{{t('notes')}}</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-if="storeItems.length === 0" class="text-center text-muted">
                                    <td colspan="9" class="py-5">
                                        <i class="fa-solid fa-folder-open fa-3x mb-3 opacity-25 text-primary"></i>
                                        <p class="mb-0 fw-medium">Open a voucher to view adjustment details</p>
                                    </td>
                                </tr>
                                <tr v-else v-for="(st, i) in storeItems" :key="st.id" class="small">
                                    <td class="text-start ps-3 border-0 font-monospace">{{st.code_no}}</td>
                                    <td class="text-start border-0 fw-bold">{{st.item_name}}</td>
                                    <td class="text-center border-0">
                                        <span class="badge bg-light text-primary border border-primary-subtle px-2 py-1 fw-medium">
                                            {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                        </span>
                                    </td>
                                    <td class="text-center border-0 fw-medium">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.before_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                    </td>
                                    <td class="text-center border-0">
                                        <span class="badge" :class="st.adj_type === 'Increment' ? 'bg-success-subtle text-success' : 'bg-danger-subtle text-danger'">
                                            {{st.adj_type}}
                                        </span>
                                    </td>
                                    <td class="text-center table-primary-subtle border-0 fw-bold">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                    </td>
                                    <td class="text-center border-0 fw-bold text-primary">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                    </td>
                                    <td class="text-center border-0 fw-medium text-dark">{{st.rts_rfs}}</td>
                                    <td class="text-start pe-3 border-0 text-muted">{{st.remark || '---'}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <AdjustmentModal @setCode="setCode" />
        </div>
    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import AdjustmentModal from '@/views/modals/adjustments/AdjustmentModal.vue'
import AdjustmentModalOld from '@/views/modals/adjustments/AdjustmentModalOld.vue'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/adjustments"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as hc from "@/lib/handleCtrl"
import {lang} from '@/lib/language'

export default {
    components: {
        PageHeader, AdjustmentModal: store.getters.theme === "new" ? AdjustmentModal : AdjustmentModalOld, LargePrintHeader
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                voucher_no: {required},             
                created_at: {required},              
                storeItems : {required}
            }
    },
    data(){
        return{       
            id:"",
            voucher_no:"", 
            location:"",
            created_at:"",        
            stock_user:{},
            store_item: {},
            storeItems: [],
            isLoading:false,
            remark:"",
            msg : {
                type :"",
                info :"",
            }
        }
    },
  
    computed:{
        user(){
            return this.$store.getters.user;
        },    
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.adjustment.mm} / ${lang.history.mm}` : `${lang.adjustment.eng} / ${lang.history.eng}`            
            return pageTitle;
        },
         t() {
            return (key) => this.$store.getters.t(key);
        },
    },
    mounted() {
        window.addEventListener('keydown', this.handlectrl);
    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handlectrl);
     },
    
    methods:{    
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        handlectrl(event) {
         if (event.ctrlKey && event.key === 'q') {
             event.preventDefault(); // Prevent the default browser save dialog             
             this.closeAdjustment(); // Call your custom function
         }
         if (event.ctrlKey && event.key === 'p') {
             event.preventDefault(); // Prevent the default browser save dialog
             this.print();
         }          
         if(event.ctrlKey && event.shiftKey && event.key=== 'N') {
             event.preventDefault(); // Prevent the default browser save dialog
             const add=document.querySelector(".new")
             if (add) add.click();
         }
         return hc.handleCtrl(event)
     },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        async print () {
         await this.$htmlToPaper('printMe');
        }, 
        goEdit(){           
            this.$router.push({name: 'EditAdjustment', params: {id: this.voucher_no}})
        },
        closeAdjustment(){
            this.id="";
            this.voucher_no="";
            this.location="";
            this.created_at="";
            this.storeItems=[]
            this.store_item={}              
            this.remark="";
        },
     
        setCode(p){           
            this.id=p.id;
            this.stock_user=p.user;
            this.location=p.location;
            this.voucher_no=p.voucher_no;
            this.created_at=p.created_at;
            this.storeItems=p.products;
            this.remark=p.remark;
        },    
        
       showTime(t){
            return hp.showTime(t)
        },
                    
    }
}
</script>

<style scoped>
.vh-65 {
    height: 65vh;
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
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
@media print {
    #printMe {
        box-shadow: none !important;
    }
    .vh-65 {
        height: auto !important;
        overflow-y: visible !important;
    }
}
</style>

