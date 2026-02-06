const adjustments=(arr)=>{
    let adjItems=[];
    arr.forEach((doc) => {         
        let item = {
            id: doc.id,
            ...doc.data()                                       
        };                                          
        adjItems.push(item);                           
    });
    return adjItems;
}
export {adjustments}