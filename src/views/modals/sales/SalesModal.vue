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
        this.setDate();

       const myModalEl = document.getElementById('salesModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   

       this.isLoading=true;   
       this.fetchSales();  
            
   },
 
   computed:{
       user(){
           return this.$store.getters.user;
       },
       
   },
 
   methods:{
        setDate(){
            let date = new Date();
            let firstDay = new Date(date.getFullYear(), date.getMonth(),1+1).toISOString().substr(0, 10);
            let lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 1).toISOString().substr(0, 10);
            this.search_by_from_sale_date=firstDay
            this.search_by_to_sale_date=lastDay
        },
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
            this.setDate();
           this.searchSales();
       },
       searchSales(){        
        
           if(this.search_by_voucher_no){
               this.sales=this.salesData.filter((f)=>{
                       return f.voucher_no===Number(this.search_by_voucher_no);
               })
           }else if(this.search_by_from_sale_date && this.search_by_to_sale_date){
                const date=new Date();         
                let fDate=new Date(this.search_by_from_sale_date)
                fDate.setHours("0", "0", "0")
                const searchFromDate=Timestamp.fromDate(fDate)
                let tDate=new Date(this.search_by_to_sale_date)
                tDate.setHours("23", "59", "59")
                const searchToDate=Timestamp.fromDate(tDate)
           
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
           this.sales=this.sales.sort((a,b)=>{
                const date_a=new Date(a.sale_date.toDate()).getTime();
                const date_b=new Date(b.sale_date.toDate()).getTime();
                return date_a - date_b;
            }).reverse()
       },
     
    async fetchSales(){
        const date=new Date();         
        let fDate=new Date(this.search_by_from_sale_date)
        fDate.setHours("0", "0", "0")
         const from_date=Timestamp.fromDate(fDate)
         let tDate=new Date(this.search_by_to_sale_date)
        tDate.setHours("23", "59", "59")
        const to_date=Timestamp.fromDate(tDate)
        
       const query_url = query(collection(db, "sales"), where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
       onSnapshot(query_url, async(snapShot) =>{                
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
