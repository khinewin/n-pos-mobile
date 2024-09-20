<template lang="">
    <div class="">
        <table class="table table-bordered border-dark table-hover">
            <tbody class="" >    
                <tr class="fw-bold text-center">
                    <td class="col-1">Code</td>
                    <td class="col-2">Description</td>
                    <td class="col-1 text-center">Packing Size</td>                                                                                    
                     <td class="col-1 text-center">Unit Price</td>                                 
                     <td class="col-1 text-center">Qty</td>                     
                    <td class="col-1 text-center"> Amount</td>
                </tr> 
                </tbody>
                <tbody v-if="reports.length > 0">                       
                    <tr v-for="p in reports" :key="p.code_no">
                        <td class="text-center">{{p.code_no}}</td>
                        <td>{{p.item_name}}</td>
                        <td class="">
                            {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                        </td>                        
                        <td class="text-end">{{p.qty_pur_price}}' {{p.qty_pur_price_unit}}</td>
                        <td class="text-end"  >
                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.pur_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-end" >
                               {{fM(p.amount)}}
                        </td>                            
                    </tr>
                    <tr>
                        <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="5">Total  Amount (Ks)</td>
                        <td class="text-end"> {{fM(total_amount)}}</td>
                    </tr>                     
                    <tr>
                        <td class="text-end fw-bold rm-b-b   border-bottom pe-2" colspan="5">Total Paid (Ks)</td>
                        <td class="text-end"> {{fM(total_paid)}}</td>
                    </tr>
                    <tr>
                        <td class="text-end fw-bold  pe-2" colspan="5">Total Balance (Ks)</td>
                        <td class="text-end"> {{fM(total_balance)}}</td>
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
import * as ss from "@/lib/snapshot/returnOut"


export default {
    props:['fromDate', 'toDate', 'supplier_code','code_no'],
    components: {
    },
  
    data(){
        return{                 
            reports:[],
            salesData:[],           
            isLoading:false,
            total_amount:0,         
            total_paid:0,
            total_balance:0,        
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
            return hp.formattedAmount(amount);
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
                             sales.push({...data, group_date : hp.showTime(res.rt_out_date), voucher_no: res.voucher_no, paid: res.paid, balance: res.balance})
                        }else{
                            sales.push({...data, group_date : hp.showTime(res.rt_out_date), voucher_no: res.voucher_no, paid: 0, balance:0})
                        }
                    })
            })        
            sales=sales.map((s)=>{
                return {...s, 
                    group_price: `${s.qty_pur_price}${s.qty_pur_price_unit}`
                }
            })  
            if(this.code_no){
                sales=sales.filter((f)=>f.code_no === Number(this.code_no))
            }
            const res1=Object.entries(hp.gb_item_name_price(sales))   
          // const res1=Object.entries(Object.groupBy(sales, s=>s.item_name))
           const res2=res1.map((res)=>{
            const item_name=res[1][0].item_name;
                let item={
                    item_name: item_name,
                    pur_qty : 0,
                    code_no:"",
                    profit: 0,               
                    paid: 0,
                    packing_size: {},
                    retail_price:{},
                    qty_pur_price_unit:{},
                    qty_pur_price:{},
                    amount:0,
                    balance:0
                }               
                res[1].map((ren)=>{
                    item.packing_size=ren.packing_size;
                    item.retail_price=ren.retail_price;
                    item.qty_pur_price_unit=ren.qty_pur_price_unit;
                    item.amount +=ren.amount;
                    item.pur_qty += ren.pur_qty;
                    item.code_no=ren.code_no;                  
                    item.qty_pur_price=ren.qty_pur_price;
                    item.paid += Number(ren.paid);
                    item.balance += ren.balance ? ren.balance : 0;
                })
                return item;
           }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)
               

              this.reports=[...res2];
        
        },
       
        async fetchSales(){
            const date=new Date();         
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)
           let query_url="";
            if(this.supplier_code){
            query_url = query(collection(db, "returnOut"),  
            where("rt_out_date", ">=", from_date), where("rt_out_date", "<=", to_date), where("supplier.code", "==", Number(this.supplier_code)), orderBy("rt_out_date", "desc"));
           }else{
             query_url = query(collection(db, "returnOut"),  
            where("rt_out_date", ">=", from_date), where("rt_out_date", "<=", to_date), orderBy("rt_out_date", "desc"));
           }
           onSnapshot(query_url,(snapShot)=>{
            const saleItems=ss.returnOut(snapShot)           
            this.salesData=saleItems;
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
