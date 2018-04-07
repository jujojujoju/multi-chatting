#ifndef MULTICHATTING_APP_H
#define MULTICHATTING_APP_H

#include <iostream>
#include <vector>
#include <sstream>
#include "Server.h"

using namespace std;

class App {
private:
    Server server;
public:
    App();
    void start();
};


#endif //MULTICHATTING_APP_H
