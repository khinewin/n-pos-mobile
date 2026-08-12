<template>
    <div>           
        <PageHeader :page-title="pageTitle" :msg-type="msg.type" :msg-info="msg.info" />
        
        <!-- Modern Sticky Toolbar -->
        <div class="container-fluid">
            <div class="container-fluid bg-light border-bottom sticky-top py-2 shadow-sm rounded-3 mb-2">
            <div class="row g-2 justify-content-end align-items-center">
                <div class="col-auto me-auto">
                    <button class="btn btn-light text-primary shadow-sm fw-bold border-0" data-bs-toggle="modal" data-bs-target="#customerModal">
                        <i class="fa-solid fa-user-plus me-2"></i> {{t('open_link')}}
                    </button>
                    <span v-if="customer.name" class="ms-3 badge bg-white text-dark shadow-sm py-2 px-3 rounded-pill border">
                        <i class="fa-solid fa-user me-2 text-primary"></i> {{customer.name}}
                    </span>
                </div>
                <div class="col-auto">
                    <button class="btn btn-light shadow-sm text-dark fw-medium border-0" :disabled="!customer.code" @click="print">
                        <i class="fa-solid fa-print me-2"></i> {{t('print_link')}}
                    </button>
                </div>
                <div class="col-auto">
                    <button type="button" @click="savePaid" class="btn btn-primary shadow-sm px-4 fw-bold border-0" :disabled="isLoading || !paid_for || !paid_amount">
                        <span v-if="!isLoading"><i class="fa-solid fa-hard-drive me-2"></i> {{t('save_link')}}</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>
                </div>
                <div class="col-auto">
                    <button @click="closePage" class="btn btn-light text-danger shadow-sm fw-medium border-0" :disabled="!customer.id">
                        <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                    </button>
                </div>
            </div>
        </div>
        </div>

        <div class="container-fluid my-2">
            <div class="row g-2">
                <!-- Left Sidebar: Customer Summary & Payment Form -->
                <div class="col-lg-4">
                    <!-- Customer Details Card -->
                    <div class="card border-0 shadow-sm rounded-3 mb-2" v-if="customer.id">
                        <div class="card-body p-2">
                            <h6 class="fw-bold text-muted text-uppercase small mb-3">
                                <i class="fa-solid fa-address-card me-2"></i>Customer Info
                            </h6>
                            <div class="d-flex align-items-center mb-2">
                                <div class="bg-primary-subtle text-primary rounded-circle p-3 me-3">
                                    <i class="fa-solid fa-user-tie fa-lg"></i>
                                </div>
                                <div>
                                    <div class="fw-bold fs-5">{{customer.name}}</div>
                                    <div class="text-muted small"><i class="fa-solid fa-location-dot me-1"></i> {{customer.address || 'No address'}}</div>
                                </div>
                            </div>
                            <div class="row g-2 text-center">
                                <div class="col-6">
                                    <div class="p-2 border rounded bg-light">
                                        <div class="small text-muted text-uppercase fw-bold" style="font-size: 0.65rem;">Total Credit</div>
                                        <div class="fw-bold text-danger">{{fM(total_credit)}}</div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="p-2 border rounded bg-light">
                                        <div class="small text-muted text-uppercase fw-bold" style="font-size: 0.65rem;">Total Paid</div>
                                        <div class="fw-bold text-success">{{fM(total_paid)}}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Payment Form Card -->
                    <div class="card border-0 shadow-sm rounded-3">
                        <div class="card-body p-2">
                            <h6 class="fw-bold text-muted text-uppercase small mb-3">
                                <i class="fa-solid fa-money-bill-transfer me-2"></i>Make a Payment
                            </h6>
                            
                            <div class="mb-3">
                                <label class="small text-muted text-uppercase fw-bold mb-1">{{t('pay_for')}} (Voucher)</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-hashtag text-secondary"></i></span>
                                    <select 
                                        class="form-select bg-light border-0 fw-medium" 
                                        v-model="paid_for" 
                                        v-if="saleItemsData.length" 
                                        @change="calAmount"
                                        :disabled="!customer.id">
                                        <option value="" disabled>Select Voucher</option>
                                        <option v-for="si in saleItemsData.filter(v => v.credit > 0)" :key="si.id" :value="si.voucher_no">
                                            {{si.voucher_no}} (Bal: {{fM(si.credit)}})
                                        </option>
                                    </select>
                                    <input v-else type="text" class="form-control bg-light border-0" value="No credit vouchers" disabled>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="small text-muted text-uppercase fw-bold mb-1">{{t('date')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-calendar-days text-secondary"></i></span>
                                    <flat-pickr 
                                        :disabled="!paid_for" 
                                        class="form-control bg-light border-0 fw-medium" 
                                        v-model="paid_date"  
                                        :config="config" />
                                </div>
                            </div>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label class="small text-muted text-uppercase fw-bold mb-0">{{t('paid_amount')}}</label>
                                    <button type="button" @click="fillVoucherCredit" class="btn btn-sm btn-link p-0 text-decoration-none small text-success" :disabled="!paid_for">
                                        Fill Voucher Due ({{fM(selectedVoucherCredit)}})
                                    </button>
                                </div>
                                <div class="input-group shadow-sm">
                                    <span class="input-group-text bg-success text-white border-0"><i class="fa-solid fa-dollar-sign"></i></span>
                                    <input 
                                        @focus="setInput" 
                                        @click="setInput" 
                                        @blur="handleBlurPaid" 
                                        @keypress.enter="savePaid" 
                                        v-model="paid_amount" 
                                        :disabled="!paid_for" 
                                        type="number" 
                                        class="form-control border-0 fw-bold text-success fs-5"
                                        placeholder="0">
                                </div>
                            </div>

                            <button 
                                @click="savePaid" 
                                class="btn btn-success w-100 py-2 fw-bold shadow-sm"
                                :disabled="!paid_for || !paid_amount">
                                <i class="fa-solid fa-check-circle me-2"></i> Confirm Payment
                            </button>
                        </div>
                    </div>

                    <!-- Bulk Payment (FIFO) Card -->
                    <div class="card border-0 shadow-sm rounded-3 mt-3">
                        <div class="card-body p-3">
                            <h6 class="fw-bold text-primary text-uppercase small mb-3">
                                <i class="fa-solid fa-layer-group me-2"></i>{{t('pay_all_vouchers')}}
                            </h6>

                            <div class="mb-3">
                                <label class="small text-muted text-uppercase fw-bold mb-1">{{t('date')}}</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-0"><i class="fa-solid fa-calendar-days text-secondary"></i></span>
                                    <flat-pickr 
                                        :disabled="!customer.id" 
                                        class="form-control bg-light border-0 fw-medium" 
                                        v-model="bulk_paid_date"  
                                        :config="config" />
                                </div>
                            </div>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <label class="small text-muted text-uppercase fw-bold mb-0">{{t('paid_amount')}}</label>
                                    <button type="button" @click="fillTotalCredit" class="btn btn-sm btn-link p-0 text-decoration-none small text-primary" :disabled="!customer.id">
                                        Fill Total Due ({{fM(total_credit)}})
                                    </button>
                                </div>
                                <div class="input-group shadow-sm">
                                    <span class="input-group-text bg-primary text-white border-0"><i class="fa-solid fa-dollar-sign"></i></span>
                                    <input 
                                        @focus="setBulkInput" 
                                        @click="setBulkInput" 
                                        @blur="handleBlurBulkPaid" 
                                        @keypress.enter="saveBulkPaid" 
                                        v-model="bulk_paid_amount" 
                                        :disabled="!customer.id" 
                                        type="number" 
                                        class="form-control border-0 fw-bold text-primary fs-5"
                                        placeholder="0">
                                </div>
                            </div>

                            <button 
                                @click="saveBulkPaid" 
                                class="btn btn-primary w-100 py-2 fw-bold shadow-sm"
                                :disabled="!customer.id || !bulk_paid_amount || isBulkLoading">
                                <span v-if="!isBulkLoading"><i class="fa-solid fa-hard-drive me-2"></i> {{t('bulk_pay')}}</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isBulkLoading"></span>
                            </button>
                        </div>
                    </div>
                </div>

                <!-- Right Side: Vouchers List -->
                <div class="col-lg-8">
                    <div class="card border-0 shadow-sm rounded-3 vh-75">
                        <div class="card-header bg-white border-0 pt-2">
                            <div class="row align-items-center">
                                <div class="col">
                                    <h6 class="fw-bold text-muted text-uppercase small mb-0">
                                        <i class="fa-solid fa-list-ul me-2"></i>Voucher History
                                    </h6>
                                </div>
                                <div class="col-auto d-flex gap-2">
                                    <!-- Display Mode Toggle -->
                                    <div class="btn-group btn-group-sm shadow-sm" role="group">
                                        <input type="radio" class="btn-check" name="vtype" id="v_credit" value="Credit" v-model="display_type" @change="filterData">
                                        <label class="btn btn-outline-primary px-3" for="v_credit">{{t('balance_due')}}</label>
                                        
                                        <input type="radio" class="btn-check" name="vtype" id="v_paid" value="Paid" v-model="display_type" @change="filterData">
                                        <label class="btn btn-outline-primary px-3" for="v_paid">{{t('paid')}}</label>
                                    </div>
                                    <!-- Limit Selector -->
                                    <select class="form-select form-select-sm shadow-sm border-0 bg-light" v-model="display_item" @change="filterData" style="width: 80px;">
                                        <option :value="10">10</option>
                                        <option :value="25">25</option>
                                        <option :value="50">50</option>
                                        <option :value="100">100</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="card-body p-0 vh-65 overflow-y-auto">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="sticky-top z-3 bg-light text-nowrap">
                                    <tr class="table-light small fw-bold text-uppercase text-muted">
                                        <th class="text-center py-3 border-0" style="width: 50px;">
                                            <input type="checkbox" class="form-check-input shadow-none" v-model="check_all" @click="handleCheckAll">
                                        </th>
                                        <th class="border-0">{{t('invoice_no')}}</th>
                                        <th class="border-0">{{t('date')}}</th>
                                        <th class="border-0 text-end">{{t('amount')}}</th>
                                        <th class="border-0 text-end text-danger">{{t('balance_due')}}</th>
                                        <th class="border-0 text-center" style="width: 40px;"></th>
                                    </tr>
                                </thead>
                                <tbody class="small" v-for="s in saleItems" :key="s.id">
                                    <!-- Main Voucher Row -->
                                    <tr class="fw-medium">
                                        <td class="text-center">
                                            <input type="checkbox" class="form-check-input shadow-none" v-model="s.is_check" @click="addToPrint(s)">
                                        </td>
                                        <td class="fw-bold text-primary">{{s.voucher_no}}</td>
                                        <td>{{showTimeHour(s.sale_date)}}</td>
                                        <td class="text-end fw-bold">{{fM(s.net_amount)}}</td>
                                        <td class="text-end fw-bold">
                                            <span :class="s.credit > 0 ? 'text-danger' : 'text-success'">
                                                {{fM(s.credit)}}
                                            </span>
                                        </td>
                                        <td class="text-center">
                                            <button class="btn btn-sm btn-light p-0 border-0 opacity-50" v-if="(s.debt_payments && s.debt_payments.length) || s.paid" type="button" data-bs-toggle="collapse" :data-bs-target="'#history-' + s.id">
                                                <i class="fa-solid fa-chevron-down"></i>
                                            </button>
                                        </td>
                                    </tr>
                                    <!-- Debt Payment History (Nested) -->
                                    <tr class="collapse bg-light" :id="'history-' + s.id">
                                        <td colspan="6" class="p-0 border-top-0">
                                            <div class="p-3 bg-white border mx-3 my-2 rounded-3 shadow-sm">
                                                <h6 class="small fw-bold text-muted mb-2 text-uppercase" style="font-size: 0.7rem;">Payment History</h6>
                                                <table class="table table-sm table-borderless mb-0 align-middle">
                                                    <tbody class="small">
                                                        <tr v-if="s.paid" class="border-bottom-dashed">
                                                            <td class="text-muted"><i class="fa-solid fa-clock-rotate-left me-2"></i>{{showTimeHour(s.sale_date)}}</td>
                                                            <td class="fw-bold">Initial Payment</td>
                                                            <td class="text-end text-success fw-bold">+ {{fM(s.paid)}}</td>
                                                        </tr>
                                                        <tr v-for="dp in (s.debt_payments || [])" :key="dp.paid_date" class="border-bottom-dashed">
                                                            <td class="text-muted"><i class="fa-solid fa-clock-rotate-left me-2"></i>{{showTimeHour(dp.paid_date)}}</td>
                                                            <td class="fw-bold">Debt Payment</td>
                                                            <td class="text-end text-success fw-bold">+ {{fM(dp.paid)}}</td>
                                                        </tr>
                                                        <tr class="table-light">
                                                            <td colspan="2" class="text-end fw-bold">Total Collected for this voucher</td>
                                                            <td class="text-end fw-bold text-success">{{fM(s.total_paid)}}</td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                                <!-- Empty State -->
                                <tbody v-if="!isLoading && saleItems.length === 0">
                                    <tr>
                                        <td colspan="6" class="text-center py-5 text-muted">
                                            <i class="fa-solid fa-file-invoice fa-3x mb-3 opacity-25"></i>
                                            <div class="h6 mb-1">No vouchers found</div>
                                            <p class="small mb-0">Try selecting a different customer or adjusting filters</p>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <!-- Sticky Footer Summary inside the card -->
                        <div class="card-footer bg-light border-0 p-3" v-if="saleItems.length > 0">
                            <div class="row g-3 justify-content-end text-end">
                                <div class="col-auto">
                                    <div class="small text-muted text-uppercase fw-bold" style="font-size: 0.7rem;">Combined Total</div>
                                    <div class="h5 fw-bold mb-0 text-dark">{{fM(total_amount)}}</div>
                                </div>
                                <div class="col-auto border-start ps-3">
                                    <div class="small text-muted text-uppercase fw-bold" style="font-size: 0.7rem;">Total Collected</div>
                                    <div class="h5 fw-bold mb-0 text-success">{{fM(total_paid)}}</div>
                                </div>
                                <div class="col-auto border-start ps-3">
                                    <div class="small text-muted text-uppercase fw-bold" style="font-size: 0.7rem;">Total Balance Due</div>
                                    <div class="h5 fw-bold mb-0 text-danger">{{fM(total_credit)}}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <CustomerModal @setCustomer="setCustomer" />      
        <div class="container-fluid d-none" id="printMe">
            <PrintSoc :customer="customer" :saleItems="saleItems" :total_amount="total_amount" :total_paid="total_paid" :total_credit="total_credit" />
        </div>     
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {disableNetwork, Timestamp, collection, addDoc, doc, increment,  updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
import store from "@/store"
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import CustomerModalOld from "@/views/modals/sales/CustomerModalOld.vue"
import Print from "@/views/sales/Print.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/sales"
import PrintSoc from "@/views/sales/PrintSoc.vue"
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"
import {lang} from "@/lib/language"

export default {
    components: {
        CustomerModal: store.getters.theme === "new" ? CustomerModal : CustomerModalOld,
        Print, 
        PrintSoc, 
        PageHeader, 
        FlatPickr
    },
    data(){
        return{    
           
            display_item: 10,
            isLoading:false,
            customer:{},
            saleItems:[],
            saleItemsData:[],
            total_amount:0,
            total_paid:0,
            total_credit:0,          
            display_type: "Credit",
            check_all: true,
            printData:{              
                print_date:"",

            },
            msg : {
                type :"",
                info :"",
            },

            paid_date:"",
            paid_for:"",
            paid_amount: 0,
            bulk_paid_date:"",
            bulk_paid_amount: 0,
            isBulkLoading: false
             
        }
    },
   
   
    mounted() {
        this.voucher_no=new Date().getTime();
        this.paid_date=hp.setToday();
        this.bulk_paid_date=hp.setToday();
        window.addEventListener('keydown', this.handleCtrl);
    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handleCtrl);
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
    computed:{
        user(){
            return this.$store.getters.user;
        },
        config(){
            return this.$store.getters.flat_pickr_config;
        },
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.soc.mm}` : `${lang.soc.eng}`            
            return pageTitle;
        },      
         t() {
            return (key) => this.$store.getters.t(key);
        },
        selectedVoucherCredit() {
            if (!this.paid_for) return 0;
            const found = this.saleItemsData.find(f => f.voucher_no == this.paid_for);
            return found ? found.credit : 0;
        }
      
    },
    methods:{
        fillVoucherCredit() {
            if (!this.paid_for) return;
            const found = this.saleItemsData.find(f => f.voucher_no == this.paid_for);
            if (found) {
                this.paid_amount = found.credit;
            }
        },
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.savePaid(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.closePage();
            }
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.print(); // Call your custom function
            }
            return hc.handleCtrl(event)
         
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "sales"), where("voucher_no", "==",this.paid_for));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {   
                            if(change.type==="modified"){
                                        this.msg.info="Making debt payment has been saved"
                                        this.msg.type="success"                                        
                                        this.paid_amount=0;
                                        this.paid_for="";
                                
                            }
                    });                                              
            });                              
        } ,
        longTimeFormat(t){
            return hp.longTimeFormat(t);
        },
        clearPrintData(){
            this.printData.saleItems=[]
            this.printData.total_credit=0;
        },
        handleCheckAll(){
          setTimeout(()=>{
            this.saleItems.map((s)=>{
                   return  s.is_check=this.check_all ? true: false;
            }, 100)
           this.addToPrint();
          })
        },
        addToPrint(s){                
                setTimeout(()=>{
                    const items=this.saleItems.filter((f)=>f.is_check)
                    this.printData.saleItems=items;
                    this.printData.total_credit=0;
                    items.map((item)=>{
                        this.printData.total_credit += item.credit
                    })
                 
                }, 50)            
        },

        async savePaid(){
            if(!this.paid_for || !this.paid_amount) return false;       
            this.isLoading = true;
            const paid_date=hp.insertDate(this.paid_date)

            const voucher={
                    paid_date: Timestamp.fromDate(paid_date),
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    paid:this.paid_amount,                 
                }        

            const cData=this.saleItemsData.filter((f)=>f.voucher_no == this.paid_for)
            const data={
                credit: cData[0].credit - this.paid_amount,
                debt_payments: [...(cData[0].debt_payments || []), voucher]
            }
            const id=cData[0].id;
            const saveDocument=doc(db, "sales",id)
            this.checkDatabase();
            await updateDoc(saveDocument, data).then(() => {
                this.isLoading = false;
            }).catch(() => {
                this.isLoading = false;
            });       
        },
        fillTotalCredit() {
            this.bulk_paid_amount = this.saleItemsData
                .filter((f) => f.credit > 0)
                .reduce((acc, cur) => acc + cur.credit, 0);
        },
        async saveBulkPaid() {
            if (!this.customer.id || !this.bulk_paid_amount || Number(this.bulk_paid_amount) <= 0) return false;
            this.isBulkLoading = true;
            try {
                const paid_date = hp.insertDate(this.bulk_paid_date);
                const paidTimestamp = Timestamp.fromDate(paid_date);
                const userObj = { uid: this.user.uid, displayName: this.user.displayName };
                let remainingPaid = Number(this.bulk_paid_amount);

                const creditVouchers = [...this.saleItemsData]
                    .filter((f) => f.credit > 0)
                    .sort((a, b) => {
                        const dateA = a.sale_date?.toDate ? a.sale_date.toDate().getTime() : (new Date(a.sale_date).getTime() || 0);
                        const dateB = b.sale_date?.toDate ? b.sale_date.toDate().getTime() : (new Date(b.sale_date).getTime() || 0);
                        return dateA - dateB;
                    });

                if (creditVouchers.length === 0) {
                    this.msg.info = "No unpaid credit vouchers found for this customer";
                    this.msg.type = "warning";
                    this.isBulkLoading = false;
                    return;
                }

                for (const v of creditVouchers) {
                    if (remainingPaid <= 0) break;

                    const payForThisVoucher = Math.min(remainingPaid, v.credit);
                    remainingPaid -= payForThisVoucher;

                    const voucherRecord = {
                        paid_date: paidTimestamp,
                        user: userObj,
                        paid: payForThisVoucher
                    };

                    const updateData = {
                        credit: v.credit - payForThisVoucher,
                        debt_payments: [...(v.debt_payments || []), voucherRecord]
                    };

                    const saveDocument = doc(db, "sales", v.id);
                    await updateDoc(saveDocument, updateData);
                }

                this.msg.info = "Bulk debt payment has been saved";
                this.msg.type = "success";
                this.bulk_paid_amount = 0;
            } catch (error) {
                console.error("Error saving bulk payment:", error);
                this.msg.info = "Failed to save bulk debt payment";
                this.msg.type = "danger";
            } finally {
                this.isBulkLoading = false;
            }
        },
       
        filterData(){
            if(this.display_type=="Paid"){
                let saleItems=this.saleItemsData.filter((f)=>f.credit <= 0).reverse()
                saleItems=saleItems.filter((s,i)=>{
                    if(i < this.display_item){
                        return s;
                    }
                })
                this.saleItems=saleItems;
            }  else if(this.display_type=="Credit"){
                let saleItems=this.saleItemsData.filter((f)=>f.credit > 0).reverse()
                saleItems=saleItems.filter((s,i)=>{
                    if(i < this.display_item){
                        return s;
                    }
                })
                this.saleItems=saleItems;
            }
            this.saleItems=this.saleItems.map((s)=>{
                return {...s, is_check: true}
            })
            this.handleCheckAll();
            this.calTotal();

        },
        calTotal(){
            let total_amount=0;
            let total_paid=0;
            let total_credit=0;
            this.saleItems=this.saleItems.map((s)=>{
                    const dp=(s.debt_payments || []).reduce((accu, cur)=>accu + cur.paid, 0)
                    total_amount += s.net_amount;
                    total_paid += (dp + s.paid);
                    total_credit += s.credit;
                    return {...s, "total_paid": dp+ s.paid}
            })
            this.total_amount=total_amount
            this.total_paid=total_paid
            this.total_credit=total_credit
       },
    
        fetchCreditSales(){
            this.isLoading = true;
            const   query_url = query(collection(db, "sales"), where("customer.id","==", this.customer.id), where("payment_type", "==", "Credit"), orderBy("sale_date", "asc"));          
            onSnapshot(query_url, async(snapShot) =>{
                    const items=ss.sales(snapShot)       
                    this.saleItemsData=items;    
                    this.filterData();
                    this.isLoading = false;
            });  
        },
        calAmount(){
            this.saleItemsData.filter((f)=>{
                if(f.voucher_no==this.paid_for){
                    this.paid_amount=f.credit;
                }
            })
        },
        setInput(e) {
            if (this.paid_amount == 0 || e.target.value == 0) {
                this.paid_amount = "";
            }
            try {
                e.target.select();
            } catch (err) {}
        },
        handleBlurPaid() {
            if (this.paid_amount === "" || this.paid_amount === null || this.paid_amount === undefined) {
                this.paid_amount = 0;
            }
        },
        setBulkInput(e) {
            if (this.bulk_paid_amount == 0 || e.target.value == 0) {
                this.bulk_paid_amount = "";
            }
            try {
                e.target.select();
            } catch (err) {}
        },
        handleBlurBulkPaid() {
            if (this.bulk_paid_amount === "" || this.bulk_paid_amount === null || this.bulk_paid_amount === undefined) {
                this.bulk_paid_amount = 0;
            }
        },
        closePage(){
            this.customer={},
            this.saleItemsData=[]
            this.saleItems=[]
            this.total_amount=0;
            this.total_paid=0;
            this.total_credit=0;
            this.paid_amount=0;
            this.paid_for="";
            this.paid_date=hp.setToday();
            this.bulk_paid_amount=0;
            this.bulk_paid_date=hp.setToday();
          
            this.clearPrintData();
        },
       setCustomer(c){
            this.customer=c;
            this.fetchCreditSales();
            this.paid_date=hp.setToday();
            this.bulk_paid_date=hp.setToday();
            this.bulk_paid_amount=0;
            this.clearPrintData();
          //   this.checkDatabase();
       },   
       
   
        async print () {
        // Pass the element id here
        if(!this.customer.code) return false;
        if(this.printData.saleItems.length <=0) return false;
         await this.$htmlToPaper('printMe');
        },
     
        showTimeHour(t){
            return hp.showTimeHour(t);
        }
      
    },
    
}
</script>
<style>
.border-bottom-dashed {
    border-bottom: 1px dashed #dee2e6;
}
.vh-65 {
    height: 65vh;
}
</style>
