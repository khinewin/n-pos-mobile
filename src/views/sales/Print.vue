<template lang="">
    <div class="container-fluid py-2">
        <div class="row">
            <div class="col-12">
                   <h4 class="text-center"> Printing with N-POS</h4>
            </div>
        </div>
    <div class="row justify-content-end small" >   
        <div class="col-6">
            <table class="table mb-0 table-borderless">
                <tbody class="small">  
                    <tr>
                        <td class="col-4 pb-0 fw-bold">Voucher no. <span class="float-end">:</span></td>
                        <td class="col-8  ps-2 pb-0" style="border-bottom: 1px dotted;">
                            {{data.voucher_no}}
                        </td>
                    </tr>
                    <tr>
                        <td class="pb-0 fw-bold">Date <span class="float-end">:</span></td>
                        <td class=" ps-2 pb-0" style="border-bottom: 1px dotted;">
                           <span>{{showPrintTime(data.sale_date)}}</span>
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
                        <td class=" ps-2 col-8 pb-0" style="border-bottom: 1px dotted;">
                            {{data.customer.name}}
                        </td>
                    </tr>      
                    <tr>
                        <td class="pb-0 fw-bold">Phone <span class="float-end">:</span></td>
                        <td class=" ps-2 pb-0" style="border-bottom: 1px dotted;">
                            {{data.customer.phone}}
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
                        <td class="col-7">Description</td>
                        <td class="col-1" v-if="isQtyL">Qty L</td>
                        <td class="col-1" v-if="isQtyL">L Unit Price</td>
                        <td class="col-1" v-if="isQtyS">Qty S</td>
                        <td class="col-1" v-if="isQtyS">S Unit Price</td>
                        <td class="col-1">Discount</td>
                        <td class="col-1">Amount</td>
                    </tr>
                    <tr class="" v-if="data.saleItems.length > 0" v-for="(s, i) in data.saleItems" :key="s.id" >   
                        <td class="text-center">{{i+1}}</td>                     
                        <td class="">{{s.item_name}}</td>
                        <td class="text-center" v-if="isQtyL"><span v-if="s.sale_qty_l">{{s.sale_qty_l}} {{s.parent_unit}}</span></td>
                        <td class="text-end" v-if="isQtyL"><span v-if="s.sale_qty_l">{{s.item_per_package * s.sale_price}}</span></td>
                        <td class="text-center" v-if="isQtyS"><span v-if="s.sale_qty_s">{{s.sale_qty_s}} {{s.child_unit}}</span></td>
                        <td class="text-end" v-if="isQtyS"><span v-if="s.sale_qty_s">{{s.sale_price}}</span></td>
                        <td class="text-end">{{s.discount}}</td>
                        <td class="text-end">{{s.sale_amount}}</td>
                    </tr>                        
              
                    <tr>
                        <td :colspan="[isQtyL >0 && isQtyS > 0 ? 7 : 5]" class="text-end col-6 pe-2 " style="border-bottom-style: hidden;">Total amount</td>
                        <td class="text-end col-6">{{data.total_amount}}</td>
                    </tr>                     
                    <tr>
                        <td :colspan="[isQtyL >0 && isQtyS > 0 ? 7 : 5]" class="text-end pe-2 " style="border-bottom-style: hidden;">Discount</td>
                        <td class="text-end">
                            {{data.discount}}
                        </td>
                    </tr>
                    <tr>
                        <td :colspan="[isQtyL >0 && isQtyS > 0 ? 7 : 5]" class="text-end pe-2 " style="border-bottom-style: hidden;">Charges</td>
                        <td class="text-end">{{data.charges}}</td>
                    </tr>
                    <tr >
                        <td :colspan="[isQtyL >0 && isQtyS > 0 ? 7 : 5]" class="text-end  pe-2 " style="border-bottom-style: hidden;">Pay</td>
                        <td class="text-end">
                            {{data.pay}}
                        </td>
                    </tr>
                    <tr>
                        <td :colspan="[isQtyL >0 && isQtyS > 0 ? 7 : 5]" class="text-end  pe-2 " >Refunds</td>
                        <td class="text-end ">
                            {{data.refunds}}
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
</template>
<script>
import {Timestamp, collection, addDoc, doc, increment,  updateDoc} from 'firebase/firestore'

export default {
    props: ['data'],
    data(){
        return{
           
        }
    },
    mounted() {
    },
    computed:{
        isQtyL(){
            let l=0
            this.data.saleItems.map((sale)=>{
                l +=sale.sale_qty_l
            })
            if(l>0) return true;      
        },
        isQtyS(){
            let s=0
            this.data.saleItems.map((sale)=>{
                s +=sale.sale_qty_s
            })
            if(s>0) return true;                    
        }
    },
    methods:{
      
        showPrintTime(t){
            const newT=Timestamp.fromDate(new Date(t))
            const date=newT.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
    },
  

}
</script>
<style lang="">
    
</style>