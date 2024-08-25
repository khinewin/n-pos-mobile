const purchases=(arr)=>{
    let purchaseItems=[];
    arr.forEach((doc) => {         
        let purchase = {
            id: doc.id,
            user: doc.data().user,
            voucher_no: doc.data().voucher_no,
            pur_date : doc.data().pur_date,
            supplier: doc.data().supplier,
            products : doc.data().products,    
            total_amount:doc.data().total_amount,
            total_discount: doc.data().total_discount,
            extra_discount: doc.data().extra_discount,
            total_price:doc.data().total_price,     
            exp_date: doc.data().exp_date,         
            location:doc.data().location,                              
        };                                       
        purchaseItems.push(purchase);                          
    });
    return purchaseItems;
}
export {purchases}