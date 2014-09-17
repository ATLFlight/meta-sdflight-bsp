#!/bin/bash
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This scripts fetches a pre-built Linaro gcc toolchain
###############################################################################

# Create this file after tweaking meta-linaro, and don't do anything if it exists
QRL_BREAD_CRUMB=".qrl_meta_linaro_tweaked"

GCC_TAR_EXT=tar.xz

GCC_4_7=gcc-linaro-arm-linux-gnueabihf-4.7-2013.04-20130415_linux
GCC_URL_4_7=https://launchpad.net/linaro-toolchain-binaries/trunk/2013.04/+download

# gcc4.8 version used in Linaro's 14.07 build
# GCC_4_8=gcc-linaro-arm-linux-gnueabihf-4.8-2014.04_linux
# GCC_URL_4_8=http://releases.linaro.org/14.04/components/toolchain/binaries

# gcc4.8 version that works on Ubuntu 14.04
GCC_4_8=gcc-linaro-arm-linux-gnueabihf-4.8-2013.08_linux
GCC_URL_4_8=http://releases.linaro.org/13.08/components/toolchain/binaries

# We are now using gcc4.8
GCC=${GCC_4_8}
GCC_URL=${GCC_URL_4_8}

###############################################################################
# Modify the meta-linaro layer to meet our needs. This can't be done from
# within a recipe
###############################################################################
modify-meta-linaro () {
   # We need to cherry-pick this commit onto our daisy branch
   COMMIT_TO_PICK="ab3610aefa2786e3172e751c0ae5e5f560e37444"
   (
      cd meta-linaro; 
      # Check if need to do this again
      if [[ -e ${QRL_BREAD_CRUMB} ]]
      then
         echo "[INFO] Nothing to do in meta-linaro"
	     return
      fi

      # First we need to cherry-pick a commit
      echo "[INFO] Cherry-picking commit to meta-linaro"
      git cherry-pick ${COMMIT_TO_PICK}
      touch ${QRL_BREAD_CRUMB}
      return

      # We also needed to modify the recipes themselves for the newer gcc4.8 (2014.04)
      # but not for the one we're settled on using eventually (2013.08).
      # Hence the following is dead code for now. Checking it in only in case we
      # need to move to the newer version
      f="meta-linaro-toolchain/conf/distro/include/external-linaro-toolchain-versions.inc"
      if [[ -e ${f} ]]
      then
         echo "[INFO] Patching $f"
	     sed -i -e "s/\(.*d.setVar\)\(.*ELT_VER_LIBC.*\)/\1\2\n\1('QRL_ELT_VER_LIBC', elt_get_libc_version(ld)[0:5])/" ${f}
      fi

      f="meta-linaro-toolchain/recipes-devtools/external-linaro-toolchain/external-linaro-toolchain.bb"
      if [[ -e ${f} ]]
      then
	     echo "[INFO] Patching $f"
	     sed -i -e 's/PKGV_${PN} .*/PKGV_${PN} = "${QRL_ELT_VER_LIBC}"/' ${f}
      fi
      
   )
}

###############################################################################
# Check if a newer file exists, and download it
wget -N ${GCC_URL}/${GCC}.${GCC_TAR_EXT}
# Remove the existing untarred files, and do a fresh untar every time
test -e ${GCC} && rm -rf ${GCC}
tar xf ${GCC}.${GCC_TAR_EXT}

modify-meta-linaro
