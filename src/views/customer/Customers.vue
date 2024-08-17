<template lang="">       
    <div>           
           <PageHeader page-title="customers" page-code="customers" :msg-type="msg.type" :msg-info="msg.info"/>
           <div class="container-fluid my-2">
               <div class="row mb-2 justify-content-end">                            
                <div class="col-sm-2 d-grid">
                    <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCustomer">
                        <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                    </button>
                </div>   
               </div>
           </div>
           <div class="container-fluid my-2">
               <div class="row gx-2">
                   <div class="col-lg-3">
                       <div class="card shadow ">
                            <div class="card-body p-2">
                                <div class="form-floating col-12 mb-2">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchCustomer" placeholder="Code" class="form-control form-control-sm">
                                    <label for="search_by_code" class="small ms-2">Code</label>
                                </div>
                                <div class="form-floating col-12 mb-2">
                                    <input type="search" id="search_by_name" v-model="search_by_name" @keyup="searchCustomer" placeholder="Description" class="form-control form-control-sm">
                                    <label for="search_by_name" class="small ms-2">Name</label>
                                </div> 
                                <div class="form-floating col-12 mb-2">
                                 <input type="search" id="search_by_address" v-model="search_by_address" @keyup="searchCustomer" placeholder="Address" class="form-control form-control-sm">
                                 <label for="search_by_address" class="small ms-2">Address</label>
                             </div> 
                                 <div class="row">
                                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="searchCustomer"><i class="fa-solid fa-magnifying-glass-arrow-right"></i> Search</button></div>
                                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateCustomer">
                                             <span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update </span>
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
                                   <tbody class="small">
                                       <tr class="text-center table-secondary" style="cursor: context-menu;">
                                           <td colspan="2" @click="sortCustomers('code')"><i class="fa-solid fa-sort"></i> Code</td>
                                           <td @click="sortCustomers('name')"><i class="fa-solid fa-sort"></i> Name</td>
                                           <td @click="sortCustomers('phone')"><i class="fa-solid fa-sort"></i> Phone</td>
                                           <td @click="sortCustomers('address')"><i class="fa-solid fa-sort"></i> Address</td>
                                           <td @click="sortCustomers('created_at')"><i class="fa-solid fa-sort"></i> Date</td>
                                       </tr>
                                       <tr>
                                            <td class="" colspan="2">
                                                <input type="text" v-model="code" class="form-control-plaintext text-center" id="code" placeholder="Code">                                                                                       
                                            </td>
                                            <td class="">
                                                <input type="text" v-model="name" :class="{'border-danger' : v$.name.$errors.length}" class="form-control-plaintext text-center" id="name" placeholder="Name">                                               
                                            </td>
                                            <td class="">
                                                <input type="tel" v-model="phone"  :class="{'border-danger' : v$.phone.$errors.length}"  class="form-control-plaintext text-center" id="phone" placeholder="Phone">                                            
                                            </td>
                                            <td class="">
                                                <input type="text" v-model="address"  :class="{'border-danger' : v$.address.$errors.length}"  class="form-control-plaintext text-center" id="address" placeholder="Address">                                              
                                            </td>
                                            <td class="">
                                                <input type="date" v-model="created_at"    class="form-control-plaintext text-center" id="company" placeholder="Date">                                              
                                            </td>
                                        </tr>
                                       
                                       <tr class="text-center" v-if="customers.length > 0"  v-for="(p, i) in customers" :key="p.id" @mouseenter="setEdit(i)" @mouseleave="saveEdit" @keypress.enter="saveEdit">
                                            <td>
                                                <a class="text-danger" href="#!" @click="removeCustomer(i, p)"><i class="fa-solid fa-square-minus"></i></a>                                                                                         
                                            </td>
                                           <td :class="{'table-secondary': sort_item ==='code'}" >
                                                {{p.code}}
                                            </td>
                                           <td :class="{'table-secondary': sort_item ==='name'}">
                                                <input type="text" class="form-control-plaintext text-center   m-0" v-model="p.name">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='phone'}">
                                            <input type="text" class="form-control-plaintext text-center  m-0" v-model="p.phone">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='address'}">
                                            <input type="text" class="form-control-plaintext text-center m-0" v-model="p.address">
                                           </td>
                                           <td :class="{'table-secondary': sort_item ==='created_at'}">{{showTime(p.created_at)}}</td>
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
   import {Timestamp, collection, addDoc, query, where, getDocs, doc, updateDoc, deleteDoc, orderBy} from 'firebase/firestore'
   import db from '@/firebase/firestore'
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
               msg : {
                   type :"",
                   info :"",
               }
           }
       },
       created() {     
           if(this.user !==null){
               this.isLoading=true;
               this.fetchCustomer();
           }
       },
       mounted() {
        const name=document.getElementById("name");
        name.focus();
        this.code= new Date().getTime()
        this.created_at=new Date().toISOString().substr(0, 10)
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
                   this.fetchCustomer();
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
        async removeCustomer(i, p){
            const cf=confirm(`Are you sure ? delete the customer : ${p.name} `)
            if(!cf) return false;
            const document=doc(db, "customers", p.id)     
            await deleteDoc(document) //if no items delete document;
            this.fetchCustomer();  
            this.msg.type="success";
            this.msg.info="Customer delete successful"
        },
        async saveCustomer(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            this.isSaving=true;
            const date=this.created_at ? new Date(this.created_at) : new Date();
            const created_at=Timestamp.fromDate(date);
            const dataCollection=collection(db, "customers", )
            const data={
                uid: this.user.uid,
                name: this.name, 
                code : this.code,
                phone: this.phone,
                address: this.address,
                created_at : created_at,               
            }
            const result=await addDoc(dataCollection, data);
            if(result){
                this.msg.type="success";
                this.msg.info="Customer save successful."
            }else{
                this.msg.type="error";
                this.msg.info="Customer save failed."
            }
            this.fetchCustomer();
            this.name="";
            this.phone="";
            this.code= new Date().getTime()
            this.created_at=new Date().toISOString().substr(0, 10)
            this.address="";
            this.isSaving=false;
            this.v$.$reset()
        },
           updateCustomer(){
               this.isUpdating=true;
               this.fetchCustomer();
           },
          async saveEdit(){
               const updateDocument=doc(db, "customers", this.editItem.id)
                   await updateDoc(updateDocument, {
                       name : this.editItem.name,
                       phone: this.editItem.phone,
                       address: this.editItem.address
                    })  
           },
           setEdit(i){
               this.editItem=this.customers[i];
           },
           clearSearch(){
               this.search_by_code="";
               this.search_by_name="";
               this.search_by_address="";
               this.searchCustomer();
           },
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
                   this.customers= this.customersData.filter((p, i)=>{
                              if(i < 20){
                                  return p;
                              }
                          }) 
               }
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
           
           async fetchCustomer(){                    
               const query_url = query(collection(db, "customers"), where("uid", "==", this.user.uid), orderBy("created_at", "desc"));
               const snapShot=await getDocs(query_url)           
               if(snapShot._snapshot.docChanges.length <=0){
                   this.msg.type='error' 
                   this.msg.info='Oops..., something went wrong!'
                   this.isLoading=false;
                   this.isUpdating=false;
                   return false;
               }
   
               let customers=[];
               snapShot && snapShot.forEach((doc) => {         
                   let cus = {
                       id: doc.id,
                       uid: doc.data().uid,
                       name: doc.data().name,
                       purchase_price : doc.data().purchase_price,
                       address : doc.data().address,
                       created_at: doc.data().created_at,
                       phone : doc.data().phone,
                       code : doc.data().code,                                
                   };                                
                   customers.push(cus);                           
                               
               });
               this.customers=[];
               this.customersData=customers;
               this.customers=customers;
               this.isLoading=false;
               this.isUpdating=false;
              // this.$store.dispatch("setcustomers", this.customers)
          } ,
          showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        }
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