<template>
    <div class="sales-by-voucher container-fluid px-0">
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
                            id="groupByCatSwitchVoucher" 
                            v-model="groupByCategory"
                        >
                        <label class="form-check-label small fw-semibold" for="groupByCatSwitchVoucher">
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
                            placeholder="Filter by voucher, customer, code, name..."
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

        <!-- Loading Spinner -->
        <div v-if="isLoading" class="d-flex justify-content-center my-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="filteredVouchers.length === 0" class="text-center text-muted py-5">
            <i class="fa-regular fa-folder-open fa-2x d-block mb-2 opacity-50"></i>
            No voucher data available for the selected period.
        </div>

        <!-- Voucher Cards List -->
        <div v-else class="row g-3">
            <div class="col-12 mb-2" v-for="sale in filteredVouchers" :key="sale.id || sale.voucher_no">
                <div class="card shadow-sm border">
                    <div class="card-body p-2">
                        <!-- Voucher Header Information -->
                        <div class="row justify-content-between g-2 mb-2">   
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless inside-table">
                                    <tbody>  
                                        <tr>
                                            <td class="col-4 pb-1 fw-bold text-end text-muted">Voucher No. :</td>
                                            <td class="col-8 ps-2 pb-1 fw-bold font-monospace text-primary">
                                                {{ sale.voucher_no }}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="pb-1 fw-bold text-end text-muted">Date :</td>
                                            <td class="ps-2 pb-1">
                                                <span>{{ showTime(sale.sale_date) }}</span>
                                            </td>
                                        </tr>       
                                    </tbody>
                                </table>
                            </div>        
                            <div class="col-sm-6">                    
                                <table class="table mb-0 table-borderless inside-table">
                                    <tbody>                                     
                                        <tr>
                                            <td class="col-4 pb-1 fw-bold text-end text-muted">Customer :</td>
                                            <td class="ps-2 col-8 pb-1 fw-semibold">
                                                {{ sale.customer?.name || '-' }}
                                            </td>
                                        </tr>      
                                        <tr>
                                            <td class="pb-1 fw-bold text-end text-muted">Cashier :</td>
                                            <td class="ps-2 pb-1">
                                                {{ sale.user?.displayName || '-' }}
                                            </td>
                                        </tr>                                               
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Products Inside Voucher Table -->
                        <div class="overflow-x-auto">
                            <!-- Grouped by Category View inside Voucher -->
                            <table 
                                v-if="groupByCategory && hasCategory" 
                                class="table table-bordered table-hover mt-1 mb-2 inside-table align-middle"
                            >
                                <thead class="table-light fw-bold text-center">
                                    <tr>
                                        <th class="col-1">Category</th>
                                        <th style="width: 45px;">No.</th>
                                        <th class="text-start">Product Name</th>
                                        <th class="col-pack text-center" style="width: 120px;">Packing Size</th>                                                                                    
                                        <th class="col-qty text-center" style="width: 80px;">Qty</th>
                                        <th class="text-center" style="width: 120px;">Unit price</th>
                                        <th class="text-end" style="width: 110px;">Amount</th>
                                        <th class="text-end" style="width: 90px;">Discount</th>
                                        <th class="text-end" style="width: 110px;">Total Amt</th>
                                    </tr>
                                </thead>
                                <tbody v-for="cat in sale.categoryGroups" :key="cat.categoryName" class="table-group-divider">
                                    <tr v-for="(s, i) in cat.items" :key="s.id || (s.code_no + '-' + i)">
                                        <td 
                                            v-if="i === 0" 
                                            class="text-center fw-semibold align-top bg-light text-primary" 
                                            :rowspan="cat.items.length + 1"
                                        >
                                            {{ cat.categoryName }}
                                        </td>
                                        <td class="text-center text-muted small">{{ i + 1 }}</td>                   
                                        <td>
                                            <span v-if="s.code_no" class="badge bg-light text-secondary border me-1 font-monospace">{{ s.code_no }}</span>
                                            {{ s.item_name }}
                                        </td>
                                        <td class="text-center small text-muted">
                                            {{ formatPackingSize(s.packing_size) }}
                                        </td>                                        
                                        <td class="text-center fw-semibold">{{ s.sale_qty }}</td>                                            
                                        <td class="text-center small">
                                            {{ s.qty_sale_price }}' {{ s.qty_sale_price_unit }}
                                        </td>                                         
                                        <td class="text-end">{{ fM(s.amount) }}</td>
                                        <td class="text-end">{{ s.discount ? fM(s.discount) : 0 }}</td>
                                        <td class="text-end fw-semibold">{{ fM(s.final_amount) }}</td>
                                    </tr>
                                    <!-- Category Subtotal Row -->
                                    <tr class="table-light text-end fw-semibold">
                                        <td colspan="5" class="pe-3 text-muted">
                                            Subtotal {{ cat.categoryName }} (Ks)
                                        </td>
                                        <td>{{ fM(cat.totalAmount) }}</td>
                                        <td>{{ fM(cat.totalDiscount) }}</td>
                                        <td class="text-primary fw-bold">{{ fM(cat.totalFinalAmount) }}</td>
                                    </tr>
                                </tbody>
                            </table>

                            <!-- Standard Product List inside Voucher (When Group by Category is OFF) -->
                            <table 
                                v-else 
                                class="table table-bordered table-hover mt-1 mb-2 inside-table align-middle"
                            >
                                <thead class="table-light fw-bold text-center">
                                    <tr>
                                        <th style="width: 45px;">No.</th>
                                        <th class="text-start">Product Name</th>
                                        <th class="col-pack text-center" style="width: 120px;">Packing Size</th>                                                                                    
                                        <th class="col-qty text-center" style="width: 80px;">Qty</th>
                                        <th class="text-center" style="width: 120px;">Unit price</th>
                                        <th class="text-end" style="width: 110px;">Amount</th>
                                        <th class="text-end" style="width: 90px;">Discount</th>
                                        <th class="text-end" style="width: 110px;">Total Amt</th>
                                    </tr>
                                </thead>
                                <tbody class="table-group-divider">
                                    <tr v-for="(s, i) in sale.filteredProducts" :key="s.id || (s.code_no + '-' + i)">     
                                        <td class="text-center text-muted small">{{ i + 1 }}</td>                   
                                        <td>
                                            <span v-if="s.code_no" class="badge bg-light text-secondary border me-1 font-monospace">{{ s.code_no }}</span>
                                            {{ s.item_name }}
                                        </td>
                                        <td class="text-center small text-muted">
                                            {{ formatPackingSize(s.packing_size) }}
                                        </td>                                        
                                        <td class="text-center fw-semibold">{{ s.sale_qty }}</td>                                            
                                        <td class="text-center small">
                                            {{ s.qty_sale_price }}' {{ s.qty_sale_price_unit }}
                                        </td>                                         
                                        <td class="text-end">{{ fM(s.amount) }}</td>
                                        <td class="text-end">{{ s.discount ? fM(s.discount) : 0 }}</td>
                                        <td class="text-end fw-semibold">{{ fM(s.final_amount) }}</td>
                                    </tr>                        
                                </tbody>                                    
                            </table>  

                            <!-- Voucher Financial Totals Breakdown -->
                            <table class="table table-bordered table-hover mb-0 inside-table">
                                <tbody class="table-group-divider">
                                    <!-- Filter notice row if products were filtered -->
                                    <tr v-if="sale.filteredProducts.length !== (sale.products || []).length" class="table-warning">
                                        <td class="text-end pe-2 col-3 fw-bold">Filtered Items Total (Ks)</td>
                                        <td class="text-end col-3 fw-bold text-primary">{{ fM(sale.filteredFinalAmount) }}</td>
                                        <td class="text-end pe-2 col-3 fw-bold">Filtered Items Qty</td>
                                        <td class="text-end col-3 fw-bold">{{ sale.filteredQty }}</td>
                                    </tr>
                                    <tr>
                                        <td class="text-end pe-2 col-3 fw-bold">Total Amount (Ks)</td>
                                        <td class="text-end col-3">{{ fM(sale.total_amount) }}</td>
                                        <td class="text-end pe-2 col-3 fw-bold">Extra Discount (Ks)</td>
                                        <td class="text-end col-3">{{ fM(sale.extra_discount) }}</td>
                                    </tr>                     
                                    <tr>
                                        <td class="text-end col-3 pe-2 fw-bold">Net Amount (Ks)</td>
                                        <td class="text-end col-3 fw-bold">{{ fM(sale.net_amount) }}</td>
                                        <td class="text-end pe-2 fw-bold">Paid (Ks)</td>
                                        <td class="text-end text-success fw-bold">{{ fM(calculatePaid(sale)) }}</td>                                            
                                    </tr>
                                    <tr>
                                        <td class="text-end pe-2 fw-bold">Change (Ks)</td>
                                        <td class="text-end">{{ fM(sale.change) }}</td>        
                                        <td class="text-end pe-2 fw-bold">Balance (Ks)</td>
                                        <td class="text-end" :class="{'text-danger fw-bold': Number(sale.credit) > 0}">{{ fM(sale.credit) }}</td>
                                    </tr>    
                                    <tr v-if="sale.remark">
                                        <td class="text-end fw-bold pe-2">Remark</td>
                                        <td colspan="3">{{ sale.remark }}</td>
                                    </tr>
                                </tbody>
                            </table>                           
                        </div>
                    </div>
                </div>
            </div>

            <!-- Grand Totals Summary Across Filtered Vouchers -->
            <div class="col-12 mt-2">
                <div class="card bg-light border shadow-sm">
                    <div class="card-body p-3">
                        <div class="row text-center align-items-center">
                            <div class="col-6 col-md-3 border-end">
                                <div class="small text-muted fw-semibold">Vouchers Count</div>
                                <div class="fs-5 fw-bold text-dark">{{ filteredVouchers.length }}</div>
                            </div>
                            <div class="col-6 col-md-3 border-end">
                                <div class="small text-muted fw-semibold">Grand Total Amount</div>
                                <div class="fs-5 fw-bold text-dark">{{ fM(grandTotalAmount) }} Ks</div>
                            </div>
                            <div class="col-6 col-md-3 border-end">
                                <div class="small text-muted fw-semibold">Grand Total Paid</div>
                                <div class="fs-5 fw-bold text-success">{{ fM(grandTotalPaid) }} Ks</div>
                            </div>
                            <div class="col-6 col-md-3">
                                <div class="small text-muted fw-semibold">Grand Total Balance</div>
                                <div class="fs-5 fw-bold" :class="{'text-danger': grandTotalBalance > 0, 'text-dark': grandTotalBalance <= 0}">{{ fM(grandTotalBalance) }} Ks</div>
                            </div>
                        </div>
                    </div>
                </div>
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
    name: "SalesByVoucher",
    props: {
        fromDate: {
            type: String,
            required: true
        },
        toDate: {
            type: String,
            required: true
        },
        voucher_no: {
            type: String,
            default: ''
        },
        customer_code: {
            type: [String, Number],
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
        report_type() {
            return this.$store.getters.reportType;
        },
        allCategories() {
            const set = new Set();
            this.salesData.forEach(sale => {
                if (Array.isArray(sale.products)) {
                    sale.products.forEach(p => {
                        const cat = (p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || '').trim();
                        if (cat && cat !== 'No Category') {
                            set.add(cat);
                        }
                    });
                }
            });
            return Array.from(set).sort((a, b) => a.localeCompare(b));
        },
        hasCategory() {
            return this.allCategories.length > 0;
        },
        filteredVouchers() {
            const q = this.searchQuery.toLowerCase().trim();
            const isCategoryFiltered = this.hasCategory && this.selectedCategories.length > 0 && this.selectedCategories.length < this.allCategories.length;

            return this.salesData.map(sale => {
                const rawProducts = Array.isArray(sale.products) ? sale.products : [];
                let products = rawProducts.map(p => {
                    const cat = (p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || '').trim() || 'No Category';
                    return {
                        ...p,
                        category_name: cat
                    };
                });

                // Filter products by selected categories
                if (isCategoryFiltered) {
                    products = products.filter(p => this.selectedCategories.includes(p.category_name));
                }

                // Filter products by search query
                if (q) {
                    const voucherMatches = 
                        (sale.voucher_no && String(sale.voucher_no).toLowerCase().includes(q)) ||
                        (sale.customer?.name && String(sale.customer.name).toLowerCase().includes(q)) ||
                        (sale.user?.displayName && String(sale.user.displayName).toLowerCase().includes(q)) ||
                        (sale.remark && String(sale.remark).toLowerCase().includes(q));

                    if (!voucherMatches) {
                        products = products.filter(p => 
                            (p.item_name && String(p.item_name).toLowerCase().includes(q)) ||
                            (p.code_no && String(p.code_no).toLowerCase().includes(q)) ||
                            (p.category_name && String(p.category_name).toLowerCase().includes(q))
                        );
                    }
                }

                if (products.length === 0) {
                    return null;
                }

                // Group by category if needed
                const catGroups = {};
                products.forEach(p => {
                    const cat = p.category_name || 'No Category';
                    if (!catGroups[cat]) {
                        catGroups[cat] = {
                            categoryName: cat,
                            items: [],
                            totalAmount: 0,
                            totalDiscount: 0,
                            totalFinalAmount: 0
                        };
                    }
                    catGroups[cat].items.push(p);
                    catGroups[cat].totalAmount += Number(p.amount) || 0;
                    catGroups[cat].totalDiscount += Number(p.discount) || 0;
                    catGroups[cat].totalFinalAmount += Number(p.final_amount) || 0;
                });
                const categoryGroups = Object.values(catGroups).sort((a, b) => a.categoryName.localeCompare(b.categoryName));

                const filteredFinalAmount = products.reduce((acc, p) => acc + (Number(p.final_amount) || 0), 0);
                const filteredQty = products.reduce((acc, p) => acc + (Number(p.sale_qty) || 0), 0);

                return {
                    ...sale,
                    filteredProducts: products,
                    categoryGroups,
                    filteredFinalAmount,
                    filteredQty
                };
            }).filter(Boolean);
        },
        grandTotalAmount() {
            return this.filteredVouchers.reduce((acc, v) => acc + (Number(v.total_amount) || 0), 0);
        },
        grandTotalPaid() {
            return this.filteredVouchers.reduce((acc, v) => acc + this.calculatePaid(v), 0);
        },
        grandTotalBalance() {
            return this.filteredVouchers.reduce((acc, v) => acc + (Number(v.credit) || 0), 0);
        }
    },
    watch: {
        fromDate() { this.fetchSales(); },
        toDate() { this.fetchSales(); },
        voucher_no() { this.fetchSales(); },
        customer_code() { this.fetchSales(); },
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
        showTime(t) {
            return hp.showTimeHour(t);
        },
        calculatePaid(sale) {
            const paid = Number(sale.paid) || 0;
            const debtPaid = (sale.debt_payments && sale.debt_payments.length) 
                ? sale.debt_payments.reduce((accu, cur) => accu + (Number(cur.paid) || 0), 0) 
                : 0;
            return paid + debtPaid;
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

            if (this.voucher_no && this.voucher_no.length > 2) {
                query_url = query(
                    collection(db, "sales"),
                    where("voucher_no", "==", this.voucher_no)
                );
            } else if (this.customer_code) {
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
                console.error("Error fetching sales by voucher:", error);
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
