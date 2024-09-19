const returnOut=(arr)=>{
    let rtOutIitems=[];
    arr.forEach((doc) => {     
        const data=doc.data();    
        let sale = {
            id: doc.id,
            user: data.user,
            voucher_no: data.voucher_no,
            rt_out_date : data.rt_out_date,
            products : data.products,    
             paid:data.paid,
             total_amount:data.total_amount,
             supplier : data.supplier,
             balance: data.balance,
             remark: data.remark,
             location: data.location,
        };                                     
        rtOutIitems.push(sale); 
    })
    return rtOutIitems;    
}
export {returnOut}