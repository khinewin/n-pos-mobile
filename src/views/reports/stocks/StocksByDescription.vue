<template lang="">
    <div class="container-fluid">            
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="" v-if="reports.length > 0">    
                            <tr class="fw-bold text-center">
                                <td >Code</td>
                                <td >Description</td>
                                <td  colspan="2">Qty</td>
                                <td >Retail price</td>
                                <td >Wholesale price</td>
                                <td >Pur price</td>
                                <td >Retail amount</td>
                                <td >Wholesale amount</td>
                                <td >Pur amount</td>
                            </tr>                        
                                <tr v-for="item in reports" :key="item.code_no">
                                    <td class="text-center">{{item.code_no}}</td>
                                    <td class="">{{item.item_name}}</td>
                                    <td class="col-1 text-center">{{item.qty}} {{item.child_unit}}</td>
                                    <td class="col-1 text-center">
                                       <span v-if="calQtyL(item.qty, item.item_per_package) > 0">{{calQtyL(item.qty, item.item_per_package)}} {{item.parent_unit}} </span>
                                        <span v-if="calQtyL(item.qty, item.item_per_package) >0 && calQtyS(item.qty, item.item_per_package) > 0"> * </span>
                                        <span v-if="calQtyS(item.qty, item.item_per_package)> 0">{{calQtyS(item.qty, item.item_per_package)}} {{item.child_unit}}  </span>
                                    </td>
                                    <td class="text-end">{{item.retail_price}}</td>
                                    <td class="text-end">{{item.whole_sale_price}}</td>
                                    <td class="text-end">{{item.pur_price}}</td>
                                    <td class="text-end">{{item.retail_amount}}</td>
                                    <td class="text-end">{{item.whole_sale_amount}}</td>
                                    <td class="text-end">{{item.pur_amount}}</td>
                                </tr>
                                <tr>
                                    <td colspan="7" class="text-end fw-bold">Total</td>
                                    <td class="text-end">{{total_retail_amount}}</td>
                                    <td class="text-end">{{total_whole_sale_amount}}</td>
                                    <td class="text-end">{{total_pur_amount}}</td>
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
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/stocks"

export default {
    props:['fromDate', 'toDate'],
    components: {
    },
  
    data(){
        return{                
            reports:[],
            stocksData:[],
            total_pur_amount:0,
            total_retail_amount:0,
            total_whole_sale_amount:0,
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
                this.fetchStocks();  
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
                this.fetchStocks();  
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
            let stocks=[]
            this.stocksData.map((res)=>{
                    res.products.map((data)=>{
                        stocks.push(data)
                    })
            })          
            const res1=Object.entries(hp.gb_item_name_retail_price(stocks))   
            console.log(res1)
            const res2=res1.map((ren)=>{
                let item={parent_unit:"",child_unit:"",item_name:ren[0], code_no:0, item_per_package:0, qty:0, pur_price:0, pur_amount:0, retail_price:0, retail_amount:0, whole_sale_price:0, whole_sale_amount:0}
                ren[1].map((rep)=>{
                    item.code_no=rep.code_no;
                    item.item_per_package=rep.item_per_package;
                    item.qty += rep.qty;
                    item.pur_price =rep.pur_price;
                    item.pur_amount +=rep.pur_amount;
                    item.retail_price =rep.retail_price;
                    item.retail_amount +=rep.retail_amount;
                    item.whole_sale_price =rep.whole_sale_price;
                    item.whole_sale_amount += rep.whole_sale_amount;
                    item.parent_unit=rep.parent_unit;
                    item.child_unit=rep.child_unit;
                })
                return item;
            }).sort((a,b)=>a.code_no - b.code_no)
                     
               res2.map((ren)=>{
                    this.total_retail_amount += ren.retail_amount;
                     this.total_whole_sale_amount+= ren.whole_sale_amount;
                    this.total_pur_amount += ren.pur_amount;
               })
              this.reports=[...res2];        
        },      
        async fetchStocks(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            const query_url = query(collection(db, "openingStocks"),
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            const snapShot=await getDocs(query_url)
            const stockItems=ss.stocks(snapShot)    
            this.stocksData=stockItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
