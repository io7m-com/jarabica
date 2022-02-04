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
import com.io7m.jarabica.extensions.efx.JAEFXType;
import com.io7m.jarabica.lwjgl.internal.JALContext;
import com.io7m.jarabica.lwjgl.internal.JALErrorChecker;
import com.io7m.jarabica.lwjgl.internal.JALStrings;
import org.lwjgl.openal.ALC10;

import java.util.Objects;

import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_ECHO;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_TYPE;
import static org.lwjgl.openal.EXTEfx.alEffecti;
import static org.lwjgl.openal.EXTEfx.alGenEffects;

/**
 * The EFX extension.
 */

public final class JALExtensionEFXContext implements JAEFXType
{
  private final JALContext context;
  private final JALErrorChecker errorChecker;
  private final JALStrings strings;

  /**
   * The EFX extension.
   *
   * @param inContext      The context
   * @param inErrorChecker An error checker
   * @param inStrings      A provider of strings
   */

  public JALExtensionEFXContext(
    final JALContext inContext,
    final JALErrorChecker inErrorChecker,
    final JALStrings inStrings)
  {
    this.context =
      Objects.requireNonNull(inContext, "context");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "errorChecker");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public int maxAuxiliarySends()
    throws JAException
  {
    this.context.check();
    return ALC10.alcGetInteger(
      this.context.deviceHandle(),
      ALC_MAX_AUXILIARY_SENDS
    );
  }

  @Override
  public JAEFXEffectEchoType createEffectEcho()
    throws JAException
  {
    this.context.check();

    final var effect = alGenEffects();
    this.errorChecker.checkErrors("alGenEffects");
    alEffecti(effect, AL_EFFECT_TYPE, AL_EFFECT_ECHO);
    this.errorChecker.checkErrors("alEffecti");
    return new JALEFXEcho(this.context, effect);
  }
}
