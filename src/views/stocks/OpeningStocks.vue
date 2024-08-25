<template lang="">
    <div>           
        <PageHeader page-title="Stocks" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end">
                <div class="col-sm-2 d-grid">
                    <button @click="print" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-print"></i> Print</button>
                </div>                
                <div class="col-sm-2 d-grid">
                        <button @click="goEdit" class="btn btn-sm" :class="{'disabled': !voucher_no || stock_user.uid !== user.uid}"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
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
            <div class="card shadow" id="printMe">
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6 col-lg-4">
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
                                <tr class=" fw-bold table-secondary text-center">
                                    <td class="col-1 ">Code</td>
                                    <td class="col-2">Desc</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1 ">Main Qty</td>         
                                    <td class="col-1">Shop Qty</td>                          
                                    <td class="col-1 ">RT price</td>
                                    <td class="col-1 ">WS price</td>
                                    <td class="col-1">Pur price</td>
                                    <td class="col-1">Main RT Amt</td>                                    
                                    <td class="col-1">Main WS Am</td>                                    
                                    <td class="col-1">Shop RT Amt</td>                                    
                                    <td class="col-1">Shop WS Am</td>
                                </tr>
                                <tr class=""v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id">
                                    <td class="text-center">
                                            {{st.code_no}}                                           
                                    </td>
                                    <td class="text-start">{{st.item_name}}</td>
                                    <td class="">1{{st.parent_unit}} * {{st.item_per_package}}{{st.child_unit}}</td>
                                    <td class="col-1 text-end">
                                        <span v-if="calQtyL(st.main_qty, st.item_per_package) > 0">{{calQtyL(st.main_qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                         <span v-if="calQtyL(st.main_qty, st.item_per_package) >0 && calQtyS(st.main_qty, st.item_per_package) > 0">, </span>
                                         <span v-if="calQtyS(st.main_qty, st.item_per_package)> 0">{{calQtyS(st.main_qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                     </td>
                                    <td class="col-1 text-end">
                                        <span v-if="calQtyL(st.qty, st.item_per_package) > 0">{{calQtyL(st.qty, st.item_per_package)}}{{st.parent_unit}} </span>
                                         <span v-if="calQtyL(st.qty, st.item_per_package) >0 && calQtyS(st.qty, st.item_per_package) > 0">, </span>
                                         <span v-if="calQtyS(st.qty, st.item_per_package)> 0">{{calQtyS(st.qty, st.item_per_package)}}{{st.child_unit}}  </span>
                                     </td>
                                    <td class="text-end">                                      
                                         <span>{{st.retail_price}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.whole_sale_price}}</span>
                                   </td>
                                   <td class="text-end">                                      
                                     <span>{{st.pur_price}}</span>
                                    </td>
                                   <td class="text-end">                                      
                                        <span>{{st.main_retail_amount}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.main_whole_sale_amount}}</span>
                                   </td>                                  
                                    <td class="text-end">                                      
                                        <span>{{st.retail_amount}}</span>
                                   </td>                                  
                               <td class="text-end">                                      
                                   <span>{{st.whole_sale_amount}}</span>
                              </td>
                                 
                                </tr>
                                <tr class="small">
                                    <td colspan="8" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_main_retail_amount}}</td>
                                    <td class="text-end">{{total_main_whole_sale_amount}}</td>
                                    <td class="text-end">{{total_retail_amount}}</td>
                                    <td class="text-end">{{total_whole_sale_amount}}</td>
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
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/stocks"
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
            stock_user:{},
            store_item: {},
            storeItems: [],
            total_retail_amount:0,
            total_whole_sale_amount:0,    
            total_main_retail_amount:0,
            total_main_whole_sale_amount:0,
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
        calQtyL(qty, ipp){
            return hp.calQtyL(qty, ipp);
        },
        calQtyS(qty, ipp){
            return hp.calQtyS(qty, ipp)
        },
        async print () {
        // Pass the element id here
         await this.$htmlToPaper('printMe');
        }, 
        goEdit(){           
            this.$router.push({name: 'EditOpeningStocks', params: {id: this.voucher_no}})
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
            this.stock_user=p.user;
            this.voucher_no=p.voucher_no;
            this.created_at=p.created_at//new Date(created_date).toISOString().substr(0, 10);
            this.storeItems=p.products;
            this.total_retail_amount=p.total_retail_amount;
            this.total_whole_sale_amount=p.total_whole_sale_amount
            this.total_pur_amount=p.total_pur_amount
            this.total_main_retail_amount=p.total_main_retail_amount
            this.total_main_whole_sale_amount=p.total_main_whole_sale_amount;
        },    
        
       showTime(t){
            return hp.showTime(t)
        },
                    
    }
}
</script>
<style lang="">
    
</style>
