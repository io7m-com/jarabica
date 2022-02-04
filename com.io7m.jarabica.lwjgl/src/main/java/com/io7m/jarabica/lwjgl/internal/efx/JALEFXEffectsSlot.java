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
import com.io7m.jarabica.extensions.efx.JAEFXGraphEdgeType.JAEFXEffectOnSlot;
import com.io7m.jarabica.extensions.efx.JAEFXGraphEdgeType.JAEFXSourceDirectToEffectsSlot;
import com.io7m.jarabica.extensions.efx.JAEXFEffectsSlotType;
import com.io7m.jarabica.lwjgl.internal.JALErrorChecker;
import com.io7m.jarabica.lwjgl.internal.JALHandle;
import org.lwjgl.openal.AL11;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.lwjgl.openal.EXTEfx.AL_AUXILIARY_SEND_FILTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECTSLOT_EFFECT;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_NULL;
import static org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSloti;
import static org.lwjgl.openal.EXTEfx.alDeleteAuxiliaryEffectSlots;

/**
 * An effects slot.
 */

public final class JALEFXEffectsSlot
  extends JALHandle
  implements JAEXFEffectsSlotType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALEFXEffectsSlot.class);

  private final int handle;
  private final JALErrorChecker errors;
  private final JALExtensionEFXContext context;

  /**
   * An effects slot.
   *
   * @param inContext The context
   * @param slot      The slot
   */

  public JALEFXEffectsSlot(
    final JALExtensionEFXContext inContext,
    final int slot)
  {
    super("effects-slot", slot, inContext.context().strings());
    this.handle = slot;
    this.context = inContext;
    this.errors = inContext.context().errorChecker();
  }

  @Override
  protected Logger logger()
  {
    return LOG;
  }

  @Override
  protected void closeActual()
    throws JAException
  {
    final var edges =
      Set.copyOf(this.context.signalGraph().incomingEdgesOf(this));

    for (final var edge : edges) {
      if (edge instanceof JAEFXEffectOnSlot) {
        alAuxiliaryEffectSloti(
          this.handle,
          AL_EFFECTSLOT_EFFECT,
          AL_EFFECT_NULL);
        this.errors.checkErrors("alAuxiliaryEffectSloti");
      }
      if (edge instanceof JAEFXSourceDirectToEffectsSlot slot) {
        AL11.alSource3i(
          (int) slot.source().source().handle(),
          AL_AUXILIARY_SEND_FILTER,
          0,
          0,
          0
        );
        this.errors.checkErrors("alSource3i");
      }
    }

    alDeleteAuxiliaryEffectSlots(this.handle);
    this.errors.checkErrors("alDeleteAuxiliaryEffectSlots");
    this.context.effectsSlotDeleted(this);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALEFXEffectsSlot ")
      .append(this.handleString())
      .append("]")
      .toString();
  }
}
