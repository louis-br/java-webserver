#!/bin/sh
make
MSGS=${MSGS:-"20"}
LOSS=${LOSS:-"0.1"}
CORRUPTION=${CORRUPTION:-"0.1"}
TIME=${TIME:-"1000.0"}
TRACE=${TRACE:-"2"}
printf "$MSGS\n$LOSS\n$CORRUPTION\n$TIME\n$TRACE" | ./alternating