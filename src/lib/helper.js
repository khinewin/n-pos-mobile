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


    const calPackingQty=(m_unit_qty, s_unit_qty, qty, l_unit, m_unit, s_unit)=>{
        if(l_unit){
            const l=Math.floor(qty / (m_unit_qty * s_unit_qty))
            const m=Math.floor((qty % (m_unit_qty * s_unit_qty)) / s_unit_qty)
            const s=(qty % (m_unit_qty * s_unit_qty)) % s_unit_qty

            const ll=`${l ? l : ''}${l ? l_unit : ''}`
            const mm=`${m ? m : ''}${m ? m_unit : ''}`
            const ss=`${s ? s : ''}${s ? s_unit : ''}`

            return `${ll}${l && m ? ", ": ""}${mm}${m && s ? ", ":""}${ss}`
        }
        if(!l_unit){
            const m=Math.floor((qty / s_unit_qty))
            const s =Math.floor(qty % s_unit_qty)

            const mm=`${m ? m : ''}${m ? m_unit : ''}`
            const ss=`${s ? s : ''}${s ? s_unit : ''}`

             return `${mm}${m && s ? ", ":""}${ss}`
        }
    }

  const  calPackingSize=(l_unit, m_unit, s_unit, m_unit_qty, s_unit_qty)=>{
        if(l_unit){
            return `1${l_unit} * ${m_unit_qty}${m_unit} * ${s_unit_qty}${s_unit}`
        }
        if(!l_unit){
            return `1${m_unit} * ${s_unit_qty}${s_unit}`
        }
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
const gb_item_name_adj_type=groupBy(['item_name', 'adj_type'])
const gb_location=groupBy(['location'])
const gb_adj_type=groupBy(['adj_type'])

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
    gb_item_name_adj_type,
    gb_location,
    gb_adj_type,
    calPackingQty,
    calPackingSize,
    };
