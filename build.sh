#!/usr/bin/env bash
bDATE=$(date '+%Y%m%d%H%M%S')
bDIR=$(dirname $0)
bDIR=$(cd $bDIR && pwd)

#OPTS="-DskipTests=true -DproxySet=true -DproxyHost=127.0.0.1 -DproxyPort=3128 -Dhttps.nonProxyHosts=127.0.0.1"
OPTS="-DskipTests=true"

while test ! -z "$1" ; do
  case "$1" in
    -major*)
      (cd $bDIR && mvn build-helper:parse-version versions:set \
                -DnewVersion="\${parsedVersion.nextMajorVersion}.1.\${parsedVersion.nextIncrementalVersion}" ) || exit 1
      ;;
    -minor*)
      (cd $bDIR && mvn build-helper:parse-version versions:set \
                -DnewVersion="\${parsedVersion.majorVersion}.\${parsedVersion.nextMinorVersion}.\${parsedVersion.nextIncrementalVersion}" ) || exit 1
      ;;
    -release*)
      (cd $bDIR && mvn -X build-helper:parse-version versions:set \
                -DnewVersion="\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}" ) || exit 1
      ;;
    -clean*)
      (cd $bDIR && mvn clean $OPTS -U) || exit 1
      ;;
    -pack*)
      (cd $bDIR && mvn clean package $OPTS -U) || exit 1
      ;;
    -fix*)
      (cd $bDIR && mvn -N versions:update-child-modules) || exit 1
      ;;
    -install*)
      (cd $bDIR && mvn install $OPTS) || exit 1
      ;;
    -deps)
      (cd $bDIR && mvn org.apache.maven.plugins:maven-dependency-plugin:tree) || exit 1
      ;;
    *) echo "unknow option ..."
      ;;
  esac
  shift
done