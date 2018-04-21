#include "User.h"
#include "UserManager.h"
#include <iomanip>
#include <ctime>
User::~User() {
    close(client_socket);
}

User::User(int cs, sockaddr_in ca) {
    client_socket = cs;
    client_address = ca;
    userList = NULL;
    setStatus("offLine");
}


void User::run() {
    cout << "connection!" << endl;
    if (userList == NULL) return;
    uint64_t size;
    uint64_t read_byte;
    char buf[4096];

    try {
        while (1) {
            cout << "message processing..." << endl;
//          만약 길이정보가 8바이트 이하의 길이면 리턴
            if (recv(client_socket, (char *) &size, sizeof(size), 0) <= 0) {
                throw exception();
            }
            size = htonll(size);
            cout << "recieve size : " << size << endl;

            int rbyte = 0;
            read_byte = 0;
            stringstream ss;
            while (read_byte < size) {
                if ((rbyte = (int) recv(client_socket, (char *) &buf, sizeof(buf) - 1, 0)) <= 0) {
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
    } catch (...) {}

}

void User::login(Json::Value value) {
    Json::Value user = value["user"];
    Json::Value resp;
    resp["type"] = TYPE::RESPONSE;
    resp["time"] = (uint32_t) getTime();

    if (findUser(user["id"].asString())) {
        resp["name"] = "";
        resp["data"] = "alreay exists";
        sendMessage(resp);
        return;
    }

    setID(user["id"].asString());
    setPwd(user["pwd"].asString());

    string username = database.login(getID(), getPwd());
    if (username != "") {
        setStatus("online");
        setName(username);
        resp["user"] = getUser();
        resp["data"] = "ok";
        resp["messages"] = "";
        sendMessage(resp);
        cout << "login user : " << userList->size() << endl;
    } else {
        // 실패
        resp["name"] = "";
        resp["data"] = "fail";
        sendMessage(resp);
    }
}

void User::signin(Json::Value value) {
    Json::Value user = value["user"];

    Json::Value resp;
    resp["type"] = TYPE::RESPONSE;
    resp["time"] = (uint32_t) getTime();

//    id check
    if (!database.idCheck(user["id"].asString())) {
        setID(user["id"].asString());
        setPwd(user["pwd"].asString());
//        sign in 때에는 사용자이름을 직접 입력받는다
        setName(user["name"].asString());
        setStatus("online");

        if (database.createUser(user));
        {
            resp["user"] = getUser();
            resp["data"] = "ok";
            sendMessage(resp);
            cout << " user sign in : " << userList->size() << endl;
        }
    } else {
        // 실패
        resp["data"] = "fail";
        sendMessage(resp);
    }

}
std::wstring wtime(const time_t &t) {
    std::tm tm = *std::localtime(&t);
    std::wstringstream wss;
    wss << std::put_time(&tm, L"%F %T");
    return wss.str();
}
void User::chat(Json::Value value) {
    Json::Value user = value["user"];
    cout << value["msg"].asString() << endl;

    Json::Value resp;
    resp["type"] = TYPE::CHAT;

    string stringtime;
    wstring wstringtime = wtime(getTime());
    resp["time"] = (stringtime.assign(wstringtime.begin(), wstringtime.end())).c_str();
    resp["user"] = getUser();
    resp["data"] = value["msg"].asString();
    sendMessageAll(resp);
}

void User::leaveUser() {

    database.logout(getID());

    UserManager::mutexLock();
    for (auto iter = userList->begin(); iter != userList->end();) {
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

void User::sendMessageAll(Json::Value value) {
    database.storeMessage(value);
    for (auto iter = userList->begin(); iter != userList->end(); iter++) {
        (*iter)->sendMessage(value);
    }
}

void User::sendMessage(Json::Value value) {
    Json::StyledWriter writer;
    string msg = writer.write(value);

    uint64_t size = msg.size() + 1;
    uint64_t s = htonll(size);

    cout << "send : " << size << endl;

    UserManager::mutexLock();
    if (send(client_socket, &s, sizeof(s), 0) <= 0) {
        UserManager::mutexUnLock();
        throw exception();
    }

    if (send(client_socket, msg.c_str(), size, 0) <= 0) {
        UserManager::mutexUnLock();
        throw exception();
    }
    UserManager::mutexUnLock();
}

void User::setUserList(vector<User *> *ulist) {
    userList = ulist;
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

User *User::findUser(string id) {
    if (id == "") return NULL;
    for (auto iter = userList->begin(); iter != userList->end(); iter++) {
        if ((*iter)->getID() == id) return *iter;
    }
    return NULL;
}
