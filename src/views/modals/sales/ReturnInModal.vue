<template lang="">
    <!-- products Modal -->
    <div class="modal fade" id="returnInModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdrosLabel" aria-hidden="true">
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
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_voucher_no" @paste="searchRtIn" v-model="search_by_voucher_no" @keypress.enter="searchRtIn" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="search_by_voucher_no" class="">Voucher No.</label>
                                            </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_customer" v-model="search_by_customer" @keyup="searchRtIn" placeholder="Customer name" class="form-control-plaintext border-bottom">
                                                <label for="search_by_customer" class="">Customer name</label>
                                            </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr @change="searchRtIn" class="form-control-plaintext border-bottom" v-model="search_by_date" id="from_date" :config="config" />
                                                    <label for="from_date">From Date</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr @change="searchRtIn" class="form-control-plaintext border-bottom" v-model="search_to_date" id="to_date" :config="config" />
                                                    <label for="to_date">To Date</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>                                      
                                    </div>
                                    <div class="row mt-2">
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-rtin"  @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear (ctrl+z)</button>
                                            </div>
                                            <!--<div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="updateSales">
                                                    <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                </button>
                                            </div>-->
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark close-rtin" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
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
                                                <td>Voucher No.</td>
                                                <td class="">Name</td>
                                                <td>Phone</td>
                                                <td>Return In Date</td>
                                                <td class="">User</td>                                    
                                            </tr>                                
                                            <tr class="change-cursor" v-if="sales.length > 0" v-for="s in sales" :key="s.id" @click="setCode(s)" data-bs-dismiss="modal" >
                                                <td class="text-center">{{s.voucher_no}}</td>
                                                <td>{{s.customer.name}}</td>
                                                <td class="text-center">{{s.customer.phone}}</td>
                                                <td class="text-center">{{showTime(s.rt_in_date)}}</td>
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
import * as ss from "@/lib/snapshot/returnIn"
import * as hp from '@/lib/helper'
import FlatPickr from 'vue-flatpickr-component';

export default {
    components:{
        FlatPickr
    },
   data(){
       return{
           sales:[],
           returnInData: [],
           search_by_voucher_no:"N-",
           search_by_date:"",
           search_to_date:"",
           search_by_customer:"",
            isUpdating:false,
           isLoading:false,
       
       }
   },
  
   mounted() {    
        this.setDate();
       const myModalEl = document.getElementById('returnInModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   

       this.isLoading=true;   
       this.fetchReturnIn();  
            
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
       updateSales(){
               this.isUpdating=true;
               this.fetchReturnIn();
       },
       setCode(s){
           this.$emit("setCode", s)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="N-";
           this.search_by_customer="";
            this.setDate();
           this.searchRtIn();
       },
       searchRtIn(){                     
         this.fetchReturnIn();        
       },    

    displayReturnIN(){     
        if(this.search_by_customer){
            this.sales=this.returnInData.filter((f)=>f.customer.name.toLowerCase().includes(this.search_by_customer.toLowerCase()))
       }else
       if(this.search_by_voucher_no.length>2){
            this.sales=this.returnInData.filter((f)=>f.voucher_no==this.search_by_voucher_no)
       }else{
            this.sales=this.returnInData;
       }
    },   
    async fetchReturnIn(){    
       const from_date=Timestamp.fromDate(hp.searchFromDate(this.search_by_date))
        const to_date=Timestamp.fromDate(hp.searchToDate(this.search_to_date))        
        let query_url="";         
        if(this.search_by_date && this.search_to_date){
            query_url = query(collection(db, "returnIn"), where("rt_in_date", ">=", from_date), where("rt_in_date", "<=", to_date), orderBy("rt_in_date", "desc"));
        }
       onSnapshot(query_url, async(snapShot) =>{                
                    const rtin=ss.returnIn(snapShot)     
                    this.returnInData=rtin;
                    this.isLoading=false;
                    this.isUpdating=false;
                    this.displayReturnIN();
            });        
      } ,
      showTime(t){
           return hp.showTimeHourShort(t)
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
