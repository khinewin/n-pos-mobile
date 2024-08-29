<template lang="">
    <div>           
        <PageHeader page-title="Edit adjustment" page-code="editAdjustment" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveStocks" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Adjustments'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks">
                        <div class="row justify-content-end mb-2">
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
                                       
                                            <td class="text-end col-2 pt-2 fw-bold">Date :</td>
                                            <td class="py-0 col-2">
                                                <input type="date" :class="{'border-0 border-bottom border-danger' : v$.created_at.$errors.length > 0}"  v-model="created_at" class="form-control-plaintext border-bottom" id="created_at" placeholder="Date">                                                
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <div class="vh-65 overflow-y-auto">
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
                                            <input disabled min="0" :max="st.qty" @change="calEdit" @keyup="calEdit"  type="number"  v-model="st.adj_qty" class="form-control-plaintext form-control-sm text-center"  placeholder="Qty">
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
                                    <td class="text-end">{{total_whole_sale_amount}}</td>
                                    <td class="text-end">{{total_pur_amount}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import * as ss from "@/lib/snapshot/adjustments"

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
            voucher_no:"", 
            location:"",
            created_at:"",         
            adjustment_item: {},
            storeItems: [],
            editItem: {},        
            total_retail_amount:0,
            total_whole_sale_amount:0,
            total_pur_amount:0,   
            isLoading:false,
            msg : {
                type :"",
                info :"",
            }
        }
    },
    mounted() {
        this.voucher_no=this.$route.params.id;
        this.fetchStock();
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
    methods:{
        setExpDate(id){
            this.editItem.exp_date=Timestamp.fromDate(new Date(this.editItem.tmp_exp_date))
        },
        setItem(item){
            this.id=item.id;
            this.voucher_no=item.voucher_no;
            this.location=item.location;
            this.created_at=this.showEditTime(item.created_at);
            this.storeItems=item.products;
            this.total_retail_amount=item.total_retail_amount;
            this.total_pur_amount=item.total_pur_amount;
            this.total_whole_sale_amount=item.total_whole_sale_amount;
        },
        async fetchStock(){
            const query_url = query(collection(db, "adjustments"), where("voucher_no", "==", Number(this.voucher_no)));
            const snapShot=await getDocs(query_url)
            const adjs=ss.adjustments(snapShot);
            this.setItem(adjs[0])
            this.isLoading=false;
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
            this.storeItems.push(this.adjustment_item)   
         },
        setCode(p){           
            this.adjustment_item=p;
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
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                created_at: Timestamp.fromDate(new Date(this.created_at)),           
                products : this.storeItems,
                user : {uid: this.user.uid, email:this.user.email},
                total_retail_amount:this.total_retail_amount,
                total_pur_amount: this.total_pur_amount,   
            }      
            
            const saveDocument=doc(db, "adjustments", this.id)
            await updateDoc(saveDocument, voucher)
          
            this.msg.type="success";
            this.msg.info="Adjustment update successful."         
             
            this.isLoading=false;
          
            this.v$.$reset()
        },
       
     
        showEditTime(t){
            const date=t.toDate()
          //  const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },
                    
    }
}
</script>
<style lang="">
    
</style>
