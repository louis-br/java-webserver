#!/bin/sh
make
MSGS=${MSGS:-"10"}
LOSS=${LOSS:-"0.0"}
CORRUPTION=${CORRUPTION:-"0.1"}
TIME=${TIME:-"1000.0"}
TRACE=${TRACE:-"3"}
printf "$MSGS\n$LOSS\n$CORRUPTION\n$TIME\n$TRACE" | ./gobackn