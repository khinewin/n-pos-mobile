<template lang="">
    <div>           
        <PageHeader page-title="Transfers / Edit" page-code="editTransfer" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveTransfers" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Transfers'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow min-vh-75">
                <div class="card-body p-2">
                    <form @submit.prevent="saveTransfers">
                        <div class="row justify-content-end">
                            <div class="col-sm-6 col-lg-4">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 pt-2 fw-bold">From location :</td>
                                            <td class="col-8 py-0">
                                                <select class="form-control-plaintext" v-model="from_location" @change="handleFromLocation(from_location)">
                                                    <option value="Store">Store</option>
                                                    <option value="Shop">Shop</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end pt-2 fw-bold">To location :</td>
                                            <td class="py-0">
                                                <select class="form-control-plaintext" v-model="to_location" @change="handleToLocation(to_location)">
                                                    <option value="Store">Store</option>
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
                                            <td class="text-end col-4 pt-2 fw-bold">Voucher no. :</td>
                                            <td class="col-8 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end pt-2 fw-bold">Date :</td>
                                            <td class="py-0">
                                                <input type="date" :class="{'border-0 border-bottom border-danger' : v$.created_at.$errors.length > 0}"  v-model="created_at" class="form-control-plaintext border-bottom" id="created_at" placeholder="Date">                                                
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-2">
                                <button  type="button" :class="{'text-danger':v$.storeItems.$errors.length > 0}"  class="btn btn-sm"   data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i></button>
                            </div>
                            <!--
                            <div class="col-2">
                                <button @click="insertAllitem"  type="button" :class="{'text-danger':v$.storeItems.$errors.length > 0}"  class="btn btn-sm" > 
                                    <i class="fa-solid fa-check-double me-1"></i> 
                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isAddAllLoading"></span>
                                </button>
                            </div>-->
                        </div>
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">From Loc Bal</td> 
                                    <td class="col-1">To Loc Bal</td> 
                                    <td class="col-1">Transfer Bal</td>
                                    <td class="col-1">After From Loc Bal</td> 
                                    <td class="col-1">After To Loc Bal</td> 
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id"  >
                                    <td class="text-center pt-2">
                                        <div class="position-relative">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle p-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                        </div>                                    
                                    </td>
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="pt-2 text-center">{{from_location==="Store" ? st.store_qty : st.qty}}</td>
                                    <td class="pt-2 text-center">{{to_location==="Store" ? st.store_qty : st.qty}}</td>
                                    <td class="text-center p-0">
                                        <input  min="0" :max="from_location==='Store' ? st.store_qty : st.qty"  type="number"  v-model="st.transfer_qty" class="form-control-plaintext  text-center"  placeholder="Qty">
                                    </td>
                                    <td class="pt-2 text-center">{{from_location==="Store" ? st.store_qty - st.transfer_qty : st.qty - st.transfer_qty}}</td>
                                    <td class="pt-2 text-center">{{to_location==="Store" ? st.store_qty + st.transfer_qty : st.qty + st.transfer_qty}}</td>                                                                                              
                                </tr>                                                                                                              
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
            <ProductModal @setCode="setCode" :storePurchase="storeItems" />
        </div>
        </div>
        </div>
        
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import * as ss from "@/lib/snapshot/transfers"
export default {
    components: {
        PageHeader, ProductModal
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                voucher_no: {required},             
                created_at: {required},              
                storeItems : {required}
            }
    },
    data(){
        return{       
            id:"",
            voucher_no:this.$route.params.id, 
            created_at:"",        
            from_location:"",
            to_location:"", 

            transfer_item: {},
            storeItems: [],        
            isLoading:false,
            isSaving:false,
            isAddAllLoading:false,
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
    mounted() {     
        this.fetchTransfers()
    },
    
    methods:{       
        async fetchTransfers(){
            const query_url = query(collection(db, "transfers"), where("voucher_no","==", Number(this.voucher_no)));
            const snapShot=await getDocs(query_url)
            const transfers=ss.transfers(snapShot);      
            this.isLoading=false;
            const item=transfers[0]
            this.from_location=item.from_location
            this.to_location=item.to_location;
            this.id=item.id;
            this.created_at=this.showEditTime(item.created_at)
            this.storeItems=item.products.map((p)=>{
                return {...p, before_edit_transfer_qty: p.transfer_qty}
            });
        } ,
        handleFromLocation(location){
            if(location==="Shop"){
                this.to_location="Store"
            }else{
                this.to_location="Shop"
            }
      },
      handleToLocation(location){
            if(location==="Shop"){
                this.from_location="Store"
            }else{
                this.from_location="Shop"
            }
      },
      
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.transfer_item)   
         },
       
       async saveTransfers(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.isSaving=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(new Date(this.created_at)),           
                products : this.storeItems,
                user : {uid: this.user.uid, email: this.user.email},
                from_location: this.from_location,
                to_location: this.to_location,                     
            }      
            const updateDocument=doc(db, "transfers", this.id)
            await updateDoc(updateDocument, voucher)
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                if(this.from_location==="Store"){
                    await updateDoc(updateDocument, {
                        store_qty : p.before_edit_transfer_qty < p.transfer_item ? increment(- (p.transfer_qty-p.before_edit_transfer_qty)) : increment(p.transfer_qty-p.before_edit_transfer_qty),    
                        qty : p.before_edit_transfer_qty < p.transfer_item ? increment(p.transfer_qty-p.before_edit_transfer_qty) : increment(-(p.transfer_qty-p.before_edit_transfer_qty)),                  
                 })  
                }
                if(this.from_location==="Shop"){
                    await updateDoc(updateDocument, {
                        qty : p.before_edit_transfer_qty < p.transfer_item ? increment(p.transfer_qty-p.before_edit_transfer_qty) : increment(-(p.transfer_qty-p.before_edit_transfer_qty)), 
                        store_qty : p.before_edit_transfer_qty < p.transfer_item ? increment(-(p.transfer_qty-p.before_edit_transfer_qty)) : increment((p.transfer_qty-p.before_edit_transfer_qty)),                          
                 })  
                }
                 
            })
            this.msg.type="success";
            this.msg.info="Transfer items save successful."          
             
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=new Date().toISOString().substr(0, 10)         
            this.storeItems=[]
            this.transfer_item=null         
            this.v$.$reset()
        },
        showEditTime(t){
            const date=t.toDate()
          //  const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },

        /*
        async insertAllitem(){     
            this.isAddAllLoading=true;            
            const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            const snapShot=await getDocs(query_url)           
            if(snapShot._snapshot.docChanges.length <=0){
                this.msg.type='error' 
                this.msg.info='Oops..., something went wrong!'
                this.isLoading=false;
                this.isUpdating=false;
                return false;
            }
            snapShot && snapShot.forEach((doc) => {         
                let product = {
                    id: doc.id,
                    user: doc.data().user,
                    item_name: doc.data().item_name,
                    retail_price : doc.data().retail_price,
                    whole_sale_price: doc.data().whole_sale_price,
                    parent_unit: doc.data().parent_unit,
                    child_unit: doc.data().child_unit,
                    pur_price:doc.data().pur_price,
                    qty : doc.data().qty,
                    created_at : doc.data().created_at,
                    code_no : doc.data().code_no,           
                    item_per_package: doc.data().item_per_package, 
                };                                
                this.setCode(product)                         
                            
            });
            this.isAddAllLoading=false;   
           
       } ,
       
        */
       showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
                    
    }
}
</script>
<style lang="">
    
</style>
