<template>
    <div>
        <PageHeader :page-title="pageTitle" page-code="editPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Modern Sticky Toolbar (Add.vue pattern) -->
            <div class="mb-2 sticky-top z-3 bg-white" style="top: 0;">
                <div class="d-flex justify-content-end align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                    <div class="d-flex gap-2">
                        <button type="button" @click="savePurchase" class="btn btn-light text-primary shadow-sm px-4 fw-bold border-0" :disabled="isLoading || !voucher_no">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive me-2"></i> {{t('save_link')}}</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                    </div>

                    <div class="d-flex gap-2">
                        <router-link :to="{name : 'Purchase'}" id="close" class="btn btn-light shadow-sm text-danger fw-medium border-0 px-3">
                            <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                        </router-link>
                    </div>
                </div>
            </div>

            <form @submit.prevent="savePurchase">
                <!-- Integrated Header Card -->
                <div class="mb-2">
                    <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                        <div class="card-body p-2">
                            <div class="row g-4 align-items-end">
                                <!-- Invoice No -->
                                <div class="col-md-4">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('invoice_no')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                        <input type="text" :class="{'border-danger' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" placeholder="No Invoice">
                                    </div>
                                </div>
                                
                                <!-- Date -->
                                <div class="col-md-4">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('date')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-regular fa-calendar text-secondary"></i></span>
                                        <flat-pickr class="form-control bg-light border-0 fw-medium text-dark" v-model="pur_date" :config="config" />
                                    </div>
                                </div>

                                <!-- Supplier -->
                                <div class="col-md-4">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('supplier')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-truck-field text-secondary"></i></span>
                                        <input type="text" readonly :class="{'border-danger' : v$.supplier.name.$errors.length > 0}" v-model="supplier.name" class="form-control bg-light border-0 fw-medium text-dark cursor-pointer" placeholder="Select Supplier" data-bs-toggle="modal" data-bs-target="#searchSupplier">
                                        <button type="button" class="btn btn-light border-0 px-3" data-bs-toggle="modal" data-bs-target="#searchSupplier">
                                            <i class="fa-solid fa-magnifying-glass text-primary"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Product List Table -->
                <div class="vh-50 overflow-y-auto dataContainer bg-white rounded-3 shadow-sm border-0 mb-3" :class="{'border border-danger':v$.storePurchase.$errors.length > 0}">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="sticky-top z-1 text-nowrap">
                            <tr class="table-primary fw-bold small text-center cursor-pointer" data-bs-toggle="modal" data-bs-target="#searchCode">
                                <th class="py-2 border-0" colspan="2" style="width: 80px;">{{t('number')}}</th>
                                <th class="py-2 text-start border-0" style="min-width: 250px;">
                                    <i class="fa-solid fa-circle-plus me-1 text-primary"></i> {{t('item_name')}}
                                </th>
                                <th class="py-2 border-0">{{t('packing_size')}}</th>
                                <th class="py-2 border-0 table-primary-subtle">{{t('qty')}}</th>
                                <th class="py-2 border-0 text-center" colspan="2" style="width: 180px;">{{t('unit_price')}}</th>
                                <th class="py-2 border-0">{{t('amount')}}</th>
                                <th class="py-2 border-0" colspan="2">{{t('discount')}}</th>
                                <th class="py-2 border-0">{{t('unit_cost')}}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-if="storePurchase.filter(f => !f.is_remove).length === 0" class="text-center text-muted cursor-pointer" data-bs-toggle="modal" data-bs-target="#searchCode">
                                <td colspan="12" class="py-5">
                                    <i class="fa-solid fa-cart-plus fa-3x mb-3 opacity-25 text-primary"></i>
                                    <p class="mb-0 fw-medium">Click here to add items</p>
                                </td>
                            </tr>
                            <tr v-else v-for="(st, i) in storePurchase.filter(f => !f.is_remove)" :key="st.id" @mouseenter="setEdit(i)" class="small">
                                <td class="text-center border-0 px-1">
                                    <a class="text-danger opacity-75 hover-opacity-100" href="#!" @click="removeStoreItem(st)">
                                        <i class="fa-solid fa-circle-minus fs-5"></i>
                                    </a>
                                </td>
                                <td class="text-center text-muted border-0 ps-0">{{i+1}}</td>
                                <td class="fw-bold border-0">{{st.item_name}}</td>
                                <td class="text-center border-0">
                                    <span class="badge bg-light text-primary border border-primary-subtle px-2 py-1">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </span>
                                </td>
                                <td class="text-end fw-bold table-primary-subtle border-0 cursor-pointer" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                </td>
                                <td class="border-0 p-0" style="width: 100px;">
                                    <input @keyup="calEdit(i)" @change="calEdit(i)" type="number" v-model="st.qty_pur_price" class="form-control form-control-sm text-center border-0 bg-transparent fw-bold" placeholder="0">
                                </td>
                                <td class="border-0 p-0" style="width: 80px;">
                                    <select @change="calEdit(i)" class="form-select form-select-sm border-0 bg-transparent py-0 cursor-pointer text-center text-primary fw-medium" v-model="st.qty_pur_price_unit">
                                        <option v-for="pu in onlyString(st.packing_size)" :value="pu">{{pu}}</option>
                                    </select>
                                </td>
                                <td class="text-end fw-bold border-0">{{fM(st.amount)}}</td>
                                <td class="text-center border-0 cursor-pointer" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                    <div v-if="st.dis_qty" class="badge bg-danger-subtle text-danger">{{st.dis_qty}}{{st.dis_qty_unit}}</div>
                                    <div v-if="st.dis_amount" class="badge bg-danger-subtle text-danger">{{st.dis_amount}}$</div>
                                    <div v-if="st.dis_percent" class="badge bg-danger-subtle text-danger">{{st.dis_percent}}%</div>
                                    <span v-if="!st.dis_qty && !st.dis_amount && !st.dis_percent" class="text-muted opacity-50"><i class="fa-solid fa-tag"></i></span>
                                </td>
                                <td class="text-end text-danger fw-bold border-0 cursor-pointer" data-bs-toggle="modal" data-bs-target="#setDiscountModal">{{fM(st.discount)}}</td>
                                <td class="text-end fw-bold text-primary border-0">{{fM(st.unit_cost)}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Sticky Bottom Summary Bar (Add.vue pattern) -->
                <div class="sticky-bottom bg-white shadow-lg border-top py-3 px-4 z-2">
                    <div class="row align-items-center">
                        <!-- Left: Remarks -->
                        <div class="col-lg-3 mb-2 mb-lg-0">
                            <div class="form-floating">
                                <textarea v-model="remark" class="form-control border-0 bg-light" placeholder="Notes" id="remarkArea" style="height: 60px; resize: none;"></textarea>
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
                                    <div class="d-flex align-items-center justify-content-end">
                                        <input @focus="setInput" @blur="setInput" type="number" @keyup="calTotal" v-model="extra_discount" class="form-control form-control-sm text-end border-0 bg-transparent fw-bold text-danger p-0 fs-5" style="width: 100px;" placeholder="0">
                                    </div>
                                </div>

                                <!-- Net Amount (Highlighted Primary) -->
                                <div class="col-12 col-sm-4 col-md-3 text-end bg-primary text-white p-2 rounded-2 shadow-sm mx-2">
                                    <label class="small fw-bold d-block text-uppercase opacity-75" style="font-size: 0.7rem;">GRAND TOTAL</label>
                                    <div class="fs-3 fw-bolder lh-1">{{fM(total - extra_discount)}}</div>
                                </div>

                                <!-- Paid -->
                                <div class="col-6 col-md-2 text-end">
                                    <label class="small text-success fw-bold d-block text-uppercase mb-1">{{t('paid')}}</label>
                                    <div class="d-flex align-items-center justify-content-end">
                                        <input @focus="setInput" @blur="setInput" @keypress.enter="savePurchase" type="number" @keyup="calTotal" v-model="paid" class="form-control form-control-sm text-end border-0 bg-transparent fw-bold text-success p-0 fs-5" style="width: 100px;" placeholder="0">
                                    </div>
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
            </form>

            <ProductModal @setCode="setCode" :storeItems="storePurchase" />
            <SupplierModal @setCode="setSupplier" />
            <div class="p-3"></div>
        </div>

        <setQtyModal @setQty="setPurQty" :ei="editItem" />
        <setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />
    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required, email, helpers } from '@vuelidate/validators'
import { Timestamp, collection, onSnapshot, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment, deleteDoc } from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import ProductModalOld from '@/views/modals/purchase/ProductModalOld.vue'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
import SupplierModalOld from '@/views/modals/purchase/SupplierModalOld.vue'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/purchases"
import * as hp from "@/lib/helper"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setQtyModalOld from "@/views/modals/setQtyModalOld.vue";
import setDiscountModal from "@/views/modals/setDiscountModal.vue";
import setDiscountModalOld from "@/views/modals/setDiscountModalOld.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"
import { lang } from '@/lib/language'

export default {
    components: {
        PageHeader, 
        ProductModal: store.getters.theme === "new" ? ProductModal : ProductModalOld,
        SupplierModal: store.getters.theme === "new" ? SupplierModal : SupplierModalOld,
        Footer,
        setQtyModal: store.getters.theme === "new" ? setQtyModal : setQtyModalOld,
        setDiscountModal: store.getters.theme === "new" ? setDiscountModal : setDiscountModalOld,
        FlatPickr
    },
    setup() {
        return { v$: useVuelidate() }
    },
    validations() {
        return {
            voucher_no: { required },
            pur_date: { required },
            supplier: {
                name: { required }
            },
            storePurchase: { required }
        }
    },
    data() {
        return {
            id: "",
            location: "",
            old_location: "",
            tmp_exp_date: "",
            voucher_no: "",
            pur_date: "",
            supplier: {
                name: "",
            },
            purchase_item: {},
            storePurchase: [],
            editItem: {},
            extra_discount: 0,
            total_amount: 0,
            total_discount: 0,
            grand_total: 0,
            total: 0,
            paid: 0,
            balance: 0,
            remark: "",
            tmpStorePurchase: [],
            removeItems: [],

            isLoading: false,
            isDeleteing: false,
            msg: {
                type: "",
                info: "",
            },

        }
    },

    computed: {
        user() {
            return this.$store.getters.user;
        },
        general_supplier() {
            return this.$store.getters.general_supplier;
        },
        config() {
            return this.$store.getters.flat_pickr_config;
        },
        pageTitle() {
            const sl = this.$store.getters.currentLang;
            let pageTitle = sl == "mm" ? `${lang.purchase.mm} / ${lang.edit.mm}` : `${lang.purchase.eng} / ${lang.edit.eng}`
            return pageTitle;
        },
        t() {
            return (key) => this.$store.getters.t(key);
        },

    },
    mounted() {
        this.voucher_no = this.$route.params.id;
        this.fetchPurchase();
        this.checkDatabase();
        window.addEventListener('keydown', this.handleCtrl);

    },
    beforeUnmount() {
        window.removeEventListener('keydown', this.handleCtrl);
    },
    watch: {
        msg: {
            handler(val) {
                if (val.type) {
                    setTimeout(() => {
                        this.msg.type = "";
                        this.msg.info = "";
                    }, 3000)
                }
            }, deep: true
        }
    },

    methods: {
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        changeSupplier() {
            this.setSupplier(this.general_supplier)
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.savePurchase(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const des = document.getElementById("close")
                des.click();
            }
            return hc.handleCtrl(event)
        },

        onlyString(p) {
            if (p.largest_unit) {
                return {
                    largest_unit: p.largest_unit,
                    medium_unit: p.medium_unit,
                    smallest_unit: p.smallest_unit
                }
            }
            if (!p.largest_unit) {
                return {
                    medium_unit: p.medium_unit,
                    smallest_unit: p.smallest_unit
                }
            }
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty) {
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit) {
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        clearItems() {
            this.id = ""
            this.tmp_exp_date = ""
            this.voucher_no = ""
            this.pur_date = ""
            this.supplier.name = ""
            this.purchase_item = {}
            this.storePurchase = []
            this.editItem = {}
            this.extra_discount = 0
            this.total_amount = 0
            this.total_discount = 0
            this.total = 0;
            this.paid = 0;
            this.balance = 0;
            this.remark = "";
            this.tmpStorePurchase = [];
            this.removeItems = [];
            this.location = "";
        },
        removeExpDate(p) {
            return this.storePurchase.filter((f) => {
                if (p.id == f.id) {
                    f.tmp_exp_date = "";
                }
            })
        },
        setExpDate(id) {
            const arr = this.storePurchase.filter((f) => f.id == id)
            const p = arr[0]
            const exp_date = hp.insertDateNoTime(p.tmp_exp_date)
            this.editItem.exp_date = p.tmp_exp_date ? Timestamp.fromDate(exp_date) : null;
            p.exp_date = p.tmp_exp_date ? Timestamp.fromDate(exp_date) : null;
        },
        setPurchaseItem(p) {
            const pur_date = hp.setOldDate(p.pur_date)
            this.voucher_no = p.voucher_no;
            this.pur_date = pur_date
            this.supplier = p.supplier;
            this.total_amount = p.total_amount;
            this.total_discount = p.total_discount;
            this.extra_discount = p.extra_discount;
            this.total = p.total;
            this.grand_total = p.grand_total;
            this.storePurchase = p.products;
            this.tmpStorePurchase = p.products;
            this.supplier = p.supplier;
            this.id = p.id;
            this.location = p.location;
            this.old_location = p.location;
            this.paid = p.paid;
            this.remark = p.remark;
            this.balance = p.balance;
            this.storePurchase.map((s) => {
                return s.old_pur_qty = s.pur_qty;
            })
            this.storePurchase.map((s) => {
                return s.tmp_exp_date = s.tmp_exp_date ? s.tmp_exp_date : "";
            })
            this.calTotal(); // Ensure calculation is correct
        },
        async fetchPurchase() {
            const query_url = query(collection(db, "purchases"), where("voucher_no", "==", this.voucher_no));
            onSnapshot(query_url, (snapShot) => {
                const purchase = ss.purchases(snapShot);
                if (purchase.length > 0) {
                    this.setPurchaseItem(purchase[0])
                }
                this.isLoading = false;

            })
        },

        setSupplier(s) {
            if (s) {
                this.supplier = { ...s };
            }
        },

        saveDiscount(e) {
            return this.storePurchase.map((f) => {
                if (f.id === e.id) {
                    f.dis_qty = e.dis_qty ? e.dis_qty : null;
                    f.dis_qty_unit = e.dis_qty_unit ? e.dis_qty_unit : null;
                    f.dis_amount = e.dis_amount ? e.dis_amount : null;
                    f.dis_percent = e.dis_percent ? e.dis_percent : null;
                    f.discount = e.discount;
                    f.unit_cost = f.amount - e.discount;
                }
            }) && this.calTotal();
        },

        setPurQty(e) {
            let pur_qty = 0;
            const m_qty = Number(e.packing_size.medium_qty) || 1;
            const s_qty = Number(e.packing_size.smallest_qty) || 1;
            
            pur_qty += e.input_l_qty ? e.input_l_qty * (m_qty * s_qty) : 0;
            pur_qty += e.input_m_qty ? e.input_m_qty * s_qty : 0;
            pur_qty += e.input_s_qty ? e.input_s_qty : 0;

            let index = -1;
            this.storePurchase.forEach((f, i) => {
                if (f.id === e.id) {
                    f.pur_qty = pur_qty;
                    f.input_l_qty = e.input_l_qty || 0;
                    f.input_m_qty = e.input_m_qty || 0;
                    f.input_s_qty = e.input_s_qty || 0;
                    index = i;
                }
            });

            if (index !== -1) {
                this.calEdit(index);
            }
        },

        setEdit(i) {
            const item = this.storePurchase.filter(f => !f.is_remove)[i];
            if (!item) return;

            this.editItem = {
                ...item,
                input_l_qty: item.input_l_qty ? item.input_l_qty : 0,
                input_m_qty: item.input_m_qty ? item.input_m_qty : 0,
                input_s_qty: item.input_s_qty ? item.input_s_qty : 0
            };
        },
        calEdit(i) {
            const f = this.storePurchase[i];
            if (!f || !f.qty_pur_price_unit) return;

            const qty_pur_price = Number(f.qty_pur_price) || 0;
            const pur_qty = Number(f.pur_qty) || 0;

            const arr = Object.entries(f.packing_size);
            const units = arr.filter((ft) => ft[1] == f.qty_pur_price_unit);
            if (units.length === 0) return;

            const pur_unit = units[0][0];
            const m_qty = Number(f.packing_size.medium_qty) || 1;
            const s_qty = Number(f.packing_size.smallest_qty) || 1;

            let amount = 0;
            if (pur_unit == "largest_unit") {
                amount = Math.floor((qty_pur_price / (m_qty * s_qty)) * pur_qty);
            } else if (pur_unit == "medium_unit") {
                amount = Math.floor((qty_pur_price / s_qty) * pur_qty);
            } else if (pur_unit == "smallest_unit") {
                amount = Math.floor(qty_pur_price * pur_qty);
            }

            f.amount = amount;
            const discount = Number(f.discount) || 0;
            f.unit_cost = amount - discount;

            let smallest_price = 0;
            let medium_price = 0;
            let largest_price = 0;

            if (pur_unit == "largest_unit") {
                smallest_price = Math.floor(qty_pur_price / (m_qty * s_qty));
                medium_price = smallest_price * s_qty;
                largest_price = medium_price * m_qty;
            } else if (pur_unit == "medium_unit") {
                smallest_price = Math.floor(qty_pur_price / s_qty);
                medium_price = smallest_price * s_qty;
                largest_price = medium_price * m_qty;
            } else if (pur_unit == "smallest_unit") {
                smallest_price = Math.floor(qty_pur_price);
                medium_price = smallest_price * s_qty;
                largest_price = medium_price * m_qty;
            }

            f.pur_price = {
                largest_unit: largest_price,
                medium_unit: medium_price,
                smallest_unit: smallest_price
            };

            this.calTotal();
        },

        calTotal() {
            let total_amount = 0;
            let total = 0;
            let total_discount = 0;

            this.storePurchase.map((m) => {
                if (!m.is_remove) {
                    total_amount += Number(m.amount) || 0;
                    total += Number(m.unit_cost) || 0;
                    total_discount += Number(m.discount) || 0;
                }
            })

            this.total_amount = total_amount;
            this.total_discount = total_discount;
            this.total = total;
            this.grand_total = this.extra_discount ? this.total - this.extra_discount : this.total;
            this.balance = (this.total - this.extra_discount) - this.paid;
        },

        async removeStoreItem(item) {
            this.storePurchase = this.storePurchase.map((m) => {
                if (m.id == item.id) {
                    return {
                        ...m, is_remove: true, pur_qty: 0
                    }
                } else {
                    return m;
                }
            })
            this.calTotal();
        },
        storingPurchase() {
            this.storePurchase.push(this.purchase_item)
        },
        setCode(p) {
            this.purchase_item = {
                ...p,
                pur_qty: 1,
                old_pur_qty: 0,
                qty_pur_price: p.pur_price.smallest_unit,
                qty_pur_price_unit: p.packing_size.smallest_unit,
                amount: p.pur_price.smallest_unit,
                unit_cost: p.pur_price.smallest_unit,
                discount: 0,
                input_m_qty: 0,
                input_s_qty: 1,
            };
            const s = this.storePurchase;
            let temStore = []
            for (let i = 0; i < s.length; i++) {
                temStore.push(s[i].id)
            }
            const result = temStore.indexOf(p.id)
            if (result < 0) {
                this.storingPurchase();
            } else {
                // If it was removed, restore it
                this.storePurchase = this.storePurchase.map(m => {
                    if (m.id === p.id) {
                        return { ...m, is_remove: false, pur_qty: 1 };
                    }
                    return m;
                });
            }
            this.calTotal();
        },

        async checkDatabase() {
            const query_url = query(collection(db, "purchases"), where("voucher_no", "==", this.voucher_no));
            onSnapshot(query_url, async (snapShot) => {
                snapShot.docChanges().forEach((change) => {
                    if (change.type === "modified") {
                        if (change.doc.data().voucher_no == this.voucher_no) {
                            this.msg.type = "success";
                            this.msg.info = hp.message(change.type, "purchase")
                            this.isLoading = false;
                        }
                    }
                    if (change.type === "removed") {
                        this.msg.type = "success";
                        this.msg.info = "The voucher has been cancelled."
                        this.isLoading = false;
                        this.clearItems();
                    }
                });
            });
        },

        async savePurchase() {
            const validation = await this.v$.$validate()
            if (!validation) return false;

            const isNotReady = this.storePurchase.filter((f) => !f.is_remove && !f.unit_cost)
            if (isNotReady.length && this.storePurchase.filter(f => !f.is_remove).length > 0) return false;

            this.isLoading = true;
            this.msg.type = "";
            this.msg.info = "";

            this.updateProducts();
            this.updateStoreItem();

            const pur_date = hp.insertDate(this.pur_date)

            const voucher = {
                voucher_no: this.voucher_no,
                pur_date: Timestamp.fromDate(pur_date),
                supplier: this.supplier,
                products: this.storePurchase,
                user: { uid: this.user.uid, displayName: this.user.displayName },
                total_amount: this.total_amount,
                total_discount: this.total_discount,
                total: this.total,
                extra_discount: this.extra_discount,
                location: this.location,
                paid: this.paid,
                balance: this.balance,
                remark: this.remark,
            }

            const saveDocument = doc(db, "purchases", this.id)
            if (this.storePurchase.length <= 0) {
                await deleteDoc(saveDocument)
            } else {
                await updateDoc(saveDocument, voucher)
            }
            this.isLoading = false;
        },
        updateStoreItem() {
            const updateStore = this.storePurchase.filter((f) => f.pur_qty != 0)
            this.storePurchase = updateStore;
        },
        updateProducts() {
            if (this.storePurchase.length > 0) {
                this.storePurchase.map(async (p) => {
                    const id = p.id;
                    const pur_qty = p.pur_qty;
                    const old_pur_qty = p.old_pur_qty;
                    const updateDocument = doc(db, "products", id)
                    if (this.location === "Mart") {
                        await updateDoc(updateDocument, {
                            qty: pur_qty > old_pur_qty ? increment(pur_qty - old_pur_qty) : increment(-(old_pur_qty - pur_qty)),
                            pur_price: p.pur_price,
                        })
                    }
                    if (this.location === "Main") {
                        await updateDoc(updateDocument, {
                            main_qty: pur_qty > old_pur_qty ? increment(pur_qty - old_pur_qty) : increment(-(old_pur_qty - pur_qty)),
                            pur_price: p.pur_price,
                        })
                    }
                })
            }
        },

        editShowTime(t) {
            const date = t.toDate()
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },
        setInput(e) {
            if (e.target.value == 0) {
                e.target.value = ""
            }
            if (e.target.value == "") {
                e.target.value = 0;
            }
        },

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
.hover-opacity-100:hover {
    opacity: 1 !important;
}
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
.cursor-pointer {
    cursor: pointer;
}
</style>