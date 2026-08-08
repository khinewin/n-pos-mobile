<template lang="">       
 <div>           
        <PageHeader :page-title="pageTitle" page-code="medicineCode" :msg-type="msg.type" :msg-info="msg.info"/>
        <div class="container-fluid">
            <div class="row">   
               
            </div>
        </div>
        <div class="container-fluid my-2">
            <div class="row g-1">
                <div class="col-lg-4">
                    <div class="card shadow">
                        <div class="card-body p-2">
                            <div class="row g-2">
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_code"  v-model="search_by_code" @paste="onPaste" @keypress.enter="searchProducts" placeholder="Code" class="form-control-plaintext border-bottom">
                                    <label for="search_by_code" class="small">{{t('barcode')}}</label>
                                </div>
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_description" v-model="search_by_description" @keypress.enter="searchProducts" placeholder="Description" class="form-control-plaintext border-bottom">
                                    <label for="search_by_description" class="small">{{t('item_name')}}</label>
                                </div>
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <select id="search_by_category" class="form-control-plaintext border-bottom" v-model="search_by_category">
                                        <option value=""></option>
                                        <option v-for="c in cats" :key="c.id" :value="c.cat_name">{{c.cat_name}}</option>
                                    </select>
                                    <label for="search_by_category" class="small">{{t('category')}}</label>
                                </div>
                            
                                    <div class="col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="searchProducts"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> {{t('search_link')}}</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateProducts"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark clear-search-product" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button></div>
                             </div> 
                        </div>
                    </div>  
                    <div class="card shadow mt-2">
                        <div class="card-body p-2 vh-50 overflow-y-auto">
                             <div v-if="products.length > 0 && !isLoading">
                                <ul class="list-group">
                                    <li v-for="p in products" :key="p.id" class="list-group-item change-cursor d-flex justify-content-between align-items-center" @click="setEdit(p)">
                                        <div>
                                            <div class="fw-semibold text-dark">{{p.item_name}}</div>
                                            <div class="small text-secondary">{{p.code_no}}</div>
                                        </div>
                                        <div>
                                            <button class="btn btn-sm btn-outline-primary px-2 py-1" @click.stop="addToQueue(p)">
                                                <i class="fa-solid fa-plus"></i> Add
                                            </button>
                                        </div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                  
                                                                                                                
                </div>                        
                <div class="col-lg-8">
                    <div class="card shadow border-0 mb-2">
                        <div class="card-header bg-white py-2 border-0">
                            <h5 class="fw-bold text-primary mb-0 d-flex align-items-center">
                                <i class="fa-solid fa-print me-2"></i> Print Queue
                                <span class="badge bg-primary ms-2" v-if="selectedItems.length">{{ selectedItems.length }}</span>
                            </h5>
                        </div>
                        <div class="card-body p-3">
                            <div v-if="selectedItems.length === 0" class="text-center py-3 text-muted">
                                <i class="fa-solid fa-barcode fa-3x mb-3 text-secondary opacity-50"></i>
                                <p class="mb-0">No products selected for printing yet.</p>
                                <small>Click "Add" button on the products from the left panel.</small>
                            </div>
                            <div v-else>
                                <div class="table-responsive max-vh-40 overflow-y-auto mb-2">
                                    <table class="table table-hover align-middle border-light">
                                        <thead class="table-light sticky-top">
                                            <tr>
                                                <th>Product</th>
                                                <th>Barcode</th>                                               
                                                <th style="width: 100px;">Stickers</th>
                                                <th class="text-center">Action</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <tr v-for="(item, index) in selectedItems" :key="index">
                                                <td>
                                                    <div class="fw-semibold text-dark">{{ item.product.item_name }}</div>
                                                </td>
                                                <td>
                                                    <span class="badge bg-light text-dark font-monospace">{{ item.product.code_no }}</span>
                                                </td>
                                               
                                               
                                                <td>
                                                    <div class="input-group input-group-sm">
                                                        <button class="btn btn-outline-secondary px-2" type="button" @click="item.qty > 1 ? item.qty-- : null">-</button>
                                                        <input type="number" v-model.number="item.qty" class="form-control text-center px-1" min="1">
                                                        <button class="btn btn-outline-secondary px-2" type="button" @click="item.qty++">+</button>
                                                    </div>
                                                </td>
                                                <td class="text-center">
                                                    <button class="btn btn-sm btn-link text-danger p-0" @click="removeFromQueue(index)">
                                                        <i class="fa-solid fa-trash-can"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="card bg-light border-0 mb-2 shadow-sm">
                                    <div class="card-body p-3">
                                        <h6 class="fw-bold mb-2"><i class="fa-solid fa-sliders me-2"></i>Sticker Settings</h6>
                                        <div class="row g-3">
                                            <div class="col-sm-3">
                                                <label class="form-label small fw-bold">Columns per row</label>
                                                <select v-model.number="colsCount" class="form-select form-select-sm bg-white border-0 shadow-sm">
                                                    <option :value="1">1 Column</option>
                                                    <option :value="2">2 Columns</option>
                                                    <option :value="3">3 Columns</option>
                                                    <option :value="4">4 Columns</option>
                                                    <option :value="6">6 Columns</option>
                                                </select>
                                            </div>
                                            <div class="col-sm-2">
                                                <label class="form-label small fw-bold">Sticker Width (mm)</label>
                                                <input type="number" v-model.number="stickerWidth" class="form-control form-control-sm bg-white border-0 shadow-sm" placeholder="Width (mm)" min="10">
                                            </div>
                                            <div class="col-sm-2">
                                                <label class="form-label small fw-bold">Sticker Height (mm)</label>
                                                <input type="number" v-model.number="stickerHeight" class="form-control form-control-sm bg-white border-0 shadow-sm" placeholder="Height (mm)" min="10">
                                            </div>
                                            <div class="col-5">
                                                <label class="form-label small fw-bold">Shop/Company Name</label>
                                                <div class="input-group input-group-sm">
                                                    <div class="input-group-text bg-white border-0 shadow-sm">
                                                        <input class="form-check-input mt-0" type="checkbox" v-model="useShopName">
                                                    </div>
                                                    <input type="text" class="form-control bg-white border-0 shadow-sm" placeholder="Enter Shop Name" v-model="shopName" :disabled="!useShopName">
                                                </div>
                                            </div>
                                            <div class="col-12">
                                                <div class="d-flex flex-wrap gap-3">
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="chkShowName" v-model="showName">
                                                        <label class="form-check-label small" for="chkShowName">Show Product Name</label>
                                                    </div>
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="chkShowPrice" v-model="showPrice">
                                                        <label class="form-check-label small" for="chkShowPrice">Show Price</label>
                                                    </div>
                                                    <div class="form-check">
                                                        <input class="form-check-input" type="checkbox" id="chkShowText" v-model="showCodeText">
                                                        <label class="form-check-label small" for="chkShowText">Show Barcode Value</label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="d-flex gap-2 justify-content-end mb-1">
                                    <button class="btn btn-outline-danger btn-sm" @click="clearQueue">
                                        <i class="fa-solid fa-trash-can me-1"></i> Clear Selected
                                    </button>
                                    <button class="btn btn-primary btn-sm px-4" @click="printStickers">
                                        <i class="fa-solid fa-print me-1"></i> Print Stickers
                                    </button>
                                </div>

                                <h6 class="fw-bold mb-2"><i class="fa-solid fa-eye me-2"></i>Live Preview</h6>
                                <div class="card border bg-light shadow-sm p-3 overflow-auto" >
                                    <div class="barcode-print-grid" :style="`display: grid; grid-template-columns: repeat(${colsCount}, 1fr); gap: 12px; width: ${stickerWidth * 4.5}px; margin: 0 auto;`">
                                        <div class="barcode-sticker border bg-white rounded shadow-sm text-center" v-for="item in flatStickers" :key="item.id" :style="`width: 100%; height: ${stickerHeight * 4.5}px; padding: ${1.5 * 4.5}px 2px 1px 2px; overflow: hidden; display: flex; flex-direction: column; justify-content: flex-start; align-items: center; box-sizing: border-box;`">
                                            <div class="barcode-sticker-shop text-uppercase fw-bold text-truncate w-100" v-if="useShopName && shopName" :style="`font-size: ${stickerHeight * 0.15 * 4.5}px; line-height: 1.1; margin-bottom: 2px;`">{{ shopName }}</div>
                                            <div class="barcode-sticker-name text-truncate small  w-100 mb-1" v-if="showName" :title="item.name" :style="`font-size: ${stickerHeight * 0.12 * 4.5}px;  font-weight: bold;`">{{ item.name }}</div>
                                            <div class="barcode-sticker-svg-container" style="display: flex; align-items: center; justify-content: center; width: 100%;">
                                                <svg :viewBox="`0 0 ${item.barcode.width} 50`" class="barcode-svg" xmlns="http://www.w3.org/2000/svg" :style="`height: ${stickerHeight * 0.35 * 4.5}px; max-width: 90%; width: auto;`">
                                                    <path :d="item.barcode.path" fill="#000000" />
                                                </svg>
                                            </div>
                                            <div class="barcode-sticker-code font-monospace small mb-1 w-100" v-if="showCodeText" :style="`font-size: ${stickerHeight * 0.1 * 4.5}px; line-height: 1;`">{{ item.code_no }}</div>
                                            <div class="barcode-sticker-price fw-bold text-primary w-100" v-if="showPrice" :style="`font-size: ${stickerHeight * 0.13 * 4.5}px; line-height: 1;`">{{ fM(item.price) }} Ks</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
            </div>
        </div>

        <!-- Printable Area for Barcode Stickers -->
        <div class="d-none" id="printStickersArea">
            <div class="barcode-print-grid-print" :style="`--cols-count: ${colsCount}; --sticker-width: ${stickerWidth}mm; --sticker-height: ${stickerHeight}mm;`">
                <div class="barcode-sticker-print" v-for="item in flatStickers" :key="'print-' + item.id">
                    <div class="barcode-sticker-shop-print" v-if="useShopName && shopName">{{ shopName }}</div>
                    <div class="barcode-sticker-name-print" v-if="showName">{{ item.name }}</div>
                    <div class="barcode-sticker-svg-container-print">
                        <svg :viewBox="`0 0 ${item.barcode.width} 50`" class="barcode-svg-print" xmlns="http://www.w3.org/2000/svg">
                            <path :d="item.barcode.path" fill="#000000" />
                        </svg>
                    </div>
                    <div class="barcode-sticker-code-print" v-if="showCodeText">{{ item.code_no }}</div>
                    <div class="barcode-sticker-price-print" v-if="showPrice">{{ fM(item.price) }} Ks</div>
                </div>
            </div>
            <component :is="'style'">
                @media print {
                    @page {
                        size: {{ printPageWidth }}mm {{ printPageHeight }}mm;
                        margin: 0 !important;
                    }
                    html, body {
                        margin: 0 !important;
                        padding: 0 !important;
                        width: 100% !important;
                        height: 100% !important;
                        background: #ffffff !important;
                    }
                    #printStickersArea {
                        display: flex !important;
                        justify-content: center !important;
                        align-items: center !important;
                        width: 100% !important;
                        height: 100% !important;
                        margin: 0 !important;
                        padding: 0 !important;
                        box-sizing: border-box !important;
                        background: #ffffff !important;
                    }
                }
                .barcode-print-grid-print {
                    display: grid !important;
                    grid-template-columns: repeat(var(--cols-count, 2), 1fr) !important;
                    gap: 0 4px !important;
                    width: var(--sticker-width, 80mm) !important;
                    margin: 0 auto !important;
                    padding: 0 !important;
                    box-sizing: border-box !important;
                    background: #ffffff !important;
                }
                .barcode-sticker-print {
                    width: 100% !important;
                    height: var(--sticker-height, 20mm) !important;
                    display: flex !important;
                    flex-direction: column !important;
                    align-items: center !important;
                    justify-content: flex-start !important;
                    border: 1px dashed #ccc !important;
                    padding: 1.5mm 2px 1px 2px !important;
                    box-sizing: border-box !important;
                    page-break-inside: avoid !important;
                    overflow: hidden !important;
                    background: #ffffff !important;
                    text-align: center !important;
                }
                .barcode-sticker-shop-print {
                    font-size: calc(var(--sticker-height, 20mm) * 0.15) !important;
                    font-weight: bold !important;
                    margin-bottom: 1px !important;
                    text-transform: uppercase !important;
                    white-space: nowrap !important;
                    overflow: hidden !important;
                    text-overflow: ellipsis !important;
                    width: 100% !important;
                    color: #000000 !important;
                    line-height: 1 !important;
                }
                 .barcode-sticker-name-print {
                    font-size: calc(var(--sticker-height, 20mm) * 0.12) !important;
                    margin-bottom: 2px !important;
                    line-height: 1.1 !important;
                    overflow: hidden !important;
                    text-overflow: ellipsis !important;
                    display: -webkit-box !important;
                    -webkit-line-clamp: 2 !important;
                    -webkit-box-orient: vertical !important;
                    color: #000000 !important;
                    font-weight: bold !important;
                    width: 100% !important;
                }
                .barcode-sticker-svg-container-print {
                    width: 100% !important;
                    margin: 0 !important;
                    text-align: center !important;
                    display: flex !important;
                    align-items: center !important;
                    justify-content: center !important;
                }
                .barcode-svg-print {
                    max-width: 90% !important;
                    width: auto !important;
                    height: calc(var(--sticker-height, 20mm) * 0.35) !important;
                    display: block !important;
                    margin: 0 auto !important;
                }
                .barcode-sticker-code-print {
                    font-size: calc(var(--sticker-height, 20mm) * 0.1) !important;
                    margin-top: 1px !important;
                    letter-spacing: 0.5px !important;
                    font-family: monospace !important;
                    color: #000000 !important;
                    line-height: 1 !important;
                }
                .barcode-sticker-price-print {
                    font-size: calc(var(--sticker-height, 20mm) * 0.13) !important;
                    font-weight: bold !important;
                    margin-top: 2px !important;
                    color: #000000 !important;
                    line-height: 1 !important;
                }
            </component>
        </div>
       

</div>   
 
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp,onSnapshot, collection, addDoc, query, where, setDoc, getCountFromServer, getDocs, doc, getDoc, updateDoc, limit, orderBy, deleteDoc} from 'firebase/firestore'
import { Modal } from 'bootstrap'
import db from '@/firebase/firestore'
import Footer from '@/views/partials/Footer.vue'
import * as ss from "@/lib/snapshot/products"
import * as hp from "@/lib/helper"



import {lang} from "@/lib/language"


import * as cat from "@/lib/snapshot/category"

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
            }
    },
    data(){
        return{
            code_no :"",
            item_name:"",
            products : [],
            isLoading: false,
            sortBy:"asc",
            sort_item:"",
            search_by_code:"",
            search_by_description:"",
            search_by_category:"",
            productsData:[],           
            editItem:{},          
           
            msg : {
                type :"",
                info :"",
            },
           
           unsubProducts:null,
         
           limitData: 20,
         
           productCount: 0,
           cats:[],
           selectedItems: [],
           colsCount: 2,
           showName: true,
           showPrice: true,
           showCodeText: true,
           useShopName: false,
           shopName: "",
           stickerWidth: 80,
           stickerHeight: 20,
        }
    },
    created() {     
      
    },
    mounted() {

        let camera=localStorage.getItem("camera");
        this.useCamera=camera=="false" ? false : false;
       
        this.isLoading=true;
     
        window.addEventListener('keydown', this.handleCtrl);
     //  this.fetchProductCount();
       this.fetchProducts();
       this.fetchCategory();
    },
    unmounted() {
        if(this.unsubProducts){
            this.unsubProducts=null
        }
    },
 
    computed:{
        user(){
            return this.$store.getters.user;
        },
        pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.barcode_sticker.mm}` : `${lang.barcode_sticker.eng}`            
            return pageTitle;
        },
        t() {
            return (key) => this.$store.getters.t(key);
        },
        flatStickers() {
            const list = [];
            for (const item of this.selectedItems) {
                const bcode = this.code128(item.product.code_no || '');
                for (let i = 0; i < item.qty; i++) {
                    list.push({
                        id: `${item.product.id}-${item.unit}-${i}`,
                        name: item.product.item_name,
                        code_no: item.product.code_no,
                        price: item.price,
                        unitName: this.getUnitLabel(item.product, item.unit),
                        barcode: bcode
                    });
                }
            }
            return list;
        },
        printPageWidth() {
            return this.stickerWidth || 80;
        },
        printPageHeight() {
            return this.stickerHeight || 20;
        }
        
      
      
    },
    watch:{
       
        msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                       this.msg.info="";
                    }, 2000)
                }
            },deep:true
        }
    },

    methods:{
        setEdit(product){
            this.editItem=product;
        },
         async fetchCategory(){                    
                const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
               const unsub= onSnapshot(query_url, async(snapShot) =>{
                    const cats=cat.category(snapShot);   
                    this.cats=cats;         
                    unsub();    
                })
               
           } ,
     
    
    
        resetInput(){            
            this.item_name="";
            this.code_no="";
            this.v$.$reset()
            this.isSaving=false;
        },
       
      
      
       
         inputSearchName(){
             this.search_by_description=event.target.value;
             this.searchProducts();
         },
         clearSearch(){
             this.search_by_code="";
             this.search_by_description="";
             this.search_by_category="";
             this.searchProducts();
         },
         onPaste(){
             setTimeout(()=>{
                 this.searchProducts();
             }, 200)
         },
      
        async searchProducts() {
            this.products = [];
            this.isLoading = true;
            try {
                if (this.search_by_code) {
                    const query_url = query(collection(db, "products"), where("code_no", "==", this.search_by_code));
                    const snapShot = await getDocs(query_url);
                    const products = ss.products(snapShot);
                    this.products = products;
                    console.log("Work");
                    
                } else if (this.search_by_description) {
                    const query_url = query(
                        collection(db, "products"), where("keywords", "array-contains", this.search_by_description.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const products = ss.products(snapShot);
                    this.products = products;
                   
                } else if (this.search_by_category) {
                    const query_url = query(
                        collection(db, "products"), where("category.cat_name","==", this.search_by_category), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const products = ss.products(snapShot);
                    this.products = products;
                   
                }else {
                    this.products = this.productsData;
                  
                }
            } catch (error) {
                console.error("Error searching products:", error);
            } finally {
                this.isLoading = false;
            }
        },

      
       async fetchProducts(){
        this.isLoading=true;
        this.products=[]
        const query_url=query(collection(db, "products"), orderBy("created_at", "desc"), limit(this.limitData));           
          this.unsubProducts=onSnapshot(query_url, async(snapShot) =>{     
                const products=ss.products(snapShot);   
                this.productsData=products;
                this.products=products;                
                this.isLoading=false;
                if((!this.editItem || Object.keys(this.editItem).length === 0) && products.length){
                    this.setEdit(this.products[0]) 
                }
            });
       },
       addToQueue(product) {
            if (!product) return;
            let unit = "";
            if (product.packing_size) {
                if (product.packing_size.largest_unit) {
                    unit = "largest_unit";
                } else if (product.packing_size.medium_unit) {
                    unit = "medium_unit";
                } else if (product.packing_size.smallest_unit) {
                    unit = "smallest_unit";
                }
            }
            if (!unit) {
                unit = "largest_unit";
            }
            const price = this.getProductPrice(product, unit);
            const existing = this.selectedItems.find(item => item.product.id === product.id && item.unit === unit);
            if (existing) {
                existing.qty += 1;
            } else {
                this.selectedItems.push({
                    product,
                    qty: 1,
                    unit: unit,
                    price: price
                });
            }
        },
        removeFromQueue(index) {
            this.selectedItems.splice(index, 1);
        },
        clearQueue() {
            this.selectedItems = [];
        },
        onUnitChange(item) {
            item.price = this.getProductPrice(item.product, item.unit);
        },
        getProductPrice(product, unit) {
            if (!product || !product.retail_price) return 0;
            return product.retail_price[unit] || 0;
        },
        getUnitLabel(product, unit) {
            if (!product || !product.packing_size) return "";
            if (unit === "largest_unit") return product.packing_size.largest_unit || "Largest";
            if (unit === "medium_unit") return product.packing_size.medium_unit || "Medium";
            if (unit === "smallest_unit") return product.packing_size.smallest_unit || "Smallest";
            return "";
        },
        fM(amount) {
            return hp.formattedAmount(amount);
        },
        code128(text) {
            if (!text) return { width: 0, path: "" };
            const patterns = [
                "212222", "222122", "222221", "121223", "121322", "131222", "122213", "122312", "132212", "221213",
                "221312", "231212", "112232", "122132", "122231", "113222", "123122", "123221", "223211", "221132",
                "221231", "213212", "223112", "312131", "311222", "321122", "321221", "312212", "322112", "322211",
                "212123", "212321", "232121", "111323", "131123", "131321", "112313", "132113", "132311", "211313",
                "231113", "231311", "112133", "112331", "132131", "113123", "113321", "133121", "313121", "211331",
                "231131", "213113", "213311", "213131", "311123", "311321", "331121", "312113", "312311", "332111",
                "314111", "221411", "431111", "111224", "111422", "121124", "121421", "141122", "141221", "112214",
                "112412", "122114", "122411", "142112", "142211", "241211", "221114", "413111", "241112", "134111",
                "111242", "121142", "121241", "114212", "124112", "124211", "411212", "421112", "421211", "212141",
                "214121", "412121", "111143", "111341", "131141", "114113", "114311", "411113", "411311", "113141",
                "114131", "311141", "411131", "211412", "211214", "211232", "2331112"
            ];
            
            let pos = 0;
            let currentMode = ""; 
            let codeList = [];
            
            const countDigits = (str, start) => {
                let count = 0;
                for (let i = start; i < str.length; i++) {
                    const c = str.charCodeAt(i);
                    if (c >= 48 && c <= 57) count++;
                    else break;
                }
                return count;
            };
            
            if (countDigits(text, 0) >= 4) {
                currentMode = "C";
                codeList.push(105); 
            } else {
                currentMode = "B";
                codeList.push(104); 
            }
            
            while (pos < text.length) {
                if (currentMode === "B") {
                    if (countDigits(text, pos) >= 4) {
                        codeList.push(99); 
                        currentMode = "C";
                    } else {
                        const code = text.charCodeAt(pos) - 32;
                        if (code >= 0 && code <= 95) {
                            codeList.push(code);
                        }
                        pos++;
                    }
                } else if (currentMode === "C") {
                    if (countDigits(text, pos) >= 2) {
                        const val = parseInt(text.substr(pos, 2), 10);
                        codeList.push(val);
                        pos += 2;
                    } else {
                        codeList.push(100); 
                        currentMode = "B";
                    }
                }
            }
            
            let checksum = codeList[0];
            for (let i = 1; i < codeList.length; i++) {
                checksum += codeList[i] * i;
            }
            
            const checkDigit = checksum % 103;
            codeList.push(checkDigit);
            codeList.push(106); 
            
            let x = 0;
            let d = "";
            for (let i = 0; i < codeList.length; i++) {
                const pattern = patterns[codeList[i]];
                for (let j = 0; j < pattern.length; j++) {
                    const width = parseInt(pattern[j], 10);
                    if (j % 2 === 0) {
                        d += `M${x},0 L${x},50 L${x + width},50 L${x + width},0 Z `;
                        x += width;
                    } else {
                        x += width;
                    }
                }
            }
            return {
                width: x,
                path: d
            };
        },
        async printStickers() {
            if (this.flatStickers.length === 0) {
                alert("Please add some items to print stickers!");
                return;
            }
            await this.$htmlToPaper("printStickersArea");
        }
    }
}
</script>
<style>
.barcode-print-grid {
  display: grid;
  grid-template-columns: repeat(var(--cols-count, 2), 1fr);
  gap: 12px;
  width: 100%;
}
.barcode-sticker {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.barcode-sticker:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1) !important;
}
.barcode-sticker-shop {
  font-size: 10px;
  font-weight: 700;
  color: #888;
}
.barcode-sticker-name {
  font-size: 11px;
  font-weight: 600;
  color: #333;
}
.barcode-svg {
  width: 100%;
  max-width: 130px;
  height: 35px;
}
.barcode-sticker-code {
  font-size: 10px;
  color: #666;
  letter-spacing: 0.5px;
}
.barcode-sticker-price {
  font-size: 12px;
  font-weight: 700;
}
</style>
