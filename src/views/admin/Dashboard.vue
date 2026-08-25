<template>
  <div class="dashboard-page bg-light min-vh-100 pb-5">             
    <PageHeader :page-title="t('dashboard')" page-code="dashboard"/>
    
    <div class="container-fluid py-3 px-md-4">
      
      <!-- Welcome Section -->
      <div class="row mb-3">
        <div class="col-12">
          <div class="card border-0 shadow-sm welcome-card text-white overflow-hidden">
            <div class="card-body p-4 position-relative z-1">
              <div class="d-flex align-items-center justify-content-between flex-wrap gap-2">
                <div class="d-flex align-items-center">
                  <div class="avatar-circle bg-white text-primary me-3 shadow-sm">
                    <i class="fa-solid fa-user-circle fa-2x"></i>
                  </div>
                  <div>
                    <h4 class="fw-bold mb-1">{{getGreeting}}!</h4>
                    <p class="mb-0 opacity-75" v-if="user">{{user.displayName}} ({{user.email}})</p>
                  </div>
                </div>

                <!-- Right Side Day & Date Time -->
                <div class="text-end text-white pe-2">
                  <div class="fw-bold" style="font-size: 1.05rem;">
                    <i class="fa-solid fa-calendar-days me-1 opacity-90"></i> {{ currentDayAndDate }}
                  </div>
                  <small class="opacity-75"><i class="fa-solid fa-clock me-1"></i> {{ currentTime }}</small>
                </div>
              </div>
            </div>
            <!-- Abstract Background Decoration -->
            <div class="bg-decoration"></div>
          </div>
        </div>
      </div>

      <!-- KPI Analytics Cards Section -->
      <div class="d-flex align-items-center justify-content-between mb-2 px-1">
        <h6 class="fw-bold text-dark mb-0 d-flex align-items-center">
          <i class="fa-solid fa-chart-pie text-primary me-2 fs-5"></i> Performance & Inventory Analytics
        </h6>
        <small class="text-muted">Real-time metrics overview</small>
      </div>

      <div class="row g-3 mb-4">
        <div class="col-lg-4 col-md-6">
          <SalesSession />
        </div>
        <div class="col-lg-4 col-md-6">
          <PurchaseSession />
        </div>
        <div class="col-lg-4 col-md-12">
          <StockValuationSession />
        </div>
      </div>

      <!-- Quick Modules Navigation Grid -->
      <div class="d-flex align-items-center justify-content-between mb-2 px-1">
        <h6 class="fw-bold text-dark mb-0 d-flex align-items-center">
          <i class="fa-solid fa-grip text-primary me-2 fs-5"></i> Quick Modules Navigation
        </h6>
        <small class="text-muted">Access POS core modules</small>
      </div>

      <div class="row g-3 mb-4">           
        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Sale'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-emerald-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-cash-register fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('sale')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Purchase'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-rose-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-cart-shopping fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('purchase')}}</span>
            </div>
          </router-link>
        </div>
        
        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Stocks'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-blue-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-boxes-stacked fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('stocks')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'StockValuation'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-amber-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-coins fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('stock_valuation')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Products'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-indigo-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-barcode fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('product_code')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Suppliers'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-teal-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-user-tie fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('suppliers')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Customers'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-purple-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-users fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('customers')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Adjustments'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-slate-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-sliders fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('adjustment')}}</span>
            </div>
          </router-link>
        </div>           
        
        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name : 'Soc'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-orange-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-file-invoice-dollar fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('soc')}}</span>
            </div>
          </router-link>
        </div>

        <div class="col-6 col-sm-4 col-md-3 col-lg-2">
          <router-link :to="{name: 'Reports'}" class="modern-action-card card border-0 shadow-sm text-decoration-none h-100 rounded-4">
            <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
              <div class="mod-icon-box bg-cyan-gradient text-white mb-2.5 rounded-3">
                <i class="fa-solid fa-chart-line fs-5"></i>
              </div>
              <span class="text-dark fw-bold small tracking-tight">{{t('reports')}}</span>
            </div>
          </router-link>
        </div>
      </div>

      <!-- Financial Charts & Trends Section -->
      <div class="d-flex align-items-center justify-content-between mb-2 px-1">
        <h6 class="fw-bold text-dark mb-0 d-flex align-items-center">
          <i class="fa-solid fa-chart-area text-primary me-2 fs-5"></i> Financial Analytics & Charts
        </h6>
        <small class="text-muted">Monthly comparison</small>
      </div>

      <div class="row g-3">
        <div class="col-lg-6">
          <div class="card border-0 shadow-sm rounded-4 h-100 bg-white">
            <div class="card-body p-3">
              <SalesChart />
            </div>
          </div>
        </div>
        <div class="col-lg-6">
          <div class="card border-0 shadow-sm rounded-4 h-100 bg-white">
            <div class="card-body p-3">
              <PurchaseChart />
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>    
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import SalesSession from "@/views/admin/SalesSession.vue"
import PurchaseSession from "@/views/admin/PurchaseSession.vue"
import StockValuationSession from "@/views/admin/StockValuationSession.vue"
import SalesChart from "@/views/admin/SalesChart.vue"
import PurchaseChart from "@/views/admin/PurchaseChart.vue"

export default {
  name: "Dashboard",
  components: {
    SalesSession, PageHeader, PurchaseSession, StockValuationSession, SalesChart, PurchaseChart
  },
  data() {
    return {
      currentDateTime: new Date(),
      timer: null
    }
  },
  mounted() {
    this.timer = setInterval(() => {
      this.currentDateTime = new Date();
    }, 1000);
  },
  beforeUnmount() {
    if (this.timer) clearInterval(this.timer);
  },
  computed: {
    t() {
      return (key) => this.$store.getters.t(key);
    },
    user() {
      return this.$store.getters.user ? this.$store.getters.user : null;
    },
    currentDayAndDate() {
      return this.currentDateTime.toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'short',
        day: 'numeric',
        year: 'numeric'
      });
    },
    currentTime() {
      return this.currentDateTime.toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true
      });
    },
    getGreeting() {
      const hours = (new Date()).getHours();
      const sl = this.$store.getters.currentLang;
      if (hours < 12) {
        return sl == "mm" ? "မင်္ဂလာနံနက်ခင်းပါ" : "Good morning";
      } else if (hours < 18) {
        return sl == "mm" ? "မင်္ဂလာနေ့လည်ခင်းပါ" : "Good afternoon";
      } else {
        return sl == "mm" ? "မင်္ဂလာညနေခင်းပါ" : "Good evening";
      }
    }
  }
}
</script>

<style scoped>
.dashboard-page {
  font-family: 'Inter', system-ui, -apple-system, sans-serif;
}

/* Welcome Card Styling */
.welcome-card {
    background: linear-gradient(135deg, #6c6a89 0%, #fafcff 100%);
    border-radius: 16px;
}
.avatar-circle {
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
}
.bg-decoration {
    position: absolute;
    top: 0;
    right: 0;
    width: 100%;
    height: 100%;
    background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5M32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.1' fill-rule='evenodd'/%3E%3C/svg%3E");
    opacity: 0.6;
}

.status-online-dot {
  position: absolute;
  bottom: 2px;
  right: 14px;
  width: 12px;
  height: 12px;
  background-color: #22c55e;
  border: 2px solid #0f172a;
  border-radius: 50%;
}

.glass-pill {
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.12);
}

.live-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  box-shadow: 0 0 8px #22c55e;
  animation: pulse-live 2s infinite ease-in-out;
}

@keyframes pulse-live {
  0% { transform: scale(0.95); opacity: 0.6; }
  50% { transform: scale(1.15); opacity: 1; }
  100% { transform: scale(0.95); opacity: 0.6; }
}

/* Modern Quick Action Cards */
.modern-action-card {
  border: 1px solid rgba(0, 0, 0, 0.05) !important;
  background-color: #ffffff;
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

.modern-action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.06) !important;
  border-color: rgba(13, 110, 253, 0.2) !important;
}

.mod-icon-box {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.25s ease;
}

.modern-action-card:hover .mod-icon-box {
  transform: scale(1.1);
}

/* Vibrant Gradients */
.bg-emerald-gradient {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.bg-rose-gradient {
  background: linear-gradient(135deg, #f43f5e 0%, #e11d48 100%);
  box-shadow: 0 4px 12px rgba(244, 63, 94, 0.3);
}

.bg-blue-gradient {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.bg-amber-gradient {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.bg-indigo-gradient {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.bg-teal-gradient {
  background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%);
  box-shadow: 0 4px 12px rgba(20, 184, 166, 0.3);
}

.bg-purple-gradient {
  background: linear-gradient(135deg, #a855f7 0%, #9333ea 100%);
  box-shadow: 0 4px 12px rgba(168, 85, 247, 0.3);
}

.bg-slate-gradient {
  background: linear-gradient(135deg, #64748b 0%, #475569 100%);
  box-shadow: 0 4px 12px rgba(100, 116, 139, 0.3);
}

.bg-orange-gradient {
  background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.3);
}

.bg-cyan-gradient {
  background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%);
  box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
}

.tracking-tight {
  letter-spacing: -0.3px;
}
</style>