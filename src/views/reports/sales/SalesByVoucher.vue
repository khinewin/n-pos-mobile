<template lang="">
    <div class="container-fluid">  
        <div v-if="isLoading">
            <div colspan="5">
                <div class="d-flex justify-content-center my-5">
                    <div class="spinner-border text-primary"  role="status">
                      <span class="visually-hidden">Loading...</span>
                    </div>
                </div>
            </div>
        </div>  
         <div class="row mb-2" v-if="reports" v-for="sale in reports">
            <div class="col-12 mb-2">
                <div class="card shadow">
                    <div class="card-body">
                        <div class="row justify-content-end" >   
                            <div class="col-6">
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">  
                                        <tr>
                                            <td class="col-4 pb-0 fw-bold">Voucher no. <span class="float-end">:</span></td>
                                            <td class="col-8  ps-2 pb-0" style="border-bottom: 1px dotted;">
                                                {{sale.voucher_no}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class="pb-0 fw-bold">Date <span class="float-end">:</span></td>
                                            <td class=" ps-2 pb-0" style="border-bottom: 1px dotted;">
                                               <span>{{showTime(sale.sale_date)}}</span>
                                            </td>
                                        </tr>       
                                    </tbody>
                                </table>
                            </div>        
                            <div class="col-6">                    
                                <table class="table mb-0 table-borderless">
                                    <tbody class="small">                                     
                                        <tr>
                                            <td class="  col-4 pb-0 fw-bold">Name <span class="float-end">:</span></td>
                                            <td class=" ps-2 col-8 pb-0" style="border-bottom: 1px dotted;">
                                                {{sale.customer.name}}
                                            </td>
                                        </tr>      
                                        <tr>
                                            <td class="pb-0 fw-bold">Phone <span class="float-end">:</span></td>
                                            <td class=" ps-2 pb-0" style="border-bottom: 1px dotted;">
                                                {{sale.customer.phone}}
                                            </td>
                                        </tr>                                               
                                    </tbody>
                              </table>
                            </div>
                            <div class="col-12">
                                <table class="table table-bordered table-hover border-dark mt-2 mb-0">
                                    <tbody class="small">
                                        <tr class="text-center fw-bold">
                                            <td class="col-4  py-1">Description</td>
                                            <td class="col-4   py-1">Qty</td>
                                            <td class="col-2   py-1">Price</td>
                                            <td class="col-2   py-1">Amount</td>
                                        </tr>
                                        <tr class="text-center" v-if="sale.products.length > 0" v-for="(s, i) in sale.products" :key="s.id" >                        
                                            <td class="py-1">{{s.item_name}}</td>
                                            <td class="  py-1">
                                                {{s.sale_qty}}
                                            </td>
                                            <td class="  py-1">{{s.sale_price}}</td>
                                            <td class="  py-1">{{s.sale_amount}}</td>
                                        </tr>                        
                                  
                                        <tr>
                                            <td colspan="3" class="text-end col-6  py-1 pe-2 " style="border-bottom-style: hidden;">Total amount</td>
                                            <td class="text-center col-6   py-1">{{sale.total_amount}}</td>
                                        </tr>                     
                                        <tr>
                                            <td colspan="3" class="text-end  py-1  pe-2 " style="border-bottom-style: hidden;">Discount</td>
                                            <td class="text-center   py-1">
                                                {{sale.discount}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="3" class="text-end   py-1  pe-2 " style="border-bottom-style: hidden;">Charges</td>
                                            <td class="text-center   py-1">{{sale.charges}}</td>
                                        </tr>
                                        <tr >
                                            <td colspan="3" class="text-end   py-1  pe-2 " style="border-bottom-style: hidden;">Pay</td>
                                            <td class="text-center  py-1">
                                                {{sale.pay}}
                                            </td>
                                        </tr>
                                        <tr>
                                            <td colspan="3" class="text-end   py-1  pe-2 " >Refunds</td>
                                            <td class="text-center   py-1">
                                                {{sale.refunds}}
                                            </td>
                                        </tr>                           
                                    </tbody>
                              </table>
                              <table>
                                    <tbody>
                                        <tr>
                                            <td></td>
                                        </tr>
                                    </tbody>
                              </table>
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
    props:['fromDate', 'toDate', 'voucher_no','customer_code'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            salesData:[],
            grand_total:"",
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
           let query_url="";
           if(this.voucher_no){
             query_url = query(collection(db, "sales"), where("uid", "==", this.user.uid), 
            where("voucher_no", "==", Number(this.voucher_no)));
           }else 
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
        this.reports=saleItems;
        this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
