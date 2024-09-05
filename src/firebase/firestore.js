import app from "./index"
import { initializeFirestore, persistentLocalCache ,persistentMultipleTabManager} from "firebase/firestore";

const db = initializeFirestore(app, {localCache: 
    persistentLocalCache(/*settings*/{tabManager: persistentMultipleTabManager()})
  });
//let db=getFirestore();
export default db;
