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

import com.io7m.jarabica.extensions.efx.JAEFXEffectEAXReverbParameters;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class JAEFXEffectEAXReverbParametersTest
{
  @Provide
  public Arbitrary<JAEFXEffectEAXReverbParameters> records()
  {
    return Arbitraries.doubles()
      .list()
      .ofMinSize(21)
      .ofMaxSize(21)
      .map(doubles -> {
        return new JAEFXEffectEAXReverbParameters(
          doubles.get(0).doubleValue(),
          doubles.get(1).doubleValue(),
          doubles.get(2).doubleValue(),
          doubles.get(3).doubleValue(),
          doubles.get(4).doubleValue(),
          doubles.get(5).doubleValue(),
          doubles.get(6).doubleValue(),
          doubles.get(7).doubleValue(),
          doubles.get(8).doubleValue(),
          doubles.get(9).doubleValue(),
          doubles.get(10).doubleValue(),
          doubles.get(11).doubleValue(),
          doubles.get(12).doubleValue(),
          doubles.get(13).doubleValue(),
          doubles.get(14).doubleValue(),
          doubles.get(15).doubleValue(),
          doubles.get(16).doubleValue(),
          doubles.get(17).doubleValue(),
          doubles.get(18).doubleValue(),
          doubles.get(19).doubleValue(),
          doubles.get(20).doubleValue() > 0.0
        );
      });
  }

  @Property
  public void testWithDensity(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDensity(t).density());
  }

  @Property
  public void testWithDiffusion(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDiffusion(t).diffusion());
  }

  @Property
  public void testWithGain(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withGain(t).gain());
  }

  @Property
  public void testWithGainHF(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withGainHF(t).gainHF());
  }

  @Property
  public void testWithGainLF(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withGainLF(t).gainLF());
  }

  @Property
  public void testWithDecaySeconds(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDecaySeconds(t).decaySeconds());
  }

  @Property
  public void testWithDecayHFRatio(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDecayHFRatio(t).decayHFRatio());
  }

  @Property
  public void testWithDecayLFRatio(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withDecayLFRatio(t).decayLFRatio());
  }

  @Property
  public void testWithReflectionsGain(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withReflectionsGain(t).reflectionsGain());
  }

  @Property
  public void testWithReflectionsDelaySeconds(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withReflectionsDelaySeconds(t).reflectionsDelaySeconds());
  }

  @Property
  public void testWithLateReverbGain(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withLateReverbGain(t).lateReverbGain());
  }

  @Property
  public void testWithLateReverbDelaySeconds(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withLateReverbDelaySeconds(t).lateReverbDelaySeconds());
  }

  @Property
  public void testWithEchoTime(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withEchoTime(t).echoTime());
  }

  @Property
  public void testWithEchoDepth(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withEchoDepth(t).echoDepth());
  }

  @Property
  public void testWithModulationTime(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withModulationTime(t).modulationTime());
  }

  @Property
  public void testWithModulationDepth(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withModulationDepth(t).modulationDepth());
  }

  @Property
  public void testWithAirAbsorptionHFGain(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withAirAbsorptionHFGain(t).airAbsorptionHFGain());
  }

  @Property
  public void testWithHfReference(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withHfReference(t).hfReference());
  }

  @Property
  public void testWithLfReference(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withLfReference(t).lfReference());
  }

  @Property
  public void testWithRoomRolloffFactor(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final double t)
  {
    assertEquals(t, r.withRoomRolloffFactor(t).roomRolloffFactor());
  }

  @Property
  public void testWithDecayHFLimit(
    @ForAll("records") final JAEFXEffectEAXReverbParameters r,
    @ForAll final boolean t)
  {
    assertEquals(t, r.withDecayHFLimit(t).decayHFLimit());
  }
}
