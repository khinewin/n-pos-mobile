<template>
    <div>
        <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info" />
        
        <div class="container-fluid">
            <!-- Action Bar -->
             <div class="row mb-3 justify-content-end align-items-center">
                 <div class="col-auto d-flex gap-2">
                     <button class="btn btn-outline-primary fw-medium border-0 bg-white shadow-sm" data-bs-toggle="modal" data-bs-target="#categoryManagementModal">
                        <i class="fa-solid fa-tags me-2"></i>{{t('category') || 'Category'}}
                      </button>
                 </div>
                 <div class="col-auto d-flex justify-content-end gap-2">
                     <button 
                         class="btn btn-primary shadow fw-bold px-4" 
                         :disabled="expenseItems.length === 0 || isLoading"
                         @click="saveExpense">
                         <span v-if="!isLoading">
                             <i class="fa-solid fa-floppy-disk me-2"></i>{{t('save')}} (ctrl+s)
                         </span>
                         <span class="spinner-border spinner-border-sm" v-else></span>
                     </button>
                     <button @click="clearForm" class="btn btn-light shadow-sm bg-white" :disabled="expenseItems.length === 0">
                         <i class="fa-solid fa-circle-xmark me-2 text-danger"></i>{{t('close') || 'Clear'}} (ctrl+q)
                     </button>
                 </div>
             </div>
        
            <div class="row g-3">
                <div class="col-12">
                    <div class="card border-0 shadow-sm rounded-3 bg-white" style="min-height: 80vh;">
                        <div class="card-body p-0 d-flex flex-column h-100">
                            
                            <!-- Header Details (Invoice & Date) -->
                            <div class="p-3 border-bottom bg-light bg-opacity-10">
                                <div class="row g-3 align-items-center">
                                    <div class="col-md-3">
                                        <div class="form-floating">
                                            <input type="text" :class="{'is-invalid' : v$.voucher_no.$errors.length > 0}" v-model="voucher_no" class="form-control border-0 shadow-none fw-bold text-primary bg-transparent" id="voucherInput" placeholder="Expense #">
                                            <label for="voucherInput" class="text-muted text-uppercase fw-bold small opacity-75">{{t('ref_no') || 'Reference No'}}</label>
                                        </div>
                                    </div>
                                    <div class="col-md-3">
                                        <div class="form-floating">
                                            <flat-pickr class="form-control border-0 shadow-none fw-bold text-primary bg-transparent" v-model="expense_date" :config="config" id="dateInput" placeholder="Select Date" />
                                            <label for="dateInput" class="text-muted text-uppercase fw-bold small opacity-75">{{t('date')}}</label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Data Table with Inline Entry -->
                            <div class="flex-grow-1 overflow-auto custom-scrollbar" style="max-height: 60vh;">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="sticky-top z-2 bg-white text-nowrap shadow-sm">
                                        <tr class="table-light small text-uppercase fw-bold text-muted">
                                            <th class="py-3 ps-4 border-bottom-0" style="width: 60px;">#</th>
                                            <th class="py-3 border-bottom-0" style="min-width: 200px;">{{t('category')}}</th>
                                            <th class="py-3 border-bottom-0" style="min-width: 300px;">{{t('description')}}</th>
                                            <th class="py-3 border-bottom-0 text-end pe-4" style="min-width: 150px;">{{t('amount')}}</th>
                                            <th class="py-3 border-bottom-0 text-center" style="width: 60px;"></th>
                                        </tr>
                                    </thead>
                                    <tbody class="border-top-0">
                                        <!-- Inline Entry Row (Top of list) -->
                                        <tr class="bg-primary bg-opacity-10 border-bottom border-primary border-opacity-25">
                                            <td class="ps-4 fw-bold text-primary opacity-50"><i class="fa-solid fa-plus"></i></td>
                                            <td class="p-2">
                                                <select v-model="newItem.category" class="form-select border-0 bg-transparent shadow-none fw-bold text-primary py-2">
                                                    <option value="" disabled selected>Select Category</option>
                                                    <option v-for="c in categories" :key="c.id" :value="c">{{c.name}}</option>
                                                </select>
                                            </td>
                                            <td class="p-2">
                                                <input 
                                                    type="text" 
                                                    v-model="newItem.description" 
                                                    @keypress.enter="focusAmount"
                                                    class="form-control border-0 bg-transparent shadow-none fw-medium py-2" 
                                                    placeholder="Enter description...">
                                            </td>
                                            <td class="p-2 text-end">
                                                <input 
                                                    type="number" 
                                                    id="amountInput"
                                                    v-model="newItem.amount" 
                                                    @keypress.enter="addItem"
                                                    class="form-control border-0 bg-transparent shadow-none fw-bold text-end text-primary py-2" 
                                                    placeholder="0">
                                            </td>
                                            <td class="text-center">
                                                <button 
                                                    class="btn btn-sm btn-primary rounded-circle shadow-sm" 
                                                    style="width: 32px; height: 32px;"
                                                    @click="addItem"
                                                    :disabled="!newItem.category || !newItem.amount">
                                                    <i class="fa-solid fa-check small"></i>
                                                </button>
                                            </td>
                                        </tr>

                                        <!-- List Items -->
                                        <tr v-for="(item, i) in expenseItems" :key="i">
                                            <td class="ps-4 text-muted fw-bold border-0">{{i+1}}</td>
                                            <td class="border-0 p-2">
                                                <select v-model="item.category_id" @change="updateCategory(item)" class="form-select border-0 bg-transparent shadow-none fw-bold text-dark py-2">
                                                      <option v-for="c in categories" :key="c.id" :value="c.id">{{c.name}}</option>
                                                </select>
                                            </td>
                                            <td class="border-0 p-2">
                                                 <input type="text" class="form-control border-0 bg-transparent shadow-none fw-medium py-2" v-model="item.description" placeholder="Description" @keypress.enter="saveEdit" @change="saveEdit">
                                            </td>
                                            <td class="border-0 p-2 text-end">
                                                <input type="number" class="form-control border-0 bg-transparent shadow-none text-end fw-bold text-dark py-2" v-model="item.amount" @keypress.enter="saveEdit" @change="calTotal">
                                            </td>
                                            <td class="text-center border-0">
                                                <button class="btn btn-link text-danger p-0 opacity-50 hover-opacity-100" @click="removeItem(i)">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </button>
                                            </td>
                                        </tr>
                                        
                                        <!-- Empty State (Only if no items) -->
                                        <tr v-if="expenseItems.length === 0">
                                            <td colspan="5" class="py-5 text-center text-muted border-0">
                                                <p class="mb-0 fw-medium small opacity-75">Added items will appear here</p>
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Footer Summary -->
                            <div class="mt-auto border-top bg-light bg-opacity-25 p-4">
                                <div class="row align-items-center g-3">
                                    <div class="col-md-7">
                                        <div class="form-floating">
                                            <textarea v-model="remark" class="form-control border-0 shadow-sm bg-white rounded-3" placeholder="Notes" id="remarkArea" style="height: 70px; resize: none;"></textarea>
                                            <label for="remarkArea" class="text-muted small">{{t('notes')}}</label>
                                        </div>
                                    </div>
                                    <div class="col-md-5">
                                        <div class="card border-0 bg-primary bg-opacity-10 rounded-3">
                                            <div class="card-body p-3 d-flex justify-content-between align-items-center">
                                                 <span class="text-primary text-uppercase small fw-bold">{{t('total')}}</span>
                                                 <span class="fs-2 fw-bolder text-primary lh-1">{{fM(total_amount)}}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <CategoryModal @setMsg="setMsg" />
    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import { Timestamp, collection, addDoc, onSnapshot, query, orderBy } from 'firebase/firestore'
import db from '@/firebase/firestore'
import CategoryModal from "@/views/modals/expenses/CategoryModal.vue"
import CategoryModalOld from "@/views/modals/expenses/CategoryModalOld.vue"
import * as hp from "@/lib/helper"
import * as hc from "@/lib/handleCtrl"
import FlatPickr from 'vue-flatpickr-component'
import { lang } from "@/lib/language"
import store from "@/store"

export default {
    components: {
        PageHeader, 
        CategoryModal: store.getters.theme === "new" ? CategoryModal : CategoryModalOld,
        FlatPickr
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data() {
        return {
            isLoading: false,
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
            return sl == "mm" ? "အသုံးစရိတ် / အသစ်" : "Expense / New";
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
            
            // Replicate ExpenseOld behavior: clear description and amount, keep category? 
            // Previous code cleared desc/amount.
            this.newItem.description = "";
            this.newItem.amount = "";
            this.calTotal();
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
        clearForm() {
            this.expenseItems = [];
            this.total_amount = 0;
            this.remark = "";
            this.newItem = { category: "", description: "", amount: "" };
            this.voucher_no = hp.setVoucherNo();
            this.v$.$reset();
        },
        async saveExpense() {
            this.v$.$validate();
            if (this.v$.$error) return;
            if (this.expenseItems.length === 0) return;

            this.isLoading = true;
            try {
                const save_time = Timestamp.fromDate(new Date());
                const data = {
                    voucher_no: this.voucher_no,
                    date: this.expense_date,
                    // payee: this.payee, // Removed as per ExpenseOld
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
.hover-opacity-100:hover {
    opacity: 1 !important;
}

.custom-scrollbar::-webkit-scrollbar {
    width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
    background: rgba(0,0,0,0.1);
    border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: rgba(0,0,0,0.2);
}
</style>
