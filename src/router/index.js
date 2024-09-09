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
import SalesOnCredit from '@/views/sales/SalesOnCredit.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),  
  routes: [
    {
      path: '/',
      name: 'Welcome',
      component: Login,
      meta : {title: "N-Pharmacy | Welcome"}

    },
    {
      path: '/auth/login',
      name: 'Login',     
      component: Login,
      meta : {title: "N-Pharmacy | User signin"}

    },
    {
      path: '/admin/dashboard',
      name: 'Dashboard',     
      component: Dashboard,
      meta : {title: "N-Pharmacy | Admin dashboard"}
    },
    
    {
      path: '/sale',
      name: 'Sale',     
      component: Sale,
      meta : {title: "N-Pharmacy | Sale"}

    },
    {
      path: '/soc',
      name: 'Soc',     
      component: SalesOnCredit,
      meta : {title: "N-Pharmacy | SOC"}

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
      meta : {title: "N-Pharmacy | Medicine code"}

    },
    {
      path: '/opening-stocks',
      name: 'OpeningStocks',     
      component: OpeningStocks,
      meta : {title: "N-Pharmacy | Opening stocks"}

    },
    {
      path: '/opening-stocks/new',
      name: 'NewOpeningStocks',     
      component: NewOpeningStocks,
      meta : {title: "N-Pharmacy | New opening stock"}
    },
    {
      path: '/opening-stocks/:id/edit',
      name: 'EditOpeningStocks',     
      component: EditOpeningStocks,
      meta : {title: "N-Pharmacy | Edit opening stock"}
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: Purchase,
      meta : {title: "N-Pharmacy | Purchase"}
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: EditPurchase,
      meta : {title: "N-Pharmacy | Edit purchase"}
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: AddPurchase,
      meta : {title: "N-Pharmacy | Add purchase"}    },
   
    {
      path: '/suppliers',
      name: 'Suppliers',     
      component: Suppliers,
      meta : {title: "N-Pharmacy | Suppliers"}
    },
    {
      path: '/customers',
      name: 'Customers',     
      component: Customers,
      meta : {title: "N-Pharmacy | Customers"}
    },
    {
      path: "/reports",
      name: "Reports",
      component: Reports,
      meta : {title: "N-Pharmacy | Reports"}
    },
    {
      path: "/reports/products/:reportType/:location",
      name: "ReportsProduct",
      component: ReportProduct,
      meta : {title: "N-Pharmacy | Report product code"}
    },
    {
      path: "/reports/transfers/:reportType/:fromDate/:toDate/:from_location/:to_location",
      name: "ReportsTransfer",
      component: ReportsTransfers,
      meta : {title: "N-Pharmacy | Report transfer location"}
    },
    {
      path: "/reports/stocks/:reportType/:fromDate/:toDate/:location",
      name: "ReportsStocks",
      component: ReportStocks,
      meta : {title: "N-Pharmacy | Report opening stock"}
    },
    {
      path: "/reports/sales/:reportType/:fromDate/:toDate",
      name: "ReportsSales",
      component: ReportSales,
      meta : {title: "N-Pharmacy | Report sales"}
    },
    {
      path: "/reports/purchase/:reportType/:fromDate/:toDate/:location",
      name: "ReportsPurchase",
      component: ReportPurchase,
      meta : {title: "N-Pharmacy | Report purchase"}
    },
    {
      path: "/reports/adjustments/:reportType/:fromDate/:toDate",
      name: "ReportsAdjustments",
      component: ReportAdjustments,
      meta : {title: "N-Pharmacy | Report adjustments"}
    },
    {
      path: '/transfers/:id/edit',
      name: 'EditTransfer',     
      component: EditTransfer,    
      meta: { title: 'N-Pharmacy | Edit transfer' },
    },
    {
      path: '/transfers/new',
      name: 'AddTransfer',     
      component: NewTransfer,    
      meta: { title: 'N-Pharmacy | New transfer' },
    },
    {
      path: '/transfers',
      name: 'Transfers',     
      component: Transfers,    
      meta: { title: 'N-Pharmacy | Transfers' },
    },
    {
      path: '/adjustments',
      name: 'Adjustments',     
      component: Adjustments,
      meta : {title: "N-Pharmacy | Adjustments"}
    },
    {
      path: '/adjustments/add',
      name: 'NewAdjustment',     
      component: NewAdjustment,
      meta : {title: "N-Pharmacy | New adjustment"}
    },
    {
      path: '/adjustments/:id/edit',
      name: 'EditAdjustment',     
      component: EditAdjustment,
      meta : {title: "N-Pharmacy | Edit adjustments"}
    },
    {
      path: '/user/profile',
      name: 'Profile',     
      component: Profile,    
      meta: { title: 'N-Pharmacy | User profile' },
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
