const handleCtrl=(event)=> {
    /*if (event.ctrlKey && event.key === 'h') {
        event.preventDefault(); // Prevent the default browser save dialog
       const closeProduct=document.querySelector(".close-product")
        closeProduct !==null &&  closeProduct.click();
        const closeSupplier=document.querySelector(".close-supplier")                
        closeSupplier!==null && closeSupplier.click();
        const closeCustomer=document.querySelector(".close-customer")
        closeCustomer!==null &&  closeCustomer.click();
        const closeSale=document.querySelector(".close-sale")
        closeSale !==null && closeSale.click();
        const closePurchase=document.querySelector(".close-purchase")
        closePurchase !==null && closePurchase.click();
        const closeStock=document.querySelector(".close-stock")
        closeStock !==null && closeStock.click();
        const closeTransfer=document.querySelector(".close-transfer")
        closeTransfer !==null && closeTransfer.click();
        const closeAdj=document.querySelector(".close-adj")
        closeAdj !==null && closeAdj.click();
        const closeRtin=document.querySelector(".close-rtin")
        closeRtin !==null && closeRtin.click();
        const closeRtout=document.querySelector(".close-rtout")
        closeRtout !==null && closeRtout.click();
    }*/
    if (event.ctrlKey && event.key === 'z') {
        event.preventDefault(); // Prevent the default browser save dialog
       const clearSearchProduct=document.querySelector(".clear-search-product")
        clearSearchProduct !==null && clearSearchProduct.click();
        const clearSearchSupplier=document.querySelector(".clear-search-supplier")
        clearSearchSupplier !==null && clearSearchSupplier.click();
        const clearSearchCustomer=document.querySelector(".clear-search-customer")
        clearSearchCustomer !==null && clearSearchCustomer.click();
        const clearSearchSale=document.querySelector(".clear-search-sale")
        clearSearchSale !==null && clearSearchSale.click();
        const clearSearchPurchase=document.querySelector(".clear-search-purchase")
        clearSearchPurchase !==null && clearSearchPurchase.click();
        const clearSearchStock=document.querySelector(".clear-search-stock")
        clearSearchStock !==null && clearSearchStock.click();
        const clearSearchTransfer=document.querySelector(".clear-search-transfer")
        clearSearchTransfer !==null && clearSearchTransfer.click();
        const clearSearchAdj=document.querySelector(".clear-search-adj")
        clearSearchAdj !==null && clearSearchAdj.click();
        const clearSearchRtin=document.querySelector(".clear-search-rtin")
        clearSearchRtin !==null && clearSearchRtin.click();
        const clearSearchRtout=document.querySelector(".clear-search-rtout")
        clearSearchRtout !==null && clearSearchRtout.click();
    }
    if (event.ctrlKey && event.key === 'i') {
        event.preventDefault(); // Prevent the default browser save dialog
        const des=document.querySelector(".description")
        des!==null && des.click();
    }
    if (event.ctrlKey && event.key === 'o') {
        event.preventDefault(); // Prevent the default browser save dialog
        const open=document.querySelector(".open")
        open!==null && open.click();
    }    
   
    if (event.ctrlKey && event.shiftKey && event.key === 'S') {
        event.preventDefault(); // Prevent the default browser save dialog     
        const supplier=document.querySelector(".supplier")
        supplier!==null && supplier.click();
    }
    if (event.ctrlKey && event.shiftKey && event.key === 'C') {
        event.preventDefault(); // Prevent the default browser save dialog
        const customer=document.querySelector(".customer")
        customer!==null && customer.click();
        
    }
  
}
export{
    handleCtrl
}