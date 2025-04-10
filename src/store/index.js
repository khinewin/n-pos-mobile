import { createStore } from 'vuex'
import {Timestamp} from 'firebase/firestore'
import { products } from '@/lib/snapshot/products';


 const store= createStore({
  state: {
    user : null,
    general_supplier: {
      id: 123456789,
      code: 123456789,
      name:"General Supplier",
      phone:"09xxxxxxxxx",
      company :"",
      created_at : Timestamp.fromDate(new Date()),
  },
  default_customer: {
    id: 123456789,
    code: 123456789,
    name:"Default Customer",
    phone:"09xxxxxxxxx",
    address :"",
    created_at : Timestamp.fromDate(new Date()),
  },
  flat_pickr_config: {
    dateFormat: 'd/m/Y', // Flatpickr options
    enableTime: false,
    disableMobile: true,
    wrap: true, // set wrap to true only when using 'input-group'
  },  
    products:[],
    packingSizes:[],
    printData:{},
    setLanguage: "eng"
  },

  getters: {
    user : state=> state.user,
    general_supplier:state=>state.general_supplier,
    default_customer:state=>state.default_customer,
    flat_pickr_config:state=>state.flat_pickr_config,
    products:state=>state.products,
    packingSizes:state=>state.packingSizes,
    printData:state=>state.printData,
    setLanguage: state=>state.setLanguage
  },

  mutations: {
        SET_AUTH_USER(state, payload){
            state.user=payload;           
        },    
        SET_PRODUCTS(state, payload){
          state.products=payload;
        },
        SET_PACKING_SIZES(state, payload){
          state.packingSizes=payload;
        },
        SET_PRINT_DATA(state, payload){
          state.printData=payload;
        }
      
  },

  actions: {
        setAuthUser(context, payload){
            context.commit("SET_AUTH_USER", payload)
        },
        setProducts(context, payload){
          context.commit("SET_PRODUCTS", payload)
        },
        setPackingSizes(context, payload){
          context.commit("SET_PACKING_SIZES", payload)
        },
        setPrintData(context, payload){
          context.commit("SET_PRINT_DATA", payload)
        }
     
  },
  
})


export default store;
