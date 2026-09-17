<template>
    <div class="sales-by-year">
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
                            id="groupByCatSwitchYear" 
                            v-model="groupByCategory"
                        >
                        <label class="form-check-label small fw-semibold" for="groupByCatSwitchYear">
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
                            placeholder="Filter by code, name, or category..."
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
                <!-- Table Header -->
                <thead class="fw-bold text-center table-light align-middle">
                    <tr v-if="groupByCategory && hasCategory">
                        <th class="col-2 align-top">Year</th>
                        <th class="col-3 align-top text-start">Category</th>
                        <th class="col-2 text-end">Sale Amount</th>
                        <th class="col-2 text-end">Pur Amount</th>
                        <th class="col-2 text-end">Extra Discount</th>
                        <th class="col-2 text-end">Net Profit</th>
                    </tr>
                    <tr v-else>
                        <th class="col-2 align-top">Year</th>
                        <th class="col-3 text-end">Sale Amount</th>
                        <th class="col-3 text-end">Pur Amount</th>
                        <th class="col-2 text-end">Extra Discount</th>
                        <th class="col-2 text-end">Net Profit</th>
                    </tr>
                </thead>

                <!-- Empty State -->
                <tbody v-if="!isLoading && yearGroups.length === 0">
                    <tr>
                        <td :colspan="groupByCategory && hasCategory ? 6 : 5" class="text-center text-muted py-5">
                            <i class="fa-regular fa-folder-open fa-2x d-block mb-2 opacity-50"></i>
                            No sales data available for the selected period.
                        </td>
                    </tr>
                </tbody>

                <!-- Grouped by Category View (When Switch ON) -->
                <template v-if="groupByCategory && hasCategory && yearGroups.length > 0">
                    <tbody v-for="r in yearGroups" :key="r.year" class="table-group-divider">
                        <tr v-for="(cat, cIndex) in r.categories" :key="cat.categoryName">
                            <!-- Year cell on first category row -->
                            <td 
                                v-if="cIndex === 0" 
                                class="text-center fw-bold align-top bg-light-subtle" 
                                :rowspan="r.categories.length + 1"
                            >
                                {{ r.year }}
                            </td>

                            <td class="fw-semibold text-primary">
                                {{ cat.categoryName }}
                            </td>
                            <td class="text-end">{{ fM(cat.sale_amount) }}</td>
                            <td class="text-end">{{ fM(cat.pur_amount) }}</td>
                            <td class="text-end text-muted">{{ fM(cat.extra_discount) }}</td>
                            <td class="text-end fw-semibold" :class="{'text-danger': cat.net_profit < 0}">
                                {{ fM(cat.net_profit) }}
                            </td>
                        </tr>

                        <!-- Year Total Row -->
                        <tr class="table-secondary fw-bold text-end">
                            <td class="pe-3 text-start">Total {{ r.year }} (Ks)</td>
                            <td>{{ fM(r.sale_amount) }}</td>
                            <td>{{ fM(r.pur_amount) }}</td>
                            <td>{{ fM(r.extra_discount) }}</td>
                            <td class="text-success">{{ fM(r.net_profit) }}</td>
                        </tr>
                    </tbody>
                </template>

                <!-- Standard Year View (When Group by Category is OFF - default) -->
                <template v-else-if="yearGroups.length > 0">
                    <tbody class="table-group-divider">
                        <tr v-for="r in yearGroups" :key="r.year">
                            <td class="text-center fw-bold">{{ r.year }}</td>
                            <td class="text-end">{{ fM(r.sale_amount) }}</td>
                            <td class="text-end">{{ fM(r.pur_amount) }}</td>
                            <td class="text-end">{{ fM(r.extra_discount) }}</td>
                            <td class="text-end fw-bold" :class="{'text-danger': r.net_profit < 0, 'text-success': r.net_profit >= 0}">
                                {{ fM(r.net_profit) }}
                            </td>
                        </tr>
                    </tbody>
                </template>

                <!-- Grand Totals Footer -->
                <tbody v-if="yearGroups.length > 0" class="table-group-divider">
                    <tr class="table-secondary fw-bold">
                        <td :colspan="groupByCategory && hasCategory ? 5 : 4" class="text-end pe-3">Grand Total Sale Amount (Ks)</td>
                        <td class="text-end">{{ fM(total_sale_amount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td :colspan="groupByCategory && hasCategory ? 5 : 4" class="text-end pe-3">Grand Total Pur Amount (Ks)</td>
                        <td class="text-end">{{ fM(total_pur_amount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td :colspan="groupByCategory && hasCategory ? 5 : 4" class="text-end pe-3">Grand Total Extra Discount (Ks)</td>
                        <td class="text-end">{{ fM(total_extra_discount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td :colspan="groupByCategory && hasCategory ? 5 : 4" class="text-end pe-3">Grand Total Profit (Ks)</td>
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
import { Timestamp, collection, where, orderBy, query, onSnapshot } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/sales"

export default {
    name: "SalesByYear",
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
                            group_year: this.yearShowTime(res.sale_date), 
                            voucher_no: res.voucher_no, 
                            extra_discount: Number(res.extra_discount) || 0,
                            category_name: categoryName
                        });
                    } else {
                        sales.push({ 
                            ...data, 
                            group_year: this.yearShowTime(res.sale_date), 
                            voucher_no: res.voucher_no, 
                            extra_discount: 0,
                            category_name: categoryName
                        });
                    }
                });
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
        yearGroups() {
            const yearMap = {};
            this.filteredSales.forEach(s => {
                const year = s.group_year || '-';
                if (!yearMap[year]) {
                    yearMap[year] = [];
                }
                yearMap[year].push(s);
            });

            const sortedYears = Object.keys(yearMap).sort((a, b) => b.localeCompare(a));

            return sortedYears.map(year => {
                const salesInYear = yearMap[year];
                
                const catMap = {};
                salesInYear.forEach(item => {
                    const cat = item.category_name || 'No Category';
                    if (!catMap[cat]) {
                        catMap[cat] = {
                            categoryName: cat,
                            sale_amount: 0,
                            pur_amount: 0,
                            extra_discount: 0,
                            profit_amount: 0,
                            net_profit: 0
                        };
                    }
                    const saleAmt = Number(item.final_amount) || 0;
                    const purSmallest = Number(item.pur_price?.smallest_unit) || 0;
                    const purAmt = purSmallest * (Number(item.sale_qty) || 0);
                    const extraDisc = Number(item.extra_discount) || 0;

                    catMap[cat].sale_amount += saleAmt;
                    catMap[cat].pur_amount += purAmt;
                    catMap[cat].extra_discount += extraDisc;
                    catMap[cat].profit_amount += (saleAmt - purAmt);
                    catMap[cat].net_profit += (saleAmt - purAmt - extraDisc);
                });

                const categories = Object.values(catMap).sort((a, b) => a.categoryName.localeCompare(b.categoryName));

                const sale_amount = categories.reduce((acc, c) => acc + c.sale_amount, 0);
                const pur_amount = categories.reduce((acc, c) => acc + c.pur_amount, 0);
                const extra_discount = this.code_no ? 0 : categories.reduce((acc, c) => acc + c.extra_discount, 0);
                const profit_amount = sale_amount - pur_amount;
                const net_profit = profit_amount - extra_discount;

                return {
                    year,
                    categories,
                    sale_amount,
                    pur_amount,
                    extra_discount,
                    profit_amount,
                    net_profit
                };
            });
        },
        total_sale_amount() {
            return this.yearGroups.reduce((sum, yg) => sum + yg.sale_amount, 0);
        },
        total_pur_amount() {
            return this.yearGroups.reduce((sum, yg) => sum + yg.pur_amount, 0);
        },
        total_extra_discount() {
            return this.yearGroups.reduce((sum, yg) => sum + yg.extra_discount, 0);
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
        yearShowTime(t) {
            if (!t) return "";
            const date = t.toDate ? t.toDate() : new Date(t);
            return date.getFullYear().toString();
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
                console.error("Error fetching sales by year:", error);
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
