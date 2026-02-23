<template lang="">
    <div>           
        <PageHeader :page-title="pageTitle" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                                     
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm open" data-bs-toggle="modal" data-bs-target="#customerModal"><i class="fa-solid fa-folder-tree"></i> {{t('open_link')}}</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button  class="btn btn-sm" :disabled="!customer.code"  @click="print"><i class="fa-solid fa-print" ></i> {{t('print_link')}}</button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button type="button" @click="savePaid" class="btn btn-sm" v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> {{t('save_link')}}
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                        <button @click="closePage" class="btn btn-sm close" :disabled="!customer.id"><i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}}</button>
                </div>
            </div>
        </div>     
        <div class="container-fluid my-2">
            <div class="card shadow">
                <div class="card-body p-2 small">
                    <div class="row justify-content-end  mb-2">
                        <div class="col-6 col-sm-4 col-md-3">
                            <input @change="filterData" @keyup="filterData" type="number" class="form-control-plaintext py-0 text-center" v-model="display_item">
                        </div>
                        <div class="col-6 col-sm-4 col-md-3">
                            <select class="form-control-plaintext  py-0 text-center" v-model="display_type" @change="filterData">
                                <option value="Paid">{{t('paid')}}</option>
                                <option value="Credit">{{t('balance_due')}}</option>
                            </select>
                        </div>
                        <div class="col-6 col-sm-4 col-md-3">
                            <span class="fw-bold">{{t('customer')}} : </span><span>{{customer.name}}</span>
                        </div>
                        <div class="col-6 col-sm-4 col-md-3">
                            <span class="fw-bold">{{t('address')}} : </span><span>{{customer.address}}</span>
                        </div>                                           
                    </div>
                <div class="row g-2">
                    <div class="col-12 col-md-7">
                        <div class="card">
                            <div class="card-body p-2 vh-65 overflow-y-auto">
                                    <table class="table table-hover table-bordered mb-0">
                                        <tbody>
                                            <tr class="text-center table-secondary fw-bold">
                                                <td class="text-center col-0"><input type="checkbox" v-model="check_all" @click="handleCheckAll"></td>
                                                <td class="col-3">{{t('invoice_no')}}</td>
                                                <td  class="col-3">{{t('date')}}</td>
                                                <td  class="col-3">{{t('amount')}}</td>
                                                <td  class="col-3">{{t('balance_due')}}</td>
                                            </tr>
                                        </tbody>
                                        <tbody v-for="s in saleItems" :key="s.id" class="table-group-divider">
                                            <tr>
                                                <td class="text-center" style="border-bottom: 2px solid;"><input type="checkbox" v-model="s.is_check" @click="addToPrint(s)"></td>
                                                <td class="text-center" style="border-bottom: 2px solid;">{{s.voucher_no}}</td>
                                                <td class="text-center" style="border-bottom: 2px solid;">{{showTimeHour(s.sale_date)}}</td>
                                                <td class="text-end" style="border-bottom: 2px solid;">{{s.net_amount}}</td>
                                                <td class="text-end" style="border-bottom: 2px solid;">{{s.credit}}</td>
                                             </tr>
                                             <tr v-if="s.paid">
                                                <td colspan="2"></td>
                                                <td class="text-center">{{showTimeHour(s.sale_date)}}</td>
                                                <td class="text-end fw-bold">{{t('paid')}}</td>
                                                <td class="text-end">{{s.paid}}</td>
                                             </tr>
                                            <tr v-for="dp in s.debt_payments" :key="dp.paid_date">
                                                    <td colspan="2"></td>
                                                    <td class="text-center">{{showTimeHour(dp.paid_date)}}</td>
                                                    <td class="text-end fw-bold">{{t('paid')}}</td>
                                                    <td class="text-end">{{dp.paid}}</td>
                                                </tr>
                                                <tr>
                                                    <td colspan="3"></td>
                                                    <td class="text-end fw-bold">{{t('paid')}}</td>
                                                    <td class="text-end">{{s.total_paid}}</td>
                                                 </tr>
                                           
                                        </tbody>
                                        <tbody class="table-group-divider">
                                            <tr class="text-center fw-bold">
                                                <td colspan="2"></td>
                                                <td>{{t('total')}}</td>
                                                <td>{{t("total_paid")}}</td>
                                                <td>{{t('total_balance_due')}}</td>
                                            </tr>
                                            <tr class="text-end">
                                                <td colspan="2"></td>
                                                <td>{{total_amount}}</td>
                                                <td>{{total_paid}}</td>
                                                <td>{{total_credit}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-12 col-md-5">
                        <div class="card">
                            <div class="card-body p-2 max-vh-65 overflow-y-auto">
                                <table class="table table-hover      table-bordered  mb-0">
                                    <tbody>
                                        <tr class="text-center fw-bold table-secondary">
                                            <td class="col-1">{{t('pay_for')}}</td>
                                            <td class="col-1">{{t('date')}}</td>
                                            <td class="col-1">{{t('paid_amount')}}</td>
                                        </tr>
                                        <tr>
                                            <td class="">
                                                <select class="form-control-plaintext py-0 text-center" v-model="paid_for" v-if="saleItems.length" @change="calAmount">
                                                    <option  v-for="si in saleItems" :key="si.id">{{si.voucher_no}}</option>
                                                </select>
                                            </td>
                                            <td class="">
                                                <flat-pickr :disabled="!paid_for" class="form-control-plaintext py-0 text-center" v-model="paid_date"  :config="config" />

                                            </td>
                                            <td class=""><input @keypress.enter="savePaid" v-model="paid_amount" :disabled="!customer.id" type="number" class="form-control-plaintext py-0 text-end"></td>
                                        </tr>
                                      
                                    </tbody>
                                </table>
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
            paid_amount: 0
             
        }
    },
   
   
    mounted() {
        this.voucher_no=new Date().getTime();
        this.paid_date=hp.setToday();
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
      
    },
    methods:{
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
            const paid_date=hp.insertDate(this.paid_date)

            const voucher={
                    paid_date: Timestamp.fromDate(paid_date),
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    paid:this.paid_amount,                 
                }        

            const cData=this.saleItems.filter((f)=>f.voucher_no == this.paid_for)
            const data={
                credit: cData[0].credit - this.paid_amount,
                debt_payments: [...cData[0].debt_payments, voucher]
            }
            const id=cData[0].id;
            const saveDocument=doc(db, "sales",id)
            this.checkDatabase();
            await updateDoc(saveDocument, data)       
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
                    const dp=s.debt_payments.reduce((accu, cur)=>accu + cur.paid, 0)
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
            const   query_url = query(collection(db, "sales"), where("customer.id","==", this.customer.id), where("payment_type", "==", "Credit"), orderBy("sale_date", "asc"));          
            onSnapshot(query_url, async(snapShot) =>{
                    const items=ss.sales(snapShot)       
                    this.saleItemsData=items;    
                    this.filterData();
            });  
        },
        calAmount(){
            this.saleItems.filter((f)=>{
                if(f.voucher_no==this.paid_for){
                    this.paid_amount=f.credit;
                }
            })
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
          
            this.clearPrintData();
        },
       setCustomer(c){
            this.customer=c;
            this.fetchCreditSales();
            this.paid_date=hp.setToday();
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
<style lang="">
 
</style>