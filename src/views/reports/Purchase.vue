<template lang="">
    <div class="container-fluid">   
        <div class="row">
            <div class="col-2">
                <button class="btn" @click="goBack">Back</button>
            </div>
            <div class="col-2">
                <button class="btn" @click="print">Print</button>
            </div>
        </div>        
        <div class="row"  id="printMe" >
            <div class="col-12 text-center">
                <h3>N-POS</h3>
                <h5>Reports purchase {{displayReportType}}</h5>
                <h6 v-if="displayFromDate || displayToDate">From {{showTime(displayFromDate)}} To {{showTime(displayToDate)}}</h6>
                <h5 v-if="supplier">{{supplier_name}} ({{supplier_company}})</h5>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <PurchaseByDate :fromDate="fromDate" :supplier="supplier" :toDate="toDate" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <PurchaseByVoucher :supplier="supplier"  :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no" />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <PurchaseByDescription :supplier="supplier" :fromDate="fromDate"  :toDate="toDate"/>
                    </div>
                    <div v-if="reportType==='by_supplier'">
                        <PurchaseBySupplier :supplier="supplier" :fromDate="fromDate"  :toDate="toDate"/>
                    </div>
                    <div v-if="reportType==='by_supplier_date'">
                        <PurchaseBySupplierDate :supplier="supplier" :fromDate="fromDate"  :toDate="toDate"/>
                    </div>
                    <div v-if="reportType==='by_exp_date'">
                        <PurchaseByExpDate  :fromDate="fromDate"  :toDate="toDate" :supplier="supplier"/>
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

export default {
    components: {
       PurchaseByDate, PurchaseByDescription, PurchaseByVoucher, PurchaseBySupplier,
       PurchaseBySupplierDate, PurchaseByExpDate
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],
            fromDate:this.$route.params['fromDate'],
            toDate:this.$route.params['toDate'],
            voucher_no: this.$route.query.voucher_no,
            supplier:this.$route.query.supplier,
            supplier_name:this.$route.query.supplier_name,
            supplier_company:this.$route.query.supplier_company,
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
