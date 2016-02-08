uint8_t serialTimeout = 0;
int pointer = 0;
byte data;

//boolean checkSerial() {
//  if (serialTimeout > 0) {
//    serialTimeout--;
//    return true;
//  } else {
//    return false;
//  }
//}

void serialEvent() {
  serialTimeout = 100;
  serialOn = true;
  if (animationState != -1) {
    for (int i=0; i<PIXELS; i++) {
      swipeTransition = false;
      fadeTransition = false;
      transitionPosition = 0;
      disp[i].a = 100;
      disp[i].on = true;
    }
    animationState = -1;
  }
  while (Serial.available()) {
    data = Serial.read();

    if (data == 255) {
      endOfMessage();
      return;
    }
    
    Serial.write(255);

    switch (pointer%3) {
    case 0:
      disp[pointer/3].r = data;
      break;
    case 1:
      disp[pointer/3].g = data;
      break;
    case 2:
      disp[pointer/3].b = data;
      break;
    }

    pointer++;
  }

}

void endOfMessage() {
  pointer = 0;
  setLights();
}








