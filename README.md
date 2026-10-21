# KU-Design-Comp
- Project for KU CS Design Competition
## Project Overview
- Gunter’s Forge is a small Java web application that simulates an online blacksmith shop. Customers can browse available items, place an order, and check its status in the forge’s queue. The application runs locally and serves a simple web interface using Java’s built-in HTTP server.
## Setup Instructions
Gunter's Forge runs on a localhost at port 8000 by default. Setup requires the two following JDK (java developer kit) terminal commands:

javac -d out app/*.java

This line will complie the program into the the .gitignored "out" folder into java bytes

java -cp app MyProgram

This will run the program class path from the MyProgram file. The local server will be activated after this

To stop the localhost, click 

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
 - HTML and CSS
 - Edited OrderQueue and BlacksmithOrder
 - Update MyProgram for localhosting
### Aarav Sharma
 - Backend/Java
