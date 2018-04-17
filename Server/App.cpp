#include "App.h"
App::App() : server(nullptr, 5556) {

}

void App::start() {
    server.binding();
    server.listening(10);
    cout << "server listening" << endl;

    while (true) {
        User * user = server.acceptUser();
        if (user == nullptr) continue;
        userManager.addUser(user);
    }

}
