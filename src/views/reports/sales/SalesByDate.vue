<template lang="">
    <div class="">
        <table class="table table-bordered mb-0 border-dark table-hover">
            <tbody class="fw-bold text-center">
                <tr><td rowspan="2" class="col-1">Date</td></tr>
                <tr class="">
                    <td class="">Code</td>
                    <td>Description</td>
                    <td>Packing size</td>
                    <td class="">Qty</td>
                    <td class="">Sale price</td>
                    <td class="">Pur price</td>
                    <td>Discount</td>
                    <td class="">Sale amount</td>
                    <td class="">Pur amount</td>
                    <td class="">Profit</td>
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" v-for="r in reports" :key="r[0]">    
                <tr><td class="text-center" :rowspan="r[1].length + 4">{{r[0]}}</td></tr>                                        
                <tr v-for="item in r[1]" :key="item.code_no">
                    <td class="col-1 text-center">{{item.code_no}}</td>
                    <td class="col-2">{{item.item_name}}</td>
                    <td class="col-1">
                        1{{item.parent_unit}} * {{item.item_per_package}}{{item.child_unit}}
                    </td>
                    <td class="col-1 text-end">
                       <span v-if="calQtyL(item.sale_qty, item.item_per_package) > 0">{{calQtyL(item.sale_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                        <span v-if="calQtyL(item.sale_qty, item.item_per_package) >0 && calQtyS(item.sale_qty, item.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(item.sale_qty, item.item_per_package)> 0">{{calQtyS(item.sale_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                    </td>
                    <td class="col-1 text-end">{{item.sale_price}}</td>
                    <td class="col-1 text-end">{{item.pur_price}}</td>
                    <td class="col-1 text-end">{{item.discount}}</td>
                    <td class="col-1 text-end">{{item.sale_amount}}</td>
                    <td class="col-1 text-end">{{item.pur_amount}}</td>
                    <td class="col-1 text-end">{{item.sale_amount - item.pur_amount}}</td>
                </tr>
                <tr>
                    <td colspan="7" class="text-end fw-bold">Total</td>
                    <td class="text-end">{{r[2].sale_amount}}</td>
                    <td class="text-end">{{r[2].pur_amount}}</td>
                    <td class="text-end">{{r[2].profit_amount}}</td>
                </tr>
                <tr>
                    <td colspan="9" class="text-end fw-bold">Extra discount</td>
                    <td class="text-end">{{r[2].extra_discount}}</td>                  
                </tr>
                <tr>
                    <td colspan="9" class="text-end fw-bold">Total profit</td>
                    <td class="text-end">{{r[2].profit_amount - r[2].extra_discount}}</td>                  
                </tr>
            </tbody>
            <tbody  v-if="reports.length > 0"  class="">
                <tr>
                    <td class="text-end fw-bold" colspan="2">Total sale amount</td>
                    <td class="text-end">{{total_sale_amount}}</td>
                </tr>                     
                <tr>
                    <td class="text-end fw-bold" colspan="2">Total pur amount</td>
                    <td class="text-end">{{total_pur_amount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold" colspan="2">Total discount</td>
                    <td class="text-end">{{total_discount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold" colspan="2">Total profit</td>
                    <td class="text-end">{{total_profit - total_discount}}</td>
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
import * as ss from "@/lib/snapshot/sales"
export default {
    props:['fromDate', 'toDate', 'customer_code', 'code_no'],   
    data(){
        return{                  
            reports:[],
            salesData:[],
            total_sale_amount:0,         
            total_pur_amount:0,
            total_profit:0,       
            total_discount:0,
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
                this.fetchSales();  
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
                this.fetchSales();  
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
            let sales=[]
            this.salesData.map((res)=>{    
                    if(!this.code_no){
                            this.total_discount += res.discount;
                        }              
                    res.products.map((data)=>{
                        let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                             sales.push({...data, group_date : hp.showTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: res.discount})
                        }else{
                            sales.push({...data, group_date : hp.showTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: 0})
                        }
                    })
            })

            if(this.code_no){
                sales=sales.filter((f)=>f.code_no === Number(this.code_no))
            }

            const gbd=Object.entries(hp.gb_date(sales))        
            let total_profit=0;
            let total_pur_amount=0;
            let total_sale_amount=0;
           const gbc=gbd.map((res)=>{
                const res1=Object.entries(hp.gb_item_name_sale_price(res[1]))
                const res2=res1.map((res)=>{
                let item={
                    item_name: res[0],
                    sale_qty : 0,
                    code_no:"",
                    sale_price:0,
                    pur_price:0,
                    sale_amount:0,
                    pur_amount:0,
                    profit: 0,
                    item_per_package:0,
                    child_unit:"",
                    parent_unit:"",
                    sale_qty_l:0,
                    sale_qty_s:0,
                    discount:0,
                    extra_discount:0,
                }                
                res[1].map((ren)=>{
                    item.item_per_package=ren.item_per_package;
                    item.sale_qty += ren.sale_qty;
                    item.code_no=ren.code_no;
                    item.sale_price=ren.sale_price;
                    item.pur_price=ren.pur_price;
                    item.pur_amount +=ren.pur_price * ren.sale_qty;
                    item.sale_amount += ren.sale_amount;
                    item.profit += ren.sale_amount - (ren.pur_price * ren.sale_qty);
                    item.child_unit=ren.child_unit;
                    item.parent_unit=ren.parent_unit;
                    item.sale_qty_l += ren.sale_qty_l;
                    item.sale_qty_s +=ren.sale_qty_s;
                    item.discount += ren.discount;
                    item.extra_discount += ren.extra_discount;
                })
                return item;
           }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)
               
                let sale_amount=0;
                let pur_amount=0;
                let profit_amount=0;
                let extra_discount=0;
                res2.map((res)=>{
                    sale_amount += res.sale_amount;
                    pur_amount += res.pur_amount;
                    profit_amount += res.sale_amount - res.pur_amount;
                    extra_discount += this.code_no ? 0 : res.extra_discount;
                })
                total_profit += profit_amount;
                total_pur_amount += pur_amount;
                total_sale_amount += sale_amount;
                const res4={sale_amount, pur_amount, profit_amount, extra_discount}
                const newData=[res[0],  [...res2],res4]    
                return newData;                             
           })           
           this.total_profit=total_profit;
           this.total_pur_amount=total_pur_amount;
           this.total_sale_amount=total_sale_amount;
           this.reports=gbc;        
        },   
        async fetchSales(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           let query_url=""
           if(this.customer_code){
            query_url = query(collection(db, "sales"), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
           }else{
             query_url = query(collection(db, "sales"), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
            }
            const snapShot=await getDocs(query_url)
            const saleItems=ss.sales(snapShot)
           
            this.salesData=saleItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
