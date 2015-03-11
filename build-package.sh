#!/bin/bash
BHL_DIR=`ls -d bhlpages_1*/ | sed -e "s:/::"`
cd $BHL_DIR
find . -type f ! -regex '.*.hg.*' ! -regex '.*?debian-binary.*' \
! -regex '.*?DEBIAN.*' -printf '%P ' | xargs md5sum > DEBIAN/md5sums
cd ..
if [ -e dist/BHLPages.jar ]; then
  cp -p dist/BHLPages.jar "$BHL_DIR/usr/local/bin/bhlpages/"
  chmod +x "$BHL_DIR/usr/local/bin/bhlpages/BHLPages.jar"
fi
rsync -az ./lib/ "./$BHL_DIR/usr/local/bin/bhlpages/lib"
dpkg-deb --build "$BHL_DIR"
