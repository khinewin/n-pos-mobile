<template>
    <div class="sales-by-description-month">
        <!-- Controls Toolbar (Hidden on Print) -->
        <div class="d-print-none mb-2 p-1 bg-light rounded-3 border shadow-sm">
            <div class="row g-2 align-items-center justify-content-between">
                <!-- Left: Group by Category & Month Filter Pills -->
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

                    <!-- Months Filter right after Group by Category -->
                    <div class="d-flex flex-wrap align-items-center gap-1 border-start ps-3" v-if="allMonths.length > 1">
                        <span class="small text-muted me-1 fw-semibold"><i class="fa-regular fa-calendar-check me-1"></i>Months:</span>
                        <button 
                            type="button" 
                            class="btn btn-xs py-0 px-2 rounded-pill me-1" 
                            :class="selectedMonthKeys.length === allMonths.length ? 'btn-dark' : 'btn-outline-secondary'"
                            @click="selectAllMonths"
                        >
                            All
                        </button>
                        <button 
                            v-for="m in allMonths" 
                            :key="m.key"
                            type="button" 
                            class="btn btn-xs py-0 px-2 rounded-pill"
                            :class="selectedMonthKeys.includes(m.key) ? 'btn-primary' : 'btn-outline-secondary'"
                            @click="toggleMonth(m.key)"
                        >
                            {{ m.label }}
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
                    <tr v-if="groupByCategory && hasCategory">
                        <th class="col-1 align-top">Category</th>
                        <th class="col-code" style="width: 100px;">Code</th>
                        <th class="text-start">Product Name</th>
                        <th class="col-pack text-center" style="width: 120px;">Packing Size</th>
                        
                        <!-- Month Columns (after Category, Code, Name, Packing) -->
                        <th 
                            v-for="m in displayedMonths" 
                            :key="m.key" 
                            class="text-center month-col"
                            style="min-width: 95px;"
                        >
                            {{ m.label }}
                        </th>

                        <!-- Total Column -->
                        <th class="text-end bg-light-subtle" style="min-width: 110px;">
                            Total
                        </th>
                    </tr>

                    <!-- Flat Header (when Group by Category is OFF - default) -->
                    <tr v-else>
                        <th class="col-index" style="width: 40px;">#</th>
                        <th class="col-code" style="width: 100px;">Code</th>
                        <th class="text-start">Product Name</th>
                        <th class="col-pack text-center" style="width: 120px;">Packing Size</th>
                        
                        <!-- Month Columns -->
                        <th 
                            v-for="m in displayedMonths" 
                            :key="m.key" 
                            class="text-center month-col"
                            style="min-width: 95px;"
                        >
                            {{ m.label }}
                        </th>

                        <!-- Total Column -->
                        <th class="text-end bg-light-subtle" style="min-width: 110px;">
                            Total
                        </th>
                    </tr>
                </thead>

                <!-- Empty State -->
                <tbody v-if="!isLoading && filteredProducts.length === 0">
                    <tr>
                        <td :colspan="tableColspan" class="text-center text-muted py-5">
                            <i class="fa-regular fa-folder-open fa-2x d-block mb-2 opacity-50"></i>
                            No sales data available for the selected period.
                        </td>
                    </tr>
                </tbody>

                <!-- Grouped by Category View (When ON) -->
                <template v-if="groupByCategory && hasCategory && categoryGroups.length > 0">
                    <tbody v-for="group in categoryGroups" :key="group.categoryName" class="table-group-divider">
                        <tr v-for="(p, pIndex) in group.items" :key="p.code_no + '-' + pIndex">
                            <!-- Category cell with rowspan on the first product row -->
                            <td 
                                v-if="pIndex === 0" 
                                class="text-center fw-bold align-top bg-light-subtle text-primary py-2" 
                                :rowspan="group.items.length + 1"
                            >
                                {{ group.categoryName }}
                            </td>

                            <td class="text-center fw-semibold font-monospace">{{ p.code_no }}</td>
                            <td>{{ p.item_name }}</td>
                            <td class="text-center small text-muted">
                                {{ formatPackingSize(p.packing_size) }}
                            </td>

                            <!-- Monthly Amount Cells -->
                            <td 
                                v-for="m in displayedMonths" 
                                :key="m.key" 
                                class="text-end"
                                :class="{'text-muted opacity-50': !p.monthlySales[m.key]}"
                            >
                                <template v-if="p.monthlySales[m.key]">
                                    {{ fM(p.monthlySales[m.key]) }}
                                </template>
                                <template v-else>-</template>
                            </td>

                            <!-- Row Total Amount -->
                            <td class="text-end fw-bold bg-light-subtle">
                                {{ fM(p.total_amount) }}
                            </td>
                        </tr>

                        <!-- Category Subtotal Row -->
                        <tr class="table-light fw-bold">
                            <td colspan="3" class="text-end pe-3 text-muted">
                                Total {{ group.categoryName }}
                            </td>
                            <td v-for="m in displayedMonths" :key="m.key" class="text-end">
                                {{ fM(getCategoryMonthTotal(group, m.key)) }}
                            </td>
                            <td class="text-end text-primary">
                                {{ fM(getCategoryGrandTotal(group)) }}
                            </td>
                        </tr>
                    </tbody>
                </template>

                <!-- Flat View (When Group by Category is OFF - default) -->
                <tbody v-else-if="filteredProducts.length > 0">
                    <tr v-for="(p, index) in filteredProducts" :key="p.code_no + '-' + index">
                        <td class="text-center text-muted small">{{ index + 1 }}</td>
                        <td class="text-center fw-semibold font-monospace">{{ p.code_no }}</td>
                        <td>{{ p.item_name }}</td>
                        <td class="text-center small text-muted">
                            {{ formatPackingSize(p.packing_size) }}
                        </td>

                        <!-- Monthly Amount Cells -->
                        <td 
                            v-for="m in displayedMonths" 
                            :key="m.key" 
                            class="text-end"
                            :class="{'text-muted opacity-50': !p.monthlySales[m.key]}"
                        >
                            <template v-if="p.monthlySales[m.key]">
                                {{ fM(p.monthlySales[m.key]) }}
                            </template>
                            <template v-else>-</template>
                        </td>

                        <!-- Row Total Amount -->
                        <td class="text-end fw-bold bg-light-subtle">
                            {{ fM(p.total_amount) }}
                        </td>
                    </tr>
                </tbody>

                <!-- Grand Totals Footer -->
                <tbody v-if="filteredProducts.length > 0" class="table-group-divider">
                    <tr class="table-secondary fw-bold">
                        <td colspan="4" class="text-end pe-3">
                            <i class="fa-solid fa-calculator me-2"></i>Grand Total
                        </td>
                        <td v-for="m in displayedMonths" :key="m.key" class="text-end">
                            {{ fM(getMonthGrandTotal(m.key)) }}
                        </td>
                        <td class="text-end text-success fs-6">
                            {{ fM(overallTotalAmount) }}
                        </td>
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
import { Timestamp, collection, query, where, orderBy, onSnapshot } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/sales"

export default {
    name: "SalesByDescriptionMonth",
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
            selectedMonthKeys: [],
        };
    },
    computed: {
        allMonths() {
            if (!this.fromDate || !this.toDate) return [];
            const fDate = new Date(this.fromDate);
            const tDate = new Date(this.toDate);
            if (isNaN(fDate.getTime()) || isNaN(tDate.getTime())) return [];

            const startYear = fDate.getFullYear();
            const startMonth = fDate.getMonth();
            const endYear = tDate.getFullYear();
            const endMonth = tDate.getMonth();

            const isSameYear = startYear === endYear;
            const months = [];
            let current = new Date(startYear, startMonth, 1);
            const end = new Date(endYear, endMonth, 1);

            while (current <= end) {
                const year = current.getFullYear();
                const monthIndex = current.getMonth();
                const key = `${year}-${String(monthIndex + 1).padStart(2, '0')}`;
                const shortName = current.toLocaleString('en-US', { month: 'short' });
                const label = isSameYear ? shortName : `${shortName} '${String(year).slice(-2)}`;
                const fullLabel = `${shortName} ${year}`;
                months.push({ key, label, fullLabel, year, monthIndex });
                current.setMonth(current.getMonth() + 1);
            }
            return months;
        },
        displayedMonths() {
            if (this.selectedMonthKeys.length === 0) return this.allMonths;
            return this.allMonths.filter(m => this.selectedMonthKeys.includes(m.key));
        },
        tableColspan() {
            return 4 + this.displayedMonths.length + 1;
        },
        processedProducts() {
            const productMap = {};

            this.salesData.forEach(sale => {
                const saleDate = sale.sale_date?.toDate ? sale.sale_date.toDate() : new Date(sale.sale_date);
                if (isNaN(saleDate.getTime())) return;
                const monthKey = `${saleDate.getFullYear()}-${String(saleDate.getMonth() + 1).padStart(2, '0')}`;

                if (!Array.isArray(sale.products)) return;

                sale.products.forEach(p => {
                    if (this.code_no && p.code_no !== this.code_no) return;

                    const prodId = p.code_no || p.item_name;
                    if (!prodId) return;

                    const categoryName = p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || 'No Category';

                    if (!productMap[prodId]) {
                        productMap[prodId] = {
                            code_no: p.code_no || '-',
                            item_name: p.item_name || '-',
                            category_name: categoryName,
                            packing_size: p.packing_size || {},
                            monthlySales: {},
                            total_amount: 0,
                        };
                    }

                    if (!productMap[prodId].monthlySales[monthKey]) {
                        productMap[prodId].monthlySales[monthKey] = 0;
                    }

                    const finalAmount = Number(p.final_amount) || 0;

                    productMap[prodId].monthlySales[monthKey] += finalAmount;
                    productMap[prodId].total_amount += finalAmount;
                });
            });

            return Object.values(productMap).sort((a, b) => a.item_name.localeCompare(b.item_name));
        },
        hasCategory() {
            return this.processedProducts.some(p => p.category_name && p.category_name !== 'No Category');
        },
        filteredProducts() {
            let list = this.processedProducts;
            if (this.searchQuery) {
                const q = this.searchQuery.toLowerCase().trim();
                list = list.filter(p => 
                    (p.item_name && p.item_name.toLowerCase().includes(q)) || 
                    (p.code_no && p.code_no.toLowerCase().includes(q)) ||
                    (p.category_name && p.category_name.toLowerCase().includes(q))
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
                        items: []
                    };
                }
                groups[cat].items.push(p);
            });

            return Object.values(groups).sort((a, b) => a.categoryName.localeCompare(b.categoryName));
        },
        overallTotalAmount() {
            return this.filteredProducts.reduce((sum, p) => sum + (p.total_amount || 0), 0);
        }
    },
    watch: {
        allMonths: {
            immediate: true,
            handler(months) {
                if (months && months.length > 0 && this.selectedMonthKeys.length === 0) {
                    this.selectedMonthKeys = months.map(m => m.key);
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
        formatPackingSize(ps) {
            if (!ps) return '-';
            return hp.calPackingSize(ps.largest_unit, ps.medium_unit, ps.smallest_unit, ps.medium_qty, ps.smallest_qty);
        },
        selectAllMonths() {
            this.selectedMonthKeys = this.allMonths.map(m => m.key);
        },
        toggleMonth(key) {
            if (this.selectedMonthKeys.includes(key)) {
                if (this.selectedMonthKeys.length > 1) {
                    this.selectedMonthKeys = this.selectedMonthKeys.filter(k => k !== key);
                }
            } else {
                this.selectedMonthKeys.push(key);
            }
        },
        getMonthGrandTotal(monthKey) {
            return this.filteredProducts.reduce((sum, p) => {
                return sum + (Number(p.monthlySales[monthKey]) || 0);
            }, 0);
        },
        getCategoryMonthTotal(group, monthKey) {
            return group.items.reduce((sum, p) => {
                return sum + (Number(p.monthlySales[monthKey]) || 0);
            }, 0);
        },
        getCategoryGrandTotal(group) {
            return group.items.reduce((sum, p) => sum + (Number(p.total_amount) || 0), 0);
        },
        async fetchSales() {
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
