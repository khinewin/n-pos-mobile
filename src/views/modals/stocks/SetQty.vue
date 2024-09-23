<template lang="">
    <div>
         <!-- setQty Modal -->
         <div class="modal fade" id="setQtyModal" data-bs-backdrop="false"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2">                       
                        <div class="col-6">
                            <button id="saveQty" type="button" class="btn btn-sm" @click="saveQty" data-bs-dismiss="modal">
                                <span ><i class="fa-solid fa-hard-drive"></i> Save</span>
                            </button>
                        </div>
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered mb-0" v-if="Object.keys(ei).length">
                            <tbody>
                                <tr>
                                    <td colspan="2" class="text-center p-2" >{{ei.item_name}}</td>
                                </tr>
                                <tr>
                                    <!--<td class="text-center col-2">Main Inventory</td>-->
                                    <td class="text-center col-1">Mart Inv Bal</td>
                                    <td class="text-center col-1">Unit</td>
                                </tr>
                                <tr v-if="ei.packing_size.largest_unit">
                                   <!--<td class="p-0">
                                        <input @keypress.enter="pressSaveBtn" type="number" v-model="ei.main_l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>-->
                                    <td class="p-0">
                                        <input @keypress.enter="pressSaveBtn" type="number" v-model="ei.l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="ei.packing_size.largest_unit">
                                                <option selected>{{ei.packing_size.largest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <!--<td class="p-0">
                                        <input @keypress.enter="pressSaveBtn" type="number" v-model="ei.main_m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>-->
                                    <td class="p-0">
                                        <input @keypress.enter="pressSaveBtn" type="number" v-model="ei.m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="ei.packing_size.medium_unit">
                                                <option selected>{{ei.packing_size.medium_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <!--<td class="p-0">
                                        <input @keypress.enter="pressSaveBtn" type="number" v-model="ei.main_s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>-->
                                    <td class="p-0">
                                        <input @keypress.enter="pressSaveBtn" type="number" v-model="ei.s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="ei.packing_size.smallest_unit">
                                                <option selected>{{ei.packing_size.smallest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>                
            </div>
            </div>
        </div>
        <!--end setQty modal-->  
    </div>
</template>
<script>
export default {
props:['ei'],
methods:{
    saveQty(){
        this.$emit("saveQty")
    },
    pressSaveBtn(){
        const btn=document.getElementById("saveQty")
        btn.click();
    }
}
}
</script>
<style lang="">
    
</style>