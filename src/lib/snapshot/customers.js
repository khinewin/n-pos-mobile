const customers=(arr)=>{
    let customerItems=[];
     arr.forEach((doc) => {         
        let cus = {
            id: doc.id,
            uid: doc.data().uid,
            name: doc.data().name,
            purchase_price : doc.data().purchase_price,
            address : doc.data().address,
            created_at: doc.data().created_at,
            phone : doc.data().phone,
            code : doc.data().code,                                
        };                                
        customerItems.push(cus);                                              
    });
    return customerItems;
}
export {customers}