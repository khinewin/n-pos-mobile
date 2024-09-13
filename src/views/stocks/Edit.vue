<template lang="">
    <div>           
        <PageHeader page-title="Stocks / Edit" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid">
            <div class="row mb-2 justify-content-end">                
                <div class="col-sm-2 d-grid">
                        <button type="button" @click="saveStocks" class="btn btn-sm" :disabled="isLoading">
                            <span v-if="!isLoading"><i class="fa-solid fa-hard-drive"></i> Save</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                        </button>
                </div>
                <div class="col-sm-2 d-grid">
                        <router-link :to="{name : 'OpeningStocks'}" class="btn btn-sm"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
            <div class="card shadow min-vh-75">
                <div class="card-body p-2">
                    <form @submit.prevent="saveStocks">
                        <div class="row justify-content-end mb-2">
                            <div class="col-sm-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody>
                                        <tr>
                                            <td class="text-end col-4">Voucher no. :</td>
                                            <td class="col-8">
                                                <input type="text" :class="{'border-0 border-bottom border-danger' : v$.voucher_no.$errors.length > 0}"  v-model="voucher_no" class="form-control-plaintext p-0  border-bottom" id="voucher_no" placeholder="Voucher no">                                                
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="text-end">Date :</td>
                                            <td class="">
                                                <flat-pickr class="form-control-plaintext p-0 border-bottom" v-model="created_at"  :config="config" />
                                            </td>
                                        </tr>                                       
                                    </tbody>
                              </table>
                            </div>
                        </div>
                        <table class="table table-bordered  mb-0 table-hover">
                            <tbody>
                                <tr class="text-center fw-bold table-secondary">
                                    <td class="col-1">Code</td>
                                    <td class="col-2" data-bs-toggle="modal" data-bs-target="#searchCode" style="cursor: context-menu;"><i class="fa-solid fa-circle-plus"></i> Description</td>
                                    <td class="col-1">Packing size</td>
                                    <td class="col-1 table-info">Main Inv Bal</td>     
                                    <td class="col-1 table-info">Pharmacy Inv Bal</td>   
                                    <td class="col-1 table-primary">Pur price</td>
                                    <td class="col-1 table-primary">Retail price</td>
                                    <td class="col-1 table-primary">Wholesale price</td>                                  
                                </tr>
                                <tr class="" v-if="storeItems.length > 0" v-for="(st, i) in storeItems" :key="st.id" @mouseenter="setEdit(i)" >
                                    <td class="text-center">
                                        <div class="position-relative">
                                            {{st.code_no}}                                           
                                        </div>                                    
                                    </td>
                                    <td class="">{{st.item_name}}</td>
                                    <td class="">
                                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                                    </td>
                                    <td class=" text-end table-info" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.main_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td> 
                                    <td class=" text-end table-info" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                                    </td> 
                                    <td class="text-end table-primary" data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                        <span v-if="st.packing_size.largest_unit"> {{st.pur_price.largest_unit}}' {{st.packing_size.largest_unit}}</span>
                                        <span v-else>{{st.pur_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>       
                                    <td class="text-end table-primary " data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                        <span v-if="st.packing_size.largest_unit">{{st.retail_price.largest_unit}}' {{st.packing_size.largest_unit}} </span>
                                        <span v-else>{{st.retail_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>
                                    <td class="text-end table-primary" data-bs-toggle="modal" data-bs-target="#setPricesModal">
                                        <span v-if="st.packing_size.largest_unit">{{st.whole_sale_price.largest_unit}}' {{st.packing_size.largest_unit}} </span>
                                        <span v-else>{{st.whole_sale_price.medium_unit}}' {{st.packing_size.medium_unit}}</span>
                                    </td>                                                                                    
                                </tr>                                                                                                                
                            </tbody>
                        </table>                                                                                                                    
                    </form>               
          
                </div>
            </div>
        </div>
        <ProductModal @setCode="setCode" :storePurchase="storeItems" />
        <SetQty :ei="editItem" @saveQty="saveQty" />
        <SetPrice :ei="editItem" />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import ProductModal from '@/views/modals/purchase/ProductModal.vue'
import SetQty from '@/views/modals/stocks/SetQty.vue'
import SetPrice from '@/views/modals/stocks/SetPrice.vue'
import * as ss from "@/lib/snapshot/stocks"
import * as hp from '@/lib/helper';
import * as cp from '@/lib/cal_price'
import FlatPickr from 'vue-flatpickr-component';


export default {
    components: {
        PageHeader, ProductModal,SetQty,SetPrice, FlatPickr
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
            id:"",
            voucher_no:"", 
            created_at:"",         
            stock_item: {},
            storeItems: [],
            editItem: {},        
          
            isLoading:false,
            msg : {
                type :"",
                info :"",
            }
        }
    },
    mounted() {
        this.voucher_no=this.$route.params.id;
        this.fetchStock();
        this.checkDatabase();
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
   
    methods:{

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
        async saveAutoCal(){
        const e=this.editItem;
        this.storeItems.map((p)=>{
            if(p.id==e.id){
                p.auto_cal = e.auto_cal;
            }
        })
        },
        saveQty(){
            const e=this.editItem;
                let main_qty=0;
                main_qty += e.main_l_qty ? e.main_l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                main_qty += e.main_m_qty ? e.main_m_qty * e.packing_size.smallest_qty: 0;
                main_qty += e.main_s_qty ? e.main_s_qty : 0;
                let qty=0;
                qty += e.l_qty ? e.l_qty * (e.packing_size.medium_qty* e.packing_size.smallest_qty) : 0;
                qty += e.m_qty ? e.m_qty * e.packing_size.smallest_qty: 0;
                qty += e.s_qty ? e.s_qty : 0;
               return this.storeItems.map((f)=>{   
                    if(f.id===e.id){
                        f.main_qty=main_qty,
                        f.qty=qty                 
                    }
               }) 
        },
     
        setEdit(i){
            let main_l=0, main_m=0, main_s=0, l=0, m=0, s=0;

            const item=this.storeItems[i];
            if(item.packing_size.largest_unit){
                 main_l=Math.floor(item.main_qty / (item.packing_size.medium_qty * item.packing_size.smallest_qty));
                 main_m=Math.floor((item.main_qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) / item.packing_size.smallest_qty);
                 main_s=Math.floor((item.main_qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) % item.packing_size.smallest_qty);
                 l=Math.floor(item.qty / (item.packing_size.medium_qty * item.packing_size.smallest_qty));
                 m=Math.floor((item.qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) / item.packing_size.smallest_qty);
                 s=Math.floor((item.qty % (item.packing_size.medium_qty * item.packing_size.smallest_qty)) % item.packing_size.smallest_qty);
            }else{
                 main_m=Math.floor(item.main_qty / item.packing_size.smallest_qty);
                 main_s=Math.floor(item.main_qty % item.packing_size.smallest_qty);
                 m=Math.floor(item.qty / item.packing_size.smallest_qty);
                 s=Math.floor(item.qty % item.packing_size.smallest_qty);
            }
            this.editItem={...item,
                 main_l_qty :main_l ? main_l : 0,
                 main_m_qty: main_m ? main_m: 0,
                 main_s_qty: main_s ? main_s : 0,
                 l_qty: l ? l : 0, 
                 m_qty:m ? m :0,
                 s_qty: s ? s :0                 
                };

        },       
     
        setItem(item){
            this.id=item.id;
            this.voucher_no=item.voucher_no;
            this.created_at=hp.setOldDate(item.created_at)
            this.storeItems=item.products;         
        },
        async fetchStock(){
            const query_url = query(collection(db, "openingStocks"), where("voucher_no", "==", Number(this.voucher_no)));
            onSnapshot(query_url, (snapShot)=>{
                const stocks=ss.stocks(snapShot);
                this.setItem(stocks[0])
                this.isLoading=false;
            })
        },
            
      
        removeStoreItem(i){
            this.storeItems.splice(i, 1)
        },
        storingItem(){           
            this.storeItems.push(this.stock_item)   
         },
        setCode(p){           
            this.stock_item=p;
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
            const query_url=query(collection(db, "openingStocks"));           
                onSnapshot(query_url, async(snapShot) =>{
                    snapShot.docChanges().forEach((change) => {    
                            if(change.type==="modified"){
                                if(change.doc.data().voucher_no==this.voucher_no){
                                   // this.printSale();
                                    this.msg.type="success";
                                    this.msg.info=hp.message(change.type, "opening stock")    
                                    this.isLoading=false;
                                }
                            }
                    });                                              
            });                              
        } ,
       async saveStocks(){
          const validattion = await this.v$.$validate()
          if(!validattion) return false;
            this.isLoading=true;       
            
            this.updateProducts();
            
            const created_at=hp.insertDateNoTime(this.created_at)
            const voucher={
                voucher_no: this.voucher_no,
                created_at: Timestamp.fromDate(created_at),           
                products : this.storeItems,
                user : {uid: this.user.uid, displayName:this.user.displayName},          
            }     
            this.isLoading=false;
            const saveDocument=doc(db, "openingStocks", this.id)
            await updateDoc(saveDocument, voucher)                     
        },
        updateProducts(){
            this.storeItems.map(async (p)=>{
                const id=p.id;
                const updateDocument=doc(db, "products", id)
                await updateDoc(updateDocument, {
                        qty : p.qty,
                        main_qty: p.main_qty,
                        retail_price: p.retail_price,
                        whole_sale_price: p.whole_sale_price,
                        pur_price: p.pur_price,
                        auto_cal: p.auto_cal,
                 })    
            })
        },
       
     
        showEditTime(t){
            const date=t.toDate()
          //  const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Date(date).toISOString().substr(0, 10)
            return formattedDate;
        },
                    
    }
}
</script>
<style lang="">
    
</style>
