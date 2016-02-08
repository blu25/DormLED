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

  void setOffset(float pOff) {
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

  void runPreset() {
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

  void loadNew(int i) {

    keySet();
    clicked = i;
    XML child = xml.getChildren("preset")[i];
    freqButton.switchValue(boolean(int(child.getChild("fft").getContent())));

    colors[0] = float(child.getChild("r1").getContent());
    colors[1] = float(child.getChild("g1").getContent());
    colors[2] = float(child.getChild("b1").getContent());
    colors[3] = float(child.getChild("r2").getContent());
    colors[4] = float(child.getChild("g2").getContent());
    colors[5] = float(child.getChild("b2").getContent());
    blend = float(child.getChild("blend").getContent());

    color1 = color(colors[0], colors[1], colors[2]);
    color2 = color(colors[3], colors[4], colors[5]);

    textBoxVal = child.getChild("name").getContent();

    blendSlide.setPos(blend);
    redrawBackground();
    setColorSliders();
  }

  void runIndicator() {
    indicatorScroll += (-offset-indicatorScroll)/5;
    indicator += (clicked*48-indicator)/5;

    fill(255, 128);
    ellipse(176, indicator+indicatorScroll+88, 6, 6);
  }

  void keyType(char theKey) {
    if (isTyping) {
      textBoxVal += theKey;
    }
  }

  void keyBackspace() {
    if (isTyping && textBoxVal.length() > 0)
      textBoxVal = textBoxVal.substring(0, textBoxVal.length()-1);
  }

  void keySet() {
    if (isTyping) {
      isTyping = false;
      xml.getChildren("preset")[clicked].getChild("name").setContent(textBoxVal);
      saveXML(xml, "data/presets.xml");
    }
  }

  void clickPreset() {
    isTyping = false;
    for (int i=0; i<allsets.size (); i++) {
      button b = allsets.get(i);
      b.clickButton();
    }
  }

  void removeElement() {
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

  void addElement() {
    XML newElement = xml.addChild("preset");
    newElement.addChild("name").setContent("");
    newElement.addChild("fft").setContent(str(int(FFTEnabled)));
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

  void saveCurrent(String theName) {
    XML myElement = xml.getChildren("preset")[clicked];
    myElement.getChild("name").setContent(theName);
    myElement.getChild("fft").setContent(str(int(FFTEnabled)));
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