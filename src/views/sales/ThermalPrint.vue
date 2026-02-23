<template>
  <div class="thermal-receipt">

    <!-- HEADER -->
    <SmallPrintHeader />
    <div class="hr"></div>

    <!-- META -->
    <div class="meta fw-bold">
      <div class="row g-0">
        <div class="col-5">Invoice No.</div>
        <div class="right col-7">{{ data.voucher_no }}</div>
      </div>
      <div class="row g-0">
        <div class="col-5">Date</div>
        <div class="right col-7">{{ sale_date }}</div>
      </div>
      <div class="row g-0">
        <div class="col-5">Customer</div>
        <div class="right col-7" >{{ data.customer?.name }}</div>
      </div>
      <div class="row g-0">
        <div class="col-5">Cashier</div>
        <div class="right col-7">{{ data.user?.displayName }}</div>
      </div>
    </div>

    <div class="hr"></div>

    <!-- ITEMS HEADER -->
    <div class="items-header fw-bold">
      <div>#</div>
      <div class="item">Item</div>
      <div class="center">Qty</div>
      <div class="right">Amt</div>
    </div>

    <!-- ITEMS -->
    <div
      class="item-row"
      v-for="(s, i) in data.saleItems"
      :key="s.id"
    >
      <div>{{ i + 1 }}</div>
      <div class="item-name">{{ s.item_name }}</div>
      <div class="center">{{ s.sale_qty }}</div>
      <div class="right">{{ fM(s.final_amount) }}</div>
    </div>

    <div class="hr"></div>

    <!-- SUMMARY -->
    <div class="summary fw-bold">
      <div class="row g-0">
        <div class="col-5">Total</div>
        <div class="right col-7">{{ fM(data.total_amount) }}</div>
      </div>
      <div class="row  g-0">
        <div class="col-5">Discount</div>
        <div class="right  col-7">{{ fM(data.extra_discount) }}</div>
      </div>
      <div class="row  g-0">
        <div class="col-5">Net</div>
        <div class="right  col-7">{{ fM(data.net_amount) }}</div>
      </div>
      <div class="row  g-0">
        <div class="col-5">Paid</div>
        <div class="right  col-7">{{ fM(data.paid) }}</div>
      </div>
      <div class="row  g-0">
        <div class="col-5">Change</div>
        <div class="right  col-7">{{ fM(data.change) }}</div>
      </div>
    </div>

    <div class="hr"></div>

    <!-- FOOTER -->
    <div class="footer fw-bold">
      Printed on {{ printDate }}<br />
      Thank you for shopping
    </div>

  </div>
</template>

<script>
import { Timestamp } from 'firebase/firestore'
import SmallPrintHeader from "@/views/partials/SmallPrintHeader.vue"
import * as hp from '@/lib/helper'

export default {
  name: 'ThermalReceipt',
  props: {
    data: {
      type: Object,
      required: true
    }
  },
  components: {
    SmallPrintHeader
  },
  data() {
    return {
      printDate: '',
      sale_date: ''
    }
  },
  mounted() {
    this.setPrintDate()
  },
  methods: {
    fM(amount) {
      return hp.formattedAmount(amount || 0)
    },
    setPrintDate() {
      const now = Timestamp.fromDate(new Date())
      this.printDate = hp.showTimeHour(now)

      let saleTime
      if (this.data.saleMode === 'Sale / New') {
        const sDate = hp.insertDate(this.data.print_date)
        saleTime = hp.showTimeHour(Timestamp.fromDate(sDate))
      } else {
        saleTime = hp.showTimeHour(this.data.print_date)
      }
      this.sale_date = saleTime
    }
  }
}
</script>

<style>
@import url("/thermalprint.css");
</style>
