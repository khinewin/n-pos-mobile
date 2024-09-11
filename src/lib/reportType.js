const displayReportType=(reportType)=>{
    if(reportType==="by_date"){
        return "by Date"
    }else 
    if(reportType==='by_voucher'){
        return "by Voucher"
    }else
    if(reportType==='by_description'){
        return "by Description"
    }else
    if(reportType==='by_description_date'){
        return "by Description and Date"
    }else
    if(reportType==='by_customer'){
        return "by Customer"
    }else
    if(reportType==='by_customer_date'){
        return "by Customer and Date"
    }else
    if(reportType==='by_chart'){
        return "by Chart"
    }else
     if(reportType==='by_location'){
        return "by Location"
   }else
    if(reportType==='by_adj_type'){
        return "by Type"
   }
   else
    if(reportType==='by_supplier'){
        return "by Supplier"
    }else
     if(reportType==='by_supplier_date'){
        return "by Supplier and Date"
   }else
    if(reportType==='by_exp_date'){
        return "by Expire date"
   }
}
   
export {
    displayReportType
}