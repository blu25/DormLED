int8_t nextAnimationState;

bool swipeTransition = false;
bool fadeTransition = false;
int transitionPosition = 0;

bool endsOnOn = false;

// purpose: sets up the transition animations to handle switching to
//          the animation it needs to switch to
//  inputs: the value of the new animation (int)
void applyTransition(int newAnimation) {

  // if an transition isn't currently in effect
  if (!swipeTransition && !fadeTransition) {

    // resets the transition timer
    transitionPosition = 0;
    endsOnOn = false;

    // if turning on, swooshify and return early
    if (animationState == 0) {
      swipeTransition = true;
      endsOnOn = true;
      nextAnimationState = newAnimation;
      return;
    }

    // if turning off, swooshify and return early
    if (newAnimation == 0) {
      swipeTransition = true;
      endsOnOn = false;
      nextAnimationState = 0;
      return;
    }

    // if not, set up the fade transition
    fadeTransition = true;
    endsOnOn = false;
    nextAnimationState = newAnimation;
  }
}

// purpose: calls one of the animation controlling functions based
//          on which animation we are trying to call
void transitionLoop() {
  if (swipeTransition)
    swipeLoop();

  if (fadeTransition)
    fadeLoop();
}

// purpose: animates the cool swooshy transition that happens when
//          the user hits the power button on and off
void swipeLoop() {
  if (endsOnOn) // if turning the lights on after they've been off
    animationState = nextAnimationState;

  if (transitionPosition >= PIXELS + 60) { // once done animating
    animationState = nextAnimationState;
    transitionPosition = 0;
    swipeTransition = false;
    return;
  }

  // start fading in a small portion of the lights for every tick
  if (transitionPosition < PIXELS) {
    for (int i = transitionPosition; i < min(transitionPosition + 3, PIXELS); i++)
      disp[i].on = endsOnOn;
  }

  transitionPosition += 3;
}

// purpose: animates the less cool fade out/fade in transition when the
//          user hits the next button
void fadeLoop() {

  // when the lights have fully faded in or out (takes 20 ticks to fully fade)
  if (transitionPosition == 20) {

    transitionPosition = 0;
    animationState = nextAnimationState;

    if (!endsOnOn)
      endsOnOn = true; // if lights are off, fade them back in
    else
      fadeTransition = false; // if lights are on, we're done here

    return;
  }

  for (int i = 0; i < PIXELS; i++)
    disp[i].on = endsOnOn;

  transitionPosition++;
}







