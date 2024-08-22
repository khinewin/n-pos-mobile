<template lang="">
    <div class="container-fluid">  
             
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="" v-if="reports.length > 0">                            
                            <tr v-for="r in reports" :key="r[0]">
                                <td class="col-1 text-center">{{r[0]}}</td>
                                <td  class="p-0 col-11">
                                    <table class="table table-bordered mb-0 border-dark table-hover" style="border-style: hidden;">
                                        <thead class="text-center">
                                            <tr class="fw-bold">
                                                <td>Code</td>
                                                <td>Description</td>
                                                <td colspan="2">Qty</td>
                                                <td>RT price</td>
                                                <td>WS price</td>
                                                <td>Pur price</td>
                                                <td>RT amount</td>
                                                <td>WS amount</td>
                                                <td>Pur amount</td>
                                            </tr>
                                        </thead>
                                        <tbody>                                            
                                            <tr v-for="item in r[1]" :key="item.code_no">
                                                <td class="col-1 text-center">{{item.code_no}}</td>
                                                <td class="col-3">{{item.item_name}}</td>
                                                <td class="col-1 text-center">{{item.qty}} {{item.child_unit}}</td>
                                                <td class="col-1 text-center">
                                                   <span v-if="calQtyL(item.qty, item.item_per_package) > 0">{{calQtyL(item.qty, item.item_per_package)}} {{item.parent_unit}} </span>
                                                    <span v-if="calQtyL(item.qty, item.item_per_package) >0 && calQtyS(item.qty, item.item_per_package) > 0"> * </span>
                                                    <span v-if="calQtyS(item.qty, item.item_per_package)> 0">{{calQtyS(item.qty, item.item_per_package)}} {{item.child_unit}}  </span>
                                                </td>
                                                <td class="col-1 text-end">{{item.retail_price}}</td>
                                                <td class="col-1 text-end">{{item.whole_sale_price}}</td>
                                                <td class="col-1 text-end">{{item.pur_price}}</td>
                                                <td class="col-1 text-end">{{item.retail_amount}}</td>
                                                <td class="col-1 text-end">{{item.whole_sale_amount}}</td>
                                                <td class="col-1 text-end">{{item.pur_amount}}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="7" class="text-end fw-bold">Total</td>
                                                <td class="text-end">{{r[2].retail_amount}}</td>
                                                <td class="text-end">{{r[2].whole_sale_amount}}</td>
                                                <td class="text-end">{{r[2].pur_amount}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
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
          //  current_month:[],
            stocksData:[],         
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
                        stocks.push({...data, group_date : this.showTime(res.created_at)})
                    })
            })             
            const gbd=Object.entries(hp.gb_date(stocks))
            const gbc=gbd.map((res)=>{
                const res1=Object.entries(hp.gb_item_name_retail_price(res[1]))   
                const res2=res1.map((ren)=>{
                let item={parent_unit:"", child_unit:"",item_name:ren[0], code_no:0, item_per_package:0, qty:0, pur_price:0, pur_amount:0, retail_price:0, retail_amount:0, whole_sale_price:0, whole_sale_amount:0}
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
                let res3={retail_amount:0, whole_sale_amount:0, pur_amount:0}
                res2.map((res)=>{
                    res3.retail_amount += res.retail_amount;
                    res3.whole_sale_amount += res.whole_sale_amount;
                    res3.pur_amount += res.pur_amount;
                })
                const newData=[res[0],  [...res2],res3]   
                return newData
            })          
           this.reports=gbc;        
        },
        showTime(t){
            return hp.showTime(t)
        },
        async fetchStocks(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            const query_url = query(collection(db, "openingStocks"), 
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            const shapShot=await getDocs(query_url)
            const stockItems=ss.stocks(shapShot);           
            this.stocksData=stockItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
