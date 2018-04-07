
#include "Server.h"
#include "ChatException.h"

Server::Server(const char *ip, int port) {
    serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == -1){
        throw ChatException(1000);
    }
    memset(&serverAddress, 0, sizeof(serverAddress));

    if (ip != nullptr) {
        serverAddress.sin_addr.s_addr = inet_addr(ip);
    } else {
        serverAddress.sin_addr.s_addr = INADDR_ANY;
    }
    serverAddress.sin_port = htons(port);
    serverAddress.sin_family = AF_INET;
}

Server::~Server() {
    close(this->serverSocket);
}

void Server::binding() {
    if (::bind(serverSocket, (const sockaddr*)&serverAddress, sizeof(serverAddress)) == -1)
        throw ChatException(1001);

}

void Server::listening(int size) {
    if (size <= 0) throw ChatException(1002);
    if (listen(this->serverSocket, size) == -1){
        throw ChatException(1002);
    }
}
