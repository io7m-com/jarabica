#!/bin/sh

export ALSOFT_CONF="$GITHUB_WORKSPACE/alsoft.conf"
export ALSOFT_DRIVERS=wave
export ALSOFT_LOGLEVEL=3

exec mvn --errors clean verify site