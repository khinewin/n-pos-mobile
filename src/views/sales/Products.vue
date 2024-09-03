<template lang="">
    <!-- Products  -->  
               <div class="card shadow">
                   <div class="card-body p-2">                                             
                        <div class="row gx-2 mb-2">
                            <div class="col-4 mb-2 d-grid">
                                <button class="btn text-dark btn-sm" @click="searchProducts"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button>
                            </div>
                            <div class="col-4 mb-2 d-grid">
                                <button :disabled="isBtnLoading" class="btn text-dark btn-sm" @click="updateProducts">
                                        <span v-if="!isBtnLoading"><i class="fa-solid fa-retweet"></i> Update   </span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isBtnLoading"></span>
                                </button>
                            </div>                                       
                            <div class="col-4 mb-2 d-grid">
                                    <button class="btn btn-sm " @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button>
                            </div>                    
                            <div class="mb-2 col-sm-6">
                                    <input type="number" id="search_by_code"  v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control form-control-sm">
                            </div>
                            <div class="mb-2 col-sm-6">
                                    <input type="search" v-model="search_by_item_name" @keyup="searchProducts" placeholder="Description" class="form-control form-control-sm">
                            </div>                                                                                                                                           
                        </div>                          
                           <div class="vh-60 overflow-y-auto">
                            <div class="row my-5" v-if="isLoading">
                                <div class="col-12">
                                        <div class="d-flex justify-content-center ">
                                            <div class="spinner-border text-primary"  role="status">
                                              <span class="visually-hidden">Loading...</span>
                                            </div>
                                        </div>                               
                                </div>
                              </div>
                            <div class="row g-1" v-if="products.length > 0"  >               
                                             
                                <div class="col-12"  v-for="(p,i) in products" :key="p.id" @click="setCode(p)"  style="cursor:context-menu">
                                        <div class="card">
                                            <div class="card-body p-1 small  text-center" :class="[i%2===1 ? 'bg-secondary-subtle' : 'bg-info']">
                                                    <div class="row g-1">
                                                        <div class="col-6">
                                                            {{p.code_no}}
                                                        </div>
                                                        <div class="col-6">
                                                            {{p.item_name}}
                                                        </div>
                                                    </div>
                                                    <div class="row g-1">
                                                        <div class="col-6">
                                                            {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                        </div> 
                                                        <div class="col-6">
                                                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                             
                                                        </div>                                                                                                              
                                                    </div>
                                                    
                                                    <div class="row g-1">
                                                        <div class="col-6">
                                                            <span v-if="p.retail_price.largest_unit">{{p.packing_size.largest_unit}}*{{p.retail_price.largest_unit}}, </span>
                                                            <span v-if="p.retail_price.medium_unit">{{p.packing_size.medium_unit}}*{{p.retail_price.medium_unit}}, </span>
                                                            <span v-if="p.retail_price.smallest_unit">{{p.packing_size.smallest_unit}}*{{p.retail_price.smallest_unit}} </span>
                                                        </div>
                                                        <div class="col-6">
                                                            <span v-if="p.whole_sale_price.largest_unit">{{p.packing_size.largest_unit}}*{{p.whole_sale_price.largest_unit}}, </span>
                                                            <span v-if="p.whole_sale_price.medium_unit">{{p.packing_size.medium_unit}}*{{p.whole_sale_price.medium_unit}}, </span>
                                                            <span v-if="p.whole_sale_price.smallest_unit">{{p.packing_size.smallest_unit}}*{{p.whole_sale_price.smallest_unit}} </span>
                                                        </div>
                                                    </div>
                                                    
                                            </div>
                                        </div>
                                </div>
                            </div>  
                           </div>                                                                                 
                   </div>
               </div>                       
     <!--end -->
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, onSnapshot } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/products"
import * as hp from "@/lib/helper"
export default {  
    props:['saleItems'],
   data(){
       return{
           products:[],
           productsData: [],
           search_by_code:"",
           search_by_item_name:"",
           isLoading:false,
           isBtnLoading:false,
           message :"",
       }
   },  
   mounted() {
        const search_by_code=document.getElementById("search_by_code")
        search_by_code.focus();
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
                        this.isBtnLoading=true;
                        this.fetchProducts();
                    }, 1000)
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
                this.isBtnLoading=true
               this.fetchProducts();
       },
       setCode(p){     
           this.$emit("setCode", p)
          // this.clearSearch();
       },
       clearSearch(){
           this.search_by_code="";
           this.search_by_item_name="";
           this.searchProducts();
       },
       onPaste(){            
           setTimeout(()=>{
               this.setCodeOnPaste();             
           }, 100)
       },
       setCodeOnPaste(){
            const item=this.productsData.filter((f)=>f.code_no===this.search_by_code);
            if(item.length > 0){
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
                           if(i < 15){
                               return p;
                           }
                    })                                     
           }
       },     
       async fetchProducts(){
            const query_url = query(collection(db, "products"))
            const snapShot=await getDocs(query_url)
            const products=ss.products(snapShot);     
            this.products=[];
            this.productsData=products;
            this.isLoading=false;
            this.isBtnLoading=false;    
            this.searchProducts();     
      
      } ,    
   }   
}
</script>
<style lang="">
   
</style>
