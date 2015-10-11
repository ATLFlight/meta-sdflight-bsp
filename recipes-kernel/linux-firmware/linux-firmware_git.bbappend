# Install only the ar3k firmware
do_install() {
    install -d ${D}/lib/firmware/ar3k
    cp -r ar3k/1020201 ${D}/lib/firmware/ar3k
}
