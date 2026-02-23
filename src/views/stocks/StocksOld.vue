<template lang="">
    <div>           
        <PageHeader :page-title="pageTitle" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button @click="print" class="btn btn-sm" :disabled="!voucher_no"><i class="fa-solid fa-print"></i> {{t('print_link')}}</button>
                </div>                
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button @click="goEdit" class="btn btn-sm" :disabled="!voucher_no || stock_user.uid !== user.uid"><i class="fa-solid fa-pen-to-square"></i> {{t('edit_link')}}</button>
                </div>                
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm open" data-bs-toggle="modal" data-bs-target="#stocksModal"><i class="fa-solid fa-folder-tree"></i> {{t('open_link')}}</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <router-link :to="{name : 'NewStocks'}" class="btn btn-sm new"><i class="fa-solid fa-square-plus"></i> {{t('new_link')}}</router-link>
            </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button @click="closeStocks" class="btn btn-sm" :disabled="!voucher_no"><i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}}</button>
                </div>
            </div>
            <div class="card shadow" id="printMe">
                <div class="d-none d-print-block">
                    <LargePrintHeader />
                    <div class="text-center h6">Stocks</div>
                </div>
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 fw-bold">{{t('notes')}} :</td>
                                            <td class="col-8">
                                                {{remark}}
                                            </td>
                                        </tr>
                                                                            
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 fw-bold ">{{t('invoice_no')}} :</td>
                                            <td class="col-8">
                                                {{voucher_no}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end fw-bold">{{t('date')}} :</td>
                                            <td class="">
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
                                <tr class=" fw-bold table-secondary text-center">
                                    <td class="col-1 ">{{t('barcode')}}</td>
                                    <td class="col-2">{{t('item_name')}}</td>
                                    <td class="col-1">{{t('packing_size')}}</td>
                                    <!--<td class="col-1">Main Inv Bal</td>-->     
                                    <td class="col-1">{{t('inv_bal')}}</td>   
                                    <td class="col-1">{{t('pur_price')}}</td>
                                    <td class="col-1">{{t('sale_price')}}</td>
                                    <!--<td class="col-1">Wholesale price</td>-->      
                                </tr>
                                <tr class=""v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id">
                                    <td class="text-center">
                                            {{st.code_no}}                                           
                                    </td>
                                    <td class="text-start">{{st.item_name}}</td>
                                    <td class="">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <!--<td class=" text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.main_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>--> 
                                    <td class=" text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td> 
                                    <td class="text-end" >
                                        <span v-if="st.packing_size.largest_unit"> {{st.pur_price.largest_unit}}' {{st.packing_size.largest_unit}}</span>
                                        <span v-else>{{st.pur_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>       
                                    <td class="text-end ">
                                        <span v-if="st.packing_size.largest_unit">{{st.retail_price.largest_unit}}' {{st.packing_size.largest_unit}} </span>
                                        <span v-else>{{st.retail_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>
                                    <!--<td class="text-end">
                                        <span v-if="st.packing_size.largest_unit">{{st.whole_sale_price.largest_unit}}' {{st.packing_size.largest_unit}} </span>
                                        <span v-else>{{st.whole_sale_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>-->                                          
                                 
                                </tr>
                                                                                                              
                            </tbody>
                        </table>  
                        </div>                                                                                                                  
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
import store from "@/store"
import StocksModal from '@/views/modals/stocks/StocksModal.vue'
import StocksModalOld from '@/views/modals/stocks/StocksModalOld.vue'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/stocks"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as hc from "@/lib/handleCtrl"
import {lang} from '@/lib/language'

export default {
    components: {
        PageHeader, StocksModal: store.getters.theme === "new" ? StocksModal : StocksModalOld, LargePrintHeader
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
            isLoading:false,
            remark:"",
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
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.stocks.mm} / ${lang.history.mm}` : `${lang.stocks.eng} / ${lang.history.eng}`            
            return pageTitle;
        },
         t() {
            return (key) => this.$store.getters.t(key);
        },
    },
    mounted() {
        window.addEventListener('keydown', this.handlectrl);
    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handlectrl);
     },
    
    methods:{    
        handlectrl(event) {        
         if (event.ctrlKey && event.key === 'q') {
             event.preventDefault(); // Prevent the default browser save dialog             
             this.closeStocks(); // Call your custom function
         }
         if (event.ctrlKey && event.key === 'p') {
             event.preventDefault(); // Prevent the default browser save dialog
             //this.printSale(); // Call your custom function
             this.print();
         }        
         if (event.ctrlKey && event.key === 'e') {
             event.preventDefault(); // Prevent the default browser save dialog
              this.goEdit();
         }      
         if(event.ctrlKey && event.shiftKey && event.key=== 'N') {
             event.preventDefault(); // Prevent the default browser save dialog
             const add=document.querySelector(".new")
             add.click();
         }
         return hc.handleCtrl(event)
      
     },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        async print () {
        // Pass the element id here
         await this.$htmlToPaper('printMe');
        }, 
        goEdit(){           
            if(this.stock_user.uid != this.user.uid) return false;
            this.$router.push({name: 'EditStocks', params: {id: this.voucher_no}})
        },
        closeStocks(){
          this.id="";
            this.voucher_no="";
            this.created_at="";
            this.storeItems=[]
            this.store_item={}              
            this.total_retail_amount=0,
           this.total_whole_sale_amount=0;
           this.total_pur_amount=0;
           this.remark="";
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
            this.remark=p.remark;
        },    
        
       showTime(t){
            return hp.showTime(t)
        },
                    
    }
}
</script>
<style lang="">
    
</style>
