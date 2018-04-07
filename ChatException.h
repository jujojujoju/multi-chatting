#ifndef MULTICHATTING_CHATEXCEPTION_H
#define MULTICHATTING_CHATEXCEPTION_H

#include <iostream>

using namespace std;

class ChatException {
private:
    int code;

public:
    ChatException(int code) : code(code) {};

    int getCode() const { return code; }
    void printError() {
        switch (code){
            case 1000:
                cerr << "socket create error (" << code << ")" << endl;
                break;
            case 1001:
                cerr << "socket binding error (" << code << ")" << endl;
                break;
            case 1002:
                cerr << "socket listening error (" << code << ")" << endl;
                break;
            case 1100:
                cerr << "session error (" << code << ")" << endl;
                break;
            default:break;
        }
    }


};


#endif //MULTICHATTING_CHATEXCEPTION_H
