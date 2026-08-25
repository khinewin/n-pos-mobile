<template lang="">
    <div class="overflow-x-auto">
        <table class="table table-bordered mb-0 border-dark table-hover inside-table">
            <thead class="fw-bold text-center">
                <tr>
                    <td class="col-2">Year</td>
                    <td class="col-3 text-center">Sale Amount</td>
                    <td class="col-3 text-center">Pur Amount</td>
                    <td class="col-2 text-center">Extra Discount</td>
                    <td class="col-2 text-center">Net Profit</td>
                </tr>
            </thead>
            <tbody v-if="!isLoading && reports.length === 0">
                <tr><td colspan="5" class="text-center text-muted py-4">No data available</td></tr>
            </tbody>
            <tbody v-if="reports.length > 0">    
                <tr v-for="r in reports" :key="r.year">
                    <td class="text-center fw-bold">{{r.year}}</td>
                    <td class="text-end">{{fM(r.sale_amount)}}</td>
                    <td class="text-end">{{fM(r.pur_amount)}}</td>
                    <td class="text-end">{{fM(r.extra_discount)}}</td>
                    <td class="text-end fw-bold">{{fM(r.profit_amount - r.extra_discount)}}</td>
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" class="table-group-divider">
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="4">Grand Total Sale Amount (Ks)</td>
                    <td class="text-end fw-bold"> {{fM(total_sale_amount)}}</td>
                </tr>                     
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="4">Grand Total Pur Amount (Ks)</td>
                    <td class="text-end fw-bold"> {{fM(total_pur_amount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="4">Grand Total Extra Discount (Ks)</td>
                    <td class="text-end fw-bold"> {{fM(total_extra_discount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold pe-2" colspan="4">Grand Total Profit (Ks)</td>
                    <td class="text-end fw-bold text-success"> {{fM(total_profit)}}</td>
                </tr>
            </tbody>
        </table>
        <div v-if="isLoading">
            <div class="d-flex justify-content-center my-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        </div>                                                                                                                       
    </div>
</template>
<script>
import { Timestamp, collection, getDocs, query, where, orderBy, onSnapshot } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/sales"

export default {
    props: ['fromDate', 'toDate', 'customer_code', 'code_no'],   
    data() {
        return {                  
            reports: [],
            salesData: [],          
            isLoading: false,
            unsub: null,
            msg: {
                type: "",
                info: "",
            },        
        }
    },
    mounted() {    
        this.isLoading = true;
        this.fetchSales();
    },
    beforeUnmount() {
        if (this.unsub) this.unsub();
    },
    watch: {
        msg: {
            handler(val) {
                if (val.type) {
                    setTimeout(() => {
                        this.msg.type = "";
                        this.msg.info = "";
                    }, 5000)
                }
            },
            deep: true
        },
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        total_sale_amount() {
            return this.salesData.reduce((total, sale) => total + Number(sale.total_amount), 0);
        },
        total_pur_amount() {
            return this.salesData.reduce((total, sale) => {
                sale.products.forEach(product => {
                    total += Number(product.pur_price.smallest_unit) * product.sale_qty;
                });
                return total;
            }, 0);
        },
        total_extra_discount() {
            return this.salesData.reduce((total, sale) => total + Number(sale.extra_discount), 0);
        },
        total_profit() {
            return (this.total_sale_amount - this.total_pur_amount) - this.total_extra_discount;
        }
    },
    methods: {
        fM(amount) {
            return hp.formattedAmount(amount);
        },
        yearShowTime(t) {
            if (!t) return "";
            const date = t.toDate ? t.toDate() : new Date(t);
            return date.getFullYear().toString();
        },
        goBack() {
            this.$router.go(-1);
        },      
        displayReports() {                 
            let yearlyData = {};

            this.salesData.forEach((sale) => {
                const year = this.yearShowTime(sale.sale_date);
                if (!yearlyData[year]) {
                    yearlyData[year] = {
                        year: year,
                        sale_amount: 0,
                        pur_amount: 0,
                        extra_discount: 0,
                        profit_amount: 0
                    };
                }

                const saleTotal = Number(sale.total_amount) || 0;
                const extraDisc = Number(sale.extra_discount) || 0;
                let purTotal = 0;

                sale.products.forEach((p) => {
                    if (!this.code_no || p.code_no === this.code_no) {
                        purTotal += (Number(p.pur_price?.smallest_unit) || 0) * (Number(p.sale_qty) || 0);
                    }
                });

                yearlyData[year].sale_amount += saleTotal;
                yearlyData[year].pur_amount += purTotal;
                yearlyData[year].extra_discount += extraDisc;
                yearlyData[year].profit_amount += (saleTotal - purTotal);
            });

            // Convert to array and sort by year descending
            const reportsArr = Object.values(yearlyData).sort((a, b) => b.year.localeCompare(a.year));
            this.reports = reportsArr;        
        },   
        async fetchSales() {           
            let fDate = new Date(this.fromDate);
            fDate.setHours("0", "0", "0");
            const from_date = Timestamp.fromDate(fDate);
            let tDate = new Date(this.toDate);
            tDate.setHours("23", "59", "59");
            const to_date = Timestamp.fromDate(tDate);

            let query_url = query(collection(db, "sales"), 
                where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));

            if (this.customer_code) {
                query_url = query(collection(db, "sales"), 
                    where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
            }
            this.unsub = onSnapshot(query_url, (snapShot) => {
                const saleItems = ss.sales(snapShot);
                this.salesData = saleItems;
                this.displayReports();
                this.isLoading = false;
            });
        }
    }
}
</script>
<style lang="">
    
</style>
