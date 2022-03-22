# Inter-Network Communication Program

The goal of this assignment is to implement an inter-network communication program. The server side of this program can provide a time service similar to NTP, with the purpose of providing time synchronization for the client side. The user interface was implemented by using the javax.swing module and used the java.util.Calendar module to get the time and date. The core of the code is TCP and UDP socket programming. The server program will create a socket and accept the connection, and after processing the request message sent by the client, return the corresponding information, including date(month/day/year), time(hour:minute:second) or an error message(if invalid request was received). Different types of thrown errors and user-unfriendly issues were handled properly and with corresponding output printed.
