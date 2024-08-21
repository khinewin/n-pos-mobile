<template>
    <div class="container-fluid">           
            <div class="row justify-content-center" style="min-height: 70vh;">                   
                <div class="col-sm-6 align-self-center" v-if="isCheckAuth"> 
                    <Welcome />           
                </div>                
                <div class="col-sm-6 align-self-center" v-else>
                        <div class="row justify-content-center">
                            <div class="col-sm-10 col-md-8">      
                                <div class="text-center fw-light mb-2">Login to continued your session</div>                            
                                    <div class="card shadow border-0">
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
                                                    <div class="mb-3 d-grid">
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
                                    <div class="text-danger text-center mt-2" v-if="message.error">{{message.error}}</div>
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
import Welcome from "@/views/Welcome.vue"
export default {
    components:{
        Welcome
    },
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
            isCheckAuth:false,
            isReady:false,
            message : {
                error: null,
            }
        }
    },
    created() {
      
    },
    mounted() {    
        this.isCheckAuth=true;
        setTimeout(()=>{
            this.checkLogin();
        }, 2000)
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        handleShowPassword(){
            this.isShowPassword = !this.isShowPassword;
        },
        checkLogin() {
            const auth = getAuth();
            onAuthStateChanged(auth, (user) => {
                if (user) {
                    this.$store.dispatch("setAuthUser", user);
                    if(this.$route.redirectedFrom===undefined){
                        this.$router.push({name: "Dashboard"})
                    }else{
                       // console.log(this.$route.redirectedFrom)
                        const redirectPage=this.$route.redirectedFrom.name
                        const redirectParams=this.$route.redirectedFrom.params
                        if(redirectPage !== "Welcome" || redirectPage !== "Login"){
                            this.$router.push({name: redirectPage, params : redirectParams})
                        }else{
                            this.$router.push({name: "Dashboard"})
                        }
                    }                                   
                }
                this.isCheckAuth=false;
            });
        },
        async doSignin(){
            const result = await this.v$.$validate()
            if(result){
                this.isLoading=true;
                const auth=getAuth();
                signInWithEmailAndPassword(auth, this.email, this.password)
                .then((res)=>{
                    this.firebase_msg="";
                   this.$store.dispatch("setAuthUser", res.user)
                    this.email="";
                    this.password="";
                    this.$router.push({name: "Dashboard"})
                })
                .catch((rej)=>{
                    this.isLoading=false;
                    this.message.error=rej.code
                })
            }
        },
        
    }
}
</script>
<style>
    
</style>