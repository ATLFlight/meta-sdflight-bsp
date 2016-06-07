DESCRIPTION = "Common networking scripts"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"

RDEPENDS_${PN} = "dnsmasq hostapd"

PROVIDES = "setup-softap"

SRC_URI = "file://hostapd.conf \
           file://qca6234.cfg.softap \
           file://wificonfig.sh \
           file://wlan.conf \
          "

PACKAGES = "${PN}"

FILES_${PN} = "/etc/hostapd.conf \
               /etc/network/interfaces.d/qca6234.cfg.softap \
               /etc/modprobe.d/wlan.conf \
               /usr/local/qr-linux/wificonfig.sh \
              "
              
inherit base

do_install() {
    dest="${D}${sysconfdir}"

    install -d ${dest}/network/interfaces.d
    install -d ${D}/usr/local/qr-linux
    install -d ${dest}/modprobe.d

    # Install the pruned down version for our actual use
    install -m 644 ${WORKDIR}/hostapd.conf ${dest}

    install -m 644 ${WORKDIR}/qca6234.cfg.softap ${dest}/network/interfaces.d

    install -m 755 ${WORKDIR}/wificonfig.sh ${D}/usr/local/qr-linux

    install -m 644 ${WORKDIR}/wlan.conf ${dest}/modprobe.d
}

# Save the user modified conf file
pkg_prerm_${PN}() {
    file_md5sum() {
        pn=$1
        fn=$2
        md5=`cat /var/lib/dpkg/info/${pn}.md5sums | grep ${fn} | cut -d' ' -f1`
        echo $md5
    }

    if [ -f /etc/hostapd.conf ]; then
        cp /etc/hostapd.conf /tmp/hostapd.conf.tmp
        echo $(file_md5sum setup-softap hostapd.conf) > /tmp/hostapd.conf.md5
    fi
}

pkg_postinst_${PN}() {
    install_conf() {
        # Set SSID and install the "live" hostapd.conf
        rand=`od -An -N2 -tu2 /dev/urandom | sed -e 's/ //g'`
        sed -e "s/^ssid=/ssid=Atlanticus_$rand/g" /etc/hostapd.conf > /tmp/hostapd.conf.tmp
        install -m 644 /tmp/hostapd.conf.tmp /etc/hostapd.conf
    }

    # Try to restore the user version if possible
    if [ -f /tmp/hostapd.conf.tmp ]; then
        # Restore the user file if the new file has same
        # contents of file in the previous package version
        new_md5=`md5sum /etc/hostapd.conf | cut -d' ' -f1`
        old_md5=`cat /tmp/hostapd.conf.md5`
        if [ "$old_md5" = "$new_md5" ]; then
            install -m 644 /tmp/hostapd.conf.tmp /etc/hostapd.conf
        else
            install_conf
        fi
    else
        install_conf
    fi

    # Update the default hostapd file to point to the hostapd.conf in the
    # sysconfdir (e.g /etc) and the set the launch options to be "-dd"
    # Since the /etc/default/hostapd file is added by the hostapd package, we want to first
    # check that this file hasn't already been configured prior.
    if ( ! grep "DAEMON_CONF=\"/etc/hostapd.conf\"" /etc/default/hostapd > /dev/null ); then
        sed -e "s|#DAEMON_CONF=\"\"|DAEMON_CONF=\"/etc/hostapd.conf\"|g" \
            -e "s|#DAEMON_OPTS=\"\"|DAEMON_OPTS=\"-dd\"|g" /etc/default/hostapd > /tmp/hostapd.tmp
        install -m 644 /tmp/hostapd.tmp /etc/default/hostapd
    fi

    # Append the dhcp address range to the dnsmasq.conf file. Since the /etc/dnsmasq.conf file is
    # added by the dnsmasq-base package, we want to ensure that it's hasn't already been 
    # configured prior.
    if ( ! grep interface=wlan0 /etc/dnsmasq.conf > /dev/null ); then
        echo "interface=wlan0"                               >> /etc/dnsmasq.conf
        echo "dhcp-range=192.168.1.10,192.168.1.254,1hr" >> /etc/dnsmasq.conf
    fi

    # Lastly, turn off wpa-supplicant and enable hostapd on wlan0. If the qca6234.cfg.lnk exists
    # remove first and then link to .qca6234.cfg.softap
    if [ -h /etc/network/interfaces.d/qca6234.cfg.lnk ]; then
        rm /etc/network/interfaces.d/qca6234.cfg.lnk
    fi

    # If qca6234.cfg exists then this is the first time installing the package. In this case
    # we want to move qca6234.cfg to .qca6234.cfg.station.
    if [ -f /etc/network/interfaces.d/qca6234.cfg ]; then
       rm -f /etc/network/interfaces.d/.qca6234.cfg.station
       mv /etc/network/interfaces.d/qca6234.cfg /etc/network/interfaces.d/.qca6234.cfg.station
    fi

    # Rename the qca6234.cfg.softap file to .qca6234.cfg.softap
    rm -f /etc/network/interfaces.d/.qca6234.cfg.softap
    mv /etc/network/interfaces.d/qca6234.cfg.softap /etc/network/interfaces.d/.qca6234.cfg.softap    

    # Finally, set the default mode to be softap.
    /usr/local/qr-linux/wificonfig.sh -s softap > /dev/null
}
