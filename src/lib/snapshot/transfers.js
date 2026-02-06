const transfers=(arr)=>{
    let transferItems=[];
    arr.forEach((doc) => {         
        let item = {
            id: doc.id,
           ...doc.data()    
        };                                       
        transferItems.push(item);                          
    });
    return transferItems;
}
export {transfers}