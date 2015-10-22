LICENSE = "NOT_APPLICABLE"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/files/custom-licenses/LIC_NOT_APPLICABLE;md5=82052abe09eb68b997d7e9ec5c22e294"

PV = "1.0"
PR = "r0"
inherit native

SRC_URI += "git://git.linaro.org/ci/ubuntu-build-service;revision=${SRCREV};protocol=git"
SRCREV = "${AUTOREV}"
LINARO_IMG_NAME = "trusty-armhf-developer"
PARTITION_USERDATA_SIZE = "8G"

# Check that the required packages are installed.
def check_pkg(d,pkg, ver):
    import subprocess
    p=subprocess.Popen(['/usr/bin/dpkg-query', '--admindir=/var/lib/dpkg', '--show', "--showformat='${Version}'", pkg], 
                      stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out,err=p.communicate()
    retCode=p.returncode
    if (retCode != 0):
        bb.fatal("Package " + pkg + " not installed: " + err + 
                 "**Please install this package. You can find it in the code downloaded by this recipe at: " + 
                 d.getVar('S', True) + "/packages")
    if ver != '' and out.startswith(ver):
        bb.fatal("Wrong version of package " + pkg + ". Installed version: "+ str(out) + ". Expected: " + ver)

# Check if sudo has been configured properly. We use live-build, which is invoked with sudo from the
# Makefile. Also invoked from the same Makefile is sudo mv.
def check_sudo(d):
    import subprocess
    bb.warn("This task requires sudo access for /usr/bin/lb and /bin/mv. If this task hangs for more than 30 seconds,\
 sudo has not been configured properly. Use Ctrl-C to kill bitbake, and kill -9 to kill the grandparent of the hung\
 sudo task (use pstree -p to find the hung task). Then configure sudo access properly and run this task again")
    bb.warn("Have a sysadmin add the following to /etc/sudoers: <username> ALL = NOPASSWD: /usr/bin/lb, /bin/mv")
    p=subprocess.Popen(['sudo', '/bin/mv', '--help'], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out,err = p.communicate()

def check_devenv(d):
    check_pkg(d, 'live-build', '3.0.5-1linaro1')
    check_pkg(d, 'qemu-user-static', '')
    check_sudo(d)


do_unpack_append() {
    import shutil
    import os
    s = d.getVar('S', True)
    wd = d.getVar('WORKDIR',True)
    if os.path.exists(s):
        shutil.rmtree(s)
    shutil.move(wd+'/git', s)

    check_devenv(d)
}

create_image() {
    # If the persist directory does not exist, we should create one with an
    # empty image file.
    if [ ! -e ${DEPLOY_DIR_IMAGE}/out ]; then
        mkdir -p ${DEPLOY_DIR_IMAGE}/out
    fi
    x=`pwd`
    bbwarn "${x} sudo rm -rf binary/dev"
    sudo rm -rf binary/dev
    sudo mkdir binary/dev

    sudo ${STAGING_BINDIR_NATIVE}/make_ext4fs -s -l ${PARTITION_USERDATA_SIZE} \
        ${DEPLOY_DIR_IMAGE}/out/userdata.img binary
}

customize() {
    # Nothing to do for now
    :
}

do_configure() {
    cd ${LINARO_IMG_NAME}
    ./configure
}
    
do_image() {
    cd ${LINARO_IMG_NAME}
    make
    customize
    create_image
}

do_image_clean() {
    cd ${LINARO_IMG_NAME}
    make clean
}

do_build[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_update_package[depends] = "imgdiff-native:do_populate_sysroot"
addtask image before do_build after do_patch do_compile
addtask image_clean

