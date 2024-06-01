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
import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoParameters;
import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.io7m.jarabica.lwjgl.internal.JALClamp.clamp;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_DAMPING;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_FEEDBACK;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_LRDELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MAX_DAMPING;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MAX_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MAX_FEEDBACK;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MAX_LRDELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MAX_SPREAD;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MIN_DAMPING;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MIN_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MIN_FEEDBACK;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MIN_LRDELAY;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_MIN_SPREAD;
import static org.lwjgl.openal.EXTEfx.AL_ECHO_SPREAD;
import static org.lwjgl.openal.EXTEfx.alEffectf;

/**
 * The EFX echo effect.
 */

public final class JALEFXEcho
  extends JALEFXEffect implements JAEFXEffectEchoType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALEFXEcho.class);

  private JAEFXEffectEchoParameters parameters;

  /**
   * The EFX echo effect.
   *
   * @param inContext    The context
   * @param inParameters The parameters
   * @param inEffect     The effect handle
   */

  public JALEFXEcho(
    final JALExtensionEFXContext inContext,
    final JAEFXEffectEchoParameters inParameters,
    final int inEffect)
  {
    super(inContext, "efx-echo", inEffect);
    this.parameters =
      Objects.requireNonNull(inParameters, "inParameters");
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
  public JAEFXEffectEchoParameters parameters()
    throws JAException
  {
    this.check();
    return this.parameters;
  }

  @Override
  public void setParameters(
    final JAEFXEffectEchoParameters newParameters)
    throws JAException
  {
    Objects.requireNonNull(newParameters, "parameters");

    this.check();

    final var errors = this.errorChecker();
    final var f = (int) this.handle();
    alEffectf(
      f,
      AL_ECHO_DELAY,
      (float) clamp(
        newParameters.delay(),
        AL_ECHO_MIN_DELAY,
        AL_ECHO_MAX_DELAY)
    );
    errors.checkErrors("alEffectf");
    alEffectf(
      f,
      AL_ECHO_LRDELAY,
      (float) clamp(
        newParameters.delayLR(),
        AL_ECHO_MIN_LRDELAY,
        AL_ECHO_MAX_LRDELAY)
    );
    errors.checkErrors("alEffectf");
    alEffectf(
      f,
      AL_ECHO_DAMPING,
      (float) clamp(
        newParameters.damping(),
        AL_ECHO_MIN_DAMPING,
        AL_ECHO_MAX_DAMPING)
    );
    errors.checkErrors("alEffectf");
    alEffectf(
      f,
      AL_ECHO_FEEDBACK,
      (float) clamp(
        newParameters.feedback(),
        AL_ECHO_MIN_FEEDBACK,
        AL_ECHO_MAX_FEEDBACK)
    );
    errors.checkErrors("alEffectf");
    alEffectf(
      f,
      AL_ECHO_SPREAD,
      (float) clamp(
        newParameters.spread(),
        AL_ECHO_MIN_SPREAD,
        AL_ECHO_MAX_SPREAD)
    );
    errors.checkErrors("alEffectf");
    this.parameters = newParameters;
    this.context().effectParametersUpdated(this);
  }

  @Override
  protected void onDeleted()
  {
    this.context().onEffectDeleted(this);
  }
}
