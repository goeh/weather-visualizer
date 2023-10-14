#!/bin/sh

set -e

umask 002

$VISUALIZER_HOME/visualizer/bin/visualizer $VISUALIZER_OUTPUT $VISUALIZER_TEMPLATES

rsync -rltp --chmod=g+r,o+r $VISUALIZER_OUTPUT/ $VISUALIZER_REMOTE
