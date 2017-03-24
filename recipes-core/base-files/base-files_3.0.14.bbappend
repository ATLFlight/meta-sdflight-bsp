# This bitbake recipe is used to override the default fstab file
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# skip volatile links to /var/log and /var/tmp folders
volatiles = ""

do_install_append() {
    mkdir -p ${D}/lib/firmware
    mkdir -p ${D}/mnt/persist
}
