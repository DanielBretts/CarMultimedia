# CarMultimedia Android App

## Overview

CarMultimedia is an Android application designed for users driving older vehicles who wish to avoid the cost of installing a dedicated multimedia system. The app simulates a modern car multimedia system, providing easy access to essential features such as call handling, internet radio streaming, navigation via Waze, built in map routing and weather temperature updates.

## App Features and Technologies

### 1. Call Handling
- **Broadcast Receiver:** This feature handles incoming calls, allowing the user to answer or decline calls directly through the app. The app utilizes a `BroadcastReceiver` to listen for telephony events and manage calls accordingly.


![call_in_progress.png](screenshots%2Fcall_in_progress.png)


### 2. Internet Radio Streaming
- **Foreground Service:** The app provides continuous internet radio streaming using a `Foreground Service`, ensuring the radio continues playing even when the app is minimized or the screen is off.


![radio_stations.png](screenshots%2Fradio_stations.png)


### 3. Weather Updates
- **Retrofit Library:** The app fetches current weather information from Yahoo weather API using the `Retrofit` library

### 4. Route Tracking
- **Mapping Feature:** Users can write their desired destination, and get the route and steps for driving to their destination. This feature uses Google Directions API.


![navigation.png](screenshots%2Fnavigation.png)


### 5. Localization
- **Multi-language Support:** The app includes localization (supports Hebrew and English).

### 6. Dark/Light Mode
- **Theme Support:** The app supports both dark and light modes according to their system settings.

<p align="center">
  <img src="screenshots%2Fhomepage_light.png" alt="Image 1" width="45%">
  <img src="screenshots%2Fhomepage.png" alt="Image 2" width="45%">
</p>



https://github.com/user-attachments/assets/a843f6c4-2d13-46d6-9da2-221f91dc45e3



## Libraries Used

- **Retrofit:** For handling network requests and JSON parsing.
- **Gson:** Used with Retrofit for converting JSON data into Java objects.
- **AndroidX Components:** Utilized for compatibility and enhanced app performance.
- **Material Design:** For a consistent and modern user interface.
- **Google Maps API:** (Optional) Used for route tracking and displaying maps.

## Challenges and Issues

### Call Handling
- **Potential Crashes:** If the app tries to access telephony functions without proper permissions, it may crash. This can also occur if the app is not properly handling lifecycle events, leading to resources being accessed when they shouldn't be.
- **Known Issue:** Retrieving the calling number (got null).

### Internet Radio Streaming
- **Known Issue** Doesn't support AAC streaming type
