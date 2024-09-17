<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="transfersModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content">               
           <div>
               <div class="card min-vh-100">
                   <div class="card-body p-2">
                    <div class="row g-1 m-0">
                        <div class="col-lg-3">                                                         
                                <div class="card shadow">
                                    <div class="card-body p-2">
                                       <div class="row g-1 small">
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchTransfers" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="search_by_voucher_no" class="">Voucher no.</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="input-group mb-2">
                                            <div class="form-floating">
                                                <flat-pickr @change="searchTransfers" class="form-control-plaintext border-bottom" v-model="search_from_date" id="from_date" :config="config" />
                                                <label for="from_date">From date</label>
                                            </div>
                                            <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                           </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="input-group mb-2">
                                            <div class="form-floating">
                                                <flat-pickr @change="searchTransfers" class="form-control-plaintext border-bottom" v-model="search_to_date" id="from_date" :config="config" />
                                                <label for="from_date">To date</label>
                                            </div>
                                            <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                           </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <select class="form-control-plaintext border-bottom" @change="searchTransfers" v-model="search_from_location" aria-placeholder="From location">
                                                    <option value="">All</option>
                                                    <option>Main</option>
                                                    <option>Pharmacy</option>
                                                </select>
                                                <label for="search_from_location" class="">From location</label>
                                            </div>
                                        </div>
                                    </div>   
                                
                                <div class="row">
                                        <div class="col-6 d-grid">
                                            <button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                        </div>
                                        <!--<div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="updateTransfers">
                                                <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                               <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                            </button>
                                        </div>-->
                                        <div class="col-6 d-grid">
                                            <button class="btn btn-sm text-dark close-transfer-modal" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close (ctrl+h)</button>
                                        </div>
                                </div>
                                </div>
                            </div>                                                                                                              
                        </div>
                        <div class="col-lg-9">
                            <div class="card shadow">
                                <div class="card-body p-2">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody class="">
                                            <tr class="table-secondary text-center">
                                                <td>Voucher no.</td>
                                                <td>From location</td>
                                                <td>To Location</td>
                                                <td>Transfer date</td>                                    
                                                <td>User</td>
                                            </tr>                                
                                            <tr v-if="transfers.length > 0" v-for="p in transfers" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal" aria-label="Close" style="cursor: context-menu;">
                                                <td class="text-center">{{p.voucher_no}}</td>
                                                <td>{{p.from_location}}</td>
                                                <td>{{p.to_location}}</td>
                                                <td class="text-center">{{showTime(p.created_at)}}</td>
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
import * as ss from "@/lib/snapshot/transfers"
import FlatPickr from 'vue-flatpickr-component';

export default {
    components:{
        FlatPickr,
    },
   props:['storePurchase'],
   data(){
       return{
           transfers:[],
           transfersData: [],

           search_by_voucher_no:"",
           search_from_date:"",
           search_to_date:"",
           search_from_location:"",

           from_location:"All",

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
    this.setDate();
       const myModalEl = document.getElementById('transfersModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   
       this.fetchTransfers();   
            
   },
 
   computed:{
       user(){
           return this.$store.getters.user;
       },
       
   },
 
   methods:{
    setDate(){
            this.search_from_date=hp.setSearchFromDate();
            this.search_to_date=hp.setSearchToDate();
        },
       updateTransfers(){
               this.isUpdating=true;
               this.fetchTransfers();
       },
       setCode(p){
           this.$emit("setCode", p)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="";
           this.search_from_location="";
           this.setDate();
           this.searchTransfers();
       },
       searchTransfers(){
            this.fetchTransfers();
       },
       displayTransfers(){
        if(this.search_by_voucher_no && this.search_from_location){
               this.transfers=this.transfersData.filter((f)=>{
                       return f.from_location===this.search_from_location && f.voucher_no== this.search_by_voucher_no;
               })
        }else   if(this.search_by_voucher_no){
               this.transfers=this.transfersData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else if(this.search_from_location){
                this.transfers=this.transfersData.filter((f)=>{
                        return f.from_location===this.search_from_location;
                })
           }else{
                   this.transfers= this.transfersData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                })                                     
           }
       },
     
    async fetchTransfers(){
        const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_from_date))
        const to_date=Timestamp.fromDate(hp.searchToDate(this.search_to_date))        
        let query_url="";         
        if(this.search_from_date && this.search_to_date){
            query_url = query(collection(db, "transfers"), where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
        }
       onSnapshot(query_url, (snapShot)=>{
        const transfers=ss.transfers(snapShot);      
        this.transfersData=transfers;
        this.isLoading=false;
        this.isUpdating=false;
        this.displayTransfers();
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

