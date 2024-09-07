<template lang="">     
    <div>           
        <PageHeader page-title="User profile" page-code="profile"  :msg-type="msg.type" :msg-info="msg.info" />
        <div class="container vh-80 overflow-y-auto">
            <div class="row justify-content-center g-2" v-if="user !==null">
                <div class="col-sm-10 col-md-8 col-lg-6">
                        <div class="card shadow mb-2"  >
                            <img src="@/assets/img/user_profile.png" alt="User photo" class="card-img-top">
                            <div class="card-body">
                                    <table class="table profile-table">
                                        <tbody >
                                            <tr>
                                                <td>Name <span class="float-end">:</span> </td>
                                                <td>
                                                    <input @keypress="saveUsername" type="text" class="form-control-plaintext form-control-sm" v-model="userName">
                                                </td>
                                            </tr>
                                            <tr>
                                                <td>E-mail <span class="float-end">:</span> </td>
                                                <td>{{user.email}}</td>
                                            </tr>
                                            <tr>
                                                <td>UID <span class="float-end">:</span> </td>
                                                <td>{{user.uid}}</td>
                                            </tr>
                                            <tr>
                                                <td>Member since <span class="float-end">:</span> </td>
                                                <td>{{user.metadata.creationTime}}</td>
                                            </tr>
                                            <tr>
                                                <td>Last login <span class="float-end">:</span> </td>
                                                <td>{{user.metadata.lastSignInTime}}</td>
                                            </tr>                                          
                                        </tbody>
                                    </table>
                            </div>
                        </div>
                        <div class="card shadow">
                            <div class="card-body">
                                <div class="mb-3">Change password</div>
                                    <div class="row g-1 mb-2">
                                        <div class="col-sm-6">
                                            <div class="form-floating">
                                                <input v-model="password" :class="{'is-invalid' : v$.confirm_password.$errors.length}" type="password" id="password" class="form-control form-control-sm" placeholder="Password">
                                                <label for="password" class="form-label">
                                                    <span v-if="v$.password.$errors.length <=0">Password</span>
                                                    <span v-for="error in v$.password.$errors" :key="error.$uid">{{error.$message}}</span>
                                                </label>
                                            </div>
                                        </div>
                                        <div class="col-sm-6">
                                            <div class="form-floating">
                                                <input v-model="confirm_password" :class="{'is-invalid' : v$.confirm_password.$errors.length}" type="password" id="confirm_password" class="form-control form-control-sm" placeholder="Confirm password">
                                                <label  for="confirm_password" class="form-label">
                                                    <span v-if="v$.confirm_password.$errors.length <=0">Confirm password</span>
                                                    <span v-for="error in v$.confirm_password.$errors" :key="error.$uid">{{error.$message}}</span>
                                                </label>
                                            </div>
                                        </div>                                        
                                    </div>
                                    <div class="row">
                                        <div class="col-12 d-grid">
                                            <button class="btn btn-secondary" @click="changePassword">
                                                <span v-if="!isUpdating">Save</span>
                                                <span class="spinner-border text-light spinner-border-sm" role="status" v-if="isUpdating">
                                                    <span class="visually-hidden">Loading...</span>
                                                </span>
                                            </button>
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
import PageHeader from "@/views/partials/PageHeader.vue"
import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers, sameAs } from '@vuelidate/validators'
import * as hp from '@/lib/helper'
import { getAuth, updatePassword, updateProfile } from "firebase/auth";

export default {
    components: {
            PageHeader
    },
    setup () {
        return { v$: useVuelidate() }
    },
    validations(){
            return {
                password: {required},
                confirm_password: {required},
            }
    },
    data(){
        return{
            msg:{
                type:"",
                info:"",
            },
            userName: "",
            isUpdating:false,
            password: "",
            confirm_password:"",
        }
    },
    mounted(){
        this.userName=this.user.displayName ? this.user.displayName : "N-User"
    },
    watch:{
        msg:{
               handler(val){
                   if(val.type){
                       setTimeout(()=>{
                           this.msg.type="";
                           this.msg.info="";
                       }, 3000)
                   }
               },deep:true
           }
    },
    computed:{
        user(){
            return this.$store.getters.user ? this.$store.getters.user : null;
        },
       
    },
    methods:{
        async saveUsername(){
            const auth = getAuth();
            updateProfile(auth.currentUser, {
            displayName: this.userName,// photoURL: "https://example.com/jane-q-user/profile.jpg"
            }).then(() => {
                this.msg.info="The user name has been saved";
                this.msg.type="success"
            }).catch((error) => {
                this.msg.type="error"
                this.msg.info="The user name set failed"
            });
        },
        async changePassword(){
            const validattion = await this.v$.$validate()
            if(!validattion) return false;
            if(this.password !== this.confirm_password){
                this.msg.type="error";
                this.msg.info="Password and confirm password must match"
                this.password="";
                this.confirm_password="";
                return false;
            }
            this.isUpdating=true;
            const auth = getAuth();
            const user = auth.currentUser;
            updatePassword(user, this.password).then(() => {
                this.msg.type="success";
                this.msg.info="Password change successful"
            }).catch((error) => {
                this.msg.type="error";
                this.msg.info=error;
            });
            this.password="";
            this.confirm_password="";
            this.isUpdating=false;
            this.v$.$reset()
        }
    }
}
</script>
<style>
 .profile-table  td {
        font-size: 14px !important;
        padding: 10px !important;
    }
</style>