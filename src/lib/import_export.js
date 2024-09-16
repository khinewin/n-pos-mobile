import {Timestamp, collection, addDoc, getDocs, doc, deleteDoc, onSnapshot, setDoc, where, orderBy, updateDoc, query} from 'firebase/firestore'
import db from '@/firebase/firestore'

const exportToJson=(arr, file_name)=>{
    const jsonString = JSON.stringify(arr, null, 2); // Pretty-print with 2 spaces
    const blob = new Blob([jsonString], { type: "application/json" });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = `${file_name}.json`; 
    link.click();
    URL.revokeObjectURL(link.href);
    return true;
  }

  const restoreSales=async (d)=> {                
        const voucher={
        voucher_no: d.voucher_no,
        sale_date: Timestamp.fromDate(new Date(d.sale_date)),
        products : d.products,
        user : d.user,
        total_amount:d.total_amount,
        extra_discount:d.extra_discount,
        net_amount: d.net_amount ,
        paid: d.paid,
        change:d.change,
        sale_type:d.sale_type,
        customer : d.customer,
        credit: d.credit,
        payment_type: d.payment_type
    }    

    const saveDocument=doc(db, "sales", d.id)
    await setDoc(saveDocument, voucher)  
}
const restorePurchases=async(d)=>{
      const voucher={
        voucher_no: d.voucher_no,
        pur_date: Timestamp.fromDate(new Date(d.pur_date)),
        supplier: d.supplier,
        location: d.location,
        products : d.products,
        user : d.user,
        total_amount:d.total_amount,
        total_discount:d.total_discount,
        total_price:d.total_price,
        extra_discount: d.extra_discount,
        grand_total_price: d.grand_total_price,
    }      
    const saveDocument=doc(db, "purchases", d.id)
    await setDoc(saveDocument, voucher)   
}
const restoreProducts=async(d)=>{
  const voucher={
    code_no: d.code_no,
    created_at: Timestamp.fromDate(new Date(d.created_at)),
   item_name:d.item_name,
   user: d.user,
   qty : d.qty,
   code_no : d.code_no,                        
   main_qty: d.main_qty, 
   packing_size: d.packing_size,
   retail_price : d.retail_price,
   whole_sale_price : d.whole_sale_price,
   pur_price : d.pur_price,
   auto_cal: d.auto_cal,
   edit_user: null,
   is_deleted:d.is_deleted,
}     
const saveDocument=doc(db, "products", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreStocks=async(d)=>{
  const voucher={
    voucher_no: d.voucher_no,
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
    products : d.products,
    user : d.user              
}  
const saveDocument=doc(db, "openingStocks", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreTransfers=async(d)=>{
  const voucher={
    voucher_no: d.voucher_no,
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
    products : d.products,
    user : d.user,
    from_location: d.from_location,
    to_location: d.to_location,              
}  
const saveDocument=doc(db, "transfers", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreAdjustments=async(d)=>{
  const voucher={
    voucher_no: d.voucher_no,
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
    products : d.products,
    user : d.user,
    location: d.location,         
}  
const saveDocument=doc(db, "adjustments", d.id)
await setDoc(saveDocument, voucher)   
}
const restoreSuppliers=async(d)=>{
  const voucher={
    code: d.code,
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
    name : d.name,
    user : d.user,
    company: d.company,
    phone: d.phone,        
    is_deleted:d.is_deleted, 
}  
const saveDocument=doc(db, "suppliers", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreCustomers=async(d)=>{
  const voucher={
    code: d.code,
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
    name : d.name,
    user : d.user,
    address: d.address,
    phone: d.phone,         
    is_deleted:d.is_deleted,
}  
const saveDocument=doc(db, "customers", d.id)
await setDoc(saveDocument, voucher)   
}
const restorePackingSizes=async(d)=>{
  const voucher={
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
    packing_unit : d.packing_unit,
    user : d.user,
    packing_name: d.packing_name,    
}  
const saveDocument=doc(db, "packingSizes", d.id)
await setDoc(saveDocument, voucher)   
}

const deleteData=(data, collection)=>{
  data.map((d, i)=>{
      setTimeout(async ()=>{
          const document=doc(db, collection, d.id)     
          await deleteDoc(document)      
      }, 200 * i)
  })
}

  export {
    exportToJson,
    restoreSales,
    restorePurchases,
    restoreProducts,
    restoreStocks,
    restoreTransfers,
    restoreAdjustments,
    restoreSuppliers,
    restoreCustomers,
    restorePackingSizes,
    deleteData,
  }
  