<template lang="">
    <div>           
        <PageHeader page-title="New supplier" page-code="add" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end"> 
                <div class="col-lg-2">
                    <button type="button" class="btn" :disabled="isLoading" @click="saveSupplier">
                        <i class="fa-solid fa-hard-drive"></i> Save
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>
                </div>               
                <div class="col-lg-2">
                    <router-link class="btn" :to="{name : 'Suppliers'}"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
        </div>

            <div class="container-fluid">
            <div class="card">
                <div class="card-body min-vh-100">           
                        <form @submit.prevent="saveSupplier">
                            <table class="table table-bordered">
                                <tbody>                                
                                    <tr>
                                        <td class="py-0">
                                            <input type="text" v-model="code" class="form-control-plaintext form-control-sm" id="code" placeholder="Code">                                                                                       
                                        </td>
                                        <td class="py-0">
                                            <input type="text" v-model="name" :class="{'border-danger' : v$.name.$errors.length}" class="form-control-plaintext" id="name" placeholder="Name">                                               
                                        </td>
                                        <td class="py-0">
                                            <input type="tel" v-model="phone"  :class="{'border-danger' : v$.phone.$errors.length}"  class="form-control-plaintext" id="phone" placeholder="Phone">                                            
                                        </td>
                                        <td class="py-0">
                                            <input type="text" v-model="company"  :class="{'border-danger' : v$.company.$errors.length}"  class="form-control-plaintext" id="company" placeholder="Company">                                              
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                             
                    </form>
                </div>                                          
            </div>
        </div>
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc} from 'firebase/firestore'
import db from '@/firebase/firestore'
export default {
    components: {
        PageHeader
    },
    setup () {
        return { v$: useVuelidate() }
    },
    data(){
        return{
            code :"",
            name:"",
            phone:"",
            company:'',
            isLoading:"",
            created_at:"",
            isLoading:false,
            msg : {
                type :"",
                info :"",
            }

        }
    },
    validations(){
            return {
                name: {required},
                phone: {required},
                company:{required}
            }
    },
    mounted() {
        const name=document.getElementById("name");
        name.focus();
        this.code= new Date().getTime()

    },
   watch:{
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
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        async saveSupplier(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            this.isLoading=true;
            const date=this.created_at ? new Date(this.created_at) : new Date();
            const created_at=Timestamp.fromDate(date);
            const dataCollection=collection(db, "suppliers", )
            const data={
                uid: this.user.uid,
                name: this.name, 
                code : this.code,
                phone: this.phone,
                company: this.company,
                created_at : created_at,               
            }
            const result=await addDoc(dataCollection, data);
            if(result){
                this.msg.type="success";
                this.msg.info="Supplier save successful."
            }else{
                this.msg.type="error";
                this.msg.info="Supplier save failed."
            }
            this.name="";
            this.phone="";
            this.code= new Date().getTime()
            this.company="";
            this.isLoading=false;
            this.v$.$reset()
        }
    }
}
</script>
<style lang="">
    
</style>

     <!--
                            <div class="row g-2">
                                <div class="col-lg-6">
                                    <div class="">
                                        <input type="text" v-model="code" class="form-control form-control-sm" id="code" placeholder="Code">
                                        <label for="code" class="form-label">Code</label>
                                    </div>
                                </div>
                                <div class="col-lg-6">
                                    <div class="">
                                        <input type="text" v-model="name" class="form-control form-control-sm" id="name" placeholder="Name">
                                        <label for="name" class="form-label">
                                        <span>Name</span>
                                            <span  class="text-danger small ms-3" v-for="error in v$.name.$errors" :key="error.$uid">
                                                {{error.$message}}
                                            </span>
                                        </label>
                                    </div>
                                </div>
                                <div class="col-lg-6">
                                    <div class="">
                                        <input type="tel" v-model="phone" class="form-control form-control-sm" id="phone" placeholder="Phone">
                                        <label for="phone" class="form-label">
                                        <span>Phone</span>
                                            <span  class="text-danger small ms-3" v-for="error in v$.phone.$errors" :key="error.$uid">
                                                {{error.$message}}
                                            </span>
                                        </label>
                                    </div>
                                </div>   
                                <div class="col-lg-6">
                                    <div class="">
                                        <input type="text" v-model="company" class="form-control form-control-sm" id="company" placeholder="Company">
                                        <label for="company" class="form-label">
                                        <span>Company</span>
                                            <span  class="text-danger small ms-3" v-for="error in v$.company.$errors" :key="error.$uid">
                                                {{error.$message}}
                                            </span>
                                        </label>
                                    </div>
                                </div>                                                                                                           
                        </div>       
                    -->    