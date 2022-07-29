cmake_minimum_required(VERSION 3.10.2)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

project(intercept_message)

add_executable(intercept_message
    intercept_message.cpp
)

find_package(MAVSDK REQUIRED)

target_link_libraries(intercept_message
    MAVSDK::mavsdk
)

if(NOT MSVC)
    add_compile_options(intercept_message PRIVATE -Wall -Wextra)
else()
    add_compile_options(intercept_message PRIVATE -WX -W2)
endif()
