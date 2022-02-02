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

package com.io7m.jarabica.api;

import java.util.List;

/**
 * A factory of devices.
 */

public interface JADeviceFactoryType
{
  /**
   * @return A list of the available devices
   */

  List<JADeviceDescription> enumerateDevices();

  /**
   * Open a new device. The provided description must be one of the descriptions
   * in the list returned by {@link #enumerateDevices()}.
   *
   * @param device The device description
   *
   * @return A new device
   *
   * @throws JAException On errors
   */

  JADeviceType openDevice(JADeviceDescription device)
    throws JAException;
}
