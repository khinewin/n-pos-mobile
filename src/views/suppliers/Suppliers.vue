<template lang="">       
    <div>           
           <PageHeader :page-title="pageTitle" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info"/>
           <div class="container-fluid">
               <div class="row  justify-content-end">  
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isUpdating || Object.keys(editItem).length===0" @click="saveEdit">
                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> {{update_link}}</span>
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
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> {{save_link}}</span>
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
                                    <label for="search_by_name" class="small">{{name_link}}</label>
                                </div> 
                                <div class="form-floating col-sm-3 col-lg-12">
                                    <input type="search" id="search_by_company" v-model="search_by_company" @keypress.enter="searchSuppliers" placeholder="Company" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_company" class="small">{{company_link}}</label>
                                 </div> 
                           
                                    <div class="col-6 col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="searchSuppliers"><i class="fa-solid fa-magnifying-glass-arrow-right"></i>  {{search_link}}</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateSuppliers"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-6 col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> {{clear_link}}</button></div>
                             </div> 
                        </div>
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
                                                <input @keypress.enter="saveSupplier" type="text" v-model="name" class="form-control py-1 textc " id="name" :placeholder="name_link">                                               
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="tel" v-model="phone"  class="form-control py-1  text-center" id="phone" :placeholder="phone_link">                                            
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="company"   class="form-control py-1" id="company" :placeholder="company_link">                                              
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
   import {disableNetwork ,Timestamp, collection, addDoc, setDoc, query, where, getDocs, doc, updateDoc, deleteDoc, orderBy, onSnapshot } from 'firebase/firestore'
   import db from '@/firebase/firestore'
   import * as lib from '@/lib/snapshot/suppliers'
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
      //  this.fetchSuppliers();
        this.checkDatabase();
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
            const sl=this.$store.getters.setLanguage;
            let  pageTitle=sl=="mm" ?  `${lang.suppliers.mm}` : `${lang.suppliers.eng}`            
            return pageTitle;
        },
        name_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.name.mm : lang.name.eng ;
            return data;
        },
        phone_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.phone.mm : lang.phone.eng ;
            return data;
        },
        company_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.company.mm : lang.company.eng ;
            return data;
        },
        save_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.save_link.mm : lang.save_link.eng ;
            return data;
        },
        update_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.update_link.mm : lang.update_link.eng ;
            return data;
        },
        search_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.search_link.mm : lang.search_link.eng ;
            return data;
        },
        clear_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.clear_link.mm : lang.clear_link.eng ;
            return data;
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
        async deleteMultiple(){
            const is_has=this.suppliers.filter((f)=>f.is_deleted)
            if(is_has.length <=0) return false;
            const cf=confirm(`Are you sure you want to delete the suppliers ?`)
            if(!cf) return false;
            this.isDeleting=true;
            this.suppliers.filter(async (s)=>{
                if(s.is_deleted){
                    const updateDocument=doc(db, "suppliers", s.id)
                    await updateDoc(updateDocument, {is_deleted: true, edit_user: {uid: this.user.uid, type: 'removed'}})  
                }
            })
        },
        async removeSupplier(s){     
            const cf=confirm(`Are you sure, you want to delete the suppliers "${s.name}" ?`)
            if(!cf) return false;
            this.isDeleting=true;             
            const document=doc(db, "suppliers", s.id)     
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
        message(type){
                this.msg.info=hp.message(type, "supplier")
                this.msg.type="success"                                                         
        }, 
        async checkDatabase(){ 
            const query_url=query(collection(db, "suppliers"),  orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{

                    const suppliers=lib.suppliers(snapShot);  
                    this.suppliersData=suppliers;
                    this.suppliers=this.suppliersData.slice(0,this.limitData)                    
                    this.isLoading=false;

                    const dc=snapShot._snapshot.docChanges;
                    const user=this.user;
                    if(dc.length > 1) return false;
                    
                    snapShot.docChanges().forEach((change) => {                       
                        const res=change.doc.data();   
                            if(change.type==="added" && res.edit_user.uid==user.uid && dc.length===1){
                                    this.msg.info="The new supplier has been created"
                                    this.msg.type="success"
                                    this.resetInput();    
                                    this.editItem={}    

                                   // hp.setAutoFocus("name")     
                            }           
                            if(change.type==="modified" && res.edit_user.type=="modified" && res.edit_user.uid==user.uid){
                                    this.msg.info="The selected supplier has been updated"
                                    this.msg.type="success"
                                    this.editItem={}    

                            }  
                            if(change.type==="removed"  && res.edit_user.uid==user.uid){
                                     this.msg.info="The selected supplier has been deleted"
                                    this.msg.type="success"
                                    this.isDeleting=false;
                                    this.editItem={}    

                                  //  this.is_check_all=false;
                                   // console.log(change.doc.id)
                                   // this.removeSupplier(change.doc.id)
                            }             
                    });                 
                  //  const isLocal=snapShot.metadata.formCache;                              
            });                              
        } ,
        async fetchSuppliers(){ 
            const query_url=query(collection(db, "suppliers"), orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{                  
                    const suppliers=lib.suppliers(snapShot);   
                    this.suppliersData=suppliers;
                    this.suppliers=this.suppliersData.slice(0, this.limitData)
                    this.isLoading=false;
                    this.isUpdating=false;
            });                              
        } ,
       
        async saveSupplier(){
         //   if(this.editItem.id) this.saveEdit();
            const validattion = await this.v$.$validate()
            if(!validattion) return false;

            let isTaken=this.suppliersData.some((f)=>f.name.toLowerCase() == this.name.toLowerCase())
            if(isTaken){
                this.msg.info="This supplier name already exists"
                this.msg.type="error"
                this.name="";               
                return false;
            }

            this.isSaving=true;
            const date=hp.insertDateNoTime(this.created_at)
            const created_at=Timestamp.fromDate(date);
            const dataCollection=collection(db, "suppliers")
            this.code=this.code ? this.code : hp.g_code();
            const data={
                user: {uid:this.user.uid, displayName:this.user.displayName},
                name: this.name, 
                code: this.code,
                phone: this.phone,
                company: this.company,
                created_at : created_at,      
                is_deleted: false,
                edit_user: {uid: this.user.uid, type: 'added'}         
            }      
            await addDoc(dataCollection, data)                          
        },
           updateSuppliers(){
               this.isUpdating=true;
               this.fetchSuppliers();
           },
          async saveEdit(){    
            let result=this.suppliersData.some((f)=>f.id != this.editItem.id && f.name.toLowerCase().includes(this.editItem.name) )
            if(result){
                this.msg.info="This supplier name already exists"
                this.msg.type="error";
                this.suppliers=this.suppliersData.map((m)=>{
                    if(m.id==this.editItem.id){
                        return {...m, name: ""}
                    }
                    return m;
                })
                return false;
            }                      
               const updateDocument=doc(db, "suppliers", this.editItem.id)
                await updateDoc(updateDocument,this.editItem)           
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
           searchSuppliers(){
               this.suppliers=[];
               if(this.search_by_code){
                   this.suppliers=this.suppliersData.filter((f)=>f.code===Number(this.search_by_code))
               }else 
               if(this.search_by_name){
                   this.suppliers=search.searchSupplierName(this.suppliersData, this.search_by_name);
               }else 
               if(this.search_by_company){
                    this.suppliers=search.searchSupplierCompany(this.suppliersData, this.search_by_company);
               }else{
                   this.suppliers= this.suppliersData.slice(0,this.limitData)
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
   
   