<template lang="">
    <div>           
        <PageHeader page-title="New adjustment" page-code="addAdjustment" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveAdjustment" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Adjustments'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="saveAdjustment">
                        <div class="row justify-content-end">
                            <div class="col-sm-10 col-lg-8">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-2 pt-2 fw-bold">Location :</td>
                                            <td class="col-2 py-0">
                                                <select v-model="location" class="form-control-plaintext text-center" :disabled="storeItems.length > 0">
                                                    <option>Main</option>
                                                    <option>Shop</option>
                                                </select>
                                            </td>
                                            <td class="text-end col-2 pt-2 fw-bold">Voucher no. :</td>
                                            <td class="col-2 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>                                       
                                            <td class="col-2 text-end pt-2 fw-bold">Date :</td>
                                            <td class="col-2 py-0">
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
                        </div>
                        <div class="vh-60 overflow-y-auto">
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">Before Adj</td>
                                    <td class="col-1">Adj type</td>
                                    <td class="col-1">Qty</td>      
                                    <td class="col-1">After Adj</td>   
                                    <td class="col-1">RTS/RFS</td>                          
                                    <td class="col-1">Remark</td>   
                                    <td class="col-1">Retail Amt</td>    
                                    <td class="col-1">WS Amt</td> 
                                    <td class="col-1">Pur Amt</td>                                                           
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id"  @mouseenter="setEdit(i)">
                                    <td class="text-center position-relative pt-2">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                    </td>
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="pt-2 text-center">{{st.before_adj_qty}}</td>
                                    <td>
                                        <select v-model="st.adj_type" class="text-center form-control-plaintext" @change="calEdit">
                                            <option>Increment</option>
                                            <option>Decrement</option>
                                        </select>
                                    </td>
                                    <td class="text-center p-0">
                                            <input min="0" :max="[location=='Main' ? st.main_qty : st.qty]" @change="calEdit" @keyup="calEdit"  type="number"  v-model="st.adj_qty" class="form-control-plaintext form-control-sm text-center"  placeholder="Qty">
                                    </td>     
                                    <td class="pt-2 text-center">{{st.after_adj_qty}}</td>      
                                    <td class="p-0">
                                        <select class="form-control-plaintext text-center" v-model="st.rts_rfs">
                                            <option>Yes</option>
                                            <option>No</option>
                                        </select>
                                    </td>                                                         
                                    <td class="p-0">
                                        <input  type="text" v-model="st.remark" class="form-control-plaintext form-control-sm text-center" placeholder="Remark">                                       
                                    </td>              
                                    <td class="text-end pt-2">{{st.retail_amount}}</td> 
                                    <td class="text-end pt-2">{{st.whole_sale_amount}}</td> 
                                    <td class="text-end pt-2">{{st.pur_amount}}</td>                                                                                 
                                </tr>         
                                <tr>
                                    <td colspan="8" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_retail_amount}}</td>
                                    <td class="text-end">{{total_pur_amount}}</td>
                                    <td class="text-end">{{total_whole_sale_amount}}</td>
                                </tr>                                                                                                         
                            </tbody>
                        </table> 
                        </div>                                                                                                                   
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
            location: "Shop",
            adj_item: {},
            storeItems: [],
            editItem: {},          
            total_retail_amount:0,
            total_pur_amount:0,       
            total_whole_sale_amount:0, 
            isLoading:false,
            isSaving:false,
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
        this.created_at=new Date().toISOString().substr(0, 10)
    },
    
    methods:{
        setExpDate(id){
            this.editItem.exp_date=Timestamp.fromDate(new Date(this.editItem.tmp_exp_date))
        },

        calEdit(){
            const oei=this.editItem;
           return this.storeItems.map((f)=>{
                if(f.id===oei.id){
                    if(this.location==="Main"){
                        f.adj_qty=f.adj_type=="Decrement" ? f.adj_qty > f.main_qty ? f.main_qty : f.adj_qty : f.adj_qty;
                    }else{
                        f.adj_qty=f.adj_type=="Decrement" ? f.adj_qty > f.qty ? f.qty : f.adj_qty : f.adj_qty;
                    }
                    f.retail_amount=f.adj_qty * f.retail_price;                  
                    f.pur_amount=f.adj_qty * f.pur_price;
                    f.whole_sale_amount=f.adj_qty * f.whole_sale_price;
                    if(this.location==="Main"){
                        f.after_adj_qty=f.adj_type =="Increment" ? f.main_qty + f.adj_qty : f.main_qty - f.adj_qty;
                    }else{
                        f.after_adj_qty=f.adj_type =="Increment" ? f.qty + f.adj_qty : f.qty - f.adj_qty;
                    }
                }
            }) && this.calTotal();
                 
        },
        calTotal(){
            this.total_retail_amount=this.storeItems.reduce((accu, cur)=>accu + cur.retail_amount,0)   
            this.total_pur_amount=this.storeItems.reduce((accu, cur)=>accu + cur.pur_amount,0) 
            this.total_whole_sale_amount=this.storeItems.reduce((accu, cur)=>accu + cur.whole_sale_amount,0) 

        },     
     
        setEdit(i){
            const item=this.storeItems[i];
            this.editItem=item;
        },            
     
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
            this.calEdit();
        },
        storingItem(){           
            this.storeItems.push(this.adj_item)   
         },
        setCode(p){           
            this.adj_item={...p, before_adj_qty: this.location=='Main' ? p.main_qty : p.qty, adj_qty:0, after_adj_qty : 0, rts_rfs: "No"};
            const s=this.storeItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }
            this.editItem=p;
            this.calEdit();           
        },
       async saveAdjustment(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.isSaving=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(new Date(this.created_at)),    
                location: this.location,       
                products : this.storeItems,
                user : {uid: this.user.uid, email: this.user.email},
                total_retail_amount:this.total_retail_amount,
                total_pur_amount:this.total_pur_amount,         
                total_whole_sale_amount: this.total_whole_sale_amount,
            }      
            const saveDocument=collection(db, "adjustments")
            await addDoc(saveDocument, voucher)
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                if(this.location==="Main"){
                    await updateDoc(updateDocument, {
                        main_qty : p.adj_type=="Decrement" ? increment(- p.adj_qty) : increment(p.adj_qty) ,                       
                 }) 
                }else{
                    await updateDoc(updateDocument, {
                        qty : p.adj_type=="Decrement" ? increment(- p.adj_qty) : increment(p.adj_qty) ,                       
                 }) 
                } 
            })
            this.msg.type="success";
            this.msg.info="Adjustment save successful."          
             
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=new Date().toISOString().substr(0, 10)         
            this.storeItems=[]
            this.adj_item=null
            this.editItem=null
            this.total_retail_amount=0
            this.total_pur_amout=0;
            this.v$.$reset()
        },   
       
        
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
