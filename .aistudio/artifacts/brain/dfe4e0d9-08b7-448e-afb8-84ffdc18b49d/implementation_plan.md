# Clean Code Number Display in Barcode Product List & Sticker

Remove the `code_no:` prefix in the **Select Product to Print Barcode** list and the `Code:` prefix on printed barcode stickers so that only the clean barcode/code number (e.g., `09383989494`) is displayed.

## User Review & Critical Decisions

> [!IMPORTANT]
> Confirmed user preferences from Phase 1 clarification:

- **Confirmed Decision 1 (Remove Prefix in Product List & Barcode Sticker)**: Both the **Select Product to Print Barcode** list item badge (`09383989494` instead of `code_no: 09383989494`) and the barcode sticker output (`09383989494` instead of `Code: 09383989494`) will display the clean number only without any prefix.
- **Confirmed Decision 2 (Retain Price Tier Labels in Product List)**: In the product selector list, Retail and Wholesale prices will keep their short labels (`လက်လီ: 1,500 • လက်ကား: 1,200 Ks` / `R: 1,500 • W: 1,200 Ks`) so staff can easily distinguish the two prices while browsing items.

---

## 1. Overview & Core Concept

- **What It Does**: Strips the redundant `"code_no: "` label from every product row in the **Select Product to Print Barcode** panel and removes the `"Code: "` prefix from barcode sticker rendering (Bitmap graphic, ESC/POS text, and TSPL label commands).
- **Target Audience / Persona**: Cashiers and inventory staff scanning and selecting products to print barcode labels.
- **Key Value**: Frees horizontal space in the product list badge so long barcode numbers (`09383989494`) and prices fit cleanly on a single line without truncation, and ensures printed stickers show only the clean numeric code below the barcode bars.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. Staff opens the **Barcode Print** screen and views the **Select Product to Print Barcode** list.
  2. Each product row displays the product name on top, followed by a clean monospace badge with just the number (e.g., `09383989494`) next to the Retail and Wholesale prices (`လက်လီ: 1,500 • လက်ကား: 1,200 Ks`).
  3. On the live sticker preview and printed output, the barcode number line renders strictly as `09383989494` without any `Code:` or `code_no:` prefix.
- **Visual Identity & Theme**:
  - *Aesthetic Direction*: Clean utilitarian POS layout with high-contrast monospace badges for scannable product codes.
  - *Color Palette & Mood*: Uses `MaterialTheme.colorScheme.secondaryContainer` and `onSecondaryContainer` for the code number pill badge, paired with `MaterialTheme.colorScheme.primary` for the price summary.
  - *Typography & Hierarchy*: Bold `FontFamily.Monospace` (`11.sp`) for the code number badge in the product selector list; bold monospace (`tfMonoBold`) centered below the Code-128 bars on the sticker.
  - *Component Styling & Layout*: Compact rounded pill (`RoundedCornerShape(4.dp)`) with `6.dp` horizontal padding so the number stands out clearly from the price text.
- **Interactive Feedback & Motion**: Tapping a product row immediately updates the live barcode sticker preview and highlights the selected row with a primary border and tonal container tint.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Clean Numeric Code Badge in Product Selector**
  - *Chosen Approach*: Display `product.codeNo` directly inside the monospace badge without `"code_no: "`.
  - *Why*: The monospace pill styling already visually communicates that the value is a product/barcode number, and removing 9 characters of prefix prevents horizontal crowding against the Retail/Wholesale prices.
  - *Alternatives Considered*: Keeping a shorter prefix like `#`, which still adds unnecessary characters to 11–13 digit EAN/Code-128 strings.
- **Decision 2: Unconditional Prefix-Free Code Number on Stickers**
  - *Chosen Approach*: Always render `rawCode` (`09383989494`) without `"Code: "` across Bitmap, ESC/POS, and TSPL generators regardless of `showFieldLabels`.
  - *Why*: Standard retail barcode stickers always display pure digits directly underneath the barcode bars.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:
```
┌───────────────────────────────────────────────────────────────────┐
│                      BarcodePrintScreen (UI)                      │
├─────────────────────────────────┬─────────────────────────────────┤
│      ProductSelectorPane        │     Live Sticker Preview &      │
│  ┌───────────────────────────┐  │     BarcodeLabelGenerator       │
│  │ Item Name                 │  │  ┌───────────────────────────┐  │
│  │ [09383989494] လက်လီ/လက်ကား │  │  │  ||||| |||| |||||| |||||  │  │
│  │ (No "code_no:" prefix)    │  │  │        09383989494        │  │
│  └───────────────────────────┘  │  │     (No "Code:" prefix)   │  │
│                                 │  └───────────────────────────┘  │
└─────────────────────────────────┴─────────────────────────────────┘
```
- **Data Model & State**:
  - `Product.codeNo`: Displayed directly in `ProductSelectorPane` badge (`text = product.codeNo`).
  - `BarcodeLabelGenerator`: In `renderLabelBitmap`, `generateEscPosTextBarcodeBytes`, and `generateTsplCommandBytes`, the code line outputs `rawCode` / `codeVal` directly without `"Code: "` prefix.
- **Interactive Component & State Mapping**:
  - Selecting any product item in `ProductSelectorPane` populates `editCodeNo` and `editBarcodeValue` with `product.codeNo`, immediately re-rendering the live preview bitmap with the clean numeric string.
