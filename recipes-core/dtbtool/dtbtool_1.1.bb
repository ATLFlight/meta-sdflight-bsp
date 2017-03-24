inherit autotools gettext

DESCRIPTION = "dtbtool for generating master dtb image"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${S}/dtbtool.c;endline=28;md5=533dd85785b269cdb0c149db273d170a"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/device/qcom/common/dtbtool"

SRC_URI = "file://device/qcom/common/dtbtool"

EXTRA_OEMAKE = "INCLUDES='-I${S}/include'"

BBCLASSEXTEND = "native"
