# KU-Design-Comp
- Project for KU CS Design Competition
## Project Overview
- Gunter’s Forge is a small Java web application that simulates an online blacksmith shop. Customers can browse available items, place an order, and check its status in the forge’s queue. The application runs locally and serves a simple web interface using Java’s built-in HTTP server.
## Setup Instructions
1. Install JDK 11 or later.
2. Open a terminal and change to the project’s root folder (the folder containing `app/` and `web/`).
3. Compile the Java files:
   ```bash
   javac -d out app/*.java
4. Start the server with java -cp out MyProgram
5. Open the local host in a browser, http://localhost:8000
6. To stop the server, press Ctrl+C in the terminal
## Usage Guide
- Start the server by following the setup instructions, open the localhost.
- Under **Place an Order**, enter a name, select an item, and enter a delivery distance, selecting from the available items.
- Submit the form, and the status page will show your order number and its place in the queue.
- To check an order later, enter the order number and click **Track Order**, and refresh the status page to see updates.
## Feature list
- Browse a catalog of forge items and their prices.
- Place an order by entering a name, choosing an item, and specifying a delivery distance.
- Receive a unique order number after submitting an order.
- Check an order’s position in the queue and estimated wait time.
- Track when an order is ready.
## Team contributions
- Listed Below
### Jacob Palan
 - Wrote README.md
### Aarav Sharma
 - Backend
 - Java
