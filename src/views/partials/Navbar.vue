<template>             
  <nav class="navbar bg-primary py-1">
    <div class="container px-3 d-flex align-items-center justify-content-between">
      <a class="navbar-brand text-white logo p-0 d-flex align-items-center" href="#!">
        <img src="/img/nav-logo.png" class="img-fluid m-0" style="width: 40px" alt="Logo">
      </a>  
      
      <div class="d-flex align-items-center gap-2">
        <!-- Language Switcher (MM / ENG) -->
        <div class="lang-switcher-container bg-white p-1 rounded-3 d-flex align-items-center shadow-sm">
          <button 
            type="button" 
            class="btn btn-sm lang-btn py-0 px-2 rounded-2 border-0 fw-bold" 
            :class="currentLang === 'mm' ? 'btn-primary text-white shadow-sm' : 'text-dark bg-transparent opacity-75'"
            @click="changeLang('mm')"
            title="Myanmar Language">
            MM
          </button>
          <button 
            type="button" 
            class="btn btn-sm lang-btn py-0 px-2 rounded-2 border-0 fw-bold" 
            :class="currentLang === 'eng' ? 'btn-primary text-white shadow-sm' : 'text-dark bg-transparent opacity-75'"
            @click="changeLang('eng')"
            title="English Language">
            ENG
          </button>
        </div>

        <!-- Offcanvas Sidebar Toggle Button -->
        <button class="btn btn-outline-light btn-sm ms-1" type="button" data-bs-toggle="offcanvas" data-bs-target="#rightSide" aria-controls="rightSide">
          <i class="fa-solid fa-bars"></i>
        </button>          
      </div>
    </div>
  </nav> 
</template>

<script>
export default {
  name: "Navbar",
  computed: {
    currentLang() {
      return this.$store.getters.currentLang || 'eng';
    }
  },
  methods: {
    changeLang(langCode) {
      this.$store.dispatch('changeLanguage', langCode);
    }
  }
}
</script>

<style scoped>
.lang-switcher-container {
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.lang-btn {
  font-size: 0.75rem;
  line-height: 1.5;
  transition: all 0.2s ease-in-out;
}

.lang-btn.text-dark:hover {
  opacity: 1 !important;
  color: #0d6efd !important;
}
</style>