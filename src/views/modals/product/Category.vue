<template>
    <!-- selectCategory Modal -->
    <div class="modal fade" id="categoryModal" data-bs-backdrop="static" tabindex="-1" aria-labelledby="categoryModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered modal-sm">
            <div class="modal-content border-0 shadow-lg ripple-container">
                <!-- Modern Header -->
                <div class="modal-header bg-white border-bottom-0 pb-0">
                    <h5 class="modal-title fw-bold text-primary" id="categoryModalLabel">
                        <i class="fa-solid fa-tags me-2"></i>Select Category
                    </h5>
                    <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>

                <div class="modal-body p-4">
                    <!-- Search Section -->
                    <div class="card bg-light border-0 rounded-3 mb-2">
                        <div class="card-body p-2">
                            <div class="input-group shadow-sm rounded-3 overflow-hidden">
                                <span class="input-group-text bg-white border-0">
                                    <i class="fa-solid fa-magnifying-glass text-muted"></i>
                                </span>
                                <input 
                                    @input="handleKeyup" 
                                    v-model="search_category" 
                                    type="search" 
                                    class="form-control border-0 bg-white py-2" 
                                    placeholder="Search categories...">
                            </div>
                        </div>
                    </div>

                    <!-- Category List -->
                    <div class="vh-50 overflow-y-auto custom-scrollbar pe-2">
                        <div v-if="cats.length === 0" class="text-center text-muted py-5">
                            <i class="fa-solid fa-folder-open fa-3x mb-3 opacity-25"></i>
                            <p class="mb-0">No categories found</p>
                        </div>

                        <div v-else>
                            <table class="table table-hover align-middle border-0">
                                <thead class="sticky-top bg-white z-3">
                                    <tr class="small text-muted text-uppercase fw-bold border-bottom">
                                        <th class="border-0 px-0 ps-2">Category Name</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="c in cats" :key="c.id" @click="setCode(c)" data-bs-dismiss="modal" class="border-0 category-row cursor-pointer">
                                        <td class="p-2">
                                            <div class="fw-medium text-dark ps-2 category-name">
                                                {{c.cat_name}}
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { onSnapshot, collection, query, orderBy } from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as cat from "@/lib/snapshot/category"
import * as hp from "@/lib/helper"

export default {
    props: ['ei'],
    data() {
        return {
            cats: [],
            catsData: [],
            search_category: "",
            isLoading: true
        }
    },
    mounted() {
        this.fetchCategory();
    },
    computed: {
        user() {
            return this.$store.getters.user;
        },
    },
    methods: {
        setCode(c) {
            this.$emit("setCode", c);
        },
        handleKeyup(e) {
            this.search_category = e.target.value;
            this.searchCategory();
        },
        searchCategory() {
            if (this.search_category) {
                this.cats = this.catsData.filter((f) => {
                    return f.cat_name.toLowerCase().includes(this.search_category.toLowerCase())
                })
            } else {
                this.cats = this.catsData;
            }
        },
        async fetchCategory() {
            const query_url = query(collection(db, "category"), orderBy("created_at", "desc"));
            onSnapshot(query_url, async (snapShot) => {
                const cats = cat.category(snapShot);
                this.catsData = cats;
                this.isLoading = false;
                this.searchCategory();
            })
        },
        showTime(t) {
            return hp.showTime(t)
        },
    }
}
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
    width: 4px;
}
.custom-scrollbar::-webkit-scrollbar-track {
    background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
    background: #e0e0e0;
    border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
    background: #cccccc;
}

.category-row:hover .category-name {
    color: var(--bs-primary) !important;
}

.category-name {
    transition: color 0.2s;
}

.cursor-pointer {
    cursor: pointer;
}
</style>