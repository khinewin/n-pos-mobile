<template>
    <!-- Products Modal -->
    <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
            <div class="modal-content bg-light">               
                <div class="container-fluid p-2">              
                    <div class="row g-2">
                        <!-- Left Sidebar: Search & Selection Preview -->
                        <div class="col-lg-3">
                            <div class="row g-2">
                                <!-- Search Card -->
                                <div class="col-sm-6 col-lg-12">
                                    <div class="card border-0 shadow-sm rounded-3">
                                        <div class="card-body p-3">
                                            <h6 class="fw-bold text-muted text-uppercase small mb-3">
                                                <i class="fa-solid fa-filter me-2"></i>Search Filters
                                            </h6>
                                            
                                            <!-- Barcode Input -->
                                            <div class="mb-2">
                                                <label class="small text-muted text-uppercase fw-bold mb-1">{{t('barcode')}}</label>
                                                <div class="input-group">
                                                    <span class="input-group-text bg-light border-0">
                                                        <i class="fa-solid fa-barcode text-secondary"></i>
                                                    </span>
                                                    <input 
                                                        type="text" 
                                                        id="search_by_code"  
                                                        v-model="search_by_code"  
                                                        @keypress.enter="searchProducts" 
                                                        placeholder="Enter barcode" 
                                                        class="form-control bg-light border-0">
                                                </div>
                                            </div>

                                            <!-- Item Name Input -->
                                            <div class="mb-2">
                                                <label class="small text-muted text-uppercase fw-bold mb-1">{{t('item_name')}}</label>
                                                <div class="input-group">
                                                    <span class="input-group-text bg-light border-0">
                                                        <i class="fa-solid fa-box text-secondary"></i>
                                                    </span>
                                                    <input 
                                                        type="search" 
                                                        v-model="search_by_item_name" 
                                                        @keypress.enter="searchProducts" 
                                                        placeholder="Search product name" 
                                                        class="form-control bg-light border-0">
                                                </div>
                                            </div>

                                            <!-- Category Select -->
                                            <div class="mb-2">
                                                <label class="small text-muted text-uppercase fw-bold mb-1">{{t('category')}}</label>
                                                <div class="input-group">
                                                    <span class="input-group-text bg-light border-0">
                                                        <i class="fa-solid fa-tags text-secondary"></i>
                                                    </span>
                                                    <select 
                                                        id="search_by_category" 
                                                        class="form-select bg-light border-0" 
                                                        v-model="search_by_category"
                                                        @change="searchProducts">
                                                        <option value="">All Categories</option>
                                                        <option v-for="c in cats" :key="c.id" :value="c.cat_name">{{c.cat_name}}</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <!-- Action Buttons -->
                                            <div class="d-flex gap-2">
                                                <button class="btn btn-primary flex-fill shadow-sm fw-medium" @click="searchProducts">
                                                    <i class="fa-solid fa-search me-2"></i> {{t('search_link')}}
                                                </button>
                                                <button class="btn btn-light flex-fill shadow-sm fw-medium" @click="clearSearch">
                                                    <i class="fa-solid fa-filter-circle-xmark me-2"></i> {{t('clear_link')}}
                                                </button>
                                                <button class="btn btn-light flex-fill shadow-sm fw-medium text-danger" data-bs-dismiss="modal" aria-label="Close">
                                                    <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                                                </button>
                                            </div>
                                        </div>
                                    </div> 
                                </div>

                                <!-- Selection Preview Card -->
                                <div class="col-sm-6 col-lg-12">
                                    <div class="card border-0 shadow-sm rounded-3">
                                        <div class="card-header bg-white border-0 pb-0">
                                            <h6 class="fw-bold text-muted text-uppercase small mb-0">
                                                <i class="fa-solid fa-list-check me-2"></i>Selected Items ({{storeItems.length}})
                                            </h6>
                                        </div>
                                        <div class="card-body p-0 vh-30 overflow-y-auto dataContainer">
                                            <table class="table table-hover mb-0">
                                                <thead class="position-sticky top-0 z-3">
                                                    <tr class="table-light small fw-bold">
                                                        <th class="col-1 text-center border-0">#</th>
                                                        <th class="col-11 border-0">{{t('item_name')}}</th>
                                                    </tr>
                                                </thead>
                                                <tbody class="small">      
                                                    <tr v-if="storeItems.length > 0" v-for="(s, i) in storeItems" :key="s.id">
                                                        <td class="text-center text-muted">{{i+1}}</td>
                                                        <td class="text-nowrap text-truncate">{{s.item_name}}</td>                                            
                                                    </tr>
                                                    <tr v-if="storeItems.length === 0">
                                                        <td colspan="2" class="text-center text-muted py-4 text-nowrap">
                                                            <i class="fa-solid fa-clipboard-list fa-2x mb-2 opacity-25"></i>
                                                            <div class="small">No items selected</div>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>  
                                </div>
                            </div>
                        </div>

                        <!-- Right: Products Table -->
                        <div class="col-lg-9">
                            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                                <div class="card-body p-0">
                                    <div class="vh-100 overflow-y-auto">
                                        <table class="table table-hover mb-0 align-middle">
                                            <thead class="position-sticky top-0 z-3">
                                                <tr class="table-primary-subtle fw-bold">
                                                    <th class="col-1 text-center border-0 py-3">
                                                        <i class="fa-solid fa-barcode me-1"></i>{{t('barcode')}}
                                                    </th>
                                                    <th class="col-3 border-0 py-3">
                                                        <i class="fa-solid fa-box me-1"></i>{{t('item_name')}}
                                                    </th>
                                                    <th class="col-3 text-center border-0 py-3">
                                                        <i class="fa-solid fa-cubes me-1"></i>{{t('packing_size')}}
                                                    </th>
                                                    <th class="col-2 text-end border-0 py-3">
                                                        <i class="fa-solid fa-warehouse me-1"></i>{{t('inv_bal')}}
                                                    </th>
                                                    <th class="col-3 text-end border-0 py-3 pe-4">
                                                        <i class="fa-solid fa-money-bill-wave me-1"></i>{{t('pur_price')}}
                                                    </th>
                                                </tr>
                                            </thead>
                                            <tbody class="small">                                   
                                                <tr 
                                                    class="cursor-pointer" 
                                                    v-if="products.length > 0"  
                                                    v-for="p in products" 
                                                    :key="p.id" 
                                                    @click="setCode(p)"
                                                    style="transition: all 0.2s;">
                                                    <td class="text-center">
                                                        <span class="badge bg-light text-dark border">{{p.code_no}}</span>
                                                    </td>
                                                    <td class="fw-bold text-dark">{{p.item_name}}</td>
                                                    <td class="text-center">
                                                        <span class="badge bg-light text-primary border border-primary-subtle px-2 py-1">
                                                            {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                        </span>
                                                    </td>
                                                    <td class="text-end">
                                                        <span class="badge fw-bold" :class="p.qty > 10 ? 'bg-success-subtle text-success' : p.qty > 0 ? 'bg-warning-subtle text-warning' : 'bg-danger-subtle text-danger'">
                                                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}
                                                        </span>
                                                    </td>                                                  
                                                    <td class="text-end fw-bold text-primary pe-4 fs-6">
                                                        <span v-if="p.pur_price.smallest_unit">
                                                            {{p.pur_price.smallest_unit}} 
                                                            <small class="text-muted small">/ {{p.packing_size.smallest_unit}}</small>
                                                        </span>
                                                    </td>
                                                </tr>
                                                <tr v-if="!isLoading && products.length === 0">
                                                    <td colspan="5" class="text-center text-muted py-5">
                                                        <i class="fa-solid fa-box-open fa-3x mb-3 opacity-25"></i>
                                                        <div class="h6">No products found</div>
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
                                                <div class="text-muted fw-medium">Loading products...</div>
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
import {Timestamp, collection, addDoc, onSnapshot, getDocs, doc, setDoc, limit, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as lib from "@/lib/snapshot/products"
import * as hp  from "@/lib/helper"
import * as search from '@/lib/search'
import * as cat from "@/lib/snapshot/category"

export default {   
    props:['storeItems'],
    data(){
        return{
            products:[],
            productsData: [],
            search_by_code:"",
            search_by_item_name:"",
            isUpdating:false,
            isLoading:false,
            limitData: 100,
            cats:[],
            search_by_category:"",
        }
    },   
    mounted() {
        this.isLoading=true; 

        const myModalEl = document.getElementById('searchCode')
        myModalEl.addEventListener('show.bs.modal', event => {
                if(this.productsData.length===0){
                    this.fetchProducts();
                    this.fetchCategory();
                }                           
        })
    
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
        storeItems:{
            handler(val){
              if(val.length){
                    setTimeout(()=>{
                        this.autoScroll();
                    }, 100)
              }
            },deep:true
        
        },      
      
    },
    methods:{
        async fetchCategory(){                    
                const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
               const unsub= onSnapshot(query_url, async(snapShot) =>{
                    const cats=cat.category(snapShot);   
                    this.cats=cats;     
                    unsub();        
                })               
           } ,
        autoScroll(){            
            const container = document.querySelector(".dataContainer");
            if(container){
                container.scrollTo({
                    top: container.scrollHeight,
                    behavior: "smooth"
                });
            }
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
        updateProducts(){
                this.isUpdating=true;
                this.fetchProducts();
        },
        setCode(p){
            this.$emit("setCode", p)
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_item_name="";
            this.search_by_category="";
            this.searchProducts();
        },
        onPaste(){            
            setTimeout(()=>{
                this.setCodeOnPaste();
            }, 300)
        },
        setCodeOnPaste(){
            const item=this.productsData.filter((f)=>f.code_no==this.search_by_code);
            if(item.length > 0){
                this.setCode(item[0])
                this.search_by_code="";
            }
       },
      async searchProducts(){
            this.products=[]
            this.isLoading=true;
            if(this.search_by_code){
                const query_url = query(collection(db, "products"),  
                where("code_no", "==", this.search_by_code));
                    const unsub=onSnapshot(query_url, (snapShot)=>{
                        const products=lib.products(snapShot);   
                        this.products=products;
                        if(products.length > 0){
                            this.setCode(products[0]);
                        }
                        unsub()    
                        this.isLoading=false;
                })
            }else if(this.search_by_item_name){
                const query_url = query(collection(db, "products"),  
                where("keywords", "array-contains", this.search_by_item_name.toLowerCase()),  orderBy("created_at", "desc"), limit(this.limitData));
                    const unsub=onSnapshot(query_url, (snapShot)=>{
                        const products=lib.products(snapShot);   
                        this.products=products;
                        unsub()    
                        this.isLoading=false;
                })
            }else if (this.search_by_category) {
                    const query_url = query(
                        collection(db, "products"), where("category.cat_name","==", this.search_by_category), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const products = lib.products(snapShot);
                    this.products = products;
                    this.isLoading=false;
                  
                }else{
                this.products= this.productsData;                                         
            }
        },
      
        async fetchProducts(){
        const query_url = query(collection(db, "products"), orderBy("created_at", "desc"), limit(this.limitData));
        onSnapshot(query_url, async(snapShot) =>{                  
                    let products=lib.products(snapShot)      
                    this.productsData=products;
                    this.products=products;
                    this.isLoading=false;
                    this.isUpdating=false;
            });        
       
       } ,
       showTime(t){
            return hp.showTime(t)
        },
    }
    
}
</script>

<style scoped>
.table-primary-subtle {
    background-color: #f0f7ff !important;
}
.cursor-pointer {
    cursor: pointer;
}
.vh-30 {
    height: 30vh;
}
.cursor-pointer:hover {
    background-color: rgba(0,0,0,0.02);
}
</style>