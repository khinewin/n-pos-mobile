<template lang="">    
    <div class="container-fluid" id="invoice"  v-if="sales.length">
        <div class="small-printer">
        <SmallPrintHeader />
        <div class="">   
            <hr class="p-0 m-2">
            <div class="">
                <table class="table-borderless small">
                    <tbody class="small">  
                        <tr>
                            <td class=" col-6 ps-4">ဘောင်ချာနံပါတ်<span class="">:</span></td>
                            <td class="col-6" >
                                {{sales[0].voucher_no}}
                            </td>
                           
                        </tr>
                        <tr>
                            <td class="ps-4">နေ့စွဲ<span class="">:</span></td>
                            <td class="" >
                               <span >{{showTimeHour(sales[0].sale_date)}}</span>
                            </td>
                           
                        </tr>   
                        <tr>
                            <td class="ps-4">အမည်<span class="">:</span></td>
                            <td class="" >
                                {{sales[0].customer.name}}
                            </td>
                        </tr>  
                        <tr>
                            <td class="ps-4">လိပ်စာ<span class="">:</span></td>
                            <td class="" >
                                {{sales[0].customer.address}}
                            </td>
                        </tr>                         
                                                       
                           
                                                               
                    </tbody>
              </table>
            </div>
            <hr class="p-0 m-2">
            <div class="">
                <table class="table  table-borderless small print-body mb-0 ">
                    <tbody class="fw-bold ">
                        <tr class="text-center border-bottom-table small">
                            <td class="ps-2">စဥ်</td>
                            <td class="">အမျိုးအမည်</td>      
                            <td class="">သင့်ငွေ</td>                  
                        </tr>
                    </tbody>
                    <tbody v-if="sales[0].products.length > 0" >
                        <tr class="small" v-for="(s, i) in sales[0].products" :key="s.id" >   
                            <td class="text-center ps-2">{{fmc(i+1)}}</td>                     
                            <td class="">{{s.item_name}}, {{fmc(s.sale_qty)}} {{s.qty_sale_price_unit}}   </td>  
                            <td class="text-end">{{fmc(s.final_amount)}}</td>
                        </tr>  
                                      
                        </tbody>
                        <tbody class="text-end border-top-table small">
                        <tr>
                            <td colspan="2" class=" ">စုစုပေါင်း</td>
                            <td class="">{{fmc(sales[0].total_amount)}}</td>
                        </tr>                     
                        <!--<tr>
                            <td colspan="2" class="">Extra discount</td>
                            <td class="">
                                {{fM(sale.extra_discount)}}
                            </td>
                        </tr>-->
                        <!--<tr>
                            <td colspan="2" class="">Net amount</td>
                            <td class="">{{fM(sale.net_amount)}}</td>
                        </tr>-->
                        <tr >
                            <td colspan="2" class="">ပေးငွေ</td>
                            <td class="">
                                {{fmc(sales[0].paid)}}
                            </td>
                        </tr>
                        <!--<tr>
                            <td colspan="2" :class="{'border-bottom rm-b-b' : sale.credit}">Change</td>
                            <td class="">
                                {{fM(sale.change)}}
                            </td>
                        </tr> -->  
                        <tr v-if="sales[0].credit">
                            <td colspan="2" class="" >ကျန်ငွေ</td>
                            <td class="">
                                {{fmc(sales[0].credit)}}
                            </td>
                        </tr>                           
                    </tbody>
              </table>
              <table class="small-print-table-time table">
                <tbody>
                    <tr>
                        <td class="text-end bg-info">Printed date : {{printDate}}</td>
                    </tr>
                    <tr>
                        <td class="text-center small">ဝယ်ယူအားပေးမှုကို အထူးကျေးဇူးတင်ရှိပါသည်။</td>
                    </tr>
                </tbody>
               
          </table>
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

            }
        },
        mounted() {
            this.setPrintDate();
            this.fetchSale();

        },
        computed:{
           
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
  
              
@font-face {
    font-family: "nts-regular";
    src: url("/natosan/NotoSansMyanmar-Regular.ttf");
  }
  @font-face {
    font-family: "nts-bold";
    src: url("/natosan/NotoSansMyanmar-Bold.ttf");
  }
  @font-face {
    font-family: "pds-regular";
    src: url("/pyidaungsu/Pyidaungsu-Book-1.8.3_Regular.ttf");
  }
  @font-face {
    font-family: "pds-bold";
    src: url("/pyidaungsu/Pyidaungsu-Book-1.8.3_Bold.ttf");
  }

@media print {
  @page {
    size: A5;
    margin-top: 10mm;
    margin-bottom: 5mm;
    margin-left: 5mm;
    margin-right: 5mm;    
    padding: 0;
  }


}
  
    </style>