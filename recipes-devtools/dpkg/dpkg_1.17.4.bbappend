FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI += "file://dpkg-deb-tar-build.patch \
            file://dpkg-deb-tar-reproducible.patch"

do_install_append() {
    install -m 0755 ${D}/${bindir}/dpkg-deb ${STAGING_BINDIR}
}
