/*
    Copyright (c) 2011 Petar Petrov <petar.petrov.georgiev(at)gmail(dot)com>

    Permission is hereby granted, free of charge, to any person
    obtaining a copy of this software and associated documentation
    files (the "Software"), to deal in the Software without
    restriction, including without limitation the rights to use,
    copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the
    Software is furnished to do so, subject to the following
    conditions:

    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
    OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
    HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
    WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.
*/

#include "ead-server.h"
#include "configuration.h"

class testclass {
    static LoggerPtr logger;

public:
    void dot() {
	LOG4CXX_INFO(this->logger, "Doing it ...");
    };
};


LoggerPtr testclass::logger(Logger::getLogger("asad"));
LoggerPtr logger(Logger::getLogger("MyApp"));

int main(int argc, char* argv[]) {

    PropertyConfigurator::configure("log4cpp.properties.conf");

    LOG4CXX_INFO(logger, "Entering application.");
    Configuration::getInstance();
    Configuration::getInstance().dispose();

    testclass t;
    t.dot();

    printf("hello world");
    return 0;
}
