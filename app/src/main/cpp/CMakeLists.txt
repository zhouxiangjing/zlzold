
cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

include_directories(${CMAKE_SOURCE_DIR}/include)

add_library(libhpsocket SHARED IMPORTED)
set_target_properties(libhpsocket PROPERTIES IMPORTED_LOCATION ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}/libhpsocket.so)

add_library(myhpsocket
            SHARED
            hpsocket.cpp)

target_link_libraries(myhpsocket
                      android
                      log
                      libhpsocket)