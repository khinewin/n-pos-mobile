<template lang="">
    <div class="">
        <table class="table table-bordered border-dark table-hover">
            <tbody class="" v-if="reports.length > 0">                            
                <tr v-for="r in reports" :key="r[0]">
                    <td class="col-1 text-center ">{{r[0]}}</td>
                    <td class="p-0 col-11">
                        <table class="table table-bordered table-hover border-dark mb-0" style="border-style: hidden;">
                            <tbody>
                                <tr v-for="res in r[1]">
                                   <td class="col-1 text-center"> {{res[0]}}</td>
                                   <td class="col-11 p-0">
                                    <table class="table table-bordered mb-0 border-dark table-hover " style="border-style: hidden;">
                                        <thead>
                                            <tr class="fw-bold text-center">
                                                <td class="col-1">Code</td>
                                                <td class="col-2">Description</td>
                                                <td class="col-1 text-center">Packing Size</td> 
                                                <td class="col-1 text-center">Unit Price</td>                                 
                                                 <td class="col-1 text-center">Qty</td>                     
                                                <td class="col-1 text-center"> Amount</td>
                                            </tr>
                                        </thead>
                                        <tbody>                                            
                                            <tr v-for="p in res[1]" :key="p.code_no">
                                                <td class="col-1 text-center">{{p.code_no}}</td>
                                                <td class="col-2">{{p.item_name}}</td>
                                                <td class="">
                                                    {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                                                </td>
                                                <td class="text-end">{{p.qty_sale_price}}' {{p.qty_sale_price_unit}}</td>
                                                <td class="text-end"  >
                                                    {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.sale_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                                                </td>
                                                <td class="text-end" >
                                                       {{fM(p.amount)}}
                                                </td> 
                                            </tr>
                                            <tr class="text-end">
                                                <td colspan="5" class=" fw-bold  rm-b-b  border-0 border-bottom pe-2">Total (Ks)</td>
                                                <td> {{fM(res[2].amount)}}</td>                  
                                            </tr>
                                            <tr>
                                                <td colspan="5" class="text-end fw-bold rm-b-b  border-0 border-bottom pe-2">Total Paid (Ks)</td>
                                                <td class="text-end"> {{fM(res[2].paid)}}</td>                  
                                            </tr>
                                            <tr>
                                                <td colspan="5" class="text-end fw-bold border-0 pe-2">Total Balance (Ks)</td>
                                                <td class="text-end"> {{fM(res[2].balance)}}</td>                  
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
                    <td colspan="2" class="p-0" style="border-style: hidden;">
                        <table class="table table-bordered border-dark mb-0 table-group-divider table-hover">
                            <tbody>
                                <tr>
                                    <td class="text-end fw-bold rm-b-b border-bottom pe-2 col-10">Grand Total  Amount (Ks)</td>
                                    <td class="text-end col-2"> {{fM(total_amount)}}</td>
                                </tr>                     
                                <tr>
                                    <td class="text-end fw-bold rm-b-b   border-bottom pe-2" >Grand Total Paid (Ks)</td>
                                    <td class="text-end"> {{fM(total_paid)}}</td>
                                </tr>
                                <tr>
                                    <td class="text-end fw-bold  pe-2" >Grand Total Balance (Ks)</td>
                                    <td class="text-end"> {{fM(total_balance)}}</td>
                                </tr>
                            
                            </tbody>
                        </table>
                    </td>
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
import {Timestamp, onSnapshot, collection, addDoc, getDocs, doc, setDoc, where, updateDoc, orderBy, query} from 'firebase/firestore'
import db from '@/firebase/firestore'
import * as hp from "@/lib/helper.js"
import * as ss from "@/lib/snapshot/returnIn"
export default {
    props:['fromDate', 'toDate', 'customer_code', 'code_no'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            salesData:[],
            total_amount:0,         
            total_paid:0,
            total_balance:0,  
            isLoading:false,
            msg : {
                type :"",
                info :"",
            },        

        }
    },
   
    mounted() {    
                this.isLoading=true;
                this.fetchSales();  
            
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
        fM(amount){
            return hp.formattedAmount(amount)
        },
        displayPrice(l_unit, m_unit, s_unit){
            return hp.displayPrice(l_unit, m_unit, s_unit);
        },
        calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty){
            return hp.calPackingSize(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty);
        },
        calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit){
            return hp.calPackingQty(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit);
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
                        this.total_amount += res.total_amount;
                        this.total_paid += res.paid;
                        this.total_balance += res.balance ? res.balance : 0;
                    }                                         
                    res.products.map((data)=>{
                        let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                            sales.push({...data, 
                            group_customer:  `${res.customer.code}`,
                            customer_name: res.customer.name,
                            group_date :  this.showTime(res.rt_in_date),
                            voucher_no: res.voucher_no, paid: res.paid, balance: res.balance})
                        }else{
                            sales.push({...data, group_customer:  `${res.customer.code}`,
                            customer_name: res.customer.name,
                            group_date :  this.showTime(res.rt_in_date),
                            voucher_no: res.voucher_no, paid: 0, balance:0})
                        }
                    })
            })
            sales=sales.map((s)=>{
                return {...s, 
                    group_price: `${s.qty_sale_price}${s.qty_sale_price_unit}`
                }
            })
            if(this.code_no){
                sales=sales.filter((f)=>f.code_no === Number(this.code_no))
            }
            sales.sort((a,b)=>Number(a.customer_code) - Number(b.customer_code))
            const gbc=Object.entries(hp.gb_customer(sales))       
           
            const data=gbc.map((res)=>{
                let customer_name="";
                const gbd=Object.entries(hp.gb_date(res[1]))
                const gbc=gbd.map((red)=>{
                    const date=red[0]
                    const gbItem=Object.entries(hp.gb_item_name_price(red[1]))
                    const res2=gbItem.map((res)=>{
                        const item_name=res[1][0].item_name;
                        customer_name=res[1][0].customer_name;
                        let item={
                            item_name: item_name,
                            sale_qty : 0,
                            code_no:"",
                            profit: 0,               
                            paid: 0,
                            packing_size: {},
                            retail_price:{},
                            qty_sale_price_unit:{},
                            qty_sale_price:{},
                            amount:0,
                            balance:0
                        }               
                            res[1].map((ren)=>{
                                item.packing_size=ren.packing_size;
                                item.retail_price=ren.retail_price;
                                item.qty_sale_price_unit=ren.qty_sale_price_unit;
                                item.amount +=ren.amount;
                                item.sale_qty += ren.sale_qty;
                                item.code_no=ren.code_no;                  
                                item.qty_sale_price=ren.qty_sale_price;
                                item.paid += Number(ren.paid);
                                item.balance += ren.balance ? ren.balance : 0;
                            })
                        return item;
                    }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)
                    let amount=0;
                    let paid=0;
                    let balance=0;
                        res2.map((res)=>{
                            amount += res.amount;                   
                            paid += this.code_no ? 0 : res.paid;
                            balance += this.code_no ? 0 :res.balance;
                        })
                      
                        const res4={amount,paid, balance}

                    const updateData=[date, res2,res4]
                    return updateData;
                })
              
                return [customer_name, gbc];
            })  
           this.reports=data;
        },
     
        async fetchSales(){
            const date=new Date();         
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)
            
            let query_url=""
            if(this.customer_code){
                query_url = query(collection(db, "returnIn"), 
                where("rt_in_date", ">=", from_date), where("rt_in_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("rt_in_date", "desc"));
            }else{
                 query_url = query(collection(db, "returnIn"), 
                 where("rt_in_date", ">=", from_date), where("rt_in_date", "<=", to_date), orderBy("rt_in_date", "desc"));
            }
           onSnapshot(query_url, (snapShot)=>{
            const saleItems=ss.returnIn(snapShot)           
            this.salesData=saleItems;
            this.displayReports();
            this.isLoading=false;
           })            
        }
    }
}
</script>
<style lang="">
    
</style>
