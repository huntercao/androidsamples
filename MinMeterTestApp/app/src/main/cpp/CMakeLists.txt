cmake_minimum_required(VERSION 3.4.1)

add_library(SerialPort SHARED
            SerialPort.c)

# Include libraries needed for SerialPort lib
target_link_libraries(SerialPort
                      android
                      log)
