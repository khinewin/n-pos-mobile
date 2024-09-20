const stocks=(arr)=>{
    let stockItems=[];
    arr.forEach((doc) => {         
        let stock = {
            id: doc.id,
            user: doc.data().user,
            voucher_no: doc.data().voucher_no,
            created_at : doc.data().created_at,          
            products : doc.data().products,    
            edit_user: null,
            remark:doc.data().remark,
          /*  total_retail_amount:doc.data().total_retail_amount,
            total_whole_sale_amount: doc.data().total_whole_sale_amount,    
            total_main_retail_amount:doc.data().total_main_retail_amount,
            total_main_whole_sale_amount: doc.data().total_main_whole_sale_amount,     
            total_pur_amount: doc.data().total_pur_amount,      */  
                                         
        };                                          
        stockItems.push(stock);                           
    });
    return stockItems;
}
export {stocks}