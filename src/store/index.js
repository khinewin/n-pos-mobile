import { createStore } from 'vuex'
import {Timestamp} from 'firebase/firestore'


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
  },

  getters: {
    user : state=> state.user,
    general_supplier:state=>state.general_supplier,
    default_customer:state=>state.default_customer,
  },

  mutations: {
        SET_AUTH_USER(state, payload){
            state.user=payload;           
        },    
      
  },

  actions: {
        setAuthUser(context, payload){
            context.commit("SET_AUTH_USER", payload)
        },
     
  },
  
})


export default store;
