# CPS 706 Group 8

## Next step
+ ~~using a text window to present most of the output instaed of using showMessageDialog~~
+ ~~server side: using while loop cause the program un-interruptable~~
+ if server died / not responding, the client will be block forever when waiting a response -> extends Thread and user other thread to interrupt; or use wait(); or use thread.suspend().
+ Add back for server side -> copy back() method from the end of client.java, interrupt and close socket if back is pressed
+ ~~Detection for illegal port is not working properly for client side -> add catch clauses~~