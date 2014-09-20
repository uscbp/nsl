@echo off
set LIB_PATH=%cd%
set LIB_PATH=%LIB_PATH:\=/%
java nslc.src.NslCompiler -libraryDir %LIB_PATH% %1 %2 %3 %4 %5 %6 %7 %8 %9
@echo on
