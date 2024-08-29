import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'
import Dashboard from "@/views/admin/Dashboard.vue"
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
import PrintSale from '@/views/sales/Print.vue'
import Reports from '@/views/reports/Reports.vue'
import ReportSales from '@/views/reports/Sales.vue'
import ReportStocks from '@/views/reports/Stocks.vue'
import ReportPurchase from '@/views/reports/Purchase.vue'
import ReportProduct from '@/views/reports/Products.vue'
import Customers from '@/views/customer/Customers.vue'
import store from "@/store"
import NewTransfer from '@/views/transfers/New.vue'
import Transfers from '@/views/transfers/Transfers.vue'
import EditTransfer from '@/views/transfers/Edit.vue'
import ReportsTransfers from '@/views/reports/Transfers.vue'
import Adjustments from '@/views/adjustments/Adjustments.vue'
import NewAdjustment from '@/views/adjustments/New.vue'
import EditAdjustment from '@/views/adjustments/Edit.vue'
import ReportAdjustments from '@/views/reports/Adjustments.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),  
  routes: [
    {
      path: '/',
      name: 'Welcome',
      component: Login,
      meta : {title: "N-PRO-POS | Welcome"}

    },
    {
      path: '/auth/login',
      name: 'Login',     
      component: Login,
      meta : {title: "N-PRO-POS | User signin"}

    },
    {
      path: '/admin/dashboard',
      name: 'Dashboard',     
      component: Dashboard,
      meta : {title: "N-PRO-POS | Admin dashboard"}
    },
    
    {
      path: '/sale',
      name: 'Sale',     
      component: Sale,
      meta : {title: "N-PRO-POS | Sale"}

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
      meta : {title: "N-PRO-POS | Products code"}

    },
    {
      path: '/products/opening-stocks',
      name: 'OpeningStocks',     
      component: OpeningStocks,
      meta : {title: "N-PRO-POS | Opening stocks"}

    },
    {
      path: '/products/opening-stocks/new',
      name: 'NewOpeningStocks',     
      component: NewOpeningStocks,
      meta : {title: "N-PRO-POS | New opening stock"}
    },
    {
      path: '/products/opening-stocks/:id/edit',
      name: 'EditOpeningStocks',     
      component: EditOpeningStocks,
      meta : {title: "N-PRO-POS | Edit opening stock"}
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: Purchase,
      meta : {title: "N-PRO-POS | Purchase"}
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: EditPurchase,
      meta : {title: "N-PRO-POS | Edit purchase"}
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: AddPurchase,
      meta : {title: "N-PRO-POS | Add purchase"}    },
   
    {
      path: '/suppliers',
      name: 'Suppliers',     
      component: Suppliers,
      meta : {title: "N-PRO-POS | Suppliers"}
    },
    {
      path: '/customers',
      name: 'Customers',     
      component: Customers,
      meta : {title: "N-PRO-POS | Customers"}
    },
    {
      path: "/reports",
      name: "Reports",
      component: Reports,
      meta : {title: "N-PRO-POS | Reports"}
    },
    {
      path: "/reports/products/:reportType/:location",
      name: "ReportsProduct",
      component: ReportProduct,
      meta : {title: "N-PRO-POS | Report product code"}
    },
    {
      path: "/reports/transfers/:reportType/:fromDate/:toDate/:from_location/:to_location",
      name: "ReportsTransfer",
      component: ReportsTransfers,
      meta : {title: "N-PRO-POS | Report transfer location"}
    },
    {
      path: "/reports/stocks/:reportType/:fromDate/:toDate/:location",
      name: "ReportsStocks",
      component: ReportStocks,
      meta : {title: "N-PRO-POS | Report opening stock"}
    },
    {
      path: "/reports/sales/:reportType/:fromDate/:toDate",
      name: "ReportsSales",
      component: ReportSales,
      meta : {title: "N-PRO-POS | Report sales"}
    },
    {
      path: "/reports/purchase/:reportType/:fromDate/:toDate/:location",
      name: "ReportsPurchase",
      component: ReportPurchase,
      meta : {title: "N-PRO-POS | Report purchase"}
    },
    {
      path: "/reports/adjustments/:reportType/:fromDate/:toDate",
      name: "ReportsAdjustments",
      component: ReportAdjustments,
      meta : {title: "N-PRO-POS | Report adjustments"}
    },
    {
      path: '/transfers/:id/edit',
      name: 'EditTransfer',     
      component: EditTransfer,    
      meta: { title: 'N-PRO-POS | Edit transfer' },
    },
    {
      path: '/transfers/new',
      name: 'AddTransfer',     
      component: NewTransfer,    
      meta: { title: 'N-PRO-POS | New transfer' },
    },
    {
      path: '/transfers',
      name: 'Transfers',     
      component: Transfers,    
      meta: { title: 'N-PRO-POS | Transfers' },
    },
    {
      path: '/adjustments',
      name: 'Adjustments',     
      component: Adjustments,
      meta : {title: "N-PRO-POS | Adjustments"}
    },
    {
      path: '/adjustments/add',
      name: 'NewAdjustment',     
      component: NewAdjustment,
      meta : {title: "N-PRO-POS | New adjustment"}
    },
    {
      path: '/adjustments/:id/edit',
      name: 'EditAdjustment',     
      component: EditAdjustment,
      meta : {title: "N-PRO-POS | Edit adjustments"}
    },
    {
      path: '/user/profile',
      name: 'Profile',     
      component: Profile,    
      meta: { title: 'N-PRO-POS | User profile' },
    }
  ], 
})

router.beforeEach((to, from, next)=>{
  document.title = to.meta.title;
  const isAuth=store.getters.user;
  if (to.name !== 'Login' && !isAuth) next({ name: 'Login' })
    else next()  
  //next();  
})

export default router
