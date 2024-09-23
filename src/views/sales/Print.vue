<template lang="">
    <div class="container-fluid py-2" v-if="largePrinter">
    <LargePrintHeader />
    <div class="row justify-content-end small" >   
        <div class="col-6">
            <table class="table mb-0 table-borderless">
                <tbody class="small">  
                    <tr>
                        <td class="col-4 pb-0 fw-bold">Voucher No. <span class="float-end">:</span></td>
                        <td class="col-8  ps-2 pb-0" >
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
                        <td class="  col-4 pb-0 fw-bold">Customer <span class="float-end">:</span></td>
                        <td class=" ps-2 col-8 pb-0" >
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
        <div class="col-12 mt-1">
            <table class="table table-borderless  mb-0">
                <tbody class="table-group-divider">
                    <tr class="text-center fw-bold">
                        <td class="col-0 border-0 border-bottom border-2 border-dark">No.</td>
                        <td class="col-5 border-0  border-bottom border-2 border-dark">Product Name</td>
                        <td class="col-1 border-0  border-bottom border-2 border-dark">Qty</td>
                        <td class="col-2 border-0  border-bottom border-2 border-dark">Unit Price</td>
                        <td class="col-2 border-0  border-bottom border-2 border-dark">Discount</td>
                        <td class="col-2 border-0  border-bottom border-2 border-dark">Amount</td>
                    </tr>
                </tbody>
                <tbody class="table-group-divider">
                    <tr cv-if="data.saleItems.length > 0" v-for="(s, i) in data.saleItems" :key="s.id" >   
                        <td class="text-center">{{i+1}}</td>                     
                        <td class="">{{s.item_name}}</td> 
                        <td class="text-center">
                            {{s.sale_qty}}
                        </td>    
                        <td class="text-end">{{fM(s.qty_sale_price)}}' {{s.qty_sale_price_unit}} </td>     
                        <!--<td class="text-center">
                            <span v-if="s.dis_qty">{{s.dis_qty}}{{s.dis_qty_unit}}</span>
                            <span v-if="s.dis_amount">{{s.dis_amount}}$</span>
                            <span v-if="s.dis_percent">{{s.dis_percent}}%</span>     
                        </td>-->         
                        <td class=" text-end">{{s.discount ? fM(s.discount) : 0}}</td>
                        <td class=" text-end">{{fM(s.final_amount)}}</td>     
                    </tr>                        
                </tbody>
            </table>
            <table class="table table-borderless mb-0">
                <tbody class="table-group-divider">
                    <tr>
                        <td colspan=""  class="ps-2">Total amount :</td>
                        <td class="text-end">{{fM(data.total_amount)}}</td>                   
                        <td colspan="" class="ps-2 ">Net amount :</td>
                        <td class="text-end">{{fM(data.net_amount)}}</td>
                    </tr>
                    <tr>
                        <td colspan="" class="ps-2">Extra discount :</td>
                        <td class="text-end">
                            {{fM(data.extra_discount)}}
                        </td>                                        
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
          </table>
          <table class="table table-borderless">
                <tbody class="table-group-divider">
                    <tr>
                        <td class="text-end">Printed on : {{printDate}}</td>
                    </tr>
                </tbody>
          </table>
        </div>      
    </div>
</div>
<div class="container-fluid py-2" v-else>
    <SmallPrintHeader />
    <div class="" >   
        <div class="">
            <table class="small-print-header">
                <tbody class="">  
                    <tr>
                        <td class="">Voucher No. <span class="">:</span></td>
                        <td class="" >
                            {{data.voucher_no}}
                        </td>
                    </tr>
                    <tr>
                        <td class="">Sale Date <span class="">:</span></td>
                        <td class="" >
                           <span >{{sale_date}}</span>
                        </td>
                    </tr>       
                                                   
                    <tr>
                        <td class="">Customer <span class="">:</span></td>
                        <td class="" >
                            {{data.customer.name}}
                        </td>
                    </tr>   
                    <tr>
                        <td class="">Cashier <span class="">:</span></td>
                        <td class="" >
                            {{data.user.displayName}}
                        </td>
                    </tr>      
                                                           
                </tbody>
          </table>
        </div>
        <div class="">
            <table class="small-print-table table-bordered">
                <tbody class="fw-bold text-center">
                    <tr class="">
                        <td class="">No.</td>
                        <td class="">Product Name</td>
                        <td class="" >Qty</td>
                        <!--<td class="" >Unit price</td>-->
                        <td class="">Discount</td>
                        <td class="">Amount</td>
                    </tr>
                </tbody>
                <tbody>
                    <tr class="" v-if="data.saleItems.length > 0" v-for="(s, i) in data.saleItems" :key="s.id" >   
                        <td class="text-center">{{i+1}}</td>                     
                        <td class="">{{s.item_name}}</td>
                        <td>                           
                             {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.sale_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                        </td>
                        <!--<td class="text-center">{{fM(s.qty_sale_price)}}' {{s.qty_sale_price_unit}}</td>    -->
                        <td class="text-end">{{s.discount ? fM(s.discount) : 0}}</td>
                        <td class="text-end">{{fM(s.final_amount)}}</td>
                    </tr>                        
                    </tbody>
                    <tbody class="text-end">
                    <tr>
                        <td colspan="4" class=" ">Total amount</td>
                        <td class="">{{fM(data.total_amount)}}</td>
                    </tr>                     
                    <tr>
                        <td colspan="4" class="">Extra discount</td>
                        <td class="">
                            {{fM(data.extra_discount)}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" class="">Net amount</td>
                        <td class="">{{fM(data.net_amount)}}</td>
                    </tr>
                    <tr >
                        <td colspan="4" class="">Paid (Ks)</td>
                        <td class="">
                            {{fM(data.paid)}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4" :class="{'border-bottom rm-b-b' : data.credit}">Change</td>
                        <td class="">
                            {{fM(data.change)}}
                        </td>
                    </tr>   
                    <tr v-if="data.credit">
                        <td colspan="4" class="" >Credit</td>
                        <td class="">
                            {{fM(data.credit)}}
                        </td>
                    </tr>                           
                </tbody>
          </table>
          <table class="small-print-table-time">
            <tbody>
                <tr>
                    <td>Print date : {{printDate}}</td>
                    <td></td>
                </tr>
            </tbody>
      </table>
        
        </div>      
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
        }
    },
    mounted() {
        this.setPrintDate();
    },
    computed:{
       
    },
    methods:{
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
      .small-print-header td{
        padding: 2px;
        font-size: 11px;
    }
    .small-print-table{
        border: 1px solid;
    }
    .small-print-table td {
        padding: 2px;
        font-size: 12px;
    }
    .small-print-table tbody{
        border-bottom: 1px solid;
    }
    .small-print-table-time{
        font-size: 11px;
    }
</style>