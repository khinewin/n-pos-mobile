<template lang="">
    <div>
        <!-- setPrices Modal -->
        <div class="modal fade" id="setPricesModal" data-bs-backdrop="false"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row mb-2">                                             
                        <div class="col-12 text-end">
                            <button id="close" type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered mb-0" v-if="Object.keys(ei).length">
                            <tbody>
                                <tr>
                                    <td colspan="4" class="text-center p-2" >{{ei.item_name}}</td>
                                </tr>
                                <tr class="fw-bold">
                                    <td class="col-1"></td>
                                    <td class="text-center col-1" v-if="ei.packing_size.largest_unit">
                                        <span >{{ei.packing_size.largest_unit}}</span>
                                    </td>
                                    <td class="text-center col-1" v-if="ei.packing_size.medium_unit">
                                        <span >{{ei.packing_size.medium_unit}}</span>
                                    </td>                                        
                                    <td class="text-center col-1"  v-if="ei.packing_size.smallest_unit">
                                        <span >{{ei.packing_size.smallest_unit}}</span>
                                    </td>
                                </tr>       
                                <tr class="">
                                    <td class="pt-2">Pur price</td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.largest_unit">
                                        <input @keypress.enter="pressClose" :disabled="!ei.packing_size.largest_unit" @keyup="calPurPrice('largest_unit')" type="number" v-model="ei.pur_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.medium_unit">
                                        <input @keypress.enter="pressClose" :disabled="!ei.packing_size.medium_unit"  @keyup="calPurPrice('medium_unit')"  type="number" v-model="ei.pur_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.smallest_unit">
                                            <input @keypress.enter="pressClose" :disabled="!ei.packing_size.smallest_unit" @keyup="calPurPrice('smallest_unit')" type="number" v-model="ei.pur_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                        
                                </tr>
                                <tr class="">
                                    <td class="pt-2">Retail price</td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.largest_unit">
                                        <input @keypress.enter="pressClose" :disabled="!ei.packing_size.largest_unit" @keyup="calRetailPrice('largest_unit')" type="number" v-model="ei.retail_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.medium_unit">
                                        <input @keypress.enter="pressClose" :disabled="!ei.packing_size.medium_unit"  @keyup="calRetailPrice('medium_unit')"  type="number" v-model="ei.retail_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.smallest_unit">
                                            <input @keypress.enter="pressClose" :disabled="!ei.packing_size.smallest_unit" @keyup="calRetailPrice('smallest_unit')" type="number" v-model="ei.retail_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                        
                                </tr>
                                <tr class="">
                                    <td class="pt-2">Wholesale price</td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.largest_unit">
                                        <input @keypress.enter="pressClose" :disabled="!ei.packing_size.largest_unit" @keyup="calWholesalePrice('largest_unit')" type="number" v-model="ei.whole_sale_price.largest_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.medium_unit">
                                        <input @keypress.enter="pressClose" :disabled="!ei.packing_size.medium_unit"  @keyup="calWholesalePrice('medium_unit')"  type="number" v-model="ei.whole_sale_price.medium_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="text-center p-0"  v-if="ei.packing_size.smallest_unit">
                                            <input @keypress.enter="pressClose" :disabled="!ei.packing_size.smallest_unit" @keyup="calWholesalePrice('smallest_unit')" type="number" v-model="ei.whole_sale_price.smallest_unit" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                        
                                </tr>  
                                <tr>
                                    <td class="ps-2 pt-2" colspan="4">
                                        <div class="form-check form-switch">
                                            <input @change="saveAutoCal" v-model="ei.auto_cal" class="form-check-input auto-cal" type="checkbox" role="switch" id="flexSwitchCheckChecked" checked>
                                            <label class="form-check-label" for="flexSwitchCheckChecked">Auto cal (shift+a)</label>
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
        <!--end setPrices modal--> 
    </div>
</template>
<script>
    import * as cp from "@/lib/cal_price"
export default {
    props:['ei'],
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
            const btn=document.getElementById("close")
            btn.click();
        } ,
        saveAutoCal(){
            this.$emit("saveAutoCal")
        }  
    }
}
</script>
<style lang="">
    
</style>