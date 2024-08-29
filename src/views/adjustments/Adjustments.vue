<template lang="">
    <div>           
        <PageHeader page-title="Adjustments" page-code="adjustments" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">
                <div class="col-sm-2 d-grid">
                    <button @click="print" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-print"></i> Print</button>
                </div>                
                <div class="col-sm-2 d-grid">
                        <button @click="goEdit" class="btn btn-sm" :class="{'disabled': !voucher_no || stock_user.uid !== user.uid}"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'NewAdjustment'}" class="btn btn-sm"><i class="fa-solid fa-square-plus"></i> New</router-link>
                </div>
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#adjustmentModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button @click="closeAdjustment" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                </div>
            </div>
            <div class="card shadow" id="printMe">
                <div class="card-body p-2">
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

                                            <td class="text-end col-2 fw-bold pt-2">Voucher no. :</td>
                                            <td class="col-2 pt-2">
                                                {{voucher_no}}
                                            </td>
                                     
                                            <td class="text-end col-2 fw-bold pt-2">Date :</td>
                                            <td class="pt-2 col-2">
                                               <span v-if="created_at"> {{showTime(created_at)}}</span>
                                            </td>
                                        </tr>
                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <div class="vh-65 overflow-y-auto">
                        <table class="table table-bordered mb-0  table-hover">
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
                                <tr class=""v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id">
                                    <td class="text-center">
                                            {{st.code_no}}                                           
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="text-center">                                      
                                         <span>{{st.before_adj_qty}}</span>
                                    </td>
                                    <td class="text-center">
                                        {{st.adj_type}}
                                    </td>
                                    <td class="text-end">                                      
                                         <span>{{st.adj_qty}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.after_adj_qty}}</span>
                                   </td>
                                    <td class="text-center">                                      
                                        <span>{{st.rts_rfs}}</span>
                                   </td>
                                    <td class="">                                      
                                         <span>{{st.remark}}</span>
                                    </td>
                                    <td class="text-end">                                      
                                        <span>{{st.retail_amount}}</span>
                                   </td>  
                                   <td class="text-end">                                      
                                        <span>{{st.whole_sale_amount}}</span>
                                    </td>                                   
                                    <td class="text-end">                                      
                                        <span>{{st.pur_amount}}</span>
                                    </td>
                                 
                                </tr>
                                <tr class="small">
                                    <td colspan="8" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_retail_amount}}</td>        
                                    <td class="text-end">{{total_whole_sale_amount}}</td>                                                           
                                    <td class="text-end">{{total_pur_amount}}</td>
                                </tr>                                                                                         
                            </tbody>
                        </table>      
                        </div>                                                                                                              
          
            <AdjustmentModal @setCode="setCode" />
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
import AdjustmentModal from '@/views/modals/adjustments/AdjustmentModal.vue'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/adjustments"
export default {
    components: {
        PageHeader, AdjustmentModal
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
            stock_user:{},
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
        async print () {
        // Pass the element id here
         await this.$htmlToPaper('printMe');
        }, 
        goEdit(){           
            this.$router.push({name: 'EditAdjustment', params: {id: this.voucher_no}})
        },
        closeAdjustment(){
          this.id="";
            this.voucher_no="";
            this.location="";
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
            this.location=p.location;
            this.voucher_no=p.voucher_no;
            this.created_at=p.created_at//new Date(created_date).toISOString().substr(0, 10);
            this.storeItems=p.products;
            this.total_retail_amount=p.total_retail_amount;
            this.total_pur_amount=p.total_pur_amount
            this.total_whole_sale_amount=p.total_whole_sale_amount
        },    
        
       showTime(t){
            return hp.showTime(t)
        },
                    
    }
}
</script>
<style lang="">
    
</style>
