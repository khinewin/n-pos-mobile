<template lang="">
    <!-- packingSizes Modal -->
    <div class="modal fade" id="packingSizeModal" data-bs-backdrop="false"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog">
         <div class="modal-content">               
           <div>
               <div class="card">
                   <div class="card-body p-2"> 
                    <div class="row justify-content-end mb-2">
                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark" @click="updateData"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                        </button></div>
                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button></div>
                 </div>
                   <div class="row g-2">
                    <div class="col-12 vh-75 overflow-y-auto">
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody class="small">                                     
                                <tr class="">
                                    <td class="p-0"><input placeholder="Packing name" type="text" @keypress.enter="savePackingSize" v-model="packing_name" class="form-control-plaintext form-control-sm text-center"></td>
                                    <td class="p-0"><input placeholder="Packing unit" type="text" @keypress.enter="savePackingSize" v-model="packing_unit" class="form-control-plaintext form-control-sm text-center"></td>
                                </tr>                                                                                       
                                <tr  class="" v-if="packingSizes.length > 0"  v-for="p in packingSizes" :key="p.id" @mouseenter="setEdit(p)"  style="cursor:context-menu">
                                    <td class="p-0 position-relative">
                                        <a href="#!" class='text-danger position-absolute start-0 top-50 translate-middle ps-2' @click="removePackingSize(p)"><i class="fa-solid fa-square-minus"></i></a>
                                        <input @keypress.enter="saveEdit" type="text" class="form-control-plaintext form-control-sm text-center" v-model="p.packing_name">
                                        </td>
                                    <td class="text-center p-0">
                                        <input @keypress.enter="saveEdit" type="text" class="form-control-plaintext form-control-sm text-center" v-model="p.packing_unit">
                                    </td>
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
            packingSizes:[],
            packing_unit:"",
            packing_name:"",
           isUpdating:false,
           isLoading:false,       
           editItem:{}
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
        setEdit(p){
            this.editItem=p;
        },
        async saveEdit(){
            const data=doc(db, "packingSizes", this.editItem.id)
            await updateDoc(data, this.editItem)
        },
        async savePackingSize(){          
            if(!this.packing_unit || !this.packing_name) return false;
            this.isSaving=true;
            const created_at=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "packingSizes", )
            const data={
                user: {uid:this.user.uid, email:this.user.email},
                packing_unit: this.packing_unit,         
                packing_name: this.packing_name,      
                created_at: created_at
            }
            const result=await addDoc(dataCollection, data);
            this.fetchpackingSizes();
            this.packing_unit="";          
            this.packing_name=""; 
        },
       updateData(){
               this.isUpdating=true;
               this.fetchpackingSizes();
       },
      
       async fetchpackingSizes(){
       const query_url = query(collection(db, "packingSizes"), orderBy("created_at", "desc"));
       const snapShot=await getDocs(query_url)
       const pz=ss.packingSizes(snapShot)          
       this.packingSizes=pz;
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
