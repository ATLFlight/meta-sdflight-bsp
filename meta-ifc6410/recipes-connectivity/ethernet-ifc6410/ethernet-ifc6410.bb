DESCRIPTION = "Ethernet configuration"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

PR = "r0"
PV = "1.0"

SRC_URI = "file://wired-connection-1"

PACKAGES = "${PN}"

do_install() {
    if [ LK_ROOT_DEV != "/dev/mmcblk0p13" -o -n "${ETH0_MAC_ADDR}" ]; then
        install -d ${D}${sysconfdir}/NetworkManager/system-connections
        install -m 600 ${WORKDIR}/wired-connection-1 ${D}${sysconfdir}/NetworkManager/system-connections
        UUID=`uuidgen`
        sed -i -e "s/@REPLACE_WITH_REAL_UUID@/${UUID}/" ${D}${sysconfdir}/NetworkManager/system-connections/wired-connection-1
        if [ ${LK_ROOT_DEV} == "/dev/mmcblk0p13" ] ; then
            sed -i -e "s/@REPLACE_WITH_REAL_MAC_ADDR@/${ETH0_MAC_ADDR}/" ${D}${sysconfdir}/NetworkManager/system-connections/wired-connection-1
        fi
    fi    
}

pkg_postinst_${PN}() {
  if [ ${LK_ROOT_DEV} == "/dev/mmcblk0p13" ] ; then
    exit 0
  fi
  # Now get the ethernet MAC address from the Android
  # userdata partition, and set that in our ethernet
  # config scripts
  mkdir -p /userdata || {
    echo "[ERROR] Error creating mount point"
    exit 1
  }
  mount /dev/mmcblk0p13 /userdata/ || {
    echo "[ERROR] Error mounting the userdata partition"
    exit 1
  }

  MAC=`cat /userdata/eth0.sh  | grep hwaddr | sed 's/.*hwaddr //g'`
  umount /userdata
  sed -i -e "s/@REPLACE_WITH_REAL_MAC_ADDR@/${MAC}/" /etc/NetworkManager/system-connections/wired-connection-1

}
