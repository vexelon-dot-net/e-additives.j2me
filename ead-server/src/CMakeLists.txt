hs_debug_message("Building src/ ..." )

set( 
    SOURCES 
    ead-server.cpp
    commandlineparser.cpp
    configuration.cpp
)

hs_debug_message("src/ Sources: ${SOURCES}")

add_executable (	
	ead-server
	${SOURCES}
)

target_link_libraries(ead-server ${LOG4_LIB_NAME} "pthread")

#add_subdirectory(exceptions)