# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -mfpu=neon")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -S")
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3")
# Declares and names the project.

project("flowehealth")
add_library( # Sets the name of the library.
        flowehealth

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp

        flowehealgorithms/basic_charts.cpp

        flowehealgorithms/peak_detection.cpp

        flowehealgorithms/histogram.cpp

        flowehealgorithms/DSP/epg_integrator.cpp

        pffft/pffft.c)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        flowehealth
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})