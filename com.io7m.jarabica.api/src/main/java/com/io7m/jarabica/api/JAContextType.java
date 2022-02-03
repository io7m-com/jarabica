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

/**
 * A device context.
 */

public interface JAContextType extends JAHandleType
{
  /**
   * @return {@code true} if the context is current
   *
   * @throws JAException On errors
   * @see #setCurrent()
   */

  boolean isCurrent()
    throws JAException;

  /**
   * Set this context as current.
   *
   * @throws JAException On errors
   */

  void setCurrent()
    throws JAException;

  /**
   * @return Access to the context's listener
   *
   * @throws JAException On errors
   */

  JAListenerType listener()
    throws JAException;

  /**
   * Create a new source.
   *
   * @return The new source
   *
   * @throws JAException On errors
   */

  JASourceType createSource()
    throws JAException;

  /**
   * Create a new buffer.
   *
   * @return The new buffer
   *
   * @throws JAException On errors
   */

  JABufferType createBuffer()
    throws JAException;

  /**
   * @return The OpenAL vendor
   *
   * @throws JAException On errors
   */

  String vendor()
    throws JAException;

  /**
   * @return The OpenAL renderer
   *
   * @throws JAException On errors
   */

  String renderer()
    throws JAException;

}
