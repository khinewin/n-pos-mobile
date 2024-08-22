<template lang="">
    <div class="container-fluid">            
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="" v-if="reports.length > 0">    
                            <tr class="fw-bold text-center">
                                <td class="col-1">Code</td>
                                <td class="col-3">Description</td>
                                <td class="col-1" colspan="2">Qty</td>
                                <td class="col-1">Sale price</td>
                                <td class="col-1">Pur price</td>
                                <td class="col-1">Discount</td>
                                <td class="col-1">Sale amount</td>                                
                                <td class="col-1">Pur amount</td>
                                <td class="col-1">Profit</td>
                            </tr>                        
                                <tr v-for="s in reports" :key="s.code_no">
                                    <td class="text-center">{{s.code_no}}</td>
                                    <td>{{s.item_name}}</td>
                                    <td class="text-center col-1">
                                        {{s.sale_qty}} {{s.child_unit}}
                                    </td>
                                    <td class="text-center col-1">
                                        <span v-if="calQtyL(s.sale_qty, s.item_per_package) > 0">{{calQtyL(s.sale_qty, s.item_per_package)}} {{s.parent_unit}}</span>
                                        <span v-if="calQtyL(s.sale_qty, s.item_per_package) > 0 && calQtyS(s.sale_qty, s.item_per_package) > 0"> * </span>
                                        <span v-if="calQtyS(s.sale_qty, s.item_per_package) > 0">{{calQtyS(s.sale_qty, s.item_per_package)}} {{s.child_unit}}</span>

                                    </td>                                    
                                    <td class="text-end">{{s.sale_price}}</td>
                                    <td  class="text-end">{{s.pur_price}}</td>
                                    <td class="text-end">{{s.discount}}</td>
                                    <td  class="text-end">{{s.sale_amount}}</td>                                   
                                    <td  class="text-end">{{s.pur_amount}}</td>
                                    <td  class="text-end">{{s.profit}}</td>
                                </tr>
                                <tr>                              
                                    <td class="fw-bold text-end" colspan="7">Total</td>
                                    <td class="text-end">{{sale_amount}}</td>
                                    <td class="text-end">{{pur_amount}}</td>
                                    <td class="text-end">{{profit_amount}}</td>
                                </tr>
                                <tr>
                                    <td colspan="9" class="text-end fw-bold">Discount</td>
                                    <td class="text-end">{{discount}}</td>
                                </tr>
                                <tr>
                                    <td colspan="9" class="text-end fw-bold">Gross profit</td>
                                    <td class="text-end">{{profit_amount-discount}}</td>
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

export default {
    props:['fromDate', 'toDate', 'customer_code'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            salesData:[],
            grand_total:"",
            total_sale_amount:0,
            total_pur_amount:0,
            total_profit:0,
            net_profit:"",
            discount:"",
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
        goBack(){
            this.$router.go(-1)
        },
      
        displayReports(){       
            let sales=[]
            this.salesData.map((res)=>{
                    res.products.map((data)=>{
                        sales.push(data)
                    })
            })          
            const res1=Object.entries(hp.gb_item_name_sale_price(sales))   
          // const res1=Object.entries(Object.groupBy(sales, s=>s.item_name))
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
                    discount: 0,
                    parent_unit:"",
                    child_unit:"",
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
                    item.parent_unit =ren.parent_unit;
                    item.child_unit=ren.child_unit;
                })
                return item;
           }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)

                let sale_amount=0;
                let pur_amount=0;
                let profit_amount=0;
                res2.map((res)=>{
                    sale_amount += res.sale_amount;
                    pur_amount += res.pur_amount;
                    profit_amount += res.profit;
                })
                this.sale_amount=sale_amount;
                this.pur_amount=pur_amount;
                this.profit_amount=profit_amount;

              this.reports=[...res2];
        
        },
       
        async fetchSales(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           let query_url="";
            if(this.customer_code){
            query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
           }else{
             query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
           }
            const shapShot=await getDocs(query_url)
            let saleItems=[];
            let discount=0;
            shapShot.forEach((doc) => {       
                discount += doc.data().discount;  
                let sale = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    sale_date : doc.data().sale_date,
                    products : doc.data().products,
                    charges: doc.data().charges,
                    pay:doc.data().pay,
                    discount: doc.data().discount,
                    refunds: doc.data().refunds,
                    total_amount:doc.data().total_amount,
                    sale_type: doc.data().sale_type,
                    customer : doc.data().customer,                       
                };   
                                
                saleItems.push(sale);      
            });
            this.discount=discount;
            this.salesData=saleItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
