<template lang="">    
    <!-- Sales dropend button -->
  <div class="btn-group dropend">
      <button type="button" class="btn btn-sm dropdown-toggle" data-bs-toggle="dropdown" aria-expanded="false">
          {{title}}
      </button>                               
      <ul class="dropdown-menu py-0">                                  
          <li class="list-group-item" v-for="a in arr" :key="a.code">                                     
              <button :class="{'disabled' : !a.active}" class="dropdown-item small" @click="report(a.code)">{{a.name}}</button>
          </li>                                          
      </ul>
  </div>
  <!--Sales dropen button-->
</template>
<script>
export default {
    props: ['title', 'arr'],
    methods:{
        report(code){
            this.$emit("report", code)
        }
    }
}
</script>
<style lang="">
    
</style>