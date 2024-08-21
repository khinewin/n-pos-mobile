<template lang="">
    <div>           
        <PageHeader page-title="Sale" page-code="sale" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
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
                <div class="col-lg-6">
                        <Products @setCode="setCode" :saleItems="saleItems" />
                </div>
                <div class="col-lg-6">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row justify-content-end mb-2">
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless small">
                                        <thead class="small">
                                            <tr>
                                                <td class="text-end col-4">Voucher no. :</td>
                                                <td class="col-8 py-0">
                                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="text-end">Date :</td>
                                                <td class="py-0">
                                                    <input type="date" :class="{'border-0 border-bottom border-danger' : v$.sale_date.$errors.length > 0}"  v-model="sale_date" class="form-control-plaintext border-bottom" id="sale_date" placeholder="Date">                                                
                                                </td>
                                            </tr> 
                                                                                   
                                        </thead>
                                  </table>
                                </div>
                                <div class="col-sm-6">
                                    <table class="table mb-0 table-borderless small">
                                        <thead class="small">                                           
                                            <tr>
                                                <td class="text-end">Name :</td>
                                                <td class="py-0">
                                                    <div class="row g-0">
                                                        <div class="col-11">
                                                            <input type="text" @click="changeCustomer" v-model="customer.name" class="form-control-plaintext border-bottom" id="customer_name" placeholder="Name">                                                
                                                        </div>
                                                        <div class="col-1 pt-3" v-if="isNewSale">
                                                            <span class="b-inline-block" data-bs-toggle="modal" data-bs-target="#customerModal"><i class="fa-solid fa-pen"></i></span>
                                                        </div>
                                                    </div>
                                                </td>
                                            </tr>      
                                            <tr>
                                                <td class="text-end" >Phone :</td>
                                                <td class="py-0">
                                                    <input type="tel"   v-model="customer.phone" class="form-control-plaintext border-bottom" id="customer_phone" placeholder="Phone">                                                
                                                </td>
                                            </tr>                                               
                                        </thead>
                                  </table>
                                </div>
                            </div>
                              <table class="table table-bordered table-hover mb-0">
                                    <tbody class="small">
                                        <tr class="text-center table-secondary">
                                            <td class="col-2">Code</td>
                                            <td class="col-3">Description</td>
                                            <td class="col-2">Qty</td>
                                            <td class="col-2">Price</td>
                                            <td class="col-2">Amount</td>
                                        </tr>
                                        <tr class="" v-if="saleItems.length > 0" v-for="(s, i) in saleItems" :key="s.id" @mouseenter="setEdit(i)" @mouseleave="saveEdit" >
                                            <td class="position-relative pt-2 text-center">
                                                <span v-if="isNewSale">
                                                    <a  class="text-danger position-absolute top-50 start-0 translate-middle p-2" href="#!" @click="removeSaleItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                                </span>
                                                {{s.code_no}}</td>
                                            <td class="pt-2">{{s.item_name}}</td>
                                            <td class="p-0">
                                                <input :disabled="!isNewSale" :max="s.qty"min="1" @keypress.enter="saveEdit" @change="saveEdit" @keyup="saveEdit"   type="number" class="form-control-plaintext  text-center" v-model="s.sale_qty">
                                            </td>
                                            <td class="pt-2 text-end">{{s.sale_price}}</td>
                                            <td class="pt-2 text-end">{{s.sale_amount}}</td>
                                        </tr>  
                                        <tr>
                                            <td colspan="4" class="text-end">Total amount</td>
                                            <td class="text-end">{{total_amount}}</td>
                                        </tr>                     
                                        <tr>
                                            <td colspan="4" class="text-end pt-2" >Discount</td>
                                            <td class="text-end p-0">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="!isNewSale"  type="number" @keypress.enter="calDiscount"  @change="calDiscount" @keyup="calDiscount" v-model="discount" class="form-control-plaintext text-end m-0">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="4" class="text-end">Charges</td>
                                            <td class="text-end">{{charges}}</td>
                                        </tr>
                                        <tr>
                                            <td colspan="4" class="text-end pt-2">Pay</td>
                                            <td class="text-end p-0">
                                                <input @click="setInput" @mouseleave="setInput" :disabled="!isNewSale" @keypress.enter="calPay" @change="calPay" @keyup="calPay" type="number" v-model="pay" class="form-control-plaintext text-end m-0">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="4" class="text-end">Refunds</td>
                                            <td class="text-end">
                                                {{refunds}}
                                            </td>
                                        </tr>
                                    </tbody>
                              </table>
                        </div>
                    </div>
                </div>
            </div>
        </div> 
        <SalesModal @setCode="setOldCode" />
        <CustomerModal @setCustomer="setCustomer" />
        <!--Pritn item start-->
        <div class="d-none" id="printMe" v-if="saleItems.length > 0" >
                <Print :data="printData" />
        </div>
        <!-- print item end-->
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, doc, increment,  updateDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
import Products from "@/views/sales/Products.vue"
import SalesModal from "@/views/modals/sales/SalesModal.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import Print from "@/views/sales/Print.vue"
export default {
    components: {
        PageHeader, Products,SalesModal,CustomerModal,Print
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
            discount:0,
            charges:0,
            pay:0,
            refunds:0,
            isLimitQty:false,
            customer: {},
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
        this.sale_date=new Date().toISOString().substr(0, 10)    
        this.setCustomer(this.customerData)
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
                discount:this.discount,
                charges:this.charges,
                pay:this.pay,
                refunds:this.refunds,
            }
        }
    },
    methods:{
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
            this.charges=s.charges;
            this.discount=s.discount;
            this.refunds=s.refunds;
            this.sale_type=s.sale_type
            this.pay=s.pay;
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
                discount:this.discount,
                charges:this.charges,
                pay: this.pay,
                refunds:this.refunds,
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
        },
        clearSaleItem(){
                this.editItem={},
                this.saleItems=[],
                this.store_item={},
                this.total_amount=0,
                this.charges=0,
                this.discount=0,
                this.pay=0,
                this.refunds=0
                this.isNewSale=true;                
                this.voucher_no=new Date().getTime();
                this.sale_date=new Date().toISOString().substr(0, 10)            
                this.setCustomer(this.customerData)
        },
        removeSaleItem(i){
            this.saleItems.splice(i, 1)
            this.calTotal();
        },
        setEdit(i){
            this.editItem=this.saleItems[i];
        },
        checkQty(p){
            if(p.qty  <1 || p.retail_price <=0 || p.whole_sale_price <=0){
                this.msg.type="error";
                this.msg.info="Insufficient inventory or empty sale prices"
                return false;
            }
            let isSufficient=true;
            this.saleItems.filter((f)=>{
                if(p.id===f.id){
                    if(p.qty <= f.sale_qty){
                        isSufficient=false
                        this.msg.type="error";
                        this.msg.info="Insufficient inventory"
                    }
                }
            });
            if(!isSufficient) return false;
            return true;
        },
        checkEditQty(p){    
            let isSufficient=true;
            this.saleItems.filter((f, i)=>{
                if(p.id===f.id){
                    if(p.qty <= f.sale_qty-1){                       
                        isSufficient=false                        
                         this.msg.type="error";
                        this.msg.info="It is the maximum amount of stock"         
                        return this.saleItems[i].sale_qty=p.qty;           
                    }
                }
            });
            if(!isSufficient) return false;
            return true;
        },
        saveEdit(){            
            const checkQty=this.checkEditQty(this.editItem);
            if(!checkQty) return false;
            this.saleItems= this.saleItems.map((f)=>{
                if(f.id ===this.editItem.id){
                    return {
                        ...f,  
                        sale_amount : f.sale_price * f.sale_qty,
                        pur_amount : f.pur_price * f.sale_qty
                    }
                }else{
                    return {...f}
                }

            })
            this.calTotal();
        },
        setCode(p){
            const checkQty=this.checkQty(p);
            if(!checkQty) return false;
           this.store_item ={
                ...p,
                sale_qty : 1,
                sale_type: this.sale_type,
                sale_price: this.sale_type==='retail' ? p.retail_price : p.whole_sale_price,
                sale_amount : this.sale_type==='retail' ? p.retail_price : p.whole_sale_price,
                pur_amount : p.pur_price,
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
                    return {...f, sale_qty : f.sale_qty + 1,
                         sale_amount : f.sale_price * (f.sale_qty + 1),
                         pur_amount : f.pur_price * (f.sale_qty + 1),
                        }
                }else{
                    return {...f}
                }
            })
            this.calTotal();
        },
        calDiscount(){           
            this.calTotal();
            this.charges=this.total_amount - this.discount;
        },
        calPay(){
            this.refunds=this.pay - this.charges;
        },
        calTotal(){
            this.total_amount=this.saleItems.reduce((accu, cur)=>accu + cur.sale_amount , 0)
            this.charges=this.total_amount;
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