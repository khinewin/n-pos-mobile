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
import MobileSale from '@/views/sales/MobileSale.vue'
import MobilePrint from '@/views/sales/MobilePrint.vue'
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
import Test from '@/views/Test.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),  
  routes: [
    {
      path: "/test",
      name: "Test",
      component:Test
    },
    {
      path: '/',
      name: 'Welcome',
      component: Login,
      meta : {title: "N-POS | Welcome"}

    },
    {
      path: '/auth/login',
      name: 'Login',     
      component: Login,
      meta : {title: "N-POS | User signin"}

    },
    {
      path: '/admin/dashboard',
      name: 'Dashboard',     
      component: Dashboard,
      meta : {title: "N-POS | Admin dashboard"}
    },
    {
      path: '/return-out',
      name: 'ReturnOut',     
      component: Returnout,
      meta : {title: "N-POS | Return Out"}

    },
    {
      path: '/return-out/:id/edit',
      name: 'EditReturnOut',     
      component: EditReturnOut,
      meta : {title: "N-POS | Return Out / Edit"}

    },
    {
      path: '/return-in',
      name: 'ReturnIn',     
      component: ReturnIn,
      meta : {title: "N-POS | Return In"}

    },
    {
      path: '/return-in/:id/edit',
      name: 'EditReturnIn',     
      component: EditReturnIn,
      meta : {title: "N-POS | Return In / Edit"}

    },
    {
      path: '/sale',
      name: 'Sale',     
      component: Sale,
      meta : {title: "N-POS | Sale"}
    },
    {
      path: '/mobile/sale',
      name: 'MobileSale',     
      component: MobileSale,
      meta : {title: "N-POS | Sale"}
    },
    {
      path: '/mobile/print',
      name: 'MobilePrint',     
      component: MobilePrint,
      meta : {title: "N-POS | Print"}
    },
    {
      path: '/sale/:id/edit',
      name: 'EditSale',     
      component: EditSale,
      meta : {title: "N-POS | Sale / Edit"}

    },
    {
      path: '/soc',
      name: 'Soc',     
      component: SalesOnCredit,
      meta : {title: "N-POS | SOC"}

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
      meta : {title: "N-POS | Product Code"}

    },
    {
      path: '/stocks',
      name: 'Stocks',     
      component: Stocks,
      meta : {title: "N-POS |  Stocks / History"}

    },
    {
      path: '/stocks/new',
      name: 'NewStocks',     
      component: NewStocks,
      meta : {title: "N-POS | Stocks / New"}
    },
    {
      path: '/stocks/:id/edit',
      name: 'EditStocks',     
      component: EditStocks,
      meta : {title: "N-POS | Stocks / Edit"}
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: Purchase,
      meta : {title: "N-POS | Purchase / History"}
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: EditPurchase,
      meta : {title: "N-POS | Purchase / Edit"}
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: AddPurchase,
      meta : {title: "N-POS | Purchase / New"}    },
   
    {
      path: '/suppliers',
      name: 'Suppliers',     
      component: Suppliers,
      meta : {title: "N-POS | Suppliers"}
    },
    {
      path: '/customers',
      name: 'Customers',     
      component: Customers,
      meta : {title: "N-POS | Customers"}
    },
    {
      path: "/reports",
      name: "Reports",
      component: Reports,
      meta : {title: "N-POS | Reports"}
    },
    {
      path: "/reports/products/:reportType/:location",
      name: "ReportsProduct",
      component: ReportProduct,
      meta : {title: "N-POS | Report Medicine Code"}
    },
    {
      path: "/reports/transfers/:reportType/:fromDate/:toDate/:from_location/:to_location",
      name: "ReportsTransfer",
      component: ReportsTransfers,
      meta : {title: "N-POS | Report Transfers"}
    },
    {
      path: "/reports/stocks/:reportType/:fromDate/:toDate/:location",
      name: "ReportsStocks",
      component: ReportStocks,
      meta : {title: "N-POS | Report Stocks"}
    },
    {
      path: "/reports/sales/:reportType/:fromDate/:toDate",
      name: "ReportsSales",
      component: ReportSales,
      meta : {title: "N-POS | Report Sales"}
    },
    {
      path: "/reports/purchase/:reportType/:fromDate/:toDate/:location",
      name: "ReportsPurchase",
      component: ReportPurchase,
      meta : {title: "N-POS | Report Purchase"}
    },
    {
      path: "/reports/adjustments/:reportType/:fromDate/:toDate",
      name: "ReportsAdjustments",
      component: ReportAdjustments,
      meta : {title: "N-POS | Report Adjustments"}
    },
    {
      path: "/reports/soc/:reportType/:fromDate/:toDate",
      name: "ReportsSoc",
      component: ReportsSoc,
      meta : {title: "N-POS | Report SOC"}
    },
    {
      path: "/reports/return-in/:reportType/:fromDate/:toDate",
      name: "ReportsReturnIn",
      component: ReportReturnIn,
      meta : {title: "N-POS | Report Return In"}
    },
    {
      path: "/reports/return-out/:reportType/:fromDate/:toDate",
      name: "ReportsReturnOut",
      component: ReportReturnOut,
      meta : {title: "N-POS | Report Return Out"}
    },
    {
      path: '/transfers/:id/edit',
      name: 'EditTransfer',     
      component: EditTransfer,    
      meta: { title: 'N-POS | Transfer / Edit' },
    },
    {
      path: '/transfers/new',
      name: 'AddTransfer',     
      component: NewTransfer,    
      meta: { title: 'N-POS | Transfer / New' },
    },
    {
      path: '/transfer',
      name: 'Transfers',     
      component: Transfers,    
      meta: { title: 'N-POS | Transfer' },
    },
    {
      path: '/adjustments',
      name: 'Adjustments',     
      component: Adjustments,
      meta : {title: "N-POS | Adjustment"}
    },
    {
      path: '/adjustments/add',
      name: 'NewAdjustment',     
      component: NewAdjustment,
      meta : {title: "N-POS | Adjustment / New"}
    },
   
    {
      path: '/user/profile',
      name: 'Profile',     
      component: Profile,    
      meta: { title: 'N-POS | User profile' },
    },
    {
      path: '/backup-restore',
      name:'BackupRestore',
      component: BackupRestore,
      meta: { title: 'N-POS | Backup / Restore' },
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
