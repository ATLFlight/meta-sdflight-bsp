
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
SRC_URI += "file://ifc6410-vendor-patches.scc"

LINUX_VERSION ?= "3.4"
LINUX_VERSION_EXTENSION ?= "-${MACHINE}"

PR = "r0"
PV = "${LINUX_VERSION}+git${SRCPV}"

GCCVERSION="4.7%"

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
        f.write('patch '+ patchFile +'\n')
    f.close()
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
