const packingParentSizes=(arr)=>{
    let sizes=[];
     arr.forEach((doc) => {         
        let size = {
            id: doc.id,
            user: doc.data().user,
            parent_unit: doc.data().parent_unit,
            packing: doc.data().packing,
            child_unit: doc.data().child_unit                 
        };                                
        if(size.parent_unit){
            sizes.push(size);       
        }                                       
    });
    return sizes;
}
const packingChildSizes=(arr)=>{
    let sizes=[];
     arr.forEach((doc) => {         
        let size = {
            id: doc.id,
            user: doc.data().user,
            parent_unit: doc.data().parent_unit,
            packing: doc.data().packing,
            child_unit: doc.data().child_unit                 
        };                                
        if(size.child_unit){
            sizes.push(size);
        }                                              
    });
    return sizes;
}
export {packingParentSizes, packingChildSizes}