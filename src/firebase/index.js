// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
 
  apiKey: "AIzaSyClM5ruGIfHMj5wdclzC8zkZwh-LJbdCLw",

  authDomain: "e-pos-eb265.firebaseapp.com",

  projectId: "e-pos-eb265",

  storageBucket: "e-pos-eb265.appspot.com",

  messagingSenderId: "447119583594",

  appId: "1:447119583594:web:c51631daba522f45679d13"

};


// Initialize Firebase
const app = initializeApp(firebaseConfig);
//const analytics = getAnalytics(app);
export default app;