<template lang="">
     <!-- Products Modal -->
     <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-fullscreen">
          <div class="modal-content">               
            <div>
                <div class="card border-0">
                    <div class="card-body  p-2">                                             
                        <div class="row g-2">
                            <div class="col-lg-3">
                                <div class="row g-2">
                                    <div class="col-sm-6 col-lg-12">
                                        <div class="card  shadow -sm">
                                            <div class="card-body p-2 ">
                                                <div class="row g-1 small">
                                                    <div class="col-md-6 col-lg-12">
                                                        <div class="form-floating">
                                                            <input type="text" id="search_by_code"  v-model="search_by_code" @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                                            <label for="search_by_code" class="">Code</label>
                                                        </div>
                                                    </div>
                                                    <div class="col-md-6 col-lg-12">
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
                                                        <button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                                                    </div>
                                                    <!--<div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark" @click="updateProducts">
                                                            <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                                        </button>
                                                    </div>-->
                                                    <div class="col-4 d-grid">
                                                        <button class="btn btn-sm text-dark close-product" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                                                    </div>
                                                </div>                                          
                                            </div>                                           
                                        </div> 
                                        <div class="card mt-1">
                                            <div class="card-body p-2">
                                                <div  class="row g-2 mx-2 mb-1">
                                                    <div class="col-2 change-cursor">
                                                        <span class="badge bg-secondary" @click="insertAllItem" ><i class="fa-solid fa-check-double"></i></span>
                                                    </div>
                                                    <div class="col-2 change-cursor" v-for="(c, i) in chunkData">
                                                        <span class="badge bg-secondary" @click="setChunkCode(c)">{{c.length}}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-6 col-lg-12">
                                        <div class="card shadow-sm" >
                                            <div class="card-body p-2 vh-30 overflow-y-auto">
                                                <table class="table table-bordered table-hover mb-0">
                                                    <tbody>
                                                        <tr class="text-center fw-bold table-secondary">
                                                            <td>No.</td>
                                                            <td>Product Name</td>
                                                        </tr>
                                                        <tr v-if="stockItems.length>0" v-for="(item, i) in stockItems" :key="item.id">
                                                                <td class="col-2 text-center">{{i+1}}</td>
                                                                <td class="col-10">{{item.item_name}}</td>
                                                        </tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>  
                                    </div>
                                </div>                                                                                            
                            </div>
                            <div class="col-lg-9">
                                <div class="card shadow-sm">
                                    <div class="card-body p-2 vh-95 overflow-y-auto">
                                        <table class="table table-bordered mb-0 table-hover">
                                            <tbody class="small">
                                                <tr class="table-secondary text-center fw-bold">
                                                    <td class="col-1">Code</td>
                                                    <td class="col-2 change-cursor add-all" @click="insertAllItem" ><i class="fa-solid fa-check-double"></i> Product Name (ctrl+a)</td>
                                                    <td class="col-1">Packing Size</td>
                                                    <td class="col-1">Mart Inv Bal</td>
                                                    <!--<td class="col-1">Main Inv</td>-->
                                                </tr>                                      
                                                <tr class="change-cursor" v-if="products.length > 0"  v-for="p in products" :key="p.id" @click="setCode(p)" >
                                                    <td class="text-center">{{p.code_no}}</td>
                                                    <td>{{p.item_name}}</td>
                                                    <td class="col-1">
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
import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as lib from "@/lib/snapshot/products"
import * as hp  from "@/lib/helper"
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
            chunkData:[]
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
        storePurchase :{
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
       inputSearchName(){
            this.search_by_item_name=event.target.value;
            this.searchProducts();
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
                this.products= this.productsData.slice(0, 20)                                                 
            }
        },
      
        async fetchProducts(){
        const query_url = query(collection(db, "products"),  orderBy("created_at", "desc"));
        onSnapshot(query_url, async(snapShot) =>{                  
                    const products=lib.products(snapShot)       
                    this.productsData=products;
                    this.products=this.productsData.slice(0, 20)
                    this.isLoading=false;
                    this.isUpdating=false;

                    let chunk=100;
                    let chunkData=[]
                    for(let i=0; i<products.length; i+= chunk){
                        chunkData.push(products.slice(i, i+chunk))
                    }   
                    this.chunkData=chunkData;
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
