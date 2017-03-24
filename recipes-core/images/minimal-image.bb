inherit core-image

SUMMARY = "Eagle rootfs image"

DEPENDS += "lk ext2simg-native"

# rootfs partition is 8G 
# IMAGE_ROOTFS_SIZE = "8388608"

IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP} ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_ROOTFS_SIZE = "524288"
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

# Adding the kernel source add 577M to the SDK
# Its preferable to let the developers just clone the kernel at a specific label
# A script to clone the kernel can be provided
# TOOLCHAIN_TARGET_TASK_append = " kernel-${MACHINE}-devsrc"

PACKAGECONFIG_pn-qemu-native = ""

# delete the old image files that already exist
python do_rootfs_prepend() {
    import glob, os

    deploydir = d.getVar("DEPLOY_DIR_IMAGE", True)
    pn = d.getVar("PN", True)
    for f in glob.glob(deploydir+"/*"+pn+"*"):
        os.remove(f)
}

python do_rootfs_append() {
    import os

    deploydir = d.getVar("DEPLOY_DIR_IMAGE", True)
    pn = d.getVar("PN", True)
    machine = d.getVar("MACHINE", True)
    rootname = deploydir+"/"+pn+"-"+machine
    os.system("ext2simg -v "+rootname+".ext4 "+rootname+".img")
}
