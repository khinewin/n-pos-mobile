<template>
    <!-- Set Discount Modal -->
    <div class="modal fade" id="setDiscountModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="setDiscountModalLabel" aria-hidden="true" style="background-color: rgba(0,0,0,0.4);">
        <div class="modal-dialog modal-dialog-centered modal-sm">
            <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">               
                <!-- Header -->
                <div class="modal-header bg-primary text-white border-0 py-2">
                    <h6 class="modal-title fw-bold" id="setDiscountModalLabel">
                        <i class="fa-solid fa-percent me-2"></i>Set Discount
                    </h6>
                    <button type="button" class="btn-close btn-close-white shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-2 bg-light">
                    <!-- Item Info Alert -->
                    <div v-if="ei && Object.keys(ei).length" class="alert alert-primary border-0 shadow-sm rounded-3 py-2 px-3 mb-2 d-flex align-items-center">
                        <i class="fa-solid fa-box fa-lg me-3"></i>
                        <div class="flex-grow-1 overflow-hidden">
                            <div class="small text-uppercase opacity-75 fw-bold text-nowrap" style="font-size: 0.65rem;">Current Product</div>
                            <div class="fw-bold fs-6 text-truncate">{{ei.item_name}}</div>
                        </div>
                    </div>

                    <div v-if="ei && Object.keys(ei).length && ei.amount">
                        <!-- Discount Methods Cards -->
                        <div class="d-flex flex-column gap-2 mb-2">
                            <!-- Unit Based Discount -->
                            <div class="card border-0 shadow-sm rounded-3">
                                <div class="card-body p-2">
                                    <div class="d-flex align-items-center gap-2 mb-1">
                                        <div class="bg-primary-subtle text-primary rounded-3 p-1 d-flex align-items-center justify-content-center" style="width: 32px; height: 32px;">
                                            <i class="fa-solid fa-gift small"></i>
                                        </div>
                                        <label class="small text-muted fw-bold text-uppercase mb-0" style="font-size: 0.65rem;">Free Unit Discount</label>
                                    </div>
                                    <div class="input-group input-group-sm">
                                        <select @change="calDiscount" class="form-select border-0 bg-light fw-medium px-2" v-model="ei.dis_qty_unit" style="border-radius: 8px 0 0 8px;">
                                            <option v-for="pu in onlyString(ei.packing_size)" :key="pu">{{pu}}</option>
                                        </select>
                                        <input 
                                            @keypress.enter="pressSetDiscountBtn" 
                                            :disabled="!ei.dis_qty_unit" 
                                            type="number" 
                                            @change="calDiscount" 
                                            @keyup="calDiscount" 
                                            v-model="ei.dis_qty" 
                                            class="form-control border-0 bg-light text-center fw-bold" 
                                            id="dis_qty"
                                            placeholder="Qty"
                                            style="border-radius: 0 8px 8px 0;">
                                    </div>
                                </div>
                            </div>

                            <div class="d-flex gap-2">
                                <!-- Percentage Discount -->
                                <div class="card border-0 shadow-sm rounded-3 flex-fill">
                                    <div class="card-body p-2">
                                        <div class="d-flex align-items-center gap-2 mb-1">
                                            <div class="bg-info-subtle text-info rounded-3 p-1 d-flex align-items-center justify-content-center" style="width: 32px; height: 32px;">
                                                <i class="fa-solid fa-percent small"></i>
                                            </div>
                                            <label class="small text-muted fw-bold text-uppercase mb-0" style="font-size: 0.62rem;">Percent</label>
                                        </div>
                                        <input 
                                            @keypress.enter="pressSetDiscountBtn" 
                                            @keyup="calDiscount" 
                                            @change="calDiscount" 
                                            type="number" 
                                            v-model="ei.dis_percent" 
                                            class="form-control form-control-sm border-0 bg-light text-center fw-bold fs-6 shadow-none py-1" 
                                            id="dis_percent"
                                            placeholder="0%"
                                            style="border-radius: 8px;">
                                    </div>
                                </div>

                                <!-- Amount Discount -->
                                <div class="card border-0 shadow-sm rounded-3 flex-fill">
                                    <div class="card-body p-2">
                                        <div class="d-flex align-items-center gap-2 mb-1">
                                            <div class="bg-success-subtle text-success rounded-3 p-1 d-flex align-items-center justify-content-center" style="width: 32px; height: 32px;">
                                                <i class="fa-solid fa-money-bill-1 small"></i>
                                            </div>
                                            <label class="small text-muted fw-bold text-uppercase mb-0" style="font-size: 0.62rem;">Amount</label>
                                        </div>
                                        <input 
                                            @keypress.enter="pressSetDiscountBtn" 
                                            @keyup="calDiscount" 
                                            @change="calDiscount" 
                                            type="number" 
                                            v-model="ei.dis_amount" 
                                            class="form-control form-control-sm border-0 bg-light text-center fw-bold fs-6 shadow-none py-1" 
                                            id="dis_amount"
                                            placeholder="Ks"
                                            style="border-radius: 8px;">
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Total Discount Preview -->
                        <div class="bg-white rounded-3 p-2 shadow-sm mb-2 text-center border border-primary-subtle">
                            <div class="small text-muted text-uppercase fw-bold mb-0" style="font-size: 0.65rem;">Calculated Discount</div>
                            <div class="h3 fw-bolder text-primary mb-0">{{ei.discount || 0}}</div>
                            <div class="small fw-medium text-muted" style="font-size: 0.75rem;">Kyats</div>
                        </div>

                        <!-- Action Buttons -->
                        <div class="d-flex flex-column gap-2">
                            <button type="button" id="setDiscountBtn" class="btn btn-primary py-2 fw-bold shadow-sm rounded-3" @click="saveDiscount" data-bs-dismiss="modal">
                                <i class="fa-solid fa-hard-drive me-2"></i> Save Discount
                            </button>
                            <div class="d-flex gap-2">
                                <button type="button" class="btn btn-light flex-fill py-2 small fw-bold text-muted rounded-3" @click="clearDiscount">
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
    methods:{
        pressSetDiscountBtn(){
            const btn=document.getElementById("setDiscountBtn");
            if(btn) btn.click();
        },
        saveDiscount(){
            if(this.ei.discount === undefined) return false;
            this.$emit("saveDiscount", this.ei)
        },
        clearDiscount(){
            this.ei.dis_amount="";
            this.ei.dis_qty="";
            this.ei.dis_percent="";
            this.ei.dis_qty_unit="";
            this.ei.discount=0;           
        },
        
        calDiscount(ev){
            const e=this.ei;
            const targetId = ev.target ? ev.target.id : "";

            if(targetId == "dis_qty"){
                e.dis_amount="";
                e.dis_percent="";
            }
            if(targetId == "dis_percent" || ev.target && ev.target.id == "dis_percent"){
                e.dis_amount="";
                e.dis_qty="";
            }
            if(targetId == "dis_amount" || ev.target && ev.target.id == "dis_amount"){
                e.dis_qty="";
                e.dis_percent="";
            }

            if(e.dis_amount){
                e.discount = Number(e.dis_amount);
            } else if(e.dis_percent){
                e.discount = (Number(e.amount) * Number(e.dis_percent)) / 100;
            } else if(e.dis_qty_unit && e.dis_qty){      
                const arr = Object.entries(e.packing_size)               
                const d_units = arr.filter((ft) => ft[1] == e.dis_qty_unit)  
                if(d_units.length > 0) {
                    const dis_unit = d_units[0][0]  
                    let discount = 0;
                    const qty = Number(e.dis_qty);

                    if(e.sale_type === undefined){
                        if(dis_unit == "largest_unit") discount = e.pur_price.largest_unit * qty
                        else if(dis_unit == "medium_unit") discount = e.pur_price.medium_unit * qty
                        else if(dis_unit == "smallest_unit") discount = e.pur_price.smallest_unit * qty
                    } else if(e.sale_type == "retail"){
                        if(dis_unit == "largest_unit") discount = e.retail_price.largest_unit * qty
                        else if(dis_unit == "medium_unit") discount = e.retail_price.medium_unit * qty
                        else if(dis_unit == "smallest_unit") discount = e.retail_price.smallest_unit * qty
                    } else if(e.sale_type == "whole_sale"){
                        if(dis_unit == "largest_unit") discount = e.whole_sale_price.largest_unit * qty
                        else if(dis_unit == "medium_unit") discount = e.whole_sale_price.medium_unit * qty
                        else if(dis_unit == "smallest_unit") discount = e.whole_sale_price.smallest_unit * qty
                    }
                    e.discount = discount;
                }
            } else {
                e.discount = 0;
            }
        },      

        onlyString(p){
           if(p.largest_unit){
            return {
                largest_unit : p.largest_unit,
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
           }
           return {
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
        },
    }
}
</script>

<style scoped>
.form-control:focus, .form-select:focus {
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