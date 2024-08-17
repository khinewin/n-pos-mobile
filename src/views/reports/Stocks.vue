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
                <h5>Reports stocks {{displayReportType}}</h5>
                <h6 v-if="displayFromDate || displayToDate">From {{showTime(displayFromDate)}} To {{showTime(displayToDate)}}</h6>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <StocksByDate :fromDate="fromDate"  :toDate="toDate" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <StocksByVoucher :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no" />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <StocksByDescription :fromDate="fromDate"  :toDate="toDate"/>
                    </div>
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import StocksByDate from "@/views/reports/stocks/StocksByDate.vue"
import StocksByDescription from "@/views/reports/stocks/StocksByDescription.vue"
import StocksByVoucher from "@/views/reports/stocks/StocksByVoucher.vue"
export default {
    components: {
        StocksByDate, StocksByDescription, StocksByVoucher
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],
            fromDate:this.$route.params['fromDate'],
            toDate:this.$route.params['toDate'],
            voucher_no: this.$route.query.voucher_no,
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
