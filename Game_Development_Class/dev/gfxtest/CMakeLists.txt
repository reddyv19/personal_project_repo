# Original Author(s) of this File: 
#   Daniel Keefe, 2017, University of Minnesota
#  
# Author(s) of Significant Updates/Modifications to the File:
#   ... 



# You are encouraged to copy this example, move it outside of the MinGfx directory, and use
# it as a starting point for your project.  When you do this, you'll have to edit the
# following line as needed to point to the MinGfx install prefix used on your system.

# !!!!!!!!!!!!! EDIT THE FOLLOWING LINE AS NEEDED !!!!!!!!!!!!! 
list(APPEND CMAKE_PREFIX_PATH ${CMAKE_CURRENT_SOURCE_DIR}/../build/install ../..)





#### BASIC PROJECT SETUP ####

project(gfxtest)

# Using 3.9 to get a modern version of FindOpenGL.cmake
cmake_minimum_required (VERSION 3.9) 

# Dependencies that are auto-downloaded, built, and installed for you will go in the 
# directory pointed to by the CMAKE_INSTALL_PREFIX.  It defaults to a location inside
# the build directory.
if (CMAKE_INSTALL_PREFIX_INITIALIZED_TO_DEFAULT OR "${CMAKE_INSTALL_PREFIX}" STREQUAL "")
    set (CMAKE_INSTALL_PREFIX "${CMAKE_BINARY_DIR}/install" CACHE PATH "default install path" FORCE )
endif()

# Add to paths cmake uses to search for scripts, modules, and config packages
list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake ${CMAKE_INSTALL_PREFIX})
list(INSERT CMAKE_PREFIX_PATH 0 ${CMAKE_INSTALL_PREFIX})

include(MessageMacros)
h1("Building ${PROJECT_NAME}")
h2("Configuring paths")

message(STATUS "Module path: ${CMAKE_MODULE_PATH}")
message(STATUS "Prefix path: ${CMAKE_PREFIX_PATH}")
message(STATUS "Install prefix: ${CMAKE_INSTALL_PREFIX}")

set(DATA_DIR_BUILD ${CMAKE_CURRENT_SOURCE_DIR}/data)
set(DATA_DIR_INSTALL ${CMAKE_INSTALL_PREFIX}/share/${PROJECT_NAME}/data)

message(STATUS "Data dir (in build tree): ${DATA_DIR_BUILD}")
message(STATUS "Data dir (in install tree): ${DATA_DIR_INSTALL}")

set(SHADERS_DIR_BUILD ${CMAKE_CURRENT_SOURCE_DIR}/shaders)
set(SHADERS_DIR_INSTALL ${CMAKE_INSTALL_PREFIX}/share/${PROJECT_NAME}/shaders)

message(STATUS "Shaders dir (in build tree): ${SHADERS_DIR_BUILD}")
message(STATUS "Shaders dir (in install tree): ${SHADERS_DIR_INSTALL}")


#### SOURCE FOR THIS PROJECT ####
h2("Configuring source files")

set(SOURCEFILES
    gfx_test.cc
    main.cc
)

set(HEADERFILES
    gfx_test.h
)

set(EXTRAFILES
    README.md
)

set(SHADERFILES
)

set_source_files_properties(${EXTRAFILES} PROPERTIES HEADER_FILE_ONLY TRUE)
set_source_files_properties(${SHADERFILES} PROPERTIES HEADER_FILE_ONLY TRUE)

source_group("Shaders" FILES ${SHADERFILES})


#### COMPILE OPTIONS ####

h2("Configuring Compiler Options")




message(STATUS "Building for " ${CMAKE_SYSTEM_NAME} ".")

# Linux specific
if (${CMAKE_SYSTEM_NAME} MATCHES "Linux")
  add_definitions(-DLINUX)
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fPIC")
  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++14")
endif()


# Apple specific
if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
  add_definitions(-DOSX)

  # RPATH settings, see https://cmake.org/Wiki/CMake_RPATH_handling
  set(CMAKE_MACOSX_RPATH ON)

  # use, i.e. don't skip the full RPATH for the build tree
  SET(CMAKE_SKIP_BUILD_RPATH  FALSE)

  # when building, don't use the install RPATH already
  # (but later on when installing)
  SET(CMAKE_BUILD_WITH_INSTALL_RPATH FALSE) 

  SET(CMAKE_INSTALL_RPATH "${CMAKE_INSTALL_PREFIX}/lib")

  # add the automatically determined parts of the RPATH
  # which point to directories outside the build tree to the install RPATH
  SET(CMAKE_INSTALL_RPATH_USE_LINK_PATH TRUE)

  # the RPATH to be used when installing, but only if it's not a system directory
  LIST(FIND CMAKE_PLATFORM_IMPLICIT_LINK_DIRECTORIES "${CMAKE_INSTALL_PREFIX}/lib" isSystemDir)
  IF("${isSystemDir}" STREQUAL "-1")
     SET(CMAKE_INSTALL_RPATH "${CMAKE_INSTALL_PREFIX}/lib")
  ENDIF("${isSystemDir}" STREQUAL "-1")

  set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++14")
endif()


# Windows specific
if (WIN32)
  add_definitions(-DWIN32)
endif()




#### DEFINE TARGET(S) ####

h2("Defining Target(s)")

add_executable(${PROJECT_NAME} ${SOURCEFILES} ${HEADERFILES} ${EXTRAFILES} ${SHADERFILES})



#### FIND AND ADD DEPENDENCIES ####

h2("Adding Dependencies")
set(EXTERNAL_DIR external)


# MinGfx (linked with an imported cmake target so no need to specify include dirs)
# This will try to find MinGfxConfig.cmake, which should have been installed under
# CMAKE_INSTALL_PREFIX/lib/cmake/MinGfx when you installed the MinGfx Toolkit.
find_package(MinGfx REQUIRED)
target_link_libraries(${PROJECT_NAME} PUBLIC MinGfx::MinGfx)


# Add dependency on OpenGL
include(UseOpenGL)
UseOpenGL(${PROJECT_NAME} PUBLIC ${EXTERNAL_DIR})



#### INSTALL TARGET(S) ####

h2("Configuring Install Target")

# The install locations are relative to the CMAKE_INSTALL_PREFIX variable
install(TARGETS ${PROJECT_NAME} RUNTIME DESTINATION bin)

install(
    DIRECTORY data/
    DESTINATION ${DATA_DIR_INSTALL}
    OPTIONAL
)

install(
    DIRECTORY shaders/
    DESTINATION ${SHADERS_DIR_INSTALL}
    OPTIONAL
)
