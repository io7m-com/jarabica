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

/**
 * An echo effect.
 */

public non-sealed interface JAEFXEffectEchoType extends JAEFXEffectType
{
  /**
   * Set the delay value.
   *
   * @param seconds The delay seconds
   *
   * @throws JAException On errors
   */

  void setDelay(double seconds)
    throws JAException;

  /**
   * @return The delay value
   *
   * @throws JAException On errors
   * @see #setDelay(double)
   */

  double delay()
    throws JAException;

  /**
   * Set the delay LR value.
   *
   * @param seconds The delay seconds
   *
   * @throws JAException On errors
   */

  void setDelayLR(double seconds)
    throws JAException;

  /**
   * @return The delay LR value
   *
   * @throws JAException On errors
   * @see #setDelayLR(double)
   */

  double delayLR()
    throws JAException;

  /**
   * Set the feedback value.
   *
   * @param feedback The feedback amount
   *
   * @throws JAException On errors
   */

  void setFeedback(double feedback)
    throws JAException;

  /**
   * @return The feedback value
   *
   * @throws JAException On errors
   * @see #setFeedback(double)
   */

  double feedback()
    throws JAException;

  /**
   * Set the damping value.
   *
   * @param damping The damping amount
   *
   * @throws JAException On errors
   */

  void setDamping(double damping)
    throws JAException;

  /**
   * @return The damping value
   *
   * @throws JAException On errors
   * @see #setDamping(double)
   */

  double damping()
    throws JAException;

  /**
   * Set the spread value.
   *
   * @param spread The spread value
   *
   * @throws JAException On errors
   */

  void setSpread(double spread)
    throws JAException;

  /**
   * @return The spread value
   *
   * @throws JAException On errors
   * @see #setSpread(double)
   */

  double spread()
    throws JAException;
}
