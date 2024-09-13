<template lang="">
    <div class="card">
        <div class="card-body p-2">
            <LargePrintHeader />
            <div class="row">
                <div class="col-12 h5 text-center">SOC</div>
            </div>
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
                                <td class="">{{print_date}}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
            <table class="table table-hover table-bordered mb-0">
                <tbody>
                    <tr class="text-center table-secondary fw-bold">
                        <td class="col-2">Voucher No.</td>
                        <td  class="col-2">Date</td>
                        <td  class="col-2">Amount</td>
                        <td  class="col-2">Credit</td>
                    </tr>
                </tbody>
                <tbody v-for="s in saleItems" :key="s.id" class="table-group-divider">
                    <tr>
                        <td class="text-center" style="border-bottom: 2px solid;">{{s.voucher_no}}</td>
                        <td class="text-center" style="border-bottom: 2px solid;">{{showTimeHour(s.sale_date)}}</td>
                        <td class="text-end" style="border-bottom: 2px solid;">{{s.net_amount}}</td>
                        <td class="text-end" style="border-bottom: 2px solid;">{{s.credit}}</td>
                     </tr>
                     <tr v-if="s.paid">
                        <td colspan=""></td>
                        <td class="text-center">{{showTimeHour(s.sale_date)}}</td>
                        <td class="text-end fw-bold">Paid</td>
                        <td class="text-end">{{s.paid}}</td>
                     </tr>
                    <tr v-for="dp in s.debt_payments" :key="dp.paid_date">
                            <td colspan=""></td>
                            <td class="text-center">{{showTimeHour(dp.paid_date)}}</td>
                            <td class="text-end fw-bold">Paid</td>
                            <td class="text-end">{{dp.paid}}</td>
                        </tr>
                        <tr>
                            <td colspan="2"></td>
                            <td class="text-end fw-bold">Total paid</td>
                            <td class="text-end">{{s.total_paid}}</td>
                         </tr>
                   
                </tbody>
                <tbody class="table-group-divider">
                    <tr class="text-center fw-bold">
                        <td colspan=""></td>
                        <td>Total amount</td>
                        <td>Total paid</td>
                        <td>Total credit</td>
                    </tr>
                    <tr class="text-end">
                        <td colspan=""></td>
                        <td>{{total_amount}}</td>
                        <td>{{total_paid}}</td>
                        <td>{{total_credit}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>  
</template>
<script>
    import LargePrintHeader from "@/views/partials/LargePrintHeader.vue"
    import * as hp from "@/lib/helper"
    import {disableNetwork, Timestamp, collection, addDoc, doc, increment,  updateDoc, onSnapshot, query, where, orderBy, getDocs} from 'firebase/firestore'

export default {
    props:['customer', 'saleItems', 'total_amount', 'total_paid', 'total_credit'],
    components:{
        LargePrintHeader
    },
    data(){
        return {
            print_date:"",
        }
    },
    mounted() {
        this.setPrintDate();
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        showTimeHour(t){
            return hp.showTimeHour(t);
        },
        setPrintDate(){
            const date=new Date();
            let print_date=new Date();
            print_date.setHours(date.getHours())
            print_date.setMinutes(date.getMinutes())
            this.print_date=hp.showTimeHour(Timestamp.fromDate(print_date))
        },

    }
}
</script>
<style lang="">
    
</style>