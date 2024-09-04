<template lang="">
    <div>           
        <PageHeader page-title="Opening stocks" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveStocks" class="btn btn-sm" :disabled="isLoading || storeItems.length <=0">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'OpeningStocks'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow min-vh-75">
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks" >
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6 col-lg-4">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-6 py-1">Voucher no. :</td>
                                            <td class="col-6 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext form-control-sm  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end py-1">Date :</td>
                                            <td class="py-0">
                                                <input type="date" :class="{'border-0 border-bottom border-danger' : v$.created_at.$errors.length > 0}"  v-model="created_at" class="form-control-plaintext form-control-sm border-bottom" id="created_at" placeholder="Date">                                                
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>                       
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2" data-bs-toggle="modal" data-bs-target="#searchCode" style="cursor: context-menu;"><i class="fa-solid fa-circle-plus"></i> Description</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1">Main Inv</td>     
                                    <td class="col-1">Pharmacy Inv</td>   
                                    <td class="col-1">Pur price</td>
                                    <td class="col-1">Retail price</td>
                                    <td class="col-1">Wholesale price</td>                                  
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id" @mouseenter="setEdit(i)" >
                                    <td class="text-center">
                                        <div class="position-relative">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle p-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                        </div>                                    
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class=" text-end" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.main_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td> 
                                    <td class=" text-end" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td> 
                                    <td class="text-end" data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                        <span v-if="st.packing_size.largest_unit"> {{st.pur_price.largest_unit}}' {{st.packing_size.largest_unit}}</span>
                                        <span v-else>{{st.pur_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>       
                                    <td class="text-end " data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                        <span v-if="st.packing_size.largest_unit">{{st.retail_price.largest_unit}}' {{st.packing_size.largest_unit}} </span>
                                        <span v-else>{{st.retail_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>
                                    <td class="text-end" data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                        <span v-if="st.packing_size.largest_unit">{{st.whole_sale_price.largest_unit}}' {{st.packing_size.largest_unit}} </span>
                                        <span v-else>{{st.whole_sale_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>                                                                                    
                                </tr>                                                                                                                
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
            <ProductModal @setCode="setCode"  />
        </div>
        </div>
        </div>
        <!-- setQty Modal -->
        <div class="modal fade" id="setQtyModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2">                       
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" @click="saveQty" data-bs-dismiss="modal">
                                <span ><i class="fa-solid fa-hard-drive"></i> Save</span>
                            </button>
                        </div>
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered mb-0" v-if="Object.keys(editItem).length">
                            <tbody>
                                <tr>
                                    <td colspan="3" class="text-center p-2" >{{editItem.item_name}}</td>
                                </tr>
                                <tr>
                                    <td class="text-center col-2">Main Inventory</td>
                                    <td class="text-center col-2">Pharmacy Inventory</td>
                                    <td class="text-center col-1">Unit</td>
                                </tr>
                                <tr v-if="editItem.packing_size.largest_unit">
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.main_l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.packing_size.largest_unit">
                                                <option selected>{{editItem.packing_size.largest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.main_m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.packing_size.medium_unit">
                                                <option selected>{{editItem.packing_size.medium_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.main_s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.packing_size.smallest_unit">
                                                <option selected>{{editItem.packing_size.smallest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>                
            </div>
            </div>
        </div>
        <!--end setQty modal-->  
        <!-- setPrices Modal -->
        <div class="modal fade" id="setPricesModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row mb-2">                                             
                        <div class="col-12 text-end">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered mb-0" v-if="Object.keys(editItem).length">
                            <tbody>
                                <tr>
                                    <td colspan="4" class="text-center p-2" >{{editItem.item_name}}</td>
                                </tr>
                                <tr class="fw-bold">
                                    <td class="col-1"></td>
                                    <td class="text-center col-1">
                                        <span v-if="Object.keys(editItem.packing_size).length">{{editItem.packing_size.largest_unit}}</span>
                                        <span v-else>L unit</span>
                                    </td>
                                    <td class="text-center col-1">
                                        <span v-if="Object.keys(editItem.packing_size).length">{{editItem.packing_size.medium_unit}}</span>
                                        <span v-else>M unit</span>
                                    </td>                                        
                                    <td class="text-center col-1" >
                                        <span v-if="Object.keys(editItem.packing_size).length">{{editItem.packing_size.smallest_unit}}</span>
                                        <span v-else>S unit</span>
                                    </td>
                                </tr>       
                                <tr class="">
                                    <td class="pt-2">Pur price</td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                        <input :disabled="!editItem.packing_size.largest_unit" @keyup="calPurPrice('largest_unit')" type="number" v-model="editItem.pur_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                        <input :disabled="!editItem.packing_size.medium_unit"  @keyup="calPurPrice('medium_unit')"  type="number" v-model="editItem.pur_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input :disabled="!editItem.packing_size.smallest_unit" @keyup="calPurPrice('smallest_unit')" type="number" v-model="editItem.pur_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>                                        
                                </tr>
                                <tr class="">
                                    <td class="pt-2">Retail price</td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                        <input :disabled="!editItem.packing_size.largest_unit" @keyup="calRetailPrice('largest_unit')" type="number" v-model="editItem.retail_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                        <input :disabled="!editItem.packing_size.medium_unit"  @keyup="calRetailPrice('medium_unit')"  type="number" v-model="editItem.retail_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input :disabled="!editItem.packing_size.smallest_unit" @keyup="calRetailPrice('smallest_unit')" type="number" v-model="editItem.retail_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>                                        
                                </tr>
                                <tr class="">
                                    <td class="pt-2">Wholesale price</td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                        <input :disabled="!editItem.packing_size.largest_unit" @keyup="calWholesalePrice('largest_unit')" type="number" v-model="editItem.whole_sale_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                        <input :disabled="!editItem.packing_size.medium_unit"  @keyup="calWholesalePrice('medium_unit')"  type="number" v-model="editItem.whole_sale_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>
                                    <td class="text-center p-0">
                                        <span v-if="Object.keys(editItem.packing_size).length > 0">
                                            <input :disabled="!editItem.packing_size.smallest_unit" @keyup="calWholesalePrice('smallest_unit')" type="number" v-model="editItem.whole_sale_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                        </span>
                                    </td>                                        
                                </tr>  
                                <tr>
                                    <td class="ps-2 pt-2" colspan="4">
                                        <div class="form-check form-switch">
                                            <input @change="saveAutoCal" v-model="editItem.auto_cal" class="form-check-input" type="checkbox" role="switch" id="flexSwitchCheckChecked" checked>
                                            <label class="form-check-label" for="flexSwitchCheckChecked">Auto cal</label>
                                          </div>                                              
                                    </td>
                                 </tr>               
                            </tbody>
                        </table>
                    </div>
                </div>                
            </div>
            </div>
        </div>
        <!--end setPrices modal-->  
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/stocks/ProductModal.vue'
import * as ss from "@/lib/snapshot/products"
import * as hp from "@/lib/helper"
import * as cp from "@/lib/cal_price"
export default {
    components: {
        PageHeader, ProductModal
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
          //  total_retail_amount:0,
          //  total_whole_sale_amount:0, 
           // total_main_retail_amount:0,
           // total_main_whole_sale_amount:0,
           //// total_pur_amount:0,    
          //  pur_amount:0, 
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
      
    },
    watch:{
       editItem(data){
           
       }
    },
    created() {     
      
        this.voucher_no=new Date().getTime();
        this.created_at=new Date().toISOString().substr(0, 10)
    },
    
    methods:{
        calRetailPrice(unit){
            return cp.calRetailPrice(unit, this.editItem);
        },
        calWholesalePrice(unit){
            return cp.calWholesalePrice(unit,this.editItem);
        },
        calPurPrice(unit){
            return cp.calPurPrice(unit, this.editItem);
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
                 s_qty: s ? s :0                 
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
       async saveStocks(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.isSaving=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(new Date(this.created_at)),           
                products : this.storeItems,
                user : {uid: this.user.uid, email: this.user.email},              
            }      
            const saveDocument=collection(db, "openingStocks")
            await addDoc(saveDocument, voucher)
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
            this.msg.type="success";
            this.msg.info="Opening stocks save successful."          
             
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=new Date().toISOString().substr(0, 10)         
            this.storeItems=[]
            this.stock_item=null
            this.editItem={}
            //this.total_retail_amount=0
            //this.total_whole_sale_amount=0
           // this.total_pur_amout=0;
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
<style lang="">
    
</style>
