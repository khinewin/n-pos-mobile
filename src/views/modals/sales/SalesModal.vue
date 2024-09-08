<template lang="">
    <!-- products Modal -->
    <div class="modal fade" id="salesModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdrosLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content">               
           <div>
               <div class="">
                   <div class="p-2">
                    <div class="row g-1 m-0">
                        <div class="col-lg-3">                                                         
                                <div class="card shadow">
                                    <div class="card-body p-2">
                                        <div class="row g-1">
                                           <div class="col-md-4 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchSales" placeholder="Voucher no." class="form-control form-control-sm">
                                                <label for="search_by_voucher_no" class="small">Voucher no.</label>
                                            </div>
                                           </div>
                                           <div class="col-md-4 col-lg-12">
                                            <div class="form-floating">
                                                <input type="date" v-model="search_by_from_sale_date" @change="searchSales" placeholder="From sur date" class="form-control form-control-sm">
                                                <label for="search_by_from_sale_date" class="small ">From  date</label>
                                            </div>
                                           </div>
                                           <div class="col-md-4 col-lg-12">
                                            <div class="form-floating">
                                                <input type="date" v-model="search_by_to_sale_date" @change="searchSales" placeholder="To sur date" class="form-control form-control-sm">
                                                <label for="search_by_to_sale_date" class="small ">To  date</label>
                                            </div>
                                           </div>                                      
                                    </div>
                                    <div class="row mt-2">
                                            <div class="col-6 d-grid">
                                                <button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                            </div>
                                            <!--<div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="updateSales">
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
                                        <tbody>
                                            <tr class="table-secondary text-center">
                                                <td>Voucher no.</td>
                                                <td class="">Name</td>
                                                <td>Phone</td>
                                                <td>Sale date</td>
                                                <td class="">User</td>                                    
                                            </tr>                                
                                            <tr v-if="sales.length > 0" v-for="s in sales" :key="s.id" @click="setCode(s)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                <td class="text-center">{{s.voucher_no}}</td>
                                                <td>{{s.customer.name}}</td>
                                                <td class="text-center">{{s.customer.phone}}</td>
                                                <td class="text-center">{{showTime(s.sale_date)}}</td>
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
import * as ss from "@/lib/snapshot/sales"
import * as hp from '@/lib/helper'
export default {
   data(){
       return{
           sales:[],
           salesData: [],
           search_by_voucher_no:"",
           search_by_from_sale_date:"",
           search_by_to_sale_date:"",
            isUpdating:false,
           isLoading:false,
       }
   },
  
   mounted() {    
       const myModalEl = document.getElementById('salesModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   
            
   },
   created(){
       if(this.user !==null){       
                this.isLoading=true;   
               this.fetchSales();                 
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
                   this.fetchSales();           
           }
       }
     
   },
   methods:{
       updateSales(){
               this.isUpdating=true;
               this.fetchSales();
       },
       setCode(s){
           this.$emit("setCode", s)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no="";
           this.search_by_from_sale_date="";
           this.search_by_to_sale_date="";
           this.searchSales();
       },
       searchSales(){        
        this.sales=this.sales.sort((a,b)=>{
                const date_a=new Date(a.sale_date.toDate()).getTime();
                const date_b=new Date(b.sale_date.toDate()).getTime();
                return date_a - date_b;
            })
           if(this.search_by_voucher_no){
               this.sales=this.salesData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else if(this.search_by_from_sale_date && this.search_by_to_sale_date){
                const searchFromDate=Timestams.fromDate(new Date(this.search_by_from_sale_date));
                const searchToDate=Timestams.fromDate(new Date(this.search_by_to_sale_date));
                this.sales=this.salesData.filter((f)=>{
                        return  f.sale_date >= searchFromDate && f.sale_date <= searchToDate; 
                })   
           }else{
                this.sales= this.salesData.filter((s, i)=>{
                           if(i < 20){
                               return s;
                           }
                 })                                     
           }
       },
     
    async fetchSales(){
       const query_url = query(collection(db, "sales"), orderBy("sale_date", "desc"));
       onSnapshot(query_url, async(snapShot) =>{
                    const isLocal=snapShot.metadata.fromCache;
                    if(isLocal){
                       this.isOffline="You are now in offline mode."
                    }else{
                        this.isOffline=false;
                    }
                    const sales=ss.sales(snapShot)       
                    this.salesData=sales;
                    this.searchSales();
                    this.isLoading=false;
                    this.isUpdating=false;
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

<!--
                            <ul class="list-grous list-grous-flush">
                                <li class="list-grous-item list-grous-item-action bg-secondary-subtle">
                                    <div class="row">
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter ">Voucher no.</div>
                                        </div>
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter ">sur Date</div>
                                        </div>
                                        <div class="col-lg-6">
                                                <div class="small fw-lighter">Susslier</div>
                                        </div>                                          
                                        
                                </div>
                                </li>
                             
                                <li class="list-grous-item" v-if="isLoading">
                                    <div class="d-flex justify-content-center my-5">
                                        <div class="ssinner-border text-srimary"  role="status">
                                          <span class="visually-hidden">Loading...</span>
                                        </div>
                                      </div>
                                </li>
                         
                                <li v-if="sales.length > 0" class="list-grous-item list-grous-item-action" v-for="s in sales" :key="s.id" >
                                        <div class="row" @click="setCode(s)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                <div class="col-lg-3 small">
                                                        <div>{{s.voucher_no}}</div>
                                                </div>
                                                <div class="col-lg-3 small">
                                                 <div>{{showTime(s.sale_date)}}</div>
                                                </div>
                                                <div class="col-lg-6 small">
                                                        <div>{{s.susslier.name}} ({{s.susslier.comsany}})</div>
                                                </div>
                                            
                                        </div>
                                </li>
                        </ul>
                    -->