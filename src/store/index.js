import { createStore } from 'vuex'

 const store= createStore({
  state: {
    user : null,
    products :[],
    suppliers:[],
    purchaseItem:{},
    stockItem:{},
    printSale:{},
    reportsData:[],
    reportType:"",
    product:{},
  },

  getters: {
    user : state=> state.user,
    products :state=>state.products,
    suppliers:state=>state.suppliers,
    purchaseItem:state=>state.purchaseItem,
    stockItem:state=>state.stockItem,
    printSale:state=>state.printSale,
    reportsData:state=>state.reportsData,
    reportType:state=>state.reportType,
    product:state=>state.product,
  },

  mutations: {
        SET_AUTH_USER(state, payload){
            state.user=payload;           
        },    
        SET_PRODUCTS(state, payload){
            state.products=payload
        },
        SET_SUPPLIER(state, payload){
          state.suppliers=payload
      },
      SET_PURCHASE_ITEM(state, payload){
          state.purchaseItem=payload
      },
      SET_STOCK_ITEM(state, payload){
        state.stockItem=payload
      },
      SET_PRINT_SALE(state, payload){
          state.printSale=payload
      },
      SET_REPORTS_DATA(state, payload){
        state.reportsData=payload
      },
      SET_REPORT_TYPE(state, payload){
          state.reportType=payload
      },
      SET_PRODUCT(state, payload){
          state.product=payload;
      }
  },

  actions: {
        setAuthUser(context, payload){
            context.commit("SET_AUTH_USER", payload)
        },
        setProducts(context,payload){
            context.commit("SET_PRODUCTS", payload)
        },
        setSupplier(context,payload){
          context.commit("SET_SUPPLIER", payload)
      },
      setPurchaseItem(context, payload){
          context.commit("SET_PURCHASE_ITEM", payload)
      },
      setStockItem(context, payload){
        context.commit("SET_STOCK_ITEM", payload)
      },
      setPrintSale(context, payload){
        context.commit("SET_PRINT_SALE", payload)
      },
      setReportsData(context, payload){
          context.commit("SET_REPORTS_DATA", payload)
      },
      setReportType(context, payload){
          context.commit("SET_REPORT_TYPE", payload)
      },
      setProduct(context, payload){
          context.commit("SET_PRODUCT", payload)
      }
  },
  
})


export default store;
