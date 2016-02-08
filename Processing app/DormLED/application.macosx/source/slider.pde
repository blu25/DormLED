class slider {
  float x, y, sx, sy, xOff, yOff, wid;
  float maxval;
  float val;
  float shouldbeval;
  color c;
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

  void setColor(color pc) {
    c = pc;
  }

  void setPos(float pp) {
    shouldbeval = (int)map(pp, 0, maxval, 0, wid);
  }
  
  void setOffset(float px, float py) {
    xOff = px;
    yOff = py;
  }

  float updateSlider() {
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

  void drawSlider() {
    stroke(c);
    strokeWeight(1);
    line(x, y, x+wid, y);
    noStroke();
    fill(c);
    ellipse(x+val, y, cwid, cwid);
  }

  void updateMovement() {
    if (shouldbeval != val)
      val+=(shouldbeval-val)/10;
  }

  void updateHovering() {
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