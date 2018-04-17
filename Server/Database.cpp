#include <json/value.h>
#include "Database.h"

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

string Database::login(const std::string &userid, const std::string &userPassword) {
    string username = "";
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery(
                "select name from users WHERE id='" + userid + "' AND password='" + userPassword + "'");
        cout << "query seccess" << endl;

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            username = res->getString("name");
            cout << username << endl;
            // 성공
            cout << "login success" << endl;
        }
//        update user last login time
        stmt->execute("UPDATE users set last_login = NOW() WHERE id = '" + userid + "'");

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

bool Database::idCheck(string userid) {
    bool isexist = false;
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery(
                "SELECT id FROM USERS WHERE id='" + userid + "'");
        cout << "query seccess" << endl;

        if (res->next() > 0) {
            isexist = true;
            cout << "id exist" << endl;
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

    return isexist;
}

bool Database::createUser(Json::Value user) {

    bool ok = false;

    string userid = user["id"].asString();
    string userpassword = user["pwd"].asString();
    string username = user["name"].asString();

    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
//        insert new user
        stmt->execute("INSERT INTO users(id, name, password, last_login) VALUES ('" + userid + "', '" + username +
                "', '" + userpassword + "', NOW());");

        cout << "query seccess" << endl;
        ok = true;

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

    return ok;
}
