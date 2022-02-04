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
import com.io7m.jarabica.api.JAHandleType;
import com.io7m.jarabica.api.JAMisuseException;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base type of handles.
 */

public abstract class JALHandle implements JAHandleType
{
  private final String type;
  private final JALStrings strings;
  private final AtomicBoolean closed;
  private final Number handleValue;

  protected JALHandle(
    final String inType,
    final Number inValue,
    final JALStrings inStrings)
  {
    this.type =
      Objects.requireNonNull(inType, "type");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.handleValue =
      Objects.requireNonNull(inValue, "inValue");
    this.closed =
      new AtomicBoolean(false);
  }

  @Override
  public final void close()
    throws JAException
  {
    if (this.closed.compareAndSet(false, true)) {
      try {
        this.closeActual();
      } catch (final JAException e) {
        this.closed.set(false);
        throw e;
      }

      final var logger = this.logger();
      if (logger.isTraceEnabled()) {
        logger.trace("closed {}: {}", this.type, this);
      }
    }
  }

  protected abstract Logger logger();

  protected abstract void closeActual()
    throws JAException;

  protected final void checkNotClosed()
    throws JAException
  {
    if (this.closed.get()) {
      throw new JAMisuseException(
        this.strings.format("errorClosed", this));
    }
  }

  protected final String handleString()
  {
    if (this.handleValue instanceof Integer x) {
      return Integer.toUnsignedString(x);
    }
    if (this.handleValue instanceof Long x) {
      return "0x" + Long.toUnsignedString(x, 16);
    }
    return this.handleValue.toString();
  }

  @Override
  public final boolean isClosed()
  {
    return this.closed.get();
  }
}
