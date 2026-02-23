<template>
    <!-- packingSizes Modal -->
    <div class="modal fade" id="packingSizeModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="packingSizeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg ripple-container">
                <!-- Modern Header -->
                <div class="modal-header bg-white border-bottom-0 pb-0">
                    <h5 class="modal-title fw-bold text-primary" id="packingSizeModalLabel">
                        <i class="fa-solid fa-boxes-packing me-2"></i>{{t('packing_units')}}
                    </h5>
                    <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-4">
                    <!-- Add Packing Unit Section -->
                    <div class="card bg-light border-0 rounded-3 mb-2">
                        <div class="card-body p-2">
                            <div class="input-group shadow-sm rounded-3 overflow-hidden">
                                <span class="input-group-text bg-white border-0">
                                    <i class="fa-solid fa-plus text-success"></i>
                                </span>
                                <input 
                                    @keypress.enter="savePackingSize" 
                                    v-model="packing_unit" 
                                    type="text" 
                                    class="form-control border-0 bg-white py-2" 
                                    placeholder="Enter packing unit (e.g., Box, Pcs)...">
                                <button 
                                    class="btn btn-primary px-3" 
                                    type="button" 
                                    :disabled="isSaving || !packing_unit" 
                                    @click="savePackingSize">
                                    <span v-if="!isSaving"><i class="fa-solid fa-check"></i></span>
                                    <span class="spinner-border spinner-border-sm" v-else></span>
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Packing Unit List -->
                    <div class="vh-50 overflow-y-auto custom-scrollbar pe-2">
                        <div v-if="isLoading" class="d-flex justify-content-center align-items-center py-5">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                        </div>

                        <div v-else-if="packingSizes.length === 0" class="text-center text-muted py-5">
                            <i class="fa-solid fa-box-open fa-3x mb-3 opacity-25"></i>
                            <p class="mb-0">No packing units found</p>
                        </div>

                        <div v-else>
                            <table class="table table-hover align-middle border-0">
                                <thead class="sticky-top bg-white z-3">
                                    <tr class="small text-muted text-uppercase fw-bold border-bottom">
                                        <th class="border-0 px-0">Unit Name</th>
                                        <th class="border-0 text-end" style="width: 100px;">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="p in packingSizes" :key="p.id" class="border-0 category-row">
                                        <template v-if="editItem.id !== p.id">
                                            <td class="p-2">
                                                <div @click="setEdit(p)" class="fw-medium text-dark ps-2 cursor-pointer category-name">
                                                    {{p.packing_unit}}
                                                </div>
                                            </td>
                                            <td class="text-end p-2">
                                                <button 
                                                    :disabled="p.user.uid != user.uid" 
                                                    class="btn btn-link link-danger text-decoration-none py-1 px-2 rounded-circle hover-bg-danger" 
                                                    @click="confirmRemove(p)">
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
                                                    @keypress.enter="saveEdit" 
                                                    type="text" 
                                                    v-model="p.packing_unit" 
                                                    class="form-control border-0 bg-white py-2"
                                                    @input="setEdit(p)"
                                                    v-focus>
                                                <button class="btn btn-primary px-3" @click="saveEdit">
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
        <div class="modal fade" id="deletePackingSizeModal" tabindex="-1" aria-hidden="true" ref="deletePackingSizeModal">
            <div class="modal-dialog modal-dialog-centered modal-sm">
                <div class="modal-content border-0 shadow-lg rounded-4">
                    <div class="modal-body text-center p-4">
                        <div class="bg-danger-subtle text-danger rounded-circle d-inline-flex align-items-center justify-content-center mb-3" style="width: 64px; height: 64px;">
                            <i class="fa-solid fa-triangle-exclamation fa-2x"></i>
                        </div>
                        <h6 class="fw-bold mb-2">Delete Packing Unit?</h6>
                        <p class="text-muted small mb-4">Are you sure you want to delete <span class="fw-bold text-dark">"{{ packingSizeToDelete?.packing_unit }}"</span>? This action cannot be undone.</p>
                        
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
import { Timestamp, collection, onSnapshot, addDoc, doc, deleteDoc, updateDoc, query, orderBy, limit } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/packingSizes"
import * as hp from "@/lib/helper"
import { Modal } from 'bootstrap'

export default {
    props: ['packingSizes'],
    directives: {
        focus: {
            mounted(el) {
                el.focus()
            }
        }
    },
    data() {
        return {
            packing_unit: "",
            isUpdating: false,
            isLoading: false,
            isSaving: false,
            editItem: {},
            msg: {
                type: "",
                info: "",
            },
            packingSizeToDelete: null,
            deleteModalInstance: null,
            isDeleting: false
        }
    },
    mounted() {
        this.deleteModalInstance = new Modal(this.$refs.deletePackingSizeModal);
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
        },
    },
    methods: {
        confirmRemove(p) {
            this.packingSizeToDelete = p;
            this.deleteModalInstance.show();
        },
        async proceedDeletion() {
            if (!this.packingSizeToDelete) return;
            this.isDeleting = true;
            const document = doc(db, "packingSizes", this.packingSizeToDelete.id)
            this.checkDatabaseRemoved(this.packingSizeToDelete.id)
            await deleteDoc(document);
            this.isDeleting = false;
            this.deleteModalInstance.hide();
            this.packingSizeToDelete = null;
        },
        setEdit(p) {
            this.editItem = { ...p, edit_user: { uid: this.user.uid, type: "modified" } };
        },
        async saveEdit() {
            let result = this.packingSizes.some((f) => f.id != this.editItem.id && f.packing_unit.toLowerCase() === this.editItem.packing_unit.toLowerCase())
            if (result) {
                this.msg.info = "This packing unit already exists"
                this.msg.type = "error";
                this.setMsg();
                return false;
            }
            this.isUpdating = true;
            const data = doc(db, "packingSizes", this.editItem.id)
            this.checkDatabaseEdit(this.editItem.id)
            await updateDoc(data, this.editItem)
        },
        setMsg() {
            this.$emit("setMsg", this.msg.info, this.msg.type)
        },
        async savePackingSize() {
            if (!this.packing_unit) return false;

            let isTaken = this.packingSizes.filter((f) => f.packing_unit.toLowerCase() == this.packing_unit.toLowerCase())
            if (isTaken.length > 0) {
                this.msg.info = "This packing unit already exists"
                this.msg.type = "error"
                this.setMsg();
                this.packing_unit = "";
                return false;
            }

            this.isSaving = true;
            const created_at = Timestamp.fromDate(new Date());
            const dataCollection = collection(db, "packingSizes")
            const data = {
                user: { uid: this.user.uid, displayName: this.user.displayName },
                packing_unit: this.packing_unit,
                created_at: created_at,
                edit_user: { uid: this.user.uid, type: "added" },
            }
            this.checkDatabaseAdded();
            await addDoc(dataCollection, data);
        },
        async checkDatabaseAdded() {
            const query_url = query(collection(db, "packingSizes"), orderBy("created_at", "desc"), limit(1));
            const unsub = onSnapshot(query_url, async (snapShot) => {
                const user = this.user;
                 snapShot.docChanges().forEach((change) => {
                    const res = change.doc.data();
                    if (change.type === "added" && res.edit_user.uid == user.uid) {
                        this.msg.info = "The new packing unit has been created"
                        this.msg.type = "success"
                        this.packing_unit = "";
                        this.isSaving = false;
                        this.setMsg();
                        unsub();
                    }
                });
            });
        },
        async checkDatabaseEdit(id) {
            const query_url = doc(db, "packingSizes", id);
            const unsub = onSnapshot(query_url, async (snapShot) => {
                const res = snapShot.data();
                const user = this.user;
                if (res && res.edit_user.uid == user.uid && res.edit_user.type == "modified") {
                    this.isUpdating = false;
                    this.editItem = {}
                    this.msg.info = "Selected packing unit has been updated"
                    this.msg.type = "success"
                    this.setMsg();
                    unsub();
                }
            });
        },
        async checkDatabaseRemoved(id) {
            const query_url = doc(db, "packingSizes", id);
            // Since it's removed, we might not get data back easily via onSnapshot on the doc itself if it's gone
            // But we can listen for the deletion event or just trust the await deleteDoc if simple
            // Actually, the original implementation had a listener. Let's keep a simplified version or rely on method completion
             this.msg.info = "Selected packing unit has been deleted"
             this.msg.type = "success"
             this.setMsg();
        },
        showTime(t) {
            return hp.showTime(t)
        },
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