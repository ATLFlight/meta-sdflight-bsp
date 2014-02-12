KTAG_liquid8064 = "AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241"
KBRANCH_liquid8064 = "linux-${MACHINE}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://defconfig"

SRC_URI += "file://liquid8064.scc \
            file://liquid8064.cfg \
            file://liquid8064-user-config.cfg \
            file://liquid8064-user-patches.scc \
           "
# tag: AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241
SRCREV="960880659d78027b3fc0274d3acf64b3c5b34bf8"

COMPATIBLE_MACHINE_liquid8064 = "liquid8064"
LINUX_VERSION_EXTENSION_liquid8064 = "-liquid8064"
