# KU-Design-Comp
- Project for KU CS Design Competition
## Project Overview
- Gunter’s Forge is a small Java web application that simulates an online blacksmith shop. Customers can browse available items, place an order, and check its status in the forge’s queue. The application runs locally and serves a simple web interface using Java’s built-in HTTP server.
## Setup Instructions
1. Install JDK 11 or later.
2. Open a terminal and change to the project’s root folder (the folder containing `app/` and `web/`).
3. Start the application from the project root. This compiles the latest Java files and starts the server:
   ```bash
   sh run.sh
   ```
4. Open the URL printed in the terminal (normally [http://localhost:8000](http://localhost:8000)).
   If port 18083 is already in use, stop the other server with **Ctrl+C** in its terminal, or choose another port with `sh run.sh 8001` and open [http://localhost:8001](http://localhost:8001).
5. To stop the server, press **Ctrl+C** in the terminal.
## Usage Guide
- Play Blocksmith on the home page: select one of the three ingot shapes, then choose an open tile on the 8-by-8 board to place it.
- Complete a full row or column to earn 15 gold and 100 points. Clearing multiple lines at once adds combo gold and points.
- Start a fresh board when you run out of moves; your gold and score carry over.
- Spend enough gold to place an order for a forge item, then use its order number to track progress.
## Feature list
- Browse a catalog of forge items and their prices.
- Place an order by entering a name, choosing an item, and specifying a delivery distance.
- Receive a unique order number after submitting an order.
- Check an order’s position in the queue and estimated wait time.
- Track when an order is ready.
- Earn gold with a Java-powered block puzzle and spend it on forge orders.
## Team contributions
- Listed Below
### Jacob Palan
 - Wrote README.md
 - HTML and CSS
 - Edited OrderQueue and BlacksmithOrder
 - Update MyProgram for localhosting
### Aarav Sharma
 - Backend
 - Java
 - Wrote README.md
##AI usage
AI was used for the following tasks in this project
###Debugging
When we came across issues in the code, and failed to solve it after multiple tries, we used ai to find issues and guide us in the direction of solving them.
 ###Integrating terminal based java into the program to make it web based.
Originally, this project was meant to be experienced in the exclusively terminals. While our MyProgram logic was right, we used AI to convert it into a series of methods for integration with our new front end.
###Minigame
The mining minigame used AI to create the minecraft gold pixel art and +10 gold effect
AI was used to make the Raid Blast minigame
