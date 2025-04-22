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
            is_deleted : doc.data().is_deleted,
            edit_user: null,        
            name_lower_case: doc.data().name_lower_case ? doc.data().name_lower_case : "",
            name_keywords: doc.data().name_keywords ? doc.data().name_keywords : "",
            company_keywords: doc.data().company_keywords ? doc.data().company_keywords: ""                
        };                                
        supplierItems.push(product);                                               
    });
    return supplierItems;
}
export {suppliers}