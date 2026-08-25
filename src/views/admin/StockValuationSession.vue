<template>
  <div class="card border-0 shadow-sm rounded-4 modern-val-card h-100 bg-white">
    <div class="card-body p-3.5 d-flex flex-column justify-content-between">
      
      <!-- Top Header -->
      <div class="d-flex align-items-center justify-content-between mb-3">
        <div class="d-flex align-items-center">
          <div class="val-icon-box text-white rounded-3 d-flex align-items-center justify-content-center me-3">
            <i class="fa-solid fa-coins fs-6"></i>
          </div>
          <div>
            <h6 class="mb-0 fw-bolder text-dark tracking-tight">{{ t('stock_valuation') }}</h6>
            <small class="text-muted fw-medium" style="font-size: 0.68rem;">Inventory Asset Overview</small>
          </div>
        </div>
        
        <router-link :to="{ name: 'StockValuation' }" class="btn btn-sm btn-light border rounded-pill px-3 py-1 fw-bold view-btn text-primary" style="font-size: 0.72rem;">
          View Report <i class="fa-solid fa-arrow-right-long ms-1"></i>
        </router-link>
      </div>

      <!-- Main Valuation Amount Container -->
      <div class="valuation-display-box p-3 rounded-3 mb-3 text-center position-relative overflow-hidden">
        <span class="text-uppercase fw-bold text-muted d-block tracking-wider mb-1" style="font-size: 0.62rem; letter-spacing: 0.5px;">
          Total Asset Valuation (Cost)
        </span>
        
        <div class="h3 fw-bolder text-primary mb-0 font-monospace tracking-tight">
          <span v-if="!isLoading" class="val-amount-gradient">{{ formatMoney(totalAssetValuation) }}</span>
          <div v-else class="spinner-border spinner-border-sm text-primary" role="status"></div>
        </div>
      </div>

      <!-- Quick Metrics Grid -->
      <div class="row g-2 text-center">
        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-slate-subtle">
            <span class="d-block text-muted fw-bold text-uppercase" style="font-size: 0.6rem;">SKUs</span>
            <span class="fw-bolder text-dark fs-6 font-monospace">{{ totalDbProductCount }}</span>
          </div>
        </div>

        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-rose-subtle">
            <span class="d-block text-rose fw-bold text-uppercase" style="font-size: 0.6rem;">Out of Stock</span>
            <span class="fw-bolder text-rose fs-6 font-monospace">{{ outOfStockCount }}</span>
          </div>
        </div>

        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-amber-subtle">
            <span class="d-block text-amber fw-bold text-uppercase" style="font-size: 0.6rem;">Reorder</span>
            <span class="fw-bolder text-amber fs-6 font-monospace">{{ reorderCount }}</span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { formatErpMoney } from "@/lib/erpHelper";
import { collection, getDocs, getCountFromServer, query, where } from 'firebase/firestore';
import db from '@/firebase/firestore';

export default {
  name: "StockValuationSession",
  data() {
    return {
      isLoading: false,
      totalAssetValuation: 0,
      totalDbProductCount: 0,
      outOfStockCount: 0,
      reorderCount: 0
    }
  },
  async mounted() {
    await this.fetchValuationSummary();
  },
  computed: {
    t() {
      return (key) => this.$store.getters.t(key);
    }
  },
  methods: {
    formatMoney(amount) {
      return formatErpMoney(amount);
    },
    async fetchValuationSummary() {
      this.isLoading = true;
      try {
        const coll = collection(db, "products");

        try {
          const [totalSnap, outOfStockSnap, lowStockSnap] = await Promise.all([
            getCountFromServer(coll),
            getCountFromServer(query(coll, where("qty", "<=", 0))),
            getCountFromServer(query(coll, where("qty", "<=", 10)))
          ]);
          this.totalDbProductCount = totalSnap.data().count;
          this.outOfStockCount = outOfStockSnap.data().count;
          const lowTotal = lowStockSnap.data().count;
          this.reorderCount = Math.max(0, lowTotal - this.outOfStockCount);
        } catch (cErr) {
          console.warn("Could not fetch server counts for dashboard:", cErr);
        }

        const snapShot = await getDocs(coll);
        let totalVal = 0;
        snapShot.forEach(doc => {
          const p = doc.data();
          const qty = Number(p.qty !== undefined && p.qty !== null ? p.qty : (p.main_qty !== undefined && p.main_qty !== null ? p.main_qty : (p.inventory_bal !== undefined ? p.inventory_bal : 0)));
          
          let unitCost = 0;
          if (typeof p.pur_price === 'object' && p.pur_price !== null) {
            unitCost = Number(p.pur_price?.smallest_unit || p.pur_price?.largest_unit || p.pur_price?.medium_unit || 0);
          } else if (p.pur_price !== undefined) {
            unitCost = Number(p.pur_price);
          } else if (p.cost !== undefined) {
            unitCost = Number(p.cost);
          }

          if (qty > 0 && unitCost > 0) {
            totalVal += (qty * unitCost);
          }
        });
        this.totalAssetValuation = totalVal;

      } catch (err) {
        console.error("Error loading dashboard stock valuation summary:", err);
      } finally {
        this.isLoading = false;
      }
    }
  }
}
</script>

<style scoped>
.modern-val-card {
  border: 1px solid rgba(0, 0, 0, 0.06) !important;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03) !important;
}

.modern-val-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.07) !important;
}

.val-icon-box {
  width: 38px;
  height: 38px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 4px 10px rgba(245, 158, 11, 0.3);
}

.valuation-display-box {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.val-amount-gradient {
  background: linear-gradient(135deg, #0f172a 0%, #2563eb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.view-btn {
  transition: all 0.2s ease;
  border-color: rgba(226, 232, 240, 0.9) !important;
}

.view-btn:hover {
  background-color: #0d6efd !important;
  color: #ffffff !important;
  border-color: #0d6efd !important;
  transform: translateX(2px);
}

.bg-slate-subtle {
  background-color: #f1f5f9;
  border: 1px solid #e2e8f0;
}

.bg-rose-subtle {
  background-color: #fff1f2;
  border: 1px solid #fecdd3;
}

.text-rose {
  color: #e11d48;
}

.bg-amber-subtle {
  background-color: #fffbeb;
  border: 1px solid #fde68a;
}

.text-amber {
  color: #d97706;
}

.tracking-tight {
  letter-spacing: -0.3px;
}
.tracking-wider {
  letter-spacing: 0.4px;
}
</style>
