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
                <h6>Reports Return In {{displayReportType(reportType)}}</h6>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="" v-if="code_no">Code : {{item_name}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div class="" v-if="customer_name">Customer : {{customer_name}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div v-if="displayFromDate || displayToDate">Date : From {{showTime(displayFromDate)}} - To {{showTime(displayToDate)}}</div>
                    </div>
                </div>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <RtInByDate  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <RtInByVoucher  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no" />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <RtInByDescription  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_description_date'">
                        <RtInByDescriptionDate  :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_customer_date'">
                        <RtInByCustomerDate :customer_code="customer_code" :fromDate="fromDate"  :toDate="toDate" :code_no="code_no"/>
                    </div>
                    <div v-if="reportType==='by_customer'">
                        <RtInByCustomer  :fromDate="fromDate"  :toDate="toDate" :customer_code="customer_code" :code_no="code_no"/>
                    </div>
                  
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import RtInByDate from "@/views/reports/returnin/RtInByDate.vue"
import RtInByVoucher from "@/views/reports/returnin/RtInByVoucher.vue"
import RtInByDescription from "@/views/reports/returnin/RtInByDescription.vue"
import RtInByCustomer from "@/views/reports/returnin/RtInByCustomer.vue"
import RtInByCustomerDate from "@/views/reports/returnin/RtInByCustomerDate.vue"
import RtInByDescriptionDate from "@/views/reports/returnin/RtInByDescriptionDate.vue"

import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as rt from "@/lib/reportType"

export default {
    components: {
        RtInByDate,RtInByVoucher,RtInByDescription, RtInByCustomer, RtInByCustomerDate, LargePrintHeader,
        RtInByDescriptionDate
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
