const sales=(arr)=>{
    let saleItems=[];
    arr.forEach((doc) => {     
        const data=doc.data();    
        let sale = {
            id: doc.id,
            user: data.user,
            voucher_no: data.voucher_no,
            sale_date : data.sale_date,
            products : data.products,    
             net_amount: data.net_amount,
             paid:data.paid,
             extra_discount: data.extra_discount ? data.extra_discount : 0,
             change: data.change,
             total_amount:data.total_amount,
             sale_type: data.sale_type,
             customer : data.customer,
             credit: data.credit,
             payment_type: data.payment_type ? data.payment_type : "Paid",
             debt_payments: data.debt_payments ? data.debt_payments : []
        };                                     
        saleItems.push(sale); 
    })
    return saleItems;    
}
export {sales}