const purchases=(arr)=>{
    let purchaseItems=[];
    arr.forEach((doc) => {         
        const data=doc.data();
        let purchase = {
            id: doc.id,
            user: data.user,
            voucher_no: data.voucher_no,
            pur_date : data.pur_date,
            supplier: data.supplier,
            products : data.products,    
            total_amount:data.total_amount,
            total_discount: data.total_discount,
            extra_discount: data.extra_discount,
            total:data.total,     
            grand_total_price: data.grand_total_price,
            paid: data.paid,
            balance: data.balance,
            location:data.location,   
            remark:data.remark,  
            edit_user:null                         
        };                                       
        purchaseItems.push(purchase);                          
    });
    return purchaseItems;
}
export {purchases}