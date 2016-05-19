SUMMARY = "Low Level Skeleton Library for Communication on Intel platforms"
SECTION = "libs"
AUTHOR = "Brendan Le Foll, Tom Ingleby"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=66493d54e65bfc12c7983ff2e884f37f"

# git is required to get a good version from git describe
DEPENDS = "nodejs swig-native"

SRC_URI = "git://github.com/intel-iot-devkit/mraa.git;protocol=git;rev=3969af2b244f250245ae14e7f090fdd2d94e9858;nobranch=1"

S = "${WORKDIR}/git"
INSANE_SKIP_${PN} += "rpaths"

inherit distutils-base pkgconfig python-dir cmake

FILES_${PN}-doc += "${datadir}/mraa/examples/"

FILES_${PN}-dbg += "${libdir}/node_modules/mraajs/.debug/ \
                    ${PYTHON_SITEPACKAGES_DIR}/.debug/"

do_compile_prepend () {
  # when yocto builds in ${D} it does not have access to ../git/.git so git
  # describe --tags fails. In order not to tag our version as dirty we use this
  # trick
  sed -i 's/-dirty//' src/version.c
}
