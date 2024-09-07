<template lang="">       
    <div>           
           <PageHeader page-title="Suppliers" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info"/>
           <div class="container-fluid">
               <div class="row mb-2 justify-content-end">                            
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
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchSuppliers" placeholder="Code" class="form-control form-control-sm">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                                <div class="form-floating col-sm-4 col-lg-12">
                                    <input type="search" id="search_by_name" v-model="search_by_name" @keyup="searchSuppliers" placeholder="Description" class="form-control form-control-sm">
                                    <label for="search_by_name" class="small">Name</label>
                                </div> 
                                <div class="form-floating col-sm-4 col-lg-12">
                                    <input type="search" id="search_by_company" v-model="search_by_company" @keyup="searchSuppliers" placeholder="Company" class="form-control form-control-sm">
                                    <label for="search_by_company" class="small">Company</label>
                                 </div> 
                            </div>
                             <div class="row mt-2">
                                    <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="searchSuppliers"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                    <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateSuppliers"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                    </button></div>
                                    <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
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
                                           <td  @click="sortSuppliers('code')"><i class="fa-solid fa-sort"></i> Code</td>
                                           <td @click="sortSuppliers('name')"><i class="fa-solid fa-sort"></i> Name</td>
                                           <td @click="sortSuppliers('phone')"><i class="fa-solid fa-sort"></i> Phone</td>
                                           <td @click="sortSuppliers('company')"><i class="fa-solid fa-sort"></i> Company</td>
                                           <td @click="sortSuppliers('created_at')"><i class="fa-solid fa-sort"></i> Date</td>
                                       </tr>
                                       <tr>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="code" class="form-control-plaintext text-center" id="code" placeholder="Code">                                                                                       
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="name" :class="{'border-danger' : v$.name.$errors.length}" class="form-control-plaintext text-center" id="name" placeholder="Name">                                               
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="tel" v-model="phone"  :class="{'border-danger' : v$.phone.$errors.length}"  class="form-control-plaintext text-center" id="phone" placeholder="Phone">                                            
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveSupplier" type="text" v-model="company"  :class="{'border-danger' : v$.company.$errors.length}"  class="form-control-plaintext text-center" id="company" placeholder="Company">                                              
                                            </td>
                                            <td class="">
                                                <input @change="saveSupplier" type="date" v-model="created_at"    class="form-control-plaintext text-center" id="company" placeholder="Date">                                              
                                            </td>
                                        </tr>
                                       
                                       <tr class="text-center" v-if="suppliers.length > 0"  v-for="(p, i) in suppliers" :key="p.id" @mouseenter="setEdit(p)" >                                           
                                           <td :class="{'table-secondary': sort_item ==='code'}" class="pt-2 position-relative">
                                                <a class="text-danger position-absolute start-0 top-50 translate-middle ps-2" href="#!" @click="removeSupplier(i, p)"><i class="fa-solid fa-square-minus"></i></a>                                                                                        
                                                {{p.code}}
                                            </td>
                                           <td :class="{'table-secondary': sort_item ==='name'}" class="p-0">
                                                <input @keypress.enter="saveEdit" type="text" class="form-control-plaintext text-center m-0" v-model="p.name">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='phone'}" class="p-0">
                                            <input @keypress.enter="saveEdit" type="text" class="form-control-plaintext text-center  m-0" v-model="p.phone">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='company'}" class="p-0">
                                            <input @keypress.enter="saveEdit" type="text" class="form-control-plaintext text-center m-0" v-model="p.company">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='created_at'}" class="pt-2">{{showTime(p.created_at)}}</td>
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
   export default {
       components: {
               PageHeader
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
               }
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
        resetInput(){
            this.name="";
            this.phone="";
            this.code= "";
            this.created_at="";
            this.company="";
            this.isSaving=false;
            this.isLoading=false;
            this.isUpdating=false;
            this.v$.$reset()
        },
        message(type){
                this.msg.info=hp.message(type, "suppliers")
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
                            if(change.type==="removed" && res.edit_user.type=="removed" && res.edit_user.uid==user.uid){
                                    this.message("removed")
                            }             
                    });                 
                    const isLocal=snapShot.metadata.formCache;                              
            });                              
        } ,
        async fetchSuppliers(){ 
            const query_url=query(collection(db, "suppliers"), orderBy("created_at", "desc"));           
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
            const date=this.created_at ? new Date(this.created_at) : new Date();
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
                edit_user: {uid: this.user.uid, type: 'added'}         
            }      
            const docRef=await addDoc(dataCollection, data)                          
        },
           updateSuppliers(){
               this.isUpdating=true;
               this.fetchSuppliers();
           },
          async saveEdit(){
                const item=this.suppliers.filter((f)=>f.id==this.editItem.id)
                this.editItem={...item[0], edit_user:  {uid:this.user.uid, type: "modified"},}
               const updateDocument=doc(db, "suppliers", this.editItem.id)
                await updateDoc(updateDocument,this.editItem)           
           },
           setEdit(p){
               this.editItem=p;
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
   
   