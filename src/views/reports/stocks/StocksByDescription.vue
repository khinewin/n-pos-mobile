<template lang="">
    <div class="container-fluid">            
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="text-center small" v-if="reports.length > 0">    
                            <tr class="fw-bold">
                                <td style="border-bottom: 2px solid black;">Code</td>
                                <td style="border-bottom: 2px solid black;">Description</td>
                                <td style="border-bottom: 2px solid black;" colspan="2">Qty</td>
                                <td style="border-bottom: 2px solid black;">Retail price</td>
                                <td style="border-bottom: 2px solid black;">Wholesale price</td>
                                <td style="border-bottom: 2px solid black;">Pur price</td>
                                <td style="border-bottom: 2px solid black;">Retail amount</td>
                                <td style="border-bottom: 2px solid black;">Wholesale amount</td>
                                <td style="border-bottom: 2px solid black;">Pur amount</td>
                            </tr>                        
                                <tr v-for="item in reports[0]" :key="item.code_no">
                                    <td class="">{{item.code_no}}</td>
                                    <td class="">{{item.item_name}}</td>
                                    <td class="">{{item.qty}}</td>
                                    <td class="">
                                        {{Math.floor(item.qty / item.item_per_package)}} x   {{Math.floor(item.qty % item.item_per_package)}}
                                    </td>
                                    <td class="">{{item.retail_price}}</td>
                                    <td class="">{{item.whole_sale_price}}</td>
                                    <td class="">{{item.pur_price}}</td>
                                    <td class="">{{item.retail_amount}}</td>
                                    <td class="">{{item.whole_sale_amount}}</td>
                                    <td class="">{{item.pur_amount}}</td>
                                </tr>
                                <tr>
                                    <td colspan="7" class="text-end fw-bold">Total</td>
                                    <td>{{reports[1].retail_amount}}</td>
                                    <td>{{reports[1].whole_sale_amount}}</td>
                                    <td>{{reports[1].pur_amount}}</td>
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

export default {
    props:['fromDate', 'toDate'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            stocksData:[],
            grand_total:"",
            pur_total:"",
            net_profit:"",
            discount:"",
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
           const res1=Object.entries(Object.groupBy(stocks, s=>s.item_name))
           const res2=res1.map((m_res)=>{
                       return m_res[1].reduce((accu, cur)=>{
                            const item= {
                                item_name : accu.item_name,
                                code_no: accu.code_no,
                                item_per_package: accu.item_per_package,
                                qty: Number(accu.qty) + Number(cur.qty),
                                pur_price: accu.pur_price + cur.pur_price,
                                retail_price: accu.retail_price + cur.retail_price,
                                whole_sale_price: accu.whole_sale_price,
                                pur_amount : accu.pur_amount + cur.pur_amount,
                                retail_amount : accu.retail_amount + cur.retail_amount,
                                whole_sale_amount : accu.whole_sale_amount + cur.whole_sale_amount,                                              
                            }                              
                            return item;                     
                        })                       
                })
                const res3=res2.reduce((accu, cur)=>{
                    return {                     
                            retail_amount:accu.retail_amount + cur.retail_amount,
                            whole_sale_amount:accu.whole_sale_amount + cur.whole_sale_amount,
                            pur_amount:accu.pur_amount + cur.pur_amount
                    }
                })        
              this.reports=[[...res2], res3];        
        },
        showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
        async fetchStocks(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           // console.log(from_date, to_date)
            const query_url = query(collection(db, "openingStocks"), where("uid", "==", this.user.uid), 
            where("created_at", ">=", from_date), where("created_at", "<=", to_date), orderBy("created_at", "desc"));
            const shapShot=await getDocs(query_url)
            let stockItems=[];
            shapShot.forEach((doc) => {       
                let stock = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    created_at : doc.data().created_at,
                    products : doc.data().products,                        
                };                  
                                
                stockItems.push(stock);      
            });        
            this.stocksData=stockItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
