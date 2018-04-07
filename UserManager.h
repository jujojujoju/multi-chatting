#ifndef MULTICHATTING_USERMANAGER_H
#define MULTICHATTING_USERMANAGER_H


#include "User.h"

class UserManager {

private:
    static pthread_mutex_t Mutex;
    static const int MAXUSER;

public:
    vector<User*> userList;

    void addUser(User *pUser);

    static void mutexLock() { pthread_mutex_lock(&Mutex); }
    static void mutexUnLock() { pthread_mutex_unlock(&Mutex); }
};

#endif //MULTICHATTING_USERMANAGER_H
