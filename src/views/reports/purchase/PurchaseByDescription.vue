<template lang="">
    <div class="container-fluid">            
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="" v-if="reports.length > 0">    
                            <tr class="fw-bold text-center">
                                <td class="col-1">Code</td>
                                <td class="col-2">Description</td>
                                <td class="col-1">Packing size</td>
                                <td class="col-1">Qty</td>
                                <td class="col-1">Item price</td>
                                <td class="col-1">Amount</td>         
                                <td class="col-1" colspan="4">Discount</td>                       
                                <td class="col-1">Price</td>
                            </tr>                        
                                <tr v-for="s in reports[0]" :key="s.code_no">
                                    <td class="text-center">{{s.code_no}}</td>
                                    <td>{{s.item_name}}</td>
                                    <td class="col-1">
                                        1{{s.parent_unit}} * {{s.item_per_package}}{{s.child_unit}}
                                    </td>
                                    <td class="col-1 text-end">
                                       <span v-if="calQtyL(s.pur_qty, s.item_per_package) > 0">{{calQtyL(s.pur_qty, s.item_per_package)}}{{s.parent_unit}} </span>
                                        <span v-if="calQtyL(s.pur_qty, s.item_per_package) >0 && calQtyS(s.pur_qty, s.item_per_package) > 0">, </span>
                                        <span v-if="calQtyS(s.pur_qty, s.item_per_package)> 0">{{calQtyS(s.pur_qty, s.item_per_package)}}{{s.child_unit}}  </span>
                                    </td>
                                    <td class="text-end">{{s.pur_price}}</td>
                                    <td class="text-end">{{s.amount}}</td>                                   
                                    <td class="text-center">{{s.dis_qty}}</td>
                                    <td class="text-center">{{s.dis_percent}}</td>
                                    <td class="text-end">{{s.dis_amount}}</td>
                                    <td class="text-end">{{s.discount}}</td>
                                    <td class="text-end">{{s.price}}</td>
                                </tr>
                                <tr class="text-end">                              
                                    <td class="fw-bold" colspan="5" >Total</td>
                                    <td class="">{{reports[1].amount}}</td>                                                                                      
                                    <td class="" colspan="4">{{total_discount}}</td>
                                    <td class="">{{reports[1].price}}</td>                                  
                                </tr>
                                <tr>                              
                                    <td class="fw-bold text-end" >Total amount</td>
                                    <td class="">{{total_amount}}</td>                                  
                                </tr>
                                <tr>
                                    <td  class="text-end fw-bold">Total discount</td>
                                    <td>{{total_discount}}</td>
                                </tr>
                                <tr>
                                    <td class="text-end fw-bold">Extra discount</td>
                                    <td>{{extra_discount}}</td>
                                </tr>
                                <tr>
                                    <td  class="text-end fw-bold">Total price</td>
                                    <td>{{total_price}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper"
export default {
    props:['fromDate', 'toDate','supplier', 'location'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
            purchaseData:[],        
            total_amount:0,
            total_discount:0,
            extra_discount:0,
            total_price:0,
            isLoading:false,
            msg : {
                type :"",
                info :"",
            },        

        }
    },
   
    mounted() {    
            if(this.user !==null){
                this.isLoading=true;
                this.fetchPurchase();  
            }
            this.displayFromDate=Timestamp.fromDate(new Date(this.fromDate))
            this.displayToDate=Timestamp.fromDate(new Date(this.toDate))
            if(this.reportType="by_date"){
                this.displayReportType="by Date"
            }
    },
   watch:{
   
    msg:{
            handler(val){
                if(val.type){
                    setTimeout(()=>{
                        this.msg.type="";
                        this.msg.info="";
                    }, 5000)
                }
            },deep:true
        },
        user(data){
            if(data){
                this.isLoading=true;
                this.fetchPurchase();  
            }
        }   
   },
    computed:{
        user(){
            return this.$store.getters.user;
        },
        report_type(){
            return this.$store.getters.reportType;
        },
      
    },
    methods:{
        calQtyL(qty, ipp){
            return hp.calQtyL(qty, ipp);
        },
        calQtyS(qty, ipp){
            return hp.calQtyS(qty, ipp)
        },
        goBack(){
            this.$router.go(-1)
        },
    
        displayReports(){       
            if (this.purchaseData.length <=0) return false;
            let purchase=[]
            this.purchaseData.map((res)=>{
                    this.total_amount += res.total_amount;
                    this.total_discount += res.total_discount;
                    this.extra_discount += res.extra_discount;
                    this.total_price +=res.total_price;
                    res.products.map((data)=>{
                        purchase.push(data)
                    })
            })          
            const res1=Object.entries(hp.gb_item_name_pur_price(purchase));           
           const res2=res1.map((res)=>{
                    let item={child_unit:"", parent_unit:"",item_name: res[0], code_no:"", discount:0, pur_qty:0, pur_price:0, amount:0, item_per_package:0, price:0, dis_qty:0, dis_amount:0, dis_percent:0}
                    res[1].map((ren)=>{
                        item.code_no=ren.code_no;
                        item.pur_qty += ren.pur_qty;
                        item.amount += ren.amount;
                        item.pur_price = ren.pur_price;
                        item.item_per_package=ren.item_per_package;
                        item.price += ren.price;
                        item.dis_qty += ren.dis_qty;
                        item.dis_percent += ren.dis_percent;
                        item.dis_amount += ren.dis_amount;
                        item.discount += ren.discount;
                        item.child_unit =ren.child_unit;
                        item.parent_unit=ren.parent_unit;
                    })
                    return item;
                }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)

                const res3={amount: 0, price:0, discount:0}
                res2.map((ren)=>{
                    res3.amount += ren.amount;
                    res3.discount += ren.discount;
                    res3.price += ren.price;
                })
                const newData=[[...res2], res3]
              
              this.reports=newData;
        
        },
      
        async fetchPurchase(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           // console.log(from_date, to_date)
           let query_url="";
           const loc=this.location=="All" ? null : this.location;
           if(this.supplier && loc !==null){
            query_url = query(collection(db, "purchases"),
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
            where('supplier.code',"==", Number(this.supplier)), where("location","==", loc),
             orderBy("pur_date", "desc"));                                         
            }else if(this.supplier){
                query_url = query(collection(db, "purchases"),
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
                where('supplier.code',"==", Number(this.supplier)),
                orderBy("pur_date", "desc"));             
            }else if(loc !==null){
                query_url = query(collection(db, "purchases"),
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
                where("location","==", loc),
                orderBy("pur_date", "desc"));        
            }else{
                query_url = query(collection(db, "purchases"),
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date", "desc"));
            }
            const shapShot=await getDocs(query_url)
            let purchaseItems=[];
            shapShot.forEach((doc) => {       
                let purchase = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    products : doc.data().products,
                    pur_date:doc.data().pur_date,
                    supplier: doc.data().supplier,                
                    total_discount: doc.data().total_discount,
                    total_amount:doc.data().total_amount,
                    total_price: doc.data().total_price,
                    extra_discount: doc.data().extra_discount,
                        
                };               
                                
                purchaseItems.push(purchase);      
            });
            this.purchaseData=purchaseItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
