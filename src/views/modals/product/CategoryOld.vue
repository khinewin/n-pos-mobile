<template lang="">
    <!-- packingSizes Modal -->
    <div class="modal fade" id="categoryModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog modal-sm">
         <div class="modal-content">               
           <div>
               <div class="card">
                   <div class="card-body p-2"> 
                    <div class="row justify-content-end mb-2">                   
                        <div class="col-6 d-grid"><button class="btn btn-sm text-dark close-s-p-z" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close (Esc)</button></div>
                    </div> 
                   <div class="row g-2">
                    <div class="col-12 vh-75 overflow-y-auto">
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody class="small">
                                <tr>
                                    <td><input @input="handleKeyup" type="search" v-model="search_category" class="form-control text-center" placeholder="Search Category"></td>
                                </tr>
                                    <tr class="text-center fw-bold table-secondary ">
                                        <td >Category Name</td>
                                    </tr>
                                    <tr v-if="cats.length" v-for="c in cats" :key="c.id" @click="setCode(c)" data-bs-dismiss="modal">
                                        <td class="text-center change-cursor">{{c.cat_name}}</td>
                                    </tr>
                            </tbody>
                        </table>
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
import * as cat from "@/lib/snapshot/category"
import * as hp  from "@/lib/helper"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
export default {   
    props:['ei'],
   data(){
       return{
            cats:[],
            catsData:[],
            search_category:"",
       }
   },   
   mounted() {
        this.fetchCategory();
   },
  
   computed:{
       user(){
           return this.$store.getters.user;
       },    
     
   },

   methods:{
        setCode(c){
            this.$emit("setCode", c);
        },
        handleKeyup(e){
            this.search_category=e.target.value;
            this.searchCategory();
        },
        searchCategory(){
            if(this.search_category){
                this.cats=this.catsData.filter((f)=>{
                  return  f.cat_name.toLowerCase().includes(this.search_category.toLowerCase())
                })
            }else{
                this.cats=this.catsData;
            }
        },
        async fetchCategory(){                    
            const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
            onSnapshot(query_url, async(snapShot) =>{
                const cats=cat.category(snapShot);   
                this.catsData=cats;             
                this.isLoading=false;
                this.searchCategory();
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