#!/bin/csh

setenv NSLJ_ROOT /home/jbonaiuto/School/USC/BrainSimLab/Projects/NSL/project/nsl-java
setenv NSL_OS unix

if (! $?PATH) then
    setenv PATH
endif
setenv PATH ${JAVA_HOME}/bin:${PATH}

if (! $?LD_LIBRARY_PATH) then
        setenv LD_LIBRARY_PATH
endif
setenv LD_LIBRARY_PATH ${JAVA_HOME}/lib:${NSLJ_ROOT}/lib/i586:${LD_LIBRARY_PATH}

setenv CLASSPATH .:${NSLJ_ROOT}/nslj/classes:${NSLJ_ROOT}:${NSLJ_ROOT}/lib/ext:${NSLJ_ROOT}/lib/ext/epsGraphics.jar:${NSLJ_ROOT}/lib/ext/j3dcore.jar:${NSLJ_ROOT}/lib/ext/j3dutils.jar:${NSLJ_ROOT}/lib/ext/jacl.jar:${NSLJ_ROOT}/lib/ext/jmatio.jar:${NSLJ_ROOT}/lib/ext/jmatlink.jar:${NSLJ_ROOT}/lib/ext/odejava-jni.jar:${NSLJ_ROOT}/lib/ext/odejava.jar:${NSLJ_ROOT}/lib/ext/openmali.jar:${NSLJ_ROOT}/lib/ext/vecmath.jar:${CLASSPATH}

java nslj.src.main.NslMain ${*}