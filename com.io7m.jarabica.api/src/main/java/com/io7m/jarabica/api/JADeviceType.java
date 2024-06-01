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
import java.util.SortedSet;

/**
 * A device.
 */

public interface JADeviceType extends JAHandleType
{
  /**
   * Create a new context for the device. The new context will be made current.
   *
   * @param extensions The extension configurations
   *
   * @return A new context
   *
   * @throws JAException On errors
   */

  JAContextType createContext(
    List<JAExtensionConfigurationType> extensions)
    throws JAException;

  /**
   * Create a new context for the device. The new context will be made current.
   *
   * @return A new context
   *
   * @throws JAException On errors
   */

  default JAContextType createContext()
    throws JAException
  {
    return this.createContext(List.of());
  }

  /**
   * @return The list of supported extension names
   *
   * @throws JAException On errors
   */

  SortedSet<String> extensions()
    throws JAException;

  /**
   * @return The OpenAL major version
   *
   * @throws JAException On errors
   */

  int versionMajor()
    throws JAException;

  /**
   * @return The OpenAL minor version
   *
   * @throws JAException On errors
   */

  int versionMinor()
    throws JAException;
}
