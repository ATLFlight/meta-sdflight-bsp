DESCRIPTION = "DumpKey"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"

SRC_URI = "file://system/core/"

inherit native

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/system/core/libmincrypt/tools', s)
}

do_compile() {
    install -d ${S}/build
    javac -d ${S}/build ${S}/DumpPublicKey.java
    cd ${S}/build
    jar -cvfm ${WORKDIR}/dumpkey.jar ${S}/DumpPublicKey.mf *
    cd -
}

do_install() {
    install -m 0644 ${WORKDIR}/dumpkey.jar ${STAGING_BINDIR_NATIVE}
}
