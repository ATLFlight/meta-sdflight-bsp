KTAG_som8064 = "AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241"
KBRANCH_som8064 = "linux-${MACHINE}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://defconfig"

SRC_URI += "file://som8064.scc \
            file://som8064.cfg \
            file://som8064-user-config.cfg \
            file://som8064-user-patches.scc \
           "
# tag: AU_LINUX_ANDROID_JB_2.5.04.02.02.40.241
SRCREV="960880659d78027b3fc0274d3acf64b3c5b34bf8"

COMPATIBLE_MACHINE_som8064 = "som8064"
LINUX_VERSION_EXTENSION_som8064 = "-som8064"
