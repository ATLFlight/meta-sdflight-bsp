DESCRIPTION = "SignApk"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=89aea4e17d99a7cacdbeed46a0096b10"

PR = "r1"

FILESPATH =+ "${WORKSPACE}:"

SRC_URI = "file://build/ \
    http://polydistortion.net/bc/download/bcprov-jdk15on-150.jar;name=bcprov \
    http://polydistortion.net/bc/download/bcpkix-jdk15on-150.jar;name=bcpkix"
SRC_URI[bcprov.md5sum] = "7d922548538cffd44a862fe6ce9574b1"
SRC_URI[bcprov.sha256sum] = "115b14a02b91fb03cb1866d6b311d33cd5518a9d8524dd63a14f19571e420404"
SRC_URI[bcpkix.md5sum] = "4947238c5ab9942f299cc15ffb368da9"
SRC_URI[bcpkix.sha256sum] = "037dca15295313174f3f88cc9631db4896d8726a3be3e0cd65e6c2534ad22ed3"

inherit native

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/build/tools/signapk', s)
    shutil.move(wd+'/org', s+'/build/org')
}

do_compile() {
    javac -d ${S}/build -cp ${S}/build ${S}/SignApk.java
    cd ${S}/build
    jar -cvfm ${WORKDIR}/signapk.jar ${S}/SignApk.mf *
    cd -
}

do_install() {
    install -m 0644 ${WORKDIR}/signapk.jar ${STAGING_BINDIR_NATIVE}
}
