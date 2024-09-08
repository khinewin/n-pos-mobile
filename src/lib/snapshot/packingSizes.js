
const packingSizes=(arr)=>{
    let sizes=[];
     arr.forEach((doc) => {         
        let size = {
            id: doc.id,
            user: doc.data().user,
            packing_unit: doc.data().packing_unit,        
            packing_name: doc.data().packing_name,      
            created_at: doc.data().created_at,   
            edit_user: null,     
        };                                       
        sizes.push(size);                                                    
    });
    return sizes;
}
export {packingSizes}