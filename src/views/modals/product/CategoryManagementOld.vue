<template>
    <div class="modal fade" id="categoryManagementModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
        <div class="modal-dialog">
          <div class="modal-content ">                                    
          
                            <div class="card shadow-sm  ">
                                <div class="card-body p-2">
                                    <div class="row  justify-content-end mb-2">   
                                        <!--<div class="col-4 d-grid">
                                            <button type="button" class="btn btn-sm" :disabled="isLoading" @click="saveCategory">
                                                <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save </span>
                                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                                            </button>
                                        </div>   
                                        <div class="col-4 d-grid">
                                            <button type="button"  :disabled="!editItem.id" class="btn btn-sm"  @click="saveEdit">
                                                <span><i class="fa-solid fa-hard-drive"></i> Update</span>
                                            </button>
                                        </div>    -->                                                                                                                          
                                        <div class="col-4 d-grid">
                                            <button class="btn btn-sm text-dark close-p-z" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                    
                                        </div> 
                                    </div> 
                                    <div class="row g-2">
                                        <div class="col-12 vh-75 overflow-y-auto">
                                            <table class="table table-bordered table-hover mb-0">
                                                <tbody>
                                                   
                                                    <tr class="table-secondary sticky-top z-3">
                                                        
                                                        <td class="col-12" colspan="2">
                                                            <input @keypress.enter="saveCategory" v-model="cat_name" type="text" class="form-control text-center py-1 px-2" placeholder="Category Name">
                                                        </td>
                                                        <td class="col-0">
                                                            <button type="button" class="btn btn-sm text-primary" :disabled="isLoading" @click="saveCategory">
                                                                <span v-if="!isSaving"><i class="fa-solid fa-check"></i> </span>
                                                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                    <tr v-if="cats.length" v-for="c in cats" :key="c.code">
                                                        <td class="col-0">
                                                              <button :disabled="c.user.uid != user.uid" class="btn btn-sm text-danger" @click="removeCategory(c)"><i class="fa-solid fa-trash"></i></button>

                                                        </td>
                                                        <td @click="setEdit(c)" class="text-center col-12" >
                                                            <span v-if="editItem.id !== c.id">{{c.cat_name}}</span>
                                                            <input v-else @input="setEdit(c)" @keypress.enter="saveEditCategory" type="text" v-model="c.cat_name" class="form-control text-center px-2 py-1">
            
                                                        </td>
                                                        <td>
                                                            <button type="button"  :disabled="!editItem.id" class="btn btn-sm text-primary"  @click="saveEdit" v-if="editItem.id==c.id">
                                                                <span><i class="fa-solid fa-check"></i> </span>
                                                            </button>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>                                                                                  
                        
                       </div>
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
</template>

<script>
    import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, limit, deleteDoc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    import * as cat from "@/lib/snapshot/category"
    
    import * as hp  from "@/lib/helper"
    import { useVuelidate  } from '@vuelidate/core'
    import { required, email ,helpers} from '@vuelidate/validators'
    import PageHeader from "@/views/partials/PageHeader.vue"
    
    export default {   
      components:{
        PageHeader
      },
       data(){
           return{         
               isLoading:false,       
               editItem:{},
               cat_name:"",
               brand_name:"",
               cats:[],
               brands:[],
               isSaving:false,
               isDeleting:false,
               editItem:{},
               msg :{
                    type: "",
                    info:"",
               },
               is_check_all_cat:false,
               is_check_all_brand:false,
               limitItem: 100,
           }
       },   
       mounted() {
            this.isLoading=true;      
         //   this.checkDatabaseBrand();
           // this.fetchCategory();   
         //   this.fetchBrand();    
         this.checkDatabaseCategory();  
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
       computed:{
           user(){
               return this.$store.getters.user;
           },     
       },
      
       methods:{
         setMsg(){
            this.$emit("setMsg", this.msg.info, this.msg.type)
        },
       
        setEdit(item){
            this.editItem=item;
        },
        saveEdit(){
          
            this.saveEditCategory();
           
        },
        async saveEditCategory(){
            const dataCollection=doc(db, "category", this.editItem.id)         
                const data={
                    cat_name:this.editItem.cat_name,          
                    edit_user: {uid: this.user.uid, type: 'modified'},
                }
            await updateDoc(dataCollection, data);    
        },
      
       async saveCategory(){
            if(!this.cat_name) return false;
                this.isSaving=true;
                const dataCollection=collection(db, "category", )
                const save_time=Timestamp.fromDate(new Date());
                const code=new Date().getTime()
                const data={
                    user: {uid:this.user.uid, displayName:this.user.displayName},
                    cat_name:this.cat_name,
                    code: code.toString(),
                    edit_user: {uid: this.user.uid, type: 'added'},
                    created_at: save_time,
                }
            await addDoc(dataCollection, data);    
        },
        
        message(type, name){
                    this.msg.info=hp.message(type, name)
                    this.msg.type="success"                                                         
         }, 
         async removeCategory(p){   
                const cf=confirm(`Are you sure,  you want to delete the category ${p.cat_name}`)
                if(!cf) return false;
                this.isDeleting=true;       
                const document=doc(db, "category", p.id)            
                 await deleteDoc(document)  
            },
        async checkDatabaseCategory(){ 
            const query_url=query(collection(db, "category"), orderBy("created_at", "desc"));           
                 this.unsubProducts= onSnapshot(query_url, async(snapShot) =>{                    
                        //update  docs
                                    const category=cat.category(snapShot);   
                                    this.cats=category.slice(0, this.limitItem);                               
                                    this.isLoading=false;
    
                            const dc=snapShot._snapshot.docChanges;
                             const user=this.user;
                             if(dc.length > 1) return false;
                            //get only update doc for message
                            snapShot.docChanges().forEach((change) => {     
                                const res=change.doc.data();   
                                const id=change.doc.id;
                                const edit_user=res.edit_user !=null ? res.edit_user : "";
                                if(change.type==="added" &&   edit_user.uid==user.uid &&  dc.length ===1){                               
                                        this.msg.info="The new category has been created"
                                        this.msg.type="success"
                                        this.isSaving=false;
                                        this.cat_name="";
                                        this.setMsg();
                                        //  hp.setAutoFocus("item_name")     
                                }
                                if(change.type==="modified" && edit_user.type=="modified" && edit_user.uid==user.uid){
                                        this.msg.info="The selected category  has been updated"
                                        this.msg.type="success"
                                        this.isUpdating=false;
                                        this.editItem={};
                                        this.setMsg();
                                      //  const items=this.products.filter((f)=>f.id==this.editItem.id)
                                }  
                                if(change.type==="removed" && this.isDeleting){
                                        this.isDeleting=false;
                                         this.msg.info="The selected category  has been deleted"
                                        this.msg.type="success"
                                        this.setMsg();
                                }
                          
                            })
                       
                     });                      
            } ,
          
            async fetchCategory(){                    
                const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
                onSnapshot(query_url, async(snapShot) =>{
                    const cats=cat.category(snapShot);   
                    this.cats=cats;             
                    this.isLoading=false;
                })
               
           } ,
         
          showTime(t){
               return hp.showTime(t)
           },
       }
       
    }
    </script>