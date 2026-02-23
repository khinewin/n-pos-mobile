<template>
    <div>           
        <PageHeader :page-title="pageTitle" page-code="addAdjustment" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Modern Sticky Toolbar -->
            <div class="mb-2 sticky-top z-3 bg-white rounded-3" style="top: 0;">
                <div class="d-flex justify-content-end align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                    <div class="d-flex gap-2">
                        <button type="button" @click="saveAdjustment" class="btn btn-light text-primary shadow-sm px-4 fw-bold border-0" :disabled="isLoading || storeItems.length <= 0">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive me-2"></i> {{t('save_link')}}</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                    </div>

                    <div class="d-flex gap-2">
                        <router-link :to="{name : 'Adjustments'}" class="btn btn-light shadow-sm text-danger fw-medium border-0 px-3 close">
                            <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                        </router-link>
                    </div>
                </div>
            </div>

            <form @submit.prevent="saveAdjustment">
                <!-- Integrated Header Card -->
                <div class="mb-2">
                    <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                        <div class="card-body p-2">
                            <div class="row g-4 align-items-end">
                                <!-- Invoice No -->
                                <div class="col-md-3">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('invoice_no')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                        <input type="text" :class="{'border-danger' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" placeholder="Voucher no">
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

                                <!-- Location -->
                                <div class="col-md-2">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('location')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-location-dot text-secondary"></i></span>
                                        <select v-model="location" class="form-select bg-light border-0 fw-medium text-dark">
                                            <option value="Mart">Mart</option>
                                            <option value="Main">Main</option>
                                        </select>
                                    </div>
                                </div>

                                <!-- Remarks/Notes -->
                                <div class="col-md-4">
                                    <label class="small text-muted text-uppercase fw-bold mb-1 d-block">{{t('notes')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0"><i class="fa-solid fa-comment-dots text-secondary"></i></span>
                                        <input type="text" v-model="remark" class="form-control bg-light border-0 fw-medium text-dark" placeholder="Adjustment reason...">
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
                                <th class="py-2 border-0" style="width: 50px;"></th>
                                <th class="py-2 border-0 text-start" style="width: 150px;">{{t('barcode')}}</th>
                                <th class="py-2 text-start border-0" style="min-width: 250px;">
                                    <i class="fa-solid fa-circle-plus me-1 text-primary"></i> {{t('item_name')}}
                                </th>
                                <th class="py-2 border-0">{{t('before_adj')}}</th>
                                <th class="py-2 border-0" style="width: 150px;">{{t('adj_type')}}</th>
                                <th class="py-2 border-0 table-primary-subtle" style="width: 150px;">{{t('qty')}}</th>
                                <th class="py-2 border-0">{{t('after_adj')}}</th>
                                <th class="py-2 border-0" style="width: 100px;">{{t('rts_rfs')}}</th>
                                <th class="py-2 border-0 text-start" style="min-width: 200px;">{{t('notes')}}</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- Empty State -->
                            <tr v-if="storeItems.length === 0" class="text-center text-muted cursor-pointer" data-bs-toggle="modal" data-bs-target="#searchCode">
                                <td colspan="9" class="py-5">
                                    <i class="fa-solid fa-screwdriver-wrench fa-3x mb-3 opacity-25 text-primary"></i>
                                    <p class="mb-0 fw-medium">Click here to add items for adjustment</p>
                                </td>
                            </tr>

                            <!-- Product Rows -->
                            <tr v-else v-for="(st, i) in storeItems" :key="st.id" @mouseenter="setEdit(i)" class="small">
                                <td class="text-center border-0 px-1">
                                    <a class="text-danger opacity-75 hover-opacity-100" href="#!" @click.stop="removeStoreItem(i)">
                                        <i class="fa-solid fa-circle-minus fs-5"></i>
                                    </a>
                                </td>
                                <td class="border-0 font-monospace">{{st.code_no}}</td>
                                <td class="fw-bold border-0">{{st.item_name}}</td>
                                <td class="text-center border-0 fw-medium">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.before_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                </td>
                                <td class="border-0">
                                    <select v-model="st.adj_type" class="form-select form-select-sm border-0 bg-light-subtle fw-medium text-center" @change="setAdjQty">
                                        <option value="Increment">Increment</option>
                                        <option value="Decrement">Decrement</option>
                                    </select>
                                </td>
                                <td class="text-center table-primary-subtle border-0 fw-bold change-cursor" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                    <i class="fa-solid fa-pen-to-square me-2 opacity-50 small"></i>
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                </td>
                                <td class="text-center border-0 fw-bold text-primary">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}
                                </td>
                                <td class="border-0">
                                    <select class="form-select form-select-sm border-0 bg-light-subtle fw-medium text-center" v-model="st.rts_rfs">
                                        <option value="Yes">Yes</option>
                                        <option value="No">No</option>
                                    </select>
                                </td>
                                <td class="border-0 p-1">
                                    <input type="text" v-model="st.remark" class="form-control form-control-sm border-0 bg-light-subtle" placeholder="Item specific note">
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </form>
        </div>

        <!-- Modals -->
        <ProductModal @setCode="setCode" :storeItems="storeItems" />
        <setQtyModal @setQty="setAdjQty" :ei="editItem" />
    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import ProductModalOld from '@/views/modals/purchase/ProductModalOld.vue'
import * as hp from "@/lib/helper"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setQtyModalOld from "@/views/modals/setQtyModalOld.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"
import {lang} from "@/lib/language"

export default {
    components: {
        PageHeader, 
        ProductModal: store.getters.theme === "new" ? ProductModal : ProductModalOld,
        setQtyModal: store.getters.theme === "new" ? setQtyModal : setQtyModalOld,
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
            location: "Mart",
            adj_item: {},
            storeItems: [],
            editItem: {},         
            remark:"",
           
            isLoading:false,
            isSaving:false,
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
            let  pageTitle=sl=="mm" ?  `${lang.adjustment.mm} / ${lang.new.mm}` : `${lang.adjustment.eng} / ${lang.new.eng}`            
            return pageTitle;
        },      
         t() {
            return (key) => this.$store.getters.t(key);
        },
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
    mounted() {           
        this.voucher_no=hp.setVoucherNo();
        this.created_at=hp.setToday();
        window.addEventListener('keydown', this.handleCtrl);
    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handleCtrl);
     },
    
    methods:{
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveAdjustment(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const des=document.querySelector(".close")
                if (des) des.click();
            }                 
            return hc.handleCtrl(event)
        },
        setAdjQty(){
            const e=this.editItem;
            if(this.location=="Mart" && e.qty <=0 && e.adj_type=="Decrement"){
                    this.msg.info="Mart inventory balance is not enough to decrement"
                    this.msg.type="error"
                    return false;
            }
            if(this.location=="Main" && e.main_qty <=0 && e.adj_type=="Decrement"){
                     this.msg.info="Main inventory balance is not enough to decrement"
                    this.msg.type="error"
                    return false;
            }

                let adj_qty=0;              
                adj_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                adj_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                adj_qty += e.input_s_qty ? e.input_s_qty : 0;
                e.adj_qty=adj_qty;
               return this.storeItems.map((f)=>{   
                    if(f.id===e.id){
                        f.adj_qty=adj_qty,
                        f.input_l_qty =e.input_l_qty ? e.input_l_qty : 0,
                        f.input_m_qty= e.input_m_qty ? e.input_m_qty : 0,
                        f.input_s_qty= e.input_s_qty  ? e.input_s_qty  : 0

                        e.after_adj_qty=f.adj_type=="Increment" ? e.before_adj_qty + e.adj_qty : e.before_adj_qty - e.adj_qty;

                        f.after_adj_qty=e.after_adj_qty;
                    }
               }) 
        },
        setEdit(i){
            const item=this.storeItems[i];
            if (!item) return;
            this.editItem={...item,               
                input_l_qty: item.input_l_qty ? item.input_l_qty : 0,
                input_m_qty: item.input_m_qty ? item.input_m_qty : 0,
                input_s_qty: item.input_s_qty  ? item.input_s_qty  : 0
            };
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
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.adj_item)   
         },
        setCode(p){           
            this.adj_item={...p, 
                before_adj_qty: this.location=='Main' ? p.main_qty : p.qty, 
                adj_qty:0, 
                after_adj_qty : 0, 
                rts_rfs: "No" , 
                adj_type: "Decrement",
                input_l_qty: 0,
                input_m_qty: 0,
                input_s_qty: 0
            };
            const s=this.storeItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }
            this.editItem=p;
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "adjustments"), where("voucher_no", "==", this.voucher_no));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {    
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                    this.msg.type="success";
                                    this.msg.info=hp.message(change.type, "adjustment") 
                                    this.resetAdjustments(); 
                                }
                            }
                    });                                              
            });                              
        } ,
       async saveAdjustment(){
          const validation = await this.v$.$validate()
          if(!validation) return false;
        
            this.msg.type="";
            this.msg.info="";
            const created_at=hp.insertDate(this.created_at)

            const emptyQty=this.storeItems.filter((f)=>f.adj_qty < 1)
            if(emptyQty.length){
                this.msg.info="Please check adjustment quantity"
                this.msg.type="error"
                return false;
            }
            this.isLoading=true;
            this.isSaving=true;
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(created_at),    
                location: this.location,       
                products : this.storeItems,
                user : {uid: this.user.uid, displayName: this.user.displayName},          
                remark: this.remark,    
            }      
            this.checkDatabase();
            const saveDocument=collection(db, "adjustments")
            await addDoc(saveDocument, voucher)                                
        },   
       resetAdjustments(){
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=hp.setVoucherNo();
            this.created_at=hp.setToday();
            this.storeItems=[]
            this.adj_item=null
            this.editItem={}           
            this.remark="";
            this.v$.$reset()
       }, 
       updateProducts(){
        this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                if(this.location==="Main"){
                    await updateDoc(updateDocument, {
                        main_qty : p.adj_type=="Decrement" ? increment(- p.adj_qty) : increment(p.adj_qty) ,                       
                 }) 
                }else{
                    await updateDoc(updateDocument, {
                        qty : p.adj_type=="Decrement" ? increment(- p.adj_qty) : increment(p.adj_qty) ,                       
                 }) 
                } 
            })
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
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
.change-cursor {
    cursor: pointer;
}
.hover-opacity-100:hover {
    opacity: 1 !important;
}
</style>

