inherit autotools

DESCRIPTION = "ext4-utils library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

DEPENDS += "libsparse zlib libselinux"

PR = "r1"

BBCLASSEXTEND = "native"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/system/extras/ext4_utils"

SRC_URI = "file://system/extras/"
CPPFLAGS += "-I${STAGING_INCDIR}/libsparse"
