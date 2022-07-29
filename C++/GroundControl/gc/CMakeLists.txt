cmake_minimum_required(VERSION 3.10.2)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

project(gc)

add_executable(gc
    gc.cpp
)

find_package(MAVSDK REQUIRED)

target_link_libraries(gc
    MAVSDK::mavsdk
)

if(NOT MSVC)
    add_compile_options(gc PRIVATE -Wall -Wextra)
else()
    add_compile_options(gc PRIVATE -WX -W2)
endif()
