#!/bin/sh

set -e

umask 002

echo "HOME=$HOME"
echo "VISUALIZER_HOME=$VISUALIZER_HOME"
echo "VISUALIZER_CONFIG=$VISUALIZER_CONFIG"
echo "VISUALIZER_TEMPLATES=$VISUALIZER_TEMPLATES"
echo "VISUALIZER_OUTPUT=$VISUALIZER_OUTPUT"
echo "VISUALIZER_OPTS=$VISUALIZER_OPTS"

ls -l $VISUALIZER_HOME
ls -ld $VISUALIZER_HOME/visualizer
ls -ld $VISUALIZER_HOME/config
ls -ld $VISUALIZER_HOME/templates

$VISUALIZER_HOME/visualizer/bin/visualizer $VISUALIZER_OUTPUT $VISUALIZER_TEMPLATES

mkdir -p $HOME/.ssh
chmod 755 $HOME/.ssh
cp $VISUALIZER_CONFIG/id_rsa* $HOME/.ssh
cp $VISUALIZER_CONFIG/known_hosts $HOME/.ssh
chmod 600 $HOME/.ssh/id_rsa
chmod 644 $HOME/.ssh/id_rsa.pub
chmod 644 $HOME/.ssh/known_hosts

ls -l $HOME/.ssh

echo "Uploading..."

rsync -vv -rltp --chmod=g+r,o+r $VISUALIZER_OUTPUT/ $VISUALIZER_REMOTE

echo "Finished uploading, waiting..."

sleep 300

echo "Done"

exit 0
