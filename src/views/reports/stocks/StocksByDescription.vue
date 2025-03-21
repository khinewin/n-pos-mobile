<template lang="">
    <div class="overflow-x-auto">
        <table class="table table-bordered border-dark table-hover inside-table">
            <tbody class="">    
                <tr class="fw-bold text-center">
                    <td class="col-1">Code</td>
                    <td class="col-2">Product Name</td>
                    <td class="col-1">Packing Size</td>
                    <!--<td class="col-1 " v-if="location=='Main' || location=='All'">Main Inv Bal</td>-->         
                    <td class="col-1" v-if="location=='Mart' || location=='All'">Mart Inv Bal</td>                                           
                    <td class="col-1">Pur Price</td>
                    <td class="col-1">Sale Price</td>
                    <!--<td class="col-1">Wholesale Price</td>-->                                            
                </tr>   
                </tbody>
                <tbody  v-if="reports.length > 0">                     
                    <tr v-for="p in reports" :key="p.code_no">
                        <td class="text-center">{{p.code_no}}</td>
                        <td class="">{{p.item_name}}</td>
                        <td class="">
                            {{calPackingSize(p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit, p.packing_size.medium_qty, p.packing_size.smallest_qty)}}
                        </td>
                        <!--<td class="text-end"  v-if="location =='All' || location=='Main'">
                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.main_qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                        </td>-->
                        <td class="text-end" v-if="location =='All' || location=='Mart'">
                            {{calPackingQty(p.packing_size.medium_qty, p.packing_size.smallest_qty, p.qty, p.packing_size.largest_unit, p.packing_size.medium_unit, p.packing_size.smallest_unit)}}                                            
                        </td>
                        <td class="text-end" >
                            {{displayPrice(p.pur_price.largest_unit, p.pur_price.medium_unit, p.pur_price.smallest_unit)}}
                    </td>
                    <td class="text-end">
                             {{displayPrice(p.retail_price.largest_unit, p.retail_price.medium_unit, p.retail_price.smallest_unit)}}
                    </td>
                    <!--<td class="text-end">
                            {{displayPrice(p.whole_sale_price.largest_unit, p.whole_sale_price.medium_unit, p.whole_sale_price.smallest_unit)}}
                    </td>-->
                    </tr>
                   
               
            </tbody>
            <tbody class="table-group-divider" v-if="reports.length > 0">
                <tr>
                    <td :colspan="[location=='All' ? 5 : 4]" class="text-end fw-bold pe-2">Total Amount (Ks)</td>                        
                    <td class="text-end">{{fM(total_retail_amount)}}</td>
                    <!--<td class="text-end">{{fM(total_whole_sale_amount)}}</td>-->
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
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/stocks"

export default {
    props:['fromDate', 'toDate', 'location', 'code_no'],
    components: {
    },
  
    data(){
        return{                
            reports:[],
            stocksData:[],
            total_pur_amount:0,
            total_retail_amount:0,
            total_whole_sale_amount:0,
            total_main_retail_amount:0,
            total_main_whole_sale_amount:0,
            isLoading:false,
            msg : {
                type :"",
                info :"",
            },       
        }
    },
   
    mounted() {    
                this.isLoading=true;
                this.fetchStocks();  
            
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
            let stocks=[]
            this.stocksData.map((res)=>{
                    res.products.map((data)=>{
                        stocks.push({...data, 
                            group_price: data.retail_price.largest_unit ? 
                             `${data.retail_price.largest_unit},
                             ${data.whole_sale_price.largest_unit},
                             ${data.pur_price.largest_unit}` 
                             : 
                             `${data.retail_price.medium_unit},
                             ${data.whole_sale_price.medium_unit},
                             ${data.pur_price.medium_unit}
                             `
                         })
                    })
            })       
            if(this.code_no){
                stocks=stocks.filter((f)=>{
                    return f.code_no == this.code_no
                })
            }   
            if(this.location=="Main"){
                stocks=stocks.filter((f)=>f.main_qty > 0)
            }
            if(this.location=="Shop"){
                stocks=stocks.filter((f)=>f.qty > 0)
            }  
            const res1=Object.entries(hp.gb_item_name_price(stocks))   
            const res2=res1.map((ren)=>{
                const item_name=ren[1][0].item_name;
                let item={main_qty:0, packing_size:"", main_whole_sale_amount:0, main_retail_amount:0,item_name:"", code_no:0,  qty:0, pur_price:0, pur_amount:0, retail_price:0, retail_amount:0, whole_sale_price:0, whole_sale_amount:0}
                ren[1].map((rep)=>{
                    item.item_name=item_name,
                    item.code_no=rep.code_no;
                    item.item_per_package=rep.item_per_package;
                    item.qty += rep.qty;
                    item.pur_price =rep.pur_price;
                    item.retail_price =rep.retail_price;
                    item.retail_amount +=this.location=="All" ? (rep.qty + rep.main_qty) * rep.retail_price.smallest_unit : this.location=="Main" ? rep.main_qty * rep.retail_price.smallest_unit : rep.qty * rep.retail_price.smallest_unit ;
                    item.whole_sale_price =rep.whole_sale_price;
                    item.whole_sale_amount += this.location=="All" ? rep.whole_sale_price.smallest_unit * (rep.qty + rep.main_qty) : this.location=="Main" ? rep.whole_sale_price.smallest_unit *rep.main_qty : rep.whole_sale_price.smallest_unit *rep.qty;
                    item.main_qty += rep.main_qty;
                    item.packing_size =rep.packing_size;
                })
                return item;
            }).sort((a,b)=>a.code_no - b.code_no)
                     
               res2.map((ren)=>{
                    this.total_retail_amount += ren.retail_amount;
                     this.total_whole_sale_amount+= ren.whole_sale_amount;                    
               })
              this.reports=[...res2];        
        },      
        async fetchStocks(){
            const date=new Date();         
            let fDate=new Date(this.fromDate)
            fDate.setHours("0", "0", "0")
            const from_date=Timestamp.fromDate(fDate)
            let tDate=new Date(this.toDate)
            tDate.setHours("23", "59", "59")
            const to_date=Timestamp.fromDate(tDate)
            
            const query_url = query(collection(db, "stocks"),
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            onSnapshot(query_url, (snapShot)=>{
                const stockItems=ss.stocks(snapShot);           
                this.stocksData=stockItems;
                this.displayReports();
                this.isLoading=false;
            })    
        }
    }
}
</script>
<style lang="">
    
</style>
