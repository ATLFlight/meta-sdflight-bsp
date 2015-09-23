DESCRIPTION = "QRL Miscellaneous scripts"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/${LICENSE};md5=550794465ba0ec5312d6919e203a55f9"

PR = "r0"
PV = "1.0"

SRC_URI = "file://board-name.sh"

PACKAGES = "${PN}"
FILES_${PN} = "/usr/local/qr-linux/*"

do_install() {
    dest=/usr/local/qr-linux
    install -d ${D}${dest}
    install -m 755 ${WORKDIR}/board-name.sh ${D}${dest}
}

pkg_postinst_${PN}() {
    # Disable v4l2 udev rules, causing issues with venus,
    # q6 and cameras
    rm /lib/udev/rules.d/60-persistent-v4l.rules
    ln -s /dev/null /lib/udev/rules.d/60-persistent-v4l.rules
    # Set upstart jobs related to graphics and audio to manual
    echo manual > /etc/init/plymouth.override
    echo manual > /etc/init/plymouth-log.override
    echo manual > /etc/init/plymouth-ready.override
    echo manual > /etc/init/plymouth-shutdown.override
    echo manual > /etc/init/plymouth-splash.override
    echo manual > /etc/init/plymouth-stop.override
    echo manual > /etc/init/plymouth-upstart-bridge.override
    echo manual > /etc/init/udev-fallback-graphics.override
    echo manual > /etc/init/alsa-restore.override
    echo manual > /etc/init/alsa-state.override
    echo manual > /etc/init/alsa-store.override
    echo manual > /etc/init/failsafe.override
    echo manual > /etc/init/openvt.override
}
