<template lang="">
    <div>       
    <LeftSidebar />
    <Navbar />  
    <div class="container-fluid">
        <div class="row">           
            <div class="col-12 pt-3 pb-2 bg-secondary-subtle">
                   <LeftMenuBtn />
                  <span class="h5 ms-3"> 
                    {{pageTitle}} <span class="float-end me-2" :class="[msgType=='success' ? 'text-success' : 'text-danger']">{{msgInfo}}</span>
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
export default {
    props: ['pageTitle', 'pageCode', 'msgType', 'msgInfo'],
    components : {
        LeftMenuBtn, RightSidebar, LeftSidebar, Navbar
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