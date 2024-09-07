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
                                       <div class="row g-1">
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchTransfers" placeholder="Voucher no." class="form-control form-control-sm">
                                                <label for="search_by_voucher_no" class="small">Voucher no.</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="date" v-model="search_from_date" @change="searchTransfers" placeholder="From pur date" class="form-control form-control-sm">
                                                <label for="search_from_date" class="small">From date</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <input type="date" v-model="search_to_date" @change="searchTransfers" placeholder="To pur date" class="form-control form-control-sm">
                                                <label for="search_to_date" class="small">To date</label>
                                            </div>
                                        </div>
                                        <div class="col-md-6 col-lg-12">
                                            <div class="form-floating ">
                                                <select class="form-select form-select-sm" @change="searchTransfers" v-model="search_from_location" aria-placeholder="From location">
                                                    <option value="">From location</option>
                                                    <option>Store</option>
                                                    <option>Shop</option>
                                                </select>
                                                <label for="search_from_location" class="small">From location</label>
                                            </div>
                                        </div>
                                    </div>   
                                
                                <div class="row">
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                        </div>
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark" @click="updateTransfers">
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
                                                <td>{{removeAfterAt(p.user.email)}}</td>
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
export default {
   props:['storePurchase'],
   data(){
       return{
           transfers:[],
           transfersData: [],

           search_by_voucher_no:"",
           search_from_date:"",
           search_to_date:"",
           search_from_location:"",

            isUpdating:false,
           isLoading:false,
       }
   },
  
   mounted() {    
       const myModalEl = document.getElementById('transfersModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   
            
   },
   created(){
       if(this.user !==null){          
               this.fetchTransfers();                 
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
                   this.fetchTransfers();           
           }
       }     
   },
   methods:{
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
           this.search_by_supplier="";
           this.search_from_date="";
           this.search_to_date="";
           this.searchTransfers();
       },
       searchTransfers(){
           if(this.search_by_voucher_no){
               this.transfers=this.transfersData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else if(this.search_from_date && this.search_to_date && this.search_from_location){
                const searchFromDate=Timestamp.fromDate(new Date(this.search_from_date));
                const searchToDate=Timestamp.fromDate(new Date(this.search_to_date));
                this.transfers=this.transfersData.filter((f)=>{
                        return  f.created_at >= searchFromDate && f.created_at <= searchToDate && f.from_location===this.search_from_location; 
                })   
           }else if(this.search_from_location){
                this.transfers=this.transfersData.filter((f)=>{
                        return f.from_location===this.search_from_location;
                })
           }else if(this.search_from_date && this.search_to_date){
                const searchFromDate=Timestamp.fromDate(new Date(this.search_from_date));
                const searchToDate=Timestamp.fromDate(new Date(this.search_to_date));
                this.transfers=this.transfersData.filter((f)=>{
                        return  f.created_at >= searchFromDate && f.created_at <= searchToDate; 
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
       const query_url = query(collection(db, "transfers"),  orderBy("created_at", "desc"));
       onSnapshot(query_url, (snapShot)=>{
        const transfers=ss.transfers(snapShot);      
        this.transfersData=transfers;
        this.isLoading=false;
        this.isUpdating=false;
        this.searchTransfers();
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

