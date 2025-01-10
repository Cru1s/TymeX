# Currency Converter App

This is a simple currency converter application that allows users to convert currencies, manage travel budgets, and estimate travel costs.

---
## App Structure

The app follows the MVVM (Model-View-ViewModel) architecture:

### Model: 

- Handles data-related operations such as fetching exchange rates from the API.
- Includes: CurrencyRepository ( fetch exchange rate ), CurrencyApi and ApiClient ( defines the API interface and Retrofit instance for network operations)

### ViewModel:
- Acts as a bridge between the Model and the View.
- Contains business logic and prepares data for the UI.
- Includes: CurrencyViewModel ( currency conversion logic and updates LiveData for the converter ), 
TravelEstimatorViewModel ( adding, deleting, and currency conversion for total trip cost )

### View 
- The UI that observes LiveData from View Model and display it
- Includes:
  - MainActivity: UI for selecting base/target currencies and displaying converted amounts.
  - TravelEstimatorListActivity: Displays a list of travel estimators and allows adding/deleting estimators.
  - TravelEstimatorAdapter: Displays estimator details in a RecyclerView.
---

## Prerequisites

Before building and running the app, ensure you have the following installed:

- **Android Studio**: Version `2021.2.1 (Chipmunk)` or higher.
- **JDK**: Java Development Kit version `8` or higher.
- **Kotlin**: Version `1.6.10` (integrated with Android Studio).
- **Android SDK**:
  - **Target SDK**: `32`
  - **Minimum SDK**: `21`

---

## Steps to Build and Run

### 1. Download the .zip file 

Download the .zip file and extract to your local machine.

### 2. Open the project in Android Studio

1. Lauch Android Studio
2. Go to File -> Open project and navigate to the project Challenge-1
3. Wait for Gradle to sync to complete

### 3.  Configure the Build Environment

Ensure the following configurations are correct:
- Gradle Version: Use the version specified in the gradle-wrapper.properties file.
- Kotlin Version: Confirm it is set to 1.6.10 in the build.gradle files.

### 4. Run the application

- Connect to a physical Android device through USB or use Android Emulator (with API 21 or higher)
- Press Run (green play button) or use the shortcut Shift + F10.
