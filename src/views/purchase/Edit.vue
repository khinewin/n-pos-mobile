<template lang="">
    <div>           
        <PageHeader page-title="Edit purchase" page-code="editPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
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
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="savePurchase">
                        <div class="row justify-content-end">
                            <div class="col-sm-6 col-lg-4">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 pt-2">Voucher no. :</td>
                                            <td class="col-8 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end pt-2">Pur date :</td>
                                            <td class="py-0">
                                                <input type="date" :class="{'border-0 border-bottom border-danger' : v$.pur_date.$errors.length > 0}"  v-model="pur_date" class="form-control-plaintext border-bottom" id="pur_date" placeholder="Date">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end pt-2">Supplier :</td>
                                            <td class="py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.supplier.name.$errors.length > 0}"  v-model="supplier.name" data-bs-toggle="modal" data-bs-target="#searchSupplier"  class="form-control-plaintext border-bottom">                                                
                                            </td>
                                        </tr>
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <button  type="button" :class="{'text-danger':v$.storePurchase.$errors.length > 0}"  class="btn btn-sm"   data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i></button>
                       <div class="table-responsive">
                        <table class="table table-bordered mb-0  table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="" colspan="2">No.</td>
                                    <td class="">Desc</td>
                                    <td class="">Exp. date</td>
                                    <td class="">Qty</td>
                                    <td class="">Item price</td>
                                    <td class="" colspan="3">
                                        Discount
                                    </td>                                   
                                    <td class="">Amount</td>
                                    <td class="">Discount</td>
                                    <td class="">Price</td>
                                </tr>
                                <tr class="small"v-if="storePurchase.length > 0" v-for="(st, i) in storePurchase" :key="st.id" @mouseenter="setEdit(i)"  @keypress.enter="calEdit(i)" @mouseleave="calEdit(i)">
                                    <td class="pt-2">
                                        <a class="text-danger" href="#!" @click="removeStoreItem(i, st)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                    </td>
                                    <td class="pt-2">
                                        {{i+1}}</td>
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="">
                                            <input @change="setExpDate(st.id)"  type="date"  v-model="st.tmp_exp_date" class="form-control-plaintext  text-center"  placeholder="Exp. date">
                                    </td>
                                    <td class="">
                                            <input   type="number"  v-model="st.pur_qty" class="form-control-plaintext  text-center"  placeholder="Qty">
                                    </td>
                                    <td class="">
                                            <input  type="number"  v-model="st.pur_price" class="form-control-plaintext text-center" placeholder="Price">
                                    </td>
                                    <td class="">
                                            <input  type="number"  v-model="st.dis_qty" class="form-control-plaintext text-center" placeholder="0">
                                    </td>
                                    <td class="">
                                            <input  type="number"  v-model="st.dis_percent" class="form-control-plaintext  text-center" placeholder="0">
                                    </td>
                                    <td class="">
                                            <input  type="number"  v-model="st.dis_amount" class="form-control-plaintext text-center" placeholder="0">
                                    </td>
                                    <td class="text-end pt-2">{{st.amount}}</td>
                                    <td class="text-end pt-2">{{st.discount}}</td>
                                    <td class="text-end pt-2">{{st.price}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="11" class="text-end">Amount</td>
                                    <td class="text-end">{{total_amount}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="11" class="text-end">Discount</td>
                                    <td class="text-end ">{{total_discount}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="11" class="text-end pt-2">Extra discount</td>
                                    <td class="text-end">
                                        <input  type="number" @keyup="calTotal"   v-model="extra_discount" class="form-control-plaintext  text-end" placeholder="0">
                                    </td>
                                </tr>
                                <tr class="small">
                                    <td colspan="11" class="text-end">Total price</td>
                                    <td class="text-end">{{total_price}}</td>
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
        <Footer />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment, deleteDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/purchases"

export default {
    components: {
        PageHeader, ProductModal, SupplierModal, Footer
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
            id:"",
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
   },
   watch:{
    msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                        this.msg.info="";
                    }, 10000)
                }
            },deep:true
        }
   },
    
    methods:{
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
        },
        setExpDate(id){
            this.editItem.exp_date=Timestamp.fromDate(new Date(this.editItem.tmp_exp_date))
        },
        setPurchaseItem(p){            
                const pur_date=this.editShowTime(p.pur_date)
                this.voucher_no=p.voucher_no;
                this.pur_date=pur_date//new Date(this.showTime(p.pur_date)).toISOString().substr(0, 10)
                this.supplier=p.supplier;
                this.total_amount=p.total_amount;
                this.total_discount=p.total_discount;
                this.extra_discount=p.extra_discount;
                this.total_price=p.total_price;
                this.storePurchase=p.products;
                this.tmpStorePurchase=p.products;// store for original item;
                this.supplier=p.supplier;
                this.id=p.id;
                this.storePurchase.map((s)=>{
                    return s.old_pur_qty=s.pur_qty;
                })
        },
        async fetchPurchase(){
            const query_url = query(collection(db, "purchases"), where("voucher_no", "==", Number(this.voucher_no)));
            const snapShot=await getDocs(query_url)
            const purchase=ss.purchases(snapShot);      
            this.setPurchaseItem(purchase[0])
            this.isLoading=false;
        },
   
        setSupplier(s){
            this.supplier_name=s.name;
            this.supplier=s;
        },     
     
        setEdit(i){
            const item=this.storePurchase[i];
            this.editItem=item;           
        },
        
        calEdit(i){
            const oei=this.editItem;
           return this.storePurchase.map((f)=>{
                if(f.id===oei.id){
                   f.amount=f.pur_qty * f.pur_price;
                   if(f.dis_qty){                        
                        f.discount=f.dis_qty * f.pur_price;
                        f.price=f.amount - f.discount;
                   }else 
                   if(f.dis_percent){
                        f.discount= f.amount * f.dis_percent / 100;
                        f.price=f.amount - f.discount;
                       
                   }else if(f.dis_amount){
                        f.price= f.amount - f.dis_amount;
                        f.discount=f.dis_amount;
                       
                   }else{
                        f.price=f.amount;
                        f.discount=0;
                   }

                }
            }) && this.calTotal();
           
        },
        calTotal(){
            this.total_amount=this.storePurchase.reduce((accu, cur)=>accu + cur.amount,0)
            this.total_discount=this.storePurchase.reduce((accu, cur)=>accu + cur.discount,0)
            this.total_price=this.storePurchase.reduce((accu, cur)=>accu + cur.price,0)
            if(this.extra_discount){
                this.total_price =this.total_price - this.extra_discount;
            }
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
            this.purchase_item={...p, pur_qty: 0, old_pur_qty: 0,  pur_price: ""};
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
            this.isLoading=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                pur_date: Timestamp.fromDate(new Date(this.pur_date)),
                supplier: this.supplier,
                products : this.storePurchase,
                user : {uid: this.user.uid, email: this.user.email},
                total_amount:this.total_amount,
                total_discount:this.total_discount,
                total_price:this.total_price,
                extra_discount: this.extra_discount,
            }                     
            if(this.storePurchase.length > 0){ //update products qty increase or decrease
                    this.storePurchase.map(async (p)=>{
                    const id=p.id;
                    const pur_qty=p.pur_qty;     
                    const old_pur_qty=p.old_pur_qty;          
                    const updateDocument=doc(db, "products", id)
                    await updateDoc(updateDocument, {                      
                        qty : pur_qty > old_pur_qty ? increment(pur_qty-old_pur_qty) : increment(- (old_pur_qty - pur_qty))
                    })    
                })
                this.storePurchase.map((s)=>{
                    return s.old_pur_qty=s.pur_qty;
                })
            }
            if(this.removeItems.length > 0){ //update products qty for remove items;
                this.removeItems.map(async (r)=>{
                    const updateDocument=doc(db, "products", r.id)
                    await updateDoc(updateDocument, {
                    qty : increment(- r.pur_qty)
                 })  
                })
                this.removeItems=[];
            }      
            const saveDocument=doc(db, "purchases", this.id)
            if(this.storePurchase.length <=0){
                    await deleteDoc(saveDocument) //if no items delete document;
                    this.clearItems();
            }else{
                    await updateDoc(saveDocument, voucher) //save on purchases
            }     
      
            this.msg.type="success";
            this.msg.info="Purchase update successful."          
             
            this.isLoading=false;      
            this.v$.$reset()
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