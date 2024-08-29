<template lang="">
    <!-- Products Modal -->
    <div class="modal fade" id="searchSupplier" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-xl">
         <div class="modal-content p-2">               
            <div class="row g-1">
                <div class="col-lg-3">
                   <div class="card shadow">
                     <div class="card-body p-2">
                        <div class="row g-1">
                            <div class="col-md-4 col-lg-12">
                                <div class="form-floating">
                                    <input type="search" id="search_by_code" v-model="search_by_code" @keypress.enter="searchSuppliers" placeholder="Code" class="form-control form-control-sm">
                                    <label for="search_by_code" class="small">Code</label>
                                </div>
                            </div>
                            <div class="col-md-4 col-lg-12">
                                <div class="form-floating ">
                                    <input type="search" id="search_by_name" v-model="search_by_name" @keyup="searchSuppliers" placeholder="Description" class="form-control form-control-sm">
                                    <label for="search_by_name" class="small ">Name</label>
                                </div> 
                            </div>
                            <div class="col-md-4 col-lg-12">
                                <div class="form-floating ">
                                    <input type="search" id="search_by_company" v-model="search_by_company" @keyup="searchSuppliers" placeholder="Company" class="form-control form-control-sm">
                                    <label for="search_by_company" class="small">Company</label>
                                </div> 
                            </div>
                        </div>
                          <div class="row mt-2">
                            <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="clearSearch"><i class="fa-solid fa-filter-circle-xmark"></i> Clear</button></div>
                            <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateSuppliers"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                                </button>
                            </div>
                            <div class="col-4 d-grid">
                                <button class="btn btn-sm text-dark" data-bs-dismiss="modal" aria-label="Close"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                            </div>
                          </div> 
                     </div>
                   </div>                                                                                                          
                </div>                        
                <div class="col-lg-9">
                    <div class="card shadow vh-80 overflow-y-auto">
                        <div class="card-body p-2 table-responsive">
                            <table class="table table-bordered table-hover mb-0">
                                <tbody>
                                    <tr class="text-center table-secondary" style="cursor: context-menu;">
                                        <td @click="sortSuppliers('code')"><i class="fa-solid fa-sort"></i> Code</td>
                                        <td @click="sortSuppliers('name')"><i class="fa-solid fa-sort"></i> Name</td>
                                        <td @click="sortSuppliers('phone')"><i class="fa-solid fa-sort"></i> Phone</td>
                                        <td @click="sortSuppliers('company')"><i class="fa-solid fa-sort"></i> Company</td>
                                        <td @click="sortSuppliers('created_at')"><i class="fa-solid fa-sort"></i> Date</td>
                                    </tr>
                                   
                                    <tr style="cursor: context-menu;" class="" v-if="suppliers.length > 0"  v-for="(p, i) in suppliers" :key="p.id" @click="setCode(p)" data-bs-dismiss="modal">                                         
                                        <td :class="{'table-secondary': sort_item ==='code'}" class="text-center">
                                             {{p.code}}
                                         </td>
                                        <td :class="{'table-secondary': sort_item ==='name'}">
                                            {{p.name}}
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='phone'}" class="text-center">
                                            {{p.phone}}
                                        </td>
                                        <td :class="{'table-secondary': sort_item ==='company'}">
                                            {{p.company}}
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
     </div>
     <!--end modal-->
</template>
<script>
    import PageHeader from "@/views/partials/PageHeader.vue"
     import { useVuelidate  } from '@vuelidate/core'
    import { required, email ,helpers} from '@vuelidate/validators'
    import {Timestamp, collection, addDoc, query, where, getDocs, doc, updateDoc, deleteDoc, orderBy} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    import * as ss from '@/lib/snapshot/suppliers'
    export default {
        components: {
                PageHeader
        },
     
        data(){
            return{         
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
            }
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
                    this.fetchSuppliers();
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
            updateSuppliers(){
                this.isUpdating=true;
                this.fetchSuppliers();
            },
            setCode(p){
                this.$emit("setCode", p)
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
            
            async fetchSuppliers(){                    
                const query_url = query(collection(db, "suppliers"), orderBy("created_at", "desc"));
                const snapShot=await getDocs(query_url)           
                if(snapShot._snapshot.docChanges.length <=0){
                    this.msg.type='error' 
                    this.msg.info='Oops..., something went wrong!'
                    this.isLoading=false;
                    this.isUpdating=false;
                    return false;
                }    
                const suppliers=ss.suppliers(snapShot)
                this.suppliers=[];
                this.suppliersData=suppliers;
                this.suppliers=suppliers;
                this.isLoading=false;
                this.isUpdating=false;
               // this.$store.dispatch("setsuppliers", this.suppliers)
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