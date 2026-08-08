<template lang="">
    <div class="container-fluid my-4">   
        <div class="row fixed-top ps-2 pt-2">
            <div class="col-4 col-sm-3 col-md-2">
                <button class="btn" @click="goBack"><i class="fa-solid fa-circle-chevron-left"></i> (Esc)</button>
            </div>
            <div class="col-4 col-sm-3 col-md-2">
                <button class="btn" @click="print"><i class="fa-solid fa-print"></i> (ctrl+p)</button>
            </div>    
        </div>
        <div class="row"  id="printMe" >
            <div class="d-none d-print-block">
                <LargePrintHeader />
            </div>
            <div class="col-12 text-center">
                <h6>Reports Sales {{displayReportType(reportType)}}</h6>
                <div class="d-flex justify-content-evenly small">
                    <div class="">
                        <div class="" v-if="code_no">Code : {{item_name}}</div>
                    </div>
                    <div class="">
                        <div class="" v-if="customer_name">Customer : {{customer_name}}</div>
                    </div>
                    <div class="">
                        <div v-if="displayFromDate || displayToDate">Date : From {{showTime(displayFromDate)}} - To {{showTime(displayToDate)}}</div>
                    </div>
                </div>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <SalesByDate  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_month'">
                        <SalesByMonth  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_year'">
                        <SalesByYear  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_top_ten'">
                        <SalesByTopTen  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_top_ten_month'">
                        <SalesByTopTenMonth  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_top_ten_year'">
                        <SalesByTopTenYear  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <SalesByVoucher  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no" />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <SalesByDescription  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_description_date'">
                        <SalesByDescriptionDate  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_customer_date'">
                        <SalesByCustomerDate :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_customer'">
                        <SalesByCustomer  :fromDate="fromDate"  :toDate="toDate" :customer_code="customer_code" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_chart'">
                        <SalesByChart  :fromDate="fromDate"  :toDate="toDate" :customer_code="customer_code" :code_no="code_no"/>
                    </div>
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import SalesByDate from "@/views/reports/sales/SalesByDate.vue"
import SalesByMonth from "@/views/reports/sales/SalesByMonth.vue"
import SalesByYear from "@/views/reports/sales/SalesByYear.vue"
import SalesByTopTen from "@/views/reports/sales/SalesByTopTen.vue"
import SalesByTopTenMonth from "@/views/reports/sales/SalesByTopTenMonth.vue"
import SalesByTopTenYear from "@/views/reports/sales/SalesByTopTenYear.vue"
import SalesByVoucher from "@/views/reports/sales/SalesByVoucher.vue"
import SalesByDescription from "@/views/reports/sales/SalesByDescription.vue"
import SalesByCustomer from "@/views/reports/sales/SalesByCustomer.vue"
import SalesByCustomerDate from "@/views/reports/sales/SalesByCustomerDate.vue"
import SalesByChart from "@/views/reports/sales/SalesByChart.vue"
import SalesByDescriptionDate from "@/views/reports/sales/SalesByDescriptionDate.vue"

import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as rt from "@/lib/reportType"

export default {
    components: {
        SalesByDate,SalesByMonth,SalesByYear,SalesByTopTen,SalesByTopTenMonth,SalesByTopTenYear,SalesByVoucher,SalesByDescription, SalesByCustomer, SalesByCustomerDate,SalesByChart, LargePrintHeader,
        SalesByDescriptionDate
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],
            fromDate:this.parseYMD(this.$route.params['fromDate']),
            toDate:this.parseYMD(this.$route.params['toDate']),     
            voucher_no: this.$route.query.voucher_no,
            customer_name: this.$route.query.customer_name,
            customer_code: this.$route.query.customer_code,
            code_no: this.$route.query.code_no,
            item_name: this.$route.query.item_name,
            displayFromDate:"",
            displayToDate:"",
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
                    }, 5000)
                }
            },deep:true
        },
      
   },
    computed:{
        user(){
            return this.$store.getters.user;
        },
   
      
    },
    methods:{
        handleCtrl(event) {         
     
            if (event.ctrlKey && event.key === 'p') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.print(); // Call your custom function
            }   
            if (event.key==="Escape") {
                event.preventDefault(); // Prevent the default browser save dialog
                this.goBack();
            }                
            
        
        },
        displayReportType(reportType){
            return rt.displayReportType(reportType)
        },
        parseYMD(dateStr) {
            const [day, month, year] = dateStr.split('/');
            return  `${year}-${month}-${day}`;
        },
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
