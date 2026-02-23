<template>
    <div>           
        <PageHeader :page-title="pageTitle" page-code="addStock" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Modern Sticky Toolbar (matching purchase/Add.vue pattern) -->
            <div class="mb-2 sticky-top z-3 bg-white rounded-3" style="top: 0;">
                <div class="d-flex justify-content-end align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                    <div class="d-flex gap-2">
                        <button type="button" @click="saveStocks" class="btn btn-light text-primary shadow-sm px-4 fw-bold border-0" :disabled="isLoading || storeItems.length <= 0">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive me-2"></i> {{t('save_link')}}</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                    </div>

                    <div class="d-flex gap-2">
                        <router-link :to="{name : 'Stocks'}" class="btn btn-light shadow-sm text-danger fw-medium border-0 px-3">
                            <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                        </router-link>
                    </div>
                </div>
            </div>

            <form @submit.prevent="saveStocks">
                <!-- Integrated Header Card -->
                <div class="mb-2">
                    <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                        <div class="card-body p-2">
                            <div class="row g-3 align-items-end">
                                <!-- Voucher No -->
                                <div class="col-md-3">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('invoice_no')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                        <input type="text" :class="{'border-danger' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" placeholder="No Voucher">
                                    </div>
                                </div>
                                
                                <!-- Date -->
                                <div class="col-md-3">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('date')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-regular fa-calendar text-secondary"></i></span>
                                        <flat-pickr class="form-control bg-light border-0 fw-medium text-dark" v-model="created_at" :config="config" />
                                    </div>
                                </div>

                                <!-- Remarks -->
                                <div class="col-md-6">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('notes')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-comment-dots text-secondary"></i></span>
                                        <input type="text" v-model="remark" class="form-control bg-light border-0 fw-medium text-dark" placeholder="Add a note (optional)">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Product List Table -->
                <div class="vh-65 overflow-y-auto dataContainer bg-white rounded-3 shadow-sm border-0 mb-3" :class="{'border border-danger': v$.storeItems.$errors.length > 0}">
                    <table class="table table-hover align-middle mb-0 text-nowrap">
                        <thead class="sticky-top z-1 text-nowrap">
                            <tr class="table-primary fw-bold small text-center cursor-pointer" data-bs-toggle="modal" data-bs-target="#searchCode">
                                <th class="py-2 border-0" colspan="2" style="width: 80px;">{{t('number')}}</th>
                                <th class="py-2 text-start border-0" style="min-width: 250px;">
                                    <i class="fa-solid fa-circle-plus me-1 text-primary"></i> {{t('item_name')}}
                                </th>
                                <th class="py-2 border-0">{{t('packing_size')}}</th>
                                <th class="py-2 border-0 table-info-subtle">{{t('inv_bal')}}</th>
                                <th class="py-2 border-0 table-primary-subtle">{{t('pur_price')}}</th>
                                <th class="py-2 border-0 table-primary-subtle">{{t('sale_price')}}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Empty State -->
                            <tr v-if="storeItems.length === 0" class="text-center text-muted cursor-pointer" data-bs-toggle="modal" data-bs-target="#searchCode">
                                <td colspan="7" class="py-5">
                                    <i class="fa-solid fa-cart-plus fa-3x mb-3 opacity-25 text-primary"></i>
                                    <p class="mb-0 fw-medium">Click here  to add items</p>
                                </td>
                            </tr>
                            
                            <!-- Product Rows -->
                            <tr v-else v-for="(st, i) in storeItems" :key="st.id" @mouseenter="setEdit(i)" class="small">
                                <td class="text-center border-0 px-1">
                                    <a class="text-danger opacity-75 hover-opacity-100" href="#!" @click="removeStoreItem(i)">
                                        <i class="fa-solid fa-circle-minus fs-5"></i>
                                    </a>
                                </td>
                                <td class="text-center text-muted border-0 ps-0">{{i+1}}</td>
                                <td class="fw-bold border-0">{{st.item_name}}</td>
                                <td class="text-center border-0">
                                    <span class="badge bg-light text-primary border border-primary-subtle px-2 py-1 fw-medium">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </span>
                                </td>
                                <td class="text-end fw-bold table-info-subtle border-0 cursor-pointer text-dark" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                    <i class="fa-solid fa-warehouse me-2 opacity-50 small"></i>
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                </td>
                                <td class="text-end table-primary-subtle border-0 cursor-pointer fw-bold" data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                    <div class="d-flex flex-column align-items-end">
                                        <template v-if="st.packing_size.largest_unit">
                                            <span>{{fM(st.pur_price.largest_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.largest_unit}}</small></span>
                                        </template>
                                        <template v-else>
                                            <span>{{fM(st.pur_price.medium_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.medium_unit}}</small></span>
                                        </template>
                                    </div>
                                </td>
                                <td class="text-end table-primary-subtle border-0 cursor-pointer fw-bold text-primary" data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                    <div class="d-flex flex-column align-items-end">
                                        <template v-if="st.packing_size.largest_unit">
                                            <span>{{fM(st.retail_price.largest_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.largest_unit}}</small></span>
                                        </template>
                                        <template v-else>
                                            <span>{{fM(st.retail_price.medium_unit)}} <small class="text-muted fw-normal">/{{st.packing_size.medium_unit}}</small></span>
                                        </template>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </form>
        </div>
       
        <ProductModal @setCode="setCode" :stockItems="storeItems" />
        <SetQty :ei="editItem" @saveQty="saveQty" />
        <SetPrice :ei="editItem" @saveAutoCal="saveAutoCal" />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, limit, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import ProductModal from '@/views/modals/stocks/ProductModal.vue'
import ProductModalOld from '@/views/modals/stocks/ProductModalOld.vue'
import SetQty from '@/views/modals/stocks/SetQty.vue'
import SetQtyOld from '@/views/modals/stocks/SetQtyOld.vue'
import SetPrice from '@/views/modals/stocks/SetPrice.vue'
import SetPriceOld from '@/views/modals/stocks/SetPriceOld.vue'
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"

import * as ss from "@/lib/snapshot/products"
import * as hp from "@/lib/helper"
import * as cp from "@/lib/cal_price"
import {lang} from "@/lib/language"

export default {
    components: {
        PageHeader, 
        ProductModal: store.getters.theme === "new" ? ProductModal : ProductModalOld,
        SetQty: store.getters.theme === "new" ? SetQty : SetQtyOld,
        SetPrice: store.getters.theme === "new" ? SetPrice : SetPriceOld,
        FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                voucher_no: {required},             
                created_at: {required},              
                storeItems : {required}
                
            }
    },
    data(){
        return{      
           
            voucher_no:"", 
            created_at:"",         
            stock_item: {},
            storeItems: [],
            editItem: {},    
            remark:"",    
            isLoading:false,
            isSaving:false,
            isAddAllLoading:false,
            msg : {
                type :"",
                info :"",
            }
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
            let  pageTitle=sl=="mm" ?  `${lang.stocks.mm} / ${lang.new.mm}` : `${lang.stocks.eng} / ${lang.new.eng}`            
            return pageTitle;
        },      
        t() {
            return (key) => this.$store.getters.t(key);
        },
      
    },
  
    mounted() {         
        this.voucher_no=this.setVoucherNo();
        this.created_at=hp.setToday();
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
                    }, 3000)
                }
            },deep:true
        }
    },
    
    methods:{       
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        setVoucherNo(){
            return hp.setVoucherNo();
        },
        handleCtrl(event) {          
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveStocks(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.$router.push({name: 'Stocks'});
            }                         
            return hc.handleCtrl(event)
        },
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
        async saveAutoCal(){
        const e=this.editItem;
        this.storeItems.map((p)=>{
            if(p.id==e.id){
                p.auto_cal = e.auto_cal;
            }
        })
        },
        saveQty(){
            const e=this.editItem;
                let main_qty=0;
                main_qty += e.main_l_qty ? e.main_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                main_qty += e.main_m_qty ? e.main_m_qty * e.packing_size.smallest_qty: 0;
                main_qty += e.main_s_qty ? e.main_s_qty : 0;
                let qty=0;
                qty += e.l_qty ? e.l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                qty += e.m_qty ? e.m_qty * e.packing_size.smallest_qty: 0;
                qty += e.s_qty ? e.s_qty : 0;
               return this.storeItems.map((f)=>{   
                    if(f.id===e.id){
                        f.main_qty=main_qty,
                        f.qty=qty                 
                    }
               }) 
        },
     
        setEdit(i){
            let main_l=0, main_m=0, main_s=0, l=0, m=0, s=0;

            const item=this.storeItems[i];
            if(item.packing_size.largest_unit){
                 main_l=Math.floor(item.main_qty / (item.packing_size.medium_qty * item.packing_size.smallest_qty));
                 main_m=Math.floor((item.main_qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) / item.packing_size.smallest_qty);
                 main_s=Math.floor((item.main_qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) % item.packing_size.smallest_qty);
                 l=Math.floor(item.qty / (item.packing_size.medium_qty * item.packing_size.smallest_qty));
                 m=Math.floor((item.qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) / item.packing_size.smallest_qty);
                 s=Math.floor((item.qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) % item.packing_size.smallest_qty);
            }else{
                 main_m=Math.floor(item.main_qty / item.packing_size.smallest_qty);
                 main_s=Math.floor(item.main_qty % item.packing_size.smallest_qty);
                 m=Math.floor(item.qty / item.packing_size.smallest_qty);
                 s=Math.floor(item.qty % item.packing_size.smallest_qty);
            }
            this.editItem={...item,
                 main_l_qty :main_l ? main_l : 0,
                 main_m_qty: main_m ? main_m: 0,
                 main_s_qty: main_s ? main_s : 0,
                 l_qty: l ? l : 0, 
                 m_qty:m ? m :0,
                 s_qty: s ? s :0,     
                };
        },       
       
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.stock_item)   
            this.editItem=this.stock_item;
         },
        setCode(p){           
            this.stock_item=p;
            const s=this.storeItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }           
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "stocks"), where("voucher_no", "==", this.voucher_no));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {    
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                   // this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message(change.type, "stocks")   
                                    this.resetStocks(); 
                                }
                            }
                    });                                              
            });                              
        } ,
       async saveStocks(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.isSaving=true;
            this.msg.type="";
            this.msg.info="";
        
            const created_at=hp.insertDate(this.created_at)

            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(created_at),           
                products : this.storeItems,
                user : {uid: this.user.uid, displayName: this.user.displayName},        
                remark:this.remark,      
            }      
            this.checkDatabase();
            const saveDocument=collection(db, "stocks")
            await addDoc(saveDocument, voucher)                                                  
        },
        updateProducts(){
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                await updateDoc(updateDocument, {
                        qty : p.qty,
                        main_qty: p.main_qty,
                        retail_price: p.retail_price,
                        whole_sale_price: p.whole_sale_price,
                        pur_price: p.pur_price,
                        auto_cal: p.auto_cal,
                 })    
            })
        },
        resetStocks(){
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=this.setVoucherNo();
            this.created_at=new Date().toISOString().substr(0, 10)         
            this.storeItems=[]
            this.stock_item=null
            this.editItem={}        
            this.remark="",
            this.v$.$reset()
        },
       showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
                    
    }
}
</script>
<style scoped>
.vh-65 {
    height: 65vh;
}
.dataContainer::-webkit-scrollbar {
    width: 6px;
}
.dataContainer::-webkit-scrollbar-track {
    background: transparent;
}
.dataContainer::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 10px;
}
.dataContainer::-webkit-scrollbar-thumb:hover {
    background: #cccccc;
}
.hover-opacity-100:hover {
    opacity: 1 !important;
}
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
.table-info-subtle {
    background-color: #f0faff !important;
}
.cursor-pointer {
    cursor: pointer;
}
</style>

