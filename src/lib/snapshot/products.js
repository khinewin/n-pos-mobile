const products=(snapShot)=>{    
    let products=[];
    snapShot.forEach((doc) => {         
        let product = {
            id: doc.id,
            user: doc.data().user,
            item_name: doc.data().item_name,
            qty : doc.data().qty,
            created_at : doc.data().created_at,
            code_no : doc.data().code_no,                        
            main_qty: doc.data().main_qty, 
            packing_size: doc.data().packing_size ? doc.data().packing_size : {},
            retail_price : doc.data().retail_price ? doc.data().retail_price : {},
            whole_sale_price : doc.data().whole_sale_price ? doc.data().whole_sale_price : {},
            pur_price : doc.data().pur_price ? doc.data().pur_price : {}

        };                                
        products.push(product);                           
    });
    return products;
} 

export {
    products
} 