<template lang="">       
    <div>           
           <PageHeader page-title="Customers" :isOffline="isOffline" :msg-type="msg.type" :msg-info="msg.info"/>
           <div class="container-fluid">
               <div class="row  justify-content-end">  
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isUpdating" @click="saveEdit">
                        <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                    </button>
                </div>  
                ,<!--<div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button"  class="btn btn-sm text-danger" :disabled="isLoading" @click="deleteMultiple">
                        <span v-if="!isDeleting"><i class="fa-solid fa-trash"></i> Delete (ctrl+d)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isDeleting"></span>
                    </button>
                </div>-->                           
                <div class="col-6 col-sm-4 col-md-3 col-lg-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCustomer">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save (ctrl+s)</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                    </button>
                </div>   
               </div>
           </div>
           <div class="container-fluid my-2">
               <div class="row g-2">
                   <div class="col-lg-3">
                       <div class="card shadow ">
                            <div class="card-body p-2">
                                <div class="row g-2">
                                    <!--<div class="form-floating col-sm-4 col-lg-12 ">
                                        <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchCustomer" placeholder="Code" class="form-control-plaintext border-bottom ">
                                        <label for="search_by_code" class="small ">Code</label>
                                    </div>-->
                                    <div class="form-floating col-sm-3 col-lg-12 ">
                                        <input type="search" id="search_by_name" v-model="search_by_name" @keypress.enter="searchCustomer" placeholder="Description" class="form-control-plaintext border-bottom ">
                                        <label for="search_by_name" class="small ">Name</label>
                                    </div> 
                                    <div class="form-floating col-sm-3 col-lg-12 ">
                                        <input type="search" id="search_by_address" v-model="search_by_address" @keypress.enter="searchCustomer" placeholder="Address" class="form-control-plaintext border-bottom ">
                                        <label for="search_by_address" class="small ">Address</label>
                                    </div> 
                               
                                        <div class="col-6 col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="searchCustomer"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                        <!--<div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateCustomer">
                                             <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
                                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                        </button></div>-->
                                        <div class="col-6 col-sm-3 col-lg-6 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
                                 </div>
                            </div>
                       </div>                                                                                                           
                   </div>                        
                   <div class="col-lg-9">
                       <div class="card shadow vh-75">
                           <div class="card-body p-2 table-responsive">
                               <table class="table table-bordered table-hover mb-0">
                                   <tbody class="small">
                                       <tr class="text-center table-secondary change-cursor">
                                        <td class="col-0">
                                            <!--<input type="checkbox" class="form-check-input" @click="checkAll" v-model="is_check_all">-->
                                        </td>
                                           <!--<td @click="sortCustomers('code')" class="col-2"><i class="fa-solid fa-sort"></i> Code</td>-->
                                           <td @click="sortCustomers('name')" class="col-4"><i class="fa-solid fa-sort"></i> Name</td>
                                           <td @click="sortCustomers('phone')" class="col-2"><i class="fa-solid fa-sort"></i> Phone</td>
                                           <td @click="sortCustomers('address')" class="col-4"><i class="fa-solid fa-sort"></i> Address</td>
                                           <td @click="sortCustomers('created_at')" class="col-2"><i class="fa-solid fa-sort"></i> Member date</td>
                                       </tr>
                                       <tr>
                                        <td class="">
                                            <!--<input class="form-check-input" type="checkbox" @click="checkAll" v-model="is_deleted">-->
                                        </td>
                                           <!--<td class="">
                                                <input @keyperss.enter="saveCustomer"  type="text" v-model="code" class="form-control-plaintext py-0 text-center" id="code" placeholder="Code">                                                                                       
                                            </td>-->
                                            <td class="">
                                                <input  @keyperss.enter="saveCustomer" type="text" v-model="name"  class="form-control py-1 " id="name" placeholder="Name">                                               
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveCustomer" type="tel" v-model="phone"    class="form-control py-1 text-center" id="phone" placeholder="Phone">                                            
                                            </td>
                                            <td class="">
                                                <input @keypress.enter="saveCustomer" type="text" v-model="address"   class="form-control py-1" id="address" placeholder="Address">                                              
                                            </td>
                                            <td class="">
                                                <flat-pickr @change="saveCustomer" class="form-control py-1  text-center" v-model="created_at"  :config="config" />
                                            </td>
                                        </tr>
                                       
                                       <tr class="" v-if="customers.length > 0"  v-for="(p, i) in customers" :key="p.id">                                            
                                        <td class="d-grid">
                                            <button :disabled="user.uid != p.user.uid" class="btn btn-sm text-danger" @click="removeCustomer(p)"><i class="fa-solid fa-trash"></i></button>

                                            <!--<input class="form-check-input" type="checkbox" v-model="p.is_deleted">-->
                                        </td>
                                           <!--<td :class="{'table-secondary': sort_item ==='code'}" class=" position-relative">
                                                {{p.code}}
                                            </td>-->
                                           <td :class="{'table-secondary': sort_item ==='name'}" class="">
                                                <input v-if="editItem.id==p.id" @input="setEdit(p)"  @keypress.enter="saveEdit" type="text" class="form-control  py-1 " v-model="p.name">
                                                <div v-else @click="setEdit(p)">{{p.name}}</div>
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='phone'}" class="">
                                                <input v-if="editItem.id==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control text-center py-1" v-model="p.phone">
                                                <div v-else @click="setEdit(p)" class="text-center">{{p.phone}}</div>
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='address'}" class="">
                                                <input v-if="editItem.id==p.id" @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1" v-model="p.address">
                                                <div v-else @click="setEdit(p)">{{p.address}}</div>
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='created_at'}" class="text-center">{{showTime(p.created_at)}}</td>
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
   import {Timestamp, collection, addDoc, query, where, getDocs,onSnapshot, doc, updateDoc, deleteDoc, orderBy} from 'firebase/firestore'
   import db from '@/firebase/firestore'
   import * as hp from "@/lib/helper"
   import * as ss from "@/lib/snapshot/customers"
   import FlatPickr from 'vue-flatpickr-component';

   export default {
       components: {
               PageHeader,FlatPickr
       },
       setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                name: {required},
                phone: {required},
                address:{required}
            }
    },
       data(){
           return{
           
            code :"",
            name:"",
            phone:"",
            address:'',
            created_at:"",
               customers : [],
               isLoading: false,
               sortBy:"asc",
               sort_item:"",
               search_by_code:"",
               search_by_name:"",
               search_by_address:"",
               customersData:[],
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
        
       },
       mounted() {
        const name=document.getElementById("name");
     //   name.focus();
        this.created_at=hp.setToday();

        this.checkDatabase();
        this.isLoading=true;
      //  this.fetchCustomer();
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
        handleCtrl(event) {
            if (event.ctrlKey && event.key === 'd') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.deleteMultiple();
            }
            if (event.ctrlKey && event.key === 's') {
                event.preventDefault(); // Prevent the default browser save dialog
                this.saveCustomer(); // Call your custom function
            }                 
        },
        inputSearchName(){
            this.search_by_name=event.target.value;
            this.searchCustomer();
        },
        inputSearchAddress(){
            this.search_by_address=event.target.value;
            this.searchCustomer();
        },
        async deleteMultiple(){
            const is_has=this.customers.filter((f)=>f.is_deleted)
            if(is_has.length <=0) return false;
            const cf=confirm(`Are you sure you want to delete the customers ?`)
            if(!cf) return false;
            this.isDeleting=true;
            this.customers.filter(async (s)=>{
                if(s.is_deleted){
                    const updateDocument=doc(db, "customers", s.id)
                    await updateDoc(updateDocument, {is_deleted: true, edit_user: {uid: this.user.uid, type: 'removed'}})  
                }
            })
        },
        checkAll(){
           setTimeout(()=>{
            this.customers=this.customers.map((s)=>{
               return {...s, is_deleted : this.is_check_all ? true : false}
            })
           },100)
        },
      
        async removeCustomer(c){   
            const cf=confirm(`Are you sure, you want to delete the customers "${c.name}" ?`)
            if(!cf) return false;
            this.isDeleting=true;        
            const document=doc(db, "customers", c.id)     
            await deleteDoc(document) 
          
        },
        async saveCustomer(){
         //   if(this.editItem.id) this.saveEdit();
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            let isTaken=this.customersData.some((f)=>f.name.toLowerCase() == this.name.toLowerCase())
            if(isTaken){
                this.msg.info="This customer name already exists"
                this.msg.type="error"
                this.name="";
               
                return false;
            }
            this.isSaving=true;

            const date=hp.insertDateNoTime(this.created_at)
            this.code=this.code ? this.code : hp.g_code();
            const created_at=Timestamp.fromDate(date);
            const dataCollection=collection(db, "customers")

            const data={
                user: {uid:this.user.uid, displayName:this.user.displayName},
                name: this.name, 
                code :  this.code,
                phone: this.phone,
                address: this.address,
                created_at : created_at,       
                is_deleted: this.is_deleted,     
                edit_user: {uid: this.user.uid, type: 'added'}
            }
            const result=await addDoc(dataCollection, data);         
           
        },
        updateCustomer(){
               this.isUpdating=true;
               this.fetchCustomer();
        },
          async saveEdit(){         
            let result=this.customersData.some((f)=>f.id != this.editItem.id && f.name.toLowerCase().includes(this.editItem.name) )
            if(result){
                this.msg.info="This customer name already exists"
                this.msg.type="error";
                this.customers=this.customersData.map((m)=>{
                    if(m.id==this.editItem.id){
                        return {...m, name: ""}
                    }
                    return m;
                })
                return false;
            }           
               const updateDocument=doc(db, "customers", this.editItem.id)
                await updateDoc(updateDocument,this.editItem)  
           },
           setEdit(p){
               this.editItem={...p, edit_user: {uid: this.user.uid, type: "modified"}}
           },
           clearSearch(){
               this.search_by_code="";
               this.search_by_name="";
               this.search_by_address="";
               this.searchCustomer();
           },

        message(type){
                this.msg.info=hp.message(type, "customer")
                this.msg.type="success"                                                         
        }, 
         
           async checkDatabase(){ 
            const query_url=query(collection(db, "customers"),  orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{

                    const customers=ss.customers(snapShot);           
                    this.customersData=customers;
                    this.customers=this.customersData.slice(0,20)                    
                    this.isLoading=false;

                    const dc=snapShot._snapshot.docChanges;
                    const user=this.user;
                    snapShot.docChanges().forEach((change) => {                       
                        const res=change.doc.data();   
                            if(change.type==="added" && res.edit_user.uid==user.uid && dc.length===1){
                                    this.msg.info="The new customer has been created"
                                    this.msg.type="success"
                                    this.resetInput();    
                                    //hp.setAutoFocus("name")     
                            }           
                            if(change.type==="modified" && res.edit_user.type=="modified" && res.edit_user.uid==user.uid){
                                   this.msg.info="The selected customer has been updated"
                                    this.msg.type="success"
                            }  
                            if(change.type==="removed" && res.edit_user.uid==user.uid){
                                   this.msg.info="The selected customer has been deleted"
                                    this.msg.type="success"
                                    this.isDeleting=false;
                                 //   this.is_check_all=false;
                                  //  this.removeCustomer(change.doc.id)
                            }             
                    });                 
            });                              
        } ,
           async fetchCustomer(){                    
               const query_url = query(collection(db, "customers"), orderBy("created_at", "desc"));
               onSnapshot(query_url, async(snapShot) =>{                    
                    const customers=ss.customers(snapShot);           
                    this.customersData=customers;
                    this.searchCustomer();          
                    this.isLoading=false;
                    this.isUpdating=false;           
            });     
              
          } ,
          searchCustomer(){
               this.customers=[];
               if(this.search_by_code){
                   this.customers=this.customersData.filter((f)=>f.code===Number(this.search_by_code))
               }else 
               if(this.search_by_name){
                   this.customers=this.customersData.filter((f)=>{
                       return f.name.toLowerCase().includes(this.search_by_name.toLowerCase())
                   })
               }else 
               if(this.search_by_address){
                   this.customers=this.customersData.filter((f)=>{
                       return f.address.toLowerCase().includes(this.search_by_address.toLowerCase())
                   })
               }else{
                   this.customers= this.customersData.slice(0,20)
               }
           },          
           resetInput(){
            this.name="";
            this.phone="";
            this.code= "";
            this.created_at=hp.setToday();
            this.address="";
            this.isSaving=false;
            this.isLoading=false;
            this.isUpdating=false;
            this.v$.$reset()
           },
          showTime(t){
            return hp.showTime(t)
        },
        sortCustomers(sort){   
               this.customers=[];
               this.sort_item=sort;
               if(this.sortBy=="asc"){
                   this.sortBy="desc"
                   this.customers=this.customersData.sort((a,b)=>{
                       const item_a=a.name.toUpperCase();
                       const item_b=b.name.toUpperCase();
                       const com_a=a.address.toUpperCase();
                       const com_b=b.address.toUpperCase();
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
                           case "address":
                           return com_a < com_b ? 1 : -1;
                           break;
                           case "created_at":
                              return  date_a - date_b;
                           break;
                       }
                   })
               }else{
                   this.sortBy="asc"
                   this.customers=this.customersData.sort((a,b)=>{
                       const item_a=a.name.toUpperCase();
                       const item_b=b.name.toUpperCase();
                       const com_a=a.address.toUpperCase();
                       const com_b=b.address.toUpperCase();
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
                           case "address":
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
                                                   <div class="small fw-lighter mt-3">phone</div>
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
                                   <li v-if="customers.length > 0" class="list-group-item list-group-item-action" v-for="p in customers" :key="p.id" >
                                           <div class="row">
                                                   <div class="col-lg-3 small">
                                                           <div>{{p.code}}</div>
                                                   </div>
                                                   <div class="col-lg-5 small">
                                                           <div>{{p.name}}</div>
                                                   </div>
                                                   <div class="col-lg-1 small">
                                                           <div>{{p.phone}}</div>
                                                   </div>
                                                   <div class="col-lg-3 small fw-lighter text-center">
                                                       <div class="row">
                                                           <div class="col-4">{{p.purchase_price}}</div>
                                                           <div class="col-4">{{p.regular_sale_price}}</div>
                                                           <div class="col-4">{{p.created_at}}</div>
                                                       </div>
                                               </div>
                                           </div>
                                   </li>
                           </ul>
                       -->