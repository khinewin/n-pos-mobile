<template lang="">
    <div>           
        <PageHeader :page-title="pageTitle" page-code="reports" :msg-type="msg.type" :msg-info="msg.info" />        
            <div class="container-fluid">
            <div class="row g-2">
                <div class="col-sm-8">
                    <div class="card  shadow min-vh-80">
                        <div class="card-body p-2 rounded small">
                            <div class="row g-2">                              
                                <div class="col-6">
                                   <div class="input-group mb-2">
                                    <div class="form-floating">
                                        <flat-pickr class="form-control-plaintext border-bottom" v-model="from_date" id="from_date" :config="config" />
                                        <label for="from_date">{{t('from_date')}}</label>
                                    </div>
                                    <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                   </div>  
                                </div>
                                <div class="col-6">
                                    <div class="input-group mb-2">
                                        <div class="form-floating">
                                            <flat-pickr class="form-control-plaintext border-bottom" v-model="to_date" id="to_date" :config="config" />
                                            <label for="to_date">{{t('to_date')}}</label>
                                        </div>
                                        <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                       </div>
                                </div>
                            </div>                                                    
                           
                           <div class="row g-2">
                                <div class="col-6">
                                    <div class="mb-2 input-group">
                                        <div class="form-floating ">
                                            <input data-bs-toggle="modal" readonly data-bs-target="#searchCode"  type="text" class="form-control-plaintext border-bottom" id="item_name" v-model="item_name" placeholder="Code">
                                            <label for="item_name">{{t('item_name')}}</label>
                                        </div> 
                                        <span class="input-group-text bg-white border-0 border-bottom" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="mb-2 input-group">
                                        <div class="form-floating ">
                                            <input  type="text" class="form-control-plaintext border-bottom" id="voucher_no" v-model="voucher_no" placeholder="Voucher no.">
                                            <label for="voucher_no">{{t('invoice_no')}}</label>
                                        </div> 
                                        <span class="input-group-text  bg-white border-0 border-bottom" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>
                                    </div>
                                </div>
                           </div>
                            <div class="row g-2">
                                <div class="col-6">
                                    <div class="input-group">
                                        <div class="form-floating">
                                            <input data-bs-toggle="modal" readonly data-bs-target="#searchSupplier" type="text" class="form-control-plaintext border-bottom" id="supplier" v-model="supplier.name" placeholder="Supplier">
                                            <label for="supplier">{{t('supplier')}}</label>
                                        </div> 
                                        <span class="input-group-text  bg-white border-0 border-bottom" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>  
                                    </div> 
                                </div>
                                <div class="col-6">
                                    <div class="input-group mb-2">
                                        <div class="form-floating">
                                            <input data-bs-toggle="modal" readonly data-bs-target="#customerModal" type="text" class="form-control-plaintext border-bottom" id="customer" v-model="customer.name" placeholder="Supplier">
                                            <label for="supplier">{{t('customer')}}</label>
                                        </div> 
                                        <span class="input-group-text  bg-white border-0 border-bottom" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>  
                                    </div>  
                                </div>                             
                            </div>         
                            <div class="row g-2 ">
                                <div class="col-6">
                                    <div class="mb-2 input-group">
                                        <div class="form-floating ">
                                            <select v-model="adj_type" class="form-control-plaintext border-bottom" id="adj_type">
                                                <option>Increment</option>
                                                <option>Decrement</option>
                                            </select>
                                            <label for="adj_type">{{t('adj_type')}}</label>
                                        </div> 
                                        <span class="input-group-text  bg-white border-0 border-bottom" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="mb-2 input-group">
                                        <div class="form-floating ">
                                            <select v-model="rts_rfs" class="form-control-plaintext border-bottom" id="rts_rfs">
                                                <option>Yes</option>
                                                <option>No</option>
                                            </select>
                                            <label for="item_name">{{t('rts_rfs')}}</label>
                                        </div> 
                                        <span class="input-group-text  bg-white border-0 border-bottom" @click="clearInput()"><i class="fa-solid fa-circle-xmark"></i></span>
                                    </div>
                                </div>
                               </div>                                                                                           
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <div class="card shadow min-vh-80">
                        <div class="card-body p-2 small report">                                                                                      
                            <table class="table table-bordered table-hover">
                                <tbody>
                                    <tr>
                                        <td>
                                            <ReportList @report="reportProducts" :title="t('product_code')" :arr="products" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <ReportList @report="reportStocks" :title="t('stocks')" :arr="stocks" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <ReportList @report="reportSales" :title="t('sale')" :arr="sales" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <ReportList @report="reportPurchase" :title="t('purchase')" :arr="purchase" />
                                        </td>
                                    </tr>
                                  
                                    <tr>
                                        <td>
                                            <ReportList @report="reportAdjustments" :title="t('adjustment')" :arr="adjustments" />
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <ReportList @report="reportSoc" :title="t('soc')" :arr="soc" />
                                        </td>
                                    </tr>
                                     <!--<tr>
                                        <td>
                                            <ReportList @report="reportExpenses" :title="t('expense')" :arr="expenses" />
                                        </td>
                                    </tr>-->
                                
                                </tbody>
                            </table>   
                        </div>                                          
                    </div>
                </div>
            </div>
        </div>
        <SupplierModal @setCode="setSupplier" />
        <CustomerModal @setCustomer="setCustomer" />
        <ProductModal @setCode="setCode" />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import store from "@/store"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
import SupplierModalOld from '@/views/modals/purchase/SupplierModalOld.vue'
import CustomerModal from '@/views/modals/sales/CustomerModal.vue'
import CustomerModalOld from '@/views/modals/sales/CustomerModalOld.vue'
import ProductModal from "@/views/modals/reports/ProductModal.vue"
import ReportList from "@/views/reports/ReportList.vue"
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"
import * as hp from "@/lib/helper"
import {lang} from '@/lib/language'

export default {
    components: {
        PageHeader, 
        SupplierModal: store.getters.theme === "new" ? SupplierModal : SupplierModalOld,
        CustomerModal: store.getters.theme === "new" ? CustomerModal : CustomerModalOld,
        ProductModal, 
        ReportList, 
        FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{      
          
            adj_type:"",   
            rts_rfs: "",
            gt_equal:null,
            lt_equal:null,
            from_location:"Main",
            to_location:"Mart",
            item_name:"",
            location: "All",
            code_no:"",
            item_name:"",

            to_date:"",
           from_date:"",

            voucher_no:"N-",
            packing_unit:"S Unit",
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
            category: "",
            products:[
                {code : "stock_list", name: "stocks list", active: true},
                {code: "stock_list_sale_price", name:"stocks list sale price", active: true},
                {code: "stock_list_pur_price", name: "stocks list pur price", active: true},    
                {code: "stock_list_pur_sale_price", name: "stocks list pur & sale price", active: true},             
             ],
             stocks:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by product name", active: true},
                {code: "by_voucher", name: "by voucher", active: true},               
             ],
             adjustments:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by product name", active: true},
                {code: "by_voucher", name: "by voucher", active: true},    
                {code: "by_location", name: "by location", active: true},  
                {code: "by_adj_type", name: "by type", active: true},               
             ],
        
            sales:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by product name", active: true},
                {code: "by_description_date", name:"by product name and date", active: true},
                {code: "by_voucher", name: "by voucher", active: true},    
                {code: "by_customer", name:"by customer", active: true},
                {code: "by_customer_date", name: "by customer and date", active: true},    
                {code: "by_chart", name: "by chart", active: true},              
             ],
              expenses:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_category", name:"by category", active: true},
                {code: "by_voucher", name: "by voucher", active: true},                 
             ],
            purchase:[
                {code : "by_date", name: "by date", active: true},
                {code: "by_description", name:"by product name", active: true},
                {code: "by_voucher", name: "by voucher", active: true},
                {code: "by_supplier", name:"by supplier", active: true},
                {code: "by_supplier_date", name: "by supplier and date", active: true},
               // {code: "by_exp_date", name: "by expire date", active: true}
            ],
            soc:[
                {code : "by_customer", name: "by customer", active: true},
                {code: "by_customer_date", name:"by customer and date", active: true},
                {code: "by_voucher", name: "by voucher", active: true},               
             ],
           
        }
    },
  
    mounted() {
        let date = new Date();
        const firstDay = new Date(date.getFullYear(), date.getMonth(),1);
        const lastDay = new Date(date.getFullYear(), date.getMonth() + 1,0)

       this.from_date=hp.setToday();
       this.to_date=hp.setToday();

       window.addEventListener('keydown', this.handleCtrl);
    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handleCtrl);
     },
   watch:{
   
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
        },
        config(){
            return this.$store.getters.flat_pickr_config;
        },
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.reports.mm}` : `${lang.reports.eng}`            
            return pageTitle;
        },
        t() {
            return (key) => this.$store.getters.t(key);
        },
        
    },
    methods:{
        handleCtrl(event) {
     
          return hc.handleCtrl(event)
       
      },
        parseYMD(dateStr) {
            const [day, month, year] = dateStr.split('-');
            return  `${year}-${month}-${day}`;
        },
        parseDMY(dateStr) {
            const [day, month, year] = dateStr.split('-');
            return  `${day}-${month}-${year}`;
        },
        setCode(p){
            this.code_no=p.code_no;
            this.item_name=p.item_name;
        },
        clearInput(){
            this.voucher_no="";
            this.supplier={}
            this.customer={}
            this.lt_equal=null;
            this.gt_equal=null;
            this.from_location="Main";
            this.to_location="Mart";
            this.location="All";
            this.adj_type="";
            this.rts_rfs="";
        },
        setCustomer(c){
            this.customer=c;
        },
        setSupplier(s){
            this.supplier=s;
        },
        handleFromLocation(location){
           if(location=="Main"){
                this.from_location="Main";
                this.to_location="Mart"
            } 
            if(location=="Mart"){
                this.from_location="Mart";
                this.to_location="Main"
            }           
      },
      handleToLocation(location){
        if(location=="Main"){
                this.from_location="Mart";
                this.to_location="Main"
            } 
            if(location=="Mart"){
                this.from_location="Main";
                this.to_location="Mart"
            }     
      },
        reportAdjustments(r_type){                     
            this.$router.push({name: "ReportsAdjustments", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no, "code_no": this.code_no ? this.code_no : null, "item_name" : this.item_name ? this.item_name : null,
            adj_type: this.adj_type ? this.adj_type : null, rts_rfs : this.rts_rfs ? this.rts_rfs :null, location: this.location
            }})
        },
        reportProducts(r_type){
            this.$router.push({name: "ReportsProduct", params :{"reportType": r_type, "location": this.location},
            query : { "code_no": this.code_no ? this.code_no : null, "item_name": this.item_name ? this.item_name : null,
            gt_equal : this.gt_equal , lt_equal : this.lt_equal, packing_unit: this.packing_unit
            }
           })
        },
        reportStocks(r_type){                     
            this.$router.push({name: "ReportsStocks", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date, "location": this.location},
            query : {voucher_no: this.voucher_no, code_no: this.code_no, item_name: this.item_name
            }})
        },
        
    
        reportSales(r_type){                     
            this.$router.push({name: "ReportsSales", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no,  customer_code: this.customer.code ? this.customer.code : null,
                customer_name: this.customer.name ? this.customer.name : null, code_no: this.code_no ? this.code_no : null, item_name: this.item_name ? this.item_name : null}
            })
        },
        reportExpenses(r_type){                     
            this.$router.push({name: "ReportsExpenses", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no,  category_code: this.category.code ? this.category.code : null,
                category_name: this.category.name ? this.category.name : null, }
            })
        },
        reportPurchase(r_type){                     
            this.$router.push({name: "ReportsPurchase", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date, "location": this.location},
            query : {voucher_no: this.voucher_no ? this.voucher_no : null,
            supplier: this.supplier.code ? this.supplier.code : null,
            supplier_name: this.supplier.code ? this.supplier.name : null,
            supplier_company: this.supplier.code ? this.supplier.company : null,
            "code_no": this.code_no ? this.code_no : null, "item_name" : this.item_name ? this.item_name : null
            }})
        },     
        reportSoc(r_type){                     
            this.$router.push({name: "ReportsSoc", params :{"reportType": r_type, "fromDate": this.from_date, "toDate": this.to_date},
            query : {voucher_no: this.voucher_no,  customer_code: this.customer.code ? this.customer.code : null,
                customer_name: this.customer.name ? this.customer.name : null, code_no: this.code_no ? this.code_no : null, item_name: this.item_name ? this.item_name : null}
            })
        },
     
    }
}
</script>
<style lang="">
    
</style>
