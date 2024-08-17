<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="stocksModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content">               
           <div>
               <div class="card min-vh-100">
                   <div class="card-body py-3 px-1">
                    <div class="row g-0 m-0">
                        <div class="col-3 row">                                                         
                                <div class="form-floating col-12 mb-2">
                                        <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchStocks" placeholder="Voucher no." class="form-control form-control-sm">
                                        <label for="search_by_voucher_no" class="small ms-2">Voucher no.</label>
                                </div>
                                <div class="form-floating col-12 mb-2">
                                     <input type="date" v-model="search_by_from_date" @change="searchStocks" placeholder="From pur date" class="form-control">
                                     <label for="search_by_from_pur_date" class="small ms-2">From pur date</label>
                                  </div>
                                  <div class="form-floating  mb-2">
                                     <input type="date" v-model="search_by_to_date" @change="searchStocks" placeholder="To pur date" class="form-control">
                                     <label for="search_by_to_pur_date" class="small ms-2">To pur date</label>
                                  </div>                             
                                <div class="col-4">
                                 <button class="btn text-dark" @click="clearSearch">Clear</button>
                                 </div>
                                 <div class="col-4">
                                     <button class="btn text-primary" @click="updatestocks">Update</button>
                                 </div>
                                 <div class="col-4">
                                     <button class="btn text-danger" data-bs-dismiss="modal" aria-label="Close">Close</button>
                                 </div>                                                                              
                        </div>
                        <div class="col-9 table-responsive">
                            <table class="table table-bordered table-hover m-0">
                                <tbody class="small text-center">
                                    <tr class="table-secondary">
                                        <td>Voucher no.</td>
                                        <td>Opening date</td>                                    
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
     </div>
     <!--end modal-->
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
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
       }
   },
  
   mounted() {
    
       const myModalEl = document.getElementById('stocksModal')
       myModalEl.addEventListener('show.bs.modal', event => {
                
       })   
            
   },
   created(){
       if(this.user !==null){          
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
                   this.fetchStocks();           
           }
       }
     
   },
   methods:{
       updatestocks(){
               this.stocks=[]
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
       this.isLoading=true;    
       const query_url = query(collection(db, "openingStocks"), where("uid", "==", this.user.uid), orderBy("created_at", "desc"));
       const snapShot=await getDocs(query_url)
       let stocks=[];
       snapShot.forEach((doc) => {         
           let stock = {
               id: doc.id,
               uid: doc.data().uid,
               voucher_no: doc.data().voucher_no,
               created_at : doc.data().created_at,          
               products : doc.data().products,    
               total_retail_amount:doc.data().total_retail_amount,
               total_whole_sale_amount: doc.data().total_whole_sale_amount,     
               total_pur_amount: doc.data().total_pur_amount,                                     
           };               
                           
           stocks.push(stock);     
                       
       });
       this.stocksData=stocks;
     //  this.$store.dispatch("setProducts", products)
       this.isLoading=false;
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