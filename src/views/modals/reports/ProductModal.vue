<template lang="">
     <!-- Products Modal -->
     <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
          <div class="modal-content">               
            <div>
                <div class="card border-0">
                    <div class="card-body p-2">                                             
                        <div class="row g-2">
                            <div class="col-lg-3 ">
                                <div class="row g-2">
                                    <div class="col-sm-6 col-lg-12">
                                        <div class="card  shadow-sm">
                                            <div class="card-body p-2 small">
                                                <div class="row g-1">
                                                    <div class="col-md-6 col-lg-12">
                                                        <div class="form-floating">
                                                            <input type="text" id="search_by_code"  v-model="search_by_code"  @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                                            <label for="search_by_code" class="">{{t('barcode')}}</label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6 col-lg-12">
                                                        <div class="form-floating">
                                                            <input type="search" v-model="search_by_item_name" @keypress.enter="searchProducts" placeholder="Product Name" class="form-control-plaintext border-bottom">
                                                            <label for="search_by_item_name" class="">{{t('item_name')}}</label>
                                                        </div> 
                                                    </div>
                                                     <div class="form-floating col-md-6 col-lg-12">
                                                        <select id="search_by_category" class="form-control-plaintext border-bottom" v-model="search_by_category">
                                                            <option value=""></option>
                                                            <option v-for="c in cats" :key="c.id" :value="c.cat_name">{{c.cat_name}}</option>
                                                        </select>
                                                        <label for="search_by_category" class="small">{{t('category')}}</label>
                                                    </div>
                                                </div>
                                                <div class="row mt-2">
                                                    <div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark clear-search-product" @click="searchProducts"><i class="fa-solid fa-search"></i> {{t('search_link')}}</button>
                                                    </div>
                                                    <div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button>
                                                    </div>
                                                    <!--<div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark" @click="updateProducts">
                                                            <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                        </button>
                                                    </div>-->
                                                    <div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark close-product" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> {{t('close_link')}}</button>
                                                    </div>
                                                </div>   
                                                
                                            </div>
                                        </div>  
                                    </div>
                                 
                                </div>
                                  
                                                      
                            </div>
                            <div class="col-lg-9">
                                <div class="card shadow-sm">
                                    <div class="card-body p-0 vh-100 overflow-y-auto">
                                        <table class="table table-bordered mb-0 table-hover text-nowrap text-break">
                                            <thead>
                                                <tr class="table-secondary text-center fw-bold sticky-top z-3">
                                                    <td class="col-1">{{t('barcode')}}</td>
                                                    <td class="col-2">{{t('item_name')}}</td>
                                                    <td class="col-1">{{t('packing_size')}}</td>
                                                    <td class="col-1">{{t('inv_bal')}}</td>
                                                    <td class="col-1">{{t('pur_price')}}</td>
                                                </tr>    
                                                </thead>
                                                <tbody class="small">                                  
                                                <tr class="change-cursor" v-if="products.length > 0"  v-for="p in products" :key="p.id" @click="setCode(p)"  data-bs-dismiss="modal">
                                                    <td class="text-center">{{p.code_no}}</td>
                                                    <td>{{p.item_name}}</td>
                                                    <td class="col-1">
                                                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                    </td>
                                                    <td class="text-end col-1">
                                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                             
                                                    </td>                                                  
                                                    <td class="text-end " >
                                                        <!--<span v-if="p.retail_price.largest_unit">{{p.retail_price.largest_unit}} * </span>-->
                                                        <!--<span v-if="p.retail_price.medium_unit">{{p.retail_price.medium_unit}} * </span>-->
                                                        <span v-if="p.pur_price.smallest_unit">{{p.pur_price.smallest_unit}} - {{p.packing_size.smallest_unit}} </span>
                                                    </td>
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
import {Timestamp, collection, addDoc, onSnapshot, getDocs, doc, setDoc, limit, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as lib from "@/lib/snapshot/products"
import * as hp  from "@/lib/helper"
import * as search from '@/lib/search'
import * as cat from "@/lib/snapshot/category"

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
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth" // Enables smooth scrolling
            });
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
          //  this.clearSearch();
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_item_name="";
            this.search_by_category="";
            this.searchProducts();
        },
        onPaste(){            
            setTimeout(()=>{
                //this.searchProducts()
                this.setCodeOnPaste();
            }, 300)
        },
        setCodeOnPaste(){
            const item=this.productsData.filter((f)=>f.code_no==this.search_by_code);
            if(item){
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
                        this.setCode(products[0]);
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
<style lang="">
    
</style>
