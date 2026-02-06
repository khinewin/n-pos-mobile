<template lang="">
    <div class="a5-print-container">
    <div class="a5-page" v-for="(pages, index) in paginatedItems">
        <div class="page-number text-end">
            Page {{ index + 1 }} of {{ paginatedItems.length }}
        </div>
     <div v-if="index===0">   
    <LargePrintHeader />
    <div class="row justify-content-end small print-header" >   
        <div class="col-6">
            <table class="table mb-0 table-borderless small">
                <tbody class="small">  
                    <tr>
                        <td class="col-5 pb-0 fw-bold">Voucher No. <span class="float-end">:</span></td>
                        <td class="col-7  ps-2 pb-0" >
                            {{data.voucher_no}}
                        </td>
                    </tr>
                    <tr>
                        <td class="pb-0 fw-bold">Sale Date <span class="float-end">:</span></td>
                        <td class=" ps-2 pb-0" >
                           <span>{{sale_date}}</span>
                        </td>
                    </tr>       
                </tbody>
            </table>
        </div>        
        <div class="col-6">                    
            <table class="table mb-0 table-borderless">
                <tbody class="small">                                     
                    <tr>
                        <td class="  col-5 pb-0 fw-bold">Customer <span class="float-end">:</span></td>
                        <td class=" ps-2 col-7 pb-0" >
                            {{data.customer.name}}
                        </td>
                    </tr>      
                    <tr>
                        <td class="pb-0 fw-bold">Cashier <span class="float-end">:</span></td>
                        <td class=" ps-2 pb-0" >
                            {{data.user.displayName}}
                        </td>
                    </tr>                                               
                </tbody>
          </table>
        </div>
        </div>
        </div>
            <table class="table table-borderless  mb-0">
                <thead class="table-group-divider" v-if="pages.length > 0">
                    <tr class="text-center fw-bold">
                        <td class="col-0 border-0 border-bottom border-2 border-dark">No.</td>
                        <td class="col-5 border-0  border-bottom border-2 border-dark">Product Name</td>
                        <td class="col-1 border-0  border-bottom border-2 border-dark">Qty</td>
                        <td class="col-2 border-0  border-bottom border-2 border-dark">Unit Price</td>
                        <td class="col-2 border-0  border-bottom border-2 border-dark">Discount</td>
                        <td class="col-2 border-0  border-bottom border-2 border-dark">Amount</td>
                    </tr>
                </thead>
                <tbody class="" v-if="pages.length > 0">
                    <tr  v-for="(s, i) in pages" :key="s.id" >   
                        <td class="text-center">{{getSerialNumber(index, i)}}</td>                     
                        <td class="">{{s.item_name}}</td> 
                        <td class="text-center">
                            {{s.sale_qty}}
                        </td>    
                        <td class="text-end">{{fM(s.qty_sale_price)}} </td>                         
                        <td class=" text-end">{{s.discount ? fM(s.discount) : 0}}</td>
                        <td class=" text-end">{{fM(s.final_amount)}}</td>     
                    </tr>                        
                </tbody>
            </table>
            <table class="table table-borderless mb-0   w-100"  v-if="index === paginatedItems.length - 1">
                <tbody class="table-group-divider">
                    <tr>
                        <td colspan=""  class="ps-2">Total amount :</td>
                        <td class="text-end">{{fM(data.total_amount)}}</td>                   
                        <td colspan="" class="ps-2">Extra discount :</td>
                        <td class="text-end">
                            {{fM(data.extra_discount)}}
                        </td>  
                    </tr>
                    <tr>
                        <td colspan="" class="ps-2 ">Net amount :</td>
                        <td class="text-end">{{fM(data.net_amount)}}</td>
                                                            
                        <td colspan="" class="ps-2">Paid (Ks) :</td>
                        <td class="text-end">
                            {{fM(data.paid)}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan=""  class=" ps-2">Change :</td>
                        <td class="text-end ">
                            {{fM(data.change)}}
                        </td>                   
                        <td colspan="" class="ps-2" >Credit :</td>
                        <td class="text-end">
                            {{fM(data.credit)}}
                        </td>
                    </tr>                                              
                </tbody>
                <tbody class="table-group-divider">
                    <tr>
                        <td class="text-end" colspan="4">Printed on : {{printDate}}</td>
                    </tr>    
                </tbody>
          </table>
        

</div>
</div>
</template>
<script>
import {Timestamp, collection, addDoc, doc, increment,  updateDoc} from 'firebase/firestore'
import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
import SmallPrintHeader from "@/views/partials/SmallPrintHeader.vue"
import * as hp from "@/lib/helper"

export default {
    props: ['data'],
    components:{
        LargePrintHeader, SmallPrintHeader
    },
    data(){
        return{
            largePrinter:true,
            printDate: "",
            sale_date:"",
            itemsPerPage: 25,
            firstPageCount:17,
        }
    },
    mounted() {
        this.setPrintDate();
    },
    computed:{
        paginatedItems() {
            const products = this.data?.saleItems || [];
            const pages = [];

            const firstPageCount = this.firstPageCount;
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
        getSerialNumber(index, i) {
            const firstPageCount = this.firstPageCount; // Number of items on first page
            const otherPageCount = this.itemsPerPage; // Items from page 2 onward

            if (index === 0) return i + 1;
            return firstPageCount + (index - 1) * otherPageCount + i + 1;
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

            let sale_time="";
            if(this.data.saleMode=='Sale / New'){                
                const sDate=hp.insertDate(this.data.print_date)
                const s_date=Timestamp.fromDate(sDate)
                sale_time=hp.showTimeHour(s_date)
            }else{
                 sale_time=hp.showTimeHour(this.data.print_date)
            }
            this.sale_date=sale_time;
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
  padding: 10mm !important;
  margin: 0 !important;
  box-sizing: border-box;
  border: 1px solid #ccc;
  display: flex;
  flex-direction: column;
  page-break-after: always;
}
.a5-page  td{
    font-size: 13px !important;
}
</style>