import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/auth/Login.vue'
import Dashboard from "@/views/admin/Dashboard.vue"
import DashboardOld from "@/views/admin/DashboardOld.vue"
import Products from '@/views/products/Products.vue'
import ProductsOld from '@/views/products/ProductsOld.vue'
import Profile from "@/views/admin/Profile.vue"
import AddPurchase from '@/views/purchase/Add.vue'
import AddPurchaseOld from '@/views/purchase/AddOld.vue'
import Purchase from '@/views/purchase/Purchase.vue'
import PurchaseOld from '@/views/purchase/PurchaseOld.vue'
import EditPurchase from '@/views/purchase/Edit.vue'
import EditPurchaseOld from '@/views/purchase/EditOld.vue'
import Stocks from '@/views/stocks/Stocks.vue'
import StocksOld from '@/views/stocks/StocksOld.vue'
import NewStocks from '@/views/stocks/New.vue'
import NewStocksOld from '@/views/stocks/NewOld.vue'
import EditStocks from '@/views/stocks/Edit.vue'
import EditStocksOld from '@/views/stocks/EditOld.vue'
import Sale from '@/views/sales/Sale.vue'
import SaleOld from '@/views/sales/SaleOld.vue'
import MobileSale from '@/views/sales/MobileSale.vue'
import MobilePrint from '@/views/sales/MobilePrint.vue'
import Suppliers from '@/views/suppliers/Suppliers.vue'
import SuppliersOld from '@/views/suppliers/SuppliersOld.vue'
import PrintSale from '@/views/sales/Print.vue'
import Reports from '@/views/reports/Reports.vue'
import ReportsOld from '@/views/reports/ReportsOld.vue'
import ReportSales from '@/views/reports/Sales.vue'
import ReportExpenses from "@/views/reports/Expenses.vue"
import ReportStocks from '@/views/reports/Stocks.vue'
import ReportPurchase from '@/views/reports/Purchase.vue'
import ReportProduct from '@/views/reports/Products.vue'
import Customers from '@/views/customer/Customers.vue'
import CustomersOld from '@/views/customer/CustomersOld.vue'
import store from "@/store"
import Adjustments from '@/views/adjustments/Adjustments.vue'
import AdjustmentsOld from '@/views/adjustments/AdjustmentsOld.vue'
import NewAdjustment from '@/views/adjustments/New.vue'
import NewAdjustmentOld from '@/views/adjustments/NewOld.vue'
import ReportAdjustments from '@/views/reports/Adjustments.vue'
import SalesOnCredit from '@/views/sales/SalesOnCredit.vue'
import SalesOnCreditOld from '@/views/sales/SalesOnCreditOld.vue'
import ReportsSoc from '@/views/reports/Soc.vue'
import BackupRestore from '@/views/backup/BackupRestore.vue'
import EditSale from '@/views/sales/Edit.vue'
import EditSaleOld from '@/views/sales/EditOld.vue'

import CustomerDisplay from '@/views/sales/CustomerDisplay.vue'

import Expense from '@/views/expenses/Expense.vue'
import ExpenseOld from '@/views/expenses/ExpenseOld.vue'
import EditExpenseOld from '@/views/expenses/EditExpenseOld.vue'

import BarcodeSticker from '@/views/products/BarcodeSticker.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),  
  routes: [
    {
      path: '/expense',
      name: 'Expense',     
      component: store.getters.theme === "new" ? Expense : ExpenseOld,
      meta : {title: "N-POS | Expense"}
    },
    {
      path: '/expense/:id/edit',
      name: 'EditExpense',
      component: EditExpenseOld,
      meta: { title: 'N-POS | Expense / Edit' }
    },
    {
      path: "/customer-display", 
      name: "CustomerDisplay", 
      component: CustomerDisplay,
      meta : {title : "N-POS | NTG Technology"}
    },
    {
      path: '/mobile/print/:voucher_no',
      name: 'MobilePrint',     
      component: MobilePrint,
      meta : {title: "N-POS | Print"}
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
      component: store.getters.theme === "new" ? Dashboard : DashboardOld,
      meta : {title: "N-POS | Admin dashboard"}
    },
    {
      path: '/sale',
      name: 'Sale',     
      component: store.getters.theme === "new" ? Sale : SaleOld,
      meta : {title: "N-POS | Sale"}
    },
    {
      path: '/mobile/sale',
      name: 'MobileSale',     
      component: MobileSale,
      meta : {title: "N-POS | Sale"}
    },
   
    {
      path: '/sale/:id/edit',
      name: 'EditSale',     
      component: store.getters.theme === "new" ? EditSale : EditSaleOld,
      meta : {title: "N-POS | Sale / Edit"}

    },
    {
      path: '/soc',
      name: 'Soc',     
      component: store.getters.theme === "new" ? SalesOnCredit : SalesOnCreditOld,
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
      component: store.getters.theme === "new" ? Products : ProductsOld,
      meta : {title: "N-POS | Product Code"}

    },
    {
      path: '/barcode-sticker',
      name: 'BarcodeSticker',     
      component: BarcodeSticker,
      meta : {title: "N-POS | Barcode Sticker"}

    },
    {
      path: '/stocks',
      name: 'Stocks',     
      component: store.getters.theme === "new" ? Stocks : StocksOld,
      meta : {title: "N-POS |  Stocks / History"}

    },
    {
      path: '/stocks/new',
      name: 'NewStocks',     
      component: store.getters.theme === "new" ? NewStocks : NewStocksOld,
      meta : {title: "N-POS | Stocks / New"}
    },
    {
      path: '/stocks/:id/edit',
      name: 'EditStocks',     
      component: store.getters.theme === "new" ? EditStocks : EditStocksOld,
      meta : {title: "N-POS | Stocks / Edit"}
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: store.getters.theme === "new" ? Purchase : PurchaseOld,
      meta : {title: "N-POS | Purchase / History"}
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: store.getters.theme === "new" ? EditPurchase : EditPurchaseOld,
      meta : {title: "N-POS | Purchase / Edit"}
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: store.getters.theme === "new" ? AddPurchase : AddPurchaseOld,
      meta : {title: "N-POS | Purchase / New"}    },
   
    {
      path: '/suppliers',
      name: 'Suppliers',     
      component: store.getters.theme === "new" ? Suppliers : SuppliersOld,
      meta : {title: "N-POS | Suppliers"}
    },
    {
      path: '/customers',
      name: 'Customers',     
      component: store.getters.theme === "new" ? Customers : CustomersOld,
      meta : {title: "N-POS | Customers"}
    },
    {
      path: "/reports",
      name: "Reports",
      component: store.getters.theme === "new" ? Reports : ReportsOld,
      meta : {title: "N-POS | Reports"}
    },
    {
      path: "/reports/products/:reportType/:location",
      name: "ReportsProduct",
      component: ReportProduct,
      meta : {title: "N-POS | Report Medicine Code"}
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
      path: "/reports/expenses/:reportType/:fromDate/:toDate",
      name: "ReportsExpenses",
      component: ReportExpenses,
      meta : {title: "N-POS | Report Expenses"}
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
      path: '/adjustments',
      name: 'Adjustments',     
      component: store.getters.theme === "new" ? Adjustments : AdjustmentsOld,
      meta : {title: "N-POS | Adjustment"}
    },
    {
      path: '/adjustments/add',
      name: 'NewAdjustment',     
      component: store.getters.theme === "new" ? NewAdjustment : NewAdjustmentOld,
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
  if (to.name !== 'Login' && !isAuth &&  to.name !== 'MobilePrint') next({ name: 'Login' })
    else next()  
  //next();  
})

export default router
