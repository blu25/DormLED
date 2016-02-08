#include <digitalWriteFast.h>
#include <Adafruit_NeoPixel.h>

#define LEDPIN 6
#define PIXELS 100
Adafruit_NeoPixel strip = Adafruit_NeoPixel(PIXELS, LEDPIN, NEO_RGB + NEO_KHZ800);

#define PBUTTON 10
#define NBUTTON 11

boolean serialOn = false;

struct Color {
  int r;
  int g;
  int b;
  int a;
  boolean on;
};

Color disp[PIXELS];

void setup() {
  Serial.begin(115200);
  strip.begin();
  sweepDeadSquirrelsFromLights();
  setupStars();
  applyTransition(1);
}

void loop() {
  if (!serialOn) {
    checkButton();
    transitionLoop();
    animateDisplay();
  }


}

// purpose: updates all of the LED pixels based on the data
//          stored in the pixel array
void setLights() {
  for (int i = 0; i < PIXELS; i++)
    strip.setPixelColor(i, getColor(i));
  strip.show();
}
