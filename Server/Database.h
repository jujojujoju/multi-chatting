#ifndef MULTICHATTING_DATABASE_H
#define MULTICHATTING_DATABASE_H

#include "mysql_connection.h"
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/statement.h>
#include <cppconn/prepared_statement.h>

class Database{
private:
    sql::Driver *driver;
    sql::Connection *con;
    sql::Statement *stmt;
    sql::ResultSet *res;
    sql::PreparedStatement *pstmt;

public:
    sql::Driver * getDriver();
    sql::Connection * getConnection();
    std::string login(const std::string &string, const std::string &basic_string);
    Json::Value getMessages(const std::string &string);

    bool idCheck(std::string userid);

    bool createUser(Json::Value user);

    bool storeMessage(Json::Value value);

    void printDBError(const sql::SQLException &e) const;

    bool logout(const std::string &basic_string);

    Json::Value getUserList(std::string string);

    Json::Value getPersonalMessages(std::string userid, std::string targetid);

    bool storePersonalMessage(Json::Value value);
};

#endif //MULTICHATTING_DATABASE_H
