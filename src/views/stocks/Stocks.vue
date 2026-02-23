<template>
    <div>           
        <PageHeader :page-title="pageTitle" page-code="addStock" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Modern Sticky Toolbar (Purchase.vue pattern) -->
            <div class="mb-2 sticky-top z-3 bg-white rounded-3" style="top: 0;">
                <div class="d-flex justify-content-between align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                    <div class="d-flex gap-2">
                        <button class="btn btn-light shadow-sm text-primary fw-bold border-0" data-bs-toggle="modal" data-bs-target="#stocksModal">
                            <i class="fa-solid fa-folder-tree me-2 text-info"></i> {{t('open_link')}}
                        </button>
                        <button class="btn btn-light shadow-sm text-dark fw-bold border-0" @click="goEdit" :disabled="!voucher_no || stock_user.uid !== user.uid">
                            <i class="fa-solid fa-pen-to-square me-2 text-warning"></i> {{t('edit_link')}}
                        </button>
                    </div>

                    <div class="d-flex gap-2">
                        <button @click="print" class="btn btn-light shadow-sm fw-medium border-0" :disabled="!voucher_no">
                            <i class="fa-solid fa-print me-2 text-primary"></i> {{t('print_link')}}
                        </button>
                        <router-link :to="{name : 'NewStocks'}" class="btn btn-light text-primary shadow-sm px-4 fw-bold border-0 new">
                            <i class="fa-solid fa-square-plus me-2"></i> {{t('new_link')}}
                        </router-link>
                        <button class="btn btn-light shadow-sm text-danger fw-medium border-0" @click="closeStocks" :disabled="!voucher_no">
                            <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                        </button>
                    </div>
                </div>
            </div>

            <!-- Integrated Voucher Details Card -->
            <div class="mb-2">
                <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                    <div class="card-body p-2">
                        <div class="row g-4 align-items-center">
                            <!-- Invoice No -->
                            <div class="col-md-3">
                                <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('invoice_no')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                    <input type="text" readonly :value="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" placeholder="No Invoice">
                                </div>
                            </div>
                            
                            <!-- Date -->
                            <div class="col-md-3">
                                <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('date')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-regular fa-calendar text-secondary"></i></span>
                                    <input type="text" readonly :value="created_at ? showTime(created_at) : ''" class="form-control bg-light border-0 fw-medium text-dark" placeholder="No Date">
                                </div>
                            </div>

                            <!-- Remarks/Notes -->
                            <div class="col-md-6">
                                <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('notes')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-comment-dots text-secondary"></i></span>
                                    <input type="text" readonly :value="remark" class="form-control bg-light border-0 fw-medium text-dark fst-italic" placeholder="No notes added">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Product List Table -->
            <div class="vh-65 overflow-y-auto dataContainer bg-white rounded-3 shadow-sm border-0 mb-3" id="printMe">
                <div class="d-none d-print-block">
                    <LargePrintHeader />
                    <div class="text-center h5 fw-bold my-3">STOCK ADJUSTMENT VOUCHER</div>
                    <div class="row mb-4 px-4 small">
                        <div class="col-6"><strong>Voucher No:</strong> {{voucher_no}}</div>
                        <div class="col-6 text-end"><strong>Date:</strong> {{created_at ? showTime(created_at) : ''}}</div>
                        <div class="col-12 mt-1"><strong>Notes:</strong> {{remark}}</div>
                    </div>
                </div>

                <table class="table table-hover align-middle mb-0 text-nowrap">
                    <thead class="sticky-top z-1 text-nowrap">
                        <tr class="table-primary fw-bold small text-center">
                            <th class="py-2 border-0" style="width: 50px;">#</th>
                            <th class="py-2 text-start border-0" style="min-width: 150px;">{{t('barcode')}}</th>
                            <th class="py-2 text-start border-0" style="min-width: 250px;">{{t('item_name')}}</th>
                            <th class="py-2 border-0">{{t('packing_size')}}</th>
                            <th class="py-2 border-0 table-info-subtle">{{t('inv_bal')}}</th>
                            <th class="py-2 border-0 table-primary-subtle">{{t('pur_price')}}</th>
                            <th class="py-2 border-0 table-primary-subtle">{{t('sale_price')}}</th>
                        </tr>
                    </thead>
                    <tbody>
                        <!-- Empty State -->
                        <tr v-if="storeItems.length === 0" class="text-center text-muted">
                            <td colspan="7" class="py-5">
                                <i class="fa-solid fa-inbox fa-3x mb-3 opacity-25 text-primary"></i>
                                <p class="mb-0 fw-medium">Open a stock voucher to view items</p>
                            </td>
                        </tr>

                        <!-- Product Rows -->
                        <tr v-else v-for="(st, i) in storeItems" :key="st.id" class="small">
                            <td class="text-center text-muted border-0 small">{{i+1}}</td>
                            <td class="border-0 font-monospace">{{st.code_no}}</td>
                            <td class="fw-bold border-0">{{st.item_name}}</td>
                            <td class="text-center border-0">
                                <span class="badge bg-light text-primary border border-primary-subtle px-2 py-1 fw-medium">
                                    {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                </span>
                            </td>
                            <td class="text-end fw-bold table-info-subtle border-0">
                                <i class="fa-solid fa-warehouse me-2 opacity-50 small"></i>
                                {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                            </td>
                            <td class="text-end table-primary-subtle border-0 fw-bold">
                                <template v-if="st.packing_size.largest_unit">
                                    {{fM(st.pur_price.largest_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.largest_unit}}</small>
                                </template>
                                <template v-else>
                                    {{fM(st.pur_price.medium_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.medium_unit}}</small>
                                </template>
                            </td>
                            <td class="text-end table-primary-subtle border-0 fw-bold text-primary">
                                <template v-if="st.packing_size.largest_unit">
                                    {{fM(st.retail_price.largest_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.largest_unit}}</small>
                                </template>
                                <template v-else>
                                    {{fM(st.retail_price.medium_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.medium_unit}}</small>
                                </template>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <StocksModal @setCode="setCode" />
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
import StocksModal from '@/views/modals/stocks/StocksModal.vue'
import StocksModalOld from '@/views/modals/stocks/StocksModalOld.vue'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/stocks"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as hc from "@/lib/handleCtrl"
import {lang} from '@/lib/language'

export default {
    components: {
        PageHeader, StocksModal: store.getters.theme === "new" ? StocksModal : StocksModalOld, LargePrintHeader
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
            let  pageTitle=sl=="mm" ?  `${lang.stocks.mm} / ${lang.history.mm}` : `${lang.stocks.eng} / ${lang.history.eng}`            
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
             this.closeStocks(); // Call your custom function
         }
         if (event.ctrlKey && event.key === 'p') {
             event.preventDefault(); // Prevent the default browser save dialog
             this.print();
         }        
         if (event.ctrlKey && event.key === 'e') {
             event.preventDefault(); // Prevent the default browser save dialog
              this.goEdit();
         }      
         if(event.ctrlKey && event.shiftKey && event.key=== 'N') {
             event.preventDefault(); // Prevent the default browser save dialog
             const add=document.querySelector(".new")
             add.click();
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
        // Pass the element id here
         await this.$htmlToPaper('printMe');
        }, 
        goEdit(){           
            if(this.stock_user.uid != this.user.uid) return false;
            this.$router.push({name: 'EditStocks', params: {id: this.voucher_no}})
        },
        closeStocks(){
          this.id="";
            this.voucher_no="";
            this.created_at="";
            this.storeItems=[]
            this.store_item={}              
            this.total_retail_amount=0,
           this.total_whole_sale_amount=0;
           this.total_pur_amount=0;
           this.remark="";
        },
     
        setCode(p){           
            this.id=p.id;
            this.stock_user=p.user;
            this.voucher_no=p.voucher_no;
            this.created_at=p.created_at;
            this.storeItems=p.products;
            this.total_retail_amount=p.total_retail_amount;
            this.total_whole_sale_amount=p.total_whole_sale_amount
            this.total_pur_amount=p.total_pur_amount
            this.total_main_retail_amount=p.total_main_retail_amount
            this.total_main_whole_sale_amount=p.total_main_whole_sale_amount;
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
.table-info-subtle {
    background-color: #f0faff !important;
}
</style>

