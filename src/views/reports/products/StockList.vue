<template lang="">
    <div class="overflow-x-auto">                                                     
        <table class="table table-bordered border-dark table-hover mb-0 inside-table">
            <tbody class="">
                <tr class="text-center fw-bold change-cursor">
                    <td class="col-1" v-if="hasCategory">Category</td>
                    <td class="col-1" @click="sortProducts('code_no')"> Code</td>
                    <td class="col-3" @click="sortProducts('item_name')"> Product Name</td>
                    <td class="col-1">Packing Size</td>
                    <!--<td class="col-1" @click="sortProducts('main_qty')" v-if="location =='All' || location=='Main'">Main Inv Bal</td>-->
                    <td class="col-1" @click="sortProducts('qty')" v-if="location =='All' || location=='Mart'"> Mart Inv Bal</td>
                    <td class="col-1" v-if="pur_price" @click="sortProducts('pur_price')"> Pur Price</td>
                    <td class="col-1" v-if="sale_price" @click="sortProducts('retail_price')"> Sale Price</td>
                    <td class="col-1" v-if="pur_price" @click="sortProducts('total_pur_price')"> Total Pur</td>
                    <td class="col-1" v-if="sale_price" @click="sortProducts('total_sale_price')"> Total Sale</td>
                </tr>   
            </tbody>  

            <!-- Grouped by Category -->
            <tbody v-if="hasCategory && reports.length > 0" v-for="g in categoryGroups" :key="g.catName" class="table-group-divider">
                <tr v-for="(p, index) in g.items" :key="p.id">
                    <td v-if="index === 0" :rowspan="g.items.length + (pur_price || sale_price ? 1 : 0)" class="text-center fw-bold align-top">{{g.catName}}</td>
                    <td class="text-center">{{p.code_no}}</td>
                    <td>{{p.item_name}}</td>
                    <td class="">
                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                    </td>
                    <td class="text-end" v-if="location =='All' || location=='Mart'">
                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                    </td>
                    <td class="text-end" v-if="pur_price">
                        {{displayPrice(p.pur_price.largest_unit, p.pur_price.medium_unit, p.pur_price.smallest_unit)}}
                    </td>
                    <td class="text-end" v-if="sale_price">
                        {{displayPrice(p.retail_price.largest_unit, p.retail_price.medium_unit, p.retail_price.smallest_unit)}}
                    </td>
                    <td class="text-end" v-if="pur_price">
                        {{fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.pur_price?.smallest_unit || 0))}}
                    </td>
                    <td class="text-end" v-if="sale_price">
                        {{fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.retail_price?.smallest_unit || 0))}}
                    </td>
                </tr>
                <tr v-if="pur_price || sale_price" class="fw-bold text-end">
                    <td :colspan="3 + (location === 'All' || location === 'Mart' ? 1 : 0) + (pur_price ? 1 : 0) + (sale_price ? 1 : 0)">
                        Subtotal ({{g.catName}})
                    </td>
                    <td v-if="pur_price">{{fM(g.pur_amount)}}</td>
                    <td v-if="sale_price">{{fM(g.sale_amount)}}</td>
                </tr>
            </tbody>

            <!-- Flat List when no Category -->
            <tbody v-if="!hasCategory && reports.length > 0">
                <tr class="" v-for="(p, i) in reports" :key="p.id">                                    
                    <td class="text-center">{{p.code_no}}</td>
                    <td>{{p.item_name}}</td>
                    <td class="">
                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                    </td>
                    <!--<td class="text-end" v-if="location =='All' || location=='Main'">
                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.main_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                    </td>-->
                    <td class="text-end" v-if="location =='All' || location=='Mart'">
                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                    </td>
                    <td class="text-end" v-if="pur_price">
                        {{displayPrice(p.pur_price.largest_unit, p.pur_price.medium_unit, p.pur_price.smallest_unit)}}
                    </td>
                    <td class="text-end" v-if="sale_price">
                        {{displayPrice(p.retail_price.largest_unit, p.retail_price.medium_unit, p.retail_price.smallest_unit)}}
                    </td>
                    <!--<td class="text-end" v-if="sale_price">
                            {{displayPrice(p.whole_sale_price.largest_unit, p.whole_sale_price.medium_unit, p.whole_sale_price.smallest_unit)}}
                    </td>-->
                    <td class="text-end" v-if="pur_price">
                        {{fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.pur_price?.smallest_unit || 0))}}
                    </td>
                    <td class="text-end" v-if="sale_price">
                        {{fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.retail_price?.smallest_unit || 0))}}
                    </td>                                                
                </tr>
            </tbody>

            <tbody v-if="reports.length > 0" class="table-group-divider">
                <tr class="fw-bold text-end">
                    <td :colspan="(hasCategory ? 1 : 0) + 3 + (location === 'All' || location === 'Mart' ? 1 : 0) + (pur_price ? 1 : 0) + (sale_price ? 1 : 0)">
                        Grand Total (Ks)
                    </td>
                    <td v-if="pur_price">{{fM(total_pur_amount)}}</td>
                    <td v-if="sale_price">{{fM(total_sale_amount)}}</td>
                </tr>
            </tbody>
        </table>
        <div class="d-grid mt-2" v-if="reports.length > 0 && isLoading===false && !this.code_no">
            <button class="btn btn-primary" :disabled="isEndPagination" @click="loadMore=true, fetchProducts()"> (Current : {{reports.length}}) Load More</button>
        </div>
        <div v-if="isLoading">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary" role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
        </div>  
    </div>
</template>
<script>
    import {Timestamp, onSnapshot, collection, addDoc, query, limit,startAfter, where, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    import * as lib from "@/lib/snapshot/products"
    import * as hp from '@/lib/helper'
    import {toRaw} from 'vue'

    export default {
    props: ['sale_price','pur_price', 'location', 'code_no', 'gt_equal', 'lt_equal', 'packing_unit'],
    data(){
        return {
            isLoading:false,
            reports :[],
            sortBy:"asc",
            perPage: 500,
            loadMore: false,
            lastDoc:null,
            isEndPagination:false
        }
    },
    mounted() {
        this.fetchProducts();
    },
   
    computed:{
        user(){
            return this.$store.getters.user;
        },
        hasCategory() {
            return this.reports.some(p => (p.category?.cat_name || p.category_name || (typeof p.category === 'string' && p.category)) && String(p.category?.cat_name || p.category_name || p.category).trim() !== '');
        },
        categoryGroups() {
            if (!this.hasCategory) return [];

            const groups = {};
            this.reports.forEach(p => {
                const cat = p.category?.cat_name || p.category_name || (typeof p.category === 'string' ? p.category : '') || 'No Category';
                if (!groups[cat]) {
                    groups[cat] = [];
                }
                groups[cat].push(p);
            });

            return Object.entries(groups).map(([catName, items]) => {
                const pur_amount = items.reduce((total, p) => {
                    const qty = this.location === 'Main' ? (p.main_qty || 0) : (p.qty || 0);
                    return total + (qty * (p.pur_price?.smallest_unit || 0));
                }, 0);
                const sale_amount = items.reduce((total, p) => {
                    const qty = this.location === 'Main' ? (p.main_qty || 0) : (p.qty || 0);
                    return total + (qty * (p.retail_price?.smallest_unit || 0));
                }, 0);
                return { catName, items, pur_amount, sale_amount };
            }).sort((a, b) => a.catName.toUpperCase().localeCompare(b.catName.toUpperCase()));
        },
        total_pur_amount() {
            return this.reports.reduce((total, p) => {
                const qty = this.location === 'Main' ? (p.main_qty || 0) : (p.qty || 0);
                return total + (qty * (p.pur_price?.smallest_unit || 0));
            }, 0);
        },
        total_sale_amount() {
            return this.reports.reduce((total, p) => {
                const qty = this.location === 'Main' ? (p.main_qty || 0) : (p.qty || 0);
                return total + (qty * (p.retail_price?.smallest_unit || 0));
            }, 0);
        },
    },
    beforeDestroy() {
      if (this.unsubscribe) this.unsubscribe();
    },
    methods: {
        displayPrice(l_unit, m_unit, s_unit){
            return hp.displayPrice(l_unit, m_unit, s_unit);
        },
        fM(amount) {
            return hp.formattedAmount(amount);
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
        },
       
       filterProduct(){
        if(this.gt_equal && this.lt_equal){
                if(this.location=="Main"){
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty >= Number(this.gt_equal) && f.main_qty <= Number(this.lt_equal)
                    })
                }else if(this.location=="Mart"){
                    this.reports=this.reports.filter((f)=>{
                        return f.qty >= Number(this.gt_equal) && f.qty <= Number(this.lt_equal)
                    })
                }else{                
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty >= Number(this.gt_equal) && f.main_qty <= Number(this.lt_equal) ||
                        f.qty >= Number(this.gt_equal) && f.qty <= Number(this.lt_equal)
                    })  
                }
            }else
            if(this.gt_equal){
                if(this.location=="Main"){
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty >= Number(this.gt_equal)
                    })
                }else if(this.location=="Mart"){
                    this.reports=this.reports.filter((f)=>{
                        return f.qty >= Number(this.gt_equal)
                    })
                }else{
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty >= Number(this.gt_equal) || f.qty >= Number(this.gt_equal)
                    })
                }
            }else
            if(this.lt_equal){                
                if(this.location=="Main"){
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty <= Number(this.lt_equal)
                    })
                }else if(this.location=="Mart"){
                    this.reports=this.reports.filter((f)=>{
                        return f.qty <= Number(this.lt_equal)
                    })
                }else{
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty <= Number(this.lt_equal) || f.qty <= Number(this.lt_equal)
                    })
                }
            }
       },
       async fetchProducts() {
            if (this.isLoading) return;

            this.isLoading = true;

            let queryConstraints = [];

            queryConstraints.push(orderBy("packing_size.smallest_qty", "desc"));
            queryConstraints.push(limit(this.perPage));

            if (this.code_no) {
                queryConstraints = [
                    where("code_no", "==", this.code_no)
                ];
            }

            if (this.loadMore && this.lastDoc && !this.code_no) {
                const lastDoc = toRaw(this.lastDoc);
                queryConstraints.push(startAfter(lastDoc));
            }

            const queryRef = query(collection(db, "products"), ...queryConstraints);

            try {
                const snapshot = await getDocs(queryRef);

                if (!snapshot.empty) {
                    const newProducts = snapshot.docs.map(doc => ({
                        id: doc.id,
                        ...doc.data()
                    }));

                    if (!this.code_no) {
                        this.lastDoc = snapshot.docs[snapshot.docs.length - 1];
                    }

                    this.reports = this.loadMore && !this.code_no
                        ? [...this.reports, ...newProducts]
                        : newProducts;

                    if (newProducts.length < this.perPage) {
                        this.isEndPagination = true;
                    }
                } else {
                    this.isEndPagination = true;
                }
            } catch (error) {
                console.error("Failed to fetch products:", error);
            } finally {
                this.isLoading = false;
            }
        },
        sortProducts(sort){   
            this.sort_item=sort;
            if(this.sortBy=="asc"){
                this.sortBy="desc"
                this.reports=this.reports.sort((a,b)=>{
                    const item_a=a.item_name.toUpperCase();
                    const item_b=b.item_name.toUpperCase();
                    const qty_a = this.location === 'Main' ? a.main_qty : a.qty;
                    const qty_b = this.location === 'Main' ? b.main_qty : b.qty;
                    switch(sort){
                        case "qty":
                            return qty_a - qty_b;
                        case "code_no":
                            return a.code_no - b.code_no;
                        case "item_name":
                            return item_a.localeCompare(item_b);
                        case "pur_price":
                            return a.pur_price.smallest_unit - b.pur_price.smallest_unit;
                        case "retail_price":
                            return a.retail_price.smallest_unit - b.retail_price.smallest_unit;
                        case "total_pur_price":
                            return (qty_a * a.pur_price.smallest_unit) - (qty_b * b.pur_price.smallest_unit);
                        case "total_sale_price":
                            return (qty_a * a.retail_price.smallest_unit) - (qty_b * b.retail_price.smallest_unit);
                        default:
                            return 0;
                    }
                })
            }else{
                this.sortBy="asc"
                this.reports=this.reports.sort((a,b)=>{
                    const item_a=a.item_name.toUpperCase();
                    const item_b=b.item_name.toUpperCase();
                    const qty_a = this.location === 'Main' ? a.main_qty : a.qty;
                    const qty_b = this.location === 'Main' ? b.main_qty : b.qty;
                    switch(sort){
                        case "qty":
                            return qty_b - qty_a;
                        case "code_no":
                            return b.code_no - a.code_no;
                        case "item_name":
                            return item_b.localeCompare(item_a);
                        case "pur_price":
                            return b.pur_price.smallest_unit - a.pur_price.smallest_unit;
                        case "retail_price":
                            return b.retail_price.smallest_unit - a.retail_price.smallest_unit;
                        case "total_pur_price":
                            return (qty_b * b.pur_price.smallest_unit) - (qty_a * a.pur_price.smallest_unit);
                        case "total_sale_price":
                            return (qty_b * b.retail_price.smallest_unit) - (qty_a * a.retail_price.smallest_unit);
                        default:
                            return 0;
                    }
                })
            }
        }
    }
}
</script>
<style lang="">
    
</style>