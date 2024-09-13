<template lang="">
    <div>
           <!-- discount price Modal -->
           <div class="modal fade" id="setDiscountModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2 justify-content-end" >     
                        <div class="col-4" v-if="Object.keys(ei).length">
                            <button v-if="ei.amount" type="button" id="setDiscountBtn" @click="saveDiscount" class="btn btn-sm"  data-bs-dismiss="modal">
                                <span><i class="fa-solid fa-hard-drive"></i> Save</span>
                            </button>
                        </div>                  
                        <div class="col-4" v-if="Object.keys(ei).length">
                            <button type="button" class="btn btn-sm" @click="clearDiscount" v-if="ei.amount">
                                <span><i class="fa-solid fa-broom"></i>  Clear</span>
                            </button>
                        </div>
                        <div class="col-4">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                        </div>
                    </div>
                    <div v-if="ei">
                        <table class="table table-bordered" v-if="Object.keys(ei).length">
                            <tbody v-if="ei.amount">
                                <tr>
                                    <td colspan="2" class="text-center p-2" >{{ei.item_name}}</td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <select @change="calDiscount" class="form-control-plaintext form-control-sm text-center" v-model="ei.dis_qty_unit">
                                                <option  v-for="pu in onlyString(ei.packing_size)">{{pu}}</option>
                                        </select>
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetDiscountBtn" :disabled="!ei.dis_qty_unit" type="number" @change="calDiscount" @keyup="calDiscount" v-model="ei.dis_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="pt-2 text-center">
                                        %
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetDiscountBtn" @keyup="calDiscount" @change="calDiscount" type="number" v-model="ei.dis_percent" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="pt-2 text-center">
                                        $
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="pressSetDiscountBtn" @keyup="calDiscount" @change="calDiscount" type="number" v-model="ei.dis_amount" class="form-control-plaintext form-control-sm text-center">
                                    </td>                                    
                                </tr>
                                <tr>
                                    <td class="p-1 text-end">Discount</td>
                                    <td  class="p-1 text-center">{{ei.discount}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>                
            </div>
            </div>
        </div>
        <!--end discount modal-->  
    </div>
</template>
<script>
export default {
    props:['ei'],
    methods:{
        pressSetDiscountBtn(){
            const btn=document.getElementById("setDiscountBtn");
            btn.click();
        },
        saveDiscount(){
            if(this.ei.discount== undefined) return false;
            this.$emit("saveDiscount", this.ei)
        },
        clearDiscount(){
            this.ei.dis_amount="";
            this.ei.dis_qty="";
            this.ei.dis_percent="";
            this.ei.dis_qty_unit="";
            const e=this.ei;
            e.discount=0;           
        },
        
        calDiscount(ev){
            const e=this.ei;
            if(e.dis_amount){
                        e.discount=e.dis_amount;
                    }else if(e.dis_percent){
                        e.discount=(e.amount * e.dis_percent) /100;
                    }else if(e.dis_qty_unit && e.dis_qty){      
                        const arr=Object.entries(e.packing_size)               
                        const d_units=arr.filter((ft)=>{
                            return ft[1]==e.dis_qty_unit;
                        })  
                       const dis_unit=d_units[0][0]  
                       let discount=0;
                       if(dis_unit=="largest_unit"){
                           discount=e.pur_price.largest_unit * e.dis_qty
                       }else if(dis_unit=="medium_unit"){
                            discount=e.pur_price.medium_unit * e.dis_qty;
                       }else if(dis_unit=="smallest_unit"){
                            discount=e.pur_price.smallest_unit * e.dis_qty;
                       }    
                       e.discount=discount;                        
                    }
                    else{
                        e.discount=0;
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
           if(!p.largest_unit){
            return {
                 medium_unit: p.medium_unit, 
                 smallest_unit:p.smallest_unit
                }
           }
        },
    }
    
}
</script>
<style lang="">
    
</style>