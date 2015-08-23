# minigzip is needed for creating the recovery initrd
# Explanation is from imgdiff, it requires minigzip instead of gzip
do_install_append_class-native() {
    cp ${B}/minigzip ${bindir}/minigzip
}
