import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'
import Dashboard from "@/views/admin/Dashboard.vue"
import Products from '@/views/products/Products.vue'
import Profile from "@/views/admin/Profile.vue"
import AddPurchase from '@/views/purchase/Add.vue'
import Purchase from '@/views/purchase/Purchase.vue'
import EditPurchase from '@/views/purchase/Edit.vue'
import Stocks from '@/views/stocks/Stocks.vue'
import NewStocks from '@/views/stocks/New.vue'
import EditStocks from '@/views/stocks/Edit.vue'
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
import ReportAdjustments from '@/views/reports/Adjustments.vue'
import SalesOnCredit from '@/views/sales/SalesOnCredit.vue'
import ReportsSoc from '@/views/reports/Soc.vue'
import BackupRestore from '@/views/backup/BackupRestore.vue'
import EditSale from '@/views/sales/Edit.vue'
import ReturnIn from '@/views/returnin/ReturnIn.vue'
import EditReturnIn from '@/views/returnin/Edit.vue'
import Returnout from '@/views/returnout/Returnout.vue'
import EditReturnOut from '@/views/returnout/Edit.vue'
import ReportReturnIn from '@/views/reports/ReturnIn.vue'
import ReportReturnOut from '@/views/reports/ReturnOut.vue'

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
      path: '/return-out',
      name: 'ReturnOut',     
      component: Returnout,
      meta : {title: "N-PRO-POS | Return Out"}

    },
    {
      path: '/return-out/:id/edit',
      name: 'EditReturnOut',     
      component: EditReturnOut,
      meta : {title: "N-PRO-POS | Return Out / Edit"}

    },
    {
      path: '/return-in',
      name: 'ReturnIn',     
      component: ReturnIn,
      meta : {title: "N-PRO-POS | Return In"}

    },
    {
      path: '/return-in/:id/edit',
      name: 'EditReturnIn',     
      component: EditReturnIn,
      meta : {title: "N-PRO-POS | Return In / Edit"}

    },
    {
      path: '/sale',
      name: 'Sale',     
      component: Sale,
      meta : {title: "N-PRO-POS | Sale"}

    },
    {
      path: '/sale/:id/edit',
      name: 'EditSale',     
      component: EditSale,
      meta : {title: "N-PRO-POS | Sale / Edit"}

    },
    {
      path: '/soc',
      name: 'Soc',     
      component: SalesOnCredit,
      meta : {title: "N-PRO-POS | SOC"}

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
      meta : {title: "N-PRO-POS | Product Code"}

    },
    {
      path: '/stocks',
      name: 'Stocks',     
      component: Stocks,
      meta : {title: "N-PRO-POS |  Stocks / History"}

    },
    {
      path: '/stocks/new',
      name: 'NewStocks',     
      component: NewStocks,
      meta : {title: "N-PRO-POS | Stocks / New"}
    },
    {
      path: '/stocks/:id/edit',
      name: 'EditStocks',     
      component: EditStocks,
      meta : {title: "N-PRO-POS | Stocks / Edit"}
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: Purchase,
      meta : {title: "N-PRO-POS | Purchase / History"}
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: EditPurchase,
      meta : {title: "N-PRO-POS | Purchase / Edit"}
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: AddPurchase,
      meta : {title: "N-PRO-POS | Purchase / New"}    },
   
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
      meta : {title: "N-PRO-POS | Report Medicine Code"}
    },
    {
      path: "/reports/transfers/:reportType/:fromDate/:toDate/:from_location/:to_location",
      name: "ReportsTransfer",
      component: ReportsTransfers,
      meta : {title: "N-PRO-POS | Report Transfers"}
    },
    {
      path: "/reports/stocks/:reportType/:fromDate/:toDate/:location",
      name: "ReportsStocks",
      component: ReportStocks,
      meta : {title: "N-PRO-POS | Report Stocks"}
    },
    {
      path: "/reports/sales/:reportType/:fromDate/:toDate",
      name: "ReportsSales",
      component: ReportSales,
      meta : {title: "N-PRO-POS | Report Sales"}
    },
    {
      path: "/reports/purchase/:reportType/:fromDate/:toDate/:location",
      name: "ReportsPurchase",
      component: ReportPurchase,
      meta : {title: "N-PRO-POS | Report Purchase"}
    },
    {
      path: "/reports/adjustments/:reportType/:fromDate/:toDate",
      name: "ReportsAdjustments",
      component: ReportAdjustments,
      meta : {title: "N-PRO-POS | Report Adjustments"}
    },
    {
      path: "/reports/soc/:reportType/:fromDate/:toDate",
      name: "ReportsSoc",
      component: ReportsSoc,
      meta : {title: "N-PRO-POS | Report SOC"}
    },
    {
      path: "/reports/return-in/:reportType/:fromDate/:toDate",
      name: "ReportsReturnIn",
      component: ReportReturnIn,
      meta : {title: "N-PRO-POS | Report Return In"}
    },
    {
      path: "/reports/return-out/:reportType/:fromDate/:toDate",
      name: "ReportsReturnOut",
      component: ReportReturnOut,
      meta : {title: "N-PRO-POS | Report Return Out"}
    },
    {
      path: '/transfers/:id/edit',
      name: 'EditTransfer',     
      component: EditTransfer,    
      meta: { title: 'N-PRO-POS | Transfer / Edit' },
    },
    {
      path: '/transfers/new',
      name: 'AddTransfer',     
      component: NewTransfer,    
      meta: { title: 'N-PRO-POS | Transfer / New' },
    },
    {
      path: '/transfer',
      name: 'Transfers',     
      component: Transfers,    
      meta: { title: 'N-PRO-POS | Transfer' },
    },
    {
      path: '/adjustments',
      name: 'Adjustments',     
      component: Adjustments,
      meta : {title: "N-PRO-POS | Adjustment"}
    },
    {
      path: '/adjustments/add',
      name: 'NewAdjustment',     
      component: NewAdjustment,
      meta : {title: "N-PRO-POS | Adjustment / New"}
    },
   
    {
      path: '/user/profile',
      name: 'Profile',     
      component: Profile,    
      meta: { title: 'N-PRO-POS | User profile' },
    },
    {
      path: '/backup-restore',
      name:'BackupRestore',
      component: BackupRestore,
      meta: { title: 'N-PRO-POS | Backup / Restore' },
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
