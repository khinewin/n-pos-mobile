<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>     
        <div class="roboto-regular">                     
                <RouterView />       
        </div>        
</template>

<script>
import db from "@/firebase/firestore"
import { getAuth,onAuthStateChanged, signInWithEmailAndPassword } from "firebase/auth";
import {Timestamp,onSnapshot, collection, addDoc, query, where, setDoc, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
import * as ss from "@/lib/snapshot/products"

export default {
    components:{
        //ProductModal, PurchaseModal, SupplierModal, SalesModal, StocksModal,
    },
    data(){
        return{
        }
    },
    created() {
        this.fetchProducts()
    },
  
    methods:{   
       
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            onSnapshot(query_url, async(snapShot) =>{
                const products=ss.products(snapShot);   
                this.$store.dispatch("setProducts", products);
            })
           
       } ,
    }
}
</script>
<style>
 
</style>
