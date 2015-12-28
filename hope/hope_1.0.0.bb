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
