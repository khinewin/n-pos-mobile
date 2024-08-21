const products=(snapShot)=>{    
    let products=[];
    snapShot.forEach((doc) => {         
        let product = {
            id: doc.id,
            uid: doc.data().uid,
            item_name: doc.data().item_name,
            retail_price : doc.data().retail_price,
            whole_sale_price: doc.data().whole_sale_price,
            qty : doc.data().qty,
            created_at : doc.data().created_at,
            code_no : doc.data().code_no,                
            item_per_package: doc.data().item_per_package,
            pur_price:doc.data().pur_price,       
        };                                
        products.push(product);                           
    });
    return products;
} 

export {
    products
} 