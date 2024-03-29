/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.apps.mytracks.io;

import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.Track;

import android.content.Context;

/**
 * A factory to produce track writers for any format.
 *
 * @author Rodrigo Damazio
 */
public class TrackWriterFactory {

  /**
   * Definition of all possible track formats.
   */
  public enum TrackFileFormat {
    GPX {
      @Override
      TrackFormatWriter newFormatWriter(Context context) {
        return new GpxTrackWriter();
      }
    },
    KML {
      @Override
      TrackFormatWriter newFormatWriter(Context context) {
        return new KmlTrackWriter(context);
      }
    },
    CSV {
      @Override
      public TrackFormatWriter newFormatWriter(Context context) {
        return new CsvTrackWriter();
      }
    };

    /**
     * Creates and returns a new format writer for each format.
     */
    abstract TrackFormatWriter newFormatWriter(Context context);

    /**
     * Returns the mime type for each format.
     */
    public String getMimeType() {
      return "application/" + getExtension() + "+xml";
    }

    /**
     * Returns the file extension for each format.
     */
    public String getExtension() {
      return this.name().toLowerCase();
    }
  }

  /**
   * Creates a new track writer to write the track with the given ID.
   *
   * @param context the context in which the track will be read
   * @param providerUtils the data provider utils to read the track with
   * @param trackId the ID of the track to be written
   * @param format the output format to write in
   * @return the new track writer
   */
  public static TrackWriter newWriter(Context context,
      MyTracksProviderUtils providerUtils,
      long trackId, TrackFileFormat format) {
    Track track = providerUtils.getTrack(trackId);
    return newWriter(context, providerUtils, track, format);
  }

  /**
   * Creates a new track writer to write the given track.
   *
   * @param context the context in which the track will be read
   * @param providerUtils the data provider utils to read the track with
   * @param track the track to be written
   * @param format the output format to write in
   * @return the new track writer
   */
  public static TrackWriter newWriter(Context context,
      MyTracksProviderUtils providerUtils,
      Track track, TrackFileFormat format) {
    TrackFormatWriter writer = format.newFormatWriter(context);
    return new TrackWriter(context, providerUtils, track, writer);
  }

  private TrackWriterFactory() { }
}
