<template lang="">
    <div>           
        <PageHeader page-title="Transfer / History" page-code="transfers" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                    <button @click="print" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-print"></i> Print</button>
                </div>                
                <!--<div class="col-sm-2 d-grid">
                        <button @click="goEdit" class="btn btn-sm" :class="{'disabled': !voucher_no || transfer_user.uid !== user.uid}"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
                </div>-->               
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#transfersModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-sm-2 d-grid">
                    <router-link :to="{name : 'AddTransfer'}" class="btn btn-sm"><i class="fa-solid fa-square-plus"></i> New</router-link>
            </div>
                <div class="col-sm-2 d-grid">
                        <button @click="closeTransfer" class="btn btn-sm" :class="{'disabled': !voucher_no}"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                </div>
            </div>
            <div class="card shadow min-vh-75" id="printMe">
                <div class="d-none d-print-block">
                    <LargePrintHeader />
                    <div>Transfers</div>
                </div>
                <div class="card-body p-2">
                    <form @submit.prevent="saveTransfers">
                        <div class="row justify-content-end">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4">From location :</td>
                                            <td class="col-8">
                                                {{from_location}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end">To location :</td>
                                            <td class="">
                                                {{to_location}}
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4">Voucher no. :</td>
                                            <td class="col-8">
                                                {{voucher_no}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end">Date :</td>
                                            <td class="">
                                                <span v-if="created_at">{{showTime(created_at)}}</span>
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                       
                        <table class="table table-bordered  mb-0 table-hover mt-2">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2">Description</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1">From Loc Bal</td> 
                                    <td class="col-1">To Loc Bal</td> 
                                    <td class="col-1">Transfer Bal</td>
                                    <td class="col-1">After From Loc Bal</td> 
                                    <td class="col-1">After To Loc Bal</td> 
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id"  >
                                    <td class="text-center">
                                        <div class="position-relative">
                                            {{st.code_no}}                                           
                                        </div>                                    
                                    </td>
                                    <td class="">{{st.item_name}}</td>                                   
                                    <td class="">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.from_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.to_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-center">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.transfer_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_from_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_to_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                                                                                                              
                                </tr>                                                                                                              
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
            <TransfersModal @setCode="setCode"  />
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
import TransfersModal from '@/views/modals/transfers/TransfersModal.vue'
import * as ss from "@/lib/snapshot/products"
import * as hp from "@/lib/helper"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"

export default {
    components: {
        PageHeader, TransfersModal,LargePrintHeader
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
            from_location:"",
            to_location:"", 
            transfer_user:{},
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
       editItem(data){
           
       }
    },
    created() {     
      
    },
    
    methods:{
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
            this.$router.push({name: 'EditTransfer', params: {id: this.voucher_no}})
        },
      
        storingItem(){           
            this.storeItems.push(this.transfer_item)   
         },
        setCode(p){           
            this.id=p.id;
            this.transfer_user=p.user;
            this.voucher_no=p.voucher_no;
            this.created_at=p.created_at//new Date(created_date).toISOString().substr(0, 10);
            this.storeItems=p.products;
            this.from_location=p.from_location;
            this.to_location=p.to_location
        },
        closeTransfer(){
           this.id="";
           this.voucher_no="";
           this.transfer_user={};
           this.created_at="";
           this.storeItems=[];
           this.from_location=""
           this.to_location=""
        },
      
       showTime(t){
            return hp.showTime(t)
        },
                    
    }
}
</script>
<style lang="">
    
</style>
