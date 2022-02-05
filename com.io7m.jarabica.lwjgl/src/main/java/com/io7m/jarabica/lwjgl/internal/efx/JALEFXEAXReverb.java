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
import com.io7m.jarabica.extensions.efx.JAEFXEffectEAXReverbParameters;
import com.io7m.jarabica.extensions.efx.JAEFXEffectEAXReverbType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static com.io7m.jarabica.lwjgl.internal.JALClamp.clamp;
import static org.lwjgl.openal.AL10.AL_FALSE;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_AIR_ABSORPTION_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_DECAY_HFLIMIT;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_DECAY_HFRATIO;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_DECAY_LFRATIO;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_DECAY_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_DENSITY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_DIFFUSION;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_ECHO_DEPTH;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_ECHO_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_GAINLF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_HFREFERENCE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_LATE_REVERB_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_LATE_REVERB_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_LFREFERENCE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_AIR_ABSORPTION_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_DECAY_HFRATIO;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_DECAY_LFRATIO;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_DECAY_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_DENSITY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_DIFFUSION;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_ECHO_DEPTH;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_ECHO_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_GAINLF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_HFREFERENCE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_LATE_REVERB_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_LATE_REVERB_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_LFREFERENCE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_MODULATION_DEPTH;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_MODULATION_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_REFLECTIONS_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_REFLECTIONS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MAX_ROOM_ROLLOFF_FACTOR;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_AIR_ABSORPTION_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_DECAY_HFRATIO;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_DECAY_LFRATIO;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_DECAY_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_DENSITY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_DIFFUSION;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_ECHO_DEPTH;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_ECHO_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_GAINHF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_GAINLF;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_HFREFERENCE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_LATE_REVERB_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_LATE_REVERB_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_LFREFERENCE;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_MODULATION_DEPTH;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_MODULATION_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_REFLECTIONS_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_REFLECTIONS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MIN_ROOM_ROLLOFF_FACTOR;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MODULATION_DEPTH;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_MODULATION_TIME;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_REFLECTIONS_DELAY;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_REFLECTIONS_GAIN;
import static org.lwjgl.openal.EXTEfx.AL_EAXREVERB_ROOM_ROLLOFF_FACTOR;
import static org.lwjgl.openal.EXTEfx.alEffectf;
import static org.lwjgl.openal.EXTEfx.alEffecti;

/**
 * The EFX reverb effect.
 */

public final class JALEFXEAXReverb
  extends JALEFXEffect implements JAEFXEffectEAXReverbType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALEFXEAXReverb.class);

  private JAEFXEffectEAXReverbParameters parameters;

  /**
   * The EFX echo effect.
   *
   * @param inContext    The context
   * @param inParameters The parameters
   * @param inEffect     The effect handle
   */

  public JALEFXEAXReverb(
    final JALExtensionEFXContext inContext,
    final JAEFXEffectEAXReverbParameters inParameters,
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
      .append("[JALEFXReverb ")
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
  public JAEFXEffectEAXReverbParameters parameters()
    throws JAException
  {
    this.check();
    return this.parameters;
  }

  @Override
  public void setParameters(
    final JAEFXEffectEAXReverbParameters newParameters)
    throws JAException
  {
    Objects.requireNonNull(newParameters, "parameters");

    this.check();

    final var errors = this.errorChecker();
    final var f = (int) this.handle();

    alEffectf(
      f,
      AL_EAXREVERB_DENSITY,
      (float) clamp(
        newParameters.density(),
        AL_EAXREVERB_MIN_DENSITY,
        AL_EAXREVERB_MAX_DENSITY)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_DIFFUSION,
      (float) clamp(
        newParameters.diffusion(),
        AL_EAXREVERB_MIN_DIFFUSION,
        AL_EAXREVERB_MAX_DIFFUSION)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_GAIN,
      (float) clamp(
        newParameters.gain(),
        AL_EAXREVERB_MIN_GAIN,
        AL_EAXREVERB_MAX_GAIN)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_GAINHF,
      (float) clamp(
        newParameters.gainHF(),
        AL_EAXREVERB_MIN_GAINHF,
        AL_EAXREVERB_MAX_GAINHF)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_GAINLF,
      (float) clamp(
        newParameters.gainLF(),
        AL_EAXREVERB_MIN_GAINLF,
        AL_EAXREVERB_MAX_GAINLF)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_DECAY_TIME,
      (float) clamp(
        newParameters.decaySeconds(),
        AL_EAXREVERB_MIN_DECAY_TIME,
        AL_EAXREVERB_MAX_DECAY_TIME)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_DECAY_HFRATIO,
      (float) clamp(
        newParameters.decayHFRatio(),
        AL_EAXREVERB_MIN_DECAY_HFRATIO,
        AL_EAXREVERB_MAX_DECAY_HFRATIO)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_DECAY_LFRATIO,
      (float) clamp(
        newParameters.decayLFRatio(),
        AL_EAXREVERB_MIN_DECAY_LFRATIO,
        AL_EAXREVERB_MAX_DECAY_LFRATIO)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_REFLECTIONS_GAIN,
      (float) clamp(
        newParameters.reflectionsGain(),
        AL_EAXREVERB_MIN_REFLECTIONS_GAIN,
        AL_EAXREVERB_MAX_REFLECTIONS_GAIN)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_REFLECTIONS_DELAY,
      (float) clamp(
        newParameters.reflectionsDelaySeconds(),
        AL_EAXREVERB_MIN_REFLECTIONS_DELAY,
        AL_EAXREVERB_MAX_REFLECTIONS_DELAY)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_LATE_REVERB_GAIN,
      (float) clamp(
        newParameters.lateReverbGain(),
        AL_EAXREVERB_MIN_LATE_REVERB_GAIN,
        AL_EAXREVERB_MAX_LATE_REVERB_GAIN)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_LATE_REVERB_DELAY,
      (float) clamp(
        newParameters.lateReverbDelaySeconds(),
        AL_EAXREVERB_MIN_LATE_REVERB_DELAY,
        AL_EAXREVERB_MAX_LATE_REVERB_DELAY)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_ECHO_TIME,
      (float) clamp(
        newParameters.echoTime(),
        AL_EAXREVERB_MIN_ECHO_TIME,
        AL_EAXREVERB_MAX_ECHO_TIME)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_ECHO_DEPTH,
      (float) clamp(
        newParameters.echoDepth(),
        AL_EAXREVERB_MIN_ECHO_DEPTH,
        AL_EAXREVERB_MAX_ECHO_DEPTH)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_MODULATION_TIME,
      (float) clamp(
        newParameters.modulationTime(),
        AL_EAXREVERB_MIN_MODULATION_TIME,
        AL_EAXREVERB_MAX_MODULATION_TIME)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_MODULATION_DEPTH,
      (float) clamp(
        newParameters.modulationDepth(),
        AL_EAXREVERB_MIN_MODULATION_DEPTH,
        AL_EAXREVERB_MAX_MODULATION_DEPTH)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_AIR_ABSORPTION_GAINHF,
      (float) clamp(
        newParameters.airAbsorptionHFGain(),
        AL_EAXREVERB_MIN_AIR_ABSORPTION_GAINHF,
        AL_EAXREVERB_MAX_AIR_ABSORPTION_GAINHF)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_HFREFERENCE,
      (float) clamp(
        newParameters.hfReference(),
        AL_EAXREVERB_MIN_HFREFERENCE,
        AL_EAXREVERB_MAX_HFREFERENCE)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_LFREFERENCE,
      (float) clamp(
        newParameters.lfReference(),
        AL_EAXREVERB_MIN_LFREFERENCE,
        AL_EAXREVERB_MAX_LFREFERENCE)
    );
    errors.checkErrors("alEffectf");

    alEffectf(
      f,
      AL_EAXREVERB_ROOM_ROLLOFF_FACTOR,
      (float) clamp(
        newParameters.roomRolloffFactor(),
        AL_EAXREVERB_MIN_ROOM_ROLLOFF_FACTOR,
        AL_EAXREVERB_MAX_ROOM_ROLLOFF_FACTOR)
    );
    errors.checkErrors("alEffectf");

    alEffecti(
      f,
      AL_EAXREVERB_DECAY_HFLIMIT,
      newParameters.decayHFLimit() ? AL_TRUE : AL_FALSE
    );
    errors.checkErrors("alEffecti");

    this.parameters = newParameters;
    this.context().effectParametersUpdated(this);
  }

  @Override
  protected void onDeleted()
  {
    this.context().onEffectDeleted(this);
  }
}
