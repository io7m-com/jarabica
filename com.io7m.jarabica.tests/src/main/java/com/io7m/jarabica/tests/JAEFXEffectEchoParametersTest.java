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


package com.io7m.jarabica.tests;

import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoParameters;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class JAEFXEffectEchoParametersTest
{
  @Provide
  public Arbitrary<JAEFXEffectEchoParameters> records()
  {
    return Arbitraries.doubles()
      .list()
      .ofMinSize(5)
      .ofMaxSize(5)
      .map(doubles -> {
        return new JAEFXEffectEchoParameters(
          doubles.get(0).doubleValue(),
          doubles.get(1).doubleValue(),
          doubles.get(2).doubleValue(),
          doubles.get(3).doubleValue(),
          doubles.get(4).doubleValue()
        );
      });
  }

  @Property
  public void testWithDelay(
    @ForAll("records") final JAEFXEffectEchoParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDelay(t).delay());
  }

  @Property
  public void testWithDelayLR(
    @ForAll("records") final JAEFXEffectEchoParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDelayLR(t).delayLR());
  }

  @Property
  public void testWithDamping(
    @ForAll("records") final JAEFXEffectEchoParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDamping(t).damping());
  }

  @Property
  public void testWithFeedback(
    @ForAll("records") final JAEFXEffectEchoParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withFeedback(t).feedback());
  }

  @Property
  public void testWithSpread(
    @ForAll("records") final JAEFXEffectEchoParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withSpread(t).spread());
  }
}
