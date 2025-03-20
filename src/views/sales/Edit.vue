<template lang="">
    <div>           
        <PageHeader :page-title="saleMode" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                           
       
                <!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <select class="form-control-plaintext text-center small" @change="changeSaleType" v-model="sale_type">
                        <option value="retail">Retail</option>
                        <option value="whole_sale">Wholesale</option>
                    </select>
                </div>-->
            
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button :disabled="saleItems.length <=0 || saleMode=='Sale / Edit'" class="btn btn-sm"  @click="printSale"><i class="fa-solid fa-print" ></i> Print (cltr+p)</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">                     
                        <button v-if="saleMode =='Sale / Edit'" type="button" @click="saveSale" class="btn btn-sm" :disabled="isLoading || saleItems.length<=0" ><i class="fa-solid fa-hard-drive"></i> Save (cltr+s)
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <router-link :to="{name: 'Sale'}" id="close" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close (cltr+q)</router-link>
                </div>
            </div>
        </div>     
        <div class="container-fluid my-2">
            <div class="row g-1">             
                <div class="col-12">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row justify-content-end mb-2">
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless">
                                        <tbody class="">
                                            <tr class="">
                                                <td class="text-end col-4 fw-bold">Voucher No. :</td>
                                                <td class="col-8">
                                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext   border-bottom py-0" id="voucher_no" placeholder="Voucher no">                                                
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="text-end fw-bold change-cursor" @click="setBarcodeFocus">Barcode :</td>
                                                <td class="">
                                                    <input :disabled="saleMode=='Sale / History'" @keypress.enter="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control-plaintext py-0   " placeholder="Barcode">
                                                </td>
                                            </tr>                            
                                        </tbody>
                                  </table>
                                </div>
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless">
                                        <tbody class="">                                           
                                            <tr>
                                                <td class="text-end col-4 fw-bold">Customer :</td>
                                                <td class="col-8">
                                                    <div class="row g-2">
                                                        <div class="col-6">
                                                            <input type="text" @click="changeCustomer" v-model="customer.name" class="form-control-plaintext py-0 border-bottom" id="customer_name" placeholder="Name">                                                
                                                        </div>
                                                        <div class="col-6 border-bottom text-center">
                                                            <span class="d-block text-primary change-cursor customer" data-bs-toggle="modal" data-bs-target="#customerModal" ><i class="fa-solid fa-pen"></i> (ctrl+shift+c)</span>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>      
                                            <tr>
                                                <td class="text-end fw-bold">Sale Date :</td>
                                                <td class="">
                                                    <flat-pickr class="form-control-plaintext py-0  border-bottom" v-model="sale_date" id="sale_date" :config="config" />
                                                </td>
                                            </tr> 
                                                                                                   
                                        </tbody>
                                  </table>
                                </div>
                            </div>                               
                              <div class="vh-50 overflow-y-auto dataContainer">
                                <table class="table table-bordered table-hover mb-0 ">
                                    <tbody class="small fw-bold table-group-divider">
                                        <tr class="table-secondary">
                                            <td class=" text-center col-0" colspan="2">
                                                No.
                                            </td>
                                            <td class="col-3 text-center change-cursor description" :data-bs-toggle="[saleMode=='Sale / History' ? '' : 'modal']" :data-bs-target="[saleMode=='Sale / History' ? '' : '#searchCode']" style="cursor: context-menu;">
                                                <span v-if="saleMode=='Sale / Edit'"><i class="fa-solid fa-circle-plus"></i></span> Product Name <span v-if="saleMode=='Sale / Edit'">(cltr+i)</span>
                                            </td>  
                                            <!--<td class="col-1  text-center">Packing Size</td>-->                                                                                
                                            <td class="col-2 text-center  table-primary">Qty</td>
                                            <td class="col-2 text-center  table-info">Unit Price</td>
                                            <td class="col-1 text-center ">Amount</td>
                                            <td class="col-2 text-center  table-warning" colspan="2">Discount</td>
                                            <td class="col-1 text-center ">Net Amt</td>
                                        </tr>    
                                    </tbody>                                  
                                    <tbody class="text-nowrap text-break">                                                                         
                                        <tr  v-if="saleItems.length > 0" v-for="(s, i) in saleItems.filter((f)=>!f.is_remove)" :key="s.id" @mouseenter="setEdit(s)"  >
                                            <td class="text-center d-grid">
                                                <button type="button" @click="removeSaleItems(s)" class="btn btn-sm text-danger" :disabled="saleMode=='Sale / History'"><i class="fa-solid fa-square-minus"></i></button>
                                        </td>
                                        <td class="text-center position-relative ">                                                
                                            {{i+1}}
                                        </td>
                                            <td class="">{{s.item_name}}</td>   
                                            <!--<td class=" ">
                                                {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                            </td>-->                                        
                                            <td class=" text-end table-primary d-flex justify-content-between">
                                                <button class="btn btn-sm btn-primary " :disabled="saleMode=='Sale / History'"  @click="decreaseQty(s, s.sale_unit, s.barcode)"><i class="fa-solid fa-minus"></i></button>
                                                <div>
                                                    <input :disabled="saleMode=='Sale / History'" type="number" class="form-control-plaintext py-0 text-center" @keyup="setEdit(s)"  @keypress.enter="calEdit(s)" v-model="s.sale_qty" @change="calEdit(s)">
                                                </div>
                                                <button :disabled="saleMode=='Sale / History'"  class="btn btn-sm btn-primary" @click="increaseQty(s)"><i class="fa-solid fa-plus"></i></button>   
                                            </td>                                            
                                            <td class=" table-info">
                                                <div class="d-flex">
                                                    <input disabled  type="number"  v-model="s.qty_sale_price" class="form-control-plaintext py-0  text-center w-75" placeholder="Price"> 
                                                    <div class="w-25">{{s.qty_sale_price_unit}}</div>
                                                </div>                                       
                                            </td>
                                           
                                            <td class=" text-end">{{s.amount}}</td>
                                            <td class=" text-center table-warning change-cursor" id="setDiscount" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                                <span v-if="s.dis_qty">{{s.dis_qty}}{{s.dis_qty_unit}}</span>
                                                <span v-if="s.dis_amount">Ks {{s.dis_amount}}</span>
                                                <span v-if="s.dis_percent">{{s.dis_percent}}%</span>      
                                            </td>
                                            <td class=" text-end table-warning change-cursor" data-bs-toggle="modal" data-bs-target="#setDiscountModal">{{s.discount}}</td>
                                            <td class="  text-end">{{s.final_amount}}</td>
                                        </tr> 
                                    </tbody>
                                </table>
                                </div>
                                <table class="table table-bordered mb-0 table-hover">
                                    <tbody class="table-group-divider">
                                        <tr>
                                            <td  class="text-end pe-2 fw-bold col-2">Total Amount</td>
                                            <td class="text-end col-2">{{total_amount}}</td>     
                                            <td  class="text-end col-2 pe-2 fw-bold " >Extra Discount</td>
                                            <td class="text-end col-2 ">
                                                <input @click="setInput" @mouseleave="setInput"  type="number"   @change="calTotal" @keyup="calTotal" v-model="extra_discount" class="form-control-plaintext py-0  text-end m-0">
                                            </td>                                   
                                            <td  class="text-end col-2 pe-2 fw-bold">Net Amount</td>
                                            <td class="text-end col-2">{{net_amount}}</td>
                                        </tr>                 
                                        <tr>
                                               
                                            <td  class="text-end pe-2 fw-bold">Paid</td>
                                            <td class="text-end">
                                                <input @click="setInput" @mouseleave="setInput"  @keypress.enter="saveSale" @change="calPaid" @keyup="calPaid" type="number" v-model="paid" class="form-control-plaintext py-0 text-end m-0">
                                            </td>   
                                            <td  class="text-end pe-2 fw-bold">Change</td>
                                            <td class="text-end">
                                                {{change}}
                                            </td>                                    
                                                            
                                            <td  class="text-end pe-2 fw-bold">Credit</td>
                                            <td class="text-end">
                                                {{credit}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td  class="text-end pe-2 fw-bold">Remark</td>
                                            <td class="text-end" colspan="5">
                                                <input :disabled="saleMode=='Sale / History'" @keypress.enter="saveSale" type="text" v-model="remark" class="form-control-plaintext py-0">
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
        <setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />

    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {disableNetwork, Timestamp, collection, addDoc, doc, increment, setDoc, deleteDoc, updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
//import Products from "@/views/sales/Products.vue"
import SalesModal from "@/views/modals/sales/SalesModal.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import Print from "@/views/sales/Print.vue"
import ProductModal from "@/views/modals/sales/ProductModal.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/sales"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setDiscountModal from "@/views/modals/setDiscountModal.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"
import * as pd from "@/lib/snapshot/products"

export default {
    components: {
        PageHeader,SalesModal,CustomerModal,Print, ProductModal,setQtyModal,setDiscountModal, FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{    
         
            saleMode: "Sale / Edit",
            isLoading:false,
            sale_type: "retail",
            saleItems:[],
            editItem:{},
            store_item:{},
            voucher_no:this.$route.params.id,
            id:"",
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
            remark:"",
            
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
        //this.voucher_no=new Date().getTime();
      //  this.sale_date=hp.setToday()
      //  this.print_date=hp.setToday();
     //   this.setCustomer(this.default_customer)
        this.fetchSale();
       setTimeout(()=>{
       // const code_no=document.getElementById("code_no");
        //code_no.focus();
       }, 300)
      // await disableNetwork(db)
     //set print date     
     window.addEventListener('keydown', this.handleCtrl);
     if(this.storeProducts.length){
            this.products=this.storeProducts;
        }
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
        },
        storeProducts:{
            handler(val){
               if(val){
                   this.products=val;
               }
            }, deep: true
       },
    },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        default_customer(){
            return this.$store.getters.default_customer;
        },
        config(){
            return this.$store.getters.flat_pickr_config;
        },
        storeProducts(){
            return this.$store.getters.products;
        },
      
    },
    methods:{   
        setBarcodeFocus(){
            const code_no=document.getElementById("code_no");
            code_no.focus();
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveSale(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const close=document.getElementById("close")
                close.click();
            }
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.printSale(); // Call your custom function
            }
           return hc.handleCtrl(event);
        },
        increaseQty(p){
        const checkQty=this.checkQty(p);
        if(!checkQty) return false;
         let sale_qty=1;         
          let saleItems= this.saleItems.map((f)=>{
                if(f.id ===p.id){
                    return {...f, 
                        sale_qty : f.sale_qty + sale_qty,
                         amount:  f.amount + f.qty_sale_price,
                         final_amount: f.final_amount + f.qty_sale_price,
                        }
                }
                return {
                    ...f
                }               
            })
            this.saleItems=saleItems;
            this.calTotal();
        },
        decreaseQty(p){     
         if(p.sale_qty <=1)return false;      
         let sale_qty=1;         
          let saleItems= this.saleItems.map((f)=>{
                if(f.id ===p.id){
                    return {...f, 
                        sale_qty : f.sale_qty - sale_qty,
                         amount:  f.amount - f.qty_sale_price,
                         final_amount: f.final_amount - f.qty_sale_price,
                        }
                }
                return {
                    ...f
                }               
            })
            this.saleItems=saleItems;
            this.calTotal();
        },
        async setCodeOnPaste(){
            const query_url = query(collection(db, "products"), where("code_no", "==", Number(this.code_no)));
           const unsub= onSnapshot(query_url, async(snapShot) =>{
                    const products=pd.products(snapShot)     
                    this.setCode(products[0])
                    this.code_no="";
                    unsub();
            });              
       } ,
      /*  setCodeOnPaste(){
            setTimeout(()=>{                
            const item=this.products.filter((f)=>f.code_no==this.code_no);
            if(item.length){
                this.setCode(item[0])
                this.code_no="";
            }
            },200)
        },*/
        async fetchSale(){
            const query_url = query(collection(db, "sales"), where("voucher_no", "==",this.voucher_no));
            onSnapshot(query_url, async(snapShot) =>{
                    const sales=ss.sales(snapShot)   
                    if(sales.length<=0) return false;
                   this.setOldCode(sales[0])  
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
       
        setEdit(item){
            this.editItem={...item}
        },
        calEdit(p){
            if(p.sale_qty <1){
                p.sale_qty=1;
            }
            if(p.sale_qty >= p.qty){
                p.sale_qty=p.qty;
                this.msg.info="Maximum Inventary Balance"
                this.msg.type="error"
            }
            
            let sale_qty=p.sale_qty;         
            let saleItems= this.saleItems.map((f)=>{
                    if(f.id ===p.id){
                        return {...f, 
                            sale_qty : sale_qty,
                            amount:  f.sale_qty * f.qty_sale_price,
                            final_amount:f.discount ? (f.sale_qty *  f.qty_sale_price) - f.discount : f.sale_qty * f.qty_sale_price,
                            }
                    }
                    return {
                        ...f
                    }               
                })
            this.saleItems=saleItems;
            this.calTotal();
        },
        
        calTotal(){
            let total_amount=0;
            
            this.saleItems.map((m)=>{
               if(!m.is_remove){
                total_amount += m.final_amount ? m.final_amount : 0;
               }
            })

            this.total_amount=total_amount;
            this.net_amount=this.extra_discount ? this.total_amount - this.extra_discount : this.total_amount;
            this.credit=this.paid ? Math.abs(this.net_amount) - this.paid :  this.net_amount;
            this.calPaid();
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
        },
        printSale(){         
           this.print();      
        },
        
        changeCustomer(){
           this.setCustomer(this.default_customer)
        },
        setCustomer(p){
            this.customer={}
            this.customer=p;
            /*
            this.customer.id=p.id;
            this.customer.name=p.name;
            this.customer.code=p.code
            this.customer.phone=p.phone
            this.customer.address=p.address
            this.customer.created_at=p.created_at
            */
        },
        setOldCode(s){
         //   this.voucher_no=s.voucher_no,
            this.id=s.id;
            this.sale_date=hp.setOldDate(s.sale_date)
            this.print_date=s.sale_date
            this.saleItems=s.products.map((p)=>{
                return {...p, old_sale_qty: p.sale_qty}
            });
            this.total_amount=s.total_amount;
            this.net_amount=s.net_amount;
            this.change=s.change;
            this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.credit=s.credit
            this.remark=s.remark
            this.customer=s.customer;
          
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "sales"), where("voucher_no", "==",this.voucher_no));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="modified"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    //this.updateProducts();
                                  //  this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message("modified", "sale")   
                                    this.isLoading=false;
                                   // this.clearSaleItem(); 
                                }
                            }
                            if(change.type==="removed"){
                                this.msg.type="success";
                                this.msg.info="The voucher has been cancelled." 
                                this.isLoading=false;
                                this.clearSaleItem();
                                setTimeout(()=>{
                                    this.goSale();
                                },2000)
                            }
                    });                                              
            });                              
        } ,
        async saveSale(){                  
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
          
            if(this.customer.code==123456789 && this.credit > 0){
                this.msg.info="Cannot sale with credit for default customer"
                this.msg.type="error"
                return false
            }
            if(this.customer.code == 123456789){
                if(this.customer.name != "Default Customer"){
                    this.msg.info="Please select customer name again"
                    this.msg.type="error"
                    return false
                }
            }
            if(!this.customer.name || !this.customer.code){
                    this.msg.info="Please select customer name again"
                    this.msg.type="error"
                    return false                
            }
            const isNotReady= this.saleItems.filter((f)=>!f.final_amount)
            if(isNotReady.length) {
                this.msg.info="Please check  item amount"
                this.msg.type="error"
               return false;
            }      

              this.isLoading=true;
                this.msg.type="";
                this.msg.info="";

                this.updateProducts();
                this.updateStoreItem();
                
                const sale_date=hp.insertDate(this.sale_date)
                const voucher={
                    voucher_no: this.voucher_no,
                   sale_date: Timestamp.fromDate(sale_date),
                    products : this.saleItems,
                  //  user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                    extra_discount:this.extra_discount ? this.extra_discount : 0,
                    net_amount: this.net_amount ,
                    paid: this.paid ? this.paid : 0,
                    change:this.change,
                    sale_type:this.sale_type,
                    customer : this.customer,
                    credit: this.credit,
                    remark: this.remark,
                    payment_type: this.credit > 0 ? "Credit" : "Paid"
                }                
                this.checkDatabase();           
                const saveDocument=doc(db, "sales", this.id)
                if(this.saleItems.length<=0){
                    await deleteDoc(saveDocument) 
                }else{
                    await updateDoc(saveDocument, voucher)           
                    this.isLoading=false;
                }                
        },
        goSale(){
            this.$router.push({name: "Sale"});
        },
      
        updateStoreItem(){
            const updateStore=this.saleItems.filter((f)=>{
                if(!f.is_remove){
                    return f;
                }
            })
            this.saleItems=updateStore;
        },
        updateProducts(){
            if(this.saleItems.length > 0){ //update products qty increase or decrease
                    this.saleItems.map(async (p)=>{
                    const id=p.id;
                    const sale_qty=p.sale_qty;     
                    const old_sale_qty=p.old_sale_qty;          
                    const updateDocument=doc(db, "products", id)
                        
                    if(p.is_remove){
                        await updateDoc(updateDocument, {                      
                            qty : increment(old_sale_qty)
                        }) 
                    }else{
                        await updateDoc(updateDocument, {                      
                        qty : sale_qty < old_sale_qty ? increment(old_sale_qty - sale_qty) : increment(- (sale_qty - old_sale_qty)),
                     }) 
                    }                                     
                })               
            }            
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
                this.credit=0
                this.voucher_no=new Date().getTime();
                this.sale_date=hp.setToday();      
                this.print_date=hp.setToday();  
                this.setCustomer(this.default_customer)
                this.isLoading=false;
                this.v$.$reset()

        },
      
        removeSaleItems(s){
            this.saleItems=this.saleItems.map((f)=>{
                if(f.id==s.id){
                    return {...f, is_remove: true}
                }else{
                    return f;
                }
            })
            this.calTotal()
        },
     
        checkQty(p){           
            if(p.qty  <1 + p.sale_qty ){
                this.msg.type="error";
                this.msg.info=`There is a maximum balance of "${p.item_name}"`
                return false;
            }          
            return true;
        },
    
     
        setCode(p){
           if(p.qty <=0){
                this.msg.info= `"${p.item_name}" is out of stock`
                this.msg.type="error";
                return false;
           }
           this.store_item ={
                ...p,           
                sale_type: this.sale_type,         
                sale_qty: 1,
                qty_sale_price_unit:p.packing_size.smallest_unit,
                qty_sale_price: p.retail_price.smallest_unit,
                amount: p.retail_price.smallest_unit,
                final_amount: p.retail_price.smallest_unit,
                old_sale_qty: 0
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
        autoScroll(){            
            const container = document.querySelector(".dataContainer");
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth" // Enables smooth scrolling
            });
        
      },
        storingItem(){          
                this.saleItems.push(this.store_item)
                this.calTotal();        
                setTimeout(()=>{
                        this.autoScroll();
                }, 100) 
        },
        updateStoringItem(){
          this.saleItems= this.saleItems.map((f)=>{
                if(f.id ===this.store_item.id){
                    return {...f, 
                        sale_qty : f.sale_qty + 1,
                        amount: f.amount + f.qty_sale_price,
                        final_amount: f.final_amount + f.qty_sale_price,
                        }
                }else{
                    return {...f}
                }
            })
            this.calTotal();
        },
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