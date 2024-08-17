<template lang="">
    <div class="container-fluid">  
             
        <div class="row">                 
            <div class="col-12">
                    <table class="table table-bordered border-dark table-hover">
                        <tbody class="text-center small" v-if="reports.length > 0">                            
                            <tr v-for="r in reports" :key="r[0]">
                                <td class="col-1">{{r[0]}}</td>
                                <td class="p-0 col-11">
                                    <table class="table table-bordered mb-0 border-dark table-hover">
                                        <thead>
                                            <tr class="fw-bold">
                                                <td class="col-2" style="border-bottom: 2px solid black;">Code</td>
                                                <td class="col-2" style="border-bottom: 2px solid black;">Description</td>
                                                <td class="col-2" style="border-bottom: 2px solid black;" colspan="2">Qty</td>
                                                <td class="col-1" style="border-bottom: 2px solid black;">Item price</td>
                                                <td class="col-1" style="border-bottom: 2px solid black;">Amount</td>
                                                <td class="col-2" style="border-bottom: 2px solid black;" colspan="3">Discount</td>                                              
                                                <td class="col-2" style="border-bottom: 2px solid black;">Price</td>
                                            </tr>
                                        </thead>
                                        <tbody>                                            
                                            <tr v-for="item in r[1]" :key="item.code_no">
                                                <td >{{item.code_no}}</td>
                                                <td >{{item.item_name}}</td>
                                                <td >{{item.pur_qty}}</td>
                                                <td >
                                                    {{Math.floor(item.pur_qty / item.item_per_package)}} x {{Math.floor(item.pur_qty % item.item_per_package)}}
                                                </td>
                                                <td >{{item.pur_price}}</td>
                                                <td >{{item.amount}}</td>
                                                <td>{{item.dis_qty}}</td>
                                                <td >{{item.dis_percent}}</td>
                                                <td >{{item.dis_amount}}</td>
                                                <td >{{item.price}}</td>
                                            </tr>
                                            <tr>
                                                <td colspan="5" class="text-end fw-bold">Total</td>
                                                <td class="text-center">{{r[2].amount}}</td>
                                                <td colspan="3 text-center">{{r[2].discount}}</td>                                                
                                                <td>{{r[2].price}}</td>
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
                                <td class="text-end fw-bold">Total price</td>
                                <td class="text-start">{{total_price}}</td>
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
    props:['fromDate', 'toDate', 'supplier'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
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
        goBack(){
            this.$router.go(-1)
        },    
        displayReports(){    
            if (this.purchaseData.length <=0) return false;   
            const from_date=Timestamp.fromDate(new Date(this.fromDate))
            const to_date=Timestamp.fromDate(new Date(this.toDate))
            let purchase=[]             
            this.purchaseData.map((res)=>{                      
                    res.products.map((data)=>{
                        if(data.exp_date){
                            purchase.push({...data, 
                                exp_date : data.exp_date ? this.showTime(data.exp_date) : '',
                                expire_date: data.exp_date,
                            })
                        }
                    })
            })
            purchase.sort((a,b)=>a.expire_date - b.expire_date)
            const filter_purchase=purchase.filter((res)=>{
                return res.expire_date >= from_date && res.expire_date <= to_date;
            })            
            const gbd=Object.entries(Object.groupBy(filter_purchase, res=>{
                return res.exp_date;
            }))
           const gbc=gbd.map((res)=>{
                const res1=Object.entries(Object.groupBy(res[1], data=>{
                        return data.item_name;
                }))
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
                                dis_qty :accu.dis_qty ? accu.dis_qty : 0 + cur.dis_qty ? cur.dis_qty : 0,    
                                dis_amount :accu.dis_amount ? accu.dis_amount : 0 + cur.dis_amount ? cur.dis_amount : 0,   
                                dis_percent :accu.dis_percent ? accu.dis_percent : 0 + cur.dis_percent ? cur.dis_percent : 0,                
                            }
                            return item;
                        })
                })
                
                const res3=res2.reduce((accu, cur)=>{
                    const item={
                        amount: accu.amount + cur.amount,
                        price: accu.price + cur.price,           
                        discount:(accu.amount + cur.amount) - (accu.price + cur.price)    
                    }
                   
                    return item;
                })
                //total_discount : accu.total_discount ? accu.total_discount : 0 + cur.total_discount ? cur.total_discount : 0, 
               // extra_discount : accu.extra_discount ? accu.extra_discount : 0 + cur.extra_discount ? cur.extra_discount : 0  
               
               this.total_amount += res3.amount;
               this.total_discount += res3.discount;
               this.total_price += res3.price;
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
        async fetchPurchase(){           
           // console.log(from_date, to_date)
           let query_url="";
           if(this.supplier){
            console.log("supplier")
            query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid),            
            where('supplier.code',"==", Number(this.supplier)),
             orderBy("pur_date", "desc"));                                         
            }else{
                query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid), 
                 orderBy("pur_date", "desc"));     
            }
            const shapShot=await getDocs(query_url)
            let purchaseItems=[];
            shapShot.forEach((doc) => {         
                let purchase = {
                    id: doc.id,
                    uid: doc.data().uid,
                    voucher_no: doc.data().voucher_no,
                   // sale_date : doc.data().sale_date,
                    products : doc.data().products,    
                    pur_date:doc.data().pur_date,
                    supplier: doc.data().supplier,
                      //  charges: doc.data().charges,
                     //   pay:doc.data().pay,
                    total_discount: doc.data().total_discount,
                     //   refunds: doc.data().refunds,
                    total_amount:doc.data().total_amount,
                    total_price: doc.data().total_price,
                    extra_discount: doc.data().extra_discount,
                     //   sale_type: doc.data().sale_type,
                       // customer : doc.data().customer,
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
