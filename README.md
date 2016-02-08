# DormLED
This is the code for an Arduino-powered christmas lights system. I used WS2811 pixels for the LEDs but anything that is compatible with Adafruit's NeoPixel library will also work.

# Programs
Arduino program: This program can be loaded onto an Arduino and run by itself. The program reads two capacitive buttons which are used to cycle through a series of 4 light animations.
Processing applet: This program can be run on a computer with Processing installed. It processes audio data from the computer's microphone, converts it into light data, and then sends the light data over to the Arduino via serial communication.
