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
                        <div class="row justify-content-end">
                            <div class="col-sm-6 col-lg-4">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 p-2">Voucher no. :</td>
                                            <td class="col-8 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end col-4 p-2">Location :</td>
                                            <td class="col-8">
                                                <select class="form-control-plaintext" v-model="location">
                                                    <option value="Main">Main</option>
                                                    <option value="Shop">Shop</option>
                                                </select>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-sm-6 col-lg-4">
                                <table class="table mb-0 table-borderless">
                                    <tbody>                                       
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
                        <table class="table table-bordered mb-0  table-hover">
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Descrption</td>
                                    <td class="col-1">Exp. date</td>                                   
                                    <td class="col-1">Qty</td>
                                    <td class="col-1">Item price</td>                                    
                                    <td class="col-2" colspan="3">Discount</td>                                    
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
                                        <input @change="setExpDate(st.id)"  type="date"  v-model="st.tmp_exp_date" class="form-control-plaintext text-center"  placeholder="Exp. date">                                   
                                    </td>
                                    <td class="p-0">
                                            <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.pur_qty" class="form-control-plaintext  text-center"  placeholder="Qty">                                      
                                    </td>                                   
                                    <td class="p-0">
                                            <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.pur_price" class="form-control-plaintext  text-center" placeholder="Price">                                        
                                    </td>
                                    <td class="p-0">
                                            <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.dis_qty" class="form-control-plaintext  text-center" placeholder="0">                                       
                                    </td>
                                    <td class="p-0">
                                            <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.dis_percent" class="form-control-plaintext  text-center" placeholder="0">                                       
                                    </td>
                                    <td class="p-0">
                                            <input @keyup="calEdit(i)" @change="calEdit(i)"   type="number"  v-model="st.dis_amount" class="form-control-plaintext text-center" placeholder="0">                                       
                                    </td>
                                    <td class="text-end pt-2">{{st.amount}}</td>
                                    <td class="text-end pt-2">{{st.discount}}</td>
                                    <td class="text-end pt-2">{{st.price}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Amount</td>
                                    <td class="text-end">{{total_amount}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Discount</td>
                                    <td class="text-end ">{{total_discount}}</td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end pt-2">Extra discount</td>
                                    <td class="text-end">
                                        <input  type="number" @keyup="calTotal"  v-model="extra_discount" class="form-control-plaintext  text-end" placeholder="0">
                                    </td>
                                </tr>
                                <tr class="small">
                                    <td colspan="10" class="text-end">Total price</td>
                                    <td class="text-end">{{total_price}}</td>
                                </tr>
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
            <ProductModal @setCode="setCode" :storePurchase="storePurchase" />
            <SupplierModal @setCode="setSupplier" />
        </div>
        </div>
        </div>
        
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import SupplierModal from '@/views/modals/purchase/SupplierModal.vue'
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
    created() {     
      
        this.voucher_no=new Date().getTime();
        this.pur_date=new Date().toISOString().substr(0, 10)
    },
    
    methods:{
        setExpDate(id){
            this.editItem.exp_date=Timestamp.fromDate(new Date(this.editItem.tmp_exp_date))
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
                        f.dis_qty=0;
                        f.dis_amount=0;
                        f.dis_percent=0;
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
        removeStoreItem(i){
            this.storePurchase.splice(i, 1)
        },
        storingPurchase(){           
            this.storePurchase.push(this.purchase_item)   
         },
        setCode(p){           
            this.purchase_item={...p, pur_qty: "", pur_price: ""};
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
                 if(this.location==="Shop"){
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
            this.editItem=null,
            this.total_amount=0,
            this.total_discount=0,
            this.total_price=0,
            this.extra_discount=0
            this.v$.$reset()
        },
           
    }
}
</script>
<style lang="">
    
</style>


  <!--
                                           <ul class="list-gorup list-group-flush">
                        <li class="list-group-item">
                            <div class="row justify-content-end">
                                <div class="col-lg-3">
                                    <div class="text-end mt-2">Voucher no. :</div>
                                </div>
                                <div class="col-lg-3">                                 
                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext border-bottom" id="voucher_no" placeholder="Voucher no">                                        
                                </div>
                            </div>     
                        </li>                       
                        <li class="list-group-item">
                            <div class="row justify-content-end">
                                <div class="col-lg-3">
                                    <div class="text-end mt-2">Date :</div>
                                </div>
                                <div class="col-lg-3">                                
                                    <input type="date" :class="{'border-0 border-bottom border-danger' : v$.pur_date.$errors.length > 0}"  v-model="pur_date" class="form-control-plaintext border-bottom" id="pur_date" placeholder="Date">                                        
                                </div>
                            </div>     
                        </li>
                        <li class="list-group-item">
                            <div class="row justify-content-end">
                                <div class="col-lg-3">                                
                                    <div class="text-end mt-2">Supplier :</div>
                                </div>
                                <div class="col-lg-3">                                
                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.supplier.name.$errors.length > 0}"  v-model="supplier.name" data-bs-toggle="modal" data-bs-target="#searchSupplier"  class="form-control-plaintext border-bottom">                                        
                                    
                                </div>
                            </div>     
                        </li>
                   </ul>              
                -->
                   <!--
                <ul class="list-group ">
        
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold">
                            <div class="col-lg-1" >Code</div>
                            <div class="col-lg-2">Desc</div>
                            <div class="col-lg-1 text-center">Qty</div>
                            <div class="col-lg-2 text-center">Item price</div>
                            <div class="col-lg-2 text-center">
                                <div class="row g-0">
                                    <div class="col-4">Dis qty</div>
                                    <div class="col-4">Dis %</div>
                                    <div class="col-4">Dis $</div>
                                </div>
                            </div>                           
                            <div class="col-lg-1 text-center">Amout</div>
                            <div class="col-lg-1 text-center">Dis</div>
                            <div class="col-lg-2 text-center">Price</div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action" v-if="storePurchase.length > 0" v-for="(st, i) in storePurchase" :key="st.id">
                        <div class="row g-0 small"  @mouseenter="setEdit(i)"  @keypress.enter="calEdit(i)" @mouseleave="calEdit(i)">
                            <div class="col-lg-1" >
                                <a class="text-danger" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>
                                 {{st.code_no}}
                                </div>
                            <div class="col-lg-2">{{st.item_name}}</div>
                            <div class="col-lg-1 text-center">                                
                                <span  v-if="editItem && editItem.code_no ==st.code_no">
                                    <input   type="number"  v-model="editItem.pur_qty" class="form-control-plaintext form-control-sm text-center"  placeholder="Qty">
                                 </span>
                                 <span v-else>{{st.pur_qty}}</span>
                            </div>
                            <div class="col-lg-2 text-center">
                                <span  v-if="editItem && editItem.code_no ==st.code_no">
                                    <input  type="number"  v-model="editItem.pur_price" class="form-control-plaintext form-control-sm text-center" placeholder="Price">
                                 </span>
                                 <span v-else>{{st.pur_price}}</span>
                            </div>
                            <div class="col-lg-2 text-center">
                                <div class="row g-0">
                                    <div class="col-4">
                                        <span  v-if="editItem && editItem.code_no ==st.code_no">
                                            <input  type="number"  v-model="editItem.dis_qty" class="form-control-plaintext form-control-sm text-center" placeholder="Dis qty">
                                         </span>
                                         <span v-else>{{st.dis_qty}}</span>
                                    </div>
                                    <div class="col-4">
                                        <span  v-if="editItem && editItem.code_no ==st.code_no">
                                            <input  type="number"  v-model="editItem.dis_percent" class="form-control-plaintext form-control-sm text-center" placeholder="Dis %">
                                         </span>
                                         <span v-else>{{st.dis_percent}}</span>
                                    </div>
                                    <div class="col-4">
                                        <span  v-if="editItem && editItem.code_no ==st.code_no">
                                            <input  type="number"  v-model="editItem.dis_amount" class="form-control-plaintext form-control-sm text-center" placeholder="Dis $">
                                         </span>
                                         <span v-else>{{st.dis_amount}}</span>
                                    </div>
                                </div>
                            </div>    
                            <div class="col-lg-1 text-center">                                
                                 <span>{{st.amount}}</span>
                            </div>
                            <div class="col-lg-1 text-center">                                
                                <span>{{st.discount}}</span>
                           </div>
                            <div class="col-lg-2 text-center">                                
                                <span>{{st.price}}</span>
                           </div>
                        </div> 
                    </li>
                    
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Amount</div>
                            <div class="col-lg-2 text-center">{{total_amount}}</div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Discount</div>
                            <div class="col-lg-2 text-center">{{total_discount}}</div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Extra discount</div>
                            <div class="col-lg-2 text-center">
                                <input  type="number" @keyup="calTotal"  v-model="extra_discount" class="form-control-plaintext form-control-sm text-center" placeholder="0">
                            </div>
                        </div> 
                    </li>
                    
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Total price</div>
                            <div class="col-lg-2 text-center">{{total_price}}</div>
                        </div> 
                    </li>
                </ul> 
            -->