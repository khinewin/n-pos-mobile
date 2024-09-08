<template lang="">
    <div class="container-fluid py-2" v-if="largePrinter">
    <LargePrintHeader />
    <div class="row justify-content-end small" >   
        <div class="col-6">
            <table class="table mb-0 table-borderless">
                <tbody class="small">  
                    <tr>
                        <td class="col-4 pb-0 fw-bold">Voucher no. <span class="float-end">:</span></td>
                        <td class="col-8  ps-2 pb-0" >
                            {{data.voucher_no}}
                        </td>
                    </tr>
                    <tr>
                        <td class="pb-0 fw-bold">Date <span class="float-end">:</span></td>
                        <td class=" ps-2 pb-0" >
                           <span v-if="data.print_date">{{showPrintTime(data.print_date)}}</span>
                        </td>
                    </tr>       
                </tbody>
            </table>
        </div>        
        <div class="col-6">                    
            <table class="table mb-0 table-borderless">
                <tbody class="small">                                     
                    <tr>
                        <td class="  col-4 pb-0 fw-bold">Name <span class="float-end">:</span></td>
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
        <div class="col-12">
            <table class="table table-bordered table-hover border-dark mt-2 mb-0">
                <tbody class="small">
                    <tr class="text-center fw-bold">
                        <td class="col-1">No.</td>
                        <td class="col-6">Description</td>
                        <td class="col-1">Qty</td>
                        <td class="col-1">Unit Price</td>
                        <td class="col-1">Discount</td>
                        <td class="col-1">Discount Amt</td>
                        <td class="col-1">Amount</td>
                    </tr>
                    <tr class="" v-if="data.saleItems.length > 0" v-for="(s, i) in data.saleItems" :key="s.id" >   
                        <td class="text-center">{{i+1}}</td>                     
                        <td class="">{{s.item_name}}</td> 
                        <td class="text-center">
                            {{calPackingQty(s.packing_size.medium_qty, s.packing_size.smallest_qty, s.sale_qty, s.packing_size.largest_unit, s.packing_size.medium_unit, s.packing_size.smallest_unit)}}                                            
                        </td>    
                        <td class="text-end">{{s.qty_sale_price}}' {{s.qty_sale_price_unit}} </td>     
                        <td class="text-center">
                            <span v-if="s.dis_qty">{{s.dis_qty}}{{s.dis_qty_unit}}</span>
                            <span v-if="s.dis_amount">{{s.dis_amount}}$</span>
                            <span v-if="s.dis_percent">{{s.dis_percent}}%</span>     
                        </td>         
                        <td class=" text-end">{{s.discount}}</td>
                        <td class=" text-end">{{s.final_amount}}</td>     
                    </tr>                        
              
                    <tr>
                        <td colspan="6"  class="text-end  pe-2 " style="border-bottom-style: hidden;">Total amount</td>
                        <td class="text-end">{{data.total_amount}}</td>
                    </tr>                     
                    <tr>
                        <td colspan="6" class="text-end pe-2 " style="border-bottom-style: hidden;">Extra discount</td>
                        <td class="text-end">
                            {{data.extra_discount}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6" class="text-end pe-2 " style="border-bottom-style: hidden;">Net amount</td>
                        <td class="text-end">{{data.net_amount}}</td>
                    </tr>
                    <tr >
                        <td colspan="6" class="text-end  pe-2 " style="border-bottom-style: hidden;">Paid</td>
                        <td class="text-end">
                            {{data.paid}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="6"  class="text-end  pe-2 " >Change</td>
                        <td class="text-end ">
                            {{data.change}}
                        </td>
                    </tr>                           
                </tbody>
          </table>
          <table>
                <tbody>
                    <tr>
                        <td></td>
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
                        <td class="">Voucher no. <span class="">:</span></td>
                        <td class="" >
                            {{data.voucher_no}}
                        </td>
                    </tr>
                    <tr>
                        <td class="">Date <span class="">:</span></td>
                        <td class="" >
                           <span v-if="data.print_date">{{showPrintTime(data.print_date)}}</span>
                        </td>
                    </tr>       
                                                   
                    <tr>
                        <td class="">Name <span class="">:</span></td>
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
            <table class="small-print-table">
                <tbody class="fw-bold text-center">
                    <tr class="">
                        <td class="">No.</td>
                        <td class="">Description</td>
                        <td class="" >Qty</td>
                        <td class="" >Unit price</td>
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
                        <td class="text-center">{{s.qty_sale_price_unit}} * {{s.qty_sale_price}}</td>    
                        <td class="text-end">{{s.discount}}</td>
                        <td class="text-end">{{s.final_amount}}</td>
                    </tr>                        
                    </tbody>
                    <tbody class="text-end">
                    <tr>
                        <td colspan="5" class=" " style="border-bottom-style: hidden;">Total amount</td>
                        <td class="">{{data.total_amount}}</td>
                    </tr>                     
                    <tr>
                        <td colspan="5" class="" style="border-bottom-style: hidden;">Extra discount</td>
                        <td class="">
                            {{data.extra_discount}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="5" class="" style="border-bottom-style: hidden;">Net amount</td>
                        <td class="">{{data.net_amount}}</td>
                    </tr>
                    <tr >
                        <td colspan="5" class="" style="border-bottom-style: hidden;">Paid</td>
                        <td class="">
                            {{data.paid}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="5" class="" >Change</td>
                        <td class="">
                            {{data.change}}
                        </td>
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
        }
    },
    mounted() {
    },
    computed:{
       
    },
    methods:{
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        showPrintTime(t){
           return hp.showTimeHour(t)
        },
    },
  

}
</script>
<style>
      .small-print-header td{
        padding: 2px;
        font-size: 12px;
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
</style>