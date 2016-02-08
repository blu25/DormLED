import ddf.minim.analysis.*;
import ddf.minim.*;

Minim minim;
AudioInput in;

FFT fftLin;

float sensitivity;

boolean justUpdatedAudio;

void setupAudio() {
  minim = new Minim(this);
  in = minim.getLineIn(Minim.MONO);
  fftLin = new FFT( in.bufferSize(), in.sampleRate() );
  fftLin.linAverages(256);
}

void updateAudio() {
  if (!justUpdatedAudio)
    if (FFTEnabled) {
      updateFFT();
    } else {
      updateAmplitude();
    }

  centrifyPixels();
  justUpdatedAudio = !justUpdatedAudio;
}

void updateAmplitude() {
  float curr_amp = min(in.left.level()*sensitivity, 1);
  for (int i=num_pixels-1; i>0; i--) {
    pixshalf[i] = pixshalf[i-1];
  }
  lerpifyPixel(0, curr_amp);
}

void updateFFT() {
  fftLin.forward( in.mix );
  for (int i=0; i<100; i++) {
    lerpifyPixel(i, min(fftLin.getAvg(i)*sensitivity/20, 1));
  }
}

void centrifyPixels() {
  for (int i=0; i<num_pixels; i++) {
    pixs[i] = lerpColor(pixs[i], pixshalf[abs(i-center)], blend);
    if (blend <= 0.5) {
      pixs[i] = color(red(pixs[i]) - 0.1, green(pixs[i]) - 0.1, blue(pixs[i]) - 0.1);      
    }
    fill(red(pixs[i])*2, green(pixs[i])*2, blue(pixs[i])*2);
    rect(i*width/num_pixels, height-5, 4, 5);
  }
}

void lerpifyPixel(int i, float amp) {
  if (amp > 0.5) {
    pixshalf[i] = lerpColor(color1, color2, (amp-0.5)*2);
  } else {
    pixshalf[i] = lerpColor(color(0), color1, amp*2);
  }

  if (FFTEnabled) {
    pixshalf[i] = blendColor(pixshalf[i], color(pow(amp*255, 2)/255), MULTIPLY);
  } else {
    pixshalf[i] = lerpColor(color(0), pixshalf[i], amp);
  }
}

void stop()
{
  minim.stop();
  super.stop();
}