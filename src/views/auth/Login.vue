<template lang="">
    <div class="container-fluid">
            <div class="row">
                <div class="col-lg-6"></div>
                <div class="col-lg-6">
                        <div class="row justify-content-center min-vh-100">
                            <div class="col-sm-10 col-md-8 align-self-center">
                                    <div class="h4 text-center mb-3">
                                        Login
                                    </div>
                                    <div class="card">
                                        <div class="card-body">
                                            <form  @submit.prevent="doSignin">                                        
                                                <div class="mb-3">
                                                    <label for="email" class="form-label">
                                                        <span>E-mail</span>
                                                        <span  class="text-danger small ms-3" v-for="error in v$.email.$errors" :key="error.$uid">
                                                            {{error.$message}}
                                                        </span>
                                                    </label>
                                                    <input type="email" class="form-control" id="email" v-model="email"  :class="{ 'is-invalid': v$.email.$errors.length}">
                                                  
                                                </div>
                                                <div class="mb-3">
                                                    <label for="password" class="form-label">
                                                        <span>Password</span>
                                                        <span  class="text-danger small ms-3" v-for="error in v$.password.$errors" :key="error.$uid">
                                                            {{error.$message}}
                                                        </span>
                                                    </label>
                                                    <div class="input-group">
                                                        <input :type="isShowPassword ? 'text' : 'password' " class="form-control" id="password" v-model="password" :class="{ 'is-invalid': v$.password.$errors.length }">
                                                                <span class="input-group-text" @click="handleShowPassword">
                                                                    <span   v-show="isShowPassword">
                                                                        <i class="fa-solid fa-eye-slash"></i>
                                                                    </span>
                                                                    <span  v-show="!isShowPassword">
                                                                        <i class="fa-solid fa-eye"></i>                                                        
                                                                    </span>
                                                                </span>                                                        
                                                        </div>
                                                        
                                                </div>
                                                <div class="mb-3">
                                                    <button type="submit" class="btn btn-primary" :class="{disabled: isLoading}">
                                                        <div class="spinner-border text-light spinner-border-sm" role="status" v-if="isLoading">
                                                            <span class="visually-hidden">Loading...</span>
                                                        </div>
                                                        Signin
                                                    </button>
                                                </div>
                                        </form>             
                                        </div>
                                    </div>
                            </div>
                        </div>
                </div>
            </div>
    </div>
</template>
<script>
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword } from "firebase/auth";

export default {
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                email : {required, email},
                password: {required}
            }
    },
    name: "Login",
    data(){
        return{
            email: "",
            password:"",
            isShowPassword:false,
            isLoading:false,
            message : {
                error: null,
                done:null,
            }
        }
    },
    methods:{
        handleShowPassword(){
            this.isShowPassword = !this.isShowPassword;
        },
        async doSignin(){
            const result = await this.v$.$validate()
            if(result){
                this.isLoading=true;
                const auth=getAuth();
                signInWithEmailAndPassword(auth, this.email, this.password)
                .then((res)=>{
                    this.firebase_msg="";
                    //console.log(res.user.email)
                   this.$store.dispatch("setAuthUser", res.user)
                    this.email="";
                    this.password="";
                    this.message.done="Authentication success.";
                    this.message.error=null;
                    this.$router.push({name: "Dashboard"})
                })
                .catch((rej)=>{
                    this.isLoading=false;
                    this.message.error=rej.code
                    this.message.done=null;
                })
            }
        },
        
    }
}
</script>
<style lang="">
    
</style>