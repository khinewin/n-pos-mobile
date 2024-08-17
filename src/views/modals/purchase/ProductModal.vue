<template lang="">
     <!-- Products Modal -->
     <div class="modal fade" id="searchCode" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog modal-xl">
          <div class="modal-content">               
            <div>
                <div class="card min-vh-100">
                    <div class="card-body">                                             
                        <div class="row">
                            <div class="col-lg-4">
                                <div class="row g-1 m-2">
                                    <div class="mb-2">
                                            <input type="number" id="search_by_code"  v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control">
                                    </div>
                                    <div class="mb-2">
                                            <input type="search" v-model="search_by_item_name" @keyup="searchProducts" placeholder="Description" class="form-control">
                                    </div> 
                                </div>                                  
                                <div class="row">
                                    <div class="col-4">
                                        <button class="btn text-dark" @click="clearSearch">Clear</button>
                                    </div>
                                    <div class="col-4">
                                        <button class="btn text-primary" @click="updateProducts">Update
                                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                        </button>
                                    </div>
                                    <div class="col-4">
                                        <button class="btn text-danger" data-bs-dismiss="modal" aria-label="Close">Close</button>
                                    </div>
                                </div>                                   
                            </div>
                            <div class="col-lg-8 table-responsive">
                                <table class="table table-bordered table-hover">
                                    <tbody class="small text-center">
                                        <tr class="table-secondary text-center">
                                            <td>Code</td>
                                            <td>Description</td>
                                            <td colspan="2">Qty</td>
                                            <td>Created date</td>
                                        </tr>                                      
                                        <tr class="text-center" v-if="products.length > 0"  v-for="p in products" :key="p.id" @click="setCode(p)"   style="cursor:context-menu">
                                            <td>{{p.code_no}}</td>
                                            <td>{{p.item_name}}</td>
                                            <td>{{p.qty}}</td>
                                            <td>{{Math.floor(p.qty % p.item_per_package)}} x {{Math.floor(p.qty / p.item_per_package)}}</td>
                                            <td>{{showTime(p.created_at)}}</td>
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
      <!--end modal-->
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
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
        }
    },
   
    mounted() {
        const myModalEl = document.getElementById('searchCode')
        myModalEl.addEventListener('show.bs.modal', event => {
                const search_by_code=document.getElementById("search_by_code");
                setTimeout(()=>{
                    search_by_code.focus();
                }, 500)
                /*
               if(this.storePurchase.length <=0){
                    this.products=[]
                    this.fetchProducts();
                 }  
                    */
                     
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
        const query_url = query(collection(db, "products"), where("uid", "==", this.user.uid));
        const snapShot=await getDocs(query_url)
        let products=[];
        snapShot.forEach((doc) => {         
            let product = {
                id: doc.id,
                uid: doc.data().uid,
                item_name: doc.data().item_name,
                retail_price : doc.data().retail_price,
                whole_sale_price: doc.data().whole_sale_price,
                pur_price:doc.data().pur_price,
                qty : doc.data().qty,
                created_at : doc.data().created_at,
                code_no : doc.data().code_no,           
                item_per_package: doc.data().item_per_package, 
                          
            };                                
            products.push(product);      
                        
        });
        this.productsData=products;
        this.$store.dispatch("setProducts", products)
        this.isLoading=false;
        this.isUpdating=false;
        this.searchProducts();
       } ,
       showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
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
                            