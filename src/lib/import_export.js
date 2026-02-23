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
        ...d,
        sale_date: Timestamp.fromDate(new Date(d.sale_date)),        
    }    

    const saveDocument=doc(db, "sales", d.id)
    await setDoc(saveDocument, voucher)  
}
const restorePurchases=async(d)=>{
      const voucher={
        voucher_no: d.voucher_no,
        ...d,
        pur_date: Timestamp.fromDate(new Date(d.pur_date)),        
    }      
    const saveDocument=doc(db, "purchases", d.id)
    await setDoc(saveDocument, voucher)   
}
const restoreProducts=async(d)=>{
  const voucher={
    code_no: d.code_no,
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at))
  
}     
const saveDocument=doc(db, "products", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreStocks=async(d)=>{
  const voucher={
    voucher_no: d.voucher_no,
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at))              
}  
const saveDocument=doc(db, "openingStocks", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreTransfers=async(d)=>{
  const voucher={
    voucher_no: d.voucher_no,
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at))              
}  
const saveDocument=doc(db, "transfers", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreAdjustments=async(d)=>{
  const voucher={
    voucher_no: d.voucher_no,
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at))              
}  
const saveDocument=doc(db, "adjustments", d.id)
await setDoc(saveDocument, voucher)   
}
const restoreSuppliers=async(d)=>{
  const voucher={
    code: d.code,
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at))              
}  
const saveDocument=doc(db, "suppliers", d.id)
await setDoc(saveDocument, voucher)   
}

const restoreCustomers=async(d)=>{
  const voucher={
    code: d.code,
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at)),           
  
}  
const saveDocument=doc(db, "customers", d.id)
await setDoc(saveDocument, voucher)   
}
const restorePackingSizes=async(d)=>{
  const voucher={
    ...d,
    created_at: Timestamp.fromDate(new Date(d.created_at))           
   
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
    deleteData
  }
  