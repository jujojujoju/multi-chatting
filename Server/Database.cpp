#include <json/value.h>
#include "Database.h"
#include <stdlib.h>

using namespace std;

sql::Driver *Database::getDriver() {
    return get_driver_instance();
}

sql::Connection *Database::getConnection() {
    driver = getDriver();
//    cout << getenv("MYSQL_HOST");
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
        cout << "query success" << endl;

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
        printDBError(e);
    }
    cout << endl;

    return username;
}

Json::Value Database::getMessages(const std::string &userid) {
    Json::Value temp;
    Json::Value messages;
    Json::Value message;
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery("SELECT m.sender, m.contents, m.sended "
                                         "FROM users u INNER JOIN messages m "
                                         "on u.id='" + userid + "' "
                                         "and m.sended >= u.last_logout "
                                         "AND m.sended <= u.last_login");
        cout << "query success" << endl;

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            message["sender"] = res->getString("sender").asStdString();
            message["sended"] = res->getString("sended").asStdString();
            message["contents"] = res->getString("contents").asStdString();
            messages.append(message);
        }

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);
    }
    cout << endl;

    return messages;
}


bool Database::idCheck(string userid) {
    bool isexist = false;
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery(
                "SELECT id FROM USERS WHERE id='" + userid + "'");
        cout << "query success" << endl;

        if (res->next() > 0) {
            isexist = true;
            cout << "id exist" << endl;
        }

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);

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

        cout << "query success" << endl;
        ok = true;

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);
    }
    cout << endl;

    return ok;
}

bool Database::storeMessage(Json::Value value) {
    string username = value["user"]["name"].asString();
    string contents = value["data"].asString();

    try {
        stmt = getConnection()->createStatement();
        stmt->execute("Insert Into messages(sender, contents, sended) VALUES ('" + username + "', '" + contents +
                      "', NOW());");
        cout << "query success" << endl;

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);
        return false;
    }
    cout << endl;

    return true;
}

void Database::printDBError(const sql::SQLException &e) const {
    cout << "# ERR: SQLException in " << __FILE__;
    cout << "(" << __FUNCTION__ << ") on line " << __LINE__ << endl;
    cout << "# ERR: " << e.what();
    cout << " (MySQL error code: " << e.getErrorCode();
    cout << ", SQLState: " << e.getSQLState() << " )" << endl;
}

bool Database::logout(const std::string &basic_string) {
    string userid = basic_string;
    try {
        stmt = getConnection()->createStatement();
        stmt->execute("UPDATE users set last_logout = NOW() WHERE id = '" + userid + "'");

        cout << "query success" << endl;

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);
        return false;
    }
    cout << endl;

    return true;
}
