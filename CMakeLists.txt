cmake_minimum_required(VERSION 3.6)
project(multichatting)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -ljsoncpp")
set(SOURCE_FILES main.cpp App.cpp App.h Server.cpp Server.h ChatException.h User.cpp User.h CThread.cpp CThread.h UserManager.cpp UserManager.h)
add_executable(multichatting ${SOURCE_FILES})