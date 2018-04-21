#include "UserManager.h"

const int UserManager::MAXUSER = 9999999;
pthread_mutex_t UserManager::Mutex = PTHREAD_MUTEX_INITIALIZER;

void UserManager::addUser(User *pUser) {
//    만약 유저가 넘치면 튕군다
    if(userList.size() > MAXUSER) {
        delete pUser;
        return;
    }

//    각 유저에게 전체유저리스트 전달
    pUser->setUserList(&userList);
//    유저 스레드 시작
    cout << "user thread start" << endl;
    pUser->start();
//    유저리스트 충돌방지 mutex lock
    mutexLock();
    userList.push_back(pUser);
    mutexUnLock();
}