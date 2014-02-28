#!/bin/sh

if [ -z "$1" ]; then
   echo $0 mac-address softmac.bin
   echo $0 00:50:c2:F3:9D:87 softmac.bin
   exit
fi

echo $1 | sed 's/://g' | xxd -r -p > $2


