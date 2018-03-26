# CS2105 
 In this assignment, a file will be transferred over UDP protocol on top of an unreliable channel that may either corrupt or drop packets randomly (but always deliver packets in order).

## Checkpoints
 | Marks | Comment | Status |
 | :---: | :-----: | :----: |
 | 2 pts | Programs compile on **sunfire** without error; program execution follows specified Java commands exactly (see sections below). In addition, submit Java programs only. The files **are not zipped, tarred, or hidden** somewhere in a folder and have the correct (file and class) names. | ✓ |
 | 1 pt  | Programs can successfully send a small file (a few KB) from Alice to Bob in a perfectly reliable channel (i.e. no error at all). 	|   |
 | 1 pt  | Programs can successfully send a large file (< 4GB) from Alice to Bob in a perfectly reliable channel (i.e. no error at all). 	|   |
 | 2 pts | Programs can successfully send a (small or large) file from Alice to Bob in the presence of _both data packet corruption and ACK/NAK packet corruption._ |   |
 | 2 pts | Programs can successfully send a (small or large) file from Alice to Bob in the presence of _data packet lost and ACK/NAK packet loss._		    |   |
 | 1 pt  | Programs can successfully send a (small or large) file from Alice to Bob in the presence of _both packet corruption and packet loss._		    |   |
 | 1 pt  | Programs pass time test (see Section UnreliNET Class). 	|   |
 
#  Run
   1.	Bob
   1.	UnreliNET
   1.	Alice
## Bob
``` Bash
	java Bob 9001
```
## UnreliNET
``` Bash
	java UnreliNET 0.1 0.1 0.1 0.1 9000 9001
```
## Alice
``` Bash
	java Alice ../test/cs2105.zip 9000 notes.zip
```
