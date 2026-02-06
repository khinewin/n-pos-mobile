const payments=(arr)=>{
    let items=[];
    arr.forEach((doc) => {     
        let item = {
            id: doc.id,
           ...doc.data()
        };                                     
        items.push(item); 
    })
    return items;    
}
export {payments}