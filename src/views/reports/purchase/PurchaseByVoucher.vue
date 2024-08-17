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
         <div class="row mb-2" v-if="reports" v-for="p in reports">
            <div class="mb-2 col-12">
                <div class="card shadow">
                    <div class="card-body">
                <div class="table-responsive">
                    <div class="row  mb-2">
                    <div class="col-lg-12">
                        <table class="table mb-0 table-borderless">
                            <tbody>
                                <tr class="">
                                    <td class="text-end pb-0">Voucher no. :</td>
                                    <td class="pb-0" style="border-bottom: 1px dotted;">
                                        {{p.voucher_no}}
                                    </td>                              
                                    <td class="text-end pb-0">Pur date :</td>
                                    <td class="pb-0" v-if="p.pur_date" style="border-bottom: 1px dotted;">
                                        {{showTime(p.pur_date)}}
                                    </td>                              
                                    <td class="text-end pb-0">Supplier :</td>
                                    <td class="pb-0" style="border-bottom: 1px dotted;">
                                        {{p.supplier.name}} ({{p.supplier.company}})
                                    </td>
                                </tr>
                            </tbody>
                      </table>
                    </div>
                </div>                 
                   <table class="table table-bordered  table-hover">
                        <tbody>
                            <tr class="small text-center fw-bold table-secondary">
                                <td class="">Code</td>
                                <td class="">Desc</td>
                                <td class="">Exp. date</td>
                                <td class="" colspan="2">Qty</td>
                                <td class="">Item price</td>
                                <td colspan="3" class="col-2">Discount</td>
                                <td class="">Amount</td>
                                <td class="">Discount</td>
                                <td class="">Price</td>
                            </tr>
                            <tr class="small text-center" v-if="p.products.length > 0" v-for="st in p.products" :key="st.id">
                                <td class="">{{st.code_no}}</td>
                                <td class="">{{st.item_name}}</td>
                                <td class="">                                   
                                     <span v-if="st.exp_date">{{showTime(st.exp_date)}}</span>
                                </td>
                                <td class="">{{st.pur_qty}}</td>                                
                                <td class="">
                                    {{Math.floor(st.pur_qty / st.item_per_package)}} x {{Math.floor(st.pur_qty % st.item_per_package)}}
                                </td>
                                <td class="">{{st.pur_price}}</td>
                                <td>{{st.dis_qty}}</td>
                                <td class="">{{st.dis_percent}}</td>
                                <td class="">{{st.dis_amount}}</td>
                                <td class="">{{st.amount}}</td>
                                <td class="">{{st.discount}}</td>
                                <td class="">{{st.price}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="9" class="text-end">Amount</td>
                                <td class="text-center">{{p.total_amount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="9" class="text-end">Discount</td>
                                <td class="text-center ">{{p.total_discount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="9" class="text-end">Extra discount</td>
                                <td class="text-center">{{p.extra_discount}}</td>
                            </tr>
                            <tr class="small">
                                <td colspan="9" class="text-end">Total price</td>
                                <td class="text-center">{{p.total_price}}</td>
                            </tr>                           
                        </tbody>
                   </table>                          
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
    props:['fromDate', 'toDate', 'voucher_no', 'supplier'],
    components: {
    },
  
    data(){
        return{       
           
            reports:[],
          //  current_month:[],
            purchaseData:[],
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
           if(this.voucher_no){
             query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid), 
            where("voucher_no", "==", Number(this.voucher_no)));
           }else if(this.supplier){
            query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid), 
            where("supplier.code", "==", Number(this.supplier)));
           }else{
             query_url = query(collection(db, "purchases"), where("uid", "==", this.user.uid), 
            where("pur_date", ">=", from_date), where("pur_date", "<=", to_date), orderBy("pur_date", "desc"));
           }           
            const shapShot=await getDocs(query_url)
            let purchaseItems=[];
            shapShot.forEach((doc) => {         
                let pur = {
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
                                
                purchaseItems.push(pur);      
            });
        this.reports=purchaseItems;
        this.isLoading=false;
        }
    }
}
</script>
<style lang="">
    
</style>
