#include <string>
#include <util.h>
#include "User.h"
#include "UserManager.h"

User::~User() {
    close(client_socket);
}

User::User(int cs, sockaddr_in ca) {
    client_socket = cs;
    client_address = ca;
    userList = NULL;
    setStatus("offline");
}

const string &User::getName() const {
    return name;
}

void User::setName(const string &name) {
    User::name = name;
}

const string &User::getID() const {
    return ID;
}

void User::setID(const string &ID) {
    User::ID = ID;
}

const string &User::getPwd() const {
    return pwd;
}

void User::setPwd(const string &pwd) {
    User::pwd = pwd;
}

const string &User::getStatus() const {
    return status;
}

void User::setStatus(const string &status) {
    User::status = status;
}

void User::run() {
    if (userList == NULL) return;
    uint64_t size;
    uint64_t read_byte;
    char buf[4096];

    try {
        while (1) {
            if (recv(client_socket, (char*)&size, sizeof(size), 0) <= 0) {
                throw exception();
            }

            int rbyte = 0;
            read_byte = 0;
            stringstream ss;
            while (read_byte < size) {
                if ((rbyte = (int) recv(client_socket, (char*)&buf, sizeof(buf) - 1, 0)) <= 0) {
                    throw exception();
                }
                read_byte += rbyte;
                buf[read_byte] = 0;
                ss << buf;
            }
            processMessage(ss.str());
        }
    } catch (...) {}
    leaveUser();
}

void User::setUserList(vector<User *> *ulist) {
    userList = ulist;
}

void User::processMessage(string msg) {
    Json::Value root;
    Json::Reader reader;
    if (!reader.parse(msg, root)) return;

    try {
        int type = stoi(root["type"].asString());

        switch (type) {
            case TYPE::LOGIN:
                login(root);
                break;
            case TYPE::SIGNIN:
                signin(root);
                break;
            case TYPE::CHAT:
                chat(root);
                break;
            default:
                break;
        }
    } catch(...) {}

}
void User::login(Json::Value value){
    Json::Value user = value["user"];

    setID(user["id"].asString());
    setPwd(user["pwd"].asString());
    setName("jjj");

    Json::Value resp;
    resp["type"] = TYPE::RESPONSE;
    resp["time"] = (uint32_t)getTime();

    // 성공
    if (getID() == "jj" && getPwd() == "dkagh") {
        setStatus("online");
        resp["user"] = getUser();

        resp["data"] = "ok";
        sendMessage(resp);
        cout << "login user : " << userList->size() << endl;

    } else {
        // 실패

        resp["data"] = "fail";
        sendMessage(resp);
    }

    // db 연동
}

void User::signin(Json::Value value) {

}

void User::chat(Json::Value value) {

}

void User::leaveUser() {
    UserManager::mutexLock();
    for (auto iter=userList->begin(); iter != userList->end();) {
        if ((*iter)->getID() == getID()) {
            delete *iter;
            iter = userList->erase(iter);
            break;
        } else {
            iter++;
        }
    }
    cout << "leave user : " << userList->size() << endl;

    UserManager::mutexUnLock();
}

time_t User::getTime() const {
    return time(NULL);
}

Json::Value User::getUser() const {
    Json::Value user;
    user["id"] = getID();
    user["name"] = getName();
    user["status"] = getStatus();
    return user;
}

void User::sendMessage(Json::Value value) {
    Json::StyledWriter writer;
    string msg = writer.write(value);

    uint64_t size = msg.size()+1;

    if (send(client_socket, &size, sizeof(size), 0) <= 0) {
        throw exception();
    }

    if (send(client_socket, msg.c_str(), size, 0) <= 0) {
        throw exception();
    }
}
