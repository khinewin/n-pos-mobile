import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'
import Dashboard from "@/views/admin/Dashboard.vue"
import Add from '@/views/products/Add.vue'
import Products from '@/views/products/Products.vue'
import Profile from "@/views/admin/Profile.vue"
import AddPurchase from '@/views/purchase/Add.vue'
import Purchase from '@/views/purchase/Purchase.vue'
import EditPurchase from '@/views/purchase/Edit.vue'
import OpeningStocks from '@/views/stocks/OpeningStocks.vue'
import NewOpeningStocks from '@/views/stocks/New.vue'
import EditOpeningStocks from '@/views/stocks/Edit.vue'
import Sale from '@/views/sales/Sale.vue'
import Suppliers from '@/views/suppliers/Suppliers.vue'
import AddSupplier from '@/views/suppliers/Add.vue'
import PrintSale from '@/views/sales/Print.vue'
import Reports from '@/views/reports/Reports.vue'
import ReportSales from '@/views/reports/Sales.vue'
import ReportStocks from '@/views/reports/Stocks.vue'
import ReportPurchase from '@/views/reports/Purchase.vue'
import ReportProduct from '@/views/reports/Products.vue'
import Customers from '@/views/customer/Customers.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Login
    },
    {
      path: '/auth/login',
      name: 'Login',     
      component: Login,
    },
    {
      path: '/admin/dashboard',
      name: 'Dashboard',     
      component: Dashboard,
    },
    {
      path: '/sale',
      name: 'Sale',     
      component: Sale,
    },
    {
      path: '/sale/print',
      name: 'PrintSale',     
      component: PrintSale,
    },
   
    {
      path: '/products',
      name: 'Products',     
      component: Products,
    },
    {
      path: '/products/opening-stocks',
      name: 'OpeningStocks',     
      component: OpeningStocks,
    },
    {
      path: '/products/opening-stocks/new',
      name: 'NewOpeningStocks',     
      component: NewOpeningStocks,
    },
    {
      path: '/products/opening-stocks/:id/edit',
      name: 'EditOpeningStocks',     
      component: EditOpeningStocks,
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: Purchase,
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: EditPurchase,
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: AddPurchase,
    },
   
    {
      path: '/suppliers',
      name: 'Suppliers',     
      component: Suppliers,
    },
    {
      path: '/customers',
      name: 'Customers',     
      component: Customers,
    },
    {
      path: "/reports",
      name: "Reports",
      component: Reports
    },
    {
      path: "/reports/products/:reportType",
      name: "ReportsProduct",
      component: ReportProduct
    },
    {
      path: "/reports/stocks/:reportType/:fromDate/:toDate",
      name: "ReportsStocks",
      component: ReportStocks
    },
    {
      path: "/reports/sales/:reportType/:fromDate/:toDate",
      name: "ReportsSales",
      component: ReportSales
    },
    {
      path: "/reports/purchase/:reportType/:fromDate/:toDate",
      name: "ReportsPurchase",
      component: ReportPurchase
    },
    {
      path: '/user/profile',
      name: 'Profile',     
      component: Profile,
    }
  ],

  
})

export default router
