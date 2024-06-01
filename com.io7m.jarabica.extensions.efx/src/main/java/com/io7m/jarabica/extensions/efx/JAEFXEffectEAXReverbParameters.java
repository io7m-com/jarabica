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
 * EAX Reverb effect parameters.
 *
 * @param density                 The reverb density
 * @param diffusion               The reverb diffusion
 * @param gain                    The reverb gain
 * @param gainHF                  The gain high frequency attenuation
 * @param gainLF                  The gain low frequency attenuation
 * @param decaySeconds            The reverb decay time in seconds
 * @param decayHFRatio            The decay high frequency ratio
 * @param decayLFRatio            The decay low frequency ratio
 * @param reflectionsDelaySeconds The reflections delay in seconds
 * @param reflectionsGain         The reflections gain
 * @param lateReverbDelaySeconds  The late reverb delay in seconds
 * @param lateReverbGain          The late reverb gain
 * @param modulationTime          The modulation time
 * @param modulationDepth         The modulation depth
 * @param echoDepth               The echo depth
 * @param echoTime                The echo time
 * @param decayHFLimit            {@code true} if the high frequency decay
 *                                should be time-limited
 * @param airAbsorptionHFGain     The amount of high frequency air absorption
 *                                (lower for fog, higher for desert)
 * @param hfReference             The high frequency reference value
 * @param lfReference             The low frequency reference value
 * @param roomRolloffFactor       The room rolloff factor
 */

public record JAEFXEffectEAXReverbParameters(
  @JARange(lower = 0.0, upper = 1.0)
  double density,

  @JARange(lower = 0.0, upper = 1.0)
  double diffusion,

  @JARange(lower = 0.0, upper = 1.0)
  double gain,

  @JARange(lower = 0.0, upper = 1.0)
  double gainHF,

  @JARange(lower = 0.0, upper = 1.0)
  double gainLF,

  @JARange(lower = 0.1, upper = 20.0)
  double decaySeconds,

  @JARange(lower = 0.1, upper = 2.0)
  double decayHFRatio,

  @JARange(lower = 0.1, upper = 2.0)
  double decayLFRatio,

  @JARange(lower = 0.0, upper = 3.16)
  double reflectionsGain,

  @JARange(lower = 0.0, upper = 0.3)
  double reflectionsDelaySeconds,

  @JARange(lower = 0.0, upper = 10.0)
  double lateReverbGain,

  @JARange(lower = 0.0, upper = 0.1)
  double lateReverbDelaySeconds,

  @JARange(lower = 0.075, upper = 0.25)
  double echoTime,

  @JARange(lower = 0.0, upper = 0.1)
  double echoDepth,

  @JARange(lower = 0.04, upper = 4.0)
  double modulationTime,

  @JARange(lower = 0.0, upper = 1.0)
  double modulationDepth,

  @JARange(lower = 0.892, upper = 1.0)
  double airAbsorptionHFGain,

  @JARange(lower = 1000.0, upper = 20000.0)
  double hfReference,

  @JARange(lower = 20.0, upper = 1000.0)
  double lfReference,

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
  public JAEFXEffectEAXReverbParameters withDensity(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      x,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withDiffusion(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      x,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withGain(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      x,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withGainHF(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      x,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the gainLF field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withGainLF(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      x,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withDecaySeconds(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      x,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withDecayHFRatio(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      x,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the decayLFRatio field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withDecayLFRatio(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      x,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withReflectionsGain(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      x,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withReflectionsDelaySeconds(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      x,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withLateReverbGain(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      x,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withLateReverbDelaySeconds(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      x,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the echoTime field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withEchoTime(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      x,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the echoDepth field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withEchoDepth(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      x,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the modulationTime field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withModulationTime(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      x,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the modulationDepth field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withModulationDepth(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      x,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withAirAbsorptionHFGain(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      x,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the hfReference field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withHfReference(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      x,
      this.lfReference,
      this.roomRolloffFactor,
      this.decayHFLimit
    );
  }

  /**
   * Set the lfReference field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEAXReverbParameters withLfReference(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
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
  public JAEFXEffectEAXReverbParameters withRoomRolloffFactor(final double x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
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
  public JAEFXEffectEAXReverbParameters withDecayHFLimit(final boolean x)
  {
    return new JAEFXEffectEAXReverbParameters(
      this.density,
      this.diffusion,
      this.gain,
      this.gainHF,
      this.gainLF,
      this.decaySeconds,
      this.decayHFRatio,
      this.decayLFRatio,
      this.reflectionsGain,
      this.reflectionsDelaySeconds,
      this.lateReverbGain,
      this.lateReverbDelaySeconds,
      this.echoTime,
      this.echoDepth,
      this.modulationTime,
      this.modulationDepth,
      this.airAbsorptionHFGain,
      this.hfReference,
      this.lfReference,
      this.roomRolloffFactor,
      x
    );
  }

}
