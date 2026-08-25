<template>
  <div class="bg-light min-vh-100 pb-5">
    <PageHeader :page-title="t('stock_valuation')" page-code="inventory" />

    <div class="container-fluid py-3">
      
      <!-- Top Metrics Cards -->
      <div class="row g-3 mb-3">
        <div class="col-md-3">
          <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-body p-3">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <small class="text-muted text-uppercase fw-bold">Total Asset Value (Cost)</small>
                <i class="fa-solid fa-coins text-primary fs-5"></i>
              </div>
              <h4 class="fw-bolder text-primary mb-0 mt-1">{{ formatMoney(totalAssetValuation) }}</h4>
              <small class="text-muted"><i class="fa-solid fa-calculator me-1"></i> Based on {{ costingMethod.toUpperCase() }} Cost</small>
            </div>
          </div>
        </div>
        
        <div class="col-md-3">
          <div class="card border-0 shadow-sm rounded-3 h-100">
            <div class="card-body p-3">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <small class="text-muted text-uppercase fw-bold">Potential Retail Value</small>
                <i class="fa-solid fa-tags text-success fs-5"></i>
              </div>
              <h4 class="fw-bolder text-success mb-0 mt-1">{{ formatMoney(totalRetailValuation) }}</h4>
              <small class="text-success"><i class="fa-solid fa-chart-line me-1"></i> Margin ~{{ estMarginPct }}%</small>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="card border-0 shadow-sm rounded-3 h-100 cursor-pointer transition-all"
               :class="{'border border-dark border-2': search_status === 'all'}"
               @click="filterStatus('all')"
               title="Click to fetch All Stock items from server">
            <div class="card-body p-3">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <small class="text-muted text-uppercase fw-bold">Total SKUs Tracked</small>
                <i class="fa-solid fa-boxes-stacked text-dark fs-5"></i>
              </div>
              <h4 class="fw-bolder text-dark mb-0 mt-1">{{ totalDbProductCount || stockItems.length }} Products</h4>
              <small class="text-muted"><i class="fa-solid fa-database me-1 text-primary"></i> Total DB: {{ totalDbProductCount }} (Showing {{ filteredStockItems.length }})</small>
            </div>
          </div>
        </div>

        <div class="col-md-3">
          <div class="card border-0 shadow-sm rounded-3 h-100 transition-all position-relative"
               :class="{'border border-danger border-2': search_status === 'reorder' || search_status === 'outofstock' || search_status === 'alert_all'}">
            <div class="card-body p-3">
              <div class="d-flex justify-content-between align-items-center mb-1">
                <small class="text-muted text-uppercase fw-bold cursor-pointer" @click="filterStatus('alert_all')" title="Click to fetch All Stock Alerts directly from server">Stock Alerts</small>
                <i class="fa-solid fa-triangle-exclamation text-danger fs-5"></i>
              </div>
              <h4 class="fw-bolder text-danger mb-0 mt-1 cursor-pointer hover-opacity"
                  @click="filterStatus('alert_all')"
                  title="Click to fetch ALL Stock Alerts (Out of Stock + Reorder Alerts) directly from server">
                {{ displayLowStockCount }} Items
              </h4>
              <div class="mt-2 pt-1 border-top d-flex justify-content-between align-items-center flex-wrap gap-1">
                <span class="badge bg-danger text-white px-2 py-1 cursor-pointer hover-scale"
                      @click.stop="filterStatus('outofstock')"
                      title="Click to fetch Out of Stock items directly from server">
                  <i class="fa-solid fa-circle-exclamation me-1"></i> {{ displayOutOfStockCount }} Out of Stock
                </span>
                <span class="badge bg-warning-subtle text-warning-emphasis px-2 py-1 cursor-pointer hover-scale"
                      @click.stop="filterStatus('reorder')"
                      title="Click to fetch Reorder Alert items directly from server">
                  {{ displayReorderCount }} Reorder
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Filter & Search Toolbar (Sticky) -->
      <div class="card border-0 shadow-sm rounded-3 mb-3 bg-white sticky-top z-2" style="top: 0;">
        <div class="card-body p-3">
          <div class="row g-2 align-items-center">
            
            <!-- Code / SKU Search -->
            <div class="col-md-3 col-lg-3">
              <div class="input-group input-group-sm">
                <span class="input-group-text bg-light border-0"><i class="fa-solid fa-barcode text-secondary"></i></span>
                <input type="search" v-model="search_code" @keyup.enter="handleSearch" class="form-control bg-light border-0 ps-1" placeholder="Code / SKU...">
                <button v-if="search_code" class="btn btn-sm btn-light border-0 text-muted" type="button" @click="search_code = ''; handleSearch()" title="Clear Code">
                  <i class="fa-solid fa-xmark"></i>
                </button>
                <button class="btn btn-primary btn-sm px-2.5" type="button" @click="handleSearch" :disabled="isLoading" title="Search Code">
                  <i class="fa-solid fa-magnifying-glass"></i>
                </button>
              </div>
            </div>

            <!-- Description Search -->
            <div class="col-md-3 col-lg-3">
              <div class="input-group input-group-sm">
                <span class="input-group-text bg-light border-0"><i class="fa-solid fa-tag text-secondary"></i></span>
                <input type="search" v-model="search_name" @keyup.enter="handleSearch" class="form-control bg-light border-0 ps-1" placeholder="Product Name...">
                <button v-if="search_name" class="btn btn-sm btn-light border-0 text-muted" type="button" @click="search_name = ''; handleSearch()" title="Clear Name">
                  <i class="fa-solid fa-xmark"></i>
                </button>
                <button class="btn btn-primary btn-sm px-2.5" type="button" @click="handleSearch" :disabled="isLoading" title="Search Product Name">
                  <i class="fa-solid fa-magnifying-glass"></i>
                </button>
              </div>
            </div>

            <!-- Category Filter -->
            <div class="col-md-2 col-lg-2">
              <select v-model="search_category" class="form-select form-select-sm bg-light border-0">
                <option value="">All Categories</option>
                <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
              </select>
            </div>

            <!-- Status Filter -->
            <div class="col-md-2 col-lg-2">
              <select v-model="search_status" @change="fetchStockData" class="form-select form-select-sm bg-light border-0">
                <option value="all">All Products</option>
                <option value="alert_all">All Stock Alerts (Combined)</option>
                <option value="reorder">Reorder Alert Only</option>
                <option value="outofstock">Out of Stock Only</option>
                <option value="optimal">Optimal Stock</option>
              </select>
            </div>

            <!-- Action Buttons: Clear, Limit, Print -->
            <div class="col-md-2 col-lg-2 d-flex align-items-center gap-1 flex-wrap justify-content-end">
              <button class="btn btn-sm btn-light border shadow-sm text-muted text-nowrap px-2 fw-medium" @click="clearSearch" :disabled="isLoading" title="Clear Search & Filters">
                <i class="fa-solid fa-rotate-left me-1"></i> Clear
              </button>

              <select v-model="limitData" @change="fetchStockData" class="form-select form-select-sm bg-light border-0 flex-shrink-0" style="width: 75px;" title="Limit">
                <option value="50">50</option>
                <option value="100">100</option>
                <option value="250">250</option>
                <option value="500">500</option>
                <option value="all">All</option>
              </select>

              <button class="btn btn-sm btn-light border shadow-sm text-dark flex-shrink-0" @click="printReport" title="Print Report">
                <i class="fa-solid fa-print"></i>
              </button>
            </div>

          </div>
        </div>
      </div>

      <!-- Main Valuation Table -->
      <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
        <div class="card-header bg-white py-3 border-bottom d-flex justify-content-between align-items-center flex-wrap gap-2">
          <div class="d-flex align-items-center gap-2">
            <i class="fa-solid fa-calculator text-primary fs-5"></i>
            <h6 class="mb-0 fw-bold text-dark">Real-Time Inventory Asset Valuation Breakdown</h6>
          </div>
          <div class="d-flex align-items-center gap-2">
            <span class="small text-muted">Costing Method:</span>
            <select v-model="costingMethod" class="form-select form-select-sm border-secondary-subtle" style="width: 190px;">
              <option value="fifo">FIFO (First-In, First-Out)</option>
              <option value="wac">Weighted Average Cost (WAC)</option>
            </select>
          </div>
        </div>

        <div v-if="isLoading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">Loading...</span>
          </div>
          <p class="mt-2 text-muted fw-medium">Loading real-time stock valuation data...</p>
        </div>

        <div v-else class="table-responsive">
          <table class="table table-sm table-bordered table-hover align-middle mb-0 w-100" style="table-layout: fixed; width: 100%; font-size: 0.78rem;">
            <thead class="table-light text-muted small text-uppercase">
              <tr>
                <th class="ps-1 text-center" style="width: 12%;">SKU / Code</th>
                <th style="width: 25%;">Product Description</th>
                <th style="width: 11%;">Category</th>
                <th class="text-center" style="width: 10%;">On Hand Qty</th>
                <th class="text-end" style="width: 11%;">Unit Cost (Ks)</th>
                <th class="text-end" style="width: 11%;">Selling Price (Ks)</th>
                <th class="text-end pe-1" style="width: 12%;">Asset Value (Cost)</th>
                <th class="text-center" style="width: 8%;">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="filteredStockItems.length === 0">
                <td colspan="8" class="text-center text-muted py-5">
                  <i class="fa-solid fa-box-open fa-3x mb-3 text-secondary opacity-50"></i>
                  <p class="mb-0 fw-medium">No matching stock items found.</p>
                  <small v-if="stockItems.length === 0">Add products or stock entries in Inventory module to view valuation.</small>
                </td>
              </tr>
              <tr v-for="item in filteredStockItems" :key="item.id || item.code" :class="{'table-danger-subtle': item.qty <= 0}">
                <td class="ps-1 font-monospace fw-bold text-muted text-center text-break" style="font-size: 0.72rem; word-break: break-all;">{{ item.code }}</td>
                <td class="text-wrap text-break" style="word-break: break-word;">
                  <div class="fw-bold text-dark lh-sm">{{ item.name }}</div>
                  <small class="text-muted" style="font-size: 0.68rem;">Packing: {{ item.packingSizeStr }}</small>
                </td>
                <td class="text-wrap text-break" style="word-break: break-word;"><span class="badge bg-light text-dark border fw-semibold" style="font-size: 0.68rem;">{{ item.category }}</span></td>
                <td class="text-center font-monospace fw-bold" :class="item.qty <= 0 ? 'text-danger' : (item.qty <= item.minQty ? 'text-warning-emphasis' : 'text-dark')">
                  <div>{{ item.formattedQty }}</div>
                </td>
                <td class="text-end font-monospace">{{ formatMoney(item.unitCost) }}</td>
                <td class="text-end font-monospace text-muted">{{ formatMoney(item.salePrice) }}</td>
                <td class="text-end pe-1 font-monospace fw-bold text-primary">{{ formatMoney(Math.max(0, item.qty) * item.unitCost) }}</td>
                <td class="text-center">
                  <span v-if="item.qty <= 0" class="badge bg-danger text-white px-1 py-1" style="font-size: 0.62rem;">Out of Stock</span>
                  <span v-else-if="item.qty <= item.minQty" class="badge bg-warning-subtle text-warning-emphasis px-1 py-1" style="font-size: 0.62rem;">Reorder Alert</span>
                  <span v-else class="badge bg-success-subtle text-success px-1 py-1" style="font-size: 0.62rem;">Optimal</span>
                </td>
              </tr>
            </tbody>
            <tfoot class="table-light fs-6 fw-bold">
              <tr>
                <td colspan="6" class="text-end text-uppercase py-2" style="font-size: 0.75rem;">Filtered Total Inventory Asset Value:</td>
                <td class="text-end pe-1 font-monospace text-primary fs-6 py-2" style="font-size: 0.85rem;">{{ formatMoney(totalAssetValuation) }}</td>
                <td></td>
              </tr>
            </tfoot>
          </table>
        </div>

      </div>

    </div>
  </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue";
import { formatErpMoney } from "@/lib/erpHelper";
import { collection, getDocs, query, limit, getCountFromServer, where } from 'firebase/firestore';
import db from '@/firebase/firestore';
import * as ss from "@/lib/snapshot/products";
import * as hp from "@/lib/helper.js";

export default {
  name: "StockValuation",
  components: { PageHeader },
  data() {
    return {
      costingMethod: 'fifo',
      isLoading: false,
      limitData: '50', // Load all records by default to display full inventory data
      search_code: '',
      search_name: '',
      applied_search_code: '',
      applied_search_name: '',
      search_category: '',
      search_status: 'all',
      stockItems: [],
      totalDbProductCount: 0,
      serverOutOfStockCount: null,
      serverLowStockCount: null
    }
  },
  async mounted() {
    await this.fetchStockData();
  },
  computed: {
    t() {
      return (key) => this.$store.getters.t(key);
    },
    categories() {
      const cats = this.stockItems.map(i => i.category).filter(Boolean);
      return [...new Set(cats)];
    },
    filteredStockItems() {
      // Search results are retrieved directly from Firebase!
      return this.stockItems;
    },
    totalAssetValuation() {
      return this.filteredStockItems.reduce((sum, i) => sum + (Math.max(0, i.qty || 0) * (i.unitCost || 0)), 0);
    },
    totalRetailValuation() {
      return this.filteredStockItems.reduce((sum, i) => sum + (Math.max(0, i.qty || 0) * (i.salePrice || 0)), 0);
    },
    estMarginPct() {
      if (!this.totalRetailValuation) return 0;
      const profit = this.totalRetailValuation - this.totalAssetValuation;
      return Math.round((profit / this.totalRetailValuation) * 100);
    },
    lowStockCount() {
      return this.stockItems.filter(i => (i.qty || 0) <= (i.minQty || 10) || (i.qty || 0) <= 10).length;
    },
    outOfStockCount() {
      return this.stockItems.filter(i => (i.qty || 0) <= 0).length;
    },
    displayOutOfStockCount() {
      if (this.serverOutOfStockCount !== null && this.serverOutOfStockCount !== undefined) {
        return this.serverOutOfStockCount;
      }
      return this.outOfStockCount;
    },
    displayLowStockCount() {
      if (this.serverLowStockCount !== null && this.serverLowStockCount !== undefined) {
        return this.serverLowStockCount;
      }
      return this.lowStockCount;
    },
    displayReorderCount() {
      const total = this.displayLowStockCount;
      const outOfStock = this.displayOutOfStockCount;
      return Math.max(0, total - outOfStock);
    }
  },
  methods: {
    formatMoney(amount) {
      return formatErpMoney(amount);
    },
    async handleSearch() {
      this.applied_search_code = this.search_code;
      this.applied_search_name = this.search_name;
      if ((this.search_code || this.search_name) && this.limitData !== 'all') {
        this.limitData = 'all';
      }
      await this.fetchStockData();
    },
    async clearSearch() {
      this.search_code = '';
      this.search_name = '';
      this.applied_search_code = '';
      this.applied_search_name = '';
      this.search_category = '';
      this.search_status = 'all';
      this.limitData = '50';
      await this.fetchStockData();
    },
    async filterStatus(status) {
      this.search_status = status;
      // Reset text inputs to display complete server results for selected status
      this.search_code = '';
      this.search_name = '';
      this.applied_search_code = '';
      this.applied_search_name = '';
      this.search_category = '';
      await this.fetchStockData();
    },
    printReport() {
      window.print();
    },
    async fetchStockData() {
      this.isLoading = true;
      try {
        const coll = collection(db, "products");

        // 1. Fetch exact server counts directly from Firestore database
        try {
          const [totalSnap, outOfStockSnap, lowStockSnap] = await Promise.all([
            getCountFromServer(coll),
            getCountFromServer(query(coll, where("qty", "<=", 0))),
            getCountFromServer(query(coll, where("qty", "<=", 10)))
          ]);
          this.totalDbProductCount = totalSnap.data().count;
          this.serverOutOfStockCount = outOfStockSnap.data().count;
          this.serverLowStockCount = lowStockSnap.data().count;
        } catch (cErr) {
          console.warn("Could not get server counts:", cErr);
        }

        // 2. Direct Server-Side Firebase Queries based on search inputs & status
        let snapShot;
        if (this.search_code) {
          const codeVal = this.search_code.trim();
          let q = query(coll, where("code_no", "==", codeVal));
          snapShot = await getDocs(q);
          if (snapShot.empty && !isNaN(codeVal)) {
            q = query(coll, where("code_no", "==", Number(codeVal)));
            snapShot = await getDocs(q);
          }
        } else if (this.search_name) {
          const nameVal = this.search_name.trim().toLowerCase();
          const q = query(coll, where("keywords", "array-contains", nameVal));
          snapShot = await getDocs(q);
        } else if (this.search_category) {
          const q = query(coll, where("category", "==", this.search_category));
          snapShot = await getDocs(q);
        } else if (this.search_status === 'outofstock') {
          const q = query(coll, where("qty", "<=", 0));
          snapShot = await getDocs(q);
        } else if (this.search_status === 'reorder' || this.search_status === 'alert_all') {
          const q = query(coll, where("qty", "<=", 10));
          snapShot = await getDocs(q);
        } else if (this.search_status === 'optimal') {
          const q = query(coll, where("qty", ">", 10));
          snapShot = await getDocs(q);
        } else {
          let q;
          if (this.limitData === 'all') {
            q = query(coll);
          } else {
            const limitCount = parseInt(this.limitData) || 50;
            q = query(coll, limit(limitCount));
          }
          snapShot = await getDocs(q);
        }

        const rawProducts = ss.products(snapShot);

        if (rawProducts && rawProducts.length > 0) {
          this.stockItems = rawProducts.map(p => {
            const code = p.code_no || p.code || p.barcode || p.sku || 'N/A';
            const name = p.item_name || p.name || p.description || 'Unnamed Item';
            const category = p.category || p.cat_name || p.category_name || 'General';
            
            const qty = Number(p.qty !== undefined && p.qty !== null ? p.qty : (p.main_qty !== undefined && p.main_qty !== null ? p.main_qty : (p.inventory_bal !== undefined ? p.inventory_bal : 0)));
            const minQty = Number(p.min_qty || p.alert_qty || p.reorder_qty || 10);
            const unit = (typeof p.packing_size === 'object' && p.packing_size !== null ? (p.packing_size?.smallest_unit || p.packing_size?.largest_unit) : p.packing_size) || p.unit || 'Pcs';
            const rawPacking = p.packing_size || {};
            let packingSizeStr = '';
            let formattedQty = '';

            if (typeof rawPacking === 'object' && rawPacking !== null && (rawPacking.smallest_unit || rawPacking.medium_unit || rawPacking.largest_unit)) {
              packingSizeStr = hp.calPackingSize(
                rawPacking.largest_unit,
                rawPacking.medium_unit,
                rawPacking.smallest_unit,
                rawPacking.medium_qty,
                rawPacking.smallest_qty
              );
              formattedQty = hp.calPackingQty(
                rawPacking.medium_qty,
                rawPacking.smallest_qty,
                qty,
                rawPacking.largest_unit,
                rawPacking.medium_unit,
                rawPacking.smallest_unit
              ) || `${qty} ${unit}`;
            } else {
              packingSizeStr = typeof rawPacking === 'string' ? rawPacking : (p.unit || 'Pcs');
              formattedQty = `${qty} ${unit}`;
            }

            let unitCost = 0;
            if (typeof p.pur_price === 'object' && p.pur_price !== null) {
              unitCost = Number(p.pur_price?.smallest_unit || p.pur_price?.largest_unit || p.pur_price?.medium_unit || 0);
            } else if (p.pur_price !== undefined) {
              unitCost = Number(p.pur_price);
            } else if (p.cost !== undefined) {
              unitCost = Number(p.cost);
            }

            let salePrice = 0;
            if (typeof p.retail_price === 'object' && p.retail_price !== null) {
              salePrice = Number(p.retail_price?.smallest_unit || p.retail_price?.largest_unit || p.retail_price?.medium_unit || 0);
            } else if (p.retail_price !== undefined) {
              salePrice = Number(p.retail_price);
            } else if (p.sale_price !== undefined) {
              salePrice = Number(p.sale_price);
            } else if (p.price !== undefined) {
              salePrice = Number(p.price);
            }

            return {
              id: p.id,
              code: code,
              name: name,
              category: category,
              qty: qty,
              minQty: minQty,
              unit: unit,
              packingSizeStr: packingSizeStr,
              formattedQty: formattedQty,
              unitCost: unitCost,
              salePrice: salePrice
            };
          });
        } else {
          this.stockItems = [];
        }
      } catch (err) {
        console.error("Error fetching stock valuation products:", err);
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.hover-opacity:hover {
  opacity: 0.8;
}
.hover-scale {
  transition: transform 0.15s ease, opacity 0.15s ease;
}
.hover-scale:hover {
  transform: scale(1.05);
  opacity: 0.9;
}
@media print {
  .sticky-top, .btn, PageHeader, select {
    display: none !important;
  }
  .card {
    border: none !important;
    box-shadow: none !important;
  }
}
</style>
