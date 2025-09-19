<template lang="">       
    <div>          
      <!-- Products Modal -->
    <div class="modal fade" id="customerModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
          <div class="modal-content"> 
           <div class="card border-0">
            <div class="card-body p-2">
               <div class="row g-2">
                   <div class="col-lg-3">
                       <div class="card shadow-sm">
                        <div class="card-body p-2 small">
                            <div class="row g-2">
                                <!--<div class="col-6  col-sm-4 col-lg-12">
                                    <div class="form-floating">
                                        <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchCustomer" placeholder="Code" class="form-control-plaintext border-bottom">
                                        <label for="search_by_code" class="">Code</label>
                                    </div>
                                </div>-->
                                <div class="col-6 col-sm-3 col-lg-12">
                                    <div class="form-floating">
                                        <input type="search" id="search_by_name" v-model="search_by_name" @keypress.enter="searchCustomer" placeholder="Description" class="form-control-plaintext border-bottom">
                                        <label for="search_by_name" class="">{{t('name')}}</label>
                                    </div> 
                                </div>
                                <div class="col-6 col-sm-3 col-lg-12">
                                    <div class="form-floating">
                                        <input type="search" id="search_by_address" v-model="search_by_address" @keypress.enter="searchCustomer" placeholder="Address" class="form-control-plaintext border-bottom">
                                         <label for="search_by_address" class="">{{t('address')}}</label>
                                </div>
                                </div> 
                                <div class="col-4 col-sm-3 col-lg-4 d-grid">
                                    <button class="btn btn-sm text-dark clear-search-product" @click="searchCustomer"><i class="fa-solid fa-search"></i> {{t('search_link')}}</button>
                                </div>
                                <div class="col-4 col-sm-3 col-lg-4 d-grid"><button class="btn btn-sm text-dark clear-search-customer" @click="clearSearch">
                                    <i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}} </button>
                                </div>
                                <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateCustomer">
                                    <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                </button></div>-->
                                <div class="col-4 col-sm-3 col-lg-4 d-grid"><button class="btn btn-sm text-dark close-customer" data-bs-dismiss="modal">
                                    <i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}} </button>
                                </div>     
                             </div> 
                        </div>
                       </div>                                                                                                          
                   </div>                        
                   <div class="col-lg-9">
                       <div class="card shadow-sm">
                           <div class="card-body p-0 vh-100 overflow-y-auto">
                               <table class="table table-bordered table-hover mb-0">
                                   <tbody class="small">
                                       <tr class="text-center table-secondary fw-bold sticky-top z-3 change-cursor " >
                                           <!--<td class="col-1" @click="sortCustomers('code')"><i class="fa-solid fa-sort"></i> Code</td>-->
                                           <td @click="sortCustomers('name')" class="col-1"><i class="fa-solid fa-sort"></i> {{t('name')}}</td>
                                           <td class="col-1" @click="sortCustomers('phone')"><i class="fa-solid fa-sort"></i> {{t('phone')}}</td>
                                           <td class="col-1" @click="sortCustomers('address')"><i class="fa-solid fa-sort"></i> {{t('address')}}</td>
                                           <td class="col-1" @click="sortCustomers('created_at')"><i class="fa-solid fa-sort"></i> {{t('member_since')}}</td>
                                       </tr>
                                   </tbody>
                                   <tbody>
                                     <tr @click="setCustomer(default_customer)" data-bs-dismiss="modal" class="change-cursor">
                                        <!--<td class="text-center">{{default_customer.code}}</td>-->
                                        <td>{{default_customer.name}}</td>
                                        <td class="text-center">{{default_customer.phone}}</td>
                                        <td>{{default_customer.address}}</td>
                                        <td class="text-center">{{showTime(default_customer.created_at)}}</td>
                                     </tr>
                                       
                                       <tr class="change-cursor" data-bs-dismiss="modal" @click="setCustomer(p)" v-if="customers.length > 0"  v-for="(p, i) in customers" :key="p.id" >                                         
                                           <!--<td :class="{'table-secondary': sort_item ==='code'}" class="text-center">
                                                {{p.code}}
                                            </td>-->
                                           <td :class="{'table-secondary': sort_item ==='name'}">
                                                {{p.name}}
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='phone'}" class="text-center">
                                                {{p.phone}}
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='address'}">
                                                {{p.address}}
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='created_at'}" class="text-center">{{showTime(p.created_at)}}</td>
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
  