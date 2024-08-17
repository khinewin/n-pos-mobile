<template lang="">
    <div>           
        <PageHeader page-title="Stocks" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button @click="goEdit" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'NewOpeningStocks'}" class="btn btn-sm"><i class="fa-solid fa-square-plus"></i> New</router-link>
                </div>
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#stocksModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button @click="closeOpeningStock" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks">
                        <div class="row justify-content-end mb-2">
                            <div class="col-lg-4">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 fw-bold">Voucher no. :</td>
                                            <td class="col-8">
                                                {{voucher_no}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end fw-bold">Date :</td>
                                            <td class="">
                                               <span v-if="created_at"> {{showTime(created_at)}}</span>
                                            </td>
                                        </tr>
                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <table class="table table-bordered mb-0  table-hover">
                            <tbody>
                                <tr class="small text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Desc</td>
                                    <td class="col-1" colspan="2">Qty</td>                                   
                                    <td class="col-1">Retail price</td>
                                    <td class="col-1">Retail amount</td>
                                    <td class="col-1">Wholesale price</td>
                                    <td class="col-1">Wholesale amount</td>
                                    <td class="col-1">Pur price</td>
                                    <td class="col-1">Pur amount</td>
                                </tr>
                                <tr class="small text-center"v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id">
                                    <td class="">
                                        <div class="">
                                            {{st.code_no}}                                           
                                        </div>                                    
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="">                                      
                                         <span>{{st.qty}}</span>
                                    </td>
                                    <td class="">
                                        {{Math.floor(st.qty / st.item_per_package)}} x   {{Math.floor(st.qty % st.item_per_package)}}
                                    </td>
                                    <td class="">                                      
                                         <span>{{st.retail_price}}</span>
                                    </td>
                                    <td class="">                                      
                                        <span>{{st.retail_amount}}</span>
                                   </td>
                                    <td class="">                                      
                                         <span>{{st.whole_sale_price}}</span>
                                    </td>
                                    <td class="">                                      
                                        <span>{{st.whole_sale_amount}}</span>
                                   </td>
                                   <td class="">                                      
                                    <span>{{st.pur_price}}</span>
                               </td>
                               <td class="">                                      
                                   <span>{{st.pur_amount}}</span>
                              </td>
                                 
                                </tr>
                                <tr class="small">
                                    <td colspan="5" class="text-end fw-bold">Total</td>
                                    <td class="text-center">{{total_retail_amount}}</td>
                                    <td class="text-end fw-bold">Total</td>
                                    <td class="text-center">{{total_whole_sale_amount}}</td>
                                    <td class="text-end fw-bold">Total</td>
                                    <td class="text-center">{{total_pur_amount}}</td>
                                </tr>                                                                                         
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
            <StocksModal @setCode="setCode" />
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
import StocksModal from '@/views/modals/stocks/StocksModal.vue'
export default {
    components: {
        PageHeader, StocksModal
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
            store_item: {},
            storeItems: [],
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
      
    },
    
    methods:{     
        goEdit(){
            const stockItem={
                    id: this.id,
                    voucher_no:this.voucher_no, 
                    created_at:this.created_at,
                    store_item: this.store_item,
                    storeItems: this.storeItems,
                    total_retail_amount:this.total_retail_amount,
                    total_whole_sale_amount:this.total_whole_sale_amount,
                    total_pur_amount: this.total_pur_amount
            }
            this.$store.dispatch("setStockItem", stockItem)
            this.$router.push({name: 'EditOpeningStocks', params: {id: this.id}})
        },
        closeOpeningStock(){
          this.id="";
            this.voucher_no="";
            this.created_at="";
            this.storeItems=[]
            this.store_item={}              
            this.total_retail_amount=0,
           this.total_whole_sale_amount=0;
           this.total_pur_amount=0;
        },
     
        setCode(p){           
          //  const created_date=this.showTime(p.created_at)
            this.id=p.id;
            this.voucher_no=p.voucher_no;
            this.created_at=p.created_at//new Date(created_date).toISOString().substr(0, 10);
            this.storeItems=p.products;
            this.total_retail_amount=p.total_retail_amount;
            this.total_whole_sale_amount=p.total_whole_sale_amount
            this.total_pur_amount=p.total_pur_amount
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
