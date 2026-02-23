<template>
    <div class="container-fluid">           
            <div class="row g-1 vh-100">                   
                <div class="col-md-6  bg-light bg-gradient d-none d-md-block" > 
                    <div class="row g-0 vh-100  justify-content-center">
                            <div class="col-sm-10 col-lg-8 align-self-center">
                                <Welcome :isCheckAuth="isCheckAuth" />
                            </div>
                    </div>           
                </div>                
                <div class="col-md-6  bg-light " >
                        <div class="row justify-content-center align-items-center vh-100">
                            <div class="col-sm-10 col-md-8">     
                                <div class="text-center">
                                    <img src="/img/logo.png" alt="" class="img-fluid" style="width: 100px">
                                    <div class="text-primary logo h2">Point Of Sale</div>
                                </div>
                                <div class="d-flex justify-content-center my-5 " v-if="isCheckAuth">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div> 
                                <div v-else> 
                                <div class="mb-1">
                                    <span class="text-danger" v-if="message.error">{{message.error}}</span>
                                    <span v-else class="fw-light">Login to continued your session</span>
                                </div>                            
                                    <div class="card shadow border-0">
                                        <div class="card-body">
                                            <form @submit.prevent="doSignin">
                                            <div class="mb-3">        
                                                <label for="email" class="form-label small">
                                                    <span>E-mail</span>
                                                    <span  class="text-danger small ms-3" v-for="error in v$.email.$errors" :key="error.$uid">
                                                        {{error.$message}}
                                                    </span>
                                                </label>                                                
                                                <input @keypress="clearError" type="email" class="form-control"  id="email" v-model="email" >                                                                                                      
                                            </div>                                                                                                  
                                            <div class="mb-3 ">  
                                                <label for="password" class="form-label small">
                                                    <span>Password</span>
                                                    <span  class="text-danger small ms-3" v-for="error in v$.password.$errors" :key="error.$uid">
                                                        {{error.$message}}
                                                    </span>
                                                </label>                                                       
                                                <div class="input-group">
                                                    <input @keypress="clearError" :type="isShowPassword ? 'text' : 'password' " class="form-control" id="password" v-model="password">
                                                            <span class="input-group-text" @click="handleShowPassword">
                                                                <span   v-show="!isShowPassword">
                                                                    <i class="fa-solid fa-eye-slash"></i>
                                                                </span>
                                                                <span  v-show="isShowPassword">
                                                                    <i class="fa-solid fa-eye"></i>                                                        
                                                                </span>
                                                            </span>                                                        
                                                    </div>                                                                                                           
                                            </div>
                                            <div class="d-grid">
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
    </div>
</template>
<script>
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword,updateProfile } from "firebase/auth";
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
         this.checkLogin();
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        clearError(){
            this.message.error=null;
        },
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
                        const redirectPage=this.$route.redirectedFrom.name
                        const redirectParams=this.$route.redirectedFrom.params
                        if(redirectPage !== "Welcome"){
                            this.$router.push({name: redirectPage, params : redirectParams})
                        }else{
                            this.$router.push({name: "Dashboard"})
                        }
                    }                                   
                }
                setTimeout(()=>{
                    this.isCheckAuth=false;
                }, 2000)
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
                    if(!res.user.displayName) this.setUsername();
                    this.$router.push({name: "Dashboard"})
                })
                .catch((rej)=>{
                    this.isLoading=false;
                    this.message.error=rej.code
                })
            }
        },
        async setUsername(){
            const auth = getAuth();
            updateProfile(auth.currentUser, {
            displayName: "N-User",// photoURL: "https://example.com/jane-q-user/profile.jpg"
            })
        },
        
    }
}
</script>
<style>
    
</style>