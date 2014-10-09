
#KTAG_ifc6410 = "A8064AAAAANLYA7012"
#SRCREV="3d4a060263030250b8a768e2ac6ad783cefbc6b1"
#KTAG_ifc6410 = "ubuntu-ifc6410-14.06"
#SRCREV="5adedb7593944d867b2de3393fb14ca4c16b1338"

KTAG_ifc6410="ubuntu-ifc6410-14.09"
SRCREV="d392f534aee60425fb23868c1164bfbe99e6d52b"
 
KBRANCH_ifc6410 = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc


#SRC_URI = "git://codeaurora.org/kernel/msm.git;nobranch=1;revision=${SRCREV};protocol=git;bareclone=1"
SRC_URI = "git://git.linaro.org/landing-teams/working/qualcomm/kernel.git;revision=${SRCREV};protocol=git;bareclone=1;nobranch=1"

#SRC_URI += "file://defconfig 
SRC_URI += "file://ifc6410.scc \
            file://ifc6410-patches.scc \
            file://ifc6410.cfg \
            file://ifc6410-user-config.cfg \
           "
SRC_URI += "https://releases.linaro.org/14.09/ubuntu/ifc6410/initrd.img-3.4.0-linaro-ifc6410;downloadfilename=initrd.img;name=initrd \
            https://releases.linaro.org/14.09/ubuntu/ifc6410/kernel_config_ifc6410;downloadfilename=defconfig;name=defconfig \
           "
SRC_URI[initrd.md5sum] = "d92fb01531698e30615f26efa2999c6c"
SRC_URI[initrd.sha256sum] = "d177ba515258df5fda6d34043261d694026c9e27f1ef8ec16674fa479c5b47fb"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r1"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.8%"

COMPATIBLE_MACHINE_ifc6410 = "ifc6410"
LINUX_VERSION_EXTENSION_ifc6410 = "-ifc6410"

