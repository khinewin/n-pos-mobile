<template>
  <div class="reports-old-page">
    <PageHeader :page-title="pageTitle" page-code="reports" :msg-type="msg.type" :msg-info="msg.info" />

    <div class="container-fluid py-2 pb-5">
      <div class="row g-2">
        <!-- Filters Section -->
        <div class="col-sm-4 ">
          <div class="card border-0 shadow-sm rounded-4 overflow-hidden vh-80 overflow-y-auto bg-secondary-subtle">
            <div class="card-header  border-bottom-0 pt-2 px-2">
              <div class="d-flex align-items-center gap-2">
                <div class="icon-frame bg-primary bg-opacity-10 text-primary">
                  <i class="fa-solid fa-sliders"></i>
                </div>
                <h6 class="fw-bold text-dark mb-0">{{ t('report_filters') }}</h6>
              </div>
            </div>
            
            <div class="card-body p-2 pt-2">
              <div class="filter-grid">
                <!-- Date Row -->
                <div class="row g-2 mb-2">
                  <div class="col-lg-6">
                    <div class="modern-field">
                      <label class="field-label">{{ t('from_date') }}</label>
                      <div class="input-wrapper">
                        <i class="fa-solid fa-calendar-days input-icon text-muted"></i>
                        <flat-pickr class="form-control border-0" v-model="from_date" :config="config" />
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="modern-field">
                      <label class="field-label">{{ t('to_date') }}</label>
                      <div class="input-wrapper">
                        <i class="fa-solid fa-calendar-days input-icon text-muted"></i>
                        <flat-pickr class="form-control border-0" v-model="to_date" :config="config" />
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Product & Voucher Row -->
                <div class="row g-2 mb-2">
                  <div class="col-lg-6">
                    <div class="modern-field searchable" data-bs-toggle="modal" data-bs-target="#searchCode">
                      <label class="field-label">{{ t('item_name') }}</label>
                      <div class="input-wrapper bg-light bg-opacity-50">
                        <i class="fa-solid fa-magnifying-glass input-icon"></i>
                        <input readonly type="text" class="form-control border-0" v-model="item_name" :placeholder="t('item_name')">
                        <button v-if="item_name" @click.stop="item_name=''; code_no=''" class="clear-btn"><i class="fa-solid fa-xmark"></i></button>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="modern-field">
                      <label class="field-label">{{ t('invoice_no') }}</label>
                      <div class="input-wrapper">
                        <i class="fa-solid fa-receipt input-icon"></i>
                        <input type="text" class="form-control border-0" v-model="voucher_no" placeholder="N-">
                        <button v-if="voucher_no !== 'N-'" @click="voucher_no='N-'" class="clear-btn"><i class="fa-solid fa-xmark"></i></button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Entities Row -->
                <div class="row g-2 mb-2">
                  <div class="col-lg-6">
                    <div class="modern-field searchable" data-bs-toggle="modal" data-bs-target="#searchSupplier">
                      <label class="field-label">{{ t('supplier') }}</label>
                      <div class="input-wrapper bg-light bg-opacity-50">
                        <i class="fa-solid fa-truck-field input-icon"></i>
                        <input readonly type="text" class="form-control border-0" v-model="supplier.name" :placeholder="t('supplier')">
                        <button v-if="supplier.name" @click.stop="supplier={name:''}" class="clear-btn"><i class="fa-solid fa-xmark"></i></button>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="modern-field searchable" data-bs-toggle="modal" data-bs-target="#customerModal">
                      <label class="field-label">{{ t('customer') }}</label>
                      <div class="input-wrapper bg-light bg-opacity-50">
                        <i class="fa-solid fa-users input-icon"></i>
                        <input readonly type="text" class="form-control border-0" v-model="customer.name" :placeholder="t('customer')">
                        <button v-if="customer.name" @click.stop="customer={name:''}" class="clear-btn"><i class="fa-solid fa-xmark"></i></button>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- Advanced Options Row -->
                <div class="row g-2">
                  <div class="col-lg-6">
                    <div class="modern-field">
                      <label class="field-label">{{ t('adj_type') }}</label>
                      <div class="input-wrapper">
                        <i class="fa-solid fa-shuffle input-icon"></i>
                        <select v-model="adj_type" class="form-select border-0">
                          <option value="">All Types</option>
                          <option>Increment</option>
                          <option>Decrement</option>
                        </select>
                      </div>
                    </div>
                  </div>
                  <div class="col-lg-6">
                    <div class="modern-field">
                      <label class="field-label">{{ t('rts_rfs') }}</label>
                      <div class="input-wrapper">
                        <i class="fa-solid fa-circle-info input-icon"></i>
                        <select v-model="rts_rfs" class="form-select border-0">
                          <option value="">All Status</option>
                          <option>Yes</option>
                          <option>No</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="mt-2 d-grid">
                <button class="btn btn-light rounded-3 px-4 py-2 border text-muted transition-3 hover-danger" @click="clearInput">
                  <i class="fa-solid fa-rotate-left"></i> {{ t('clear_link') }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Report List Section -->
        <div class="col-sm-8 ">
          <div class="card border-0 shadow-sm rounded-4 bg-white vh-80 box-scroll bg-secondary-subtle">
         
             <div class="card-header  border-bottom-0 pt-2 px-2">
              <div class="d-flex align-items-center gap-2">
                <div class="icon-frame bg-primary bg-opacity-10 text-primary">
                  <i class="fa-solid fa-file-export text-primary"></i>
                </div>
                <h6 class="fw-bold text-dark mb-0">  {{ t('available_reports') }}</h6>
              </div>
            </div>
            <div class="card-body p-2 pt-0">
              <div class="row g-2 pt-2">
                <div class="col-md-6 col-lg-4" v-for="group in reportGroups" :key="group.titleKey">
                  <div class="report-group-card p-2 rounded-3 transition-3 d-flex flex-column align-items-start text-start justify-content-between" :class="group.colorCls">
                    <div class="d-flex align-items-center gap-3 mb-2">
                      <div class="group-icon-mini d-flex align-items-center justify-content-center bg-white rounded-2 shadow-sm">
                        <i :class="group.icon" class="small"></i>
                      </div>
                      <div class="lh-1">
                        <span class="fw-bold d-block text-dark small">{{ t(group.titleKey) }}</span>
                        <small class="text-muted opacity-100 small" >{{ group.items.length }} Reports</small>
                      </div>
                    </div>

                    <div class="dropdown w-100">
                      <button class="btn btn-white w-100 shadow-sm rounded-3 py-2 px-3 fw-semibold d-flex align-items-center justify-content-between transition-3" data-bs-toggle="dropdown">
                        <span class="small">{{ t('select_report') }}</span>
                        <i class="fa-solid fa-chevron-down small opacity-100"></i>
                      </button>
                      <ul class="dropdown-menu dropdown-menu-end shadow-lg border-0 rounded-3 py-1 mt-2 w-100 custom-dropdown">
                        <li v-for="item in group.items" :key="item.code">
                          <button class="dropdown-item py-2 px-3 d-flex align-items-center justify-content-between gap-3" @click="group.handler(item.code)">
                            <span class="report-name fw-medium small">{{ item.name }}</span>
                            <i class="fa-solid fa-arrow-right-to-bracket small text-muted opacity-100"></i>
                          </button>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <SupplierModal @setCode="setSupplier" />
    <CustomerModal @setCustomer="setCustomer" />
    <ProductModal @setCode="setCode" />
  </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue";
import store from "@/store";
import { useVuelidate } from '@vuelidate/core';
import { required, email, helpers } from '@vuelidate/validators';
import { Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query } from 'firebase/firestore';
import db from '@/firebase/firestore';
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue';
import SupplierModalOld from '@/views/modals/purchase/SupplierModalOld.vue';
import CustomerModal from '@/views/modals/sales/CustomerModal.vue';
import CustomerModalOld from '@/views/modals/sales/CustomerModalOld.vue';
import ProductModal from "@/views/modals/reports/ProductModal.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl";
import * as hp from "@/lib/helper";
import { lang } from '@/lib/language';

export default {
    components: {
        PageHeader,
        SupplierModal: store.getters.theme === "new" ? SupplierModal : SupplierModalOld,
        CustomerModal: store.getters.theme === "new" ? CustomerModal : CustomerModalOld,
        ProductModal,
        FlatPickr
    },
    setup() {
        return { v$: useVuelidate() }
    },
    data() {
        return {
            adj_type: "",
            rts_rfs: "",
            gt_equal: null,
            lt_equal: null,
            from_location: "Main",
            to_location: "Mart",
            item_name: "",
            location: "All",
            code_no: "",
            to_date: "",
            from_date: "",
            voucher_no: "N-",
            packing_unit: "S Unit",
            supplier: { name: "" },
            customer: { name: "" },
            category: "",
            msg: { type: "", info: "" },
        }
    },
    computed: {
        reportGroups() {
          const isCashier = this.user?.role === 'cashier';
          const groups = [
            { titleKey: 'product_code', icon: 'fa-solid fa-barcode', items: this.products, handler: this.reportProducts, colorCls: 'group-blue' },
            { titleKey: 'stocks', icon: 'fa-solid fa-boxes-stacked', items: this.stocks, handler: this.reportStocks, colorCls: 'group-cyan' },
            { titleKey: 'sale', icon: 'fa-solid fa-cart-shopping', items: this.sales, handler: this.reportSales, colorCls: 'group-green' },
            { titleKey: 'purchase', icon: 'fa-solid fa-truck-ramp-box', items: this.purchase, handler: this.reportPurchase, colorCls: 'group-orange' },
            { titleKey: 'adjustment', icon: 'fa-solid fa-sliders', items: this.adjustments, handler: this.reportAdjustments, colorCls: 'group-red' },
            { titleKey: 'soc', icon: 'fa-solid fa-file-invoice-dollar', items: this.soc, handler: this.reportSoc, colorCls: 'group-indigo' },
            //{ titleKey: 'expense', icon: 'fa-solid fa-wallet', items: this.expenses, handler: this.reportExpenses, colorCls: 'group-purple' },
          ];

          return groups.map(group => {
            const items = isCashier ? group.items.filter(i => i.cashier) : group.items;
            return { ...group, items };
          });
        },
        products: () => [
            { code: "stock_list", name: "Stock List", active: true , cashier: true},
            { code: "stock_list_sale_price", name: "Stock List Sale Price", active: true , cashier: true},
            { code: "stock_list_pur_price", name: "Stock List Pur Price", active: true , cashier: true},
            { code: "stock_list_pur_sale_price", name: "Stock List Pur & Sale Price", active: true , cashier: true},
        ],
        stocks: () => [
            { code: "by_date", name: "By Date", active: true , cashier: true},
            { code: "by_description", name: "By Product Name", active: true , cashier: true},
            { code: "by_voucher", name: "By Voucher", active: true , cashier: true},
        ],
        adjustments: () => [
            { code: "by_date", name: "By Date", active: true , cashier: true},
            { code: "by_description", name: "By Product Name", active: true , cashier: true},
            { code: "by_voucher", name: "By Voucher", active: true , cashier: true},
            { code: "by_location", name: "By Location", active: true , cashier: true},
            { code: "by_adj_type", name: "By Type", active: true , cashier: true},
        ],
        sales: () => [
            { code: "by_date", name: "By Date", active: true },
            { code: "by_top_ten", name: "Top 10 By Date", active: true },
            { code: "by_month", name: "By Month", active: true },
            { code: "by_top_ten_month", name: "Top 10 By Month", active: true },
            { code: "by_year", name: "By Year", active: true },
            { code: "by_top_ten_year", name: "Top 10 By Year", active: true },
            { code: "by_description", name: "By Product Name", active: true },
            { code: "by_description_date", name: "By Product Name & Date", active: true },
            { code: "by_voucher", name: "By Voucher", active: true , cashier: true},
            { code: "by_customer", name: "By Customer", active: true },
            { code: "by_customer_date", name: "By Customer & Date", active: true },
            { code: "by_chart", name: "By Chart", active: true },
        ],
        expenses: () => [
            { code: "by_date", name: "By Date", active: true , cashier: true},
            { code: "by_category", name: "By Category", active: true , cashier: true},
            { code: "by_voucher", name: "By Voucher", active: true , cashier: true},
            { code: "profit_by_date", name: "Sales Profit By Date", active: true},
        ],
        purchase: () => [
            { code: "by_date", name: "By Date", active: true , cashier: true},
            { code: "by_description", name: "By Product Name", active: true , cashier: true},
            { code: "by_voucher", name: "By Voucher", active: true , cashier: true},
            { code: "by_balance", name: "By Balance", active: true , cashier: true},
            { code: "by_supplier", name: "By Supplier", active: true , cashier: true},
            { code: "by_supplier_date", name: "By Supplier & Date", active: true , cashier: true},
        ],
        soc: () => [
            { code: "by_customer", name: "By Customer", active: true , cashier: true},
            { code: "by_customer_date", name: "By Customer & Date", active: true , cashier: true},
            { code: "by_voucher", name: "By Voucher", active: true , cashier: true},
        ],
        user() { return this.$store.getters.user; },
        config() { return this.$store.getters.flat_pickr_config; },
        pageTitle() {
            const sl = this.$store.getters.currentLang;
            return sl == "mm" ? `${lang.reports.mm}` : `${lang.reports.eng}`;
        },
        t() {
          return (key) => {
              const extra = {
                  'report_filters': 'Report Configuration',
                  'available_reports': 'Generate Reports',
                  'clear_all_filters': 'Reset Filters',
                  'select_item': 'Select Item...',
                  'select_supplier': 'Select Supplier...',
                  'select_customer': 'Select Customer...',
                  'select_report': 'Select Report'
              }
              return this.$store.getters.t(key) || extra[key] || key;
          };
        },
    },
    mounted() {
        this.from_date = hp.setToday();
        this.to_date = hp.setToday();
        window.addEventListener('keydown', this.handleCtrl);
    },
    beforeUnmount() {
        window.removeEventListener('keydown', this.handleCtrl);
    },
    methods: {
        handleCtrl(event) { return hc.handleCtrl(event) },
        setCode(p) { this.code_no = p.code_no; this.item_name = p.item_name; },
        clearInput() {
            this.voucher_no = "N-";
            this.supplier = { name: "" };
            this.customer = { name: "" };
            this.lt_equal = null;
            this.gt_equal = null;
            this.adj_type = "";
            this.rts_rfs = "";
            this.item_name = "";
            this.code_no = "";
            this.from_date = hp.setToday();
            this.to_date = hp.setToday();
        },
        setCustomer(c) { this.customer = c; },
        setSupplier(s) { this.supplier = s; },
        reportAdjustments(r_type) {
            this.$router.push({
                name: "ReportsAdjustments",
                params: { "reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date },
                query: {
                    voucher_no: this.voucher_no, "code_no": this.code_no || null, "item_name": this.item_name || null,
                    adj_type: this.adj_type || null, rts_rfs: this.rts_rfs || null, location: this.location
                }
            })
        },
        reportProducts(r_type) {
            this.$router.push({
                name: "ReportsProduct",
                params: { "reportType": r_type, "location": this.location },
                query: {
                    "code_no": this.code_no || null, "item_name": this.item_name || null,
                    gt_equal: this.gt_equal, lt_equal: this.lt_equal, packing_unit: this.packing_unit
                }
            })
        },
        reportStocks(r_type) {
            this.$router.push({
                name: "ReportsStocks",
                params: { "reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date, "location": this.location },
                query: { voucher_no: this.voucher_no, code_no: this.code_no, item_name: this.item_name }
            })
        },
        reportSales(r_type) {
            this.$router.push({
                name: "ReportsSales",
                params: { "reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date },
                query: {
                    voucher_no: this.voucher_no, customer_code: this.customer.code || null,
                    customer_name: this.customer.name || null, code_no: this.code_no || null, item_name: this.item_name || null
                }
            })
        },
        reportExpenses(r_type) {
            this.$router.push({
                name: "ReportsExpenses",
                params: { "reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date },
                query: {
                    voucher_no: this.voucher_no, category_code: this.category.code || null,
                    category_name: this.category.name || null,
                }
            })
        },
        reportPurchase(r_type) {
            this.$router.push({
                name: "ReportsPurchase",
                params: { "reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date, "location": this.location },
                query: {
                    voucher_no: this.voucher_no || null,
                    supplier: this.supplier.code || null,
                    supplier_name: this.supplier.code ? this.supplier.name : null,
                    supplier_company: this.supplier.code ? this.supplier.company : null,
                    "code_no": this.code_no || null, "item_name": this.item_name || null
                }
            })
        },
        reportSoc(r_type) {
            this.$router.push({
                name: "ReportsSoc",
                params: { "reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date },
                query: {
                    voucher_no: this.voucher_no, customer_code: this.customer.code || null,
                    customer_name: this.customer.name || null, code_no: this.code_no || null, item_name: this.item_name || null
                }
            })
        },
    }
}
</script>

<style scoped>
.reports-old-page {
  background-color: #fcfcfd;
  min-height: 100vh;
  font-family: 'Roboto', 'Inter', sans-serif;
}

/* Common UI Elements */
.transition-3 { transition: all 0.3s ease; }
.hover-danger:hover { color: #dc3545 !important; border-color: #dc3545 !important; }

.icon-frame {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
}

/* Modern Fields */
.modern-field {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}
.field-label {
  font-size: 0.725rem;
  font-weight: 700;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding-left: 2px;
}
.input-wrapper {
  background: #f8f9fa;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 12px;
  display: flex;
  align-items: center;
  padding: 0.2rem 0.5rem;
  transition: all 0.2s ease;
}
.input-wrapper:focus-within {
  background: #fff;
  border-color: #0d6efd;
  box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.08);
}
.input-icon {
  padding: 0 0.6rem;
  font-size: 0.9rem;
  color: #adb5bd;
}
.input-wrapper .form-control, 
.input-wrapper .form-select {
  box-shadow: none !important;
  font-size: 0.875rem;
  padding: 0.6rem 0.4rem;
  color: #495057;
  font-weight: 500;
}
.searchable { cursor: pointer; }

.clear-btn {
  background: none;
  border: none;
  color: #adb5bd;
  padding: 0 0.6rem;
  font-size: 0.8rem;
  opacity: 0.6;
}
.clear-btn:hover { opacity: 1; color: #dc3545; }

.report-group-card {
  border: 1px solid rgba(0, 0, 0, 0.03);
  min-height: auto;
  position: relative;
  z-index: 1;
}
.report-group-card:focus-within {
  z-index: 1060; /* Higher than regular cards when interacting with dropdown */
}
.custom-dropdown {
  z-index: 9999 !important;
  position: absolute !important;
}
.box-scroll {
  overflow-y: auto;
  overflow-x: visible !important; /* Extremely important for dropdown visibility */
}
.group-icon-mini {
  width: 30px;
  height: 30px;
  flex-shrink: 0;
}
.report-group-card:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.08) !important;
  border-color: rgba(13, 110, 253, 0.2) !important;
}

.hover-lift:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.08) !important;
  border-color: rgba(13, 110, 253, 0.2) !important;
}

/* Color Palettes */
.group-blue { background-color: #f0f7ff !important; color: #0d6efd !important; }
.group-cyan { background-color: #f0fdff !important; color: #00acc1 !important; }
.group-green { background-color: #f2fff4 !important; color: #2e7d32 !important; }
.group-orange { background-color: #fff9f0 !important; color: #ef6c00 !important; }
.group-red { background-color: #fff5f5 !important; color: #c62828 !important; }
.group-indigo { background-color: #f5f6ff !important; color: #3f51b5 !important; }
.group-purple { background-color: #faf5ff !important; color: #8e24aa !important; }

@media (max-width: 991.98px) {
  .card { min-height: auto !important; }
}
</style>