<template lang="">
    <!-- packingSizes Modal -->
    <div class="modal fade" id="selectPackingSizeModal"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
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
                    <div class="col-12">
                        <table class="table table-bordered mb-0">
                            <tbody class="small">
                                <tr class="table-secondary text-center">
                                    <td>Parent unit</td>
                                    <td>Packing</td>
                                    <td>Child unit</td>
                                </tr>     
                                                       
                                <tr class="" style="cursor:context-menu">
                                    <td class="text-center p-0">
                                        <select @change="setCode" class="form-control-plaintext form-select-sm text-center" v-model="parent_unit">
                                            <option  v-if="parentPackingSizes.length > 0"  v-for="p in parentPackingSizes" :key="p.id" >{{p.parent_unit}}</option>
                                        </select>
                                    </td>
                                    <td class="p-0">
                                        <input @keypress.enter="setCode" type="number" class="form-control-plaintext text-center" v-model="packing">
                                    </td>
                                    <td class="text-center p-0">
                                        <select @change="setCode" class="form-control-plaintext form-select-sm text-center" v-model="child_unit">
                                            <option  v-if="childPackingSizes.length > 0"  v-for="p in childPackingSizes" :key="p.id" >{{p.child_unit}}</option>
                                        </select>
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
        setCode(){
            if(this.parent_unit && this.packing && this.child_unit){
                const packingSize={parent_unit : this.parent_unit, packing: this.packing, child_unit: this.child_unit}
                this.$emit("setPackingSize", packingSize)
            }
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
