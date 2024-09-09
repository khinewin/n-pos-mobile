<template lang="">
    <div>           
        <PageHeader page-title="SOC" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                                     
                <div class="col-sm-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#customerModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-sm-2 d-grid">
                    <button  class="btn btn-sm"  @click="print"><i class="fa-solid fa-print" ></i> Print</button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="savePaid" class="btn btn-sm" v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <button @click="closePage" class="btn btn-sm" :disabled="!customer.id"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                </div>
            </div>
        </div>     
        <div class="container-fluid my-2">
            <div class="card shadow">
                <div class="card-body p-2">
                    <div class="row justify-content-end">
                        <div class="col-8">
                                <table class="table  table-borderless mb-0">
                                    <tbody>
                                        <tr>
                                            <td class="col-1 p-0">
                                                <select class="form-control-plaintext text-center" v-model="display_type" @change="filterData">
                                                    <option>All</option>
                                                    <option>Paid</option>
                                                    <option>Credit</option>
                                                </select>
                                            </td>                                          

                                            <td class="text-end col-1">Customer :</td>
                                            <td class="col-1">{{customer.name}}</td>
                                            <td class="text-end col-1">Phone :</td>
                                            <td class="col-1">{{customer.phone}}</td>
                                            <td class="text-end col-1">Code :</td>
                                            <td class="col-1">{{customer.code}}</td>
                                        </tr>
                                    </tbody>
                                </table>
                        </div>
                    </div>
                <div class="row g-2">
                    <div class="col-7">
                        <div class="card">
                            <div class="card-body p-2 vh-65 overflow-y-auto">
                                    <table class="table table-hover table-bordered mb-0">
                                        <tbody>
                                            <tr class="text-center table-secondary fw-bold">
                                                <td class="text-center col-1"><input type="checkbox" v-model="check_all" @click="handleCheckAll"></td>
                                                <td class="col-2">Voucher No.</td>
                                                <td  class="col-2">Date</td>
                                                <td  class="col-2">Amount</td>
                                                <td  class="col-2">Paid</td>
                                                <td  class="col-2">Credit</td>
                                            </tr>
                                            <tr v-for="s in saleItems" :key="s.id">
                                                <td class="text-center"><input type="checkbox" v-model="s.is_check" @click="addToPrint(s)"></td>
                                                <td class="text-center">{{s.voucher_no}}</td>
                                                <td class="text-center">{{showTimeHour(s.sale_date)}}</td>
                                                <td class="text-end">{{s.net_amount}}</td>
                                                <td class="text-end">{{s.paid}}</td>
                                                <td class="text-end">{{s.credit}}</td>
                                            </tr>
                                            <tr class="text-end">
                                                <td colspan="3">Total</td>
                                                <td>{{total_amount}}</td>
                                                <td>{{total_paid}}</td>
                                                <td>{{total_credit}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-5">
                        <div class="card">
                            <div class="card-body p-2 vh-65 overflow-y-auto">
                                <table class="table table-hover      table-bordered ">
                                    <tbody>
                                        <tr class="text-center fw-bold table-secondary">
                                            <td class="col-1">Pay for</td>
                                            <td class="col-1">Date</td>
                                            <td class="col-1">Paid amount</td>
                                        </tr>
                                        <tr>
                                            <td class="p0">
                                                <select class="form-control-plaintext text-center" v-model="paid_for" v-if="saleItems.length" @change="calAmount">
                                                    <option  v-for="si in saleItems" :key="si.id">{{si.voucher_no}}</option>
                                                </select>
                                            </td>
                                            <td class="p-0"><input v-model="paid_date" :disabled="!customer.id" type="date" class="form-control-plaintext text-center"></td>
                                            <td class="p-0"><input @keypress.enter="savePaid" v-model="paid_amount" :disabled="!customer.id" type="number" class="form-control-plaintext text-end"></td>
                                        </tr>
                                        <tr v-if="debtpayments.length" v-for="dp in debtpayments" :key="dp.id">
                                            <td class="text-center">{{dp.paid_for}}</td>
                                            <td class="text-center">{{showTimeHour(dp.paid_date)}}</td>
                                            <td class="text-end">{{dp.paid_amount}}</td>
                                        </tr>
                                        <tr>
                                            <td colspan="2" class="text-end">Total</td>
                                            <td class="text-end">{{total_debtpayment_amount}}</td>
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
            <PrintSoc :printData="printData" :customer="customer" />
        </div>     
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {disableNetwork, Timestamp, collection, addDoc, doc, increment,  updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'
import db from '@/firebase/firestore'
import CustomerModal from "@/views/modals/sales/CustomerModal.vue"
import Print from "@/views/sales/Print.vue"
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/sales"
import * as dp from "@/lib/snapshot/debtpayments"
import PrintSoc from "@/views/sales/PrintSoc.vue"


export default {
    components: {
        CustomerModal,Print ,PrintSoc, PageHeader
    },
    data(){
        return{    
          
            isLoading:false,
            customer:{},
            saleItems:[],
            saleItemsData:[],
            total_amount:0,
            total_paid:0,
            total_credit:0,
            debtpayments:[],
            debtpaymentsData:[],
            total_debtpayment_amount:0,
            display_type: "Credit",
            check_all: true,
            printData:{
                saleItems:[],
                debtpayments:[],
                total_credit:0,
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
        this.paid_date=new Date().toISOString().substr(0, 10)     
        this.setPrintDate();
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
        
      
    },
    methods:{
        async checkDatabase(){ 
            const query_url=query(collection(db, "debtPayment"), where("customer.id", "==", this.customer.id));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {        
                            if(change.type==="added"){
                                if(change.doc.data().paid_for==this.paid_for){
                                        this.msg.info="Making debt payment has been saved"
                                        this.msg.type="success"                                        
                                        this.paid_amount=0;
                                        this.paid_for="";
                                }
                            }
                    });                                              
            });                              
        } ,
        clearPrintData(){
            this.printData.saleItems=[]
            this.printData.debtpayments=[];
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

                   let dbs=[]
                    this.debtpaymentsData.filter((f)=>{
                            items.map((i)=>{
                                if(i.voucher_no==f.paid_for){
                                    dbs.push(f)
                                }
                            })
                    })
                    this.printData.debtpayments=dbs;
                    this.debtpayments=[]
                    this.debtpayments=dbs;
                    this.calTotal();
                }, 50)            
        },

        async savePaid(){
            if(!this.paid_for || !this.paid_amount) return false;
            let date=new Date();
            let paid_date=new Date(this.paid_date)
            paid_date.setHours(date.getHours())
            paid_date.setMinutes(date.getMinutes())
            const voucher={
                    paid_date: Timestamp.fromDate(paid_date),
                    user : {uid: this.user.uid, displayName: this.user.displayName},
                    paid_amount:this.paid_amount,
                    paid_for: this.paid_for,
                    customer: this.customer,
                }        
                this.modifyCreditSales();          
                const saveDocument=collection(db, "debtPayment")
                await addDoc(saveDocument, voucher)     
        },
        async modifyCreditSales(){
            const cData=this.saleItems.filter((f)=>f.voucher_no == this.paid_for)
            const data={
                paid: cData[0].paid + this.paid_amount,
                credit: cData[0].credit - this.paid_amount,
            }
            const id=cData[0].id;
            const saveDocument=doc(db, "creditSales",id)
            await updateDoc(saveDocument, data)     
        },
        filterData(){
            if(this.display_type=="All"){
                this.saleItems=this.saleItemsData
            } else if(this.display_type=="Paid"){
                this.saleItems=this.saleItemsData.filter((f)=>f.credit <= 0)
            }  else if(this.display_type=="Credit"){
                this.saleItems=this.saleItemsData.filter((f)=>f.credit > 0)
            }
            this.saleItems=this.saleItems.map((s)=>{
                return {...s, is_check: true}
            })
            this.debtpayments=[]
            this.handleCheckAll();
        },
        calTotal(){
                    this.total_amount=0;
                    this.total_credit=0;
                    this.total_paid=0;
                    this.saleItems.map((s)=>{                       
                        this.total_amount += s.net_amount;
                        this.total_paid += s.paid;
                        this.total_credit += s.credit;
                    })   

                    this.total_debtpayment_amount=0;
                    this.debtpayments.map((p)=>{
                        this.total_debtpayment_amount += p.paid_amount;
                    })
        },
        fetchDebtPayment(){
            const query_url = query(collection(db, "debtPayment"), where("customer.id","==", this.customer.id), orderBy("paid_date", "asc"));
            onSnapshot(query_url, async(snapShot) =>{
                    const items=dp.payments(snapShot)       
                    this.debtpaymentsData=items;     
                    this.filterData();                                                                                   
            });  
        },
        fetchCreditSales(){
            const   query_url = query(collection(db, "creditSales"), where("customer.id","==", this.customer.id), orderBy("sale_date", "asc"));          
            onSnapshot(query_url, async(snapShot) =>{
                    const items=ss.sales(snapShot)       
                    this.saleItemsData=items;    
            });  
            this.fetchDebtPayment();

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
            this.paid_date="";
            this.debtpayments=[]
            this.debtpaymentsData=[]
            this.clearPrintData();
        },
       setCustomer(c){
            this.customer=c;
            this.fetchCreditSales();
            this.paid_date=new Date().toISOString().substr(0, 10)   
            this.clearPrintData();
            this.checkDatabase();
       },
        setPrintDate(){
            const date=new Date();
            let print_date=new Date();
            print_date.setHours(date.getHours())
            print_date.setMinutes(date.getMinutes())
          this.printData.print_date=Timestamp.fromDate(print_date);
        },

       
   
        async print () {
        // Pass the element id here
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