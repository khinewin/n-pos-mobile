<template lang="">
     <!-- Products Modal -->
     <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
          <div class="modal-content">               
            <div>
                <div class="card">
                    <div class="card-body p-2">                                             
                        <div class="row g-1">
                            <div class="col-lg-3">
                                <div class="card  shadow">
                                    <div class="card-body p-2 small">
                                        <div class="row g-1">
                                            <div class="col-md-6 col-lg-12">
                                                <div class="form-floating">
                                                    <input type="number" id="search_by_code"  v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control form-control-sm">
                                                    <label for="search_by_code" class="small">Code</label>
                                                </div>
                                            </div>
                                            <div class="col-md-6 col-lg-12">
                                                <div class="form-floating">
                                                    <input type="search" v-model="search_by_item_name" @keyup="searchProducts" placeholder="Description" class="form-control form-control-sm">
                                                    <label for="search_by_item_name" class="small">Description</label>
                                                </div> 
                                            </div>
                                        </div>
                                        <div class="row mt-2">
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                            </div>
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" @click="updateProducts">
                                                    <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                </button>
                                            </div>
                                            <div class="col-4 d-grid">
                                                <button class="btn btn-sm text-dark" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                                            </div>
                                        </div>   
                                    </div>
                                </div>                                
                            </div>
                            <div class="col-lg-9">
                                <div class="card shadow vh-80 overflow-y-auto">
                                    <div class="card-body p-2">
                                        <table class="table table-bordered mb-0 table-hover">
                                            <tbody class="small">
                                                <tr class="table-secondary text-center">
                                                    <td class="col-1">Code</td>
                                                    <td class="col-2">Description</td>
                                                    <td class="col-1">Packing size</td>
                                                    <td class="col-1">Pharmacy Inv</td>
                                                    <td class="col-2" v-if="sale_type=='retail'">Retail price</td>
                                                    <td class="col-2" v-if="sale_type=='whole_sale'">Wholesale price</td>

                                                </tr>                                      
                                                <tr class="" v-if="products.length > 0"  v-for="p in products" :key="p.id" @click="setCode(p)"   style="cursor:context-menu">
                                                    <td class="text-center">{{p.code_no}}</td>
                                                    <td>{{p.item_name}}</td>
                                                    <td class="">
                                                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                    </td>
                                                    <td class="text-end col-1">
                                                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                             
                                                    </td>                                                  
                                                    <td class="text-center" v-if="sale_type=='retail'">
                                                        <span v-if="p.retail_price.largest_unit">{{p.packing_size.largest_unit}}*{{p.retail_price.largest_unit}}, </span>
                                                        <span v-if="p.retail_price.medium_unit">{{p.packing_size.medium_unit}}*{{p.retail_price.medium_unit}}, </span>
                                                        <span v-if="p.retail_price.smallest_unit">{{p.packing_size.smallest_unit}}*{{p.retail_price.smallest_unit}} </span>
                                                    </td>
                                                    <td class="text-center" v-if="sale_type=='whole_sale'">
                                                        <span v-if="p.whole_sale_price.largest_unit">{{p.packing_size.largest_unit}}*{{p.whole_sale_price.largest_unit}}, </span>
                                                        <span v-if="p.whole_sale_price.medium_unit">{{p.packing_size.medium_unit}}*{{p.whole_sale_price.medium_unit}}, </span>
                                                        <span v-if="p.whole_sale_price.smallest_unit">{{p.packing_size.smallest_unit}}*{{p.whole_sale_price.smallest_unit}} </span>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
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
        }
    },   
    mounted() {
        const myModalEl = document.getElementById('searchCode')
        myModalEl.addEventListener('show.bs.modal', event => {
                const search_by_code=document.getElementById("search_by_code");
                setTimeout(()=>{
                    search_by_code.focus();
                }, 500)              
        })
    },
    created(){
        if(this.user !==null){         
                this.isLoading=true; 
                this.fetchProducts();                 
        }
    },   
    computed:{
        user(){
            return this.$store.getters.user;
        },
        storeProducts(){
            return this.$store.getters.products;
        }
    },
    watch:{       
        user(data){
                if(data !==null){         
                    this.isLoading=true;          
                    this.fetchProducts();           
            }
        },
        saleItems :{
            handler(newVal){
                if(Object.keys(newVal).length <=0){
                    setTimeout(()=>{
                        this.fetchProducts();
                    }, 300)
                }
            },
            deep: true,
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
            if(item.length){
                this.setCode(item[0])
                this.search_by_code="";
            }
       },
        searchProducts(){
            if(this.search_by_code){
                this.products=this.productsData.filter((f)=>{
                        return f.code_no===this.search_by_code;
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
        const snapShot=await getDocs(query_url)
        const products=lib.products(snapShot)       
        this.productsData=products;
        this.$store.dispatch("setProducts", products)
        this.isLoading=false;
        this.isUpdating=false;
        this.searchProducts();
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
                                                    <div class="small fw-lighter ">Description</div>
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
                            