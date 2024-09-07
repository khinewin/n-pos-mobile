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
            edit_user:  null  
        };                                
        customerItems.push(cus);                                              
    });
    return customerItems;
}
export {customers}