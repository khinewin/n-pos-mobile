<template>
    <div class="stock-list">
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
                            id="groupByCatSwitchStockList" 
                            v-model="groupByCategory"
                        >
                        <label class="form-check-label small fw-semibold" for="groupByCatSwitchStockList">
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
            <table class="table table-bordered border-dark table-hover mb-0 inside-table align-middle">
                <!-- Table Header -->
                <thead class="table-light fw-bold text-center align-middle">
                    <!-- Header when Group by Category is ON -->
                    <tr v-if="groupByCategory && hasCategory" class="change-cursor">
                        <th class="col-0 text-center">Category</th>
                        <th class="col-1 text-center" @click="sortProducts('code_no')">Code</th>
                        <th class="col-4 text-start" @click="sortProducts('item_name')">Product Name</th>
                        <th class="col-2 text-center">Packing Size</th>
                        <th class="col-1 text-end" @click="sortProducts('qty')" v-if="location == 'All' || location == 'Mart'">Mart Inv Bal</th>
                        <th class="col-1 text-end" v-if="pur_price" @click="sortProducts('pur_price')">Pur Price</th>
                        <th class="col-1 text-end" v-if="sale_price" @click="sortProducts('retail_price')">Sale Price</th>
                        <th class="col-1 text-end" v-if="pur_price" @click="sortProducts('total_pur_price')">Total Pur</th>
                        <th class="col-1 text-end" v-if="sale_price" @click="sortProducts('total_sale_price')">Total Sale</th>
                    </tr>
                    <!-- Header when Group by Category is OFF (default) -->
                    <tr v-else class="change-cursor">
                        <th class="col-0 text-center">No.</th>
                        <th class="col-1 text-center" @click="sortProducts('code_no')">Code</th>
                        <th class="col-4 text-start" @click="sortProducts('item_name')">Product Name</th>
                        <th class="col-2 text-center">Packing Size</th>
                        <th class="col-1 text-end" @click="sortProducts('qty')" v-if="location == 'All' || location == 'Mart'">Mart Inv Bal</th>
                        <th class="col-1 text-end" v-if="pur_price" @click="sortProducts('pur_price')">Pur Price</th>
                        <th class="col-1 text-end" v-if="sale_price" @click="sortProducts('retail_price')">Sale Price</th>
                        <th class="col-1 text-end" v-if="pur_price" @click="sortProducts('total_pur_price')">Total Pur</th>
                        <th class="col-1 text-end" v-if="sale_price" @click="sortProducts('total_sale_price')">Total Sale</th>
                    </tr>
                </thead>

                <!-- Empty State -->
                <tbody v-if="!isLoading && filteredProducts.length === 0">
                    <tr>
                        <td :colspan="tableColspan" class="text-center text-muted py-5">
                            <i class="fa-regular fa-folder-open fa-2x d-block mb-2 opacity-50"></i>
                            No product data available.
                        </td>
                    </tr>
                </tbody>

                <!-- Grouped by Category View (When Switch ON) -->
                <template v-if="groupByCategory && hasCategory && filteredProducts.length > 0">
                    <tbody v-for="g in categoryGroups" :key="g.catName" class="table-group-divider">
                        <tr v-for="(p, index) in g.items" :key="p.id || (p.code_no + '-' + index)">
                            <td 
                                v-if="index === 0" 
                                :rowspan="g.items.length + (pur_price || sale_price ? 1 : 0)" 
                                class="col-2 text-center fw-bold align-top bg-light text-primary"
                            >
                                {{ g.catName }}
                            </td>
                            <td class="col-1 text-center font-monospace">{{ p.code_no }}</td>
                            <td class="col-2 text-start">{{ p.item_name }}</td>
                            <td class="col-1 text-center small text-muted">
                                {{ calPackingSize(p.packing_size?.largest_unit, p.packing_size?.medium_unit, p.packing_size?.smallest_unit, p.packing_size?.medium_qty, p.packing_size?.smallest_qty) }}
                            </td>
                            <td class="col-1 text-end" v-if="location == 'All' || location == 'Mart'">
                                {{ calPackingQty(p.packing_size?.medium_qty, p.packing_size?.smallest_qty, p.qty, p.packing_size?.largest_unit, p.packing_size?.medium_unit, p.packing_size?.smallest_unit) }}
                            </td>
                            <td class="col-1 text-end" v-if="pur_price">
                                {{ displayPrice(p.pur_price?.largest_unit, p.pur_price?.medium_unit, p.pur_price?.smallest_unit) }}
                            </td>
                            <td class="col-1 text-end" v-if="sale_price">
                                {{ displayPrice(p.retail_price?.largest_unit, p.retail_price?.medium_unit, p.retail_price?.smallest_unit) }}
                            </td>
                            <td class="col-1 text-end" v-if="pur_price">
                                {{ fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.pur_price?.smallest_unit || 0)) }}
                            </td>
                            <td class="col-1 text-end" v-if="sale_price">
                                {{ fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.retail_price?.smallest_unit || 0)) }}
                            </td>
                        </tr>
                        <!-- Category Subtotal Row -->
                        <tr v-if="pur_price || sale_price" class="table-light fw-bold text-end">
                            <td :colspan="3 + (location === 'All' || location === 'Mart' ? 1 : 0) + (pur_price ? 1 : 0) + (sale_price ? 1 : 0)" class="pe-3 text-muted">
                                Subtotal ({{ g.catName }})
                            </td>
                            <td v-if="pur_price" class="text-primary">{{ fM(g.pur_amount) }}</td>
                            <td v-if="sale_price" class="text-primary">{{ fM(g.sale_amount) }}</td>
                        </tr>
                    </tbody>
                </template>

                <!-- Flat View (When Group by Category is OFF - default) -->
                <tbody v-else-if="filteredProducts.length > 0" class="table-group-divider">
                    <tr v-for="(p, i) in filteredProducts" :key="p.id || (p.code_no + '-' + i)">
                        <td class="col-0 text-center text-muted small">{{ i + 1 }}</td>
                        <td class="col-1 text-center font-monospace">{{ p.code_no }}</td>
                        <td class="col-2 text-start">{{ p.item_name }}</td>
                        <td class="col-1 text-center small text-muted">
                            {{ calPackingSize(p.packing_size?.largest_unit, p.packing_size?.medium_unit, p.packing_size?.smallest_unit, p.packing_size?.medium_qty, p.packing_size?.smallest_qty) }}
                        </td>
                        <td class="col-1 text-end" v-if="location == 'All' || location == 'Mart'">
                            {{ calPackingQty(p.packing_size?.medium_qty, p.packing_size?.smallest_qty, p.qty, p.packing_size?.largest_unit, p.packing_size?.medium_unit, p.packing_size?.smallest_unit) }}
                        </td>
                        <td class="col-1 text-end" v-if="pur_price">
                            {{ displayPrice(p.pur_price?.largest_unit, p.pur_price?.medium_unit, p.pur_price?.smallest_unit) }}
                        </td>
                        <td class="col-1 text-end" v-if="sale_price">
                            {{ displayPrice(p.retail_price?.largest_unit, p.retail_price?.medium_unit, p.retail_price?.smallest_unit) }}
                        </td>
                        <td class="col-1 text-end" v-if="pur_price">
                            {{ fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.pur_price?.smallest_unit || 0)) }}
                        </td>
                        <td class="col-1 text-end" v-if="sale_price">
                            {{ fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.retail_price?.smallest_unit || 0)) }}
                        </td>
                    </tr>
                </tbody>

                <!-- Grand Totals Footer -->
                <tbody v-if="filteredProducts.length > 0 && (pur_price || sale_price)" class="table-group-divider">
                    <tr class="table-secondary fw-bold text-end">
                        <td :colspan="4 + (location === 'All' || location === 'Mart' ? 1 : 0) + (pur_price ? 1 : 0) + (sale_price ? 1 : 0)" class="pe-3">
                            Grand Total (Ks)
                        </td>
                        <td v-if="pur_price" class="text-end text-success fs-6">{{ fM(total_pur_amount) }}</td>
                        <td v-if="sale_price" class="text-end text-success fs-6">{{ fM(total_sale_amount) }}</td>
                    </tr>
                </tbody>
            </table>

            <!-- Load More Pagination Button (Hidden on Print) -->
            <div class="d-grid mt-2 d-print-none" v-if="reports.length > 0 && !isLoading && !this.code_no">
                <button class="btn btn-primary" :disabled="isEndPagination" @click="loadMore = true; fetchProducts()">
                    (Loaded : {{ reports.length }}) Load More
                </button>
            </div>

            <!-- Loading Spinner -->
            <div v-if="isLoading" class="d-flex justify-content-center my-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Loading...</span>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { collection, query, limit, startAfter, where, getDocs, orderBy } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from '@/lib/helper'
import { toRaw } from 'vue'

export default {
    name: "StockList",
    props: {
        sale_price: {
            type: Boolean,
            default: false
        },
        pur_price: {
            type: Boolean,
            default: false
        },
        location: {
            type: String,
            default: 'All'
        },
        code_no: {
            type: [String, Number],
            default: null
        },
        gt_equal: {
            type: [String, Number],
            default: null
        },
        lt_equal: {
            type: [String, Number],
            default: null
        },
        packing_unit: {
            type: String,
            default: null
        }
    },
    data() {
        return {
            isLoading: false,
            reports: [],
            sortBy: "asc",
            sort_item: "",
            perPage: 500,
            loadMore: false,
            lastDoc: null,
            isEndPagination: false,
            groupByCategory: false,
            selectedCategories: [],
            searchQuery: ''
        };
    },
    mounted() {
        this.fetchProducts();
    },
    watch: {
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
        },
        code_no() {
            this.reports = [];
            this.lastDoc = null;
            this.isEndPagination = false;
            this.fetchProducts();
        }
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        allCategories() {
            const set = new Set();
            this.reports.forEach(p => {
                const cat = (p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || '').trim();
                if (cat && cat !== 'No Category') {
                    set.add(cat);
                }
            });
            return Array.from(set).sort((a, b) => a.localeCompare(b));
        },
        hasCategory() {
            return this.allCategories.length > 0;
        },
        tableColspan() {
            let cols = 4;
            if (this.location === 'All' || this.location === 'Mart') cols++;
            if (this.pur_price) cols += 2;
            if (this.sale_price) cols += 2;
            return cols;
        },
        filteredProducts() {
            let list = this.reports;

            // Apply gt_equal / lt_equal if defined
            if (this.gt_equal && this.lt_equal) {
                if (this.location === "Main") {
                    list = list.filter(f => Number(f.main_qty || 0) >= Number(this.gt_equal) && Number(f.main_qty || 0) <= Number(this.lt_equal));
                } else if (this.location === "Mart") {
                    list = list.filter(f => Number(f.qty || 0) >= Number(this.gt_equal) && Number(f.qty || 0) <= Number(this.lt_equal));
                } else {
                    list = list.filter(f => (Number(f.main_qty || 0) >= Number(this.gt_equal) && Number(f.main_qty || 0) <= Number(this.lt_equal)) ||
                                            (Number(f.qty || 0) >= Number(this.gt_equal) && Number(f.qty || 0) <= Number(this.lt_equal)));
                }
            } else if (this.gt_equal) {
                if (this.location === "Main") {
                    list = list.filter(f => Number(f.main_qty || 0) >= Number(this.gt_equal));
                } else if (this.location === "Mart") {
                    list = list.filter(f => Number(f.qty || 0) >= Number(this.gt_equal));
                } else {
                    list = list.filter(f => (Number(f.main_qty || 0) >= Number(this.gt_equal)) || (Number(f.qty || 0) >= Number(this.gt_equal)));
                }
            } else if (this.lt_equal) {
                if (this.location === "Main") {
                    list = list.filter(f => Number(f.main_qty || 0) <= Number(this.lt_equal));
                } else if (this.location === "Mart") {
                    list = list.filter(f => Number(f.qty || 0) <= Number(this.lt_equal));
                } else {
                    list = list.filter(f => (Number(f.main_qty || 0) <= Number(this.lt_equal)) || (Number(f.qty || 0) <= Number(this.lt_equal)));
                }
            }

            // Category filter pills
            if (this.hasCategory && this.selectedCategories.length > 0 && this.selectedCategories.length < this.allCategories.length) {
                list = list.filter(p => {
                    const cat = (p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || '').trim() || 'No Category';
                    return this.selectedCategories.includes(cat);
                });
            }

            // Search query filter
            if (this.searchQuery) {
                const q = this.searchQuery.toLowerCase().trim();
                list = list.filter(p => {
                    const cat = (p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || '').trim();
                    return (p.item_name && String(p.item_name).toLowerCase().includes(q)) ||
                           (p.code_no && String(p.code_no).toLowerCase().includes(q)) ||
                           (cat && cat.toLowerCase().includes(q));
                });
            }

            return list;
        },
        categoryGroups() {
            if (!this.hasCategory) return [];

            const groups = {};
            this.filteredProducts.forEach(p => {
                const cat = (p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || '').trim() || 'No Category';
                if (!groups[cat]) {
                    groups[cat] = [];
                }
                groups[cat].push(p);
            });

            return Object.entries(groups).map(([catName, items]) => {
                const pur_amount = items.reduce((total, p) => {
                    const qty = this.location === 'Main' ? (Number(p.main_qty) || 0) : (Number(p.qty) || 0);
                    return total + (qty * (Number(p.pur_price?.smallest_unit) || 0));
                }, 0);
                const sale_amount = items.reduce((total, p) => {
                    const qty = this.location === 'Main' ? (Number(p.main_qty) || 0) : (Number(p.qty) || 0);
                    return total + (qty * (Number(p.retail_price?.smallest_unit) || 0));
                }, 0);
                return { catName, items, pur_amount, sale_amount };
            }).sort((a, b) => a.catName.toUpperCase().localeCompare(b.catName.toUpperCase()));
        },
        total_pur_amount() {
            return this.filteredProducts.reduce((total, p) => {
                const qty = this.location === 'Main' ? (Number(p.main_qty) || 0) : (Number(p.qty) || 0);
                return total + (qty * (Number(p.pur_price?.smallest_unit) || 0));
            }, 0);
        },
        total_sale_amount() {
            return this.filteredProducts.reduce((total, p) => {
                const qty = this.location === 'Main' ? (Number(p.main_qty) || 0) : (Number(p.qty) || 0);
                return total + (qty * (Number(p.retail_price?.smallest_unit) || 0));
            }, 0);
        }
    },
    methods: {
        displayPrice(l_unit, m_unit, s_unit) {
            return hp.displayPrice(l_unit, m_unit, s_unit);
        },
        fM(amount) {
            return hp.formattedAmount(amount || 0);
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty) {
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit) {
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
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
        async fetchProducts() {
            if (this.isLoading) return;

            this.isLoading = true;

            let queryConstraints = [];
            queryConstraints.push(orderBy("packing_size.smallest_qty", "desc"));
            queryConstraints.push(limit(this.perPage));

            if (this.code_no) {
                queryConstraints = [
                    where("code_no", "==", this.code_no)
                ];
            }

            if (this.loadMore && this.lastDoc && !this.code_no) {
                const lastDoc = toRaw(this.lastDoc);
                queryConstraints.push(startAfter(lastDoc));
            }

            const queryRef = query(collection(db, "products"), ...queryConstraints);

            try {
                const snapshot = await getDocs(queryRef);

                if (!snapshot.empty) {
                    const newProducts = snapshot.docs.map(doc => ({
                        id: doc.id,
                        ...doc.data()
                    }));

                    if (!this.code_no) {
                        this.lastDoc = snapshot.docs[snapshot.docs.length - 1];
                    }

                    this.reports = this.loadMore && !this.code_no
                        ? [...this.reports, ...newProducts]
                        : newProducts;

                    if (newProducts.length < this.perPage) {
                        this.isEndPagination = true;
                    }
                } else {
                    this.isEndPagination = true;
                }
            } catch (error) {
                console.error("Failed to fetch products:", error);
            } finally {
                this.isLoading = false;
                this.loadMore = false;
            }
        },
        sortProducts(sort) {
            this.sort_item = sort;
            const isAsc = this.sortBy === "asc";
            this.sortBy = isAsc ? "desc" : "asc";

            this.reports = [...this.reports].sort((a, b) => {
                const item_a = String(a.item_name || '').toUpperCase();
                const item_b = String(b.item_name || '').toUpperCase();
                const qty_a = Number(this.location === 'Main' ? a.main_qty : a.qty) || 0;
                const qty_b = Number(this.location === 'Main' ? b.main_qty : b.qty) || 0;
                const pur_a = Number(a.pur_price?.smallest_unit) || 0;
                const pur_b = Number(b.pur_price?.smallest_unit) || 0;
                const sale_a = Number(a.retail_price?.smallest_unit) || 0;
                const sale_b = Number(b.retail_price?.smallest_unit) || 0;

                let diff = 0;
                switch (sort) {
                    case "qty":
                        diff = qty_a - qty_b;
                        break;
                    case "code_no":
                        diff = String(a.code_no || '').localeCompare(String(b.code_no || ''), undefined, { numeric: true });
                        break;
                    case "item_name":
                        diff = item_a.localeCompare(item_b);
                        break;
                    case "pur_price":
                        diff = pur_a - pur_b;
                        break;
                    case "retail_price":
                        diff = sale_a - sale_b;
                        break;
                    case "total_pur_price":
                        diff = (qty_a * pur_a) - (qty_b * pur_b);
                        break;
                    case "total_sale_price":
                        diff = (qty_a * sale_a) - (qty_b * sale_b);
                        break;
                    default:
                        diff = 0;
                }
                return isAsc ? -diff : diff;
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
.change-cursor {
    cursor: pointer;
}
@media print {
    .inside-table {
        font-size: 10px !important;
    }
}
</style>