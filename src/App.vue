<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>     
        <div>          
            <div>
                <RouterView />       
            </div>
        </div>        
</template>

<script>
import db from "@/firebase/firestore"
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword } from "firebase/auth";
import { doc, onSnapshot } from "firebase/firestore";
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
    updated() {
        
    },
    watch:{        
        
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
<style>
 
</style>
