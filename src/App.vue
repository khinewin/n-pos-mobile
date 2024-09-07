<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>     
        <div>          
            <span v-if="!isFirestoreConnected" class="position-fixed start-0 top-0  z-3 alert alert-secondary text-danger" role="alert">
                <span><i class="fa-solid fa-triangle-exclamation"></i> </span>
               <span class="ms-2">You are currently in offline mode</span>
            </span>
            <div>
                <RouterView />       
            </div>
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
            isFirestoreConnected: true,
        }
    },
    async created(){
       // await disableNetwork(db)
     //  await enableNetwork(db)
    },
    updated() {
        //setInterval(()=>{
           // this.updateOnlineStatus();
       // }, 5000)
    },
    
    watch:{        
       
    },
    computed:{
        
    },
    methods:{       
        async updateOnlineStatus() {
            const query_url=query(collection(db, "products"), limit(1));           
            onSnapshot(query_url, (snapShot)=>{
                    snapShot.forEach((doc)=>{
                        if(doc.metadata.fromCache){
                            this.isFirestoreConnected=false
                        }else{
                            this.isFirestoreConnected=true;
                        }
                    })
            })
           
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
