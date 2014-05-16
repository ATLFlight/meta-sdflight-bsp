#!/bin/sh
#################################################################################################
## Author: Christopher Wingert
## 
## This script configures bluetooth during kernel booting sequence.
##
## v0.1: Basic draft
#################################################################################################
SCRIPT_VERSION="v0.1"
echo "BT Init Script: $SCRIPT_VERSION"

/usr/bin/ath3k_hciattach -n /dev/ttyHS0 ath3k 3000000 &

echo "Done."


