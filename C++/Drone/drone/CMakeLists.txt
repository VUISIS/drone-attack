cmake_minimum_required(VERSION 3.10.2)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

project(drone)

add_executable(drone
    drone.cpp
)

find_package(MAVSDK REQUIRED)

target_link_libraries(drone
    MAVSDK::mavsdk
)

if(NOT MSVC)
    add_compile_options(drone PRIVATE -Wall -Wextra)
else()
    add_compile_options(drone PRIVATE -WX -W2)
endif()
