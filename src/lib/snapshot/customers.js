const customers=(arr)=>{
    let customerItems=[];
     arr.forEach((doc) => {      
        let cus = {
            id: doc.id,
            ...doc.data()
        };                                
        customerItems.push(cus);                                              
    });
    return customerItems;
}
export {customers}