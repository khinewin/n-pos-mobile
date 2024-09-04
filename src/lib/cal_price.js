const calRetailPrice=(unit, editItem)=>{
    const er=editItem.retail_price;
    const ep=editItem.packing_size;
    if(editItem.auto_cal){
        switch(unit){
            case "largest_unit":
                editItem.retail_price.medium_unit = Math.floor(er.largest_unit / ep.medium_qty)
                editItem.retail_price.smallest_unit = Math.floor((er.largest_unit / ep.medium_qty) / ep.smallest_qty)
            break;
            case "medium_unit":
                editItem.retail_price.largest_unit = Math.floor(er.medium_unit * ep.medium_qty)
                editItem.retail_price.smallest_unit = Math.floor(er.medium_unit / ep.smallest_qty)
            break;
            case "smallest_unit":
                editItem.retail_price.largest_unit = Math.floor((er.smallest_unit * ep.smallest_qty) * ep.medium_qty)
                editItem.retail_price.medium_unit = Math.floor(er.smallest_unit * ep.smallest_qty)
            break;
        }
    }
}
const calPurPrice=(unit,editItem)=>{
    const er=editItem.pur_price;
    const ep=editItem.packing_size;
    if(editItem.auto_cal){
        switch(unit){
            case "largest_unit":
                editItem.pur_price.medium_unit = Math.floor(er.largest_unit / ep.medium_qty)
                editItem.pur_price.smallest_unit = Math.floor((er.largest_unit / ep.medium_qty) / ep.smallest_qty)
            break;
            case "medium_unit":
                editItem.pur_price.largest_unit = Math.floor(er.medium_unit * ep.medium_qty)
                editItem.pur_price.smallest_unit = Math.floor(er.medium_unit / ep.smallest_qty)
            break;
            case "smallest_unit":
                editItem.pur_price.largest_unit = Math.floor((er.smallest_unit * ep.smallest_qty) * ep.medium_qty)
                editItem.pur_price.medium_unit = Math.floor(er.smallest_unit * ep.smallest_qty)
            break;
        }
    }
}
const calWholesalePrice=(unit,editItem)=>{
    const er=editItem.whole_sale_price;
    const ep=editItem.packing_size;
    if(editItem.auto_cal){
        switch(unit){
            case "largest_unit":
                editItem.whole_sale_price.medium_unit = Math.floor(er.largest_unit / ep.medium_qty)
                editItem.whole_sale_price.smallest_unit = Math.floor((er.largest_unit / ep.medium_qty) / ep.smallest_qty)
            break;
            case "medium_unit":
                editItem.whole_sale_price.largest_unit = Math.floor(er.medium_unit * ep.medium_qty)
                editItem.whole_sale_price.smallest_unit = Math.floor(er.medium_unit / ep.smallest_qty)
            break;
            case "smallest_unit":
                editItem.whole_sale_price.largest_unit = Math.floor((er.smallest_unit * ep.smallest_qty) * ep.medium_qty)
                editItem.whole_sale_price.medium_unit = Math.floor(er.smallest_unit * ep.smallest_qty)
            break;
        }
    }
}

export {
    calRetailPrice,
    calWholesalePrice,
    calPurPrice
}