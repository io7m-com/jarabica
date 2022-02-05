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
 * Echo effect parameters.
 *
 * @param delay    The delay in seconds
 * @param delayLR  The L/R delay in seconds
 * @param damping  The damping amount
 * @param feedback The feedback amount
 * @param spread   The spread amount
 */

public record JAEFXEffectEchoParameters(
  @JARange(lower = 0.0, upper = 0.207)
  double delay,
  @JARange(lower = 0.0, upper = 0.404)
  double delayLR,
  @JARange(lower = 0.0, upper = 0.99)
  double damping,
  @JARange(lower = 0.0, upper = 1.0)
  double feedback,
  @JARange(lower = -1.0, upper = 1.0)
  double spread)
{
  /**
   * Set the delay field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEchoParameters withDelay(final double x)
  {
    return new JAEFXEffectEchoParameters(
      x,
      this.delayLR,
      this.damping,
      this.feedback,
      this.spread
    );
  }

  /**
   * Set the delayLR field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEchoParameters withDelayLR(final double x)
  {
    return new JAEFXEffectEchoParameters(
      this.delay,
      x,
      this.damping,
      this.feedback,
      this.spread
    );
  }

  /**
   * Set the damping field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEchoParameters withDamping(final double x)
  {
    return new JAEFXEffectEchoParameters(
      this.delay,
      this.delayLR,
      x,
      this.feedback,
      this.spread
    );
  }

  /**
   * Set the feedback field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEchoParameters withFeedback(final double x)
  {
    return new JAEFXEffectEchoParameters(
      this.delay,
      this.delayLR,
      this.damping,
      x,
      this.spread
    );
  }

  /**
   * Set the spread field.
   *
   * @param x The new value.
   *
   * @return A record with the new value set.
   */
  public JAEFXEffectEchoParameters withSpread(final double x)
  {
    return new JAEFXEffectEchoParameters(
      this.delay,
      this.delayLR,
      this.damping,
      this.feedback,
      x
    );
  }
}
