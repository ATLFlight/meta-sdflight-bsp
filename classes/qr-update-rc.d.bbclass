inherit update-rc.d

updatercd_postinst() {
if type update-rc.d >/dev/null 2>/dev/null; then
	update-rc.d ${INITSCRIPT_NAME} ${INITSCRIPT_PARAMS}
fi
}
