export NSLJ_ROOT=/home/jbonaiuto/projects/nsl/nsl-java
export NSL_OS=unix

export PATH=${JAVA_HOME}/bin:${PATH}

export LD_LIBRARY_PATH=${JAVA_HOME}/lib:${NSLJ_ROOT}/lib/amd64:${LD_LIBRARY_PATH}

export CLASSPATH=.:${NSLJ_ROOT}/nslj/classes:${NSLJ_ROOT}:${NSLJ_ROOT}/lib/ext:${NSLJ_ROOT}/lib/ext/epsGraphics.jar:${NSLJ_ROOT}/lib/ext/j3dcore.jar:${NSLJ_ROOT}/lib/ext/j3dutils.jar:${NSLJ_ROOT}/lib/ext/jacl.jar:${NSLJ_ROOT}/lib/ext/jmatio.jar:${NSLJ_ROOT}/lib/ext/jmatlink.jar:${NSLJ_ROOT}/lib/ext/odejava-jni.jar:${NSLJ_ROOT}/lib/ext/odejava.jar:${NSLJ_ROOT}/lib/ext/openmali.jar:${NSLJ_ROOT}/lib/ext/vecmath.jar:${CLASSPATH}

java nslj.src.main.NslMain ${*}
