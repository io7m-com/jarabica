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

import com.io7m.jarabica.api.JABufferType;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JASourceState;
import com.io7m.jarabica.api.JASourceType;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_INITIAL;
import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_PAUSED;
import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_PLAYING;
import static com.io7m.jarabica.api.JASourceState.SOURCE_STATE_STOPPED;

/**
 * A source.
 */

public final class JALSource extends JALHandle implements JASourceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALSource.class);

  private final JALContext context;
  private final MemoryStack stack;
  private final JALStrings strings;
  private final JALErrorChecker errorChecker;
  private final int sourceHandle;

  JALSource(
    final JALContext inContext,
    final MemoryStack inStack,
    final JALStrings inStrings,
    final JALErrorChecker inErrorChecker,
    final int inSourceHandle)
  {
    super("source", inSourceHandle, inStrings);

    this.context =
      Objects.requireNonNull(inContext, "context");
    this.stack =
      Objects.requireNonNull(inStack, "stack");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "errorChecker");
    this.sourceHandle = inSourceHandle;
  }

  private static JASourceState toSourceState(
    final int x)
  {
    return switch (x) {
      case AL10.AL_PLAYING -> SOURCE_STATE_PLAYING;
      case AL10.AL_PAUSED -> SOURCE_STATE_PAUSED;
      case AL10.AL_STOPPED -> SOURCE_STATE_STOPPED;
      case AL10.AL_INITIAL -> SOURCE_STATE_INITIAL;
      default -> throw new IllegalStateException("Unrecognized state value: " + x);
    };
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
    AL10.alDeleteSources(this.sourceHandle);
    this.errorChecker.checkErrors("alDeleteSources");
    this.context.onSourceDeleted(this);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALSource ")
      .append(this.handleString())
      .append("]")
      .toString();
  }

  @Override
  public Vector3D position()
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocFloat(3);
      AL10.alGetSourcefv(
        this.sourceHandle,
        AL10.AL_POSITION,
        buffer
      );
      this.errorChecker.checkErrors("alGetSourcefv");

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

    AL10.alSource3f(
      this.sourceHandle,
      AL10.AL_POSITION,
      (float) x,
      (float) y,
      (float) z
    );
    this.errorChecker.checkErrors("alSource3f");
  }

  @Override
  public Vector3D velocity()
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocFloat(3);
      AL10.alGetSourcefv(
        this.sourceHandle,
        AL10.AL_VELOCITY,
        buffer
      );
      this.errorChecker.checkErrors("alGetSourcefv");

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

    AL10.alSource3f(
      this.sourceHandle,
      AL10.AL_VELOCITY,
      (float) x,
      (float) y,
      (float) z
    );
    this.errorChecker.checkErrors("alSource3f");
  }

  @Override
  public void play()
    throws JAException
  {
    this.check();
    AL10.alSourcePlay(this.sourceHandle);
    this.errorChecker.checkErrors("alSourcePlay");
  }

  @Override
  public void pause()
    throws JAException
  {
    this.check();
    AL10.alSourcePause(this.sourceHandle);
    this.errorChecker.checkErrors("alSourcePause");
  }

  @Override
  public void rewind()
    throws JAException
  {
    this.check();
    AL10.alSourceRewind(this.sourceHandle);
    this.errorChecker.checkErrors("alSourceRewind");
  }

  @Override
  public void stop()
    throws JAException
  {
    this.check();
    AL10.alSourceStop(this.sourceHandle);
    this.errorChecker.checkErrors("alSourceStop");
  }

  @Override
  public void setBuffer(final JABufferType buffer)
    throws JAException
  {
    Objects.requireNonNull(buffer, "buffer");
    this.check();

    final var jalBuffer = (JALBuffer) buffer;
    jalBuffer.check();

    AL10.alSourcei(this.sourceHandle, AL10.AL_BUFFER, (int) jalBuffer.handle());
    this.errorChecker.checkErrors("alSourcei");
    this.context.onSourceSetBuffer(this, jalBuffer);
  }

  @Override
  public void detachBuffer()
    throws JAException
  {
    this.check();

    AL10.alSourcei(this.sourceHandle, AL10.AL_BUFFER, AL10.AL_NONE);
    this.errorChecker.checkErrors("alSourcei");
    this.context.onSourceUnsetBuffer(this);
  }

  @Override
  public Optional<JABufferType> buffer()
  {
    return this.context.onSourceWantBuffer(this);
  }

  @Override
  public JASourceState state()
    throws JAException
  {
    this.check();

    try (var current = this.stack.push()) {
      final var buffer = current.mallocInt(1);
      AL10.alGetSourcei(this.sourceHandle, AL10.AL_SOURCE_STATE, buffer);
      this.errorChecker.checkErrors("alGetSourcei");
      return toSourceState(buffer.get(0));
    }
  }

  @Override
  public double gain()
    throws JAException
  {
    this.check();

    final var r = AL10.alGetSourcef(
      this.sourceHandle,
      AL10.AL_GAIN
    );
    this.errorChecker.checkErrors("alGetSourcef");
    return r;
  }

  @Override
  public void setGain(
    final double m)
    throws JAException
  {
    this.check();

    AL10.alSourcef(
      this.sourceHandle,
      AL10.AL_GAIN,
      (float) m
    );
    this.errorChecker.checkErrors("alSourcef");
  }

  @Override
  public double pitch()
    throws JAException
  {
    final var r = AL10.alGetSourcef(
      this.sourceHandle,
      AL10.AL_PITCH
    );
    this.errorChecker.checkErrors("alGetSourcef");
    return r;
  }

  @Override
  public void setPitch(
    final double m)
    throws JAException
  {
    this.check();

    AL10.alSourcef(
      this.sourceHandle,
      AL10.AL_PITCH,
      (float) m
    );
    this.errorChecker.checkErrors("alSourcef");
  }

  @Override
  public void setLooping(
    final boolean looping)
    throws JAException
  {
    this.check();

    AL10.alSourcei(
      this.sourceHandle,
      AL10.AL_LOOPING,
      looping ? AL10.AL_TRUE : AL10.AL_FALSE
    );
    this.errorChecker.checkErrors("alSourcei");
  }

  @Override
  public boolean looping()
    throws JAException
  {
    final var i = AL10.alGetSourcei(this.sourceHandle, AL10.AL_LOOPING);
    this.errorChecker.checkErrors("alGetSourcei");
    return i == AL10.AL_TRUE;
  }

  private void check()
    throws JAException
  {
    this.checkNotClosed();
    this.context.checkCurrent(this, this.context);
  }
}
