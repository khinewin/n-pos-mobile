<template>       
    <div>          
      <!-- Customer Modal -->
    <div class="modal fade" id="customerModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
          <div class="modal-content bg-light"> 
            <div class="container-fluid p-2">
                <div class="row g-2">
                    <!-- Left Sidebar: Search & Actions -->
                    <div class="col-lg-3">
                        <div class="card border-0 shadow-sm rounded-3">
                            <div class="card-body p-3">
                                <h6 class="fw-bold text-muted text-uppercase small mb-3">
                                    <i class="fa-solid fa-filter me-2"></i>Search Filters
                                </h6>
                                
                                <!-- Name Input -->
                                <div class="mb-2">
                                    <label class="small text-muted text-uppercase fw-bold mb-1">{{t('name')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0">
                                            <i class="fa-solid fa-user text-secondary"></i>
                                        </span>
                                        <input 
                                            type="search" 
                                            id="search_by_name" 
                                            v-model="search_by_name" 
                                            @keypress.enter="searchCustomer" 
                                            placeholder="Search by name" 
                                            class="form-control bg-light border-0">
                                    </div>
                                </div>

                                <!-- Address Input -->
                                <div class="mb-2">
                                    <label class="small text-muted text-uppercase fw-bold mb-1">{{t('address')}}</label>
                                    <div class="input-group">
                                        <span class="input-group-text bg-light border-0">
                                            <i class="fa-solid fa-location-dot text-secondary"></i>
                                        </span>
                                        <input 
                                            type="search" 
                                            id="search_by_address" 
                                            v-model="search_by_address" 
                                            @keypress.enter="searchCustomer" 
                                            placeholder="Search by address" 
                                            class="form-control bg-light border-0">
                                    </div>
                                </div>

                                <!-- Action Buttons -->
                                <div class="d-flex gap-2 mb-3">
                                    <button class="btn btn-primary flex-fill shadow-sm fw-medium" @click="searchCustomer">
                                        <i class="fa-solid fa-search me-2"></i> {{t('search_link')}}
                                    </button>
                                    <button class="btn btn-light flex-fill shadow-sm fw-medium" @click="clearSearch">
                                        <i class="fa-solid fa-filter-circle-xmark me-2"></i> {{t('clear_link')}}
                                    </button>
                                    <button class="btn btn-light flex-fill shadow-sm fw-medium text-danger" data-bs-dismiss="modal">
                                        <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                                    </button>
                                </div>

                                <!-- Manage Customers Link -->
                                <div class="border-top pt-3">
                                    <router-link class="btn btn-success w-100 shadow-sm fw-medium" :to="{name : 'Customers'}">
                                        <i class="fa-solid fa-users me-2"></i> {{t('customers')}}
                                    </router-link>
                                </div>
                            </div>
                        </div>                                                                                           
                    </div>

                    <!-- Right: Customers Table -->
                    <div class="col-lg-9">
                        <div class="card border-0 shadow-sm rounded-3">
                            <div class="card-body p-0">
                                <div class="vh-100 overflow-y-auto">
                                    <table class="table table-hover mb-0">
                                        <thead class="position-sticky top-0 z-3">
                                            <tr class="table-light fw-bold cursor-pointer">
                                                <th @click="sortCustomers('name')" class="col-3 border-0">
                                                    <i class="fa-solid fa-sort me-1"></i>
                                                    <i class="fa-solid fa-user me-1"></i>{{t('name')}}
                                                </th>
                                                <th class="col-2 text-center border-0" @click="sortCustomers('phone')">
                                                    <i class="fa-solid fa-sort me-1"></i>
                                                    <i class="fa-solid fa-phone me-1"></i>{{t('phone')}}
                                                </th>
                                                <th class="col-4 border-0" @click="sortCustomers('address')">
                                                    <i class="fa-solid fa-sort me-1"></i>
                                                    <i class="fa-solid fa-location-dot me-1"></i>{{t('address')}}
                                                </th>
                                                <th class="col-2 text-center border-0" @click="sortCustomers('created_at')">
                                                    <i class="fa-solid fa-sort me-1"></i>
                                                    <i class="fa-solid fa-calendar me-1"></i>{{t('member_since')}}
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody class="small">
                                            <!-- Default Customer -->
                                            <tr @click="setCustomer(default_customer)" data-bs-dismiss="modal" class="cursor-pointer bg-light" style="transition: all 0.2s;">
                                                <td class="fw-bold">
                                                    <i class="fa-solid fa-star text-warning me-2"></i>{{default_customer.name}}
                                                </td>
                                                <td class="text-center text-muted">{{default_customer.phone}}</td>
                                                <td class="text-muted">{{default_customer.address}}</td>
                                                <td class="text-center">
                                                    <span class="badge bg-secondary">{{showTime(default_customer.created_at)}}</span>
                                                </td>
                                            </tr>
                                            
                                            <!-- Customer List -->
                                            <tr 
                                                class="cursor-pointer" 
                                                data-bs-dismiss="modal" 
                                                @click="setCustomer(p)" 
                                                v-if="customers.length > 0"  
                                                v-for="(p, i) in customers" 
                                                :key="p.id"
                                                style="transition: all 0.2s;">
                                                <td :class="{'table-secondary': sort_item ==='name'}" class="fw-medium">
                                                    {{p.name}}
                                                </td>
                                                <td :class="{'table-secondary': sort_item ==='phone'}" class="text-center text-muted">
                                                    {{p.phone}}
                                                </td>
                                                <td :class="{'table-secondary': sort_item ==='address'}" class="text-muted">
                                                    {{p.address}}
                                                </td>
                                                <td :class="{'table-secondary': sort_item ==='created_at'}" class="text-center">
                                                    <span class="badge bg-light text-dark border">{{showTime(p.created_at)}}</span>
                                                </td>
                                            </tr>

                                            <!-- Empty State -->
                                            <tr v-if="!isLoading && customers.length === 0">
                                                <td colspan="4" class="text-center text-muted py-5">
                                                    <i class="fa-solid fa-users fa-3x mb-3 opacity-25"></i>
                                                    <div class="h6">No customers found</div>
                                                    <small>Try adjusting your search filters</small>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>

                                    <!-- Loading State -->
                                    <div v-if="isLoading" class="d-flex justify-content-center align-items-center py-5">
                                        <div class="text-center">
                                            <div class="spinner-border text-primary mb-3" role="status">
                                                <span class="visually-hidden">Loading...</span>
                                            </div>
                                            <div class="text-muted">Loading customers...</div>
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
     
    </template>
   <script>
   import PageHeader from "@/views/partials/PageHeader.vue"
    import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
   import {Timestamp, collection, addDoc, onSnapshot, query, where, getDocs, limit, getDoc, doc, updateDoc, deleteDoc, orderBy} from 'firebase/firestore'
   import db from '@/firebase/firestore'
   import * as ss from "@/lib/snapshot/customers"
   import * as search from "@/lib/search"

   export default {
       components: {
               PageHeader
       },   
  
       data(){
           return{
            code :"",
            name:"",
            phone:"",
            address:'',
            created_at:"",
               customers : [],
               isLoading: false,
               sortBy:"asc",
               sort_item:"",
               search_by_code:"",
               search_by_name:"",
               search_by_address:"",
               customersData:[],
               editItem:{},
               isUpdating:false,
               isSaving:false,
               msg : {
                   type :"",
                   info :"",
               },
               default_customer: {
                    id: 123456789,
                    code: 123456789,
                    name:"Default Customer",
                    phone:"09xxxxxxxxx",
                    address :"",
                    created_at : Timestamp.fromDate(new Date()),
                },
               unsubCustomers:null,
               limitData: 100,
           }
       },
     
       mounted() {
        //const name=document.getElementById("name");
        //name.focus();
        this.code= new Date().getTime()
        this.created_at=new Date().toISOString().substr(0, 10)
        this.isLoading=true;       
            const myModalEl = document.getElementById('customerModal')
            myModalEl.addEventListener('show.bs.modal', event => {
                if(this.customersData.length===0){
                        this.fetchCustomer();
                }            
            })
       },
       unmounted() {
            if(this.unsubCustomers){
                this.unsubCustomers=null
            }
       },
       computed:{
           user(){
               return this.$store.getters.user;
           },
           t() {
            return (key) => this.$store.getters.t(key);
        },
           /*default_customer(){
                return this.$store.getters.default_customer;
           }*/
       },
       watch:{
          
           msg:{
               handler(val){
                   if(val.type){
                       setTimeout(()=>{
                           this.msg.type="";
                           this.msg.info="";
                       }, 2000)
                   }
               },deep:true
           }
       },
       methods:{
          
            setCustomer(p){
                this.$emit("setCustomer", p)
            },
           clearSearch(){
               this.search_by_code="";
               this.search_by_name="";
               this.search_by_address="";
               this.searchCustomer();
           },
           async searchCustomer() {
            this.customers = [];
            this.isLoading = true;
            try {
                if (this.search_by_name) {
                    const query_url = query(
                        collection(db, "customers"), where("name_keywords", "array-contains", this.search_by_name.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const cus = ss.customers(snapShot);
                    this.customers = cus;
                   
                } else if (this.search_by_address) {
                    const query_url = query(
                        collection(db, "customers"), where("address_keywords", "array-contains", this.search_by_address.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const cus = ss.customers(snapShot);
                    this.customers = cus;                   
                } else {
                    this.customers = this.customersData;                   
                }
            } catch (error) {
                console.error("Error searching customers:", error);
            } finally {
                this.isLoading = false;
            }
        },     
           sortCustomers(sort){   
               this.customers=[];
               this.sort_item=sort;
               if(this.sortBy=="asc"){
                   this.sortBy="desc"
                   this.customers=this.customersData.sort((a,b)=>{
                       const item_a=a.name.toUpperCase();
                       const item_b=b.name.toUpperCase();
                       const com_a=a.address.toUpperCase();
                       const com_b=b.address.toUpperCase();
                       const date_a=new Date(a.created_at.toDate()).getTime();
                       const date_b=new Date(b.created_at.toDate()).getTime();
                       switch(sort){
                           case "phone":
                               return a.phone - b.phone;
                           break;
                           case "name":
                             return item_a < item_b ? 1 : -1;
                           break;
                           case "code":
                               return a.code - b.code;
                           break;
                           case "address":
                           return com_a < com_b ? 1 : -1;
                           break;
                           case "created_at":
                              return  date_a - date_b;
                           break;
                       }
                   })
               }else{
                   this.sortBy="asc"
                   this.customers=this.customersData.sort((a,b)=>{
                       const item_a=a.name.toUpperCase();
                       const item_b=b.name.toUpperCase();
                       const com_a=a.address.toUpperCase();
                       const com_b=b.address.toUpperCase();
                       const date_a=new Date(a.created_at.toDate()).getTime();
                       const date_b=new Date(b.created_at.toDate()).getTime();
                       switch(sort){
                           case "phone":
                               return a.phone + b.phone;
                           break;
                           case "name":
                               return item_a < item_b ? -1 : 1;
                           break;
                           case "code":
                               return a.code + b.code;
                           break;
                           case "address":
                               return com_a < com_b ? -1 : 1;
                           break;
                           case "created_at":
                              return  date_a + date_b;
                           break;
                       }
                   })
               }            
           },
           
           async fetchCustomer(){                    
               const query_url = query(collection(db, "customers"), orderBy("created_at", "desc"), limit(this.limitData));
              this.unsubCustomers= onSnapshot(query_url, async(snapShot) =>{                 
                    const customers=ss.customers(snapShot)       
                    this.customersData=customers;
                    this.customers=customers;
                    this.isLoading=false;
            });  
          
               
          } ,
          showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        }
       },
       
   }
   </script>
   <style lang="">
       
   </style>
  