# JewelryKo — Fine Jewelry Business Management System

JewelryKo is a Java Swing desktop management system designed to digitize operational tasks for fine jewelry businesses, replacing traditional physical logbooks with an organized, database-backed platform.

---

## Core Features

* **Admin Authentication & Role Security:** Protected access via Admin ID and password, with additional management password validation for administrative actions[cite: 2, 8].
* **Inventory Management:** View, search, filter (by type, karat, size, price, and weight), add, inspect, and sell fine jewelry items.
* **Sales & Transaction Recording:** Track transactions, calculate balance remaining, and manage payment statuses (Unpaid, Partial, Paid, Cancelled).
* **Order Status Tracking:** Monitor unavailable, reserved, processing, shipped, or delivered orders with priority handling.
* **User Management:** Panel to manage both Admin credentials/salaries and Client purchase records.
* **Dynamic Price Management:** Live pricing engine calculates individual jewelry prices based on updated market gold rates ($Price = GoldRate \times Weight$).

---

## Tech Stack

* **Language:** Java
* **GUI Framework:** Java Swing
* **Database:** SQLite (via SQLite JDBC Driver)
* **Architecture:** Component-based UI (`MainFrame`, `LoginFrame`, modular View panels)

---

## Repository Structure

JewelryKo/
├── src/                        # Java source code files
│   ├── MainFrame.java          # Main program entry point
│   ├── LoginFrame.java         # Authentication frame
│   ├── DashboardFrame.java     # Navigation hub frame
│   ├── ViewInventoryPanel.java # Inventory UI component
│   ├── ViewItemPanel.java      # Detailed item modal & sell actions
│   ├── ViewRecordFrame.java    # Sales & payment record UI
│   ├── ViewTransactionFrame.java # Edit transaction details
│   ├── ViewStatusFrame.java    # Order status tracking UI
│   ├── ViewUserFrame.java      # Admin and client management UI
│   ├── PriceCalculator.java    # Gold rate calculation logic
│   └── ...                     # Models and Data Access Objects
├── database/                   # SQLite database (.db) files
├── lib/                        # External libraries (SQLite JDBC Driver)
├── Image/                      # Application image assets and featured photos
└── README.md                   # System documentation
### Running the Application

1. **Clone the Repository:**
   ```bash
   git clone [https://github.com/your-username/jewelryko-management-system.git](https://github.com/your-username/jewelryko-management-system.git)
