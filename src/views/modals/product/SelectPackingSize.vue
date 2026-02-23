<template>
    <!-- selectPackingSize Modal -->
    <div class="modal fade" id="selectPackingSizeModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="selectPackingSizeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg ripple-container">
                <!-- Modern Header -->
                <div class="modal-header bg-white border-bottom-0 pb-0">
                    <h5 class="modal-title fw-bold text-primary" id="selectPackingSizeModalLabel">
                        <i class="fa-solid fa-boxes-packing me-2"></i>Set Packing Size
                    </h5>
                    <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-2">
                    <!-- Unit Configuration Section -->
                    <div class="card border-0 bg-light rounded-4 mb-2">
                        <div class="card-body p-2">
                            <!-- Largest Unit (Optional/Header) 
                            <div class="mb-4">
                                <label class="small text-muted text-uppercase fw-bold mb-2 d-block">Largest Unit (Optional)</label>
                                <div class="input-group shadow-sm rounded-3 overflow-hidden">
                                    <span class="input-group-text bg-white border-0">
                                        <i class="fa-solid fa-box text-primary"></i>
                                    </span>
                                    <select class="form-select border-0 bg-white py-2" v-model="largest_unit">
                                        <option value="">No parent unit (Standard)</option>
                                        <option v-for="s in packingSizes" :key="s.id">{{s.packing_unit}}</option>
                                    </select>
                                </div>
                            </div>-->

                            <!-- Medium Unit Selection -->
                            <div class="mb-2">
                                <label class="small text-muted text-uppercase fw-bold mb-2 d-block">Parent Unit</label>
                                <div class="input-group shadow-sm rounded-3 overflow-hidden">
                                    <span class="input-group-text bg-white border-0">
                                        <i class="fa-solid fa-layer-group text-primary"></i>
                                    </span>
                                    <select class="form-select border-0 bg-white py-2" v-model="medium_unit">
                                        <option value="" disabled>Select parent unit</option>
                                        <option v-for="s in packingSizes" :key="s.id">{{s.packing_unit}}</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Relationship Mapping (1 Medium = X Smallest) -->
                            <div class="p-2 bg-white rounded-3 shadow-sm">
                                <div class="d-flex align-items-center justify-content-between mb-2">
                                    <span class="fw-bold text-dark small">Contains</span>
                                </div>
                                <div class="row g-2 align-items-center">
                                    <div class="col-5">
                                        <input 
                                            :disabled="!medium_unit" 
                                            @keypress.enter="pressSetBtn" 
                                            type="number" 
                                            class="form-control border-0 bg-light py-2 text-center fw-bold" 
                                            v-model="smallest_qty"
                                            placeholder="Qty">
                                    </div>
                                    <div class="col-7">
                                        <select class="form-select border-0 bg-light py-2" v-model="smallest_unit">
                                            <option value="" disabled>Select sub unit</option>
                                            <option v-for="s in packingSizes" :key="s.id">{{s.packing_unit}}</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="d-grid gap-2">
                        <button 
                            id="setBtn" 
                            class="btn btn-primary rounded-3 py-2 fw-bold shadow-sm" 
                            data-bs-dismiss="modal" 
                            @click="setCode"
                            :disabled="isSaving || !medium_unit || !smallest_unit || !smallest_qty">
                            <span v-if="!isSaving"><i class="fa-solid fa-check me-2"></i>Apply Changes</span>
                            <span class="spinner-border spinner-border-sm" v-else></span>
                        </button>
                        <button type="button" class="btn btn-light rounded-3 py-2 text-muted fw-medium border-0" data-bs-dismiss="modal">
                            <i class="fa-solid fa-times me-2"></i>Cancel
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import * as hp from "@/lib/helper"

export default {
    props: ['ei', 'packingSizes'],
    data() {
        return {
            isUpdating: false,
            isLoading: false,
            isSaving: false,
            smallest_unit: "",
            smallest_qty: "",
            medium_unit: "",
            medium_qty: "",
            largest_unit: "",
        }
    },
    mounted() {
        const myModalEl = document.getElementById('selectPackingSizeModal')
        myModalEl.addEventListener('show.bs.modal', event => {
            this.smallest_unit = this.ei.packing_size.smallest_unit
            this.smallest_qty = this.ei.packing_size.smallest_qty;
            this.medium_qty = this.ei.packing_size.medium_qty;
            this.medium_unit = this.ei.packing_size.medium_unit;
            this.largest_unit = this.ei.packing_size.largest_unit;
        })
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
        pressSetBtn() {
            const btn = document.getElementById("setBtn")
            if (btn && !btn.disabled) btn.click();
        },
        setCode() {
            if (this.medium_unit && this.smallest_qty && this.smallest_unit) {
                const packingSize = {
                    largest_unit: this.largest_unit ? this.largest_unit : null,
                    medium_qty: this.medium_qty ? this.medium_qty : null,
                    medium_unit: this.medium_unit ? this.medium_unit : null,
                    smallest_qty: this.smallest_qty ? this.smallest_qty : null,
                    smallest_unit: this.smallest_unit ? this.smallest_unit : null,
                }
                this.$emit("setPackingSize", packingSize)

                // Reset local state after emit
                this.largest_unit = "";
                this.medium_qty = "";
                this.medium_unit = "";
                this.smallest_qty = "";
                this.smallest_unit = "";
            }
        },
        showTime(t) {
            return hp.showTime(t)
        },
    }
}
</script>

<style scoped>
.form-select:focus, .form-control:focus {
    box-shadow: none;
}
.input-group-text {
    min-width: 45px;
    justify-content: center;
}
</style>
