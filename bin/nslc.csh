#!/bin/csh
# SCCS @(#)resume	1.2 ---01/14/99 --17:31:10
# Sun Workstations resume script
# 98/9/23

setenv NSLJ_ROOT /home/jbonaiuto/School/USC/BrainSimLab/Projects/NSL/project/nsl-java
setenv NSL_OS unix

if (! $?PATH) then
    setenv PATH
endif
setenv PATH ${JAVA_HOME}/bin:${PATH}

setenv CLASSPATH .:${NSLJ_ROOT}/nslc/classes:${NSLJ_ROOT}/nslj/classes:${NSLJ_ROOT}:${NSLJ_ROOT}/lib/ext:${NSLJ_ROOT}/lib/ext/epsGraphics.jar:${NSLJ_ROOT}/lib/ext/j3dcore.jar:${NSLJ_ROOT}/lib/ext/j3dutils.jar:${NSLJ_ROOT}/lib/ext/jacl.jar:${NSLJ_ROOT}/lib/ext/jmatio.jar:${NSLJ_ROOT}/lib/ext/jmatlink.jar:${NSLJ_ROOT}/lib/ext/odejava-jni.jar:${NSLJ_ROOT}/lib/ext/odejava.jar:${NSLJ_ROOT}/lib/ext/openmali.jar:${NSLJ_ROOT}/lib/ext/vecmath.jar:${CLASSPATH}

java nslc.src.NslCompiler ${*}