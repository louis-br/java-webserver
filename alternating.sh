#!/bin/sh
make
MSGS=${MSGS:-"10"}
LOSS=${LOSS:-"0.0"}
CORRUPTION=${CORRUPTION:-"0.2"}
TIME=${TIME:-"10.0"}
TRACE=${TRACE:-"2"}
printf "$MSGS\n$LOSS\n$CORRUPTION\n$TIME\n$TRACE" | ./alternating