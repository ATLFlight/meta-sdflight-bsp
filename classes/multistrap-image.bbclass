# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"
do_rootfs[dirs] = "${TOPDIR} ${WORKDIR}/intercept_scripts"
do_rootfs[lockfiles] += "${IMAGE_ROOTFS}.lock"
do_rootfs[cleandirs] += "${S} ${WORKDIR}/intercept_scripts"
do_rootfs[recrdeptask] += "do_packagedata"
# Must call real_do_rootfs() from inside here, rather than as a separate
# task, so that we have a single fakeroot context for the whole process.
do_rootfs[umask] = "022"
fakeroot do_rootfs() {
	# Replace place holders with build system values.
	sed -e "s|@DEPLOY_DIR@|${DEPLOY_DIR}|" -e "s|@MACHINE@|${MACHINE}|" -e "s|@WORKDIR@|${WORKDIR}|" -i ${WORKDIR}/multistrap.conf

	# Convert flat directories to package repositories
	CURDIR=`pwd`
	cd ${DEPLOY_DIR}/deb/${MACHINE}
	dpkg-scanpackages . /dev/null | gzip -9c > ${DEPLOY_DIR}/deb/${MACHINE}/Packages.gz
	dpkg-scansources . /dev/null | gzip -9c > ${DEPLOY_DIR}/deb/${MACHINE}/Sources.gz
	cd ${CURDIR}

	# Construct the user space.
	APT_CONFIG=${WORKDIR}/apt.conf /usr/sbin/multistrap -f ${WORKDIR}/multistrap.conf -d ${IMAGE_ROOTFS}

	# Create the image directory
	mkdir -p ${DEPLOY_DIR_IMAGE}

	${IMAGE_PREPROCESS_COMMAND}

	${@get_imagecmds(d)}

	${IMAGE_POSTPROCESS_COMMAND}
	
	${MACHINE_POSTPROCESS_COMMAND}
}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

addtask rootfs before do_build

DEPENDS += "e2fsprogs-native"
