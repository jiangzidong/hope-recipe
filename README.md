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
LIC_FILES_CHKSUM = "file://LICENSE;md5=14115ff11211df04b031ec7d40b6d31b"

DEPENDS = "openssl"
DEPENDS_class-target = "nodejs-native"

SRC_URI = "http://nodejs.org/dist/v0.12.0/node-v0.12.0.tar.gz"

RC_URI[md5sum] = "62c8d9c74c8f68193f60e4cba020eb48"
SRC_URI[sha256sum] = "9700e23af4e9b3643af48cef5f2ad20a1331ff531a12154eef2bfb0bb1682e32"

INSANE_SKIP_${PN} = "installed-vs-shipped "
S = "${WORKDIR}/node-v0.12.0"
  ```
7. /edison-src/out/linux64/poky/meta-intel-iot-middleware/recipes-devtools/mraa
`SRC_URI = "git://github.com/intel-iot-devkit/mraa.git;protocol=git;rev=ea183b37388d96d51ab8cb64712259e86a03c465;nobranch=1"`
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
