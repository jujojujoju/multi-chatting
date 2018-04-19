# Multichatting

This program is a multi-chat program using Android applications.
Users are managed through Sign up, and users can send and receive text and files in a chat room where multiple users are connected at the same time.

## Function

* Server
    -	TCP socket 
    -	Multithread 
    -	Message delivery 

* Client
    - Create user profile (login and password creation and authentication).
    - Register to server and connect.
    - Retrieve chat history.
    - Check peer status (online, offline, busy).
    - Send and receive message.

## Development setting
#### Server

Move to Server Project
```sh
$ cd Server
```

Create Makefile
```sh
$ cmake .
```

Build
```sh
$ make
```
Run Server of Multichatting
```sh
$ ./multichatting
```

#### Client

Move to Client Project
```sh
$ cd Client
```

Uses project's gradle wrapper to execute clean and build task
```sh
$ ./gradlew clean build
$ ./gradlew :app:dependencies
```

