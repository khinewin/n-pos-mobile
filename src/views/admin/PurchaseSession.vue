<template>
  <div class="card border-0 shadow-sm rounded-4 modern-session-card h-100 bg-white">
    <div class="card-body p-3.5 d-flex flex-column justify-content-between">
      
      <!-- Top Header -->
      <div class="d-flex align-items-center justify-content-between mb-3">
        <div class="d-flex align-items-center">
          <div class="session-icon-box text-white rounded-3 d-flex align-items-center justify-content-center bg-primary-grad me-3">
            <i class="fa-solid fa-cart-shopping fs-6"></i>
          </div>
          <div>
            <h6 class="mb-0 fw-bolder text-dark tracking-tight">{{ t('pur_amount') }}</h6>
            <small class="text-muted fw-medium" style="font-size: 0.68rem;">Purchase Expenditure Summary</small>
          </div>
        </div>

        <span class="badge bg-primary-subtle text-primary border border-primary-subtle rounded-pill px-2.5 py-1 fw-bold" style="font-size: 0.68rem;">
          <i class="fa-solid fa-truck-ramp-box me-1"></i> Purchases
        </span>
      </div>

      <!-- Main Value Metric Box -->
      <div class="valuation-display-box p-3 rounded-3 mb-3 text-center position-relative overflow-hidden">
        <span class="text-uppercase fw-bold text-muted d-block tracking-wider mb-1" style="font-size: 0.62rem; letter-spacing: 0.5px;">
          Monthly Total Purchase Expense
        </span>
        
        <div class="h3 fw-bolder text-primary mb-0 font-monospace tracking-tight">
          <span v-if="purchaseYearlyData.length">{{ fM(purchase_this_month) }}</span>
          <div v-else class="spinner-border spinner-border-sm text-primary" role="status"></div>
        </div>
      </div>

      <!-- Quick Metrics Grid -->
      <div class="row g-2 text-center">
        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-light border">
            <span class="d-block text-muted fw-bold text-uppercase mb-1" style="font-size: 0.6rem;">{{ t('today') }}</span>
            <span v-if="purchaseYearlyData.length" class="fw-bolder text-dark font-monospace" style="font-size: 0.8rem;">{{ fM(purchase_today) }}</span>
            <span v-else @click="fetchPurchaseYearly" class="cursor-pointer text-muted"><i class="fa-solid fa-rotate-right fa-spin"></i></span>
          </div>
        </div>

        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-light border">
            <span class="d-block text-muted fw-bold text-uppercase mb-1" style="font-size: 0.6rem;">{{ t('this_week') }}</span>
            <span v-if="purchaseYearlyData.length" class="fw-bolder text-dark font-monospace" style="font-size: 0.8rem;">{{ fM(purchase_this_week) }}</span>
            <span v-else @click="fetchPurchaseYearly" class="cursor-pointer text-muted"><i class="fa-solid fa-rotate-right fa-spin"></i></span>
          </div>
        </div>

        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-primary-light border border-primary-subtle">
            <span class="d-block text-primary fw-bold text-uppercase mb-1" style="font-size: 0.6rem;">{{ t('this_month') }}</span>
            <span v-if="purchaseYearlyData.length" class="fw-bolder text-primary font-monospace" style="font-size: 0.8rem;">{{ fM(purchase_this_month) }}</span>
            <span v-else @click="fetchPurchaseYearly" class="cursor-pointer text-primary"><i class="fa-solid fa-rotate-right fa-spin"></i></span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { Timestamp, onSnapshot, collection, where, orderBy, query } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/purchases"
import * as hp from "@/lib/helper"

export default {
  name: "PurchaseSession",
  data() {
    return {
      purchaseYearlyData: [],
      purchase_this_month: 0,
      purchase_this_week: 0,
      purchase_this_year: 0,
      purchase_today: 0
    }
  },
  computed: {
    user() {
      return this.$store.getters.user ? this.$store.getters.user : null;
    },
    t() {
      return (key) => this.$store.getters.t(key);
    },
  },
  mounted() {
    this.fetchPurchaseYearly();
  },
  methods: {
    fM(amount) {
      return hp.formattedAmountCurrency(amount);
    },
    showTime(t) {
      return hp.showTime(t)
    },
    monthlyPurchaseReport() {
      this.purchase_this_year = 0;
      this.purchase_today = 0;
      this.purchase_this_week = 0;
      this.purchase_this_month = 0;

      const today = this.showTime(Timestamp.fromDate(new Date()));
      
      this.purchaseYearlyData.forEach((f) => {
        this.purchase_this_year += (f.total_amount || 0);
        const pur_date = this.showTime(f.pur_date);
        if (pur_date === today) {
          this.purchase_today += (f.total_amount || 0);
        }
      });

      function getCurrentWeekDates() {
        const currentDate = new Date();
        const firstDayOfWeek = new Date(currentDate);
        const dayOfWeek = currentDate.getDay();
        const diffToMonday = (dayOfWeek + 6) % 7;
        firstDayOfWeek.setDate(currentDate.getDate() - diffToMonday);
        firstDayOfWeek.setHours(0, 0, 0, 0);
        const lastDayOfWeek = new Date(firstDayOfWeek);
        lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6);
        lastDayOfWeek.setHours(23, 59, 59, 999);
        return {
          firstDay: firstDayOfWeek,
          lastDay: lastDayOfWeek
        };
      }
      const w = getCurrentWeekDates();

      this.purchaseYearlyData.forEach((f) => {
        if (f.pur_date && typeof f.pur_date.toDate === 'function') {
          const pd = f.pur_date.toDate();
          if (pd >= w.firstDay && pd <= w.lastDay) {
            this.purchase_this_week += (f.total_amount || 0);
          }
        }
      });

      let date = new Date();
      let first_day = new Date(date.getFullYear(), date.getMonth(), 1, 0, 0, 0);
      let last_day = new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59);
      const first_day_month = Timestamp.fromDate(first_day);
      const last_day_month = Timestamp.fromDate(last_day);

      this.purchaseYearlyData.forEach((f) => {
        if (f.pur_date >= first_day_month && f.pur_date <= last_day_month) {
          this.purchase_this_month += (f.total_amount || 0);
        }
      });
    },
    async fetchPurchaseYearly() {
      const currentDate = new Date();
      const theFirst = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1, 0, 0, 0);
      const theLast = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0, 23, 59, 59);
      const first_day_month = Timestamp.fromDate(theFirst);
      const last_day_month = Timestamp.fromDate(theLast);

      const query_url = query(
        collection(db, "purchases"),  
        where("pur_date", ">=", first_day_month),
        where("pur_date", "<=", last_day_month),
        orderBy("pur_date")
      );

      onSnapshot(query_url, (snapShot) => {
        let items = ss.purchases(snapShot);           
        this.purchaseYearlyData = items;
        this.monthlyPurchaseReport();
      });
    }
  }
}
</script>

<style scoped>
.modern-session-card {
  border: 1px solid rgba(0, 0, 0, 0.06) !important;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03) !important;
}

.modern-session-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.07) !important;
}

.session-icon-box {
  width: 38px;
  height: 38px;
}

.valuation-display-box {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.bg-primary-grad {
  background: linear-gradient(135deg, #3b82f6 0%, #0d6efd 100%);
  box-shadow: 0 4px 10px rgba(13, 110, 253, 0.3);
}

.bg-primary-subtle {
  background-color: #eff6ff;
}

.bg-primary-light {
  background-color: #eff6ff;
}

.border-primary-subtle {
  border-color: #bfdbfe !important;
}

.tracking-tight {
  letter-spacing: -0.3px;
}

.cursor-pointer {
  cursor: pointer;
}
</style>