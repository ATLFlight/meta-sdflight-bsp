inherit autotools gettext

DESCRIPTION = "libsparse library"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

DEPENDS += "zlib"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/system/core/${BPN}/"

SRC_URI = "file://system/core/${BPN}/"

BBCLASSEXTEND = "native"
