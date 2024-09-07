import app from "./index"
import { initializeFirestore, persistentLocalCache ,persistentMultipleTabManager, getFirestore,persistentSingleTabManager, enableIndexedDbPersistence } from "firebase/firestore";

/*
const db = initializeFirestore(app, {localCache: 
    persistentLocalCache({tabManager: persistentSingleTabManager()})
  });
*/
const db = initializeFirestore(app, {localCache: 
  persistentLocalCache({tabManager: persistentMultipleTabManager()})
});

 
//let db=getFirestore();
export default db;
