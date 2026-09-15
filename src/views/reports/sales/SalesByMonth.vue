<template lang="">
    <div class="overflow-x-auto">
        <table class="table table-bordered mb-0 border-dark table-hover inside-table">
            <tbody class="fw-bold text-center">
                <tr><td rowspan="2" class="col-1">Month</td></tr>
                <tr class="">
                    <td class="col-1">Code</td>
                    <td class="col-4">Product Name</td>
                    <td class="col-1 text-center">Packing Size</td>                                                                                    
                    <td class="col-1 text-center">Qty</td>
                    <td class="col-1 text-center">Sale Amount</td>
                    <td class="col-1 text-center">Pur Amount</td>
                    <td class="col-2 text-center">Profit</td>
                </tr>
            </tbody>
            <tbody v-if="!isLoading && reports.length === 0">
                <tr><td colspan="8" class="text-center text-muted py-4">No data available</td></tr>
            </tbody>
            <tbody v-if="reports.length > 0" v-for="r in reports" :key="r[0]" class="table-group-divider">    
                <tr><td class="text-center fw-bold" :rowspan="r[1].length + 4">{{r[0]}}</td></tr>                                        
                <tr v-for="p in r[1]" :key="p.code_no">
                    <td class="col-1 text-center">{{p.code_no}}</td>
                    <td class="col-4">{{p.item_name}}</td>
                    <td class="">
                        {{calPackingSize(p.packing_size?.largest_unit, p.packing_size?.medium_unit, p.packing_size?.smallest_unit, p.packing_size?.medium_qty, p.packing_size?.smallest_qty)}}
                    </td>
                    <td class="text-end">
                        {{calPackingQty(p.packing_size?.medium_qty, p.packing_size?.smallest_qty, p.sale_qty, p.packing_size?.largest_unit, p.packing_size?.medium_unit, p.packing_size?.smallest_unit)}}                                            
                    </td>
                    <td class="text-end">{{fM(p.final_amount)}}</td>
                    <td class="text-end">
                        {{fM(p.pur_amount)}}
                    </td>
                    <td class="text-end">
                        {{fM(p.profit)}}
                    </td>
                </tr>
                <tr class="text-end">
                    <td colspan="4" class="fw-bold rm-b-b border-0 border-bottom pe-2">Total (Ks)</td>
                    <td> {{fM(r[2].sale_amount)}}</td>
                    <td> {{fM(r[2].pur_amount)}}</td>
                    <td> {{fM(r[2].profit_amount)}}</td>
                </tr>
                <tr>
                    <td colspan="6" class="text-end fw-bold rm-b-b border-0 border-bottom pe-2">Total Extra Discount (Ks)</td>
                    <td class="text-end"> {{fM(r[2].extra_discount)}}</td>                  
                </tr>
                <tr>
                    <td colspan="6" class="text-end fw-bold border-0 pe-2">Net Profit (Ks)</td>
                    <td class="text-end"> {{fM(r[2].profit_amount - r[2].extra_discount)}}</td>                  
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" class="table-group-divider">
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="7">Grand Total Sale Amount (Ks)</td>
                    <td class="text-end"> {{fM(total_sale_amount)}}</td>
                </tr>                     
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="7">Grand Total Pur Amount (Ks)</td>
                    <td class="text-end"> {{fM(total_pur_amount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="7">Grand Total Extra Discount (Ks)</td>
                    <td class="text-end"> {{fM(total_extra_discount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold pe-2" colspan="7">Grand Total Profit (Ks)</td>
                    <td class="text-end"> {{fM(total_profit)}}</td>
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
                if (Array.isArray(sale.products)) {
                    sale.products.forEach(product => {
                        total += Number(product.pur_price?.smallest_unit || 0) * (Number(product.sale_qty) || 0);
                    });
                }
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
        monthlyShowTime(t) {
            if (!t) return "";
            const date = t.toDate ? t.toDate() : new Date(t);
            const options = { year: 'numeric', month: 'short' };
            return new Intl.DateTimeFormat('en-UK', options).format(date);
        },
        displayPrice(l_unit, m_unit, s_unit) {
            return hp.displayPrice(l_unit, m_unit, s_unit);
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty) {
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit) {
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        goBack() {
            this.$router.go(-1);
        },      
        displayReports() {                 
            let sales = [];
            this.salesData.forEach((res) => {                                
                res.products.forEach((data) => {
                    let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                    if (index === -1) {
                        sales.push({ ...data, group_month: this.monthlyShowTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: res.extra_discount });
                    } else {
                        sales.push({ ...data, group_month: this.monthlyShowTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: 0 });
                    }
                });
            });

            sales = sales.map((s) => {
                const retail_price = s.retail_price || {};
                const pur_price = s.pur_price || {};
                const packing_size = s.packing_size || {};
                const unitType = packing_size.largest_unit ? 'largest_unit' : 'smallest_unit';
                const group_price = `${retail_price[unitType] || 0}, ${pur_price[unitType] || 0}`;
                return { ...s, packing_size, retail_price, pur_price, group_price };
            });

            if (this.code_no) {
                sales = sales.filter((f) => f.code_no === this.code_no);
            }

            const gbm = Object.entries(hp.gb_month(sales));
          
            const gbc = gbm.map((ren) => {
                const res1 = Object.entries(hp.gb_item_name_price(ren[1]));
                const res2 = res1.map((res) => {
                    const item_name = res[1][0].item_name;
                    let item = {
                        item_name: item_name,
                        sale_qty: 0,
                        code_no: "",
                        profit: 0,               
                        extra_discount: 0,
                        packing_size: {},
                        retail_price: {},
                        whole_sale_price: {},
                        pur_price: {},
                        pur_amount: 0,
                        final_amount: 0,
                    };               
                    res[1].map((renItem) => {
                        item.packing_size = renItem.packing_size;
                        item.retail_price = renItem.retail_price;
                        item.whole_sale_price = renItem.whole_sale_price;
                        item.pur_price = renItem.pur_price;
                        item.final_amount += renItem.final_amount;
                        item.sale_qty += renItem.sale_qty;
                        item.code_no = renItem.code_no;                  
                        const purSmallest = Number(renItem.pur_price?.smallest_unit) || 0;
                        item.pur_amount += purSmallest * renItem.sale_qty;
                        item.profit += renItem.final_amount - (purSmallest * renItem.sale_qty);                                
                        item.extra_discount += Number(renItem.extra_discount);
                    });
                    return item;
                }).sort((a, b) => a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1);              
           
                let sale_amount = res2.reduce((accu, item) => Number(accu) + Number(item.final_amount), 0);
                let pur_amount = res2.reduce((accu, item) => Number(accu) + Number(item.pur_amount), 0);
                let profit_amount = sale_amount - pur_amount;
                let extra_discount = res2.reduce((accu, item) => { return this.code_no ? Number(accu) : Number(accu) + Number(item.extra_discount) }, 0);
           
                const res4 = { sale_amount, pur_amount, profit_amount, extra_discount };
                const newData = [ren[0], [...res2], res4];    
                return newData;                             
            });           
          
            this.reports = gbc;        
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
