<template lang="">
    <div>           
        <PageHeader :page-title="rtInMode" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                           
               
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">    
                    <button class="btn btn-sm" @click="goEdit" :disabled="!id || user.uid != sale_user.uid"><i class="fa-solid fa-pen-to-square"></i> Edit (ctrl+e)</button>
                </div>
                
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm open"  data-bs-toggle="modal" data-bs-target="#returnInModal"><i class="fa-solid fa-folder-tree"></i> Open (ctrl+o)</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button :disabled="rtInItems.length <=0 || rtInMode=='Return In / New'" class="btn btn-sm"  @click="printRtIn"><i class="fa-solid fa-print" ></i> Print (ctrl+p)</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button v-if="rtInMode=='Return In / New' || rtInMode=='Return In / History'" type="button" @click="saveReturnIn" class="btn btn-sm" :disabled="isLoading || rtInItems.length <=0 || rtInMode=='Return In / History' " >
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>                      
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button @click="clearReturnIn" class="btn btn-sm" :disabled="rtInItems.length <=0"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+q)</button>
                </div>
            </div>
        </div>     
        <div class="container-fluid my-2">
            <div class="row g-2">
                <!--<div class="col-lg-2">
                        <Products @setCode="setCode" :rtInItems="rtInItems" />
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
                                                <td class="text-end fw-bold">Return In Date :</td>
                                                <td class="">
                                                    <flat-pickr class="form-control-plaintext py-0  border-bottom" v-model="rt_in_date" id="rt_in_date" :config="config" />
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
                                                        <div class="col-6 border-bottom text-center change-cursor">
                                                            <span class="d-block text-primary customer" :data-bs-toggle="[rtInMode=='Return In / History' ? '' : 'modal']" :data-bs-target="[rtInMode=='Return In / History' ? '' : '#customerModal']" ><i class="fa-solid fa-pen"></i> (ctrl+shift+c)</span>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>      
                                            <tr>
                                                <td class="text-end fw-bold" >User :</td>
                                                <td class="">
                                                    {{sale_user.uid ? sale_user.displayName : user.displayName}}
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
                                                <input :disabled="rtInMode=='Return In / History'" @paste="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control-plaintext p-0  text-center" placeholder="Scan code here">
                                            </td>
                                            <td  class="col-3 text-center change-cursor description" :data-bs-toggle="[rtInMode=='Return In / History' ? '' : 'modal']" :data-bs-target="[rtInMode=='Return In / History' ? '' : '#searchCode']" style="cursor: context-menu;">
                                                <span v-if="rtInMode=='Return In / New'"><i class="fa-solid fa-circle-plus" ></i></span> Description <span v-if="rtInMode=='Return In / New'">(cltr+i)</span>
                                            </td>  
                                            <td class="col-1  text-center">Packing size</td>                                                                                    
                                            <td class="col-1 text-center  table-primary">Qty</td>
                                            <td class="col-2 text-center  table-info" colspan="2">Price</td>
                                            <td class="col-1 text-center ">Amount</td>                                          
                                        </tr>    
                                    </tbody>                                  
                                    <tbody class="text-nowrap text-break">                                                                         
                                        <tr class="" v-if="rtInItems.length > 0" v-for="(s, i) in rtInItems" :key="s.id" @mouseenter="setEdit(s)"  >
                                            <td class="position-relative text-center">
                                                <span v-if="rtInMode=='Return In / New'">
                                                    <a  class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeSaleItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                                </span>
                                                {{s.code_no}}
                                            </td>
                                            <td class="">{{s.item_name}}</td>   
                                            <td class="">
                                                {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                            </td>                                        
                                            <td class=" text-end table-primary change-cursor" id="setQty" :data-bs-toggle="[rtInMode=='Return In / History' ? '' : 'modal']" :data-bs-target="[rtInMode=='Return In / History' ? '' : '#setQtyModal']">
                                                {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.sale_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                                            </td>                                            
                                            <td class=" table-info col-1">
                                                <input @keyup="calEdit"   type="number"  v-model="s.qty_sale_price" class="form-control-plaintext py-0  text-center" placeholder="Price">                                        
                                            </td>
                                            <td class=" table-info col-0">
                                                <select @change="calEdit" class="form-control-plaintext py-0 change-cursor text-center" v-model="s.qty_sale_price_unit">
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
                                            <td  class="text-end pe-2 fw-bold col-3">Paid</td>
                                            <td class="text-end col-3">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="rtInMode=='Return In / History'" @keypress.enter="saveReturnIn" @change="calPaid" @keyup="calPaid" type="number" v-model="paid" class="form-control-plaintext py-0 text-end m-0">
                                            </td>
                                            <td  class="text-end pe-2 fw-bold col-3">Total amount</td>
                                            <td class="text-end col-3">{{total_amount}}</td>                                                                                                                                                                                             
                                        </tr>
                                        <tr>
                                            <td  class="text-end pe-2 fw-bold">Change</td>
                                            <td class="text-end">
                                                {{change}}
                                            </td> 
                                            <td  class="text-end pe-2 fw-bold">Balance</td>
                                            <td class="text-end">
                                                {{balance}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end pe-2 fw-bold">Remark</td>
                                            <td colspan="3">
                                                <input type="text" :disabled="rtInMode=='Return In / History'"  class="form-control-plaintext p-0 px-2" placeholder="Remark" v-model="remark">
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                        </div>
                    </div>
                </div>
            </div>
        </div> 
        <ProductModal @setCode="setCode" :sale_type="sale_type" :saleItems="rtInItems"/>
        <ReturnInModal @setCode="setOldCode" />
        <CustomerModal @setCustomer="setCustomer" />
        <!--Print item start-->
        <div class="d-none" id="printMe" v-if="rtInItems.length > 0" >
                <Print :data="{
                    voucher_no: this.voucher_no,
                    customer: this.customer,
                    rtInItems: this.rtInItems,
                    rt_in_date:this.rt_in_date,
                    print_date: this.print_date,
                    total_amount:this.total_amount,
                    paid:this.paid,
                    change:this.change,
                    user: this.user,     
                    balance: this.balance,    
                    rtInMode: this.rtInMode,   
            }  " />
        </div>       
        <setQtyModal @setQty="setRtInQty" :ei="editItem" />
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
import ReturnInModal from "@/views/modals/sales/ReturnInModal.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import Print from "@/views/returnin/Print.vue"
import ProductModal from "@/views/modals/sales/ProductModal.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/products"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setDiscountModal from "@/views/modals/setDiscountModal.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"

export default {
    components: {
        PageHeader,ReturnInModal,CustomerModal,Print, ProductModal,setQtyModal,setDiscountModal, FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{    
          
            id:"",
            rtInMode: "Return In / New",
            isLoading:false,
            sale_type: "retail",
            rtInItems:[],
            editItem:{},
            store_item:{},
            voucher_no:"",
            rt_in_date:"",
            print_date:"",
            total_amount:0,
         //   net_amount:0,
        //    extra_discount:0,
            balance:0,
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
                rt_in_date: {required}
            }
    },
   
    mounted() {
        this.voucher_no=new Date().getTime();
        this.rt_in_date=hp.setToday()
        this.print_date=hp.setToday();
        this.setCustomer(this.default_customer)
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
        default_customer(){
            return this.$store.getters.default_customer;
        },
        config(){
            return this.$store.getters.flat_pickr_config;
        }
    },
    methods:{
        handleCtrl(event) {
          
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveReturnIn(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.clearReturnIn(); // Call your custom function
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
            if(this.rtInMode=="Return In / New") return false;
            this.$router.push({name: "EditReturnIn", params: {id:this.voucher_no}})
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
    
       
        setRtInQty(){           
         //   const isCheck=this.checkEditQty();
            const e=this.editItem;
                let sale_qty=0;
                sale_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                sale_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                sale_qty += e.input_s_qty ? e.input_s_qty : 0;                                      

               return this.rtInItems.map((f)=>{   
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
     
        setEdit(item){
            this.editItem={...item,               
                input_l_qty: item.input_l_qty ? item.input_l_qty : "",
                input_m_qty: item.input_m_qty ? item.input_m_qty : "",
                input_s_qty: item.input_s_qty  ? item.input_s_qty  : ""
            };
        },
        calEdit(i){
            const oei=this.editItem;
            this.rtInItems.map((f)=>{
                if(f.id===oei.id){
                       const arr=Object.entries(f.packing_size)
                        const units=arr.filter((ft)=>{
                            return ft[1]==f.qty_sale_price_unit;
                        })  
                       const sale_unit=units[0][0]     
                      let qty_sale_price=0;
                       if(sale_unit=="largest_unit"){
                            qty_sale_price=f.whole_sale_price.largest_unit
                         }else if(sale_unit=="medium_unit"){
                            qty_sale_price= f.whole_sale_price.medium_unit
                        }else if(sale_unit=="smallest_unit"){
                            qty_sale_price= f.whole_sale_price.smallest_unit
                        }//get price from retail or wholesale
                      f.qty_sale_price=f.qty_sale_price> 0 ? f.qty_sale_price : qty_sale_price;

                      let amount=0;
                       if(sale_unit=="largest_unit"){
                           amount=Math.floor((f.qty_sale_price / (f.packing_size.medium_qty * f.packing_size.smallest_qty)) * f.sale_qty)
                       }else if(sale_unit=="medium_unit"){
                            amount=Math.floor((f.qty_sale_price / f.packing_size.smallest_qty) * f.sale_qty)
                       }else if(sale_unit=="smallest_unit"){
                            amount=Math.floor(f.qty_sale_price  * f.sale_qty)
                       }    
                        f.amount=amount;
                       // f.final_amount=f.discount ? amount - f.discount : amount;                  
                   
                }
            }) && this.calTotal();
           
        },
        
        calTotal(){
            let total_amount=0;
            
            this.rtInItems.map((m)=>{
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
         this.clearReturnIn();
        },
        printRtIn(){         
           this.print();      
        },
        changeCustomer(){
           this.setCustomer(this.default_customer)
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
            this.rtInMode="Return In / History";
            this.voucher_no=s.voucher_no,
            this.rt_in_date=hp.setOldDate(s.rt_in_date)
            this.print_date=s.rt_in_date
            this.rtInItems=s.products;
            this.total_amount=s.total_amount;
         //   this.net_amount=s.net_amount;
            this.change=s.change;
         //   this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.balance=s.balance
            this.sale_user=s.user;
            this.remark=s.remark;
            this.customer.name=s.customer.name;
            this.customer.phone=s.customer.phone;
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "returnIn"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                  //  this.printRtIn();
                                    this.msg.type="success";
                                    this.msg.info=hp.message("added", "return in")   
                                    this.clearReturnIn(); 
                                }
                            }
                    });                                              
            });                              
        } ,
        async saveReturnIn(){                  
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            if(this.rtInItems.length <=0) return false;
          
            const isNotReady= this.rtInItems.filter((f)=>!f.amount)
            if(isNotReady.length) return false;            

              this.isLoading=true;
                this.msg.type="";
                this.msg.info="";

                const rt_in_date=hp.insertDate(this.rt_in_date)

                const voucher={
                    voucher_no: this.voucher_no,
                   rt_in_date: Timestamp.fromDate(rt_in_date),
                    products : this.rtInItems,
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                  //  extra_discount:this.extra_discount ? this.extra_discount : 0,
                  //  net_amount: this.net_amount ,
                    remark:this.remark,
                    paid: this.paid ? this.paid : 0,
                    change:this.change,
                    customer : this.customer,
                    balance: this.balance,
                    payment_type: this.balance > 0 ? "Credit" : "Paid"
                }                           
                const saveDocument=collection(db, "returnIn")
                await addDoc(saveDocument, voucher)           
                        
        },
    
        updateProducts(){
            this.rtInItems.map(async (p)=>{
                    const id=p.id;
                    const sale_qty=p.sale_qty;               
                    const updateDocument=doc(db, "products", id)
                    await updateDoc(updateDocument, {
                        qty : increment(+sale_qty)
                    })    
                })
        },
        clearReturnIn(){
                this.sale_user={}
                this.editItem={},
                this.rtInItems=[],
                this.store_item={},
                this.total_amount=0,
             //   this.extra_discount=0,
             //   this.net_amount=0;
                this.paid=0,
                this.change=0
                this.balance=0
                this.remark="";
                this.rtInMode="Return In / New";                
                this.voucher_no=new Date().getTime();
                this.rt_in_date=hp.setToday();      
                this.print_date=hp.setToday();  
                this.setCustomer(this.default_customer)
                this.isLoading=false;
                this.v$.$reset()

        },
        removeSaleItem(i){
            this.rtInItems.splice(i, 1)
            this.calTotal();
        },
     
      
     
        setCode(p){
         
           this.store_item ={
                ...p,           
                sale_type: this.sale_type,         
                sale_qty: 0,
           }
           const s=this.rtInItems;
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
                this.rtInItems.push(this.store_item)
                this.calTotal();        
        },
        updateStoringItem(){
          this.rtInItems= this.rtInItems.map((f)=>{
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