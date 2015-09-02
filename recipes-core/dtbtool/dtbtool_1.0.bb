inherit autotools gettext

DESCRIPTION = "dtbtool for generating master dtb image"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"
S = "${WORKDIR}/device/common"

SRC_URI = "file://device/qcom/common"

EXTRA_OEMAKE = "INCLUDES='-I${S}/include'"

BBCLASSEXTEND = "native"
