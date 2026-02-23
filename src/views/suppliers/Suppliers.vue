<template>
    <div>
        <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid">
        <div class="row g-3">
            <!-- Sidebar: Search Filters -->
            <div class="col-12 col-lg-3">
                <div class="card border-0 shadow-sm rounded-3 fade-in sticky-top" style="top: 1rem; z-index: 10;">
                    <div class="card-header bg-white border-bottom-0 pt-3 pb-0">
                        <h6 class="card-title fw-bold text-primary mb-0">
                            <i class="fa-solid fa-filter me-2"></i>{{ t('search_link') }} <span class="badge float-end bg-primary rounded-pill">{{ suppliers.length }}</span>
                        </h6>
                    </div>
                    <div class="card-body">
                        <div class="row g-2">
                             <div class="col-sm-6 col-lg-12">
                                <div class="">
                                    <label class="form-label small text-muted text-uppercase fw-bold">{{ t('name') }}</label>
                                    <input 
                                        type="search" class="form-control" :placeholder="t('name')"
                                        v-model="search_by_name" @keydown.enter="performServerSearch"
                                    >
                                </div>
                            </div>
                            <div class="col-sm-6 col-lg-12">
                                <div class="">
                                    <label class="form-label small text-muted text-uppercase fw-bold">{{ t('company') }}</label>
                                    <input 
                                        type="search" class="form-control" :placeholder="t('company')"
                                        v-model="search_by_company" @keydown.enter="performServerSearch"
                                    >
                                </div>
                            </div>
                             <div class="col-12">
                                <div class="d-grid gap-2">
                                     <button class="btn btn-primary" @click="performServerSearch">
                                        <i class="fa-solid fa-search me-2"></i> {{ t('search_link') }}
                                    </button>
                                    <button class="btn btn-light text-danger" @click="clearSearch" v-if="search_by_name || search_by_company">
                                        <i class="fa-solid fa-xmark me-2"></i> {{ t('clear_link') }}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                 <!-- Action Bar -->
                 <div class="d-flex justify-content-between mt-3">
                     <router-link class="btn shadow-sm px-4 fw-medium" :to="{name : 'AddPurchase'}" >
                        <i class="fa-solid fa-cart-shopping me-2"></i> {{t('purchase')}}
                    </router-link>
                    <button class="btn btn-primary shadow-sm px-4 fw-medium" @click="openModal">
                        <i class="fa-solid fa-plus me-2"></i> {{t('new_link')}}
                    </button>
                </div>
            </div>

            <!-- Main Content: Data Table -->
            <div class="col-12 col-lg-9">
                 <div class="fade-in">
                    <!-- Table Card -->
                    <div class="card border-0 shadow-sm rounded-3 vh-80 overflow-y-auto" >
                        <div class="card-body p-0">
                            <div class="table-responsive">
                                <table class="table table-hover align-middle mb-0">
                                    <thead class="bg-light text-secondary text-uppercase small fw-semibold">
                                        <tr >
                                            <th class="ps-4 border-0 py-3" style="width: 25%">{{t('name')}}</th>
                                            <th class="border-0 py-3" style="width: 20%">{{t('phone')}}</th>
                                            <th class="border-0 py-3" style="width: 30%">{{t('company')}}</th>
                                            <th class="text-center border-0 py-3" style="width: 15%">{{t('member_since')}}</th>
                                            <th class="text-end pe-4 border-0 py-3" style="width: 10%">{{t('actions')}}</th>
                                        </tr>
                                    </thead>
                                    <tbody v-if="isLoading">
                                         <tr>
                                            <td colspan="5" class="py-5 text-center">
                                                <div class="spinner-border text-primary" role="status">
                                                    <span class="visually-hidden">Loading...</span>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                    <tbody v-else-if="suppliers.length > 0">
                                        <tr v-for="s in suppliers" :key="s.id" class="cursor-pointer transition-hover">
                                             <td class="ps-4 py-2">
                                                <div class="d-flex align-items-center">
                                                    <div class="avatar-circle me-3 bg-primary-subtle text-primary fw-bold shadow-sm" style="width: 38px; height: 38px;">
                                                        {{ s.name.charAt(0).toUpperCase() }}
                                                    </div>
                                                    <div>
                                                        <div class="fw-bold text-dark">{{ s.name }}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="text-secondary"><i class="fa-solid fa-phone me-2 small text-muted opacity-50"></i>{{ s.phone }}</td>
                                            <td class="text-secondary"><i class="fa-solid fa-building me-2 small text-muted opacity-50"></i>{{ s.company }}</td>
                                            <td class="text-center text-secondary small">{{ showTime(s.created_at) }}</td>
                                            <td class="text-end pe-4">
                                                <div class="btn-group">
                                                    <button 
                                                        class="btn btn-sm btn-light text-primary rounded-2 me-1" 
                                                        @click.stop="editSupplier(s)" 
                                                        title="Edit"
                                                    >
                                                        <i class="fa-solid fa-pen-to-square"></i>
                                                    </button>
                                                    <button 
                                                        class="btn btn-sm btn-light text-danger rounded-2" 
                                                        @click.stop="removeSupplier(s)" 
                                                        :disabled="user.uid != s.user.uid"
                                                        title="Delete"
                                                    >
                                                        <i class="fa-solid fa-trash"></i>
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    </tbody>
                                     <tbody v-else>
                                        <tr>
                                            <td colspan="5" class="text-center py-5 text-muted">
                                                <div class="mb-3"><i class="fa-solid fa-inbox fa-3x opacity-25"></i></div>
                                                <div>No suppliers found</div>
                                            </td>
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
        
        <!-- Add/Edit Modal -->
        <div class="modal fade" data-bs-backdrop="static" data-bs-keyboard="false" id="supplierModal" tabindex="-1" aria-hidden="true" ref="supplierModal">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <div class="modal-header border-bottom-0 pb-0">
                         <h5 class="modal-title fw-bold">{{ isUpdating ? 'Edit Supplier' : t('save_link') + ' Supplier' }}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                         <form @submit.prevent="handleSubmit">
                            <div class="mb-3">
                                <label for="s_name" class="form-label small text-muted text-uppercase fw-bold">{{t('name')}}</label>
                                <input type="text" class="form-control" id="s_name" v-model="form.name" :class="{'is-invalid': v$.form.name.$error}" placeholder="Enter supplier name">
                                <div class="invalid-feedback" v-if="v$.form.name.$error">Name is required</div>
                            </div>
                             <div class="mb-3">
                                <label for="s_phone" class="form-label small text-muted text-uppercase fw-bold">{{t('phone')}}</label>
                                <input type="tel" class="form-control" id="s_phone" v-model="form.phone" :class="{'is-invalid': v$.form.phone.$error}" placeholder="Enter phone number">
                                <div class="invalid-feedback" v-if="v$.form.phone.$error">Phone is required</div>
                            </div>
                             <div class="mb-3">
                                <label for="s_company" class="form-label small text-muted text-uppercase fw-bold">{{t('company')}}</label>
                                <input type="text" class="form-control" id="s_company" v-model="form.company" :class="{'is-invalid': v$.form.company.$error}" placeholder="Enter company">
                                <div class="invalid-feedback" v-if="v$.form.company.$error">Company is required</div>
                            </div>
                             <div class="mb-3" v-if="!isUpdating">
                                <label class="form-label small text-muted text-uppercase fw-bold">{{t('member_since')}}</label>
                                <flat-pickr class="form-control bg-white" v-model="form.created_at" :config="config" />
                            </div>
                        </form>
                    </div>
                     <div class="modal-footer border-top-0 pt-0">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Close</button>
                        <button type="button" class="btn btn-primary px-4" @click="handleSubmit" :disabled="isSaving || isUpdatingProcess">
                             <span v-if="isSaving || isUpdatingProcess" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
                             {{ isUpdating ? 'Update' : 'Save' }}
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deleteConfirmationModal" tabindex="-1" aria-hidden="true" ref="deleteConfirmationModal">
             <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <div class="modal-header border-bottom-0 pb-0">
                        <h5 class="modal-title fw-bold text-danger">Confirm Delete</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center py-4">
                        <div class="mb-3 text-danger opacity-50">
                            <i class="fa-solid fa-triangle-exclamation fa-3x"></i>
                        </div>
                        <p class="mb-0 fw-medium">Are you sure you want to delete this supplier?</p>
                        <p class="text-muted small" v-if="supplierToDelete">"{{ supplierToDelete.name }}"</p>
                    </div>
                    <div class="modal-footer border-top-0 pt-0 justify-content-center">
                        <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-danger px-4" @click="confirmDelete">Delete</button>
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate } from '@vuelidate/core'
import { required } from '@vuelidate/validators'
import { Timestamp, collection, addDoc, query, where, getDocs, onSnapshot, limit, doc, updateDoc, deleteDoc, orderBy } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/suppliers"
import FlatPickr from 'vue-flatpickr-component';
import { lang } from "@/lib/language"
import { Modal } from 'bootstrap'

export default {
    components: {
        PageHeader, FlatPickr
    },
    setup() {
        return { v$: useVuelidate() }
    },
    validations() {
        return {
            form: {
                name: { required },
                phone: { required },
                company: { required }
            }
        }
    },
    data() {
        return {
            form: {
                id: null,
                code: "",
                name: "",
                phone: "",
                company: "",
                created_at: "",
                is_deleted: false,
            },
            search_by_name: "",
            search_by_company: "",
            suppliers: [],
            suppliersData: [],
            isLoading: false,
            isSaving: false,
            isUpdating: false,
            isUpdatingProcess: false,
            isOffline: false,
            msg: {
                type: "",
                info: "",
            },
            limitData: 100,
            modalInstance: null,
            deleteModalInstance: null,
            supplierToDelete: null,
        }
    },
    mounted() {
        this.form.created_at = hp.setToday();
        this.fetchSuppliers();
        this.modalInstance = new Modal(this.$refs.supplierModal);
        this.deleteModalInstance = new Modal(this.$refs.deleteConfirmationModal);
        window.addEventListener('keydown', this.handleCtrl);
    },
    beforeUnmount() {
        if (this.modalInstance) this.modalInstance.hide();
        if (this.deleteModalInstance) this.deleteModalInstance.hide();
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
            return sl == "mm" ? `${lang.suppliers.mm}` : `${lang.suppliers.eng}`;
        },
        t() {
            return (key) => this.$store.getters.t(key);
        },
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
    methods: {
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault();
                const modalEl = this.$refs.supplierModal;
                if (modalEl.classList.contains('show')) {
                     this.handleSubmit();
                } else {
                     this.openModal();
                }
            }
        },
        openModal() {
            this.resetForm();
            this.modalInstance.show();
        },
        closeModal() {
            this.modalInstance.hide();
        },
        resetForm() {
            this.form = {
                id: null,
                code: "",
                name: "",
                phone: "",
                company: "",
                created_at: hp.setToday(),
                is_deleted: false,
            };
            this.isUpdating = false;
            this.isSaving = false;
            this.isUpdatingProcess = false;
            this.v$.$reset();
        },
        editSupplier(s) {
            this.isUpdating = true;
            this.form = {
                ...s,
            };
            this.modalInstance.show();
        },
        async handleSubmit() {
            const validation = await this.v$.$validate();
            if (!validation) return;

            if (this.isUpdating) {
                await this.updateSupplier();
            } else {
                await this.saveSupplier();
            }
        },
        generateKeywords(data) {
            return hp.generateKeywords(data);
        },
        async saveSupplier() {
            // Check duplicate name
            const coll = collection(db, "suppliers");
            const q = query(coll, where("name_lower_case", "==", this.form.name.toLowerCase()));
            const isTaken = await getDocs(q);
            if (!isTaken.empty) {
                this.msg.info = "This supplier name already exists";
                this.msg.type = "error";
                return;
            }

            this.isSaving = true;
            const date = hp.insertDateNoTime(this.form.created_at);
            const code = this.form.code ? this.form.code : hp.g_code();
            const created_at = Timestamp.fromDate(date);
            const dataCollection = collection(db, "suppliers");

            const data = {
                user: { uid: this.user.uid, displayName: this.user.displayName },
                name: this.form.name,
                name_lower_case: this.form.name.toLowerCase(),
                name_keywords: this.generateKeywords(this.form.name),
                company_keywords: this.generateKeywords(this.form.company),
                code: code,
                phone: this.form.phone,
                company: this.form.company,
                created_at: created_at,
                is_deleted: this.form.is_deleted,
                edit_user: { uid: this.user.uid, type: 'added' }
            };

            this.isCreated(code); 
            await addDoc(dataCollection, data);
        },
        async updateSupplier() {
            const coll = collection(db, "suppliers");
            // Check duplicate name
            const q = query(coll, where("name_lower_case", "==", this.form.name.toLowerCase()));
            const isTaken = await getDocs(q);
            
            let duplicate = false;
             if (!isTaken.empty) {
                isTaken.forEach(doc => {
                    if (doc.id !== this.form.id) duplicate = true;
                });
            }

            if (duplicate) {
                this.msg.info = "This supplier name already exists";
                this.msg.type = "error";
                return;
            }

            this.isUpdatingProcess = true;
            const updateDocument = doc(db, "suppliers", this.form.id);
            const data = {
                name: this.form.name,
                phone: this.form.phone,
                company: this.form.company,
                name_lower_case: this.form.name.toLowerCase(),
                name_keywords: this.generateKeywords(this.form.name),
                company_keywords: this.generateKeywords(this.form.company),
                edit_user: { uid: this.user.uid, type: "modified" }
            };

            this.isUpdated(this.form.code);
            await updateDoc(updateDocument, data);
        },
        async removeSupplier(s) {
            this.supplierToDelete = s;
            this.deleteModalInstance.show();
        },
        async confirmDelete() {
            if (!this.supplierToDelete) return;
            const s = this.supplierToDelete;
            
            const document = doc(db, "suppliers", s.id);
            this.isRemoved(s.code);
            await deleteDoc(document);
            
            this.deleteModalInstance.hide();
            this.supplierToDelete = null;
        },
        
        // Listeners
        async isCreated(code) {
            const query_url = query(collection(db, "suppliers"), where("code", "==", Number(code)));
            const unsub = onSnapshot(query_url, async (snapShot) => {
                snapShot.docChanges().forEach((change) => {
                    if (change.type != "added") return;
                    this.msg.info = "Supplier created successfully";
                    this.msg.type = "success";
                    this.closeModal();
                    unsub();
                })
            })
        },
        async isUpdated(code) {
            const query_url = query(collection(db, "suppliers"), where("code", "==", Number(code)));
            const unsub = onSnapshot(query_url, async (snapShot) => {
                snapShot.docChanges().forEach((change) => {
                    if (change.type != "modified") return;
                    this.msg.info = "Supplier updated successfully";
                    this.msg.type = "success";
                    this.closeModal();
                    unsub();
                })
            })
        },
        async isRemoved(code) {
            this.msg.info = "Supplier deleted successfully";
            this.msg.type = "success";
        },

        async fetchSuppliers() {
            this.isLoading = true;
            const query_url = query(collection(db, "suppliers"), orderBy("created_at", "desc"), limit(this.limitData));
            onSnapshot(query_url, async (snapShot) => {
                const suppliers = ss.suppliers(snapShot);
                this.suppliersData = suppliers;
                this.suppliers = suppliers;
                this.isLoading = false;
            });
        },
        
        clearSearch() {
            this.search_by_name = "";
            this.search_by_company = "";
            this.fetchSuppliers();
        },
        async performServerSearch() {
            if (!this.search_by_name && !this.search_by_company) {
                return;
            }
            this.isLoading = true;
            this.suppliers = []; 
            try {
                 const promises = [];
                 
                 if (this.search_by_name) {
                     const q = query(
                        collection(db, "suppliers"), 
                        where("name_keywords", "array-contains", this.search_by_name.toLowerCase()), 
                        orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    promises.push(getDocs(q));
                 }
                 
                 if (this.search_by_company) {
                     const q = query(
                        collection(db, "suppliers"), 
                        where("company_keywords", "array-contains", this.search_by_company.toLowerCase()), 
                        orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    promises.push(getDocs(q));
                 }

                const snapshots = await Promise.all(promises);
                let results = [];
                snapshots.forEach(snap => {
                    results = [...results, ...ss.suppliers(snap)];
                });

                // Deduplicate
                const map = new Map();
                results.forEach(s => {
                    if(!map.has(s.id)) map.set(s.id, s);
                });

                this.suppliers = Array.from(map.values());
                
            } catch (error) {
                console.error("Error searching:", error);
            } finally {
                this.isLoading = false;
            }
        },
        showTime(t) {
            return hp.showTime(t)
        },
    }

}
</script>

<style scoped>
.fade-in {
    animation: fadeIn 0.4s ease-in-out;
}
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(10px); }
    to { opacity: 1; transform: translateY(0); }
}

.avatar-circle {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
}

.cursor-pointer {
    cursor: pointer;
}

.transition-hover {
    transition: background-color 0.2s, transform 0.1s;
}
.transition-hover:hover {
    background-color: #f8f9fa; 
    transform: scale(1.002);
}

/* Custom scrollbar for table container if needed */
.table-responsive::-webkit-scrollbar {
    height: 6px;
}
.table-responsive::-webkit-scrollbar-thumb {
    background: #ccc;
    border-radius: 3px;
}
</style>