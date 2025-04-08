<template lang="">    
    <div  v-if="sales.length">
        <div class="">       
        <div class="">           
            <div class="a5-print-container">           
            <div class="a5-page" v-for="(pages, index) in paginatedItems">
                <div class="page-number">
                    Page {{ index + 1 }} of {{ paginatedItems.length }}
                </div>
                <div v-if="index===0">
                    <SmallPrintHeader />
                    <hr>
                    <table class="table">
                        <tbody class="small">  
                            <tr>
                                <td class="  ">ဘောင်ချာနံပါတ်<span class="">:</span></td>
                                <td class="" >
                                    {{sales[0].voucher_no}}
                                </td>  
                                <td class="">နေ့စွဲ<span class="">:</span></td>
                                <td class="" >
                                   <span >{{showTimeHour(sales[0].sale_date)}}</span>
                                </td>                           
                            </tr>
                           
                            <tr>
                                <td class="">အမည်<span class="">:</span></td>
                                <td class="" >
                                    {{sales[0].customer.name}}
                                </td>
                                <td class="">လိပ်စာ<span class="">:</span></td>
                                <td class="" >
                                    {{sales[0].customer.address}}
                                </td>
                            </tr>  
                                                                                                                                                                                       
                        </tbody>
                  </table>
                 </div>   
                <table class="table table-bordered">
                    <tbody class="fw-bold " v-if="pages.length > 0">
                        <tr class="text-center border-bottom-table small">
                            <td class="ps-2 col-1">စဥ် </td>
                            <td class="col-9">အမျိုးအမည်</td>      
                            <td class="col-2">သင့်ငွေ</td>                  
                        </tr>
                    </tbody>
                    <tbody v-if="pages.length > 0" >
                        <tr class="small" v-for="(s, i) in pages" :key="s.id" >   
                            <td class="text-center ps-2">{{index > 0 ? Number(index + "0") + i +1 : i +1}}</td>                     
                            <td class="">{{s.item_name}}, {{fmc(s.sale_qty)}} {{s.qty_sale_price_unit}}   </td>  
                            <td class="text-end">{{fmc(s.final_amount)}}</td>
                        </tr>                                       
                    </tbody>     
                                 
              </table>
              <table class="table  w-100"  v-if="index === paginatedItems.length - 1">
                <tfoot class="text-end border-top-table small ">
                    <tr>
                        <td colspan="2" class=" ">စုစုပေါင်း</td>
                        <td class="">{{fmc(sales[0].total_amount)}}</td>
                    </tr>                                         
                    <tr >
                        <td colspan="2" class="">ပေးငွေ</td>
                        <td class="">
                            {{fmc(sales[0].paid)}}
                        </td>
                    </tr>                     
                    <tr v-if="sales[0].credit">
                        <td colspan="2" class="" >ကျန်ငွေ</td>
                        <td class="">
                            {{fmc(sales[0].credit)}}
                        </td>
                    </tr>                           
                </tfoot>   
              </table>
              </div>             
            </div>         
            </div>      
        </div>
      </div>
    </template>
    <script>
    import {Timestamp, collection, addDoc, doc, increment, onSnapshot, query, where, updateDoc} from 'firebase/firestore'
    import db from '@/firebase/firestore'

    import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
    import SmallPrintHeader from "@/views/partials/SmallPrintHeader.vue"
    import * as hp from "@/lib/helper"
    import * as ss from "@/lib/snapshot/sales"
    
    export default {
        components:{
            LargePrintHeader, SmallPrintHeader
        },
        data(){
            return{
                sales:[],
                largePrinter:false,
                printDate: "",
                voucher_no: this.$route.params.voucher_no,
                itemsPerPage:25,
            }   
        },
        mounted() {
            this.setPrintDate();
            this.fetchSale();

        },
        computed:{
            paginatedItems() {
            const products = this.sales[0]?.products || [];
            const pages = [];

            const firstPageCount = 17;
            const otherPageCount = this.itemsPerPage;

            // First page
            pages.push(products.slice(0, firstPageCount));

            // Middle pages (if any)
            for (let i = firstPageCount; i < products.length; i += otherPageCount) {
                pages.push(products.slice(i, i + otherPageCount));
            }

            // Check last page item count
            const lastPage = pages[pages.length - 1];
            if (lastPage.length > 15) {
                // Push an empty page for the footer if last page is too full
                pages.push([]);
            }

            return pages;
            }

        },
        methods:{
         
         showTimeHour(t){
                return hp.showTimeHour(t);
            },
            goBack(){
                this.$router.push({name: "Sale"});
            },
            printAndClose() {
               // let printContent = document.getElementById("invoice").innerHTML;
              //  document.body.innerHTML = printContent;  // Show only the invoi
                window.print();
                window.close();

            },
             fmc(amount) {
                const engNumbers = "0123456789";
                const mmNumbers = "၀၁၂၃၄၅၆၇၈၉";
    
                // Format as currency (adds commas for thousands separator)
                let formattedAmount = new Intl.NumberFormat('en-US').format(amount);
    
                // Convert to Myanmar numbers
                return formattedAmount.replace(/[0-9]/g, (digit) => mmNumbers[engNumbers.indexOf(digit)]);
            },
            fM(amount){
                return hp.formattedAmount(amount)
            },
            calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
                return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
            },
            calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
                return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
            },
            showPrintTime(t){
               return hp.showTimeHour(t)
            },
            setPrintDate(){
                const date=new Date();
                const today=Timestamp.fromDate(date)
                const pDate=hp.showTimeHour(today)
                this.printDate=pDate;
    
              
           },
           fetchSale(){
            let  query_url = query(collection(db, "sales"), where("voucher_no", "==", this.voucher_no));        
             const unsub= onSnapshot(query_url, async(snapShot) =>{                
                        const sales=ss.sales(snapShot)       
                        this.sales=sales;
                        setTimeout(()=>{
                           this.printAndClose();
                        }, 100)
                       unsub();
                });   
            }     
           
        },
      
    
    }
    </script>
    <style>     

  .footer-table{
    margin-top: auto !important;
  }
  .a5-print-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.page-number{
    margin-bottom: 2mm;
    font-size: 12px;
}

.a5-page {
    width: 148mm;
  height: 210mm;
  padding: 10mm;
  padding-top: 5mm;
  box-sizing: border-box;
  border: 1px solid #ccc;
  margin-bottom: 10mm;
  display: flex;
  flex-direction: column;
  page-break-after: always;
}

@media print {
  body {
    margin: 0;
  }

  .a5-page {
    border: none;
    margin: 0;
    page-break-after: always;
  }
}
    </style>