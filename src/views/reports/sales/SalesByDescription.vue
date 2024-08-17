<template lang="">
    <div class="container-fluid">            
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="text-center small" v-if="reports.length > 0">    
                            <tr class="fw-bold">
                                <td>Code</td>
                                <td>Description</td>
                                <td colspan="2">Qty</td>
                                <td>Sale price</td>
                                <td>Pur price</td>
                                <td>Sale amount</td>                                
                                <td>Pur amount</td>
                                <td>Profit</td>
                            </tr>                        
                                <tr v-for="s in reports" :key="s.code_no">
                                    <td>{{s.code_no}}</td>
                                    <td>{{s.item_name}}</td>
                                    <td>{{s.sale_qty}}</td>
                                    <td>
                                         {{Math.floor(s.sale_qty / s.item_per_package)}} x  {{Math.floor(s.sale_qty % s.item_per_package)}}
                                    </td>
                                    <td>{{s.sale_price}}</td>
                                    <td>{{s.pur_price}}</td>
                                    <td>{{s.sale_amount}}</td>                                   
                                    <td>{{s.pur_amount}}</td>
                                    <td>{{s.item_profit}}</td>
                                </tr>
                                <tr>                              
                                    <td class="fw-bold text-end" colspan="6">Total</td>
                                    <td class="text-center">{{grand_total}}</td>
                                    <td class="text-center">{{pur_total}}</td>
                                    <td class="text-center">{{net_profit}}</td>
                                </tr>
                                <tr>
                                    <td colspan="8" class="text-end fw-bold">Discount</td>
                                    <td>{{discount}}</td>
                                </tr>
                                <tr>
                                    <td colspan="8" class="text-end fw-bold">Net profit</td>
                                    <td>{{net_profit-discount}}</td>
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
    props:['fromDate', 'toDate', 'customer_code'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            salesData:[],
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
        goBack(){
            this.$router.go(-1)
        },
        /*
        createMonthArray(){
            let date = new Date();
            let firstDay = new Date(date.getFullYear(), date.getMonth(),1).getDate();
            let lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
            let monthArray=[];
            for(let i=1; i<=lastDay; i++){
                monthArray.push(new Date(date.getFullYear(), date.getMonth(), i))
            }
            this.current_month=monthArray;
        },
        */
        displayReports(){       
            let sales=[]
            this.salesData.map((res)=>{
                    res.products.map((data)=>{
                        sales.push(data)
                    })
            })
           const res1=Object.entries(Object.groupBy(sales, s=>s.item_name))
           const res2=res1.map((m_res)=>{
                       return m_res[1].reduce((accu, cur)=>{
                            const item= {
                                item_name : accu.item_name,
                                code_no: accu.code_no,
                                sale_qty: accu.sale_qty + cur.sale_qty,
                                sale_price: Math.floor((accu.sale_price + cur.sale_price) /2),
                                sale_amount: accu.sale_amount + cur.sale_amount,
                                item_per_package: accu.item_per_package,
                                pur_price:Math.floor((accu.pur_price + cur.pur_price) /2),     
                                              
                            }                              
                            return item;                     
                        })
                       
                })
                const res3=res2.map((res)=>{
                    return {
                        ...res,
                        pur_amount : res.pur_price * res.sale_qty,
                        item_profit : res.sale_amount - (res.pur_price * res.sale_qty)
                    }
                })
              const grand_total=res3.reduce((accu, cur)=>accu + cur.sale_amount, 0)
              const pur_total=res3.reduce((accu, cur)=>accu + cur.pur_amount, 0 )
              this.grand_total=grand_total;
              this.pur_total=pur_total;
              this.net_profit=grand_total - pur_total;
              this.reports=res3;
        
        },
        showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
        async fetchSales(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           let query_url="";
            if(this.customer_code){
            query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
           }else{
             query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
           }
            const shapShot=await getDocs(query_url)
            let saleItems=[];
            let discount=0;
            shapShot.forEach((doc) => {       
                discount += doc.data().discount;  
                let sale = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    sale_date : doc.data().sale_date,
                    products : doc.data().products,
                    charges: doc.data().charges,
                    pay:doc.data().pay,
                    discount: doc.data().discount,
                    refunds: doc.data().refunds,
                    total_amount:doc.data().total_amount,
                    sale_type: doc.data().sale_type,
                    customer : doc.data().customer,   
                        
                };               
                                
                saleItems.push(sale);      
            });
            this.discount=discount;
            this.salesData=saleItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
