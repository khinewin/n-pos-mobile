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
            <div class="col-12 text-center">
                <h3>N-PRO-POS</h3>
                <h5>Reports stocks {{displayReportType}}</h5>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="h6" >Code : {{item_name}}</div>
                    </div>
                    <div class="col-sm-4">
                        <h6>Location : {{location}}</h6>
                    </div>
                    <div class="col-sm-4">
                         <h6 v-if="displayFromDate || displayToDate">Date : From {{showTime(displayFromDate)}} - To {{showTime(displayToDate)}}</h6>
                    </div>
                </div>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <StocksByDate :fromDate="fromDate"  :toDate="toDate" :location="location" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <StocksByVoucher :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no" :location="location" />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <StocksByDescription :fromDate="fromDate"  :toDate="toDate" :location="location"/>
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
            location: this.$route.params['location'],
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
            this.$router.push({name: "Reports"})
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
