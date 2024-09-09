<template lang="">
    <div class="card">
        <div class="card-body p-2">
            <LargePrintHeader />
            <div class="row mb-2">
                <div class="col-6">
                    <table class="table table-borderless mb-0">
                        <tbody>
                            <tr>
                                <td class="text-end col-6 fw-bold">Customer :</td>
                                <td class="col-6">{{customer.name}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Phone :</td>
                                <td class="">{{customer.phone}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div class="col-6">
                    <table class="table table-borderless mb-0">
                        <tbody>
                            <tr>
                                <td class="col-6 text-end fw-bold">Cashier :</td>
                                <td class="col-6">{{user.displayName}}</td>
                            </tr>
                            <tr>    
                                <td class="text-end fw-bold">Print date :</td>
                                <td class="">{{printData.print_date ? showTimeHour(printData.print_date) : ""}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <table class="table table-bordered border-dark mb-0">
                <tbody>
                    <tr class="text-center fw-bold">
                        <td class="col-2">Voucher No.</td>
                        <td  class="col-2">Date</td>
                        <td  class="col-2">Amount</td>
                        <td  class="col-2">Paid</td>
                        <td  class="col-2">Credit</td>
                    </tr>
                    </tbody>
                    <tbody class="table-group-divider" v-if="printData.saleItems.length" v-for="ps in printData.saleItems" :key="ps.id">
                    <tr>
                        <td class="text-center">{{ps.voucher_no}}</td>
                        <td class="text-center">{{showTimeHour(ps.sale_date)}}</td>
                        <td class="text-end">{{ps.net_amount}}</td>
                        <td class="text-end">{{ps.paid}}</td>
                        <td class="text-end">{{ps.credit}}</td>
                    </tr>
                    <tr v-for="dp in printData.debtpayments" :key="dp.id">
                        <td class="text-end fw-bold" colspan="2" v-if="dp.paid_for==ps.voucher_no">Payment date</td>
                        <td class="text-center"  v-if="dp.paid_for==ps.voucher_no">{{showTimeHour(dp.paid_date)}}</td>
                        <td class="text-end fw-bold"   v-if="dp.paid_for==ps.voucher_no">Paid amount</td>
                        <td class="text-end"  v-if="dp.paid_for==ps.voucher_no">{{dp.paid_amount}}</td>
                    </tr>
                </tbody>
                <tbody>
                    <tr class="fw-bold">
                        <td colspan="4" class="text-end">Total credit balance</td>
                        <td class="text-end">{{printData.total_credit}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>  
</template>
<script>
    import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
    import * as hp from "@/lib/helper"
export default {
    props:['printData', 'customer'],
    components:{
        LargePrintHeader
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        showTimeHour(t){
            return hp.showTimeHour(t);
        }
    }
}
</script>
<style lang="">
    
</style>