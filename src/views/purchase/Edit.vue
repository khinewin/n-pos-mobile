<template lang="">
    <div>           
        <PageHeader page-title="Edit purchase" page-code="editPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="savePurchase" class="btn btn-sm" :disabled="isLoading || !voucher_no">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>             
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Purchase'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow min-vh-75">
                <div class="card-body p-2">
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
                                            <td class="text-end  py-1">Location :</td>
                                            <td class="">
                                                <select class="form-control-plaintext form-control-sm border-bottom" v-model="location" disabled>
                                                    <option value="Main">Main</option>
                                                    <option value="Pharmacy">Pharmacy</option>
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
                                            <td class="text-end col-6 py-1">Pur date :</td>
                                            <td class="py-0 col-6">
                                                <flat-pickr class="form-control-plaintext form-control-sm border-bottom" v-model="pur_date"  :config="config" />
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
                       <div class="table-responsive">
                        <table class="table table-bordered mb-0  table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2"  data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i> Descrption</td>
                                    <td class="col-1">Exp. date</td>     
                                    <td class="col-1">Packing size</td>                              
                                    <td class="col-1 table-primary" colspan="1">Qty</td>
                                    <td class="col-2 table-info" colspan="2">Unit price</td>                                    
                                    <td class="col-1 table-warning">Discount</td>                                    
                                    <td class="col-1">Amount</td>
                                    <td class="col-1">Discount</td>
                                    <td class="col-1">Price</td>
                                </tr>
                                <tr class="small"v-if="storePurchase.length > 0" v-for="(st, i) in storePurchase" :key="st.id" @mouseenter="setEdit(i)"  >
                                    <td class="pt-2 position-relative text-center">
                                        <!--<a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeStoreItem(i, st)"><i class="fa-solid fa-square-minus"></i></a>-->
                                        {{st.code_no}}                                                                                         
                                    </td>                                    
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="p-0">
                                        <flat-pickr class="form-control-plaintext form-control-sm text-center" v-model="st.tmp_exp_date" @change="setExpDate(st.id)"  :config="config" />
                                    </td>
                                    <td class="pt-2">
                                         {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                   <td class="pt-2 text-end table-primary" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                   </td>
                                   <td class="p-0 table-info">
                                        <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.qty_pur_price" class="form-control-plaintext form-control-sm  text-center" placeholder="Price">                                        
                                    </td>
                                    <td class="p-0 table-info">
                                        <select @change="calEdit" class="form-control-plaintext form-control-sm text-center" v-model="st.qty_pur_price_unit">
                                            <option  v-for="pu in onlyString(st.packing_size)">{{pu}}</option>
                                        </select>
                                    </td>
                                    <td class="text-center pt-2 table-warning" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
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
                                        <input   type="number" @keyup="calTotal"  @change="calTotal"  v-model="extra_discount" class="form-control-plaintext form-control-sm  text-end" placeholder="0">
                                    </td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Grand total price</td>
                                    <td class="text-end">{{grand_total_price}}</td>
                                </tr>
                            </tbody>
                        </table>
                        </div>                                                                                            
                </form>
       
          
            <ProductModal @setCode="setCode" :storePurchase="storePurchase" />
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
                totalAmount:0,
                supplier : {
                    name : {required}
                },
                storePurchase : {required}
            }
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
            grand_total_price:0,
            total_price:0,
            tmpStorePurchase:[],
            removeItems:[],
        
            isLoading:false,
            isDeleteing: false,
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
   created() {
    this.voucher_no=this.$route.params.id;
     this.fetchPurchase();
     this.checkDatabase();
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
            this.total_price=0;
            this.tmpStorePurchase=[];
            this.removeItems=[];
            this.location="";
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
                this.total_price=p.total_price;
                this.grand_total_price=p.grand_total_price;
                this.storePurchase=p.products;
                this.tmpStorePurchase=p.products;// store for original item;
                this.supplier=p.supplier;
                this.id=p.id;
                this.location=p.location;
                this.old_location=p.location;
                this.storePurchase.map((s)=>{
                    return s.old_pur_qty=s.pur_qty;
                })
        },
        async fetchPurchase(){
            const query_url = query(collection(db, "purchases"), where("voucher_no", "==", Number(this.voucher_no)));
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
                    f.price =f.amount - e.discount;
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
                        f.price=0
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
            const isNotReady= this.storePurchase.filter((f)=>!f.price)
            if(isNotReady.length) return false;
             
            this.isLoading=true;
            this.msg.type="";
            this.msg.info="";
        
            this.updateProducts();
           this.updateStoreItem();

           const pur_date=hp.insertDateNoTime(this.pur_date)

            const voucher={
                voucher_no: this.voucher_no,
                pur_date: Timestamp.fromDate(pur_date),
                supplier: this.supplier,
                products : this.storePurchase,
                user : {uid: this.user.uid, displayName: this.user.displayName},
                total_amount:this.total_amount,
                total_discount:this.total_discount,
                total_price:this.total_price,
                extra_discount: this.extra_discount,
                grand_total_price: this.grand_total_price,
                location: this.location
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
                    if(this.location==="Pharmacy"){
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
                    
    }
}
</script>
<style lang="">
    
</style>