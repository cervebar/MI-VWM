/*
  Copyright (c) 2003, Holger Crysandt

  This file is part of the MPEG7AudioEnc project.
*/

package de.crysandt.audio.mpeg7audio;

import de.crysandt.xml.*;

public class MP7MediaInformation
    extends XMLNode
{
  // private final static int PRIO_MEDIA_PROFILE     = -1;
  // private final static int PRIO_MEDIA_INFORMATION = 0;

  private final static int PRIO_CONTENT     = -10;
  // private final static int PRIO_MEDIUM      = -9;
  // private final static int PRIO_FILE_FORMAT = -8;
  private final static int PRIO_FILE_SIZE   = -7;
  // private final static int PRIO_SYSTEM      = -6;
  private final static int PRIO_BANDWIDTH   = -5;
  private final static int PRIO_BIT_RATE    = -4;
  // private final static int PRIO_TARGET_CBR  = -3;
  // private final static int PRIO_SCALABLE    = -2;
  // private final static int PRIO_VISUAL      = -1;
  private final static int PRIO_AUDIO       = 0;

  public final static int PRIO_AUDIO_CHANNELS = -1;
  private final static int PRIO_AUDIO_SAMPLE   = 0;

  public MP7MediaInformation() {
    super("MediaInformation", MP7Writer.PRIO_MEDIA_INFO);

    getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaFormat"))
        .getNode((new XMLNode("Content", PRIO_CONTENT))
                 .setAttribute("href", "MPEG7ContentCS"))
        .getNode( new XMLNode("Name", false))
        .setChild(new XMLCharacters("audio"));
  }

  public void setBitRate(int bit_rate) {
    getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaFormat"))
        .getNode(new XMLNode("BitRate", false, PRIO_BIT_RATE))
        .setChild(new XMLCharacters("" + bit_rate));
  }

  public void setBandWidth(float band_width) {
    getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaFormat"))
        .getNode(new XMLNode("Bandwidth", false, PRIO_BANDWIDTH))
        .setChild(new XMLCharacters("" + band_width));
  }

  /**
   * Sets the size of the audio file in bytes.
   *
   * @param file_size Size of the file [bytes]
   */
  public void setFileSize(long file_size) {
    getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaFormat"))
        .getNode(new XMLNode("FileSize", false, PRIO_FILE_SIZE))
        .setChild(new XMLCharacters("" + file_size));
  }

  /**
   * Sets the number of channels of the audio signal
   *
   * @param number_of_channels Number of channels (1: Mono, 2: Stereo)
   */
  public void setNumberOfChannels(int number_of_channels){
    getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaFormat"))
        .getNode(new XMLNode("AudioCoding", PRIO_AUDIO))
        .getNode(new XMLNode("AudioChannels", false, PRIO_AUDIO_CHANNELS))
        .setChild(new XMLCharacters("" + number_of_channels));
  }

  /**
   * Sets samplerate or/and bits per sample of the audio signal. If
   * only one of both values should be used, choose zero or a
   * negative value for the other parameter.
   *
   * @param sample_rate     Sample rate of the audio signal
   * @param bits_per_sample Number of bits per sample
   */
  public void setSample(float sample_rate, int bits_per_sample) {
    XMLNode sample = new XMLNode("Sample", PRIO_AUDIO_SAMPLE);

    if (sample_rate > 0)
      sample.setAttribute("rate", "" + sample_rate);

    if (bits_per_sample>0)
      sample.setAttribute("bitsPer", ""  + bits_per_sample);

    getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaFormat"))
        .getNode(new XMLNode("AudioCoding", PRIO_AUDIO))
        .getNode(sample);
  }

  public void setMediaLocation(String uri, String identifier) {
    XMLNode media_instance = getNode(new XMLNode("MediaProfile"))
        .getNode(new XMLNode("MediaInstance"));

    media_instance.getNode(new XMLNode("InstanceIdentifier", false, -1))
        .setChild(new XMLCharacters(identifier));

    media_instance.getNode(new XMLNode("MediaLocator", false, 0))
        .getNode(new XMLNode("MediaUri"))
        .setChild(new XMLCharacters(uri));
  }

  public static void main(String[] args) {
    MP7MediaInformation mi = new MP7MediaInformation();

    mi.setFileSize(123456);
    mi.setSample(22050.0f, 16);
    mi.setNumberOfChannels(2);
    mi.setBitRate(1234);
    mi.setBandWidth(234.5f);

    System.out.println(mi.toString());
  }
}
