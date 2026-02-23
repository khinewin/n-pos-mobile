<template lang="">
    <div class="overflow-x-auto">                                                     
        <table class="table table-bordered border-dark table-hover mb-0 inside-table">
            <tbody class="">
                <tr class="text-center fw-bold change-cursor">
                    <td class="col-1"  @click="sortProducts('code_no')"> Code</td>
                    <td class="col-2"  @click="sortProducts('item_name')"> Product Name</td>
                    <td class="col-1">Packing Size</td>
                    <!--<td class="col-1" @click="sortProducts('main_qty')" v-if="location =='All' || location=='Main'">Main Inv Bal</td>-->
                    <td class="col-1" @click="sortProducts('qty')" v-if="location =='All' || location=='Mart'"> Mart Inv Bal</td>
                    <td class="col-1"  v-if="pur_price"  @click="sortProducts('pur_price')"> Pur Price</td>
                    <td class="col-1" v-if="sale_price"  @click="sortProducts('retail_price')"> Sale Price</td>
                    <td class="col-1" v-if="pur_price" @click="sortProducts('total_pur_price')"> Total Pur</td>
                    <td class="col-1" v-if="sale_price" @click="sortProducts('total_sale_price')"> Total Sale</td>
                </tr>   
            </tbody>  
            <tbody v-if="reports.length > 0" >                                                  
                <tr class=""  v-for="(p, i) in reports" :key="p.id" >                                    
                    <td class="text-center">{{p.code_no}}</td>
                    <td >{{p.item_name}}</td>
                    <td class="">
                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                    </td>
                    <!--<td class="text-end"  v-if="location =='All' || location=='Main'">
                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.main_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                    </td>-->
                    <td class="text-end" v-if="location =='All' || location=='Mart'">
                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                    </td>
                    <td class="text-end"  v-if="pur_price"  >
                        {{displayPrice(p.pur_price.largest_unit, p.pur_price.medium_unit, p.pur_price.smallest_unit)}}
                    </td>
                    <td class="text-end"  v-if="sale_price" >
                            {{displayPrice(p.retail_price.largest_unit, p.retail_price.medium_unit, p.retail_price.smallest_unit)}}
                    </td>
                    <!--<td class="text-end"  v-if="sale_price" >
                            {{displayPrice(p.whole_sale_price.largest_unit, p.whole_sale_price.medium_unit, p.whole_sale_price.smallest_unit)}}
                    </td>-->
                    <td class="text-end"  v-if="pur_price">
                        {{fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.pur_price?.smallest_unit || 0))}}
                    </td>
                    <td class="text-end"  v-if="sale_price">
                        {{fM((location === 'Main' ? (p.main_qty || 0) : (p.qty || 0)) * (p.retail_price?.smallest_unit || 0))}}
                    </td>                                                
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" class="table-group-divider">
                <tr class="fw-bold text-end">
                    <td :colspan="3 + (location === 'All' || location === 'Mart' ? 1 : 0) + (pur_price ? 1 : 0) + (sale_price ? 1 : 0)">
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
                    <div class="spinner-border text-primary"  role="status">
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
    /*created() {     
        if(this.user !==null){
            this.isLoading=true;
            this.fetchProducts();
        }
    },*/
    mounted() {
        this.fetchProducts();
    },
   
    computed:{
        user(){
            return this.$store.getters.user;
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

            // Always order and limit
            queryConstraints.push(orderBy("packing_size.smallest_qty", "desc"));
            queryConstraints.push(limit(this.perPage));

            // Filter by code_no (skip pagination in this case)
            if (this.code_no) {
                queryConstraints = [
                    where("code_no", "==", Number(this.code_no))
                ];
            }

            // If loadMore is requested and not filtering by code_no
            if (this.loadMore && this.lastDoc && !this.code_no) {
                const lastDoc = toRaw(this.lastDoc);
                queryConstraints.push(startAfter(lastDoc));
            }

            // Build query
            const queryRef = query(collection(db, "products"), ...queryConstraints);

            try {
                const snapshot = await getDocs(queryRef);

                if (!snapshot.empty) {
                    const newProducts = snapshot.docs.map(doc => ({
                        id: doc.id,
                        ...doc.data()
                    }));

                    // Update lastDoc only when not filtering by code_no
                    if (!this.code_no) {
                        this.lastDoc = snapshot.docs[snapshot.docs.length - 1];
                    }

                    // Merge or replace reports
                    this.reports = this.loadMore && !this.code_no
                        ? [...this.reports, ...newProducts]
                        : newProducts;

                    // End of pagination check
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
                            return qty_b - qty_a;
                        break;
                        case "main_qty":
                            return b.main_qty - a.main_qty;
                        break;
                        case "item_per_package":
                            return b.item_per_package - a.item_per_package;
                        break;
                        case "item_name":
                          return item_a < item_b ? 1 : -1;
                        break;
                        case "code_no":
                            return b.code_no - a.code_no;
                        break;
                        case "retail_price":
                            return b.retail_price.smallest_unit - a.retail_price.smallest_unit;
                        break;
                        case "whole_sale_price":
                           return  b.whole_sale_price.smallest_unit - a.whole_sale_price.smallest_unit;
                        break;
                        case "pur_price":
                           return  b.pur_price.smallest_unit - a.pur_price.smallest_unit;
                        break;
                        case "total_pur_price":
                            return (qty_b * b.pur_price.smallest_unit) - (qty_a * a.pur_price.smallest_unit);
                        break;
                        case "total_sale_price":
                            return (qty_b * b.retail_price.smallest_unit) - (qty_a * a.retail_price.smallest_unit);
                        break;
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
                            return qty_a - qty_b;
                        break;
                        case "main_qty":
                            return a.main_qty - b.main_qty;
                        break;
                        case "item_per_package":
                            return a.item_per_package - b.item_per_package;
                        break;
                        case "item_name":
                            return item_a < item_b ? -1 : 1;
                        break;
                        case "code_no":
                            return a.code_no - b.code_no;
                        break;
                        case "retail_price":
                            return a.retail_price.smallest_unit - b.retail_price.smallest_unit;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price.smallest_unit - b.whole_sale_price.smallest_unit;
                        break;
                        case "pur_price":
                           return  a.pur_price.smallest_unit - b.pur_price.smallest_unit;
                        break;
                        case "total_pur_price":
                            return (qty_a * a.pur_price.smallest_unit) - (qty_b * b.pur_price.smallest_unit);
                        break;
                        case "total_sale_price":
                            return (qty_a * a.retail_price.smallest_unit) - (qty_b * b.retail_price.smallest_unit);
                        break;
                    }
                })
            }            
        },
       
     
    }
}
</script>
<style>
  
</style>