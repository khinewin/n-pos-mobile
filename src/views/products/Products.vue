<template lang="">       
 <div>           
        <PageHeader page-title="Product Code" page-code="medicineCode" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid">
            <div class="row  justify-content-end">   
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveEdit">
                        <span><i class="fa-solid fa-retweet"></i> Update </span>
                    </button>
                </div> 
                <!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button"  class="btn btn-sm text-danger" :disabled="isLoading" @click="deleteMultiple">
                        <span v-if="!isDeleting"><i class="fa-solid fa-trash"></i> Delete (ctrl+d)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isDeleting"></span>
                    </button>
                </div>-->      
                                            
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCode">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                    </button>
                </div> 
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button data-bs-toggle="modal" data-bs-target="#packingSizeModal" class="btn btn-sm packing-unit"><i class="fa-solid fa-boxes-packing"></i> Packing Units (ctrl+u)</button>
                </div>   
            </div>
        </div>
        <div class="container-fluid my-2">
            <div class="row g-1">
                <div class="col-lg-3">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row g-2">
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_description" v-model="search_by_description" @keypress.enter="searchProducts" placeholder="Description" class="form-control-plaintext border-bottom">
                                    <label for="search_by_description" class="small">Product Name</label>
                                </div>
                            
                                    <div class="col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="searchProducts"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateProducts"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
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
                                            <td class="p-0 col-3">
                                                <input placeholder="Code"  type="text" v-model="code_no" class="form-control py-1  text-center">
                                            </td>
                                            <td class="p-0 col-9">
                                                <input  id="item_name" placeholder="Product Name" @keypress.enter="saveCode"   type="text" v-model="item_name" class="form-control py-1">                                            </td>                                     
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
                                        
                                                                
                                    <tr class="text-center"  v-if="products.length > 0"  v-for="(p, i) in products" :key="p.id"  :class="{'table-secondary' : editItem.id === p.id}">   
                                        <td class="">
                                            <!--<div class="form-check form-switch">
                                                <input class=" form-check-input" type="checkbox" v-model="p.is_deleted">
                                            </div>-->
                                            <button :disabled="p.user.uid != user.uid" class="btn btn-sm text-danger d-block" @click="removeProduct(p)"><i class="fa-solid fa-trash"></i></button>
                                        </td>                                 
                                        <td :class="{'table-secondary': sort_item ==='code_no'}" class=" p-0">
                                            <span>
                                                <input v-if="editItem.id==p.id" @click="setEdit(p)" @input="setEdit(p)" @keypress.enter="saveEdit" @paste="pasteEdit" type="text" class="form-control py-1 text-center " v-model="p.code_no">
                                                <div v-else class="py-1" @click="setEdit(p)">{{p.code_no}}</div>
                                            </span>
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='item_name'}" class="p-0">
                                            <input v-if="editItem.id==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1" v-model="p.item_name">
                                            <div v-else class="text-start py-1 px-1" @click="setEdit(p)">{{p.item_name}}</div>
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
                            <table class="table table-bordered mb-0 table-hover" v-if="Object.keys(editItem).length >0" >
                                <tbody>
                                    <tr class="">
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 4 : 5]">
                                            <div class="text-center text-primary h6 pt-2">
                                                    {{editItem.item_name}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="table-primary packing-size change-cursor"  data-bs-toggle="modal" data-bs-target="#selectPackingSizeModal">
                                        <td class="col-1">Packing Size (ctrl+p) </td>
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]" class="text-center col-3">
                                            <div v-if="editItem.packing_size.smallest_unit && editItem.packing_size.medium_unit">
                                                    {{calPackingSize(editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit, editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty)}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="change-cursor table-warning" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        <td class="">Mart Inv Bal</td>                                       
                                        <td class="text-center" :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]">
                                            <div  v-if="editItem.packing_size">
                                                {{calPackingQty(editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty, editItem.qty, editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit)}}                                            
                                            </div>
                                        </td>
                                    </tr>    
                                   
                                    <tr class="fw-bold">
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
                                    <tr class="">
                                        <td class="">Pur Price</td>
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
                                    <tr class="">
                                        <td class="">Sale Price</td>
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
                                <tfoot>
                                    <tr>
                                        <td class="small">
                                            <div class="form-check form-switch">
                                                <input  v-model="editItem.auto_cal" class="form-check-input auto-cal" type="checkbox" role="switch" id="flexSwitchCheckChecked" checked>
                                                <label class="form-check-label" for="flexSwitchCheckChecked">Auto (ctrl+a)</label>
                                              </div>                                              
                                        </td>
                                        <td colspan="3" class="p-0 text-end">
                                            <button class="btn btn-sm" @click="savePrice">                     
                                            <span v-if="!isSavePrice"><i class="fa-solid fa-hard-drive"></i> Save Price (ctrl+shift+s)</span>
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
        <SetQty :ei="editItem" @saveQty="saveQty" />
        <PackingSizeModal />
        <SelectPackingSizeModal @setPackingSize="setPackingSize" :ei="editItem" />
</div>   
 
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp,onSnapshot, collection, addDoc, query, where, setDoc, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/products"
import PackingSizeModal from "@/views/modals/product/PackingSize.vue";
import SelectPackingSizeModal from "@/views/modals/product/SelectPackingSize.vue";
import * as hp from "@/lib/helper"
import * as cp from "@/lib/cal_price"
import SetQty from '@/views/modals/stocks/SetQty.vue'

export default {
    components: {
            PageHeader, Footer,PackingSizeModal,SelectPackingSizeModal, SetQty
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
            productsData:[],
            editItem:{},
            isUpdating:false,
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
        }
    },
    created() {     
      
    },
    mounted() {
        const code_no=document.getElementById("search_by_code");
      //  code_no.focus();
      //  this.isLoading=true;
       // this.fetchProducts();
       // this.checkDatabase();
       if(this.storeProducts.length){
            this.isLoading=false;
            this.productsData=this.storeProducts;
            if(this.search_by_code || this.search_by_description){
                this.searchProducts();
            }else{
                this.products=this.productsData.slice(0, 15);
                if(Object.keys(this.editItem).length ===0 && this.products.length > 0){
                     this.setEdit(this.products[0])
                }
            }
          
        }
        window.addEventListener('keydown', this.handleCtrl);
        
    },
 
    computed:{
        user(){
            return this.$store.getters.user;
        },
        storeProducts(){
            return this.$store.getters.products;
        }
    },
    watch:{
        storeProducts:{
            handler(val){
               if(val){
                    this.isLoading=false;
                    this.productsData=val;
                    if(this.search_by_code || this.search_by_description){
                        this.searchProducts();
                    }else{
                        this.products=this.productsData.slice(0, 15);
                        if(Object.keys(this.editItem).length ===0 && this.products.length > 0){
                        this.setEdit(this.products[0])
                    }
                    }
                   // this.products=this.productsData.slice(0, 15);
                   
               }
            }, deep: true
       },
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
        saveQty(){
            const e=this.editItem;
            
                let qty=0;
                qty += e.l_qty ? e.l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                qty += e.m_qty ? e.m_qty * e.packing_size.smallest_qty: 0;
                qty += e.s_qty ? e.s_qty : 0;               
         
               e.qty=qty;
               this.saveEdit();
        },
        async deleteMultiple(){
            const is_has=this.products.filter((f)=>f.is_deleted)
            if(is_has.length <=0) return false;
            let msg=""
            if(is_has.length > 1){
                msg=`"${is_has[0].item_name}" and  "${is_has.length - 1}  more"`
            }else{
                msg=`"${is_has[0].item_name}"`
            }
            const cf=confirm(`Are you sure,  you want to delete product code ${msg}`)
            if(!cf) return false;
            this.isDeleting=true;
            setTimeout(()=>{
                this.msg.info="The product code has been deleted"
                this.msg.type="success"
            }, 200)

           this.products.filter(async (s)=>{
                if(s.is_deleted){
                    this.removeProduct(s.id)
                    this.isDeleting=false;
                  /*  const updateDocument=doc(db, "products", s.id)
                    await updateDoc(updateDocument, {is_deleted: true, edit_user: {uid: this.user.uid, type: 'removed'}})  
                    */
                } 
            })
        },
        async removeProduct(p){   
            const cf=confirm(`Are you sure,  you want to delete product code ${p.item_name}`)
            if(!cf) return false;
            this.isDeleting=true;       
            const document=doc(db, "products", p.id)     
            this.msg.info="The selected product code has been deleted"
            this.msg.type="success"
            
           // setTimeout(async()=>{
             await deleteDoc(document)  
            //}, 200)    
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
        async saveEdit(){    
           /* let result=this.productsData.filter((f)=>f.id != this.editItem.id && f.item_name.toLowerCase().includes(this.editItem.item_name) )
            if(result.length > 0){
                this.msg.info="This item name already exists"
                this.msg.type="error";
                this.products=this.productsData.map((m)=>{
                    if(m.id==this.editItem.id){
                        return {...m, item_name: ""}
                    }
                    return m;
                })
                return false;
            }   */    
            if(!this.editItem.item_name) return false;
           // setTimeout(()=>{
                this.msg.info="The product code has been updated"
                this.msg.type="success"
          //  }, 200)
            const updateDocument=doc(db, "products", this.editItem.id)
            await updateDoc(updateDocument, this.editItem)  
        },
        setPackingSize(p){
            this.editItem.packing_size=p;
           this.savePackingSize();
        } ,
        async savePackingSize(){
          //  setTimeout(()=>{
                this.msg.info="The product packing size has been saved"
                this.msg.type="success"
          //  }, 200)
            const updateDocument=doc(db, "products", this.editItem.id)
                await updateDoc(updateDocument, {
                    packing_size : this.editItem.packing_size,        
                    edit_user : this.editItem.edit_user          
                 })  
        },
      
        async saveCode(){
          //  if(Object.keys(this.editItem).length) this.saveEdit();

            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            let isTaken=this.productsData.some((f)=>f.item_name.toLowerCase() == this.item_name.toLowerCase())
            if(isTaken){
                this.msg.info="This item name already exists"
                this.msg.type="error"
                this.item_name="";
                return false;
            }
            this.isSaving=true;
            const save_time=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "products", )
            this.code_no=this.code_no ? this.code_no : new Date().getTime()
            const data={
                user: {uid:this.user.uid, displayName:this.user.displayName},
                item_name: this.item_name, 
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
         //   setTimeout(()=>{
                this.isSaving=false;
                this.msg.info="The product code has been created"
                this.msg.type="success"
                this.resetInput();
           // }, 300) 
            const result=await addDoc(dataCollection, data);                    
        },
        resetInput(){            
            this.item_name="";
            this.code_no="";
            this.v$.$reset()
            this.isSaving=false;
        },
       
       async savePrice(){
            const updateDocument=doc(db, "products", this.editItem.id)
              //  setTimeout(()=>{
                    this.msg.info="The product price has been saved"
                    this.msg.type="success"
              //  }, 200)
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
            let  l=0, m=0, s=0;

            const item=p;
            if(item.packing_size.largest_unit){              
                l=Math.floor(item.qty / (item.packing_size.medium_qty * item.packing_size.smallest_qty));
                m=Math.floor((item.qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) / item.packing_size.smallest_qty);
                s=Math.floor((item.qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) % item.packing_size.smallest_qty);
            }else{           
                m=Math.floor(item.qty / item.packing_size.smallest_qty);
                s=Math.floor(item.qty % item.packing_size.smallest_qty);
            }
            this.editItem=p ? {...item,            
                l_qty: l ? l : 0, 
                m_qty:m ? m :0,
                s_qty: s ? s :0,     
                edit_user:  {uid:this.user.uid, type: "modified"}
                } : {};
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_description="";
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
        checkEdit(){
             if(this.products.length && Object.keys(this.editItem).length <=0){
                  this.setEdit(this.products[0])                    
             }         
        },
        searchProducts(){
            this.products=[];
            if(this.search_by_code){
                this.products=this.productsData.filter((f)=>f.code_no===Number(this.search_by_code))
                if(this.products.length){
                    this.setEdit(this.products[0])
                }
            }else 
            if(this.search_by_description){
                this.products=this.productsData.filter((f)=>{
                    return f.item_name.toLowerCase().includes(this.search_by_description.toLowerCase())                    
                })
                if(this.products.length){
                    this.setEdit(this.products[0])
                }
            }else{
                this.products=this.productsData.slice(0, 15);
                if(this.products.length && Object.keys(this.editItem).length <=0){
                  this.setEdit(this.products[0])                    
             }  
            }
          //  this.checkEdit();
                      
        },
        message(type){
                this.msg.info=hp.message(type, "product code")
                this.msg.type="success"                                                         
        }, 
        async checkDatabase(){ 
            const code_no=Object.keys(this.editItem).length ? this.editItem.code_no : "";
            console.log(code_no)
            const query_url=query(collection(db, "products"), where("code_no", "==",  code_no));              
                onSnapshot(query_url, async(snapShot) =>{
                    const dc=snapShot._snapshot.docChanges;
                    const user=this.user;
                    snapShot.docChanges().forEach((change) => {     
                        const res=change.doc.data();   
                            if(change.type==="added" && res.edit_user.uid==user.uid && dc.length===1){
                                    this.msg.info="The new product code has been created"
                                    this.msg.type="success"
                                    this.resetInput();    
                                  //  hp.setAutoFocus("item_name")     
                            }           
                            if(change.type==="modified" && res.edit_user.type=="modified" && res.edit_user.uid==user.uid){
                                    this.msg.info="The product code has been updated"
                                    this.msg.type="success"
                                   
                            }  
                            if(change.type==="removed"){
                                    this.msg.info="The selected product code has been deleted"
                                    this.msg.type="success"
                                    this.isDeleting=false;
                                    this.is_check_all=false;
                                    this.removeProduct(change.doc.id)
                                    //console.log(change.doc.id)
                                    //this.editItem={}
                            }    
                            this.checkEdit();
                    });                 
            });                              
        } ,
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            onSnapshot(query_url, async(snapShot) =>{
                const products=ss.products(snapShot);   
                this.productsData=products;
                this.searchProducts();
                this.isLoading=false;
                this.isUpdating=false;
            })
           
       } ,
    }
}
</script>
<style>
 
</style>

