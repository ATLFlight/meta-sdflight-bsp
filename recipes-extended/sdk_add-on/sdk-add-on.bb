DESCRIPTION = "SDK Add-on Utilities"
LICENSE     = "CLOSED"

PR = "r1"
PV = "1.0"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI  = "file://qcom_flight_controller_hexagon_sdk_add_on.zip;subdir=sdk_add_on-${PV}"

S = "${WORKDIR}/sdk_add_on-${PV}"

INSANE_SKIP_${PN} += "installed-vs-shipped"

FILES_${PN}-staticdev = "/usr/lib/*.a"
FILES_${PN} = "/usr/lib/*.so"
FILES_${PN} += "/usr/tests/*"
FILES_${PN} += "/firmware/image/*"
FILES_${PN} += "/usr/include/sensor-imu/*"
FILES_${PN} += "/usr/bin/*"
FILES_${PN}-firmware = "/lib/firmware/*"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_PACKAGE_STRIP = "1"

PACKAGES = "${PN} ${PN}-firmware"
RPROVIDES_${PN}-firmware = "${PN}-firmware"

DEPENDS_${PN} = "adsprpc"
RDEPENDS_${PN} = "adsprpc sdk-add-on-adsp libgcc glibc"

do_install_append() {
    dest=/lib/firmware
    install -d ${D}${dest}
    install -m 0644 ${S}/images/firmware/*.* -D ${D}${dest}

    dest=/usr/lib
    install -d ${D}${dest}
    install -m 0644 ${S}/flight_controller/krait/libs/*.* -D ${D}${dest}

    dest=/usr/tests
    install -d ${D}${dest}
    install -m 0755 ${S}/flight_controller/hexagon/tests/* -D ${D}${dest}

    dest=/usr/include/sensor-imu
    install -d ${D}${dest}
    install -m 0755 ${S}/flight_controller/krait/inc/sensor_datatypes.h -D ${D}${dest}
    install -m 0755 ${S}/flight_controller/krait/inc/SensorImu.hpp -D ${D}${dest}
    install -m 0755 ${S}/flight_controller/krait/inc/sensor_imu_api.h -D ${D}${dest}

    dest=/usr/bin
    install -d ${D}${dest}
    install -m 0755 ${S}/flight_controller/krait/apps/* -D ${D}${dest}

    dest=/usr/share/sdk_add_on/${PV}
    install -d ${D}${dest}
    install ${WORKDIR}/${QTI_LICENSE} ${D}${dest}/${QTI_LICENSE}
}

INSANE_SKIP_${PN}-firmware += "arch"
INSANE_SKIP_${PN} += "textrel"
INSANE_SKIP_${PN} += "libdir"
INSANE_SKIP_${PN} += "ldflags"
INSANE_SKIP_${PN} += "dev-deps"
