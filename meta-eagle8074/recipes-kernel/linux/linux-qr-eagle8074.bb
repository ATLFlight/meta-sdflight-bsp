# This file was derived from oe-core/meta-qr-linux/meta-som8064/recipes-kernel/linux/linux-qr-som8064.bb

require include/linux-caf.inc

FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://linux"
SRC_URI += "file://defconfig \
            file://${MACHINE}.scc \
            file://${MACHINE}-user-config.cfg \
            file://bluetooth.patch;apply=no \
           "

SRC_URI += "http://releases.linaro.org/14.09/ubuntu/ifc6410/initrd.img-3.4.0-linaro-ifc6410;downloadfilename=initrd.img;name=initrd"
SRC_URI[initrd.md5sum] = "d92fb01531698e30615f26efa2999c6c"
SRC_URI[initrd.sha256sum] = "d177ba515258df5fda6d34043261d694026c9e27f1ef8ec16674fa479c5b47fb"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}"

GCCVERSION="4.8%"

COMPATIBLE_MACHINE_eagle8074 = "eagle8074"
LINUX_VERSION_EXTENSION_eagle8074 = "-eagle8074"

PROVIDES += "kernel-module-cfg80211"

python do_rem_old_linux () {
    import os
    os.system("rm -rf %s/linux-v3.6.9" % d.getVar('DL_DIR', True))
}

do_after_unpack() {
    rm -f ${WORKDIR}/bluetooth.patch.done
}

# Override BT kernel driver files with the ones from upstream kernel v3.6.9
# in order to support the bluez BT protocol stack
do_override_bluetooth_files() {
    btsrc=${DL_DIR}/linux-v3.6.9
    btdst=${WORKDIR}/linux

    # If the 3.6.9 kernel tree hasn't been cloned yet, do so now.
    if [ ! -d ${btsrc} ]; then
        git clone -b v3.6.9 --depth 1 git://codeaurora.org/quic/la/kernel/msm.git ${btsrc}
        pushd ${btsrc}
        git checkout v3.6.9
        popd
    fi

    # If we haven't already replaced and patched the BT kernel driver
    # files, do so now
    if [ ! -f ${WORKDIR}/bluetooth.patch.done ]; then
        # If haven't already done so, apply the patch to re-enable sleep
        # and power mgmt
        /bin/cp -fr ${btsrc}/net/bluetooth/* ${btdst}/net/bluetooth
        /bin/cp -fr ${btsrc}/include/net/bluetooth/* ${btdst}/include/net/bluetooth
        /bin/cp -fr ${btsrc}/drivers/bluetooth/* ${btdst}/drivers/bluetooth

        pushd ${btdst}
        patch -p1 < ${WORKDIR}/bluetooth.patch
        touch ${WORKDIR}/bluetooth.patch.done
        popd
    fi
}

addtask rem_old_linux after do_cleansstate before do_cleanall
addtask do_after_unpack after do_unpack before do_override_bluetooth_files
addtask do_override_bluetooth_files after do_kernel_checkout before do_patch
