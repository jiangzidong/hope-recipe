# hope-recipe
hope recipe for building edison image

1. wget http://downloadmirror.intel.com/24910/eng/edison-src-ww18-15.tgz
2. untar
3. proxy: git config --global url.https://.insteadOf git://   
4. /edison-src/meta-intel-edison$ vi setup.sh
line 158
`git clone --mirror http://git.yoctoproject.org/git/$1 $1-mirror.git`
5. make setup
6. /edison-src/out/linux64/poky/meta-intel-iot-middleware/recipes-devtools/nodejs edit the bb file

  ```
LIC_FILES_CHKSUM = "file://LICENSE;md5=96aa1ac122c41f8c08a0683d4b2126b5"

DEPENDS = "openssl"
DEPENDS_class-target = "nodejs-native"

SRC_URI = "http://nodejs.org/dist/v4.4.4/node-v4.4.4.tar.gz"

SRC_URI[md5sum] = "1a0f41618b8290a9e96a5dc5d53c7b9d"
SRC_URI[sha256sum] = "53c694c203ee18e7cd393612be08c61ed6ab8b2a165260984a99c014d1741414"


INSANE_SKIP_${PN} = "installed-vs-shipped "
S = "${WORKDIR}/node-v4.4.4"

  ```
7. /edison-src/out/linux64/poky/meta-intel-iot-middleware/recipes-devtools/mraa

```
LIC_FILES_CHKSUM = "file://COPYING;md5=66493d54e65bfc12c7983ff2e884f37f"

# git is required to get a good version from git describe
DEPENDS = "nodejs swig-native"

SRC_URI = "git://github.com/intel-iot-devkit/mraa.git;protocol=git;rev=3969af2b244f250245ae14e7f090fdd2d94e9858;nobranch=1"

S = "${WORKDIR}/git"
INSANE_SKIP_${PN} += "rpaths"
```
8. remove iotkit-xxx, upm, xdk-deamon in /edison-src/out/linux64/poky/meta-intel-iot-middleware/recipes-devtools
and remove those pakage in edison-src /meta-intel-edison/meta-intel-edison-distro/recipes-core/images/edison-image.bb file
9. add x11 support  ~/edison-src/meta-intel-edison/meta-intel-edison-distro/conf/distro/poky-edison.conf
`add x11 in DISTRO_FEATURES`
10. add hope recipe in /home/jzd/edison-src/out/linux64/poky/meta-intel-iot-middleware/recipes-devtools/hope

  ```
.
├── files
│   └── hope_lib
│       ├── libnspr4.so
│       ├── libnss3.so
│       ├── libnssutil3.so
│       ├── libplc4.so
│       ├── libplds4.so
│       ├── libsmime3.so
│       ├── LICENSE
│       └── nss
│           ├── libfreebl3.chk
│           ├── libfreebl3.so
│           ├── libnssckbi.so
│           ├── libnssdbm3.chk
│           ├── libnssdbm3.so
│           ├── libnsssysinit.so
│           ├── libsoftokn3.chk
│           └── libsoftokn3.so
├── hope_1.0.0.bb
  ```
  ```
LICENSE = "hope"
LIC_FILES_CHKSUM = "file://hope_lib/LICENSE;md5=d41d8cd98f00b204e9800998ecf8427e"
S="${WORKDIR}"
SRC_URI="file://hope_lib"
#INSANE_SKIP_${PN} += "installed-vs-shipped"
INSANE_SKIP_${PN} += "already-stripped"
#ALLOW_EMPTY_${PN} = "1"
do_install(){
    install -d ${D}${libdir}
    install -d ${D}${libdir}/nss
    install -c ${S}/hope_lib/*.so ${D}${libdir}
    install -c ${S}/hope_lib/nss/* ${D}${libdir}/nss
}
PACKAGES =+ "libhope"
FILES_libhope = "${libdir}/* ${libdir}/nss/*"
  ```
11. add webrtc related libs
~/edison-src/meta-intel-edison/meta-intel-edison-distro/recipes-core/images/edison-image.bb

  ```
IMAGE_INSTALL += "libx11"
IMAGE_INSTALL += "libxext"
IMAGE_INSTALL += "libxcomposite"
IMAGE_INSTALL += "libxrender"

IMAGE_INSTALL += "libhope"
  ```
12. make image
13. if no mkimage in the end

  ```
sudo apt-get install u-boot-tools
jzd@jzd:~/edison-src/out/current/build/tmp/work/edison-poky-linux/u-boot$ ln -s /usr/bin/mkimage mkimage
  ```
