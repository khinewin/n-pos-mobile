<template>
  <div class="offcanvas offcanvas-end modern-right-sidebar shadow-sm" tabindex="-1" id="rightSide" aria-labelledby="rightSide">
    <!-- Account Header Section -->
    <div class="offcanvas-header py-2 px-3 pb-2">
      <div class="d-flex flex-column">
        <div class="d-flex align-items-center gap-2 mb-0">
          <div class="avatar-box bg-primary bg-opacity-10 text-primary d-flex align-items-center justify-content-center rounded-circle" style="width: 32px; height: 32px;">
            <i class="fa-solid fa-user fs-6"></i>
          </div>
          <h5 class="logo text-dark fw-bolder mb-0" id="rightSideLabel" style="letter-spacing: -0.5px;">
            <span v-if="user !== null">{{ user.displayName || 'User' }}</span>
          </h5>
        </div>
        <small class="text-muted fw-medium" style="font-size: 0.65rem;">Account Settings & Preferences</small>
      </div>
      <button type="button" class="btn text-muted shadow-none ms-auto p-1" data-bs-dismiss="offcanvas" aria-label="Close">
        <i class="fa-solid fa-chevron-right fs-6"></i>
      </button>
    </div>
    
    <!-- User Actions Body -->
    <div class="offcanvas-body p-0">
      <div class="nav-container px-2 py-1">
        <ul class="nav flex-column gap-1">
          <li class="nav-item" data-bs-dismiss="offcanvas">
            <router-link :to="{ name: 'Profile' }" class="nav-link sidebar-item d-flex align-items-center gap-2 py-1.5 px-2 rounded-2">
              <div class="icon-frame d-flex align-items-center justify-content-center">
                <i class="fa-solid fa-id-card-clip" style="font-size: 0.9rem;"></i>
              </div>
              <span class="item-label text-nowrap">{{ profile_link }}</span>
            </router-link>
          </li>
          
          <li class="nav-item">
            <a href="#!" class="nav-link sidebar-item d-flex align-items-center gap-2 py-1.5 px-2 rounded-2 text-danger" @click="showConfirm">
              <div class="icon-frame d-flex align-items-center justify-content-center text-danger">
                <i class="fa-solid fa-right-from-bracket" style="font-size: 0.9rem;"></i>
              </div>
              <span class="item-label text-nowrap">{{ logout_link }}</span>
            </a>
          </li>
        </ul>

       
      </div>
    </div>

    <!-- Sidebar Footer -->
    <div class="sidebar-footer p-2 border-top bg-light bg-opacity-25 mt-auto text-center">
      <span class="text-muted opacity-50" style="font-size: 0.6rem;">Powered by N-PRO POS v2.1.0</span>
    </div>

  </div> <!-- Closing offcanvas -->

  <!-- Redesigned Logout Confirmation Modal (Moved outside offcanvas for proper z-index) -->
  <div class="modal fade" id="confirmModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-sm">
      <div class="modal-content border-0 shadow-lg rounded-4 overflow-hidden">
        <div class="modal-body p-4 text-center">
          <div class="logout-icon-shell mb-3 mx-auto">
            <i class="fa-solid fa-right-from-bracket text-danger fs-3"></i>
          </div>
          <h5 class="fw-bold text-dark mb-2">Sign Out?</h5>
          <p class="text-muted small mb-4">Are you sure you want to log out of your account?</p>
          <div class="d-flex gap-2">
            <button type="button" class="btn btn-light flex-fill fw-bold text-muted border-0 py-2" @click="hideConfirmModal" data-bs-dismiss="modal">Cancel</button>
            <button type="button" class="btn btn-danger flex-fill fw-bold py-2 shadow-sm" @click="logout">Confirm</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAuth, signOut } from "firebase/auth";
import { lang } from "@/lib/language"

export default {
  name: "RightSidebar",
  data() {
    return {
      confirmModal: null
    }
  },
  computed: {
    user() {
      return this.$store.getters.user;
    },
    profile_link() {
      const sl = this.$store.getters.setLanguage;
      return sl == "mm" ? lang.profile.mm : lang.profile.eng;
    },
    logout_link() {
      const sl = this.$store.getters.setLanguage;
      return sl == "mm" ? lang.logout.mm : lang.logout.eng;
    },
    theme() {
      return this.$store.getters.theme;
    },
    t() {
      return (key) => this.$store.getters.t(key);
    },
  },
  methods: {
    changeTheme(event) {
      this.$store.dispatch('setTheme', event.target.value);
      setTimeout(() => {
        window.location.reload();
      }, 100);
    },
    showConfirm() {
      const modalEl = document.getElementById('confirmModal');
      this.confirmModal = bootstrap.Modal.getOrCreateInstance(modalEl);
      this.confirmModal.show();
    },
    hideConfirmModal() {
      const modalEl = document.getElementById('confirmModal');
      const instance = bootstrap.Modal.getInstance(modalEl);
      if (instance) {
        instance.hide();
      }
      this.confirmModal = null;
    },
    logout() {
      const auth = getAuth();
      signOut(auth).then(() => {
        this.hideConfirmModal();
        this.$store.dispatch("setAuthUser", null);
        this.$router.push("/");
      });
    }
  }
}
</script>

<style scoped>
.modern-right-sidebar {
  background-color: #f8f9fa;
  border-left: 1px solid rgba(0, 0, 0, 0.05);
  width: 250px !important;
  font-family: 'Roboto', 'Inter', sans-serif;
}

.sidebar-item {
  color: #5f6368 !important;
  transition: all 0.2s ease-in-out;
  border: 1px solid transparent;
  font-weight: 500;
  padding-top: 6px !important;
  padding-bottom: 6px !important;
}

.sidebar-item:hover {
  background-color: #ffffff;
  color: #0d6efd !important;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}

.sidebar-item.text-danger:hover {
  color: #dc3545 !important;
  background-color: #fff1f2;
}

.icon-frame {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.sidebar-item:hover .icon-frame {
  background-color: rgba(13, 110, 253, 0.05);
}

.sidebar-item.text-danger:hover .icon-frame {
  background-color: rgba(220, 53, 69, 0.05);
}

.item-label {
  font-size: 0.825rem;
  letter-spacing: 0.1px;
}

.theme-switch-card {
  transition: transform 0.2s ease;
}

.theme-switch-card:hover {
  transform: translateY(-1px);
}

.logout-icon-shell {
  width: 64px;
  height: 64px;
  background: #fff1f2;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Custom shadow */
.shadow-sm {
  box-shadow: -4px 0 20px rgba(0, 0, 0, 0.02) !important;
}

/* Modal styling */
.modal-content {
  border: none;
}
</style>