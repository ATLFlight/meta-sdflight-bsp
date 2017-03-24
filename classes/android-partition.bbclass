# NOTE: You must inherit image or coreimage before this class

DEPENDS += "ext2simg-native"

IMAGE_POSTPROCESS_COMMAND += "create_simg; "

create_simg() {
    echo "RUNNING create_simg"
    ext2simg -v ${IMGDEPLOYDIR}/${PN}-${MACHINE}.ext4 ${IMGDEPLOYDIR}/${PN}-${MACHINE}.img
}

# For an empty parition without /etc /var
# set IMAGE_PREPROCESS_COMMAND = "remove_extra_files; "
python remove_extra_files() {
    import shutil

    # If using RPMs, remove the RPM handling additions
    workdir = d.getVar("WORKDIR", True)
    shutil.rmtree(workdir+"/rootfs/etc")
    shutil.rmtree(workdir+"/rootfs/var")
}

# The cache partition contains the formware and is mounted at /lib 
# Fix the paths if this is the cache partition:
# set IMAGE_PREPROCESS_COMMAND += "move_firmware_files; "
python move_firmware_files() {
    import shutil

    # Since we are moving /lib/firmware to a new partition, fix paths
    workdir = d.getVar("WORKDIR", True)
    if os.path.isdir(workdir+"/rootfs/firmware"):
        shutil.rmtree(workdir+"/rootfs/firmware")
    shutil.move(workdir+"/rootfs/lib/firmware", workdir+"/rootfs/")
    shutil.rmtree(workdir+"/rootfs/lib")
}

