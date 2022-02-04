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

import com.io7m.jarabica.api.JACallException;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.lwjgl.internal.JALContext;
import com.io7m.jarabica.lwjgl.internal.JALHandle;

import java.util.Objects;

import static org.lwjgl.openal.EXTEfx.alDeleteEffects;
import static org.lwjgl.openal.EXTEfx.alEffectf;
import static org.lwjgl.openal.EXTEfx.alGetEffectf;

/**
 * The base type of effects.
 */

public abstract class JALEFXEffect extends JALHandle
{
  private final JALContext context;
  private final int effect;

  protected JALEFXEffect(
    final String inType,
    final Number inValue,
    final JALContext inContext)
  {
    super(inType, inValue, inContext.strings());
    this.effect = inValue.intValue();
    this.context = Objects.requireNonNull(inContext, "context");
  }

  @Override
  protected final void closeActual()
    throws JAException
  {
    alDeleteEffects(this.effect);
    this.context.errorChecker().checkErrors("alDeleteEffects");
  }

  protected final void check()
    throws JAException
  {
    this.checkNotClosed();
    this.context.checkCurrent(this, this.context);
  }

  protected final void realSet(
    final int param,
    final double x)
    throws JACallException
  {
    alEffectf(this.effect, param, (float) x);
    this.context.errorChecker().checkErrors("alEffectf");
  }

  protected final double realGet(
    final int x)
    throws JACallException
  {
    final var r = alGetEffectf(this.effect, x);
    this.context.errorChecker().checkErrors("alGetEffectf");
    return r;
  }
}
