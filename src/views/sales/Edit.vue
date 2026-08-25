<template>
    <div>       
      
        <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
          <div class="container-fluid">    
        <div class="mb-2">
            <div class="d-flex justify-content-between align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                 <!-- Left: Actions -->
                 <div class="d-flex gap-2">
                     <router-link :to="{name: 'Sale'}" class="btn btn-light shadow-sm text-primary fw-bold border-0">
                        <i class="fa-solid fa-arrow-left me-2"></i> {{t('back')}}
                     </router-link>
                 </div>

                 <!-- Center: Mode Indicator -->
                 <div class="bg-light rounded-pill px-4 py-2">
                    <span class="fw-medium text-muted text-uppercase small">
                        <i class="fa-solid fa-pen-to-square me-2"></i> Edit Mode
                    </span>
                 </div>

                 <!-- Right: Main Actions -->
                 <div class="d-flex gap-2">
                     <button :disabled="saleItems.length <=0" class="btn btn-light shadow-sm fw-medium border-0" @click="printSale">
                        <i class="fa-solid fa-print me-2"></i> {{t('print_link')}}
                     </button>
                     <router-link :to="{name: 'Sale'}" id="close" class="btn btn-light shadow-sm text-danger fw-medium border-0">
                        <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                     </router-link>
                     <button v-if="saleMode =='Sale / Edit'" type="button" @click="saveSale" class="btn btn-primary shadow-sm px-4 fw-bold border-0" :disabled="isLoading || saleItems.length<=0">
                        <span v-if="!isLoading"><i class="fa-solid fa-hard-drive me-2"></i> {{t('save_link')}}</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>
                 </div>
            </div>
        </div>

        <div class="mb-2">
             <div class="row g-3">
                 <!-- Invoice Details Card -->
                 <div class="col-12">
                     <div class="card border-0 shadow-sm rounded-3">
                         <div class="card-body p-2">
                             <div class="row g-4 align-items-center">
                                 <!-- Voucher No -->
                                 <div class="col-md-3">
                                     <label class=" small text-muted text-uppercase fw-bold">{{t('invoice_no')}}</label>
                                     <div class="input-group">
                                         <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                         <input type="text" :class="{'is-invalid' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" id="voucher_no" placeholder="Invoice #">
                                     </div>
                                 </div>
                                 
                                  <!-- Date -->
                                 <div class="col-md-3">
                                     <label class=" small text-muted text-uppercase fw-bold">{{t('date')}}</label>
                                       <div class="input-group">
                                         <span class="input-group-text bg-light border-0"><i class="fa-regular fa-calendar text-secondary"></i></span>
                                         <flat-pickr class="form-control bg-light border-0 fw-medium" v-model="sale_date" id="sale_date" :config="config" />
                                       </div>
                                 </div>

                                  <!-- Customer -->
                                 <div class="col-md-3">
                                     <label class=" small text-muted text-uppercase fw-bold">{{t('customer')}}</label>
                                     <div class="input-group cursor-pointer" data-bs-toggle="modal" data-bs-target="#customerModal">
                                         <span class="input-group-text bg-light border-0"><i class="fa-regular fa-user text-secondary"></i></span>
                                          <input type="text" readonly v-model="customer.name" class="form-control bg-light border-0 fw-medium cursor-pointer" id="customer_name" placeholder="Select Customer">
                                          <span class="input-group-text bg-light border-0 text-primary"><i class="fa-solid fa-chevron-down small"></i></span>
                                     </div>
                                 </div>

                                  <!-- Barcode -->
                                 <div class="col-md-3">
                                      <label class=" small text-muted text-uppercase fw-bold change-cursor" @click="setBarcodeFocus">{{t('barcode')}}</label>
                                      <div class="input-group shadow-sm">
                                            <span class="input-group-text bg-primary text-white border-0"><i class="fa-solid fa-barcode"></i></span>
                                            <input :disabled="saleMode=='Sale / History'" @keypress.enter="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control border-0 bg-white fw-bold" placeholder="Scan Barcode..." autofocus>
                                      </div>
                                 </div>
                             </div>
                         </div>
                     </div>
                 </div>
             </div>
        </div>
                              <div class="vh-50 overflow-y-auto dataContainer bg-white rounded-3 shadow-sm border-0">
                                <table class="table table-hover mb-0 align-middle">
                                    <thead class="position-sticky top-0 z-3">
                                        <tr class="table-primary fw-bold small text-uppercase" style="font-size: 0.75rem;">
                                            <th class="text-center col-0" colspan="2">#</th>
                                            <th class="col-3">{{t('item_name')}}</th>  
                                            <th class="col-2 text-center">{{t('qty')}}</th>
                                            <th class="col-2 text-center">{{t('unit_price')}}</th>
                                            <th class="col-1 text-end">{{t('amount')}}</th>
                                            <th class="col-2 text-center" colspan="2">{{t('discount')}}</th>
                                            <th class="col-1 text-end pe-3">{{t('net_amount')}}</th>
                                        </tr>    
                                    </thead>                                  
                                    <tbody class="text-nowrap small">                                                                         
                                        <tr  v-if="saleItems.length > 0" v-for="(s, i) in saleItems.filter((f)=>!f.is_remove)" :key="s.id" @mouseenter="setEdit(s)"  >
                                            <td class="text-center">
                                                <button type="button" @click="removeSaleItems(s)" class="btn btn-sm text-danger border-0 p-0" :disabled="saleMode=='Sale / History'"><i class="fa-solid fa-circle-minus fa-lg"></i></button>
                                            </td>
                                            <td class="text-center text-muted font-monospace">                                                
                                                {{i+1}}
                                            </td>
                                            <td class="fw-bold text-dark">{{s.item_name}}</td>                                           
                                            <td class="table-primary-subtle">
                                                <div class="d-flex align-items-center justify-content-between px-2">
                                                    <button class="btn btn-link text-danger p-0" :disabled="saleMode=='Sale / History'"  @click="decreaseQty(s, s.sale_unit, s.barcode)">
                                                        <i class="fa-solid fa-circle-minus fs-5"></i>
                                                    </button>
                                                    <input :disabled="saleMode=='Sale / History'" type="number" class="form-control form-control-sm border-0 bg-transparent text-center fw-bold w-50" @keyup="setEdit(s)" @keypress.enter="calEdit(s)" v-model="s.sale_qty" @change="calEdit(s)">
                                                    <button :disabled="saleMode=='Sale / History'" class="btn btn-link text-success p-0" @click="increaseQty(s)">
                                                        <i class="fa-solid fa-circle-plus fs-5"></i>
                                                    </button>
                                                </div>
                                            </td>                                            
                                            <td class="table-info-subtle text-center">
                                                <div class="d-flex align-items-center justify-content-center gap-1">
                                                    <input disabled type="number" v-model="s.qty_sale_price" class="form-control form-control-sm border-0 bg-transparent text-center fw-bold w-50 p-0"> 
                                                    <span class="badge bg-light text-secondary border fw-normal">{{s.qty_sale_price_unit}}</span>
                                                </div>                                       
                                            </td>
                                           
                                            <td class="text-end fw-medium text-muted">{{fM(s.amount)}}</td>
                                            <td class="text-center table-warning-subtle cursor-pointer" id="setDiscount" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                                <div class="d-flex flex-column gap-1 small" style="font-size: 0.65rem;">
                                                    <span v-if="s.dis_qty" class="badge bg-danger-subtle text-danger">{{s.dis_qty}} {{s.dis_qty_unit}}</span>
                                                    <span v-if="s.dis_amount" class="badge bg-danger-subtle text-danger">Ks {{fM(s.dis_amount)}}</span>
                                                    <span v-if="s.dis_percent" class="badge bg-danger-subtle text-danger">{{s.dis_percent}}%</span>
                                                    <span v-if="!s.dis_qty && !s.dis_amount && !s.dis_percent" class="text-muted opacity-25"><i class="fa-solid fa-tag"></i></span>
                                                </div>      
                                            </td>
                                            <td class="text-end table-warning-subtle cursor-pointer fw-bold text-danger px-2" data-bs-toggle="modal" data-bs-target="#setDiscountModal">{{fM(s.discount??0)}}</td>
                                            <td class="text-end fw-bold text-primary pe-3">{{fM(s.final_amount)}}</td>
                                        </tr> 
                                        <!-- Empty State -->
                                        <tr v-if="saleItems.length === 0">
                                            <td colspan="9" class="text-center py-5">
                                                <div class="text-muted py-4">
                                                    <i class="fa-solid fa-cart-shopping fa-3x mb-3 opacity-25"></i>
                                                    <div class="fw-bold">No items found</div>
                                                    <small>Add items to this sale record</small>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                              </div>

                                </div>

                                
                                <!-- Premium Sticky Bottom Summary Bar -->
                                <div class="sticky-bottom bg-white shadow-lg border-top py-3 px-4 z-2">
                                    <div class="row align-items-center">
                                         <!-- Left: Remarks/Notes -->
                                         <div class="col-lg-3 mb-2 mb-lg-0">
                                              <div class="form-floating text-muted">
                                                  <textarea :disabled="saleMode=='Sale / History'" @keypress.enter="saveSale" v-model="remark" class="form-control border-0 bg-light rounded-3" placeholder="Notes" id="editRemarkArea" style="height: 60px; resize: none;"></textarea>
                                                  <label for="editRemarkArea" class="small">{{t('notes')}}</label>
                                              </div>
                                         </div>
            
                                         <!-- Right: Totals Breakdown -->
                                         <div class="col-lg-9">
                                              <div class="row g-2 align-items-end justify-content-end text-nowrap">
                                                   <!-- Subtotal -->
                                                   <div class="col-6 col-md-2 text-end">
                                                        <label class="small text-muted fw-bold d-block text-uppercase mb-1" style="font-size: 0.7rem;">{{t('subtotal')}}</label>
                                                        <div class="fs-5 fw-bold text-secondary">{{fM(total_amount)}}</div>
                                                   </div>

                                                   <!-- Discount -->
                                                   <div class="col-6 col-md-2 text-end">
                                                        <label class="small text-danger fw-bold d-block text-uppercase mb-1" style="font-size: 0.7rem;">{{t('additional_discount')}}</label>
                                                        <div class="d-flex align-items-center justify-content-end">
                                                            <input 
                                                                @focus="setInput" 
                                                                @blur="setInput" 
                                                                type="number" 
                                                                @keyup="calTotal" 
                                                                v-model="extra_discount" 
                                                                class="form-control form-control-sm text-end border-0 bg-transparent fw-bold text-danger p-0 fs-5 shadow-none" 
                                                                style="width: 100px;"
                                                                placeholder="0">
                                                        </div>
                                                   </div>
            
                                                   <!-- Net Amount (Highlighted) -->
                                                   <div class="col-12 col-sm-4 col-md-3 text-end bg-primary text-white p-2 rounded-2 shadow-sm mx-2">
                                                        <label class="small fw-bold d-block text-uppercase opacity-75" style="font-size: 0.65rem;">{{t('total')}}</label>
                                                        <div class="fs-3 fw-bolder lh-1">{{fM(net_amount)}}</div>
                                                    </div>
            
                                                   <!-- Paid -->
                                                   <div class="col-6 col-md-2 text-end">
                                                        <label class="small text-success fw-bold d-block text-uppercase mb-1" style="font-size: 0.7rem;">{{t('paid')}}</label>
                                                        <div class="d-flex align-items-center justify-content-end">
                                                            <input 
                                                                @focus="setInput" 
                                                                @blur="setInput" 
                                                                @keypress.enter="saveSale" 
                                                                @keyup="calPaid" 
                                                                type="number" 
                                                                v-model="paid" 
                                                                class="form-control form-control-sm text-end border-0 bg-transparent fw-bold text-success p-0 fs-5 shadow-none" 
                                                                style="width: 100px;"
                                                                placeholder="0">
                                                        </div>
                                                   </div>
                                                   
                                                   <!-- Change/Balance Due -->
                                                   <div class="col-6 col-md-2 text-end border-start ps-3">
                                                       <div v-if="change > 0">
                                                           <label class="small text-muted fw-bold d-block text-uppercase mb-0" style="font-size: 0.6rem;">{{t('change')}}</label>
                                                           <span class="fs-5 fw-bold text-dark">{{fM(change)}}</span>
                                                       </div>
                                                       <div v-else>
                                                           <label class="small text-danger fw-bold d-block text-uppercase mb-0" style="font-size: 0.6rem;">{{t('balance_due')}}</label>
                                                           <span class="fs-5 fw-bold text-danger">{{fM(credit)}}</span>
                                                       </div>
                                                   </div>
                                              </div>
                                         </div>
                                    </div>
                                </div>

                        
                   
        <ProductModal @setCode="setCode" :sale_type="sale_type" :saleItems="saleItems"/>
        <SalesModal @setCode="setOldCode" />
        <CustomerModal @setCustomer="setCustomer" />
        <!--Print item start-->
        <div class="d-none" id="printMe" v-if="saleItems.length > 0" >
                <Print :data="{
                    voucher_no: this.voucher_no,
                    customer: this.customer,
                    saleItems: this.saleItems,
                    sale_date:this.sale_date,
                    print_date: this.print_date,
                    total_amount:this.total_amount,
                    net_amount:this.net_amount,
                    extra_discount: this.extra_discount,
                    paid:this.paid,
                    change:this.change,
                    user: this.user,     
                    credit: this.credit,    
                    saleMode: this.saleMode,   
            }  " />
        </div>       
        <setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />
        
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {disableNetwork, Timestamp, collection, addDoc, doc, increment, setDoc, deleteDoc, updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
//import Products from "@/views/sales/Products.vue"
import SalesModal from "@/views/modals/sales/SalesModal.vue"
import SalesModalOld from "@/views/modals/sales/SalesModalOld.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import CustomerModalOld from "@/views/modals/sales/CustomerModalOld.vue"
import Print from "@/views/sales/Print.vue"
import ProductModal from "@/views/modals/sales/ProductModal.vue"
import ProductModalOld from "@/views/modals/sales/ProductModalOld.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/sales"
import setQtyModal from "@/views/modals/setQtyModal.vue"
import setQtyModalOld from "@/views/modals/setQtyModalOld.vue"
import setDiscountModal from "@/views/modals/setDiscountModal.vue"
import setDiscountModalOld from "@/views/modals/setDiscountModalOld.vue"
import FlatPickr from 'vue-flatpickr-component'
import * as hc from "@/lib/handleCtrl"
import * as pd from "@/lib/snapshot/products"
import {lang} from "@/lib/language"

export default {
    components: {
        PageHeader,
        SalesModal: store.getters.theme === "new" ? SalesModal : SalesModalOld,
        CustomerModal: store.getters.theme === "new" ? CustomerModal : CustomerModalOld,
        Print, 
        ProductModal: store.getters.theme === "new" ? ProductModal : ProductModalOld,
        setQtyModal: store.getters.theme === "new" ? setQtyModal : setQtyModalOld,
        setDiscountModal: store.getters.theme === "new" ? setDiscountModal : setDiscountModalOld,
        FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{    
         
            saleMode: "Sale / Edit",
            isLoading:false,
            sale_type: "retail",
            saleItems:[],
            editItem:{},
            store_item:{},
            voucher_no:this.$route.params.id,
            id:"",
            sale_date:"",
            print_date:"",
            total_amount:0,
            net_amount:0,
            extra_discount:0,
            credit:0,
            charges:0,
            paid:0,
            change:0,
            isLimitQty:false,
            customer: {},
            code_no:"",
            products:[],
            print_date:"",
            isOffline: false,
            remark:"",
            
            msg : {
                type :"",
                info :"",
            },
            output: null,        
             
        }
    },
    validations(){
            return {
                voucher_no: {required},
                sale_date: {required}
            }
    },
   
    mounted() {
        //this.voucher_no=new Date().getTime();
      //  this.sale_date=hp.setToday()
      //  this.print_date=hp.setToday();
     //   this.setCustomer(this.default_customer)
        this.fetchSale();
       setTimeout(()=>{
       // const code_no=document.getElementById("code_no");
        //code_no.focus();
       }, 300)
      // await disableNetwork(db)
     //set print date     
     window.addEventListener('keydown', this.handleCtrl);
     if(this.storeProducts.length){
            this.products=this.storeProducts;
        }
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
                    }, 3000)
                }
            },deep:true
        },
        storeProducts:{
            handler(val){
               if(val){
                   this.products=val;
               }
            }, deep: true
       },
    },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        default_customer(){
            return this.$store.getters.default_customer;
        },
        config(){
            return this.$store.getters.flat_pickr_config;
        },
        storeProducts(){
            return this.$store.getters.products;
        },
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            const pageTitle=sl=="mm" ?  `${lang.sale.mm} / ${lang.edit.mm}` : `${lang.sale.eng} / ${lang.edit.eng}`
            return pageTitle;
        },
      
       t() {
            return (key) => this.$store.getters.t(key);
        },
      
    },
    methods:{   
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        setBarcodeFocus(){
            const code_no=document.getElementById("code_no");
            code_no.focus();
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveSale(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const close=document.getElementById("close")
                close.click();
            }
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.printSale(); // Call your custom function
            }
           return hc.handleCtrl(event);
        },
        increaseQty(p){
        const checkQty=this.checkQty(p);
        if(!checkQty) return false;
         let sale_qty=1;         
          let saleItems= this.saleItems.map((f)=>{
                if(f.id ===p.id){
                    return {...f, 
                        sale_qty : f.sale_qty + sale_qty,
                         amount:  f.amount + f.qty_sale_price,
                         final_amount: f.final_amount + f.qty_sale_price,
                        }
                }
                return {
                    ...f
                }               
            })
            this.saleItems=saleItems;
            this.calTotal();
        },
        decreaseQty(p){     
         if(p.sale_qty <=1)return false;      
         let sale_qty=1;         
          let saleItems= this.saleItems.map((f)=>{
                if(f.id ===p.id){
                    return {...f, 
                        sale_qty : f.sale_qty - sale_qty,
                         amount:  f.amount - f.qty_sale_price,
                         final_amount: f.final_amount - f.qty_sale_price,
                        }
                }
                return {
                    ...f
                }               
            })
            this.saleItems=saleItems;
            this.calTotal();
        },
        async setCodeOnPaste(){
            const query_url = query(collection(db, "products"), where("code_no", "==", this.code_no));
           const unsub= onSnapshot(query_url, async(snapShot) =>{
                    const products=pd.products(snapShot)     
                    this.setCode(products[0])
                    this.code_no="";
                    unsub();
            });              
       } ,
      /*  setCodeOnPaste(){
            setTimeout(()=>{                
            const item=this.products.filter((f)=>f.code_no==this.code_no);
            if(item.length){
                this.setCode(item[0])
                this.code_no="";
            }
            },200)
        },*/
        async fetchSale(){
            const query_url = query(collection(db, "sales"), where("voucher_no", "==",this.voucher_no));
            onSnapshot(query_url, async(snapShot) =>{
                    const sales=ss.sales(snapShot)   
                    if(sales.length<=0) return false;
                   this.setOldCode(sales[0])  
            });              
       } ,
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        onlyString(p){
           if(p.largest_unit){
            return {
                largest_unit : p.largest_unit,
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
           }
           if(!p.largest_unit){
            return {
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
           }
        },
        clearDiscount(){
            this.editItem.dis_amount=0;
            this.editItem.dis_qty=0;
            this.editItem.dis_percent=0;
            this.editItem.dis_qty_unit="";
            const e=this.editItem;
            e.discount=0;           
        },
        
        saveDiscount(){
            const e=this.editItem;
            return this.saleItems.map((f)=>{  
                if(f.id===e.id){
                    f.dis_qty=e.dis_qty ? e.dis_qty : null;
                    f.dis_qty_unit=e.dis_qty_unit ? e.dis_qty_unit : null;
                    f.dis_amount=e.dis_amount ? e.dis_amount : null;
                    f.dis_percent=e.dis_percent ? e.dis_percent :null;
                    f.discount=e.discount;
                    f.final_amount =f.amount - e.discount;
                }
            }) && this.calTotal();
        },
       
        setEdit(item){
            this.editItem={...item}
        },
        calEdit(p){
            if(p.sale_qty <1){
                p.sale_qty=1;
            }
            if(p.sale_qty >= p.qty){
                p.sale_qty=p.qty;
                this.msg.info="Maximum Inventary Balance"
                this.msg.type="error"
            }
            
            let sale_qty=p.sale_qty;         
            let saleItems= this.saleItems.map((f)=>{
                    if(f.id ===p.id){
                        return {...f, 
                            sale_qty : sale_qty,
                            amount:  f.sale_qty * f.qty_sale_price,
                            final_amount:f.discount ? (f.sale_qty *  f.qty_sale_price) - f.discount : f.sale_qty * f.qty_sale_price,
                            }
                    }
                    return {
                        ...f
                    }               
                })
            this.saleItems=saleItems;
            this.calTotal();
        },
        
        calTotal(){
            let total_amount=0;
            
            this.saleItems.map((m)=>{
               if(!m.is_remove){
                total_amount += m.final_amount ? m.final_amount : 0;
               }
            })

            this.total_amount=total_amount;
            this.net_amount=this.extra_discount ? this.total_amount - this.extra_discount : this.total_amount;
            this.credit=this.paid ? Math.abs(this.net_amount) - this.paid :  this.net_amount;
            this.calPaid();
        },
        setInput(e){
           if(e.target.value==0){
                e.target.value=""
           }
           if(e.target.value==""){
                e.target.value=0;
           }
        },
        async print () {
        // Pass the element id here
         await this.$htmlToPaper('printMe');
        },
        printSale(){         
           this.print();      
        },
        
        changeCustomer(){
           this.setCustomer(this.default_customer)
        },
        setCustomer(p){
            this.customer={}
            this.customer=p;
            /*
            this.customer.id=p.id;
            this.customer.name=p.name;
            this.customer.code=p.code
            this.customer.phone=p.phone
            this.customer.address=p.address
            this.customer.created_at=p.created_at
            */
        },
        setOldCode(s){
         //   this.voucher_no=s.voucher_no,
            this.id=s.id;
            this.sale_date=hp.setOldDate(s.sale_date)
            this.print_date=s.sale_date
            this.saleItems=s.products.map((p)=>{
                return {...p, old_sale_qty: p.sale_qty}
            });
            this.total_amount=s.total_amount;
            this.net_amount=s.net_amount;
            this.change=s.change;
            this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.credit=s.credit
            this.remark=s.remark
            this.customer=s.customer;
          
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "sales"), where("voucher_no", "==",this.voucher_no));           
               let unsub= onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="modified"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    //this.updateProducts();
                                  //  this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message("modified", "sale")   
                                    this.isLoading=false;
                                   // this.clearSaleItem(); 
                                   unsub();
                                }
                            }
                            if(change.type==="removed"){
                                this.msg.type="success";
                                this.msg.info="The voucher has been cancelled." 
                                this.isLoading=false;
                                this.clearSaleItem();
                                setTimeout(()=>{
                                    this.goSale();
                                },2000)
                                unsub();
                            }
                    });                                              
            });                              
        } ,
        async saveSale(){                  
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
          
            if(this.customer.code==123456789 && this.credit > 0){
                this.msg.info="Cannot sale with credit for default customer"
                this.msg.type="error"
                return false
            }
            if(this.customer.code == 123456789){
                if(this.customer.name != "Default Customer"){
                    this.msg.info="Please select customer name again"
                    this.msg.type="error"
                    return false
                }
            }
            if(!this.customer.name || !this.customer.code){
                    this.msg.info="Please select customer name again"
                    this.msg.type="error"
                    return false                
            }
            const isNotReady= this.saleItems.filter((f)=>!f.final_amount)
            if(isNotReady.length) {
                this.msg.info="Please check  item amount"
                this.msg.type="error"
               return false;
            }      

              this.isLoading=true;
                this.msg.type="";
                this.msg.info="";

                this.updateProducts();
                this.updateStoreItem();
                
                const sale_date=hp.insertDate(this.sale_date)
                const voucher={
                    voucher_no: this.voucher_no,
                   sale_date: Timestamp.fromDate(sale_date),
                    products : this.saleItems,
                  //  user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                    extra_discount:this.extra_discount ? this.extra_discount : 0,
                    net_amount: this.net_amount ,
                    paid: this.paid ? this.paid : 0,
                    change:this.change,
                    sale_type:this.sale_type,
                    customer : this.customer,
                    credit: this.credit,
                    remark: this.remark,
                    payment_type: this.credit > 0 ? "Credit" : "Paid"
                }                
                this.checkDatabase();           
                const saveDocument=doc(db, "sales", this.id)
                if(this.saleItems.length<=0){
                    await deleteDoc(saveDocument) 
                }else{
                    await updateDoc(saveDocument, voucher)           
                    this.isLoading=false;
                }                
        },
        goSale(){
            const isMobile = window.matchMedia("(max-width: 1199px)").matches;
            if(isMobile){
                this.$router.push({name: "MobileSale"});
            }else{
                this.$router.push({name: "Sale"});
            }
        },
      
        updateStoreItem(){
            const updateStore=this.saleItems.filter((f)=>{
                if(!f.is_remove){
                    return f;
                }
            })
            this.saleItems=updateStore;
        },
        updateProducts(){
            if(this.saleItems.length > 0){ //update products qty increase or decrease
                    this.saleItems.map(async (p)=>{
                    const id=p.id;
                    const sale_qty=p.sale_qty;     
                    const old_sale_qty=p.old_sale_qty;          
                    const updateDocument=doc(db, "products", id)
                        
                    if(p.is_remove){
                        await updateDoc(updateDocument, {                      
                            qty : increment(old_sale_qty)
                        }) 
                    }else{
                        await updateDoc(updateDocument, {                      
                        qty : sale_qty < old_sale_qty ? increment(old_sale_qty - sale_qty) : increment(- (sale_qty - old_sale_qty)),
                     }) 
                    }                                     
                })               
            }            
        },
        clearSaleItem(){
                this.editItem={},
                this.saleItems=[],
                this.store_item={},
                this.total_amount=0,
                this.extra_discount=0,
                this.net_amount=0;
                this.paid=0,
                this.change=0
                this.credit=0
                this.voucher_no=new Date().getTime();
                this.sale_date=hp.setToday();      
                this.print_date=hp.setToday();  
                this.setCustomer(this.default_customer)
                this.isLoading=false;
                this.v$.$reset()

        },
      
        removeSaleItems(s){
            this.saleItems=this.saleItems.map((f)=>{
                if(f.id==s.id){
                    return {...f, is_remove: true}
                }else{
                    return f;
                }
            })
            this.calTotal()
        },
     
        checkQty(p){           
            if(p.qty  <1 + p.sale_qty ){
                this.msg.type="error";
                this.msg.info=`There is a maximum balance of "${p.item_name}"`
                return false;
            }          
            return true;
        },
    
     
        setCode(p){
           if(p.qty <=0){
                this.msg.info= `"${p.item_name}" is out of stock`
                this.msg.type="error";
                return false;
           }
           this.store_item ={
                ...p,           
                sale_type: this.sale_type,         
                sale_qty: 1,
                qty_sale_price_unit:p.packing_size.smallest_unit,
                qty_sale_price: p.retail_price.smallest_unit,
                amount: p.retail_price.smallest_unit,
                final_amount: p.retail_price.smallest_unit,
                old_sale_qty: 0
           }
           const s=this.saleItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }else{
                this.updateStoringItem();
            }      
           
        },
        autoScroll(){            
            const container = document.querySelector(".dataContainer");
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth" // Enables smooth scrolling
            });
        
      },
        storingItem(){          
                this.saleItems.push(this.store_item)
                this.calTotal();        
                setTimeout(()=>{
                        this.autoScroll();
                }, 100) 
        },
        updateStoringItem(){
          this.saleItems= this.saleItems.map((f)=>{
                if(f.id ===this.store_item.id){
                    return {...f, 
                        sale_qty : f.sale_qty + 1,
                        amount: f.amount + f.qty_sale_price,
                        final_amount: f.final_amount + f.qty_sale_price,
                        }
                }else{
                    return {...f}
                }
            })
            this.calTotal();
        },
        calPaid(){
            this.change=this.paid - this.net_amount > 0 ? this.paid - this.net_amount : 0;
            this.credit=this.paid - this.net_amount < 0 ? Math.abs(this.paid - this.net_amount) : 0;
        },
       
         editShowTime(t){
            const date=t.toDate()
            //const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },
      
    },
    
}
</script>
<style lang="">
 
</style>