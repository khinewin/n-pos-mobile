const displayReportType=(reportType)=>{
    if(reportType==="by_date"){
        return "by Date"
    }else 
    if(reportType==='by_voucher'){
        return "by Voucher"
    }else
    if(reportType==='by_description'){
        return "by Product Name"
    }else
    if(reportType==='by_description_date'){
        return "by Product Name and Date"
    }else
    if(reportType==='by_description_month'){
        return "by Product Name and Month"
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
   }else
    if(reportType==='by_category'){
        return "by Category"
   }else
    if(reportType==='by_month'){
        return "by Month"
    }else
    if(reportType==='by_year'){
        return "by Year"
    }else
    if(reportType==='by_top_ten'){
        return "by Top 10 (Daily)"
    }else
    if(reportType==='by_top_ten_month'){
        return "by Top 10 (Monthly)"
    }else
    if(reportType==='by_top_ten_year'){
        return "by Top 10 (Yearly)"
    }else
    if(reportType==='by_balance'){
        return "by Balance Voucher"
   }
}
   
export {
    displayReportType
}