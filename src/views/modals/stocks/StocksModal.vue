<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="stocksModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content p-2 min-vh-100">                         
                    <div class="row g-2">
                        <div class="col-4">                                                         
                              <div class="card shadow">
                                <div class="card-body p-2">
                                        <div class="form-floating mb-2">
                                          <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchStocks" placeholder="Voucher no." class="form-control form-control-sm">
                                         <label for="search_by_voucher_no" class="small ">Voucher no.</label>
                                        </div>
                                        <div class="form-floating  mb-2">
                                            <input type="date" v-model="search_by_from_date" @change="searchStocks" placeholder="From pur date" class="form-control form-control-sm">
                                            <label for="search_by_from_pur_date" class="small ">From pur date</label>
                                        </div>
                                        <div class="form-floating  mb-2">
                                            <input type="date" v-model="search_by_to_date" @change="searchStocks" placeholder="To pur date" class="form-control form-control-sm">
                                            <label for="search_by_to_pur_date" class="small ">To pur date</label>
                                        </div>                             
                                        <div class="row mt-2">
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="clearSearch"> <i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                                </div>
                                                <div class="col-4 d-grid">
                                                    <button class="btn btn-sm text-dark" @click="updatestocks">
                                                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
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
                        <div class="col-8 table-responsive">
                            <div class="card shadow">
                                <div class="card-body p-2">
                                    <table class="table table-bordered table-hover m-0">
                                        <tbody class="small text-center">
                                            <tr class="table-secondary">
                                                <td>Voucher no.</td>
                                                <td>Opening date</td>   
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
                                            <tr v-if="stocks.length > 0" v-for="p in stocks" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                <td>{{p.voucher_no}}</td>
                                                <td>{{showTime(p.created_at)}}</td>
                                                <td>{{removeAfterAt(p.user.email)}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/stocks"
export default {
   props:['storestock'],
   data(){
       return{
           stocks:[],
           stocksData: [],
           search_by_voucher_no:"",
           search_by_from_date:"",
           search_by_to_date:"",
           isLoading:false,
           isUpdating:false,
       }
   },
  
   mounted() {    
       const myModalEl = document.getElementById('stocksModal')
       myModalEl.addEventListener('show.bs.modal', event => {
                
       })               
   },
   created(){
       if(this.user !==null){     
                this.isLoading=true;     
               this.fetchStocks();                 
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
                   this.fetchStocks();           
           }
       }
     
   },
   methods:{
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
           this.search_by_voucher_no="";
           this.search_by_from_date="";
           this.search_by_to_date="";
           this.searchStocks();
       },
       searchStocks(){
           if(this.search_by_voucher_no){
               this.stocks=this.stocksData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else
            if(this.search_by_from_date && this.search_by_to_date){
                const searchFromDate=Timestamp.fromDate(new Date(this.search_by_from_date));
                const searchToDate=Timestamp.fromDate(new Date(this.search_by_to_date));
                this.stocks=this.stocksData.filter((f)=>{
                        return  f.created_at >= searchFromDate && f.created_at <= searchToDate; 
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
       const query_url = query(collection(db, "openingStocks"), orderBy("created_at", "desc"));
       const snapShot=await getDocs(query_url)
       const stocks=ss.stocks(snapShot);
      
        this.stocksData=[];
        this.stocksData=stocks;
        this.isLoading=false;
        this.isUpdating=false;
        this.searchStocks();
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