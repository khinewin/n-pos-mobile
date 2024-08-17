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
                                <td>Item price</td>
                                <td>Amount</td>         
                                <td colspan="3">Discount</td>                       
                                <td>Price</td>
                            </tr>                        
                                <tr v-for="s in reports[0]" :key="s.code_no">
                                    <td>{{s.code_no}}</td>
                                    <td>{{s.item_name}}</td>
                                    <td>{{s.pur_qty}}</td>
                                    <td>
                                        {{Math.floor(s.pur_qty / s.item_per_package)}} x {{Math.floor(s.pur_qty % s.item_per_package)}}
                                    </td>
                                    <td>{{s.pur_price}}</td>
                                    <td>{{s.amount}}</td>                                   
                                    <td>{{s.dis_qty}}</td>
                                    <td>{{s.dis_percent}}</td>
                                    <td>{{s.dis_amount}}</td>
                                    <td>{{s.price}}</td>
                                </tr>
                                <tr>                              
                                    <td class="fw-bold text-end" colspan="5" >Total</td>
                                    <td class="text-center">{{reports[1].amount}}</td>                                                                                      
                                    <td class="text-center" colspan="3">{{total_discount}}</td>
                                    <td class="text-center">{{reports[1].price}}</td>                                  
                                </tr>
                                <tr>                              
                                    <td class="fw-bold text-end" >Total amount</td>
                                    <td class="text-center">{{total_amount}}</td>                                  
                                </tr>
                                <tr>
                                    <td  class="text-end fw-bold">Total discount</td>
                                    <td>{{total_discount}}</td>
                                </tr>
                                <tr>
                                    <td class="text-end fw-bold">Extra discount</td>
                                    <td>{{extra_discount}}</td>
                                </tr>
                                <tr>
                                    <td  class="text-end fw-bold">Total price</td>
                                    <td>{{total_price}}</td>
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
    props:['fromDate', 'toDate','supplier'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
            purchaseData:[],        
            total_amount:0,
            total_discount:0,
            extra_discount:0,
            total_price:0,
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
        goBack(){
            this.$router.go(-1)
        },
    
        displayReports(){       
            if (this.purchaseData.length <=0) return false;
            let purchase=[]
            this.purchaseData.map((res)=>{
                    this.total_amount += res.total_amount;
                    this.total_discount += res.total_discount;
                    this.extra_discount += res.extra_discount;
                    this.total_price +=res.total_price;

                    res.products.map((data)=>{
                        purchase.push(data)
                    })
            })
           const res1=Object.entries(Object.groupBy(purchase, s=>s.item_name))
           const res2=res1.map((m_res)=>{
                       return m_res[1].reduce((accu, cur)=>{
                            const item= {                              
                                item_name : accu.item_name,
                                code_no: accu.code_no,
                                pur_qty: accu.pur_qty + cur.pur_qty,
                                pur_price: Math.floor((accu.pur_price + cur.pur_price) /2),
                                amount: accu.amount + cur.amount,
                                item_per_package: accu.item_per_package,
                                price: accu.price + cur.price,        
                                dis_qty :accu.dis_qty  + cur.dis_qty ,    
                                dis_amount :accu.dis_amount + cur.dis_amount ,   
                                dis_percent :accu.dis_percent+ cur.dis_percent,  
                                              
                            }                              
                            return item;                     
                        })                       
                })
                const res3=res2.reduce((accu, cur)=>{
                    return {                       
                        amount : accu.amount + cur.amount,
                        price :   accu.price + cur.price
                    }
                })
                const newData=[[...res2], res3]
              
              this.reports=newData;
        
        },
        showTime(t){
            const date=t.toDate()
            const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
            const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
            return formattedDate;
        },
        async fetchPurchase(){
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
           // console.log(from_date, to_date)
           let query_url="";
           if(this.supplier){
            query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid), 
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date),
            where('supplier.code',"==", Number(this.supplier)),
             orderBy("pur_date", "desc"));                               
            }else{
                query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid), 
                where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date", "desc"));
            }
            const shapShot=await getDocs(query_url)
            let purchaseItems=[];
            shapShot.forEach((doc) => {       
                let purchase = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                    products : doc.data().products,
                    pur_date:doc.data().pur_date,
                    supplier: doc.data().supplier,                
                    total_discount: doc.data().total_discount,
                    total_amount:doc.data().total_amount,
                    total_price: doc.data().total_price,
                    extra_discount: doc.data().extra_discount,
                        
                };               
                                
                purchaseItems.push(purchase);      
            });
            this.purchaseData=purchaseItems;
            this.displayReports();
            this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
