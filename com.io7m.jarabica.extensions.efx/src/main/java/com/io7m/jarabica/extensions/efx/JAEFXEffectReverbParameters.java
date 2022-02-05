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

package com.io7m.jarabica.extensions.efx;

import com.io7m.jarabica.api.JARange;

/**
 * Reverb effect parameters.
 *
 * @param density                 The reverb density
 * @param diffusion               The reverb diffusion
 * @param gain                    The reverb gain
 * @param gainHF                  The gain high frequency attenuation
 * @param decaySeconds            The reverb decay time in seconds
 * @param decayHFRatio            The decay high frequency ratio
 * @param reflectionsDelaySeconds The reflections delay in seconds
 * @param reflectionsGain         The reflections gain
 * @param lateReverbDelaySeconds  The late reverb delay in seconds
 * @param lateReverbGain          The late reverb gain
 * @param decayHFLimit            {@code true} if the high frequency decay
 *                                should be time-limited
 * @param airAbsorptionHFGain     The amount of high frequency air absorption
 *                                (lower for fog, higher for desert)
 * @param roomRolloffFactor       The room rolloff factor
 */

public record JAEFXEffectReverbParameters(
  @JARange(lower = 0.0, upper = 1.0)
  double density,
  @JARange(lower = 0.0, upper = 1.0)
  double diffusion,
  @JARange(lower = 0.0, upper = 1.0)
  double gain,
  @JARange(lower = 0.0, upper = 1.0)
  double gainHF,
  @JARange(lower = 0.1, upper = 20.0)
  double decaySeconds,
  @JARange(lower = 0.1, upper = 2.0)
  double decayHFRatio,
  @JARange(lower = 0.0, upper = 3.16)
  double reflectionsGain,
  @JARange(lower = 0.0, upper = 0.3)
  double reflectionsDelaySeconds,
  @JARange(lower = 0.0, upper = 10.0)
  double lateReverbGain,
  @JARange(lower = 0.0, upper = 0.1)
  double lateReverbDelaySeconds,
  @JARange(lower = 0.892, upper = 1.0)
  double airAbsorptionHFGain,
  @JARange(lower = 0.0, upper = 10.0)
  double roomRolloffFactor,
  boolean decayHFLimit)
{
  /**
   * Set the density field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withDensity(final double x)
  {
    return new JAEFXEffectReverbParameters(
      x,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the diffusion field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withDiffusion(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      x,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the gain field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withGain(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      x,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the gainHF field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withGainHF(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      x,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the decaySeconds field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withDecaySeconds(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      x,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the decayHFRatio field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withDecayHFRatio(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      x,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the reflectionsGain field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withReflectionsGain(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      x,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the reflectionsDelaySeconds field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withReflectionsDelaySeconds(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      x,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the lateReverbGain field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withLateReverbGain(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      x,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the lateReverbDelaySeconds field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withLateReverbDelaySeconds(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      x,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the airAbsorptionHFGain field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectReverbParameters withAirAbsorptionHFGain(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      x,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the roomRolloffFactor field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */

  public JAEFXEffectReverbParameters withRoomRolloffFactor(final double x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      x,
      this.decayHFLimit
    );
  }

  /**
   * Set the decayHFLimit field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */

  public JAEFXEffectReverbParameters withDecayHFLimit(final boolean x)
  {
    return new JAEFXEffectReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.decaySeconds,
      this.decayHFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.airAbsorptionHFGain,
      this.roomRolloffFactor,
      x
    );
  }
}
