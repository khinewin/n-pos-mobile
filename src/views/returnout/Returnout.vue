<template lang="">
    <div>           
        <PageHeader :page-title="rtOutMode"  :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                                          
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">    
                    <button class="btn btn-sm" @click="goEdit" :disabled="!id || user.uid != rt_out_user.uid"><i class="fa-solid fa-pen-to-square"></i> Edit (ctrl+e)</button>
                </div>
                
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm open"  data-bs-toggle="modal" data-bs-target="#returnOutModal"><i class="fa-solid fa-folder-tree"></i> Open (ctrl+o)</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button :disabled="rtOutItems.length <=0 || rtOutMode=='Return Out / New'" class="btn btn-sm"  @click="printRtIn"><i class="fa-solid fa-print" ></i> Print (ctrl+p)</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button v-if="rtOutMode=='Return Out / New' || rtOutMode=='Return Out / History'" type="button" @click="saveReturnOut" class="btn btn-sm" :disabled="isLoading || rtOutItems.length <=0 || rtOutMode=='Return Out / History' " >
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>                      
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button @click="clearReturnOut" class="btn btn-sm" :disabled="rtOutItems.length <=0"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+q)</button>
                </div>
            </div>
        </div>     
        <div class="container-fluid my-2">
            <div class="row g-1">
                <!--<div class="col-lg-2">
                        <Products @setCode="setCode" :rtOutItems="rtOutItems" />
                </div>-->
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
                                                <td class="text-end fw-bold">Return Out Date :</td>
                                                <td class="">
                                                    <flat-pickr class="form-control-plaintext py-0  border-bottom" v-model="rt_out_date" id="rt_out_date" :config="config" />
                                                </td>
                                            </tr> 
                                                                                   
                                        </tbody>
                                  </table>
                                </div>
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless">
                                        <tbody class="">                                           
                                            <tr>
                                                <td class="text-end col-4 fw-bold">Supplier :</td>
                                                <td class="col-8">
                                                    <div class="row g-2">
                                                        <div class="col-6">
                                                            <input type="text" @click="changeSupplier" v-model="supplier.name" class="form-control-plaintext py-0 border-bottom" id="supplier_name" placeholder="Name">                                                
                                                        </div>
                                                        <div class="col-6 border-bottom text-center change-cursor">
                                                            <span class="d-block text-primary supplier " :data-bs-toggle="[rtOutMode=='Return Out / History' ? '' : 'modal']" :data-bs-target="[rtOutMode=='Return Out / History' ? '' : '#searchSupplier']" ><i class="fa-solid fa-pen"></i> (ctrl+shift+s)</span>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>      
                                            <tr>
                                                <td class="text-end fw-bold">Location :</td>
                                                <td class="">
                                                    <select class="form-control-plaintext p-0  change-cursor border-bottom" v-model="location">
                                                        <option value="Main">Main</option>
                                                        <option value="Pharmacy">Pharmacy</option>
                                                    </select>
                                                </td>
                                            </tr>                                          
                                        </tbody>
                                  </table>
                                </div>
                            </div>                               
                              <div class="vh-55 overflow-y-auto">
                                <table class="table table-bordered table-hover mb-0 inside-table">
                                    <tbody class="small fw-bold table-group-divider">
                                        <tr class="table-secondary">
                                            <td class="col-1 text-center">
                                                <input :disabled="rtOutMode=='Return Out / History'" @paste="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control-plaintext p-0  text-center" placeholder="Scan code here">
                                            </td>
                                            <td  class="col-3 text-center change-cursor description" :data-bs-toggle="[rtOutMode=='Return Out / History' ? '' : 'modal']" :data-bs-target="[rtOutMode=='Return Out / History' ? '' : '#searchCode']" style="cursor: context-menu;">
                                                <span v-if="rtOutMode=='Return Out / New'"><i class="fa-solid fa-circle-plus" ></i></span> Description <span v-if="rtOutMode=='Return Out / New'">(cltr+i)</span>
                                            </td>  
                                            <td class="col-1  text-center">Packing Size</td>     
                                            <td class="col-1 text-center table-warning">Exp Date</td>                                                                               
                                            <td class="col-1 text-center  table-primary">Qty</td>
                                            <td class="col-2 text-center  table-info" colspan="2">Pur Price</td>
                                            <td class="col-1 text-center ">Amount</td>                                          
                                        </tr>    
                                    </tbody>                                  
                                    <tbody class="text-nowrap text-break">                                                                         
                                        <tr class="" v-if="rtOutItems.length > 0" v-for="(s, i) in rtOutItems" :key="s.id" @mouseenter="setEdit(s)"  >
                                            <td class="position-relative text-center">
                                                <span v-if="rtOutMode=='Return Out / New'">
                                                    <a  class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeRtOutItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                                </span>
                                                {{s.code_no}}
                                            </td>
                                            <td class="">{{s.item_name}}</td>   
                                            <td class="">
                                                {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                            </td>                    
                                            <td class="table-warning change-cursor position-relative">
                                                <a v-if="rtOutMode=='Return Out / New' && s.tmp_exp_date" class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeExpDate(s)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                                <flat-pickr class="form-control-plaintext  text-center py-0 change-cursor" v-model="s.tmp_exp_date" @change="setExpDate(s.id)"  :config="config" />
                                            </td>                    
                                            <td class=" text-end table-primary change-cursor" id="setQty" :data-bs-toggle="[rtOutMode=='Return Out / History' ? '' : 'modal']" :data-bs-target="[rtOutMode=='Return Out / History' ? '' : '#setQtyModal']">
                                                {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.pur_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                                            </td>                                            
                                            <td class=" table-info col-1">
                                                <input @keyup="calEdit"   type="number"  v-model="s.qty_pur_price" class="form-control-plaintext py-0  text-center" placeholder="Price">                                        
                                            </td>
                                            <td class=" table-info col-0">
                                                <select @change="calEdit" class="form-control-plaintext py-0 change-cursor text-center" v-model="s.qty_pur_price_unit">
                                                    <option   v-for="pu in onlyString(s.packing_size)">{{pu}}</option>
                                                </select>
                                            </td>
                                            <td class=" text-end">{{s.amount}}</td>                                           
                                        </tr> 
                                    </tbody>
                                </table>
                                </div>
                                <table class="table table-bordered table-hover mb-0">
                                    <tbody class="table-group-divider">
                                        <tr>                                           
                                            <td  class="text-end pe-2 fw-bold col-3">Total Amount</td>
                                            <td class="text-end col-3">{{total_amount}}</td>    
                                            <td  class="text-end pe-2 fw-bold col-3">Paid</td>
                                            <td class="text-end col-3">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="rtOutMode=='Return Out / History'" @keypress.enter="saveReturnOut" @change="calPaid" @keyup="calPaid" type="number" v-model="paid" class="form-control-plaintext py-0 text-end m-0">
                                            </td>                                                                                                                                                                                         
                                        </tr>
                                        <tr>                                           
                                            <td  class="text-end pe-2 fw-bold">Balance</td>
                                            <td class="text-end">
                                                {{balance}}
                                            </td>                                        
                                            <td class="text-end pe-2 fw-bold">Remark</td>
                                            <td colspan="3">
                                                <input type="text" :disabled="rtOutMode=='Return Out / History'"  class="form-control-plaintext p-0 px-2" placeholder="Remark" v-model="remark">
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                        </div>
                    </div>
                </div>
            </div>
        </div> 
        <ProductModal @setCode="setCode"  :storeItems="rtOutItems"/>
        <ReturnOutModal @setCode="setOldCode" />
        <SupplierModal @setCode="setSupplier" />
        <!--Print item start-->
        <div class="d-none" id="printMe" v-if="rtOutItems.length > 0" >
                <Print :data="{
                    voucher_no: this.voucher_no,
                    supplier: this.supplier,
                    rtOutItems: this.rtOutItems,
                    rt_out_date:this.rt_out_date,
                    print_date: this.print_date,
                    total_amount:this.total_amount,
                    paid:this.paid,
                   // change:this.change,
                    user: this.user,     
                    balance: this.balance,    
                    rtOutMode: this.rtOutMode,   
            }  " />
        </div>       
        <setQtyModal @setQty="setRtOutQty" :ei="editItem" />
        <!--<setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />-->

    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {disableNetwork, Timestamp, collection, addDoc, doc, increment,  updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
//import Products from "@/views/sales/Products.vue"
import ReturnOutModal from "@/views/modals/purchase/ReturnOutModal.vue"
import SupplierModal from "@/views/modals/purchase/SupplierModal.vue"
import Print from "@/views/returnout/Print.vue"
import ProductModal from "@/views/modals/purchase/ProductModal.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/products"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setDiscountModal from "@/views/modals/setDiscountModal.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"

export default {
    components: {
        PageHeader,ReturnOutModal,SupplierModal,Print, ProductModal,setQtyModal,setDiscountModal, FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{    
          
            id:"",
            rtOutMode: "Return Out / New",
            isLoading:false,
            location: "Pharmacy",
            sale_type: "retail",
            rtOutItems:[],
            editItem:{},
            store_item:{},
            voucher_no:"",
            rt_out_date:"",
            print_date:"",
            total_amount:0,
         //   net_amount:0,
        //    extra_discount:0,
            balance:0,
            paid:0,
            supplier: {},
            code_no:"",
            products:[],
            print_date:"",
            rt_out_user:{},
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
                rt_out_date: {required}
            }
    },
   
    mounted() {
        this.voucher_no=new Date().getTime();
        this.rt_out_date=hp.setToday()
        this.print_date=hp.setToday();
        this.setSupplier(this.general_supplier)
        this.fetchProducts();
       setTimeout(()=>{
        const code_no=document.getElementById("code_no");
        code_no.focus();
       }, 300)
      // await disableNetwork(db)
     this.checkDatabase();
     //set print date     
         window.addEventListener('keydown', this.handleCtrl);
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
        }
    },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        general_supplier(){
            return this.$store.getters.general_supplier;
        },
        config(){
            return this.$store.getters.flat_pickr_config
        }    
    },
    methods:{
        handleCtrl(event) {
          
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveReturnOut(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.clearReturnOut(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.printRtIn(); // Call your custom function
            }        
            if (event.ctrlKey && event.key === 'e') {
                event.preventDefault(); // Prevent the default browser save dialog
                 this.goEdit();
            }
            return hc.handleCtrl(event)
        },
       
        goEdit(){           
            if(this.rtOutMode=="Return Out / New") return false;
            this.$router.push({name: "EditReturnOut", params: {id:this.voucher_no}})
        },      
        removeExpDate(p){
            return this.rtOutItems.filter((f)=>{
                if(p.id==f.id){
                    f.tmp_exp_date="";
                }
            })
        },
        setExpDate(id){
           const arr=this.rtOutItems.filter((f)=>f.id==id)
           const p=arr[0]
           const exp_date=hp.insertDateNoTime(p.tmp_exp_date)
           this.editItem.exp_date=p.tmp_exp_date ? Timestamp.fromDate(exp_date)  : null;
           p.exp_date=p.tmp_exp_date ? Timestamp.fromDate(exp_date)  : null;
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
    
       
        setRtOutQty(){           
         //   const isCheck=this.checkEditQty();
            const e=this.editItem;
                if(this.location=="Main" && e.main_qty <=0) return false;
                if(this.location=="Pharmacy" && e.qty <=0) return false;
                let pur_qty=0;
                pur_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                pur_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                pur_qty += e.input_s_qty ? e.input_s_qty : 0;                                      

               return this.rtOutItems.map((f)=>{   
                    if(f.id===e.id){
                        f.pur_qty=pur_qty,
                        f.input_l_qty =e.input_l_qty ? e.input_l_qty : 0,
                        f.input_m_qty= e.input_m_qty ? e.input_m_qty : 0
                        f.input_s_qty= e.input_s_qty  ? e.input_s_qty  : 0;
                        f.qty_pur_price_unit=e.input_l_qty ? e.packing_size.largest_unit : e.input_m_qty ? e.packing_size.medium_unit : e.packing_size.smallest_unit ;
                    }
               }) &&
                this.calEdit();
        },
     
        setEdit(item){
            this.editItem={...item,               
                input_l_qty: item.input_l_qty ? item.input_l_qty : "",
                input_m_qty: item.input_m_qty ? item.input_m_qty : "",
                input_s_qty: item.input_s_qty  ? item.input_s_qty  : ""
            };
        },
        calEdit(){
            const oei=this.editItem;
            this.rtOutItems.map((f)=>{
                if(f.id===oei.id){
                  //  if(f.qty_pur_price == undefined) return false;
                       const arr=Object.entries(f.packing_size)
                        const units=arr.filter((ft)=>{
                            return ft[1]==f.qty_pur_price_unit;
                        })  
                       const sale_unit=units[0][0]     
                       let amount=0;
                      let qty_pur_price=0;
                       if(sale_unit=="largest_unit"){
                            qty_pur_price=f.pur_price.largest_unit
                         }else if(sale_unit=="medium_unit"){
                            qty_pur_price= f.pur_price.medium_unit
                        }else if(sale_unit=="smallest_unit"){
                            qty_pur_price=f.pur_price.smallest_unit
                        }//get price from retail or wholesale
                       f.qty_pur_price= f.qty_pur_price >0 ? f.qty_pur_price : qty_pur_price;

                       if(sale_unit=="largest_unit"){
                           amount=Math.floor((f.qty_pur_price / (f.packing_size.medium_qty * f.packing_size.smallest_qty)) * f.pur_qty)
                       }else if(sale_unit=="medium_unit"){
                            amount=Math.floor((f.qty_pur_price / f.packing_size.smallest_qty) * f.pur_qty)
                       }else if(sale_unit=="smallest_unit"){
                            amount=Math.floor(f.qty_pur_price  * f.pur_qty)
                       }    
                        f.amount=amount;
                       // f.final_amount=f.discount ? amount - f.discount : amount;                  
                   
                }
            }) && this.calTotal();
           
        },
        
        calTotal(){
            let total_amount=0;
            
            this.rtOutItems.map((m)=>{
                total_amount += m.amount ? m.amount : 0;
            })

            this.total_amount=total_amount;
         //   this.net_amount=this.extra_discount ? this.total_amount - this.extra_discount : this.total_amount;
           this.balance=this.paid ? Math.abs(this.total_amount) - this.paid :  this.total_amount;
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
         this.clearReturnOut();
        },
        printRtIn(){         
           this.print();      
        },
        changeSupplier(){
            this.setSupplier(this.general_supplier)
        },
        setSupplier(s){
            this.supplier_name=s.name;
            this.supplier=s;
        },
        setOldCode(s){
            this.id=s.id;
            this.rtOutMode="Return Out / History";
            this.voucher_no=s.voucher_no,
            this.rt_out_date=hp.setOldDate(s.rt_out_date)
            this.print_date=s.rt_out_date
            this.rtOutItems=s.products;
            this.total_amount=s.total_amount;
         //   this.net_amount=s.net_amount;
         //   this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.balance=s.balance
            this.rt_out_user=s.user;
            this.remark=s.remark;
            this.location=s.location;
           this.setSupplier(s.supplier)
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "returnOut"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                  //  this.printRtIn();
                                    this.msg.type="success";
                                    this.msg.info=hp.message("added", "return out")   
                                    this.clearReturnOut(); 
                                }
                            }
                    });                                              
            });                              
        } ,
        async saveReturnOut(){                
            if(this.rtOutMode=="Return Out / History") return false;  
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            if(this.rtOutItems.length <=0) return false;
          
            const isNotReady= this.rtOutItems.filter((f)=>!f.amount)
            if(isNotReady.length) return false;            

              this.isLoading=true;
                this.msg.type="";
                this.msg.info="";

                const rt_out_date=hp.insertDate(this.rt_out_date)

                const voucher={
                    voucher_no: this.voucher_no,
                   rt_out_date: Timestamp.fromDate(rt_out_date),
                    products : this.rtOutItems,
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                  //  extra_discount:this.extra_discount ? this.extra_discount : 0,
                  //  net_amount: this.net_amount ,
                    remark:this.remark,
                    paid: this.paid ? this.paid : 0,
                    supplier : this.supplier,
                    balance: this.balance,
                    location: this.location,
                   // payment_type: this.balance > 0 ? "Credit" : "Paid"
                }                           
                const saveDocument=collection(db, "returnOut")
                await addDoc(saveDocument, voucher)           
                        
        },
    
        updateProducts(){
            this.rtOutItems.map(async (p)=>{
                    const id=p.id;
                    const pur_qty=p.pur_qty;               
                    const updateDocument=doc(db, "products", id)
                   if(this.location=="Pharmacy"){
                    await updateDoc(updateDocument, {
                        qty : increment(-pur_qty)
                    })  
                   }else if(this.location="Main"){
                    await updateDoc(updateDocument, {
                        main_qty : increment(-pur_qty)
                    })  
                   }
                })
        },
        clearReturnOut(){
                this.rt_out_user={}
                this.editItem={},
                this.rtOutItems=[],
                this.store_item={},
                this.total_amount=0,
             //   this.extra_discount=0,
             //   this.net_amount=0;
                this.paid=0,
                this.change=0
                this.balance=0
                this.remark="";
                this.rtOutMode="Return Out / New";                
                this.voucher_no=new Date().getTime();
                this.rt_out_date=hp.setToday();      
                this.print_date=hp.setToday();  
                this.location="Pharmacy";
                this.setSupplier(this.general_supplier)
                this.isLoading=false;
                this.v$.$reset()

        },
        removeRtOutItem(i){
            this.rtOutItems.splice(i, 1)
            this.calTotal();
        },
     
      
     
        setCode(p){
         
           this.store_item ={
                ...p,           
                //sale_type: this.sale_type,         
                pur_qty: 0,
           }
           const s=this.rtOutItems;
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
                this.rtOutItems.push({...this.store_item, tmp_exp_date:""})
                this.calTotal();        
        },
        updateStoringItem(){
          this.rtOutItems= this.rtOutItems.map((f)=>{           
                    return {...f}            
            })
            this.calTotal();
        },
        /*calDiscount(){           
            this.calTotal();
            this.charges=this.total_amount - this.discount;
        },*/
        calPaid(){
            this.change=this.paid - this.total_amount > 0 ? this.paid - this.total_amount : 0;
            this.balance=this.paid - this.total_amount < 0 ? Math.abs(this.paid - this.total_amount) : 0;
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