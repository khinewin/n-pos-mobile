<template lang="">
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-body p-0 table-responsive">
                        <table class="table table-bordered border-dark table-hover mb-0">
                            <tbody class="">
                                <tr class="text-center fw-bold">
                                    <td> Code</td>
                                    <td > Description</td>
                                    <td colspan="2"> Qty</td>
                                    <td v-if="sale_price"> Retail price</td>
                                    <td v-if="sale_price"> Wholesale price</td>
                                    <td  v-if="pur_price"> Pur price</td>
                                </tr>
                                                       
                                <tr class="text-center" v-if="reports.length > 0"  v-for="(p, i) in reports" :key="p.id" >
                                    
                                    <td >{{p.code_no}}</td>
                                    <td >{{p.item_name}}</td>
                                    <td >{{p.qty}}</td>
                                    <td>{{Math.floor(p.qty / p.item_per_package)}} x {{Math.floor(p.qty % p.item_per_package)}}</td>                                        

                                    <td v-if="sale_price">{{p.retail_price}}</td>
                                    <td v-if="sale_price">{{p.whole_sale_price}}</td>
                                    <td v-if="pur_price">{{p.pur_price}}</td>
                                
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
        </div>
    </div>
</template>
<script>
    import {Timestamp, collection, addDoc, query, where, getDocs, doc, updateDoc, orderBy, deleteDoc} from 'firebase/firestore'
    import db from '@/firebase/firestore'
    export default {
    props: ['sale_price','pur_price',],
    data(){
        return {
            isLoading:false,
            reports :[],
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
        async fetchProducts(){                    
            const query_url = query(collection(db, "products"), where("uid", "==", this.user.uid), orderBy("created_at", "desc"));
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
    }
}
</script>
<style>
  
</style>