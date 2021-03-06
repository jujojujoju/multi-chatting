#ifndef MULTICHATTING_USER_H
#define MULTICHATTING_USER_H

#include <iostream>
#include <vector>
#include <sstream>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <pthread.h>
//#include <util.h>
#include <string>

#ifdef __linux__
#include <endian.h>
#endif

#ifdef __linux__
#include <jsoncpp/json/json.h>
#endif
#include "json/json.h"


#include "CThread.h"
#include "Database.h"

using namespace std;

enum TYPE {
    RESPONSE = 100,
    CHAT = 200,
    LOGIN = 300,
    SIGNIN = 400,
    GET_MESSAGE = 500,
    GET_PERSONAL_MESSAGE = 600,
    GET_USERLIST = 700,
    PERSONAL_CHAT = 800
};

class User : public CThread{
private:
    Database database;
    int client_socket;
    sockaddr_in client_address;
    string name;
    string ID;
    string pwd;
    string status;

    vector<User*> *userList;

    User* findUser(string id);

    void run();

public:
    User(int cs, sockaddr_in ca);
    ~User();
    User(const User &user) = delete;
    void operator=(const User &user) = delete;

    const string &getName() const;

    void setName(const string &name);

    const string &getID() const;

    void setID(const string &ID);

    const string &getPwd() const;

    void setPwd(const string &pwd);

    const string &getStatus() const;

    void setStatus(const string &status);

    void setUserList(vector<User*> *ulist);

    int processMessage(string msg);

    void leaveUser(int type);

    void login(Json::Value value);

    void signin(Json::Value value);

    void chat(Json::Value value);

    time_t getTime() const;

    Json::Value getUser() const;

    void sendMessage(Json::Value value);

    void sendMessageAll(Json::Value value);

    void getMessages(Json::Value value);

    void getUserList(Json::Value value);

    void personalChat(Json::Value value);

    void getPersonalMessages(Json::Value value);

    void sendMessage2Target(Json::Value value);
};


#endif //MULTICHATTING_USER_H
