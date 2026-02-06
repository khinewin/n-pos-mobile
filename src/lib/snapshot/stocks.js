const stocks=(arr)=>{
    let stockItems=[];
    arr.forEach((doc) => {         
        let stock = {
            id: doc.id,
           ...doc.data()    
                                         
        };                                          
        stockItems.push(stock);                           
    });
    return stockItems;
}
export {stocks}