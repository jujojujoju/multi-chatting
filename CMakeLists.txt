cmake_minimum_required(VERSION 3.6)
project(multichatting)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES main.cpp App.cpp App.h Server.cpp Server.h ChatException.h)
add_executable(multichatting ${SOURCE_FILES})