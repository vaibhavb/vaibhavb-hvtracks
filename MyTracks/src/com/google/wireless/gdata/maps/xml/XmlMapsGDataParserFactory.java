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
package com.google.wireless.gdata.maps.xml;

import com.google.wireless.gdata.client.GDataParserFactory;
import com.google.wireless.gdata.data.Entry;
import com.google.wireless.gdata.maps.MapFeatureEntry;
import com.google.wireless.gdata.maps.MapsClient;
import com.google.wireless.gdata.parser.GDataParser;
import com.google.wireless.gdata.parser.ParseException;
import com.google.wireless.gdata.parser.xml.XmlGDataParser;
import com.google.wireless.gdata.parser.xml.XmlParserFactory;
import com.google.wireless.gdata.serializer.GDataSerializer;
import com.google.wireless.gdata.serializer.xml.XmlEntryGDataSerializer;

import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Factory of Xml parsers for gdata maps data.
 */
public class XmlMapsGDataParserFactory implements GDataParserFactory {
  private XmlParserFactory xmlFactory;

  public XmlMapsGDataParserFactory(XmlParserFactory xmlFactory) {
    this.xmlFactory = xmlFactory;
  }

  @Override
  public GDataParser createParser(InputStream is) throws ParseException {
    is = maybeLogCommunication(is);
    try {
      return new XmlGDataParser(is, xmlFactory.createParser());
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public GDataParser createParser(Class cls, InputStream is)
      throws ParseException {
    is = maybeLogCommunication(is);
    try {
      return createParserForClass(cls, is);
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      return null;
    }
  }

  private InputStream maybeLogCommunication(InputStream is)
      throws ParseException {
    if (MapsClient.LOG_COMMUNICATION) {
      StringBuilder builder = new StringBuilder();
      byte[] buffer = new byte[2048];
      try {
        for (int n = is.read(buffer); n >= 0; n = is.read(buffer)) {
          String part = new String(buffer, 0, n);
          builder.append(part);
          Log.d("Response part", part);
        }
      } catch (IOException e) {
        throw new ParseException("Could not read stream", e);
      }
      String whole = builder.toString();
      Log.d("Response", whole);
      is = new ByteArrayInputStream(whole.getBytes());
    }
    return is;
  }

  private GDataParser createParserForClass(
      Class<? extends Entry> cls, InputStream is)
      throws ParseException, XmlPullParserException {
    if (cls == MapFeatureEntry.class) {
      return new XmlMapsGDataParser(is, xmlFactory.createParser());
    } else {
      return new XmlGDataParser(is, xmlFactory.createParser());
    }
  }

  @Override
  public GDataSerializer createSerializer(Entry en) {
    if (en.getClass() == MapFeatureEntry.class) {
      return new XmlMapsGDataSerializer(xmlFactory, en);
    } else {
      return new XmlEntryGDataSerializer(xmlFactory, en);
    }
  }
}
