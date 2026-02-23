<template>
    <div class="login-wrapper">
      <div class="login-container">
  
        <!-- LEFT PANEL -->
        <div class="login-left d-none d-md-flex shadow">
            <div class="justify-content-center">
                <div class="text-light">     
                        <div class="h5 mb-3">Upgrade Your Mini Mart with</div>     
                        <div class="h2 mb-3">N-POS Software</div>                                                                 
                        <div class="mb-2"><i class="fa-solid fa-circle-check"></i> Fast Checkout</div>
                        <div class="mb-2"><i class="fa-solid fa-circle-check"></i> Sales Reports</div>
                        <div class="mb-2"><i class="fa-solid fa-circle-check"></i> Real-time Inventory</div> 
                        <div class="mb-2"><i class="fa-solid fa-circle-check"></i> Cloud-Based & Secure Data</div>                         
                        <div class="mb-2"><i class="fa-solid fa-circle-check"></i> Work Everywhere, On Any Devices</div>         
                        <div class="mb-2"><i class="fa-solid fa-circle-check"></i> Sales Better, Faster, Smarter with N-POS</div>                          
                 
                </div>
            </div>
        </div>
  
        <!-- RIGHT PANEL -->
        <div class="login-right shadow">
          <div class="login-card">
  
            <!-- LOGO -->
            <div class="text-center mb-4">
              <img src="/img/logo.png" class="logo-img" alt="POS Logo" />
              <h4 class="brand-title mt-3">Point Of Sale</h4>
            </div>
  
            <!-- AUTH CHECK -->
            <div v-if="isCheckAuth" class="text-center py-4">
              <div class="spinner-border text-primary"></div>
            </div>
  
            <!-- LOGIN FORM -->
            <form v-else @submit.prevent="doSignin">
  
              <!-- ERROR MESSAGE -->
              <div v-if="message.error" class="alert alert-danger py-2">
                {{ message.error }}
              </div>
  
              <!-- EMAIL -->
              <div class="mb-2">
                <label class="form-label">Email 
                    <span  class="text-danger small ms-3" v-for="error in v$.email.$errors" :key="error.$uid">
                    {{error.$message}}
                    </span>
                </label>
                <input
                  type="email"
                  class="form-control"
                  v-model="email"
                  @input="clearError"
                  :class="{ 'is-invalid': v$.email.$error }"
                />
                
              </div>
  
              <!-- PASSWORD -->
              <div class="mb-3">
                <label class="form-label">Password 
                    <span  class="text-danger small ms-3" v-for="error in v$.password.$errors" :key="error.$uid">
                        {{error.$message}}
                    </span>
                </label>
                <div class="input-group">
                  <input
                    :type="isShowPassword ? 'text' : 'password'"
                    class="form-control"
                    v-model="password"
                    @input="clearError"
                    :class="{ 'is-invalid': v$.password.$error }"
                  />
                  <button
                    type="button"
                    class="input-group-text bg-white"
                    @click="handleShowPassword"
                  >
                    <i
                      :class="isShowPassword
                        ? 'fa-solid fa-eye'
                        : 'fa-solid fa-eye-slash'"
                    ></i>
                  </button>
                </div>
              
              </div>
  
              <!-- SUBMIT -->
              <button
                type="submit"
                class="btn btn-primary w-100"
                :disabled="isLoading"
              >
                <span
                  v-if="isLoading"
                  class="spinner-border spinner-border-sm me-2"
                ></span>
                Sign In
              </button>
  
            </form>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { useVuelidate } from "@vuelidate/core";
  import { required, email } from "@vuelidate/validators";
  import {
    getAuth,
    onAuthStateChanged,
    signInWithEmailAndPassword,
    updateProfile,
  } from "firebase/auth";
  import Welcome from "@/views/Welcome.vue";
  
  export default {
    name: "Login",
    components: { Welcome },
  
    setup() {
      return { v$: useVuelidate() };
    },
  
    validations() {
      return {
        email: { required, email },
        password: { required },
      };
    },
  
    data() {
      return {
        email: "",
        password: "",
        isShowPassword: false,
        isLoading: false,
        isCheckAuth: true,
        message: {
          error: null,
        },
      };
    },
  
    mounted() {
      this.checkLogin();
    },
  
    methods: {
      clearError() {
        this.message.error = null;
      },
  
      handleShowPassword() {
        this.isShowPassword = !this.isShowPassword;
      },
  
      checkLogin() {
        const auth = getAuth();
  
        onAuthStateChanged(auth, (user) => {
          if (user) {
            this.$store.dispatch("setAuthUser", user);
  
            const redirect = this.$route.redirectedFrom;
            if (redirect && redirect.name !== "Welcome") {
              this.$router.push({
                name: redirect.name,
                params: redirect.params,
              });
            } else {
              this.$router.push({ name: "Dashboard" });
            }
          }
          this.isCheckAuth = false;
        });
      },
  
      async doSignin() {
        const isValid = await this.v$.$validate();
        if (!isValid) return;
  
        this.isLoading = true;
        const auth = getAuth();
  
        signInWithEmailAndPassword(auth, this.email, this.password)
          .then(async (res) => {
            this.$store.dispatch("setAuthUser", res.user);
  
            if (!res.user.displayName) {
              await updateProfile(auth.currentUser, {
                displayName: "N-User",
              });
            }
  
            this.email = "";
            this.password = "";
            this.$router.push({ name: "Dashboard" });
          })
          .catch((err) => {
            const errorMap = {
              "auth/user-not-found": "User not found.",
              "auth/wrong-password": "Incorrect password.",
              "auth/invalid-email": "Invalid email address.",
              "auth/too-many-requests":
                "Too many attempts. Please try again later.",
            };
  
            this.message.error = errorMap[err.code] || "Login failed.";
          })
          .finally(() => {
            this.isLoading = false;
          });
      },
    },
  };
  </script>
  
  <style scoped>
    

  .login-wrapper {
    height: 100vh;
    display: flex;
  }
  
  .login-container {
    display: flex;
    flex: 1;
  }
  
  /* LEFT PANEL */
  .login-left {
    flex: 1;
    background: linear-gradient(135deg, #0d6efd, #3a7bfd);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: white;
  }
  
  /* RIGHT PANEL */
  .login-right {
    flex: 1;
    background: #f8f9fa;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  /* CARD */
  .login-card {
    width: 100%;
    max-width: 400px;
    padding: 40px;
    background: white;
    border-radius: 16px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  }
  
  .logo-img {
    width: 80px;
  }
  
  .brand-title {
    font-weight: 600;
    color: #0d6efd;
  }
  
  .form-control {
    border-radius: 8px;
  }
  
  .btn-primary {
    border-radius: 8px;
    font-weight: 500;
  }
  </style>
  