#
###############################################################################
## Author: Rahul Anand (ranand@codeaurora.org)
## 
## This script fixes up the file system created by multistrap. It will
## be automatically run on-target, after booting the kernel for the first time
##
## It does standard things discussed in the multistrap guide, i.e. runs
## the dash.preinst script, and dkpg --configure -a.
## It then does user-space configuration for QR-Linux.
###############################################################################
export DEBIAN_FRONTEND=noninteractive DEBCONF_NONINTERACTIVE_SEEN=true
export LC_ALL=C LANGUAGE=C LANG=C

# Configure local time
LOCAL_TIMEZONE=
echo "America/Los_Angeles" > /etc/timezone

echo "[INFO] Configuring target on first-boot"
HOST_NAME=snapdragon

# Need to create man1 for dash configuration to proceed for emdebian only, but it does not
# hurt to have it for other user spaces either
mkdir -p /usr/share/man/man1
/var/lib/dpkg/info/dash.preinst install

##
## Add a diversion for /sbin/initctl, which gets called, but fails, in the configuration
## phase. We only need it during normal boot, so we'll delete the diversion later in 
## this script
## 
dpkg-divert --local --rename --add /sbin/initctl
ln -s /bin/true /sbin/initctl

##
## Add a temporary hostname
## 
serialNum=100
hostName=${HOST_NAME}${serialNum}
echo $hostName >> /etc/hostname
echo "127.0.0.1 localhost" >> /etc/hosts

##
## Create /dev/random and /dev/urandom
##
test -e /dev/random || /bin/mknod -m 644 /dev/random c 1 8
test -e /dev/urandom || /bin/mknod -m 644 /dev/urandom c 1 9

##
## Create /dev/mmcblk0p12 (system), /dev/mmcblk0p13 (userdata),
## /dev/mmcblk0p14 (persist), /dev/mmcblk0p15 (cache), 
## and /dev/mmcblk0p1 (firmware).
##
test -e /dev/mmcblk0p1  || /bin/mknod -m 644 /dev/mmcblk0p1  b 179  1
test -e /dev/mmcblk0p12 || /bin/mknod -m 644 /dev/mmcblk0p12 b 179 12
test -e /dev/mmcblk0p13 || /bin/mknod -m 644 /dev/mmcblk0p13 b 179 13
test -e /dev/mmcblk0p14 || /bin/mknod -m 644 /dev/mmcblk0p14 b 179 14
test -e /dev/mmcblk0p15 || /bin/mknod -m 644 /dev/mmcblk0p15 b 179 15

##
## Do the dkpg post-configure thingy
## 
echo "[INFO] Configuring packages..."
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv dpkg --configure -a
mount proc -t proc /proc
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv dpkg --configure -a

##
## Change the owners on /dev/random and /dev/urandom
##
chown root:root /dev/random /dev/urandom

##
## Make a symlink of /var/log/syslog as /var/log/messages
##
ln -s /var/log/syslog /var/log/messages

##
## Set the default runlevel to 2
## 
sed -i -e 's/DEFAULT_RUNLEVEL=1/DEFAULT_RUNLEVEL=2/' /etc/init/rc-sysinit.conf

##
## Remove the diversion we created earlier
## 
/bin/rm /sbin/initctl
dpkg-divert --rename --remove /sbin/initctl

##
## Set right permissions for /tmp 
## 
/bin/chmod 777 /tmp 

##
## Set the root password 
## 
echo "[INFO] Setting root password..."
echo root:clarence | /usr/sbin/chpasswd

##
## Add a non-root user account "qrl-user" and add it to sudo group
##
(useradd -b /home -m -s /bin/bash qrl-user && usermod -aG sudo qrl-user)
## 
## Creat a group for diag.
groupadd diag
# Now modify the Wi-Fi configuration file
sed -i -e "s/#gStaKeepAlivePeriod = 30/gStaKeepAlivePeriod = 30/" /lib/firmware/wlan/prima/WCNSS_qcom_cfg.ini

##
## Uniqify the hostname using /dev/random
## 

# Get a 3-digit random number for appending to the hostname
serialNum=$(( 100+( $(od -An -N2 -i /dev/random) )%(1000) ))
hostName=${HOST_NAME}${serialNum}
echo "[INFO[ Configuring host name to: $hostName"
echo -n > /etc/hostname # Truncate the file first
echo $hostName >> /etc/hostname

# Touch /etc/apt/sources.list since multistrap doesn't create it, and motd complains if
# it doesn't exist
touch /etc/apt/sources.list

##
## Some other administrative tasks
## 
/bin/rm /sbin/insserv
/bin/ln -s /usr/lib/insserv/insserv /sbin/insserv
locale-gen en_US.UTF-8
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv dpkg-reconfigure locales

sync;sync

# Install our binary packages.
/usr/local/qr-linux/installPkgs.sh
