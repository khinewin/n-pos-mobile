<template lang="">
    <div class="">
        <table class="table table-bordered border-dark table-hover">
            <tbody class="" v-if="reports.length > 0">                            
                <tr v-for="r in reports" :key="r[0]">
                    <td class="col-1 text-center">{{r[0]}}</td>
                    <td class="p-0 col-11">
                        <table class="table table-bordered table-hover border-dark mb-0" style="border-style: hidden;">
                            <tbody>
                                <tr v-for="res in r[1]">
                                   <td class="col-1 text-center"> {{res[0]}}</td>
                                   <td class="col-11 p-0">
                                    <table class="table table-bordered mb-0 border-dark table-hover " style="border-style: hidden;">
                                        <thead>
                                            <tr class="fw-bold text-center">
                                                <td >Code</td>
                                                <td >Description</td>
                                                <td>Packing size</td>
                                                <td >Qty</td>
                                                <td >Sale price</td>
                                                <td>Discount</td>
                                                <td >Sale amount</td>
                                                <td>Pur Amount</td>
                                                <td>Profit</td>
                                            </tr>
                                        </thead>
                                        <tbody>                                            
                                            <tr v-for="s in res[1]" :key="s.code_no">
                                                <td class="col-1 text-center">{{s.code_no}}</td>
                                                <td class="col-2">{{s.item_name}}</td>
                                                <td class="col-1">
                                                    1{{s.parent_unit}} * {{s.item_per_package}}{{s.child_unit}}
                                                </td>
                                                <td class="text-end col-1">
                                                    <span v-if="calQtyL(s.sale_qty, s.item_per_package) > 0">{{calQtyL(s.sale_qty, s.item_per_package)}}{{s.parent_unit}}</span>
                                                    <span v-if="calQtyL(s.sale_qty, s.item_per_package) > 0 && calQtyS(s.sale_qty, s.item_per_package) > 0">, </span>
                                                    <span v-if="calQtyS(s.sale_qty, s.item_per_package) > 0">{{calQtyS(s.sale_qty, s.item_per_package)}} {{s.child_unit}}</span>                
                                                </td>
                                                <td class="col-1 text-end">{{s.sale_price}}</td>
                                                <td class="col-1 text-end">{{s.discount}}</td>
                                                <td class="col-1 text-end">{{s.sale_amount}}</td>
                                                <td class="col-1 text-end">{{s.pur_amount}}</td>
                                                <td class="col-1 text-end">{{s.profit}}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="6" class="text-end fw-bold">Total</td>
                                                <td class="text-end">{{res[2].sale_amount}}</td>
                                                <td class="text-end">{{res[2].pur_amount}}</td>
                                                <td class="text-end">{{res[2].profit_amount}}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="8" class="text-end fw-bold">Extra discount</td>
                                                <td class="text-end">{{res[2].extra_discount}}</td>                  
                                            </tr>
                                            <tr>
                                                <td colspan="8" class="text-end fw-bold">Total profit</td>
                                                <td class="text-end">{{res[2].profit_amount - res[2].extra_discount}}</td>                  
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
                    <td class="text-end fw-bold">Total sale amount</td>
                    <td class="text-start">{{total_sale_amount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold">Total pur amount</td>
                    <td class="text-start">{{total_pur_amount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold">Discount</td>
                    <td class="text-start">{{total_discount}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold">Total profit</td>
                    <td class="text-start">{{total_profit - total_discount}}</td>
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
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            salesData:[],
            total_sale_amount:0,       
            total_pur_amount:0,
            total_discount:0,
            total_profit:0,
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
        calQtyL(qty,ipp){
            return hp.calQtyL(qty,ipp)
        },
        calQtyS(qty, ipp){
            return hp.calQtyS(qty,ipp)
        },
        showTime(t){
            return hp.showTime(t);
        },
        goBack(){
            this.$router.go(-1)
        },     
        displayReports(){       
            let sales=[]
            this.salesData.map((res)=>{   
                if(!this.code_no){
                        this.total_discount += res.discount
                    }                                       
                    res.products.map((data)=>{
                        let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                            sales.push({...data, 
                            group_customer:  `${res.customer.name}`,
                            customer_code: res.customer.code,
                            group_date :  this.showTime(res.sale_date),
                            extra_discount : res.discount,
                             voucher_no: res.voucher_no, extra_discount: res.discount})
                        }else{
                            sales.push({...data, group_customer:  `${res.customer.name}`,
                            customer_code: res.customer.code,
                            group_date :  this.showTime(res.sale_date),
                            extra_discount : res.discount, 
                            voucher_no: res.voucher_no, extra_discount: 0})
                        }
                    })
            })
            if(this.code_no){
                sales=sales.filter((f)=>f.code_no === Number(this.code_no))
            }
            sales.sort((a,b)=>Number(a.customer_code) - Number(b.customer_code))
            const gbc=Object.entries(hp.gb_customer(sales))       
            let total_sale_amount=0;
            let total_pur_amount=0;
            let total_profit=0;  
            const data=gbc.map((res)=>{
                const name=res[0];
                const gbd=Object.entries(hp.gb_date(res[1]))
                const gbc=gbd.map((red)=>{
                    const date=red[0]
                    const gbItem=Object.entries(hp.gb_item_name_sale_price(red[1]))
                    const res2=gbItem.map((res)=>{
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
                                discount:0,
                                extra_discount : 0,
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
                                item.discount += ren.discount;
                                item.child_unit=ren.child_unit;
                                item.parent_unit=ren.parent_unit;
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
                            extra_discount += res.extra_discount;
                        })
                        total_pur_amount +=pur_amount;
                        total_sale_amount += sale_amount;
                        total_profit+=profit_amount;
                        const res4={sale_amount, pur_amount, profit_amount, extra_discount}

                    const updateData=[date, res2,res4]
                    return updateData;
                })
                this.total_profit=total_profit;
                this.total_sale_amount=total_sale_amount;
                this.total_pur_amount=total_pur_amount;
                return [name, gbc];
            })  
           this.reports=data;
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
