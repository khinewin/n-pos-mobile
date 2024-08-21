<template lang="">       
 <div>           
        <PageHeader page-title="Products code" page-code="products" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end">                               
                <div class="col-sm-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCode">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                    </button>
                </div>    
            </div>
        </div>
        <div class="container-fluid my-2">
            <div class="row g-2">
                <div class="col-lg-3">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row g-2">
                                <div class="form-floating col-sm-6 col-lg-12">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchProducts" placeholder="Code" class="form-control form-control-sm">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                                <div class="form-floating col-sm-6 col-lg-12">
                                    <input type="search" id="search_by_description" v-model="search_by_description" @keyup="searchProducts" placeholder="Description" class="form-control form-control-sm">
                                    <label for="search_by_description" class="small">Description</label>
                                </div>
                            </div> 
                             <div class="row">
                                    <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="searchProducts"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                    <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateProducts"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>
                                    <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
                             </div> 
                        </div>
                    </div>                                                                                                          
                </div>                        
                <div class="col-lg-9">
                    <div class="card shadow">
                        <div class="card-body p-2 table-responsive">
                            <table class="table table-bordered table-hover mb-0">
                                <tbody class="">
                                    <tr class="text-center table-secondary" style="cursor: context-menu;">
                                        <td colspan="2" @click="sortProducts('code_no')"><i class="fa-solid fa-sort"></i> Code</td>
                                        <td @click="sortProducts('item_name')"><i class="fa-solid fa-sort"></i> Description</td>
                                        <td @click="sortProducts('item_per_package')"><i class="fa-solid fa-sort"></i> IPP</td>
                                        <td @click="sortProducts('qty')" colspan="2"><i class="fa-solid fa-sort"></i> Qty</td>
                                        <td @click="sortProducts('retail_price')"><i class="fa-solid fa-sort"></i> Retail price</td>
                                        <td @click="sortProducts('whole_sale_price')"><i class="fa-solid fa-sort"></i> Wholesale price</td>
                                        <td @click="sortProducts('pur_price')"><i class="fa-solid fa-sort"></i> Pur price</td>

                                    </tr>
                                    <tr>
                                        <td colspan="2">
                                            <input placeholder="Code"  type="text" v-model="code_no" class="form-control-plaintext  text-center">
                                        </td>
                                        <td>
                                            <input placeholder="Description"  :class="{'border-danger' : v$.item_name.$errors.length}"  type="text" v-model="item_name" class="form-control-plaintext  text-center">
                                        </td>
                                        <td>
                                            <input placeholder="IPP"  :class="{'border-danger' : v$.item_per_package.$errors.length}"  type="number" v-model="item_per_package" class="form-control-plaintext  text-center">
                                        </td>
                                        <td colspan="2">
                                            <input placeholder="Qty" disabled type="number"  class="form-control-plaintext text-center">
                                        </td>
                                        <td>
                                            <input placeholder="Retail price" disabled type="number"  class="form-control-plaintext text-center">
                                        </td>
                                        <td>
                                            <input placeholder="Wholesale Price" disabled type="number"  class="form-control-plaintext  text-center">
                                        </td>
                                        <td>
                                            <input placeholder="Pur Price" disabled type="number"  class="form-control-plaintext  text-center">
                                        </td>
                                    </tr>                                   
                                    <tr class="text-center" v-if="products.length > 0"  v-for="(p, i) in products" :key="p.id" @mouseenter="setEdit(i)" @mouseleave="saveEdit" @keypress.enter="saveEdit">
                                        <td>
                                            <a class="text-danger" href="#!" @click="removeProduct(i, p)"><i class="fa-solid fa-square-minus"></i></a>                                                                                       
                                           <!-- <input type="checkbox" @change="handleSelected(p.id, $event)"> -->
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='code_no'}" class="p-0">
                                            <span>
                                                <input type="text" class="form-control-plaintext text-center  " v-model="p.code_no">
                                            </span>
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='item_name'}" class="p-0">
                                            <span>
                                                <input type="text" class="form-control-plaintext text-center  " v-model="p.item_name">
                                            </span>
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='item_per_package'}" class="p-0">
                                            <span>
                                                <input type="number" class="form-control-plaintext text-center " v-model="p.item_per_package">
                                            </span>
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='qty'}" class="pt-2">
                                            {{p.qty}}                                           
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='qty'}" class="pt-2">{{Math.floor(p.qty / p.item_per_package)}} x {{Math.floor(p.qty % p.item_per_package)}} </td>                                         

                                        <td :class="{'table-secondary': sort_item ==='retail_price'}" class="pt-2 text-end">{{p.retail_price}}</td>
                                        <td :class="{'table-secondary': sort_item ==='whole_sale_price'}" class="pt-2 text-end">{{p.whole_sale_price}}</td>
                                        <td :class="{'table-secondary': sort_item ==='pur_price'}" class="pt-2 text-end">{{p.pur_price}}</td>
                                    
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
        <Footer />
</div>   
 
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, query, where, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/products"
export default {
    components: {
            PageHeader, Footer
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                item_name: {required},
                item_per_package: {required}
            }
    },
    data(){
        return{
            code_no :"",
            item_name:"",
            item_per_package: "",
            products : [],
            isLoading: false,
            sortBy:"asc",
            sort_item:"",
            search_by_code:"",
            search_by_description:"",
            productsData:[],
            editItem:{},
            isUpdating:false,
            isSaving:false,
            isDeleting:false,
            selected_items:[],
            msg : {
                type :"",
                info :"",
            }
        }
    },
    created() {     
        if(this.user !==null){
            this.isLoading=true;
            this.fetchProducts();
        }
    },
    mounted() {
        const code_no=document.getElementById("search_by_code");
        code_no.focus();
       
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    watch:{
        user(data){
            if(data !==null){
                this.isLoading=true;
                this.fetchProducts();
            }
        },
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
        async removeProduct(i, p){
            const cf=confirm(`Are you sure ? delete the product code : ${p.item_name} `)
            if(!cf) return false;
            const document=doc(db, "products", p.id)     
            await deleteDoc(document) //if no items delete document;
            this.fetchProducts();  
            this.msg.type="success";
            this.msg.info="Product code delete successful"
        },
        async saveCode(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            this.isSaving=true;
            const save_time=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "products", )
            const code_no=this.code_no ? this.code_no : new Date().getTime()
            const data={
                uid: this.user.uid,
                item_name: this.item_name, 
                code_no : code_no,
                item_per_package: Number(this.item_per_package),
                qty : 0,
                retail_price: 0,
                whole_sale_price: 0,
                created_at : save_time,          
                pur_price: 0,

            }
            const result=await addDoc(dataCollection, data);
            if(result){
                this.msg.type="success";
                this.msg.info="Product code save successful."
            }else{
                this.msg.type="error";
                this.msg.info="Product code save failed."
            }
            this.fetchProducts();
            this.item_name="";
            this.item_per_package="";
            this.code_no="";
            this.v$.$reset()
            this.isSaving=false;
        },
        updateProducts(){
            this.isUpdating=true;
            this.fetchProducts();
        },
       async saveEdit(){
            const updateDocument=doc(db, "products", this.editItem.id)
                await updateDoc(updateDocument, {
                    item_name : this.editItem.item_name,
                    item_per_package: this.editItem.item_per_package,
                    code_no: this.editItem.code_no,
                 })  
        },
        setEdit(i){
            this.editItem=this.products[i];
        },
        clearSearch(){
            this.search_by_code="";
            this.search_by_description="";
            this.searchProducts();
        },
        searchProducts(){
            this.products=[];
            if(this.search_by_code){
                this.products=this.productsData.filter((f)=>f.code_no===Number(this.search_by_code))
            }else 
            if(this.search_by_description){
                this.products=this.productsData.filter((f)=>{
                    return f.item_name.toLowerCase().includes(this.search_by_description.toLowerCase())
                })
            }else{
                this.products= this.productsData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                       }) 
            }
        },
        sortProducts(sort){   
            this.products=[];
            this.sort_item=sort;
            if(this.sortBy=="asc"){
                this.sortBy="desc"
                this.products=this.productsData.sort((a,b)=>{
                    const item_a=a.item_name.toUpperCase();
                    const item_b=b.item_name.toUpperCase();
                    switch(sort){
                        case "qty":
                            return a.qty - b.qty;
                        break;
                        case "item_per_package":
                            return a.item_per_package - b.item_per_package;
                        break;
                        case "item_name":
                          return item_a < item_b ? 1 : -1;
                        break;
                        case "code_no":
                            return a.code_no - b.code_no;
                        break;
                        case "retail_price":
                            return a.retail_price - b.retail_price;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price - b.whole_sale_price;
                        break;
                        case "pur_price":
                           return  a.pur_price - b.pur_price;
                        break;
                    }
                })
            }else{
                this.sortBy="asc"
                this.products=this.productsData.sort((a,b)=>{
                    const item_a=a.item_name.toUpperCase();
                    const item_b=b.item_name.toUpperCase();
                    switch(sort){
                        case "qty":
                            return a.qty + b.qty;
                        break;
                        case "item_per_package":
                            return a.item_per_package + b.item_per_package;
                        break;
                        case "item_name":
                            return item_a < item_b ? -1 : 1;
                        break;
                        case "code_no":
                            return a.code_no + b.code_no;
                        break;
                        case "retail_price":
                            return a.retail_price + b.retail_price;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price + b.whole_sale_price;
                        break;
                        case "pur_price":
                           return  a.pur_price + b.pur_price;
                        break;
                    }
                })
            }            
        },
        
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            const snapShot=await getDocs(query_url)           
            if(snapShot._snapshot.docChanges.length <=0){
                this.msg.type='error' 
                this.msg.info='Oops..., something went wrong!'
                this.isLoading=false;
                this.isUpdating=false;
            }
            const products=ss.products(snapShot);            
            this.products=[];
            this.productsData=products;
            this.products=products;
            this.isLoading=false;
            this.isUpdating=false;
            this.$store.dispatch("setProducts", this.products)
       } ,
    }
}
</script>
<style lang="">
    
</style>

<!--
                                            <ul class="list-group list-group-flush">
                                <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                    <div class="row">
                                        <div class="col-lg-3">
                                                <div class="small fw-lighter mt-3">Code</div>
                                        </div>
                                        <div class="col-lg-5">
                                                <div class="small fw-lighter mt-3">Description</div>
                                        </div>
                                        <div class="col-lg-1">
                                                <div class="small fw-lighter mt-3">Qty</div>
                                        </div>
                                        <div class="col-lg-3 small fw-lighter text-center">
                                                <div>Prices</div>
                                                <div class="row">
                                                    <div class="col-4">Purchase</div>
                                                    <div class="col-4">Regular sale</div>
                                                    <div class="col-4">Whole sale</div>
                                                </div>
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
                                        <div class="row">
                                                <div class="col-lg-3 small">
                                                        <div>{{p.code_no}}</div>
                                                </div>
                                                <div class="col-lg-5 small">
                                                        <div>{{p.item_name}}</div>
                                                </div>
                                                <div class="col-lg-1 small">
                                                        <div>{{p.qty}}</div>
                                                </div>
                                                <div class="col-lg-3 small fw-lighter text-center">
                                                    <div class="row">
                                                        <div class="col-4">{{p.purchase_price}}</div>
                                                        <div class="col-4">{{p.regular_sale_price}}</div>
                                                        <div class="col-4">{{p.whole_sale_price}}</div>
                                                    </div>
                                            </div>
                                        </div>
                                </li>
                        </ul>
                    -->