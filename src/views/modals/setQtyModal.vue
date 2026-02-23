<template>
    <!-- Set Quantity Modal -->
    <div class="modal fade" id="setQtyModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="setQtyModalLabel" aria-hidden="true" style="background-color: rgba(0,0,0,0.4);">
        <div class="modal-dialog modal-dialog-centered modal-sm">
            <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">               
                <!-- Header -->
                <div class="modal-header bg-primary text-white border-0 py-2">
                    <h6 class="modal-title fw-bold" id="setQtyModalLabel">
                        <i class="fa-solid fa-layer-group me-2"></i>Set Quantities
                    </h6>
                    <button type="button" class="btn-close btn-close-white shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-2 bg-light">
                    <!-- Item Info Alert -->
                    <div v-if="ei && Object.keys(ei).length" class="alert alert-primary border-0 shadow-sm rounded-3 py-2 px-3 mb-2 d-flex align-items-center">
                        <i class="fa-solid fa-box fa-lg me-3"></i>
                        <div>
                            <div class="small text-uppercase opacity-75 fw-bold" style="font-size: 0.65rem;">Current Product</div>
                            <div class="fw-bold fs-6">{{ei.item_name}}</div>
                        </div>
                    </div>

                    <div v-if="ei && Object.keys(ei).length">
                        <!-- Input Cards -->
                        <div class="d-flex flex-row gap-2 mb-2">
                            <!-- Largest Unit -->
                            <div v-if="ei.packing_size.largest_unit" class="card border-0 shadow-sm rounded-3">
                                <div class="card-body p-2 d-flex align-items-center gap-2">
                                    <div class="bg-primary-subtle text-primary rounded-3 p-2 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                        <i class="fa-solid fa-cubes"></i>
                                    </div>
                                    <div class="flex-grow-1">
                                        <label class="small text-muted fw-bold text-uppercase mb-0" style="font-size: 0.6rem;">{{ei.packing_size.largest_unit}}</label>
                                        <input 
                                            @keypress.enter="pressSetBtn" 
                                            type="number" 
                                            v-model="ei.input_l_qty" 
                                            class="form-control form-control-sm border-0 bg-transparent p-0 fw-bold fs-5 shadow-none" 
                                            placeholder="0">
                                    </div>
                                </div>
                            </div>

                            <!-- Medium Unit -->
                            <div v-if="ei.packing_size.medium_unit" class="card border-0 shadow-sm rounded-3">
                                <div class="card-body p-2 d-flex align-items-center gap-2">
                                    <div class="bg-info-subtle text-info rounded-3 p-2 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                        <i class="fa-solid fa-layer-group"></i>
                                    </div>
                                    <div class="flex-grow-1">
                                        <label class="small text-muted fw-bold text-uppercase mb-0" style="font-size: 0.6rem;">{{ei.packing_size.medium_unit}}</label>
                                        <input 
                                            @keypress.enter="pressSetBtn" 
                                            type="number" 
                                            v-model="ei.input_m_qty" 
                                            class="form-control form-control-sm border-0 bg-transparent p-0 fw-bold fs-5 shadow-none" 
                                            placeholder="0">
                                    </div>
                                </div>
                            </div>

                            <!-- Smallest Unit -->
                            <div v-if="ei.packing_size.smallest_unit" class="card border-0 shadow-sm rounded-3">
                                <div class="card-body p-2 d-flex align-items-center gap-2">
                                    <div class="bg-success-subtle text-success rounded-3 p-2 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                        <i class="fa-solid fa-cube"></i>
                                    </div>
                                    <div class="flex-grow-1">
                                        <label class="small text-muted fw-bold text-uppercase mb-0" style="font-size: 0.6rem;">{{ei.packing_size.smallest_unit}}</label>
                                        <input 
                                            @keypress.enter="pressSetBtn" 
                                            type="number" 
                                            v-model="ei.input_s_qty" 
                                            class="form-control form-control-sm border-0 bg-transparent p-0 fw-bold fs-5 shadow-none" 
                                            placeholder="0">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Calculation Preview -->
                        <div class="bg-white rounded-3 p-2 shadow-sm mb-2 text-center border">
                            <div class="small text-muted text-uppercase fw-bold mb-1" style="font-size: 0.7rem;">Total Purchase Qty</div>
                            <div class="h3 fw-bolder text-primary mb-0">{{calculatedPurQty}}</div>
                            <div class="small fw-medium text-muted">{{ei.packing_size.smallest_unit}}</div>
                        </div>

                        <!-- Action Buttons -->
                        <div class="d-flex flex-column gap-2">
                            <button type="button" id="setBtn" class="btn btn-primary py-2 fw-bold shadow-sm rounded-3" @click="setQty" data-bs-dismiss="modal">
                                <i class="fa-solid fa-hard-drive me-2"></i> Save Changes
                            </button>
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-light flex-fill py-2 small fw-bold text-muted rounded-3" @click="resetQty">
                                    <i class="fa-solid fa-broom me-1"></i> Clear
                                </button>
                                <button type="button" class="btn btn-light flex-fill py-2 small fw-bold text-danger rounded-3" data-bs-dismiss="modal">
                                    <i class="fa-solid fa-circle-xmark me-1"></i> Cancel
                                </button>
                            </div>
                        </div>
                    </div>
                </div>                
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props:['ei'],
    computed: {
        calculatedPurQty() {
            if (!this.ei || !this.ei.packing_size) return 0;
            const m_qty = Number(this.ei.packing_size.medium_qty) || 0;
            const s_qty = Number(this.ei.packing_size.smallest_qty) || 0;
            
            let total = 0;
            total += (Number(this.ei.input_l_qty) || 0) * (m_qty * s_qty);
            total += (Number(this.ei.input_m_qty) || 0) * s_qty;
            total += (Number(this.ei.input_s_qty) || 0);
            return total;
        }
    },
    methods:{
        setQty(){
            this.$emit("setQty", this.ei)
        },
        pressSetBtn(){
            const btn=document.getElementById("setBtn");
            if(btn) btn.click();
        },
        resetQty(){
            this.ei.input_l_qty="";
            this.ei.input_m_qty="";
            this.ei.input_s_qty="";
        }
    }
}
</script>

<style scoped>
.form-control:focus {
    box-shadow: none !important;
}
.card {
    transition: transform 0.2s, box-shadow 0.2s;
}
.card:focus-within {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.1) !important;
}
</style>