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


package com.io7m.jarabica.lwjgl.internal;

import com.io7m.jarabica.api.JACallException;
import org.lwjgl.openal.AL10;

import java.util.Objects;

/**
 * Functions to check AL errors.
 */

public final class JALErrorChecker
{
  private final JALStrings strings;

  /**
   * Functions to check AL errors.
   *
   * @param inStrings A provider of strings
   */

  public JALErrorChecker(
    final JALStrings inStrings)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
  }

  private static String errorNameOf(
    final int error)
  {
    return switch (error) {
      case AL10.AL_OUT_OF_MEMORY -> "AL_OUT_OF_MEMORY";
      case AL10.AL_INVALID_NAME -> "AL_INVALID_NAME";
      case AL10.AL_INVALID_ENUM -> "AL_INVALID_ENUM";
      case AL10.AL_INVALID_VALUE -> "AL_INVALID_VALUE";
      case AL10.AL_INVALID_OPERATION -> "AL_INVALID_OPERATION";
      default -> "Unknown (0x" + Integer.toUnsignedString(error, 16) + ")";
    };
  }

  /**
   * Check errors. Raise an exception with the given function name if the error
   * stack is not empty.
   *
   * @param function The function name
   *
   * @throws JACallException If the error stack is not empty
   */

  public void checkErrors(
    final String function)
    throws JACallException
  {
    final var error = AL10.alGetError();
    if (error != AL10.AL_NO_ERROR) {
      throw new JACallException(
        this.strings.format("errorALCall", function, errorNameOf(error)),
        function,
        errorNameOf(error)
      );
    }
  }
}
