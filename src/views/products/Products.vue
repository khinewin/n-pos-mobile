<template lang="">       
 <div>           
        <PageHeader page-title="Medicine code" page-code="medicineCode" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">   
                <div class="col-sm-2 d-grid">
                    <button data-bs-toggle="modal" data-bs-target="#packingSizeModal" class="btn"><i class="fa-solid fa-boxes-packing"></i> Packing size</button>
                </div>                            
                <div class="col-sm-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCode">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                    </button>
                </div>    
            </div>
        </div>
        <div class="container-fluid my-2">
            <div class="row g-1">
                <div class="col-lg-3">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row g-2">
                                <div class="form-floating col-sm-6 col-lg-12">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control form-control-sm">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                                <div class="form-floating col-sm-6 col-lg-12">
                                    <input type="search" id="search_by_description" v-model="search_by_description" @keyup="searchProducts" placeholder="Description" class="form-control form-control-sm">
                                    <label for="search_by_description" class="small">Description</label>
                                </div>
                            </div> 
                             <div class="row  mt-2">
                                    <div class="col-6 d-grid"><button class="btn btn-sm text-dark" @click="searchProducts"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateProducts"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-6 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
                             </div> 
                        </div>
                    </div>                                                                                                          
                </div>                        
                <div class="col-lg-5">
                    <div class="card shadow vh-75">
                        <div class="card-body p-2 table-responsive ">
                            <div>
                                <table class="table table-bordered mb-0">
                                    <tbody>                                                                     
                                        <tr>
                                            <td>
                                                <input placeholder="Code"  type="text" v-model="code_no" class="form-control-plaintext form-control-sm  text-center">
                                            </td>
                                            <td>
                                                <input id="item_name" placeholder="Description" @keypress.enter="saveCode" :class="{'border-danger' : v$.item_name.$errors.length}"  type="text" v-model="item_name" class="form-control-plaintext form-control-sm ">
                                            </td>                                     
                                        </tr>     
                                                                
                                    <tr class="text-center"  v-if="products.length > 0"  v-for="(p, i) in products" :key="p.id" @click="setEdit(p)" :class="{'table-secondary' : editItem.id === p.id}">                                    
                                        <td :class="{'table-secondary': sort_item ==='code_no'}" class="p-0 col-1 position-relative">
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeProduct(i, p)"><i class="fa-solid fa-square-minus"></i></a>                                                                                       
                                            <span>
                                                <input @keyup="setEdit(p)" @keypress.enter="saveEdit" @paste="pasteEdit" type="text" class="form-control-plaintext form-control-sm text-center " v-model="p.code_no">
                                            </span>
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='item_name'}" class="p-0 col-2">
                                            <span>
                                                <input @keyup="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control-plaintext form-control-sm ms-2" v-model="p.item_name">
                                            </span>
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
                <div class="col-lg-4">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <table class="table table-bordered mb-0" v-if="Object.keys(editItem).length >0" >
                                <tbody>
                                    <tr class="table-white">
                                        <td colspan="4">
                                            <div class="text-center text-primary h6 pt-2">
                                                    {{editItem.item_name}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="table-info">
                                        <td class="col-1 py-1">Packing size <span class="float-end"><i class="fa-solid fa-caret-right"></i></span></td>
                                        <td colspan="3" class="text-center col-2" data-bs-toggle="modal" data-bs-target="#selectPackingSizeModal">
                                            <div class="" v-if="Object.keys(editItem.packing_size).length" >
                                                    <span v-if="editItem.packing_size.largest_unit">1{{editItem.packing_size.largest_unit}} * </span>
                                                    <span v-if="editItem.packing_size.medium_qty">{{editItem.packing_size.medium_qty}}</span><span v-else>1</span>{{editItem.packing_size.medium_unit}}
                                                    * {{editItem.packing_size.smallest_qty}}{{editItem.packing_size.smallest_unit}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="table-secondary">
                                        <td class="py-1">Pharmacy inventory</td>                                       
                                        <td class="text-center" colspan="3">
                                            <div  v-if="editItem.packing_size">
                                                {{calPackingQty(editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty, editItem.qty, editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit)}}                                            
                                            </div>
                                        </td>
                                    </tr>    
                                    <tr class="table-primary">
                                        <td class="py-1">Main inventory</td>                                      
                                        <td class="text-center" colspan="3">
                                            <div  v-if="editItem.packing_size">
                                                {{calPackingQty(editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty, editItem.main_qty, editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit)}}                                            
                                            </div>
                                        </td>
                                    </tr>            
                                    <tr class="fw-bold">
                                        <td></td>
                                        <td class="text-center">
                                            <span v-if="Object.keys(editItem.packing_size).length">{{editItem.packing_size.largest_unit}}</span>
                                            <span v-else>L unit</span>
                                        </td>
                                        <td class="text-center">
                                            <span v-if="Object.keys(editItem.packing_size).length">{{editItem.packing_size.medium_unit}}</span>
                                            <span v-else>M unit</span>
                                        </td>                                        
                                        <td class="text-center" >
                                            <span v-if="Object.keys(editItem.packing_size).length">{{editItem.packing_size.smallest_unit}}</span>
                                            <span v-else>S unit</span>
                                        </td>
                                    </tr>                        
                                    <tr class="">
                                        <td class="pt-2">Pur price</td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calPurPrice('largest_unit')" type="number" v-model="editItem.pur_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calPurPrice('medium_unit')"  type="number" v-model="editItem.pur_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calPurPrice('smallest_unit')" type="number" v-model="editItem.pur_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>                                        
                                    </tr>
                                    <tr class="">
                                        <td class="pt-2">Retail price</td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calRetailPrice('largest_unit')" type="number" v-model="editItem.retail_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calRetailPrice('medium_unit')"  type="number" v-model="editItem.retail_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calRetailPrice('smallest_unit')" type="number" v-model="editItem.retail_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>                                        
                                    </tr>
                                    <tr class="">
                                        <td class="pt-2">Wholesale price</td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calWholesalePrice('largest_unit')" type="number" v-model="editItem.whole_sale_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calWholesalePrice('medium_unit')"  type="number" v-model="editItem.whole_sale_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="Object.keys(editItem.packing_size).length > 0">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calWholesalePrice('smallest_unit')" type="number" v-model="editItem.whole_sale_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>                                        
                                    </tr>                                              
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td class="small">
                                            <div class="form-check form-switch">
                                                <input  v-model="editItem.auto_cal" class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckChecked" checked>
                                                <label class="form-check-label" for="flexSwitchCheckChecked">Auto cal</label>
                                              </div>                                              
                                        </td>
                                        <td colspan="3" class="p-0 text-end">
                                            <button class="btn btn-sm" @click="savePrice">                     
                                            <span v-if="!isSavePrice"><i class="fa-solid fa-hard-drive"></i> Save</span>
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
        <PackingSizeModal />
        <SelectPackingSizeModal @setPackingSize="setPackingSize" :ei="editItem" />
</div>   
 
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp,onSnapshot, collection, addDoc, query, where, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/products"
import PackingSizeModal from "@/views/modals/product/PackingSize.vue";
import SelectPackingSizeModal from "@/views/modals/product/SelectPackingSize.vue";
import * as hp from "@/lib/helper"
import * as cp from "@/lib/cal_price"


export default {
    components: {
            PageHeader, Footer,PackingSizeModal,SelectPackingSizeModal
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
            }
        }
    },
    created() {     
      
    },
    mounted() {
        const code_no=document.getElementById("search_by_code");
        code_no.focus();
        this.isLoading=true;
        this.fetchProducts();
        this.checkDatabase();
       
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
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
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        pasteEdit(){
            setTimeout(()=>{
                this.saveEdit();
            },300)
        },
        async saveEdit(){           
            const updateDocument=doc(db, "products", this.editItem.id)
                await updateDoc(updateDocument, this.editItem)  
        },
        setPackingSize(p){
            this.editItem.packing_size=p;
           this.savePackingSize();
        } ,
        async savePackingSize(){
            const updateDocument=doc(db, "products", this.editItem.id)
                await updateDoc(updateDocument, {
                    packing_size : this.editItem.packing_size,        
                    edit_user : this.editItem.edit_user          
                 })  
        },
        async removeProduct(i, p){
            const cf=confirm(`Are you sure ? delete the product code : ${p.item_name} `)
            if(!cf) return false;

            const updateDocument=doc(db, "products", p.id)
            await updateDoc(updateDocument, {edit_user: {uid: this.user.uid, type: 'removed'}})  

            const document=doc(db, "products", p.id)     
            await deleteDoc(document)      
        },
        async saveCode(){
            if(Object.keys(this.editItem).length) this.saveEdit();

            const validattion = await this.v$.$validate()
            if(!validattion) return false;
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
                created_at : save_time,          
                pur_price: {},
                main_qty:0,
                edit_user: {uid: this.user.uid, type: 'added'}

            }
            const result=await addDoc(dataCollection, data);                    
        },
        resetInput(){            
            this.item_name="";
            this.code_no="";
            this.v$.$reset()
            this.isSaving=false;
        },
        updateProducts(){
            this.isUpdating=true;
            this.fetchProducts();
        },
       async savePrice(){
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
            this.editItem={...p,  edit_user:  {uid:this.user.uid, type: "modified"}};
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
        searchProducts(){
            this.products=[];
            if(this.search_by_code){
                this.products=this.productsData.filter((f)=>f.code_no===Number(this.search_by_code))
            }else 
            if(this.search_by_description){
                this.products=this.productsData.filter((f)=>{
                    return f.item_name.toLowerCase().includes(this.search_by_description.toLowerCase())
                })
            }else{
                this.products= this.productsData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                       }) 
            }
            if(this.products.length > 0){
                let item={}
                if(Object.keys(this.editItem).length){
                    const items=this.products.filter((f)=>f.id==this.editItem.id)
                    item=items[0]
                }else{
                     item=this.products[0]
                }
                this.setEdit(item)
            }
        },
        message(type){
                this.msg.info=hp.message(type, "medicine code")
                this.msg.type="success"                                                         
        }, 
        async checkDatabase(){ 
            const query_url=query(collection(db, "products"), orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{
                    const dc=snapShot._snapshot.docChanges;
                    const user=this.user;
                    snapShot.docChanges().forEach((change) => {                       
                        const res=change.doc.data();   
                            if(change.type==="added" && res.edit_user.uid==user.uid && dc.length===1){
                                    this.message("added")
                                    this.resetInput();    
                                    hp.setAutoFocus("item_name")     
                            }           
                            if(change.type==="modified" && res.edit_user.type=="modified" && res.edit_user.uid==user.uid){
                                    this.message("modified")
                            }  
                            if(change.type==="removed" && res.edit_user.type=="removed" && res.edit_user.uid==user.uid){
                                    this.message("removed")
                            }             
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
<style lang="">
    
</style>

<!--
                                            <ul class="list-group list-group-flush">
                                <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                    <div class="row">
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter mt-3">Code</div>
                                        </div>
                                        <div class="col-lg-5">
                                                <div class="small fw-lighter mt-3">Description</div>
                                        </div>
                                        <div class="col-lg-1">
                                                <div class="small fw-lighter mt-3">Qty</div>
                                        </div>
                                        <div class="col-lg-3 small fw-lighter text-center">
                                                <div>Prices</div>
                                                <div class="row">
                                                    <div class="col-4">Purchase</div>
                                                    <div class="col-4">Regular sale</div>
                                                    <div class="col-4">Whole sale</div>
                                                </div>
                                        </div>
                                </div>
                                </li>
                                <li class="list-group-item" v-if="isLoading">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                      </div>
                                </li>
                                <li v-if="products.length > 0" class="list-group-item list-group-item-action" v-for="p in products" :key="p.id" >
                                        <div class="row">
                                                <div class="col-lg-3 small">
                                                        <div>{{p.code_no}}</div>
                                                </div>
                                                <div class="col-lg-5 small">
                                                        <div>{{p.item_name}}</div>
                                                </div>
                                                <div class="col-lg-1 small">
                                                        <div>{{p.qty}}</div>
                                                </div>
                                                <div class="col-lg-3 small fw-lighter text-center">
                                                    <div class="row">
                                                        <div class="col-4">{{p.purchase_price}}</div>
                                                        <div class="col-4">{{p.regular_sale_price}}</div>
                                                        <div class="col-4">{{p.whole_sale_price}}</div>
                                                    </div>
                                            </div>
                                        </div>
                                </li>
                        </ul>
                    -->