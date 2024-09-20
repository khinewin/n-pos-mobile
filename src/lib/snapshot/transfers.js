const transfers=(arr)=>{
    let transferItems=[];
    arr.forEach((doc) => {         
        let item = {
            id: doc.id,
            user: doc.data().user,
            voucher_no: doc.data().voucher_no,
            created_at : doc.data().created_at,
            products : doc.data().products,    
            from_location: doc.data().from_location,
            to_location:doc.data().to_location,              
            remark: doc.data().remark,     
        };                                       
        transferItems.push(item);                          
    });
    return transferItems;
}
export {transfers}