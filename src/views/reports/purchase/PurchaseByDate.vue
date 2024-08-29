<template lang="">
    <div class="">  
        <table class="table table-bordered mb-0 border-dark table-hover">
            <tbody class="fw-bold text-center">
                <tr><td class="col-1" rowspan="2">Date</td></tr>
                <tr class="">
                    <td class="col-1">Code</td>
                    <td class="col-2">Description</td>
                    <td class="col-1">Packing size</td>
                    <td class="col-1">Qty</td>
                    <td class="col-1">Item price</td>
                    <td class="col-1">Amount</td>
                    <td class="col-2" colspan="4">Discount</td>                                              
                    <td class="col-1">Price</td>
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" v-for="r in reports" :key="r[0]">        
                <tr>
                    <td class="text-center" :rowspan="r[1].length + 4">{{r[0]}}</td>
                </tr>                                    
                <tr v-for="item in r[1]" :key="item.code_no">
                    <td class="text-center">{{item.code_no}}</td>
                    <td >{{item.item_name}}</td>
                    <td class="col-1">                                               
                             1{{item.parent_unit}} * {{item.item_per_package}}{{item.child_unit}}
                    </td>
                    <td class="col-1 text-end">
                       <span v-if="calQtyL(item.pur_qty, item.item_per_package) > 0">{{calQtyL(item.pur_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                        <span v-if="calQtyL(item.pur_qty, item.item_per_package) >0 && calQtyS(item.pur_qty, item.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(item.pur_qty, item.item_per_package)> 0">{{calQtyS(item.pur_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                    </td>
                    <td class="text-end">{{item.pur_price}}</td>
                    <td class="text-end">{{item.amount}}</td>
                    <td class="text-center">{{item.dis_qty}}</td>
                    <td class="text-center">{{item.dis_percent}}</td>
                    <td class="text-center">{{item.dis_amount}}</td>
                    <td class="text-end">{{item.discount}}</td>
                    <td class="text-end">{{item.price}}</td>
                </tr>
                <tr class="text-end">
                    <td colspan="5" class="text-end fw-bold">Total</td>
                    <td>{{r[2].amount}}</td>
                    <td colspan="4">{{r[2].discount}}</td>                                                
                    <td>{{r[2].price}}</td>
                </tr>
                <tr class="text-end">
                    <td colspan="10" class="text-end fw-bold">Extra discount</td>
                    <td>{{r[2].extra_discount}}</td>                  
                </tr>
                <tr class="text-end">
                    <td colspan="10" class="text-end fw-bold">Total price</td>
                    <td>{{r[2].price - r[2].extra_discount}}</td>                  
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0">
                <tr >
                    <td class="text-end fw-bold" colspan="2">Total amount</td>
                    <td class="text-end">{{total_amount}}</td>                                
                </tr>                         
                <tr>
                    <td class="text-end fw-bold" colspan="2">Total discount</td>
                    <td class="text-end">{{total_discount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold" colspan="2">Extra discount</td>
                    <td class="text-end">{{extra_discount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold" colspan="2">Total price</td>
                    <td class="text-end">{{total_price - extra_discount}}</td>
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
import {Timestamp, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/purchases"
export default {
    props:['fromDate', 'toDate', 'supplier', 'location', 'code_no'],
    components: {
    },
  
    data(){
        return{                
            reports:[],
          //  current_month:[],
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
                if(!this.code_no){
                    this.extra_discount += res.extra_discount;
                }        
                    res.products.map((data)=>{
                        if(this.code_no){
                            if(this.code_no==data.code_no) purchase.push({...data, group_date : this.showTime(res.pur_date), extra_discount: res.extra_discount})
                        }else{
                            purchase.push({...data, group_date : this.showTime(res.pur_date), extra_discount: res.extra_discount})
                        }
                    })
            })

            console.log(purchase)

            let total_amount=0;
            let total_discount=0;
            let total_price=0;

            const gbd=Object.entries(hp.gb_date(purchase))
            const gbc=gbd.map((res)=>{
                const res1=Object.entries(hp.gb_item_name_pur_price(res[1]))
                const res2=res1.map((res)=>{
                    let item={extra_discount:0, child_unit:"", parent_unit:"",item_name: res[0], code_no:"", discount:0, pur_qty:0, pur_price:0, amount:0, item_per_package:0, price:0, dis_qty:0, dis_amount:0, dis_percent:0}
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
                        item.child_unit=ren.child_unit;
                        item.parent_unit=ren.parent_unit;
                        item.extra_discount += ren.extra_discount;

                    })
                    return item;
                }).sort((a,b)=>a.code_no - b.code_no)
                
                let amount=0;
                let discount=0;
                let price=0;

                const res3={amount: 0, price:0, discount:0, extra_discount:0}
                res2.map((ren)=>{
                    res3.amount += ren.amount;
                    res3.discount += ren.discount;
                    res3.price += ren.price;
                    res3.extra_discount += this.code_no ? 0 : ren.extra_discount;

                    amount += ren.amount;
                    discount += ren.discount;
                    price += ren.price;
                })
                const newData=[res[0],  [...res2],res3]   
                total_amount += amount;
                total_discount += discount;
                total_price += price;           
                return newData;                             
           })           
                this.total_amount = total_amount;
                this.total_discount=total_discount;
                this.total_price=total_price;
           this.reports=gbc;       
        },
        showTime(t){
            return hp.showTime(t)
        },
        async fetchPurchase(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
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
            }
            else{
                query_url = query(collection(db, "purchases"),
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date", "desc"));     
            }
            const snapShot=await getDocs(query_url)
            const purchaseItems=ss.purchases(snapShot)
            this.purchaseData=purchaseItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
