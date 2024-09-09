const payments=(arr)=>{
    let items=[];
    arr.forEach((doc) => {     
        const data=doc.data();    
        let item = {
            id: doc.id,
            user: data.user,
            paid_for: data.paid_for,
            paid_amount: data.paid_amount,
            paid_date: data.paid_date,
            customer: data.customer,
        };                                     
        items.push(item); 
    })
    return items;    
}
export {payments}