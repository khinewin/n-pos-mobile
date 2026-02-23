<template lang="">
    <div>
        <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm text-primary" data-bs-toggle="modal" data-bs-target="#categoryManagementModal">
                        <i class="fa-solid fa-tags"></i> {{t('category')}}
                    </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm" @click="saveExpense" :disabled="isLoading || expenseItems.length <= 0">
                        <i class="fa-solid fa-hard-drive"></i> {{t('save_link')}}
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <router-link :to="{name: 'Expense'}" id="close" class="btn btn-sm">
                        <i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}}
                    </router-link>
                </div>
            </div>
        </div>

        <div class="container-fluid my-2">
            <div class="row g-1">
                <div class="col-12">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <!-- Details Section -->
                            <div class="row mb-2">
                                <div class="col-12">
                                    <table class="table mb-0 table-borderless">
                                        <tbody>
                                            <tr>
                                                <td class="text-end col-3 fw-bold">{{t('invoice_no')}} :</td>
                                                <td class="col-3">
                                                    <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control-plaintext border-bottom py-0" placeholder="Expense #">
                                                </td>
                                                <td class="text-end col-3 fw-bold">{{t('date')}} :</td>
                                                <td class="col-3">
                                                    <flat-pickr class="form-control-plaintext py-0 border-bottom" v-model="expense_date" :config="config" />
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <!-- List Table -->
                            <div class="vh-60 overflow-y-scroll dataContainer">
                                <table class="table table-bordered table-hover">
                                    <tbody class="small fw-bold table-group-divider position-sticky top-0 z-3">
                                        <!-- Inline Entry Row -->
                                        <tr class="text-center">
                                            <td class="col-1" colspan="2">{{t('number')}}</td>
                                            <td class="col-3">
                                                <select v-model="newItem.category" class="form-select form-select-sm border-0 bg-transparent py-0 text-center fw-bold">
                                                    <option value="" disabled selected>Category</option>
                                                    <option v-for="c in categories" :key="c.id" :value="c">{{c.name}}</option>
                                                </select>
                                            </td>
                                            <td class="col-5">
                                                <input
                                                    type="text"
                                                    v-model="newItem.description"
                                                    @keypress.enter="focusAmount"
                                                    class="form-control-plaintext py-0 fw-bold"
                                                    placeholder="Description">
                                            </td>
                                            <td class="col-2">
                                                <input
                                                    type="number"
                                                    id="amountInput"
                                                    v-model="newItem.amount"
                                                    @keypress.enter="addItem"
                                                    class="form-control-plaintext py-0 text-center fw-bold"
                                                    placeholder="Amount">
                                            </td>
                                            <td class="text-center" style="width: 50px;">
                                                <a href="#!" @click="addItem" class="text-primary">
                                                    <span class="d-block"><i class="fa-solid fa-plus"></i></span>
                                                </a>
                                            </td>
                                        </tr>
                                    </tbody>
                                    <tbody class="text-nowrap text-break table-group-divider">
                                        <tr v-if="expenseItems.length > 0" v-for="(item, i) in expenseItems.filter(f => !f.is_remove)" :key="i">
                                            <td class="text-center">
                                                <a href="#!" @click="removeItem(item)" class="text-danger">
                                                    <i class="fa-solid fa-square-minus"></i>
                                                </a>
                                            </td>
                                            <td class="text-center">{{i+1}}</td>
                                            <td class="text-center text-primary">
                                                <select v-model="item.category_id" @change="updateCategory(item)" class="form-select form-select-sm border-0 bg-transparent py-0 text-center fw-bold">
                                                    <option v-for="c in categories" :key="c.id" :value="c.id">{{c.name}}</option>
                                                </select>
                                            </td>
                                            <td>
                                                <input @keypress.enter="calTotal" type="text" class="form-control-plaintext py-0" v-model="item.description">
                                            </td>
                                            <td class="text-end fw-bold">
                                                <input @change="calTotal" @keypress.enter="calTotal" type="number" class="form-control-plaintext text-end py-0" v-model="item.amount">
                                            </td>
                                            <td class="text-center">
                                                <a href="#!" class="text-primary">
                                                    <i class="fa-solid fa-check"></i>
                                                </a>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Footer Summary -->
                            <div class="mt-3 border-top pt-2 fixed-bottom">
                                <table class="table table-borderless table-hover mb-0">
                                    <tbody>
                                        <tr>
                                            <td class="text-end pe-2 fw-bold">{{t('notes')}}</td>
                                            <td class="text-end" colspan="3">
                                                <textarea name="remark" v-model="remark" @keypress.enter="saveExpense" class="form-control py-0"></textarea>
                                            </td>
                                            <td class="text-end pe-2 fw-bold fs-5 col-3">{{t('total')}} :</td>
                                            <td class="text-start col-3 fw-bold fs-5 ps-3">{{fM(total_amount)}}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <CategoryModalOld @setMsg="setMsg" />

</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import { Timestamp, collection, doc, updateDoc, onSnapshot, query, orderBy, where } from 'firebase/firestore'
import db from '@/firebase/firestore'
import CategoryModalOld from "@/views/modals/expenses/CategoryModalOld.vue"
import * as hp from "@/lib/helper"
import FlatPickr from 'vue-flatpickr-component'
import { lang } from "@/lib/language"
import * as hc from "@/lib/handleCtrl"

export default {
    components: {
        PageHeader,
        CategoryModalOld,
        FlatPickr
    },
    setup() {
        return { v$: useVuelidate() }
    },
    data() {
        return {
            isLoading: false,
            id: "",
            voucher_no: this.$route.params.id,
            expense_date: "",
            expenseItems: [],
            newItem: {
                category: "",
                description: "",
                amount: ""
            },
            categories: [],
            total_amount: 0,
            remark: "",
            isOffline: false,
            msg: {
                type: "",
                info: ""
            }
        }
    },
    validations() {
        return {
            voucher_no: { required },
            expense_date: { required }
        }
    },
    mounted() {
        this.fetchExpense();
        this.fetchCategories();
        window.addEventListener('keydown', this.handleCtrl);
    },
    beforeUnmount() {
        if (this.catUnsub) this.catUnsub();
        window.removeEventListener('keydown', this.handleCtrl);
    },
    watch: {
        msg: {
            handler(val) {
                if (val.type) {
                    setTimeout(() => {
                        this.msg.type = "";
                        this.msg.info = "";
                    }, 3000)
                }
            }, deep: true
        }
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        config() {
            return this.$store.getters.flat_pickr_config;
        },
        pageTitle() {
            const sl = this.$store.getters.currentLang;
            return sl == "mm" ? "အသုံးစရိတ် / ပြင်ဆင်" : "Expense / Edit";
        },
        t() {
            return (key) => this.$store.getters.t(key);
        }
    },
    methods: {
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault();
                this.saveExpense();
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault();
                const close = document.getElementById("close");
                close.click();
            }
            return hc.handleCtrl(event);
        },
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        setMsg(info, type) {
            this.msg.info = info;
            this.msg.type = type;
        },
        async fetchExpense() {
            const q = query(collection(db, "expenses"), where("voucher_no", "==", this.voucher_no));
            this.expenseUnsub = onSnapshot(q, (snapshot) => {
                if (snapshot.empty) return;
                const docSnap = snapshot.docs[0];
                const data = { id: docSnap.id, ...docSnap.data() };
                this.id = data.id;
                this.expense_date = hp.setOldDate(data.date);
                this.expenseItems = data.items || [];
                this.total_amount = data.total_amount || 0;
                this.remark = data.remark || "";
            });
        },
        fetchCategories() {
            const q = query(collection(db, "expense_categories"), orderBy("name"));
            this.catUnsub = onSnapshot(q, (snapshot) => {
                this.categories = snapshot.docs.map(doc => ({
                    id: doc.id,
                    ...doc.data()
                }));
            });
        },
        focusAmount() {
            document.getElementById('amountInput').focus();
        },
        addItem() {
            if (!this.newItem.category || !this.newItem.amount) return;
            this.expenseItems.push({
                category_id: this.newItem.category.id,
                category_name: this.newItem.category.name,
                description: this.newItem.description,
                amount: parseFloat(this.newItem.amount)
            });
            this.newItem.description = "";
            this.newItem.amount = "";
            this.calTotal();
        },
        removeItem(item) {
            this.expenseItems = this.expenseItems.map(f => {
                if (f === item) {
                    return { ...f, is_remove: true };
                }
                return f;
            });
            this.calTotal();
        },
        updateCategory(item) {
            const cat = this.categories.find(c => c.id === item.category_id);
            if (cat) {
                item.category_name = cat.name;
            }
        },
        calTotal() {
            this.total_amount = this.expenseItems
                .filter(f => !f.is_remove)
                .reduce((acc, item) => acc + parseFloat(item.amount || 0), 0);
        },
        async saveExpense() {
            const valid = await this.v$.$validate();
            if (!valid) return;

            const activeItems = this.expenseItems.filter(f => !f.is_remove);
            if (activeItems.length === 0) return;

            this.isLoading = true;
            try {
                const expense_date = hp.insertDate(this.expense_date);
                const data = {
                    voucher_no: this.voucher_no,
                    date: Timestamp.fromDate(expense_date),
                    items: activeItems,
                    total_amount: this.total_amount,
                    remark: this.remark,
                    edit_user: { uid: this.user.uid, displayName: this.user.displayName || this.user.email },
                    updated_at: Timestamp.fromDate(new Date()),
                };

                await updateDoc(doc(db, "expenses", this.id), data);
                this.setMsg("Expense updated successfully", "success");

                setTimeout(() => {
                    this.$router.push({ name: "Expense" });
                }, 1500);

            } catch (error) {
                console.error("Error updating expense:", error);
                this.setMsg("Error updating expense", "error");
            } finally {
                this.isLoading = false;
            }
        }
    }
}
</script>

<style scoped>
    table td, table th {
        padding: 5px 5px !important;
    }
</style>
