<template lang="">
    <div>           
        <PageHeader page-title="New adjustment" page-code="addAdjustment" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveAdjustment" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Adjustments'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="saveAdjustment">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-12">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-2 py-1">Location :</td>
                                            <td class="col-2 py-0">
                                                <select v-model="location" class="form-control-plaintext form-control-sm  border-bottom" :disabled="storeItems.length > 0">
                                                    <option>Main</option>
                                                    <option>Pharmacy</option>
                                                </select>
                                            </td>
                                            <td class="text-end col-2 py-1">Voucher no. :</td>
                                            <td class="col-2 py-0">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext form-control-sm  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>                                       
                                            <td class="col-2 text-end py-1">Date :</td>
                                            <td class="col-2 py-0">
                                                <input type="date" :class="{'border-0 border-bottom border-danger' : v$.created_at.$errors.length > 0}"  v-model="created_at" class="form-control-plaintext form-control-sm border-bottom" id="created_at" placeholder="Date">                                                
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                       
                        <div class="vh-60 overflow-y-auto">
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2" data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i> Description</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1">Before Adj</td>
                                    <td class="col-1">Adj type</td>
                                    <td class="col-1">Qty</td>      
                                    <td class="col-1">After Adj</td>   
                                    <td class="col-1">RTS/RFS</td>                          
                                    <td class="col-2">Remark</td>                                                                                       
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id"  @mouseenter="setEdit(i)">
                                    <td class="text-center position-relative pt-2">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                    </td>
                                    <td class="pt-2">{{st.item_name}}</td>
                                    <td class="pt-2">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class="pt-2 text-center">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.before_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td>
                                        <select v-model="st.adj_type" class="text-center form-control-plaintext form-control-sm" @change="setAdjQty">
                                            <option>Increment</option>
                                            <option>Decrement</option>
                                        </select>
                                    </td>
                                    <td class="text-center pt-2" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>     
                                    <td class="pt-2 text-center">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>      
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="st.rts_rfs">
                                            <option>Yes</option>
                                            <option>No</option>
                                        </select>
                                    </td>                                                         
                                    <td class="p-0">
                                        <input  type="text" v-model="st.remark" class="form-control-plaintext form-control-sm form-control-sm text-center" placeholder="Remark">                                       
                                    </td>              
                                                                                            
                                </tr>         
                                                                                                                                      
                            </tbody>
                        </table> 
                        </div>                                                                                                                   
                </form>                        
            <ProductModal @setCode="setCode" :storePurchase="storeItems" />
        </div>
        </div>
        </div>
        <!-- set qty Modal -->
        <div class="modal fade" id="setQtyModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
            <div class="modal-content">               
                <div class="modal-body p-2">
                    <div class="row text-end mb-2">                       
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" @click="setAdjQty" data-bs-dismiss="modal">
                                <span ><i class="fa-solid fa-hard-drive"></i> Save</span>
                            </button>
                        </div>
                        <div class="col-6">
                            <button type="button" class="btn btn-sm" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                        </div>
                    </div>
                    <div>
                        <table class="table table-bordered mb-0" v-if="Object.keys(editItem).length">
                            <tbody>
                                <tr>
                                    <td colspan="2" class="text-center p-2" >{{editItem.item_name}}</td>
                                </tr>
                                <tr v-if="editItem.packing_size.largest_unit">
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.adj_l_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.adj_l_unit">
                                                <option selected>{{editItem.packing_size.largest_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.adj_m_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.adj_m_unit">
                                                <option selected>{{editItem.packing_size.medium_unit}}</option>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="p-0">
                                        <input type="number" v-model="editItem.adj_s_qty" class="form-control-plaintext form-control-sm text-center">
                                    </td>
                                    <td class="p-0">
                                        <select class="form-control-plaintext form-control-sm text-center" v-model="editItem.adj_s_unit">
                                                <option selected>{{editItem.packing_size.smallest_unit}}</option>
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
        <!--end qty modal-->  
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import * as hp from "@/lib/helper"

export default {
    components: {
        PageHeader, ProductModal
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                voucher_no: {required},             
                created_at: {required},              
                storeItems : {required}
            }
    },
    data(){
        return{       
            voucher_no:"", 
            created_at:"",         
            location: "Pharmacy",
            adj_item: {},
            storeItems: [],
            editItem: {},          
            total_retail_amount:0,
            total_pur_amount:0,       
            total_whole_sale_amount:0, 
            isLoading:false,
            isSaving:false,
            msg : {
                type :"",
                info :"",
            }
        }
    },
  
    computed:{
        user(){
            return this.$store.getters.user;
        },    
      
    },
    watch:{
        msg:{
               handler(val){
                   if(val.type){
                       setTimeout(()=>{
                           this.msg.type="";
                           this.msg.info="";
                       }, 3000)
                   }
               },deep:true
           }
    },
    created() {     
      
        this.voucher_no=new Date().getTime();
        this.created_at=new Date().toISOString().substr(0, 10)
    },
    
    methods:{
        setAdjQty(){
            const e=this.editItem;
                let adj_qty=0;              
                adj_qty += e.adj_l_qty ? e.adj_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                adj_qty += e.adj_m_qty ? e.adj_m_qty * e.packing_size.smallest_qty: 0;
                adj_qty += e.adj_s_qty ? e.adj_s_qty : 0;
                e.adj_qty=adj_qty;
               return this.storeItems.map((f)=>{   
                    if(f.id===e.id){
                        f.adj_qty=adj_qty,
                        f.adj_l_qty =e.adj_l_qty ? e.adj_l_qty : 0,
                        f.adj_m_qty= e.adj_m_qty ? e.adj_m_qty : 0,
                        f.adj_s_qty= e.adj_s_qty  ? e.adj_s_qty  : 0

                        e.after_adj_qty=f.adj_type=="Increment" ? e.before_adj_qty + e.adj_qty : e.before_adj_qty - e.adj_qty;

                        f.after_adj_qty=e.after_adj_qty;
                    }
               }) 
        },
        setEdit(i){
            const item=this.storeItems[i];
            this.editItem={...item, 
                adj_l_unit: item.packing_size.largest_unit,
                adj_m_unit: item.packing_size.medium_unit,
                adj_s_unit: item.packing_size.smallest_unit,
                adj_l_qty: item.adj_l_qty ? item.adj_l_qty : 0,
                adj_m_qty: item.adj_m_qty ? item.adj_m_qty : 0,
                adj_s_qty: item.adj_s_qty  ? item.adj_s_qty  : 0
                
            };
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
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
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.adj_item)   
         },
        setCode(p){           
            this.adj_item={...p, before_adj_qty: this.location=='Main' ? p.main_qty : p.qty, adj_qty:0, after_adj_qty : 0, rts_rfs: "No" , adj_type: "Decrement"};
            const s=this.storeItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }
            this.editItem=p;
        },
       async saveAdjustment(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.isSaving=true;
            this.msg.type="";
            this.msg.info="";
        
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(new Date(this.created_at)),    
                location: this.location,       
                products : this.storeItems,
                user : {uid: this.user.uid, email: this.user.email},
                total_retail_amount:this.total_retail_amount,
                total_pur_amount:this.total_pur_amount,         
                total_whole_sale_amount: this.total_whole_sale_amount,
            }      
            const saveDocument=collection(db, "adjustments")
            await addDoc(saveDocument, voucher)
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                if(this.location==="Main"){
                    await updateDoc(updateDocument, {
                        main_qty : p.adj_type=="Decrement" ? increment(- p.adj_qty) : increment(p.adj_qty) ,                       
                 }) 
                }else{
                    await updateDoc(updateDocument, {
                        qty : p.adj_type=="Decrement" ? increment(- p.adj_qty) : increment(p.adj_qty) ,                       
                 }) 
                } 
            })
            this.msg.type="success";
            this.msg.info="Adjustment save successful."          
             
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=new Date().toISOString().substr(0, 10)         
            this.storeItems=[]
            this.adj_item=null
            this.editItem={}
            this.total_retail_amount=0
            this.total_pur_amout=0;
            this.v$.$reset()
        },   
       
        
       showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
                    
    }
}
</script>
<style lang="">
    
</style>
