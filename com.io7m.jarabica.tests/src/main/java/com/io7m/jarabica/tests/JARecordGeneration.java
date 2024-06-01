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
import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoParameters;
import com.io7m.jarabica.extensions.efx.JAEFXEffectReverbParameters;
import org.apache.commons.text.WordUtils;
import org.junit.jupiter.api.Test;

public final class JARecordGeneration
{
  @Test
  public void generateWithersReverb()
  {
    generate(JAEFXEffectReverbParameters.class);
  }

  @Test
  public void generateWithersReverbTests()
  {
    generateTests(JAEFXEffectReverbParameters.class, "records");
  }

  @Test
  public void generateWithersEcho()
  {
    generate(JAEFXEffectEchoParameters.class);
  }

  @Test
  public void generateWithersEchoTests()
  {
    generateTests(JAEFXEffectEchoParameters.class, "records");
  }

  @Test
  public void generateWithersEAXReverb()
  {
    generate(JAEFXEffectEAXReverbParameters.class);
  }

  @Test
  public void generateWithersEAXReverbTests()
  {
    generateTests(JAEFXEffectEAXReverbParameters.class, "records");
  }

  private static void generateTests(
    final Class<?> clazz,
    final String arbitraryName)
  {
    final var fields = clazz.getDeclaredFields();

    for (final var field : fields) {
      System.out.printf(
        "@Property public void testWith%s (@ForAll(\"%s\") %s r, @ForAll %s t)%n {",
        WordUtils.capitalize(field.getName()),
        arbitraryName,
        clazz.getSimpleName(),
        field.getType().getSimpleName()
      );
      System.out.printf("  assertEquals(t, r.with%s(t).%s());",
                        WordUtils.capitalize(field.getName()),
                        field.getName());
      System.out.println("}");
      System.out.println();
    }
  }

  private static void generate(
    final Class<?> clazz)
  {
    final var fields = clazz.getDeclaredFields();

    for (final var field : fields) {
      System.out.println("/**");
      System.out.printf(" * Set the %s field.%n", field.getName());
      System.out.printf(" * @param x The new value.%n");
      System.out.printf(" * @return A record with the new value set.%n");
      System.out.println("*/");
      System.out.printf(
        "public %s with%s (%s x)%n {",
        clazz.getName(),
        WordUtils.capitalize(field.getName()),
        field.getType().getSimpleName()
      );
      System.out.printf("  return new %s(%n", clazz.getName());
      for (int index = 0; index < fields.length; ++index) {
        final var otherField = fields[index];
        if (otherField == field) {
          System.out.print("    x");
        } else {
          System.out.print("    this." + otherField.getName());
        }
        if (index + 1 < fields.length) {
          System.out.println(",");
        } else {
          System.out.println("");
        }
      }
      System.out.println(");");
      System.out.println("}");
      System.out.println();
    }
  }
}
