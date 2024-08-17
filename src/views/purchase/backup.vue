<template lang="">       
    <div>           
           <PageHeader page-title="Purchase" page-code="purchase"/>
           <div class="container py-2">
                <div class="row">
                    <div class="col-lg-2">
                            <button class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#searchModal"><i class="fa-brands fa-searchengin"></i> Search</button>
                    </div>
                </div>
           </div>
           <div class="container mb-2">
            <div class="row justify-content-end">
                <div class="col-12 col-sm-6 col-lg-3">
                        <ul class="list-group list-group-flush text-end">
                            <li class="list-group-item"><span class="small fw-lighter">Voucher no.</span> : <span v-if="selected_voucher">{{selected_voucher}}</span></li>
                            <li class="list-group-item"><span class="small fw-lighter">Date</span> : <span v-if="selected_date">{{showTime(selected_date)}}</span></li>
                        </ul>
                </div>
            </div>
               <div class="card">
                   <div class="card-body p-0">                        
                           <ul class="list-group list-group-flush">
                                   <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                       <div class="row">                                     
                                          
                                           <div class="col-3 col-lg-3">
                                                   <div class="small fw-lighter">Code</div>
                                           </div>
                                           <div class="col-3 col-lg-3">
                                                   <div class="small fw-lighter">Desc</div>
                                           </div>
                                           <div class="col-3 col-lg-3">
                                                   <div class="small fw-lighter text-center">Qty</div>
                                           </div>
                                           <div class="col-3 col-lg-3">
                                                   <div class="small fw-lighter text-end">
                                                        Price
                                                   </div>
                                           </div>
                                       
                                   </div>
                                   </li>
                                   <li class="list-group-item" v-if="isLoading">
                                       <div class="d-flex justify-content-center my-5">
                                           <div class="spinner-border text-primary"  role="status">
                                             <span class="visually-hidden">Loading...</span>
                                           </div>
                                         </div>
                                   </li>
                                   <li v-if="purchase_items.length > 0" class="list-group-item list-group-item-action" v-for="p in purchase_items" :key="p.id" >
                                           <div class="row">                                                
                                                   <div class="col-3 col-lg-3 small">
                                                        <div>{{p.code_no}}</div>
                                                    </div>
                                                   <div class="col-3 col-lg-3 small">
                                                           <div>{{p.item_name}}</div>
                                                   </div>
                                                   <div class="col-3 col-lg-3 small text-center">
                                                           <div>{{p.pur_qty}}</div>
                                                   </div>
                                                   <div class="col-3 col-lg-3 small text-end">
                                                    <div>{{p.pur_price}}</div>
                                                    </div>                                                   
                                           </div>
                                   </li>
                                   <li class="list-group-item list-group-item-action">
                                        <div class="row justify-content-end">
                                                <div class="col-3 col-lg-3 text-end">Total :</div>
                                                <div class="col-3 col-lg-3 text-end" v-if="amount">{{amount}}</div>
                                        </div>
                                   </li>
                           </ul>
                   </div>
               </div>
           </div>
            <!-- Modal -->
          <div class="modal fade" id="searchModal" data-bs-backdrop="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
              <div class="modal-content">               
                <div>
                    <div class="card">
                        <div class="card-body p-0 min-vh-100">
                            <div class="row g-2 m-2">
                                <div class="col-lg-5">
                                        <input type="search" v-model="search_by_voucher" @keypress.enter="searchPurchaseVoucher" placeholder="Voucher no." class="form-control">
                                </div>
                                <div class="col-lg-5">
                                        <input type="date" v-model="search_by_date" @change="searchPurchaseVoucher" placeholder="Date" class="form-control">
                                </div>
                                <div class="col-lg-1">
                                    <button class="btn text-primary" @click="clearSearch">Clear</button>
                                </div>
                                <div class="col-lg-1">
                                    <button class="btn text-primary" data-bs-dismiss="modal">Close</button>
                                </div>
                            </div>
                            <ul class="list-group list-group-flush">
                                <li class="list-group-item list-group-item-action bg-secondary-subtle">
                                    <div class="row">
                                         <div class="col-lg-6">
                                                <div class="small fw-lighter">Voucher no</div>
                                        </div>
                                        <div class="col-lg-6">
                                                <div class="small fw-lighter">Purcahse date</div>
                                        </div>  
                                </div>
                                </li>
                                <li class="list-group-item list-group-item-action" v-for="pv in pur_vouchers">
                                    <div class="row"  data-bs-dismiss="modal">
                                        <div class="col-lg-6 ">
                                                <div  class="small fw-lighter " @click="setPurchase(pv)"><span style="cursor: pointer;">{{pv.voucher_no}}</span></div>
                                        </div>
                                        <div class="col-lg-6">
                                                <div class="small fw-lighter" @click="setPurchaseDate(pv)"><span style="cursor: pointer;">{{showTime(pv.pur_date)}}</span></div>
                                        </div>  
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
              </div>
            </div>
          </div>
          <!--end modal-->
   </div>   
    
   </template>
   <script>
   import PageHeader from "@/views/partials/PageHeader.vue"
   import {Timestamp, collection, addDoc, query, where, getDocs} from 'firebase/firestore'
   import db from '@/firebase/firestore'
   export default {
       components: {
               PageHeader
       },
       data(){
           return{
               purchase_items : [],
               purchaseData:[],
               pur_vouchers: [],
               pur_vouchersData:[],
               isLoading: false,
               selected_voucher: "",
               selected_date : "",
               amount : 0,
               search_by_voucher:"",
               search_by_date:""
           }
       },
       created() {     
           if(this.user !==null){
               this.getPurchase();
           }
       },
       computed:{
           user(){
               return this.$store.getters.user;
           }
       },
       watch:{
           user(data){
               if(data !==null){
                   this.getPurchase();
               }
           }
       },
       methods:{
        clearSearch(){
            this.search_by_voucher="";
            this.search_by_date="";
            this.searchPurchaseVoucher();
        },
        searchPurchaseVoucher(){
            if(this.search_by_voucher){
                this.pur_vouchers=this.pur_vouchersData.filter((f)=>{
                        return f.voucher_no===this.search_by_voucher;
                })
            }else if(this.search_by_date){            
                this.pur_vouchers=this.pur_vouchersData.filter((f)=>{
                        const search_date=Timestamp.fromDate(new Date(this.search_by_date));
                        return this.showTime(f.pur_date).includes(this.showTime(search_date))
                })
            }else{
                this.pur_vouchers=[]
            }
        },
            setPurchase(p){
                this.selected_voucher=p.voucher_no;
                this.selected_date=p.pur_date;
                this.showPurchase();
            },
            setPurchaseDate(p){
                this.selected_date=p.pur_date;
                this.selected_voucher="";
                this.showPurchase();
            },
            showPurchase(){
                this.clearSearch();
                this.purchase_items=[];
                let purchase_items=[];
               if(this.selected_voucher){
                    purchase_items=this.purchaseData.filter((p)=>{
                        return p.voucher_no===this.selected_voucher;
                    })
               }else{
                    purchase_items=this.purchaseData.filter((p)=>{
                        return this.showTime(p.pur_date).includes(this.showTime(this.selected_date))
                    })
               }
               const amount=purchase_items.reduce((accu, cur)=>accu + cur.pur_price, 0)
                this.purchase_items=purchase_items;
                this.amount=amount;
            },
           async getPurchase(){
            this.isLoading=true;    
                const query_url = query(collection(db, "products"), where("uid", "==", this.user.uid));
                const snapShot=await getDocs(query_url)
                let purchase=[];
                snapShot.forEach((doc) => {         
                    doc.data().history.purchase.map((res)=>{
                            purchase.push(res)                   
                })                      
                            
            });
           const pur_items=purchase.sort((a,b)=>a.voucher_no - b.voucher_no)
           const combine = (a, prop) =>
            Object.values(
                [...a].reduce((acc, v) => {
                if (v[prop])
                    acc[v[prop]] = acc[v[prop]]
                    ? { ...acc[v[prop]], ...v }
                    : { ...v };
                return acc;
                }, {})
            );
            const vouchers=combine(purchase, 'voucher_no')
            this.pur_vouchersData=vouchers;         
           this.purchaseData=pur_items;
           this.isLoading=false;
          } ,
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