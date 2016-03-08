#!/bin/bash
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
##
## This scripts fetches a pre-built Linaro gcc toolchain
##
## Copyright (c) 2014-2015, The Linux Foundation.
## All rights reserved.
##
## Redistribution and use in source and binary forms, with or without
## modification, are permitted (subject to the limitations in the
## disclaimer below) provided that the following conditions are met:
##
## * Redistributions of source code must retain the above copyright
##   notice, this list of conditions and the following disclaimer.
##
## * Redistributions in binary form must reproduce the above copyright
##   notice, this list of conditions and the following disclaimer in the
##   documentation and/or other materials provided with the distribution.
##
## * Neither the name of the Linux Foundation nor the names of its
##   contributors may be used to endorse or promote products derived from
##   this software without specific prior written permission.
##
## NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE
## GRANTED BY THIS LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT
## HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
## WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
## MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
## DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
## LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
## CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
## SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
## BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
## WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
## OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
## IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
##
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
GCC_URL_4_8=https://releases.linaro.org/archive/13.08/components/toolchain/binaries

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
      git add ${QRL_BREAD_CRUMB}
      # Modify recipe to download from archive
      sed -i 's/org\/${MMYY}/org\/archive\/${MMYY}/g' meta-linaro-toolchain/recipes-devtools/gdb/gdb-linaro-7.6.1.inc
      git add meta-linaro-toolchain/recipes-devtools/gdb/gdb-linaro-7.6.1.inc
      sed -i 's/http/https/g' meta-linaro-toolchain/recipes-devtools/binutils/binutils-linaro-2.24.inc
      sed -i 's/http/https/g' meta-linaro/recipes-extra/powerdebug/powerdebug_0.7.1.bb
      git add meta-linaro-toolchain/recipes-devtools/binutils/binutils-linaro-2.24.inc
      git add meta-linaro/recipes-extra/powerdebug/powerdebug_0.7.1.bb
      git commit -m "QRL changes to meta-linaro"
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
