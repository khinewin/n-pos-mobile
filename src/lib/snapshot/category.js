const category=(arr)=>{
    let catItems=[];
     arr.forEach((doc) => {      
        const data=doc.data();   
        let cus = {
            id: doc.id,
            ...doc.data()
        };                                
        catItems.push(cus);                                              
    });
    return catItems;
}
export {category}