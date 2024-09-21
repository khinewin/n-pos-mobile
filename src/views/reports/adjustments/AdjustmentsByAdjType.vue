<template lang="">
    <div class="overflow-x-auto">
        <table class="table table-bordered mb-0 border-dark table-hover inside-table">
            <tbody class=" text-center fw-bold">
                <tr><td rowspan="2" class="col-1">Adj type</td></tr>
                <tr class="">
                    <td class="col-1">Date</td>
                    <td class="col-1">Code</td>
                    <td class="col-2">Description</td>
                    <td class="col-1">Packing Size</td>
                    <td class="col-1">Before Adj</td>
                    <td class="col-1">Location</td>
                    <td class="col-1">Qty</td>      
                    <td class="col-1">After Adj</td>   
                    <td class="col-1">RTS/RFS</td>                          
                    <td class="col-1">Remark</td>                          
                </tr>
            </tbody>
            <tbody  v-if="reports.length > 0" v-for="r in reports" :key="r[0]"> 
                <tr><td :rowspan="r[1].length  + 3" class="text-center">{{r[0]}}</td></tr>                                           
                <tr v-for="st in r[1]" :key="st.code_no">
                    <td class="text-center">{{st.group_date}}</td>
                    <td class="text-center">
                        {{st.code_no}}                                           
                    </td>
                    <td class="">{{st.item_name}}</td>   
                    <td class="">
                        {{calPackingSize(st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit, st.packing_size.medium_qty, st.packing_size.smallest_qty)}}
                    </td>
                    <td class=" text-end">
                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.before_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                    </td>
                    <td class="text-center">
                      {{st.location}}
                    </td>
                    <td class="text-center" data-bs-toggle="modal" data-bs-target="#setQtyModal">
                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                    </td>     
                    <td class=" text-end">
                        {{calPackingQty(st.packing_size.medium_qty, st.packing_size.smallest_qty, st.after_adj_qty, st.packing_size.largest_unit, st.packing_size.medium_unit, st.packing_size.smallest_unit)}}                                            
                    </td>      
                    <td class="text-center">
                        {{st.rts_rfs}}
                    </td>                                                         
                    <td class="">
                        {{st.remark}}
                    </td>               
                </tr>
                <tr class="text-center fw-bold">
                    <td colspan="7" class="border-bottom rm-b-b"></td>
                    <td>Pur Amount</td>
                    <td>Retail Amount</td>
                    <td>WS Amount</td>
                </tr>
                <tr>
                    <td colspan="7" class="text-end fw-bold pe-2">Total (Ks)</td>
                    <td class="text-end">{{fM(r[2].pur_amount)}}</td>
                    <td class="text-end">{{fM(r[2].retail_amount)}}</td>
                  <td class="text-end">{{fM(r[2].whole_sale_amount)}}</td>
                </tr>
            </tbody>
            <tbody class="table-group-divider" v-if="reports.length">
                <tr>
                    <td colspan="8" class="text-end fw-bold pe-2">Grand Total (Ks)</td>
                    <td class="text-end">{{fM(total_pur_amount)}}</td>
                    <td class="text-end">{{fM(total_retail_amount)}}</td>
                    <td class="text-end">{{fM(total_whole_sale_amount)}}</td>
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
import * as ss from "@/lib/snapshot/adjustments"
export default {
    props:['fromDate', 'toDate', 'code_no', 'adj_type', 'rts_rfs', 'location'],
    components: {
    },  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            adjustmentsData:[],         
            total_retail_amount:0,
            total_pur_amount:0,
            total_whole_sale_amount:0,
            isLoading:false,
            msg : {
                type :"",
                info :"",
            },        

        }
    },
   
    mounted() {    
                this.isLoading=true;
                this.fetchAdjustments();  
            
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
            let stocks=[]
            this.adjustmentsData.map((res)=>{            
                    res.products.map((data)=>{
                        stocks.push({...data, group_date : this.showTime(res.created_at), location: res.location})
                    })
            })         
            if(this.code_no){
                stocks=stocks.filter((f)=>f.code_no === Number(this.code_no))
            }    
            if(this.location !="All"){
                stocks=stocks.filter((f)=>f.location == this.location)
            }

            stocks=stocks.filter((f)=>{
                 if(this.adj_type) {
                  return  f.adj_type== this.adj_type
                }
                return f;
            })
            stocks=stocks.filter((f)=>{
                 if(this.rts_rfs) {
                  return  f.rts_rfs== this.rts_rfs
                }
                return f;
            }).sort((a,b)=>a.code_no - b.code_no)

            let total_pur_amount=0;
            let total_retail_amount=0;
            let total_whole_sale_amount=0;

            const gbd=Object.entries(hp.gb_adj_type(stocks))
            const gbc=gbd.map((res)=>{               

                let retail_amount=0;
                let pur_amount=0;
                let whole_sale_amount=0;

                let res3={retail_amount:0, whole_sale_amount:0, pur_amount:0}
                res[1].map((res)=>{
                    res3.retail_amount += res.retail_price.smallest_unit * res.adj_qty;
                    res3.pur_amount += res.pur_price.smallest_unit * res.adj_qty;        
                    res3.whole_sale_amount += res.whole_sale_price.smallest_unit * res.adj_qty;            

                    retail_amount += res.retail_price.smallest_unit * res.adj_qty;
                    pur_amount += res.pur_price.smallest_unit * res.adj_qty;        
                    whole_sale_amount += res.whole_sale_price.smallest_unit * res.adj_qty;    
                    return res;
                })
                
                const newData=[...res, res3]       
                total_pur_amount += pur_amount;
                total_retail_amount += retail_amount;
                total_whole_sale_amount += whole_sale_amount;       

                return newData
            })          
            this.total_pur_amount=total_pur_amount;
            this.total_retail_amount =total_retail_amount;
            this.total_whole_sale_amount =total_whole_sale_amount;
           this.reports=gbc;        
        },
        showTime(t){
            return hp.showTime(t)
        },
        async fetchAdjustments(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            const query_url = query(collection(db, "adjustments"), 
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            onSnapshot(query_url, (snapShot)=>{
                const items=ss.adjustments(snapShot)    
                this.adjustmentsData=items;
                this.displayReports();
                this.isLoading=false;
           })
        }
    }
}
</script>
<style lang="">
    
</style>
