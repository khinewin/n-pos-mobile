// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {

  apiKey: "AIzaSyAnhLH5v1uhUWCHqqWeZ4W6LfMqGwwqmCg",

  authDomain: "n-pharmacy.firebaseapp.com",

  projectId: "n-pharmacy",

  storageBucket: "n-pharmacy.appspot.com",

  messagingSenderId: "335355320769",

  appId: "1:335355320769:web:927c7d7181265b7f22f740",

  measurementId: "G-HWMYGY8Q3E"


};


// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
export default app;