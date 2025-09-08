# FinanceApp

**FinanceApp** is a JavaFX-based desktop application designed to help users manage personal finances efficiently. It allows users to track income, expenses, and account balances in real time, providing an intuitive interface for financial management.

---

## Table of Contents

* [Features](#features)
* [Installation](#installation)
* [Usage](#usage)
* [Tech Stack](#tech-stack)
* [Database](#database)
* [Future Enhancements](#future-enhancements)


---

## Features

* **User Authentication** – Secure login and registration system.
* **Income & Expense Tracking** – Add, edit, and categorize income and expenses.
* **Real-Time Balance Preview** – Live calculation of balances when entering transactions.
* **Detailed Summaries** – Automatic computation of total income, total expenses, and net balance.
* **Multi-User Support** – Each user has a separate account with isolated financial records.
* **Clean JavaFX Interface** – Modern and responsive GUI.
* **Lightweight Database** – Uses SQLite for local storage of financial data.

---


## Installation

1. **Clone the repository**:

```bash
git clone https://github.com/your-username/FinanceApp.git
```

2. **Open the project** in your preferred Java IDE (IntelliJ IDEA, Eclipse, NetBeans).

3. **Add JavaFX libraries** to your project:

   * Download the JavaFX SDK: [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
   * Configure your IDE to include the JavaFX SDK.

4. **Run the project**:

   * Set `LoginController` as the main entry point.
   * Ensure the database file `financeapp.db` is created in the project root.

---

## Usage

1. Launch the application.
2. Register a new account or login with an existing account.
3. Navigate through the dashboard to:

   * Add income and expenses
   * View total balance, income, and expenses
   * Access the About section for app information
4. All transactions are stored locally in an SQLite database.

---

## Tech Stack

* **Language:** Java
* **GUI Framework:** JavaFX
* **Database:** SQLite
* **Architecture:** MVC (Model-View-Controller)

---

## Database

* **SQLite** is used to store all user, income, and expense data.
* Tables include:

  * `users` – Stores user credentials.
  * `income` – Stores all income entries with user association.
  * `expenses` – Stores all expense entries with user association.

---

## Future Enhancements

* Export reports to CSV or PDF.
* Add visual charts for income and expense trends.
* Cloud synchronization for multi-device access.
* Multi-currency support.

---

