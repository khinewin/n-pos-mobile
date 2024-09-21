<template lang="">
    <!-- products Modal -->
    <div class="modal fade" id="returnOutModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdrosLabel" aria-hidden="true">
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
                                           <div class="col-6 col-sm-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_voucher_no" @paste="searchRtOut" v-model="search_by_voucher_no" @keypress.enter="searchRtOut" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="search_by_voucher_no" class="">Voucher No.</label>
                                            </div>
                                           </div>
                                           <div class="col-6 col-sm-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_supplier" v-model="search_by_supplier" @keyup="searchRtOut" placeholder="Customer name" class="form-control-plaintext border-bottom">
                                                <label for="search_by_supplier" class="">Supplier</label>
                                            </div>
                                           </div>
                                           <div class="col-6 col-sm-6 col-md-3 col-lg-12">
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr @change="searchRtOut" class="form-control-plaintext border-bottom" v-model="search_by_date" id="from_date" :config="config" />
                                                    <label for="from_date">From Date</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr @change="searchRtOut" class="form-control-plaintext border-bottom" v-model="search_to_date" id="to_date" :config="config" />
                                                    <label for="to_date">To Date</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>                                      
                                    </div>
                                    <div class="row mt-2">
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-rtout"  @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear (ctrl+z)</button>
                                            </div>
                                            <!--<div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="updatertOuts">
                                                    <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                </button>
                                            </div>-->
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark close-rtout" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                                            </div>
                                    </div>
                                    </div>
                                </div>                                                                                                              
                        </div>
                        <div class="col-lg-9">
                            <div class="card shadow-sm">
                                <div class="card-body p-2 vh-95 overflow-y-auto">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody>
                                            <tr class="table-secondary text-center fw-bold">
                                                <td class="col-1">Voucher No.</td>
                                                <td class="col-1">Location</td>
                                                <td class="col-2">Supplier</td>
                                                <td class="col-1">Phone</td>
                                                <td class="col-2">Company</td>
                                                <td class="col-1">Date</td>
                                                <td class="col-1">User</td>                                    
                                            </tr>                                
                                            <tr class="change-cursor" v-if="rtOuts.length > 0" v-for="s in rtOuts" :key="s.id" @click="setCode(s)" data-bs-dismiss="modal" >
                                                <td class="text-center">{{s.voucher_no}}</td>
                                                <td class="text-center">{{s.location}}</td>
                                                <td>{{s.supplier.name}}</td>
                                                <td class="text-center">{{s.supplier.phone}}</td>
                                                <td>{{s.supplier.company}}</td>
                                                <td class="text-center">{{showTime(s.rt_out_date)}}</td>
                                                <td class="text-center">{{s.user.displayName}}</td>
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
import {Timestamp, collection, addDoc, getDocs, onSnapshot, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/returnOut"
import * as hp from '@/lib/helper'
import FlatPickr from 'vue-flatpickr-component';

export default {
    components:{
        FlatPickr
    },
   data(){
       return{
           rtOuts:[],
           returnOutData: [],
           search_by_voucher_no:"",
           search_by_date:"",
           search_to_date:"",
           search_by_supplier:"",
            isUpdating:false,
           isLoading:false,
        
       }
   },
  
   mounted() {    
        this.setDate();
       const myModalEl = document.getElementById('returnOutModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   

       this.isLoading=true;   
       this.fetchRtOut();  
            
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
            this.search_by_date=hp.setSearchFromDate();
            this.search_to_date=hp.setSearchToDate();
        },
       updatertOuts(){
               this.isUpdating=true;
               this.fetchRtOut();
       },
       setCode(s){
           this.$emit("setCode", s)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="";
           this.search_by_supplier="";
            this.setDate();
           this.searchRtOut();
       },
       searchRtOut(){                     
         this.fetchRtOut();        
       },    

    displayReturnout(){     
        if(this.search_by_supplier){
            this.rtOuts=this.returnOutData.filter((f)=>f.supplier.name.toLowerCase().includes(this.search_by_supplier.toLowerCase()))
       }else
       if(this.search_by_voucher_no){
            this.rtOuts=this.returnOutData.filter((f)=>f.voucher_no==Number(this.search_by_voucher_no))
       }else{
            this.rtOuts=this.returnOutData;
       }
    },   
    async fetchRtOut(){    
       const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_by_date))
        const to_date=Timestamp.fromDate(hp.searchToDate(this.search_to_date))        
        let query_url="";         
        if(this.search_by_date && this.search_to_date){
            query_url = query(collection(db, "returnOut"), where("rt_out_date", ">=", from_date), where("rt_out_date", "<=", to_date), orderBy("rt_out_date", "desc"));
        }
       onSnapshot(query_url, async(snapShot) =>{                
                    const rtin=ss.returnOut(snapShot)     
                    this.returnOutData=rtin;
                    this.isLoading=false;
                    this.isUpdating=false;
                    this.displayReturnout();
            });        
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
