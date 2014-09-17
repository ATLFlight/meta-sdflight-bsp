
KTAG_ifc6410 = "A8064AAAAANLYA7012"
SRCREV="3d4a060263030250b8a768e2ac6ad783cefbc6b1"

KBRANCH_ifc6410 = "linux-${MACHINE}"
KBRANCH_DEFAULT = "linux-${MACHINE}"

require include/linux-caf.inc


#SRC_URI = "git://codeaurora.org/kernel/msm.git;branch=jb_2.5_auto;revision=${SRCREV};protocol=git;bareclone=1"
SRC_URI = "git://codeaurora.org/kernel/msm.git;nobranch=1;revision=${SRCREV};protocol=git;bareclone=1"

SRC_URI += "file://defconfig \
            file://ifc6410.scc \
            file://ifc6410-patches.scc \
            file://ifc6410.cfg \
            file://ifc6410-user-config.cfg \
           "
SRC_URI += "file://ifc6410-vendor-patches.scc"

SRC_URI += "https://www.codeaurora.org/cgit/quic/la/kernel/msm/patch/\?id=e6fffec6636ffa3062891bae69a3895bbb73b148;patch=1;downloadfilename=arm-7668-1-fix-memset-related-crashes-caused-by-recent-gcc-4.7.2-optimizations.patch;name=memsetPatch1"
SRC_URI[memsetPatch1.md5sum] = "f6c2c2bdfd9471c02024ed05b143271f"
SRC_URI[memsetPatch1.sha256sum] = "9901c8c1171b2f529bfeb033acabc811aea4fea6dc65e8d6134317e1c518d4cd"
SRC_URI += "https://www.codeaurora.org/cgit/quic/la/kernel/msm/patch/\?id=d1814ea12da067fda7bac933e06ef205163617f6;patch=1;downloadfilename=arm-7670-1-fix-the-memset-fix.patch;name=memsetPatch2"
SRC_URI[memsetPatch2.md5sum] = "74dfd49a0d50fb88a3750956275510ad"
SRC_URI[memsetPatch2.sha256sum] = "afd3fa8f7d5f72ac4707e16bdb0b680f54fafa56cb1e20de7ce2eb0a2e6ecf9d"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.8%"

COMPATIBLE_MACHINE_ifc6410 = "ifc6410"
LINUX_VERSION_EXTENSION_ifc6410 = "-ifc6410"

# Look at the *patch files in the top-level directory (brought down
# by repo from the Inforce site), and create a ifc6410-vendor-patches.scc
# file with that list, in the recipe's directory.
# We will later (in patch_prepend) copy these patch files to the same directory
# 
# NOTE: Other, probably more elegant approaches, like adding these
#       files to the SRC_URI, or just create a .scc file with absolute
#       paths didn't work. In particular, modifying SRC_URI didn't
#       work because it changed the order of patch application

do_fetch_prepend() {
    import shutil
    import glob

    import os
    # Where to look for downloaded patches
    src = d.getVar('COREBASE', True)+'/../inforce-ifc6410-rel2.0/kernel'
    patchesFile = d.getVar('FILE_DIRNAME', True)+'/linux-qr-ifc6410/ifc6410-vendor-patches.scc'
    f = open(patchesFile, 'w+')
    for patchFilePath in sorted(glob.glob(src+"/*.patch")):
        patchFile = os.path.basename(patchFilePath)
        if not patchFile.startswith("0024") and not patchFile.startswith("0029"):
            f.write('patch '+ patchFile +'\n')
    f.close()
}

## Override bluetooth kernel components
do_kernel_checkout_append() {
    btsrc=${COREBASE}/../kernel-v3.4.66
    btdst=${WORKDIR}/linux 
    # Note that at this point we are in a headless state, that will
    # be converted to a branch (KERNEL_BRANCH) in do_patch.
    	
    # Copy baseline bluetooth
    /bin/cp -fr ${btsrc}/net/bluetooth/* ${btdst}/net/bluetooth
    /bin/cp -fr ${btsrc}/include/net/bluetooth/* ${btdst}/include/net/bluetooth
    /bin/cp -fr ${btsrc}/drivers/bluetooth/* ${btdst}/drivers/bluetooth
	
    curdir=`pwd`
    cd ${btdst}
    git status
    echo "Commiting baseline bluetooth"
    git add -A
    git commit -m "Updated bluetooth baseline" 
    cd ${curdir}
}

# Copy the *patch files from top level dir to recipe's dir
do_patch_prepend() {
    src="${COREBASE}/../inforce-ifc6410-rel2.0/kernel"
    dst=${FILE_DIRNAME}/${PN}
    /bin/cp ${src}/*patch ${dst}
}

# Important to clean these files in the do_clean
do_clean_append() {
    import shutil
    import glob
    import os
    import string

    # Where the patch files to be deleted are located
    patchesDir = d.getVar('FILE_DIRNAME', True)+'/'+d.getVar('PN', True)
    # We only delete the patches we copied, which are in the ifc6410-vendor-patches.scc file
    patchesFile = patchesDir+'/ifc6410-vendor-patches.scc'

    try:
        # Read each line, split, take the second field, and add it to our array
        patchesToDel = [string.split(line.strip(), ' ')[1] for line in open(patchesFile)]
        for patch in patchesToDel:
            os.remove(patchesDir+'/'+patch)
    except Exception as e:
        bb.warn("{}".format(e))
    # We don't delete the ifc6410-vendor-patches.scc file, since it'll be truncated
    # in do_fetch_prepend anyways.
}
