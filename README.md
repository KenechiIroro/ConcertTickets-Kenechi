CONCERT TICKETS 2.0 by Kenechi Iroro 001362581

This version is Concert Tickets 2.0, the new lab app from the ELEE1146 Mobile Applications for Engineers module. The app provides an option to choose the artist, how many tickets to buy, and subtracts the price from the number of tickets. I’ve adapted the basic idea and made it work better – improved UI, pricing rules, Kotlin coroutines, and better MVVM-style structure.

The basic goal was to create modern Android development using Jetpack Compose and object-oriented design and handling background work while maintaining the app being easy to use and intuitive.

Features

- Pick your favorite artist by dropdown menu .

- The price of tickets differs depending on the artist.

- Bulk discount for 5 or more tickets.

- Service fee $4.00 added per ticket.

- Friday and Saturday evening prices at peak

- Modern HTML development fully via Jetpack Compose.

- Help screen explaining how everything works

- MVVM separating UI and business logic

- background calculations for the app to make it more responsive

Architecture & Design

The app I created is in MVVM style, with the UI and logic separating so that it’s easier to read and keep consistent.

Main Parts:

- MainActivity.kt – Connects app and direction between the main screen and help screen.

- User Interface – Adminitable UI functions, just for layout and display.

- TicketViewModel – manages the selected artist, ticket count and total price. All calculations are done here.

- Band data class – A simple model for storing artist names and base prices.

Concurrency

I had Kotlin coroutines in the ViewModel to run ticket calculations on the background. This will prevent the UI from freezing while the calculation takes place, and it will clean itself up after the screen closes.

UI / UX Design

Using Material Design ideas is the interface built in Jetpack Compose.
Colours: Dark blue primary colour with light backgrounds for a good contrast.

Layout: Works on different screen sizes and orientations.

Accessibility: clear text, good size buttons and type fonts

Scaffold, TopAppBar, Column, Row, TextField, DropdownMenu, Card, and Navigation components

Screens

Home Screen: Select artist, enter ticket number, check for charge update

Help Screen: Instructions for how to use the app and how to purchase prices

How to Build & Run

1. Clone the repo

GITHUB 

2. Open a version of android Studio Giraffe or newer.

3. Let Gradle sync

4. Connect a device or get an emulator (API 24+)

5. Click Run

Technologies Used

Kotlin

The Android Studio

The Jetpack Compose

Material Design 3

Kotlin Coroutines

Jetpack Navigation (Compose)

Pattern of MVVM

Threading background

Background Tasks

The app computes price in a coroutine so that the UI doesn’t lock up. It works on a small load spinning spinner. I made sure to reset the loading state when something has a problem, even if it happened.

References

Developers – Jetpack Compose documents

The Guide to Android Developers: Kotlin Coroutines

Material Design website

Reference notes and laboratory examples

AI Usage

Some parts of the project were made up with the help of AI tools like ChatGPT, but I was careful checking, adjusting and rewriting in order to meet my expectations and needs. The final code and report are mine.

Licence

Educational Community License 2.0 (ECL)

Author

KENECHI Iroro,GITHUB,001362581

Future Improvements

If I had more time I’d love to add.

Save previous orders locally e.g. with Room or DataStore.

More ticket types such as VIP or seated tickets.

Code is structure but i would like to have made it modular

It improves accessibility for screen readers.

Smooth animations to make the app feel cleaner and faster.

Some graphics design but i left that out to not loose focus on more paramount objectives.

