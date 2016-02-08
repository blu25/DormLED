int8_t animationState = 0;

// purpose: calls the appropriate animation based on the current animation state
void applyAnimation() {
  switch (animationState) {
    case 0:
      solidColor(0, 0, 0);
      break;
    case 1: //solid
      solidColor(255, 150, 50);
      break;
    case 2:
      ripples(255, 64, 0);
      break;
    case 3:
      stars(112,255,123);
      break;
    case 4:
      rainbow();
      break;
  }
}

// purpose: sets all the pixels to the specified RGB color
//  inputs: the red, green, and blue values as integers
// outputs: none
void solidColor(int r, int g, int b) {
  for (int i = 0; i < PIXELS; i++) {
    disp[i].r = r;
    disp[i].g = g;
    disp[i].b = b;
  }
}

// purpose: makes the pixels display a rainbow wipe animation,
//          ripped directly from the Neopixel strandtest program
int rainbowTime = 0;
void rainbow() {
  uint16_t i, WheelPos;
  for (i = 0; i < PIXELS; i++) {
    WheelPos = (((i * 256 / PIXELS) + rainbowTime) & 255);
    if (WheelPos < 85) {
      disp[i].r = WheelPos * 3;
      disp[i].g = 255 - WheelPos * 3;
      disp[i].b = 0;
    }
    else if (WheelPos < 170) {
      WheelPos -= 85;
      disp[i].r = 255 - WheelPos * 3;
      disp[i].g = 0;
      disp[i].b = WheelPos * 3;
    }
    else {
      WheelPos -= 170;
      disp[i].r = 0;
      disp[i].g = WheelPos * 3;
      disp[i].b = 255 - WheelPos * 3;
    }
  }

  rainbowTime--;
  if (rainbowTime < 0)
    rainbowTime = 255;
}


// Stars Section
#define NUM_STARS 70

// Twinkle is a particle used to show the stars
struct Twinkle {
  int x;
  uint8_t shine;
};

Twinkle star[NUM_STARS];

// purpose: prewarms the star particles
void setupStars() {
  for (int i = 0; i < NUM_STARS; i++) {
    star[i].shine = random(0, 200);
    star[i].x = random(0, PIXELS);
  }
}

// purpose: runs the twinkle animation
void stars(int r, int g, int b) {
  solidColor(0, 0, 0); //clear canvas
  for (int i = 0; i < NUM_STARS; i++) {

    // amount is the current brightness of the twinkle particle
    int amount = min(disp[star[i].x].b + (100 - abs(star[i].shine - 100)) / 2, 255);
    disp[star[i].x].r = max(amount*(r/255.), disp[star[i].x].r);
    disp[star[i].x].g = max(amount*(g/255.), disp[star[i].x].g);
    disp[star[i].x].b = max(amount*(b/255.), disp[star[i].x].b);

    star[i].shine++;

    // when the star is done animating, set it to a new position and reset the shine value
    if (star[i].shine >= 200) {
      star[i].shine = 0;
      star[i].x = random(0, PIXELS);
    }
  }
}


// Ripples Section
float rippleTime = 20;
uint8_t brightness;

// purpose: animates the ripple animation
//  inputs: the red, green, and blue values to set as the color (int)
void ripples(int r, int g, int b) {
  for (int i = 0; i < PIXELS; i++) {
    
    // the pixel pulsates based on the current tick (stored in rippleTime)
    // this is a lot faster than using a sine function
    if ((int(rippleTime) + i) % 10 >= 5) {
      brightness = int(8 * (rippleTime + i)) % 40;
    } else {
      brightness = (40 - int(8 * (rippleTime + i)) % 40);
    }

    brightness *= 6;
    brightness += 10; // scales the brightness for the 8bit brightness color value

    disp[i].r = brightness * (r / 255.0);
    disp[i].g = brightness * (g / 255.0);
    disp[i].b = brightness * (b / 255.0);

  }
  if (rippleTime >= 30) { // prevents eventual overflow errors
    rippleTime -= 10;
  }
  rippleTime += 0.1;
}
