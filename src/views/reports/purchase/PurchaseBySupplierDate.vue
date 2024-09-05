<template lang="">
    <div class="">  
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
                                                <tbody>
                                                    <tr class="fw-bold text-center">
                                                        <td class="col-1">Code</td>
                                                        <td class="col-2">Description</td>
                                                        <td class="col-1">Packing size</td>
                                                        <td class="col-1">Qty</td>
                                                        <td class="col-1">Unit price</td>
                                                        <td class="col-1">Amount</td>
                                                        <td class="col-1">Discount</td>                                              
                                                        <td class="col-1">Price</td>
                                                    </tr>
                                                </tbody>
                                                <tbody>                                            
                                                    <tr v-for="st in res[1]" :key="st.code_no">
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
                                                        <td class="text-end">{{st.amount}}</td>
                                                        <td class="text-end">{{st.discount}}</td>
                                                        <td class="text-end">{{st.price}}</td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="5" class="text-end fw-bold">Total</td>
                                                        <td class="text-end">{{res[2].amount}}</td>
                                                        <td class="text-end">{{res[2].discount}}</td>                                                
                                                        <td class="text-end">{{res[2].price}}</td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="7" class="text-end fw-bold">Extra discount</td>
                                                        <td class="text-end">{{res[2].extra_discount}}</td>                                                        
                                                    </tr>
                                                    <tr>
                                                        <td colspan="7" class="text-end fw-bold">Total price</td>
                                                        <td class="text-end">{{res[2].price - res[2].extra_discount}}</td>                                                        
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
                    <table class="table table-bordered border-dark table-hover" >
                        <tbody class="table-group-divider">
                            <tr>
                                <td class="text-end fw-bold col-10" >Grand total amount</td>
                                <td class="text-end col-2">{{total_amount}}</td>                                
                            </tr>                         
                            <tr>
                                <td class="text-end fw-bold">Grand total discount</td>
                                <td class="text-end">{{total_discount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Extra discount</td>
                                <td class="text-end">{{extra_discount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Grand total price</td>
                                <td class="text-end">{{total_price - extra_discount}}</td>
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
                    this.extra_discount += Number(res.extra_discount);
                 }  
                    res.products.map((data)=>{
                        let index = purchase.findIndex((item) => item.voucher_no == res.voucher_no);
                        if(index === -1){
                            purchase.push({...data, group_supplier : `${res.supplier.name} (${res.supplier.company})`, group_date :  this.showTime(res.pur_date),  voucher_no: res.voucher_no, extra_discount: res.extra_discount})
                        }else{
                            purchase.push({...data, group_supplier : `${res.supplier.name} (${res.supplier.company})`, group_date :  this.showTime(res.pur_date), voucher_no: res.voucher_no, extra_discount: 0})
                        }
                     })
            })
            purchase=purchase.map((s)=>{
                return {...s, 
                    group_price: s.packing_size.largest_unit ? s.pur_price.largest_unit  :  s.pur_price.medium_unit                             
                }
            })
            if(this.code_no){
                purchase=purchase.filter((f)=>f.code_no === Number(this.code_no))
            }

            let total_amount=0;
            let total_price=0;
            let total_discount=0;
           
            const gbs=Object.entries(hp.gb_supplier(purchase))       
            const data=gbs.map((res)=>{
                const supplier_name=res[0]
                const gbd=Object.entries(hp.gb_date(res[1])) 
                const gbc=gbd.map((ren)=>{
                    const date=ren[0];
                    const res1=Object.entries(hp.gb_item_name_price(ren[1]))
                    const res2=res1.map((rep)=>{
                        const item_name=res[1][0].item_name;
                        let item={extra_discount:0,packing_size:"",
                            item_name: item_name, code_no:"", 
                            discount:0, 
                            pur_qty:0, 
                            qty_pur_price:0, 
                            qty_pur_price_unit: "",
                            amount:0,
                            price:0,
                        }
                        rep[1].map((r)=>{
                            item.code_no=r.code_no;
                            item.pur_qty += r.pur_qty;
                            item.amount += r.amount;
                            item.qty_pur_price = r.qty_pur_price;
                            item.qty_pur_price_unit=r.qty_pur_price_unit;
                            item.packing_size=r.packing_size;
                            item.price += r.price;                        
                            item.discount += r.discount ? r.discount : 0;
                            item.extra_discount += r.extra_discount;
                        })
                        return item;                    

                    }).sort((a,b)=>a.item_name.toUpperCase() < b.item_name.toUpperCase() ? -1 : 1)

                    let amount=0;
                    let price=0;
                    let discount=0;

                    const res3={amount: 0, price:0, discount:0, extra_discount: 0}
                    res2.map((ren)=>{
                        res3.amount += ren.amount;
                        res3.discount += ren.discount;
                        res3.price += ren.price;
                        res3.extra_discount += ren.extra_discount;

                        amount += ren.amount;
                        price += ren.price;
                        discount += ren.discount;
                    })    
                    total_amount += amount;
                    total_price += price;
                    total_discount += discount;
                    return [date, res2, res3];
                })
                this.total_amount = total_amount;
                this.total_price =total_price;
                this.total_discount =total_discount;
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
