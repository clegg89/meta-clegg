SUMMARY = "Application to continuously blink an LED"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

RDEPENDS_${PN} += "python rpi-gpio"

SRC_URI = "file://blink.py \
           file://daemon.py \
           file://init"

S = "${WORKDIR}"

inherit update-rc.d

INITSCRIPT_NAME = "blink"
INITSCRIPT_PARAMS = "defaults"

do_install() {
  install -m 0755 -d ${D}${sbindir} \
    ${D}${sbindir}/blink.d \
    ${D}${sysconfdir}/init.d

  install -m 0755 ${S}/blink.py ${D}${sbindir}/blink.d
  install -m 0755 ${S}/daemon.py ${D}${sbindir}/blink.d

  sed -e 's,/etc,${sysconfdir},g' \
    -e 's,/usr/sbin,${sbindir},g' \
    -e 's,/var,${localstatedir},g' \
    -e 's,/usr/bin,${bindir},g' \
    -e 's,/usr,${prefix},g' ${WORKDIR}/init > ${D}${sysconfdir}/init.d/blink

  chmod 755 ${D}${sysconfdir}/init.d/blink
}

pkg_postinst_${PN}() {
  ln -s ${sbindir}/blink.d/blink.py ${sbindir}/blink
}

pkg_postrm_${PN}() {
  if [ -L "${sbindir}/blink" ]; then
    rm ${sbindir}/blink
  fi
  if [ -d "${sbindir}/blink.d" ]; then
    rm -r ${sbindir}/blink.d
  fi
}

COMPATIBLE_MACHINE = "raspberrypi"
