const products=(snapShot)=>{    
    let products=[];
    snapShot.forEach((doc) => {       
        const data=doc.data();  
        let product = {
            id: doc.id,
            user: data.user,
            item_name: data.item_name,
            qty : data.qty,
            created_at : data.created_at,
            code_no : data.code_no,                        
            main_qty: data.main_qty, 
            packing_size: data.packing_size ? data.packing_size : {},
            retail_price : data.retail_price ? data.retail_price : {},
            whole_sale_price : data.whole_sale_price ? data.whole_sale_price : {},
            pur_price : data.pur_price ? data.pur_price : {},
            auto_cal: data.auto_cal ? data.auto_cal : false,

        };                                
        products.push(product);                           
    });
    return products;
} 

export {
    products
} 