#!/bin/sh

set -e

umask 002

#echo "HOME=$HOME"
#echo "VISUALIZER_HOME=$VISUALIZER_HOME"
#echo "VISUALIZER_CONFIG=$VISUALIZER_CONFIG"
#echo "VISUALIZER_TEMPLATES=$VISUALIZER_TEMPLATES"
#echo "VISUALIZER_OUTPUT=$VISUALIZER_OUTPUT"
#echo "VISUALIZER_OPTS=$VISUALIZER_OPTS"
#
#ls -l $VISUALIZER_HOME
#ls -ld $VISUALIZER_HOME/visualizer
#ls -ld $VISUALIZER_HOME/config
#ls -ld $VISUALIZER_HOME/templates

#echo "Rendering..."

$VISUALIZER_HOME/visualizer/bin/visualizer $VISUALIZER_OUTPUT $VISUALIZER_TEMPLATES

#echo "Uploading..."

rsync -vv -rltp --chmod=g+r,o+r $VISUALIZER_OUTPUT/ $VISUALIZER_REMOTE

#echo "Finished uploading"
