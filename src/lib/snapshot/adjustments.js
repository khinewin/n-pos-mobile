const adjustments=(arr)=>{
    let adjItems=[];
    arr.forEach((doc) => {         
        let item = {
            id: doc.id,
            user: doc.data().user,
            voucher_no: doc.data().voucher_no,
            location: doc.data().location,
            created_at : doc.data().created_at,          
            products : doc.data().products,    
            total_retail_amount:doc.data().total_retail_amount,
            total_pur_amount: doc.data().total_pur_amount,   
            total_whole_sale_amount: doc.data().total_whole_sale_amount,                                  
        };                                          
        adjItems.push(item);                           
    });
    return adjItems;
}
export {adjustments}