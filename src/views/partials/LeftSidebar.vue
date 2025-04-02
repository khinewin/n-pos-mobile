<template lang="">       
      <div class="offcanvas offcanvas-start bg-secondary" tabindex="-1" id="leftSide" aria-labelledby="leftSide">
          <div class="offcanvas-header">
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
          </div>
          <div class="offcanvas-body ">
              <ul class="list-group">
                <li class="list-group-item">
                    <ul class="list-group small">
                      <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                        <router-link class="nav-link py-2 px-3" :to="{name : 'Dashboard'}" ><i class="fa-solid fa-earth-asia"></i> {{dashboard_link}}</router-link>
                      </li>
                    </ul>
                </li>
                <li class="list-group-item">
                      <ul class="list-group small ">                       
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Sale'}" ><i class="fa-solid fa-cash-register"></i> {{sale_link}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'MobileSale'}" ><i class="fa-solid fa-cash-register"></i> Mobile Sale</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping"></i> {{pur_link}}</router-link>
                          </li>     
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Stocks'}"><i class="fa-solid fa-money-bill-trend-up"></i> {{stocks_link}}</router-link>
                          </li>               
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Products'}"><i class="fa-solid fa-list-check"></i> {{product_link}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Suppliers'}"><i class="fa-solid fa-person-walking-luggage"></i> {{suppliers_link}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Customers'}"><i class="fa-solid fa-users"></i> {{customers_link}}</router-link>
                          </li>
                          <!--<li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Transfers'}"><i class="fa-solid fa-right-left"></i> Transfer</router-link>
                          </li>-->
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Adjustments'}"><i class="fa-solid fa-sliders"></i> {{adj_link}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Soc'}"><i class="fa-solid fa-file-invoice-dollar"></i> {{soc_link}}</router-link>
                          </li>
                          <!--<li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'ReturnIn'}"><i class="fa-solid fa-file-import"></i> Return In</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'ReturnOut'}"><i class="fa-solid fa-file-export"></i> Return Out</router-link>
                          </li>-->
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Reports'}"><i class="fa-solid fa-chart-pie"></i> {{reports_link}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'BackupRestore'}"><i class="fa-solid fa-download"></i> {{backup_link}}</router-link>
                          </li>
                      </ul>
                </li>
              </ul>
          </div>
      </div>
</template>
<script>
  import {lang} from '@/lib/language'

export default {
  name : "LeftSidebar",
  computed:{
    dashboard_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.dashboard.mm : lang.dashboard.eng ;
            return data;
        },
    sale_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.sale.mm : lang.sale.eng ;
            return data;
      },
        pur_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.purchase.mm : lang.purchase.eng ;
            return data;
        },
        stocks_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.stocks.mm : lang.stocks.eng ;
            return data;
        },
        product_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.product_code.mm : lang.product_code.eng ;
            return data;
        },   
        suppliers_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.suppliers.mm : lang.suppliers.eng ;
            return data;
        },
        customers_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.customers.mm : lang.customers.eng ;
            return data;
        },
        adj_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.adjustment.mm : lang.adjustment.eng ;
            return data;
        },
        soc_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.soc.mm : lang.soc.eng ;
            return data;
        },
        reports_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.reports.mm : lang.reports.eng ;
            return data;
        },
        backup_link(){
            const sl=this.$store.getters.setLanguage;
            const data=sl=="mm" ? lang.backup_restore.mm : lang.backup_restore.eng ;
            return data;
        },
  }
}
</script>
<style>
      
      .list-group-item-action:hover .nav-link{
          background-color: rgba(59, 58, 58, 0.5) !important;
          transition: all .2s;
          color: white !important;
      }
      .router-link-active {
          color : #0d6efd !important
      }
</style>