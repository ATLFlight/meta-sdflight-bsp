DESCRIPTION = "Compile the compat-wireless module"

inherit module

PR = "r0"

LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRCREV = "88457d6774646e68a772cc8d180897f3937b36c2"
SRC_URI = "git://codeaurora.org/platform/external/compat-wireless.git;revision=${SRCREV};protocol=git"

SRC_URI += " \
   file://0000-Kbuild.patch \
   file://0001-compiler-warning.patch \
   file://prima-init.sh \
   file://prima.cfg \
"

PROVIDES += "virtual/wlan-module"

PACKAGES = "kernel-module-cfg80211 kernel-module-wlan ${PN}"

FILES_kernel-modules-cfg80211 = " \
            ${base_libdir}/modules/3.4.0-caf-standard/extra/cfg80211.ko \
	    "			  

FILES_kernel-module-wlan = " \
            ${base_libdir}/modules/3.4.0-caf-standard/extra/wlan.ko \
	    "
FILES_${PN} = "	\
	    ${sysconfdir}/network/* \
	    ${sysconfdir}/network/interfaces.d/* \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.alias \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.alias.bin \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.builtin.bin \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.dep \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.dep.bin \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.devname \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.softdep \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.symbols \
	    ${base_libdir}/modules/3.4.0-caf-standard/modules.symbols.bin \
	    "

do_unpack_append() {
    import os
    import shutil
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR', True)
    if os.path.exists(s):
        shutil.rmtree(s)
    os.rename(wd+'/git', s)
    
}

do_setup_dirs() {
  cd ${S}
  mkdir src
  ln -s ../drivers/ src/drivers
  ln -s ../net/ src/net
}

module_do_compile() {
   unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
   CROSS_COMPILE=${CROSS_COMPILE} make ARCH=arm KLIB_BUILD=${STAGING_KERNEL_DIR}/source/../linux-${MACHINE}-standard-build O=${WORKDIR} HAVE_CFG80211=1 BUILD_ATH6KL_VER_35=1
}

module_do_install() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	CROSS_COMPILE=${CROSS_COMPILE} make V=1 -C ${STAGING_KERNEL_DIR}/source/../linux-${MACHINE}-standard-build ARCH=arm M=${S} O=${WORKDIR} INSTALL_MOD_PATH=${D} -j4 modules_install 

	# Install prima-init.sh and prima.cfg
	mkdir -p ${D}/etc/network
	install ${WORKDIR}/prima-init.sh ${D}/etc/network/prima-init.sh
	mkdir -p ${D}/etc/network/interfaces.d
	install -m 644 ${WORKDIR}/prima.cfg ${D}/etc/network/interfaces.d/prima.cfg
}

addtask do_setup_dirs after do_unpack before do_patch
