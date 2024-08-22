<template lang="">
    <div>           
        <PageHeader page-title="Edit opening stocks" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveStocks" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
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
                        <button  type="button" :class="{'text-danger':v$.storeItems.$errors.length > 0}"  class="btn btn-sm"   data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i></button>
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2 text-start">Desc</td>
                                    <td class="col-1">Qty</td>                                   
                                    <td class="col-1">Retail price</td>
                                    <td class="col-1">Wholesale price</td>
                                    <td class="col-1">Pur price</td>
                                    <td class="col-1 text-end">Retail Amt</td>
                                    <td class="col-1 text-end">Wholesale Amt</td>
                                    <td class="col-1 text-end">Pur Amt</td>
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id" @mouseenter="setEdit(i)"  @keypress.enter="calEdit(i)" @mouseleave="calEdit(i)">
                                    <td class="text-center pt-2">
                                            {{st.code_no}}                                           
                                    </td>
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="p-0">
                                            <input   type="number"  v-model="st.qty" class="form-control-plaintext  text-center"  placeholder="Qty">
                                    </td>                                  
                                    <td class="p-0">
                                            <input  type="number"  v-model="st.retail_price" class="form-control-plaintext text-center" placeholder="0">                                     
                                    </td>
                                    <td class="p-0">
                                        <input  type="number"  v-model="st.whole_sale_price" class="form-control-plaintext text-center" placeholder="0">                                        
                                    </td>                                   
                                    <td class="p-0">
                                        <input  type="number"  v-model="st.pur_price" class="form-control-plaintext text-center" placeholder="0">                                        
                                    </td>
                                    <td class="text-end pt-2">                                       
                                        <span>{{st.retail_amount}}</span>
                                   </td>
                                    <td class="text-end pt-2">                                    
                                         <span>{{st.whole_sale_amount}}</span>
                                    </td>                                     
                                    <td class="text-end pt-2">                                    
                                        <span>{{st.pur_amount}}</span>
                                    </td>                                 
                                </tr>
                                <tr class="small">
                                    <td colspan="6" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_retail_amount}}</td>
                                    <td class="text-end">{{total_whole_sale_amount}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import * as ss from "@/lib/snapshot/stocks"

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
            created_at:"",         
            purchase_item: {},
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
       editItem(data){           
       }
    },   
    methods:{
        setExpDate(id){
            this.editItem.exp_date=Timestamp.fromDate(new Date(this.editItem.tmp_exp_date))
        },
        setItem(item){
            this.id=item.id;
            this.voucher_no=item.voucher_no;
            this.created_at=this.showEditTime(item.created_at);
            this.storeItems=item.products;
            this.total_retail_amount=item.total_retail_amount;
            this.total_whole_sale_amount=item.total_whole_sale_amount;
            this.total_pur_amount=item.total_pur_amount;
        },
        async fetchStock(){
            const query_url = query(collection(db, "openingStocks"), where("voucher_no", "==", Number(this.voucher_no)));
            const snapShot=await getDocs(query_url)
            const stocks=ss.stocks(snapShot);
            console.log(stocks)
            this.setItem(stocks[0])
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
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(new Date(this.created_at)),           
                products : this.storeItems,
                user : {uid: this.user.uid, email:this.user.email},
                total_retail_amount:this.total_retail_amount,
                total_whole_sale_amount:this.total_whole_sale_amount,         
                total_pur_amount: this.total_pur_amount,   
            }      
            
            const saveDocument=doc(db, "openingStocks", this.id)
            await setDoc(saveDocument, voucher)
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                await updateDoc(updateDocument, {
                        qty : p.qty,
                        retail_price: p.retail_price,
                        whole_sale_price: p.whole_sale_price,
                 })    
            })
            this.msg.type="success";
            this.msg.info="Opening stocks update successful."         
             
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
