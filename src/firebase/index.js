// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyBkhkIJ0GkHANgBOcBtesbLNJDP-XGw7wU",
  authDomain: "ntgpos-47e62.firebaseapp.com",
  projectId: "ntgpos-47e62",
  storageBucket: "ntgpos-47e62.appspot.com",
  messagingSenderId: "547067398616",
  appId: "1:547067398616:web:9f46e87570cbbae4a64aad",
  measurementId: "G-LM4GNPNZWX"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
export default app;