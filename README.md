# N-POS — Cloud Point of Sale (Android Native)

A modern, high-performance **Native Android Cloud POS** application designed for retail shops, marts, restaurants, and multi-branch businesses. Built with **100% Jetpack Compose**, N-POS connects directly to **Google Cloud Firebase / Firestore** and provides seamless integration with point-of-sale hardware peripherals including thermal receipt printers, customer-facing secondary displays, and barcode scanners.

---

## 🌟 Key Features

### 1. 🚀 100% Native Jetpack Compose & Adaptive POS Layouts
- **Pure Native Architecture**: Responsive, fluid UI following modern Material Design 3 guidelines without sluggish web views.
- **Adaptive View Modes**:
  - **Auto Mode**: Automatically detects screen size and orientation.
  - **Phone Mode**: Optimized compact vertical layout with collapsible bottom checkout sheet for handheld mobile phones.
  - **Tablet / POS Mode**: Dual-pane split layout (Product Catalog & Category grid on the left, Real-time Cart and Payment summary on the right) for 7"-15" POS terminals and tablets.
- **Instant Layout Switcher**: Easily switch between Phone, Tablet, and Auto modes on the fly from the sidebar navigation.

### 2. 📱 Multi-Branch & Device-Specific Firebase Database Settings
- **Independent Database Assignment**:
  - Configure a dedicated **Firebase Project ID** and **Web API Key** directly on each device from the Login screen.
  - Allows different branches, franchises, or test environments to connect to separate databases on each phone.
- **Persistent Local Storage**:
  - Settings are saved securely per device using Android `SharedPreferences`.
- **Live Connection Status**:
  - Visual status indicator under the login form showing real-time database connection (🟢 `Connected: [Project ID]` or ⚪ `Database: Not Connected`).
  - Safe configuration dialog accessible from the top-bar Database Setting button.

### 3. 🛒 Comprehensive Sales & Cart Processing
- **Dynamic Product Catalog**:
  - Direct Firestore queries with category filtering and instant search.
  - Search by product name, barcode, and code number.
- **Fast Cart Operations**:
  - Add items with instant stock and price calculation.
  - Increment/decrement quantity, custom price editing, and per-item discounts.
  - One-tap cart clear and item removal.
- **Flexible Checkout & Payments**:
  - Automatic voucher number generation with real-time timestamping.
  - Multiple payment options: **Cash**, **KBZPay (KPay)**, **WavePay**, **CBPay**, **AYA Pay**, **Credit Card**, and **Bank Transfer**.
  - Extra invoice discounts, paid amount input, and change calculation.
  - Direct Firestore sales document creation with full itemization and cashier audit fields.

### 4. 🖨️ Direct Thermal Bluetooth Silent Printing (ESC/POS)
- **Instant Silent Printing**:
  - Prints receipts directly to paired Bluetooth thermal printers without interrupting the cashier with native OS print dialogues.
- **Dual Paper Width Support**:
  - Supports standard **58mm (2-inch)** and **80mm (3-inch)** thermal receipt formats.
- **Professional Receipt Formatting**:
  - Store header, cashier name, voucher number, date/time, customer details.
  - Itemized table (Name, Qty, Unit Price, Total).
  - Subtotal, Discounts, Net Amount, Paid, and Change breakdown.
  - Customizable footer message with barcode or QR code.
- **Integrated Printer Manager**:
  - One-tap Bluetooth device discovery, connection testing, and sample test print.

### 5. 🖥️ Dual-Screen Customer Display (Presentation API)
- **Hardware POS Integration**:
  - Native integration with Android dual-screen terminals (Sunmi, iMin, Telpo, external HDMI/USB-C monitors) via Android's `DisplayManager` and `Presentation` APIs.
- **Real-Time Customer Screen Mirroring**:
  - Displays cart items, unit pricing, item quantities, total discount, net amount, and payment status to the customer as items are scanned.
- **Single-Screen Preview Dialog**:
  - Interactive in-app customer display preview modal for cashiers using single-screen devices or tablets.

### 6. 🔍 Hardware & Live Barcode Scanner Support
- **USB & Bluetooth HID Scanner Support**:
  - Intercepts keystroke events from handheld and tabletop hardware barcode scanners with automatic ENTER key detection for rapid scanning.
- **Live Scanner Status Indicator**:
  - Real-time visual status indicator showing scanner readiness.

### 7. 👥 Customer Relationship Management (CRM)
- **Customer Selection at Checkout**:
  - Assign sales to existing customers or default Walk-in Customer.
- **Customer Directory**:
  - View, search, create, and edit customer details (Name, Phone, Code, Address) with direct Firestore synchronization.

### 8. 📊 Cashier Sales Dashboard & Summary
- **Shift & Daily Metrics**:
  - Real-time tracking of today's total revenue and transaction count.
  - Monthly sales volume and cashier performance summary.

---

## 🛠️ Tech Stack & Architecture

- **Platform**: Android Native (Kotlin 2.0+)
- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Backend / Cloud**: Firebase Authentication (REST) & Google Cloud Firestore (REST API via OkHttp)
- **Hardware APIs**:
  - Android Bluetooth Serial Port Profile (SPP) with ESC/POS protocol
  - Android `DisplayManager` & `Presentation` API for secondary customer displays
  - Android `KeyEvent` dispatching for hardware HID barcode scanners
- **Build System**: Gradle 9.3.1 with Android Gradle Plugin 9.1.1
- **Target SDK**: 36 (Android 16) | **Min SDK**: 24 (Android 7.0)

---

## 🔌 Hardware Compatibility

| Hardware Type | Supported Models / Standards |
|---|---|
| **Android POS Terminals** | Sunmi (T2, D2, V2), iMin (D4, Falcon), Telpo, Feitian, OCOM, Pax |
| **Thermal Receipt Printers** | 58mm & 80mm ESC/POS Bluetooth printers (Xprinter, POS-58, Milestone, PT-210, Rongta, Goojprt) |
| **Barcode Scanners** | Zebra, Honeywell, Netum, Eyoyo, Datalogic USB/Bluetooth HID scanners |
| **Secondary Displays** | Native dual-display POS hardware, HDMI monitors, USB-C external displays |

---

## 📦 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/<your-username>/<repo-name>.git
   cd <repo-name>
   ```
2. **Open in Android Studio** (Ladybug / Iguana or later recommended).
3. **Build and Run**:
   - Run on an Android POS terminal, tablet, or phone running Android 7.0 or higher.
4. **Database Setup**:
   - On the Login screen, tap **Database Setting** at the top right.
   - Enter your **Firebase Project ID** and **Web API Key**, then tap **Save & Connect**.
   - Log in with your cashier or admin credentials to start selling!

---

## 📄 License
This project is licensed under the Apache 2.0 License.
