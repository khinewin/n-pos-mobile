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
import * as pz from "@/lib/snapshot/packingSizes"

export default {
    components:{
        //ProductModal, PurchaseModal, SupplierModal, SalesModal, StocksModal,
    },
    data(){
        return{
        }
    },
    created() {
       // this.fetchProducts()
      //this.fetchPackingSizes();
    },
  
    methods:{   
       async fetchPackingSizes(){
        const query_url = query(collection(db, "packingSizes"), orderBy("created_at", "desc"));
        onSnapshot(query_url, (snapShot)=>{
                const pSIzes=pz.packingSizes(snapShot)          
                this.$store.dispatch("setPackingSizes", pSIzes);
        })
       },
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
