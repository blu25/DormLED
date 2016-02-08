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

  void switchValue(boolean nVal) {
    isOn = nVal;
  }

  void switchName(String nname) {
    value = nname;
  }

  void setOffset(float px, float py) {
    xOff = px;
    yOff = py;
  }

  boolean updateButton() {
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
      text(value, x+wid/2+textWidth(value2)/1.75, y+hgt/1.6);

      fill(255, 55+toggleValue);
      text(value2, x+wid/2-textWidth(value)/1.75, y+hgt/1.6);

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
      text(value, x+wid/2, y+hgt/1.6);

      if (isClicked) {
        isClicked = false;
        return true;
      } else {
        return false;
      }
    }
  }

  void clickButton() {
    if (mouseX > x && mouseX < x+wid && mouseY > y && mouseY < y+hgt) {
      isClicked = true;
      if (isToggle) {
        isOn = !isOn;
      }
    }
  }
}

