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


package com.io7m.jarabica.lwjgl.internal.efx;

import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.lwjgl.internal.JALErrorChecker;
import com.io7m.jarabica.lwjgl.internal.JALHandle;

import java.util.Objects;

import static org.lwjgl.openal.EXTEfx.alDeleteEffects;

/**
 * The base type of effects.
 */

public abstract class JALEFXEffect extends JALHandle
{
  private final JALExtensionEFXContext context;
  private final int effect;

  protected JALEFXEffect(
    final JALExtensionEFXContext inContext,
    final String inType,
    final Number inValue)
  {
    super(inType, inValue, inContext.context().strings());
    this.effect = inValue.intValue();
    this.context = Objects.requireNonNull(inContext, "context");
  }

  /**
   * @return The extension context
   */

  public final JALExtensionEFXContext context()
  {
    return this.context;
  }

  protected final JALErrorChecker errorChecker()
  {
    return this.context.context().errorChecker();
  }

  @Override
  protected final void closeActual()
    throws JAException
  {
    alDeleteEffects(this.effect);
    this.context.context().errorChecker().checkErrors("alDeleteEffects");
    this.onDeleted();
  }

  protected abstract void onDeleted();

  protected final void check()
    throws JAException
  {
    this.checkNotClosed();
    this.context.context().checkCurrent(this, this.context.context());
  }
}
