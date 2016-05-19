DESCRIPTION = "nodeJS Evented I/O for V8 JavaScript"
HOMEPAGE = "http://nodejs.org"
LICENSE = "MIT & BSD-2-Clause & BSD-3-Clause & BSD-4-Clause & ISC & GPLv2 & GPLv3 & AFL-2.0 & GPL-2.0-with-OpenSSL-exception & Zlib"


LIC_FILES_CHKSUM = "file://LICENSE;md5=96aa1ac122c41f8c08a0683d4b2126b5"

DEPENDS = "openssl"
DEPENDS_class-target = "nodejs-native"

SRC_URI = "http://nodejs.org/dist/v4.4.4/node-v4.4.4.tar.gz"

SRC_URI[md5sum] = "1a0f41618b8290a9e96a5dc5d53c7b9d"
SRC_URI[sha256sum] = "53c694c203ee18e7cd393612be08c61ed6ab8b2a165260984a99c014d1741414"


INSANE_SKIP_${PN} = "installed-vs-shipped "
S = "${WORKDIR}/node-v4.4.4"

# v8 errors out if you have set CCACHE
CCACHE = ""

ARCHFLAGS_arm = "${@bb.utils.contains('TUNE_FEATURES', 'callconvention-hard', '--with-arm-float-abi=hard', '--with-arm-float-abi=softfp', d)}"
ARCHFLAGS ?= ""

# Node is way too cool to use proper autotools, so we install two wrappers to forcefully inject proper arch cflags to workaround gypi
do_configure () {
    export LD="${CXX}"

    ./configure --prefix=${prefix} ${ARCHFLAGS}
}

do_compile () {
    export LD="${CXX}"
    make BUILDTYPE=Release
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

do_install_append_class-target () {
    # install node-gyp node hedaers in /usr/include/node-gyp/
    cd ${D}/${libdir}/node_modules/npm/node_modules/node-gyp/
    export HOME=${D}/usr/include/node-gyp
    sed -i 's/\.node-gyp//' lib/node-gyp.js

    # configure http proxy if neccessary
    if [ -n "${http_proxy}" ]; then
        ${STAGING_BINDIR_NATIVE}/node bin/node-gyp.js --verbose --proxy=${http_proxy} install
    elif [ -n "${HTTP_PROXY}" ]; then
        ${STAGING_BINDIR_NATIVE}/node bin/node-gyp.js --verbose --proxy=${HTTP_PROXY} install
    else
        ${STAGING_BINDIR_NATIVE}/node bin/node-gyp.js --verbose install
    fi
}

do_install_append_class-native() {
    # /usr/bin/npm is symlink to /usr/lib/node_modules/npm/bin/npm-cli.js
    # use sed on npm-cli.js because otherwise symlink is replaced with normal file and
    # npm-cli.js continues to use old shebang
    sed "1s^.*^#\!/usr/bin/env node^g" -i ${D}${libdir}/node_modules/npm/bin/npm-cli.js
}

do_install_append_class-target() {
    sed "1s^.*^#\!${bindir}/env node^g" -i ${D}${libdir}/node_modules/npm/bin/npm-cli.js
}

pkg_postinst_${PN} () {
    sed -e '/^PATH=/aNODE_PATH=\/usr\/lib\/node_modules\/' \
        -e 's/\(^export\)\(.*\)/\1 NODE_PATH\2/' \
        -i $D/etc/profile
}

pkg_prerm_${PN} () {
    sed -e '/^NODE_PATH.*$/d' \
        -e 's/\(^export\)\(.*\)\(\<NODE_PATH\>\s\)\(.*\)/\1\2\4/' \
        -i $D/etc/profile
}

RDEPENDS_${PN} = "curl"
RDEPENDS_${PN}_class-native = ""

PACKAGES += "${PN}-npm"
FILES_${PN}-npm = "${libdir}/node_modules ${bindir}/npm"
RDEPENDS_${PN}-npm = "python-shell python-datetime python-subprocess python-crypt python-textutils \
                      python-netclient python-ctypes python-misc python-compiler python-multiprocessing"

BBCLASSEXTEND = "native"
