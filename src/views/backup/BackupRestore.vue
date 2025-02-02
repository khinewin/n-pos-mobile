<template lang="">
    <div>
        <PageHeader page-title="Backup / Restore" page-code="reports" :msg-type="msg.type" :msg-info="msg.info" />        
        <div class="container-fluid">
        <div class="card">
        <div class="card-body">               
        <div class="row mb-4">
            <div class="col-12 col-sm-12 col-md-6">
                <div class="h5 mb-2">Restore</div>
                <div class="row">
                    <div class="col-12">
                        <div class="input-group">
                            <input :disabled="isLoading" type="file" id="importTmp" @change="tmpFile" accept=".json" class="form-control form-control-sm">
                            <span class="input-group-text p-0 bg-white">
                                <button class="btn btn-primary btn-sm" @click="importTmp">
                                    <span v-if="!isLoading">Upload</span>
                                    <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                                </button>
                            </span>
                          </div> 
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <div class="h5  mb-2">Backup</div>
                <div class="min-vh-60">
                    <div class=" p-2 rounded small">
                        <div class="row g-2">                              
                            <div class="col-6 col-sm-6 col-md-4">
                               <div class="input-group mb-2">
                                <div class="form-floating">
                                    <flat-pickr @change="changeBackupDate" class="form-control-plaintext border-bottom" v-model="from_date" id="from_date" :config="config" />
                                    <label for="from_date">From date</label>
                                </div>
                                <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                               </div>  
                            </div>
                            <div class="col-6 col-sm-6 col-md-4">
                                <div class="input-group mb-2">
                                    <div class="form-floating">
                                        <flat-pickr @change="changeBackupDate" class="form-control-plaintext border-bottom" v-model="to_date" id="to_date" :config="config" />
                                        <label for="to_date">To date</label>
                                    </div>
                                    <span class="input-group-text bg-white border-0 border-bottom"><i class="fa-solid fa-calendar-days"></i></span>
                                   </div>
                            </div>
                            <div class="col-12 col-md-4 d-grid">
                                <button  @click="exportAll" class="btn btn-outline-dark">Export All</button>
                            </div>
                        </div>
                        <div class="row mt-2 justify-content-center mb-4">
                            <div class="col-12">
                                <div class="row g-2">    
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="products.length <=0" @click="exportProducts" class="btn py-4 btn-primary">Product Code
                                             ({{products.length}})                                                
                                            </button>
                                            <button type="button" v-if="products.length <= 0"  @click="fetchProducts"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="sales.length <=0" @click="exportSales" class="btn py-4 btn-success">Sales ({{sales.length}})</button>
                                        <button type="button" v-if="sales.length <= 0"  @click="fetchSales"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="purchases.length <=0" @click="exportPurchases" class="btn py-4 btn-dark">Purchases ({{purchases.length}})</button>
                                        <button type="button" v-if="purchases.length <= 0"  @click="fetchPurchase"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="stocks.length <=0" @click="exportStocks" class="btn py-4 btn-secondary">Stocks ({{stocks.length}})</button>
                                        <button type="button" v-if="stocks.length <= 0"  @click="fetchStocks"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                  
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="adjustments.length <=0" @click="exportAdjustments" class="btn py-4 btn-success">Adjustments ({{adjustments.length}})</button>
                                        <button type="button" v-if="adjustments.length <= 0"  @click="fetchAdjustments"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="suppliers.length <=0" @click="exportSuppliers" class="btn py-4 btn-success">Suppliers ({{suppliers.length}})</button>
                                        <button type="button" v-if="suppliers.length <= 0"  @click="fetchSuppliers"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="customers.length <=0" @click="exportCustomers" class="btn py-4 btn-secondary">Customers ({{customers.length}})</button>
                                        <button type="button" v-if="customers.length <= 0"  @click="fetchCustomers"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                    <div class="col-6 col-sm-4 d-grid position-relative">
                                        <button :disabled="packingSizes.length <=0" @click="exportPackingSizes" class="btn py-4 btn-primary">Packing Size ({{packingSizes.length}})</button>
                                        <button type="button" v-if="packingSizes.length <= 0"  @click="fetchPackingSizes"  class="btn position-absolute top-50 start-50 translate-middle"><i class="fa-solid fa-rotate-right"></i></button>
                                    </div>
                                                               
                                        <!--<div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="products.length <=0" @click="exportProducts" class="btn py-4 btn-outline-primary">Product Code ({{products.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="sales.length <=0" @click="exportSales" class="btn py-4 btn-outline-success">Sales ({{sales.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="purchases.length <=0" @click="exportPurchases" class="btn py-4 btn-outline-dark">Purchases ({{purchases.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="stocks.length <=0" @click="exportStocks" class="btn py-4 btn-outline-secondary">Stocks ({{stocks.length}})</button>
                                        </div>-->
                                        <!--<div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="transfers.length <=0" @click="exportTransfers" class="btn py-4 btn-outline-primary">Transfers ({{transfers.length}})</button>
                                        </div>-->
                                        <!--<div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="adjustments.length <=0" @click="exportAdjustments" class="btn py-4 btn-outline-success">Adjustments ({{adjustments.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="suppliers.length <=0" @click="exportSuppliers" class="btn py-4 btn-outline-success">Suppliers ({{suppliers.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="customers.length <=0" @click="exportCustomers" class="btn py-4 btn-outline-secondary">Customers ({{customers.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="packingSizes.length <=0" @click="exportPackingSizes" class="btn py-4 btn-outline-primary">Packing Size ({{packingSizes.length}})</button>
                                        </div>-->
                                        <!--<div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="returnIn.length <=0" @click="exportReturnIn" class="btn py-4 btn-outline-primary">Return In ({{returnIn.length}})</button>
                                        </div>
                                        <div class="col-6 col-sm-4 d-grid">
                                            <button :disabled="returnOut.length <=0" @click="exportReturnOut" class="btn py-4 btn-outline-primary">Return Out ({{returnOut.length}})</button>
                                        </div>-->
                                </div>
                            </div>                                                     
                        </div>
                        <div class="col-12">
                            <div class="h5  mb-2 change-cursor" @click="showDelete=!showDelete">Delete</div>
                            <div class="row g-2" v-if="showDelete">                                   
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="products.length <=0" @click="deleteProducts" class="btn py-4 btn-danger">Product Code ({{products.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="sales.length <=0" @click="deleteSales" class="btn py-4 btn-danger">Sales ({{sales.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="purchases.length <=0" @click="deletePurchases" class="btn py-4 btn-danger">Purchases ({{purchases.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="stocks.length <=0" @click="deleteStocks" class="btn py-4 btn-danger">Stocks ({{stocks.length}})</button>
                                </div>
                                <!--<div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="transfers.length <=0" @click="deleteTransfers" class="btn py-4 btn-danger">Transfers ({{transfers.length}})</button>
                                </div>-->
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="adjustments.length <=0" @click="deleteAdjustments" class="btn py-4 btn-danger">Adjustments ({{adjustments.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="suppliers.length <=0" @click="deleteSuppliers" class="btn py-4 btn-danger">Suppliers ({{suppliers.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="customers.length <=0" @click="deleteCustomers" class="btn py-4 btn-danger">Customers ({{customers.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="packingSizes.length <=0" @click="deletePackingSizes" class="btn py-4 btn-danger">Packing Size ({{packingSizes.length}})</button>
                                </div>
                                <!--<div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="returnIn.length <=0" @click="deleteReturnIn" class="btn py-4 btn-danger">Return In ({{returnIn.length}})</button>
                                </div>
                                <div class="col-6 col-sm-4 d-grid">
                                    <button :disabled="returnOut.length <=0" @click="deleteReturnOut" class="btn py-4 btn-danger">Return Out ({{returnOut.length}})</button>
                                </div>-->
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
import {Timestamp, collection, addDoc, getDocs, doc, deleteDoc, onSnapshot, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp_sales from "@/lib/snapshot/sales"
import * as hp_purchases from "@/lib/snapshot/purchases"
import * as hp_products from "@/lib/snapshot/products"
import * as hp_stocks from "@/lib/snapshot/stocks"
import * as hp_transfers from "@/lib/snapshot/transfers"
import * as hp_adjustments from "@/lib/snapshot/adjustments"
import * as hp_suppliers from "@/lib/snapshot/suppliers"
import * as hp_customers from "@/lib/snapshot/customers"
import * as hp_packingSizes from "@/lib/snapshot/packingSizes"
import * as hp_rt_in from "@/lib/snapshot/returnIn"
import * as hp_rt_out from "@/lib/snapshot/returnOut"



import * as hp  from "@/lib/helper"
import * as ie from "@/lib/import_export"
import FlatPickr from 'vue-flatpickr-component';
import PageHeader from "@/views/partials/PageHeader.vue"

export default {

    components:{
        FlatPickr, PageHeader
    },
    data(){
        return {
                isLoading:false,
                sales:[],     
                purchases:[], 
                products:[],  
                stocks:[],
                transfers:[],
                adjustments:[],
                suppliers:[],
                customers:[],
                packingSizes:[],
                returnIn:[],
                returnOut:[],

                from_date:"",
                to_date:"",

                restore_sales:"",
                restore_purchases:"",
                restore_products:"",
                restore_stocks:"",

                restore_file:"",
                restore_name:"",
                showDelete:false,

                msg:{
                    info:"",
                    type:"",
                },
                config: {
                dateFormat: 'd/m/Y', // Flatpickr options
                enableTime: false,
                wrap: true, // set wrap to true only when using 'input-group'
               // altFormat: 'M j, Y',
            },  
            timer: 10,
        }
    },
    watch:{
        msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                        this.msg.info="";
                    }, 3000)
                }
            },deep:true
        }
    },
    created() {
        this.from_date=hp.setSearchFromDate();
        this.to_date=hp.setToday();
    },
    mounted(){
       /*
        this.fetchSales();
        this.fetchPurchase();
        this.fetchProducts();
        this.fetchStocks();
        //this.fetchTransfers();
        this.fetchAdjustments();
        this.fetchSuppliers();
        this.fetchCustomers();
        this.fetchPackingSizes();
      //  this.fetchReturnIn();
        //this.fetchReturnOut();
        */
    },
    
    methods:{

        deleteProducts(){
            ie.deleteData(this.products, "products")
        },
        deleteSales(){
            ie.deleteData(this.sales, "sales")
        },
        deletePurchases(){
            ie.deleteData(this.purchases, "purchases")
        },
        deleteStocks(){
            ie.deleteData(this.stocks, "stocks")
        },
        deleteTransfers(){
            ie.deleteData(this.transfers, "transfers")
        },
        deleteAdjustments(){
            ie.deleteData(this.adjustments, "adjustments")
        },
        deletePackingSizes(){
            ie.deleteData(this.packingSizes, "packingSizes")
        },
        deleteSuppliers(){
            ie.deleteData(this.suppliers, "suppliers")
        },
        deleteCustomers(){
            ie.deleteData(this.customers, "customers")
        },
        deleteReturnIn(){
            ie.deleteData(this.returnIn, "returnIn")
        },
        deleteReturnOut(){
            ie.deleteData(this.returnOut, "returnOut")
        },
        

        changeBackupDate(){
            this.fetchSales();
            this.fetchPurchase();
            this.fetchStocks();
           // this.fetchTransfers();
            this.fetchAdjustments();
           // this.fetchReturnIn();
            //this.fetchReturnOut();
        },
        finishProcess(name){
            this.msg.info=`${name} data has ben restore on the server`
            this.msg.type="success"
            const input=document.querySelector("#importTmp")
            input.value="";
            this.isLoading=false;
        },
        tmpFile(event){
            const file = event.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (e) => {
                        const file_name=file.name.substring(0, file.name.indexOf("_"));
                        this.restore_file=e.target.result;
                        this.restore_name=file_name;
                };
                reader.readAsText(file);
            }
        },

        importTmp(){         
            if(!this.restore_file) return false;
            try {
                this.isLoading=true;
                const data = JSON.parse(this.restore_file);
                data.map(async (d,i)=>{
                    setTimeout(async ()=>{      
                        if(this.restore_name=="ProductsCode"){
                            ie.restoreProducts(d)
                        }else if(this.restore_name=="Stocks"){
                            ie.restoreStocks(d)
                        }else if(this.restore_name=="Sales"){
                            ie.restoreSales(d)
                        }else if(this.restore_name=="Purchases"){
                            ie.restorePurchases(d);
                        }else if(this.restore_name=="Transfers"){
                            ie.restoreTransfers(d);
                        }else if(this.restore_name=="Adjustments"){
                            ie.restoreAdjustments(d);
                        }else if(this.restore_name=="Suppliers"){
                            ie.restoreSuppliers(d);
                        }else if(this.restore_name=="Customers"){
                            ie.restoreCustomers(d);
                        }else if(this.restore_name=="PackingSize"){
                            ie.restorePackingSizes(d);
                        }else if(this.restore_name=="ReturnIn"){
                            ie.restoreReturnIn(d);
                        }else if(this.restore_name=="ReturnOut"){
                            ie.restoreReturnOut(d);
                        }
                        if(i+1 == data.length){
                            this.finishProcess(this.restore_name)
                            this.restore_file="";
                            this.restore_name="";
                        }
                    }, this.timer * i)
                })
            }catch(err){
                 this.msg.info="Restoring in error."
                this.msg.type="error"
                this.isLoading=false;
            }  
        },       
      
    exportAll(){
        this.exportSales();
        this.exportPurchases()
        this.exportProducts();
        this.exportStocks();
        this.exportTransfers();
        this.exportAdjustments();
        this.exportSuppliers();
        this.exportCustomers();
        this.exportPackingSizes();
    }, 
    exportSales(){
    if(this.sales.length<=0) return false;
       const result= ie.exportToJson(this.sales, `Sales_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Sales data has been backup to local"
           this.msg.type="success"
       }
    },
    exportPurchases(){
        if(this.purchases.length<=0) return false;
       const result= ie.exportToJson(this.purchases, `Purchases_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Purchase data has been backup to local"
           this.msg.type="success"
       }
    },
    exportProducts(){
        if(this.products.length<=0) return false;
       const result= ie.exportToJson(this.products, `ProductsCode_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Medicines code data has been backup to local"
           this.msg.type="success"
       }
    },
    exportStocks(){
        if(this.stocks.length <0) return false
       const result= ie.exportToJson(this.stocks, `Stocks_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Stocks data has been backup to local"
           this.msg.type="success"
       }
    },
    exportTransfers(){
        if(this.transfers.length<=0) return false;
       const result= ie.exportToJson(this.transfers, `Transfers_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Transfers data has been backup to local"
           this.msg.type="success"
       }
    },
    exportAdjustments(){
        if(this.adjustments.length<=0) return false;
       const result= ie.exportToJson(this.adjustments, `Adjustments_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Adjustments data has been backup to local"
           this.msg.type="success"
       }
    },
    exportSuppliers(){
        if(this.suppliers.length<=0) return false;
       const result= ie.exportToJson(this.suppliers, `Suppliers_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Suppliers data has been backup to local"
           this.msg.type="success"
       }
    },
    exportCustomers(){
        if(this.customers.length<=0) return false;
       const result= ie.exportToJson(this.customers, `Customers_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Customers data has been backup to local"
           this.msg.type="success"
       }
    },
    exportPackingSizes(){
        if(this.packingSizes.length<=0) return false;
       const result= ie.exportToJson(this.packingSizes, `PackingSize_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Packing size data has been backup to local"
           this.msg.type="success"
       }
    },
    exportReturnIn(){
        if(this.returnIn.length<=0) return false;
       const result= ie.exportToJson(this.returnIn, `ReturnIn_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Return In data has been backup to local"
           this.msg.type="success"
       }
    },
    exportReturnOut(){
        if(this.returnOut.length<=0) return false;
       const result= ie.exportToJson(this.returnOut, `ReturnOut_${this.from_date}_${this.to_date}`)
       if (result){
           this.msg.info="Return Out data has been backup to local"
           this.msg.type="success"
       }
    },
    
    fetchSales(){
            const from_date=Timestamp.fromDate(hp.searchFromDate(this.from_date))
             const to_date=Timestamp.fromDate(hp.searchToDate(this.to_date))     
            const query_url = query(collection(db, "sales"), where("sale_date", ">=", from_date), where("sale_date", "<=", to_date));
            onSnapshot(query_url, async(snapShot) =>{                                 
                        let sales=hp_sales.sales(snapShot)  
                        sales=sales.map((p)=>{
                            return {...p, sale_date: new Date(p.sale_date.toDate())}
                        })
                       this.sales=sales;
                });              
     },
    
    fetchPurchase(){
            const from_date=Timestamp.fromDate(hp.searchFromDate(this.from_date))
             const to_date=Timestamp.fromDate(hp.searchToDate(this.to_date))     
            const query_url = query(collection(db, "purchases"), where("pur_date", ">=", from_date), where("pur_date", "<=", to_date));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let purchases=hp_purchases.purchases(snapShot)  
                        purchases=purchases.map((p)=>{
                            return {...p, pur_date: new Date(p.pur_date.toDate())}
                        })
                       this.purchases=purchases;
                });              
        },

        fetchProducts(){       
            const query_url = query(collection(db, "products"));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let products=hp_products.products(snapShot)  
                        products=products.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.products=products;
                });              
        },
        fetchStocks(){
            const from_date=Timestamp.fromDate(hp.searchFromDate(this.from_date))
             const to_date=Timestamp.fromDate(hp.searchToDate(this.to_date))     
            const query_url = query(collection(db, "stocks"), where("created_at", ">=", from_date), where("created_at", "<=", to_date));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let stocks=hp_stocks.stocks(snapShot)  
                        stocks=stocks.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.stocks=stocks;
                });              
        },
        fetchTransfers(){
            const from_date=Timestamp.fromDate(hp.searchFromDate(this.from_date))
             const to_date=Timestamp.fromDate(hp.searchToDate(this.to_date))     
            const query_url = query(collection(db, "transfers"), where("created_at", ">=", from_date), where("created_at", "<=", to_date));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let transfers=hp_transfers.transfers(snapShot)  
                        transfers=transfers.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.transfers=transfers;
                });              
        },
        fetchAdjustments(){
            const from_date=Timestamp.fromDate(hp.searchFromDate(this.from_date))
             const to_date=Timestamp.fromDate(hp.searchToDate(this.to_date))     
            const query_url = query(collection(db, "adjustments"), where("created_at", ">=", from_date), where("created_at", "<=", to_date));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let adjustments=hp_adjustments.adjustments(snapShot)  
                        adjustments=adjustments.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.adjustments=adjustments;
                });              
        },
        fetchSuppliers(){           
            const query_url = query(collection(db, "suppliers"));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let suppliers=hp_suppliers.suppliers(snapShot)  
                        suppliers=suppliers.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.suppliers=suppliers;
                });              
        },
        fetchCustomers(){           
            const query_url = query(collection(db, "customers"));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let customers=hp_customers.customers(snapShot)  
                        customers=customers.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.customers=customers;
                });              
        },
        fetchPackingSizes(){           
            const query_url = query(collection(db, "packingSizes"));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let packingSizes=hp_packingSizes.packingSizes(snapShot)  
                        packingSizes=packingSizes.map((p)=>{
                            return {...p, created_at: new Date(p.created_at.toDate())}
                        })
                       this.packingSizes=packingSizes;
                });              
        },
        fetchReturnIn(){           
            const query_url = query(collection(db, "returnIn"));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let returnIn=hp_rt_in.returnIn(snapShot)  
                        returnIn=returnIn.map((p)=>{
                            return {...p, rt_in_date: new Date(p.rt_in_date.toDate())}
                        })
                       this.returnIn=returnIn;
                });              
        },
        fetchReturnOut(){           
            const query_url = query(collection(db, "returnOut"));
            onSnapshot(query_url, async(snapShot) =>{                  
                        let returnOut=hp_rt_out.returnOut(snapShot)  
                        returnOut=returnOut.map((p)=>{
                            return {...p, rt_out_date: new Date(p.rt_out_date.toDate())}
                        })
                       this.returnOut=returnOut;
                });              
        },
},        

}
</script>
<style lang="">
    
</style>