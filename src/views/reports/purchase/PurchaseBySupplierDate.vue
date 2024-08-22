<template lang="">
    <div class="container-fluid">  
             
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="" v-if="reports.length > 0">                            
                            <tr v-for="r in reports" :key="r[0]">
                                <td class="col-1 text-center">{{r[0]}}</td>
                                <td class="p-0 col-11">
                                    <table class="table table-bordered border-dark table-hover mb-0" style="border-style: hidden;">
                                        <tbody>
                                            <tr v-for="res in r[1]">
                                                    <td class="col-1 text-center">{{res[0]}}</td>
                                                    <td class="p-0 col-11">
                                                        <table class="table table-bordered mb-0 border-dark table-hover mb-0" style="border-style: hidden;">
                                                            <thead>
                                                                <tr class="fw-bold text-center">
                                                                    <td class="col-1">Code</td>
                                                                    <td class="col-3">Description</td>
                                                                    <td class="col-2" colspan="2">Qty</td>
                                                                    <td class="col-1">Item price</td>
                                                                    <td class="col-1">Amount</td>
                                                                    <td class="col-2" colspan="4">Discount</td>                                              
                                                                    <td class="col-1">Price</td>
                                                                </tr>
                                                            </thead>
                                                            <tbody>                                            
                                                                <tr v-for="item in res[1]" :key="item.code_no">
                                                                    <td class="text-center">{{item.code_no}}</td>
                                                                    <td >{{item.item_name}}</td>
                                                                    <td class="col-1 text-center">{{item.pur_qty}} {{item.child_unit}}</td>
                                                                    <td class="col-1 text-center">
                                                                    <span v-if="calQtyL(item.pur_qty, item.item_per_package) > 0">{{calQtyL(item.pur_qty, item.item_per_package)}} {{item.parent_unit}} </span>
                                                                        <span v-if="calQtyL(item.pur_qty, item.item_per_package) >0 && calQtyS(item.pur_qty, item.item_per_package) > 0"> * </span>
                                                                        <span v-if="calQtyS(item.pur_qty, item.item_per_package)> 0">{{calQtyS(item.pur_qty, item.item_per_package)}} {{item.child_unit}}  </span>
                                                                    </td>
                                                                    <td class="text-end">{{item.pur_price}}</td>
                                                                    <td class="text-end">{{item.amount}}</td>
                                                                    <td class="text-center">{{item.dis_qty}}</td>
                                                                    <td class="text-center">{{item.dis_percent}}</td>
                                                                    <td class="text-end">{{item.dis_amount}}</td>
                                                                    <td class="text-end">{{item.discount}}</td>
                                                                    <td class="text-end">{{item.price}}</td>
                                                                </tr>
                                                                <tr>
                                                                    <td colspan="5" class="text-end fw-bold">Total</td>
                                                                    <td class="text-end">{{res[2].amount}}</td>
                                                                    <td colspan="4" class="text-end">{{res[2].discount}}</td>                                                
                                                                    <td class="text-end">{{res[2].price}}</td>
                                                                </tr>
                                                            </tbody>
                                                        </table>
                                                    </td>
                                            </tr>                                           
                                        </tbody>
                                    </table>
                                
                                </td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total amount</td>
                                <td class="text-start">{{total_amount}}</td>                                
                            </tr>                         
                            <tr>
                                <td class="text-end fw-bold">Total discount</td>
                                <td class="text-start">{{total_discount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Extra discount</td>
                                <td class="text-start">{{extra_discount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total price</td>
                                <td class="text-start">{{total_price}}</td>
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
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/purchases"
export default {
    props:['fromDate', 'toDate', 'supplier'],
    components: {
    },
  
    data(){
        return{                  
            reports:[],
            purchaseData:[],
            total_amount:0,
            total_price:0,
            total_discount:0,
            extra_discount:0,
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
                    this.total_price += res.total_price;
                    this.extra_discount += Number(res.extra_discount);
                    this.total_discount += res.total_discount;
                    res.products.map((data)=>{
                        purchase.push({...data,
                            supplier_code: res.supplier.code,
                             group_supplier : `${res.supplier.name} (${res.supplier.company})`, 
                             supplier_name: res.supplier.name,
                             supplier_company :res.supplier.company,
                             supplier_phone: res.supplier.phone,
                             supplier_id: res.supplier.id, 
                             group_date :  this.showTime(res.pur_date)
                            })
                       })
            })
           
            const gbs=Object.entries(hp.gb_supplier(purchase))       
            const data=gbs.map((res)=>{
                const supplier_name=res[0]
                const gbd=Object.entries(hp.gb_date(res[1])) 
                const gbc=gbd.map((ren)=>{
                    const date=ren[0];
                    const res1=Object.entries(hp.gb_item_name_pur_price(ren[1]))
                    const res2=res1.map((rep)=>{
                        let item={parent_unit:"", child_unit:"",item_name: rep[0], code_no:"", discount:0, pur_qty:0, pur_price:0, amount:0, item_per_package:0, price:0, dis_qty:0, dis_amount:0, dis_percent:0}
                        rep[1].map((r)=>{
                            item.code_no=r.code_no;
                            item.pur_qty += r.pur_qty;
                            item.amount += r.amount;
                            item.pur_price = r.pur_price;
                            item.item_per_package=r.item_per_package;
                            item.price += r.price;
                            item.dis_qty += r.dis_qty;
                            item.dis_percent += r.dis_percent;
                            item.dis_amount += r.dis_amount;
                            item.discount += r.discount;
                            item.parent_unit=r.parent_unit;
                            item.child_unit=r.child_unit;
                        })
                        return item;                    

                    }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)
                    const res3={amount: 0, price:0, discount:0}
                    res2.map((ren)=>{
                        res3.amount += ren.amount;
                        res3.discount += ren.discount;
                        res3.price += ren.price;
                    })    
                    return [date, res2, res3];
                })
                return [supplier_name, gbc];                     
            })
           // console.log(data)
            this.reports=data;
        },
        showTime(t){
           return hp.showTime(t)
        },
        async fetchPurchase(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           let query_url=""
           if(this.supplier){
            query_url = query(collection(db, "purchases"), 
            where("supplier.code", "==", Number(this.supplier)));
           }else{
            query_url = query(collection(db, "purchases"), 
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date", "desc"));
           }             
            const snapShot=await getDocs(query_url)
            let purchaseItems=ss.purchases(snapShot);
         
            this.purchaseData=purchaseItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
