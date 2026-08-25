import * as hp from "@/lib/helper.js";

export function formatErpMoney(amount) {
  if (amount === null || amount === undefined || isNaN(amount)) return "0";
  return hp.formattedAmount(amount);
}
