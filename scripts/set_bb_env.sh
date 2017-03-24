#!/bin/bash
rebake() {
  bitbake -c cleanall $@ && bitbake $@
}

build-sdflight(){
    bitbake boot-image     && \
    bitbake cache-image    && \
    bitbake persist-image  && \
    bitbake userdata-image

    # Unsupported
    #bitbake factory-image  && \
    #bitbake recovery-image && \
}

scriptdir=
if [[ -z ${BASH} ]]
then
   scriptdir="$(dirname $0)"
else
   scriptdir="$(dirname "${BASH_SOURCE}")"
fi
WS=$(readlink -f $scriptdir/../..)

export TEMPLATECONF=${WS}/meta-sdflight-bsp/conf 

if [ "$1" = "--prebuild" ]; then
build-prebuilts(){
    bitbake packagegroup-qti-prebuilt
}
    source ${WS}/oe-init-build-env build &&  \
    sed -i 's/meta-qti-repackage/meta-qti/' conf/bblayers.conf
else
    source ${WS}/oe-init-build-env build
fi
