<template lang="">
    <div>           
        <PageHeader page-title="New purchase" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="savePurchase" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Purchase'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow min-vh-75">
                <div  class="card-body p-2">
                    <form @submit.prevent="savePurchase">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-6 py-1">Voucher no. :</td>
                                            <td class="col-6 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext form-control-sm  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end py-1">Location :</td>
                                            <td class="">
                                                <select class="form-control-plaintext form-control-sm border-bottom" v-model="location">
                                                    <option value="Main">Main</option>
                                                    <option value="Pharmacy">Pharmacy</option>
                                                </select>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>                                       
                                        <tr>
                                            <td class="text-end py-1 col-6">Pur date :</td>
                                            <td class="py-0 col-6">
                                                <input type="date" :class="{'border-0 border-bottom border-danger' : v$.pur_date.$errors.length > 0}"  v-model="pur_date" class="form-control-plaintext form-control-sm border-bottom" id="pur_date" placeholder="Date">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end py-1">Supplier :</td>
                                            <td class="py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.supplier.name.$errors.length > 0}"  v-model="supplier.name" data-bs-toggle="modal" data-bs-target="#searchSupplier"  class="form-control-plaintext form-control-sm border-bottom">                                                
                                            </td>
                                        </tr>
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <table class="table table-bordered mb-0  table-hover" :class="{'border-danger':v$.storePurchase.$errors.length > 0}" >
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1"> 
                                        Code
                                    </td>
                                    <td class="col-2 " data-bs-toggle="modal" data-bs-target="#searchCode" style="cursor: context-menu;" ><i class="fa-solid fa-circle-plus"></i> Description</td>
                                    <td class="col-1 ">Exp. date</td>     
                                    <td class="col-1">Packing size</td>                              
                                    <td class="col-1" colspan="1">Qty</td>
                                    <td class="col-2" colspan="2">Unit price</td>                                    
                                    <td class="col-1">Discount</td>                                    
                                    <td class="col-1">Amount</td>
                                    <td class="col-1">Discount</td>
                                    <td class="col-1">Price</td>
                                </tr>
                                <tr class="small"v-if="storePurchase.length > 0" v-for="(st, i) in storePurchase" :key="st.id" @mouseenter="setEdit(i)"  >
                                    <td class="pt-2 text-center">
                                        <div class="position-relative">
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle p-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                            {{st.code_no}}                                           
                                        </div>                                    
                                    </td>
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="p-0">
                                        <input @change="setExpDate(st.id)"  type="date"  v-model="st.tmp_exp_date" class="form-control-plaintext form-control-sm text-center"  placeholder="Exp. date">                                   
                                    </td>
                                    <td class="pt-2">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class="pt-2 text-end" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>                                                                   
                                    <td class="p-0">
                                            <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.qty_pur_price" class="form-control-plaintext form-control-sm  text-center" placeholder="Price">                                        
                                    </td>
                                    <td class="p-0">
                                        <select @change="calEdit" class="form-control-plaintext form-control-sm text-center" v-model="st.qty_pur_price_unit">
                                            <option  v-for="pu in onlyString(st.packing_size)">{{pu}}</option>
                                        </select>
                                    </td>
                                    <td class="text-center pt-2" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                        <span v-if="st.dis_qty">{{st.dis_qty}}{{st.dis_qty_unit}}</span>
                                            <span v-if="st.dis_amount">{{st.dis_amount}}$</span>
                                            <span v-if="st.dis_percent">{{st.dis_percent}}%</span>                                      
                                    </td>                                                                   
                                    <td class="text-end pt-2">{{st.amount}}</td>
                                    <td class="text-end pt-2">{{st.discount}}</td>
                                    <td class="text-end pt-2">{{st.price}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Total amount</td>
                                    <td class="text-end">{{total_amount}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Total discount</td>
                                    <td class="text-end ">{{total_discount}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Total price</td>
                                    <td class="text-end">{{total_price}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end pt-2">Extra discount</td>
                                    <td class="text-end">
                                        <input  type="number" @keyup="calTotal"  v-model="extra_discount" class="form-control-plaintext form-control-sm  text-end" placeholder="0">
                                    </td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Grand total price</td>
                                    <td class="text-end">{{grand_total_price}}</td>
                                </tr>
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
            <ProductModal @setCode="setCode" :storePurchase="storePurchase" />
            <SupplierModal @setCode="setSupplier" />
        </div>
        </div>
        </div>
        <!-- set qty Modal -->
        <div class="modal fade" id="setQtyModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2">                       
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" @click="setPurQty" data-bs-dismiss="modal">
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
                                    <td colspan="2" class="text-center p-2" >{{editItem.item_name}}</td>
                                </tr>
                                <tr v-if="editItem.packing_size.largest_unit">
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.pur_l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.pur_l_unit">
                                                <option selected>{{editItem.packing_size.largest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.pur_m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.pur_m_unit">
                                                <option selected>{{editItem.packing_size.medium_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.pur_s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.pur_s_unit">
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
        <!--end qty modal-->  
        <!-- discount price Modal -->
        <div class="modal fade" id="setDiscountModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2 justify-content-end">     
                        <div class="col-4" v-if="Object.keys(editItem).length">
                            <button v-if="editItem.amount" type="button" @click="saveDiscount" class="btn btn-sm" :disabled="isLoading" data-bs-dismiss="modal">
                                <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                            </button>
                        </div>                  
                        <div class="col-4" v-if="Object.keys(editItem).length">
                            <button type="button" class="btn btn-sm" @click="clearDiscount" v-if="editItem.amount">
                                <span><i class="fa-solid fa-broom"></i>  Clear</span>
                            </button>
                        </div>
                        <div class="col-4">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered" v-if="Object.keys(editItem).length">
                            <tbody v-if="editItem.amount">
                                <tr>
                                    <td colspan="2" class="text-center p-2" >{{editItem.item_name}}</td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <select @change="calDiscount" class="form-control-plaintext form-control-sm text-center" v-model="editItem.dis_qty_unit">
                                                <option  v-for="pu in onlyString(editItem.packing_size)">{{pu}}</option>
                                        </select>
                                    </td>
                                    <td class="p-0">
                                        <input type="number" @change="calDiscount" @keyup="calDiscount" v-model="editItem.dis_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="pt-2 text-center">
                                        %
                                    </td>
                                    <td class="p-0">
                                        <input @keyup="calDiscount" @change="calDiscount" type="number" v-model="editItem.dis_percent" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="pt-2 text-center">
                                        $
                                    </td>
                                    <td class="p-0">
                                        <input @keyup="calDiscount" @change="calDiscount" type="number" v-model="editItem.dis_amount" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="p-1 text-end">Discount</td>
                                    <td  class="p-1 text-center">{{editItem.discount}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>                
            </div>
            </div>
        </div>
        <!--end discount modal-->  
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment, orderBy} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/products"

export default {
    components: {
        PageHeader, ProductModal, SupplierModal
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                voucher_no: {required},             
                pur_date: {required},
                totalAmount:0,
                supplier : {
                    name : {required}
                },
                storePurchase : {required}
            }
    },
    data(){
        return{       
            voucher_no:"", 
            tmp_exp_date:"",
            pur_date:"",
            supplier : {
                name : "",
            }, 
            purchase_item: {},
            storePurchase: [],
            editItem: {},

            extra_discount: 0,
            total_amount:0,
            total_discount:0,
            total_price:0,
            grand_total_price:0,

            code_no:"",
            products:[],

            location: "Main",

        
            isLoading:false,
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
        this.voucher_no=new Date().getTime();
        this.pur_date=new Date().toISOString().substr(0, 10)

      
    },
    
    methods:{
       
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
        setExpDate(id){
           const arr=this.storePurchase.filter((f)=>f.id==id)
           const p=arr[0]
           this.editItem.exp_date=p.tmp_exp_date ? Timestamp.fromDate(new Date(p.tmp_exp_date))  : null;
           p.exp_date=p.tmp_exp_date ? Timestamp.fromDate(new Date(p.tmp_exp_date))  : null;
        },
        setSupplier(s){
            this.supplier_name=s.name;
            this.supplier=s;
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
            return this.storePurchase.map((f)=>{  
                if(f.id===e.id){
                    f.dis_qty=e.dis_qty ? e.dis_qty : null;
                    f.dis_qty_unit=e.dis_qty_unit ? e.dis_qty_unit : null;
                    f.dis_amount=e.dis_amount ? e.dis_amount : null;
                    f.dis_percent=e.dis_percent ? e.dis_percent :null;
                    f.discount=e.discount;
                    f.price =f.amount - e.discount;
                }
            }) && this.calTotal();
        },
        calDiscount(){
            const e=this.editItem;
            return this.storePurchase.map((f)=>{   
                if(f.id===e.id){
                    if(e.dis_amount){
                        e.discount=e.dis_amount;
                    }else if(e.dis_percent){
                        e.discount=(f.amount * e.dis_percent) /100;
                    }else if(e.dis_qty){      
                        const arr=Object.entries(e.packing_size)               
                        const d_units=arr.filter((ft)=>{
                            return ft[1]==e.dis_qty_unit;
                        })  
                       const dis_unit=d_units[0][0]  
                       let discount=0;
                       if(dis_unit=="largest_unit"){
                           discount=e.pur_price.largest_unit * e.dis_qty
                       }else if(dis_unit=="medium_unit"){
                            discount=e.pur_price.medium_unit * e.dis_qty;
                       }else if(dis_unit=="smallest_unit"){
                            discount=e.pur_price.smallest_unit * e.dis_qty;
                       }    
                       e.discount=discount;                        
                    }
                    else{
                        e.discount=0;
                    }
                }
            })
        },      

        setPurQty(){
            const e=this.editItem;
                let pur_qty=0;
                pur_qty += e.pur_l_qty ? e.pur_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                pur_qty += e.pur_m_qty ? e.pur_m_qty * e.packing_size.smallest_qty: 0;
                pur_qty += e.pur_s_qty ? e.pur_s_qty : 0;
               return this.storePurchase.map((f)=>{   
                    if(f.id===e.id){
                        f.pur_qty=pur_qty,
                        f.pur_l_qty =e.pur_l_qty ? e.pur_l_qty : 0,
                        f.pur_m_qty= e.pur_m_qty ? e.pur_m_qty : 0,
                        f.pur_s_qty= e.pur_s_qty  ? e.pur_s_qty  : 0
                    }
               }) &&
               this.calEdit();
        },
     
        setEdit(i){
            const item=this.storePurchase[i];
            this.editItem={...item, 
                pur_l_unit: item.packing_size.largest_unit,
                pur_m_unit: item.packing_size.medium_unit,
                pur_s_unit: item.packing_size.smallest_unit,
                pur_l_qty: item.pur_l_qty ? item.pur_l_qty : 0,
                pur_m_qty: item.pur_m_qty ? item.pur_m_qty : 0,
                pur_s_qty: item.pur_s_qty  ? item.pur_s_qty  : 0,
            };
        },
        calEdit(i){
            const oei=this.editItem;
            this.storePurchase.map((f)=>{
                if(f.id===oei.id){
                    if(f.qty_pur_price && f.qty_pur_price_unit){
                       const arr=Object.entries(f.packing_size)
                        const units=arr.filter((ft)=>{
                            return ft[1]==f.qty_pur_price_unit;
                        })  
                       const pur_unit=units[0][0]     
                       let amount=0;
                       if(pur_unit=="largest_unit"){
                           amount=Math.floor((f.qty_pur_price / (f.packing_size.medium_qty * f.packing_size.smallest_qty)) * f.pur_qty)
                       }else if(pur_unit=="medium_unit"){
                            amount=Math.floor((f.qty_pur_price / f.packing_size.smallest_qty) * f.pur_qty)
                       }else if(pur_unit=="smallest_unit"){
                            amount=Math.floor(f.qty_pur_price  * f.pur_qty)
                       }    
                        f.amount=amount;
                        f.price=f.discount ? amount - f.discount : amount;

                       let smallest_price=0;
                       let medium_price=0;
                       let largest_price=0; 
                       if(pur_unit=="largest_unit"){
                           smallest_price=Math.floor((f.qty_pur_price / (f.packing_size.medium_qty * f.packing_size.smallest_qty)))
                           medium_price=smallest_price * f.packing_size.smallest_qty
                           largest_price=medium_price * f.packing_size.medium_qty
                       }else if(pur_unit=="medium_unit"){
                            smallest_price=Math.floor((f.qty_pur_price / f.packing_size.smallest_qty))
                            medium_price=smallest_price * f.packing_size.smallest_qty
                            largest_price=medium_price * f.packing_size.medium_qty
                       }else if(pur_unit=="smallest_unit"){
                            smallest_price=Math.floor(f.qty_pur_price)
                            medium_price=smallest_price * f.packing_size.smallest_qty
                            largest_price=medium_price * f.packing_size.medium_qty
                       }    

                       const pur_price={
                            largest_unit : largest_price,
                            medium_unit: medium_price,
                            smallest_unit: smallest_price
                       }
                       f.pur_price=pur_price;
                      
                    }else{
                        f.amount=0
                    }
                }
            }) && this.calTotal();
           
        },
        
        calTotal(){
            let total_amount=0;
            let total_price=0;
            let total_discount=0;
            
            this.storePurchase.map((m)=>{
                total_amount += m.amount ? m.amount : 0;
                total_price += m.price ? m.price : 0;
                total_discount += m.discount ? m.discount : 0;
            })

            this.total_amount=total_amount;
            this.total_discount=total_discount;
            this.total_price=total_price;
            this.grand_total_price=this.extra_discount ? this.total_price - this.extra_discount : this.total_price;
           
        },
        removeStoreItem(i){
            this.storePurchase.splice(i, 1)
        },
        storingPurchase(){           
            this.storePurchase.push(this.purchase_item)   
         },
        setCode(p){           
            this.purchase_item={...p, pur_qty: "", qty_pur_price: ""};
            const s=this.storePurchase;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingPurchase();
            }
           
        },
       async savePurchase(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                pur_date: Timestamp.fromDate(new Date(this.pur_date)),
                supplier: this.supplier,
                location: this.location,
                products : this.storePurchase,
                user : {uid: this.user.uid, email: this.user.email},
                total_amount:this.total_amount,
                total_discount:this.total_discount,
                total_price:this.total_price,
                extra_discount: this.extra_discount,
                grand_total_price: this.grand_total_price,
            }      
            
            const saveDocument=collection(db, "purchases")
            await addDoc(saveDocument, voucher)
            this.storePurchase.map(async (p)=>{
                const id=p.id;
                const pur_qty=p.pur_qty;               
                const updateDocument=doc(db, "products", id)
                if(this.location==="Main"){
                    await updateDoc(updateDocument, {
                     main_qty : increment(pur_qty),
                     pur_price: p.pur_price
                 })
                }
                 if(this.location==="Pharmacy"){
                    await updateDoc(updateDocument, {
                     qty : increment(pur_qty),
                     pur_price: p.pur_price,
                 })
                }    
            })
            this.msg.type="success";
            this.msg.info="Purchase save successful."          
             
            this.isLoading=false;
            this.voucher_no=new Date().getTime();
            this.pur_date=new Date().toISOString().substr(0, 10)
            this.supplier={}
            this.storePurchase=[]
            this.purchase_item=null
            this.editItem={},
            this.total_amount=0,
            this.total_discount=0,
            this.total_price=0,
            this.extra_discount=0
            this.grand_total_price=0;
            this.v$.$reset()
        },
           
    }
}
</script>
<style lang="">
    
</style>

