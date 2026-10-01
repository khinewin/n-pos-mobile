# N-POS Android POS Application

Android POS application wrapping the N-POS web application (`https://n-pos.web.app`) with native hardware integrations:

## Key Features

1. **Integrated Web View**:
   - Seamlessly loads `https://n-pos.web.app` with hardware acceleration, cookie management, responsive scaling, and offline fallback handling.
   - Quick Login Autofill for test account (`n1@web.app` / `password`).

2. **Direct Thermal Bluetooth Silent Printing**:
   - Intercepts Sales page printing (`https://print?data=...`, `AndroidBridge.printThermal(json)`).
   - Direct ESC/POS silent printing to paired 58mm / 80mm Bluetooth thermal receipt printers without blocking OS print dialogs.
   - Printer management dialog with paired device selection, paper width customization (58mm/80mm), and instant test printing.

3. **Android POS Dual Screen Customer Display**:
   - Supports dual-screen Android POS terminals (Sunmi, iMin, Telpo, external HDMI/USB-C displays) using Android's `Presentation` API.
   - Automatically synchronizes cart items, subtotal, discount, net total, paid amount, and change to the customer-facing secondary display.
   - In-app preview modal for single-screen devices to test and preview customer screen data.

4. **Technical Specifications**:
   - Kotlin + Jetpack Compose
   - Target SDK: 36 (Android 16), Min SDK: 24 (Android 7.0)
   - Gradle 9.3.1 with Android Gradle Plugin 9.1.1
