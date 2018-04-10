#include "Database.h"
#include <iostream>

using namespace std;

sql::Driver *Database::getDriver() {
    return get_driver_instance();
}

sql::Connection *Database::getConnection() {
    driver = getDriver();
    con = driver->connect("localhost", "root", " ");
    /* Connect to the MySQL test database */
    con->setSchema("multichatting");

    return con;
}

string Database::isLogin(const std::string &userid, const std::string &userPassword) {
    string username = "";
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery(
                "select name from users WHERE name='" + userid + "' AND password='" + userPassword + "'");

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            cout << res->getString("name") << endl;
            // 성공
            cout << "login success" << endl;
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

    return username;
}
