<template lang="">
    <div class="container-fluid my-4">   
        <div class="row fixed-top ps-2 pt-2">
            <div class="col-2">
                <button class="btn" @click="goBack"><i class="fa-solid fa-circle-chevron-left"></i> (Esc)</button>
            </div>
            <div class="col-2">
                <button class="btn" @click="print"><i class="fa-solid fa-print"></i> (ctrl+p)</button>
            </div>
        </div>        
        <div class="row"  id="printMe" >
            <div class="d-none d-print-block">
                <LargePrintHeader />
            </div>
            <div class="col-12 text-center">
                <h6>Reports Adjustments {{displayReportType(reportType)}}</h6>
                <div class="row">
                    <div class="col-sm-4">
                        <div class="" v-if="code_no">Code : {{item_name}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div class="">Location : {{location}}</div>
                    </div>
                    <div class="col-sm-4">
                        <div v-if="displayFromDate || displayToDate">Date : From {{showTime(displayFromDate)}} - To {{showTime(displayToDate)}}</div>
                    </div>
                </div>
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='by_date'">
                        <AdjustmentsByDate :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" :rts_rfs="rts_rfs" :adj_type="adj_type" :location="location" />
                    </div>
                    <div v-if="reportType==='by_voucher'">
                        <AdjustmentsByVoucher :fromDate="fromDate"  :toDate="toDate" :voucher_no="voucher_no"  :code_no="code_no" :rts_rfs="rts_rfs" :adj_type="adj_type"  :location="location"   />
                    </div>
                    <div v-if="reportType==='by_description'">
                        <AdjustmentsByDescription :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" :rts_rfs="rts_rfs" :adj_type="adj_type"  :location="location"  />
                    </div>
                    <div v-if="reportType==='by_location'">
                        <AdjustmentsByLocation :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" :rts_rfs="rts_rfs" :adj_type="adj_type"  :location="location"  />
                    </div>
                    <div v-if="reportType==='by_adj_type'">
                        <AdjustmentsByAdjType :fromDate="fromDate"  :toDate="toDate" :code_no="code_no" :rts_rfs="rts_rfs" :adj_type="adj_type"  :location="location"  />
                    </div>
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import AdjustmentsByDate from "@/views/reports/adjustments/AdjustmentsByDate.vue"
import AdjustmentsByDescription from "@/views/reports/adjustments/AdjustmentsByDescription.vue"
import AdjustmentsByVoucher from "@/views/reports/adjustments/AdjustmentsByVoucher.vue"
import AdjustmentsByLocation from "@/views/reports/adjustments/AdjustmentsByLocation.vue"
import AdjustmentsByAdjType from "@/views/reports/adjustments/AdjustmentsByAdjType.vue"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import * as rt from '@/lib/reportType'

export default {
    components: {
        AdjustmentsByDate, AdjustmentsByDescription, AdjustmentsByVoucher, AdjustmentsByLocation, AdjustmentsByAdjType,LargePrintHeader
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],
            fromDate:this.parseYMD(this.$route.params['fromDate']),
            toDate:this.parseYMD(this.$route.params['toDate']),
            voucher_no: this.$route.query.voucher_no,
            code_no: this.$route.query.code_no,
            item_name: this.$route.query.item_name,    
            adj_type: this.$route.query.adj_type,
            rts_rfs: this.$route.query.rts_rfs,    
            location:this.$route.query.location,
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
                    }, 3000)
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
