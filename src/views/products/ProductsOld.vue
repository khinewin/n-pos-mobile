<template lang="">       
 <div>           
        <PageHeader :page-title="pageTitle" page-code="medicineCode" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid">
            <div class="row  justify-content-end">   
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <div class="form-check form-switch  d-flex justify-content-center">
                        <!--<input class="form-check-input" @change="setCamera" v-model="useCamera" type="checkbox" role="switch" id="useCamera">-->
                        <!--<label class="form-check-label ms-1 change-cursor" for="useCamera" @click="setCamera" :class="[useCamera ? 'text-success' : 'text-muted']"> <i class="fa-solid fa-camera"></i></label>-->
                      </div>
                </div>
                 <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#categoryManagementModal"><i class="fa-solid fa-code-branch"></i> {{t('categories')}}</button>
                    <!--<router-link :to="{name: 'Category'}" class="btn btn-sm"><i class="fa-solid fa-code-branch"></i> အမျိုးအစားများ</router-link>-->
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <select class="form-control-plaintext text-center py-0" v-model="limitData" @change="changeLimitData">
                            <option>50</option>
                            <option>100</option>
                            <option>150</option>
                            <option>200</option>
                            <option>250</option>
                            <option>300</option>
                        </select>
                </div> 
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm">{{t('display_link')}} {{products.length}} of {{productCount}}</button>
                </div> 
                <!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveEdit">
                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> {{update_link}} </span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                    </button>
                </div> -->
                <!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button"  class="btn btn-sm text-danger" :disabled="isLoading" @click="deleteMultiple">
                        <span v-if="!isDeleting"><i class="fa-solid fa-trash"></i> Delete (ctrl+d)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isDeleting"></span>
                    </button>
                </div>-->      
                                            
                <!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCode">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> {{save_link}}</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                    </button>
                </div> -->
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button data-bs-toggle="modal" data-bs-target="#packingSizeModal" class="btn btn-sm packing-unit"><i class="fa-solid fa-boxes-packing"></i> {{t('packing_units')}}</button>
                </div>   
            </div>
        </div>
        <div class="container-fluid my-2">
            <div class="row g-1">
                <div class="col-lg-3">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row g-2">
                                <div class="form-floating col-sm-3 col-lg-12" @click="initScanner(status='search')">
                                    <input type="search" id="search_by_code"  v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                    <label for="search_by_code" class="small">{{t('barcode')}}</label>
                                </div>
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_description" v-model="search_by_description" @keypress.enter="searchProducts" placeholder="Description" class="form-control-plaintext border-bottom">
                                    <label for="search_by_description" class="small">{{t('item_name')}}</label>
                                </div>
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <select id="search_by_category" class="form-control-plaintext border-bottom" v-model="search_by_category">
                                        <option value=""></option>
                                        <option v-for="c in cats" :key="c.id" :value="c.cat_name">{{c.cat_name}}</option>
                                    </select>
                                    <label for="search_by_category" class="small">{{t('category')}}</label>
                                </div>
                            
                                    <div class="col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="searchProducts"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> {{t('search_link')}}</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateProducts"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button></div>
                             </div> 
                        </div>
                    </div>     
                  
                                                                                                                
                </div>                        
                <div class="col-md-7 col-lg-5">
                    <div class="card shadow vh-75 overflow-y-auto">
                        <div class="card-body p-2">
                            <div>

                                <table class="table table-bordered mb-0">
                                    <tbody>
                                        <tr class="table-secondary position-sticky top-0">
                                            <td class="position-relative col-0">
                                                <!--<div class="form-check form-switch">
                                                    <input @click="checkAll"  v-model="is_check_all" class="form-check-input " type="checkbox" role="switch" >
                                                  </div>
                                                <input class="position-absolute start-50 top-50 translate-middle" type="checkbox" @click="checkAll" v-model="is_deleted">-->
                                            </td>
                                            <!--<td class="p-0 col-3" @click="initScanner(status='new')">
                                                <input placeholder="Code" :readonly="useCamera"  type="text" v-model="code_no" class="form-control py-1  text-center">
                                            </td>-->
                                            <td class="p-0 col-12">
                                                <input  id="item_name" :placeholder="t('item_name')" @keypress.enter="saveCode"   type="text" v-model="item_name" class="form-control py-1">                                
                                            </td>  
                                            <td>
                                                <button class="btn btn-sm text-primary" @click="saveCode">
                                                    <span v-if="!isSaving"><i class="fa-solid fa-check"></i> </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                                                </button>
                                            </td>                                   
                                        </tr>    
                                    </tbody>
                                    <tbody> 
                                        <!--<tr class="text-center table-secondary" style="cursor: context-menu;">
                                            <td class="co-0">
                                                <input  type="checkbox" @click="checkAll" v-model="is_check_all" class="form-check-input">
                                            </td>
                                            <td class="col-3">Code</td>
                                            <td class="col-9">Product Name</td>
                                        </tr>-->                                                                    
                                        
                                                                
                                    <tr class="text-center"  v-if="products.length > 0"  v-for="(p, i) in products" :key="p.id"  :class="{'table-secondary' : editItem?.id === p.id}">   
                                        <td class="">
                                            <!--<div class="form-check form-switch">
                                                <input class=" form-check-input" type="checkbox" v-model="p.is_deleted">
                                            </div>-->
                                            <button :disabled="p.user.uid != user.uid" class="btn btn-sm text-danger d-block" @click="setProductToDelete(p)"><i class="fa-solid fa-trash"></i></button>
                                        </td>                                 
                                        <!--<td :class="{'table-secondary': sort_item ==='code_no'}" class=" p-0" @click="initScanner(status='edit')" >
                                            <span>
                                                <input v-if="editItem.id==p.id"   :readonly="useCamera"  @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1 text-center " v-model="p.code_no">
                                                <div v-else class="py-1" @click="setEdit(p)">{{p.code_no}}</div>
                                            </span>
                                        </td>-->
                                        <td :class="{'table-secondary': sort_item ==='item_name'}" class="p-0">
                                            <input v-if="editItem.id==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1" v-model="p.item_name">
                                            <div v-else class="text-start py-1 px-1" @click="setEdit(p)">{{p.item_name}}</div>
                                        </td>         
                                        <td>
                                            <button class="btn btn-sm text-primary" @click="saveEdit" v-if="editItem.id==p.id">
                                                <span v-if="!isUpdating"><i class="fa-solid fa-check"></i> </span>
                                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating && editItem.id==p.id"></span>
                                            </button>
                                        </td>                              
                                    </tr>
                                </tbody>
                            </table>

                            <div v-if="isLoading">
                                <div colspan="5">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                    </div>
                                </div>
                            </div>   
                        </div>
                     </div>                            
                    </div>
                </div>
                <div class="col-md-5 col-lg-4">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <table class="table table-bordered mb-0 table-hover" v-if="editItem && Object.keys(editItem).length >0 && editItem.packing_size" >
                                <tbody>
                                    <tr class="">
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 4 : 5]">
                                            <div class="text-center fw-bold h6 pt-2">
                                                    {{editItem.item_name}}
                                            </div>
                                        </td>
                                    </tr>
                                      <tr class="change-cursor table-info" data-bs-toggle="modal" data-bs-target="#categoryModal">
                                        <td class="align-middle"> {{t('category')}}</td>                                       
                                        <td class="text-center" :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]">
                                               <span v-if="editItem.category"> {{editItem.category.cat_name}}</span>
                                        </td>
                                    </tr>  
                                    <tr @click="initScanner(status='edit')">
                                        <td class="col-1 align-middle">{{t('barcode')}}</td>
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]" class="text-center col-3">
                                            <div class="d-flex">
                                                <input   :readonly="useCamera"   @keypress.enter="updateCode" type="text" class="form-control-plaintext py-0 text-center " v-model="editItem.code_no">
                                                <button class="btn btn-sm text-primary" @click="updateCode">
                                                    <span v-if="!isUpdatingCode"><i class="fa-solid fa-check"></i> </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdatingCode"></span>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="table-primary packing-size change-cursor"  data-bs-toggle="modal" data-bs-target="#selectPackingSizeModal">
                                        <td class="col-1 align-middle">{{t('packing_size')}}</td>
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]" class="text-center col-3">
                                            <div v-if="editItem.packing_size.smallest_unit && editItem.packing_size.medium_unit">
                                                    {{calPackingSize(editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit, editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty)}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="change-cursor table-warning" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        <td class="align-middle">{{t('inv_bal')}}</td>                                       
                                        <td class="text-center" :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]">
                                            <div  v-if="editItem.packing_size">
                                                {{calPackingQty(editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty, editItem.qty, editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit)}}                                            
                                            </div>
                                        </td>
                                    </tr>    
                                   
                                    <tr class="fw-bold" v-if="editItem.packing_size && Object.keys(editItem.packing_size).length > 0">
                                        <td></td>
                                        <td class="text-center " v-if="editItem.packing_size.largest_unit !=null">
                                            <span >{{editItem.packing_size.largest_unit}}</span>
                                        </td>
                                        <td class="text-center " v-if="editItem.packing_size.medium_unit !=null">
                                            <span >{{editItem.packing_size.medium_unit}}</span>
                                        </td>                                        
                                        <td class="text-center"  v-if="editItem.packing_size.smallest_unit !=null">
                                            <span>{{editItem.packing_size.smallest_unit}}</span>
                                        </td>
                                    </tr>                        
                                    <tr class="" v-if="editItem.packing_size && Object.keys(editItem.packing_size).length > 0">
                                        <td class="">{{t('pur_price')}}</td>
                                        <td class="text-center" v-if="editItem.packing_size.largest_unit !=null">
                                            <span >
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calPurPrice('largest_unit')" type="number" v-model="editItem.pur_price.largest_unit" class="form-control-plaintext p-0 text-center">
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <span v-if="editItem.packing_size.medium_unit !=null">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calPurPrice('medium_unit')"  type="number" v-model="editItem.pur_price.medium_unit" class="form-control-plaintext p-0 text-center">
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <span v-if="editItem.packing_size.smallest_unit !=null">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calPurPrice('smallest_unit')" type="number" v-model="editItem.pur_price.smallest_unit" class="form-control-plaintext p-0 text-center">
                                            </span>
                                        </td>                                        
                                    </tr>
                                    <tr class="" v-if="editItem.packing_size && Object.keys(editItem.packing_size).length > 0">
                                        <td class="">{{t('sale_price')}}</td>
                                        <td class="text-center" v-if="editItem.packing_size.largest_unit !=null">
                                            <span >
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calRetailPrice('largest_unit')" type="number" v-model="editItem.retail_price.largest_unit" class="form-control-plaintext p-0 text-center">
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <span v-if="editItem.packing_size.medium_unit !=null">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calRetailPrice('medium_unit')"  type="number" v-model="editItem.retail_price.medium_unit" class="form-control-plaintext p-0 text-center">
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <span v-if="editItem.packing_size.smallest_unit !=null">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calRetailPrice('smallest_unit')" type="number" v-model="editItem.retail_price.smallest_unit" class="form-control-plaintext p-0 text-center">
                                            </span>
                                        </td>                                        
                                    </tr>
                                                                        
                                </tbody>
                                <tfoot v-if="editItem.packing_size && Object.keys(editItem.packing_size).length > 0">
                                    <tr>
                                        <td class="small">
                                            <div class="form-check form-switch">
                                                <input  v-model="editItem.auto_cal" class="form-check-input auto-cal" type="checkbox" role="switch" id="flexSwitchCheckChecked" checked>
                                                <label class="form-check-label" for="flexSwitchCheckChecked">{{t('auto')}} </label>
                                              </div>                                              
                                        </td>
                                        <td colspan="3" class="p-0 text-end">
                                            <button class="btn btn-sm" @click="savePrice">                     
                                            <span v-if="!isSavePrice"><i class="fa-solid fa-hard-drive"></i> {{save_price}}</span>
                                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSavePrice"></span>
                                             </button>
                                        </td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <CategoryManagementModal @setMsg="setMsg"/>
        <CategoryModal @setCode="setCategory" />
        <SetQty :ei="editItem || {} " @saveQty="saveQty" />
        <PackingSizeModal @setMsg="setMsg" :packingSizes="packingSizes" />
        <SelectPackingSizeModal @setPackingSize="setPackingSize" :ei="editItem" :packingSizes="packingSizes" />
          <!-- Delete Confirmation Modal -->
          <div class="modal fade" id="deleteProductModal" tabindex="-1" data-bs-backdrop="static" data-bs-keyboard="false" aria-hidden="true" ref="deleteProductModal">
              <div class="modal-dialog modal-dialog-centered">
                  <div class="modal-content border-0 shadow">
                      <div class="modal-header border-bottom-0 pb-0">
                          <h5 class="modal-title fw-bold text-danger">{{t('confirm_delete')}}</h5>
                          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                      </div>
                      <div class="modal-body text-center py-4">
                          <div class="mb-3 text-danger opacity-50">
                              <i class="fa-solid fa-triangle-exclamation fa-3x"></i>
                          </div>
                          <p class="mb-0 fw-medium">Are you sure you want to delete this product?</p>
                          <p class="text-muted small" v-if="productToDelete">"{{ productToDelete.item_name }}"</p>
                      </div>
                      <div class="modal-footer border-top-0 pt-0 justify-content-center">
                          <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Cancel</button>
                          <button type="button" class="btn btn-danger px-4" @click="confirmDelete">Delete</button>
                      </div>
                  </div>
              </div>
          </div>

          <!-- Barcode Modal -->
          <div class="modal fade" id="barcodeModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog position-relative">
            <div class="modal-content">   
                <div class="modal-body ">
                    <button class="btn btn-secondary btn-sm position-absolute end-0 top-0 z-3" data-bs-dismiss="modal" @click="stopScanner">Close</button>
                    <div id="reader" class="img-fluid"></div>
                </div>               
            </div>
            </div>
        </div>
</div>   
 
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp,onSnapshot, collection, addDoc, query, where, setDoc, getCountFromServer, getDocs, doc, getDoc, updateDoc, limit, orderBy, deleteDoc} from 'firebase/firestore'
import { Modal } from 'bootstrap'
import db from '@/firebase/firestore'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/products"
import PackingSizeModal from "@/views/modals/product/PackingSize.vue";
import SelectPackingSizeModal from "@/views/modals/product/SelectPackingSize.vue";
import * as hp from "@/lib/helper"
import * as cp from "@/lib/cal_price"
import SetQty from '@/views/modals/stocks/SetQty.vue'
import * as pz from "@/lib/snapshot/packingSizes"
import * as search from "@/lib/search"
import { Html5QrcodeScanner, Html5Qrcode } from "html5-qrcode";
import {lang} from "@/lib/language"
import CategoryManagementModal from "@/views/modals/product/CategoryManagement.vue"
import CategoryModal from "@/views/modals/product/Category.vue"

import * as cat from "@/lib/snapshot/category"

export default {
    components: {
            PageHeader, Footer,PackingSizeModal,SelectPackingSizeModal, SetQty, CategoryManagementModal, CategoryModal
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                item_name: {required},
            }
    },
    data(){
        return{
            code_no :"",
            item_name:"",
            products : [],
            isLoading: false,
            sortBy:"asc",
            sort_item:"",
            search_by_code:"",
            search_by_description:"",
            search_by_category:"",
            productsData:[],
            packingSizes:[],
            editItem:{},
            old_item_name:"",
            isUpdating:false,
            isUpdatingCode:false,
            isSaving:false,
            isSavePrice:false,
            isDeleting:false,
            selected_items:[],
            auto_cal:true,
            msg : {
                type :"",
                info :"",
            },
            is_deleted:false,
            is_check_all: false,
            isDeleting:false,
           // deviceId:"",
           unsubProducts:null,
           unsubPackingSizes:null,
           limitData: 100,
           useCamera: false,
           scannerActive: false,
           html5QrCode:null,
           productCount: 0,
           cats:[],
           productToDelete: null,
           deleteModalInstance: null,
        }
    },
    created() {     
      
    },
    mounted() {

        let camera=localStorage.getItem("camera");
        this.useCamera=camera=="false" ? false : false;
       
        this.isLoading=true;

        let limitData=localStorage.getItem("limitData");
        this.limitData=limitData ? limitData : 100;
     
        window.addEventListener('keydown', this.handleCtrl);
       this.fetchPackingSizes();
       this.fetchProductCount();
       this.fetchProducts();
       this.fetchCategory();
       this.deleteModalInstance = new Modal(this.$refs.deleteProductModal);
    },
    unmounted() {
        if(this.unsubProducts){
            this.unsubProducts=null
        }
        if(this.unsubPackingSizes){
            this.unsubPackingSizes=null;
        }
        if (this.deleteModalInstance) this.deleteModalInstance.hide();
    },
 
    computed:{
        user(){
            return this.$store.getters.user;
        },
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.product_code.mm}` : `${lang.product_code.eng}`            
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
                    }, 2000)
                }
            },deep:true
        }
    },

    methods:{
         async fetchCategory(){                    
                const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
               const unsub= onSnapshot(query_url, async(snapShot) =>{
                    const cats=cat.category(snapShot);   
                    this.cats=cats;         
                    unsub();    
                })
               
           } ,
       async setCategory(c){
            if(this.editItem.category &&  this.editItem.category.cat_name== c.cat_name) return false;
            this.editItem.category=c;
              const data=this.editItem
            const updateDocument=doc(db, "products", this.editItem.id)
            this.isUpdated(this.editItem.code_no);
            await updateDoc(updateDocument, data)  
            
        },
        changeLimitData(){
            localStorage.setItem("limitData", this.limitData)    
            this.fetchProducts();   
        },
        setCamera(){     
            const c=this.useCamera ? false : true;      
            localStorage.setItem("camera", c)    
            this.useCamera=c;       
           
        },
        stopScanner() {
            this.scannerActive = false;

            if (this.html5QrCode) {
                this.html5QrCode.stop().then(() => {
                    console.log("Scanner stopped.");
                }).catch((err) => {
                    console.error("Error stopping scanner:", err);
                });
            }
        },

        initScanner(status="new", camera = true) {
            if (!this.useCamera) return false;
            if (this.scannerActive) return false;

            const myModal = new bootstrap.Modal('#barcodeModal', {
                keyboard: false
            });
            myModal.show();

            const isMobile = window.matchMedia("(max-width: 1199px)").matches;
            this.scannerActive = true;

            this.html5QrCode = new Html5Qrcode("reader"); // 👈 store instance

            const qrCodeSuccessCallback = (decodedText, decodedResult) => {
                this.scannerActive = false;

                if (status=="new") {
                    this.code_no = decodedText;
                } else if(status=="edit") {
                    this.editItem.code_no = decodedText;
                    this.updateCode();
                }else if(status=="search"){
                    this.search_by_code=decodedText
                    this.searchProducts();
                }

                this.html5QrCode.stop().then(() => {
                    myModal.hide();
                });
            };

            const config = { fps: 10, qrbox: { width: 250, height: 250 } };

            const facing = isMobile ? "environment" : "user";
            this.html5QrCode.start({ facingMode: facing }, config, qrCodeSuccessCallback);
        },
     
        setMsg(info, type){
            this.msg.type=type;
            this.msg.info=info
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 'h') {
                event.preventDefault(); // Prevent the default browser save dialog
               const p=document.querySelector(".close-p-z")
               const sp=document.querySelector(".close-s-p-z")
                p.click();
                sp.click();
            }
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveCode(); // Call your custom function
            }  
            if (event.ctrlKey && event.key === 'd') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.deleteMultiple(); // Call your custom function
            }  
            if (event.ctrlKey && event.shiftKey && event.key === 'S') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.savePrice(); // Call your custom function
            }   
            if (event.ctrlKey && event.key === 'a') {
                event.preventDefault(); // Prevent the default browser save dialog
               const a=document.querySelector(".auto-cal")
               a.checked=!a.checked
            }  
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
               const p=document.querySelector(".packing-size")
               p.click();
            }    
            if (event.ctrlKey && event.key === 'u') {
                event.preventDefault(); // Prevent the default browser save dialog
               const pu=document.querySelector(".packing-unit")
               pu.click();
            }          
         
        },
        async saveQty(){
            const e=this.editItem;            
                let qty=0;
                qty += e.l_qty ? e.l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                qty += e.m_qty ? e.m_qty * e.packing_size.smallest_qty: 0;
                qty += e.s_qty ? e.s_qty : 0;                       
               e.qty=qty;
               this.isUpdated(this.editItem.code_no);
               const updateDocument=doc(db, "products", this.editItem.id)
              await updateDoc(updateDocument, this.editItem)  
        },
      
        setProductToDelete(p) {
            this.productToDelete = p;
            this.deleteModalInstance.show();
        },
        async confirmDelete() {
            if (!this.productToDelete) return false;
            this.isDeleting = true;
            const document = doc(db, "products", this.productToDelete.id);
            this.isRemoved(this.productToDelete.code_no);
            await deleteDoc(document);
            this.deleteModalInstance.hide();
            this.productToDelete = null;
        },
        checkAll(){
            setTimeout(()=>{
                this.products=this.products.map((s)=>{
                    return {...s, is_deleted : this.is_check_all ? true : false}
                })
            }, 100)
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        pasteEdit(){
            setTimeout(()=>{
                this.saveEdit();
            },300)
        },
        async updateCode(){
            if(!this.editItem.code_no) return false;
            this.isUpdatingCode=true;
            const updateDocument=doc(db, "products", this.editItem.id)
            const data={
                ...this.editItem,
                code_no: this.editItem.code_no
            }
            this.isUpdated(this.editItem.code_no);
            await updateDoc(updateDocument, data)  
        },
        async saveEdit(){               
            if(!this.editItem.item_name) return false;
            const coll = collection(db, "products");
            const q = query(coll, where("item_name_lower_case", "==", this.editItem.item_name.toLowerCase()));
            const isTaken = await getDocs(q);
            this.isUpdating=true;

            if(!isTaken.empty){
                const docRef = doc(db, "products", this.editItem.id);
                const docSnapshot = await getDoc(docRef);
                const docData = docSnapshot.data();
                const index = this.products.findIndex(product => product.id == docData.id);
                if(index !=-1){
                this.products[index].item_name=docData.item_name;
                this.editItem.item_name=docData.item_name;
                this.msg.info="This item name already exists"
                this.msg.type="error"
                this.isUpdating=false;
                return false;
                }
            }
            const data={
                ...this.editItem,
                item_name_lower_case: this.editItem.item_name.toLowerCase(),
                keywords: this.generateKeywords(this.editItem.item_name),
             }
            const updateDocument=doc(db, "products", this.editItem.id)
            this.isUpdated(this.editItem.code_no);
            await updateDoc(updateDocument, data)  
        },
        setPackingSize(p){
            this.editItem.packing_size=p;
           this.savePackingSize();
        } ,
        async savePackingSize(){       
            this.isUpdated(this.editItem.code_no);
            const updateDocument=doc(db, "products", this.editItem.id)
            await updateDoc(updateDocument, {
                    packing_size : this.editItem.packing_size,        
                    edit_user : this.editItem.edit_user          
            })  
        },
         generateKeywords(text) {
            const words = text.toLowerCase().split(" ");
            let tokens = new Set();
            words.forEach((word) => {
                // Store all substrings (3+ characters for better efficiency)
                for (let i = 0; i < word.length; i++) {
                for (let j = i + 2; j <= word.length; j++) { // At least 2-letter substrings
                    tokens.add(word.substring(i, j));
                }
                }
            });
            return Array.from(tokens);
         },
         generateKeywordsNew(text) {
        const words = text
            .toLowerCase()
            .replace(/[^\w\s]/g, " ") // Strip punctuation
            .split(/\s+/)
            .filter((word) => word.length > 0);
        
        const tokens = new Set();
        const maxTokensPerDoc = 200; // Limit to keep docs under 1MB
        
        for (const word of words) {
            const trimmedWord = word.slice(0, 15); // Cap word length at 15 chars
            // Add full word if long enough
            if (trimmedWord.length >= 3) tokens.add(trimmedWord);
            
            // Generate substrings (min 3 chars)
            for (let i = 0; i < trimmedWord.length; i++) {
            for (let j = i + 3; j <= trimmedWord.length; j++) {
                tokens.add(trimmedWord.substring(i, j));
                if (tokens.size >= maxTokensPerDoc) {
                return Array.from(tokens); // Early exit
                }
            }
            }
        }        
        return Array.from(tokens);
        },
    
      
        async saveCode(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;

            const coll = collection(db, "products");
            const q = query(coll, where("item_name_lower_case", "==", this.item_name.toLowerCase()));
            const isTaken = await getDocs(q);           
            if(!isTaken.empty){                
                this.msg.info="This item name already exists"
                this.msg.type="error"
                this.item_name="";
                return true;          
            }

            this.isSaving=true;
            const save_time=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "products", )
            this.code_no=this.code_no ? String(this.code_no) : String(new Date().getTime())
            const data={
                user: {uid:this.user.uid, displayName:this.user.displayName},
                item_name: this.item_name, 
                item_name_lower_case:this.item_name.toLowerCase(),
                keywords: this.generateKeywords(this.item_name),
                code_no : this.code_no,
                qty : 0,
                retail_price: {},
                whole_sale_price: {},
                packing_size:{},
                created_at : save_time,          
                pur_price: {},
                main_qty:0,
                is_deleted:this.is_deleted,
                edit_user: {uid: this.user.uid, type: 'added'},
                auto_cal: true,
            }
            this.isCreated(this.code_no);
            await addDoc(dataCollection, data);                    
        },
        resetInput(){            
            this.item_name="";
            this.code_no="";
            this.v$.$reset()
            this.isSaving=false;
        },
       
       async savePrice(){
            this.isUpdated(this.editItem.code_no);
            const updateDocument=doc(db, "products", this.editItem.id)           
                await updateDoc(updateDocument, {
                    pur_price: this.editItem.pur_price,
                    retail_price: this.editItem.retail_price,
                    whole_sale_price: this.editItem.whole_sale_price,
                    auto_cal: this.editItem.auto_cal,
                    edit_user : this.editItem.edit_user
                 })  
        },

        calRetailPrice(unit){
            return cp.calRetailPrice(unit, this.editItem);
        },
        calWholesalePrice(unit){
            return cp.calWholesalePrice(unit,this.editItem);
        },
        calPurPrice(unit){
            return cp.calPurPrice(unit, this.editItem);
        },
       
      
        setEdit(p){  
            if (!p) {
                this.editItem = {};
                return;
            }
         
            let  l=0, m=0, s=0;

            const item=p;
            const packing_size = item.packing_size || {};
            if(packing_size.largest_unit){              
                l=Math.floor(item.qty / (packing_size.medium_qty * packing_size.smallest_qty));
                m=Math.floor((item.qty % (packing_size.medium_qty * packing_size.smallest_qty)) / packing_size.smallest_qty);
                s=Math.floor((item.qty % (packing_size.medium_qty * packing_size.smallest_qty)) % packing_size.smallest_qty);
            }else if(packing_size.smallest_qty){           
                m=Math.floor(item.qty / packing_size.smallest_qty);
                s=Math.floor(item.qty % packing_size.smallest_qty);
            }
            this.editItem=p ? {...item,
                packing_size,
                l_qty: l ? l : 0, 
                m_qty:m ? m :0,
                s_qty: s ? s :0,     
                edit_user:  {uid:this.user.uid, type: "modified"}
                } : {};
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_description="";
            this.search_by_category="";
            this.searchProducts();
        },
        onPaste(){
            setTimeout(()=>{
                this.searchProducts();
            }, 200)
        },
        inputSearchName(){
            this.search_by_description=event.target.value;
            this.searchProducts();
        },
      
        async searchProducts() {
            this.products = [];
            this.isLoading = true;
            try {
                if (this.search_by_code) {
                    const query_url = query(collection(db, "products"), where("code_no", "==", this.search_by_code));
                    const snapShot = await getDocs(query_url);
                    const products = ss.products(snapShot);
                    this.products = products;
                    if (products.length) {
                        this.setEdit(products[0]);
                    }
                } else if (this.search_by_description) {
                    const query_url = query(
                        collection(db, "products"), where("keywords", "array-contains", this.search_by_description.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const products = ss.products(snapShot);
                    this.products = products;
                    if (products.length) {
                        this.setEdit(products[0]);
                    }
                } else if (this.search_by_category) {
                    const query_url = query(
                        collection(db, "products"), where("category.cat_name","==", this.search_by_category), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const products = ss.products(snapShot);
                    this.products = products;
                    if (products.length) {
                        this.setEdit(products[0]);
                    }
                }else {
                    this.products = this.productsData;
                    if (this.products.length && (!this.editItem || Object.keys(this.editItem).length <= 0)) {
                        this.setEdit(this.products[0]);
                    }
                }
            } catch (error) {
                console.error("Error searching products:", error);
            } finally {
                this.isLoading = false;
            }
        },

        async fetchPackingSizes(){
        const query_url = query(collection(db, "packingSizes"), orderBy("created_at", "desc"));
        this.unsubPackingSizes=onSnapshot(query_url, (snapShot)=>{
                const pSIzes=pz.packingSizes(snapShot)      
                this.packingSizes=pSIzes;    
        })
       },
       async fetchProducts(){
        this.isLoading=true;
        this.products=[]
        const query_url=query(collection(db, "products"), orderBy("created_at", "desc"), limit(this.limitData));           
          this.unsubProducts=onSnapshot(query_url, async(snapShot) =>{     
                const products=ss.products(snapShot);   
                this.productsData=products;
                this.products=products;                
                this.isLoading=false;
                if((!this.editItem || Object.keys(this.editItem).length === 0) && products.length){
                    this.setEdit(this.products[0]) 
                }
            });
       },
      async isCreated(code_no){
        const query_url=query(collection(db, "products"),where("code_no", "==", code_no));           
         const unsub=onSnapshot(query_url, async(snapShot) =>{   
            snapShot.docChanges().forEach((change) => {     
                if(change.type =="added"){
                this.msg.info="The new product code has been created"
                this.msg.type="success"
                this.resetInput();    
                this.setEdit(this.products[0])
                setTimeout(()=>{
                    this.fetchProductCount();
                }, 200)     
                unsub();         
            }                  
            })
        })
      },
      async isUpdated(code_no){
        const query_url=query(collection(db, "products"),where("code_no", "==", code_no));           
         const unsub=onSnapshot(query_url, async(snapShot) =>{   
            snapShot.docChanges().forEach((change) => {   
                const res=change.doc.data();   
                const id=change.doc.id;
                this.msg.info="The selected product code has been updated"  
                this.msg.type="success"
                 this.isUpdating=false;
                 this.isUpdatingCode=false;
                this.editItem={...res, id}                     
                   
                if(change.type=="modified"){
                    unsub();  
                }               
            })
        })
      },
      async isRemoved(code_no){
        const query_url=query(collection(db, "products"),where("code_no", "==", code_no));           
         const unsub=onSnapshot(query_url, async(snapShot) =>{   
            snapShot.docChanges().forEach((change) => {   
                if(change.type =="removed"){
                this.isDeleting=false;
                this.msg.info="The selected product code has been deleted"
                this.msg.type="success"                
                setTimeout(()=>{
                    this.editItem=this.products.length ? this.products[0] : {};
                    this.fetchProductCount();
                }, 1000)
                unsub();         
            }                  
            })
        })
      },      
  
        async fetchProductCount() {
            const coll = collection(db, "products");
            const snapshot = await getCountFromServer(coll);
            this.productCount = snapshot.data().count;       
        },
      

       
    }
}
</script>
<style>
 
</style>


<!--
     delay(ms) {
        return new Promise((resolve) => setTimeout(resolve, ms));
        },

    async updateAllProducts() {
      this.msg.info = "Fetching products...";
      this.msg.type="success";
      try {
        const querySnapshot = await getDocs(collection(db, "products"));
        const docs = querySnapshot.docs;
        const batchSize = 100;

        for (let i = 0; i < docs.length; i += batchSize) {
          const batchDocs = docs.slice(i, i + batchSize);

          const updatePromises = batchDocs.map((document) => {
            const data = document.data();
            const oldItemName = data.item_name || "";

            const docRef = doc(db, "products", document.id);

            return updateDoc(docRef, {
                item_name_lower_case:oldItemName.toLowerCase(),
                code_no: String(data.code_no),
                keywords: this.generateKeywords(oldItemName),
            });
          });

          await Promise.all(updatePromises);
          this.msg.info = `✅ Updated ${i + batchDocs.length} of ${docs.length} products`;

          await this.delay(5000); // pause between batches
        }

        this.msg.info = "🎉 All products updated!";
      } catch (error) {
        console.error("❌ Error updating products:", error);
        this.msg.info = "❌ Error occurred!";
      }
    },

     async checkDatabase(code_no){
            const query_url=query(collection(db, "products"),where("code_no", "==", code_no));           
             this.unsubProducts= onSnapshot(query_url, async(snapShot) =>{                                       
                   //     const dc=snapShot._snapshot.docChanges;
                         const user=this.user;
                        //get only update doc for message
                        snapShot.docChanges().forEach((change) => {     
                            const res=change.doc.data();   
                            const id=change.doc.id;
                            const edit_user=res.edit_user !=null ? res.edit_user : "";
                            if(change.type==="added" &&   edit_user.uid==user.uid && !this.isDeleting){                               
                                    this.msg.info="The new product code has been created"
                                    this.msg.type="success"
                                    this.resetInput();    
                                    //  hp.setAutoFocus("item_name")     
                                    this.setEdit(this.products[0])
                                    setTimeout(()=>{
                                        this.fetchProductCount();
                                    }, 1000)
                            }
                            if(change.type==="modified" && edit_user.type=="modified" && edit_user.uid==user.uid){
                                    this.msg.info="The selected product code has been updated"
                                    this.msg.type="success"
                                    this.isUpdating=false;
                                  //  const items=this.products.filter((f)=>f.id==this.editItem.id)
                                    this.setEdit({...res, id})
                            }  
                            if(change.type==="removed" && this.isDeleting){
                                    this.isDeleting=false;
                                     this.msg.info="The selected product code has been deleted"
                                    this.msg.type="success"
                                    this.editItem=this.products[0];
                                    setTimeout(()=>{
                                        this.fetchProductCount();
                                    }, 1000)
                            }else if(change.type==="removed"){                                  
                                    if(change.doc.id==this.editItem.id){                                       
                                        this.editItem=this.products[0];
                                    }
                            }
                      
                        })
                   
                 });       
        },
-->