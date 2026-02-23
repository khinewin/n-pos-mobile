<template>
    <!-- Product Selection Modal -->
    <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="productModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
            <div class="modal-content bg-light">               
                <div class="container-fluid p-2">              
                    <div class="row g-2">
                        <!-- Left Sidebar: Filters & Selection Summary -->
                        <div class="col-lg-3">
                            <div class="d-flex flex-column gap-2 vh-95">
                                <!-- Search & Filters Card -->
                                <div class="card border-0 shadow-sm rounded-3">
                                    <div class="card-body p-3">
                                        <h6 class="fw-bold text-muted text-uppercase small mb-3">
                                            <i class="fa-solid fa-search me-2"></i>Product Search
                                        </h6>
                                        
                                        <!-- Barcode / Code Search -->
                                        <div class="mb-2">
                                            <label class="small text-muted text-uppercase fw-bold mb-1">{{t('barcode')}}</label>
                                            <div class="input-group input-group-sm">
                                                <span class="input-group-text bg-light border-0">
                                                    <i class="fa-solid fa-barcode text-secondary"></i>
                                                </span>
                                                <input 
                                                    type="text" 
                                                    id="search_by_code" 
                                                    v-model="search_by_code" 
                                                    @keypress.enter="searchProducts" 
                                                    placeholder="Scan or type code..." 
                                                    class="form-control bg-light border-0">
                                            </div>
                                        </div>

                                        <!-- Item Name Search -->
                                        <div class="mb-3">
                                            <label class="small text-muted text-uppercase fw-bold mb-1">{{t('item_name')}}</label>
                                            <div class="input-group input-group-sm">
                                                <span class="input-group-text bg-light border-0">
                                                    <i class="fa-solid fa-tag text-secondary"></i>
                                                </span>
                                                <input 
                                                    type="search" 
                                                    v-model="search_by_item_name" 
                                                    @keypress.enter="searchProducts" 
                                                    placeholder="Type product name..." 
                                                    class="form-control bg-light border-0">
                                            </div>
                                        </div>

                                        <!-- Action Buttons -->
                                        <div class="d-flex flex-column gap-2 mt-3">
                                            <button class="btn btn-primary shadow-sm fw-bold btn-sm" @click="searchProducts">
                                                <i class="fa-solid fa-magnifying-glass me-2"></i> {{t('search_link')}}
                                            </button>
                                            <div class="d-flex gap-2">
                                                <button class="btn btn-light flex-fill shadow-sm fw-bold btn-sm" @click="clearSearch">
                                                    <i class="fa-solid fa-filter-circle-xmark me-2"></i> {{t('clear_link')}}
                                                </button>
                                                <button class="btn btn-light flex-fill shadow-sm fw-bold btn-sm text-danger" data-bs-dismiss="modal" aria-label="Close">
                                                    <i class="fa-solid fa-circle-xmark me-2"></i> {{t('close_link')}}
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Batch Import Actions -->
                                <div class="card border-0 shadow-sm rounded-3">
                                    <div class="card-body p-3">
                                        <h6 class="fw-bold text-muted text-uppercase small mb-2">
                                            <i class="fa-solid fa-layer-group me-2"></i>Batch Import
                                        </h6>
                                        <div class="d-flex flex-wrap gap-1 mt-2">
                                            <button class="btn btn-outline-secondary btn-xs rounded-pill px-3 py-1" @click="insertAllItem">
                                                <i class="fa-solid fa-check-double me-1"></i> All
                                            </button>
                                            <button 
                                                v-for="(c, i) in chunkData" 
                                                :key="i"
                                                class="btn btn-outline-primary btn-xs rounded-pill px-3 py-1" 
                                                @click="setChunkCode(c)">
                                                Batch {{i + 1}} ({{c.length}})
                                            </button>
                                        </div>
                                    </div>
                                </div>

                                <!-- Selected Preview List -->
                                <div class="card border-0 shadow-sm rounded-3 flex-grow-1 overflow-hidden">
                                    <div class="card-header bg-white border-bottom py-2">
                                        <h6 class="fw-bold text-muted text-uppercase small mb-0">
                                            <i class="fa-solid fa-list-check me-2"></i>Added Items ({{stockItems.length}})
                                        </h6>
                                    </div>
                                    <div class="card-body p-0">
                                        <div class="vh-40 overflow-y-auto dataContainer">
                                            <table class="table table-hover mb-0">
                                                <tbody class="small">
                                                    <tr v-if="stockItems.length === 0">
                                                        <td class="text-center py-5 text-muted opacity-50">
                                                            <i class="fa-solid fa-cart-plus d-block fs-2 mb-2"></i>
                                                            No items added yet
                                                        </td>
                                                    </tr>
                                                    <tr v-for="(item, i) in stockItems" :key="item.id">
                                                        <td class="ps-3 py-2 border-0">
                                                            <div class="d-flex align-items-center">
                                                                <span class="badge bg-primary bg-opacity-10 text-primary rounded-circle me-2" style="width: 20px; height: 20px; display: inline-flex; align-items: center; justify-content: center; font-size: 0.6rem;">{{i+1}}</span>
                                                                <span class="fw-medium text-dark line-clamp-1">{{item.item_name}}</span>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Right: Results Table Section -->
                        <div class="col-lg-9">
                            <div class="card border-0 shadow-sm rounded-3 overflow-hidden">
                                <div class="card-body p-0">
                                    <div class="vh-95 overflow-y-auto position-relative dataContainer">
                                        <table class="table table-hover mb-0 align-middle">
                                            <thead class="position-sticky top-0 z-3">
                                                <tr class="table-primary-subtle fw-bold text-center">
                                                    <th class="col-2 border-0 py-3">{{t('barcode')}}</th>
                                                    <th class="col-5 border-0 py-3 text-start">{{t('item_name')}}</th>
                                                    <th class="col-2 border-0 py-3">{{t('packing_size')}}</th>
                                                    <th class="col-3 border-0 py-3 text-end pe-4">{{t('inv_bal')}}</th>
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
                                                        <span class="badge bg-light text-dark border font-monospace px-3">{{p.code_no}}</span>
                                                    </td>
                                                    <td class="fw-bold text-dark">{{p.item_name}}</td>
                                                    <td class="text-center text-muted">
                                                        <span class="badge bg-secondary-subtle text-secondary fw-medium px-2 py-1">
                                                            {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                        </span>
                                                    </td>
                                                    <td class="text-end pe-4">
                                                        <span class="fw-bold text-primary">
                                                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}
                                                        </span>
                                                    </td>
                                                </tr>

                                                <!-- No Records Found -->
                                                <tr v-if="!isLoading && products.length === 0">
                                                    <td colspan="4" class="text-center text-muted py-5">
                                                        <i class="fa-solid fa-box-open fa-4x mb-3 opacity-10"></i>
                                                        <div class="h5 fw-bold">No products found</div>
                                                        <p class="mb-0">Try different keywords or check the barcode.</p>
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>

                                        <!-- Interactive Loading Overlay -->
                                        <div v-if="isLoading" class="position-absolute top-50 start-50 translate-middle text-center bg-white bg-opacity-75 w-100 h-100 d-flex flex-column justify-content-center align-items-center" style="z-index: 10;">
                                            <div class="spinner-border text-primary mb-3" role="status">
                                                <span class="visually-hidden">Loading...</span>
                                            </div>
                                            <div class="fw-bold text-secondary">Looking for products...</div>
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
import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as lib from "@/lib/snapshot/products"
import * as hp  from "@/lib/helper"
import * as search from "@/lib/search"

export default {   
props:['stockItems'],
 data(){
        return{
            products:[],
            productsData: [],
            search_by_code:"",
            search_by_item_name:"",
            isUpdating:false,
            isLoading:false,
            chunkData:[],
            limitData: 100,
        }
    },   
    mounted() {
        this.isLoading=true; 
        const myModalEl = document.getElementById('searchCode')
        if (myModalEl) {
            myModalEl.addEventListener('show.bs.modal', event => {
                if(this.productsData.length===0){
                    this.fetchProducts();
                }
            })
        }
    },
   
    computed:{
        user(){
            return this.$store.getters.user;
        },
        storeProducts(){
            return this.$store.getters.products;
        },
         t() {
            return (key) => this.$store.getters.t(key);
        },
    },
    watch:{       
        user(data){
            if(data !==null){         
                this.isLoading=true;          
                this.fetchProducts();           
            }
        },
    },
    methods:{
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
        setChunkCode(items){
            items.map((m, i)=>{
               setTimeout(()=>{
                    this.setCode(m)
               },5 * i)
            })
        },
        insertAllItem(){
            this.productsData.map((p, i)=>{
                setTimeout(()=>{    
                    this.setCode(p)
                }, 5 * i)
            })
        },
        setCode(p){
            this.$emit("setCode", p)
            this.clearSearch();
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_item_name="";
            this.searchProducts();
        },
        searchProducts(){
            if(this.search_by_code){
                this.products=search.searchByCode(this.productsData, this.search_by_code);
            }else if(this.search_by_item_name){
                this.products=search.searchByDescription(this.productsData, this.search_by_item_name);
            }else{
                this.products= this.productsData.slice(0, this.limitData)                                                 
            }
        },
        async fetchProducts(){
            const query_url = query(collection(db, "products"),  orderBy("created_at", "desc"));
            onSnapshot(query_url, async(snapShot) =>{                  
                const products=lib.products(snapShot)       
                this.productsData=products;
                this.products=this.productsData.slice(0, this.limitData)
                this.isLoading=false;
                this.isUpdating=false;

                let chunk=100;
                let chunkData=[]
                for(let i=0; i<products.length; i+= chunk){
                    chunkData.push(products.slice(i, i+chunk))
                }   
                this.chunkData=chunkData;
            });              
       }
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
.cursor-pointer:hover {
    background-color: rgba(0,0,0,0.035);
    transform: scale(1.001);
}
.vh-95 {
    height: 95vh;
}
.vh-40 {
    height: 40vh;
}
.font-monospace {
    font-family: 'Courier New', Courier, monospace;
}
.line-clamp-1 {
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    overflow: hidden;
}
.btn-xs {
    padding: 0.2rem 0.5rem;
    font-size: 0.75rem;
}
.dataContainer::-webkit-scrollbar {
    width: 6px;
}
.dataContainer::-webkit-scrollbar-track {
    background: transparent;
}
.dataContainer::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 10px;
}
.dataContainer::-webkit-scrollbar-thumb:hover {
    background: #cccccc;
}
</style>
