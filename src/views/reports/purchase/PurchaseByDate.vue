<template lang="">
    <div class="overflow-x-auto">  
        <table class="table table-bordered mb-0 border-dark table-hover inside-table">
            <tbody class="fw-bold text-center">
                <tr><td class="col-1" rowspan="2">Date</td></tr>
                <tr class="">
                    <td class="col-1">Code</td>
                    <td class="col-2">Product Name</td>
                    <td class="col-1">Packing Size</td>
                    <td class="col-1">Qty</td>
                    <td class="col-1">Unit Price</td>
                    <td class="col-1">Amount</td>
                    <td class="col-1">Discount</td>                                              
                    <td class="col-1">Unit Cost</td>
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" v-for="r in reports" :key="r[0]" class="table-group-divider">        
                <tr>
                    <td class="text-center" :rowspan="r[1].length + 5">{{r[0]}}</td>
                </tr>                                    
                <tr v-for="st in r[1]" :key="st.code_no">
                    <td class="text-center">{{st.code_no}}</td>
                    <td >{{st.item_name}}</td>
                    <td class="col-1">
                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                     </td>
                    <td class="text-end">
                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.pur_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                    </td>
                    <td class="text-center">
                        {{st.qty_pur_price}}' {{st.qty_pur_price_unit}}
                    </td>                   
                    <td class="text-end">{{fM(st.amount)}}</td>
                    <td class="text-end">{{fM(st.discount)}}</td>
                    <td class="text-end">{{fM(st.unit_cost)}}</td>
                   
                </tr>
                <tr class="text-end">
                    <td colspan="5" class="text-end fw-bold border-0 border-bottom rm-b-b  pe-2">Total (Ks)</td>
                    <td>{{fM(r[2].amount)}}</td>
                    <td>{{fM(r[2].discount)}}</td>                                                
                    <td>{{fM(r[2].unit_cost)}}</td>
                </tr>
                <tr class="text-end">
                    <td colspan="7" class="text-end fw-bold  border-0 border-bottom rm-b-b  pe-2">Extra Discount (Ks)</td>
                    <td>{{fM(r[2].extra_discount)}}</td>                  
                </tr>
                <tr class="text-end">
                    <td colspan="7" class="text-end fw-bold  border-0 border-bottom rm-b-b pe-2">Paid (Ks)</td>
                    <td>{{fM(r[2].paid)}}</td>                  
                </tr>
                <tr class="text-end">
                    <td colspan="7" class="text-end fw-bold  border-0  pe-2">Balance (Ks)</td>
                    <td>{{fM(r[2].balance)}}</td>                  
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" class="table-group-divider">
                <tr >
                    <td class="text-end fw-bold  border-bottom rm-b-b  pe-2" colspan="8">Total Amount (Ks)</td>
                    <td class="text-end">{{fM(total_amount)}}</td>                                
                </tr>                        
            
                <tr>
                    <td class="text-end fw-bold border-bottom   rm-b-b  pe-2" colspan="8">Total Extra Discount (Ks)</td>
                    <td class="text-end">{{fM(total_extra_discount)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold  border-bottom   rm-b-b  pe-2" colspan="8">Total Paid (Ks)</td>
                    <td class="text-end">{{fM(total_paid)}}</td>
                </tr>
                <tr>
                    <td class="text-end fw-bold    pe-2" colspan="8">Total Balance (Ks)</td>
                    <td class="text-end">{{fM(total_balance)}}</td>
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
            total_extra_discount:0,
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
                this.fetchPurchase();  
            
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
            if (this.purchaseData.length <=0) return false;   
            let purchase=[]  
           
            this.purchaseData.map((res)=>{       
                if(!this.code_no){
                    this.total_amount += res.total;
                    this.total_extra_discount += res.extra_discount;
                    this.total_paid += res.paid;
                    this.total_balance += res.balance;
                }        
                    res.products.map((data)=>{
                        let index = purchase.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                            purchase.push({...data, group_date : this.showTime(res.pur_date), 
                                voucher_no: res.voucher_no, extra_discount: res.extra_discount, paid: res.paid, balance: res.balance
                            })
                        }else{
                            purchase.push({...data, group_date : this.showTime(res.pur_date), voucher_no: res.voucher_no, extra_discount: 0,
                                paid: 0, balance: 0
                            })
                        }
                    })
            })

            purchase=purchase.map((s)=>{
                return {...s, 
                    group_price: s.packing_size.largest_unit ? s.pur_price.largest_unit  :  s.pur_price.medium_unit                             
                }
            })


            if(this.code_no){
                purchase =purchase.filter((f)=>f.code_no === Number(this.code_no))
            }  
            const gbd=Object.entries(hp.gb_date(purchase))
            const gbc=gbd.map((res)=>{
                const res1=Object.entries(hp.gb_item_name_price(res[1]))
                const res2=res1.map((res)=>{
                    const item_name=res[1][0].item_name;
                    let item={extra_discount:0,packing_size:"",
                        item_name: item_name, code_no:"", 
                        discount:0, 
                        pur_qty:0, 
                        qty_pur_price:0, 
                        qty_pur_price_unit: "",
                        amount:0,
                        unit_cost:0,
                        paid:0,
                        balance:0
                      }
                    res[1].map((ren)=>{
                        item.code_no=ren.code_no;
                        item.pur_qty += ren.pur_qty;
                        item.amount += ren.amount;
                        item.qty_pur_price = ren.qty_pur_price;
                        item.qty_pur_price_unit=ren.qty_pur_price_unit;
                        item.packing_size=ren.packing_size;
                        item.unit_cost += ren.unit_cost;                        
                        item.discount += ren.discount ? ren.discount : 0;
                        item.extra_discount += ren.extra_discount;
                        item.paid += ren.paid;
                        item.balance += ren.balance;
                    })
                    return item;
                }).sort((a,b)=>a.code_no - b.code_no)
                
                let amount=0;
                let discount=0;
                let unit_cost=0;
                let paid=0;
                let balance=0;

                const res3={amount: 0, unit_cost:0, discount:0, extra_discount:0, paid: 0, balance:0}
                res2.map((ren)=>{
                    res3.amount += ren.amount;
                    res3.discount += ren.discount;
                    res3.unit_cost += ren.unit_cost;
                    res3.extra_discount += this.code_no ? 0 : ren.extra_discount;
                    res3.paid += this.code_no ? 0 : ren.paid;
                    res3.balance += this.code_no? 0 : ren.balance;

                    amount += ren.amount;
                    discount += ren.discount;
                    unit_cost += ren.unit_cost;
                    paid += ren.paid;
                    balance += ren.balance;
                })
                const newData=[res[0],  [...res2],res3]   
             
                return newData;                             
           })           
             
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
           onSnapshot(query_url, (snapShot)=>{
                const purchaseItems=ss.purchases(snapShot)
                this.purchaseData=purchaseItems;
                this.displayReports();
                this.isLoading=false;
           })
            
        }
    }
}
</script>
<style lang="">
    
</style>
