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
import com.io7m.jarabica.extensions.efx.JAEFXFilterLowPassParameters;
import com.io7m.jarabica.extensions.efx.JAEFXFilterLowPassType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.io7m.jarabica.lwjgl.internal.JALClamp.clamp;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_MAX_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_MAX_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_MIN_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_LOWPASS_MIN_GAINHF;
import static org.lwjgl.openal.EXTEfx.alFilterf;

/**
 * The EFX low pass filter.
 */

public final class JALEFXFilterLowPass
  extends JALEFXFilter implements JAEFXFilterLowPassType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALEFXFilterLowPass.class);

  private JAEFXFilterLowPassParameters parameters;

  /**
   * The EFX low pass filter.
   *
   * @param inContext    The context
   * @param inEffect     The effect handle
   * @param inParameters The initial parameters
   */

  public JALEFXFilterLowPass(
    final JALExtensionEFXContext inContext,
    final JAEFXFilterLowPassParameters inParameters,
    final int inEffect)
  {
    super(inContext, "efx-low-pass", inEffect);
    this.parameters =
      Objects.requireNonNull(inParameters, "parameters");
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALEFXFilterLowPass ")
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
  public void setParameters(
    final JAEFXFilterLowPassParameters newParameters)
    throws JAException
  {
    Objects.requireNonNull(newParameters, "parameters");

    this.check();

    final var errors = this.errorChecker();
    final var f = (int) this.handle();
    alFilterf(
      f,
      AL_LOWPASS_GAIN,
      (float) clamp(
        newParameters.gain(),
        AL_LOWPASS_MIN_GAIN,
        AL_LOWPASS_MAX_GAIN)
    );
    errors.checkErrors("alFilterf");
    alFilterf(
      f,
      AL_LOWPASS_GAINHF,
      (float) clamp(
        newParameters.frequency(),
        AL_LOWPASS_MIN_GAINHF,
        AL_LOWPASS_MAX_GAINHF)
    );
    errors.checkErrors("alFilterf");
    this.parameters = newParameters;
    this.context().filterParametersUpdated(this);
  }

  @Override
  public JAEFXFilterLowPassParameters parameters()
    throws JAException
  {
    this.check();
    return this.parameters;
  }

  @Override
  protected void onDeleted()
  {
    this.context().onFilterDeleted(this);
  }
}
