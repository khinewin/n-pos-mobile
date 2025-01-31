<template lang="">
    <!-- packingSizes Modal -->
    <div class="modal fade" id="packingSizeModal" data-bs-backdrop="false"  tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
       <div class="modal-dialog">
         <div class="modal-content">               
           <div>
               <div class="card">
                   <div class="card-body p-2"> 
                    <div class="row justify-content-end mb-2">
                        <div class="col-4 d-grid">
                            <button class="btn btn-sm text-dark" @click="savePackingSize">
                                <span v-if="!isSaving"><i class="fa-solid fa-hard-drive"></i> Save</span>
                                <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isSaving"></span>
                            </button>
                        </div>
                        <div class="col-4 d-grid"><button :disabled="!editItem.id" class="btn btn-sm text-dark" @click="saveEdit"><span v-if="!isUpdating"><i class="fa-solid fa-retweet"></i> Update</span>
                            <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isUpdating"></span>
                        </button>
                    </div>
                        <div class="col-4 d-grid"><button class="btn btn-sm text-dark close-p-z" data-bs-dismiss="modal"><i class="fa-solid fa-circle-xmark"></i> Close</button></div>
                 </div>
                   <div class="row g-2">
                    <div class="col-12 vh-75 overflow-y-auto">
                        <table class="table table-bordered mb-0 table-hover">
                            <tbody class="">                                     
                                <tr class="">
                                    <!--<td class=""><input id="packing_name" placeholder="Packing name" type="text" @keypress.enter="savePackingSize" v-model="packing_name" class="form-control-plaintext py-0 text-center"></td>-->
                                    <td><input placeholder="Packing unit" type="text" @keypress.enter="savePackingSize" v-model="packing_unit" class="form-control py-1 text-center"></td>
                                    <td class="d-grid">
                                        <button class="btn btn-sm btn-secondary" @click="savePackingSize">
                                            <span v-if="!isSaving">Save</span>
                                        </button>
                                    </td>
                                </tr>                                                                                       
                                <tr  class="" v-if="packingSizes.length > 0"  v-for="p in packingSizes" :key="p.id" :class="{'table-primary' : p.id==editItem.id}" >                                   
                                    <td class="text-center col-12">
                                        <input v-if="editItem.id == p.id"  @input="setEdit(p)" @keypress.enter="saveEdit" type="text" class="form-control py-1 text-center" v-model="p.packing_unit">
                                        <div v-else @click="setEdit(p)" class="py-1">{{p.packing_unit}}</div>
                                    </td>
                                    <td class="d-flex justify-content-around col-0">
                                        <a href="#!" class="btn btn-sm btn-danger" @click="removePackingSize(p)">Delete</a>
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
import {Timestamp, collection, onSnapshot, addDoc, getDocs, doc, deleteDoc, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
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
           // packing_name:"",
           isUpdating:false,
           isLoading:false,       
           isSaving:false,
           editItem:{},
           msg :{
                type: "",
                info:"",
           }
       }
   },   
   mounted() {
    this.isLoading=true; 
  //  this.fetchpackingSizes();   
    this.checkDatabase(); 
    const myModalEl = document.getElementById('packingSizeModal')
       myModalEl.addEventListener('show.bs.modal', event => {    
                  
       })   
            
   },
 
   computed:{
       user(){
           return this.$store.getters.user;
       },     
   },
  
   methods:{
        async removePackingSize(p){
            const cf=confirm(`Are you sure, you want to delete packing unit "${p.packing_unit}" ? `)
            if(!cf) return false;
            const document=doc(db, "packingSizes", p.id)     
            await deleteDoc(document) //if no items delete document;
        },
        setEdit(p){
            this.editItem={...p, edit_user : {uid: this.user.uid, type: "modified"}};
        },
        async saveEdit(){
            let result=this.packingSizes.filter((f)=>f.id != this.editItem.id && f.packing_unit.toLowerCase().includes(this.editItem.packing_unit) )
            if(result.length > 0){
                this.msg.info="This packing unit already exists"
                this.msg.type="error";
                this.packingSizes=this.packingSizes.map((m)=>{
                    if(m.id==this.editItem.id){
                        return {...m, packing_unit: ""}
                    }
                    return m;
                })
                this.setMsg();
                return false;
            }
            this.isUpdating=true;
            const data=doc(db, "packingSizes", this.editItem.id)
            await updateDoc(data, this.editItem)
        },
        setMsg(){
            this.$emit("setMsg", this.msg.info, this.msg.type)
        },
        async savePackingSize(){          
            if(!this.packing_unit) return false;

            let isTaken=this.packingSizes.filter((f)=>f.packing_unit.toLowerCase() == this.packing_unit.toLowerCase())
            if(isTaken.length > 0){
                this.msg.info="This packing unit  already exists"
                this.msg.type="error"
                this.setMsg();
                this.packing_unit="";
                return false;
            }

            this.isSaving=true;
            const created_at=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "packingSizes", )
            const data={
                user: {uid:this.user.uid, displayName:this.user.displayName},
                packing_unit: this.packing_unit,         
                created_at: created_at,
                edit_user: {uid:this.user.uid, type:"added"},

            }
        await addDoc(dataCollection, data);
           
        },
        message(type){
                this.msg.info=hp.message(type, "packing size")
                this.msg.type="success"                                                         
        }, 
        async checkDatabase(){ 
            const query_url=query(collection(db, "packingSizes"), orderBy("created_at", "desc"));           
                onSnapshot(query_url, async(snapShot) =>{
                    //show data
                    const pz=ss.packingSizes(snapShot)          
                    this.packingSizes=pz;
                    this.isLoading=false;
                    this.isUpdating=false;
                    //
                    const dc=snapShot._snapshot.docChanges;
                    const user=this.user;
                    snapShot.docChanges().forEach((change) => {                       
                        const res=change.doc.data();   
                            if(change.type==="added" && res.edit_user.uid==user.uid && dc.length===1){
                                    this.message("added")
                                    this.packing_unit="";          
                                    this.isSaving=false;
                                    this.msg.info="The packing unit has been saved"
                                    this.msg.type="success"
                                    this.setMsg();
                            }           
                            if(change.type==="modified" && res.edit_user.type=="modified" && res.edit_user.uid==user.uid){
                                    this.message("modified")
                                    this.isUpdating=false;
                                    this.editItem={}
                                    this.msg.info="This packing unit has been updated"
                                    this.msg.type="success"
                                    this.setMsg();
                            }  
                            if(change.type==="removed" && res.edit_user.type=="removed" && res.edit_user.uid==user.uid){
                                    this.message("removed")
                                    this.msg.info="The packing unit has been deleted"
                                    this.msg.type="success"
                                    this.setMsg();
                            }             
                    });                 
            });                              
        } ,
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