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
                        <router-link class="nav-link py-2 px-3" :to="{name : 'Dashboard'}" ><i class="fa-solid fa-earth-asia"></i> {{t('dashboard')}}</router-link>
                      </li>
                    </ul>
                </li>
                <li class="list-group-item">
                      <ul class="list-group small ">                       
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Sale'}" ><i class="fa-solid fa-cash-register"></i> {{t('sale')}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'MobileSale'}" ><i class="fa-solid fa-cash-register"></i> Mobile Sale</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping"></i> {{t('purchase')}}</router-link>
                          </li>     
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Stocks'}"><i class="fa-solid fa-money-bill-trend-up"></i> {{t('stocks')}}</router-link>
                          </li>               
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Products'}"><i class="fa-solid fa-list-check"></i> {{t('product_code')}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'BarcodeSticker'}"><i class="fa-solid fa-barcode"></i> {{t('barcode_sticker')}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Suppliers'}"><i class="fa-solid fa-person-walking-luggage"></i> {{t('suppliers')}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Customers'}"><i class="fa-solid fa-users"></i> {{t('customers')}}</router-link>
                          </li>
                          <!--<li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Transfers'}"><i class="fa-solid fa-right-left"></i> Transfer</router-link>
                          </li>-->
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Adjustments'}"><i class="fa-solid fa-sliders"></i> {{t('adjustment')}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Soc'}"><i class="fa-solid fa-file-invoice-dollar"></i> {{t('soc')}}</router-link>
                          </li>
                         <!--<li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Expense'}"><i class="fa-solid fa-file-invoice-dollar"></i> {{t('expense')}}</router-link>
                          </li>-->
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'Reports'}"><i class="fa-solid fa-chart-pie"></i> {{t('reports')}}</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action p-0 bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link py-2 px-3" :to="{name : 'BackupRestore'}"><i class="fa-solid fa-download"></i> {{t('backup_restore')}}</router-link>
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
    t() {
            return (key) => this.$store.getters.t(key);
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