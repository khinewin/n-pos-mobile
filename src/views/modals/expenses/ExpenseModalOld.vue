<template lang="">
    <!-- Expense Modal -->
    <div class="modal fade" id="expenseModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="expenseModalLabel" aria-hidden="true">
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
                                                    <flat-pickr class="form-control-plaintext border-bottom" v-model="search_by_from_date" id="exp_from_date" :config="config" />
                                                    <label for="exp_from_date">{{t('from_date')}}</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="input-group mb-2">
                                                <div class="form-floating">
                                                    <flat-pickr class="form-control-plaintext border-bottom" v-model="search_by_to_date" id="exp_to_date" :config="config" />
                                                    <label for="exp_to_date">{{t('to_date')}}</label>
                                                </div>
                                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                               </div>
                                           </div>   
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="exp_search_voucher" @paste="searchExpenses" v-model="search_by_voucher_no" @keypress.enter="searchExpenses" placeholder="Voucher no." class="form-control-plaintext border-bottom">
                                                <label for="exp_search_voucher" class="">{{t('invoice_no')}}</label>
                                            </div>
                                           </div>
                                           <div class="col-6 col-md-3 col-lg-12">
                                            <div class="form-floating">
                                                <input type="search" id="exp_search_remark" v-model="search_by_remark" @input="inputSearchRemark" placeholder="Remark" class="form-control-plaintext border-bottom">
                                                <label for="exp_search_remark" class="">{{t('notes')}}</label>
                                            </div>
                                           </div>                                   
                                    </div>
                                    <div class="row mt-2">
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark clear-search-expense" @click="fetchExpenses"><i class="fa-solid fa-search"></i> {{t('search_link')}}</button>
                                        </div>
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-expense-btn" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button>
                                            </div>
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark close-expense" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}}</button>
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
                                                <td class="col-2">{{t('invoice_no')}}</td>
                                                <td class="col-2">{{t('date')}}</td>
                                                <td class="col-2">{{t('total')}}</td>
                                                <td class="col-6">{{t('notes')}}</td>
                                            </tr>                                
                                            <tr class="change-cursor" v-if="expenses.length > 0" v-for="e in expenses" :key="e.id" @click="setExpense(e)" data-bs-dismiss="modal">
                                                <td class="text-center">{{e.voucher_no}}</td>
                                                <td class="text-center">{{showTime(e.date)}}</td>
                                                <td class="text-end fw-bold">{{fM(e.total_amount)}}</td>
                                                <td>{{e.remark}}</td>
                                            </tr>
                                            <tr v-if="expenses.length === 0 && !isLoading">
                                                <td colspan="5" class="text-center text-muted py-4">No expenses found.</td>
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
import {Timestamp, collection, getDocs, where, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from '@/lib/helper'
import FlatPickr from 'vue-flatpickr-component';
import {lang} from "@/lib/language"

export default {
    components:{
        FlatPickr
    },
   data(){
       return{
           expenses:[],
           expensesData: [],
           search_by_voucher_no:"",
           search_by_from_date:"",
           search_by_to_date:"",
           search_by_remark:"",
           isLoading:false,
       }
   },
  
   mounted() {    
        this.setDate();
       const myModalEl = document.getElementById('expenseModal')
       myModalEl.addEventListener('show.bs.modal', event => {   
          this.fetchExpenses();             
       })   
     
            
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
            this.search_by_from_date = hp.setToday();
            this.search_by_to_date = hp.setToday();
        },
       setExpense(e){
           this.$emit("setExpense", e)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_voucher_no = "";
           this.search_by_remark = "";
            this.setDate();
            this.fetchExpenses();
       },
       inputSearchRemark(){
            this.search_by_remark = event.target.value;
            this.searchExpenses();
       },
         

    searchExpenses(){     
        if(this.search_by_remark){
            this.expenses = this.expensesData.filter((f) => f.remark && f.remark.toLowerCase().includes(this.search_by_remark.toLowerCase()))
       }else
       if(this.search_by_voucher_no.length > 0){
            this.expenses = this.expensesData.filter((f) => f.voucher_no && f.voucher_no.toLowerCase().includes(this.search_by_voucher_no.toLowerCase()))
       }else{
            this.expenses = this.expensesData;
       }
    },   
    async fetchExpenses() {
            this.expenses = [];
            this.isLoading = true;

            try {
                let expensesQuery;
                if (this.search_by_from_date && this.search_by_to_date) {
                    const from_date = Timestamp.fromDate(hp.searchFromDate(this.search_by_from_date));
                    const to_date = Timestamp.fromDate(hp.searchToDate(this.search_by_to_date));
                    expensesQuery = query(
                        collection(db, "expenses"),
                        where("date", ">=", from_date),
                        where("date", "<=", to_date),
                        orderBy("date", "desc")
                    );
                } else {
                    expensesQuery = query(
                        collection(db, "expenses"),
                        orderBy("date", "desc")
                    );
                }
                const snapShot = await getDocs(expensesQuery);
                const expenses = snapShot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
                this.expensesData = expenses;
                this.expenses = expenses;
            } catch (error) {
                console.error("Error fetching expenses:", error);
            } finally {
                this.isLoading = false;
            }
        },

      showTime(t){
           return hp.showTimeHourShort(t)
       },
      fM(amount) {
          return hp.formattedAmount(amount)
      },
   }
   
}
</script>
<style lang="">
   
</style>
