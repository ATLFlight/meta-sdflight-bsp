#!/bin/sh
wget https://launchpad.net/linaro-toolchain-binaries/trunk/2013.04/+download/gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux.tar.xz
test -e gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux && rm -rf /gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux
tar xf gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux.tar.xz
rm gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux.tar.xz
