<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="searchSupplier" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-fullscreen">
         <div class="modal-content p-2">               
            <div class="row g-2">
                <div class="col-lg-3">
                   <div class="card  shadow-sm">
                     <div class="card-body p-2">
                        <div class="row g-2">
                            <!--<div class="col-6 col-sm-4 col-lg-12">
                                <div class="form-floating">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchSuppliers" placeholder="Code" class="form-control-plaintext border-bottom">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                            </div>-->
                            <div class="col-6 col-sm-3 col-lg-12">
                                <div class="form-floating ">
                                    <input type="search" id="search_by_name" v-model="search_by_name" @keypress.enter="searchSuppliers" placeholder="Name" class="form-control-plaintext border-bottom">
                                    <label for="search_by_name" class="small ">{{t('name')}}</label>
                                </div> 
                            </div>
                            <div class="col-sm-3 col-lg-12">
                                <div class="form-floating ">
                                    <input type="search" id="search_by_company" v-model="search_by_company" @keypress.enter="searchSuppliers" placeholder="Company" class="form-control-plaintext border-bottom">
                                    <label for="search_by_company" class="small">{{t("company")}}</label>
                                </div> 
                            </div>
                            <div class="col-4 col-sm-3 col-lg-4 d-grid">
                                <button class="btn btn-sm text-dark clear-search-product" @click="searchSuppliers"><i class="fa-solid fa-search"></i> {{t('search_link')}}</button>
                            </div>
                            <div class="col-4 col-sm-3 col-lg-4 d-grid"><button class="btn btn-sm text-dark clear-search-supplier" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button></div>
                            <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateSuppliers"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                </button>
                            </div>-->
                            <div class="col-4 col-sm-3 col-lg-4 d-grid">
                                <button class="btn btn-sm text-dark close-supplier" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}} </button>
                            </div>
                          </div> 
                     </div>
                   </div>                                                                                                          
                </div>                        
                <div class="col-lg-9">
                    <div class="card shadow-sm">
                        <div class="card-body p-2 pt-0  vh-100 overflow-y-auto">
                            <table class="table table-bordered table-hover mb-0">
                                <thead>
                                    <tr class="text-center table-secondary fw-bold sticky-top z-3 change-cursor">
                                        <td class="col-2" @click="sortSuppliers('name')"><i class="fa-solid fa-sort"></i> {{t('name')}}</td>
                                        <td class="col-2" @click="sortSuppliers('phone')"><i class="fa-solid fa-sort"></i> {{t('phone')}}</td>
                                        <td class="col-4" @click="sortSuppliers('company')"><i class="fa-solid fa-sort"></i> {{t('company')}}</td>
                                        <td class="col-2" @click="sortSuppliers('created_at')"><i class="fa-solid fa-sort"></i> {{t('member_since')}}</td>
                                    </tr>
                                </thead>
                                <tbody>                                    
                                   <tr @click="setCode(general_supplier)" data-bs-dismiss="modal" class="change-cursor">
                                        <td>{{general_supplier.name}}</td>
                                        <td class="text-center">{{general_supplier.phone}}</td>
                                        <td>{{general_supplier.company}}</td>
                                        <td class="text-center">{{showTime(general_supplier.created_at)}}</td>
                                   </tr>
                                    <tr  class="change-cursor" v-if="suppliers.length > 0"  v-for="(p, i) in suppliers" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal">                                         
                                        
                                        <td :class="{'table-secondary': sort_item ==='name'}">
                                            {{p.name}}
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='phone'}" class="text-center">
                                            {{p.phone}}
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='company'}">
                                            {{p.company}}
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
     <!--end modal-->
</template>
<script>
    import PageHeader from "@/views/partials/PageHeader.vue"
     import { useVuelidate  } from '@vuelidate/core'
    import { required, email ,helpers} from '@vuelidate/validators'
    import {Timestamp, onSnapshot, collection, addDoc, query, where, limit, getDoc, getDocs, doc, updateDoc, deleteDoc, orderBy} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    import * as ss from '@/lib/snapshot/suppliers'
    import * as search from "@/lib/search"
    export default {
        components: {
                PageHeader
        },
     
        data(){
            return{         
                suppliers : [],
                isLoading: false,
                sortBy:"asc",
                sort_item:"",
                search_by_code:"",
                search_by_name:"",
                search_by_company:"",
                suppliersData:[],
                editItem:{},
                isUpdating:false,
                msg : {
                    type :"",
                    info :"",
                },
                unsubSuppliers:null,
                general_supplier: {
                    id: 123456789,
                    code: 123456789,
                    name:"General Supplier",
                    phone:"09xxxxxxxxx",
                    company :"",
                    created_at : Timestamp.fromDate(new Date()),
                },
            }
        },
       
        mounted() {
            this.isLoading=true;       
            const myModalEl = document.getElementById('searchSupplier')
            myModalEl.addEventListener('show.bs.modal', event => {
                if(this.suppliersData.length===0){
                        this.fetchSuppliers();
                }            
            })
        },
        unmounted() {
            if(this.unsubSuppliers){
                this.unsubSuppliers();
            }
        },
        computed:{
            user(){
                return this.$store.getters.user;
            },
           t() {
            return (key) => this.$store.getters.t(key);
        },
        },
        watch:{
         
            msg:{
                handler(val){
                    if(val.type){
                        setTimeout(()=>{
                            this.msg.type="";
                            this.msg.info="";
                        }, 5000)
                    }
                },deep:true
            }
        },
        methods:{      
            updateSuppliers(){
                this.isUpdating=true;
                this.fetchSuppliers();
            },
            setCode(p){
                this.$emit("setCode", p)
            },
            clearSearch(){
                this.search_by_code="";
                this.search_by_name="";
                this.search_by_company="";
                this.searchSuppliers();
            },
            async searchSuppliers() {
            this.suppliers = [];
            this.isLoading = true;
            try {
                if (this.search_by_name) {
                    const query_url = query(
                        collection(db, "suppliers"), where("name_keywords", "array-contains", this.search_by_name.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const cus = ss.suppliers(snapShot);
                    this.suppliers = cus;
                   
                } else if (this.search_by_company) {
                    const query_url = query(
                        collection(db, "suppliers"), where("company_keywords", "array-contains", this.search_by_company.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const cus = ss.suppliers(snapShot);
                    this.suppliers = cus;                   
                } else {
                    this.suppliers = this.suppliersData;                   
                }
            } catch (error) {
                console.error("Error searching suppliers:", error);
            } finally {
                this.isLoading = false;
            }
        },      
            async fetchSuppliers(){ 
            const query_url=query(collection(db, "suppliers"),orderBy("created_at", "desc"), limit(this.limitData));           
            this.unsubSuppliers= onSnapshot(query_url, async(snapShot) =>{                  
                    const suppliers=ss.suppliers(snapShot);   
                    this.suppliersData=suppliers;
                    this.suppliers=suppliers;
                    this.isLoading=false;
            });                              
        } ,
           showTime(t){
             const date=t.toDate()
             const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
             const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
             return formattedDate;
         },
            sortSuppliers(sort){   
                this.suppliers=[];
                this.sort_item=sort;
                if(this.sortBy=="asc"){
                    this.sortBy="desc"
                    this.suppliers=this.suppliersData.sort((a,b)=>{
                        const item_a=a.name.toUpperCase();
                        const item_b=b.name.toUpperCase();
                        const com_a=a.company.toUpperCase();
                        const com_b=b.company.toUpperCase();
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
                            case "company":
                            return com_a < com_b ? 1 : -1;
                            break;
                            case "created_at":
                               return  date_a - date_b;
                            break;
                        }
                    })
                }else{
                    this.sortBy="asc"
                    this.suppliers=this.suppliersData.sort((a,b)=>{
                        const item_a=a.name.toUpperCase();
                        const item_b=b.name.toUpperCase();
                        const com_a=a.company.toUpperCase();
                        const com_b=b.company.toUpperCase();
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
                            case "company":
                                return com_a < com_b ? -1 : 1;
                            break;
                            case "created_at":
                               return  date_a + date_b;
                            break;
                        }
                    })
                }            
            },
            
           
        },
        
    }
   
    </script>
<style lang="">
   
</style>