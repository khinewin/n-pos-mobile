<template>
  <div class="card border-0 shadow-sm rounded-4 modern-session-card h-100 bg-white">
    <div class="card-body p-3.5 d-flex flex-column justify-content-between">
      
      <!-- Top Header -->
      <div class="d-flex align-items-center justify-content-between mb-3">
        <div class="d-flex align-items-center">
          <div class="session-icon-box text-white rounded-3 d-flex align-items-center justify-content-center bg-emerald me-3">
            <i class="fa-solid fa-cash-register fs-6"></i>
          </div>
          <div>
            <h6 class="mb-0 fw-bolder text-dark tracking-tight">{{ t('sales_amount') }}</h6>
            <small class="text-muted fw-medium" style="font-size: 0.68rem;">Revenue Analytics Summary</small>
          </div>
        </div>

        <span class="badge bg-emerald-subtle text-emerald border border-emerald-subtle rounded-pill px-2.5 py-1 fw-bold" style="font-size: 0.68rem;">
          <i class="fa-solid fa-arrow-trend-up me-1"></i> Sales
        </span>
      </div>

      <!-- Main Value Metric Box -->
      <div class="valuation-display-box p-3 rounded-3 mb-3 text-center position-relative overflow-hidden">
        <span class="text-uppercase fw-bold text-muted d-block tracking-wider mb-1" style="font-size: 0.62rem; letter-spacing: 0.5px;">
          Monthly Total Sales Revenue
        </span>
        
        <div class="h3 fw-bolder text-emerald mb-0 font-monospace tracking-tight">
          <span v-if="salesYearlyData.length">{{ fM(sales_this_month) }}</span>
          <div v-else class="spinner-border spinner-border-sm text-emerald" role="status"></div>
        </div>
      </div>

      <!-- Quick Metrics Grid -->
      <div class="row g-2 text-center">
        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-light border">
            <span class="d-block text-muted fw-bold text-uppercase mb-1" style="font-size: 0.6rem;">{{ t('today') }}</span>
            <span v-if="salesYearlyData.length" class="fw-bolder text-dark font-monospace" style="font-size: 0.8rem;">{{ fM(sales_today) }}</span>
            <span v-else @click="fetchSalesYearly" class="cursor-pointer text-muted"><i class="fa-solid fa-rotate-right fa-spin"></i></span>
          </div>
        </div>

        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-light border">
            <span class="d-block text-muted fw-bold text-uppercase mb-1" style="font-size: 0.6rem;">{{ t('this_week') }}</span>
            <span v-if="salesYearlyData.length" class="fw-bolder text-dark font-monospace" style="font-size: 0.8rem;">{{ fM(sales_this_week) }}</span>
            <span v-else @click="fetchSalesYearly" class="cursor-pointer text-muted"><i class="fa-solid fa-rotate-right fa-spin"></i></span>
          </div>
        </div>

        <div class="col-4">
          <div class="metric-pill p-2 rounded-3 bg-emerald-light border border-emerald-subtle">
            <span class="d-block text-emerald fw-bold text-uppercase mb-1" style="font-size: 0.6rem;">{{ t('this_month') }}</span>
            <span v-if="salesYearlyData.length" class="fw-bolder text-emerald font-monospace" style="font-size: 0.8rem;">{{ fM(sales_this_month) }}</span>
            <span v-else @click="fetchSalesYearly" class="cursor-pointer text-emerald"><i class="fa-solid fa-rotate-right fa-spin"></i></span>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { Timestamp, onSnapshot, collection, where, orderBy, query } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/sales"
import * as hp from "@/lib/helper"

export default {
  name: "SalesSession",
  data() {
    return {
      salesYearlyData: [],
      sales_this_year: 0,
      sales_this_week: 0,
      sales_this_month: 0,
      sales_today: 0,
      isLoading: false,
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
    this.fetchSalesYearly();
  },
  methods: {
    fM(amount) {
      return hp.formattedAmountCurrency(amount)
    },
    showTime(t) {
      return hp.showTime(t)
    },
    yearlyReports() {
      this.sales_this_year = 0;
      this.sales_today = 0;
      this.sales_this_week = 0;
      this.sales_this_month = 0;

      this.salesYearlyData.forEach((res) => {
        this.sales_this_year += (res.total_amount || 0);
      });

      let startToday = new Date();
      startToday.setHours(0, 0, 0, 0);
      let endToday = new Date();
      endToday.setHours(23, 59, 59, 999);
      const start_today = Timestamp.fromDate(startToday);
      const end_today = Timestamp.fromDate(endToday);

      this.salesYearlyData.forEach((f) => {
        const sale_date = f.sale_date;
        if (sale_date >= start_today && sale_date <= end_today) {
          this.sales_today += (f.total_amount || 0);
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

      this.salesYearlyData.forEach((f) => {
        if (f.sale_date && typeof f.sale_date.toDate === 'function') {
          const sd = f.sale_date.toDate();
          if (sd >= w.firstDay && sd <= w.lastDay) {
            this.sales_this_week += (f.total_amount || 0);
          }
        }
      });

      let date = new Date();
      let first_day = new Date(date.getFullYear(), date.getMonth(), 1, 0, 0, 0);
      let last_day = new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59);
      const first_day_month = Timestamp.fromDate(first_day);
      const last_day_month = Timestamp.fromDate(last_day);

      this.salesYearlyData.forEach((f) => {
        if (f.sale_date >= first_day_month && f.sale_date <= last_day_month) {
          this.sales_this_month += (f.total_amount || 0);
        }
      });
    },
    async fetchSalesYearly() {
      this.isLoading = true;
      const currentDate = new Date();
      const theFirst = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1, 0, 0, 0);
      const theLast = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0, 23, 59, 59);
      const first_day_month = Timestamp.fromDate(theFirst);
      const last_day_month = Timestamp.fromDate(theLast);
      
      const query_url = query(
        collection(db, "sales"),  
        where("sale_date", ">=", first_day_month),
        where("sale_date", "<=", last_day_month),
        orderBy("sale_date")
      );
      
      onSnapshot(query_url, (snapShot) => {
        let saleItems = ss.sales(snapShot);           
        this.salesYearlyData = saleItems;
        this.yearlyReports();
        this.isLoading = false;
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

.bg-emerald {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 10px rgba(16, 185, 129, 0.3);
}

.valuation-display-box {
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border: 1px solid rgba(226, 232, 240, 0.8);
}

.bg-emerald-subtle {
  background-color: #ecfdf5;
}

.bg-emerald-light {
  background-color: #f0fdf4;
}

.text-emerald {
  color: #059669;
}

.border-emerald-subtle {
  border-color: #a7f3d0 !important;
}

.tracking-tight {
  letter-spacing: -0.3px;
}

.cursor-pointer {
  cursor: pointer;
}
</style>