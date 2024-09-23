<template lang="">
    <!-- packingSizes Modal -->
    <div class="modal fade" id="selectPackingSizeModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-sm">
         <div class="modal-content">               
           <div>
               <div class="card">
                   <div class="card-body p-2"> 
                    <div class="row justify-content-end mb-2">
                        <div class="col-6 d-grid">
                            <button id="setBtn" class="btn btn-sm text-dark" data-bs-dismiss="modal" @click="setCode"><span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                            </button>
                        </div>
                        <!--<div class="col-4 d-grid">
                            <button class="btn btn-sm text-dark" @click="updateData"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                            </button>
                        </div>-->
                        <div class="col-6 d-grid"><button class="btn btn-sm text-dark close-s-p-z" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button></div>
                    </div> 
                   <div class="row g-2">
                    <div class="col-12">
                        <table class="table table-bordered mb-0">
                            <tbody class="small">
                                <!--<tr class="text-center">                                    
                                    <td class="col-1" colspan="2">
                                        <select class="form-control-plaintext py-0 text-center" v-model="largest_unit">
                                            <option value=""></option>
                                            <option v-for="s in packingSizes" :key="s.id">{{s.packing_unit}}</option>
                                        </select>
                                    </td>
                                </tr>--> 
                                <tr class=" text-center">
                                    <!--<td class="col-">
                                        <input :disabled="!largest_unit" @keypress.enter="pressSetBtn" type="number" class="form-control-plaintext py-0 text-center" v-model="medium_qty">
                                    </td>-->
                                    <td class="col-2" colspan="2">
                                        <select class="form-control-plaintext py-0 text-center" v-model="medium_unit">
                                            <option v-for="s in packingSizes" :key="s.id">{{s.packing_unit}}</option>
                                        </select>
                                    </td>
                                </tr>      
                                <tr class="text-center">
                                    <td class="col-1">
                                        <input :disabled="!medium_unit" @keypress.enter="pressSetBtn" type="number" class="form-control-plaintext py-0 text-center" v-model="smallest_qty">
                                    </td>
                                    <td class="col-1">
                                        <select class="form-control-plaintext py-0 text-center " v-model="smallest_unit">
                                            <option v-for="s in packingSizes" :key="s.id">{{s.packing_unit}}</option>
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
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, deleteDoc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as ss from "@/lib/snapshot/packingSizes"
import * as hp  from "@/lib/helper"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
export default {   
    props:['ei'],
   data(){
       return{
           packingSizes:[],
           isUpdating:false,
           isLoading:false,
           isSaving:false,
           smallest_unit:"",
           smallest_qty:"",
        
           medium_unit:"",
           medium_qty:"",

           largest_unit:"",
       }
   },   
   mounted() {
    const myModalEl = document.getElementById('selectPackingSizeModal')
       myModalEl.addEventListener('show.bs.modal', event => {     
            this.smallest_unit=this.ei.packing_size.smallest_unit
            this.smallest_qty=this.ei.packing_size.smallest_qty;
            this.medium_qty=this.ei.packing_size.medium_qty;
            this.medium_unit=this.ei.packing_size.medium_unit;
            this.largest_unit=this.ei.packing_size.largest_unit;
       })   
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
      product(){
        return this.$store.getters.product;
      }
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
        pressSetBtn(){
            const btn=document.getElementById("setBtn")
            btn.click();
        },
        setCode(){
            if(this.medium_unit && this.smallest_qty && this.smallest_unit){
                const packingSize={
                    largest_unit: this.largest_unit ? this.largest_unit : null,
                    medium_qty: this.medium_qty ? this.medium_qty : null,
                    medium_unit: this.medium_unit ? this.medium_unit : null,
                    smallest_qty: this.smallest_qty ? this.smallest_qty : null,
                    smallest_unit: this.smallest_unit ? this.smallest_unit : null,
                }
                this.$emit("setPackingSize", packingSize)

                this.largest_unit="";
                this.medium_qty="";
                this.medium_unit="";
                this.smallest_qty="";
                this.smallest_unit="";
            }
        },
       updateData(){
               this.isUpdating=true;
               this.fetchpackingSizes();
       },
      
       async fetchpackingSizes(){
       const query_url = query(collection(db, "packingSizes"), orderBy("created_at", "desc"));
        onSnapshot(query_url, (snapShot)=>{
            const pz=ss.packingSizes(snapShot)  
            this.packingSizes=pz;
            this.isLoading=false;
            this.isUpdating=false;
        })
      } ,
      showTime(t){
           return hp.showTime(t)
       },
   }
   
}
</script>
<style lang="">
   
</style>
