<template lang="">       
      <div class="offcanvas offcanvas-start bg-secondary" tabindex="-1" id="leftSide" aria-labelledby="leftSide">
          <div class="offcanvas-header">
            <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
          </div>
          <div class="offcanvas-body ">
              <ul class="list-group">
                <li class="list-group-item">
                    <ul class="list-group small">
                      <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                        <router-link class="nav-link" :to="{name : 'Dashboard'}" ><i class="fa-solid fa-earth-asia"></i> Dashboard</router-link>
                      </li>
                    </ul>
                </li>
                <li class="list-group-item">
                      <ul class="list-group small ">                       
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Sale'}" ><i class="fa-solid fa-cash-register"></i> Sale</router-link>
                          </li>
                        
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Purchase'}"><i class="fa-solid fa-cart-shopping"></i> Purchase</router-link>
                          </li>     
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'OpeningStocks'}"><i class="fa-solid fa-money-bill-trend-up"></i> Stocks</router-link>
                          </li>               
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Products'}"><i class="fa-solid fa-list-check"></i> Medicine code</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Suppliers'}"><i class="fa-solid fa-person-walking-luggage"></i> Suppliers</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Customers'}"><i class="fa-solid fa-users"></i> Customers</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Transfers'}"><i class="fa-solid fa-right-left"></i> Transfers</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Adjustments'}"><i class="fa-solid fa-sliders"></i> Adjustment</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Soc'}"><i class="fa-solid fa-file-invoice-dollar"></i> SOC</router-link>
                          </li>
                          <li class="list-group-item list-group-item-action bg-secondary-subtle border-0"  data-bs-dismiss="offcanvas" aria-label="Close">
                            <router-link class="nav-link" :to="{name : 'Reports'}"><i class="fa-solid fa-chart-pie"></i> Reports</router-link>
                          </li>
                      </ul>
                </li>
              </ul>
          </div>
      </div>
</template>
<script>
export default {
  name : "LeftSidebar"
    
}
</script>
<style>
      
      .list-group-item-action:hover{
          padding-left: 30px;
          transition: all 0.4s;
          background-color: rgba(128, 128, 128, 0.5) !important;
      }
      .router-link-active {
          color : red !important
      }
</style>