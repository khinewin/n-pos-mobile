<template>
    <!-- Suppliers Modal -->
    <div class="modal fade" id="searchSupplier" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
            <div class="modal-content bg-light p-2">               
                <div class="container-fluid">
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
                                                @keypress.enter="searchSuppliers" 
                                                placeholder="Search by name" 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Company Input -->
                                    <div class="mb-2">
                                        <label class="small text-muted text-uppercase fw-bold mb-1">{{t('company')}}</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-0">
                                                <i class="fa-solid fa-building text-secondary"></i>
                                            </span>
                                            <input 
                                                type="search" 
                                                id="search_by_company" 
                                                v-model="search_by_company" 
                                                @keypress.enter="searchSuppliers" 
                                                placeholder="Search by company" 
                                                class="form-control bg-light border-0">
                                        </div>
                                    </div>

                                    <!-- Action Buttons -->
                                    <div class="d-flex gap-2 mb-3">
                                        <button class="btn btn-primary flex-fill shadow-sm fw-medium" @click="searchSuppliers">
                                            <i class="fa-solid fa-search me-2"></i> {{t('search_link')}}
                                        </button>
                                        <button class="btn btn-light flex-fill shadow-sm fw-medium" @click="clearSearch">
                                            <i class="fa-solid fa-filter-circle-xmark me-2"></i> {{t('clear_link')}}
                                        </button>
                                        <button class="btn btn-light flex-fill shadow-sm fw-medium text-danger" data-bs-dismiss="modal">
                                            <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                                        </button>
                                    </div>

                                    <!-- Manage Suppliers Link -->
                                    <div class="border-top pt-3">
                                        <router-link class="btn btn-success w-100 shadow-sm fw-medium" :to="{name : 'Suppliers'}">
                                            <i class="fa-solid fa-truck-field me-2"></i> {{t('suppliers')}}
                                        </router-link>
                                    </div>
                                </div>
                            </div>                                                                                           
                        </div>

                        <!-- Right: Suppliers Table -->
                        <div class="col-lg-9">
                            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                                <div class="card-body p-0">
                                    <div class="vh-100 overflow-y-auto">
                                        <table class="table table-hover mb-0 align-middle">
                                            <thead class="position-sticky top-0 z-3">
                                                <tr class="table-primary-subtle fw-bold cursor-pointer">
                                                    <th @click="sortSuppliers('name')" class="col-3 border-0 py-3">
                                                        <i class="fa-solid fa-sort me-1"></i>
                                                        <i class="fa-solid fa-user me-1"></i>{{t('name')}}
                                                    </th>
                                                    <th class="col-2 text-center border-0 py-3" @click="sortSuppliers('phone')">
                                                        <i class="fa-solid fa-sort me-1"></i>
                                                        <i class="fa-solid fa-phone me-1"></i>{{t('phone')}}
                                                    </th>
                                                    <th class="col-4 border-0 py-3" @click="sortSuppliers('company')">
                                                        <i class="fa-solid fa-sort me-1"></i>
                                                        <i class="fa-solid fa-building me-1"></i>{{t('company')}}
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3" @click="sortSuppliers('created_at')">
                                                        <i class="fa-solid fa-sort me-1"></i>
                                                        <i class="fa-solid fa-calendar me-1"></i>{{t('member_since')}}
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">
                                                <!-- General Supplier -->
                                                <tr @click="setCode(general_supplier)" data-bs-dismiss="modal" class="cursor-pointer bg-light" style="transition: all 0.2s;">
                                                    <td class="fw-bold">
                                                        <i class="fa-solid fa-star text-warning me-2"></i>{{general_supplier.name}}
                                                    </td>
                                                    <td class="text-center text-muted">{{general_supplier.phone}}</td>
                                                    <td class="text-muted">{{general_supplier.company || '-'}}</td>
                                                    <td class="text-center">
                                                        <span class="badge bg-secondary-subtle text-secondary">{{showTime(general_supplier.created_at)}}</span>
                                                    </td>
                                                </tr>
                                                
                                                <!-- Supplier List -->
                                                <tr 
                                                    class="cursor-pointer" 
                                                    data-bs-dismiss="modal" 
                                                    @click="setCode(p)" 
                                                    v-if="suppliers.length > 0"  
                                                    v-for="(p, i) in suppliers" 
                                                    :key="p.id"
                                                    style="transition: all 0.2s;">
                                                    <td :class="{'table-secondary': sort_item ==='name'}" class="fw-medium text-dark">
                                                        {{p.name}}
                                                    </td>
                                                    <td :class="{'table-secondary': sort_item ==='phone'}" class="text-center text-muted">
                                                        {{p.phone}}
                                                    </td>
                                                    <td :class="{'table-secondary': sort_item ==='company'}" class="text-dark fw-medium">
                                                        {{p.company || '-'}}
                                                    </td>
                                                    <td :class="{'table-secondary': sort_item ==='created_at'}" class="text-center">
                                                        <span class="badge bg-light text-dark border">{{showTime(p.created_at)}}</span>
                                                    </td>
                                                </tr>

                                                <!-- Empty State -->
                                                <tr v-if="!isLoading && suppliers.length === 0">
                                                    <td colspan="4" class="text-center text-muted py-5">
                                                        <i class="fa-solid fa-truck-field fa-3x mb-3 opacity-25"></i>
                                                        <div class="h6">No suppliers found</div>
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
                                                <div class="text-muted fw-medium">Loading suppliers...</div>
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
import {Timestamp, onSnapshot, collection, addDoc, query, where, limit, getDoc, getDocs, doc, updateDoc, deleteDoc, orderBy} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from '@/lib/snapshot/suppliers'
import * as search from "@/lib/search"

export default {
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
            limitData: 100,
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
                    const com_a=(a.company || "").toUpperCase();
                    const com_b=(b.company || "").toUpperCase();
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
                    const com_a=(a.company || "").toUpperCase();
                    const com_b=(b.company || "").toUpperCase();
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

<style scoped>
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
.cursor-pointer {
    cursor: pointer;
}
.cursor-pointer:hover {
    background-color: rgba(0,0,0,0.02);
}
</style>