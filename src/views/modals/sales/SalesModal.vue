<template lang="">
    <!-- products Modal -->
    <div class="modal fade" id="salesModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdrosLabel" aria-hidden="true">
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
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr  class="form-control-plaintext border-bottom" v-model="search_by_from_sale_date" id="from_date" :config="config" />
                                                    <label for="from_date">{{t('from_date')}}</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr  class="form-control-plaintext border-bottom" v-model="search_by_to_sale_date" id="to_date" :config="config" />
                                                    <label for="to_date">{{t('to_date')}}</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>   
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_voucher_no" @paste="searchSales" v-model="search_by_voucher_no" @keypress.enter="searchSales" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="search_by_voucher_no" class="">{{t('invoice_no')}}</label>
                                            </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="search_by_customer" v-model="search_by_customer" @input="inputSearchCustomer" placeholder="Customer name" class="form-control-plaintext border-bottom">
                                                <label for="search_by_customer" class="">{{t('customer')}}</label>
                                            </div>
                                           </div>                                   
                                    </div>
                                    <div class="row mt-2">
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark clear-search-product" @click="fetchSales"><i class="fa-solid fa-search"></i> {{t('search_link')}}</button>
                                        </div>
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm  text-dark clear-search-sale" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button>
                                            </div>
                                            <!--<div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="updateSales">
                                                    <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                </button>
                                            </div>-->
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark close-sale" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}}</button>
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
                                                <td>{{t('invoice_no')}}</td>
                                                <td class="">{{t('customer')}}</td>
                                                <td>{{t('phone')}}</td>
                                                <td>{{t('date')}}</td>
                                                <td class="">{{t('cashier')}}</td>                                    
                                            </tr>                                
                                            <tr class="change-cursor" v-if="sales.length > 0" v-for="s in sales.sort((a,b)=>a.user.uid.localeCompare(b.user.uid))" :key="s.id" @click="setCode(s)" data-bs-dismiss="modal">
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
import FlatPickr from 'vue-flatpickr-component';
import {lang} from "@/lib/language"

export default {
    components:{
        FlatPickr
    },
   data(){
       return{
           sales:[],
           salesData: [],
           search_by_voucher_no:"N-",
           search_by_from_sale_date:"",
           search_by_to_sale_date:"",
           search_by_customer:"",
            isUpdating:false,
           isLoading:false,
          
       }
   },
  
   mounted() {    
        this.setDate();
       const myModalEl = document.getElementById('salesModal')
       myModalEl.addEventListener('show.bs.modal', event => {                
       })   
       this.fetchSales();  
            
   },
 
   computed:{
        config(){
            return this.$store.getters.flat_pickr_config;
        },
       user(){
           return this.$store.getters.user;
       },       
        t() {
            return (key) => this.$store.getters.t(key);
        },
   },
 
   methods:{
        setDate(){
            this.search_by_from_sale_date=hp.setToday();
            this.search_by_to_sale_date=hp.setToday();
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
           this.search_by_voucher_no="N-";
           this.search_by_customer="";
            this.setDate();
            this.fetchSales();
         //  this.searchSales();
       },
       inputSearchCustomer(){
            this.search_by_customer=event.target.value;
            this.searchSales();
       },
         

    searchSales(){     
        if(this.search_by_customer){
            this.sales=this.salesData.filter((f)=>f.customer.name.toLowerCase().includes(this.search_by_customer.toLowerCase()))
       }else
       if(this.search_by_voucher_no.length>2){
            this.sales=this.salesData.filter((f)=>f.voucher_no==this.search_by_voucher_no)
       }else{
            this.sales=this.salesData;
       }
    },   
    async fetchSales() {
            this.sales=[];
            this.isLoading = true;
            this.isUpdating = true;

            try {
                let salesQuery;
                if (this.search_by_from_sale_date && this.search_by_to_sale_date) {
                    const from_date = Timestamp.fromDate(hp.searchFromDate(this.search_by_from_sale_date));
                    const to_date = Timestamp.fromDate(hp.searchToDate(this.search_by_to_sale_date));
                    salesQuery = query(
                        collection(db, "sales"),
                        where("sale_date", ">=", from_date),
                        where("sale_date", "<=", to_date),
                        orderBy("sale_date", "desc")
                    );
                } 
                const snapShot = await getDocs(salesQuery);
                const sales = ss.sales(snapShot);
                this.salesData = sales;
                this.sales = sales;
            } catch (error) {
                console.error("Error fetching sales:", error);
            } finally {
                this.isLoading = false;
                this.isUpdating = false;
            }
        },

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
