<template lang="">
    <div class="container" id="printMe">
        <div class="row justify-content-end">           
            <div class="col-lg-12">
                <table class="table mb-0 table-borderless">
                    <tbody class="small">  
                        <tr>
                            <td class="text-end col-10 p-0">Voucher no. :</td>
                            <td class="col-2 p-0 ps-2">
                                {{sale.voucher_no}}
                            </td>
                        </tr>
                        <tr>
                            <td class="text-end p-0">Date :</td>
                            <td class="p-0 ps-2">
                                {{showTime(sale.sale_date)}}
                            </td>
                        </tr>                                          
                        <tr>
                            <td class="text-end p-0">Name :</td>
                            <td class="p-0 ps-2">
                                {{sale.customer.name}}
                            </td>
                        </tr>      
                        <tr>
                            <td class="text-end p-0">Phone :</td>
                            <td class="p-0 ps-2">
                                {{sale.customer.phone}}
                            </td>
                        </tr>                                               
                    </tbody>
              </table>
            </div>
            <table class="table table-bordered table-hover border-dark">
                <tbody class="small">
                    <tr class="text-center fw-bold">
                        <td class="col-3 p-0 py-1">Description</td>
                        <td class="col-2  p-0 py-1">Qty</td>
                        <td class="col-2  p-0 py-1">Price</td>
                        <td class="col-2  p-0 py-1">Amount</td>
                    </tr>
                    <tr class="text-center" v-if="sale.saleItems.length > 0" v-for="(s, i) in sale.saleItems" :key="s.id" >                        
                        <td class=" p-0 py-1">{{s.item_name}}</td>
                        <td class=" p-0 py-1">
                            {{s.qty}}
                        </td>
                        <td class=" p-0 py-1">{{s.sale_price}}</td>
                        <td class=" p-0 py-1">{{s.sale_amount}}</td>
                    </tr>  
                    <tr>
                        <td colspan="3 py-1" class="text-end  p-0 py-1 pe-2">Total amount</td>
                        <td class="text-center  p-0 py-1">{{sale.total_amount}}</td>
                    </tr>                     
                    <tr>
                        <td colspan="3 py-1" class="text-end  p-0 py-1  pe-2" >Discount</td>
                        <td class="text-center  p-0 py-1">
                            {{sale.discount}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" class="text-end  p-0 py-1  pe-2">Charges</td>
                        <td class="text-center  p-0 py-1">{{sale.charges}}</td>
                    </tr>
                    <tr >
                        <td colspan="3" class="text-end  p-0 py-1  pe-2">Pay</td>
                        <td class="text-center  p-0 py-1">
                            {{sale.pay}}
                        </td>
                    </tr>
                    <tr>
                        <td colspan="3" class="text-end  p-0 py-1  pe-2">Refunds</td>
                        <td class="text-center  p-0 py-1">
                            {{sale.refunds}}
                        </td>
                    </tr>
                </tbody>
          </table>
        </div>
    </div>
</template>
<script>
export default {
    data () {
    return {
      output: null
    }
  },
    mounted() {
        this.print();
    },
    computed:{
        sale(){
            return this.$store.getters.printSale;
        }
    },
    methods:{
        async print () {
        // Pass the element id here
        await this.$htmlToPaper('printMe');
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