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


package com.io7m.jarabica.lwjgl.internal;

import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAExtensionType;

import java.util.SortedSet;

/**
 * A factory of extensions.
 */

public interface JALExtensionFactoryType
{
  /**
   * @return The implemented extension class
   */

  Class<? extends JAExtensionType> extensionClass();

  /**
   * @param extensions The list of supported device extension strings
   *
   * @return {@code true} if the extension is supported
   */

  default boolean isSupported(
    final SortedSet<String> extensions)
  {
    return extensions.contains(this.name());
  }

  /**
   * @return The extension name
   */

  String name();

  /**
   * Create a new extension instance.
   *
   * @param context The context
   *
   * @return A new extension instance
   */

  JAExtensionType create(JALContext context)
    throws JAException;
}
