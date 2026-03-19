# 🐝 Bee Canteen

A modern, real-time polling and voting Android application built to manage canteen food choices and preferences. Built entirely with Kotlin and Jetpack Compose, this app features a robust role-based system for both regular users and administrators.

## ✨ Key Features

**For Users:**
* **Real-Time Voting:** Cast and revoke votes instantly. UI updates in real-time as other users vote.
* **Time-Restricted Polls:** Polls are intelligently filtered and only active during specific daily time windows.
* **Custom Daily Reminders:** Users can schedule their own personalized, locally-triggered daily push notifications to remind them to vote.
* **Modern UI/UX:** Pull-to-refresh capabilities, smooth animations, and a responsive design.

**For Administrators:**
* **Dashboard Management:** Create new categories, set specific voting time windows, and manage poll options.
* **Live Analytics:** View real-time vote counts and detailed poll statistics.

**Cross-Device Support:**
* **Adaptive Navigation:** Utilizes `NavigationSuiteScaffold` to automatically adapt the UI between a bottom navigation bar on phones and a side navigation rail on tablets and foldables.

## 🛠️ Tech Stack & Architecture

This project is built using modern Android development practices, adhering to **Clean Architecture** and the **MVVM (Model-View-ViewModel)** design pattern.

* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Language:** [Kotlin](https://kotlinlang.org/)
* **Architecture Components:** ViewModel, StateFlow, Coroutines
* **Dependency Injection:** [Dagger-Hilt](https://dagger.dev/hilt/)
* **Backend & Database:** [Firebase](https://firebase.google.com/) (Authentication & Firestore Realtime Database)
* **Local Storage:** [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (Preferences)
* **Background Tasks:** `AlarmManager` & `BroadcastReceiver` (for scheduling daily notifications)
* **Navigation:** Jetpack Compose Navigation

## 📸 Screenshots

*(Add your screenshots here! Create a folder called `art` or `screenshots` in your repo, upload your images, and replace the links below.)*

| Auth & User Flow | Admin Panel | Reminder Setup | Tablet/Foldable View |
| :---: | :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/a9cf3d93-336e-41fd-89c5-d6d97ca6d2b2" width="250" alt="login screen" /><br><br><img src="https://github.com/user-attachments/assets/d8267ca7-0791-4179-9dba-0af2df8d2f43" width="250" alt="registration screen" /><br><br><img src="https://github.com/user-attachments/assets/ca52477e-5a48-459a-ab19-13e5fb754635" width="250" alt="voting screen" /> | <img src="https://github.com/user-attachments/assets/5f8b2d06-80de-4103-824a-9a7e8c6fef65" width="250" alt="admin screen" /><br><br><img src="https://github.com/user-attachments/assets/ba2f08c1-be60-4ca7-83e4-baababd1be7d" width="250" alt="admin add category screen" /><br><br><img src="https://github.com/user-attachments/assets/c18ad4e4-790e-4888-9cd8-eb663d6f8ce8" width="250" alt="admin number of voters screen" /> | <img src="https://github.com/user-attachments/assets/19c6163f-f17d-4ce7-912e-2f2b7ffee72b" width="250" alt="add reminder screen" /><br><br><img src="https://github.com/user-attachments/assets/4288a014-88e4-4741-867f-a1cbd4a75a8b" width="250" alt="time picker screen" /> | <img src="https://github.com/user-attachments/assets/44b6d37a-4f32-4e78-b135-1175be7b993a" width="250" alt="foldable screen" /> |







## 🚀 Getting Started

### Prerequisites
* Android Studio (Latest version recommended)
* JDK 17+
* A Firebase Project

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/selva7378/BeeCanteen.git
