const sales=(arr)=>{
    let saleItems=[];
    arr.forEach((doc) => {     
        let sale = {
            id: doc.id,
           ...doc.data()
        };                                     
        saleItems.push(sale); 
    })
    return saleItems;    
}
export {sales}