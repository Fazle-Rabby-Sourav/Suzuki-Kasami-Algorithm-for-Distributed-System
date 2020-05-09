## Suzuki Kasami's Mutual Exclusion Algorithm for the Distributed system
This project is an implementation of the token based Suzuki-Kasami's broadcasting algorithm in a distributed system. Here we consider five sites for demonstration. 


#### Details of the system:
The Suzuki–Kasami algorithm is a token-based algorithm for achieving mutual exclusion in distributed systems. In the system there are multiple site which can execute some specific task with entering into the critical section, mutual exclusively. To achieve this mutual exclusion, we implement token based Suzuki-Kasami Broadcasting Algorithm here.
If a site possess a unique token, then only it can be allowed to execute in the critical section.

For implementation we use, Java 1.8, Java Socket Programming, and Threading. the program will achieve mutual exclusion over the network or on a local computer using different ports. Note that, the ip's and ports should be ensured to be accessible to each sites.


#### Configuration files:
There is a configuration file in the same level of other source code, `/src/nodes.config`. This file contain he information associated with site number, its ip address and it's port number.

- The format of `/src/nodes.config` file is : 
`<site_id> <Ip_of_site> <port>`

This file should be changed as requirement. (e.g. how many sites should be in the system). Here, client_id is started from 1 and can be assigned incrementally to each client.

#### Steps:
 - The program can be run locally on terminals.
 - Each terminal runs a single site.
 - To start a site: 
    - We need to run the command `javac SuzukiKasamiMutualExclusion.java`
    - Then `java SuzukiKasamiMutualExclusion.java`
 - Once the program starts, it will ask the site number : `Enter site number (1-5):`
 - After entering the corresponding site number, it would display the message `Press ENTER to enter CS:`
 - It is recommended to ensure that all sites are initialized and running(one terminal for each site for running the program in a single local computer). 
 - When all the sites are running simultaneously, press enter on any site for requesting entering into the critical section. 
 - Initially, Site 1 possesses the token.
 - In this implementation, the program will run until terminating it deliberately using `Ctrl+C` or `Cmd+C`.
