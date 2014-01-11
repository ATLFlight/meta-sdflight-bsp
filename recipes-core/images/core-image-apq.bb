LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://../COPYING;md5=11c1d78c92548a586eafd0c08349534b"

inherit image_types
inherit multistrap-image

#IMAGE_INSTALL = "image-base"
IMAGE_FSTYPES = "ext4"
IMAGE_LINGUAS = " "
IMAGE_ROOTFS_SIZE = "8192"


SRC_URI += " \
   file://apt.conf \
   file://multistrap.conf \
   file://COPYING \
   "
