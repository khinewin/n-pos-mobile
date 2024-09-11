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
                <h6>Reports transfers {{displayReportType(reportType)}}</h6>
                <div class="row">
                    <div class="col-sm-4">
                        <div v-if="code_no">Code : {{item_name}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div>From  : {{from_location}} - To  : {{to_location}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div v-if="displayFromDate || displayToDate">From {{showTime(displayFromDate)}} - To {{showTime(displayToDate)}}</div>
                    </div>
                </div>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <TransferByDate :fromDate="fromDate"  :toDate="toDate" :from_location="from_location" :to_location="to_location" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <TransferByVoucher :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no" :from_location="from_location" :to_location="to_location" :code_no="code_no" />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <TransferByDescription :fromDate="fromDate"  :toDate="toDate" :from_location="from_location" :to_location="to_location" :code_no="code_no"/>
                    </div>
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import TransferByDate from "@/views/reports/transfers/TransferByDate.vue"
import TransferByDescription from "@/views/reports/transfers/TransferByDescription.vue"
import TransferByVoucher from "@/views/reports/transfers/TransferByVoucher.vue"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as rt from "@/lib/reportType"

export default {
    components: {
        TransferByDate, TransferByDescription, TransferByVoucher,LargePrintHeader
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],
            from_location: this.$route.params['from_location'],
            to_location: this.$route.params['to_location'],
            fromDate:this.parseYMD(this.$route.params['fromDate']),
            toDate:this.parseYMD(this.$route.params['toDate']),
            voucher_no: this.$route.query.voucher_no,
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
