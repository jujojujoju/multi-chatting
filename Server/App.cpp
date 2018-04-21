#include "App.h"
#include <stdlib.h>

App::App() : server(nullptr, 5555) {

}

void App::start() {
    server.binding();
    server.listening(10);
//    cout << getenv("MYSQL_HOST") << endl;

    cout << "server listening" << endl;

    while (true) {
        User * user = server.acceptUser();
        if (user == nullptr) continue;
        userManager.addUser(user);
    }

}
