### E-Additives Server
### CMAKE file
###
#################################################
set(HS_DEBUG ON)

macro(HS_DEBUG_MESSAGE _message)
  if (HS_DEBUG)
    message(STATUS "(DEBUG) ${_message}")
  endif (HS_DEBUG)
endmacro(HS_DEBUG_MESSAGE _message)

cmake_minimum_required (VERSION 2.8)
project(ead-server) 

###
hs_debug_message("Build type is ${CMAKE_BUILD_TYPE}" )

### src
include_directories(src/)
include_directories(build/) # Config.h

# Setup configuration

set (VERSION_MAJ 1)
set (VERSION_MIN 0)

### requirements
# PThread
#find_package(pthread REQUIRED)

# Log4Cxx
set( LOG4_LIB_NAME "liblog4cxx.so" )
find_library( 
    LOG4_LIBRARY
    NAMES ${LOG4_LIB_NAME}
    PATHS "/usr/lib/" "/usr/local/lib/"
)
if ( NOT LOG4_LIBRARY )
      message( FATAL_ERROR "Log4Cpp lib (${LOG4_LIB_NAME}) not found!" )
endif ( NOT LOG4_LIBRARY )

## flags
add_definitions(-DLINUX_BUILD)

if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    add_definitions(-g)
    add_definitions(-Wall)
    add_definitions(-D_DEBUG)
    add_definitions(-DDEBUG)
else (CMAKE_BUILD_TYPE STREQUAL "Debug")
    add_definitions(-O3 -DNDEBUG)
endif (CMAKE_BUILD_TYPE STREQUAL "Debug")

if (UNIX AND NOT WIN32)
    if (CMAKE_SIZEOF_VOID_P MATCHES "8")
    	set(CPU_ARCH 64)
		add_definitions(-DARCH_X64)
    else (CMAKE_SIZEOF_VOID_P MATCHES "8")
		add_definitions(-DARCH_X86)
		set(CPU_ARCH 64)
    endif (CMAKE_SIZEOF_VOID_P MATCHES "8")
endif (UNIX AND NOT WIN32)


### configure a header file to pass some of the CMake settings to the source code
configure_file (
  "${PROJECT_SOURCE_DIR}/Config.h.in"
  "${PROJECT_BINARY_DIR}/Config.h"
  )

### build subdirs
add_subdirectory(src)

### set runtime directory
#set(savagewheels PROPERTIES RUNTIME_OUTPUT_DIRECTORY "./")
