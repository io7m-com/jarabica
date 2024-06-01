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

import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAExtensionContextType;
import com.io7m.jarabica.api.JASourceType;
import org.jgrapht.Graph;

import java.util.Optional;

/**
 * The EFX extension.
 */

public interface JAEFXType extends JAExtensionContextType
{
  /**
   * The official extension name.
   */

  String NAME = "ALC_EXT_EFX";

  /**
   * @return The maximum number of supported auxiliary sends.
   *
   * @throws JAException On errors
   */

  int maxAuxiliarySends()
    throws JAException;

  /**
   * Create an echo effect.
   *
   * @param parameters The initial parameters
   *
   * @return The effect
   *
   * @throws JAException On errors
   */

  JAEFXEffectEchoType createEffectEcho(
    JAEFXEffectEchoParameters parameters)
    throws JAException;

  /**
   * Create a reverb effect.
   *
   * @param parameters The initial parameters
   *
   * @return The effect
   *
   * @throws JAException On errors
   */

  JAEFXEffectReverbType createEffectReverb(
    JAEFXEffectReverbParameters parameters)
    throws JAException;

  /**
   * Create an EAX reverb effect.
   *
   * @param parameters The initial parameters
   *
   * @return The effect
   *
   * @throws JAException On errors
   */

  JAEFXEffectEAXReverbType createEffectEAXReverb(
    JAEFXEffectEAXReverbParameters parameters)
    throws JAException;

  /**
   * Create a low-pass filter.
   *
   * @param parameters The initial parameters
   *
   * @return The filter
   *
   * @throws JAException On errors
   */

  JAEFXFilterLowPassType createFilterLowPass(
    JAEFXFilterLowPassParameters parameters)
    throws JAException;

  /**
   * Create a high-pass filter.
   *
   * @param parameters The initial parameters
   *
   * @return The filter
   *
   * @throws JAException On errors
   */

  JAEFXFilterHighPassType createFilterHighPass(
    JAEFXFilterHighPassParameters parameters)
    throws JAException;

  /**
   * Attach the given filter directly to the output of the given source. If any
   * other filter is already attached, it will be detached and returned.
   *
   * @param source The source
   * @param filter The filter
   *
   * @return The previous filter, if any
   *
   * @throws JAException On errors
   */

  Optional<JAEFXFilterType<?>> attachSourceDirectOutputToFilter(
    JASourceType source,
    JAEFXFilterType<?> filter)
    throws JAException;

  /**
   * Attach the output of the given source to the input of the given effects
   * slot.
   *
   * @param source The source
   * @param slot   The slot
   *
   * @throws JAException On errors
   */

  void attachSourceDirectToEffectsSlot(
    JASourceType source,
    JAEXFEffectsSlotType slot)
    throws JAException;

  /**
   * Detach any filter that happens to be attached to the direct output of the
   * given source. If a filter was attached, it is returned.
   *
   * @param source The source
   *
   * @return The existing filter, if any
   *
   * @throws JAException On errors
   */

  Optional<JAEFXFilterType<?>> detachSourceDirectOutputFromFilter(
    JASourceType source)
    throws JAException;

  /**
   * Create a new effects slot.
   *
   * @return A new effects slot
   *
   * @throws JAException On errors
   */

  JAEXFEffectsSlotType createEffectsSlot()
    throws JAException;

  /**
   * Attach an effect to an effect slot. If the effects slot already has an
   * effect, it will be detached and returned.
   *
   * @param effect      The effect
   * @param effectsSlot The effect slot
   *
   * @return The previous effect, if any
   *
   * @throws JAException On errors
   */

  Optional<JAEFXEffectType<?>> attachEffectToEffectsSlot(
    JAEFXEffectType<?> effect,
    JAEXFEffectsSlotType effectsSlot)
    throws JAException;

  /**
   * @return A read-only view of the signal graph
   */

  Graph<JAEFXGraphNodeType, JAEFXGraphEdgeType> signalGraph();
}
