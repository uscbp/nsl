# This makefile builds JMatLink library on linux
# Jimmy Bonaiuto 3/3/07
# based on Linux make file by
# Chase Krumpelman 2/1/03
# based on Solaris make file by
# Christian Schaffner 12/12/00 

CC = gcc
LD = ld 

# MATLAB
MATLAB=/usr/local/matlabR2007a
MLIB=-L$(MATLAB)/extern/lib/glnxa64 
MINCLUDE=-I$(MATLAB)/extern/include

# JAVA
JAVA=/usr/lib/jvm/java-6-sun
JINCLUDE = -I$(JAVA)/include -I$(JAVA)/include/linux

libJMatlink.so  : JMatLink.o
	$(LD) -G $(CFLAGS) -fPIC $(MLIB) -zdefs -o libJMatLink.so JMatLink.o $(MATLAB)/bin/glnxa64/libeng.so /usr/lib/libc.so $(MATLAB)/bin/glnxa64/libmx.so
	rm -f *.o

JMatLink.o      : JMatLink.c JMatLink.h
	$(CC) -c -shared $(JINCLUDE) $(MINCLUDE) JMatLink.c

clean:
	rm -f *.class
