<template lang="">
    <div class="overflow-x-auto">
        <div v-if="isLoading" class="d-flex justify-content-center my-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>

        <table v-else class="table table-bordered mb-0 border-dark table-hover ">
            <!-- Header -->
            <tbody class="fw-bold text-center">
                <tr>
                    <td  class="col-1">Date</td>
                    <td class="col-2">Category</td>
                    <td class="col-3">Description</td>
                    <td class="col-1 text-center">Amount (Ks)</td>
                </tr>
            </tbody>

            <!-- No data state -->
            <tbody v-if="reports.length === 0">
                <tr>
                    <td colspan="4" class="text-center text-muted py-4">No expense records found for the selected period.</td>
                </tr>
            </tbody>

            <!-- Grouped by Date -->
            <tbody
                v-if="reports.length > 0"
                v-for="group in reports"
                :key="group.date"
                class="table-group-divider"
            >
                <!-- Date cell spanning all rows in this group -->
                <tr>
                    <td class="text-center fw-bold" :rowspan="group.items.length + 2">
                        {{ group.date }}
                    </td>
                </tr>

                <!-- Expense item rows -->
                <tr v-for="(item, i) in group.items" :key="i">
                    <td class="col-2">{{ item.category_name }}</td>
                    <td class="col-3 text-start">{{ item.description }}</td>
                    <td class="col-1 text-end">{{ fM(item.amount) }}</td>
                </tr>

                <!-- Daily total row -->
                <tr class="text-end table-light">
                    <td colspan="2" class="fw-bold border-bottom pe-2">{{group.date}} / Total (Ks)</td>
                    <td class="fw-bold">{{ fM(group.total) }}</td>
                </tr>
            </tbody>

            <!-- Grand Total -->
            <tbody v-if="reports.length > 0" class="table-group-divider">
                <tr>
                    <td class="text-end fw-bold pe-2" colspan="3">Grand Total (Ks)</td>
                    <td class="text-end fw-bold">{{ fM(grand_total) }}</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>
<script>
import { Timestamp, onSnapshot, collection, where, orderBy, query } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"

export default {
    props: ['fromDate', 'toDate'],
    data() {
        return {
            reports: [],
            expensesData: [],
            isLoading: false,
            unsub: null,
        }
    },
    mounted() {
        this.isLoading = true;
        this.fetchExpenses();
    },
    beforeUnmount() {
        if (this.unsub) this.unsub();
    },
    computed: {
        grand_total() {
            return this.reports.reduce((sum, group) => sum + group.total, 0);
        }
    },
    methods: {
        fM(amount) {
            return hp.formattedAmount(amount);
        },
        showDate(dateStr) {
            // dateStr is stored as "DD/MM/YYYY" from Expense.vue
            return dateStr;
        },
        displayReports() {
            // Flatten all expense items with their parent date
            let flatItems = [];
            this.expensesData.forEach((expense) => {
                const dateLabel =hp.showTime(expense.date);
                expense.items.forEach((item) => {
                    flatItems.push({
                        ...item,
                        group_date: dateLabel,
                    });
                });
            });

            // Group by date using gb_exp_date helper
            const grouped = hp.gb_date(flatItems);

            // Build sorted array of groups (most recent first)
            const groupArray = Object.entries(grouped).map(([date, items]) => {
                const total = items.reduce((sum, item) => sum + Number(item.amount || 0), 0);
                return { date, items, total };
            });

            // Sort by date descending (parse DD/MM/YYYY for comparison)
            groupArray.sort((a, b) => {
                const parseDate = (d) => {
                    const parts = d.split('/');
                    if (parts.length === 3) {
                        return new Date(`${parts[2]}-${parts[1]}-${parts[0]}`);
                    }
                    return new Date(d);
                };
                return parseDate(b.date) - parseDate(a.date);
            });

            this.reports = groupArray;
        },
        async fetchExpenses() {
            let fDate = new Date(this.fromDate);
            fDate.setHours(0, 0, 0, 0);
            const from_date = Timestamp.fromDate(fDate);

            let tDate = new Date(this.toDate);
            tDate.setHours(23, 59, 59, 999);
            const to_date = Timestamp.fromDate(tDate);

            const query_url = query(
                collection(db, "expenses"),
                where("date", ">=", from_date),
                where("date", "<=", to_date),
                orderBy("date", "desc")
            );

            this.unsub = onSnapshot(query_url, (snapShot) => {
                this.expensesData = snapShot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
                this.displayReports();
                this.isLoading = false;
            });
        }
    }
}
</script>
<style lang="">
</style>
