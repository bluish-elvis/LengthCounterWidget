#!/usr/bin/env bash

if [ "$(uname)" == "Darwin" ]; then
  # Do something under Mac OS X platform
  xattr -w com.dropbox.ignored 1 build
  xattr -w com.dropbox.ignored 1 .gradle
else
  # Do something under GNU/Linux platform
  attr -s com.dropbox.ignored -V 1 build
  attr -s com.dropbox.ignored -V 1 .gradle
fi
