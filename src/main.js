import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import * as bootstrap from 'bootstrap';
window.bootstrap = bootstrap;
import "../node_modules/bootstrap/dist/css/bootstrap.css"
import "./Style.css"
/* import the fontawesome core */
import { library, dom } from '@fortawesome/fontawesome-svg-core'
import store from "@/store"
import 'flatpickr/dist/flatpickr.css';
import "flatpickr/dist/themes/dark.css"
/* import font awesome icon component */
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

import { fas } from '@fortawesome/free-solid-svg-icons'
//import { fab } from '@fortawesome/free-brands-svg-icons';
//import { far } from '@fortawesome/free-regular-svg-icons';


library.add(fas)

dom.watch();

import VueHtmlToPaper from 'vue-html-to-paper';

const options = {
    name: '_blank',
    specs: [
      'fullscreen=yes',
      'titlebar=yes',
      'scrollbars=yes'
    ],
    styles: [
      'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css',
    ],
    timeout: 1000, // default timeout before the print window appears
    autoClose: true, // if false, the window will not close after printing
   // windowTitle: window.document.title, // override the window title
  }

const app = createApp(App)
app.component('font-awesome-icon', FontAwesomeIcon)
app.use(store)
app.use(router)
app.use(VueHtmlToPaper, options);

app.mount('#app')
