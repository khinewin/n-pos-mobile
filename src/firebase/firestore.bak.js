import app from "./index.bak"
import { getFirestore, CACHE_SIZE_UNLIMITED,disableNetwork, initializeFirestore ,persistentLocalCache ,persistentMultipleTabManager} from "firebase/firestore";


const db = initializeFirestore(app, {
    localCache: 
      persistentLocalCache(/*settings*/{tabManager: persistentMultipleTabManager()})    
  });
  await disableNetwork(db);

//let db=getFirestore();
export default db;
