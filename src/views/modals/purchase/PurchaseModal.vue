<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="purchaseModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content">               
           <div>
               <div class="card">
                   <div class="card-body p-2">
                    <div class="row g-1 m-0">
                        <div class="col-lg-3">                                                         
                                <div class="card shadow">
                                    <div class="card-body p-2">
                                       <div class="row g-1 small">
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchPurchase" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="search_by_voucher_no" class="">Voucher no.</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="input-group">
                                                <div class="form-floating">
                                                    <flat-pickr @change="searchPurchase" class="form-control-plaintext border-bottom" v-model="search_by_from_pur_date" id="from_date" :config="config" />
                                                    <label for="from_date">From date</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="input-group">
                                                <div class="form-floating">
                                                    <flat-pickr @change="searchPurchase" class="form-control-plaintext border-bottom" v-model="search_by_to_pur_date" id="to_date" :config="config" />
                                                    <label for="to_date">To date</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" v-model="search_by_supplier" @keyup="searchPurchase" placeholder="Supplier" class="form-control-plaintext border-bottom">
                                                <label for="search_by_supplier" class="">Supplier</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <select @change="searchPurchase" class="form-control-plaintext border-bottom" v-model="search_by_location">
                                                    <option value="">All</option>
                                                    <option>Main</option>
                                                    <option>Pharmacy</option>
                                                </select>
                                                <label for="search_by_supplier" class="">Location</label>
                                            </div>
                                        </div>
                                       </div>
                                
                                <div class="row mt-2">
                                        <div class="col-6 d-grid">
                                            <button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                        </div>
                                        <!--<div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="updatePurchases">
                                                <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                               <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                            </button>
                                        </div>-->
                                        <div class="col-6 d-grid">
                                            <button class="btn btn-sm text-dark" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                                        </div>
                                </div>
                                </div>
                            </div>                                                                                                              
                        </div>
                        <div class="col-lg-9">
                            <div class="card shadow vh-80 overflow-y-auto">
                                <div class="card-body p-2">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody class="">
                                            <tr class="table-secondary text-center">
                                                <td>Voucher no.</td>
                                                <td>Pur date</td>                                    
                                                <td>Supplier</td>
                                                <td>Location</td>
                                                <td>User</td>

                                            </tr>                                
                                            <tr v-if="purchases.length > 0" v-for="p in purchases" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal" aria-label="Close" style="cursor: context-menu;">
                                                <td class="text-center">{{p.voucher_no}}</td>
                                                <td class="text-center">{{showTime(p.pur_date)}}</td>
                                                <td>{{p.supplier.name}} ({{p.supplier.company}})</td>
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

export default {
   props:['storePurchase'],
   components:{
    FlatPickr,
   },
   data(){
       return{
           purchases:[],
           purchasesData: [],

           search_by_voucher_no:"",
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
   },
   created(){           
        this.fetchPurchase();                       
   },
   computed:{
       user(){
           return this.$store.getters.user;
       },       
   },
 
   methods:{
    setDate(){
        this.search_by_from_pur_date=hp.setSearchFromDate();
        this.search_by_to_pur_date=hp.setSearchToDate();
    },
       updatePurchases(){
               this.isUpdating=true;
               this.fetchPurchase();
       },
       setCode(p){
           this.$emit("setCode", p)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="";
           this.search_by_supplier="";
           this.search_by_from_pur_date=hp.setSearchFromDate();
           this.search_by_to_pur_date=hp.setSearchToDate();
           this.search_by_location="";
           this.searchPurchase();
       },
       searchPurchase(){
            this.fetchPurchase();
       },
       displayPurchase(){   
             if(this.search_by_supplier && this.search_by_location){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.location==this.search_by_location && (f.supplier.name.toLowerCase().includes(this.search_by_supplier.toLowerCase()) || f.supplier.company.toLowerCase().includes(this.search_by_supplier.toLowerCase()))
               })
           }else 
            if(this.search_by_voucher_no && this.search_by_location){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.location==this.search_by_location && f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else  if(this.search_by_voucher_no){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
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
                   this.purchases= this.purchasesData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                 })                                     
           }
       },
     
    async fetchPurchase(){
        let query_url="";
        if(this.search_by_from_pur_date && this.search_by_to_pur_date){
            const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_by_from_pur_date))
            const to_date=Timestamp.fromDate(hp.searchToDate(this.search_by_to_pur_date))       
            query_url = query(collection(db, "purchases"), where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),  orderBy("pur_date", "desc"));
        }
       onSnapshot(query_url, (snapShot)=>{
            const purchases=ss.purchases(snapShot);      
            this.purchasesData=purchases;
            this.isLoading=false;
            this.isUpdating=false;
            this.displayPurchase();
       })
      
      } ,
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

<!--
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                    <div class="row">
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter ">Voucher no.</div>
                                        </div>
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter ">Pur Date</div>
                                        </div>
                                        <div class="col-lg-6">
                                                <div class="small fw-lighter">Supplier</div>
                                        </div>                                          
                                        
                                </div>
                                </li>
                             
                                <li class="list-group-item" v-if="isLoading">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="spinner-border text-primary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                      </div>
                                </li>
                         
                                <li v-if="purchases.length > 0" class="list-group-item list-group-item-action" v-for="p in purchases" :key="p.id" >
                                        <div class="row" @click="setCode(p)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                <div class="col-lg-3 small">
                                                        <div>{{p.voucher_no}}</div>
                                                </div>
                                                <div class="col-lg-3 small">
                                                 <div>{{showTime(p.pur_date)}}</div>
                                                </div>
                                                <div class="col-lg-6 small">
                                                        <div>{{p.supplier.name}} ({{p.supplier.company}})</div>
                                                </div>
                                            
                                        </div>
                                </li>
                        </ul>
                    -->