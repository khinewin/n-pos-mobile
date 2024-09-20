<template lang="">
    <div>
        <PageHeader page-title="Purchase / History" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">
                <div class="col-sm-2 d-grid">
                    <button @click="print" class="btn btn-sm" :disabled="!voucher_no"><i class="fa-solid fa-print"></i> Print (ctrl+p)</button>
                </div>   
                <div class="col-sm-2 d-grid">
                    <button @click="goEdit" class='btn btn-sm' :disabled="!voucher_no || pur_user.uid !==  user.uid"><i class="fa-solid fa-pen-to-square"></i> Edit (ctrl+e)</button>
                </div>
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm open"  data-bs-toggle="modal" data-bs-target="#purchaseModal"><i class="fa-solid fa-folder-tree"></i> Open (ctrl+o)</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'AddPurchase'}" id="add" class="btn btn-sm"><i class="fa-solid fa-square-plus"></i> New (shift+n)</router-link>
                </div>
                <div class="col-sm-2 d-grid">
                        <button class="btn btn-sm" :disabled="!voucher_no" @click="closeVoucher"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+q)</button>
                </div>
            </div>
            <div class="card shadow" id="printMe">
                <div class="d-none d-print-block">
                    <LargePrintHeader />
                    <div>Purchase</div>
                </div>
                <div class=" card-body p-2">
                    <div class="row justify-content-end mb-2">
                    <div class="col-sm-6 ">
                        <table class="table mb-0 table-borderless">
                            <tbody>
                                <tr>
                                    <td class=" col-4 text-end fw-bold">Voucher No. <span class="">:</span></td>
                                    <td class="col-8" >
                                        {{voucher_no}}
                                    </td>
                                </tr>
                                <tr>
                                    <td class=" text-end fw-bold">Location <span class="">:</span></td>
                                    <td class=""  >
                                        {{location}}
                                    </td>
                                </tr>
                              
                            </tbody>
                      </table>
                    </div>
                    <div class="col-sm-6">
                        <table class="table mb-0 table-borderless">
                            <tbody>                              
                                <tr>
                                    <td class="col-4 text-end fw-bold">Purchase Date <span class="">:</span></td>
                                    <td class="col-8"  >
                                        <div  v-if="pur_date">
                                            {{showTime(pur_date)}}
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text-end fw-bold">Supplier <span class="">:</span></td>
                                    <td class=""  >
                                        {{supplier.name}} <span v-if="supplier.company">{{supplier.company}}</span>
                                    </td>
                                </tr>
                            </tbody>
                      </table>
                    </div>
                </div>  
                <div class="vh-55 overflow-y-auto">               
                   <table class="table table-bordered mb-0 table-hover">
                        <tbody class="table-group-divider">
                            <tr class="text-center fw-bold table-secondary">
                                <td class="col-1">Code</td>
                                <td class="col-2">Descrption</td>
                                <td class="col-1">Exp. Date</td>     
                                <td class="col-1">Packing Size</td>                              
                                <td class="col-1" colspan="1">Qty</td>
                                <td class="col-1">Unit Price</td> 
                                <td class="col-1">Amount</td>                                   
                                <td class="col-1" colspan="2">Discount</td>                                    
                                <td class="col-1">Unit Cost</td>
                            </tr>
                            </tbody>
                            <tbody class="table-group-divider">
                            <tr class="" v-if="products.length > 0" v-for="st in products" :key="st.id">
                                <td class="">{{st.code_no}}</td>
                                <td class="">{{st.item_name}}</td>
                                <td class="text-center">                                   
                                     <span v-if="st.exp_date">{{showTime(st.exp_date)}}</span>
                                </td>
                                <td class="col-1">
                                    {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                 </td>
                                <td class="text-end">
                                    {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                </td>
                                <td class="text-center">
                                    {{st.qty_pur_price}} * {{st.qty_pur_price_unit}}
                                </td>
                                <td class="text-end">{{fM(st.amount)}}</td>
                                <td class="text-center w-5">
                                    <span v-if="st.dis_qty">{{st.dis_qty}}{{st.dis_qty_unit}}</span>
                                    <span v-if="st.dis_amount">{{st.dis_amount}}$</span>
                                    <span v-if="st.dis_percent">{{st.dis_percent}}%</span> 
                                </td>
                                <td class="text-end w-5">{{st.discount ? fM(st.discount) : 0}}</td>
                                <td class="text-end">{{fM(st.unit_cost)}}</td>
                            </tr>                                                
                        </tbody>
                   </table>     
                 </div>
                 <table class="table table-bordered table-hover mb-0">
                    <tbody class="table-group-divider">
                      
                        <tr class="small">
                            <td  class="text-end col-3 pe-2 fw-bold">Total</td>
                            <td class="text-end col-3">{{fM(total)}}</td>
                            <td  class="text-end pe-2 col-3 fw-bold">Extra Discount</td>
                            <td class="text-end col-3">
                                {{fM(extra_discount)}}
                            </td>
                        </tr>                            
                        <tr class="small">
                            <td  class="text-end pe-2 fw-bold">Paid</td>
                            <td class="text-end">{{fM(paid)}}</td>
                            <td  class="text-end pe-2 fw-bold">Banalce</td>
                            <td class="text-end">{{fM(balance)}}</td>
                        </tr>
                        <tr>
                            <td  class="text-end pe-2 fw-bold">Remark</td>
                            <td colspan="3">{{remark}}</td>
                        </tr>
                    </tbody>
                 </table>
      
                </div>
            </div>
        </div>
        <PurchaseModal @setCode="setCode"/>           

    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import PurchaseModal from '@/views/modals/purchase/PurchaseModal.vue'
import Footer from '@/views/partials/Footer.vue'
import * as hp from "@/lib/helper.js"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as hc from "@/lib/handleCtrl"
export default {
    components: {
        PageHeader, PurchaseModal, Footer, LargePrintHeader
    },
   
    data(){
        return{ 
            id:"",      
            voucher_no:"", 
            location:"",
            pur_date:"",
            supplier : "",
            products:[],
            pur_user:{},
            total_amount:0,
            total_discount:0,
            extra_discount:0,
            grand_total:0,
            paid:0,
            balance:0,
            total:0,
            remark:"",
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
    mounted() {
        window.addEventListener('keydown', this.handlectrl);
    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handlectrl);
     },
 
    methods:{     
        fM(amount){
            return hp.formattedAmount(amount)
        },
        handlectrl(event) {         
         
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog             
                this.closeVoucher(); // Call your custom function
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
     
            if(event.shiftKey && event.key.toLowerCase()=== 'n') {
                event.preventDefault(); // Prevent the default browser save dialog
                const add=document.getElementById("add")
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
        closeVoucher(){
            this.id="";    
            this.voucher_no=""; 
            this.pur_date=""; 
            this.supplier={}; 
            this.supplier_name=""; 
            this.products=[]
            this.total_amount=0
            this.total_discount=0
            this.extra_discount=0
            this.total=0,
            this.location="";
            this.paid=0;
            this.balance=0;
            this.remark="";
        },
        goEdit(){           
            if(this.pur_user.uid!=this.user.uid) return false;
            this.$router.push({name: "EditPurchase", params: {id:this.voucher_no}})
        },
        setCode(p){
            this.id=p.id;
            this.voucher_no=p.voucher_no;
            this.pur_date=p.pur_date;
            this.supplier=p.supplier;
            this.pur_user=p.user;
            this.location=p.location;
           // this.products=p.products;
           this.products=p.products;
           this.remark=p.remark;
            this.calAmount(p);
        },
        calAmount(p){
            this.total_amount=p.total_amount;
            this.total_discount=p.total_discount;
            this.extra_discount=p.extra_discount;
            this.total=p.total;
            this.paid=p.paid;
            this.balance=p.balance;
            this.grand_total=p.grand_total;
        },
        
       showTime(t){
            return hp.showTime(t)
        }
                    
    }
}
</script>
<style lang="">
    
</style>
