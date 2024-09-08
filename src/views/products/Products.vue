<template lang="">       
 <div>           
        <PageHeader page-title="Medicine code" page-code="medicineCode" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">   
                <div class="col-sm-2 d-grid">
                    <button type="button"  class="btn btn-sm text-danger" :disabled="isLoading" @click="deleteMultiple">
                        <span v-if="!isDeleting"><i class="fa-solid fa-trash"></i> Delete</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isDeleting"></span>
                    </button>
                </div>       
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
                                        <tr class="text-center table-secondary" style="cursor: context-menu;">
                                            <td class="">
                                                <input type="checkbox" @click="checkAll" v-model="is_check_all">
                                            </td>
                                            <td class="">Code</td>
                                            <td class="">Description</td>
                                        </tr>                                                                    
                                        <tr>
                                            <td class="position-relative">
                                                <input class="position-absolute start-50 top-50 translate-middle" type="checkbox" @click="checkAll" v-model="is_deleted">
                                            </td>
                                            <td>
                                                <input placeholder="Code"  type="text" v-model="code_no" class="form-control-plaintext form-control-sm  text-center">
                                            </td>
                                            <td>
                                                <input id="item_name" placeholder="Description" @keypress.enter="saveCode" :class="{'border-danger' : v$.item_name.$errors.length}"  type="text" v-model="item_name" class="form-control-plaintext form-control-sm ">
                                            </td>                                     
                                        </tr>     
                                                                
                                    <tr class="text-center"  v-if="products.length > 0"  v-for="(p, i) in products" :key="p.id"  :class="{'table-secondary' : editItem.id === p.id}">   
                                        <td class="position-relative">
                                            <input class="position-absolute start-50 top-50 translate-middle" type="checkbox" v-model="p.is_deleted">
                                        </td>                                 
                                        <td :class="{'table-secondary': sort_item ==='code_no'}" class="p-0  position-relative">
                                            <!---<a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeProduct(i, p)"><i class="fa-solid fa-square-minus"></i></a>-->                                                                                       
                                            <span>
                                                <input @click="setEdit(p)" @keyup="setEdit(p)" @keypress.enter="saveEdit" @paste="pasteEdit" type="text" class="form-control-plaintext form-control-sm text-center " v-model="p.code_no">
                                            </span>
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='item_name'}" class="p-0">
                                            <span>
                                                <input @click="setEdit(p)" @keyup="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control-plaintext form-control-sm ms-2" v-model="p.item_name">
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
                            <table class="table table-bordered mb-0 table-hover" v-if="Object.keys(editItem).length >0" >
                                <tbody>
                                    <tr class="">
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 4 : 5]">
                                            <div class="text-center text-primary h6 pt-2">
                                                    {{editItem.item_name}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="table-primary"  data-bs-toggle="modal" data-bs-target="#selectPackingSizeModal">
                                        <td class="col-1 py-1">Packing size <span class="float-end"><i class="fa-solid fa-caret-right"></i></span></td>
                                        <td :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]" class="text-center col-3">
                                            <div v-if="editItem.packing_size.smallest_unit && editItem.packing_size.medium_unit">
                                                    {{calPackingSize(editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit, editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty)}}
                                            </div>
                                        </td>
                                    </tr>
                                    <tr class="">
                                        <td class="py-1">Pharmacy Inv</td>                                       
                                        <td class="text-center" :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]">
                                            <div  v-if="editItem.packing_size">
                                                {{calPackingQty(editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty, editItem.qty, editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit)}}                                            
                                            </div>
                                        </td>
                                    </tr>    
                                    <tr class="">
                                        <td class="py-1">Main Inv</td>                                      
                                        <td class="text-center" :colspan="[editItem.packing_size.largest_unit !=null ? 3 : 4]">
                                            <div  v-if="editItem.packing_size">
                                                {{calPackingQty(editItem.packing_size.medium_qty, editItem.packing_size.smallest_qty, editItem.main_qty, editItem.packing_size.largest_unit, editItem.packing_size.medium_unit, editItem.packing_size.smallest_unit)}}                                            
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
                                        <td class="pt-2">Pur price</td>
                                        <td class="text-center p-0" v-if="editItem.packing_size.largest_unit !=null">
                                            <span >
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calPurPrice('largest_unit')" type="number" v-model="editItem.pur_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="editItem.packing_size.medium_unit !=null">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calPurPrice('medium_unit')"  type="number" v-model="editItem.pur_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="editItem.packing_size.smallest_unit !=null">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calPurPrice('smallest_unit')" type="number" v-model="editItem.pur_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>                                        
                                    </tr>
                                    <tr class="">
                                        <td class="pt-2">Retail price</td>
                                        <td class="text-center p-0" v-if="editItem.packing_size.largest_unit !=null">
                                            <span >
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calRetailPrice('largest_unit')" type="number" v-model="editItem.retail_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="editItem.packing_size.medium_unit !=null">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calRetailPrice('medium_unit')"  type="number" v-model="editItem.retail_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="editItem.packing_size.smallest_unit !=null">
                                                <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.smallest_unit" @keyup="calRetailPrice('smallest_unit')" type="number" v-model="editItem.retail_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>                                        
                                    </tr>
                                    <tr class="">
                                        <td class="pt-2">Wholesale price</td>
                                        <td class="text-center p-0" v-if="editItem.packing_size.largest_unit != null">
                                            <span >
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.largest_unit" @keyup="calWholesalePrice('largest_unit')" type="number" v-model="editItem.whole_sale_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="editItem.packing_size.medium_unit !=null">
                                            <input @keypress.enter="savePrice" :disabled="!editItem.packing_size.medium_unit"  @keyup="calWholesalePrice('medium_unit')"  type="number" v-model="editItem.whole_sale_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                            </span>
                                        </td>
                                        <td class="text-center p-0">
                                            <span v-if="editItem.packing_size.smallest_unit !=null">
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
            },
            is_deleted:false,
            is_check_all: false,
            isDeleting:false,
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
        async deleteMultiple(){
            const is_has=this.products.filter((f)=>f.is_deleted)
            if(is_has.length <=0) return false;
            const cf=confirm(`Are you sure you want to delete the medicine code ?`)
            if(!cf) return false;
            this.isDeleting=true;
            this.products.filter(async (s)=>{
                if(s.is_deleted){
                    const updateDocument=doc(db, "products", s.id)
                    await updateDoc(updateDocument, {is_deleted: true, edit_user: {uid: this.user.uid, type: 'removed'}})  
                }
            })
        },
        checkAll(){
            this.products=this.products.map((s)=>{
               return {...s, is_deleted : this.is_check_all ? false : true}
            })
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
                packing_size:{},
                created_at : save_time,          
                pur_price: {},
                main_qty:0,
                is_deleted:this.is_deleted,
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
       
        checkEdit(){
            if(this.editItem.id){
                const items=this.products.filter((f)=>f.id==this.editItem.id)
                this.setEdit(items[0])
            }else{
                this.setEdit(this.products[0])
            }
           
        },
        setEdit(p){          
             this.editItem=p ? {...p,  edit_user:  {uid:this.user.uid, type: "modified"}} : {};
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
            this.checkEdit();
                      
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
                            if(change.type==="modified" && res.edit_user.type=="removed" && res.edit_user.uid==user.uid){
                                    this.message("removed")    
                                    this.isDeleting=false;
                                    this.is_check_all=false;
                                    //this.editItem={}
                            }    
                            this.checkEdit();
   
                    });                 
            });                              
        } ,
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), where("is_deleted", "==", false), orderBy("created_at", "desc"));
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

