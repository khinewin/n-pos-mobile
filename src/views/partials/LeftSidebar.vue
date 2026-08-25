<template>
  <div class="offcanvas offcanvas-start modern-sidebar shadow-sm" tabindex="-1" id="leftSide" aria-labelledby="leftSide">
    <!-- Brand Header Section -->
    <div class="offcanvas-header py-2 px-3 pb-2">
      <div class="d-flex flex-column">
        <div class="d-flex align-items-center gap-2 mb-0">
          <div class="brand-box bg-primary bg-opacity-10 text-primary d-flex align-items-center justify-content-center rounded-2" style="width: 32px; height: 32px;">
            <i class="fa-solid fa-shop fs-6"></i>
          </div>
          <h5 class="logo text-dark fw-bolder mb-0" id="leftSideLabel" style="letter-spacing: -0.5px;">N-POS</h5>
        </div>
        <small class="text-muted fw-medium" style="font-size: 0.65rem;">Retail Software for minimart</small>
      </div>
      <button type="button" class="btn-close shadow-none ms-auto m-2 position-absolute top-0 end-0" data-bs-dismiss="offcanvas" aria-label="Close" style="font-size: 0.5rem;"></button>
    </div>
    
    <!-- Unified Navigation List -->
    <div class="offcanvas-body p-0">
      <div class="nav-container px-2 py-1">
        <ul class="nav flex-column gap-0">
          <li class="nav-item" v-for="item in menuItems" :key="item.routeName" data-bs-dismiss="offcanvas">
            <router-link 
              :to="{ name: item.routeName }" 
              class="nav-link sidebar-item d-flex align-items-center gap-2 py-1.5 px-2 rounded-2" 
              active-class="active"
            >
              <div class="icon-frame d-flex align-items-center justify-content-center">
                <i :class="item.icon" style="font-size: 0.9rem;"></i>
              </div>
              <span class="item-label text-nowrap">{{ t(item.labelKey) }}</span>
            </router-link>
          </li>
        </ul>
      </div>
    </div>

    <!-- Professional Sidebar Footer -->
    <div class="sidebar-footer p-2 border-top bg-light bg-opacity-25 mt-auto">
      <div class="d-flex align-items-center justify-content-between px-1">
        <div class="d-flex align-items-center gap-2">
          <div class="status-indicator-ring">
            <div class="status-dot"></div>
          </div>
          <span class="fw-bold text-muted" style="font-size: 0.6rem; letter-spacing: 0.5px;">ONLINE</span>
        </div>
        <span class="text-primary fw-bold" style="font-size: 0.6rem; opacity: 0.8;">v2.1.0</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "LeftSidebar",
  data() {
    return {
      menuItems: [
        { routeName: 'Dashboard', labelKey: 'dashboard', icon: 'fa-solid fa-house' },
        { routeName: 'Sale', labelKey: 'sale', icon: 'fa-solid fa-cart-shopping' },
        { routeName: 'Purchase', labelKey: 'purchase', icon: 'fa-solid fa-truck-ramp-box' },
        { routeName: 'Stocks', labelKey: 'stocks', icon: 'fa-solid fa-boxes-stacked' },
        { routeName: 'StockValuation', labelKey: 'stock_valuation', icon: 'fa-solid fa-coins' },
        { routeName: 'Products', labelKey: 'product_code', icon: 'fa-solid fa-barcode' },
        { routeName: 'Suppliers', labelKey: 'suppliers', icon: 'fa-solid fa-user-tie' },
        { routeName: 'Customers', labelKey: 'customer', icon: 'fa-solid fa-users' },
        { routeName: 'Adjustments', labelKey: 'adjustment', icon: 'fa-solid fa-sliders' },
        { routeName: 'Soc', labelKey: 'soc', icon: 'fa-solid fa-file-invoice-dollar' },
        { routeName: 'Reports', labelKey: 'reports', icon: 'fa-solid fa-chart-line' },
        { routeName: 'BackupRestore', labelKey: 'backup_restore', icon: 'fa-solid fa-cloud-arrow-down' },
      ]
    }
  },
  computed: {
    t() {
      return (key) => this.$store.getters.t(key);
    },
  }
}
</script>

<style scoped>
.modern-sidebar {
  background-color: #f8f9fa; /* btn-light color */
  border-right: 1px solid rgba(0, 0, 0, 0.05);
  width: 250px !important;
  font-family: 'Roboto', 'Inter', sans-serif;
}

.sidebar-item {
  color: #5f6368 !important;
  transition: all 0.2s ease-in-out;
  border: 1px solid transparent;
  font-weight: 500;
  padding-top: 6px !important;
  padding-bottom: 6px !important;
}

.sidebar-item:hover {
  background-color: #ffffff;
  color: #0d6efd !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}

.sidebar-item.active {
  background-color: #ffffff !important;
  color: #0d6efd !important;
  box-shadow: 0 2px 8px rgba(13, 110, 253, 0.06);
  border-color: rgba(13, 110, 253, 0.05);
  font-weight: 600;
}

.icon-frame {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.sidebar-item:hover .icon-frame,
.sidebar-item.active .icon-frame {
  background-color: rgba(13, 110, 253, 0.05);
  color: #0d6efd;
}

.item-label {
  font-size: 0.825rem;
  letter-spacing: 0.1px;
}

.status-indicator-ring {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid rgba(13, 110, 253, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-dot {
  width: 6px;
  height: 6px;
  background-color: #22c55e;
  border-radius: 50%;
  animation: blink 2s infinite ease-in-out;
}

@keyframes blink {
  0% { opacity: 0.4; transform: scale(0.85); }
  50% { opacity: 1; transform: scale(1); }
  100% { opacity: 0.4; transform: scale(0.85); }
}

/* Custom shadow for side elevation */
.shadow-sm {
  box-shadow: 4px 0 20px rgba(0, 0, 0, 0.02) !important;
}

/* Smooth hidden overflow scroll */
.offcanvas-body {
  scrollbar-width: thin;
  scrollbar-color: #e2e8f0 transparent;
}

.offcanvas-body::-webkit-scrollbar {
  width: 4px;
}

.offcanvas-body::-webkit-scrollbar-track {
  background: transparent;
}

.offcanvas-body::-webkit-scrollbar-thumb {
  background-color: #e2e8f0;
  border-radius: 10px;
}
</style>