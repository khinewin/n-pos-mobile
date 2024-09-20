<template lang="">
    <div>
         <!-- set qty Modal -->
         <div class="modal fade" id="setQtyModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2">                       
                        <div class="col-4">
                            <button type="button" id="setBtn" class="btn btn-sm" @click="setQty" data-bs-dismiss="modal">
                                <span ><i class="fa-solid fa-hard-drive"></i> Save</span>
                            </button>
                        </div>
                        <div class="col-4" v-if="Object.keys(ei).length">
                            <button type="button" class="btn btn-sm" @click="resetQty" >
                                <span><i class="fa-solid fa-broom"></i>  Clear</span>
                            </button>
                        </div>
                        <div class="col-4">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                        </div>
                    </div>
                    <div v-if="ei">
                        <table class="table table-bordered mb-0" v-if="Object.keys(ei).length">
                            <tbody>
                                <tr>
                                    <td colspan="2" class="text-center p-2" >{{ei.item_name}}</td>
                                </tr>
                                <tr v-if="ei.packing_size.largest_unit">
                                    <td class="p-0 col-10">
                                        <input @keypress.enter="pressSetBtn" type="number" v-model="ei.input_l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="pt-2 col-2 text-center">
                                       {{ei.packing_size.largest_unit}}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetBtn" type="number" v-model="ei.input_m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="pt-2 text-center">
                                        {{ei.packing_size.medium_unit}}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetBtn" type="number" v-model="ei.input_s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="pt-2 text-center">
                                        {{ei.packing_size.smallest_unit}}
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>                
            </div>
            </div>
        </div>
        <!--end qty modal-->  
    </div>
</template>
<script>
export default {
    props:['ei'],

    methods:{
        setQty(){
           // if(!this.ei.input_l_qty && !this.ei.input_m_qty && !this.input_s_qty) return false;
            this.$emit("setQty", this.ei)
        },
        pressSetBtn(){
            const btn=document.getElementById("setBtn");
            btn.click();
        },
        resetQty(){
            this.ei.input_l_qty="";
            this.ei.input_m_qty="";
            this.ei.input_s_qty="";
        }
      
    }
}
</script>
<style lang="">
    
</style>