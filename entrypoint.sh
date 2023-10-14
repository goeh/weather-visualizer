#!/bin/sh

set -e

mkdir -p $HOME/.ssh
chmod 755 $HOME/.ssh
cp $VISUALIZER_CONFIG/id_rsa* $HOME/.ssh
cp $VISUALIZER_CONFIG/known_hosts $HOME/.ssh
chmod 600 $HOME/.ssh/id_rsa
chmod 644 $HOME/.ssh/id_rsa.pub
chmod 644 $HOME/.ssh/known_hosts

env >> /etc/environment

exec "$@"
