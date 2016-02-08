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

void setupInterface() {
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

void drawInterface() {
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

void updateSettingsMode() {
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

void redrawBackground() {
  bkg.beginDraw();
  for (int i=0; i<width; i++) {
    bkg.stroke(lerpColor(lerpColor(color1, color(0), 0.6), lerpColor(color2, color(0), 0.6), float(i)/width));
    bkg.strokeWeight(4);
    bkg.line(i*2-width, 0, i*2, height);
  }
  bkg.endDraw();
}

/* COLOR SLIDERS */
void setupColorSliders() {
  for (int i=0; i<3; i++)
    colsel[i] = new slider(213, 175+i*32, 150, 254);

  for (int i=3; i<6; i++)
    colsel[i] = new slider(213, 195+i*32, 150, 254);
  setColorSliders();
}

void updateColorSliders() {
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

void setColorSliders() {
  for (int i=0; i<6; i++)
    colsel[i].setPos(colors[i]);
}
/* COLOR SLIDERS */


/* CUSTOMIZE PART */
void setupCustomize() {
  blendSlide.setPos(blend);
  blendSlide.setColor(color(255));
  
  // settings section
  centerSlide.setPos(0);
  centerSlide.setColor(color(255));
  
  sensitSlide.setPos(1.4);
  sensitSlide.setColor(color(255));
}

void updateCustomize() {
  blend = blendSlide.updateSlider();
  textFont(smallFont);
  text("blend", 288, blendSlide.y+20);
  
  // settings section
  center = int(centerSlide.updateSlider());
  textFont(smallFont);
  text("center", 288, centerSlide.y+20);
  
  sensitivity = sensitSlide.updateSlider();
  textFont(smallFont);
  text("sensitivity", 288, sensitSlide.y+20);
}
/* CUSTOMIZE PART */

/* TOP BAR PART */
void setupTopBar() {
  for (int i=0; i<3; i++)
    topButtons[i] = new button(i*64, 0, 64, 64, " ");
}

void updateTopBar() {
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

void mouseReleased() {
  if (mouseX > 192 && mouseX < 384 && mouseY > 159 && mouseY < 384)
    redrawBackground();

  freqButton.clickButton();
  thePresets.clickPreset();

  for (int i=0; i<3; i++) {
    topButtons[i].clickButton();
  }
}


void keyPressed()
{
  if (keyCode >= 65 && keyCode <= 90 || keyCode >= 49 && keyCode <= 57 || keyCode == 32) {
    thePresets.keyType(key);
  }
  if (keyCode == BACKSPACE)
    thePresets.keyBackspace();

  if (keyCode == ENTER)
    thePresets.keySet();
}

void mouseWheel(MouseEvent event) {
  thePresets.setOffset(event.getCount());
}
