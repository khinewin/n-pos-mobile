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
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),  
  routes: [
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
      path: '/sale',
      name: 'Sale',     
      component: Sale,
      meta : {title: "N-POS | Sales"}

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
      meta : {title: "N-POS | Products code"}

    },
    {
      path: '/products/opening-stocks',
      name: 'OpeningStocks',     
      component: OpeningStocks,
      meta : {title: "N-POS | Opening stocks"}

    },
    {
      path: '/products/opening-stocks/new',
      name: 'NewOpeningStocks',     
      component: NewOpeningStocks,
      meta : {title: "N-POS | New opening stock"}
    },
    {
      path: '/products/opening-stocks/:id/edit',
      name: 'EditOpeningStocks',     
      component: EditOpeningStocks,
      meta : {title: "N-POS | Edit opening stock"}
    },
    {
      path: '/purchase',
      name: 'Purchase',     
      component: Purchase,
      meta : {title: "N-POS | Purchase"}
    },
    {
      path: '/purchase/:id/edit',
      name: 'EditPurchase',     
      component: EditPurchase,
      meta : {title: "N-POS | Edit purchase"}
    },
    {
      path: '/purchase/add',
      name: 'AddPurchase',     
      component: AddPurchase,
      meta : {title: "N-POS | Add purchase"}    },
   
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
      path: "/reports/products/:reportType",
      name: "ReportsProduct",
      component: ReportProduct,
      meta : {title: "N-POS | Report product code"}
    },
    {
      path: "/reports/stocks/:reportType/:fromDate/:toDate",
      name: "ReportsStocks",
      component: ReportStocks,
      meta : {title: "N-POS | Report opening stock"}
    },
    {
      path: "/reports/sales/:reportType/:fromDate/:toDate",
      name: "ReportsSales",
      component: ReportSales,
      meta : {title: "N-POS | Report sales"}
    },
    {
      path: "/reports/purchase/:reportType/:fromDate/:toDate",
      name: "ReportsPurchase",
      component: ReportPurchase,
      meta : {title: "N-POS | Report purchase"}
    },
    {
      path: '/user/profile',
      name: 'Profile',     
      component: Profile,    
      meta: { title: 'N-POS | User profile' },
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
