<template>
  <div class="container-fluid">
  <div class=" row g-0">
    <!-- LEFT SIDE: Logo and Store Info -->
    <div class="col-5 vh-100">
        <div class="text-center mt-5 vh-75">
          <img src="/img/logo.png" alt="Sneaker Sky Logo" class="img-fluid" width="200px"  />
          <h1 class="fw-bold">N-POS</h1>
          <h4 class="fw-bold">POS Software for Minimart</h4>
          <hr>
          <div class="fw-bold h5">ခြံအမှတ် (၅)၊ သဲပုံစေတီအရှေ့လမ်းကြား (Rocket သောက်ရေသန့်စက်ရုံလမ်း)၊ တက္ကသိုလ်ပတ်လမ်း၊ ရှိုးကန့်ရပ်ကွက်၊ စျေးကြို ၊ မော်လမြိုင်မြို့။</div>
          <h6 class="fw-bold h5">PH: 09970488345</h6>
        </div>
        <div>
             <h5 class="text-center fw-bold mb-0"> <img src="/img//ntg-logo.png" width="30px" alt=""> NEW TECH GROUP</h5>
             <div class="text-center small" style="font-size: 11px;">YOUR TRUSTED PARTNER IN TECHNOLOGY</div>
             <div class="text-center small">PH : 09970488345</div>
        </div>
    </div>

    <!-- RIGHT SIDE: Sale Items and Summary -->
    <div class="col-7  vh-100 bg-light">
      <div class=" vh-70 dataContainer overflow-y-auto" >
        <table class="table " >
          <thead class="fw-bold table-primary text-center">
            <tr style="font-size: 20px;" class="sticky-top z-3">
              <th class="col-0">No.</th>
              <th class="col-5">Item</th>
              <th class="col-1">Qty</th>
              <th class="col-2">Price</th>
              <th class="col-1">Disc</th>
              <th class="col-3">Amount</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(i, index) in saleItems" :key="i.id" >
              <td class="text-center">{{ index + 1 }}</td>
              <td>{{ i.item_name }}</td>
              <td class="text-center">{{ i.sale_qty }}</td>
              <td class="text-end">{{ formatCurrency(i.qty_sale_price) }}</td>
              <td class="text-end">{{ formatCurrency(i.discount || 0) }}</td>
              <td class="text-end">{{ formatCurrency(i.final_amount) }}</td>
            </tr>
            <tr v-if="saleItems.length === 0 " >
              <td colspan="6" class="text-center">Waiting for sale...</td>
            </tr>
          </tbody>
          
        </table>
      </div>
      <div>
        <table class="table">
          <tfoot class="fw-bold table-group-divider" style="font-size: 21px;">
            <tr >
              <td colspan="" class="text-end col-8">Total : </td>
              <td class="col-4">{{ formatCurrency(net_amount) }} MMK</td>
              </tr>
               <tr>
              <td colspan="" class="text-end">Discount :</td>
              <td> {{ formatCurrency(extra_discount) }} MMK</td>
              </tr>
              <tr>
              <td colspan="" class="text-end">Paid :</td>
              <td> {{ formatCurrency(paid) }} MMK</td>
              </tr>
              <tr>
              <td colspan="" class="text-end">Change :</td>
              <td> {{ formatCurrency(change) }} MMK</td>
              </tr>
         
          </tfoot>
        </table>
      </div>

    </div>
  </div>
  </div>
</template>

<script>
export default {
  name: "CustomerDisplay",
  data() {
    return {
      saleItems: [],
      net_amount: 0,
      paid: 0,
      change: 0,
      extra_discount:0
    }
  },
  mounted() {
    window.addEventListener("message", this.receiveData)
    
  },
  beforeUnmount() {
    window.removeEventListener("message", this.receiveData)
  },
  methods: {
    autoScroll(){            
            const container = document.querySelector(".dataContainer");
            container.scrollTo({
                top: container.scrollHeight,
                behavior: "smooth" // Enables smooth scrolling
            });
        
      },
    receiveData(event) {
      if (!event.data || event.data.type !== "POS_UPDATE") return
      const d = event.data.payload
      this.saleItems = d.saleItems || []
      this.net_amount = d.net_amount || 0
      this.paid = d.paid || 0
      this.change = d.change || 0
      this.extra_discount=d.extra_discount ||0
      this.autoScroll();
      
    },
    formatCurrency(value) {
      if (!value) return "0.00";
      return Number(value)
        .toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

  }
}
</script>
<style scoped>
  table tbody td{
    padding: 4px 3px;
    font-size: 19px;
  }
</style>