<template lang="">
    <div>           
        <PageHeader page-title="Purchase  / Edit" page-code="editPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button type="button" @click="savePurchase" class="btn btn-sm" :disabled="isLoading || !voucher_no">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>             
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <router-link :to="{name : 'Purchase'}" class="btn btn-sm" id="close"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+q)</router-link>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="savePurchase">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 fw-bold">Voucher no. :</td>
                                            <td class="col-8">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext p-0  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end fw-bold ">Location :</td>
                                            <td class="">
                                                <select  class="form-control-plaintext p-0 border-bottom" v-model="location" disabled>
                                                    <option value="Main">Main</option>
                                                    <option value="Mart">Mart</option>
                                                </select>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-sm-6 ">
                                <table class="table mb-0 table-borderless">
                                    <tbody>                                      
                                        <tr>
                                            <td class="text-end col-4 fw-bold">Purchase date :</td>
                                            <td class="col-8">
                                                <flat-pickr class="form-control-plaintext  p-0 border-bottom" v-model="pur_date" :config="config" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end fw-bold">Supplier :</td>
                                            <td class="row g-2">
                                                <div class="col-6">
                                                    <input type="text"  @click="changeSupplier" v-model="supplier.name"  class="form-control-plaintext p-0  border-bottom">                                                
                                                </div>
                                                <div class="col-6 border-bottom text-center">
                                                    <span class="d-block text-primary supplier change-cursor" data-bs-toggle="modal" data-bs-target="#searchSupplier" ><i class="fa-solid fa-pen"></i> (ctrl+shift+s)</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                              </table>
                            </div>
                        </div>
                       <div class="vh-50 overflow-y-auto">
                        <table class="table table-bordered mb-0  table-hover inside-table">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2" > Product Name</td>
                                    <!--<td class="col-1 table-info">Exp. Date</td>-->     
                                    <td class="col-1">Packing Size</td>                              
                                    <td class="col-1 table-primary" colspan="1">Qty</td>
                                    <td class="col-2 table-info" colspan="2">Unit Price</td>     
                                    <td class="col-1">Amount</td>                               
                                    <td class="col-1 table-warning" colspan="2">Discount</td>                                    
                                    <td class="col-1">Unit Cost</td>
                                </tr>
                                <tr class="small"v-if="storePurchase.length > 0" v-for="(st, i) in storePurchase" :key="st.id" @mouseenter="setEdit(i)"  >
                                    <td class=" position-relative text-center">
                                        <!--<a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeStoreItem(i, st)"><i class="fa-solid fa-square-minus"></i></a>-->
                                        {{st.code_no}}                                                                                         
                                    </td>                                    
                                    <td class="">{{st.item_name}}</td>
                                    <!--<td class=" position-relative table-info" >
                                        <a v-if="st.tmp_exp_date" class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeExpDate(st)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                        <flat-pickr class="form-control-plaintext py-0 text-center" v-model="st.tmp_exp_date" @change="setExpDate(st.id)"  :config="config" />
                                    </td>-->
                                    <td class="">
                                         {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                   <td class=" text-end table-primary change-cursor" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                   </td>
                                   <td class=" table-info w-10">
                                        <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.qty_pur_price" class="form-control-plaintext py-0  text-center" placeholder="Price">                                        
                                    </td>
                                    <td class=" table-info w-5">
                                        <select @change="calEdit" class="form-control-plaintext py-0  text-center change-cursor" v-model="st.qty_pur_price_unit">
                                            <option  v-for="pu in onlyString(st.packing_size)">{{pu}}</option>
                                        </select>
                                    </td>
                                    <td class="text-end ">{{st.amount}}</td>

                                    <td class="text-center  table-warning change-cursor" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                        <span v-if="st.dis_qty">{{st.dis_qty}}{{st.dis_qty_unit}}</span>
                                            <span v-if="st.dis_amount">{{st.dis_amount}}$</span>
                                            <span v-if="st.dis_percent">{{st.dis_percent}}%</span>                                      
                                    </td>  
                                    <td class="text-end  table-warning change-cursor" data-bs-toggle="modal" data-bs-target="#setDiscountModal">{{st.discount}}</td>
                                    <td class="text-end ">{{st.unit_cost}}</td>
                                </tr>
                                
                            </tbody>
                        </table>
                        </div>    
                        <table class="table table-bordered table-hover">
                            <tbody>
                                <tr class="small">
                                    <td  class="text-end col-3 pe-2 fw-bold">Total</td>
                                    <td class="text-end col-3">{{total}}</td>
                                  
                                    <td  class="text-end pe-2 col-3 fw-bold">Extra discount</td>
                                    <td class="text-end col-3">
                                        <input @click="setInput" @mouseleave="setInput"  type="number" @keyup="calTotal"  v-model="extra_discount" class="form-control-plaintext p-0  text-end" placeholder="0">
                                    </td>
                                </tr>                            
                                <tr class="small">
                                    <td  class="text-end pe-2 fw-bold">Paid</td>
                                    <td class="text-end">
                                        <input @click="setInput" @mouseleave="setInput" @keypress.enter="savePurchase"  type="number" @keyup="calTotal"  v-model="paid" class="form-control-plaintext p-0  text-end" placeholder="0">
                                    </td>
                                    <td  class="text-end pe-2 fw-bold">Balance</td>
                                    <td class="text-end">{{balance}}</td>
                                </tr>
                                <tr class="small">
                                    <td  class="text-end pe-2 fw-bold">Remark</td>
                                    <td class="text-end" colspan="3">
                                        <input @keypress.enter="savePurchase"  type="text"   v-model="remark" class="form-control-plaintext p-0 " placeholder="Remark">
                                    </td>                                   
                                </tr>
                            </tbody>
                        </table>                                                                                        
                </form>
       
          
            <ProductModal @setCode="setCode" :storeItems="storePurchase" />
            <SupplierModal @setCode="setSupplier" />
        </div>
        </div>
        </div>      
            <setQtyModal @setQty="setPurQty" :ei="editItem" />
            <setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment, deleteDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/purchases"
import * as hp  from "@/lib/helper"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import setDiscountModal from "@/views/modals/setDiscountModal.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"

export default {
    components: {
        PageHeader, ProductModal, SupplierModal, Footer, setQtyModal,setDiscountModal,FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                voucher_no: {required},             
                pur_date: {required},
                supplier : {
                    name : {required}
                },
                storePurchase : {required}
            }
    },
    data(){
        return{    
           
            id:"",
            location:"",
            old_location:"",
            tmp_exp_date:"",
            voucher_no:"", 
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
            grand_total:0,
            total:0,
            paid:0,
            balance:0,
            remark:"",
            tmpStorePurchase:[],
            removeItems:[],
            
        
            isLoading:false,
            isDeleteing: false,
            msg : {
                type :"",
                info :"",
            },
          
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
            return this.$store.getters.flat_pickr_config;
        }
      
    },
   mounted() {
    this.voucher_no=this.$route.params.id;
     this.fetchPurchase();
     this.checkDatabase();
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
    
    methods:{
        changeSupplier(){
            this.setSupplier(this.general_supplier)
        },
        handleCtrl(event) {
          
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.savePurchase(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const des=document.getElementById("close")
                des.click();
            }
          return hc.handleCtrl(event)
          
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
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        clearItems(){
            this.id=""
            this.tmp_exp_date=""
            this.voucher_no=""
            this.pur_date=""
            this.supplier.name=""
            this.purchase_item= {}
            this.storePurchase= []
            this.editItem= {}
            this.extra_discount= ""
            this.total_amount=0
            this.total_discount=0
            this.total=0;
            this.paid=0;
            this.balance=0;
            this.remark="";
            this.tmpStorePurchase=[];
            this.removeItems=[];
            this.location="";
        },
        removeExpDate(p){
            return this.storePurchase.filter((f)=>{
                if(p.id==f.id){
                    f.tmp_exp_date="";
                }
            })
        },
        setExpDate(id){
            const arr=this.storePurchase.filter((f)=>f.id==id)
           const p=arr[0]
           const exp_date=hp.insertDateNoTime(p.tmp_exp_date)
           this.editItem.exp_date=p.tmp_exp_date ? Timestamp.fromDate(exp_date)  : null;
           p.exp_date=p.tmp_exp_date ? Timestamp.fromDate(exp_date)  : null;
        },
        setPurchaseItem(p){            
                const pur_date=hp.setOldDate(p.pur_date)
                this.voucher_no=p.voucher_no;
                this.pur_date=pur_date//new Date(this.showTime(p.pur_date)).toISOString().substr(0, 10)
                this.supplier=p.supplier;
                this.total_amount=p.total_amount;
                this.total_discount=p.total_discount;
                this.extra_discount=p.extra_discount;
                this.total=p.total;
                this.grand_total=p.grand_total;
                this.storePurchase=p.products;
                this.tmpStorePurchase=p.products;// store for original item;
                this.supplier=p.supplier;
                this.id=p.id;
                this.location=p.location;
                this.old_location=p.location;
                this.paid=p.paid;
                this.remark=p.remark;
                this.balance=p.balance;
                this.storePurchase.map((s)=>{
                    return s.old_pur_qty=s.pur_qty;
                })
                this.storePurchase.map((s)=>{
                    return s.tmp_exp_date=s.tmp_exp_date ? s.tmp_exp_date : "";
                })
        },
        async fetchPurchase(){
            const query_url = query(collection(db, "purchases"), where("voucher_no", "==", this.voucher_no));
            onSnapshot(query_url, (snapShot)=>{
                    const purchase=ss.purchases(snapShot);      
                    if(purchase.length > 0){
                        this.setPurchaseItem(purchase[0])
                    }
                    this.isLoading=false;
                
            })           
        },
   
        setSupplier(s){
            this.supplier_name=s.name;
            this.supplier=s;
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
                    f.unit_cost =f.amount - e.discount;
                }
            }) && this.calTotal();
        },
       
        setPurQty(){
            const e=this.editItem;
                let pur_qty=0;
                pur_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                pur_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                pur_qty += e.input_s_qty ? e.input_s_qty : 0;
               return this.storePurchase.map((f)=>{   
                    if(f.id===e.id){
                        f.pur_qty=pur_qty,
                        f.input_l_qty =e.input_l_qty ? e.input_l_qty : 0,
                        f.input_m_qty= e.input_m_qty ? e.input_m_qty : 0,
                        f.input_s_qty= e.input_s_qty  ? e.input_s_qty  : 0
                    }
               }) &&
               this.calEdit();
        },
     
        setEdit(i){
            const item=this.storePurchase[i];
            this.editItem={...item,            
                input_l_qty: item.input_l_qty ? item.input_l_qty : 0,
                input_m_qty: item.input_m_qty ? item.input_m_qty : 0,
                input_s_qty: item.input_s_qty  ? item.input_s_qty  : 0
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
                        f.unit_cost=f.discount ? amount - f.discount : amount;

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
                        f.unit_cost=0
                    }
                }
            }) && this.calTotal();
           
        },
        
        calTotal(){
            let total_amount=0;
            let total=0;
            let total_discount=0;
            
            this.storePurchase.map((m)=>{
                total_amount += m.amount ? m.amount : 0;
                total += m.unit_cost ? m.unit_cost : 0;
                total_discount += m.discount ? m.discount : 0;
            })

            this.total_amount=total_amount;
            this.total_discount=total_discount;
            this.total=total;
            this.grand_total=this.extra_discount ? this.total - this.extra_discount : this.total;
            this.balance=(this.total - this.extra_discount) - this.paid;

        },
      

        async removeStoreItem(i, item){      
            this.removeItems.push(item);
            this.storePurchase.splice(i, 1)
            this.calTotal();
        },
        storingPurchase(){           
            this.storePurchase.push(this.purchase_item)   
         },
        setCode(p){           
            this.purchase_item={...p, pur_qty: 0, old_pur_qty: 0};
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

        async checkDatabase(){ 
            const query_url=query(collection(db, "purchases"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {    
                            if(change.type==="modified"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                   // this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message(change.type, "purchase") 
                                    this.isLoading=false;
                                }
                            }
                            if(change.type==="removed"){
                                this.msg.type="success";
                                this.msg.info="The voucher has been cancelled." 
                                this.isLoading=false;
                                this.clearItems();
                            }
                    });                                              
            });                              
        } ,
   
        async savePurchase(){         
            const isNotReady= this.storePurchase.filter((f)=>!f.unit_cost)
            if(isNotReady.length) return false;
             
            this.isLoading=true;
            this.msg.type="";
            this.msg.info="";
        
            this.updateProducts();
           this.updateStoreItem();

           const pur_date=hp.insertDate(this.pur_date)

            const voucher={
                voucher_no: this.voucher_no,
                pur_date: Timestamp.fromDate(pur_date),
                supplier: this.supplier,
                products : this.storePurchase,
                user : {uid: this.user.uid, displayName: this.user.displayName},
                total_amount:this.total_amount,
                total_discount:this.total_discount,
                total:this.total,
                extra_discount: this.extra_discount,
                location: this.location,
                paid:this.paid,
                balance: this.balance,
                remark: this.remark,
            }                        

            const saveDocument=doc(db, "purchases", this.id)
            if(this.storePurchase.length <=0){
                    await deleteDoc(saveDocument) //if no items delete document;
            }else{
                this.isLoading=false;
                  await updateDoc(saveDocument, voucher) //save on purchases
            }                                    
        },
        updateStoreItem(){
            const updateStore=this.storePurchase.filter((f)=>f.pur_qty != 0)
            this.storePurchase=updateStore;
        },
        updateProducts(){
            if(this.storePurchase.length > 0){ //update products qty increase or decrease
                    this.storePurchase.map(async (p)=>{
                    const id=p.id;
                    const pur_qty=p.pur_qty;     
                    const old_pur_qty=p.old_pur_qty;          
                    const updateDocument=doc(db, "products", id)
                    if(this.location==="Mart"){
                        await updateDoc(updateDocument, {                      
                        qty : pur_qty > old_pur_qty ? increment(pur_qty-old_pur_qty) : increment(- (old_pur_qty - pur_qty)),
                        pur_price: p.pur_price,
                    })   
                    } 
                    if(this.location==="Main"){
                        await updateDoc(updateDocument, {                      
                        main_qty : pur_qty > old_pur_qty ? increment(pur_qty-old_pur_qty) : increment(- (old_pur_qty - pur_qty)),
                        pur_price: p.pur_price,
                    })   
                    } 
                })               
            }            
        },
 
        editShowTime(t){
            const date=t.toDate()
            //const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },
        setInput(e){
           if(e.target.value==0){
                e.target.value=""
           }
           if(e.target.value==""){
                e.target.value=0;
           }
        },
                    
    }
}
</script>
<style lang="">
    
</style>