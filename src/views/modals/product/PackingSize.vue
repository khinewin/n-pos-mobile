<template lang="">
    <!-- packingSizes Modal -->
    <div class="modal fade" id="packingSizeModal"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog">
         <div class="modal-content">               
           <div>
               <div class="card">
                   <div class="card-body p-2"> 
                    <div class="row justify-content-end">
                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateData"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                        </button></div>
                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button></div>
                 </div>
                   <div class="row g-2">
                    <div class="col-6">
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody class="small">
                                <tr class="table-secondary text-center">
                                    <td>Parent unit</td>
                                </tr>      
                                <tr class="">
                                    <td class="p-0"><input type="text" @keypress.enter="saveParentUnit" v-model="parent_unit" class="form-control-plaintext text-center"></td>
                                </tr>                                
                                <tr class="" v-if="parentPackingSizes.length > 0"  v-for="p in parentPackingSizes" :key="p.id"  style="cursor:context-menu">
                                    <td class="text-center"><a href="#!" class='text-danger float-start' @click="removePackingSize(p)"><i class="fa-solid fa-square-minus"></i></a> {{p.parent_unit}}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <div class="col-6">
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody class="small">
                                <tr class="table-secondary text-center">
                                    <td>Child unit</td>
                                </tr>      
                                <tr class="">
                                    <td class="p-0"><input type="text" @keypress.enter="saveChildUnit" v-model="child_unit" class="form-control-plaintext text-center"></td>
                                </tr>                                
                                <tr class="" v-if="childPackingSizes.length > 0"  v-for="p in childPackingSizes" :key="p.id"   style="cursor:context-menu">
                                    <td class="text-center"><a href="#!" class='text-danger float-start' @click="removePackingSize(p)"><i class="fa-solid fa-square-minus"></i></a> {{p.child_unit}}</td>                                             
                                </tr>
                            </tbody>
                        </table>
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
       </div>
     </div>
     <!--end modal-->
</template>
<script>
import {Timestamp, collection, addDoc, getDocs, doc, deleteDoc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/packingSizes"
import * as hp  from "@/lib/helper"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
export default {   
  
   data(){
       return{
           parentPackingSizes:[],
           childPackingSizes:[],
           isUpdating:false,
           isLoading:false,
           parent_unit:"",
           packing:"",
           child_unit:"",
       }
   },   
   mounted() {
      
   },
   created(){
       if(this.user !==null){         
              this.isLoading=true; 
              this.fetchpackingSizes();                
       }
   },   
   computed:{
       user(){
           return this.$store.getters.user;
       },     
   },
   watch:{       
       user(data){
               if(data !==null){         
                this.isLoading=true;          
                this.fetchpackingSizes();           
           }
       },         
   },
   methods:{
        async removePackingSize(p){
            const document=doc(db, "packingSizes", p.id)     
            await deleteDoc(document) //if no items delete document;
            this.fetchpackingSizes();  
        },
      async saveParentUnit(){          
            if(!this.parent_unit) return false;
            this.isSaving=true;
            const created_at=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "packingSizes", )
            const data={
                user: {uid:this.user.uid, email:this.user.email},
                parent_unit: this.parent_unit,               
                created_at: created_at
            }
            const result=await addDoc(dataCollection, data);
            this.fetchpackingSizes();
            this.parent_unit="";           
        },
        async saveChildUnit(){          
            if(!this.child_unit) return false;
            this.isSaving=true;
            const created_at=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "packingSizes", )
            const data={
                user: {uid:this.user.uid, email:this.user.email},
                child_unit: this.child_unit,               
                created_at: created_at
            }
            const result=await addDoc(dataCollection, data);
            this.fetchpackingSizes();
            this.child_unit="";           
        },
       updateData(){
               this.isUpdating=true;
               this.fetchpackingSizes();
       },
      
       async fetchpackingSizes(){
       const query_url = query(collection(db, "packingSizes"), orderBy("created_at", "desc"));
       const snapShot=await getDocs(query_url)
       const parentPackingSizes=ss.packingParentSizes(snapShot)     
       const childPackingSizes=ss.packingChildSizes(snapShot)  
       this.parentPackingSizes=parentPackingSizes;
       this.childPackingSizes=childPackingSizes;
       this.isLoading=false;
       this.isUpdating=false;
      } ,
      showTime(t){
           return hp.showTime(t)
       },
   }
   
}
</script>
<style lang="">
   
</style>
