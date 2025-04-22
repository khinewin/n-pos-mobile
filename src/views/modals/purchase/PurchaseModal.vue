<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="purchaseModal" data-bs-backdrop="false"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-fullscreen">
         <div class="modal-content">               
           <div>
               <div class="card border-0">
                   <div class="card-body p-2">
                    <div class="row g-2">
                        <div class="col-lg-3">                                                         
                                <div class="card shadow-sm">
                                    <div class="card-body p-2">
                                       <div class="row g-1 small">
                                      
                                        <div class="col-6 col-sm-3 col-lg-12">
                                            <div class="input-group">
                                                <div class="form-floating">
                                                    <flat-pickr  class="form-control-plaintext border-bottom" v-model="search_by_from_pur_date" id="from_date" :config="config" />
                                                    <label for="from_date">{{from_date_link}}</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-6 col-sm-3 col-lg-12">
                                            <div class="input-group">
                                                <div class="form-floating">
                                                    <flat-pickr  class="form-control-plaintext border-bottom" v-model="search_by_to_pur_date" id="to_date" :config="config" />
                                                    <label for="to_date">{{to_date_link}}</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-6 col-sm-3 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchPurchase" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="search_by_voucher_no" class="">{{voucher_no_link}}</label>
                                            </div>
                                        </div>
                                        <div class="col-6 col-sm-3 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" v-model="search_by_supplier" @input="inputSearchSupplier" placeholder="Supplier" class="form-control-plaintext border-bottom">
                                                <label for="search_by_supplier" class="">{{supplier_link}}</label>
                                            </div>
                                        </div>
                                        
                                       </div>
                                
                                <div class="row mt-2">
                                    <div class="col-4 d-grid">
                                        <button class="btn btn-sm text-dark clear-search-product" @click="fetchPurchase"><i class="fa-solid fa-search"></i> {{search_link}}</button>
                                    </div>
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark clear-search-purchase" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{clear_link}}</button>
                                        </div>
                                        <!--<div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="updatePurchases">
                                                <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                               <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                            </button>
                                        </div>-->
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark close-purchase" data-bs-dismiss="modal" aria-label="Close" ><i class="fa-solid fa-circle-xmark"></i> {{close_link}} </button>
                                        </div>
                                </div>
                                </div>
                            </div>                                                                                                              
                        </div>
                        <div class="col-lg-9">
                            <div class="card shadow-sm">
                                <div class="card-body p-2 vh-95 overflow-y-auto">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody class="">
                                            <tr class="table-secondary text-center fw-bold">
                                                <td class="col-1">Voucher No.</td>
                                                <td class="col-1">Pur Date</td>                                    
                                                <td class="col-2">Supplier</td>
                                                <td class="col-1">Location</td>
                                                <td class="col-1">User</td>

                                            </tr>                                
                                            <tr class="change-cursor" v-if="purchases.length > 0" v-for="p in purchases" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal" aria-label="Close" >
                                                <td class="text-center">{{p.voucher_no}}</td>
                                                <td class="text-center">{{showTime(p.pur_date)}}</td>
                                                <td>{{p.supplier.name}} <span v-if="p.supplier.company">({{p.supplier.company}})</span></td>
                                                <td class="text-center">{{p.location}}</td>
                                                <td class="text-center">{{p.user.displayName}}</td>

                                            </tr>
                                        </tbody>
                                    </table>   
                                    <div v-if="isLoading">
                                        <div colspan="5">
                                            <div class="d-flex justify-content-center my-5">
                                                <div class="spinner-border text-primary"  role="status">
                                                  <span class="visually-hidden">Loading...</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>   
                                </div>
                            </div>                       
                        </div>
                    </div>                                                                                     
                   </div>
               </div>
           </div>
         </div>
       </div>
     </div>
     <!--end modal-->
</template>
<script>
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/purchases"
import FlatPickr from 'vue-flatpickr-component';
import {lang} from "@/lib/language"

export default {
   components:{
    FlatPickr,
   },
   data(){
       return{
           purchases:[],
           purchasesData: [],

           search_by_voucher_no:"N-",
           search_by_from_pur_date:"",
           search_by_to_pur_date:"",
           search_by_supplier:"",
           search_by_location:"",
            isUpdating:false,
           isLoading:false,
           config: {
                dateFormat: 'd/m/Y', // Flatpickr options
                enableTime: false,
                wrap: true, // set wrap to true only when using 'input-group'
               // altFormat: 'M j, Y',
            },  
       }
   },
  
   mounted() {    
       const myModalEl = document.getElementById('purchaseModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   
       this.setDate();
       this.fetchPurchase();         
   },
 
   computed:{
       user(){
           return this.$store.getters.user;
       },      
       search_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.search_link.mm : lang.search_link.eng ;
            return data;
        },
        clear_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.clear_link.mm : lang.clear_link.eng ;
            return data;
        },
        close_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.close_link.mm : lang.close_link.eng ;
            return data;
        },
        from_date_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.from_date.mm : lang.from_date.eng ;
            return data;
        },
        to_date_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.to_date.mm : lang.to_date.eng ;
            return data;
        },
        voucher_no_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.voucher_no.mm : lang.voucher_no.eng ;
            return data;
        },
        supplier_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.supplier.mm : lang.supplier.eng ;
            return data;
        }, 
   },
 
   methods:{
    setDate(){
        this.search_by_from_pur_date=hp.setToday();
        this.search_by_to_pur_date=hp.setToday();
    },
       updatePurchases(){
               this.isUpdating=true;
               this.fetchPurchase();
       },
       setCode(p){
           this.$emit("setCode", p)
           this.clearSearch();
       },
       inputSearchSupplier(){
        this.search_by_supplier=event.target.value;
        this.searchPurchase();
       },
       clearSearch(){
           this.search_by_voucher_no="N-";
           this.search_by_supplier="";
           this.search_by_from_pur_date=hp.setToday();
           this.search_by_to_pur_date=hp.setToday();
           this.search_by_location="";
           this.fetchPurchase();
       },
      
       searchPurchase(){   
             if(this.search_by_supplier && this.search_by_location){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.location==this.search_by_location && (f.supplier.name.toLowerCase().includes(this.search_by_supplier.toLowerCase()) || f.supplier.company.toLowerCase().includes(this.search_by_supplier.toLowerCase()))
               })
           }else 
            if(this.search_by_voucher_no.length>2 && this.search_by_location){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.location==this.search_by_location && f.voucher_no==this.search_by_voucher_no;
               })
           }else  if(this.search_by_voucher_no.length>2){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.voucher_no==this.search_by_voucher_no;
               })
           }else if(this.search_by_supplier){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.supplier.name.toLowerCase().includes(this.search_by_supplier.toLowerCase()) || f.supplier.company.toLowerCase().includes(this.search_by_supplier.toLowerCase())
               })
           }else if(this.search_by_location){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.location == this.search_by_location;
               })
           }else{
                   this.purchases= this.purchasesData                     
           }
       },
     
       async fetchPurchase() {
            this.purchases=[]
            this.isLoading = true;
            try {
                let purchaseQuery;

                if (this.search_by_from_pur_date && this.search_by_to_pur_date) {
                    const from_date = Timestamp.fromDate(hp.searchFromDate(this.search_by_from_pur_date));
                    const to_date = Timestamp.fromDate(hp.searchToDate(this.search_by_to_pur_date));

                    purchaseQuery = query(
                        collection(db, "purchases"),
                        where("pur_date", ">=", from_date),
                        where("pur_date", "<=", to_date),
                        orderBy("pur_date", "desc")
                    );
                } 
                const snapShot = await getDocs(purchaseQuery);
                const pur = ss.purchases(snapShot);

                this.purchasesData = pur;
                this.purchases = pur;
            } catch (error) {
                console.error("Error fetching purchases:", error);
            } finally {
                this.isLoading = false;
            }
        },

      showTime(t){
          return hp.showTime(t)
       },
       removeAfterAt(email) {
        const parts = email.split('@');
        return parts[0];
    }
   }
   
}
</script>
<style lang="">
   
</style>
