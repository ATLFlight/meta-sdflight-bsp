
KTAG_ifc6410 = "A8064AAAAANLYA1335"
SRCREV="b664511e86ed8a9d486c098fe7fd5eb0a72481f5"

KBRANCH_ifc6410 = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc


SRC_URI = "git://codeaurora.org/kernel/msm.git;revision=${SRCREV};protocol=git;bareclone=1"

SRC_URI += "file://defconfig \
            file://ifc6410.scc \
            file://ifc6410.cfg \
            file://ifc6410-user-config.cfg \
            file://ifc6410-user-patches.scc \
           "

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.7%"

COMPATIBLE_MACHINE_if6410 = "ifc6410"
LINUX_VERSION_EXTENSION_if6410 = "-ifc6410"


