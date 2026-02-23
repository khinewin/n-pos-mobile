<template lang="">       
    <div>           
           <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info"/>
           <div class="container-fluid">
               <div class="row  justify-content-end">  
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isUpdating || Object.keys(editItem).length===0" @click="saveEdit">
                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> {{t('update_link')}}</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                    </button>
                </div>  
                <!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button"  class="btn btn-sm text-danger" :disabled="isLoading" @click="deleteMultiple">
                        <span v-if="!isDeleting"><i class="fa-solid fa-trash"></i> Delete (ctrl+d)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isDeleting"></span>
                    </button>
                </div>-->                           
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveSupplier">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> {{t('save_link')}}</span>
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
                                <!--<div class="form-floating col-sm-4 col-lg-12">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchSuppliers" placeholder="Code" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>-->
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_name" v-model="search_by_name" @keypress.enter="searchSuppliers" placeholder="Description" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_name" class="small">{{t('name')}}</label>
                                </div> 
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_company" v-model="search_by_company" @keypress.enter="searchSuppliers" placeholder="Company" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_company" class="small">{{t('company')}}</label>
                                 </div> 
                           
                                    <div class="col-6 col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="searchSuppliers"><i class="fa-solid fa-magnifying-glass-arrow-right"></i>  {{t('search_link')}}</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateSuppliers"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-6 col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{t('clear_link')}}</button></div>
                             </div> 
                        </div>
                      </div>      
                      <div class="mt-3 d-grid">
                            <router-link class="btn btn-sm  " :to="{name : 'AddPurchase'}" ><i class="fa-solid fa-cart-shopping text-danger"></i>   {{t('purchase')}}</router-link>
                        </div>                                                                                                  
                   </div>                        
                   <div class="col-lg-9">
                       <div class="card shadow vh-75 overflow-y-auto">
                           <div class="card-body p-2">
                               <table class="table table-bordered table-hover mb-0">
                                   <tbody class="table-secondary sticky-top z-3">
                                       <!--<tr class="text-center table-secondary change-cursor">
                                        <td class="col-0">
                                        </td>
                                           <td @click="sortSuppliers('name')" class="col4-"><i class="fa-solid fa-sort"></i> Name</td>
                                           <td @click="sortSuppliers('phone')" class="col-2"><i class="fa-solid fa-sort"></i> Phone</td>
                                           <td @click="sortSuppliers('company')" class="col-4"><i class="fa-solid fa-sort"></i> Company</td>
                                           <td @click="sortSuppliers('created_at')" class="col-2"><i class="fa-solid fa-sort"></i> Date</td>
                                       </tr>-->
                                       <tr>
                                        <td class="text-center">
                                            <!--<input class="form-check-input" type="checkbox" @click="checkAll" v-model="is_deleted">-->
                                        </td>
                                            <!--<td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="code" class="form-control-plaintext py-0 text-center" id="code" placeholder="Code">                                                                                       
                                            </td>-->
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="name" class="form-control py-1 textc " id="name" :placeholder="t('name')">                                               
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="tel" v-model="phone"  class="form-control py-1  text-center" id="phone" :placeholder="t('phone')">                                            
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="company"   class="form-control py-1" id="company" :placeholder="t('company')">                                              
                                            </td>
                                            <td class="">
                                                <flat-pickr @change="saveSupplier" class="form-control py-1  text-center" v-model="created_at"  :config="config" />
                                            </td>
                                        </tr>
                                       </tbody>
                                       <tbody v-if="suppliers.length > 0">
                                       <tr class="text-center"   v-for="(p, i) in suppliers" :key="p.id"  >                                           
                                        <td class="d-grid">
                                            <!--<input class="form-check-input" type="checkbox" v-model="p.is_deleted">-->
                                            <button :disabled="user.uid != p.user.uid" class="btn btn-sm text-danger" @click="removeSupplier(p)"><i class="fa-solid fa-trash"></i></button>
                                        </td>
                                           <!--<td :class="{'table-secondary': sort_item ==='code'}" class="position-relative">
                                                <span class="position-absolute start-50 top-50 translate-middle">{{p.code}}</span>
                                            </td>-->
                                           <td :class="{'table-secondary': sort_item ==='name'}" class="">
                                                <input v-if="editItem.id ==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1 m-0" v-model="p.name">
                                                <div v-else class="pt-1 text-start" @click="setEdit(p)">{{p.name}}</div>
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='phone'}" class="">
                                            <input  v-if="editItem.id==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control text-center py-1  m-0" v-model="p.phone">
                                            <div v-else class="pt-1" @click="setEdit(p)">{{p.phone}}</div>
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='company'}" class="">
                                            <input v-if="editItem.id==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1  m-0" v-model="p.company">
                                            <div v-else class="pt-1 text-start" @click="setEdit(p)">{{p.company}}</div>
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='created_at'}" class="position-relative">
                                                <span class="position-absolute start-50 top-50 translate-middle">{{showTime(p.created_at)}}</span>
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
    
   </template>
   <script>
   import PageHeader from "@/views/partials/PageHeader.vue"
    import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
   import {disableNetwork ,Timestamp, collection, addDoc, setDoc, query, where, limit, getDoc, getDocs, doc, updateDoc, deleteDoc, orderBy, onSnapshot } from 'firebase/firestore'
   import db from '@/firebase/firestore'
   import * as ss from '@/lib/snapshot/suppliers'
   import * as hp from '@/lib/helper'
   import FlatPickr from 'vue-flatpickr-component';
   import * as search from '@/lib/search'
   import {lang} from "@/lib/language"
   


   export default {
       components: {
               PageHeader, FlatPickr
       },
       setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                name: {required},
                phone: {required},
                company:{required}
            }
    },
       data(){
           return{
           
            code :"",
            name:"",
            phone:"",
            company:'',
            created_at:"",
               suppliers : [],
               isLoading: false,
               sortBy:"asc",
               sort_item:"",
               search_by_code:"",
               search_by_name:"",
               search_by_company:"",
               suppliersData:[],
               editItem:{},
               isUpdating:false,
               isSaving:false,
               isOffline:false,
               msg : {
                   type :"",
                   info :"",
               },
               is_deleted:false,
               is_check_all: false,
               isDeleting:false,
               limitData: 100,
           }
       },
     
       async mounted() {
        this.isLoading=true;
        this.fetchSuppliers();
        this.created_at=hp.setToday();
        window.addEventListener('keydown', this.handleCtrl);

       },
       beforeUnmount() {
         window.removeEventListener('keydown', this.handleCtrl);
     },
       computed:{
           user(){
               return this.$store.getters.user;
           },
           config(){
            return this.$store.getters.flat_pickr_config;
           },
           pageTitle(){
            const sl=this.$store.getters.currentLang;
            let  pageTitle=sl=="mm" ?  `${lang.suppliers.mm}` : `${lang.suppliers.eng}`            
            return pageTitle;
        },
       t() {
            return (key) => this.$store.getters.t(key);
        },
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
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 'd') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.deleteMultiple();
            }
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveSupplier(); // Call your custom function
            }                 
        },
        inputSearchName(){
            this.search_by_name=event.target.value;
            this.searchSuppliers();
        },
        inputSearchCompany(){
            this.search_by_company=event.target.value;
            this.searchSuppliers();
        },
        
        async removeSupplier(s){     
            const cf=confirm(`Are you sure, you want to delete the suppliers "${s.name}" ?`)
            if(!cf) return false;
            this.isDeleting=true;             
            const document=doc(db, "suppliers", s.id)     
            this.isRemoved(s.code);
            await deleteDoc(document) 
        },
     
        checkAll(){
            setTimeout(()=>{
                this.suppliers=this.suppliers.map((s)=>{
               return {...s, is_deleted : this.is_check_all ? true : false}
             })
            }, 100)
        },
        resetInput(){
            this.name="";
            this.phone="";
            this.code= "";
            this.created_at=hp.setToday();
            this.company="";
            this.isSaving=false;
            this.isLoading=false;
            this.isUpdating=false;
            this.v$.$reset()
        },
        async isCreated(code){
            const query_url=query(collection(db, "suppliers"), where("code","==", Number(code)));           
           const unsub=onSnapshot(query_url, async(snapShot) =>{
                snapShot.docChanges().forEach((change) => {   
                    if(change.type != "added") return true;
                    this.msg.info="The new supplier has been created"
                    this.msg.type="success"
                    this.resetInput(); 
                    unsub();
                })
            })
        },
        async isUpdated(code){
            const query_url=query(collection(db, "suppliers"), where("code","==", Number(code)));           
           const unsub=onSnapshot(query_url, async(snapShot) =>{
                snapShot.docChanges().forEach((change) => {   
                    if(change.type != "modified") return true;
                    this.msg.info="The selected supplier has been updated"
                    this.msg.type="success"
                    this.editItem={}
                    unsub();
                })
            })
        },
        async isRemoved(code){
            const query_url=query(collection(db, "suppliers"), where("code","==", Number(code)));           
           const unsub=onSnapshot(query_url, async(snapShot) =>{
                snapShot.docChanges().forEach((change) => {   
                    if(change.type != "removed") return true;
                    this.msg.info="The selected supplier has been deleted"
                    this.msg.type="success"
                    this.editItem={}
                    this.isDeleting=false;
                    unsub();
                })
            })
        },     
        async fetchSuppliers(){                    
               const query_url = query(collection(db, "suppliers"), orderBy("created_at", "desc"), limit(this.limitData));
               onSnapshot(query_url, async(snapShot) =>{                    
                    const suppliers=ss.suppliers(snapShot);           
                    this.suppliersData=suppliers;
                    this.suppliers=suppliers;
                    this.isLoading=false;
            });     
              
          } ,
       generateKeywords(data){
            return hp.generateKeywords(data);
       },
        async saveSupplier(){
         //   if(this.editItem.id) this.saveEdit();
            const validattion = await this.v$.$validate()
            if(!validattion) return false;

            const coll = collection(db, "suppliers");
            const q = query(coll, where("name_lower_case", "==", this.name.toLowerCase()));
            const isTaken = await getDocs(q);           
            if(!isTaken.empty){                
                this.msg.info="This supplier name already exists"
                this.msg.type="error"
                this.name="";
                return true;          
            }

            this.isSaving=true;
            const date=hp.insertDateNoTime(this.created_at)
            const created_at=Timestamp.fromDate(date);
            const dataCollection=collection(db, "suppliers")
            this.code=this.code ? this.code : hp.g_code();
            const data={
                user: {uid:this.user.uid, displayName:this.user.displayName},
                name: this.name, 
                name_lower_case:this.name.toLowerCase(),
                name_keywords: this.generateKeywords(this.name),
                company_keywords: this.generateKeywords(this.company),
                code: this.code,
                phone: this.phone,
                company: this.company,
                created_at : created_at,      
                is_deleted: false,
                edit_user: {uid: this.user.uid, type: 'added'}         
            }      
            this.isCreated(this.code);
            await addDoc(dataCollection, data)                          
        },
          
          async saveEdit(){    
            const coll = collection(db, "suppliers");
            const q = query(coll, where("name_lower_case", "==", this.editItem.name.toLowerCase()));
            const isTaken = await getDocs(q);
            if(!isTaken.empty){
                const docRef = doc(db, "suppliers", this.editItem.id);
                const docSnapshot = await getDoc(docRef);
                const docData = docSnapshot.data();
                const index = this.suppliers.findIndex(sp => sp.code == docData.code);
                this.suppliers[index].name=docData.name;
                this.msg.info="This supplier name already exists"
                this.msg.type="error"
                return false;
            }                  
               const updateDocument=doc(db, "suppliers", this.editItem.id)
               const data={
                ...this.editItem,
                name_lower_case: this.editItem.name.toLowerCase(),
                name_keywords: this.generateKeywords(this.editItem.name),
                company_keywords: this.generateKeywords(this.editItem.company),
            }
            this.isUpdated(this.editItem.code);
            await updateDoc(updateDocument, data)           
           },
           setEdit(p){
               this.editItem={...p,  edit_user:  {uid:this.user.uid, type: "modified"}};
           },
           clearSearch(){
               this.search_by_code="";
               this.search_by_name="";
               this.search_by_company="";
               this.searchSuppliers();
           },
           async searchSuppliers() {
            this.suppliers = [];
            this.isLoading = true;
            try {
                if (this.search_by_name) {
                    const query_url = query(
                        collection(db, "suppliers"), where("name_keywords", "array-contains", this.search_by_name.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const cus = ss.suppliers(snapShot);
                    this.suppliers = cus;
                   
                } else if (this.search_by_company) {
                    const query_url = query(
                        collection(db, "suppliers"), where("company_keywords", "array-contains", this.search_by_company.toLowerCase()), orderBy("created_at", "desc"),
                        limit(this.limitData)
                    );
                    const snapShot = await getDocs(query_url);
                    const cus = ss.suppliers(snapShot);
                    this.suppliers = cus;                   
                } else {
                    this.suppliers = this.suppliersData;                   
                }
            } catch (error) {
                console.error("Error searching suppliers:", error);
            } finally {
                this.isLoading = false;
            }
        },                        
           
          showTime(t){
            return hp.showTime(t)
         },
         sortSuppliers(sort){   
               this.suppliers=[];
               this.sort_item=sort;
               if(this.sortBy=="asc"){
                   this.sortBy="desc"                   
                   this.suppliers=this.suppliersData.sort((a,b)=>{
                       const item_a=a.name.toUpperCase();
                       const item_b=b.name.toUpperCase();
                       const com_a=a.company.toUpperCase();
                       const com_b=b.company.toUpperCase();
                       const date_a=new Date(a.created_at.toDate()).getTime();
                       const date_b=new Date(b.created_at.toDate()).getTime();
                       switch(sort){
                           case "phone":
                               return a.phone - b.phone;
                           break;
                           case "name":
                             return item_a < item_b ? 1 : -1;
                           break;
                           case "code":
                               return a.code - b.code;
                           break;
                           case "company":
                           return com_a < com_b ? 1 : -1;
                           break;
                           case "created_at":
                              return  date_a - date_b;
                           break;
                       }
                   })
               }else{
                   this.sortBy="asc"                  
                   this.suppliers=this.suppliersData.sort((a,b)=>{
                       const item_a=a.name.toUpperCase();
                       const item_b=b.name.toUpperCase();
                       const com_a=a.company.toUpperCase();
                       const com_b=b.company.toUpperCase();
                       const date_a=new Date(a.created_at.toDate()).getTime();
                       const date_b=new Date(b.created_at.toDate()).getTime();
                       switch(sort){
                           case "phone":
                               return a.phone + b.phone;
                           break;
                           case "name":
                               return item_a < item_b ? -1 : 1;
                           break;
                           case "code":
                               return a.code + b.code;
                           break;
                           case "company":
                               return com_a < com_b ? -1 : 1;
                           break;
                           case "created_at":
                              return  date_a + date_b;
                           break;
                       }
                     })
                       
               }            
           },
       },
       
   }
   </script>
   <style lang="">
       
   </style>
   
   