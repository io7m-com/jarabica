/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.jarabica.tests;

import com.io7m.jarabica.api.JABufferFormat;
import com.io7m.jarabica.extensions.efx.JAEFXConfiguration;
import com.io7m.jarabica.extensions.efx.JAEFXFilterHighPassParameters;
import com.io7m.jarabica.extensions.efx.JAEFXFilterLowPassParameters;
import com.io7m.jarabica.extensions.efx.JAEFXType;
import com.io7m.jarabica.lwjgl.JALWDeviceFactory;
import com.io7m.jmulticlose.core.CloseableCollection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class JALDemo
{
  private JALDemo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final var done =
      new AtomicBoolean(false);

    final var thread = new Thread(() -> {
      try {
        System.in.read();
      } catch (final IOException e) {
        // Don't care!
      }
      done.set(true);
    });
    thread.setDaemon(true);
    thread.start();

    try (var resources = CloseableCollection.create()) {
      final var devices =
        new JALWDeviceFactory();
      final var descriptions =
        devices.enumerateDevices();

      if (descriptions.isEmpty()) {
        throw new IllegalStateException("No available audio devices.");
      }

      final var device =
        resources.add(devices.openDevice(descriptions.get(0)));

      final var context =
        resources.add(device.createContext(List.of(
          new JAEFXConfiguration(2)
        )));

      final var efx =
        context.extension(JAEFXType.class)
          .orElseThrow();

      final var filter =
        resources.add(efx.createFilterHighPass(
          new JAEFXFilterHighPassParameters(
            1.0,
            1.0
          )
        ));

      final var buffer =
        resources.add(context.createBuffer());
      final var source =
        resources.add(context.createSource());

      source.setGain(0.5);
      efx.attachSourceDirectOutputToFilter(source, filter);

      final var rng =
        SecureRandom.getInstanceStrong();
      final var data =
        ByteBuffer.allocateDirect(48000);
      final var bytes =
        new byte[48000];

      rng.nextBytes(bytes);
      data.put(bytes);
      data.rewind();

      buffer.setData(JABufferFormat.AUDIO_8_BIT_MONO, 48000, data);
      source.setBuffer(buffer);

      efx.signalGraph()
        .edgeSet()
        .forEach(edge -> {
          System.out.printf("%s -> %s%n", edge.source(), edge.target());
        });

      while (!done.get()) {
        if (!source.isPlaying()) {
          source.play();
        }

        filter.setParameters(new JAEFXFilterHighPassParameters(
          1.0,
          rng.nextDouble()
        ));

        Thread.sleep(100L);
      }
    }
  }
}
