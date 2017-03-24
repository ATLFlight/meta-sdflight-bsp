DESCRIPTION = "ros-sdflight package group"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "${PN}"

RDEPENDS_${PN} = "\
    packagegroup-ros-comm \
    actionlib \
    bond \
    bondcpp \
    bondpy \
    smclib \
    class-loader \
    actionlib-msgs \
    diagnostic-msgs \
    nav-msgs \
    geometry-msgs \
    sensor-msgs \
    shape-msgs \
    stereo-msgs \
    trajectory-msgs \
    visualization-msgs \
    dynamic-reconfigure \
    tf2 \
    tf2-msgs \
    tf2-py \
    tf2-ros \
    tf \
    image-transport \
    nodelet-topic-tools \
    nodelet \
    pluginlib \
    cmake-modules \
    rosconsole-bridge \
    "
