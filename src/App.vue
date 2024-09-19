<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>     
        <div>                     
                <RouterView />       
        </div>        
</template>

<script>
import db from "@/firebase/firestore"
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword } from "firebase/auth";
import { disableNetwork, enableNetwork, onSnapshot, collection, getDocs,query, limit} from "firebase/firestore";
export default {
    components:{
        //ProductModal, PurchaseModal, SupplierModal, SalesModal, StocksModal,
    },
    data(){
        return{
        }
    },
    async created(){
       // await disableNetwork(db)
     //  await enableNetwork(db)
    },
  
    
    watch:{        
       
    },
    computed:{
        
    },
    methods:{       

        setCusrot(){
          
         },
       
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
