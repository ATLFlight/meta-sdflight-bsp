DEPENDS += "libffi"

EXTRA_OECONF += "LIBFFI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/libffi-3.0.13/include"
# Have the recipe create an extra dpkg, libglib2.0-bin, for the similarly
# named Ubuntu package, and move the named binaries to that pkg
PACKAGES =+ "${PN}-bin"
PROVIDES += "${PN}-bin"
PACKAGES_DYNAMIC = "glib-2.0-.*"
FILES_${PN}-bin = "${bindir}/gdbus \
                   ${bindir}/gio-querymodules \
                   ${bindir}/glib-compile-resources \
                   ${bindir}/glib-compile-schemas \
                   ${bindir}/gresource \
                   ${bindir}/gsettings"

