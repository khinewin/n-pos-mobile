<template lang="">
    <div class="overflow-x-auto">
        <table class="table table-bordered mb-0 border-dark table-hover inside-table">
            <tbody class="fw-bold text-center">
                <tr><td rowspan="2" class="col-1">Date</td></tr>
                <tr class="">
                    <td class="col-1">Code</td>
                    <td class="col-2">Product Name</td>
                    <td class="col-1 text-center">Packing Size</td>                                                                                    
                     <td class="col-1 text-center">Qty</td>
                    <td class="col-1 text-center">Sale Amount</td>
                    <td class="col-1 text-center">Pur Amount</td>
                    <td class="col-1 text-center">Profit</td>
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" v-for="r in reports" :key="r[0]" class="table-group-divider">    
                <tr><td class="text-center" :rowspan="r[1].length + 4">{{r[0]}}</td></tr>                                        
                <tr v-for="p in r[1]" :key="p.code_no">
                    <td class="col-1 text-center">{{p.code_no}}</td>
                    <td class="col-2">{{p.item_name}}</td>
                    <td class="">
                        {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                    </td>
                    <td class="text-end"  >
                        {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.sale_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                    </td>
                    <td class="text-end">{{fM(p.final_amount)}}</td>
                    <td class="text-end" >
                           {{fM(p.pur_amount)}}
                    </td>
                    <td class="text-end">
                           {{fM(p.profit)}}
                    </td>
                    
                </tr>
                <tr class="text-end">
                    <td colspan="4" class=" fw-bold  rm-b-b  border-0 border-bottom pe-2">Total (Ks)</td>
                    <td> {{fM(r[2].sale_amount)}}</td>
                    <td> {{fM(r[2].pur_amount)}}</td>
                    <td> {{fM(r[2].profit_amount)}}</td>
                </tr>
                <tr>
                    <td colspan="6" class="text-end fw-bold rm-b-b  border-0 border-bottom pe-2">Total Extra Discount (Ks)</td>
                    <td class="text-end"> {{fM(r[2].extra_discount)}}</td>                  
                </tr>
                <tr>
                    <td colspan="6" class="text-end fw-bold border-0 pe-2">Net Profit (Ks)</td>
                    <td class="text-end"> {{fM(r[2].profit_amount - r[2].extra_discount)}}</td>                  
                </tr>
            </tbody>
            <tbody  v-if="reports.length > 0"  class="table-group-divider">
                <tr>
                    <td class="text-end fw-bold rm-b-b border-bottom pe-2" colspan="7">Grand Total Sale Amount (Ks)</td>
                    <td class="text-end"> {{fM(total_sale_amount)}}</td>
                </tr>                     
                <tr>
                    <td class="text-end fw-bold rm-b-b   border-bottom pe-2" colspan="7">Grand Total Pur Amount (Ks)</td>
                    <td class="text-end"> {{fM(total_pur_amount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold rm-b-b   border-bottom pe-2" colspan="7">Grand Total Extra Discount (Ks)</td>
                    <td class="text-end"> {{fM(total_discount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold pe-2" colspan="7">Grand Total Profit (Ks)</td>
                    <td class="text-end"> {{fM(total_profit - total_discount)}}</td>
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
        this.isLoading=true;
        this.fetchSales();  
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
        mmNumber(input) {
        const myanmarDigits = ['၀', '၁', '၂', '၃', '၄', '၅', '၆', '၇', '၈', '၉'];        
        return input.toString().replace(/\d/g, (digit) => myanmarDigits[digit]);
        },
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
                             ${s.pur_price.largest_unit}` 
                             : 
                             `${s.retail_price.smallest_unit},
                             ${s.pur_price.smallest_unit}
                             `
                }
            })

            if(this.code_no){
                sales=sales.filter((f)=>f.code_no === Number(this.code_no))
            }

            const gbd=Object.entries(hp.gb_date(sales))        
            let total_profit=0;
            let total_pur_amount=0;
            let total_sale_amount=0;
            let total_discount=0
           const gbc=gbd.map((ren)=>{
                const res1=Object.entries(hp.gb_item_name_price(ren[1]))
                const res2=res1.map((res)=>{
                    const item_name=res[1][0].item_name;
                let item={
                    item_name: item_name,
                    sale_qty : 0,
                    code_no:"",
                    profit: 0,               
                    extra_discount: 0,
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
                let sale_amount=0;
                let pur_amount=0;
                let profit_amount=0;
                let extra_discount=0;
              // res2.map((res)=>{
                 //   sale_amount += res.final_amount;
                 //   pur_amount += res.pur_amount;
                 //   profit_amount += res.final_amount - res.pur_amount;
                   // extra_discount += this.code_no ? 0 : res.extra_discount;
             //   }) 
               sale_amount=res2.reduce((accu, item)=>Number(accu) + Number(item.final_amount), 0)
               pur_amount=res2.reduce((accu, item)=>Number(accu) + Number(item.pur_amount), 0)
               profit_amount=sale_amount - pur_amount
              extra_discount=res2.reduce((accu, item)=>{ return  this.code_no ? Number(accu) : Number(accu) + Number(item.extra_discount)}, 0)

                total_profit += profit_amount;
                total_pur_amount += pur_amount;
                total_sale_amount += sale_amount;
                total_discount += extra_discount;
                const res4={sale_amount, pur_amount, profit_amount, extra_discount}
                const newData=[ren[0],  [...res2],res4]    
                return newData;                             
           })           
           this.total_profit=total_profit;
           this.total_pur_amount=total_pur_amount;
           this.total_sale_amount=total_sale_amount;
           this.total_discount =total_discount;
           this.reports=gbc;        
        },   
        async fetchSales(){           
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)

           let query_url=""
           if(this.customer_code){
            query_url = query(collection(db, "sales"), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
           }else{
             query_url = query(collection(db, "sales"), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
            }
            onSnapshot(query_url, (snapShot)=>{
                const saleItems=ss.sales(snapShot)           
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
