<template>
    <!-- Set Quantity Modal -->
    <div class="modal fade" id="setQtyModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="setQtyModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
                <!-- Premium Header -->
                <div class="modal-header bg-white border-bottom py-2 px-3">
                    <div class="d-flex align-items-center">
                        <div class="bg-primary bg-opacity-10 p-2 rounded-3 me-3">
                            <i class="fa-solid fa-boxes-stacked text-primary fs-5"></i>
                        </div>
                        <div>
                            <h5 class="modal-title fw-bold mb-0" id="setQtyModalLabel">Inventory Balance</h5>
                            <p class="text-muted small mb-0" v-if="ei.item_name">{{ei.item_name}}</p>
                        </div>
                    </div>
                    <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-2 bg-light-subtle">
                    <div v-if="Object.keys(ei).length">
                        <!-- Horizontal Quantity Grid -->
                        <div class="d-flex flex-row gap-2 mb-2">
                            <!-- Largest Unit -->
                            <div v-if="ei.packing_size.largest_unit" class="card border-0 shadow-sm rounded-3 flex-fill">
                                <div class="card-body p-3 text-center">
                                    <div class="bg-primary-subtle text-primary rounded-circle d-flex align-items-center justify-content-center mx-auto mb-2" style="width: 45px; height: 45px;">
                                        <i class="fa-solid fa-cubes"></i>
                                    </div>
                                    <label class="small text-muted text-uppercase fw-bold d-block mb-1">{{ei.packing_size.largest_unit}}</label>
                                    <input 
                                        @keypress.enter="pressSaveBtn" 
                                        type="number" 
                                        v-model="ei.l_qty" 
                                        class="form-control form-control-sm border-0 bg-transparent text-center fw-bold fs-5 p-0" 
                                        placeholder="0">
                                </div>
                            </div>

                            <!-- Medium Unit -->
                            <div v-if="ei.packing_size.medium_unit" class="card border-0 shadow-sm rounded-3 flex-fill">
                                <div class="card-body p-2 text-center">
                                    <div class="bg-info-subtle text-info rounded-circle d-flex align-items-center justify-content-center mx-auto mb-2" style="width: 45px; height: 45px;">
                                        <i class="fa-solid fa-layer-group"></i>
                                    </div>
                                    <label class="small text-muted text-uppercase fw-bold d-block mb-1">{{ei.packing_size.medium_unit}}</label>
                                    <input 
                                        @keypress.enter="pressSaveBtn" 
                                        type="number" 
                                        v-model="ei.m_qty" 
                                        class="form-control form-control-sm border-0 bg-transparent text-center fw-bold fs-5 p-0" 
                                        placeholder="0">
                                </div>
                            </div>

                            <!-- Smallest Unit -->
                            <div v-if="ei.packing_size.smallest_unit" class="card border-0 shadow-sm rounded-3 flex-fill">
                                <div class="card-body p-2 text-center">
                                    <div class="bg-success-subtle text-success rounded-circle d-flex align-items-center justify-content-center mx-auto mb-2" style="width: 45px; height: 45px;">
                                        <i class="fa-solid fa-cube"></i>
                                    </div>
                                    <label class="small text-muted text-uppercase fw-bold d-block mb-1">{{ei.packing_size.smallest_unit}}</label>
                                    <input 
                                        @keypress.enter="pressSaveBtn" 
                                        type="number" 
                                        v-model="ei.s_qty" 
                                        class="form-control form-control-sm border-0 bg-transparent text-center fw-bold fs-5 p-0" 
                                        placeholder="0">
                                </div>
                            </div>
                        </div>

                        <!-- Info Area -->
                        <div class="p-2 bg-white rounded-3 shadow-sm border-start border-4 border-primary mt-2">
                             <div class="d-flex align-items-center">
                                <div class="bg-primary bg-opacity-10 p-2 rounded-circle me-3">
                                    <i class="fa-solid fa-info-circle text-primary small"></i>
                                </div>
                                <div>
                                    <h6 class="fw-bold mb-0">Update Inventory</h6>
                                    <p class="text-muted small mb-0">Adjust quantities for each available unit type</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal-footer bg-white border-top py-2 px-2">
                    <div class="d-flex gap-2 w-100">
                        <button type="button" class="btn btn-light flex-fill fw-bold py-2 border-0" data-bs-dismiss="modal">Cancel</button>
                        <button id="saveQty" type="button" class="btn btn-primary flex-fill fw-bold py-2 shadow-sm" @click="saveQty" data-bs-dismiss="modal">
                            <i class="fa-solid fa-check me-2"></i>Apply Changes
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>


<script>
export default {
    props: ['ei'],
    computed: {
        t() {
            return (key) => this.$store.getters.t(key);
        },
    },
    methods: {
        saveQty() {
            this.$emit("saveQty")
        },
        pressSaveBtn() {
            const btn = document.getElementById("saveQty")
            if (btn) btn.click();
        }
    }
}
</script>

<style scoped>
.form-control-lg:focus {
    box-shadow: none;
    background-color: #fff !important;
    outline: 2px solid #0d6efd;
}
.bg-light-subtle {
    background-color: #f8f9fa !important;
}
</style>