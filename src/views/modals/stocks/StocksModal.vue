<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="stocksModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-fullscreen">
         <div class="modal-content p-2">                         
                    <div class="row g-2">
                        <div class="col-4">                                                         
                              <div class="card shadow-sm">
                                <div class="card-body p-2">
                                        <div class="row small">
                                            <div class="col-12">
                                                <div class="form-floating mb-2">
                                                    <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchStocks" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                   <label for="search_by_voucher_no" class=" ">Voucher No.</label>
                                                  </div>
                                            </div>
                                            <div class="col-12">
                                                <div class="input-group mb-2">
                                                    <div class="form-floating">
                                                        <flat-pickr @change="searchStocks" class="form-control-plaintext border-bottom" v-model="search_by_from_date" id="from_date" :config="config" />
                                                        <label for="from_date">From Date</label>
                                                    </div>
                                                    <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                                   </div>
                                            </div>
                                            <div class="col-12">
                                                <div class="input-group mb-2">
                                                    <div class="form-floating">
                                                        <flat-pickr @change="searchStocks" class="form-control-plaintext border-bottom" v-model="search_by_to_date" id="to_date" :config="config" />
                                                        <label for="to_date">From Date</label>
                                                    </div>
                                                    <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                                   </div>
                                            </div>
                                        </div>                                        
                                            
                                        <div class="row mt-2">
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-stock" @click="clearSearch"> <i class="fa-solid fa-filter-circle-xmark"></i> Clear (ctrl+z)</button>
                                                </div>
                                                <!--<div class="col-4 d-grid">
                                                    <button class="btn btn-sm text-dark" @click="updatestocks">
                                                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                    </button>
                                                </div>-->
                                                <div class="col-6 d-grid">
                                                    <button class="btn btn-sm text-dark close-stock" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                                                </div>
                                        </div>    
                                </div>
                              </div>                                                                            
                        </div>
                        <div class="col-8 table-responsive">
                            <div class="card shadow-sm">
                                <div class="card-body p-2 vh-95 overflow-y-auto">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody class="small text-center">
                                            <tr class="table-secondary fw-bold">
                                                <td>Voucher no.</td>
                                                <td>Date</td>   
                                                <td>User</td>                                 
                                            </tr>
                                            <tr v-if="isLoading">
                                                <td colspan="3">
                                                    <div class="d-flex justify-content-center my-5">
                                                        <div class="spinner-border text-primary"  role="status">
                                                          <span class="visually-hidden">Loading...</span>
                                                        </div>
                                                      </div>
                                                </td>
                                            </tr>
                                            <tr class="change-cursor" v-if="stocks.length > 0" v-for="p in stocks" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal" >
                                                <td>{{p.voucher_no}}</td>
                                                <td>{{showTime(p.created_at)}}</td>
                                                <td>{{p.user.displayName}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
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
import * as ss from "@/lib/snapshot/stocks"
import FlatPickr from 'vue-flatpickr-component';
import * as hp from '@/lib/helper'
export default {
   props:['storestock'],
   components:{
    FlatPickr
   },
   data(){
       return{
           stocks:[],
           stocksData: [],
           search_by_voucher_no:"N-",
           search_by_from_date:"",
           search_by_to_date:"",
           isLoading:false,
           isUpdating:false,
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
       const myModalEl = document.getElementById('stocksModal')
       myModalEl.addEventListener('show.bs.modal', event => {
                
       })               
       this.fetchStocks();                     

   },
 
   computed:{
       user(){
           return this.$store.getters.user;
       },       
   },
  
   methods:{
    setDate(){
            this.search_by_from_date=hp.setSearchFromDate();
            this.search_by_to_date=hp.setSearchToDate();
        },
    removeAfterAt(email) {
        const parts = email.split('@');
        return parts[0];
    },
       updatestocks(){
                this.isUpdating=true;
               this.fetchStocks();
       },
       setCode(p){
           this.$emit("setCode", p)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="N-";
           this.search_by_from_date=hp.setSearchFromDate();
           this.search_by_to_date=hp.setSearchToDate();
           this.searchStocks();
       },
       searchStocks(){
            this.fetchStocks();
       },
       displayStocks(){
           if(this.search_by_voucher_no.length>2){
               this.stocks=this.stocksData.filter((f)=>{
                       return f.voucher_no===this.search_by_voucher_no;
               })
           }else{
                   this.stocks= this.stocksData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                       })                                     
           }
       },
     
    async fetchStocks(){
        const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_by_from_date))
        const to_date=Timestamp.fromDate(hp.searchToDate(this.search_by_to_date))   
        let query_url="";
        if(this.search_by_from_date && this.search_by_to_date){
            query_url = query(collection(db, "stocks"), where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
        } 
       onSnapshot(query_url, (snapShot)=>{
        const stocks=ss.stocks(snapShot);      
        this.stocksData=[];
        this.stocksData=stocks;
        this.isLoading=false;
        this.isUpdating=false;
        this.displayStocks();
       })
       
      } ,
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
                         
                                <li v-if="stocks.length > 0" class="list-group-item list-group-item-action" v-for="p in stocks" :key="p.id" >
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