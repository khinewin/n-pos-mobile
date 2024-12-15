<template lang="">
    <div class="">    
        <span v-if="msgType" class="position-fixed end-0 top-0  z-3 text-white rounded py-2 px-3 my-2 mx-2" role="alert" :class="[msgType=='success' ? 'bg-success' : 'bg-danger']">
            <span v-if="msgType=='error'"><i class="fa-solid fa-circle-exclamation"></i></span>
            <span v-if="msgType=='success'"><i class="fa-solid fa-circle-check"></i></span>
           <span class="ms-2"> {{msgInfo}}</span>
        </span>
        <span v-if="isOffline" class="position-fixed start-0 top-0  z-3 alert alert-secondary text-danger" role="alert">
            <span><i class="fa-solid fa-triangle-exclamation"></i> </span>
           <span class="ms-2"> {{isOffline}}</span>
        </span>
   
    <LeftSidebar />
    <Navbar />  
    <div class="container-fluid">
        <div class="row">           
            <div class="col-12 py-2">
                   <LeftMenuBtn />
                  <span class="h5 ms-3"> 
                    {{pageTitle}} 
                </span>
            </div>
        </div>
    </div>     
    <RightSidebar />
    
</div>
</template>
<script>
    import RightSidebar from '@/views/partials/RightSidebar.vue';
    import LeftSidebar from "@/views/partials/LeftSidebar.vue"
    import Navbar from "@/views/partials/Navbar.vue"
    import LeftMenuBtn from '@/views/partials/LeftMenuBtn.vue'
    import db from '@/firebase/firestore'
    import { enableNetwork, disableNetwork } from "firebase/firestore"; 

export default {
    props: ['pageTitle', 'isOffline', 'msgType', 'msgInfo',],
    components : {
        LeftMenuBtn, RightSidebar, LeftSidebar, Navbar
    },
    mounted() {
        this.checkConnection();
    },
    methods:{
       async checkConnection(){
        }
    },
    watch:{
        
    }
}
</script>
<style lang="">
    
</style>

<!--
    <div class="container-fluid border-bottom p-3">
        <div class="row">
            <div class="col-2" v-show="pageCode ==='products'">
                <router-link class="btn" :to="{name : 'AddProduct'}"><i class="fa-solid fa-square-plus"></i> Add code</router-link>
            </div>
            <div class="col-2" v-show="pageCode==='add'">
                <router-link class="btn" :to="{name : 'Products'}"><i class="fa-solid fa-database"></i> Products</router-link>
            </div>
            <div class="col-2" v-show="pageCode==='purchase'">
                <router-link class="btn" :to="{name : 'AddPurchase'}"><i class="fa-solid fa-square-plus"></i> Add purchase</router-link>
            </div>
            <div class="col-2" v-show="pageCode==='addPurchase'">
                <router-link class="btn" :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping"></i> Purchase</router-link>
            </div>
        </div>
    </div>
-->