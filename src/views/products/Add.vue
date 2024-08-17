<template lang="">
    <div>           
        <PageHeader page-title="Add code" page-code="add" :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container-fluid my-2">
            <div class="row mb-2 justify-content-end"> 
                <div class="col-lg-2">
                    <button type="button" class="btn" :disabled="isLoading" @click="saveCode">
                        <i class="fa-solid fa-hard-drive"></i> Save
                        <span class="spinner-border spinner-border-sm" aria-hidden="true" v-if="isLoading"></span>
                    </button>
                </div>               
                <div class="col-lg-2">
                    <router-link class="btn" :to="{name : 'Products'}"><i class="fa-solid fa-circle-xmark"></i> Close</router-link>
                </div>
            </div>
        </div>

        <div class="container-fluid">
        <div class="card">
            <div class="card-body min-vh-100">
            <div class="row mt-4 justify-content-center">                
                <div class="col-sm-10 col-md-8 col-lg-6">
                        <form @submit.prevent="saveCode">
                        <div class="form-floating mb-3">
                            <input type="text" v-model="code_no" class="form-control form-control-sm" id="code_no" placeholder="Code">
                            <label for="code_no" class="form-label">Code</label>
                        </div>
                    
                        <div class="form-floating mb-3">
                            <input type="text" v-model="item_name" class="form-control form-control-sm" id="item_name" placeholder="Description">
                            <label for="item_name" class="form-label">
                            <span>Description</span>
                                <span  class="text-danger small ms-3" v-for="error in v$.item_name.$errors" :key="error.$uid">
                                    {{error.$message}}
                                </span>
                            </label>
                        </div>
                        <div class="form-floating mb-3">
                            <input type="text" v-model="item_per_package" class="form-control form-control-sm" id="item_per_package" placeholder="Packing item">
                            <label for="item_per_package" class="form-label">
                            <span>Item per package</span>
                                <span  class="text-danger small ms-3" v-for="error in v$.item_per_package.$errors" :key="error.$uid">
                                    {{error.$message}}
                                </span>
                            </label>
                        </div>                      
                    </form>
                </div>                                
            </div>
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
            code_no :"",
            item_name:"",
            item_per_package:"",
            isLoading:false,
            msg : {
                type :"",
                info :"",
            }

        }
    },
    validations(){
            return {
                item_name: {required},
                item_per_package: {required}
            }
    },
    mounted() {
        const code_no=document.getElementById("code_no");
        code_no.focus();
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        async saveCode(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            this.isLoading=true;
            const save_time=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "products", )
            const code_no=this.code_no ? this.code_no : new Date().getTime()
            const data={
                uid: this.user.uid,
                item_name: this.item_name, 
                code_no : code_no,
                item_per_package: Number(this.item_per_package),
                qty : 0,
                retail_price: 0,
                whole_sale_price: 0,
                created_at : save_time,               
            }
            const result=await addDoc(dataCollection, data);
            if(result){
                this.msg.type="success";
                this.msg.info="Product code save successful."
            }else{
                this.msg.type="error";
                this.msg.info="Product code save failed."
            }
            this.item_name="";
            this.item_per_package="";
            this.isLoading=false;
            this.v$.$reset()
        }
    }
}
</script>
<style lang="">
    
</style>