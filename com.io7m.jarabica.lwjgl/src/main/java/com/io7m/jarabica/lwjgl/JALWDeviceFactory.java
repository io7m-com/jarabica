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

package com.io7m.jarabica.lwjgl;

import com.io7m.jarabica.api.JADeviceDescription;
import com.io7m.jarabica.api.JADeviceException;
import com.io7m.jarabica.api.JADeviceFactoryType;
import com.io7m.jarabica.api.JADeviceType;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAMisuseException;
import com.io7m.jarabica.lwjgl.internal.JALDevice;
import com.io7m.jarabica.lwjgl.internal.JALErrorChecker;
import com.io7m.jarabica.lwjgl.internal.JALStrings;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The default LWJGL-based device factory.
 */

public final class JALWDeviceFactory implements JADeviceFactoryType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALWDeviceFactory.class);

  private final JALStrings strings;
  private final JALErrorChecker errorChecker;
  private List<JADeviceDescription> devices;

  /**
   * The default LWJGL-based device factory.
   */

  public JALWDeviceFactory()
  {
    try {
      this.strings = new JALStrings(Locale.getDefault());
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    this.errorChecker = new JALErrorChecker(this.strings);
  }

  @Override
  public List<JADeviceDescription> enumerateDevices()
  {
    if (this.devices != null) {
      return this.devices;
    }

    final var names =
      ALUtil.getStringList(0L, ALC10.ALC_DEVICE_SPECIFIER);

    if (names == null) {
      return List.of();
    }

    final var descriptions = new ArrayList<JADeviceDescription>(names.size());
    for (final var name : names) {
      descriptions.add(new JADeviceDescription(name));
    }

    this.devices = List.copyOf(descriptions);
    return descriptions;
  }

  @Override
  public JADeviceType openDevice(
    final JADeviceDescription deviceDescription)
    throws JAException
  {
    Objects.requireNonNull(deviceDescription, "deviceDescription");

    final var deviceList = this.enumerateDevices();
    if (!deviceList.contains(deviceDescription)) {
      throw new JAMisuseException(
        this.strings.format(
          "errorNoSuchDevice",
          deviceDescription.name(),
          deviceList.stream()
            .map(JADeviceDescription::name)
            .collect(Collectors.toList()))
      );
    }

    final var result = ALC10.alcOpenDevice(deviceDescription.name());
    if (result == 0L) {
      throw new JADeviceException(this.strings.format("errorDeviceCreate"));
    }

    final var newDevice =
      new JALDevice(
        this.strings,
        this.errorChecker,
        result);

    if (LOG.isTraceEnabled()) {
      LOG.trace("opened device: {}", newDevice);
    }
    return newDevice;
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALWDeviceFactory 0x")
      .append(Integer.toUnsignedString(this.hashCode(), 16))
      .append("]")
      .toString();
  }
}
