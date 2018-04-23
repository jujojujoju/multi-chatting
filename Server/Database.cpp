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
        stmt->execute("UPDATE users set last_login = NOW() , status='online' WHERE id = '" + userid + "'");

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);
    }
    cout << endl;

    return username;
}

Json::Value Database::getPersonalMessages(std::string userid, std::string targetid) {
    Json::Value messages;
    Json::Value message;
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery(
                "SELECT * FROM personal_messages WHERE (senderID='" + userid + "' AND receiverID='" + targetid +
                "') or (senderID='" + targetid + "' AND receiverID='" + userid + "');");

        cout << "query success" << endl;

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            message["sender"] = res->getString("sender").asStdString();
            message["receiver"] = res->getString("receiver").asStdString();
            message["senderID"] = res->getString("senderID").asStdString();
            message["receiverID"] = res->getString("receiverID").asStdString();
            message["sended"] = res->getString("send_time").asStdString();
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

Json::Value Database::getMessages(const std::string &userid) {
    Json::Value messages;
    Json::Value message;
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery("SELECT * FROM MESSAGES");

        cout << "query success" << endl;

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            message["sender"] = res->getString("sender").asStdString();
            message["senderID"] = res->getString("senderID").asStdString();
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

Json::Value Database::getUserList(std::string userid) {
    Json::Value users;
    Json::Value user;
    try {
        /* Select in ascending order */
        stmt = getConnection()->createStatement();
        res = stmt->executeQuery("SELECT * FROM USERS WHERE id != '" + userid + "'");
        cout << "query success" << endl;

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            user["name"] = res->getString("name").asStdString();
            user["status"] = res->getString("status").asStdString();
            user["id"] = res->getString("id").asStdString();
            users.append(user);
        }

        delete stmt;
        delete con;

    } catch (sql::SQLException &e) {
        printDBError(e);
    }
    cout << endl;

    return users;
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

bool Database::storePersonalMessage(Json::Value value) {
    string username = value["user"]["name"].asString();
    string userid = value["user"]["id"].asString();
    string targetid = value["target"].asString();
    string contents = value["data"].asString();
    string targetUsername;

    try {

        stmt = getConnection()->createStatement();
        res = stmt->executeQuery("SELECT name FROM USERS WHERE id = '" + targetid + "'");
        cout << "query success" << endl;

        while (res->next()) {
            cout << "\t... MySQL replies: ";
            targetUsername = res->getString("name").asStdString();
        }

        stmt->execute(
                "INSERT INTO personal_messages (sender, senderID, contents, receiver, receiverID, send_time) VALUES ('" +
                username + "','" + userid + "', '" + contents + "', '" + targetUsername + "', '" + targetid +
                "' , NOW());");
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

bool Database::storeMessage(Json::Value value) {
    string username = value["user"]["name"].asString();
    string contents = value["data"].asString();
    string userid = value["user"]["id"].asString();
    try {
        stmt = getConnection()->createStatement();
        stmt->execute("Insert Into messages(senderID, sender, contents, sended) VALUES ('" + userid + "','" + username +
                      "', '" + contents +
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
        stmt->execute("UPDATE users set last_logout = NOW(), status='offline' WHERE id = '" + userid + "'");

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


