const searchByDescription=(arr, query)=>{
    const indexData = new Map(arr.map(item => [item.item_name.toLowerCase(), item]));
    const q = query.toLowerCase();
    return [...indexData.values()].filter(item => item.item_name.toLowerCase().includes(q)).slice(0,100);   
}
const searchByCode=(arr, query)=>{
    const indexData = new Map(arr.map(item => [item.code_no, item]));
    return [...indexData.values()].filter(item => item.code_no==query);   
}

const searchCustomerName=(arr, query)=>{
    const indexData = new Map(arr.map(item => [item.name.toLowerCase(), item]));
    const q = query.toLowerCase();
    return [...indexData.values()].filter(item => item.name.toLowerCase().includes(q)).slice(0,100);    
}
const searchCustomerAddress=(arr, query)=>{
    const indexData = new Map(arr.map(item => [item.address.toLowerCase(), item]));
    const q = query.toLowerCase();
    return [...indexData.values()].filter(item => item.address.toLowerCase().includes(q)).slice(0,100);    
}

const searchSupplierName=(arr, query)=>{
    const indexData = new Map(arr.map(item => [item.name.toLowerCase(), item]));
    const q = query.toLowerCase();
    return [...indexData.values()].filter(item => item.name.toLowerCase().includes(q)).slice(0,100);    
}
const searchSupplierCompany=(arr, query)=>{
    const indexData = new Map(arr.map(item => [item.company.toLowerCase(), item]));
    const q = query.toLowerCase();
    return [...indexData.values()].filter(item => item.company.toLowerCase().includes(q)).slice(0,100);    
}
export {
    searchByDescription,
    searchByCode,
    searchCustomerAddress, 
    searchCustomerName,
    searchSupplierCompany,
    searchSupplierName
}
