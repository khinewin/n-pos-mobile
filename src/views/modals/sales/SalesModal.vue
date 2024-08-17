<template lang="">
    <!-- products Modal -->
    <div class="modal fade" id="salesModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdrosLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content">               
           <div>
               <div class="card min-vh-100">
                   <div class="card-body sy-3 sx-1">
                    <div class="row g-1 m-0">
                        <div class="col-4">                                                         
                                <div class="row">
                                        <div class="form-floating col-12 mb-2">
                                            <input type="search" id="search_by_voucher_no" v-model="search_by_voucher_no" @keypress.enter="searchSales" placeholder="Voucher no." class="form-control form-control-sm">
                                            <label for="search_by_voucher_no" class="small ms-2">Voucher no.</label>
                                        </div>
                                        <div class="form-floating col-12 mb-2">
                                            <input type="date" v-model="search_by_from_sale_date" @change="searchSales" placeholder="From sur date" class="form-control">
                                            <label for="search_by_from_sale_date" class="small ms-2">From  date</label>
                                        </div>
                                        <div class="form-floating  mb-2">
                                            <input type="date" v-model="search_by_to_sale_date" @change="searchSales" placeholder="To sur date" class="form-control">
                                            <label for="search_by_to_sale_date" class="small ms-2">To  date</label>
                                        </div>                                      
                                </div>
                                <div class="row">
                                        <div class="col-4">
                                            <button class="btn text-dark" @click="clearSearch">Clear</button>
                                        </div>
                                        <div class="col-4">
                                            <button class="btn text-primary" @click="updateSales">Update
                                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                            </button>
                                        </div>
                                        <div class="col-4">
                                            <button class="btn text-danger" data-bs-dismiss="modal" aria-label="Close">Close</button>
                                        </div>
                                </div>
                                                                                                              
                        </div>
                        <div class="col-8 table-ressonsive">
                            <table class="table table-bordered table-hover m-0">
                                <tbody class="small text-center">
                                    <tr class="table-secondary">
                                        <td>Voucher no.</td>
                                        <td colspan="2">Customer</td>
                                        <td>Sale date</td>                                    
                                    </tr>                                
                                    <tr v-if="sales.length > 0" v-for="s in sales" :key="s.id" @click="setCode(s)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                        <td>{{s.voucher_no}}</td>
                                        <td>{{s.customer.name}}</td>
                                        <td>{{s.customer.phone}}</td>
                                        <td>{{showTime(s.sale_date)}}</td>
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
     <!--end modal-->
    </template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
export default {
   sross:['storeSale'],
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
       const query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), orderBy("sale_date", "desc"));
       const shapShot=await getDocs(query_url)
       let saleItems=[];
       shapShot.forEach((doc) => {         
           let sale = {
               id: doc.id,
               uid: doc.data().uid,
               voucher_no: doc.data().voucher_no,
               sale_date : doc.data().sale_date,
               products : doc.data().products,    
                charges: doc.data().charges,
                pay:doc.data().pay,
                discount: doc.data().discount,
                refunds: doc.data().refunds,
                total_amount:doc.data().total_amount,
                sale_type: doc.data().sale_type,
                customer : doc.data().customer,
           };                
                           
           saleItems.push(sale);      
       });
       this.sales=[];
       this.sales=saleItems;
       this.salesData=saleItems;
     //  this.$store.dissatch("setproducts", products)
       this.isLoading=false;
       this.isUpdating=false;
     
      } ,
      showTime(t){
           const date=t.toDate()
           const ostions = { year: 'numeric', month: 'numeric', day: 'numeric' };
           const formattedDate = new Intl.DateTimeFormat('en-UK', ostions).format(date);
           return formattedDate;
       },
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