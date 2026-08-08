<template>
  <div class="reports-page">
    <PageHeader :page-title="pageTitle" page-code="reports" :msg-type="msg.type" :msg-info="msg.info" />

    <div class="container-fluid py-4">
      <div class="row g-4">
        <!-- Filters Sidebar -->
        <div class="col-lg-3 col-xl-3">
          <div class="filter-card shadow-sm border-0 p-2 sticky-top" style="top: 20px; z-index: 10;">
            <div class="card-header bg-transparent border-0 pt-3 pb-0">
              <h6 class="text-uppercase fw-bold text-muted small"><i class="fa-solid fa-sliders me-2"></i>{{ t('filters') }}</h6>
            </div>
            <div class="card-body">
              <!-- Date Range -->
              <div class="mb-4">
                <label class="form-label text-muted small fw-semibold">{{ t('from_date') }}</label>
                <div class="input-group mb-2 shadow-sm rounded-3 overflow-hidden">
                  <span class="input-group-text bg-white border-0"><i class="fa-regular fa-calendar text-primary"></i></span>
                  <flat-pickr
                    class="form-control border-0 bg-white ps-0"
                    v-model="from_date"
                    :config="config"
                    placeholder="Start Date"
                  />
                </div>
                <label class="form-label text-muted small fw-semibold">{{ t('to_date') }}</label>

                <div class="input-group shadow-sm rounded-3 overflow-hidden">
                  <span class="input-group-text bg-white border-0"><i class="fa-regular fa-calendar-check text-primary"></i></span>
                  <flat-pickr
                    class="form-control border-0 bg-white ps-0"
                    v-model="to_date"
                    :config="config"
                    placeholder="End Date"
                  />
                </div>
              </div>

              <!-- Search Inputs -->
              <div class="mb-4">
                
                <div class="mb-2 input-wrapper shadow-sm rounded-3">
                  <input
                    data-bs-toggle="modal"
                    readonly
                    data-bs-target="#searchCode"
                    type="text"
                    class="form-control border-0 bg-white"
                    v-model="item_name"
                    :placeholder="t('item_name')"
                  />
                  <i class="fa-solid fa-magnifying-glass input-icon text-muted"></i>
                  <button v-if="item_name" @click="item_name=''; code_no=''" class="btn btn-link btn-sm p-0 clear-btn text-muted"><i class="fa-solid fa-times"></i></button>
                </div>

                <div class="mb-2 input-wrapper shadow-sm rounded-3">
                   <input
                    type="text"
                    class="form-control border-0 bg-white"
                    v-model="voucher_no"
                    placeholder="Voucher No."
                  />
                  <i class="fa-solid fa-receipt input-icon text-muted"></i>
                   <button v-if="voucher_no && voucher_no!=='N-'" @click="voucher_no='N-'" class="btn btn-link btn-sm p-0 clear-btn text-muted"><i class="fa-solid fa-times"></i></button>
                </div>
              </div>

              <!-- Parties -->
              <div class="mb-4">
                <div class="mb-2 input-wrapper shadow-sm rounded-3">
                  <input
                    data-bs-toggle="modal"
                    readonly
                    data-bs-target="#searchSupplier"
                    type="text"
                    class="form-control border-0 bg-white"
                    v-model="supplier.name"
                    :placeholder="t('supplier')"
                  />
                  <i class="fa-solid fa-truck-field input-icon text-muted"></i>
                  <button v-if="supplier.name" @click="supplier={}" class="btn btn-link btn-sm p-0 clear-btn text-muted"><i class="fa-solid fa-times"></i></button>
                </div>

                <div class="mb-2 input-wrapper shadow-sm rounded-3">
                  <input
                    data-bs-toggle="modal"
                    readonly
                    data-bs-target="#customerModal"
                    type="text"
                    class="form-control border-0 bg-white"
                    v-model="customer.name"
                    :placeholder="t('customer')"
                  />
                  <i class="fa-solid fa-users input-icon text-muted"></i>
                   <button v-if="customer.name" @click="customer={}" class="btn btn-link btn-sm p-0 clear-btn text-muted"><i class="fa-solid fa-times"></i></button>
                </div>
              </div>

               <!-- Advanced Filters -->
               <div class="mb-3">
                  <button class="btn btn-light w-100 d-flex justify-content-between align-items-center text-muted small" type="button" data-bs-toggle="collapse" data-bs-target="#advancedFilters">
                      {{ t('advanced_filters') }} <i class="fa-solid fa-chevron-down"></i>
                  </button>
                  <div class="collapse mt-2" id="advancedFilters">
                      <div class="card card-body bg-light border-0 p-2 rounded-3">
                           <div class="mb-2">
                              <label class="small text-muted mb-1">{{ t('adj_type') }}</label>
                              <select v-model="adj_type" class="form-select border-0 shadow-sm text-small">
                                  <option value="">All</option>
                                  <option>Increment</option>
                                  <option>Decrement</option>
                              </select>
                          </div>
                          <div class="mb-0">
                              <label class="small text-muted mb-1">{{ t('rts_rfs') }}</label>
                              <select v-model="rts_rfs" class="form-select border-0 shadow-sm text-small">
                                  <option value="">All</option>
                                  <option>Yes</option>
                                  <option>No</option>
                              </select>
                          </div>
                      </div>
                  </div>
              </div>

              <button class="btn btn-outline-danger w-100 rounded-3 shadow-sm mt-2" @click="clearInput">
                  <i class="fa-solid fa-rotate-left me-2"></i> {{ t('clear_link') }}
              </button>

            </div>
          </div>
        </div>

        <!-- Reports Grid -->
        <div class="col-lg-9 col-xl-9">
          
          <!-- Category Tabs -->
          <div class="nav nav-pills mb-4 overflow-x-auto gap-2 flex-nowrap pb-2 report-tabs" id="pills-tab" role="tablist">
               <button 
                  v-for="cat in reportCategories" 
                  :key="cat.id"
                  class="nav-link rounded-pill px-4 py-2 shadow-sm border-0 d-flex align-items-center gap-2"
                  :class="{ active: activeTab === cat.id }"
                  @click="activeTab = cat.id"
               >
                  <i :class="cat.icon"></i>
                  <span class="fw-semibold">{{ cat.label }}</span>
               </button>
          </div>

          <!-- Report Cards -->
          <div class="row g-3">
              <template v-for="(group, groupName) in filteredReports" :key="groupName">
                  <div class="col-12 mt-4 mb-2" v-if="activeTab === 'all' || activeTab === group.id">
                       <h5 class="fw-bold text-dark border-start border-4 border-primary ps-3 mb-0">{{ group.label }}</h5>
                  </div>
                  
                  <div class="col-md-6 col-lg-4 col-xxl-3" v-for="report in group.items" :key="report.code" v-show="report.active && (activeTab === 'all' || activeTab === group.id)">
                      <div class="card h-100 report-card border-0 shadow-sm" @click="handleReportClick(group.handler, report.code)">
                          <div class="card-body p-4 d-flex flex-column align-items-center text-center justify-content-center">
                              <div class="icon-box mb-3 rounded-circle" :class="group.colorClass">
                                  <i :class="report.customIcon || group.defaultIcon" class="fs-4"></i>
                              </div>
                              <h6 class="card-title fw-bold text-dark mb-1">{{ report.name }}</h6>
                              <p class="card-text text-muted small mb-0">{{ getReportDescription(report.code) }}</p>
                          </div>
                          <div class="card-footer bg-transparent border-0 text-center pb-3 pt-0">
                              <span class="text-primary small fw-bold generate-btn">{{ t('generate') }} <i class="fa-solid fa-arrow-right ms-1"></i></span>
                          </div>
                      </div>
                  </div>
              </template>
          </div>

          <!-- Empty State -->
           <div v-if="Object.keys(filteredReports).length === 0" class="text-center py-5 text-muted">
              <i class="fa-regular fa-folder-open fa-3x mb-3 opacity-50"></i>
              <p>No reports found for this category.</p>
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
import SupplierModal from "@/views/modals/purchase/SupplierModal.vue";
import SupplierModalOld from "@/views/modals/purchase/SupplierModalOld.vue";
import CustomerModal from "@/views/modals/sales/CustomerModal.vue";
import CustomerModalOld from "@/views/modals/sales/CustomerModalOld.vue";
import ProductModal from "@/views/modals/reports/ProductModal.vue";
import FlatPickr from "vue-flatpickr-component";
import * as hc from "@/lib/handleCtrl";
import * as hp from "@/lib/helper";
import { lang } from "@/lib/language";
import { computed, defineComponent } from 'vue';

export default {
  components: {
    PageHeader,
    SupplierModal: store.getters.theme === "new" ? SupplierModal : SupplierModalOld,
    CustomerModal: store.getters.theme === "new" ? CustomerModal : CustomerModalOld,
    ProductModal,
    FlatPickr,
  },
  data() {
    return {
      activeTab: 'all',
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
      msg: { type: "", info: "" },
      
      // Data Structure for Reports
      reportsData: {
          products: {
              id: 'inventory',
              label: 'Inventory',
              colorClass: 'bg-primary-subtle text-primary',
              defaultIcon: 'fa-solid fa-box',
              handler: 'reportProducts',
              items: [
                  { code: "stock_list", name: "Stock List", active: true, customIcon: "fa-solid fa-clipboard-list" },
                  { code: "stock_list_sale_price", name: "Sale Price List", active: true, customIcon: "fa-solid fa-tag" },
                  { code: "stock_list_pur_price", name: "Purchase Price List", active: true, customIcon: "fa-solid fa-tags" },
                  { code: "stock_list_pur_sale_price", name: "Full Price List", active: true, customIcon: "fa-solid fa-file-invoice-dollar" },
              ]
          },
          stocks: {
               id: 'inventory',
               label: 'Stock Movement',
               colorClass: 'bg-info-subtle text-info',
               defaultIcon: 'fa-solid fa-boxes-stacked',
               handler: 'reportStocks',
               items: [
                  { code: "by_date", name: "Movement By Date", active: true },
                  { code: "by_description", name: "Movement By Product", active: true },
                  { code: "by_voucher", name: "Movement By Voucher", active: true },
               ]
          },
          sales: {
              id: 'sales',
              label: 'Sales',
              colorClass: 'bg-success-subtle text-success',
              defaultIcon: 'fa-solid fa-cash-register',
              handler: 'reportSales',
               items: [
                  { code: "by_date", name: "Sales By Date", active: true },
                  { code: "by_top_ten", name: "Top 10 Sales By Date", active: true, customIcon: "fa-solid fa-trophy" },
                  { code: "by_month", name: "Sales By Month", active: true, customIcon: "fa-solid fa-calendar-days" },
                  { code: "by_top_ten_month", name: "Top 10 Sales By Month", active: true, customIcon: "fa-solid fa-award" },
                  { code: "by_year", name: "Sales By Year", active: true, customIcon: "fa-solid fa-calendar" },
                  { code: "by_top_ten_year", name: "Top 10 Sales By Year", active: true, customIcon: "fa-solid fa-crown" },
                  { code: "by_description", name: "Sales By Product", active: true },
                  { code: "by_description_date", name: "Product & Date", active: true },
                  { code: "by_voucher", name: "Sales By Voucher", active: true },
                  { code: "by_customer", name: "Sales By Customer", active: true },
                  { code: "by_customer_date", name: "Customer & Date", active: true },
                  { code: "by_chart", name: "Sales Chart", active: true, customIcon: "fa-solid fa-chart-line" },
               ]
          },
          purchase: {
              id: 'purchase',
              label: 'Purchase',
              colorClass: 'bg-warning-subtle text-warning',
              defaultIcon: 'fa-solid fa-truck-moving',
              handler: 'reportPurchase',
              items: [
                  { code: "by_date", name: "Purchase By Date", active: true },
                  { code: "by_description", name: "Purchase By Product", active: true },
                  { code: "by_voucher", name: "Purchase By Voucher", active: true },
                  { code: "by_balance", name: "Purchase Balance", active: true },
                  { code: "by_supplier", name: "Purchase By Supplier", active: true },
                  { code: "by_supplier_date", name: "Supplier & Date", active: true },
              ]
          },
          adjustments: {
              id: 'adjustments',
              label: 'Adjustments',
              colorClass: 'bg-danger-subtle text-danger',
              defaultIcon: 'fa-solid fa-sliders',
              handler: 'reportAdjustments',
              items: [
                  { code: "by_date", name: "By Date", active: true },
                  { code: "by_description", name: "By Product", active: true },
                  { code: "by_voucher", name: "By Voucher", active: true },
                  { code: "by_location", name: "By Location", active: true },
                  { code: "by_adj_type", name: "By Type", active: true },
              ]
          },
           soc: {
              id: 'sales', // Group under Sales
              label: 'Outstanding (SOC)',
              colorClass: 'bg-secondary-subtle text-secondary',
              defaultIcon: 'fa-solid fa-file-invoice',
              handler: 'reportSoc',
               items: [
                  { code: "by_customer", name: "By Customer", active: true },
                  { code: "by_customer_date", name: "By Customer & Date", active: true },
                  { code: "by_voucher", name: "By Voucher", active: true },
               ]
           },
      }
    };
  },
  computed: {
    filteredReports(){
        return this.reportsData;
    },
    reportCategories(){
        return [
            { id: 'all', label: 'All', icon: 'fa-solid fa-layer-group' },
            { id: 'sales', label: 'Sales', icon: 'fa-solid fa-cash-register' },
            { id: 'purchase', label: 'Purchase', icon: 'fa-solid fa-truck-moving' },
            { id: 'inventory', label: 'Inventory', icon: 'fa-solid fa-boxes-stacked' },
            { id: 'adjustments', label: 'Adjustments', icon: 'fa-solid fa-wrench' },
        ]
    },
    user() {
      return this.$store.getters.user;
    },
    config() {
      return this.$store.getters.flat_pickr_config;
    },
    pageTitle() {
      const sl = this.$store.getters.currentLang;
      return sl == "mm" ? `${lang.reports.mm}` : `${lang.reports.eng}`;
    },
    t() {
      return (key) => this.$store.getters.t(key);
    },
  },
  mounted() {
    this.from_date = hp.setToday();
    this.to_date = hp.setToday();
    window.addEventListener("keydown", this.handleCtrl);
  },
  beforeUnmount() {
    window.removeEventListener("keydown", this.handleCtrl);
  },
  methods: {
    handleCtrl(event) {
      return hc.handleCtrl(event);
    },
    setCode(p) {
      this.code_no = p.code_no;
      this.item_name = p.item_name;
    },
    getReportDescription(code) {
        const descriptions = {
            'stock_list': 'View current stock levels',
            'by_date': 'Report filtered by date range',
            'by_chart': 'Visual analytics graph',
            // Add more descriptions as needed
        }
        return descriptions[code] || 'Generate detailed report';
    },
    handleReportClick(handlerName, code) {
        if (this[handlerName]) {
            this[handlerName](code);
        }
    },
    clearInput() {
      this.voucher_no = "N-";
      this.supplier = {};
      this.customer = {};
      this.lt_equal = null;
      this.gt_equal = null;
      this.from_date = hp.setToday();
      this.to_date = hp.setToday();
      this.item_name = "";
      this.code_no = "";
      this.adj_type = "";
      this.rts_rfs = "";
    },
    setCustomer(c) {
      this.customer = c;
    },
    setSupplier(s) {
      this.supplier = s;
    },
    reportAdjustments(r_type) {
      this.$router.push({
        name: "ReportsAdjustments",
        params: { reportType: r_type, fromDate: this.from_date, toDate: this.to_date },
        query: {
          voucher_no: this.voucher_no,
          code_no: this.code_no || null,
          item_name: this.item_name || null,
          adj_type: this.adj_type || null,
          rts_rfs: this.rts_rfs || null,
          location: this.location,
        },
      });
    },
    reportProducts(r_type) {
      this.$router.push({
        name: "ReportsProduct",
        params: { reportType: r_type, location: this.location },
        query: {
          code_no: this.code_no || null,
          item_name: this.item_name || null,
          gt_equal: this.gt_equal,
          lt_equal: this.lt_equal,
          packing_unit: this.packing_unit,
        },
      });
    },
    reportStocks(r_type) {
      this.$router.push({
        name: "ReportsStocks",
        params: {
          reportType: r_type,
          fromDate: this.from_date,
          toDate: this.to_date,
          location: this.location,
        },
        query: {
          voucher_no: this.voucher_no,
          code_no: this.code_no,
          item_name: this.item_name,
        },
      });
    },
   
    reportSales(r_type) {
      this.$router.push({
        name: "ReportsSales",
        params: {
          reportType: r_type,
          fromDate: this.from_date,
          toDate: this.to_date,
        },
        query: {
          voucher_no: this.voucher_no,
          customer_code: this.customer.code || null,
          customer_name: this.customer.name || null,
          code_no: this.code_no || null,
          item_name: this.item_name || null,
        },
      });
    },
    reportPurchase(r_type) {
      this.$router.push({
        name: "ReportsPurchase",
        params: {
          reportType: r_type,
          fromDate: this.from_date,
          toDate: this.to_date,
          location: this.location,
        },
        query: {
          voucher_no: this.voucher_no,
          supplier: this.supplier.code || null,
          supplier_name: this.supplier.name || null,
          supplier_company: this.supplier.company || null,
          code_no: this.code_no || null,
          item_name: this.item_name || null,
        },
      });
    },
    reportSoc(r_type) {
      this.$router.push({
        name: "ReportsSoc",
        params: {
          reportType: r_type,
          fromDate: this.from_date,
          toDate: this.to_date,
        },
        query: {
          voucher_no: this.voucher_no,
          customer_code: this.customer.code || null,
          customer_name: this.customer.name || null,
          code_no: this.code_no || null,
          item_name: this.item_name || null,
        },
      });
    },
  },
};
</script>

<style scoped>
.reports-page {
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* Filter Sidebar */
.filter-card {
  background: #ffffff;
  border-radius: 16px !important;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border: 1px solid transparent;
  transition: all 0.2s ease;
}
.input-wrapper:focus-within {
  border-color: #dee2e6;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(0, 0, 0, 0.05);
}
.input-wrapper input {
  background: transparent !important;
  padding-left: 36px;
  padding-right: 30px;
  font-size: 0.9rem;
}
.input-wrapper input:focus {
   box-shadow: none;
}
.input-icon {
  position: absolute;
  left: 12px;
  font-size: 14px;
  opacity: 0.5;
}
.clear-btn {
  position: absolute;
  right: 10px;
  opacity: 0.5;
}
.clear-btn:hover {
    opacity: 1;
}

/* Report Tabs */
.nav-pills .nav-link {
  background: #ffffff;
  color: #495057;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  white-space: nowrap;
}
.nav-pills .nav-link:hover {
  transform: translateY(-2px);
}
.nav-pills .nav-link.active {
  background: #0d6efd; /* Bootstrap Primary */
  color: #fff;
  box-shadow: 0 4px 10px rgba(13, 110, 253, 0.3) !important;
}

/* Report Cards */
.report-card {
  border-radius: 16px !important;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  background: #ffffff;
  position: relative;
  overflow: hidden;
}
.report-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.08) !important;
}
.report-card:hover .icon-box {
    transform: scale(1.1);
}
.report-card:hover .generate-btn {
    color: #0b5ed7 !important;
}
.icon-box {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

/* Scrollbar for tabs on small screens */
.report-tabs::-webkit-scrollbar {
    height: 4px;
}
.report-tabs::-webkit-scrollbar-thumb {
    background: #dee2e6;
    border-radius: 4px;
}

/* Responsive */
@media (max-width: 991.98px) {
    .filter-card {
        position: static !important;
        margin-bottom: 20px;
    }
}
</style>
