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

import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAListenerType;
import com.io7m.jarabica.api.JAMisuseException;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;

import java.util.Objects;

final class JALListener implements JAListenerType
{
  private final JALContext context;
  private final MemoryStack stack;
  private final JALStrings strings;
  private final JALErrorChecker errorChecker;

  JALListener(
    final JALContext inContext,
    final MemoryStack inStack,
    final JALStrings inStrings,
    final JALErrorChecker inErrorChecker)
  {
    this.context =
      Objects.requireNonNull(inContext, "device");
    this.stack =
      Objects.requireNonNull(inStack, "stack");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "inErrorChecker");
  }

  @Override
  public Vector3D position()
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocFloat(3);
      AL10.alGetListenerfv(AL10.AL_POSITION, buffer);
      this.errorChecker.checkErrors("alGetListenerfv");
      return Vector3D.of(
        buffer.get(0),
        buffer.get(1),
        buffer.get(2)
      );
    }
  }

  @Override
  public void setPosition(
    final double x,
    final double y,
    final double z)
    throws JAException
  {
    this.check();

    AL10.alListener3f(
      AL10.AL_POSITION,
      (float) x,
      (float) y,
      (float) z
    );
    this.errorChecker.checkErrors("alListener3f");
  }

  @Override
  public Vector3D velocity()
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocFloat(3);
      AL10.alGetListenerfv(AL10.AL_VELOCITY, buffer);
      this.errorChecker.checkErrors("alGetListenerfv");
      return Vector3D.of(
        buffer.get(0),
        buffer.get(1),
        buffer.get(2)
      );
    }
  }

  @Override
  public void setVelocity(
    final double x,
    final double y,
    final double z)
    throws JAException
  {
    this.check();

    AL10.alListener3f(
      AL10.AL_VELOCITY,
      (float) x,
      (float) y,
      (float) z
    );
    this.errorChecker.checkErrors("alListener3f");
  }

  @Override
  public Orientation orientation()
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocFloat(6);
      AL10.alGetListenerfv(AL10.AL_ORIENTATION, buffer);
      this.errorChecker.checkErrors("alGetListenerfv");
      return new Orientation(
        Vector3D.of(
          buffer.get(0),
          buffer.get(1),
          buffer.get(2)
        ),
        Vector3D.of(
          buffer.get(3),
          buffer.get(4),
          buffer.get(5)
        )
      );
    }
  }

  @Override
  public void setOrientation(
    final double forwardX,
    final double forwardY,
    final double forwardZ,
    final double upX,
    final double upY,
    final double upZ)
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocFloat(6);
      buffer.put(0, (float) forwardX);
      buffer.put(1, (float) forwardY);
      buffer.put(2, (float) forwardZ);
      buffer.put(3, (float) upX);
      buffer.put(4, (float) upY);
      buffer.put(5, (float) upZ);

      AL10.alListenerfv(AL10.AL_ORIENTATION, buffer);
      this.errorChecker.checkErrors("alListenerfv");
    }
  }

  private void check()
    throws JAException
  {
    if (this.context.isClosed()) {
      throw new JAMisuseException(
        this.strings.format("errorClosed", this));
    }

    this.context.checkCurrent(this, this.context);
  }
}
