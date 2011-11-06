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


#ifndef __TYPEDEFS_HPP__
#define __TYPEDEFS_HPP__

#include <stdint.h>

typedef uint8_t		uint8;
typedef uint32_t	uint32;
typedef uint64_t	uint64_t;

typedef int8_t		int8;
typedef int32_t		int32;
typedef int64_t		int64;

#include <iostream>
#include <string>
typedef std::string		tstring;
typedef std::stringstream	tstringstream;

#define FREE_PTR(x) {if (x!=NULL) {delete x; x = NULL;}}

#endif