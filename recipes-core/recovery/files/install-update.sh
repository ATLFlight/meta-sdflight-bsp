#!/bin/sh
# Copyright (c) 2015, The Linux Foundation. All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are
# met:
#     * Redistributions of source code must retain the above copyright
#       notice, this list of conditions and the following disclaimer.
#     * Redistributions in binary form must reproduce the above
#       copyright notice, this list of conditions and the following
#       disclaimer in the documentation and/or other materials provided
#       with the distribution.
#     * Neither the name of The Linux Foundation nor the names of its
#       contributors may be used to endorse or promote products derived
#       from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
# ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
# BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
# BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
# OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
# IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

update_file="$1"
update_block="/dev/mmcblk0p17"
update_mount="/mnt/update"
recovery_folder="${update_mount}/recovery"

if [ "$update_file" = "" ] || [ ! -f ${update_file} ]; then
    echo "Error: Update file ${update_file} not found"
    echo "Usage: $0 <path/to/update.zip>"
    exit 1
fi

# Mount the update partition
mkdir -p ${update_mount}
mount -t ext4 ${update_block} ${update_mount}

# Clean cache before copying the file
rm -rf ${update_mount}/*.zip

# Copy update file to the update folder
[ -d ${recovery_folder} ] || mkdir -p ${recovery_folder}
cp ${update_file} ${update_mount}

# Create command file for recovery
echo "--update_package=/cache/${update_file##*/}" > ${recovery_folder}/command
echo "Installing update file ${update_file}..."
sync

# Unmount the update partition
umount ${update_mount}

# Reboot to recovery
reboot2fastboot recovery
