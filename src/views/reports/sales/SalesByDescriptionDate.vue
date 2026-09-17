<template>
    <div class="sales-by-description-date">
        <!-- Controls Toolbar (Hidden on Print) -->
        <div class="d-print-none mb-2 p-1 bg-light rounded-3 border shadow-sm">
            <div class="row g-2 align-items-center justify-content-between">
                <!-- Left: Group by Category & Category Filter Pills -->
                <div class="col-auto d-flex flex-wrap align-items-center gap-3">
                    <!-- Group by Category Switch (OFF by default) -->
                    <div class="form-check form-switch mb-0" v-if="hasCategory">
                        <input 
                            class="form-check-input" 
                            type="checkbox" 
                            id="groupByCatSwitchDescDate" 
                            v-model="groupByCategory"
                        >
                        <label class="form-check-label small fw-semibold" for="groupByCatSwitchDescDate">
                            Group by Category
                        </label>
                    </div>

                    <!-- Category Filter Pills -->
                    <div class="d-flex flex-wrap align-items-center gap-1 border-start ps-3" v-if="allCategories.length > 1">
                        <span class="small text-muted me-1 fw-semibold"><i class="fa-solid fa-tags me-1"></i>Categories:</span>
                        <button 
                            type="button" 
                            class="btn btn-xs py-0 px-2 rounded-pill me-1" 
                            :class="selectedCategories.length === allCategories.length ? 'btn-primary' : 'btn-outline-secondary'"
                            @click="selectAllCategories"
                        >
                            All
                        </button>
                        <button 
                            v-for="cat in allCategories" 
                            :key="cat"
                            type="button" 
                            class="btn btn-xs py-0 px-2 rounded-pill"
                            :class="selectedCategories.includes(cat) ? 'btn-primary' : 'btn-outline-secondary'"
                            @click="toggleCategory(cat)"
                        >
                            {{ cat }}
                        </button>
                    </div>
                </div>

                <!-- Right: Search Input -->
                <div class="col-12 col-md-4 col-lg-3">
                    <div class="input-group input-group-sm">
                        <span class="input-group-text bg-white"><i class="fa-solid fa-magnifying-glass text-muted"></i></span>
                        <input 
                            type="text" 
                            class="form-control" 
                            v-model="searchQuery" 
                            placeholder="Filter by code or name..."
                        >
                        <button 
                            v-if="searchQuery" 
                            class="btn btn-outline-secondary" 
                            type="button" 
                            @click="searchQuery = ''"
                        >
                            <i class="fa-solid fa-times"></i>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Main Report Table -->
        <div class="overflow-x-auto">
            <table class="table table-bordered border-dark table-hover inside-table align-middle mb-0">
                <tbody v-if="!isLoading && productGroups.length === 0">
                    <tr>
                        <td colspan="7" class="text-center text-muted py-5">
                            <i class="fa-regular fa-folder-open fa-2x d-block mb-2 opacity-50"></i>
                            No sales data available for the selected period.
                        </td>
                    </tr>
                </tbody>

                <!-- Grouped by Category View -->
                <template v-if="groupByCategory && hasCategory && categoryHierarchicalGroups.length > 0">
                    <tbody v-for="catGroup in categoryHierarchicalGroups" :key="catGroup.categoryName" class="table-group-divider">
                        <!-- Category Header Banner -->
                        <tr class="table-primary">
                            <td colspan="7" class="fw-bold py-2 fs-6">
                                <i class="fa-solid fa-tag me-2"></i>Category: {{ catGroup.categoryName }}
                            </td>
                        </tr>

                        <!-- Products in this Category -->
                        <template v-for="r in catGroup.products" :key="r.item_name">
                            <tr class="table-light">
                                <td colspan="7" class="fw-bold ps-3 text-primary">
                                    {{ r.item_name }} <span class="badge bg-secondary ms-2" v-if="r.code_no">{{ r.code_no }}</span>
                                </td>
                            </tr>
                            <tr class="fw-bold text-center table-light">
                                <th class="col-1">Date</th>
                                <th class="col-1">Code</th>
                                <th class="col-pack text-center" style="width: 120px;">Packing Size</th>
                                <th class="col-qty text-center" style="width: 90px;">Qty</th>
                                <th class="text-end" style="width: 110px;">Sale Amount</th>
                                <th class="text-end" style="width: 110px;">Pur Amount</th>
                                <th class="text-end" style="width: 110px;">Profit</th>
                            </tr>

                            <!-- Dates for this product -->
                            <template v-for="res in r.dates" :key="res.date">
                                <tr v-for="(p, pIndex) in res.items" :key="p.code_no + '-' + pIndex">
                                    <td v-if="pIndex === 0" class="text-center fw-semibold align-top bg-light-subtle" :rowspan="res.items.length + 3">
                                        {{ res.date }}
                                    </td>
                                    <td class="text-center font-monospace">{{ p.code_no }}</td>
                                    <td class="text-center small text-muted">{{ formatPackingSize(p.packing_size) }}</td>
                                    <td class="text-end">{{ formatPackingQty(p.packing_size, p.sale_qty) }}</td>
                                    <td class="text-end">{{ fM(p.final_amount) }}</td>
                                    <td class="text-end">{{ fM(p.pur_amount) }}</td>
                                    <td class="text-end" :class="{'text-danger': p.profit < 0}">{{ fM(p.profit) }}</td>
                                </tr>
                                <tr class="text-end table-light">
                                    <td colspan="3" class="fw-bold pe-2">Total (Ks)</td>
                                    <td>{{ fM(res.sale_amount) }}</td>
                                    <td>{{ fM(res.pur_amount) }}</td>
                                    <td>{{ fM(res.profit_amount) }}</td>
                                </tr>
                                <tr class="text-end table-light">
                                    <td colspan="5" class="fw-semibold pe-2 text-muted">Extra Discount (Ks)</td>
                                    <td>{{ fM(res.extra_discount) }}</td>
                                </tr>
                                <tr class="text-end table-light fw-bold">
                                    <td colspan="5" class="pe-2 text-muted">Net Profit (Ks)</td>
                                    <td class="text-success">{{ fM(res.net_profit) }}</td>
                                </tr>
                            </template>
                        </template>

                        <!-- Category Subtotal Summary -->
                        <tr class="table-info fw-bold text-end">
                            <td colspan="4" class="pe-2">Total {{ catGroup.categoryName }} (Ks)</td>
                            <td>{{ fM(catGroup.sale_amount) }}</td>
                            <td>{{ fM(catGroup.pur_amount) }}</td>
                            <td class="text-primary">{{ fM(catGroup.profit_amount) }}</td>
                        </tr>
                    </tbody>
                </template>

                <!-- Standard Product -> Date View (When Group by Category is OFF - default) -->
                <template v-else-if="productGroups.length > 0">
                    <tbody v-for="r in productGroups" :key="r.item_name" class="table-group-divider">
                        <tr class="table-light">
                            <td colspan="7" class="fw-bold ps-2 py-2 text-primary fs-6">
                                <i class="fa-solid fa-box me-2"></i>{{ r.item_name }} <span class="badge bg-secondary ms-2" v-if="r.code_no">{{ r.code_no }}</span>
                            </td>
                        </tr>
                        <tr class="fw-bold text-center table-light">
                            <th class="col-1">Date</th>
                            <th class="col-1">Code</th>
                            <th class="col-pack text-center" style="width: 120px;">Packing Size</th>
                            <th class="col-qty text-center" style="width: 90px;">Qty</th>
                            <th class="text-end" style="width: 110px;">Sale Amount</th>
                            <th class="text-end" style="width: 110px;">Pur Amount</th>
                            <th class="text-end" style="width: 110px;">Profit</th>
                        </tr>

                        <!-- Dates for this product -->
                        <template v-for="res in r.dates" :key="res.date">
                            <tr v-for="(p, pIndex) in res.items" :key="p.code_no + '-' + pIndex">
                                <td v-if="pIndex === 0" class="text-center fw-semibold align-top bg-light-subtle" :rowspan="res.items.length + 3">
                                    {{ res.date }}
                                </td>
                                <td class="text-center font-monospace">{{ p.code_no }}</td>
                                <td class="text-center small text-muted">{{ formatPackingSize(p.packing_size) }}</td>
                                <td class="text-end">{{ formatPackingQty(p.packing_size, p.sale_qty) }}</td>
                                <td class="text-end">{{ fM(p.final_amount) }}</td>
                                <td class="text-end">{{ fM(p.pur_amount) }}</td>
                                <td class="text-end" :class="{'text-danger': p.profit < 0}">{{ fM(p.profit) }}</td>
                            </tr>
                            <tr class="text-end table-light">
                                <td colspan="3" class="fw-bold pe-2">Total (Ks)</td>
                                <td>{{ fM(res.sale_amount) }}</td>
                                <td>{{ fM(res.pur_amount) }}</td>
                                <td>{{ fM(res.profit_amount) }}</td>
                            </tr>
                            <tr class="text-end table-light">
                                <td colspan="5" class="fw-semibold pe-2 text-muted">Extra Discount (Ks)</td>
                                <td>{{ fM(res.extra_discount) }}</td>
                            </tr>
                            <tr class="text-end table-light fw-bold">
                                <td colspan="5" class="pe-2 text-muted">Net Profit (Ks)</td>
                                <td class="text-success">{{ fM(res.net_profit) }}</td>
                            </tr>
                        </template>
                    </tbody>
                </template>

                <!-- Grand Totals Footer -->
                <tbody v-if="productGroups.length > 0" class="table-group-divider">
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="6">Grand Total Sale Amount (Ks)</td>
                        <td class="text-end">{{ fM(total_sale_amount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="6">Grand Total Pur Amount (Ks)</td>
                        <td class="text-end">{{ fM(total_pur_amount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="6">Grand Total Extra Discount (Ks)</td>
                        <td class="text-end">{{ fM(total_extra_discount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="6">Grand Total Profit (Ks)</td>
                        <td class="text-end text-success fs-6">{{ fM(total_profit) }}</td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- Loading Spinner -->
        <div v-if="isLoading" class="d-flex justify-content-center my-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    </div>
</template>

<script>
import { Timestamp, onSnapshot, collection, where, orderBy, query } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/sales"

export default {
    name: "SalesByDescriptionDate",
    props: {
        fromDate: {
            type: String,
            required: true
        },
        toDate: {
            type: String,
            required: true
        },
        customer_code: {
            type: [String, Number],
            default: null
        },
        code_no: {
            type: String,
            default: null
        }
    },
    data() {
        return {
            salesData: [],
            isLoading: false,
            unsub: null,
            searchQuery: '',
            groupByCategory: false,
            selectedCategories: []
        };
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        processedSales() {
            let sales = [];
            this.salesData.forEach((res) => {
                if (!Array.isArray(res.products)) return;
                res.products.forEach((data) => {
                    let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                    const categoryName = (data.category?.cat_name || data.category_name || (typeof data.category === 'string' ? data.category : '') || '').trim() || 'No Category';
                    if (index === -1) {
                        sales.push({ 
                            ...data, 
                            group_date: hp.showTime(res.sale_date), 
                            voucher_no: res.voucher_no, 
                            extra_discount: Number(res.extra_discount) || 0,
                            category_name: categoryName
                        });
                    } else {
                        sales.push({ 
                            ...data, 
                            group_date: hp.showTime(res.sale_date), 
                            voucher_no: res.voucher_no, 
                            extra_discount: 0,
                            category_name: categoryName
                        });
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
            return sales;
        },
        hasCategory() {
            return this.processedSales.some(s => s.category_name && s.category_name !== 'No Category');
        },
        allCategories() {
            const set = new Set();
            this.processedSales.forEach(s => {
                if (s.category_name && s.category_name !== 'No Category') {
                    set.add(s.category_name);
                }
            });
            return Array.from(set).sort((a, b) => a.localeCompare(b));
        },
        filteredSales() {
            let list = this.processedSales;

            if (this.hasCategory && this.selectedCategories.length > 0 && this.selectedCategories.length < this.allCategories.length) {
                list = list.filter(p => this.selectedCategories.includes(p.category_name));
            }

            if (this.searchQuery) {
                const q = this.searchQuery.toLowerCase().trim();
                list = list.filter(p => 
                    (p.item_name && String(p.item_name).toLowerCase().includes(q)) || 
                    (p.code_no && String(p.code_no).toLowerCase().includes(q)) ||
                    (p.category_name && String(p.category_name).toLowerCase().includes(q))
                );
            }
            return list;
        },
        productGroups() {
            // Group by code_no or item_name
            const prodMap = {};
            this.filteredSales.forEach(s => {
                const key = s.code_no || s.item_name;
                if (!prodMap[key]) {
                    prodMap[key] = [];
                }
                prodMap[key].push(s);
            });

            return Object.values(prodMap).map(salesOfProduct => {
                const first = salesOfProduct[0];
                const item_name = String(first.item_name || '-');
                const code_no = first.code_no ? String(first.code_no) : "-";
                const category_name = String(first.category_name || 'No Category');

                // Group by date
                const dateMap = {};
                salesOfProduct.forEach(s => {
                    const d = s.group_date || '-';
                    if (!dateMap[d]) dateMap[d] = [];
                    dateMap[d].push(s);
                });

                const dates = Object.entries(dateMap).map(([date, salesInDate]) => {
                    const res1 = Object.entries(hp.gb_item_name_price(salesInDate));
                    const items = res1.map(res => {
                        const itmFirst = res[1][0];
                        let item = {
                            item_name: String(itmFirst.item_name || '-'),
                            category_name: String(itmFirst.category_name || 'No Category'),
                            code_no: itmFirst.code_no ? String(itmFirst.code_no) : "-",
                            sale_qty: 0,
                            profit: 0,
                            extra_discount: 0,
                            packing_size: itmFirst.packing_size || {},
                            retail_price: itmFirst.retail_price || {},
                            whole_sale_price: itmFirst.whole_sale_price || {},
                            pur_price: itmFirst.pur_price || {},
                            pur_amount: 0,
                            final_amount: 0,
                        };
                        res[1].forEach(ren => {
                            item.packing_size = ren.packing_size;
                            item.retail_price = ren.retail_price;
                            item.whole_sale_price = ren.whole_sale_price;
                            item.pur_price = ren.pur_price;
                            item.final_amount += Number(ren.final_amount) || 0;
                            item.sale_qty += Number(ren.sale_qty) || 0;
                            if (ren.code_no) item.code_no = String(ren.code_no);
                            const purSmallest = Number(ren.pur_price?.smallest_unit) || 0;
                            const purAmount = purSmallest * (Number(ren.sale_qty) || 0);
                            item.pur_amount += purAmount;
                            item.profit += (Number(ren.final_amount) || 0) - purAmount;
                            item.extra_discount += Number(ren.extra_discount) || 0;
                        });
                        return item;
                    });

                    const sale_amount = items.reduce((acc, it) => acc + it.final_amount, 0);
                    const pur_amount = items.reduce((acc, it) => acc + it.pur_amount, 0);
                    const profit_amount = sale_amount - pur_amount;
                    const extra_discount = this.code_no ? 0 : items.reduce((acc, it) => acc + it.extra_discount, 0);
                    const net_profit = profit_amount - extra_discount;

                    return { date, items, sale_amount, pur_amount, profit_amount, extra_discount, net_profit };
                });

                return { item_name, code_no, category_name, dates };
            }).sort((a, b) => a.item_name.localeCompare(b.item_name));
        },
        categoryHierarchicalGroups() {
            const catMap = {};
            this.productGroups.forEach(p => {
                const cat = p.category_name || 'No Category';
                if (!catMap[cat]) {
                    catMap[cat] = {
                        categoryName: cat,
                        products: [],
                        sale_amount: 0,
                        pur_amount: 0,
                        profit_amount: 0
                    };
                }
                catMap[cat].products.push(p);
                p.dates.forEach(d => {
                    catMap[cat].sale_amount += d.sale_amount;
                    catMap[cat].pur_amount += d.pur_amount;
                    catMap[cat].profit_amount += d.profit_amount;
                });
            });
            return Object.values(catMap).sort((a, b) => a.categoryName.localeCompare(b.categoryName));
        },
        total_sale_amount() {
            return this.productGroups.reduce((sum, p) => {
                return sum + p.dates.reduce((dSum, d) => dSum + d.sale_amount, 0);
            }, 0);
        },
        total_pur_amount() {
            return this.productGroups.reduce((sum, p) => {
                return sum + p.dates.reduce((dSum, d) => dSum + d.pur_amount, 0);
            }, 0);
        },
        total_extra_discount() {
            return this.productGroups.reduce((sum, p) => {
                return sum + p.dates.reduce((dSum, d) => dSum + d.extra_discount, 0);
            }, 0);
        },
        total_profit() {
            return (this.total_sale_amount - this.total_pur_amount) - this.total_extra_discount;
        }
    },
    watch: {
        fromDate() { this.fetchSales(); },
        toDate() { this.fetchSales(); },
        customer_code() { this.fetchSales(); },
        code_no() { this.fetchSales(); },
        allCategories: {
            immediate: true,
            handler(cats) {
                if (cats && cats.length > 0) {
                    const valid = this.selectedCategories.filter(c => cats.includes(c));
                    if (valid.length === 0) {
                        this.selectedCategories = [...cats];
                    } else {
                        this.selectedCategories = valid;
                    }
                }
            }
        }
    },
    mounted() {
        this.isLoading = true;
        this.fetchSales();
    },
    beforeUnmount() {
        if (this.unsub) this.unsub();
    },
    methods: {
        fM(amount) {
            return hp.formattedAmount(amount || 0);
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty) {
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit) {
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        formatPackingSize(ps) {
            if (!ps) return '-';
            return hp.calPackingSize(ps.largest_unit, ps.medium_unit, ps.smallest_unit, ps.medium_qty, ps.smallest_qty);
        },
        formatPackingQty(ps, qty) {
            if (!ps) return qty || 0;
            const res = hp.calPackingQty(ps.medium_qty, ps.smallest_qty, qty, ps.largest_unit, ps.medium_unit, ps.smallest_unit);
            return res || qty || '0';
        },
        selectAllCategories() {
            this.selectedCategories = [...this.allCategories];
        },
        toggleCategory(cat) {
            if (this.selectedCategories.length === this.allCategories.length) {
                this.selectedCategories = [cat];
            } else if (this.selectedCategories.includes(cat)) {
                if (this.selectedCategories.length > 1) {
                    this.selectedCategories = this.selectedCategories.filter(c => c !== cat);
                } else {
                    this.selectedCategories = [...this.allCategories];
                }
            } else {
                this.selectedCategories.push(cat);
            }
        },
        goBack() {
            this.$router.go(-1);
        },
        async fetchSales() {
            if (this.unsub) {
                this.unsub();
                this.unsub = null;
            }
            this.isLoading = true;

            let fDate = new Date(this.fromDate);
            fDate.setHours(0, 0, 0, 0);
            const from_date = Timestamp.fromDate(fDate);

            let tDate = new Date(this.toDate);
            tDate.setHours(23, 59, 59, 999);
            const to_date = Timestamp.fromDate(tDate);

            let query_url = query(
                collection(db, "sales"),
                where("sale_date", ">=", from_date),
                where("sale_date", "<=", to_date),
                orderBy("sale_date", "desc")
            );

            if (this.customer_code) {
                query_url = query(
                    collection(db, "sales"),
                    where("sale_date", ">=", from_date),
                    where("sale_date", "<=", to_date),
                    where("customer.code", "==", Number(this.customer_code)),
                    orderBy("sale_date", "desc")
                );
            }

            this.unsub = onSnapshot(query_url, (snapShot) => {
                const saleItems = ss.sales(snapShot);
                this.salesData = saleItems;
                this.isLoading = false;
            }, (error) => {
                console.error("Error fetching sales by description date:", error);
                this.isLoading = false;
            });
        }
    }
};
</script>

<style scoped>
.inside-table {
    font-size: 0.85rem;
}
.inside-table th {
    white-space: nowrap;
    vertical-align: middle;
}
.btn-xs {
    font-size: 0.75rem;
    line-height: 1.5;
}
@media print {
    .inside-table {
        font-size: 10px !important;
    }
}
</style>
