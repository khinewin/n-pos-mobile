<template lang="">
    <div class="container-fluid my-4">   
        <div class="row fixed-top ps-2 pt-2">
            <div class="col-2">
                <button class="btn" @click="goBack"><i class="fa-solid fa-circle-chevron-left"></i></button>
            </div>
            <div class="col-2">
                <button class="btn" @click="print"><i class="fa-solid fa-print"></i></button>
            </div>    
        </div>      
        <div class="row"  id="printMe" >
            <div class="d-none d-print-block">
                <LargePrintHeader />
            </div>
            <div class="col-12 text-center">
                <h6>Reports purchase {{displayReportType}}</h6>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="" v-if="code_no">Code : {{item_name}}</div>
                    </div>
                    <div class="col-sm-2">
                        <div v-if="supplier" class="" v-is="supplier_name">Supplier : {{supplier_name}} ({{supplier_company}}), </div>
                    </div>
                    <div class="col-sm-2">
                        <div>Location : {{this.location}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div v-if="displayFromDate || displayToDate">Date : From {{showTime(displayFromDate)}} - To {{showTime(displayToDate)}}</div>
                    </div>
                </div>    
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <PurchaseByDate :fromDate="fromDate" :supplier="supplier" :toDate="toDate" :location="location" :code_no="code_no"  />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <PurchaseByVoucher :supplier="supplier"  :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no"  :location="location" :code_no="code_no"  />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <PurchaseByDescription :supplier="supplier" :fromDate="fromDate"  :toDate="toDate"  :location="location" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_supplier'">
                        <PurchaseBySupplier :supplier="supplier" :fromDate="fromDate"  :toDate="toDate"  :location="location" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_supplier_date'">
                        <PurchaseBySupplierDate :supplier="supplier" :fromDate="fromDate"  :toDate="toDate"  :location="location" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_exp_date'">
                        <PurchaseByExpDate  :fromDate="fromDate"  :toDate="toDate" :supplier="supplier"  :location="location" :code_no="code_no" />
                    </div>
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import PurchaseByDate from "@/views/reports/purchase/PurchaseByDate.vue"
import PurchaseByVoucher from "@/views/reports/purchase/PurchaseByVoucher.vue"
import PurchaseByDescription from "@/views/reports/purchase/PurchaseByDescription.vue"
import PurchaseBySupplier from "@/views/reports/purchase/PurchaseBySupplier.vue"
import PurchaseBySupplierDate from "@/views/reports/purchase/PurchaseBySupplierDate.vue"
import PurchaseByExpDate from "@/views/reports/purchase/PurchaseByExpDate.vue"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"

export default {
    components: {
       PurchaseByDate, PurchaseByDescription, PurchaseByVoucher, PurchaseBySupplier,
       PurchaseBySupplierDate, PurchaseByExpDate, LargePrintHeader
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],
            fromDate:this.$route.params['fromDate'],
            location: this.$route.params['location'],
            toDate:this.$route.params['toDate'],
            voucher_no: this.$route.query.voucher_no,
            supplier:this.$route.query.supplier,
            supplier_name:this.$route.query.supplier_name,
            supplier_company:this.$route.query.supplier_company,
            code_no: this.$route.query.code_no,
            item_name: this.$route.query.item_name,   
            displayFromDate:"",
            displayToDate:"",
            displayReportType:"",
            reports:[],
            current_month:[],
            salesData:[],
            isLoading:false,
            msg : {
                type :"",
                info :"",
            },       

        }
    },
   
    mounted() {    
      
            this.displayFromDate=Timestamp.fromDate(new Date(this.fromDate))
            this.displayToDate=Timestamp.fromDate(new Date(this.toDate))
            if(this.reportType==="by_date"){
                this.displayReportType="by Date"
            }else 
            if(this.reportType==='by_voucher'){
                this.displayReportType="by Voucher"
            }else
            if(this.reportType==='by_description'){
                this.displayReportType="by Description"
            }else
            if(this.reportType==='by_supplier'){
                this.displayReportType="by Supplier"
            }else
            if(this.reportType==='by_supplier_date'){
                this.displayReportType="by Supplier and Date"
            }else
            if(this.reportType==='by_exp_date'){
                this.displayReportType="by Expire Date"
            }
    },
   watch:{
   
    msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                        this.msg.info="";
                    }, 5000)
                }
            },deep:true
        },
      
   },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        report_type(){
            return this.$store.getters.reportType;
        },
      
    },
    methods:{
        async print () {
        // Pass the element id here
         await this.$htmlToPaper('printMe');
        },
        goBack(){
            this.$router.go(-1)
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
