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
            remark: doc.data().remark,                                          
        };                                          
        adjItems.push(item);                           
    });
    return adjItems;
}
export {adjustments}