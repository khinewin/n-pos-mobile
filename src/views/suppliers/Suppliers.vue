<template lang="">       
    <div>           
           <PageHeader page-title="Suppliers" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info"/>
           <div class="container-fluid">
               <div class="row  justify-content-end">   
                <div class="col-sm-2 d-grid">
                    <button type="button"  class="btn btn-sm text-danger" :disabled="isLoading" @click="deleteMultiple">
                        <span v-if="!isDeleting"><i class="fa-solid fa-trash"></i> Delete</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isDeleting"></span>
                    </button>
                </div>                           
                <div class="col-sm-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveSupplier">
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
                                <div class="form-floating col-sm-4 col-lg-12">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchSuppliers" placeholder="Code" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                                <div class="form-floating col-sm-4 col-lg-12">
                                    <input type="search" id="search_by_name" v-model="search_by_name" @keyup="searchSuppliers" placeholder="Description" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_name" class="small">Name</label>
                                </div> 
                                <div class="form-floating col-sm-4 col-lg-12">
                                    <input type="search" id="search_by_company" v-model="search_by_company" @keyup="searchSuppliers" placeholder="Company" class="form-control-plaintext border-bottom ">
                                    <label for="search_by_company" class="small">Company</label>
                                 </div> 
                            </div>
                             <div class="row mt-2">
                                    <div class="col-6 d-grid"><button class="btn btn-sm text-dark" @click="searchSuppliers"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                    <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateSuppliers"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>-->
                                    <div class="col-6 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
                             </div> 
                        </div>
                      </div>                                                                                                          
                   </div>                        
                   <div class="col-lg-9">
                       <div class="card shadow vh-75">
                           <div class="card-body p-2 table-responsive">
                               <table class="table table-bordered table-hover mb-0">
                                   <tbody class="">
                                       <tr class="text-center table-secondary" style="cursor: context-menu;">
                                        <td class="col-0">
                                            <input type="checkbox" @click="checkAll" v-model="is_check_all">
                                        </td>
                                           <td  @click="sortSuppliers('code')" class="col-2"><i class="fa-solid fa-sort"></i> Code</td>
                                           <td @click="sortSuppliers('name')" class="col-2"><i class="fa-solid fa-sort"></i> Name</td>
                                           <td @click="sortSuppliers('phone')" class="col-2"><i class="fa-solid fa-sort"></i> Phone</td>
                                           <td @click="sortSuppliers('company')" class="col-4"><i class="fa-solid fa-sort"></i> Company</td>
                                           <td @click="sortSuppliers('created_at')" class="col-2"><i class="fa-solid fa-sort"></i> Date</td>
                                       </tr>
                                       <tr>
                                        <td class="position-relative">
                                            <input class="position-absolute start-50 top-50 translate-middle" type="checkbox" @click="checkAll" v-model="is_deleted">
                                        </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="code" class="form-control-plaintext py-0 text-center" id="code" placeholder="Code">                                                                                       
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="name" class="form-control-plaintext py-0 " id="name" placeholder="Name">                                               
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="tel" v-model="phone"  class="form-control-plaintext py-0  text-center" id="phone" placeholder="Phone">                                            
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="company"   class="form-control-plaintext py-0" id="company" placeholder="Company">                                              
                                            </td>
                                            <td class="">
                                                <flat-pickr @change="saveSupplier" class="form-control-plaintext py-0  text-center" v-model="created_at"  :config="config" />
                                            </td>
                                        </tr>
                                       
                                       <tr class="text-center" v-if="suppliers.length > 0"  v-for="(p, i) in suppliers" :key="p.id" @mouseenter="setEdit(p)" >                                           
                                        <td class="position-relative">
                                            <input class="position-absolute start-50 top-50 translate-middle" type="checkbox" v-model="p.is_deleted">
                                        </td>
                                           <td :class="{'table-secondary': sort_item ==='code'}" class="position-relative">
                                                <!--<a class="text-danger position-absolute start-0 top-50 translate-middle ps-2" href="#!" @click="removeSupplier(i, p)"><i class="fa-solid fa-square-minus"></i></a>-->                                                                                        
                                                <span class="position-absolute start-50 top-50 translate-middle">{{p.code}}</span>
                                            </td>
                                           <td :class="{'table-secondary': sort_item ==='name'}" class="">
                                                <input @keyup="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control-plaintext py-0 m-0" v-model="p.name">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='phone'}" class="">
                                            <input @keyup="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control-plaintext text-center py-0  m-0" v-model="p.phone">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='company'}" class="">
                                            <input @keyup="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control-plaintext py-0  m-0" v-model="p.company">
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
   import * as hp from '@/lib//helper'
   import FlatPickr from 'vue-flatpickr-component';

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
            config: {
                dateFormat: 'd/m/Y', // Flatpickr options
                enableTime: false,
                wrap: true, // set wrap to true only when using 'input-group'
               // altFormat: 'M j, Y',
            },   
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
           }
       },
       created() {     
           if(this.user !==null){
               this.isLoading=true;
               this.fetchSuppliers();
               this.checkDatabase();
           }
       },
       async mounted() {
        this.created_at=hp.setToday();

       },
      
       computed:{
           user(){
               return this.$store.getters.user;
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
       methods:{
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
            const query_url=query(collection(db, "suppliers"), orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{
                    const dc=snapShot._snapshot.docChanges;
                    const user=this.user;
                    snapShot.docChanges().forEach((change) => {                       
                        const res=change.doc.data();   
                            if(change.type==="added" && res.edit_user.uid==user.uid && dc.length===1){
                                    this.message("added")
                                    this.resetInput();    
                                    hp.setAutoFocus("name")     
                            }           
                            if(change.type==="modified" && res.edit_user.type=="modified" && res.edit_user.uid==user.uid){
                                    this.message("modified")
                            }  
                            if(change.type==="modified" && res.edit_user.type=="removed" && res.edit_user.uid==user.uid){
                                    this.message("removed")
                                    this.isDeleting=false;
                                    this.is_check_all=false;
                            }             
                    });                 
                    const isLocal=snapShot.metadata.formCache;                              
            });                              
        } ,
        async fetchSuppliers(){ 
            const query_url=query(collection(db, "suppliers"), where("is_deleted", "==", false), orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{                  
                    const suppliers=lib.suppliers(snapShot);   
                    this.suppliersData=suppliers;
                    this.searchSuppliers();
                    this.isLoading=false;
                    this.isUpdating=false;
            });                              
        } ,
        async removeSupplier(i, p){
            const cf=confirm(`Are you sure ? delete the supplier : ${p.name} `)
            if(!cf) return false;

            const updateDocument=doc(db, "suppliers", p.id)
            await updateDoc(updateDocument, {edit_user: {uid: this.user.uid, type: 'removed'}})  
            
            const document=doc(db, "suppliers", p.id)     
            await deleteDoc(document)                           
        },
     
        async saveSupplier(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
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
                is_deleted: this.is_deleted,
                edit_user: {uid: this.user.uid, type: 'added'}         
            }      
            const docRef=await addDoc(dataCollection, data)                          
        },
           updateSuppliers(){
               this.isUpdating=true;
               this.fetchSuppliers();
           },
          async saveEdit(){             
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
                   this.suppliers=this.suppliersData.filter((f)=>{
                       return f.name.toLowerCase().includes(this.search_by_name.toLowerCase())
                   })
               }else 
               if(this.search_by_company){
                   this.suppliers=this.suppliersData.filter((f)=>{
                       return f.company.toLowerCase().includes(this.search_by_company.toLowerCase())
                   })
               }else{
                   this.suppliers= this.suppliersData.filter((p, i)=>{
                              if(i < 20){
                                  return p;
                              }
                    }) 
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
   
   