# Roomie — Smart Roommate Management App

A modern Android application designed to help roommates organize their shared living space.

## About

Roomie centralizes all shared responsibilities and information in one app, accessible to all household members in real time. The goal is to reduce misunderstandings between roommates and simplify everyday household management.

## Features

- **Authentication** — registration, login, password reset
- **Household system** — create a household, join via invite code, share invite code
- **Chore management** — create, assign, track and complete household tasks with deadline support
- **Expense splitting** — add shared expenses with automatic debt calculation per member
- **Shopping list** — shared shopping list with real-time sync and quick-add
- **Smart notifications** — FCM push notifications for new chores, expenses and quick actions
- **WorkManager notifications** — scheduled task reminders and overdue alerts
- **Shake gesture** — shake device on Add Chore screen to randomly assign a member
- **Theme support** — Light / Dark / System theme with persistent preference

## Tech Stack

- **Language** — Kotlin
- **UI** — Jetpack Compose + Material 3
- **Architecture** — MVVM (ViewModel + Repository + UI Layer)
- **Backend** — Firebase Authentication + Firebase Firestore
- **Push notifications** — Firebase Cloud Messaging (FCM V1 API)
- **Background work** — WorkManager + Hilt Worker
- **Dependency injection** — Hilt
- **Navigation** — Navigation Compose
- **Async** — Coroutines + StateFlow
- **Local storage** — DataStore Preferences
