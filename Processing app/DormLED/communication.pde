import processing.serial.*;

Serial myPort;  // Create object from Serial class
int timeout;
int portToTry = 2;

void updateLEDs() {
  connectToArduino();

  try {
    for (int i=0; i<num_pixels; i++) {
      myPort.write(min((int)red(pixs[i]), 254));
      myPort.write(min((int)green(pixs[i]), 254));
      myPort.write(min((int)blue(pixs[i]), 254));
    }
    myPort.write(255);
  } 
  catch (Exception e) {
    println("well shit");
  }
}

void connectToArduino() {
  timeout--;
  
  if (timeout <= 0) {
    timeout = 30;
    try {
      myPort = new Serial(this, Serial.list()[portToTry], 115200);
    } 
    catch (Exception e) {
      timeout = 0;
    }

    portToTry++;
    if (portToTry >= Serial.list().length) {
      portToTry = 0;
    }
  }
}

void serialEvent(Serial p) { 
  timeout = 30;
} 