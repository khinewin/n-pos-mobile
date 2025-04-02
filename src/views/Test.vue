<template lang="">
    <div>
        
    </div>
</template>
<script>
export default {
    data(){
        return{
            packageName:"store.yoeyarvegas.app",
            queryParam:"n-pos.web.app/products",
        }
    },
    mounted() {
            this.updateQueryParam();
    },
    methods: {
        updateQueryParam() {
        const urlParams = new URLSearchParams(window.location.search);
       // this.queryParam = urlParams.get("q");
        if(this.queryParam !=null){
            this.autoOpenApk();
        }
        },
        autoOpenApk(){
            window.location.href = `intent://${this.queryParam}#Intent;package=${this.packageName};scheme=https;end;`;

        }
    },
}
</script>
<style lang="">
    
</style>