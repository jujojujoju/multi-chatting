#ifndef MULTICHATTING_SERVER_H
#define MULTICHATTING_SERVER_H

#include <iostream>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>

class Server {
private:
    int serverSocket;
    sockaddr_in serverAddress;
public:
    Server(const char *ip=nullptr, int port=0);
    ~Server();

    void binding();
    void listening(int size);

};


#endif //MULTICHATTING_SERVER_H
