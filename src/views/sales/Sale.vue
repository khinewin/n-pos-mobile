<template lang="">
    <div>           
        <PageHeader :page-title="saleMode" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                            
             <!--<div class="col-sm-2 lg-md-2 pt-1 ps-4 small">
                    <div class="form-check form-check-inline" >
                        <input class="form-check-input border-dark" :disabled="saleItems.length > 0" type="radio" name="inlineRadioOptions" id="inlineRadio1" v-model="sale_type" value="retail">
                        <label class="form-check-label" for="inlineRadio1" :class="[sale_type==='retail' ? 'tex-primary' : 'text-dark']">Retail</label>
                      </div>
                      <div class="form-check form-check-inline" >
                        <input class="form-check-input border-dark" :disabled="saleItems.length > 0" type="radio" name="inlineRadioOptions" id="inlineRadio2" v-model="sale_type" value="whole_sale">
                        <label class="form-check-label" for="inlineRadio2" :class="[sale_type==='whole_sale' ? 'tex-primary' : 'text-dark']">Wholesale</label>
                      </div>
                </div>-->
                <div class="col-sm-2 d-grid">
                    <select class="form-control-plaintext text-center" :disabled="saleItems.length > 0" v-model="sale_type">
                        <option value="retail">Retail</option>
                        <option value="whole_sale">Wholesale</option>
                    </select>
                </div>    
                <div class="col-sm-2 d-grid">    
                    <button class="btn btn-sm" @click="goEdit" :disabled="!id || user.uid != sale_user.uid"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
                </div>
                
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#salesModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-sm-2 d-grid">
                    <button :disabled="saleItems.length <=0 || saleMode=='Sale / New'" class="btn btn-sm"  @click="printSale"><i class="fa-solid fa-print" ></i> Print</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button v-if="saleMode=='Sale / New' || saleMode=='Sale / History'" type="button" @click="saveSale" class="btn btn-sm" :disabled="isLoading || saleItems.length <=0 || saleMode=='Sale / History' " ><i class="fa-solid fa-hard-drive"></i> Save
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                        <button v-if="saleMode =='Sale / Edit'" type="button" @click="updateSale" class="btn btn-sm" :disabled="isLoading" ><i class="fa-solid fa-hard-drive"></i> Update
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
                                        <tbody class="">
                                            <tr class="">
                                                <td class="text-end col-4 ">Voucher no. :</td>
                                                <td class="col-8">
                                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext   border-bottom py-0" id="voucher_no" placeholder="Voucher no">                                                
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="text-end">Sale date :</td>
                                                <td class="">
                                                    <flat-pickr class="form-control-plaintext py-0  border-bottom" v-model="sale_date" id="sale_date" :config="config" />
                                                </td>
                                            </tr> 
                                                                                   
                                        </tbody>
                                  </table>
                                </div>
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless">
                                        <tbody class="">                                           
                                            <tr>
                                                <td class="text-end col-4">Customer :</td>
                                                <td class="col-8">
                                                    <div class="row g-2">
                                                        <div class="col-6">
                                                            <input type="text" @click="changeCustomer" v-model="customer.name" class="form-control-plaintext py-0 border-bottom" id="customer_name" placeholder="Name">                                                
                                                        </div>
                                                        <div class="col-6 border-bottom text-center">
                                                            <span class="d-block text-primary" :data-bs-toggle="[saleMode=='Sale / History' ? '' : 'modal']" :data-bs-target="[saleMode=='Sale / History' ? '' : '#customerModal']" ><i class="fa-solid fa-pen"></i></span>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>      
                                            <tr>
                                                <td class="text-end" >Cashier :</td>
                                                <td class="">
                                                    {{sale_user.uid ? sale_user.displayName : user.displayName}}
                                                </td>
                                            </tr>                                               
                                        </tbody>
                                  </table>
                                </div>
                            </div>                               
                              <div class="vh-50 overflow-y-auto">
                                <table class="table table-bordered table-hover mb-0">
                                    <tbody class="small fw-bold table-group-divider">
                                        <tr class="table-secondary">
                                            <td class="col-1 text-center">
                                                <input :disabled="saleMode=='Sale / History'" @paste="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control-plaintext p-0  text-center" placeholder="Scan code here">
                                            </td>
                                            <td id="description" class="col-3 text-center" :data-bs-toggle="[saleMode=='Sale / History' ? '' : 'modal']" :data-bs-target="[saleMode=='Sale / History' ? '' : '#searchCode']" style="cursor: context-menu;">
                                                <i class="fa-solid fa-circle-plus"></i> Description
                                            </td>  
                                            <td class="col-1  text-center">Packing size</td>                                                                                    
                                            <td class="col-1 text-center  table-primary">Qty</td>
                                            <td class="col-2 text-center  table-info" colspan="2">Unit price</td>
                                            <td class="col-1 text-center ">Amount</td>
                                            <td class="col-2 text-center  table-warning" colspan="2">Discount</td>
                                            <td class="col-1 text-center ">Total Amt</td>
                                        </tr>    
                                    </tbody>                                  
                                    <tbody>                                                                         
                                        <tr class="" v-if="saleItems.length > 0" v-for="(s, i) in saleItems" :key="s.id" @mouseenter="setEdit(i)"  >
                                            <td class="position-relative text-center">
                                                <span v-if="saleMode=='Sale / New'">
                                                    <a  class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeSaleItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                                </span>
                                                {{s.code_no}}
                                            </td>
                                            <td class="">{{s.item_name}}</td>   
                                            <td class="">
                                                {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                            </td>                                        
                                            <td class=" text-end table-primary" id="setQty" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                                {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.sale_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                                            </td>                                            
                                            <td class=" table-info col-1">
                                                <input disabled  type="number"  v-model="s.qty_sale_price" class="form-control-plaintext py-0  text-center" placeholder="Price">                                        
                                            </td>
                                            <td class=" table-info col-0">
                                                <select @change="calEdit" class="form-control-plaintext p-0 text-center" v-model="s.qty_sale_price_unit">
                                                    <option   v-for="pu in onlyString(s.packing_size)">{{pu}}</option>
                                                </select>
                                            </td>
                                            <td class=" text-end">{{s.amount}}</td>
                                            <td class=" text-center table-warning" id="setDiscount" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                                <span v-if="s.dis_qty">{{s.dis_qty}}{{s.dis_qty_unit}}</span>
                                                <span v-if="s.dis_amount">{{s.dis_amount}}$</span>
                                                <span v-if="s.dis_percent">{{s.dis_percent}}%</span>      
                                            </td>
                                            <td class=" text-end table-warning" data-bs-toggle="modal" data-bs-target="#setDiscountModal">{{s.discount}}</td>
                                            <td class="text-end">{{s.final_amount}}</td>
                                        </tr> 
                                    </tbody>
                                </table>
                                </div>
                                <table class="table table-bordered">
                                    <tbody class="table-group-divider">
                                        <tr>
                                            <td  class="text-end pe-2 fw-bold col-3">Total amount</td>
                                            <td class="text-end col-3">{{total_amount}}</td>                                        
                                            <td  class="text-end col-3 pe-2 fw-bold">Net amount</td>
                                            <td class="text-end col-3">{{net_amount}}</td>
                                        </tr>                 
                                        <tr>
                                            <td  class="text-end pe-2 fw-bold " >Extra discount</td>
                                            <td class="text-end ">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="saleMode=='Sale / History'"  type="number"   @change="calTotal" @keyup="calTotal" v-model="extra_discount" class="form-control-plaintext py-0  text-end m-0">
                                            </td>    
                                            <td  class="text-end pe-2 fw-bold">Change</td>
                                            <td class="text-end">
                                                {{change}}
                                            </td>                                    
                                          
                                        </tr>
                                        <tr>
                                            <td  class="text-end pe-2 fw-bold">Paid</td>
                                            <td class="text-end">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="saleMode=='Sale / History'" @keypress.enter="saveSale" @change="calPaid" @keyup="calPaid" type="number" v-model="paid" class="form-control-plaintext py-0 text-end m-0">
                                            </td>                               
                                            <td  class="text-end pe-2 fw-bold">Credit</td>
                                            <td class="text-end">
                                                {{credit}}
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
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
        <setQtyModal @setQty="setSaleQty" :ei="editItem" />
        <setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />

    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {disableNetwork, Timestamp, collection, addDoc, doc, increment,  updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
//import Products from "@/views/sales/Products.vue"
import SalesModal from "@/views/modals/sales/SalesModal.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import Print from "@/views/sales/Print.vue"
import ProductModal from "@/views/modals/sales/ProductModal.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/products"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setDiscountModal from "@/views/modals/setDiscountModal.vue";
import FlatPickr from 'vue-flatpickr-component';


export default {
    components: {
        PageHeader,SalesModal,CustomerModal,Print, ProductModal,setQtyModal,setDiscountModal, FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{    
            config: {
                dateFormat: 'd/m/Y', // Flatpickr options
                enableTime: false,
                wrap: true, // set wrap to true only when using 'input-group'
               // altFormat: 'M j, Y',
            },  
            id:"",
            saleMode: "Sale / New",
            isLoading:false,
            sale_type: "retail",
            saleItems:[],
            editItem:{},
            store_item:{},
            voucher_no:"",
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
            sale_user:{},
            customerData: {
                id: 123456789,
                code: 123456789,
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
        this.sale_date=hp.setToday()
        this.print_date=hp.setToday();
        this.setCustomer(this.customerData)
        this.fetchProducts();
       setTimeout(()=>{
        const code_no=document.getElementById("code_no");
        code_no.focus();
       }, 300)
      // await disableNetwork(db)
     this.checkDatabase();
     //set print date     
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
      
    },
    methods:{
    
        goEdit(){           
            this.$router.push({name: "EditSale", params: {id:this.voucher_no}})
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
            onSnapshot(query_url, async(snapShot) =>{
                    const isLocal=snapShot.metadata.fromCache;                  
                    const products=ss.products(snapShot)       
                    this.products=products;                  
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
       
        setSaleQty(){           
            const isCheck=this.checkEditQty();
            const e=this.editItem;
                let sale_qty=0;
                sale_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                sale_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                sale_qty += e.input_s_qty ? e.input_s_qty : 0;                        
                

               return this.saleItems.map((f)=>{   
                    if(f.id===e.id){
                        f.sale_qty=sale_qty,
                        f.input_l_qty =e.input_l_qty ? e.input_l_qty : 0,
                        f.input_m_qty= e.input_m_qty ? e.input_m_qty : 0
                        f.input_s_qty= e.input_s_qty  ? e.input_s_qty  : 0;
                        f.qty_sale_price_unit=e.input_l_qty ? e.packing_size.largest_unit : e.input_m_qty ? e.packing_size.medium_unit : e.packing_size.smallest_unit ;
                    }
               }) &&
                this.calEdit();
        },
     
        setEdit(i){
            const item=this.saleItems[i];
            this.editItem={...item,               
                input_l_qty: item.input_l_qty ? item.input_l_qty : "",
                input_m_qty: item.input_m_qty ? item.input_m_qty : "",
                input_s_qty: item.input_s_qty  ? item.input_s_qty  : ""
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
            this.credit=this.paid ? Math.abs(this.net_amount) - this.paid :  this.net_amount;
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
            this.id=s.id;
            this.saleMode="Sale / History";
            this.voucher_no=s.voucher_no,
            this.sale_date=hp.setOldDate(s.sale_date)
            this.print_date=s.sale_date
            this.saleItems=s.products;
            this.total_amount=s.total_amount;
            this.net_amount=s.net_amount;
            this.change=s.change;
            this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.credit=s.credit
            this.sale_user=s.user;
            this.customer.name=s.customer.name;
            this.customer.phone=s.customer.phone;
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "sales"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                    this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message("added", "sale")   
                                    this.clearSaleItem(); 
                                }
                            }
                    });                                              
            });                              
        } ,
        async saveSale(){                  
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            if(this.saleItems.length <=0) return false;
            if(this.customer.code==123456789 && this.credit > 0) return false;
            const isNotReady= this.saleItems.filter((f)=>!f.final_amount)
            if(isNotReady.length) return false;            

              this.isLoading=true;
                this.msg.type="";
                this.msg.info="";

              /*  if(this.credit > 0){
                    this.saveCredit();
                }
                    */

                const sale_date=hp.insertDate(this.sale_date)

                const voucher={
                    voucher_no: this.voucher_no,
                   sale_date: Timestamp.fromDate(sale_date),
                    products : this.saleItems,
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                    extra_discount:this.extra_discount ? this.extra_discount : 0,
                    net_amount: this.net_amount ,
                    paid: this.paid ? this.paid : 0,
                    change:this.change,
                    sale_type:this.sale_type,
                    customer : this.customer,
                    credit: this.credit,
                    payment_type: this.credit > 0 ? "Credit" : "Paid"
                }                           
                const saveDocument=collection(db, "sales")
                await addDoc(saveDocument, voucher)           
                        
        },
        async saveCredit(){
            let date=new Date();
            let sale_date=new Date(this.sale_date)
            sale_date.setHours(date.getHours())
            sale_date.setMinutes(date.getMinutes())
            const voucher={
                    voucher_no: this.voucher_no,
                    sale_date: Timestamp.fromDate(sale_date),
                    products : this.saleItems,
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                    extra_discount:this.extra_discount,
                    net_amount: this.net_amount,
                    paid: this.paid,
                    change:this.change,
                    sale_type:this.sale_type,
                    customer : this.customer,
                    credit: this.credit,
                }                  
                const saveDocument=collection(db, "creditSales")
                await addDoc(saveDocument, voucher)     
        },
        updateProducts(){
            this.saleItems.map(async (p)=>{
                    const id=p.id;
                    const sale_qty=p.sale_qty;               
                    const updateDocument=doc(db, "products", id)
                    await updateDoc(updateDocument, {
                        qty : increment(-sale_qty)
                    })    
                })
        },
        clearSaleItem(){
                this.sale_user={}
                this.editItem={},
                this.saleItems=[],
                this.store_item={},
                this.total_amount=0,
                this.extra_discount=0,
                this.net_amount=0;
                this.paid=0,
                this.change=0
                this.credit=0
                this.saleMode="Sale / New";                
                this.voucher_no=new Date().getTime();
                this.sale_date=hp.setToday();      
                this.print_date=hp.setToday();  
                this.setCustomer(this.customerData)
                this.isLoading=false;
                this.v$.$reset()

        },
        removeSaleItem(i){
            this.saleItems.splice(i, 1)
            this.calTotal();
        },
     
        checkQty(p){
            if(p.qty  <1 ){
                this.msg.type="error";
                this.msg.info="Insufficient inventory balance"
                return false;
            }          
            return true;
        },
        checkEditQty(){    
            let isSufficient=true;
            const e=this.editItem
            let sale_qty=0;
                sale_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                sale_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                sale_qty += e.input_s_qty ? e.input_s_qty : 0;

                let bal_qty_largest_unit=e.packing_size.largest_unit ? Math.floor(e.qty / (e.packing_size.medium_qty * e.packing_size.smallest_qty)) : 0;
                let bal_qty_medium_unit=e.packing_size.medium_unit ? Math.floor((e.qty % (e.packing_size.medium_qty * e.packing_size.smallest_qty)) / e.packing_size.smallest_qty): 0;
                let bal_qty_smallest_unit=e.qty;
                //console.log(e.input_l_qty)//has a trouble
                    if(sale_qty > e.qty){
                        this.msg.type="error";
                        this.msg.info="Maximum  inventory balance"
                        e.input_l_qty = bal_qty_largest_unit ? bal_qty_largest_unit : 0;
                        e.input_m_qty = bal_qty_largest_unit ? 0 : bal_qty_medium_unit ? bal_qty_medium_unit : 0;
                        e.input_s_qty = bal_qty_largest_unit || bal_qty_medium_unit ? 0 : bal_qty_smallest_unit ? bal_qty_smallest_unit : 0;
                        isSufficient=false;                        
                    }                                       
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