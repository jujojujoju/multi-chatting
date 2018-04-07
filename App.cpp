#include "App.h"

App::App() : server(nullptr, 5454) {

}

void App::start() {
    server.binding();
    server.listening(10);
    cout << "server listening" << endl;

    while (true) {

    }

}
