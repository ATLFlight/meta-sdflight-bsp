#
###############################################################################
## Author: Rahul Anand (ranand@qti.qualcomm.com)
## 
## This script fixes up the file system created by multistrap. It will
## be automatically run on-target, after booting the kernel for the first time
##
## It does standard things discussed in the multistrap guide, i.e. runs
## the dash.preinst script, and dkpg --configure -a.
## It does user-space specific things, and then adds a default user too.
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
## phase. We only need it during normal boot, so we'll delete the diversion at the end
## 
dpkg-divert --local --rename --add /sbin/initctl
ln -s /bin/true /sbin/initctl

##
## Add a hostname
## 
#serialNum=$(( $RANDOM % 10 ))
serialNum=100
hostName=${HOST_NAME}${serialNum}
echo "[INFO[ Configuring host name to: $hostName"
echo $hostName >> /etc/hostname
echo "127.0.0.1 localhost" >> /etc/hosts

##
## Create /dev/random and /dev/urandom
##
echo "[INFO] Creating /dev/random and /dev/urandom..."
test -e /dev/random || /bin/mknod -m 644 /dev/random c 1 8
test -e /dev/urandom || /bin/mknod -m 644 /dev/urandom c 1 9

##
## Do the dkpg post-configure thingy
## 
echo "[INFO] Configuring packages..."
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv dpkg --configure -a
mount proc -t proc /proc
PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/insserv dpkg --configure -a
echo "[INFO] Done configuring packages"

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
#echo "S:2345:respawn /sbin/getty -L ttyHSL0 115200 linux >> /etc/inittab

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
echo "Set root password..."
echo root:clarence | /usr/sbin/chpasswd

##
## Add a non-root user account "clarence" and add it to sudo group
##
(useradd -b /home -m -s /bin/bash clarence && usermod -aG sudo clarence)

##
## Uniqify the hostname and the Wi-Fi MAC address using /dev/random
## 
# Get a 2-digit random nunmber for changing the MAC address
mac0=$(( 10+$(od -An -N2 -i /dev/random)%(10) ))
mac1=$(( $mac0+1 ))
mac2=$(( $mac0+2 ))
mac3=$(( $mac0+3 ))
newMac0="Intf0MacAddress=000AF58989${mac0}"
newMac1="Intf1MacAddress=000AF58989${mac1}"
newMac2="Intf2MacAddress=000AF58989${mac2}"
newMac3="Intf3MacAddress=000AF58989${mac3}"
sed -i -e "s/Intf0MacAddress=000AF58989FF/${newMac0}/" /lib/firmware/wlan/prima/WCNSS_qcom_cfg.ini
sed -i -e "s/Intf1MacAddress=000AF58989FE/${newMac1}/" /lib/firmware/wlan/prima/WCNSS_qcom_cfg.ini
sed -i -e "s/Intf2MacAddress=000AF58989FD/${newMac2}/" /lib/firmware/wlan/prima/WCNSS_qcom_cfg.ini
sed -i -e "s/Intf3MacAddress=000AF58989FC/${newMac3}/" /lib/firmware/wlan/prima/WCNSS_qcom_cfg.ini
echo "[INFO] Configuring Wi-Fi MAC addresses to: ${newMac0}, ${newMac1}, ${newMac2}, ${newMac3}"
# Now make other changes to the file
sed -i -e "s/#gStaKeepAlivePeriod = 30/gStaKeepAlivePeriod = 30/" /lib/firmware/wlan/prima/WCNSS_qcom_cfg.ini

# Get a 3-digit random number for appending to the hostname
serialNum=$(( 100+( $(od -An -N2 -i /dev/random) )%(1000) ))
hostName=${HOST_NAME}${serialNum}
echo "[INFO[ Configuring host name to: $hostName"
echo -n > /etc/hostname # Truncate the file first
echo $hostName >> /etc/hostname

# Touch /etc/apt/sources.list since multistrap doesn't create it, and motd complains if
# it doesn't exist
touch /etc/apt/sources.list

sync;sync
## 
## Add start_usb and start_adbd services 
##
export PATH=$PATH:/usr/lib/insserv
echo "Adding adb_usb service"
#/usr/lib/insserv/insserv start_usb
/usr/sbin/update-rc.d start_usb defaults
echo "Adding adb_deamon service"
#/usr/lib/insserv/insserv start_adbd
/usr/sbin/update-rc.d start_adbd defaults

sync;sync

