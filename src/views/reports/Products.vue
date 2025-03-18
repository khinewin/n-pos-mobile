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
            <div class="col-12 text-center">
                <div class="d-none d-print-block">
                    <LargePrintHeader />
                </div>
                <h6>Reports Products Code  {{displayReportType}}</h6>
                <div class="row small">
                    <div class="col-sm-6 col-md-3">
                        <div class="" v-if="code_no">Code : {{item_name}}</div>
                    </div>
                    <div class="col-sm-6 col-md-3">
                        <div class="" v-if="gt_equal || lt_equal">Balance : 
                            <span v-if="gt_equal">>= {{gt_equal}} </span> 
                            <span v-if="gt_equal && lt_equal"> and </span>
                            <span v-if="lt_equal"><= {{lt_equal}} </span>
                            <span class="ms-2"> {{packing_unit}}</span>
                        </div>
                    </div> 
                    <div class="col-sm-6 col-md-3">
                        <div class="d-none">Location : {{this.location}} </div>
                    </div>                                       
                    <div class="col-sm-6 col-md-3">
                        <div  class="" v-if="displayDate">Date : {{showTime(displayDate)}}</div>
                    </div>
                </div>  
            </div>            
            <div class="col-12">                   
                    <div v-if="reportType==='stock_list'">
                        <StockList :location="location" :code_no="code_no" :lt_equal="lt_equal" :gt_equal="gt_equal" :packing_unit="packing_unit" />
                    </div>        
                    <div v-if="reportType==='stock_list_sale_price'">
                        <StockList :sale_price="true" :location="location" :code_no="code_no"  :lt_equal="lt_equal" :gt_equal="gt_equal"  :packing_unit="packing_unit" />
                    </div>
                    <div v-if="reportType==='stock_list_pur_price'">
                        <StockList :pur_price="true" :location="location" :code_no="code_no"  :lt_equal="lt_equal" :gt_equal="gt_equal"  :packing_unit="packing_unit" />
                    </div>   
                    <div v-if="reportType==='stock_list_pur_sale_price'">
                        <StockList :pur_price="true" :sale_price="true" :location="location"  :code_no="code_no"  :lt_equal="lt_equal" :gt_equal="gt_equal"  :packing_unit="packing_unit"/>
                    </div>        
            </div>
        </div>
    </div>
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import StockList from "@/views/reports/products/StockList.vue"
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"

export default {
    components: {
       StockList, LargePrintHeader
    },
  
    data(){
        return{         
            reportType:this.$route.params['reportType'],         
            location: this.$route.params['location'],
            code_no: this.$route.query['code_no'],
            item_name: this.$route.query["item_name"],
            gt_equal: this.$route.query.gt_equal,    
            lt_equal: this.$route.query.lt_equal,    
            packing_unit: this.$route.query.packing_unit,
            displayDate:"",
            displayReportType:"",
            reports:[],
            current_month:[],
            salesData:[],
            isLoading:false,
            is_stock_list_sale_price: false,
            is_stock_list_pur_price: false,
            is_stock_list_pur_sale_price:false,
            msg : {
                type :"",
                info :"",
            },        

        }
    },
   
    mounted() {      
            this.displayDate=Timestamp.fromDate(new Date())
            if(this.reportType==="stock_list"){
                this.displayReportType="Stocks List"
            }else 
            if(this.reportType==="stock_list_sale_price"){
                this.displayReportType="Stocks List with Sale Price"
                this.is_stock_list_sale_price=true;
            }else
            if(this.reportType==="stock_list_pur_price"){
                this.displayReportType="Stocks List with Purchase Price"
                this.is_stock_list_pur_price=true;
            }else
            if(this.reportType==="stock_list_pur_sale_price"){
                this.displayReportType="Stocks List with Purchase and Sale Price"
                this.is_stock_list_pur_sale_price=true;
            }
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
<style>
    #camera-preview {
      width: 100%;
      max-width: 500px;
      height: auto;
      margin: 20px auto;
    }
</style>
