#ifndef __CTHREADEXCEPTION_H__
#define __CTHREADEXCEPTION_H__

#include <exception>

using std::exception;

// thread safe
class CThreadException : public exception {
private:
    int error_code;

public:
    CThreadException(int code=0) : error_code(code) {}

    virtual const char* what() const throw() {
        switch (error_code) {
        case 0:
            return "**Error : CThreadException(0) : CThread";

        case 1:
            return "**Error : CThreadException(1) : CThread Start";

        case 2:
            return "**Error : CThreadException(2) : CThread Join";
        }

        return "";
    }

};

#endif