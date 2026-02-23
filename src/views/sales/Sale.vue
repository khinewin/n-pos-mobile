<template>
    <div>       
      
        <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
          <div class="container-fluid">    
        <div class="mb-2">
            <div class="d-flex justify-content-between align-items-center bg-white p-2 rounded-3 shadow-sm flex-wrap gap-2">
                 <!-- Left: Scan/Actions -->
                 <div class="d-flex gap-2">
                     <button class="btn btn-light shadow-sm text-primary fw-bold border-0" data-bs-toggle="modal" data-bs-target="#salesModal">
                        <i class="fa-solid fa-folder-tree me-2"></i> {{t('open_link')}}
                     </button>
                      <button class="btn btn-light shadow-sm text-dark fw-bold border-0" @click="goEdit" :disabled="!id || user.uid != sale_user.uid">
                        <i class="fa-solid fa-pen-to-square me-2"></i> {{t('edit_link')}}
                     </button>
                 </div>

                 <!-- Center: Print Toggle -->
                 <div class="form-check form-switch d-flex align-items-center bg-light rounded-pill px-3 py-2">
                    <input class="form-check-input" @change="setPos" v-model="print_on_save" type="checkbox" role="switch" id="flexSwitchCheckDisabled" style="cursor: pointer;">
                    <label class="form-check-label ms-2 fw-medium small text-muted text-uppercase cursor-pointer" for="flexSwitchCheckDisabled">
                        <i class="fa-solid fa-print me-1"></i> Auto Print
                    </label>
                 </div>

                 <!-- Right: Main Actions -->
                 <div class="d-flex gap-2">
                     <button :disabled="saleItems.length <=0 || saleMode=='Sale / New'" class="btn btn-light shadow-sm fw-medium border-0" @click="print">
                        <i class="fa-solid fa-print me-2"></i> {{t('print_link')}}
                     </button>
                     <button @click="clearSaleItem" class="btn btn-light shadow-sm text-danger fw-medium border-0" :disabled="saleItems.length <=0">
                        <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                     </button>
                     <button v-if="saleMode=='Sale / New' || saleMode=='Sale / History'" type="button" data-bs-toggle="modal" data-bs-target="#cfModal" class="btn btn-primary shadow-sm px-4 fw-bold border-0" :disabled="isLoading || saleItems.length <=0 || saleMode=='Sale / History'">
                        <span v-if="!isLoading"><i class="fa-solid fa-hard-drive me-2"></i> {{t('save_link')}}</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>
                 </div>
            </div>
        </div>

        <div class="mb-2">
             <div class="row g-3">
                 <!-- Invoice Details Card -->
                 <div class="col-12">
                     <div class="card border-0 shadow-sm rounded-3">
                         <div class="card-body p-2">
                             <div class="row g-4 align-items-center">
                                 <!-- Voucher No -->
                                 <div class="col-md-3">
                                     <label class=" small text-muted text-uppercase fw-bold">{{t('invoice_no')}}</label>
                                     <div class="input-group">
                                         <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                         <input type="text" :class="{'is-invalid' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control bg-light border-0 fw-bold text-dark" id="voucher_no" placeholder="Invoice #">
                                     </div>
                                 </div>
                                 
                                  <!-- Date -->
                                 <div class="col-md-3">
                                     <label class=" small text-muted text-uppercase fw-bold">{{t('date')}}</label>
                                       <div class="input-group">
                                         <span class="input-group-text bg-light border-0"><i class="fa-regular fa-calendar text-secondary"></i></span>
                                         <flat-pickr class="form-control bg-light border-0 fw-medium" v-model="sale_date" id="sale_date" :config="config" />
                                       </div>
                                 </div>

                                  <!-- Customer -->
                                 <div class="col-md-3">
                                     <label class=" small text-muted text-uppercase fw-bold">{{t('customer')}}</label>
                                     <div class="input-group" :class="{'cursor-pointer': saleMode!='Sale / History'}" :data-bs-toggle="[saleMode=='Sale / History' ? '' : 'modal']" :data-bs-target="[saleMode=='Sale / History' ? '' : '#customerModal']">
                                         <span class="input-group-text bg-light border-0"><i class="fa-regular fa-user text-secondary"></i></span>
                                          <input type="text" readonly v-model="customer.name" class="form-control bg-light border-0 fw-medium cursor-pointer" id="customer_name" placeholder="Select Customer">
                                          <span class="input-group-text bg-light border-0 text-primary"><i class="fa-solid fa-chevron-down small"></i></span>
                                     </div>
                                 </div>

                                  <!-- Barcode -->
                                 <div class="col-md-3">
                                      <label class=" small text-muted text-uppercase fw-bold change-cursor" @click="setBarcodeFocus">{{t('barcode')}}</label>
                                      <div class="input-group shadow-sm">
                                            <span class="input-group-text bg-primary text-white border-0"><i class="fa-solid fa-barcode"></i></span>
                                            <input :disabled="saleMode=='Sale / History'" @keypress.enter="setCodeOnPaste" id="code_no" v-model="code_no" type="text" class="form-control border-0 bg-white fw-bold" placeholder="Scan Barcode..." autofocus>
                                      </div>
                                 </div>
                             </div>
                         </div>
                     </div>
                 </div>
             </div>
        </div>
                              <!-- Redesigned Sales Table -->
                              <div class="vh-50 overflow-y-auto dataContainer bg-white rounded-3 shadow-sm border-0 mb-3">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="sticky-top z-3 text-nowrap">
                                        <tr class="table-primary fw-bold small text-center cursor-pointer" :data-bs-toggle="[saleMode=='Sale / History' ? '' : 'modal']" :data-bs-target="[saleMode=='Sale / History' ? '' : '#searchCode']">
                                            <th class="py-2 border-0" colspan="2" style="width: 80px;">{{t('number')}}</th>
                                            <th class="py-2 text-start border-0" style="min-width: 200px;">
                                                <i class="fa-solid fa-circle-plus me-1 text-primary"></i> {{t('item_name')}}
                                            </th>  
                                            <th class="py-2 border-0">{{t('packing_size')}}</th>                                                                                
                                            <th class="py-2 border-0 table-primary-subtle" style="width: 140px;">{{t('qty')}}</th>
                                            <th class="py-2 border-0" style="width: 160px;">{{t('unit_price')}}</th>
                                            <th class="py-2 border-0 text-end">{{t('amount')}}</th>
                                            <th class="py-2 border-0 text-center" colspan="2">{{t('discount')}}</th>
                                            <th class="py-2 border-0 text-end" style="width: 120px;">{{t('net_amount')}}</th>
                                        </tr>    
                                    </thead>                                  
                                    <tbody class="text-nowrap">                                                                         
                                        <tr v-if="saleItems.length > 0" v-for="(s, i) in saleItems" :key="s.id" @mouseenter="setEdit(s)" class="small">
                                            <td class="text-center border-0 px-1">
                                                <button type="button" @click="removeSaleItem(i)" class="btn btn-link text-danger p-0 border-0 opacity-75 hover-opacity-100" :disabled="saleMode=='Sale / History'">
                                                    <i class="fa-solid fa-circle-minus fs-5"></i>
                                                </button>
                                            </td>
                                            <td class="text-center text-muted border-0 ps-0">                                                
                                                {{i+1}}
                                            </td>
                                            <td class="fw-bold border-0">{{s.item_name}}</td>   
                                            <td class="text-center border-0">
                                                <span v-if="s.packing_size" class="badge bg-light text-primary border border-primary-subtle px-2 py-1">
                                                    {{calPackingSize(s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit, s.packing_size.medium_qty, s.packing_size.smallest_qty)}}
                                                </span>
                                            </td>                                        
                                            <td class="border-0 table-primary-subtle p-0">
                                                <div class="d-flex align-items-center justify-content-center px-1">
                                                    <button class="btn btn-link text-danger p-0 border-0 opacity-75 hover-opacity-100 shadow-none" :disabled="saleMode=='Sale / History'" @click="decreaseQty(s)">
                                                        <i class="fa-solid fa-circle-minus fs-4"></i>
                                                    </button>
                                                    <input 
                                                        :disabled="saleMode=='Sale / History'" 
                                                        type="number" 
                                                        class="form-control form-control-sm border-0 bg-transparent text-center fw-bold px-1" 
                                                        style="width: 55px;"
                                                        @keyup="setEdit(s)"  
                                                        @keypress.enter="calEdit(s)" 
                                                        v-model="s.sale_qty" 
                                                        @change="calEdit(s)">
                                                    <button :disabled="saleMode=='Sale / History'" class="btn btn-link text-success p-0 border-0 opacity-75 hover-opacity-100 shadow-none" @click="increaseQty(s)">
                                                        <i class="fa-solid fa-circle-plus fs-4"></i>
                                                    </button>
                                                </div>
                                            </td>                                            
                                            <td class="border-0 text-center">
                                                <div class="d-flex align-items-center justify-content-center">
                                                    <span class="fw-bold text-dark">{{s.qty_sale_price}}</span>
                                                    <small class="text-muted ms-1">/ {{s.qty_sale_price_unit}}</small>
                                                </div>                                       
                                            </td>
                                           
                                            <td class="text-end border-0 fw-bold">{{s.amount}}</td>
                                            <td class="text-center border-0 cursor-pointer" id="setDiscount" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                                <div v-if="s.dis_qty" class="badge bg-danger-subtle text-danger">{{s.dis_qty}}{{s.dis_qty_unit}}</div>
                                                <div v-if="s.dis_amount" class="badge bg-danger-subtle text-danger">{{s.dis_amount}} Ks</div>
                                                <div v-if="s.dis_percent" class="badge bg-danger-subtle text-danger">{{s.dis_percent}}%</div>      
                                                <span v-if="!s.dis_qty && !s.dis_amount && !s.dis_percent" class="text-muted opacity-25">
                                                    <i class="fa-solid fa-tag"></i>
                                                </span>
                                            </td>
                                            <td class="text-end border-0 text-danger fw-bold cursor-pointer" data-bs-toggle="modal" data-bs-target="#setDiscountModal">
                                                {{fM(s.discount??0)}}
                                            </td>
                                            <td class="text-end border-0 fw-bold text-primary">{{fM(s.final_amount)}}</td>
                                        </tr> 
                                        <!-- Empty State -->
                                        <tr v-if="saleItems.length === 0" class="text-center text-muted cursor-pointer" :data-bs-toggle="[saleMode=='Sale / History' ? '' : 'modal']" :data-bs-target="[saleMode=='Sale / History' ? '' : '#searchCode']">
                                            <td colspan="10" class="py-5">
                                                <i class="fa-solid fa-cart-plus fa-3x mb-3 opacity-25 text-primary"></i>
                                                <p class="mb-0 fw-medium">Click here to add items</p>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                              </div>

                                </div>

                                
                                <!-- Premium Sticky Bottom Summary Bar -->
                                <div class="sticky-bottom bg-white shadow-lg border-top py-3 px-4 z-2">
                                    <div class="row align-items-center">
                                         <!-- Left: Remarks/Notes -->
                                         <div class="col-lg-3 mb-2 mb-lg-0">
                                              <div class="form-floating">
                                                  <textarea :disabled="saleMode=='Sale / History'" @keypress.enter="saveSale" v-model="remark" class="form-control border-0 bg-light rounded-3" placeholder="Notes" id="remarkArea" style="height: 60px; resize: none;"></textarea>
                                                  <label for="remarkArea" class="text-muted small">{{t('notes')}}</label>
                                              </div>
                                         </div>
            
                                         <!-- Right: Totals Breakdown -->
                                         <div class="col-lg-9">
                                              <div class="row g-2 align-items-end justify-content-end text-nowrap">
                                                   <!-- Subtotal -->
                                                   <div class="col-6 col-md-2 text-end">
                                                        <label class="small text-muted fw-bold d-block text-uppercase mb-1" style="font-size: 0.7rem;">{{t('subtotal')}}</label>
                                                        <div class="fs-5 fw-bold text-secondary">{{fM(total_amount)}}</div>
                                                   </div>

                                                   <!-- Additional Discount -->
                                                   <div class="col-6 col-md-2 text-end">
                                                        <label class="small text-danger fw-bold d-block text-uppercase mb-1" style="font-size: 0.7rem;">{{t('additional_discount')}}</label>
                                                        <div class="d-flex align-items-center justify-content-end">
                                                            <input 
                                                                @focus="setInput" 
                                                                @blur="setInput" 
                                                                :disabled="saleMode=='Sale / History'" 
                                                                type="number" 
                                                                @keyup="calTotal" 
                                                                v-model="extra_discount" 
                                                                class="form-control form-control-sm text-end border-0 bg-transparent fw-bold text-danger p-0 fs-5 shadow-none" 
                                                                style="width: 100px;"
                                                                placeholder="0">
                                                        </div>
                                                   </div>
            
                                                   <!-- Net Amount (Highlighted) -->
                                                   <div class="col-12 col-sm-4 col-md-3 text-end bg-primary text-white p-2 rounded-2 shadow-sm mx-2">
                                                        <label class="small fw-bold d-block text-uppercase opacity-75" style="font-size: 0.65rem;">{{t('total')}}</label>
                                                        <div class="fs-3 fw-bolder lh-1">{{fM(net_amount)}}</div>
                                                    </div>
            
                                                   <!-- Paid -->
                                                   <div class="col-6 col-md-2 text-end">
                                                        <label class="small text-success fw-bold text-uppercase mb-1 d-block" style="font-size: 0.7rem;">{{t('paid')}}</label>
                                                        <div class="d-flex align-items-center justify-content-end">
                                                            <input 
                                                                @focus="setInput" 
                                                                @blur="setInput" 
                                                                :disabled="saleMode=='Sale / History'" 
                                                                @keypress.enter="saveSale" 
                                                                @keyup="calPaid" 
                                                                type="number" 
                                                                v-model="paid" 
                                                                class="form-control form-control-sm text-end border-0 bg-transparent fw-bold text-success p-0 fs-5 shadow-none" 
                                                                style="width: 100px;"
                                                                placeholder="0">
                                                        </div>
                                                   </div>
                                                   
                                                   <!-- Change / Balance Due -->
                                                   <div class="col-6 col-md-2 text-end border-start ps-3">
                                                       <div v-if="change > 0">
                                                           <label class="small text-muted fw-bold d-block text-uppercase mb-0" style="font-size: 0.6rem;">{{t('change')}}</label>
                                                           <span class="fs-5 fw-bold text-dark">{{fM(change)}}</span>
                                                       </div>
                                                       <div v-else>
                                                           <label class="small text-danger fw-bold d-block text-uppercase mb-0" style="font-size: 0.6rem;">{{t('balance_due')}}</label>
                                                           <span class="fs-5 fw-bold text-danger">{{fM(credit)}}</span>
                                                       </div>
                                                   </div>
                                              </div>
                                         </div>
                                    </div>
                                </div>

                        
                   
        <ProductModal @setCode="setCode" :sale_type="sale_type" :saleItems="saleItems"/>
        <SalesModal @setCode="setOldCode" />
        <CustomerModal @setCustomer="setCustomer" />
        <!-- Redesigned Checkout Confirmation Modal -->
        <div class="modal fade" id="cfModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="checkoutModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
                    <!-- Modal Header -->
                    <div class="modal-header bg-primary text-white border-0 py-3 px-4">
                        <h5 class="modal-title fw-bold" id="checkoutModalLabel">
                            <i class="fa-solid fa-receipt me-2"></i>{{t('save_link')}} Confirmation
                        </h5>
                        <button type="button" class="btn-close btn-close-white shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    
                    <div class="modal-body p-2 bg-light">
                        <!-- Summary Card -->
                        <div class="card border-0 shadow-sm rounded-3 mb-2 overflow-hidden">
                            <div class="card-body p-0">
                                <!-- Customer Info -->
                                <div class="bg-primary-subtle p-2 border-bottom border-white border-2">
                                    <div class="d-flex align-items-center">
                                        <div class="bg-primary text-white rounded-circle p-2 me-3 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                            <i class="fa-solid fa-user"></i>
                                        </div>
                                        <div>
                                            <small class="text-muted fw-bold text-uppercase d-block" style="font-size: 0.7rem;">{{t('customer')}}</small>
                                            <div class="fw-bold text-dark fs-5">{{customer.name}}</div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Financial Breakdown -->
                                <div class="p-2">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span class="text-muted fw-medium">{{t('subtotal')}}</span>
                                        <span class="fw-bold text-dark fs-5">{{fM(total_amount)}}</span>
                                    </div>
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <div class="text-danger fw-medium d-flex align-items-center">
                                            <i class="fa-solid fa-tag me-2 small"></i>{{t('additional_discount')}}
                                        </div>
                                        <div style="width: 130px;">
                                            <input 
                                                @click="setInput" 
                                                @mouseleave="setInput" 
                                                :disabled="saleMode=='Sale / History'" 
                                                type="number" 
                                                @change="calTotal" 
                                                @keyup="calTotal" 
                                                v-model="extra_discount" 
                                                class="form-control form-control-sm text-end fw-bold text-danger border-danger-subtle bg-danger-subtle rounded-3">
                                        </div>
                                    </div>
                                    
                                    <!-- Grand Total Highlighting -->
                                    <div class="bg-primary text-white p-2 rounded-3 d-flex justify-content-between align-items-center mb-2 shadow-sm">
                                        <span class="text-uppercase fw-bold opacity-75" style="letter-spacing: 1px;">{{t('total')}}</span>
                                        <span class="fs-2 fw-bolder">{{fM(net_amount)}}</span>
                                    </div>

                                    <!-- Payment Input Section -->
                                    <div class="row g-3 mb-2">
                                        <div class="col-6">
                                            <label class="small text-success fw-bold text-uppercase mb-1 d-block"><i class="fa-solid fa-hand-holding-dollar me-1"></i>{{t('paid')}}</label>
                                            <input 
                                                @click="setInput" 
                                                @mouseleave="setInput" 
                                                :disabled="saleMode=='Sale / History'" 
                                                @change="calPaid" 
                                                @keyup="calPaid" 
                                                type="number" 
                                                v-model="paid" 
                                                class="form-control form-control-lg text-end fw-bold text-success border-success-subtle bg-success-subtle rounded-3 shadow-none">
                                        </div>
                                        <div class="col-6">
                                            <label class="small text-muted fw-bold text-uppercase mb-1 d-block"><i class="fa-solid fa-coins me-1"></i>{{t('change')}}</label>
                                            <div class="form-control form-control-lg border-0 bg-white text-end fw-bold text-dark fs-5 pt-1 shadow-sm px-3 rounded-3">{{fM(change)}}</div>
                                        </div>
                                    </div>

                                    <!-- Balance Due (Only show if credit > 0) -->
                                    <div v-if="credit > 0" class="alert alert-danger border-0 rounded-3 py-2 px-3 d-flex justify-content-between align-items-center mb-2 shadow-sm">
                                        <div class="fw-bold"><i class="fa-solid fa-triangle-exclamation me-2"></i>{{t('balance_due')}}</div>
                                        <div class="fs-4 fw-bolder">{{fM(credit)}}</div>
                                    </div>

                                    <!-- Notes Area -->
                                    <div class="form-floating text-muted">
                                        <textarea :disabled="saleMode=='Sale / History'" v-model="remark" class="form-control border-0 bg-light rounded-3" placeholder="Notes" id="checkoutNote" style="height: 70px; resize: none;"></textarea>
                                        <label for="checkoutNote" class="small">{{t('notes')}}</label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Footer Actions -->
                        <div class="d-flex gap-2">
                            <button type="button" class="btn btn-light btn-lg flex-fill fw-bold text-muted border-0 shadow-sm py-3" data-bs-dismiss="modal">
                                <i class="fa-solid fa-arrow-left me-2"></i>{{t('close_link')}}
                            </button>
                            <button type="button" class="btn btn-primary btn-lg flex-fill fw-bolder shadow py-3" @click="saveSale">
                                <i class="fa-solid fa-circle-check me-2"></i>{{t('save_link')}}
                            </button>
                        </div>
                    </div>                
                </div>
            </div>
        </div>

  
        <!--Print item start-->
        <div class="d-none" id="printMe" v-if="saleItems.length > 0" >
                <Print :data="{
                    voucher_no: this.voucher_no,
                    customer: this.customer,
                    saleItems: this.saleItems,
                    sale_date:this.sale_date,
                    print_date: this.print_date,
                    total_amount:this.total_amount,
                    net_amount:this.net_amount,
                    extra_discount: this.extra_discount,
                    paid:this.paid,
                    change:this.change,
                    user: this.user,     
                    credit: this.credit,    
                    saleMode: this.saleMode,   
            }  " />
        </div>       
        <setDiscountModal @saveDiscount="saveDiscount" :ei="editItem" />
        
    </div>
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required, email, helpers } from '@vuelidate/validators'
import { disableNetwork, Timestamp, collection, addDoc, doc, increment, limit, updateDoc, onSnapshot, query, where, orderBy, getDocs } from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import SalesModal from "@/views/modals/sales/SalesModal.vue"
import SalesModalOld from "@/views/modals/sales/SalesModalOld.vue"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import CustomerModalOld from "@/views/modals/sales/CustomerModalOld.vue"
import Print from "@/views/sales/A5Print.vue"
import ProductModal from "@/views/modals/sales/ProductModal.vue"
import ProductModalOld from "@/views/modals/sales/ProductModalOld.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/products"
import setQtyModal from "@/views/modals/setQtyModal.vue"
import setQtyModalOld from "@/views/modals/setQtyModalOld.vue"
import setDiscountModal from "@/views/modals/setDiscountModal.vue"
import setDiscountModalOld from "@/views/modals/setDiscountModalOld.vue"
import FlatPickr from 'vue-flatpickr-component'
import * as hc from "@/lib/handleCtrl"
import { lang } from "@/lib/language"

export default {
    components: {
        PageHeader, 
        SalesModal: store.getters.theme === "new" ? SalesModal : SalesModalOld,
        CustomerModal: store.getters.theme === "new" ? CustomerModal : CustomerModalOld,
        Print, 
        ProductModal: store.getters.theme === "new" ? ProductModal : ProductModalOld,
        setQtyModal: store.getters.theme === "new" ? setQtyModal : setQtyModalOld,
        setDiscountModal: store.getters.theme === "new" ? setDiscountModal : setDiscountModalOld,
        FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data() {
        return {
            id: "",
            saleMode: "Sale / New",
            isLoading: false,
            sale_type: "retail",
            saleItems: [],
            editItem: {},
            store_item: {},
            voucher_no: "",
            sale_date: "",
            print_date: "",
            total_amount: 0,
            net_amount: 0,
            extra_discount: 0,
            credit: 0,
            charges: 0,
            remark: "",
            paid: 0,
            change: 0,
            isLimitQty: false,
            customer: {},
            code_no: "",
            products: [],
            isOffline: false,
            sale_user: {},
            print_on_save: false,
            msg: {
                type: "",
                info: ""
            },
            output: null,
             buffer: '',
            lastTime: 0,
            SCAN_GAP: 40,      // ms between keys (scanner speed)
            MIN_LENGTH: 4,    // minimum barcode length
        }
    },
    validations() {
        return {
            voucher_no: { required },
            sale_date: { required }
        }
    },
   
    mounted() {
        let pos = localStorage.getItem("pos");
        this.print_on_save = pos == "false" ? false : true;

        this.voucher_no = this.setVoucherNo();
        this.sale_date = hp.setToday();
        this.print_date = hp.setToday();
        this.setCustomer(this.default_customer);

        window.addEventListener('keydown', this.handleCtrl);
        const isMobile = window.matchMedia("(max-width: 1199px)").matches;
        if (!isMobile) {
           // this.setBarcodeFocus();
        }
        window.addEventListener('keydown', this.handleKey)
    },
    beforeUnmount() {
        window.removeEventListener('keydown', this.handleCtrl);
        window.removeEventListener('keydown', this.handleKey)
    },
  
    watch: {
        msg: {
            handler(val) {
                if (val.type) {
                    setTimeout(() => {
                        this.msg.type = "";
                        this.msg.info = "";
                    }, 2000)
                }
            },
            deep: true
        }
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        default_customer() {
            return this.$store.getters.default_customer;
        },
        config() {
            return this.$store.getters.flat_pickr_config;
        },
        pageTitle() {
            const sl = this.$store.getters.currentLang;
            let pageTitle = "";
            if (this.saleMode == "Sale / New") {
                pageTitle = sl == "mm" ? `${lang.sale.mm} / ${lang.new.mm}` : `${lang.sale.eng} / ${lang.new.eng}`
            } else {
                pageTitle = sl == "mm" ? `${lang.sale.mm} / ${lang.history.mm}` : `${lang.sale.eng} / ${lang.history.eng}`
            }
            return pageTitle;
        },
        t() {
            return (key) => this.$store.getters.t(key);
        }
    },
    methods: {
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        handleKey(e) {
            const now = Date.now()
            const diff = now - this.lastTime
            this.lastTime = now

            // If typing is slow → human → reset
            if (diff > this.SCAN_GAP) {
                this.buffer = ''
            }

            // Scanner end key
            if (e.key === 'Enter') {
                if (this.buffer.length >= this.MIN_LENGTH) {
                    const code_no=String(this.buffer)
                    this.code_no=code_no;
                    this.setCodeOnPaste()
                }
                this.buffer = ''
                return
            }
            // Accept only printable chars
            if (e.key.length === 1) {
                this.buffer += e.key
            }
            },
        closeCfModal() {
            const modalEl = document.getElementById('cfModal');
            const modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
            modalInstance.hide();
        },
        setPos() {
            localStorage.setItem("pos", this.print_on_save)
        },
        setBarcodeFocus() {
            setTimeout(() => {
                const code_no = document.getElementById("code_no");
                code_no.focus();
            }, 100)
        },
        getUsername(email) {
            return email.split('@')[0]; // Split by '@' and take the first part
        },
        setVoucherNo(){
            let user=this.getUsername(this.user.email);
           return hp.setVoucherNo(user);
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveSale(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.clearSaleItem(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.printSale(); // Call your custom function
            }
        
            if (event.ctrlKey && event.key === 'e') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.goEdit();
            }
            return hc.handleCtrl(event)          
         
        },
        autoScroll(){            
            const container = document.querySelector(".dataContainer");
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth" // Enables smooth scrolling
            });
        
      },
      increaseQty(p){
        const checkQty=this.checkQty(p);
        if(!checkQty) return false;
         let sale_qty=1;         
          let saleItems= this.saleItems.map((f)=>{
                if(f.id ===p.id){
                    return {...f, 
                        sale_qty : f.sale_qty + sale_qty,
                         amount:  f.amount + f.qty_sale_price,
                         final_amount: f.final_amount + f.qty_sale_price,
                        }
                }
                return {
                    ...f
                }               
            })
            this.saleItems=saleItems;
            this.calTotal();
        },
        decreaseQty(p){     
         if(p.sale_qty <=1)return false;      
         let sale_qty=1;         
          let saleItems= this.saleItems.map((f)=>{
                if(f.id ===p.id){
                    return {...f, 
                        sale_qty : f.sale_qty - sale_qty,
                         amount:  f.amount - f.qty_sale_price,
                         final_amount: f.final_amount - f.qty_sale_price,
                        }
                }
                return {
                    ...f
                }               
            })
            this.saleItems=saleItems;
            this.calTotal();
        },
      
        goEdit(){           
            if(this.saleMode=="Sale / New") return false;
            this.$router.push({name: "EditSale", params: {id:this.voucher_no}})
        },      

        async setCodeOnPaste() {
            const query_url = query(collection(db, "products"), where("code_no", "==", this.code_no));
            const unsub = onSnapshot(query_url, async (snapShot) => {
                const products = ss.products(snapShot)
                if (products.length == 0) {
                    this.msg.info = "Barcode not found."
                    this.msg.type = "error"
                    this.code_no = "";
                    return true;
                }
                this.setCode(products[0])
                this.code_no = "";
                unsub();
            });
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        onlyString(p){
           if(p.largest_unit){
            return {
                largest_unit : p.largest_unit,
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
           }
           if(!p.largest_unit){
            return {
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
           }
        },
        clearDiscount(){
            this.editItem.dis_amount=0;
            this.editItem.dis_qty=0;
            this.editItem.dis_percent=0;
            this.editItem.dis_qty_unit="";
            const e=this.editItem;
            e.discount=0;           
        },
        
        saveDiscount(){
            const e=this.editItem;
            return this.saleItems.map((f)=>{  
                if(f.id===e.id){
                    f.dis_qty=e.dis_qty ? e.dis_qty : null;
                    f.dis_qty_unit=e.dis_qty_unit ? e.dis_qty_unit : null;
                    f.dis_amount=e.dis_amount ? e.dis_amount : null;
                    f.dis_percent=e.dis_percent ? e.dis_percent :null;
                    f.discount=e.discount;
                    f.final_amount =f.amount - e.discount;
                }
            }) && this.calTotal();
        },
       
     
        setEdit(item){
            this.editItem={...item};
        },
        calEdit(p){    
            
            if(p.sale_qty >= p.qty){
                p.sale_qty=p.qty;
                this.msg.info="Maximum Inventary Balance"
                this.msg.type="error"
            }
            
            let sale_qty=p.sale_qty;         
            let saleItems= this.saleItems.map((f)=>{
                    if(f.id ===p.id){
                        return {...f, 
                            sale_qty : sale_qty,
                            amount:  f.sale_qty * f.qty_sale_price,
                            final_amount:f.discount ? (f.sale_qty *  f.qty_sale_price) - f.discount : f.sale_qty * f.qty_sale_price,
                            }
                    }
                    return {
                        ...f
                    }               
                })
            this.saleItems=saleItems;
            this.calTotal();
        },
        
        calTotal(){
            let total_amount=0;
            
            this.saleItems.map((m)=>{
                total_amount += m.final_amount ? m.final_amount : 0;
            })

            this.total_amount=total_amount;
            this.net_amount=this.extra_discount ? this.total_amount - this.extra_discount : this.total_amount;
            this.credit=this.paid ? Math.abs(this.net_amount) - this.paid :  this.net_amount;
        },
        setInput(e){
           if(e.target.value==0){
                e.target.value=""
           }
           if(e.target.value==""){
                e.target.value=0;
           }
        },
         openAndroidApp() {
            const data={
                    voucher_no: this.voucher_no,
                    customer: this.customer,
                    saleItems: this.saleItems,
                    sale_date:this.sale_date,
                    print_date: this.print_date,
                    total_amount:this.total_amount,
                    net_amount:this.net_amount,
                    extra_discount: this.extra_discount,
                    paid:this.paid,
                    change:this.change,
                    user: this.user,     
                    credit: this.credit,    
                    saleMode: this.saleMode,   
            } 
            // Prepare the data in a format the Android app can understand
           const jsonString = encodeURIComponent(JSON.stringify(data));
            const url = `https://print?data=${jsonString}`;
            window.location.href = url;     
        },
        async print () {
        // Pass the element id here
        //window.print();
        const isAndroid = /Android/i.test(navigator.userAgent);
         const isMobile = window.matchMedia("(max-width: 1199px)").matches;
        if(isAndroid){
            this.openAndroidApp();
        }else{
            await this.$htmlToPaper('printMe');
            if(this.saleMode=="Sale / New"){
                this.clearSaleItem();
            }
        }
        },
        printSale(){         
            if(this.print_on_save){
              this.print();
           }   
        },
        changeCustomer(){
           this.setCustomer(this.default_customer)
        },
        setCustomer(p) {
            this.customer = {};
            this.customer = p;
        },
        setOldCode(s){
            this.id=s.id;
            this.saleMode="Sale / History";
            this.voucher_no=s.voucher_no,
            this.sale_date=hp.setOldDate(s.sale_date)
            this.print_date=s.sale_date
            this.saleItems=s.products;
            this.total_amount=s.total_amount;
            this.net_amount=s.net_amount;
            this.change=s.change;
            this.extra_discount=s.extra_discount;
            this.paid=s.paid;
            this.sale_type=s.sale_type
            this.credit=s.credit
            this.sale_user=s.user;
            this.remark=s.remark;
            this.customer=s.customer;
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "sales"), where("voucher_no", "==", this.voucher_no));           
               let unsub= onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                    this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message("added", "sale")   
                                    this.clearSaleItem(); 
                                    unsub();
                                }
                            }
                    });                                              
            });                              
        } ,
        async saveSale(){                
            if(this.saleMode=="Sale / History") return false;  
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            if(this.saleItems.length <=0) return false;
            if(this.customer.code==123456789 && this.credit > 0){
                this.msg.info="Cannot sale with credit for default customer"
                this.msg.type="error"
                return false
            }
            this.closeCfModal();
            if(this.customer.code == 123456789){
                if(this.customer.name != "Default Customer"){
                    this.msg.info="Please select customer name again"
                    this.msg.type="error"
                    return false
                }
            }
            if(!this.customer.name || !this.customer.code){
                    this.msg.info="Please select customer name again"
                    this.msg.type="error"
                    return false                
            }
            const isNotReady= this.saleItems.filter((f)=>!f.final_amount)
            if(isNotReady.length) {
                this.msg.info="Please check  item amount"
                this.msg.type="error"
                return false;
            }           

              this.isLoading=true;
                this.msg.type = "";
                this.msg.info = "";

                const sale_date=hp.insertDate(this.sale_date)

                const voucher={
                    voucher_no: this.voucher_no,
                   sale_date: Timestamp.fromDate(sale_date),
                    products : this.saleItems,
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                    extra_discount:this.extra_discount ? this.extra_discount : 0,
                    net_amount: this.net_amount ,
                    paid: this.paid ? this.paid : 0,
                    change:this.change,
                    sale_type:this.sale_type,
                    customer : this.customer,
                    credit: this.credit,
                    remark: this.remark,
                    payment_type: this.credit > 0 ? "Credit" : "Paid"
                }                 
                this.checkDatabase();          
                const saveDocument=collection(db, "sales")
                await addDoc(saveDocument, voucher)           
                        
        },
        async saveCredit(){
            let date=new Date();
            let sale_date=new Date(this.sale_date)
            sale_date.setHours(date.getHours())
            sale_date.setMinutes(date.getMinutes())
            const voucher={
                    voucher_no: this.voucher_no,
                    sale_date: Timestamp.fromDate(sale_date),
                    products : this.saleItems,
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    total_amount:this.total_amount,
                    extra_discount:this.extra_discount,
                    net_amount: this.net_amount,
                    paid: this.paid,
                    change:this.change,
                    sale_type:this.sale_type,
                    customer : this.customer,
                    credit: this.credit,
                }                  
                const saveDocument=collection(db, "creditSales")
                await addDoc(saveDocument, voucher)     
        },
        updateProducts(){
            this.saleItems.map(async (p)=>{
                    const id=p.id;
                    const sale_qty=p.sale_qty;               
                    const updateDocument=doc(db, "products", id)
                    await updateDoc(updateDocument, {
                        qty : increment(-sale_qty)
                    })    
                })
        },
        clearSaleItem(){
                this.sale_user={}
                this.editItem={},
                this.saleItems=[],
                this.store_item={},
                this.total_amount=0,
                this.extra_discount=0,
                this.net_amount=0;
                this.paid=0,
                this.change=0
                this.credit=0
                this.saleMode="Sale / New";                
                this.voucher_no=this.setVoucherNo();
                this.sale_date=hp.setToday();      
                this.print_date=hp.setToday();  
                this.setCustomer(this.default_customer)
                this.remark="";
                this.isLoading=false;
                this.v$.$reset()

        },
        removeSaleItem(i) {
            this.saleItems.splice(i, 1)
            this.calTotal();
        },
     
        checkQty(p){           
            if(p.qty  <1 + p.sale_qty ){
                this.msg.type="error";
                this.msg.info=`There is a maximum balance of "${p.item_name}"`
                return false;
            }          
            return true;
        },
     
        setCode(p){
           if(p.qty <=0){
                this.msg.info= `"${p.item_name}" is out of stock`
                this.msg.type="error";
                return false;
           }
           this.store_item ={
                ...p,           
                sale_type: this.sale_type,         
                sale_qty: 1,
                qty_sale_price_unit:p.packing_size.smallest_unit,
                qty_sale_price: p.retail_price.smallest_unit,
                amount: p.retail_price.smallest_unit,
                final_amount: p.retail_price.smallest_unit
           }
           const s=this.saleItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }else{
                this.updateStoringItem();
            }      
           
        },
        storingItem(){          
                this.saleItems.push(this.store_item)
                this.calTotal();        
                setTimeout(()=>{
                        this.autoScroll();
                }, 100) 
        },
        updateStoringItem(){
          this.saleItems= this.saleItems.map((f)=>{
                if(f.id ===this.store_item.id){
                    return {...f, 
                        sale_qty : f.sale_qty + 1,
                        amount: f.amount + f.qty_sale_price,
                        final_amount: f.final_amount + f.qty_sale_price,
                        }
                }else{
                    return {...f}
                }
            })
            this.calTotal();
        },
        calPaid(){
            this.change=this.paid - this.net_amount > 0 ? this.paid - this.net_amount : 0;
            this.credit=this.paid - this.net_amount < 0 ? Math.abs(this.paid - this.net_amount) : 0;
        },
       
         editShowTime(t){
            const date=t.toDate()
            //const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },
      
    },
    
}
</script>
<style lang="">
 
</style>