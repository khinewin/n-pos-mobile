<template lang="">
    <div>           
        <PageHeader page-title="Opening stocks" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveStocks" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'OpeningStocks'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks">
                        <div class="row justify-content-end">
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
                            <div class="col-2">
                                <button @click="insertAllitem"  type="button" :class="{'text-danger':v$.storeItems.$errors.length > 0}"  class="btn btn-sm" > 
                                    <i class="fa-solid fa-check-double me-1"></i> 
                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isAddAllLoading"></span>
                                </button>
                            </div>
                        </div>
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Desc</td>
                                    <td class="col-1">Qty</td>                                   
                                    <td class="col-1">Retail price</td>
                                    <td class="col-1">Retail amount</td>
                                    <td class="col-1">Wholesale price</td>
                                    <td class="col-1">Wholesale amount</td>
                                    <td class="col-1">Pur price</td>
                                    <td class="col-1">Pur amount</td>
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id" @mouseenter="setEdit(i)"  @keypress.enter="calEdit(i)" @mouseleave="calEdit(i)">
                                    <td class="text-center">
                                        <div class="position-relative">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-0 start-0 translate-middle p-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                        </div>                                    
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="text-center">
                                            <input   type="number"  v-model="st.qty" class="form-control-plaintext form-control-sm text-center"  placeholder="Qty">
                                    </td>                                  
                                    <td class="">
                                            <input  type="number"  v-model="st.retail_price" class="form-control-plaintext form-control-sm text-center" placeholder="0">                                    
                                    </td>
                                    <td class="text-end">                                       
                                         <span>{{st.retail_amount}}</span>
                                    </td>
                                    <td class="">
                                            <input  type="number"  v-model="st.whole_sale_price" class="form-control-plaintext form-control-sm text-center" placeholder="0">                                        
                                    </td>
                                    <td class="text-end">                                    
                                         <span>{{st.whole_sale_amount}}</span>
                                    </td>
                                    <td class="">
                                            <input  type="number"  v-model="st.pur_price" class="form-control-plaintext form-control-sm text-center" placeholder="0">                                       
                                    </td>
                                    <td class="text-end">                                    
                                         <span>{{st.pur_amount}}</span>
                                    </td>                                 
                                </tr>
                                <tr class="small">
                                    <td colspan="4" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_retail_amount}}</td>
                                    <td  class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_whole_sale_amount}}</td>
                                    <td  class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_pur_amount}}</td>
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
            voucher_no:"", 
            created_at:"",         
            purchase_item: {},
            storeItems: [],
            editItem: {},        
            total_retail_amount:0,
            total_whole_sale_amount:0, 
            total_pur_amount:0,    
            pur_amount:0, 
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
       editItem(data){
           
       }
    },
    created() {     
      
        this.voucher_no=new Date().getTime();
        this.created_at=new Date().toISOString().substr(0, 10)
    },
    
    methods:{
        setExpDate(id){
            this.editItem.exp_date=Timestamp.fromDate(new Date(this.editItem.tmp_exp_date))
        },
     
     
        setEdit(i){
            const item=this.storeItems[i];
            this.editItem=item;
        },       
        calEdit(){
            const oei=this.editItem;
           return this.storeItems.map((f)=>{
                if(f.id===oei.id){
                    f.retail_amount=f.qty * f.retail_price;                  
                    f.whole_sale_amount=f.qty * f.whole_sale_price;
                    f.pur_amount=f.qty * f.pur_price;
                }
            }) && this.calTotal();
                 
        },
        calTotal(){
            this.total_whole_sale_amount=this.storeItems.reduce((accu, cur)=>accu + cur.whole_sale_amount,0)
            this.total_retail_amount=this.storeItems.reduce((accu, cur)=>accu + cur.retail_amount,0)   
            this.total_pur_amount=this.storeItems.reduce((accu, cur)=>accu + cur.pur_amount,0) 
        },
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.purchase_item)   
         },
        setCode(p){           
            this.purchase_item=p;
            const s=this.storeItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }
           
        },
       async saveStocks(){
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
                total_retail_amount:this.total_retail_amount,
                total_whole_sale_amount:this.total_whole_sale_amount,   
                total_pur_amount:this.total_pur_amount,         
            }      
            const saveDocument=collection(db, "openingStocks")
            await addDoc(saveDocument, voucher)
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                await updateDoc(updateDocument, {
                        qty : p.qty,
                        retail_price: p.retail_price,
                        whole_sale_price: p.whole_sale_price,
                        pur_price: p.pur_price,
                 })    
            })
            this.msg.type="success";
            this.msg.info="Opening stocks save successful."          
             
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=new Date().toISOString().substr(0, 10)         
            this.storeItems=[]
            this.purchase_item=null
            this.editItem=null
            this.total_retail_amount=0
            this.total_whole_sale_amount=0
            this.total_pur_amout=0;
            this.v$.$reset()
        },

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
                    uid: doc.data().uid,
                    item_name: doc.data().item_name,
                    retail_price : doc.data().retail_price,
                    whole_sale_price: doc.data().whole_sale_price,
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
