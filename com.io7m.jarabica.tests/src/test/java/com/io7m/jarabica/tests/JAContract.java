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

import com.io7m.jarabica.api.JADeviceDescription;
import com.io7m.jarabica.api.JADeviceFactoryType;
import com.io7m.jarabica.api.JAListenerType;
import com.io7m.jarabica.api.JAMisuseException;
import com.io7m.jarabica.api.JASourceState;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.io7m.jarabica.api.JABufferFormat.*;
import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_INITIAL;
import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_PAUSED;
import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_STOPPED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class JAContract
{
  private JADeviceFactoryType devices;
  private List<JADeviceDescription> deviceDescriptions;
  private CloseableCollectionType<ClosingResourceFailedException> resources;

  protected abstract JADeviceFactoryType deviceFactory();

  @BeforeEach
  public final void setup()
  {
    this.devices =
      this.deviceFactory();
    this.deviceDescriptions =
      this.devices.enumerateDevices();
    assertTrue(this.deviceDescriptions.size() > 0, "Devices non-empty");

    this.resources =
      CloseableCollection.create();
  }

  @AfterEach
  public final void tearDown()
    throws Exception
  {
    this.resources.close();
  }

  /**
   * Nonexistent devices can't be opened.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testDeviceNonexistent()
    throws Exception
  {
    final var ex =
      assertThrows(JAMisuseException.class, () -> {
        this.devices.openDevice(
          new JADeviceDescription("NO SUCH DEVICE!"));
      });
    assertTrue(ex.getMessage().contains("No such device"));
  }

  /**
   * Opening and closing devices works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testDeviceOpenClose()
    throws Exception
  {
    try (var device =
           this.devices.openDevice(this.deviceDescriptions.get(0))) {
      assertFalse(device.isClosed());
    }
  }

  /**
   * Using a closed device fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testDeviceClosed()
    throws Exception
  {
    final var device =
      this.devices.openDevice(this.deviceDescriptions.get(0));

    device.close();
    assertThrows(JAMisuseException.class, device::createContext);
    assertTrue(device.isClosed());
  }

  /**
   * Opening and closing contexts works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextOpenClose()
    throws Exception
  {
    try (var device =
           this.devices.openDevice(this.deviceDescriptions.get(0))) {
      assertFalse(device.isClosed());

      try (var context = device.createContext()) {
        assertTrue(context.isCurrent());
      }
    }
  }

  /**
   * Switching contexts works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSwitch()
    throws Exception
  {
    final var device =
      this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
    final var context0 =
      this.resources.add(device.createContext());
    final var context1 =
      this.resources.add(device.createContext());

    assertFalse(context0.isCurrent());
    assertTrue(context1.isCurrent());

    context0.setCurrent();
    assertTrue(context0.isCurrent());
    assertFalse(context1.isCurrent());

    context1.setCurrent();
    assertFalse(context0.isCurrent());
    assertTrue(context1.isCurrent());
  }

  /**
   * Using a closed context fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextClosed()
    throws Exception
  {
    try (var device =
           this.devices.openDevice(this.deviceDescriptions.get(0))) {
      final var context = device.createContext();
      final var listener = context.listener();
      context.close();
      assertThrows(JAMisuseException.class, context::createSource);
      assertThrows(JAMisuseException.class, () -> listener.setPosition(
        (double) 0,
        (double) 0,
        (double) 0));
      assertTrue(context.isClosed());
    }
  }

  /**
   * Setting listener properties works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextListener()
    throws Exception
  {
    final var device =
      this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
    final var context =
      this.resources.add(device.createContext());

    final var listener = context.listener();
    listener.setPosition(1.0, 2.0, 3.0);
    assertEquals(
      Vector3D.of(1.0, 2.0, 3.0),
      listener.position()
    );

    listener.setPosition(Vector3D.of(11.0, 12.0, 13.0));
    assertEquals(
      Vector3D.of(11.0, 12.0, 13.0),
      listener.position()
    );

    listener.setPosition(PVector3D.of(21.0, 22.0, 23.0));
    assertEquals(
      Vector3D.of(21.0, 22.0, 23.0),
      listener.position()
    );

    listener.setVelocity(20.0, 30.0, 40.0);
    assertEquals(
      Vector3D.of(20.0, 30.0, 40.0),
      listener.velocity()
    );

    listener.setVelocity(Vector3D.of(120.0, 130.0, 140.0));
    assertEquals(
      Vector3D.of(120.0, 130.0, 140.0),
      listener.velocity()
    );

    listener.setVelocity(PVector3D.of(1120.0, 1130.0, 1140.0));
    assertEquals(
      Vector3D.of(1120.0, 1130.0, 1140.0),
      listener.velocity()
    );

    listener.setOrientation(
      Vector3D.of(0.0, 0.0, -2.0),
      Vector3D.of(0.0, 2.0, 0.0)
    );
    assertEquals(
      new JAListenerType.Orientation(
        Vector3D.of(0.0, 0.0, -2.0),
        Vector3D.of(0.0, 2.0, 0.0)
      ),
      listener.orientation()
    );

    listener.setOrientation(
      PVector3D.of(0.0, 0.0, -3.0),
      PVector3D.of(0.0, 3.0, 0.0)
    );
    assertEquals(
      new JAListenerType.Orientation(
        Vector3D.of(0.0, 0.0, -3.0),
        Vector3D.of(0.0, 3.0, 0.0)
      ),
      listener.orientation()
    );
  }

  /**
   * Creating and manipulating sources works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSource()
    throws Exception
  {
    final var device =
      this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
    final var context =
      this.resources.add(device.createContext());
    final var source =
      this.resources.add(context.createSource());

    assertFalse(source.isClosed());
    assertFalse(source.isPlaying());

    source.setPosition(10.0, 20.0, 30.0);
    assertEquals(
      Vector3D.of(10.0, 20.0, 30.0),
      source.position()
    );

    source.setPosition(
      Vector3D.of(200.0, 300.0, 400.0));
    assertEquals(
      Vector3D.of(200.0, 300.0, 400.0),
      source.position()
    );

    source.setPosition(
      PVector3D.of(500.0, 600.0, 700.0));
    assertEquals(
      Vector3D.of(500.0, 600.0, 700.0),
      source.position()
    );

    source.setVelocity(100.0, 200.0, 300.0);
    assertEquals(
      Vector3D.of(100.0, 200.0, 300.0),
      source.velocity()
    );

    source.setVelocity(
      Vector3D.of(200.0, 300.0, 400.0));
    assertEquals(
      Vector3D.of(200.0, 300.0, 400.0),
      source.velocity()
    );

    source.setVelocity(
      PVector3D.of(500.0, 600.0, 700.0));
    assertEquals(
      Vector3D.of(500.0, 600.0, 700.0),
      source.velocity()
    );

    source.setGain(1.2);
    assertEquals(1.2, source.gain(), 0.0001);

    source.setPitch(0.5);
    assertEquals(0.5, source.pitch(), 0.0001);
  }

  /**
   * Playing sources works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourcePlay()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var source =
        this.resources.add(context.createSource());
      final var buffer =
        this.resources.add(context.createBuffer());

      for (final var format : values()) {
        final var data = ByteBuffer.allocateDirect(48000);
        buffer.setData(format, 48000, data);

        source.setBuffer(buffer);
        assertEquals(Optional.of(buffer), source.buffer());
        assertFalse(source.isPlaying());
        source.play();
        assertTrue(source.isPlaying());

        while (source.isPlaying()) {
          Thread.sleep(100L);
        }

        source.detachBuffer();
      }
    });
  }

  /**
   * Pausing sources works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourcePlayPause()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var source =
        this.resources.add(context.createSource());
      final var buffer =
        this.resources.add(context.createBuffer());

      for (final var format : values()) {
        final var data = ByteBuffer.allocateDirect(48000);
        buffer.setData(format, 48000, data);

        source.setBuffer(buffer);
        assertEquals(Optional.of(buffer), source.buffer());
        assertFalse(source.isPlaying());
        source.play();
        assertTrue(source.isPlaying());
        source.pause();
        assertEquals(SOURCE_STATE_PAUSED, source.state());
        source.play();
        assertTrue(source.isPlaying());

        while (source.isPlaying()) {
          Thread.sleep(100L);
        }

        source.detachBuffer();
      }
    });
  }

  /**
   * Stopping sources works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourcePlayStop()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var source =
        this.resources.add(context.createSource());
      final var buffer =
        this.resources.add(context.createBuffer());

      for (final var format : values()) {
        final var data = ByteBuffer.allocateDirect(48000);
        buffer.setData(format, 48000, data);

        source.setBuffer(buffer);
        assertEquals(Optional.of(buffer), source.buffer());
        assertFalse(source.isPlaying());
        source.play();
        assertTrue(source.isPlaying());
        source.stop();
        assertEquals(SOURCE_STATE_STOPPED, source.state());
        source.play();
        assertTrue(source.isPlaying());

        while (source.isPlaying()) {
          Thread.sleep(100L);
        }

        source.detachBuffer();
      }
    });
  }

  /**
   * Rewinding sources works.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourcePlayRewind()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var source =
        this.resources.add(context.createSource());
      final var buffer =
        this.resources.add(context.createBuffer());

      for (final var format : values()) {
        final var data = ByteBuffer.allocateDirect(48000);
        buffer.setData(format, 48000, data);

        source.setBuffer(buffer);
        assertEquals(Optional.of(buffer), source.buffer());
        assertFalse(source.isPlaying());
        source.play();
        assertTrue(source.isPlaying());
        source.rewind();
        assertEquals(SOURCE_STATE_INITIAL, source.state());
        source.play();
        assertTrue(source.isPlaying());

        while (source.isPlaying()) {
          Thread.sleep(100L);
        }

        source.detachBuffer();
      }
    });
  }

  /**
   * Using a closed source fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourceClosed()
    throws Exception
  {
    final var device =
      this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
    final var context =
      this.resources.add(device.createContext());
    final var source =
      this.resources.add(context.createSource());

    source.close();
    assertThrows(JAMisuseException.class, () -> source.setVelocity(0.0, 0.0, 0.0));
  }

  /**
   * Trying to delete a buffer with an attached source fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourceDeleteBufferAttached()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var source =
        this.resources.add(context.createSource());
      final var buffer =
        this.resources.add(context.createBuffer());

      for (final var format : values()) {
        final var data = ByteBuffer.allocateDirect(48000);
        buffer.setData(format, 48000, data);
        source.setBuffer(buffer);

        assertThrows(JAMisuseException.class, buffer::close);
        assertFalse(buffer.isClosed());
        source.detachBuffer();
      }
    });
  }

  /**
   * Using a closed buffer fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextDeletedBuffer()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var source =
        this.resources.add(context.createSource());
      final var buffer =
        this.resources.add(context.createBuffer());

      buffer.close();
      final var data = ByteBuffer.allocateDirect(48000);

      assertThrows(JAMisuseException.class, () -> {
        buffer.setData(AUDIO_8_BIT_MONO, 48000, data);
      });
      assertThrows(JAMisuseException.class, () -> {
        source.setBuffer(buffer);
      });
    });
  }

  /**
   * Using a heap buffer fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextBufferNotDirect()
    throws Exception
  {
    assertTimeout(Duration.ofSeconds(10L), () -> {
      final var device =
        this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
      final var context =
        this.resources.add(device.createContext());
      final var buffer =
        this.resources.add(context.createBuffer());

      final var data = ByteBuffer.allocate(48000);
      assertThrows(JAMisuseException.class, () -> {
        buffer.setData(AUDIO_8_BIT_MONO, 48000, data);
      });
    });
  }

  /**
   * Trying to use sources on the wrong context fails.
   *
   * @throws Exception On errors
   */

  @Test
  public final void testContextSourceNotCurrent()
    throws Exception
  {
    final var device =
      this.resources.add(this.devices.openDevice(this.deviceDescriptions.get(0)));
    final var context0 =
      this.resources.add(device.createContext());
    final var source =
      this.resources.add(context0.createSource());
    final var context1 =
      this.resources.add(device.createContext());

    context1.setCurrent();

    assertThrows(JAMisuseException.class, () -> {
      source.setPosition(10.0, 20.0, 30.0);
    });

    context0.setCurrent();
  }
}
