<template lang="">
    <div>           
        <PageHeader page-title="Transfer / New" page-code="addTransfer" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveTransfers" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'Transfers'}" class="btn btn-sm close"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+q)</router-link>
                </div>
            </div>
            <div class="card shadow min-vh-75">
                <div class="card-body p-2">
                    <form @submit.prevent="saveTransfers">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4">From location :</td>
                                            <td class="col-8">
                                                <select :disabled="storeItems.length > 0" class="form-control-plaintext py-0 border-bottom" v-model="from_location" @change="handleFromLocation(from_location)">
                                                    <option value="Main">Main</option>
                                                    <option value="Pharmacy">Pharmacy</option>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end">To location :</td>
                                            <td class="">
                                                <select :disabled="storeItems.length > 0" class="form-control-plaintext py-0 border-bottom" v-model="to_location" @change="handleToLocation(to_location)">
                                                    <option value="Main">Main</option>
                                                    <option value="Pharmacy">Pharmacy</option>
                                                </select>
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4">Voucher no. :</td>
                                            <td class="col-8">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext py-0  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end">Date :</td>
                                            <td class="">
                                                <flat-pickr class="form-control-plaintext py-0 border-bottom" v-model="created_at"  :config="config" />
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>                       
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary" >
                                    <td class="col-1">Code</td>
                                    <td class="col-2 change-cursor" id="description" data-bs-toggle="modal" data-bs-target="#searchCode" ><i class="fa-solid fa-circle-plus"></i> Description (ctrl+i)</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1">From Loc Bal</td> 
                                    <td class="col-1">To Loc Bal</td> 
                                    <td class="col-1 table-primary">Transfer Bal</td>
                                    <td class="col-1">After From Loc Bal</td> 
                                    <td class="col-1">After To Loc Bal</td> 
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id"  @mouseenter="setEdit(i)">
                                    <td class="text-center">
                                        <div class="position-relative">
                                            {{st.code_no}}                                           
                                            <a class="text-danger position-absolute top-50 start-0 translate-middle p-2" href="#!" @click="removeStoreItem(i)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                        </div>                                    
                                    </td>
                                    <td class="">{{st.item_name}}</td>                                   
                                    <td class="">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.from_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.to_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-center table-primary change-cursor" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.transfer_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_from_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                    <td class="text-end">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_to_loc_bal, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td>
                                                                                                                               
                                </tr>                                                                                                              
                            </tbody>
                        </table>                                                                                                                    
                </form>               
          
                </div>
            </div>
        </div>
        <ProductModal @setCode="setCode" :storeItems="storeItems" />
        <setQtyModal @setQty="setTransferQty" :ei="editItem" />

    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import * as ss from "@/lib/snapshot/products"
import * as hp from '@/lib/helper'
import setQtyModal from "@/views/modals/setQtyModal.vue";
import FlatPickr from 'vue-flatpickr-component';


export default {
    components: {
        PageHeader, ProductModal,setQtyModal, FlatPickr
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
            from_location:"Main",
            to_location:"Pharmacy", 
            editItem:{},
            transfer_item: {},
            storeItems: [],        
            isLoading:false,
            isSaving:false,
            isAddAllLoading:false,
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
            if (event.ctrlKey && event.key === 'h') {
                event.preventDefault(); // Prevent the default browser save dialog
               const p=document.querySelector(".close-product")
                p.click();
            }
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveTransfers(); // Call your custom function
            }
            if (event.ctrlKey && event.key === 'q') {
                event.preventDefault(); // Prevent the default browser save dialog
                const des=document.querySelector(".close")
                des.click();
            }         
          
            if (event.ctrlKey && event.key === 'i') {
                event.preventDefault(); // Prevent the default browser save dialog
                const des=document.getElementById("description")
                des.click();
            }
        },
        checkQty(p){
            const org_qty=this.from_location=="Main" ? p.main_qty : p.qty;
            if(org_qty  <1 ){
                this.msg.type="error";
                this.msg.info=`Insufficient ${this.from_location} inventory balance`
                return false;
            }          
            return true;
        },
        checkEditQty(){    
            let isSufficient=true;
            const e=this.editItem
            const org_qty=this.from_location=="Main" ? e.main_qty : e.qty;
                let trans_qty=0;
                trans_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                trans_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                trans_qty += e.input_s_qty ? e.input_s_qty : 0;

                let bal_qty_largest_unit=e.packing_size.largest_unit ? Math.floor(org_qty / (e.packing_size.medium_qty * e.packing_size.smallest_qty)) : 0;
                let bal_qty_medium_unit=e.packing_size.medium_unit ? Math.floor((org_qty % (e.packing_size.medium_qty * e.packing_size.smallest_qty)) / e.packing_size.smallest_qty): 0;
                let bal_qty_smallest_unit=org_qty;
                //console.log(e.input_l_qty)//has a trouble
                    if(trans_qty > org_qty){
                        this.msg.type="error";
                        this.msg.info="Maximum  inventory balance"
                        e.input_l_qty = bal_qty_largest_unit ? bal_qty_largest_unit : 0;
                        e.input_m_qty = bal_qty_largest_unit ? 0 : bal_qty_medium_unit ? bal_qty_medium_unit : 0;
                        e.input_s_qty = bal_qty_largest_unit || bal_qty_medium_unit ? 0 : bal_qty_smallest_unit ? bal_qty_smallest_unit : 0;
                        isSufficient=false;                        
                    }                                       
            if(!isSufficient) return false;
            return true;
        },
        setTransferQty(){
            const isCheck=this.checkEditQty();
            const e=this.editItem;           
                let transfer_qty=0;
                let after_from_loc_bal=0;
                let after_to_loc_bal=0;
                transfer_qty += e.input_l_qty ? e.input_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                transfer_qty += e.input_m_qty ? e.input_m_qty * e.packing_size.smallest_qty: 0;
                transfer_qty += e.input_s_qty ? e.input_s_qty : 0;
                e.transfer_qty=transfer_qty;

                after_from_loc_bal=e.from_loc_bal - e.transfer_qty;
                after_to_loc_bal = e.to_loc_bal + e.transfer_qty;
                e.after_from_loc_bal=after_from_loc_bal;
                e.after_to_loc_bal=after_to_loc_bal;

               return this.storeItems.map((f)=>{   
                    if(f.id===e.id){
                        f.transfer_qty=transfer_qty,
                        f.input_l_qty =e.input_l_qty ? e.input_l_qty : 0,
                        f.input_m_qty= e.input_m_qty ? e.input_m_qty : 0,
                        f.input_s_qty= e.input_s_qty  ? e.input_s_qty  : 0
                        f.after_from_loc_bal=e.after_from_loc_bal;
                        f.after_to_loc_bal=e.after_to_loc_bal;
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
        handleFromLocation(location){
           if(location=="Main"){
                this.from_location="Main";
                this.to_location="Pharmacy"
            } 
            if(location=="Pharmacy"){
                this.from_location="Pharmacy";
                this.to_location="Main"
            }           
      },
      handleToLocation(location){
        if(location=="Main"){
                this.from_location="Pharmacy";
                this.to_location="Main"
            } 
            if(location=="Pharmacy"){
                this.from_location="Main";
                this.to_location="Pharmacy"
            }     
      },
      
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.transfer_item)   
         },
        setCode(p){        
            const checkQty=this.checkQty(p);
            if(!checkQty) return false;   
            this.transfer_item={...p,
                 transfer_qty:0, 
                 from_loc_bal: this.from_location=="Main" ? p.main_qty : p.qty,
                 to_loc_bal : this.to_location=="Main" ? p.main_qty : p.qty,
                };
            const s=this.storeItems;
            let temStore=[]
            for(let i=0; i<s.length; i++){
                   temStore.push(s[i].id)
            }
            const result=temStore.indexOf(p.id)
            if(result < 0){
                this.storingItem();
            }
           
        },
        async checkDatabase(){ 
            const query_url=query(collection(db, "transfers"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {    
                            if(change.type==="added"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                    this.updateProducts();
                                   // this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message(change.type, "transfer")
                                    this.resetTransfer(); 
                                }
                            }
                    });                                              
            });                              
        } ,
       async saveTransfers(){
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
                products : this.storeItems,
                user : {uid: this.user.uid, displayName: this.user.displayName},
                from_location: this.from_location,
                to_location: this.to_location,                     
            }      
            const saveDocument=collection(db, "transfers")
            await addDoc(saveDocument, voucher)                                                   
        },
        resetTransfer(){
            this.isLoading=false;
            this.isSaving=false;
            this.voucher_no=new Date().getTime();
            this.created_at=hp.setToday();
            this.storeItems=[]
            this.transfer_item={}         
            this.v$.$reset()
        },
        updateProducts(){
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                if(this.from_location==="Main"){
                    await updateDoc(updateDocument, {
                        main_qty : increment(- p.transfer_qty),    
                        qty : increment(p.transfer_qty)                   
                 })  
                }
                if(this.from_location==="Pharmacy"){
                    await updateDoc(updateDocument, {
                        main_qty : increment(p.transfer_qty),    
                        qty : increment(-p.transfer_qty)                   
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
