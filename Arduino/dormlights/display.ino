// purpose: calls the necessary functions to get the lights to animate
//          doesn't call animation if plugged into a computer, since serialEvent()
//          sets animationState to -1 if plugged in
void animateDisplay() {
  applyAlpha();
  
  if (animationState >= 0) {
    applyAnimation();
    setLights();
    
    delay(10);
  }

}

// purpose: animates fading in and out for each of the lights
//          based on whether or not the light should be on (on) and
//          what the current value for on-ness is (a = alpha)
void applyAlpha() {
  for (int i=0; i<PIXELS; i++) {
    if (disp[i].a < 100 && disp[i].on)
      disp[i].a+=5;

    if (disp[i].a > 0 && !disp[i].on)
      disp[i].a-=5;
  }
}

// purpose: sets all of the pixel colors to black, turning the lights off
void sweepDeadSquirrelsFromLights() {
  solidColor(0, 0, 0);
  for (int i=0; i<PIXELS; i++) {
    disp[i].a = 0;
    disp[i].on = false;
  }
}

// purpose: gets the color at the specified point
//  inputs: the pixel to get the color from (integer)
// outputs: the RGB color as an integer
uint32_t getColor(int i) {
  return ((uint32_t)disp[i].r*disp[i].a/100 << 16)
    | ((uint32_t)disp[i].g*disp[i].a/100 <<  8)
      | disp[i].b*disp[i].a/100;
}


