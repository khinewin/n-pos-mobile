<template>
    <div class="modal fade" id="categoryManagementModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="categoryManagementModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg ripple-container">
                <!-- Modern Header -->
                <div class="modal-header bg-white border-bottom-0 pb-0">
                    <h5 class="modal-title fw-bold text-primary" id="categoryManagementModalLabel">
                        <i class="fa-solid fa-tags me-2"></i>{{t('categories')}}
                    </h5>
                    <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-4">
                    <!-- Add Category Section -->
                    <div class="card bg-light border-0 rounded-3 mb-2">
                        <div class="card-body p-2">
                            <div class="input-group shadow-sm rounded-3 overflow-hidden">
                                <span class="input-group-text bg-white border-0">
                                    <i class="fa-solid fa-plus text-success"></i>
                                </span>
                                <input 
                                    @keypress.enter="saveCategory" 
                                    v-model="cat_name" 
                                    type="text" 
                                    class="form-control border-0 bg-white py-2" 
                                    placeholder="Enter category name...">
                                <button 
                                    class="btn btn-primary px-3" 
                                    type="button" 
                                    :disabled="isSaving || !cat_name" 
                                    @click="saveCategory">
                                    <span v-if="!isSaving"><i class="fa-solid fa-check"></i></span>
                                    <span class="spinner-border spinner-border-sm" v-else></span>
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Category List -->
                    <div class="vh-50 overflow-y-auto custom-scrollbar pe-2">
                        <div v-if="isLoading" class="d-flex justify-content-center align-items-center py-5">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                        </div>

                        <div v-else-if="cats.length === 0" class="text-center text-muted py-5">
                            <i class="fa-solid fa-folder-open fa-3x mb-3 opacity-25"></i>
                            <p class="mb-0">No categories found</p>
                        </div>

                        <div v-else>
                            <table class="table table-hover align-middle border-0">
                                <thead class="sticky-top bg-white z-3">
                                    <tr class="small text-muted text-uppercase fw-bold border-bottom">
                                        <th class="border-0 px-0">Name</th>
                                        <th class="border-0 text-end" style="width: 100px;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="c in cats" :key="c.id" class="border-0 category-row">
                                        <template v-if="editItem.id !== c.id">
                                            <td class="p-2">
                                                <div @click="setEdit(c)" class="fw-medium text-dark ps-2 cursor-pointer category-name">
                                                    {{c.cat_name}}
                                                </div>
                                            </td>
                                            <td class="text-end p-2">
                                                <button 
                                                    :disabled="c.user.uid != user.uid" 
                                                    class="btn btn-link link-danger text-decoration-none py-1 px-2 rounded-circle hover-bg-danger" 
                                                    @click="confirmRemove(c)">
                                                    <i class="fa-solid fa-trash-can"></i>
                                                </button>
                                            </td>
                                        </template>
                                        <td v-else colspan="2" class="p-2">
                                            <div class="input-group shadow-sm rounded-3 overflow-hidden">
                                                <span class="input-group-text bg-white border-0">
                                                    <i class="fa-solid fa-pen-to-square text-success"></i>
                                                </span>
                                                <input 
                                                    @keypress.enter="saveEditCategory" 
                                                    type="text" 
                                                    v-model="c.cat_name" 
                                                    class="form-control border-0 bg-white py-2"
                                                    @input="setEdit(c)"
                                                    v-focus>
                                                <button class="btn btn-primary px-3" @click="saveEditCategory">
                                                    <i class="fa-solid fa-check"></i>
                                                </button>
                                                <button class="btn btn-light px-3" @click="editItem = {}">
                                                    <i class="fa-solid fa-times text-muted"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

              
            </div>
        </div>

        <!-- Delete Confirmation Modal -->
        <div class="modal fade" id="deleteCategoryModal" tabindex="-1" aria-hidden="true" ref="deleteCategoryModal">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content border-0 shadow-lg rounded-4">
                    <div class="modal-body text-center p-4">
                        <div class="bg-danger-subtle text-danger rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 64px; height: 64px;">
                            <i class="fa-solid fa-triangle-exclamation fa-2x"></i>
                        </div>
                        <h6 class="fw-bold mb-2">Delete Category?</h6>
                        <p class="text-muted small mb-4">Are you sure you want to delete <span class="fw-bold text-dark">"{{ categoryToDelete?.cat_name }}"</span>? This action cannot be undone.</p>
                        
                        <div class="d-grid gap-2">
                            <button type="button" class="btn btn-danger rounded-3 py-2 fw-bold shadow-sm" @click="proceedDeletion" :disabled="isDeleting">
                                <span v-if="!isDeleting">Delete Forever</span>
                                <span class="spinner-border spinner-border-sm" v-else></span>
                            </button>
                            <button type="button" class="btn btn-light rounded-3 py-2 text-muted fw-medium" data-bs-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { Timestamp, collection, onSnapshot, addDoc, doc, deleteDoc, updateDoc, query, orderBy } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as cat from "@/lib/snapshot/category"
import * as hp from "@/lib/helper"
import { Modal } from 'bootstrap'

export default {
    directives: {
        focus: {
            mounted(el) {
                el.focus()
            }
        }
    },
    data() {
        return {
            isLoading: false,
            editItem: {},
            cat_name: "",
            cats: [],
            isSaving: false,
            isDeleting: false,
            msg: {
                type: "",
                info: "",
            },
            limitItem: 100,
            categoryToDelete: null,
            deleteModalInstance: null
        }
    },
    mounted() {
        this.isLoading = true;
        this.checkDatabaseCategory();
        this.deleteModalInstance = new Modal(this.$refs.deleteCategoryModal);
    },
    unmounted() {
        if (this.deleteModalInstance) this.deleteModalInstance.hide();
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
        t() {
            return (key) => this.$store.getters.t(key);
        }
    },
    methods: {
        setMsg() {
            this.$emit("setMsg", this.msg.info, this.msg.type)
        },
        setEdit(item) {
            this.editItem = { ...item };
        },
        async saveEditCategory() {
            if (!this.editItem.cat_name) return;
            const dataCollection = doc(db, "category", this.editItem.id)
            const data = {
                cat_name: this.editItem.cat_name,
                edit_user: { uid: this.user.uid, type: 'modified' },
            }
            await updateDoc(dataCollection, data);
            this.editItem = {};
        },
        async saveCategory() {
            if (!this.cat_name) return false;
            this.isSaving = true;
            const dataCollection = collection(db, "category")
            const save_time = Timestamp.fromDate(new Date());
            const code = new Date().getTime()
            const data = {
                user: { uid: this.user.uid, displayName: this.user.displayName },
                cat_name: this.cat_name,
                code: code.toString(),
                edit_user: { uid: this.user.uid, type: 'added' },
                created_at: save_time,
            }
            await addDoc(dataCollection, data);
            this.cat_name = "";
            this.isSaving = false;
        },
        confirmRemove(p) {
            this.categoryToDelete = p;
            this.deleteModalInstance.show();
        },
        async proceedDeletion() {
            if (!this.categoryToDelete) return;
            this.isDeleting = true;
            const document = doc(db, "category", this.categoryToDelete.id)
            await deleteDoc(document);
            this.isDeleting = false;
            this.deleteModalInstance.hide();
            this.categoryToDelete = null;
        },
        async checkDatabaseCategory() {
            const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
            onSnapshot(query_url, async (snapShot) => {
                const category = cat.category(snapShot);
                this.cats = category.slice(0, this.limitItem);
                this.isLoading = false;

                snapShot.docChanges().forEach((change) => {
                    const res = change.doc.data();
                    const edit_user = res.edit_user != null ? res.edit_user : "";
                    const user = this.user;

                    if (change.type === "added" && edit_user.uid == user.uid && snapShot.docChanges().length === 1) {
                        this.msg.info = "The new category has been created"
                        this.msg.type = "success"
                        this.setMsg();
                    }
                    if (change.type === "modified" && edit_user.type == "modified" && edit_user.uid == user.uid) {
                        this.msg.info = "The selected category has been updated"
                        this.msg.type = "success"
                        this.setMsg();
                    }
                    if (change.type === "removed" && this.isDeleting) {
                        this.msg.info = "The selected category has been deleted"
                        this.msg.type = "success"
                        this.setMsg();
                    }
                })
            });
        }
    }
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
    width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #cccccc;
}

.category-row:hover .category-name {
    color: var(--bs-primary) !important;
}

.hover-bg-danger:hover {
    background-color: rgba(220, 53, 69, 0.1) !important;
}

.category-name {
    transition: color 0.2s;
}

.cursor-pointer {
    cursor: pointer;
}
</style>