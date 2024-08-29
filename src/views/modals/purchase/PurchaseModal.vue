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
                                       <div class="row g-1">
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchPurchases" placeholder="Voucher no." class="form-control form-control-sm">
                                                <label for="search_by_voucher_no" class="small">Voucher no.</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="date" v-model="search_by_from_pur_date" @change="searchPurchases" placeholder="From pur date" class="form-control form-control-sm">
                                                <label for="search_by_from_pur_date" class="small">From pur date</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="date" v-model="search_by_to_pur_date" @change="searchPurchases" placeholder="To pur date" class="form-control form-control-sm">
                                                <label for="search_by_to_pur_date" class="small">To pur date</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" v-model="search_by_supplier" @keyup="searchPurchases" placeholder="Supplier" class="form-control form-control-sm">
                                                <label for="search_by_supplier" class="small">Supplier</label>
                                            </div>
                                        </div>
                                       </div>
                                
                                <div class="row mt-2">
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                        </div>
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="updatePurchases">
                                                <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                               <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                            </button>
                                        </div>
                                        <div class="col-4 d-grid">
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
                                                <td>User</td>
                                                <td>Location</td>
                                            </tr>                                
                                            <tr v-if="purchases.length > 0" v-for="p in purchases" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal" aria-label="Close" style="cursor: context-menu;">
                                                <td class="text-center">{{p.voucher_no}}</td>
                                                <td class="text-center">{{showTime(p.pur_date)}}</td>
                                                <td>{{p.supplier.name}} ({{p.supplier.company}})</td>
                                                <td>{{removeAfterAt(p.user.email)}}</td>
                                                <td>{{p.location}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/purchases"
export default {
   props:['storePurchase'],
   data(){
       return{
           purchases:[],
           purchasesData: [],

           search_by_voucher_no:"",
           search_by_from_pur_date:"",
           search_by_to_pur_date:"",
           search_by_supplier:"",
            isUpdating:false,
           isLoading:false,
       }
   },
  
   mounted() {    
       const myModalEl = document.getElementById('purchaseModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   
            
   },
   created(){
       if(this.user !==null){          
               this.fetchPurchase();                 
       }
   },
   computed:{
       user(){
           return this.$store.getters.user;
       },
       
   },
   watch:{       
       user(data){
               if(data !==null){             
                    this.isLoading=true;      
                   this.fetchPurchase();           
           }
       }     
   },
   methods:{
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
           this.search_by_from_pur_date="";
           this.search_by_to_pur_date="";
           this.searchPurchases();
       },
       searchPurchases(){
           if(this.search_by_voucher_no){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else if(this.search_by_supplier){
               this.purchases=this.purchasesData.filter((f)=>{
                       return f.supplier.name.toLowerCase().includes(this.search_by_supplier.toLowerCase()) || f.supplier.company.toLowerCase().includes(this.search_by_supplier.toLowerCase())
               })
           }else if(this.search_by_from_pur_date && this.search_by_to_pur_date){
                const searchFromDate=Timestamp.fromDate(new Date(this.search_by_from_pur_date));
                const searchToDate=Timestamp.fromDate(new Date(this.search_by_to_pur_date));
                this.purchases=this.purchasesData.filter((f)=>{
                        return  f.pur_date >= searchFromDate && f.pur_date <= searchToDate; 
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
       const query_url = query(collection(db, "purchases"),  orderBy("pur_date", "desc"));
       const snapShot=await getDocs(query_url)
       const purchases=ss.purchases(snapShot);      
       this.purchasesData=purchases;
     //  this.$store.dispatch("setProducts", products)
       this.isLoading=false;
       this.isUpdating=false;
       this.searchPurchases();
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