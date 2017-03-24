do_install_append() {
	echo "# Disabled by meta-sdflight-bsp" > ${D}/lib/udev/rules.d/60-persistent-v4l.rules
}
