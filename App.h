#ifndef MULTICHATTING_APP_H
#define MULTICHATTING_APP_H

#include <iostream>
#include <vector>
#include <sstream>
#include "Server.h"
#include "UserManager.h"

using namespace std;

class App {
private:
    Server server;
    UserManager userManager;
public:
    App();
    void start();
};


#endif //MULTICHATTING_APP_H
