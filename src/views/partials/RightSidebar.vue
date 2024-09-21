<template lang="">       
      <div class="offcanvas offcanvas-end bg-secondary" tabindex="-1" id="rightSide" aria-labelledby="rightSide">
          <div class="offcanvas-header">            
            <a href="#!" role="button " class="btn text-white" data-bs-dismiss="offcanvas" aria-label="Close">
              <i class="fa-solid fa-caret-right"></i>
            </a>
            <h5 class="offcanvas-title text-white" id="rightSide"><span v-if="user !==null">{{user.displayName}}</span></h5>
          </div>
          <div class="offcanvas-body ">      
            <ul class="list-group small ">
              <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0" data-bs-dismiss="offcanvas" aria-label="Close">
                <router-link class="nav-link py-2 px-3" :to="{name : 'Profile'}"><i class="fa-solid fa-id-card-clip"></i> Profile</router-link>
              </li>
              <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0" >
                <a href="#!"  class="nav-link py-2 px-3" @click="showConfirm"><i class="fa-solid fa-right-from-bracket" ></i> Logout</a>
              </li>                       
           </ul>
          </div>
          <!-- Modal -->
          <div class="modal fade" id="confirmModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-sm">
              <div class="modal-content">               
                <div class="modal-body">
                  <h3 class="text-center">Are you sure ?</h3>
                  <div class="text-center">Do you want to logout</div>               
                  <div class="row mt-4">
                    <div class="col-6 text-end">
                      <button type="button"  class="btn btn-secondary" @click="hideConfirmModal">Cancel</button>
                    </div>
                    <div class="col-6">
                      <button type="button" @click="logout"  class="btn btn-primary">Confirm</button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <!--end modal-->
      </div>
</template>
<script>
  import { getAuth, signOut } from "firebase/auth";
export default {
  name : "RightSidebar",
  data(){
      return {
          confirmModal : null
      }
  },
  computed:{
      user(){
          return this.$store.getters.user;
      }
  },
  methods:{
    showConfirm(){    
      const confirmModal = new bootstrap.Modal('#confirmModal')
      this.confirmModal=confirmModal
      this.confirmModal.show();
     
    },
    hideConfirmModal(){
      this.confirmModal.hide(); 
      this.confirmModal=null;
    },
    logout(){
        const auth = getAuth();
        signOut(auth).then(() => {
          this.$store.dispatch("setAuthUser", null);
          this.$router.push("/");
        });
      }  
  }
    
}
</script>
<style>
        .list-group-item-action:hover .nav-link{
          background-color: rgba(59, 58, 58, 0.5) !important;
          transition: all .2s;
          color: white !important;
      }
      .router-link-active {
          color : #0d6efd !important
      }
</style>