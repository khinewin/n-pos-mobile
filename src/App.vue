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
     .min-vh-80{
        min-height: 80vh !important;
   }
   .vh-80{
        height: 80vh !important;
   }
   .min-vh-75{
        min-height: 75vh !important;
   }
   .vh-75{
        height: 75vh !important;
   }
   .min-vh-70{
        min-height: 70vh !important;
   }
   .vh-70{
        height: 70vh !important;
   }
   .min-vh-65{
    min-height: 65vh !important;
   }
   .vh-65{
        height: 65vh !important;
   }
   .min-vh-60{
    min-height: 60vh !important;
   }
   .vh-60{
        height: 60vh !important;
   }
  
</style>
