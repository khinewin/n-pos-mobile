const suppliers=(arr)=>{
    let supplierItems=[];
    arr.forEach((doc) => {         
        let product = {
            id:doc.id,
            user: doc.data().user,
            name: doc.data().name,
            company : doc.data().company,
            created_at: doc.data().created_at,
            phone : doc.data().phone,
            code : doc.data().code,     
            edit_user: null,                           
        };                                
        supplierItems.push(product);                                               
    });
    return supplierItems;
}
export {suppliers}