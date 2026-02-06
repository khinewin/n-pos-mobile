const purchases=(arr)=>{
    let purchaseItems=[];
    arr.forEach((doc) => {         
        let purchase = {
            id: doc.id,
            ...doc.data()             
        };                                       
        purchaseItems.push(purchase);                          
    });
    return purchaseItems;
}
export {purchases}