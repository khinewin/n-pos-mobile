<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="searchSupplier" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-lg">
         <div class="modal-content">               
           <div>
               <div class="card min-vh-100">
                   <div class="card-body p-0">
                       
                       <div class="row g-2 m-2">
                           <div class="col-lg-5">
                                   <input type="search" v-model="search_by_code" @keypress.enter="searchSupplier" placeholder="Code" class="form-control">
                           </div>
                           <div class="col-lg-5">
                                   <input type="search" v-model="search_by_name" @keyup="searchSupplier" placeholder="Name" class="form-control">
                           </div>
                           <div class="col-lg-1">
                               <button class="btn text-dark" @click="clearSearch">Clear</button>
                           </div>
                           <div class="col-lg-1">
                               <button class="btn text-danger" data-bs-dismiss="modal" aria-label="Close">Close</button>
                           </div>
                       </div>
                 
                           <ul class="list-group list-group-flush">
                                   <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                       <div class="row">
                                           <div class="col-lg-3">
                                                   <div class="small fw-lighter ">Code</div>
                                           </div>
                                           <div class="col-lg-3">
                                                   <div class="small fw-lighter ">Name</div>
                                           </div>
                                           <div class="col-lg-2">
                                                   <div class="small fw-lighter">Phone</div>
                                           </div>
                                           <div class="col-lg-4">
                                               <div class="small fw-lighter">Company</div>
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
                            
                                   <li v-if="suppliers.length > 0" class="list-group-item list-group-item-action" v-for="p in suppliers" :key="p.id" >
                                           <div class="row" @click="setCode(p)" data-bs-dismiss="modal" style="cursor: context-menu;">
                                                   <div class="col-lg-3 small">
                                                           <div>{{p.code}}</div>
                                                   </div>
                                                   <div class="col-lg-3 small">
                                                           <div>{{p.name}}</div>
                                                   </div>
                                                   <div class="col-lg-2 small">
                                                           <div>{{p.phone}}</div>
                                                   </div>
                                                   <div class="col-lg-4 small">
                                                       <div>{{p.company}}</div>
                                                   </div>
                                           </div>
                                   </li>
                           </ul>
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
   data(){
       return{
           suppliers:[],
           suppliersData: [],

           search_by_code:"",
           search_by_name:"",

           isLoading:false,
       }
   },
  
   mounted() {
        const myModalEl = document.getElementById('searchSupplier')
        myModalEl.addEventListener('show.bs.modal', event => {
            if(this.suppliersData.length <=0){
                this.fetchSupplier();
            }
            this.searchSupplier();
        })
    },
   created(){
       if(this.user !==null){
           if(this.storeSuppliers.length > 0){
               this.suppliersData=this.storeSuppliers;
           }else{
               this.fetchSupplier();
           }            
       }
   },
   computed:{
       user(){
           return this.$store.getters.user;
       },
       storeSuppliers(){
           return this.$store.getters.suppliers;
       }
   },
   watch:{
       user(data){
               if(data !==null){
                   if(this.storeSuppliers.length > 0){
                   this.suppliersData=this.storeSuppliers;
               }else{
                   this.fetchSupplier();
               }
           }
       }
   },
   methods:{
       setCode(p){
           this.$emit("setCode", p)
           this.clearSearch();
       },
       clearSearch(){
           this.search_by_code="";
           this.search_by_name="";
           this.searchSupplier();
       },
       searchSupplier(){
           if(this.search_by_code){
               this.suppliers=this.suppliersData.filter((f)=>{
                       return f.code===this.search_by_code;
               })
           }else if(this.search_by_name){
               this.suppliers=this.suppliersData.filter((f)=>{
                       return f.name.toLowerCase().includes(this.search_by_name.toLowerCase())
               })
           }else{
                   this.suppliers= this.suppliersData.filter((p, i)=>{
                           if(i < 20){
                               return p;
                           }
                     })                                     
           }
       },
     
       async fetchSupplier(){
       this.isLoading=true;    
       const query_url = query(collection(db, "suppliers"), where("uid", "==", this.user.uid));
       const snapShot=await getDocs(query_url)
       let suppliers=[];
       snapShot.forEach((doc) => {         
           let supplier = {
               id: doc.id,
               uid: doc.data().uid,
               name: doc.data().name,          
               phone : doc.data().phone,
               company : doc.data().company,           
               code: doc.data().code, 
                         
           };                                
           suppliers.push(supplier);      
                       
       });
       this.suppliersData=suppliers;
       this.$store.dispatch("setSupplier", suppliers)
       this.isLoading=false;
       this.searchSupplier();
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