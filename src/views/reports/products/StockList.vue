<template lang="">
    <div class="overflow-x-auto">                                                     
        <table class="table table-bordered border-dark table-hover mb-0 inside-table">
            <tbody class="">
                <tr class="text-center fw-bold">
                    <td class="col-1"  @click="sortProducts('code_no')"> Code</td>
                    <td class="col-2"  @click="sortProducts('item_name')"> Product Name</td>
                    <td class="col-1">Packing Size</td>
                    <!--<td class="col-1" @click="sortProducts('main_qty')" v-if="location =='All' || location=='Main'">Main Inv Bal</td>-->
                    <td class="col-1" @click="sortProducts('qty')" v-if="location =='All' || location=='Mart'"> Mart Inv Bal</td>
                    <td class="col-1"  v-if="pur_price"  @click="sortProducts('pur_price')"> Pur Price</td>
                    <td class="col-1" v-if="sale_price"  @click="sortProducts('retail_price')"> Sale Price</td>
                    <!--<td class="col-1" v-if="sale_price"  @click="sortProducts('whole_sale_price')"> Wholesale Price</td>-->
                </tr>   
            </tbody>  
            <tbody>                                                  
                <tr class="" v-if="reports.length > 0"  v-for="(p, i) in reports" :key="p.id" >                                    
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
                            {{p.retail_price.smallest_unit}}-{{p.packing_size.smallest_unit}}
                    </td>
                    <!--<td class="text-end"  v-if="sale_price" >
                            {{displayPrice(p.whole_sale_price.largest_unit, p.whole_sale_price.medium_unit, p.whole_sale_price.smallest_unit)}}
                    </td>-->
                                                    
                </tr>
            </tbody>
        </table>
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
    import {Timestamp, onSnapshot, collection, addDoc, query, where, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    import * as lib from "@/lib/snapshot/products"
    import * as hp from '@/lib/helper'
    export default {
    props: ['sale_price','pur_price', 'location', 'code_no', 'gt_equal', 'lt_equal', 'packing_unit'],
    data(){
        return {
            isLoading:false,
            reports :[],
            sortBy:"asc"
        }
    },
    /*created() {     
        if(this.user !==null){
            this.isLoading=true;
            this.fetchProducts();
        }
    },*/
    mounted() {
        this.isLoading=true;
        if(this.storeProducts.length){
            if(this.code_no){
                this.reports=this.storeProducts.filter((f)=>f.code_no==this.code_no);
            }else{
                this.reports=this.storeProducts.slice(0, 20);
            }
            this.isLoading=false;
        }
    },
    watch:{
        storeProducts:{
            handler(val){
               if(val){
                    if(this.code_no){
                        this.reports=val.filter((f)=>f.code_no==this.code_no);
                    }else{
                        this.reports=val.slice(0, 20);
                    }
                   this.isLoading=false;
                   this.filterProduct();
               }
            }, deep: true
       },
    },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        storeProducts(){
            return this.$store.getters.products;
        }
    },
    methods: {
        displayPrice(l_unit, m_unit, s_unit){
            return hp.displayPrice(l_unit, m_unit, s_unit);
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
        async fetchProducts(){   
            let query_url="";           
            if(this.code_no){
                query_url = query(collection(db, "products"), where("code_no", "==", Number(this.code_no)), orderBy("created_at", "desc"));
            }else{
                 query_url = query(collection(db, "products"), where("is_deleted", "==", false), orderBy("created_at", "desc"));
            }
           onSnapshot(query_url, (snapShot)=>{
            const products=lib.products(snapShot)
                this.reports=products;
                this.isLoading=false;
                this.filterProduct();
           })                                 
       } ,
       sortProducts(sort){   
            this.sort_item=sort;
            if(this.sortBy=="asc"){
                this.sortBy="desc"
                this.reports=this.reports.sort((a,b)=>{
                    const item_a=a.item_name.toUpperCase();
                    const item_b=b.item_name.toUpperCase();
                    switch(sort){
                        case "qty":
                            return a.qty - b.qty;
                        break;
                        case "main_qty":
                            return a.main_qty - b.main_qty;
                        break;
                        case "item_per_package":
                            return a.item_per_package - b.item_per_package;
                        break;
                        case "item_name":
                          return item_a < item_b ? 1 : -1;
                        break;
                        case "code_no":
                            return a.code_no - b.code_no;
                        break;
                        case "retail_price":
                            return a.retail_price.largest_unit - b.retail_price.largest_unit;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price.largest_unit - b.whole_sale_price.largest_unit;
                        break;
                        case "pur_price":
                           return  a.pur_price.largest_unit - b.pur_price.largest_unit;
                        break;
                    }
                })
            }else{
                this.sortBy="asc"
                this.reports=this.reports.sort((a,b)=>{
                    const item_a=a.item_name.toUpperCase();
                    const item_b=b.item_name.toUpperCase();
                    switch(sort){
                        case "qty":
                            return a.qty + b.qty;
                        break;
                        case "main_qty":
                            return a.main_qty + b.main_qty;
                        break;
                        case "item_per_package":
                            return a.item_per_package + b.item_per_package;
                        break;
                        case "item_name":
                            return item_a < item_b ? -1 : 1;
                        break;
                        case "code_no":
                            return a.code_no + b.code_no;
                        break;
                        case "retail_price":
                            return a.retail_price.largest_unit + b.retail_price.largest_unit;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price.largest_unit+ b.whole_sale_price.largest_unit;
                        break;
                        case "pur_price":
                           return  a.pur_price.largest_unit + b.pur_price.largest_unit;
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