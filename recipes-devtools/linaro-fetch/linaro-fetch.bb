inherit native

LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://../COPYING;md5=11c1d78c92548a586eafd0c08349534b"

SRC_URI += "https://launchpad.net/linaro-toolchain-binaries/trunk/2013.04/+download/gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux.tar.xz;md5sum=f4b054cb3f6a28465c102e79bf386530"
SRC_URI += "file://COPYING"

INHIBIT_DEFAULT_DEPS = "1"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_populate_license[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

do_unpack () {
    cd ${TOPDIR}/..
    test -e ${TOPDIR}/../gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux && rm -rf ${TOPDIR}/../gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux
    tar xf ${DL_DIR}/gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux.tar.xz
}
