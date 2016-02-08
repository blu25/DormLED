import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.analysis.*; 
import ddf.minim.*; 
import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class DormLED extends PApplet {

int num_pixels = 100;

boolean FFTEnabled = false;
int center;
float blend;

int pixshalf[] = new int[num_pixels];
int pixs[] = new int[num_pixels];

float colors[] = new float[6];
int color1 = color(255, 150, 0);
int color2 = color(0, 180, 255);

public void setup() {
  setupDefaults();

 
  setupInterface();
  setupAudio();
  frameRate(40);
  noStroke();
  textAlign(CENTER);
  connectToArduino();
}

public void setupDefaults() {
  colors[0] = 255;
  colors[1] = 150;
  colors[2] = 0;
  colors[3] = 0;
  colors[4] = 180;
  colors[5] = 255;
}

public void draw() {
  smooth();
  drawInterface();
  updateAudio();

  if (justUpdatedAudio)
    updateLEDs();
}



Minim minim;
AudioInput in;

FFT fftLin;

float sensitivity;

boolean justUpdatedAudio;

public void setupAudio() {
  minim = new Minim(this);
  in = minim.getLineIn(Minim.MONO);
  fftLin = new FFT( in.bufferSize(), in.sampleRate() );
  fftLin.linAverages(256);
}

public void updateAudio() {
  if (!justUpdatedAudio)
    if (FFTEnabled) {
      updateFFT();
    } else {
      updateAmplitude();
    }

  centrifyPixels();
  justUpdatedAudio = !justUpdatedAudio;
}

public void updateAmplitude() {
  float curr_amp = min(in.left.level()*sensitivity, 1);
  for (int i=num_pixels-1; i>0; i--) {
    pixshalf[i] = pixshalf[i-1];
  }
  lerpifyPixel(0, curr_amp);
}

public void updateFFT() {
  fftLin.forward( in.mix );
  for (int i=0; i<100; i++) {
    lerpifyPixel(i, min(fftLin.getAvg(i)*sensitivity/20, 1));
  }
}

public void centrifyPixels() {
  for (int i=0; i<num_pixels; i++) {
    pixs[i] = lerpColor(pixs[i], pixshalf[abs(i-center)], blend);
    if (blend <= 0.5f) {
      pixs[i] = color(red(pixs[i]) - 0.1f, green(pixs[i]) - 0.1f, blue(pixs[i]) - 0.1f);      
    }
    fill(red(pixs[i])*2, green(pixs[i])*2, blue(pixs[i])*2);
    rect(i*width/num_pixels, height-5, 4, 5);
  }
}

public void lerpifyPixel(int i, float amp) {
  if (amp > 0.5f) {
    pixshalf[i] = lerpColor(color1, color2, (amp-0.5f)*2);
  } else {
    pixshalf[i] = lerpColor(color(0), color1, amp*2);
  }

  if (FFTEnabled) {
    pixshalf[i] = blendColor(pixshalf[i], color(pow(amp*255, 2)/255), MULTIPLY);
  } else {
    pixshalf[i] = lerpColor(color(0), pixshalf[i], amp);
  }
}

public void stop()
{
  minim.stop();
  super.stop();
}
class button {
  float x, y, sx, sy, xOff, yOff, wid, hgt;
  boolean isToggle, isOn, isClicked;
  String value, value2;
  int hoverValue, toggleValue;

  button(int px, int py, int pw, int ph, String pv) {
    x = px;
    y = py;
    sx = x;
    sy = y;
    wid = pw;
    hgt = ph;
    value = pv;
    isToggle = false;
  }

  button(int px, int py, int pw, int ph, String pv, String pv2) {
    x = px;
    y = py;
    sx = x;
    sy = y;
    wid = pw;
    hgt = ph;
    value = pv;
    value2 = pv2;
    isToggle = true;
  }

  public void switchValue(boolean nVal) {
    isOn = nVal;
  }

  public void switchName(String nname) {
    value = nname;
  }

  public void setOffset(float px, float py) {
    xOff = px;
    yOff = py;
  }

  public boolean updateButton() {
    x += ((sx-xOff)-x)/5;
    y += ((sy-yOff)-y)/5;

    if (mouseX > x && mouseX < x+wid && mouseY > y && mouseY < y+hgt)
      hoverValue = min(hoverValue+10, 50);
    else
      hoverValue = max(hoverValue-10, 0);

    fill(255, hoverValue);
    rect(x, y, wid, hgt);


    if (isToggle) {
      textFont(buttonFont);

      fill(255, 255-toggleValue);
      text(value, x+wid/2+textWidth(value2)/1.75f, y+hgt/1.6f);

      fill(255, 55+toggleValue);
      text(value2, x+wid/2-textWidth(value)/1.75f, y+hgt/1.6f);

      if (isOn) {
        toggleValue = min(toggleValue+40, 200);
        return true;
      } else {
        toggleValue = max(toggleValue-40, 0);
        return false;
      }
    } else {
      fill(255);
      textFont(buttonFont);
      text(value, x+wid/2, y+hgt/1.6f);

      if (isClicked) {
        isClicked = false;
        return true;
      } else {
        return false;
      }
    }
  }

  public void clickButton() {
    if (mouseX > x && mouseX < x+wid && mouseY > y && mouseY < y+hgt) {
      isClicked = true;
      if (isToggle) {
        isOn = !isOn;
      }
    }
  }
}


Serial myPort;  // Create object from Serial class
int timeout;
int portToTry = 2;

public void updateLEDs() {
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

public void connectToArduino() {
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

public void serialEvent(Serial p) { 
  timeout = 30;
} 
PGraphics bkg;
PFont smallFont;
PFont buttonFont;

PImage addImg;
PImage subImg;
PImage setImg;

slider colsel[] = new slider[6];
slider blendSlide = new slider(213, 72, 150, 1);
button freqButton = new button(192, 0, 192, 48, "volume", "frequency");
presetHolder thePresets;
button topButtons[] = new button[3];

slider centerSlide = new slider(213, 410, 150, 100);
slider sensitSlide = new slider(213, 450, 150, 3);

boolean settingsMode = false;

public void setupInterface() {
  addImg = loadImage("add.png");
  subImg = loadImage("remove.png");
  setImg = loadImage("settings.png");

  bkg = createGraphics(width, height, JAVA2D);
  redrawBackground();

  setupColorSliders();
  setupCustomize();
  thePresets = new presetHolder();

  setupTopBar();

  smallFont = loadFont("small.vlw");
  buttonFont = loadFont("button.vlw");
}

public void drawInterface() {
  image(bkg, width/2, 0, width/2, height, width/2, 0, width, height);

  fill(0);
  rect(0, 64, width/2, height-64);
  thePresets.runPreset();

  updateColorSliders();
  updateCustomize();

  if (freqButton.updateButton())
    FFTEnabled = true;
  else
    FFTEnabled = false;

  updateSettingsMode();

  image(bkg, 0, 0, width/2, 64, 0, 0, width/2, 64);
  fill(255, 100);
  rect(0, 0, width/2, 64);
  updateTopBar();
}

public void updateSettingsMode() {
  int ofVal;
  if (settingsMode) {
    ofVal = height;
  } else {
    ofVal = 0;
  }
  blendSlide.setOffset(0, ofVal);
  freqButton.setOffset(0, ofVal);
  for (int i=0; i<6; i++)
    colsel[i].setOffset(0, ofVal);
    
  centerSlide.setOffset(0, ofVal);
  sensitSlide.setOffset(0, ofVal);
}

public void redrawBackground() {
  bkg.beginDraw();
  for (int i=0; i<width; i++) {
    bkg.stroke(lerpColor(lerpColor(color1, color(0), 0.6f), lerpColor(color2, color(0), 0.6f), PApplet.parseFloat(i)/width));
    bkg.strokeWeight(4);
    bkg.line(i*2-width, 0, i*2, height);
  }
  bkg.endDraw();
}

/* COLOR SLIDERS */
public void setupColorSliders() {
  for (int i=0; i<3; i++)
    colsel[i] = new slider(213, 175+i*32, 150, 254);

  for (int i=3; i<6; i++)
    colsel[i] = new slider(213, 195+i*32, 150, 254);
  setColorSliders();
}

public void updateColorSliders() {
  //  color1 = color(colsel[0].updateSlider(), colsel[1].updateSlider(), colsel[2].updateSlider());
  //  color2 = color(colsel[3].updateSlider(), colsel[4].updateSlider(), colsel[5].updateSlider());
  color1 = color(colors[0], colors[1], colors[2]);
  color2 = color(colors[3], colors[4], colors[5]);


  for (int i=0; i<3; i++)  
    colsel[i].setColor(color(colors[0], colors[1], colors[2]));

  for (int i=3; i<6; i++)  
    colsel[i].setColor(color(colors[3], colors[4], colors[5]));

  for (int i=0; i<6; i++) {
    colors[i] = colsel[i].updateSlider();
  }
}

public void setColorSliders() {
  for (int i=0; i<6; i++)
    colsel[i].setPos(colors[i]);
}
/* COLOR SLIDERS */


/* CUSTOMIZE PART */
public void setupCustomize() {
  blendSlide.setPos(blend);
  blendSlide.setColor(color(255));
  
  // settings section
  centerSlide.setPos(0);
  centerSlide.setColor(color(255));
  
  sensitSlide.setPos(1.4f);
  sensitSlide.setColor(color(255));
}

public void updateCustomize() {
  blend = blendSlide.updateSlider();
  textFont(smallFont);
  text("blend", 288, blendSlide.y+20);
  
  // settings section
  center = PApplet.parseInt(centerSlide.updateSlider());
  textFont(smallFont);
  text("center", 288, centerSlide.y+20);
  
  sensitivity = sensitSlide.updateSlider();
  textFont(smallFont);
  text("sensitivity", 288, sensitSlide.y+20);
}
/* CUSTOMIZE PART */

/* TOP BAR PART */
public void setupTopBar() {
  for (int i=0; i<3; i++)
    topButtons[i] = new button(i*64, 0, 64, 64, " ");
}

public void updateTopBar() {
  if (topButtons[0].updateButton()) {
    thePresets.addElement();
  }

  if (topButtons[1].updateButton()) {
    thePresets.removeElement();
  }

  if (topButtons[2].updateButton()) {
    settingsMode = !settingsMode;
  }

  image(addImg, 0, 0);
  image(subImg, 64, 0);
  image(setImg, 128, 0);
}
/* TOP BAR PART */

public void mouseReleased() {
  if (mouseX > 192 && mouseX < 384 && mouseY > 159 && mouseY < 384)
    redrawBackground();

  freqButton.clickButton();
  thePresets.clickPreset();

  for (int i=0; i<3; i++) {
    topButtons[i].clickButton();
  }
}


public void keyPressed()
{
  if (keyCode >= 65 && keyCode <= 90 || keyCode >= 49 && keyCode <= 57 || keyCode == 32) {
    thePresets.keyType(key);
  }
  if (keyCode == BACKSPACE)
    thePresets.keyBackspace();

  if (keyCode == ENTER)
    thePresets.keySet();
}

public void mouseWheel(MouseEvent event) {
  thePresets.setOffset(event.getCount());
}
class presetHolder {
  ArrayList<button> allsets = new ArrayList<button>();
  XML xml;

  int clicked = 0;
  float offset = 0;

  float indicator;
  float indicatorScroll;

  String textBoxVal = "";
  boolean isTyping;

  int saveTimer;

  presetHolder() {
    xml = loadXML("presets.xml");
    for (int i=0; i<xml.getChildren ("preset").length; i++) {
      allsets.add(new button(0, 64+i*48, 192, 48, xml.getChildren("preset")[i].getChild("name").getContent()));
    }

    if (xml.getChildren("preset").length > 0) {
      loadNew(0);
    } else {
      clicked = -1;
      indicator = -48;
    }
  }

  public void setOffset(float pOff) {
    if (offset <= 0 && pOff < 0) {
      offset = 0;
      return;
    }
    if (offset >= max(allsets.size()*48-height+64, 0) && pOff > 0) {
      offset = max(allsets.size()*48-height+64, 0);
      return;
    }

    offset += pOff*10;
  }

  public void runPreset() {
    saveTimer++;
    for (int i=0; i<allsets.size (); i++) {
      button b = allsets.get(i);
      b.setOffset(0, offset);
      if (b.updateButton()) 
      {
        if (clicked == i) {
          isTyping = true;
          textBoxVal = b.value;
        } else {
          saveCurrent(textBoxVal);
          loadNew(i);
        }
      }
      if (isTyping && clicked == i) {
        b.switchName(textBoxVal);
        //noSmooth();
        rect(b.x+96+textWidth(b.value)/2, b.y+12, 1, 24);
        //smooth();
      }

      if (saveTimer > 750 && clicked == i) {
        saveCurrent(b.value);
        saveTimer = 0;
      }
    }

    runIndicator();
  }

  public void loadNew(int i) {

    keySet();
    clicked = i;
    XML child = xml.getChildren("preset")[i];
    freqButton.switchValue(PApplet.parseBoolean(PApplet.parseInt(child.getChild("fft").getContent())));

    colors[0] = PApplet.parseFloat(child.getChild("r1").getContent());
    colors[1] = PApplet.parseFloat(child.getChild("g1").getContent());
    colors[2] = PApplet.parseFloat(child.getChild("b1").getContent());
    colors[3] = PApplet.parseFloat(child.getChild("r2").getContent());
    colors[4] = PApplet.parseFloat(child.getChild("g2").getContent());
    colors[5] = PApplet.parseFloat(child.getChild("b2").getContent());
    blend = PApplet.parseFloat(child.getChild("blend").getContent());

    color1 = color(colors[0], colors[1], colors[2]);
    color2 = color(colors[3], colors[4], colors[5]);

    textBoxVal = child.getChild("name").getContent();

    blendSlide.setPos(blend);
    redrawBackground();
    setColorSliders();
  }

  public void runIndicator() {
    indicatorScroll += (-offset-indicatorScroll)/5;
    indicator += (clicked*48-indicator)/5;

    fill(255, 128);
    ellipse(176, indicator+indicatorScroll+88, 6, 6);
  }

  public void keyType(char theKey) {
    if (isTyping) {
      textBoxVal += theKey;
    }
  }

  public void keyBackspace() {
    if (isTyping && textBoxVal.length() > 0)
      textBoxVal = textBoxVal.substring(0, textBoxVal.length()-1);
  }

  public void keySet() {
    if (isTyping) {
      isTyping = false;
      xml.getChildren("preset")[clicked].getChild("name").setContent(textBoxVal);
      saveXML(xml, "data/presets.xml");
    }
  }

  public void clickPreset() {
    isTyping = false;
    for (int i=0; i<allsets.size (); i++) {
      button b = allsets.get(i);
      b.clickButton();
    }
  }

  public void removeElement() {
    if (allsets.size() > 0) {
      xml.removeChild(xml.getChildren("preset")[clicked]);
      saveXML(xml, "data/presets.xml");

      allsets.remove(clicked);

      for (int i=clicked; i<allsets.size (); i++) {
        button b = allsets.get(i);
        b.sy-=48;
      }

      if (clicked > xml.getChildren("preset").length-1)
        clicked--;

      if (clicked > 0)
        loadNew(clicked);
    }
  }

  public void addElement() {
    XML newElement = xml.addChild("preset");
    newElement.addChild("name").setContent("");
    newElement.addChild("fft").setContent(str(PApplet.parseInt(FFTEnabled)));
    newElement.addChild("blend").setContent(str(blend));
    newElement.addChild("r1").setContent(str(colors[0]));
    newElement.addChild("g1").setContent(str(colors[1]));
    newElement.addChild("b1").setContent(str(colors[2]));
    newElement.addChild("r2").setContent(str(colors[3]));
    newElement.addChild("g2").setContent(str(colors[4]));
    newElement.addChild("b2").setContent(str(colors[5]));
    saveXML(xml, "data/presets.xml");

    int len = xml.getChildren("preset").length-1;
    allsets.add(new button(0, 64+len*48, 192, 48, ""));
    clicked = len;
    isTyping = true;
    textBoxVal = "";
  }

  public void saveCurrent(String theName) {
    XML myElement = xml.getChildren("preset")[clicked];
    myElement.getChild("name").setContent(theName);
    myElement.getChild("fft").setContent(str(PApplet.parseInt(FFTEnabled)));
    myElement.getChild("blend").setContent(str(blend));
    myElement.getChild("r1").setContent(str(colors[0]));
    myElement.getChild("g1").setContent(str(colors[1]));
    myElement.getChild("b1").setContent(str(colors[2]));
    myElement.getChild("r2").setContent(str(colors[3]));
    myElement.getChild("g2").setContent(str(colors[4]));
    myElement.getChild("b2").setContent(str(colors[5]));

    saveXML(xml, "data/presets.xml");
  }
}
class slider {
  float x, y, sx, sy, xOff, yOff, wid;
  float maxval;
  float val;
  float shouldbeval;
  int c;
  int cwid = 16;
  boolean hovering;

  slider(int px, int py, int pwid, int pmaxval) {
    x = px;
    y = py;
    sx = x;
    sy = y;
    wid = pwid;
    maxval = pmaxval;
  }

  public void setColor(int pc) {
    c = pc;
  }

  public void setPos(float pp) {
    shouldbeval = (int)map(pp, 0, maxval, 0, wid);
  }
  
  public void setOffset(float px, float py) {
    xOff = px;
    yOff = py;
  }

  public float updateSlider() {
    x += ((sx-xOff)-x)/5;
    y += ((sy-yOff)-y)/5;
    
    updateHovering();
    updateMovement();

    if (mousePressed && mouseX > x && mouseX < x+wid && mouseY > y-8 && mouseY < y+24) {
      val = mouseX - x;
      shouldbeval = val;
    }

    drawSlider();
    return map(shouldbeval, 0, wid, 0, maxval);
  }

  public void drawSlider() {
    stroke(c);
    strokeWeight(1);
    line(x, y, x+wid, y);
    noStroke();
    fill(c);
    ellipse(x+val, y, cwid, cwid);
  }

  public void updateMovement() {
    if (shouldbeval != val)
      val+=(shouldbeval-val)/10;
  }

  public void updateHovering() {
    if (mouseX > x && mouseX < x+wid && mouseY > y-8 && mouseY < y+24)
      hovering = true;
    else
      hovering = false;

    if (hovering && cwid < 20)
      cwid++;

    if (!hovering && cwid > 16)
      cwid--;
  }
}
  public void settings() {  size(384, 384); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DormLED" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
