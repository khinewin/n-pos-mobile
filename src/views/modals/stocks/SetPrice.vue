<template>
    <!-- Set Prices Modal -->
    <div class="modal fade" id="setPricesModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="setPricesModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
                <!-- Premium Header -->
                <div class="modal-header bg-white border-bottom py-3 px-4">
                    <div class="d-flex align-items-center">
                        <div class="bg-primary bg-opacity-10 p-2 rounded-3 me-3">
                            <i class="fa-solid fa-tags text-primary fs-5"></i>
                        </div>
                        <div>
                            <h5 class="modal-title fw-bold mb-0" id="setPricesModalLabel">{{t('set_price') || 'Configure Pricing'}}</h5>
                            <p class="text-muted small mb-0" v-if="ei.item_name">{{ei.item_name}}</p>
                        </div>
                    </div>
                    <button id="close" type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-2 bg-light-subtle">
                    <div v-if="Object.keys(ei).length">
                        <!-- Pricing Grid -->
                        <div class="card border-0 shadow-sm rounded-3 mb-2 overflow-hidden">
                            <div class="table-responsive">
                                <table class="table table-borderless align-middle mb-0">
                                    <thead class="bg-white border-bottom">
                                        <tr class="small text-muted text-uppercase fw-bold">
                                            <th class="py-3 ps-4 border-0"></th>
                                            <th class="py-3 text-center border-0" v-if="ei.packing_size.largest_unit">{{ei.packing_size.largest_unit}}</th>
                                            <th class="py-3 text-center border-0" v-if="ei.packing_size.medium_unit">{{ei.packing_size.medium_unit}}</th>
                                            <th class="py-3 text-center border-0" v-if="ei.packing_size.smallest_unit">{{ei.packing_size.smallest_unit}}</th>
                                        </tr>
                                    </thead>
                                    <tbody class="bg-white">
                                        <!-- Purchase Price -->
                                        <tr class="border-bottom">
                                            <td class="py-2 ps-2">
                                                <div class="d-flex align-items-center">
                                                    <i class="fa-solid fa-cart-shopping text-secondary opacity-50 me-2"></i>
                                                    <span class="fw-medium text-dark">{{t('pur_price')}}</span>
                                                </div>
                                            </td>
                                            <td class="p-2" v-if="ei.packing_size.largest_unit">
                                                <input @keypress.enter="pressClose" @keyup="calPurPrice('largest_unit')" type="number" v-model="ei.pur_price.largest_unit" class="form-control form-control-sm text-center border-0 bg-light-subtle fw-bold" placeholder="0.00">
                                            </td>
                                            <td class="p-2" v-if="ei.packing_size.medium_unit">
                                                <input @keypress.enter="pressClose" @keyup="calPurPrice('medium_unit')" type="number" v-model="ei.pur_price.medium_unit" class="form-control form-control-sm text-center border-0 bg-light-subtle fw-bold" placeholder="0.00">
                                            </td>
                                            <td class="p-2" v-if="ei.packing_size.smallest_unit">
                                                <input @keypress.enter="pressClose" @keyup="calPurPrice('smallest_unit')" type="number" v-model="ei.pur_price.smallest_unit" class="form-control form-control-sm text-center border-0 bg-light-subtle fw-bold" placeholder="0.00">
                                            </td>
                                        </tr>
                                        <!-- Sale Price -->
                                        <tr class="border-bottom">
                                            <td class="py-2 ps-2">
                                                <div class="d-flex align-items-center">
                                                    <i class="fa-solid fa-sack-dollar text-success opacity-75 me-2"></i>
                                                    <span class="fw-medium text-dark">{{t('sale_price')}}</span>
                                                </div>
                                            </td>
                                            <td class="p-2" v-if="ei.packing_size.largest_unit">
                                                <input @keypress.enter="pressClose" @keyup="calRetailPrice('largest_unit')" type="number" v-model="ei.retail_price.largest_unit" class="form-control form-control-sm text-center border-0 bg-primary-subtle text-primary fw-bold" placeholder="0.00">
                                            </td>
                                            <td class="p-2" v-if="ei.packing_size.medium_unit">
                                                <input @keypress.enter="pressClose" @keyup="calRetailPrice('medium_unit')" type="number" v-model="ei.retail_price.medium_unit" class="form-control form-control-sm text-center border-0 bg-primary-subtle text-primary fw-bold" placeholder="0.00">
                                            </td>
                                            <td class="p-2" v-if="ei.packing_size.smallest_unit">
                                                <input @keypress.enter="pressClose" @keyup="calRetailPrice('smallest_unit')" type="number" v-model="ei.retail_price.smallest_unit" class="form-control form-control-sm text-center border-0 bg-primary-subtle text-primary fw-bold" placeholder="0.00">
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Auto Calculation Controls -->
                        <div class="p-2 bg-white rounded-3 shadow-sm border-start border-4 border-primary">
                            <div class="d-flex align-items-center justify-content-between">
                                <div class="d-flex align-items-center">
                                    <div class="bg-primary bg-opacity-10 p-2 rounded-circle me-3">
                                        <i class="fa-solid fa-calculator text-primary small"></i>
                                    </div>
                                    <div>
                                        <h6 class="fw-bold mb-0">Synchronize Unit Prices</h6>
                                        <p class="text-muted small mb-0">Automatically adjust other units when one is changed</p>
                                    </div>
                                </div>
                                <div class="form-check form-switch m-0">
                                    <input @change="saveAutoCal" v-model="ei.auto_cal" class="form-check-input" type="checkbox" role="switch" id="autoCalSwitch">
                                    <label class="form-check-label d-none" for="autoCalSwitch">{{t('auto')}}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal-footer bg-white border-top py-3 px-4">
                    <button type="button" class="btn btn-primary px-4 fw-bold shadow-sm" data-bs-dismiss="modal">
                        <i class="fa-solid fa-check me-2"></i>Apply Changes
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import * as cp from "@/lib/cal_price"

export default {
    props:['ei'],
    computed:{
        t() {
            return (key) => this.$store.getters.t(key);
        },
    },
    methods:{
        calRetailPrice(unit){
            return cp.calRetailPrice(unit, this.ei);
        },
        calWholesalePrice(unit){
            return cp.calWholesalePrice(unit, this.ei);
        },
        calPurPrice(unit){
            return cp.calPurPrice(unit, this.ei);
        },    
        pressClose(){
            const btn = document.getElementById("close")
            if (btn) btn.click();
        } ,
        saveAutoCal(){
            this.$emit("saveAutoCal")
        }  
    }
}
</script>

<style scoped>
.form-control:focus {
    box-shadow: none;
    background-color: #fff !important;
    outline: 2px solid #0d6efd;
}
.bg-light-subtle {
    background-color: #f8f9fa !important;
}
.bg-primary-subtle {
    background-color: #e7f1ff !important;
}
.form-switch .form-check-input {
    width: 3em;
    height: 1.5em;
    cursor: pointer;
}
</style>
