LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=ab87f20cd7e8c0d0a6539b34d3791d0e"
SUMMARY = "User space daemon for extended IEEE 802.11 management"

DEPENDS = "libnl (= 3.2.21) openssl"

PROVIDES = "${PN}"

SRC_URI = "git://codeaurora.org/platform/external/hostap;nobranch=1;tag=hostap_2_1 \
           file://defconfig.patch;patch=1 \
"
# http://ports.ubuntu.com/ubuntu-ports/pool/universe/w/wpa/hostapd_2.1-0ubuntu1_armhf.deb

SRC_URI += "\
http://bazaar.launchpad.net/~ubuntu-branches/ubuntu/trusty/wpa/trusty/download/head:/hostapd.sh-20120512164208-bwtj2zmf8oi7hmeh-824/hostapd.sh\
;downloadfilename=hostapd.ifupdown\
;unpack=false\
;md5sum=5381c4fc91178191dd0d09b59323c652\
;sha256sum=94af51e653269381bf1b83c30bfade926c44c0db50e6f1af77d1be9aeffe8fdd \
http://bazaar.launchpad.net/~ubuntu-branches/ubuntu/trusty/wpa/trusty/download/head:/hostapd.default-20120512164208-bwtj2zmf8oi7hmeh-793/hostapd.default\
;md5sum=b326712dd39d93391a785abba6f2a9b4;\
;sha256sum=4c3fb1a9a23dbab1357d2c6f4980aa8fdd64739442af49cb2158cc45d53dda87 \
http://bazaar.launchpad.net/~ubuntu-branches/ubuntu/trusty/wpa/trusty/download/head:/hostapd.init-20120512164208-bwtj2zmf8oi7hmeh-819/hostapd.init\
;md5sum=a7931d02817e1ae9beb1b2cd0398dcb8\
;sha256sum=e7d37e0dd07e40d7d7b2b38e2a4559e446fb7742e5e6cffd400a1d8e0d1c9615\
"

inherit update-rc.d

INITSCRIPT_NAME = "hostapd"

# LINARO Ubuntu 14.04 Requirement
# We need to override the definitions of the following updatercd_ functions
# because the implementations inside of oe update-rc.d bbclass aren't compatible
# with the sysv-rc scripts that come with the linaro userdata image, and if
# we try to simply install update-rc.d pacakge over the top of the sysv-rc package
# dpkg -i fails when it tries to overwite /usr/sbin/update-rc.d
updatercd_preinst() {
    if [ -z "$D" -a -f "${INIT_D_DIR}/${INITSCRIPT_NAME}" ]; then
        ${INIT_D_DIR}/${INITSCRIPT_NAME} stop > /dev/null 2>&1
    fi
}

updatercd_postinst() {
}

updatercd_prerm() {
    if [ -z "$D" ]; then
        ${INIT_D_DIR}/${INITSCRIPT_NAME} stop > /dev/null 2>&1
    fi
}

updatercd_postrm() {
}

DEFAULT_PREFERENCE = "-1"

# LINARO Ubuntu 14.04 Requirement
# Need to prepend a "1:" to the version otherwise dpkg -i complains
# that the resident version of initscripts breaks hostapd
PKGV="1:${PV}"

do_configure_prepend() {
    install -m 0644 ${WORKDIR}/hostapd-${PV}/hostapd/defconfig ${WORKDIR}/hostapd-${PV}/hostapd/.config
}

do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)
}

do_compile() {
    export CFLAGS="-MMD -O2 -Wall -g -I${STAGING_INCDIR}/libnl3"
    make -C ${WORKDIR}/hostapd-${PV}/hostapd
}

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir}/init.d ${D}/${sysconfdir}/default ${D}/${sysconfdir}/hostapd
    install -d ${D}/${sysconfdir}/network/if-pre-up.d ${D}/${sysconfdir}/network/if-post-down.d
    install -m 755 ${WORKDIR}/hostapd-${PV}/hostapd/hostapd                      ${D}${sbindir}
    install -m 755 ${WORKDIR}/hostapd-${PV}/hostapd/hostapd_cli                  ${D}${sbindir}
    install -m 644 ${WORKDIR}/hostapd-${PV}/hostapd/hostapd.conf                 ${D}${sysconfdir}/hostapd.conf.template
    install -m 644 ${DL_DIR}/hostapd.default                                     ${D}${sysconfdir}/default/hostapd
    install -m 755 ${DL_DIR}/hostapd.ifupdown                                    ${D}${sysconfdir}/hostapd/ifupdown.sh
    install -m 755 ${DL_DIR}/hostapd.init                                        ${D}${sysconfdir}/init.d/hostapd
}

pkg_postinst_${PN}() {
    ln -s ${sysconfdir}/hostapd/ifupdown.sh ${sysconfdir}/network/if-pre-up.d/hostapd
    ln -s ${sysconfdir}/hostapd/ifupdown.sh ${sysconfdir}/network/if-post-down.d/hostapd
}

pkg_postrm_${PN}() {
    rm -f ${sysconfdir}/network/if-pre-up.d/hostapd
    rm -f ${sysconfdir}/network/if-post-down.d/hostapd
}

CONFFILES_${PN} += "${sysconfdir}/hostapd.conf"
