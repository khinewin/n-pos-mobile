<template>
    <div>           
        <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <!-- Action Bar -->
            <div class="row mb-2 justify-content-end">   
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm text-primary" data-bs-toggle="modal" data-bs-target="#categoryManagementModal">
                        <i class="fa-solid fa-tags"></i> {{t('category')}}
                    </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#expenseModal">
                        <i class="fa-solid fa-folder-tree"></i> {{t('open_link')}}
                    </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button class="btn btn-sm" @click="goEdit" :disabled="expenseMode == 'Expense / New'">
                        <i class="fa-solid fa-pen-to-square"></i> {{t('edit_link')}}
                    </button>
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button 
                        @click="saveExpense" 
                        class="btn btn-sm" 
                        :disabled="isLoading || expenseItems.length <= 0 || expenseMode == 'Expense / History'">
                        <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> {{t('save_link')}} (ctrl+s)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>                      
                </div>
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button @click="clearForm" class="btn btn-sm" :disabled="expenseItems.length <= 0">
                        <i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}} (ctrl+q)
                    </button>
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
                                <!-- Removed Payee Column -->
                            </div>       


                            <!-- List Table (Inline Entry) -->
                            <div class="vh-60 overflow-y-scroll dataContainer">
                                <table class="table table-bordered table-hover">
                                    <tbody class="small fw-bold table-group-divider position-sticky top-0 z-3">
                                        <!-- Inline Entry Row (hidden in History mode) -->
                                        <tr class="text-center" v-if="expenseMode == 'Expense / New'">                                         
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
                                                    class="form-control-plaintext py-0  fw-bold" 
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
                                        <!-- Header row in History mode -->
                                        <tr class="text-center table-secondary" v-if="expenseMode == 'Expense / History'">
                                            <td colspan="2">{{t('number')}}</td>
                                            <td>{{t('category')}}</td>
                                            <td>{{t('description')}}</td>
                                            <td>{{t('amount')}}</td>
                                            <td></td>
                                        </tr>
                                    </tbody>                                  
                                    <tbody class="text-nowrap text-break table-group-divider">                                                                         
                                        <tr v-if="expenseItems.length > 0" v-for="(item, i) in expenseItems" :key="i">
                                            <td class="text-center">
                                                <a href="#!" @click="expenseMode == 'Expense / New' && removeItem(i)" :class="expenseMode == 'Expense / History' ? 'text-muted pe-none' : 'text-danger'">
                                                    <i class="fa-solid fa-square-minus"></i>
                                                </a>
                                            </td>
                                            <td class="text-center">{{i+1}}</td>
                                            <td class="text-center text-primary">
                                                <select v-model="item.category_id" @change="updateCategory(item)" :disabled="expenseMode == 'Expense / History'" class="form-select form-select-sm border-0 bg-transparent py-0 text-center fw-bold">
                                                    <option v-for="c in categories" :key="c.id" :value="c.id">{{c.name}}</option>
                                                </select>
                                            </td>   
                                            <td>
                                                <input @keypress.enter="saveEdit" type="text" :disabled="expenseMode == 'Expense / History'" class="form-control-plaintext py-0" v-model="item.description">
                                            </td>                                            
                                            <td class="text-end fw-bold">
                                                <input @change="calTotal" @keypress.enter="saveEdit" type="number" :disabled="expenseMode == 'Expense / History'" class="form-control-plaintext text-end py-0" v-model="item.amount">
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
        
        <CategoryModalOld @setMsg="setMsg" />
        <ExpenseModalOld @setExpense="setExpense" />

    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import { Timestamp, collection, addDoc, onSnapshot, query, orderBy } from 'firebase/firestore'
import db from '@/firebase/firestore'
import CategoryModalOld from "@/views/modals/expenses/CategoryModalOld.vue"
import ExpenseModalOld from "@/views/modals/expenses/ExpenseModalOld.vue"
import * as hp from "@/lib/helper"
import FlatPickr from 'vue-flatpickr-component'
import { lang } from "@/lib/language"
import * as hc from "@/lib/handleCtrl"

export default {
    components: {
        PageHeader, 
        CategoryModalOld,
        ExpenseModalOld,
        FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data() {
        return {
            isLoading: false,
            expenseMode: "Expense / New",
            voucher_no: "",
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
        this.voucher_no = hp.setVoucherNo();
        this.expense_date = hp.setToday();
        this.fetchCategories();
        window.addEventListener('keydown', this.handleCtrl);
    },
    unmounted() {
        if (this.catUnsub) {
            this.catUnsub();
        }
        window.removeEventListener('keydown', this.handleCtrl);
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
            if (this.expenseMode == "Expense / New") {
                return sl == "mm" ? "အသုံးစရိတ် / အသစ်" : "Expense / New";
            } else {
                return sl == "mm" ? "အသုံးစရိတ် / မှတ်တမ်း" : "Expense / History";
            }
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
                this.clearForm(); 
            }
            return hc.handleCtrl(event)          
        },
        fM(amount) {
            return hp.formattedAmount(amount)
        },
        setMsg(info, type) {
            this.msg.info = info;
            this.msg.type = type;
            setTimeout(() => {
                this.msg.type = "";
                this.msg.info = "";
            }, 3000);
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
            
            // Reset input
            this.newItem.description = "";
            this.newItem.amount = "";
            
            // Keep category selected for faster entry or reset?
            // this.newItem.category = ""; 
            
            this.calTotal();
            // Refocus description for rapid entry
            // document.querySelector('input[placeholder="Description"]').focus();
        },
        removeItem(index) {
            this.expenseItems.splice(index, 1);
            this.calTotal();
        },
        updateCategory(item) {
            const cat = this.categories.find(c => c.id === item.category_id);
            if (cat) {
                item.category_name = cat.name;
            }
        },
        saveEdit() {
            this.calTotal();
        },
        calTotal() {
            this.total_amount = this.expenseItems.reduce((acc, item) => acc + parseFloat(item.amount || 0), 0);
        },
        goEdit() {
            if (this.expenseMode == "Expense / New") return;
            this.$router.push({ name: "EditExpense", params: { id: this.voucher_no } });
        },
        setExpense(e) {
            this.expenseMode = "Expense / History";
            this.voucher_no = e.voucher_no;
            this.expense_date = hp.setOldDate(e.date)
            this.expenseItems = e.items || [];
            this.total_amount = e.total_amount || 0;
            this.remark = e.remark || "";
        },
        clearForm() {
            this.expenseMode = "Expense / New";
            this.expenseItems = [];
            this.total_amount = 0;
            this.remark = "";
            this.newItem = { category: "", description: "", amount: "" };
            this.voucher_no = hp.setVoucherNo();
            this.expense_date = hp.setToday();
            this.v$.$reset();
        },
        async saveExpense() {
            this.v$.$validate();
            if (this.v$.$error) return;
            if (this.expenseItems.length === 0) return;

            this.isLoading = true;
            try {
                const save_time = Timestamp.fromDate(new Date());
                const expense_date = hp.insertDate(this.expense_date);
                const data = {
                    voucher_no: this.voucher_no,
                    date: Timestamp.fromDate(expense_date),
                    items: this.expenseItems,
                    total_amount: this.total_amount,
                    remark: this.remark,
                    user: { uid: this.user.uid, displayName: this.user.displayName || this.user.email },
                    created_at: save_time,
                    type: "expense"
                };

                await addDoc(collection(db, "expenses"), data);
                
                this.setMsg("Expense saved successfully", "success");
                this.clearForm();

            } catch (error) {
                console.error("Error saving expense:", error);
                this.setMsg("Error saving expense", "error");
            } finally {
                this.isLoading = false;
            }
        }
    }
}
</script>

<style scoped>
    table td, table th{
        padding: 5px 5px !important;
    }
</style>
