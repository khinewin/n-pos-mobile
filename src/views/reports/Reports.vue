<template lang="">
    <div>           
        <PageHeader page-title="Reports" page-code="reports" :msg-type="msg.type" :msg-info="msg.info" />        
            <div class="container my-2">
            <div class="row">
                <div class="col-6">
                    <div class="card  shadow">
                        <div class="card-body p-2 rounded small">
                            <div class="form-floating mb-2">
                                <input type="date" class="form-control form-control-sm" id="from_date" v-model="from_date" placeholder="From date">
                                <label for="from_date">From date</label>
                            </div>  
                            <div class="form-floating mb-2">
                                <input type="date" class="form-control form-control-sm" id="to_date" v-model="to_date" placeholder="To date">
                                <label for="to_date">To date</label>
                            </div>    
                            <div class="mb-2 input-group">
                                <div class="form-floating ">
                                    <input  type="text" class="form-control form-control-sm" id="voucher_no" v-model="voucher_no" placeholder="Voucher no.">
                                    <label for="voucher_no">Voucher no.</label>
                                </div> 
                                <span class="input-group-text" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>
                            </div>
                            <div class="input-group mb-2">
                                <div class="form-floating">
                                    <input data-bs-toggle="modal" data-bs-target="#customerModal" type="text" class="form-control form-control-sm" id="customer" v-model="customer.name" placeholder="Supplier">
                                    <label for="supplier">Customer</label>
                                </div> 
                                <span class="input-group-text" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>  
                            </div>   
                            <div class="input-group">
                                <div class="form-floating">
                                    <input data-bs-toggle="modal" data-bs-target="#searchSupplier" type="text" class="form-control form-control-sm" id="supplier" v-model="supplier.name" placeholder="Supplier">
                                    <label for="supplier">Supplier</label>
                                </div> 
                                <span class="input-group-text" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>  
                            </div> 
                                             
                        </div>
                    </div>
                </div>
                <div class="col-6">
                    <div class="card shadow">
                        <div class="card-body p-2 small report">                                                                                      
                            <ul class="list-group">
                                <li class="list-group-item">
                                          <!-- Stock dropend button -->
                                          <div class="btn-group dropend">
                                            <button type="button" class="btn btn-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                                Product code
                                            </button>                               
                                            <ul class="dropdown-menu py-0">                                  
                                                <li class="list-group-item" v-for="sl in products" :key="sl.code">                                     
                                                    <button class="dropdown-item small" @click="reportProducts(sl.code)">{{sl.name}}</button>
                                                </li>                                             
                                            </ul>
                                        </div>
                                        <!--stock dropen button-->
                                </li>
                                <li class="list-group-item">
                                    <!-- stocks dropend button -->
                                  <div class="btn-group dropend">
                                      <button type="button" class="btn btn-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                          Stocks
                                      </button>                               
                                      <ul class="dropdown-menu py-0">                                  
                                          <li class="list-group-item" v-for="st in stocks" :key="st.code">                                     
                                              <button :class="{'disabled' : !st.active}" class="dropdown-item small" @click="reportStocks(st.code)">{{st.name}}</button>
                                          </li>   
                                      </ul>
                                  </div>
                                  <!--stocks dropen button-->
                              </li>
                                <li class="list-group-item">
                                      <!-- Sales dropend button -->
                                    <div class="btn-group dropend">
                                        <button type="button" class="btn btn-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                            Sales
                                        </button>                               
                                        <ul class="dropdown-menu py-0">                                  
                                            <li class="list-group-item" v-for="s in sales" :key="s.code">                                     
                                                <button :class="{'disabled' : !s.active}" class="dropdown-item small" @click="reportSales(s.code)">{{s.name}}</button>
                                            </li>   
                                        </ul>
                                    </div>
                                    <!--Sales dropen button-->
                                </li>
                                <li class="list-group-item">
                                    <!-- Sales dropend button -->
                                  <div class="btn-group dropend">
                                      <button type="button" class="btn btn-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
                                          Purchase
                                      </button>                               
                                      <ul class="dropdown-menu py-0">                                  
                                          <li class="list-group-item" v-for="p in purchase" :key="p.code">                                     
                                              <button :class="{'disabled' : !p.active}" class="dropdown-item small" @click="reportPurchase(p.code)">{{p.name}}</button>
                                          </li>                                          
                                      </ul>
                                  </div>
                                  <!--Sales dropen button-->
                              </li>
                            </ul>
                        </div>                                          
                    </div>
                </div>
            </div>
        </div>
        <SupplierModal @setCode="setSupplier" />
        <CustomerModal @setCustomer="setCustomer" />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
import CustomerModal from '@/views/modals/sales/CustomerModal.vue'

export default {
    components: {
        PageHeader, SupplierModal, CustomerModal
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{         
            from_date:"",
            to_date:"",
            voucher_no:"",
            supplier: {
                name: "",
            },
            customer: {
                name:"",
            },
            isLoading:false,
            salesData:[],
            msg : {
                type :"",
                info :"",
            },
            products:[
                {code : "stock_list", name: "stock list", active: true},
                {code: "stock_list_sale_price", name:"stock list sale price", active: true},
                {code: "stock_list_pur_price", name: "stock list pur price", active: true},    
                {code: "stock_list_pur_sale_price", name: "stock list pur & sale price", active: true},             
             ],
             stocks:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by description", active: true},
                {code: "by_voucher", name: "by voucher", active: true},               
             ],
            sales:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by description", active: true},
                {code: "by_voucher", name: "by voucher", active: true},    
                {code: "by_customer", name:"by customer", active: true},
                {code: "by_customer_date", name: "by customer and date", active: true},    
                {code: "by_chart", name: "by chart", active: true},              
             ],
            purchase:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by description", active: true},
                {code: "by_voucher", name: "by voucher", active: true},
                {code: "by_supplier", name:"by supplier", active: true},
                {code: "by_supplier_date", name: "by supplier and date", active: true},
                {code: "by_exp_date", name: "by expire date", active: true}
            ],
           
        }
    },
  
    mounted() {
        let date = new Date();
        let firstDay = new Date(date.getFullYear(), date.getMonth(),1+1).toISOString().substr(0, 10);
        let lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 1).toISOString().substr(0, 10);
        this.from_date=firstDay
        this.to_date=lastDay
    },
   watch:{
    user(data){
               if(data !==null){             
                //   this.fetchSales();           
           }
       },
    msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                        this.msg.info="";
                    }, 5000)
                }
            },deep:true
        }
   },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        clearInput(){
            this.voucher_no="";
            this.supplier={},
            this.customer={}
        },
        setCustomer(c){
            this.customer=c;
        },
        setSupplier(s){
            this.supplier=s;
        },
        reportProducts(r_type){
            this.$router.push({name: "ReportsProduct", params :{"reportType": r_type}
           })
        },
        reportStocks(r_type){                     
            this.$router.push({name: "ReportsStocks", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no
            }})
        },
        reportSales(r_type){                     
            this.$router.push({name: "ReportsSales", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no,  customer_code: this.customer.code ? this.customer.code : null,
                customer_name: this.customer.name ? this.customer.name : null,}})
        },
        reportPurchase(r_type){                     
            this.$router.push({name: "ReportsPurchase", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no ? this.voucher_no : null,
            supplier: this.supplier.code ? this.supplier.code : null,
            supplier_name: this.supplier.code ? this.supplier.name : null,
            supplier_company: this.supplier.code ? this.supplier.company : null,
            }})
        },     
     
    }
}
</script>
<style lang="">
    
</style>
