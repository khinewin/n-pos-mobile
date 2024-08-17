<template lang="">
    <div>
        <PageHeader page-title="Purchases" page-code="addPurchase" :msg-type="msg.type" :msg-info="msg.info" />
        <PurchaseModal @setCode="setCode"/>           
        <div class="container-fluid my-2 ">
            <div class="row mb-2 justify-content-end">
                <div class="col-lg-2">
                    <button @click="goEdit" class='btn btn-sm' :disabled="!voucher_no"><i class="fa-solid fa-pen-to-square"></i> Edit</button>
                </div>
                <div class="col-lg-2">
                    <button class="btn btn-sm" data-bs-toggle="modal" data-bs-target="#purchaseModal"><i class="fa-solid fa-folder-tree"></i> Browse</button>
                </div>
                <div class="col-lg-2">
                    <div>
                        <router-link :to="{name : 'AddPurchase'}" class="btn btn-sm"><i class="fa-solid fa-square-plus"></i> New</router-link>
                    </div>
                </div>
                <div class="col-lg-2">
                    <div>
                        <button class="btn btn-sm" :disabled="!voucher_no" @click="closeVoucher"><i class="fa-solid fa-circle-xmark"></i> Close</button>
                    </div>
                </div>
            </div>
            <div>
                <div class="table-responsive">
                    <div class="row justify-content-end mb-3">
                    <div class="col-lg-4">
                        <table class="table mb-0 table-borderless">
                            <tbody>
                                <tr>
                                    <td class="text-end col-4">Voucher no. :</td>
                                    <td class="col-8">
                                        {{voucher_no}}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text-end">Pur date :</td>
                                    <td class="" v-if="pur_date">
                                        {{showTime(pur_date)}}
                                    </td>
                                </tr>
                                <tr>
                                    <td class="text-end">Supplier :</td>
                                    <td class="">
                                        {{supplier_name}}
                                    </td>
                                </tr>
                            </tbody>
                      </table>
                    </div>
                </div>                 
                   <table class="table table-bordered  table-hover">
                        <tbody>
                            <tr class="small text-center fw-bold table-secondary">
                                <td class="col-1">Code</td>
                                <td class="col-2">Desc</td>
                                <td class="col-1">Exp. date</td>
                                <td class="col-1">Qty</td>
                                <td class="col-1">Item price</td>
                                <td class="col-1">D(Qty)</td>
                                <td class="col-1">D(%)</td>
                                <td class="col-1">D($)</td>
                                <td class="col-1">Amount</td>
                                <td class="col-1">Discount</td>
                                <td class="col-1">Price</td>
                            </tr>
                            <tr class="small text-center" v-if="products.length > 0" v-for="st in products" :key="st.id">
                                <td class="">{{st.code_no}}</td>
                                <td class="">{{st.item_name}}</td>
                                <td class="">                                   
                                     <span v-if="st.exp_date">{{showTime(st.exp_date)}}</span>
                                </td>
                                <td class="">{{st.pur_qty}}</td>
                                <td class="">{{st.pur_price}}</td>
                                <td class="">{{st.dis_qty}}</td>
                                <td class="">{{st.dis_percent}}</td>
                                <td class="">{{st.dis_amount}}</td>
                                <td class="text-end">{{st.amount}}</td>
                                <td class="text-end">{{st.discount}}</td>
                                <td class="text-end">{{st.price}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="10" class="text-end">Amount</td>
                                <td class="text-end">{{this.total_amount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="10" class="text-end">Discount</td>
                                <td class="text-end ">{{this.total_discount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="10" class="text-end">Extra discount</td>
                                <td class="text-end">{{this.extra_discount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="10" class="text-end">Total price</td>
                                <td class="text-end">{{this.total_price}}</td>
                            </tr>                           
                        </tbody>
                   </table>     
                 
      
                </div>
            </div>
        </div>
        <Footer />
    </div>
</template>
<script>
 import PageHeader from "@/views/partials/PageHeader.vue"
 import { useVuelidate  } from '@vuelidate/core'
import { required, email ,helpers} from '@vuelidate/validators'
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, query, increment} from 'firebase/firestore'
import db from '@/firebase/firestore'
import PurchaseModal from '@/views/modals/purchase/PurchaseModal.vue'
import Footer from '@/views/partials/Footer.vue'

export default {
    components: {
        PageHeader, PurchaseModal, Footer
    },
   
    data(){
        return{ 
            id:"",      
            voucher_no:"", 
            pur_date:"",
            supplier : "",
            supplier_name:"",
            products:[],
            total_amount:0,
            total_discount:0,
            extra_discount:0,
            total_price:0,
            isLoading:false,
            msg : {
                type :"",
                info :"",
            }

        }
    },
  
    computed:{
        user(){
            return this.$store.getters.user;
        },    
      
    },
 
    methods:{     
        closeVoucher(){
            this.id="";    
            this.voucher_no=""; 
            this.pur_date=""; 
            this.supplier={}; 
            this.supplier_name=""; 
            this.products=[]
            this.total_amount=0
            this.total_discount=0
            this.extra_discount=0
            this.total_price=0
        },
        goEdit(){
            const purchaseItem={
                id:this.id,
                voucher_no:this.voucher_no, 
                pur_date:this.pur_date,
                supplier_name : this.supplier_name,
                supplier:this.supplier,
                products:this.products,
                total_amount:this.total_amount,
                total_discount:this.total_discount,
                extra_discount:this.extra_discount,
                total_price:this.total_price,
            }
            this.$store.dispatch("setPurchaseItem", purchaseItem)
            this.$router.push({name: "EditPurchase", params: {id:this.id}})
        },
        setCode(p){
            this.id=p.id;
            this.voucher_no=p.voucher_no;
            this.pur_date=p.pur_date;
            this.supplier_name=`${p.supplier.name} (${p.supplier.company})`;
            this.supplier=p.supplier;
            
           // this.products=p.products;
           this.products=p.products.map((p)=>{
               return {
                    ...p, amount : p.pur_qty * p.pur_price
               }                
           })
            this.calAmount(p);
        },
        calAmount(p){
            this.total_amount=p.total_amount;
            this.total_discount=p.total_discount;
            this.extra_discount=p.extra_discount;
            this.total_price=p.total_price;
        },
        
       showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        }
                    
    }
}
</script>
<style lang="">
    
</style>

   <!--
                   <ul class="list-gorup list-group-flush">
                        <li class="list-group-item">
                            <div class="row justify-content-end">
                                <div class="col-lg-3">
                                    <div class="text-end mt-2">Voucher no. :</div>
                                </div>
                                <div class="col-lg-3">                                 
                                    <input type="text"  data-bs-toggle="modal" data-bs-target="#purchaseModal"  v-model="voucher_no" class="form-control-plaintext border-bottom" id="voucher_no" placeholder="Voucher no">                                        
                                </div>
                            </div>     
                        </li>                       
                        <li class="list-group-item">
                            <div class="row justify-content-end">
                                <div class="col-lg-3">
                                    <div class="text-end mt-2">Date :</div>
                                </div>
                                <div class="col-lg-3">                                
                                    <input type="date" v-model="pur_date" data-bs-toggle="modal" data-bs-target="#purchaseModal"  class="form-control-plaintext border-bottom" id="pur_date" placeholder="Date">                                        
                                </div>
                            </div>     
                        </li>
                        <li class="list-group-item">
                            <div class="row justify-content-end">
                                <div class="col-lg-3">                                
                                    <div class="text-end mt-2">Supplier :</div>
                                </div>
                                <div class="col-lg-3">                                
                                    <input type="text"  data-bs-toggle="modal" data-bs-target="#purchaseModal"  v-model="supplier" class="form-control-plaintext border-bottom" id="voucher_no" placeholder="Name (Company)">                                        
                                   
                                </div>
                            </div>     
                        </li>
                   </ul>        
                --> 
                <!--
                <ul class="list-group d-lg-none">
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold">
                            <div class="col-lg-1" >Code</div>
                            <div class="col-lg-2">Desc</div>
                            <div class="col-lg-1 text-center">Qty</div>
                            <div class="col-lg-2 text-center">Item price</div>
                            <div class="col-lg-2 text-center">
                                <div class="row g-0">
                                    <div class="col-4">Dis qty</div>
                                    <div class="col-4">Dis %</div>
                                    <div class="col-4">Dis $</div>
                                </div>
                            </div>                           
                            <div class="col-lg-1 text-center">Amout</div>
                            <div class="col-lg-1 text-center">Dis</div>
                            <div class="col-lg-2 text-center">Price</div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action" v-if="products.length > 0" v-for="(st, i) in products" :key="st.id">
                        <div class="row g-0 small"  >
                            <div class="col-lg-1" >{{st.code_no}}</div>
                            <div class="col-lg-2">{{st.item_name}}</div>
                            <div class="col-lg-1 text-center">           
                                <span>{{st.pur_qty}}</span>
                            </div>
                            <div class="col-lg-2 text-center">                               
                                 <span>{{st.pur_price}}</span>
                            </div>
                            <div class="col-lg-2 text-center">
                                <div class="row g-0">
                                    <div class="col-4"><span v-if="st.dis_qty">{{st.dis_qty}}</span><span v-else>0</span></div>
                                    <div class="col-4"><span v-if="st.dis_percent">{{st.dis_percent}}</span><span v-else>0</span></div>
                                    <div class="col-4"><span v-if="st.dis_amount">{{st.dis_amount}}</span><span v-else>0</span></div>
                                </div>
                            </div>   
                            <div class="col-lg-1 text-end">                               
                                <span>{{st.amount}}</span>
                           </div>
                           <div class="col-lg-1 text-end">                               
                                <span>{{st.discount}}</span>
                            </div>
                            <div class="col-lg-2 text-end">                               
                                <span>{{st.price}}</span>
                            </div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Amount</div>
                            <div class="col-lg-2 text-end">{{this.total_amount}}</div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Discount</div>
                            <div class="col-lg-2 text-end">{{this.total_discount}}</div>
                        </div> 
                    </li>
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Extra discount</div>                      
                                <div class="col-lg-2 text-end">{{this.extra_discount}}</div>                      
                        </div> 
                    </li>
                    
                    <li class="list-group-item list-group-item-action">
                        <div class="row g-0 small fw-bold justify-content-end">                            
                            <div class="col-lg-1 text-end">Total price</div>
                            <div class="col-lg-2 text-end">{{this.total_price}}</div>
                        </div> 
                    </li>
                
                </ul>        
            -->          