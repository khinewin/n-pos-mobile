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
//import ProductModal from "@/views/modals/purchase/ProductModal.vue"
//import PurchaseModal from '@/views/modals/purchase/PurchaseModal.vue'
//import SupplierModal from "@/views/modals/purchase/SupplierModal.vue"
//import SalesModal from "@/views/modals/sales/SalesModal.vue"
//import StocksModal from "@/views/modals/stocks/StocksModal.vue"
export default {
    components:{
        //ProductModal, PurchaseModal, SupplierModal, SalesModal, StocksModal,
    },
    created(){
        this.checkLogin();
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
