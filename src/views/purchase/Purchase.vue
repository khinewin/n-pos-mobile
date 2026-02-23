<template>
    <div>
        <PageHeader :page-title="pageTitle" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Modern Sticky Toolbar (Sale.vue pattern) -->
            <div class="mb-2 sticky-top z-3 bg-white" style="top: 0;">
                <div class="d-flex justify-content-between align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                    <div class="d-flex gap-2">
                        <button class="btn btn-light shadow-sm text-primary fw-bold border-0" data-bs-toggle="modal" data-bs-target="#purchaseModal">
                            <i class="fa-solid fa-folder-tree me-2 text-info"></i> {{t('open_link')}}
                        </button>
                        <button class="btn btn-light shadow-sm text-dark fw-bold border-0" @click="goEdit" :disabled="!voucher_no || pur_user.uid !== user.uid">
                            <i class="fa-solid fa-pen-to-square me-2 text-warning"></i> {{t('edit_link')}}
                        </button>
                    </div>

                    <div class="d-flex gap-2">
                        <button @click="print" class="btn btn-light shadow-sm fw-medium border-0" :disabled="!voucher_no">
                            <i class="fa-solid fa-print me-2 text-primary"></i> {{t('print_link')}}
                        </button>
                        <router-link :to="{name : 'AddPurchase'}" id="add" class="btn btn-primary shadow-sm px-4 fw-bold border-0">
                            <i class="fa-solid fa-square-plus me-2"></i> {{t('new_link')}}
                        </router-link>
                        <button class="btn btn-light shadow-sm text-danger fw-medium border-0" :disabled="!voucher_no" @click="closeVoucher">
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
                            <div class="col-md-4">
                                <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('invoice_no')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                    <input type="text" readonly v-model="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" placeholder="No Invoice">
                                </div>
                            </div>
                            
                            <!-- Date -->
                            <div class="col-md-4">
                                <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('date')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-regular fa-calendar text-secondary"></i></span>
                                    <input type="text" readonly :value="pur_date ? showTime(pur_date) : ''" class="form-control bg-light border-0 fw-medium text-dark" placeholder="No Date">
                                </div>
                            </div>

                            <!-- Supplier -->
                            <div class="col-md-4">
                                <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('supplier')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-truck-field text-secondary"></i></span>
                                    <input type="text" readonly v-model="supplier.name" class="form-control bg-light border-0 fw-medium text-dark" placeholder="No Supplier">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Product List Table -->
            <div class="vh-50 overflow-y-auto dataContainer bg-white rounded-3 shadow-sm border-0 mb-3">
                <table class="table table-hover align-middle mb-0">
                    <thead class="sticky-top z-1 text-nowrap">
                        <tr class="table-primary fw-bold small text-center">
                                <th class="py-2 border-0">{{t('number')}}</th>
                                <th class="py-2 text-start border-0" style="min-width: 250px;">{{t('item_name')}}</th>
                                <th class="py-2 border-0">{{t('packing_size')}}</th>
                                <th class="py-2 border-0">{{t('qty')}}</th>
                                <th class="py-2 border-0">{{t('unit_price')}}</th>
                                <th class="py-2 border-0">{{t('sale_price')}}</th>
                                <th class="py-2 border-0">{{t('amount')}}</th>
                                <th class="py-2 border-0" colspan="2">{{t('discount')}}</th>
                                <th class="py-2 border-0">{{t('unit_cost')}}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-if="products.length === 0" class="text-center text-muted">
                                <td colspan="10" class="py-5">
                                    <i class="fa-solid fa-inbox fa-3x mb-3 opacity-25 text-primary"></i>
                                    <p class="mb-0 fw-medium">Open a purchase voucher to view items</p>
                                </td>
                            </tr>
                            <tr v-else v-for="(st, i) in products" :key="st.id">
                                <td class="text-center text-muted small">{{i+1}}</td>
                                <td class="fw-bold">{{st.item_name}}</td>
                                <td class="text-center">
                                    <span class="badge bg-light text-primary border border-primary-subtle px-2 py-1">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </span>
                                </td>
                                <td class="text-end fw-medium">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                </td>
                                <td class="text-center">
                                    <div class="small fw-bold">{{st.qty_pur_price}}</div>
                                    <div class="small text-muted">{{st.qty_pur_price_unit}}</div>
                                </td>
                                <td class="text-center">
                                    <div class="small fw-bold">{{st.qty_sale_price}}</div>
                                    <div class="small text-muted">{{st.qty_pur_price_unit}}</div>
                                </td>
                                <td class="text-end fw-bold">{{fM(st.amount)}}</td>
                                <td class="text-center small">
                                    <div v-if="st.dis_qty" class="badge bg-danger-subtle text-danger">{{st.dis_qty}}{{st.dis_qty_unit}}</div>
                                    <div v-if="st.dis_amount" class="badge bg-danger-subtle text-danger">{{st.dis_amount}}$</div>
                                    <div v-if="st.dis_percent" class="badge bg-danger-subtle text-danger">{{st.dis_percent}}%</div>
                                </td>
                                <td class="text-end text-danger fw-bold">{{st.discount ? fM(st.discount) : 0}}</td>
                                <td class="text-end fw-bold text-primary">{{fM(st.unit_cost)}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Sticky Bottom Summary Bar (Sale.vue pattern) -->
            <div class="sticky-bottom bg-white shadow-lg border-top py-3 px-4 z-2" >
                <div class="container-fluid">
                    <div class="row align-items-center">
                        <!-- Left: Remarks -->
                        <div class="col-lg-3 mb-2 mb-lg-0">
                            <div class="form-floating">
                                <textarea readonly v-model="remark" class="form-control border-0 bg-light" placeholder="Notes" id="remarkArea" style="height: 60px; resize: none;"></textarea>
                                <label for="remarkArea" class="text-muted small">{{t('notes')}}</label>
                            </div>
                        </div>

                        <!-- Right: Totals Breakdown -->
                        <div class="col-lg-9">
                            <div class="row g-2 align-items-end justify-content-end">
                                <!-- Subtotal -->
                                <div class="col-6 col-md-2 text-end">
                                    <label class="small text-muted fw-bold d-block text-uppercase mb-1">{{t('subtotal')}}</label>
                                    <div class="fs-5 fw-bold text-secondary">{{fM(total)}}</div>
                                </div>
                                <!-- Extra Discount -->
                                <div class="col-6 col-md-2 text-end">
                                    <label class="small text-danger fw-bold d-block text-uppercase mb-1">{{t('additional_discount')}}</label>
                                    <div class="fs-5 fw-bold text-danger">{{fM(extra_discount)}}</div>
                                </div>

                                <!-- Net Amount (Highlighted Primary) -->
                                <div class="col-12 col-sm-4 col-md-3 text-end bg-primary text-white p-2 rounded-2 shadow-sm mx-2">
                                    <label class="small fw-bold d-block text-uppercase opacity-75" style="font-size: 0.7rem;">GRAND TOTAL</label>
                                    <div class="fs-3 fw-bolder lh-1">{{fM(total - extra_discount)}}</div>
                                </div>

                                <!-- Paid -->
                                <div class="col-6 col-md-2 text-end">
                                    <label class="small text-success fw-bold d-block text-uppercase mb-1">{{t('paid')}}</label>
                                    <div class="fs-5 fw-bold text-success">{{fM(paid)}}</div>
                                </div>
                                
                                <!-- Balance Due -->
                                <div class="col-6 col-md-2 text-end border-start ps-3">
                                    <label class="small text-danger fw-bold d-block text-uppercase mb-1">{{t('balance_due')}}</label>
                                    <div class="fs-5 fw-bold text-danger">{{fM(balance)}}</div>
                                </div>
                            </div>
                        </div>
                    </div>
            </div>
        </div>

        <div class="d-none">
            <div id="printMe">
                <LargePrintHeader />
                <div class="text-center h6 mb-4">PURCHASE VOUCHER</div>
                <!-- Simple print content could be expanded here if needed -->
            </div>
        </div>

        <PurchaseModal @setCode="setCode"/>
    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required, email, helpers } from '@vuelidate/validators'
import { Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment } from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import PurchaseModal from '@/views/modals/purchase/PurchaseModal.vue'
import PurchaseModalOld from '@/views/modals/purchase/PurchaseModalOld.vue'
import Footer from '@/views/partials/Footer.vue'
import * as hp from "@/lib/helper.js"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as hc from "@/lib/handleCtrl"
import { lang } from "@/lib/language"

export default {
    components: {
        PageHeader, 
        PurchaseModal: store.getters.theme === "new" ? PurchaseModal : PurchaseModalOld, 
        Footer, 
        LargePrintHeader
    },

    data() {
        return {
            id: "",
            voucher_no: "",
            location: "",
            pur_date: "",
            supplier: "",
            products: [],
            pur_user: {},
            total_amount: 0,
            total_discount: 0,
            extra_discount: 0,
            grand_total: 0,
            paid: 0,
            balance: 0,
            total: 0,
            remark: "",
            isLoading: false,
            msg: {
                type: "",
                info: "",
            }

        }
    },

    computed: {
        user() {
            return this.$store.getters.user;
        },
        pageTitle() {
            const sl = this.$store.getters.currentLang;
            let pageTitle = sl == "mm" ? `${lang.purchase.mm} / ${lang.history.mm}` : `${lang.purchase.eng} / ${lang.history.eng}`
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

    methods: {
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        handlectrl(event) {

            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog             
                this.closeVoucher(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                //this.printSale(); // Call your custom function
                this.print();
            }

            if (event.ctrlKey && event.key === 'e') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.goEdit();
            }

            if (event.ctrlKey && event.shiftKey && event.key === 'N') {
                event.preventDefault(); // Prevent the default browser save dialog
                const add = document.getElementById("add")
                add.click();
            }

            return hc.handleCtrl(event)
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty) {
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit) {
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        async print() {
            // Pass the element id here
            await this.$htmlToPaper('printMe');
        },
        closeVoucher() {
            this.id = "";
            this.voucher_no = "";
            this.pur_date = "";
            this.supplier = {};
            this.supplier_name = "";
            this.products = []
            this.total_amount = 0
            this.total_discount = 0
            this.extra_discount = 0
            this.total = 0,
                this.location = "";
            this.paid = 0;
            this.balance = 0;
            this.remark = "";
        },
        goEdit() {
            if (this.pur_user.uid != this.user.uid) return false;
            this.$router.push({ name: "EditPurchase", params: { id: this.voucher_no } })
        },
        setCode(p) {
            this.id = p.id;
            this.voucher_no = p.voucher_no;
            this.pur_date = p.pur_date;
            this.supplier = p.supplier;
            this.pur_user = p.user;
            this.location = p.location;
            // this.products=p.products;
            this.products = p.products;
            this.remark = p.remark;
            this.calAmount(p);
        },
        calAmount(p) {
            this.total_amount = p.total_amount;
            this.total_discount = p.total_discount;
            this.extra_discount = p.extra_discount;
            this.total = p.total;
            this.paid = p.paid;
            this.balance = p.balance;
            this.grand_total = p.grand_total;
        },

        showTime(t) {
            return hp.showTime(t)
        }

    }
}
</script>

<style scoped>
.vh-50 {
    height: 50vh;
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
