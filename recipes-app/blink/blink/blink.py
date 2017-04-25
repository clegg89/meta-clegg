#!/usr/bin/env python

from daemon import Daemon
import RPi.GPIO as GPIO
import time
import signal
import sys

class Blink(Daemon):
    def __init__(self, pin):
        super(Blink, self).__init__(pidfile='/var/run/blink.pid')
        self.pin = int(pin)

    def cleanup(self):
        GPIO.cleanup()

    def sigterm_handler(self, signal, frame):
        self.daemon_alive = False
        self.cleanup()
        sys.exit(0)

    def blink(self):
        GPIO.output(self.pin, GPIO.HIGH)
        time.sleep(1)
        GPIO.output(self.pin, GPIO.LOW)
        time.sleep(1)

    def run(self):
        signal.signal(signal.SIGTERM, self.sigterm_handler)
        signal.signal(signal.SIGINT, self.sigterm_handler)

        GPIO.setmode(GPIO.BOARD)
        GPIO.setwarnings(False)
        GPIO.setup(self.pin, GPIO.OUT)

        while True:
            self.blink()

if __name__ == "__main__":
    if len(sys.argv) == 2:
        blinky = Blink(sys.argv[1])
        blinky.start()

    else:
        print 'Usage: %s PIN' % sys.argv[0]
