<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>     
        <div class="my-5">
            <RouterView />       
        </div>        
</template>

<script>
import db from "@/firebase/firestore"
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword } from "firebase/auth";
export default {
    components:{
        //ProductModal, PurchaseModal, SupplierModal, SalesModal, StocksModal,
    },
    data(){
        return{
        }
    },
    created(){
        this.checkLogin();
    },
    mounted() {
    },  
 
    computed:{
    
    },
    methods:{        
        checkLogin() {
            const auth = getAuth();
            onAuthStateChanged(auth, (user) => {
                if (user) {
                    this.$store.dispatch("setAuthUser", user);
                }
            });
        },
    }
}
</script>
<style scoped>
   
</style>
