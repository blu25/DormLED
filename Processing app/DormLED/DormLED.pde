int num_pixels = 100;

boolean FFTEnabled = false;
int center;
float blend;

color pixshalf[] = new color[num_pixels];
color pixs[] = new color[num_pixels];

float colors[] = new float[6];
color color1 = color(255, 150, 0);
color color2 = color(0, 180, 255);

void setup() {
  setupDefaults();

  size(384, 384);
  setupInterface();
  setupAudio();
  frameRate(40);
  noStroke();
  textAlign(CENTER);
  connectToArduino();
}

void setupDefaults() {
  colors[0] = 255;
  colors[1] = 150;
  colors[2] = 0;
  colors[3] = 0;
  colors[4] = 180;
  colors[5] = 255;
}

void draw() {
  smooth();
  drawInterface();
  updateAudio();

  if (justUpdatedAudio)
    updateLEDs();
}