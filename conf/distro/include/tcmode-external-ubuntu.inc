#
# Configuration to use the standard ubuntu crossbuild toolchain.
#

EXTERNAL_TOOLCHAIN ?= "/usr"
TARGET_SYS_arm ?= "arm-linux-gnueabihf"

EXTERNAL_TOOLCHAIN ?= "/usr/local/csl/${TARGET_ARCH}"

TOOLCHAIN_PATH_ADD = "${EXTERNAL_TOOLCHAIN}/bin:"
PATH =. "${TOOLCHAIN_PATH_ADD}"

TARGET_PREFIX = "${TARGET_SYS}-"

ASSUME_PROVIDED = "\
	linux-libc-headers \
	linux-libc-headers-dev \
	virtual/linux-libc-headers \
	virtual/linux-libc-headers-dev \
	virtual/${TARGET_PREFIX}gcc \
	virtual/${TARGET_PREFIX}g++ \
	virtual/${TARGET_PREFIX}gcc-initial \
	virtual/${TARGET_PREFIX}gcc-intermediate \
	virtual/${TARGET_PREFIX}binutils \
	virtual/${TARGET_PREFIX}libc-for-gcc \
	virtual/${TARGET_PREFIX}libc-initial \
	virtual/${TARGET_PREFIX}compilerlibs \
	virtual/libc \
	virtual/libintl \
	virtual/libiconv \
	glibc-thread-db \
	libgcc \
	eglibc \
	virtual/linux-libc-headers \
	gdbserver \
"

# No need to re-compile the locale files
GLIBC_INTERNAL_USE_BINARY_LOCALE = "precompiled"
ENABLE_BINARY_LOCALE_GENERATION = ""
    
