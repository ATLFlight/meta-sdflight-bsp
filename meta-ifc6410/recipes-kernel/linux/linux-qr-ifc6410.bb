
KTAG_ifc6410 = "A8064AAAAANLYA7012"
SRCREV="3d4a060263030250b8a768e2ac6ad783cefbc6b1"

KBRANCH_ifc6410 = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc


SRC_URI = "git://codeaurora.org/kernel/msm.git;revision=${SRCREV};protocol=git;bareclone=1"

SRC_URI += "file://defconfig \
            file://ifc6410.scc \
            file://ifc6410-patches.scc \
            file://ifc6410.cfg \
            file://ifc6410-user-config.cfg \
           "

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.7%"

COMPATIBLE_MACHINE_ifc6410 = "ifc6410"
LINUX_VERSION_EXTENSION_ifc6410 = "-ifc6410"


