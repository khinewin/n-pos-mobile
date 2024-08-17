<template lang="">
    <div class="container-fluid">  
             
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="text-center small" v-if="reports.length > 0">                            
                            <tr v-for="r in reports" :key="r[0]">
                                <td>{{r[0]}}</td>
                                <td colspan="5" class="p-0">
                                    <table class="table table-bordered mb-0 border-dark table-hover">
                                        <thead>
                                            <tr class="fw-bold">
                                                <td style="border-bottom: 2px solid black;">Code</td>
                                                <td style="border-bottom: 2px solid black;">Description</td>
                                                <td style="border-bottom: 2px solid black;" colspan="2">Qty</td>
                                                <td style="border-bottom: 2px solid black;">Price</td>
                                                <td style="border-bottom: 2px solid black;">Amount</td>
                                            </tr>
                                        </thead>
                                        <tbody>                                            
                                            <tr v-for="item in r[1]" :key="item.code_no">
                                                <td class="col-2">{{item.code_no}}</td>
                                                <td class="col-4">{{item.item_name}}</td>
                                                <td class="col-1">{{item.sale_qty}}</td>
                                                <td class="col-1">
                                                    {{Math.floor(item.sale_qty / item.item_per_package)}} x {{Math.floor(item.sale_qty % item.item_per_package)}}  
                                                </td>
                                                <td class="col-2">{{item.sale_price}}</td>
                                                <td class="col-2">{{item.sale_amount}}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="5" class="text-end fw-bold">Total</td>
                                                <td class="text-center">{{r[2].sale_amount}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total amount</td>
                                <td class="text-start">{{total_amount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total discount</td>
                                <td class="text-start">{{total_discount}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total charges</td>
                                <td class="text-start">{{total_charges}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total pay</td>
                                <td class="text-start">{{total_pay}}</td>
                            </tr>
                            <tr>
                                <td class="text-end fw-bold">Total refunds</td>
                                <td class="text-start">{{total_refunds}}</td>
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
            total_amount:0,
            total_discount:0,
            total_charges:0,
            total_pay:0,
            total_refunds:0,
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
                    this.total_amount += res.total_amount
                    this.total_discount +=res.discount
                    this.total_charges += res.total_amount - res.discount
                    this.total_pay += res.pay;
                    this.total_refunds += res.refunds
                    res.products.map((data)=>{
                        sales.push({...data, date_group : this.showTime(res.sale_date)})
                    })
            })
            const gbd=Object.entries(Object.groupBy(sales, res=>{
                return res.date_group;
            }))
           const gbc=gbd.map((res)=>{
                const res1=Object.entries(Object.groupBy(res[1], data=>{
                        return data.item_name;
                }))
                const res2=res1.map((m_res)=>{
                       return m_res[1].reduce((accu, cur)=>{
                            return {
                                item_name : accu.item_name,
                                code_no: accu.code_no,
                                sale_qty: accu.sale_qty + cur.sale_qty,
                                sale_price: Math.floor((accu.sale_price + cur.sale_price) /2),
                                sale_amount: accu.sale_amount + cur.sale_amount,
                                item_per_package: accu.item_per_package
                            }
                        })
                })
                const res3=res2.reduce((accu, cur)=>{
                    return {
                        sale_amount : accu.sale_amount + cur.sale_amount
                    }
                })
                console.log(res3)
                const newData=[res[0],  [...res2],res3]               
                return newData;                             
           })           
           this.reports=gbc;
        
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
           // console.log(from_date, to_date)
           let query_url=""
           if(this.customer_code){
            query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), where("customer.code", "==", Number(this.customer_code)), orderBy("sale_date", "desc"));
           }else{
             query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("sale_date", ">=", from_date), where("sale_date", "<=", to_date), orderBy("sale_date", "desc"));
            }
            const shapShot=await getDocs(query_url)
            let saleItems=[];
            shapShot.forEach((doc) => {         
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
        this.salesData=saleItems;
        this.displayReports();
        this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
