<template lang="">
    <div class="">                                                     
        <table class="table table-bordered border-dark table-hover mb-0">
            <tbody class="">
                <tr class="text-center fw-bold">
                    <td class="col-1"  @click="sortProducts('code_no')"> Code</td>
                    <td class="col-2"  @click="sortProducts('item_name')"> Description</td>
                    <td class="col-1">Packing size</td>
                    <td class="col-1" @click="sortProducts('main_qty')" v-if="location =='All' || location=='Main'">Main Qty</td>
                    <td class="col-1" @click="sortProducts('qty')" v-if="location =='All' || location=='Shop'"> Shop Qty</td>
                    <td class="col-1" v-if="sale_price"  @click="sortProducts('retail_price')"> Retail price</td>
                    <td class="col-1" v-if="sale_price"  @click="sortProducts('whole_sale_price')"> Wholesale price</td>
                    <td class="col-1"  v-if="pur_price"  @click="sortProducts('pur_price')"> Pur price</td>
                </tr>   
            </tbody>  
            <tbody>                                                  
                <tr class="" v-if="reports.length > 0"  v-for="(p, i) in reports" :key="p.id" >                                    
                    <td class="text-center">{{p.code_no}}</td>
                    <td >{{p.item_name}}</td>
                    <td>
                        1{{p.parent_unit}} * {{p.item_per_package}}{{p.child_unit}}
                    </td>
                    <td class="text-end" v-if="location =='All' || location=='Main'">
                        <span v-if="calQtyL(p.main_qty, p.item_per_package) > 0">{{calQtyL(p.main_qty, p.item_per_package)}}{{p.parent_unit}}</span>
                        <span v-if="calQtyL(p.main_qty, p.item_per_package) > 0 && calQtyS(p.main_qty, p.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(p.main_qty, p.item_per_package) > 0">{{calQtyS(p.main_qty, p.item_per_package)}}{{p.child_unit}}</span>
                    </td>
                    <td class="text-end" v-if="location =='All' || location=='Shop'">
                        <span v-if="calQtyL(p.qty, p.item_per_package) > 0">{{calQtyL(p.qty, p.item_per_package)}}{{p.parent_unit}}</span>
                        <span v-if="calQtyL(p.qty, p.item_per_package) > 0 && calQtyS(p.qty, p.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(p.qty, p.item_per_package) > 0">{{calQtyS(p.qty, p.item_per_package)}}{{p.child_unit}}</span>
                    </td>                                            
                    <td v-if="sale_price" class="text-end">{{p.retail_price}}</td>
                    <td v-if="sale_price" class="text-end">{{p.whole_sale_price}}</td>
                    <td v-if="pur_price" class="text-end">{{p.pur_price}}</td>                                
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
    import {Timestamp, collection, addDoc, query, where, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    import * as lib from "@/lib/snapshot/products"
    import * as hp from '@/lib/helper'
    export default {
    props: ['sale_price','pur_price', 'location', 'code_no', 'gt_equal', 'lt_equal'],
    data(){
        return {
            isLoading:false,
            reports :[],
            sortBy:"asc"
        }
    },
    created() {     
        if(this.user !==null){
            this.isLoading=true;
            this.fetchProducts();
        }
    },
    watch:{
        user(data){
                if(data !==null){
                    this.isLoading=true;
                    this.fetchProducts();
                }
            }
        },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods: {
        
        calQtyL(qty,ipp){
            return hp.calQtyL(qty,ipp)
        },
        calQtyS(qty, ipp){
            return hp.calQtyS(qty,ipp)
        },
       
       filterProduct(){
        if(this.gt_equal && this.lt_equal){
                if(this.location=="Main"){
                    this.reports=this.reports.filter((f)=>{
                        return f.main_qty >= Number(this.gt_equal) && f.main_qty <= Number(this.lt_equal)
                    })
                }else if(this.location=="Shop"){
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
                }else if(this.location=="Shop"){
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
                }else if(this.location=="Shop"){
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
                 query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            }
            const snapShot=await getDocs(query_url)           
            if(snapShot._snapshot.docChanges.length <=0){             
                this.isLoading=false;
                this.isUpdating=false;
                return false;
            }                             
           else{
                const products=lib.products(snapShot)
                this.reports=[];
                this.reports=products;
                this.isLoading=false;
                this.filterProduct();
           }
           
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
                            return a.retail_price - b.retail_price;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price - b.whole_sale_price;
                        break;
                        case "pur_price":
                           return  a.pur_price - b.pur_price;
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
                            return a.retail_price + b.retail_price;
                        break;
                        case "whole_sale_price":
                           return  a.whole_sale_price + b.whole_sale_price;
                        break;
                        case "pur_price":
                           return  a.pur_price + b.pur_price;
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