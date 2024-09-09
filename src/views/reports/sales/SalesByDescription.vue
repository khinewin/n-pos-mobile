<template lang="">
    <div class="">
        <table class="table table-bordered border-dark table-hover">
            <tbody class="" >    
                <tr class="fw-bold text-center">
                    <td class="col-1">Code</td>
                    <td class="col-2">Description</td>
                    <td class="col-1 text-center">Packing size</td>                                                                                    
                     <td class="col-1 text-center">Qty</td>
                    <td class="col-1 text-center">Sale Amount</td>
                    <td class="col-1 text-center">Pur Amount</td>
                    <td class="col-1 text-center">Profit</td>
                </tr> 
                </tbody>
                <tbody v-if="reports.length > 0">                       
                    <tr v-for="p in reports" :key="p.code_no">
                        <td class="text-center">{{p.code_no}}</td>
                        <td>{{p.item_name}}</td>
                        <td class="">
                            {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                        </td>
                        <td class="text-end"  >
                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.sale_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-end">{{p.final_amount}}</td>
                        <td class="text-end" >
                               {{p.pur_amount}}
                        </td>
                        <td class="text-end">
                                {{p.profit}}
                        </td>                        
                    </tr>
                    <tr class="text-end">
                        <td colspan="4" class=" fw-bold">Total</td>
                        <td>{{sale_amount}}</td>
                        <td>{{pur_amount}}</td>
                        <td>{{profit_amount}}</td>
                    </tr>
                    <tr>
                        <td colspan="6" class="text-end fw-bold">Extra discount</td>
                        <td class="text-end">{{extra_discount}}</td>                  
                    </tr>
                    <tr>
                        <td colspan="6" class="text-end fw-bold">Net profit</td>
                        <td class="text-end">{{profit_amount - extra_discount}}</td>                  
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
import * as ss from "@/lib/snapshot/sales"


export default {
    props:['fromDate', 'toDate', 'customer_code','code_no'],
    components: {
    },
  
    data(){
        return{                 
            reports:[],
            salesData:[],           
            isLoading:false,
            sale_amount :0, 
            pur_amount:0,
            profit_amount:0,
            extra_discount:0,        
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
                    res.products.map((data)=>{
                        let index = sales.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                             sales.push({...data, group_date : hp.showTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: res.extra_discount})
                        }else{
                            sales.push({...data, group_date : hp.showTime(res.sale_date), voucher_no: res.voucher_no, extra_discount: 0})
                        }
                    })
            })        
            sales=sales.map((s)=>{
                return {...s, 
                    group_price: s.packing_size.largest_unit ? 
                             `${s.retail_price.largest_unit},
                             ${s.whole_sale_price.largest_unit}, 
                             ${s.pur_price.largest_unit}` 
                             : 
                             `${s.retail_price.medium_unit},
                             ${s.whole_sale_price.medium_unit},
                             ${s.pur_price.medium_unit}
                             `
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
                    sale_qty : 0,
                    code_no:"",
                    profit: 0,               
                    extra_discount:0,
                    packing_size: {},
                    retail_price:{},
                    whole_sale_price:{},
                    pur_price:{},
                    pur_amount:0,
                    final_amount:0,
                }               
                res[1].map((ren)=>{
                    item.packing_size=ren.packing_size;
                    item.retail_price=ren.retail_price;
                    item.whole_sale_price=ren.whole_sale_price;
                    item.pur_price=ren.pur_price;
                    item.final_amount +=ren.final_amount;
                    item.sale_qty += ren.sale_qty;
                    item.code_no=ren.code_no;                  
                    item.pur_price=ren.pur_price;
                    item.pur_amount +=ren.pur_price.smallest_unit * ren.sale_qty;
                    item.profit += ren.final_amount - (ren.pur_price.smallest_unit * ren.sale_qty);                                
                    item.extra_discount += Number(ren.extra_discount);
                })
                return item;
           }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)
                let extra_discount=0;
                let sale_amount=0;
                let pur_amount=0;
                let profit_amount=0;
                res2.map((res)=>{
                    sale_amount += res.final_amount;
                    pur_amount += res.pur_amount;
                    profit_amount += res.final_amount - res.pur_amount;
                    extra_discount += this.code_no ? 0 : res.extra_discount;
                })
                this.sale_amount=sale_amount;
                this.pur_amount=pur_amount;
                this.profit_amount=profit_amount;
                this.extra_discount=extra_discount;

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
            if(this.customer_code){
            query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
           }else{
             query_url = query(collection(db, "sales"),  
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
           }
           onSnapshot(query_url,(snapShot)=>{
            const saleItems=ss.sales(snapShot)           
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
