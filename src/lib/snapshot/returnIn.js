const returnIn=(arr)=>{
    let rtInItems=[];
    arr.forEach((doc) => {     
        const data=doc.data();    
        let sale = {
            id: doc.id,
            user: data.user,
            voucher_no: data.voucher_no,
            rt_in_date : data.rt_in_date,
            products : data.products,    
             paid:data.paid,
             change: data.change,
             total_amount:data.total_amount,
             customer : data.customer,
             balance: data.balance,
             payment_type: data.payment_type,
             remark: data.remark,
        };                                     
        rtInItems.push(sale); 
    })
    return rtInItems;    
}
export {returnIn}