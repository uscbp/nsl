@echo off
set NSLJ_ROOT=C:\Documents\USCBP\projects\nsl\nsl-java
set NSL_OS=windows

echo Updating path and classpath

set PATH=%JAVA_HOME%\bin;%NSLJ_ROOT%\nslc\bin;%PATH%
set CLASSPATH=.;%NSLJ_ROOT%\nslc\classes;%NSLJ_ROOT%\nslj\classes;%NSLJ_ROOT%;%NSLJ_ROOT%\lib\ext;%NSLJ_ROOT%\lib\ext\epsGraphics.jar;%NSLJ_ROOT%\lib\ext\j3dcore.jar;%NSLJ_ROOT%\lib\ext\j3dutils.jar;%NSLJ_ROOT%\lib\ext\jacl.jar;%NSLJ_ROOT%\lib\ext\jmatio.jar;%NSLJ_ROOT%\lib\ext\jmatlink.jar;%NSLJ_ROOT%\lib\ext\odejava-jni.jar;%NSLJ_ROOT%\lib\ext\odejava.jar;%NSLJ_ROOT%\lib\ext\openmali.jar;%NSLJ_ROOT%\lib\ext\vecmath.jar;%CLASSPATH%
doskey /insert
@echo on
%NSLJ_ROOT%\nslc\bin\nslc %*
