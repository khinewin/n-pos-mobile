<template lang="">
     <!-- Products Modal -->
     <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
          <div class="modal-content">               
            <div>
                <div class="card border-0">
                    <div class="card-body p-2">                                             
                        <div class="row g-2">
                            <div class="col-lg-3">
                                <div class="card  shadow-sm ">
                                    <div class="card-body p-2 small">
                                        <div class="row g-1">
                                            <div class="col-6 col-lg-12">
                                                <div class="form-floating">
                                                    <input type="text" id="search_by_code"  v-model="search_by_code"  @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                                    <label for="search_by_code" class="">Code</label>
                                                </div>
                                            </div>
                                            <div class="col-6 col-lg-12">
                                                <div class="form-floating">
                                                    <input type="search" v-model="search_by_item_name" @keypress.enter="searchProducts" placeholder="Product Name" class="form-control-plaintext border-bottom">
                                                    <label for="search_by_item_name" class="">Product Name</label>
                                                </div> 
                                            </div>
                                        </div>
                                        <div class="row mt-2">
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-product" @click="searchProducts"><i class="fa-solid fa-search"></i> Search</button>
                                            </div>
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear </button>
                                            </div>
                                            <!--<div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="updateProducts">
                                                    <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                </button>
                                            </div>-->
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark close-product" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close </button>
                                            </div>
                                        </div>   
                                    </div>
                                </div>                                
                            </div>
                            <div class="col-lg-9">
                                <div class="card  shadow-sm vh-100 overflow-y-auto">
                                    <div class="card-body p-0">
                                        <table class="table table-bordered mb-0 table-hover">
                                            <thead>
                                                <tr class="table-secondary text-center fw-bold sticky-top z-3">
                                                    <td class="col-1">Code</td>
                                                    <td class="col-2">Product Name</td>
                                                    <td class="col-1">Packing size</td>
                                                    <td class="col-1">Mart Inv Bal</td>
                                                    <!--<td class="col-1">Main Inv Bal</td>-->
                                                </tr>
                                                </thead>
                                                <tbody class="small">                                      
                                                <tr class="change-cursor" data-bs-dismiss="modal" v-if="products.length > 0"  v-for="p in products" :key="p.id" @click="setCode(p)"   style="cursor:context-menu">
                                                    <td class="text-center">{{p.code_no}}</td>
                                                    <td>{{p.item_name}}</td>
                                                    <td class="">
                                                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                    </td>
                                                    <td class="text-end col-1">
                                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                             
                                                    </td>
                                                    <!--<td class="text-end col-1">
                                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.main_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                             
                                                    </td>-->
                                                   
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
import {Timestamp, collection, addDoc, getDocs, doc, onSnapshot, setDoc, limit, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as lib from "@/lib/snapshot/products"
import * as hp  from "@/lib/helper"
export default {   
    props:['storePurchase'],
    data(){
        return{
            products:[],
            productsData: [],
            search_by_code:"",
            search_by_item_name:"",
            isUpdating:false,
            isLoading:false,
            unsubProducts:null,
            limitData: 100,
        }
    },   
    mounted() {
        this.isLoading=true; 
        const myModalEl = document.getElementById('searchCode')
        myModalEl.addEventListener('show.bs.modal', event => {
                if(this.productsData.length===0){
                    this.fetchProducts();
                }  
        })      
     
    },
    unmounted() {
        if(this.unsubProducts){
            this.unsubProducts();
        }
    },
  
    computed:{
        user(){
            return this.$store.getters.user;
        },
    
    },
    watch:{       
     
      
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
        setCode(p){
            this.$emit("setCode", p)
            this.clearSearch();
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_item_name="";
            this.searchProducts();
        },
        onPaste(){            
            setTimeout(()=>{
                //this.searchProducts()
                this.setCodeOnPaste();
            }, 300)
        },
        setCodeOnPaste(){
            const item=this.productsData.filter((f)=>f.code_no===this.search_by_code);
            if(item){
                this.setCode(item[0])
                this.search_by_code="";
            }
       },
       searchProducts(){
            this.products=[]
            this.isLoading=true;
            if(this.search_by_code){
                const query_url = query(collection(db, "products"),  
                where("code_no", "==", this.search_by_code));
                    const unsub=onSnapshot(query_url, (snapShot)=>{
                        const products=lib.products(snapShot);   
                        this.products=products;
                        unsub()    
                        this.isLoading=false;
                })
            }else if(this.search_by_item_name){
                const query_url = query(collection(db, "products"),  
                where("keywords", "array-contains", this.search_by_item_name.toLowerCase()),  orderBy("created_at", "desc"), limit(this.limitItem));
                    const unsub=onSnapshot(query_url, (snapShot)=>{
                        const products=lib.products(snapShot);   
                        this.products=products;
                        unsub()    
                        this.isLoading=false;
                })
            }else{
                this.products= this.productsData;                                         
            }
        },
      
        async fetchProducts(){
        const query_url = query(collection(db, "products"), orderBy("created_at", "desc"), limit(this.limitData));
       this.unsubProducts= onSnapshot(query_url, async(snapShot) =>{                  
                    let products=lib.products(snapShot)       
                    this.productsData=products;
                    this.products=products;
                    this.isLoading=false;
            });        
       
       } ,
       showTime(t){
            return hp.showTime(t)
        },
    }
    
}
</script>
<style lang="">
    
</style>

 <!--
                        <div class="card">
                            <div class="card-body">
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                        <div class="row">
                                            <div class="col-lg-3">
                                                    <div class="small fw-lighter ">Code</div>
                                            </div>
                                            <div class="col-lg-5">
                                                    <div class="small fw-lighter ">Product Name</div>
                                            </div>
                                            <div class="col-lg-1">
                                                    <div class="small fw-lighter">Qty</div>
                                            </div>
                                            <div class="col-lg-3">
                                                <div class="small fw-lighter">Created date</div>
                                        </div>
                                            
                                    </div>
                                    </li>
                                 
                                    <li class="list-group-item" v-if="isLoading">
                                        <div class="d-flex justify-content-center my-5">
                                            <div class="spinner-border text-primary"  role="status">
                                              <span class="visually-hidden">Loading...</span>
                                            </div>
                                          </div>
                                    </li>
                             
                                    <li v-if="products.length > 0" class="list-group-item list-group-item-action" v-for="p in products" :key="p.id" >
                                            <div class="row" @click="setCode(p)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                    <div class="col-lg-3 small">
                                                            <div>{{p.code_no}}</div>
                                                    </div>
                                                    <div class="col-lg-5 small">
                                                            <div>{{p.item_name}}</div>
                                                    </div>
                                                    <div class="col-lg-1 small">
                                                            <div>{{p.qty}}</div>
                                                    </div>
                                                    <div class="col-lg-3 small">
                                                        <div>{{showTime(p.created_at)}}</div>
                                                    </div>
                                            </div>
                                    </li>
                            </ul>
                            </div>
                        </div>
                    -->
                            