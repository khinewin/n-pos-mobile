<template>
    <div class="sales-by-description">
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
                            id="groupByCatSwitch" 
                            v-model="groupByCategory"
                        >
                        <label class="form-check-label small fw-semibold" for="groupByCatSwitch">
                            Group by Category
                        </label>
                    </div>

                    <!-- Category Filter Pills right after Group by Category -->
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
            <table class="table table-bordered border-dark table-hover inside-table align-middle">
                <!-- Table Header -->
                <thead class="fw-bold text-center table-light align-middle">
                    <!-- Grouped Header -->
                    <tr v-if="groupByCategory && hasCategory">
                        <th class="col-1 align-top">Category</th>
                        <th class="col-code" style="width: 100px;">Code</th>
                        <th class="text-start">Product Name</th>
                        <th class="col-pack text-center" style="width: 120px;">Packing Size</th>
                        <th class="col-qty text-center" style="width: 90px;">Qty</th>
                        <th class="text-end" style="width: 110px;">Sale Amount</th>
                        <th class="text-end" style="width: 110px;">Pur Amount</th>
                        <th class="text-end" style="width: 110px;">Profit</th>
                    </tr>

                    <!-- Flat Header (when Group by Category is OFF - default) -->
                    <tr v-else>
                        <th class="col-index" style="width: 40px;">#</th>
                        <th class="col-code" style="width: 100px;">Code</th>
                        <th class="text-start">Product Name</th>
                        <th class="col-pack text-center" style="width: 120px;">Packing Size</th>
                        <th class="col-qty text-center" style="width: 90px;">Qty</th>
                        <th class="text-end" style="width: 110px;">Sale Amount</th>
                        <th class="text-end" style="width: 110px;">Pur Amount</th>
                        <th class="text-end" style="width: 110px;">Profit</th>
                    </tr>
                </thead>

                <!-- Empty State -->
                <tbody v-if="!isLoading && filteredProducts.length === 0">
                    <tr>
                        <td colspan="8" class="text-center text-muted py-5">
                            <i class="fa-regular fa-folder-open fa-2x d-block mb-2 opacity-50"></i>
                            No sales data available for the selected period.
                        </td>
                    </tr>
                </tbody>

                <!-- Grouped by Category View (When ON) -->
                <template v-if="groupByCategory && hasCategory && categoryGroups.length > 0">
                    <tbody v-for="group in categoryGroups" :key="group.categoryName" class="table-group-divider">
                        <tr v-for="(p, pIndex) in group.items" :key="p.code_no + '-' + p.item_name + '-' + pIndex">
                            <!-- Category cell with rowspan on the first product row -->
                            <td 
                                v-if="pIndex === 0" 
                                class="text-center fw-bold align-top bg-light-subtle text-primary py-2" 
                                :rowspan="group.items.length + 3"
                            >
                                {{ group.categoryName }}
                            </td>

                            <td class="text-center fw-semibold font-monospace">{{ p.code_no }}</td>
                            <td>{{ p.item_name }}</td>
                            <td class="text-center small text-muted">
                                {{ formatPackingSize(p.packing_size) }}
                            </td>
                            <td class="text-end">
                                {{ formatPackingQty(p.packing_size, p.sale_qty) }}
                            </td>
                            <td class="text-end">{{ fM(p.final_amount) }}</td>
                            <td class="text-end">{{ fM(p.pur_amount) }}</td>
                            <td class="text-end" :class="{'text-danger': p.profit < 0}">{{ fM(p.profit) }}</td>
                        </tr>

                        <!-- Category Subtotal Rows -->
                        <tr class="table-light fw-bold text-end">
                            <td colspan="4" class="pe-3 text-muted">
                                Total {{ group.categoryName }} (Ks)
                            </td>
                            <td>{{ fM(group.sale_amount) }}</td>
                            <td>{{ fM(group.pur_amount) }}</td>
                            <td class="text-primary">{{ fM(group.profit_amount) }}</td>
                        </tr>
                        <tr class="table-light text-end">
                            <td colspan="6" class="fw-semibold pe-3 text-muted">
                                Total Extra Discount (Ks)
                            </td>
                            <td>{{ fM(group.extra_discount) }}</td>
                        </tr>
                        <tr class="table-light text-end fw-bold">
                            <td colspan="6" class="pe-3 text-muted">
                                Net Profit (Ks)
                            </td>
                            <td class="text-success">{{ fM(group.net_profit) }}</td>
                        </tr>
                    </tbody>
                </template>

                <!-- Flat View (When Group by Category is OFF - default) -->
                <tbody v-else-if="filteredProducts.length > 0">
                    <tr v-for="(p, index) in filteredProducts" :key="p.code_no + '-' + p.item_name + '-' + index">
                        <td class="text-center text-muted small">{{ index + 1 }}</td>
                        <td class="text-center fw-semibold font-monospace">{{ p.code_no }}</td>
                        <td>{{ p.item_name }}</td>
                        <td class="text-center small text-muted">
                            {{ formatPackingSize(p.packing_size) }}
                        </td>
                        <td class="text-end">
                            {{ formatPackingQty(p.packing_size, p.sale_qty) }}
                        </td>
                        <td class="text-end">{{ fM(p.final_amount) }}</td>
                        <td class="text-end">{{ fM(p.pur_amount) }}</td>
                        <td class="text-end" :class="{'text-danger': p.profit < 0}">{{ fM(p.profit) }}</td>
                    </tr>
                </tbody>

                <!-- Totals Footer (Grouped View) -->
                <tbody v-if="groupByCategory && hasCategory && filteredProducts.length > 0" class="table-group-divider">
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="7">Grand Total Sale Amount (Ks)</td>
                        <td class="text-end">{{ fM(total_sale_amount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="7">Grand Total Pur Amount (Ks)</td>
                        <td class="text-end">{{ fM(total_pur_amount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-3" colspan="7">Grand Total Extra Discount (Ks)</td>
                        <td class="text-end">{{ fM(total_extra_discount) }}</td>
                    </tr>
                    <tr class="table-secondary fw-bold">
                        <td class="text-end pe-2" colspan="7">Grand Total Profit (Ks)</td>
                        <td class="text-end text-success fs-6">{{ fM(total_net_profit) }}</td>
                    </tr>
                </tbody>

                <!-- Totals Footer (Flat View) -->
                <tbody v-else-if="filteredProducts.length > 0" class="table-group-divider">
                    <tr class="table-secondary fw-bold text-end">
                        <td colspan="5" class="pe-3">
                            <i class="fa-solid fa-calculator me-2"></i>Total (Ks)
                        </td>
                        <td>{{ fM(total_sale_amount) }}</td>
                        <td>{{ fM(total_pur_amount) }}</td>
                        <td :class="{'text-danger': total_profit_amount < 0}">{{ fM(total_profit_amount) }}</td>
                    </tr>
                    <tr class="table-secondary text-end">
                        <td colspan="7" class="fw-semibold pe-3 text-muted">Total Extra Discount (Ks)</td>
                        <td>{{ fM(total_extra_discount) }}</td>
                    </tr>
                    <tr class="table-secondary text-end fw-bold">
                        <td colspan="7" class="pe-3 text-muted">Net Profit (Ks)</td>
                        <td class="text-success fs-6">{{ fM(total_net_profit) }}</td>
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
    name: "SalesByDescription",
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
            groupByCategory: false, // Default is OFF as requested
            selectedCategories: []
        };
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        processedProducts() {
            let sales = [];
            this.salesData.forEach((res) => {
                if (!Array.isArray(res.products)) return;
                res.products.forEach((data) => {
                    let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                    const categoryName = (data.category?.cat_name || data.category_name || (typeof data.category === 'string' ? data.category : '') || '').trim() || 'No Category';
                    if (index === -1) {
                        sales.push({ ...data, voucher_no: res.voucher_no, extra_discount: Number(res.extra_discount) || 0, category_name: categoryName });
                    } else {
                        sales.push({ ...data, voucher_no: res.voucher_no, extra_discount: 0, category_name: categoryName });
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

            const res1 = Object.entries(hp.gb_item_name_price(sales));
            const res2 = res1.map((res) => {
                const firstItem = res[1][0];
                const item_name = firstItem.item_name || '-';
                const category_name = firstItem.category_name || 'No Category';
                let item = {
                    item_name: String(item_name),
                    category_name: String(category_name),
                    sale_qty: 0,
                    code_no: firstItem.code_no ? String(firstItem.code_no) : "-",
                    profit: 0,
                    extra_discount: 0,
                    packing_size: firstItem.packing_size || {},
                    retail_price: firstItem.retail_price || {},
                    whole_sale_price: firstItem.whole_sale_price || {},
                    pur_price: firstItem.pur_price || {},
                    pur_amount: 0,
                    final_amount: 0,
                };
                res[1].forEach((ren) => {
                    item.packing_size = ren.packing_size;
                    item.retail_price = ren.retail_price;
                    item.whole_sale_price = ren.whole_sale_price;
                    item.pur_price = ren.pur_price;
                    item.final_amount += Number(ren.final_amount) || 0;
                    item.sale_qty += Number(ren.sale_qty) || 0;
                    if (ren.code_no) item.code_no = String(ren.code_no);
                    const purSmallest = Number(ren.pur_price?.smallest_unit) || 0;
                    const purItemAmount = purSmallest * (Number(ren.sale_qty) || 0);
                    item.pur_amount += purItemAmount;
                    item.profit += (Number(ren.final_amount) || 0) - purItemAmount;
                    item.extra_discount += Number(ren.extra_discount) || 0;
                    if (ren.category_name && ren.category_name !== 'No Category') {
                        item.category_name = ren.category_name;
                    }
                });
                return item;
            }).sort((a, b) => (a.item_name || '').localeCompare(b.item_name || ''));

            return res2;
        },
        hasCategory() {
            return this.processedProducts.some(p => p.category_name && p.category_name !== 'No Category');
        },
        allCategories() {
            const set = new Set();
            this.processedProducts.forEach(p => {
                if (p.category_name && p.category_name !== 'No Category') {
                    set.add(p.category_name);
                }
            });
            return Array.from(set).sort((a, b) => a.localeCompare(b));
        },
        filteredProducts() {
            let list = this.processedProducts;

            // Category pills filter
            if (this.hasCategory && this.selectedCategories.length > 0 && this.selectedCategories.length < this.allCategories.length) {
                list = list.filter(p => this.selectedCategories.includes(p.category_name));
            }

            // Search query filter
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
        categoryGroups() {
            const groups = {};
            this.filteredProducts.forEach(p => {
                const cat = p.category_name || 'No Category';
                if (!groups[cat]) {
                    groups[cat] = {
                        categoryName: cat,
                        items: [],
                        sale_amount: 0,
                        pur_amount: 0,
                        profit_amount: 0,
                        extra_discount: 0,
                        net_profit: 0
                    };
                }
                groups[cat].items.push(p);
                groups[cat].sale_amount += Number(p.final_amount) || 0;
                groups[cat].pur_amount += Number(p.pur_amount) || 0;
                groups[cat].profit_amount += Number(p.profit) || 0;
                if (!this.code_no) {
                    groups[cat].extra_discount += Number(p.extra_discount) || 0;
                }
            });

            Object.values(groups).forEach(g => {
                g.net_profit = g.profit_amount - g.extra_discount;
            });

            return Object.values(groups).sort((a, b) => a.categoryName.localeCompare(b.categoryName));
        },
        total_sale_amount() {
            return this.filteredProducts.reduce((sum, p) => sum + (Number(p.final_amount) || 0), 0);
        },
        total_pur_amount() {
            return this.filteredProducts.reduce((sum, p) => sum + (Number(p.pur_amount) || 0), 0);
        },
        total_profit_amount() {
            return this.total_sale_amount - this.total_pur_amount;
        },
        total_extra_discount() {
            if (this.code_no) return 0;
            return this.filteredProducts.reduce((sum, p) => sum + (Number(p.extra_discount) || 0), 0);
        },
        total_net_profit() {
            return this.total_profit_amount - this.total_extra_discount;
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
        displayPrice(l_unit, m_unit, s_unit) {
            return hp.displayPrice(l_unit, m_unit, s_unit);
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
                // When all are selected, click isolates this category
                this.selectedCategories = [cat];
            } else if (this.selectedCategories.includes(cat)) {
                if (this.selectedCategories.length > 1) {
                    this.selectedCategories = this.selectedCategories.filter(c => c !== cat);
                } else {
                    // Clicking the only selected category resets back to All
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
                console.error("Error fetching sales data:", error);
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
