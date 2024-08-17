<template lang="">        

    <div>           
        <PageHeader page-title="Dashboard" page-code="dashboard"/>
    </div>   
 
</template>
<script>
import PageHeader from "@/views/partials/PageHeader.vue"
export default {
    components: {
        PageHeader
    },
    computed:{
        user(){
            return this.$store.getters.user;
        }
    },
    methods:{
        /*
        async getSalesHistory(){
            const query_url = query(collection(db, "products"), where("uid", "==", this.user.uid));
            const snapShot=await getDocs(query_url)
            let sales_history=[];
            snapShot.forEach((doc) => {         
              doc.data().history.sales.map((res)=>{
                    sales_history.push(res)
              })                            
                            
            });
          
            console.log(sales_history)
            
        },
       async getProducts(){
        const query_url = query(collection(db, "products"), where("uid", "==", this.user.uid));
        const snapShot=await getDocs(query_url)
        let products=[];
        snapShot.forEach((doc) => {         
            let product = {
                                id: doc.id,
                                uid: doc.data().uid,
                                item_name: doc.data().name,
                                regular_sale_price : doc.data().regular_sale_price,
                                whole_sale_price: doc.data().whole_sale_price,
                                qty : doc.data().qty,
                                created_at : doc.data().created_at,
                                code_no : doc.data().code_no,
                            };
                            products.push(product);                             
                        
         });
         console.log(products)
       } ,

       async saveProduct(){       
            const save_time=Timestamp.fromDate(new Date());
            const dataCollection=collection(db, "products", )
            const code_no=this.code_no ? this.code_no : new Date().getTime()
            const data={
                uid: this.user.uid,
                item_name: "Yum Yum kw", 
                code_no : code_no,
                packing_format: 10,
                qty : 35,
                regular_sale_price: 2000,
                whole_sale_price: 1900,
                created_at : save_time,
                history : {
                    purchase : [
                        { qty : 50, pur_price: 1500, pur_date:  save_time, item_name : "Yum Yum kw", code_no : code_no }
                    ],
                    sales:[
                        { qty : 5, whole_sale_price: 1900, sale_date:  save_time, item_name : "Yum Yum kw", code_no : code_no},
                        { qty : 10, regular_sale_price: 2000, sale_date:  save_time, item_name : "Yum Yum kw", code_no : code_no}
                    ]
                }
            }
            const result=await addDoc(dataCollection, data);
            if(result){
                console.log("success")
            }else{
                console.log("failed")
            }
               
        }
            */
    }
}
</script>
<style lang="">
    
</style>