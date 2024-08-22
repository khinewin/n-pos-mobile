const suppliers=(arr)=>{
    let supplierItems=[];
    arr.forEach((doc) => {         
        let product = {
            user: doc.user,
            user: doc.data().user,
            name: doc.data().name,
            company : doc.data().company,
            created_at: doc.data().created_at,
            phone : doc.data().phone,
            code : doc.data().code,                                
        };                                
        supplierItems.push(product);                                               
    });
    return supplierItems;
}
export {suppliers}