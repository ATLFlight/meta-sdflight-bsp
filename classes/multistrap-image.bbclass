LIC_FILES_CHKSUM = "file://${COREBASE}/LICENSE;md5=3f40d7994397109285ec7b81fdeb3b58 \
                    file://${COREBASE}/meta-qr-linux/COPYING;md5=7b4fa59a65c2beb4b3795e2b3fbb8551"


IMAGE_FEATURES ?= ""
EXTRA_IMAGE_FEATURES ?= ""

python () {
    section = {}
    debootstrap = ''
    aptsources = ''

    # Retrieve generic multistrap config info
    sections = d.getVar('MULTISTRAP_SECTIONS',True).split()
    for s in sections:
        section[s] = {}
        section[s]['PACKAGES'] = d.getVar( 'MULTISTRAP_PACKAGES_' + s, True )
        if d.getVar( 'MULTISTRAP_BUILD_' +s, True) == '1':
            section[s]['BUILD'] = True
        else:
            section[s]['BUILD'] = False
    default_section = d.getVar('MULTISTRAP_DEFAULT_SECTION',True)
    if default_section not in sections:
        bb.fatal ('The specified MULTISTRAP_DEFAULT_SECTION (%s) is not a defined section in MULTISTRAP_SECTIONS (%s)' % (default_section,sections))

    # Compile a feature list.
    feature_list = []
    for var in d:
        if var.startswith("PACKAGE_GROUP_"):
           # Strip off prefix and add to feature list.
           feature_list.append(var[14:])

    features_to_install = []
    # Check for IMAGE_FEATURES
    image_features = d.getVar('IMAGE_FEATURES', True).split()
    for feature in image_features:
        if feature in feature_list:
           features_to_install.append(feature)
        else:
           bb.fatal ('The specified feature from IMAGE_FEATURES (%s) is not a defined PACKAGE_GROUP.' % (feature))

    # Check for EXTRA_IMAGE_FEATURES
    extra_image_features = d.getVar('EXTRA_IMAGE_FEATURES', True).split()
    for feature in extra_image_features:
        if feature in feature_list:
           features_to_install.append(feature)
        else:
           bb.fatal ('The specified feature from EXTRA_IMAGE_FEATURES (%s) is not a defined PACKAGE_GROUP.' % (feature))

    # Get package specific multistrap info and dependencies
    deps = ""
    for var in d:
        if var.startswith("PACKAGE_GROUP_"):
            # Strip off prefix
            package = var[14:]
            package_list = d.getVar( var )
            if package in features_to_install:
                s = d.getVar('MULTISTRAP_SECTION_'+package)
                if s:
                    if not section[s]:
                        bb.fatal ('The specified MULTISTRAP_SECTION (%s) for PACKAGE_GROUP (%s) does not exist.' % (s, package))
                    if section[s]['PACKAGES']:
                        section[s]['PACKAGES'] += ' ' + package_list
                    else:
                        section[s]['PACKAGES'] = package_list
                else:
                    if section[default_section]['PACKAGES']:
                        section[default_section]['PACKAGES'] += ' ' + package_list
                    else:
                        section[default_section]['PACKAGES'] = package_list

    # Set up the build dependences
    for s in sections:
    	if section[s]['BUILD'] and section[s]['PACKAGES']:
            package_list_split = section[s]['PACKAGES'].split()
            for pkg in package_list_split:
                deps += " %s:do_package_write_deb" % pkg
    d.appendVarFlag('do_rootfs', 'depends', deps)
}

python do_write_multistrap_conf() {
    section = {}
    debootstrap = ''
    aptsources = ''

    # Retrieve generic multistrap config info
    sections = d.getVar('MULTISTRAP_SECTIONS',True).split()
    for s in sections:
        section[s] = {}
        section[s]['SOURCE'] = d.getVar( 'MULTISTRAP_SOURCE_' + s, True )
        section[s]['SUITE'] = d.getVar( 'MULTISTRAP_SUITE_' + s, True )
        section[s]['COMPONENTS'] = d.getVar( 'MULTISTRAP_COMPONENTS_' + s, True )
        section[s]['PACKAGES'] = d.getVar( 'MULTISTRAP_PACKAGES_' + s, True )
        if d.getVar( 'MULTISTRAP_BUILD_' +s, True) == '1':
            section[s]['BUILD'] = True
        else:
            section[s]['BUILD'] = False
        if d.getVar( 'MULTISTRAP_DEBOOTSTRAP_' + s, True ) == '1':
            debootstrap += ' ' + s
        if d.getVar( 'MULTISTRAP_APTSOURCES_' + s, True ) == '1':
            aptsources += ' ' + s

    default_section = d.getVar('MULTISTRAP_DEFAULT_SECTION',True)
    if default_section not in sections:
        bb.fatal ('The specified MULTISTRAP_DEFAULT_SECTION (%s) is not a defined section in MULTISTRAP_SECTIONS (%s)' % (default_section,sections))

    # Compile a feature list.
    feature_list = []
    for var in d:
        if var.startswith("PACKAGE_GROUP_"):
           # Strip off prefix and add to feature list.
           feature_list.append(var[14:])

    features_to_install = []
    # Check for IMAGE_FEATURES
    image_features = d.getVar('IMAGE_FEATURES', True).split()
    for feature in image_features:
        if feature in feature_list:
           features_to_install.append(feature)
        else:
           bb.fatal ('The specified feature from IMAGE_FEATURES (%s) is not a defined PACKAGE_GROUP.' % (feature))

    # Check for EXTRA_IMAGE_FEATURES
    extra_image_features = d.getVar('EXTRA_IMAGE_FEATURES', True).split()
    for feature in extra_image_features:
        if feature in feature_list:
           features_to_install.append(feature)
        else:
           bb.fatal ('The specified feature from EXTRA_IMAGE_FEATURES (%s) is not a defined PACKAGE_GROUP.' % (feature))

    # Get package specific multistrap info and dependencies
    deps = ""
    for var in d:
        if var.startswith("PACKAGE_GROUP_"):
            # Strip off prefix
            package = var[14:]
            package_list = d.getVar( var )
            if package in features_to_install:
                s = d.getVar('MULTISTRAP_SECTION_'+package)
                if s:
                    if not section[s]:
                        bb.fatal ('The specified MULTISTRAP_SECTION (%s) for PACKAGE_GROUP (%s) does not exist.' % (s, package))
                    if section[s]['PACKAGES']:
                        section[s]['PACKAGES'] += ' ' + package_list
                    else:
                        section[s]['PACKAGES'] = package_list
                else:
                    if section[default_section]['PACKAGES']:
                        section[default_section]['PACKAGES'] += ' ' + package_list
                    else:
                        section[default_section]['PACKAGES'] = package_list

    # Write out the multistrap.conf
    mf = open(d.getVar('WORKDIR',True) + '/multistrap.conf', 'w')
    mf.write('# This multistrap.conf file is generated by the multistrap-image.bbclass\n\n\n')

    # Get the general section paramaters
    mf.write('[General]\n')
    for var in d:
        if var.startswith("MULTISTRAP_GENERAL_"):
            param = var[19:]
            mf.write('%s=%s\n' % (param,d.getVar(var)))

    # write out debootstrap and aptsources
    mf.write('debootstrap=%s\n' % debootstrap )
    mf.write('aptsources=%s\n' % aptsources )

    # write out each section
    for s in sections:
        mf.write('\n[%s]\n' % s )
        if section[s]['SOURCE']:
             mf.write('source=%s\n' % section[s]['SOURCE'] )
        if section[s]['SUITE']:
             mf.write('suite=%s\n' % section[s]['SUITE'] )
        if section[s]['COMPONENTS']:
             mf.write('components=%s\n' % section[s]['COMPONENTS'] )
        if section[s]['PACKAGES']:
             mf.write('packages=%s\n' % section[s]['PACKAGES'] )
        bb.debug(2,'construct_multistrap_conf:section=' + str(s))
}

do_write_multistrap_conf[dirs] = "${TOPDIR} ${WORKDIR}"

addtask write_multistrap_conf before do_rootfs after do_fetch

# Images are generally built explicitly, do not need to be part of world.
EXCLUDE_FROM_WORLD = "1"
# FIXME:ranand Disabled for now
#do_rootfs[dirs] = "${TOPDIR} ${WORKDIR}/intercept_scripts"
#do_rootfs[lockfiles] += "${IMAGE_ROOTFS}.lock"
#do_rootfs[cleandirs] += "${S} ${WORKDIR}/intercept_scripts"
#do_rootfs[deptask] += "do_package_write_deb"
#do_rootfs[rdeptask] += "do_package_write_deb"

# Must call real_do_rootfs() from inside here, rather than as a separate
# task, so that we have a single fakeroot context for the whole process.
#do_rootfs[umask] = "022"

#fakeroot do_rootfs() {
#        if [ -e ${IMAGE_ROOTFS} ]; then
#            rm -rf ${IMAGE_ROOTFS}
#        fi
#        if [ -e ${WORKDIR}/${MACHINE} ]; then
#            rm -rf ${WORKDIR}/${MACHINE}
#        fi
#        install -d ${IMAGE_ROOTFS}
#        install -d ${WORKDIR}/${MACHINE}
#
#        # Do any preprocessing for multistrap
#        ${MULTISTRAP_PREPROCESS_COMMAND}
#
#        # Construct the user space.
#        APT_CONFIG=${WORKDIR}/apt.conf /usr/sbin/multistrap -f ${WORKDIR}/multistrap.conf -d ${IMAGE_ROOTFS} --tidy-up --source-dir ${WORKDIR}/${MACHINE}
#
#        # Create the image directory
#        mkdir -p ${DEPLOY_DIR_IMAGE}
#
#        ${IMAGE_PREPROCESS_COMMAND}
#
#        ${@get_imagecmds(d)}
#
#        ${IMAGE_POSTPROCESS_COMMAND}
#
#        ${MACHINE_POSTPROCESS_COMMAND}
#
#}

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
do_populate_sysroot[noexec] = "1"
do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_rpm[noexec] = "1"

addtask rootfs before do_build

DEPENDS += "e2fsprogs-native"
