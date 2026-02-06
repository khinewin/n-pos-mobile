
const packingSizes=(arr)=>{
    let sizes=[];
     arr.forEach((doc) => {         
        let size = {
            id: doc.id,
           ...doc.data()
        };                                       
        sizes.push(size);                                                    
    });
    return sizes;
}
export {packingSizes}