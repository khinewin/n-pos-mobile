const products=(snapShot)=>{    
    let products=[];
    snapShot.forEach((doc) => {       
        const data=doc.data();  
        let product = {
            id: doc.id,
            ...doc.data()
        };                                
        products.push(product);                           
    });
    return products;
} 

export {
    products
} 