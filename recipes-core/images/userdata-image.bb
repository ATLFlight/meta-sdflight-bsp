inherit core-image
inherit android-partition

SUMMARY = "Eagle rootfs image"

DEPENDS += "lk ext2simg-native"

# rootfs partition is 8G 
IMAGE_ROOTFS_SIZE = "8388608"

IMAGE_INSTALL = "packagegroup-core-boot ${ROOTFS_PKGMANAGE_BOOTSTRAP} ${CORE_IMAGE_EXTRA_INSTALL}"
IMAGE_INSTALL += "packagegroup-ros-sdflight"
IMAGE_INSTALL += "android-tools"
IMAGE_INSTALL += "gdb"
#IMAGE_INSTALL += "oprofile"
#IMAGE_INSTALL += "strace"
IMAGE_INSTALL += "libstdc++"
IMAGE_INSTALL += "adsprpc"
IMAGE_INSTALL += "kernel-module-wlan"
IMAGE_INSTALL += "wireless-tools"
IMAGE_INSTALL += "hostapd"
IMAGE_INSTALL += "wpa-supplicant"
IMAGE_INSTALL += "pciutils"
#IMAGE_INSTALL += "setup-softap"
IMAGE_INSTALL += "file"

IMAGE_INSTALL += "camera-hal"
IMAGE_INSTALL += "mm-video-oss"
IMAGE_INSTALL += "mm-video-fpv"
IMAGE_INSTALL += "hostapd"
IMAGE_INSTALL += "libnl"
IMAGE_INSTALL += "dnsmasq"
IMAGE_INSTALL += "sdcard-automount"
IMAGE_INSTALL += "ueventd"
IMAGE_INSTALL += "live555"
IMAGE_INSTALL += "libjpeg-turbo"
IMAGE_INSTALL += "libopenh264"
IMAGE_INSTALL += "frameworks-av"
IMAGE_INSTALL += "post-boot"
IMAGE_INSTALL += "libhardware"
IMAGE_INSTALL += "power-hal"

# From meta-qti or meta-qti-repackage
IMAGE_INSTALL += "reboot2fastboot"
IMAGE_INSTALL += "diag"
IMAGE_INSTALL += "mp-decision"
IMAGE_INSTALL += "qmi"
IMAGE_INSTALL += "qmi-framework"
IMAGE_INSTALL += "thermal-engine"
IMAGE_INSTALL += "ath6kl-utils"
IMAGE_INSTALL += "q6-start"
IMAGE_INSTALL += "ss-restart"
IMAGE_INSTALL += "mm-camera"
IMAGE_INSTALL += "mm-still"
IMAGE_INSTALL += "mm-video"
IMAGE_INSTALL += "ftmdaemon"
IMAGE_INSTALL += "fastmmi"
IMAGE_INSTALL += "remote-debug-agent"
IMAGE_INSTALL += "perf-tools"
IMAGE_INSTALL += "adreno200"
IMAGE_INSTALL += "math-cl"

#IMAGE_INSTALL += "ath3k-bluez"
IMAGE_INSTALL += "qrl-scripts"
#IMAGE_INSTALL += "recovery"
#IMAGE_INSTALL += "recovery-image"
IMAGE_INSTALL += "recovery-script"
#IMAGE_INSTALL += "signapk-java"

# Nodejs
IMAGE_INSTALL += "nodejs"
IMAGE_INSTALL += "nodejs-npm"

# Testing
IMAGE_INSTALL += "tcpdump"
IMAGE_INSTALL += "wget"
IMAGE_INSTALL += "unzip"
IMAGE_INSTALL += "psmisc"
IMAGE_INSTALL += "p7zip"

# LTE
IMAGE_INSTALL += "openvpn"

# DISABLED - too many extraneous deps
#IMAGE_INSTALL += "imagemagick"

# GStreamer
IMAGE_INSTALL += "gstreamer1.0-plugins-base"
IMAGE_INSTALL += "gstreamer1.0-plugins-good"

# Kernel
IMAGE_INSTALL += "kernel-module-wlan"
IMAGE_INSTALL += "compat-wireless"
IMAGE_INSTALL += "kernel-module-rdbg"

# depmodwrapper-cross doesn't generate packages in morty
#IMAGE_INSTALL += "depmodwrapper-cross"

# Missing
IMAGE_INSTALL += "openssl"
IMAGE_INSTALL += "rsyslog"
IMAGE_INSTALL += "dropbear"

# OpenCV
CORE_IMAGE_EXTRA_INSTALL += "opencv libopencv-core libopencv-imgproc" 

IMAGE_ROOTFS_SIZE = "524288"
IMAGE_ROOTFS_EXTRA_SPACE_append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "" ,d)}"

PACKAGECONFIG_pn-qemu-native = ""

