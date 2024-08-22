<template lang="">
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">               
                        <table class="table table-bordered border-dark table-hover mb-0">
                            <tbody class="">
                                <tr class="text-center fw-bold">
                                    <td class="col-1"> Code</td>
                                    <td class="col-2"> Description</td>
                                    <td class="col-1" colspan="2" @click="sortQty"><i class="fa-solid fa-sort"></i> Qty</td>
                                    <td class="col-1" v-if="sale_price"> Retail price</td>
                                    <td class="col-1" v-if="sale_price"> Wholesale price</td>
                                    <td class="col-1"  v-if="pur_price"> Pur price</td>
                                </tr>                                                       
                                <tr class="" v-if="reports.length > 0"  v-for="(p, i) in reports" :key="p.id" >                                    
                                    <td class="text-center">{{p.code_no}}</td>
                                    <td >{{p.item_name}}</td>
                                    <td class="text-center col-1">
                                        {{p.qty}} {{p.child_unit}}
                                    </td>
                                    <td class="text-center col-1">
                                        <span v-if="calQtyL(p.qty, p.item_per_package) > 0">{{calQtyL(p.qty, p.item_per_package)}} {{p.parent_unit}}</span>
                                        <span v-if="calQtyL(p.qty, p.item_per_package) > 0 && calQtyS(p.qty, p.item_per_package) > 0"> * </span>
                                        <span v-if="calQtyS(p.qty, p.item_per_package) > 0">{{calQtyS(p.qty, p.item_per_package)}} {{p.child_unit}}</span>

                                    </td>                                            
                                    <td v-if="sale_price" class="text-end">{{p.retail_price}}</td>
                                    <td v-if="sale_price" class="text-end">{{p.whole_sale_price}}</td>
                                    <td v-if="pur_price" class="text-end">{{p.pur_price}}</td>                                
                                </tr>
                            </tbody>
                        </table>
                        <div v-if="isLoading">
                            <div colspan="5">
                                <div class="d-flex justify-content-center my-5">
                                    <div class="spinner-border text-primary"  role="status">
                                      <span class="visually-hidden">Loading...</span>
                                    </div>
                                </div>
                            </div>
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
    props: ['sale_price','pur_price',],
    data(){
        return {
            isLoading:false,
            reports :[],
            sort:"asc"
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
        sortQty(){
            if(this.sort==="asc"){
                this.reports=this.reports.sort((a,b)=>{
                        return a.qty - b.qty;
                })
                this.sort="desc"
            }else{
                this.reports=this.reports.sort((a,b)=>{
                        return a.qty + b.qty;
                })
                this.sort="asc"
            }
           
        },
        calQtyL(qty,ipp){
            return hp.calQtyL(qty,ipp)
        },
        calQtyS(qty, ipp){
            return hp.calQtyS(qty,ipp)
        },
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            const snapShot=await getDocs(query_url)           
            if(snapShot._snapshot.docChanges.length <=0){
                this.msg.type='error' 
                this.msg.info='Oops..., something went wrong!'
                this.isLoading=false;
                this.isUpdating=false;
                return false;
            }                             
           else{
                const products=lib.products(snapShot)
                this.products=[];
                this.reports=products;
                this.isLoading=false;
           }
           
       } ,
       
        
        /*
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), orderBy("created_at", "desc"));
            const snapShot=await getDocs(query_url)           
            if(snapShot._snapshot.docChanges.length <=0){
                this.msg.type='error' 
                this.msg.info='Oops..., something went wrong!'
                this.isLoading=false;
                this.isUpdating=false;
                return false;
            }
            let products=[];
            snapShot && snapShot.forEach((doc) => {         
                let product = {
                    id: doc.id,
                    uid: doc.data().uid,
                    item_name: doc.data().item_name,
                    purchase_price : doc.data().purchase_price,
                    retail_price : doc.data().retail_price,
                    whole_sale_price: doc.data().whole_sale_price,
                    qty : doc.data().qty,
                    created_at : doc.data().created_at,
                    code_no : doc.data().code_no,                
                    item_per_package: doc.data().item_per_package,
                    pur_price:doc.data().pur_price,       
                };                                
                products.push(product);                           
                            
            });
            this.products=[];
            this.reports=products;
            this.isLoading=false;
       } ,
        */
    }
}
</script>
<style>
  
</style>