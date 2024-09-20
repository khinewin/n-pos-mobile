<template lang="">
    <div>           
        <PageHeader page-title="Adjustment / New" page-code="addAdjustment" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveAdjustment" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Adjustments'}" class="btn btn-sm close"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+q)</router-link>
                </div>
            </div>
            <div class="card shadow">
                <div class="card-body p-2">
                    <form @submit.prevent="saveAdjustment">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4 fw-bold">Location :</td>
                                            <td class="col-8">
                                                <select v-model="location" class="form-control-plaintext py-0  border-bottom" :disabled="storeItems.length > 0">
                                                    <option>Main</option>
                                                    <option>Pharmacy</option>
                                                </select>
                                            </td>                                           
                                        </tr>        
                                        <tr>
                                            <td class="text-end col-4 fw-bold">Remark :</td>
                                            <td class="col-8">
                                             <input type="text" v-model="remark" class="form-control-plaintext py-0 border-bottom" placeholder="Remark">
                                            </td>                                           
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>                                          
                                            <td class="text-end col-2 fw-bold col-4">Voucher No. :</td>
                                            <td class="col-8">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext py-0  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td> 
                                        </tr>
                                        <tr>                                      
                                            <td class="col-2 text-end fw-bold">Date :</td>
                                            <td class="col-2">
                                                <flat-pickr class="form-control-plaintext py-0 border-bottom" v-model="created_at"  :config="config" />
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                       
                        <div class="vh-65 overflow-y-auto">
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2 change-cursor description"  data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i> Description (ctrl+i)</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1">Before Adj</td>
                                    <td class="col-1">Adj type</td>
                                    <td class="col-1 table-primary">Qty</td>      
                                    <td class="col-1">After Adj</td>   
                                    <td class="col-1">RTS/RFS</td>                          
                                    <td class="col-2">Remark</td>                                                                                       
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id"  @mouseenter="setEdit(i)">
                                    <td class="text-center position-relative ">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle ps-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class=" text-center">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.before_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td>
                                        <select v-model="st.adj_type" class="text-center form-control-plaintext py-0" @change="setAdjQty">
                                            <option>Increment</option>
                                            <option>Decrement</option>
                                        </select>
                                    </td>
                                    <td class="text-center  table-primary change-cursor" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>     
                                    <td class=" text-center">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>      
                                    <td class="">
                                        <select class="form-control-plaintext py-0 text-center" v-model="st.rts_rfs">
                                            <option>Yes</option>
                                            <option>No</option>
                                        </select>
                                    </td>                                                         
                                    <td class="p-0">
                                        <input  type="text" v-model="st.remark" class="form-control-plaintext py-0" placeholder="Remark">                                       
                                    </td>              
                                                                                            
                                </tr>         
                                                                                                                                      
                            </tbody>
                        </table> 
                        </div>                                                                                                                   
                </form>                        
                </div>
            </div>
        </div>
        <ProductModal @setCode="setCode" :storeItems="storeItems" />
        <setQtyModal @setQty="setAdjQty" :ei="editItem" />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import * as hp from "@/lib/helper"
import setQtyModal from "@/views/modals/setQtyModal.vue";
import FlatPickr from 'vue-flatpickr-component';
import * as hc from "@/lib/handleCtrl"

export default {
    components: {
        PageHeader, ProductModal,setQtyModal,FlatPickr
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
            config: {
                dateFormat: 'd/m/Y', // Flatpickr options
                enableTime: false,
                wrap: true, // set wrap to true only when using 'input-group'
               // altFormat: 'M j, Y',
            },   
            voucher_no:"", 
            created_at:"",         
            location: "Pharmacy",
            adj_item: {},
            storeItems: [],
            editItem: {},         
            remark:"",
           
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
    mounted() {           
        this.voucher_no=new Date().getTime();
        this.created_at=hp.setToday();
        this.checkDatabase();
        window.addEventListener('keydown', this.handleCtrl);

    },
    beforeUnmount() {
         window.removeEventListener('keydown', this.handleCtrl);
     },
    
    methods:{
        handleCtrl(event) {
          
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveAdjustment(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const des=document.querySelector(".close")
                des.click();
            }                 
            return hc.handleCtrl(event)
        },
        setAdjQty(){
            const e=this.editItem;
            if(this.location=="Pharmacy" && e.qty <=0 && e.adj_type=="Decrement"){
                    this.msg.info="Pharmacy inventory balance is not enough to decrement"
                    this.msg.type="error"
                    return false;
            }
            if(this.location=="Main" && e.main_qty <=0 && e.adj_type=="Decrement"){
                     this.msg.info="Main inventory balance is not enough to decrement"
                    this.msg.type="error"
                    return false;
            }

                let adj_qty=0;              
                adj_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                adj_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                adj_qty += e.input_s_qty ? e.input_s_qty : 0;
                e.adj_qty=adj_qty;
               return this.storeItems.map((f)=>{   
                    if(f.id===e.id){
                        f.adj_qty=adj_qty,
                        f.input_l_qty =e.input_l_qty ? e.input_l_qty : 0,
                        f.input_m_qty= e.input_m_qty ? e.input_m_qty : 0,
                        f.input_s_qty= e.input_s_qty  ? e.input_s_qty  : 0

                        e.after_adj_qty=f.adj_type=="Increment" ? e.before_adj_qty + e.adj_qty : e.before_adj_qty - e.adj_qty;

                        f.after_adj_qty=e.after_adj_qty;
                    }
               }) 
        },
        setEdit(i){
            const item=this.storeItems[i];
            this.editItem={...item,               
                input_l_qty: item.input_l_qty ? item.input_l_qty : 0,
                input_m_qty: item.input_m_qty ? item.input_m_qty : 0,
                input_s_qty: item.input_s_qty  ? item.input_s_qty  : 0
                
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
        async checkDatabase(){ 
            const query_url=query(collection(db, "adjustments"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {    
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                   // this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message(change.type, "adjustment") 
                                    this.resetAdjustments(); 
                                }
                            }
                    });                                              
            });                              
        } ,
       async saveAdjustment(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;
            this.isSaving=true;
            this.msg.type="";
            this.msg.info="";
            const created_at=hp.insertDate(this.created_at)

            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(created_at),    
                location: this.location,       
                products : this.storeItems,
                user : {uid: this.user.uid, displayName: this.user.displayName},          
                remark: this.remark,    
            }      
            const saveDocument=collection(db, "adjustments")
            await addDoc(saveDocument, voucher)                                
        },   
       resetAdjustments(){
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=hp.setToday();
            this.storeItems=[]
            this.adj_item=null
            this.editItem={}           
            this.remark="";
            this.v$.$reset()
       }, 
       updateProducts(){
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
