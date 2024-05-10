# Guess Game

Welcome to the Guess Game project! This application implements a multiplayer guessing game utilizing Java and socket programming. Players compete to guess a number closest to 2/3 of the average of all guesses, adding an exciting twist to the traditional guessing game concept.

## How to Play

1. **Start the Server**: Run the `ServerDriver.java` file to initialize the multithreaded server, enabling multiple players to connect concurrently.

2. **Connect Players**: Players can connect to the server either locally or by specifying the server's IP address. Utilize the provided client program or implement custom clients using socket programming to join the gaming session.

3. **Guess a Number**: Each connected player submits their guess for the number. The objective is to guess a number closest to 2/3 of the average of all guesses.

4. **Determine the Winner**: Once all players have submitted their guesses, the server calculates the average of all guesses and determines 2/3 of that value. The player whose guess is nearest to this calculated value emerges as the winner of the round!

5. **Repeat and Enjoy**: Engage in multiple rounds of thrilling gameplay with friends or colleagues, and relish the competitive spirit of the Guess Game!

## Features

- **Multithreaded Server**: The server boasts multithreading capabilities, ensuring seamless handling of multiple client connections simultaneously, thus guaranteeing a responsive gaming experience.
- **Command Line Client**: Players can connect to the server using a command line tool, providing a simple and straightforward interface for participating in the game. Typing `help` within the client program provides additional details and instructions for interaction.
- **Private Messaging**: Players have the ability to engage in private messaging with each other, fostering communication and camaraderie during gameplay sessions.
- **Customizable**: The project structure allows for easy customization of game rules and the incorporation of additional features, empowering users to tailor the gameplay to their preferences.

## Getting Started

1. Clone the repository to your local machine.

2. Compile the Java files using your preferred IDE or command line tools.

3. Run the `ServerDriver.java` file to start the server.

4. Connect clients to the server locally or specify the server's IP address to join remotely, utilizing the provided client program or implementing custom clients using socket programming.

5. Begin playing and immerse yourself in the exhilarating Guess Game experience!

