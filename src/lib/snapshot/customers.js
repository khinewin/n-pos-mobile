const customers=(arr)=>{
    let customerItems=[];
     arr.forEach((doc) => {      
        const data=doc.data();   
        let cus = {
            id: doc.id,
            user: data.user,
            name: data.name,
            address : data.address,
            created_at: data.created_at,
            phone : data.phone,
            code : data.code,      
            is_deleted: data.is_deleted,
            edit_user:  null ,
            name_lower_case: data.name_lower_case,
            name_keywords: data.name_keywords,
            address_keywords: data.address_keywords
        };                                
        customerItems.push(cus);                                              
    });
    return customerItems;
}
export {customers}