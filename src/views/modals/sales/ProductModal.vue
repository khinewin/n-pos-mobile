<template lang="">
     <!-- Products Modal -->
     <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
          <div class="modal-content">               
            <div>
                <div class="card border-0">
                    <div class="card-body p-2">                                             
                        <div class="row g-2">
                            <div class="col-lg-4">
                                <div class="row g-2">
                                    <div class="col-sm-6 col-lg-12">
                                        <div class="card  shadow-sm" :class="{'border-danger border-3' : isError}">
                                            <div class="card-body p-2 small">
                                                <div class="row g-1">
                                                    <div class="col-md-6 col-lg-12">
                                                        <div class="form-floating">
                                                            <input type="text" id="search_by_code"  v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                                            <label for="search_by_code" class="">Code</label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6 col-lg-12">
                                                        <div class="form-floating">
                                                            <input type="search" v-model="search_by_item_name" @keyup="searchProducts" placeholder="Product Name" class="form-control-plaintext border-bottom">
                                                            <label for="search_by_item_name" class="">Product Name</label>
                                                        </div> 
                                                    </div>
                                                </div>
                                                <div class="row mt-2">
                                                    <div class="col-6 d-grid">
                                                        <button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear (ctrl+z)</button>
                                                    </div>
                                                    <!--<div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark" @click="updateProducts">
                                                            <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                        </button>
                                                    </div>-->
                                                    <div class="col-6 d-grid">
                                                        <button class="btn btn-sm text-dark close-product" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button>
                                                    </div>
                                                </div>   
                                            </div>
                                        </div> 
                                    </div>
                                    <div class="col-sm-6 col-lg-12">
                                        <div class="card shadow-sm" >
                                            <div class="card-body p-2 vh-30 overflow-y-auto dataContainer">
                                                <table class="table table-bordered table-hover mb-0">
                                                    <tbody>      
                                                        <tr class="text-center fw-bold table-secondary">
                                                            <td class="col-1">No.</td>
                                                            <td class="col-11">Product Name</td>
                                                        </tr>                                         
                                                        <tr v-if="saleItems.length>0" v-for="(s,i) in saleItems" :key="s.id" >
                                                            <td class="text-center">{{i+1}}</td>
                                                            <td class="col-5">{{s.item_name}}</td>                                            
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>  
                                    </div>
                                </div>
                                  
                                                           
                            </div>
                            <div class="col-lg-8">
                                <div class="card shadow-sm">
                                    <div class="card-body p-2 vh-95 overflow-y-auto">
                                        <table class="table table-bordered mb-0 table-hover">
                                            <tbody class="small text-nowrap text-break ">
                                                <tr class="table-secondary text-center fw-bold">
                                                    <td class="col-1">Code</td>
                                                    <td class="col-2">Product Name</td>
                                                    <td class="col-1">Packing size</td>
                                                    <td class="col-1">Mart Inv Bal</td>
                                                    <td class="col-1" >Sale Price</td>
                                                    <!--<td class="col-1">Wholesale price</td>-->

                                                </tr>                                      
                                                <tr class="change-cursor" v-if="products.length > 0"  v-for="p in products" :key="p.id" @click="setCode(p)"  >
                                                    <td class="text-center">{{p.code_no}}</td>
                                                    <td class="text-nowrap text-break">{{p.item_name}}</td>
                                                    <td class="text-nowrap text-break">
                                                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                    </td>
                                                    <td class="text-end ">
                                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                             
                                                    </td>                                                  
                                                    <td class="text-end " >
                                                        <span v-if="p.retail_price.largest_unit">{{p.retail_price.largest_unit}} * </span>
                                                        <span v-if="p.retail_price.medium_unit">{{p.retail_price.medium_unit}} * </span>
                                                        <span v-if="p.retail_price.smallest_unit">{{p.retail_price.smallest_unit}} </span>
                                                    </td>
                                                    <!--<td class="text-end " >
                                                        <span v-if="p.whole_sale_price.largest_unit">{{p.whole_sale_price.largest_unit}} * </span>
                                                        <span v-if="p.whole_sale_price.medium_unit">{{p.whole_sale_price.medium_unit}} * </span>
                                                        <span v-if="p.whole_sale_price.smallest_unit">{{p.whole_sale_price.smallest_unit}} </span>
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
import {Timestamp, collection, addDoc, getDocs, doc, onSnapshot, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as lib from "@/lib/snapshot/products"
import * as hp  from "@/lib/helper"
export default {   
    props:['sale_type', 'saleItems'],
    data(){
        return{
            products:[],
            productsData: [],
            search_by_code:"",
            search_by_item_name:"",
            isUpdating:false,
            isLoading:false,
            isError:false,
        }
    },   
    mounted() {
        const myModalEl = document.getElementById('searchCode')
        /* myModalEl.addEventListener('show.bs.modal', event => {
                const search_by_code=document.getElementById("search_by_code");
                setTimeout(()=>{
                    search_by_code.focus();
                }, 500)              
        })*/
        this.isLoading=true; 
        this.fetchProducts(); 
    },

    watch:{
        saleItems:{
            handler(val){
              if(val.length){
                    setTimeout(()=>{
                        this.autoScroll();
                    }, 100)
              }
            },deep:true
        
        },
        isError:{
            handler(val){
                if(val){
                    setTimeout(()=>{
                      this.isError=false;
                    }, 1000)
                }
            },deep:true
        }
    },
    
    computed:{
        user(){
            return this.$store.getters.user;
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
        autoScroll(){            
            const container = document.querySelector(".dataContainer");
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth" // Enables smooth scrolling
            });
        },
        setCode(p){
            const items=this.productsData.filter((f)=>f.code_no==p.code_no);
            if(items.length){
                const item=items[0];
                if(item.qty <=0 && this.sale_type !== undefined){
                    this.isError=true;
                    return false;
                }
            }
            this.$emit("setCode", p)
            //this.clearSearch();
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
            const items=this.productsData.filter((f)=>f.code_no==this.search_by_code);
            if(items.length){
                const item=items[0];
                if(item.qty <=0 && this.sale_type !== undefined){
                    this.isError=true;
                    return false;
                }
            }     
            this.setCode(items[0])
            this.search_by_code="";
            
       },
        searchProducts(){
            if(this.search_by_code){
                this.products=this.productsData.filter((f)=>{
                        return f.code_no==this.search_by_code;
                })
            }else if(this.search_by_item_name){
                this.products=this.productsData.filter((f)=>{
                        return f.item_name.toLowerCase().includes(this.search_by_item_name.toLowerCase())
                })
            }else{
                this.products= this.productsData.filter((p, i)=>{
                    if(i < 20){
                        return p;
                     }
                 })                                                         
            }
        },
      
        async fetchProducts(){
        const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
        onSnapshot(query_url, async(snapShot) =>{                  
                    let products=lib.products(snapShot)       
                    products=products.filter((f)=>f.packing_size.smallest_unit && f.packing_size.medium_unit)
                    this.productsData=products;
                    this.searchProducts();
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

 