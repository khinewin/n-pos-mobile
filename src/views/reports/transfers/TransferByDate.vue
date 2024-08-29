<template lang="">
    <div class=""> 
        <table class="table table-bordered mb-0 border-dark table-hover">
            <tbody class="text-center fw-bold">
                <tr><td class="col-1" rowspan="2">Date</td></tr>
                <tr class="">
                    <td class="col-1">Code</td>
                    <td class="col-2">Description</td>
                    <td class="col-1">Packing size</td>
                    <td class="col-1">From Loc Bal</td> 
                    <td class="col-1">To Loc Bal</td> 
                    <td class="col-1">Transfer Bal</td>
                    <td class="col-1">After From Loc Bal</td> 
                    <td class="col-1">After To Loc Bal</td> 
                </tr>
            </tbody>
            <tbody v-if="reports.length > 0" v-for="r in reports" :key="r[0]">     
                <tr><td class="text-center" :rowspan="r[1].length + 1">{{r[0]}}</td></tr>                                       
                <tr v-for="item in r[1]" :key="item.code_no">
                    <td class="text-center">{{item.code_no}}</td>
                    <td class="">{{item.item_name}}</td>
                    <td class="">                 
                            1{{item.parent_unit}} * {{item.item_per_package}}{{item.child_unit}}
                    </td>
                    <td class="text-end">
                       <span  v-if="from_location=='Main'">
                        <span v-if="calQtyL(item.main_qty, item.item_per_package) > 0">{{calQtyL(item.main_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                        <span v-if="calQtyL(item.main_qty, item.item_per_package) >0 && calQtyS(item.main_qty, item.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(item.main_qty, item.item_per_package)> 0">{{calQtyS(item.main_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                       </span>
                       <span v-if="from_location=='Shop'">
                        <span v-if="calQtyL(item.qty, item.item_per_package) > 0">{{calQtyL(item.qty, item.item_per_package)}}{{item.parent_unit}} </span>
                        <span v-if="calQtyL(item.qty, item.item_per_package) >0 && calQtyS(item.qty, item.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(item.qty, item.item_per_package)> 0">{{calQtyS(item.qty, item.item_per_package)}}{{item.child_unit}}  </span>
                       </span>
                     </td>
                     <td class="text-end">
                        <span  v-if="to_location=='Main'">
                         <span v-if="calQtyL(item.main_qty, item.item_per_package) > 0">{{calQtyL(item.main_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                         <span v-if="calQtyL(item.main_qty, item.item_per_package) >0 && calQtyS(item.main_qty, item.item_per_package) > 0">, </span>
                         <span v-if="calQtyS(item.main_qty, item.item_per_package)> 0">{{calQtyS(item.main_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                        </span>
                        <span v-if="to_location=='Shop'">
                         <span v-if="calQtyL(item.qty, item.item_per_package) > 0">{{calQtyL(item.qty, item.item_per_package)}}{{item.parent_unit}} </span>
                         <span v-if="calQtyL(item.qty, item.item_per_package) >0 && calQtyS(item.qty, item.item_per_package) > 0">, </span>
                         <span v-if="calQtyS(item.qty, item.item_per_package)> 0">{{calQtyS(item.qty, item.item_per_package)}}{{item.child_unit}}  </span>
                        </span>
                      </td>
                      <td class="text-end">
                        <span v-if="calQtyL(item.transfer_qty, item.item_per_package) > 0">{{calQtyL(item.transfer_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                        <span v-if="calQtyL(item.transfer_qty, item.item_per_package) >0 && calQtyS(item.transfer_qty, item.item_per_package) > 0">, </span>
                        <span v-if="calQtyS(item.transfer_qty, item.item_per_package)> 0">{{calQtyS(item.transfer_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                      </td>
                      <td class="text-end">
                        <span  v-if="from_location=='Main'">
                         <span v-if="calQtyL(item.main_qty -item.transfer_qty, item.item_per_package) > 0">{{calQtyL(item.main_qty-item.transfer_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                         <span v-if="calQtyL(item.main_qty -item.transfer_qty, item.item_per_package) >0 && calQtyS(item.main_qty-item.transfer_qty, item.item_per_package) > 0">, </span>
                         <span v-if="calQtyS(item.main_qty-item.transfer_qty, item.item_per_package)> 0">{{calQtyS(item.main_qty-item.transfer_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                        </span>
                        <span v-if="from_location=='Shop'">
                         <span v-if="calQtyL(item.qty-item.transfer_qty, item.item_per_package) > 0">{{calQtyL(item.qty-item.transfer_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                         <span v-if="calQtyL(item.qty-item.transfer_qty, item.item_per_package) >0 && calQtyS(item.qty-item.transfer_qty, item.item_per_package) > 0">, </span>
                         <span v-if="calQtyS(item.qty-item.transfer_qty, item.item_per_package)> 0">{{calQtyS(item.qty-item.transfer_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                        </span>
                      </td>
                      <td class="text-end">
                        <span  v-if="to_location=='Main'">
                         <span v-if="calQtyL(item.main_qty +item.transfer_qty, item.item_per_package) > 0">{{calQtyL(item.main_qty+item.transfer_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                         <span v-if="calQtyL(item.main_qty +item.transfer_qty, item.item_per_package) >0 && calQtyS(item.main_qty+item.transfer_qty, item.item_per_package) > 0">, </span>
                         <span v-if="calQtyS(item.main_qty+item.transfer_qty, item.item_per_package)> 0">{{calQtyS(item.main_qty+item.transfer_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                        </span>
                        <span v-if="to_location=='Shop'">
                         <span v-if="calQtyL(item.qty+item.transfer_qty, item.item_per_package) > 0">{{calQtyL(item.qty+item.transfer_qty, item.item_per_package)}}{{item.parent_unit}} </span>
                         <span v-if="calQtyL(item.qty+item.transfer_qty, item.item_per_package) >0 && calQtyS(item.qty+item.transfer_qty, item.item_per_package) > 0">, </span>
                         <span v-if="calQtyS(item.qty+item.transfer_qty, item.item_per_package)> 0">{{calQtyS(item.qty+item.transfer_qty, item.item_per_package)}}{{item.child_unit}}  </span>
                        </span>
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
import * as hp from "@/lib/helper"
import * as ss from "@/lib/snapshot/transfers"
export default {
    props:['fromDate', 'toDate', 'from_location', 'to_location', 'code_no'],
    components: {
    },  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            transfersData:[],         
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
            this.transfersData.map((res)=>{            
                    res.products.map((data)=>{
                        stocks.push({...data, group_date : this.showTime(res.created_at)})
                    })
            })    
                     
            if(this.code_no){
                stocks=stocks.filter((f)=>f.code_no === Number(this.code_no))
            }
            const gbd=Object.entries(hp.gb_date(stocks))
            const gbc=gbd.map((res)=>{
                const res1=Object.entries(hp.gb_item_name_retail_price(res[1]))   
                const res2=res1.map((ren)=>{
                let item={main_qty:0, parent_unit:"", child_unit:"",item_name:ren[0], code_no:0, item_per_package:0, qty:0, transfer_qty:0}
                ren[1].map((rep)=>{
                    item.code_no=rep.code_no;
                    item.item_per_package=rep.item_per_package;
                    item.qty += rep.qty;    
                    item.main_qty += rep.main_qty;            
                    item.transfer_qty +=rep.transfer_qty;
                    item.parent_unit=rep.parent_unit;
                    item.child_unit=rep.child_unit;
                })
                return item;
                }).sort((a,b)=>a.code_no - b.code_no)
              
                const newData=[res[0],  [...res2]]   
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
            const query_url = query(collection(db, "transfers"), 
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), 
            where("from_location", "==", this.from_location), where("to_location", "==", this.to_location), 
            orderBy("created_at", "desc"));
            const shapShot=await getDocs(query_url)
            const items=ss.transfers(shapShot);           
            this.transfersData=items;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
