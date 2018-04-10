#include "App.h"
#include "User.h"
#include "mysql_connection.h"

#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/statement.h>
#include <cppconn/prepared_statement.h>

App::App() : server(nullptr, 5559) {

}

void App::start() {
    try {
        sql::Driver *driver;
        sql::Connection *con;
        sql::Statement *stmt;
        sql::ResultSet *res;
        sql::PreparedStatement *pstmt;

        /* Create a connection */
        driver = get_driver_instance();
        con = driver->connect("localhost", "root", " ");
        /* Connect to the MySQL test database */
        con->setSchema("multichatting");

        /* Select in ascending order */
        stmt = con->createStatement();
        res = stmt->executeQuery("SELECT * FROM USERS");

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            cout << res->getString(1) << endl;
            cout << res->getString("name") << endl;
            cout << res->getString("id") << endl;
            cout << res->getString("password") << endl;
        }
        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        cout << "# ERR: SQLException in " << __FILE__;
        cout << "(" << __FUNCTION__ << ") on line " << __LINE__ << endl;
        cout << "# ERR: " << e.what();
        cout << " (MySQL error code: " << e.getErrorCode();
        cout << ", SQLState: " << e.getSQLState() << " )" << endl;
    }
    cout << endl;

    server.binding();
    server.listening(10);
    cout << "server listening" << endl;

    while (true) {
        User * user = server.acceptUser();
        if (user == nullptr) continue;
        userManager.addUser(user);
    }

}
