const sales=(arr)=>{
    let saleItems=[];
    arr.forEach((doc) => {         
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
    })
    return saleItems;    
}
export {sales}