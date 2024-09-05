<template lang="">
    <div>           
        <PageHeader page-title="Sale" page-code="sale" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                            
                <div class="col-sm-4 lg-md-2 pt-1 ps-4 small">
                    <div class="form-check form-check-inline" >
                        <input class="form-check-input border-dark" :disabled="saleItems.length > 0" type="radio" name="inlineRadioOptions" id="inlineRadio1" v-model="sale_type" value="retail">
                        <label class="form-check-label" for="inlineRadio1" :class="[sale_type==='retail' ? 'tex-primary' : 'text-dark']">Retail</label>
                      </div>
                      <div class="form-check form-check-inline" >
                        <input class="form-check-input border-dark" :disabled="saleItems.length > 0" type="radio" name="inlineRadioOptions" id="inlineRadio2" v-model="sale_type" value="whole_sale">
                        <label class="form-check-label" for="inlineRadio2" :class="[sale_type==='whole_sale' ? 'tex-primary' : 'text-dark']">Wholesale</label>
                      </div>
                </div>
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#salesModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-sm-2 d-grid">
                    <button :disabled="saleItems.length <=0 || isNewSale" class="btn btn-sm"  @click="printSale"><i class="fa-solid fa-print" ></i> Print</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveSale" class="btn btn-sm" :disabled="isLoading || saleItems.length <=0 || !isNewSale"><i class="fa-solid fa-hard-drive"></i> Save
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button @click="clearSaleItem" class="btn btn-sm" :disabled="saleItems.length <=0"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                </div>
            </div>
        </div>     
        <div class="container-fluid my-2">
            <div class="row g-1">
                <!--<div class="col-lg-4">
                        <Products @setCode="setCode" :saleItems="saleItems" />
                </div>-->
                <div class="col-12">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row justify-content-end mb-2">
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless">
                                        <thead class="small">
                                            <tr class="">
                                                <td class="text-end col-4 py-1">Voucher no. :</td>
                                                <td class="col-6 py-0">
                                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext form-control-sm  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="text-end py-1">Date :</td>
                                                <td class="py-0">
                                                    <input type="date" :class="{'border-0 border-bottom border-danger' : v$.sale_date.$errors.length > 0}"  v-model="sale_date" class="form-control-plaintext form-control-sm border-bottom" id="sale_date" placeholder="Date">                                                
                                                </td>
                                            </tr> 
                                                                                   
                                        </thead>
                                  </table>
                                </div>
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless">
                                        <thead class="small">                                           
                                            <tr>
                                                <td class="text-end col-4 py-1">Name :</td>
                                                <td class="py-0 col-6">
                                                    <div class="row g-0">
                                                        <div class="col-11">
                                                            <input type="text" @click="changeCustomer" v-model="customer.name" class="form-control-plaintext form-control-sm border-bottom" id="customer_name" placeholder="Name">                                                
                                                        </div>
                                                        <div class="col-1 py-0" v-if="isNewSale">
                                                            <span class="b-inline-block" data-bs-toggle="modal" data-bs-target="#customerModal"><i class="fa-solid fa-pen"></i></span>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>      
                                            <tr>
                                                <td class="text-end py-1" >Phone :</td>
                                                <td class="py-0">
                                                    <input type="tel"   v-model="customer.phone" class="form-control-plaintext form-control-sm border-bottom" id="customer_phone" placeholder="Phone">                                                
                                                </td>
                                            </tr>                                               
                                        </thead>
                                  </table>
                                </div>
                            </div>                               
                              <div class="vh-60 overflow-y-auto">
                                <table class="table table-bordered table-hover mb-0">
                                    <tbody class="small fw-bold">
                                        <tr class="table-secondary">
                                            <td class="col-1 text-center">
                                                <input @paste="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control-plaintext form-control-sm text-center" placeholder="Scan code here">
                                            </td>
                                            <td class="col-2 text-center pt-2" data-bs-toggle="modal" data-bs-target="#searchCode" style="cursor: context-menu;">
                                                <i class="fa-solid fa-circle-plus"></i> Description
                                            </td>  
                                            <td class="col-1 pt-2 text-center">Packing size</td>                                                                                    
                                            <td class="col-1 text-center pt-2">Qty</td>
                                            <td class="col-2 text-center pt-2" colspan="2">Unit price</td>
                                            <td class="col-1 text-center pt-2">Discount</td>
                                            <td class="col-1 text-center pt-2">Amount</td>
                                            <td class="col-1 text-center pt-2">Discount Amount</td>
                                            <td class="col-1 text-center pt-2">Final amount</td>
                                        </tr>    
                                    </tbody>
                                  
                                    <tbody>                                   
                                        <tr class="" v-if="saleItems.length > 0" v-for="(s, i) in saleItems" :key="s.id" @mouseenter="setEdit(i)"  >
                                            <td class="position-relative pt-2 text-center">
                                                <span v-if="isNewSale">
                                                    <a  class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeSaleItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                                </span>
                                                {{s.code_no}}
                                            </td>
                                            <td class="pt-2">{{s.item_name}}</td>   
                                            <td class="pt-2">
                                                {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                            </td>                                        
                                            <td class="pt-2 text-end" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                                {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.sale_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                                            </td>                                            
                                            <td class="p-0">
                                                <input disabled  type="number"  v-model="s.qty_sale_price" class="form-control-plaintext form-control-sm  text-center" placeholder="Price">                                        
                                            </td>
                                            <td class="p-0">
                                                <select @change="calEdit" class="form-control-plaintext form-control-sm text-center" v-model="s.qty_sale_price_unit">
                                                    <option  v-for="pu in onlyString(s.packing_size)">{{pu}}</option>
                                                </select>
                                            </td>
                                            <td class="pt-2 text-center" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                                <span v-if="s.dis_qty">{{s.dis_qty}}{{s.dis_qty_unit}}</span>
                                                <span v-if="s.dis_amount">{{s.dis_amount}}$</span>
                                                <span v-if="s.dis_percent">{{s.dis_percent}}%</span>      
                                            </td>
                                            <td class="pt-2 text-end">{{s.amount}}</td>
                                            <td class="pt-2 text-end">{{s.discount}}</td>
                                            <td class="pt-2 text-end">{{s.final_amount}}</td>
                                        </tr>  
                                        <tr>
                                            <td colspan="9" class="text-end">Total amount</td>
                                            <td class="text-end">{{total_amount}}</td>
                                        </tr>                     
                                        <tr>
                                            <td colspan="9" class="text-end pt-2" >Extra discount</td>
                                            <td class="text-end p-0">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="!isNewSale"  type="number"  @change="calTotal" @keyup="calTotal" v-model="extra_discount" class="form-control-plaintext form-control-sm text-end m-0">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="9" class="text-end">Net amount</td>
                                            <td class="text-end">{{net_amount}}</td>
                                        </tr>
                                        <tr>
                                            <td colspan="9" class="text-end pt-2">Paid</td>
                                            <td class="text-end p-0">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="!isNewSale" @keypress.enter="calPaid" @change="calPaid" @keyup="calPaid" type="number" v-model="paid" class="form-control-plaintext form-control-sm text-end m-0">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="9" class="text-end">Change</td>
                                            <td class="text-end">
                                                {{change}}
                                            </td>
                                        </tr>
                                    </tbody>
                              </table>
                              </div>
                        </div>
                    </div>
                </div>
            </div>
        </div> 
        <ProductModal @setCode="setCode" :sale_type="sale_type" :saleItems="saleItems"/>
        <SalesModal @setCode="setOldCode" />
        <CustomerModal @setCustomer="setCustomer" />
        <!--Pritn item start-->
        <div class="d-none" id="printMe" v-if="saleItems.length > 0" >
                <Print :data="printData" />
        </div>
        <!-- print item end-->

         <!-- pur price Modal -->
         <div class="modal fade" id="setQtyModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2">                       
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" @click="setSaleQty" id="setSaleBtn"  data-bs-dismiss="modal">
                                <span ><i class="fa-solid fa-hard-drive"></i> Save</span>
                            </button>
                        </div>
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered" v-if="Object.keys(editItem).length">
                            <tbody>
                                <tr>
                                    <td colspan="2" class="p-2 text-center">{{editItem.item_name}}</td>
                                </tr>
                                <tr v-if="editItem.packing_size.largest_unit">
                                    <td class="p-0">
                                        <input @keyup="checkEditQty" @keypress.enter="pressSetSaleQty"  type="number" v-model="editItem.sale_l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select  class="form-control-plaintext form-control-sm text-center" v-model="editItem.sale_l_unit">
                                                <option selected>{{editItem.packing_size.largest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input @keyup="checkEditQty" @keypress.enter="pressSetSaleQty" type="number" v-model="editItem.sale_m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.sale_m_unit">
                                                <option selected>{{editItem.packing_size.medium_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input @keyup="checkEditQty" @keypress.enter="pressSetSaleQty" type="number" v-model="editItem.sale_s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.sale_s_unit">
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
        <!--end pur price modal-->  
        <!-- discount price Modal -->
        <div class="modal fade" id="setDiscountModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2 justify-content-end">     
                        <div class="col-4" v-if="Object.keys(editItem).length">
                            <button id="setDiscountBtn" v-if="editItem.amount" type="button" @click="saveDiscount" class="btn btn-sm" :disabled="isLoading" data-bs-dismiss="modal">
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
                                    <td colspan="2" class="p-2 text-center">{{editItem.item_name}}</td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <select @change="calDiscount" class="form-control-plaintext form-control-sm text-center" v-model="editItem.dis_qty_unit">
                                                <option  v-for="pu in onlyString(editItem.packing_size)">{{pu}}</option>
                                        </select>
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetDiscountQty" type="number" @change="calDiscount" @keyup="calDiscount" v-model="editItem.dis_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="pt-2 text-center">
                                        %
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetDiscountQty"  @keyup="calDiscount" @change="calDiscount" type="number" v-model="editItem.dis_percent" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="pt-2 text-center">
                                        $
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetDiscountQty" @keyup="calDiscount" @change="calDiscount" type="number" v-model="editItem.dis_amount" class="form-control-plaintext form-control-sm text-center">
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
import {Timestamp, collection, addDoc, doc, increment,  updateDoc, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
//import Products from "@/views/sales/Products.vue"
import SalesModal from "@/views/modals/sales/SalesModal.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import Print from "@/views/sales/Print.vue"
import ProductModal from "@/views/modals/sales/ProductModal.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/products"
export default {
    components: {
        PageHeader,SalesModal,CustomerModal,Print, ProductModal
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{    
            isNewSale:true,
            isLoading:false,
            sale_type: "retail",
            saleItems:[],
            editItem:{},
            store_item:{},
            voucher_no:"",
            sale_date:"",
            total_amount:0,
            net_amount:0,
            extra_discount:0,
            charges:0,
            paid:0,
            change:0,
            isLimitQty:false,
            customer: {},
            code_no:"",
            products:[],
            customerData: {
                id: 123456669,
                code: 123456669,
                name:"Default Customer",
                phone:"09xxxxxxxxx",
                address :"",
                created_at : Timestamp.fromDate(new Date()),
            },
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
        this.voucher_no=new Date().getTime();
        this.sale_date=new Date().toISOString().substr(0, 10)    
        this.setCustomer(this.customerData)
        this.fetchProducts();
       setTimeout(()=>{
        const code_no=document.getElementById("code_no");
        code_no.focus();
       }, 300)
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
    computed:{
        user(){
            return this.$store.getters.user;
        },
        printData(){
            return{
                voucher_no: this.voucher_no,
                customer: this.customer,
                saleItems: this.saleItems,
                sale_date:this.sale_date,
                total_amount:this.total_amount,
                net_amount:this.net_amount,
                extra_discount: this.extra_discount,
                paid:this.paid,
                change:this.change,
            }
        }
    },
    methods:{
        pressSetSaleQty(){
            const btn=document.getElementById("setSaleBtn");
            btn.click();
        },
        pressSetDiscountQty(){
            const btn=document.getElementById("setDiscountBtn");
            btn.click();
        },
        setCodeOnPaste(){
            setTimeout(()=>{                
            const item=this.products.filter((f)=>f.code_no==this.code_no);
            if(item.length){
                this.setCode(item[0])
                this.code_no="";
            }
            },200)
        },
        async fetchProducts(){
        const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
        const snapShot=await getDocs(query_url)
        const products=ss.products(snapShot)       
        this.products=products;
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
        calDiscount(){
            const e=this.editItem;
            return this.saleItems.map((f)=>{   
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
                           discount=e.retail_price.largest_unit * e.dis_qty
                       }else if(dis_unit=="medium_unit"){
                            discount=e.retail_price.medium_unit * e.dis_qty;
                       }else if(dis_unit=="smallest_unit"){
                            discount=e.retail_price.smallest_unit * e.dis_qty;
                       }    
                       e.discount=discount;                        
                    }
                    else{
                        e.discount=0;
                    }
                }
            })
        },      

        setSaleQty(){           
            const e=this.editItem;
                let sale_qty=0;
                sale_qty += e.sale_l_qty ? e.sale_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                sale_qty += e.sale_m_qty ? e.sale_m_qty * e.packing_size.smallest_qty: 0;
                sale_qty += e.sale_s_qty ? e.sale_s_qty : 0;
               return this.saleItems.map((f)=>{   
                    if(f.id===e.id){
                        f.sale_qty=sale_qty,
                        f.sale_l_qty =e.sale_l_qty ? e.sale_l_qty : 0,
                        f.sale_m_qty= e.sale_m_qty ? e.sale_m_qty : 0,
                        f.sale_s_qty= e.sale_s_qty  ? e.sale_s_qty  : 0
                    }
               }) &&
               e.qty_sale_price ? this.calEdit() : null;
        },
     
        setEdit(i){
            const item=this.saleItems[i];
            this.editItem={...item, 
                sale_l_unit: item.packing_size.largest_unit,
                sale_m_unit: item.packing_size.medium_unit,
                sale_s_unit: item.packing_size.smallest_unit,
                sale_l_qty: item.sale_l_qty ? item.sale_l_qty : 0,
                sale_m_qty: item.sale_m_qty ? item.sale_m_qty : 0,
                sale_s_qty: item.sale_s_qty  ? item.sale_s_qty  : 0
            };
        },
        calEdit(i){
            const oei=this.editItem;
            this.saleItems.map((f)=>{
                if(f.id===oei.id){
                       const arr=Object.entries(f.packing_size)
                        const units=arr.filter((ft)=>{
                            return ft[1]==f.qty_sale_price_unit;
                        })  
                       const sale_unit=units[0][0]     
                       let amount=0;
                       let qty_sale_price=0;
                       if(sale_unit=="largest_unit"){
                            qty_sale_price=this.sale_type=='retail' ? f.retail_price.largest_unit : f.whole_sale_price.largest_unit
                         }else if(sale_unit=="medium_unit"){
                            qty_sale_price=this.sale_type=='retail' ? f.retail_price.medium_unit : f.whole_sale_price.medium_unit
                        }else if(sale_unit=="smallest_unit"){
                            qty_sale_price=this.sale_type=='retail' ? f.retail_price.smallest_unit : f.whole_sale_price.smallest_unit
                        }
                       f.qty_sale_price=qty_sale_price;

                       if(sale_unit=="largest_unit"){
                           amount=Math.floor((f.qty_sale_price / (f.packing_size.medium_qty * f.packing_size.smallest_qty)) * f.sale_qty)
                       }else if(sale_unit=="medium_unit"){
                            amount=Math.floor((f.qty_sale_price / f.packing_size.smallest_qty) * f.sale_qty)
                       }else if(sale_unit=="smallest_unit"){
                            amount=Math.floor(f.qty_sale_price  * f.sale_qty)
                       }    
                        f.amount=amount;
                        f.final_amount=f.discount ? amount - f.discount : amount;

                        /*
                       let smallest_price=0;
                       let medium_price=0;
                       let largest_price=0; 
                       if(sale_unit=="largest_unit"){
                           smallest_price=Math.floor((f.qty_sale_price / (f.packing_size.medium_qty * f.packing_size.smallest_qty)))
                           medium_price=smallest_price * f.packing_size.smallest_qty
                           largest_price=medium_price * f.packing_size.medium_qty
                       }else if(sale_unit=="medium_unit"){
                            smallest_price=Math.floor((f.qty_sale_price / f.packing_size.smallest_qty))
                            medium_price=smallest_price * f.packing_size.smallest_qty
                            largest_price=medium_price * f.packing_size.medium_qty
                       }else if(sale_unit=="smallest_unit"){
                            smallest_price=Math.floor(f.qty_sale_price)
                            medium_price=smallest_price * f.packing_size.smallest_qty
                            largest_price=medium_price * f.packing_size.medium_qty
                       }    

                       const retail_price={
                            largest_unit : largest_price,
                            medium_unit: medium_price,
                            smallest_unit: smallest_price
                       }
                       f.retail_price=retail_price;
                      */
                   
                }
            }) && this.calTotal();
           
        },
        
        calTotal(){
            let total_amount=0;
            
            this.saleItems.map((m)=>{
                total_amount += m.final_amount ? m.final_amount : 0;
            })

            this.total_amount=total_amount;
            this.net_amount=this.extra_discount ? this.total_amount - this.extra_discount : this.total_amount;
           
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
         this.clearSaleItem();
        },
        printSale(){         
           this.print();      
           this.clearSaleItem();
        },
        changeCustomer(){
           this.setCustomer(this.customerData)
        },
        setCustomer(p){
            this.customer.id=p.id;
            this.customer.name=p.name;
            this.customer.code=p.code
            this.customer.phone=p.phone
            this.customer.address=p.address
            this.customer.created_at=p.created_at
        },
        setOldCode(s){
            this.isNewSale=false;
            this.voucher_no=s.voucher_no,
            this.sale_date=this.editShowTime(s.sale_date)
            this.saleItems=s.products;
            this.total_amount=s.total_amount;
            this.net_amount=s.net_amount;
            this.change=s.change;
            this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.customer.name=s.customer.name;
            this.customer.phone=s.customer.phone;
        },
        async saveSale(){                  
        const validattion = await this.v$.$validate()
          if(!validattion) return false;
          if(this.saleItems.length <=0) return false;
            this.isLoading=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                sale_date: Timestamp.fromDate(new Date(this.sale_date)),
                products : this.saleItems,
                user : {uid: this.user.uid, email: this.user.email},
                total_amount:this.total_amount,
                extra_discount:this.extra_discount,
                net_amount: this.net_amount,
                paid: this.paid,
                change:this.change,
                sale_type:this.sale_type,
                customer : this.customer,
            }                  
            const saveDocument=collection(db, "sales")
            await addDoc(saveDocument, voucher)
            this.saleItems.map(async (p)=>{
                const id=p.id;
                const sale_qty=p.sale_qty;               
                const updateDocument=doc(db, "products", id)
                await updateDoc(updateDocument, {
                    qty : increment(-sale_qty)
                 })    
            })
            this.msg.type="success";
            this.msg.info="Sale successful."          
             
            this.isLoading=false;
           
            this.printSale();
            this.v$.$reset()
            this.fetchProducts();
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
                this.isNewSale=true;                
                this.voucher_no=new Date().getTime();
                this.sale_date=new Date().toISOString().substr(0, 10)            
                this.setCustomer(this.customerData)
        },
        removeSaleItem(i){
            this.saleItems.splice(i, 1)
            this.calTotal();
        },
     
        checkQty(p){
            if(p.qty  <1 ){
                this.msg.type="error";
                this.msg.info="Insufficient inventory"
                return false;
            }          
            return true;
        },
        checkEditQty(){    
            let isSufficient=true;
            const e=this.editItem
            let sale_qty=0;
                sale_qty += e.sale_l_qty ? e.sale_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                sale_qty += e.sale_m_qty ? e.sale_m_qty * e.packing_size.smallest_qty: 0;
                sale_qty += e.sale_s_qty ? e.sale_s_qty : 0;

                let bal_qty_largest_unit=Math.floor(e.qty / (e.packing_size.medium_qty * e.packing_size.smallest_qty));
                let bal_qty_medium_unit=Math.floor((e.qty % (e.packing_size.medium_qty * e.packing_size.smallest_qty)) / e.packing_size.smallest_qty);
                let bal_qty_smallest_unit=e.qty;
                
            this.saleItems.filter((f, i)=>{
                if(e.id===f.id){
                    if(sale_qty > e.qty){
                        this.msg.type="error";
                        this.msg.info="Maximum  inventory"
                        e.sale_l_qty = bal_qty_largest_unit ? bal_qty_largest_unit : 0;
                        e.sale_m_qty = bal_qty_largest_unit ? 0 : bal_qty_medium_unit ? bal_qty_medium_unit : 0;
                        e.sale_s_qty = bal_qty_largest_unit || bal_qty_medium_unit ? 0 : bal_qty_smallest_unit ? bal_qty_smallest_unit : 0;
                    }                 
                }
            });
            if(!isSufficient) return false;
            return true;
        },
     
        setCode(p){
            const checkQty=this.checkQty(p);
            if(!checkQty) return false;
           this.store_item ={
                ...p,           
                sale_type: this.sale_type,         
                sale_qty: 0,
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
        storingItem(){          
                this.saleItems.push(this.store_item)
                this.calTotal();        
        },
        updateStoringItem(){
          this.saleItems= this.saleItems.map((f)=>{
                if(f.id ===this.store_item.id){
                    return {...f, sale_qty : f.sale_qty + 1,sale_qty_s : f.sale_qty_s + 1,
                         sale_amount : f.sale_price * (f.sale_qty + 1),
                         pur_amount : f.retail_price * (f.sale_qty + 1),
                        }
                }else{
                    return {...f}
                }
            })
            this.calTotal();
        },
        /*calDiscount(){           
            this.calTotal();
            this.charges=this.total_amount - this.discount;
        },*/
        calPaid(){
            this.change=this.paid - this.net_amount;
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