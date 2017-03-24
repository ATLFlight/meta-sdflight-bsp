SRC_URI += "file://interfaces"

do_install_append() {
   install ${S}/interfaces ${D}/${sysconfdir}/network/interfaces
}
