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

import com.io7m.jarabica.api.JABufferFormat;
import com.io7m.jarabica.api.JABufferType;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAMisuseException;
import org.lwjgl.openal.AL10;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Objects;

final class JALBuffer extends JALHandle implements JABufferType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALBuffer.class);

  private final JALContext context;
  private final MemoryStack stack;
  private final JALStrings strings;
  private final JALErrorChecker errorChecker;
  private final int bufferHandle;
  private final HashSet<JALSource> attachedSources;

  JALBuffer(
    final JALContext inContext,
    final MemoryStack inStack,
    final JALStrings inStrings,
    final JALErrorChecker inErrorChecker,
    final int inSourceHandle)
  {
    super("buffer", inSourceHandle, inStrings);

    this.context =
      Objects.requireNonNull(inContext, "context");
    this.stack =
      Objects.requireNonNull(inStack, "stack");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "errorChecker");
    this.bufferHandle = inSourceHandle;
    this.attachedSources =
      new HashSet<>();
  }

  private static int alFormatOf(
    final JABufferFormat format)
  {
    return switch (format) {
      case AUDIO_8_BIT_MONO -> AL10.AL_FORMAT_MONO8;
      case AUDIO_16_BIT_MONO -> AL10.AL_FORMAT_MONO16;
      case AUDIO_8_BIT_STEREO -> AL10.AL_FORMAT_STEREO8;
      case AUDIO_16_BIT_STEREO -> AL10.AL_FORMAT_STEREO16;
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
    if (this.attachedSources.isEmpty()) {
      AL10.alDeleteBuffers(this.bufferHandle);
      this.errorChecker.checkErrors("alDeleteBuffers");
    } else {
      throw new JAMisuseException(
        this.strings.format(
          "errorBufferDeleteSources", this, this.attachedSources)
      );
    }
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALBuffer ")
      .append(this.handleString())
      .append("]")
      .toString();
  }

  void check()
    throws JAException
  {
    if (this.isClosed()) {
      throw new JAMisuseException(
        this.strings.format("errorClosed", this));
    }

    this.context.checkCurrent(this, this.context);
  }

  @Override
  public void setData(
    final JABufferFormat format,
    final int frequency,
    final ByteBuffer data)
    throws JAException
  {
    Objects.requireNonNull(format, "format");
    Objects.requireNonNull(data, "data");
    this.check();

    if (!data.isDirect()) {
      throw new JAMisuseException(
        this.strings.format("errorBufferDataNotDirect", this));
    }

    AL10.alBufferData(
      this.bufferHandle,
      alFormatOf(format),
      data,
      frequency
    );
    this.errorChecker.checkErrors("alBufferData");
  }

  int handle()
  {
    return this.bufferHandle;
  }

  void addSource(
    final JALSource source)
  {
    this.attachedSources.add(source);
  }

  void removeSource(
    final JALSource source)
  {
    this.attachedSources.remove(source);
  }
}
