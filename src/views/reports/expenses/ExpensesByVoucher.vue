<template lang="">
    <div class="container-fluid">
        <!-- Loading -->
        <div v-if="isLoading" class="d-flex justify-content-center my-5">
            <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>

        <!-- No data -->
        <div v-else-if="reports.length === 0" class="text-center text-muted py-5">
            No expense records found for the selected period.
        </div>

        <!-- One card per expense voucher -->
        <div v-else class="row" v-for="expense in reports" :key="expense.id">
            <div class="col-12 mb-2">
                <div class="card">
                    <div class="card-body p-2">

                        <!-- Voucher header info -->
                        <div class="row justify-content-between">
                            <div class="col-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">
                                        <tr>
                                            <td class="col-4 pb-0 fw-bold text-end">Invoice No. :</td>
                                            <td class="col-8 ps-2 pb-0">{{ expense.voucher_no }}</td>
                                        </tr>
                                        <tr>
                                            <td class="pb-0 fw-bold text-end">Date :</td>
                                            <td class="ps-2 pb-0">{{ showTime(expense.date) }}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div class="col-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">
                                        <tr>
                                            <td class="col-4 pb-0 fw-bold text-end">Recorded by :</td>
                                            <td class="col-8 ps-2 pb-0">{{ expense.user && expense.user.displayName }}</td>
                                        </tr>
                                        <tr>
                                            <td class="pb-0 fw-bold text-end">Remark :</td>
                                            <td class="ps-2 pb-0">{{ expense.remark }}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Items table -->
                        <div class="overflow-x-auto">
                            <table class="table table-bordered table-hover mt-2 mb-0">
                                <tbody class="table-group-divider">
                                    <tr class="text-center fw-bold">
                                        <td class="col-1">No.</td>
                                        <td class="col-2">Category</td>
                                        <td class="col-5">Description</td>
                                        <td class="col-2 text-end">Amount (Ks)</td>
                                    </tr>
                                    <tr v-for="(item, i) in expense.items" :key="i">
                                        <td class="text-center">{{ i + 1 }}</td>
                                        <td>{{ item.category_name }}</td>
                                        <td>{{ item.description }}</td>
                                        <td class="text-end">{{ fM(item.amount) }}</td>
                                    </tr>
                                </tbody>
                            </table>

                            <!-- Totals footer -->
                            <table class="table table-bordered mb-0">
                                <tbody class="table-group-divider">
                                    <tr>
                                        <td class="text-end fw-bold pe-2 col-9">Total Amount (Ks)</td>
                                        <td class="text-end col-3">{{ fM(expense.total_amount) }}</td>
                                    </tr>
                                </tbody>
                            </table>
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

export default {
    props: ['fromDate', 'toDate'],
    data() {
        return {
            reports: [],
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
    methods: {
        fM(amount) {
            return hp.formattedAmount(amount);
        },
        showTime(t) {
            return hp.showTimeHour(t);
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
                this.reports = snapShot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
                this.isLoading = false;
            });
        }
    }
}
</script>
<style lang="">
</style>
