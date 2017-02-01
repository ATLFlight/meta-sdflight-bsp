#!/bin/bash
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This scripts installs the SDK at the desired location. It needs to be called from
## the directory where it was originally un-tarred, and takes one argument, the
## destination location
## 
## Copyright (c) 2014, The Linux Foundation.
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

optSDKDir=
optTarsLocation=

usage ()
{
    n=`basename $0`
    cat << EOF
USAGE: $n OPTIONS
This scripts installs the SDK for cross-development of applications on the Snapdragon
apps processor.

OPTIONS:
   -h      Show this message
   -d      The destination directory where to install the SDK [Required]
   -s      The source directory where the SDK tarball was extracted [Optional]
EOF
}

################################################################################
## handleCommandLine
################################################################################
handleCommandLine () {
   while getopts "hd:s:" o
   do
      case $o in
         h)
             usage
             exit 1
             ;;
          d)
              optSDKDir=$OPTARG
              ;;
          s)
              optTarsLocation=$OPTARG
              ;;
          ?)
              echo "Unknown arg $o"
              usage
              exit
              ;;
      esac
   done
   if [[ -z ${optSDKDir} ]]
   then
      echo "[ERROR] Missing destination directory. Specify using -d"
      usage
      exit 1
   fi
   optSDKDir=`readlink -fm ${optSDKDir}`

   if [[ -z ${optTarsLocation} ]]
   then
      echo "[INFO] Missing source directory. Checking pwd"
      optTarsLocation=`pwd`
   fi
   if [[ ! -e ${optTarsLocation}/%PATTERN_ENV_FILE% ]]
   then
      echo "[ERROR] Current directory is not a valid SDK source directory. Specify using -s"
      usage
      exit 1
   fi
   optTarsLocation=`readlink -f ${optTarsLocation}`
}

################################################################################
## updateEnvFile
################################################################################
updateEnvFile () {
   sed -i "s|SDK_DIR=.*|SDK_DIR=\"${optSDKDir}\"|" ${optTarsLocation}/%PATTERN_ENV_FILE%
}

################################################################################
## installSysroots
################################################################################
installSysroots () {
   if [[ ! -d ${optSDKDir} && -e ${optSDKDir} ]]
   then
      echo "[ERROR] ${optSDKDir} exists and is not a directory. Please remove or rename it"
      exit 1
   fi
   mkdir -p ${optSDKDir}
   cp ${optTarsLocation}/%PATTERN_ENV_FILE% ${optSDKDir}
   (
      cd ${optSDKDir}
      echo "[INFO] Unpacking SDK. This might take some time"
      tar zxf ${optTarsLocation}/%PATTERN_SYSROOTS_TGZ%
   )
}

################################################################################
## getToolchain
################################################################################
getToolchain () {
   GCC_4_8="%PATTERN_GCC48%"
   GCC_4_9="%PATTERN_GCC49%"
   GCC_URL_4_8="%PATTERN_GCC_URL48%"
   GCC_URL_4_9="%PATTERN_GCC_URL49%"
   GCC_TAR_EXT=tar.xz

   # Check if a newer file exists, and download it
   echo "[INFO] Check gcc4.8 and gcc4.9 toolchains and download if necessary"
   (
      cd ${optSDKDir}
      wget -N ${GCC_URL_4_8}/${GCC_4_8}.${GCC_TAR_EXT}
      wget -N ${GCC_URL_4_9}/${GCC_4_9}.${GCC_TAR_EXT}
      # Remove the existing untarred files, and do a fresh untar every time
      test -e ${GCC_4_8} && rm -rf ${GCC_4_8}
      test -e ${GCC_4_9} && rm -rf ${GCC_4_9}
      tar xf ${GCC_4_8}.${GCC_TAR_EXT}
      tar xf ${GCC_4_9}.${GCC_TAR_EXT}
   )
}

################################################################################
handleCommandLine $@
updateEnvFile && installSysroots && getToolchain
