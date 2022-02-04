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
import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoType;
import com.io7m.jarabica.lwjgl.internal.JALContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.openal.EXTEfx.AL_ECHO_DAMPING;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_FEEDBACK;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_LRDELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_SPREAD;

/**
 * The EFX echo effect.
 */

public final class JALEFXEcho
  extends JALEFXEffect implements JAEFXEffectEchoType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALEFXEcho.class);

  /**
   * The EFX echo effect.
   *
   * @param inContext The context
   * @param inEffect  The effect handle
   */

  public JALEFXEcho(
    final JALContext inContext,
    final int inEffect)
  {
    super("efx-echo", inEffect, inContext);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALEFXEcho ")
      .append(this.handleString())
      .append("]")
      .toString();
  }

  @Override
  protected Logger logger()
  {
    return LOG;
  }

  @Override
  public void setDelay(
    final double seconds)
    throws JAException
  {
    this.check();
    this.realSet(AL_ECHO_DELAY, seconds);
  }

  @Override
  public double delay()
    throws JAException
  {
    this.check();
    return this.realGet(AL_ECHO_DELAY);
  }

  @Override
  public void setDelayLR(
    final double seconds)
    throws JAException
  {
    this.check();
    this.realSet(AL_ECHO_LRDELAY, seconds);
  }

  @Override
  public double delayLR()
    throws JAException
  {
    this.check();
    return this.realGet(AL_ECHO_LRDELAY);
  }

  @Override
  public void setFeedback(
    final double feedback)
    throws JAException
  {
    this.check();
    this.realSet(AL_ECHO_FEEDBACK, feedback);
  }

  @Override
  public double feedback()
    throws JAException
  {
    this.check();
    return this.realGet(AL_ECHO_FEEDBACK);
  }

  @Override
  public void setDamping(
    final double damping)
    throws JAException
  {
    this.check();
    this.realSet(AL_ECHO_DAMPING, damping);
  }

  @Override
  public double damping()
    throws JAException
  {
    this.check();
    return this.realGet(AL_ECHO_DAMPING);
  }

  @Override
  public void setSpread(
    final double spread)
    throws JAException
  {
    this.check();
    this.realSet(AL_ECHO_SPREAD, spread);
  }

  @Override
  public double spread()
    throws JAException
  {
    this.check();
    return this.realGet(AL_ECHO_SPREAD);
  }
}
