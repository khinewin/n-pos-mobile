const suppliers=(arr)=>{
    let supplierItems=[];
    arr.forEach((doc) => {         
        let product = {
            id:doc.id,
           ...doc.data()             
        };                                
        supplierItems.push(product);                                               
    });
    return supplierItems;
}
export {suppliers}