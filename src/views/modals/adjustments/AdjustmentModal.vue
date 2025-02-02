<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="adjustmentModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-fullscreen">
         <div class="modal-content">
                <div class="card border-0">
                    <div class="card-body p-2">                                        
                    <div class="row g-2">
                        <div class="col-12 col-md-4">                                                         
                              <div class="card shadow-sm">
                                <div class="card-body p-2">
                                        <div class="row g-1 small">
                                            <div class="col-6 col-md-12">
                                                <div class="form-floating mb-2">
                                                    <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchAdjustments" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                   <label for="search_by_voucher_no" class=" ">Voucher No.</label>
                                                  </div>
                                            </div>                                           
                                            <div class="col-6 col-md-12">
                                                <div class="input-group mb-2">
                                                    <div class="form-floating">
                                                        <flat-pickr @change="searchAdjustments" class="form-control-plaintext border-bottom" v-model="search_by_from_date" id="from_date" :config="config" />
                                                        <label for="from_date">From Date</label>
                                                    </div>
                                                    <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                                   </div>
                                            </div>
                                            <div class="col-6 col-md-12">
                                                <div class="input-group mb-2">
                                                    <div class="form-floating">
                                                        <flat-pickr @change="searchAdjustments" class="form-control-plaintext border-bottom" v-model="search_by_to_date" id="from_date" :config="config" />
                                                        <label for="from_date">To Date</label>
                                                    </div>
                                                    <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                                   </div>
                                            </div>
                                            <div class="col-6 col-md-12">
                                                <div class="form-floating ">
                                                    <select disabled class="form-control-plaintext border-bottom" @change="searchAdjustments" v-model="search_by_location" aria-placeholder="Location">
                                                        <option value="">All</option>
                                                        <option>Main</option>
                                                        <option>Mart</option>
                                                    </select>
                                                    <label for="search_from_location" class="">Location</label>
                                                </div>
                                            </div>
                                        </div>                                       
                                                        
                                        <div class="row mt-2">
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-adj" @click="clearSearch"> <i class="fa-solid fa-filter-circle-xmark"></i> Clear (ctrl+z)</button>
                                                </div>
                                                <!--<div class="col-4 d-grid">
                                                    <button class="btn btn-sm text-dark" @click="updateadjustments">
                                                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                    </button>
                                                </div>-->
                                                <div class="col-6 d-grid">
                                                    <button class="btn btn-sm text-dark close-adj" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                                                </div>
                                        </div>    
                                </div>
                              </div>                                                                            
                        </div>
                        <div class="col-12 col-md-8">
                            <div class="card shadow-sm">
                                <div class="card-body p-2 vh-95 overflow-y-auto">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody class="small text-center">
                                            <tr class="table-secondary fw-bold">
                                                <td>Voucher no.</td>
                                                <td>Adjustment date</td>   
                                                <td>Location</td>
                                                <td>User</td>                                 
                                            </tr>                                           
                                            <tr class="change-cursor" v-if="adjustments.length > 0" v-for="p in adjustments" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal">
                                                <td>{{p.voucher_no}}</td>
                                                <td>{{showTime(p.created_at)}}</td>
                                                <td>{{p.location}}</td>
                                                <td>{{p.user.displayName}}</td>
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
     <!--end modal-->
</template>
<script>
import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/adjustments"
import FlatPickr from 'vue-flatpickr-component';
import * as hp from '@/lib/helper'

export default {
    components:{
        FlatPickr,
    },
   data(){
       return{
           adjustments:[],
           adjustmentsData: [],
           search_by_voucher_no:"N-",
           search_by_from_date:"",
           search_by_to_date:"",
           search_by_location: "",
           isLoading:false,
           isUpdating:false,
          
       }
   },
   created() {
     this.setDate();

   },
   mounted() {    
       const myModalEl = document.getElementById('adjustmentModal')
       myModalEl.addEventListener('show.bs.modal', event => {
               if(this.adjustmentsData.length ===0){
                  this.fetchAdjustments();                
               }
       })       
        
   },
  
   computed:{
       user(){
           return this.$store.getters.user;
       },
       config(){
        return this.$store.getters.flat_pickr_config;
       }
       
   },
  
   methods:{
    setDate(){
          //  this.search_by_from_date=hp.setSearchFromDate();
          //  this.search_by_to_date=hp.setSearchToDate();
          this.search_by_from_date=hp.setToday();
          this.search_by_to_date=hp.setToday();
        },
    removeAfterAt(email) {
        const parts = email.split('@');
        return parts[0];
    },
       updateadjustments(){
                this.isUpdating=true;
               this.fetchadjustments();
       },
       setCode(p){
           this.$emit("setCode", p)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="N-";
           this.search_by_location="";
            this.setDate();
           this.searchAdjustments();
       },
       searchAdjustments(){
            this.fetchAdjustments();
       },
       displayAdjustments(){
        if(this.search_by_voucher_no.length>2 && this.search_by_location){
               this.adjustments=this.adjustmentsData.filter((f)=>{
                       return f.location== this.search_by_location && f.voucher_no== this.search_by_voucher_no;
               })
        }else  if(this.search_by_location){
               this.adjustments=this.adjustmentsData.filter((f)=>{
                       return f.location== this.search_by_location;
               })
        }else  if(this.search_by_voucher_no.length>2){
               this.adjustments=this.adjustmentsData.filter((f)=>{
                       return f.voucher_no== this.search_by_voucher_no;
               })
           }else{
                   this.adjustments= this.adjustmentsData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                       })                                     
           }
       },
     
    async fetchAdjustments(){
        const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_by_from_date))
        const to_date=Timestamp.fromDate(hp.searchToDate(this.search_by_to_date))        
        let query_url="";         
        if(this.search_by_from_date && this.search_by_to_date){
            query_url = query(collection(db, "adjustments"), where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
        }
       onSnapshot(query_url, (snapShot)=>{
        const adjustments=ss.adjustments(snapShot);      
        this.adjustmentsData=adjustments;
        this.isLoading=false;
        this.isUpdating=false;
        this.displayAdjustments();
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
                         
                                <li v-if="adjustments.length > 0" class="list-group-item list-group-item-action" v-for="p in adjustments" :key="p.id" >
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