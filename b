#!/bin/sh -e
basedir=`dirname $0`
version=`${basedir}/version`
echo building version ${version}
${basedir}/gradlew -Pversion=${version} "$@"
