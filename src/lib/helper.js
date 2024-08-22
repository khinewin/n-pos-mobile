const groupBy = keys => array =>
    array.reduce((objectsByKeyValue, obj) => {
        const value = keys.map(key => obj[key]).join('-');
        objectsByKeyValue[value] = (objectsByKeyValue[value] || []).concat(obj);
        return objectsByKeyValue;
}, {});

const showTime=(t)=>{
    const date=t.toDate()
    const options = { year: 'numeric', month: 'numeric', day: 'numeric' };
    const formattedDate = new Intl.DateTimeFormat('en-UK', options).format(date);
    return formattedDate;
};

const calQtyL=(qty, ipp)=>{
    return Math.floor(qty / ipp) 
}
const calQtyS=(qty, ipp)=>{
    return Math.floor(qty % ipp) 
}


const gb_date=groupBy(['group_date'])
const gb_item_name_sale_price=groupBy(['item_name', 'sale_price'])
const gb_item_name_retail_price=groupBy(['item_name', 'retail_price']);
const gb_month=groupBy(['group_month'])
const gb_customer=groupBy(['group_customer'])
const gb_supplier=groupBy(['group_supplier'])
const gb_code=groupBy(['code_no'])
const gb_item_name_pur_price=groupBy(['item_name', 'pur_price'])
const gb_exp_date=groupBy(['group_exp_date'])

export   {
    showTime,
    gb_code,
    gb_date, 
    gb_month, 
    gb_item_name_sale_price,
    gb_customer, 
    gb_supplier,
    gb_item_name_retail_price,
    gb_item_name_pur_price,
    gb_exp_date,
    calQtyL,
    calQtyS,
    };
