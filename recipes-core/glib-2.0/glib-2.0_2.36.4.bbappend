DEPENDS += "libffi"

EXTRA_OECONF += "LIBFFI_CFLAGS=-I${PKG_CONFIG_SYSROOT_DIR}/usr/lib/libffi-3.0.13/include"