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
import com.io7m.jarabica.api.JAContextType;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAExtensionContextType;
import com.io7m.jarabica.api.JAListenerType;
import com.io7m.jarabica.api.JAMisuseException;
import com.io7m.jarabica.api.JASourceType;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

final class JALContext implements JAContextType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALContext.class);

  private final JALDevice device;
  private final MemoryStack stack;
  private final JALStrings strings;
  private final JALErrorChecker errorChecker;
  private final long contextHandle;
  private final ALCCapabilities alcCapabilities;
  private final ALCapabilities alCapabilities;
  private final AtomicBoolean closed;
  private final JALListener listener;
  private final JALExtensionRegistry extensions;

  JALContext(
    final JALDevice inDevice,
    final MemoryStack inStack,
    final JALStrings inStrings,
    final JALErrorChecker inErrorChecker,
    final long inContextHandle,
    final ALCCapabilities inAlcCapabilities,
    final ALCapabilities inAlCapabilities,
    final JALExtensionRegistry inExtensions)
  {
    this.device =
      Objects.requireNonNull(inDevice, "device");
    this.stack =
      Objects.requireNonNull(inStack, "stack");
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "inErrorChecker");
    this.contextHandle =
      inContextHandle;
    this.alcCapabilities =
      Objects.requireNonNull(inAlcCapabilities, "alcCapabilities");
    this.alCapabilities =
      Objects.requireNonNull(inAlCapabilities, "alCapabilities");
    this.extensions =
      Objects.requireNonNull(inExtensions, "extensions");
    this.listener =
      new JALListener(this, this.stack, this.strings, this.errorChecker);

    this.closed =
      new AtomicBoolean(false);
  }

  @Override
  public void close()
  {
    if (this.closed.compareAndSet(false, true)) {
      final var current = ALC10.alcGetCurrentContext();
      if (current == this.contextHandle) {
        ALC10.alcMakeContextCurrent(0L);
      }

      this.device.contextDelete(this);
      if (LOG.isTraceEnabled()) {
        LOG.trace("closed context: {}", this);
      }
    }
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALContext 0x")
      .append(Long.toUnsignedString(this.contextHandle, 16))
      .append("]")
      .toString();
  }

  @Override
  public boolean isClosed()
  {
    return this.closed.get();
  }

  @Override
  public <T extends JAExtensionContextType> Optional<T> extension(
    final Class<T> clazz)
    throws JAException
  {
    this.checkNotClosed();
    return this.extensions.extension(this, clazz);
  }

  @Override
  public boolean isCurrent()
    throws JAException
  {
    this.checkNotClosed();
    return ALC10.alcGetCurrentContext() == this.contextHandle;
  }

  @Override
  public void setCurrent()
    throws JAException
  {
    this.checkNotClosed();
    final var current = ALC10.alcGetCurrentContext();
    if (current != this.contextHandle) {
      ALC10.alcMakeContextCurrent(this.contextHandle);
    }
    this.errorChecker.checkErrors("alcMakeContextCurrent");
  }

  @Override
  public JAListenerType listener()
    throws JAException
  {
    this.check();
    return this.listener;
  }

  @Override
  public JASourceType createSource()
    throws JAException
  {
    this.check();

    final var sourceHandle = AL10.alGenSources();
    this.errorChecker.checkErrors("alGenSources");

    final var source =
      new JALSource(
        this,
        this.stack,
        this.strings,
        this.errorChecker,
        sourceHandle
      );

    if (LOG.isTraceEnabled()) {
      LOG.trace("created source: {}", source);
    }

    return source;
  }

  @Override
  public JABufferType createBuffer()
    throws JAException
  {
    this.check();

    final var bufferHandle = AL10.alGenBuffers();
    this.errorChecker.checkErrors("alGenBuffers");

    final var buffer =
      new JALBuffer(
        this,
        this.stack,
        this.strings,
        this.errorChecker,
        bufferHandle
      );

    if (LOG.isTraceEnabled()) {
      LOG.trace("created buffer: {}", buffer);
    }

    return buffer;
  }

  @Override
  public String vendor()
    throws JAException
  {
    this.check();
    final var text = AL10.alGetString(AL10.AL_VENDOR);
    this.errorChecker.checkErrors("alGetString");
    return text;
  }

  @Override
  public String renderer()
    throws JAException
  {
    this.check();
    final var text = AL10.alGetString(AL10.AL_RENDERER);
    this.errorChecker.checkErrors("alGetString");
    return text;
  }

  void check()
    throws JAException
  {
    this.checkNotClosed();
    this.checkCurrent(this, this);
  }

  private void checkNotClosed()
    throws JAMisuseException
  {
    if (this.closed.get()) {
      throw new JAMisuseException(
        this.strings.format("errorClosed", this));
    }
  }

  void checkCurrent(
    final Object object,
    final JAContextType objectContext)
    throws JAException
  {
    if (!this.isCurrent()) {
      throw new JAMisuseException(
        this.strings.format(
          "errorContextNotCurrent",
          object,
          objectContext,
          this.device.contextCurrent()
        )
      );
    }
  }

  long handle()
  {
    return this.contextHandle;
  }

  long deviceHandle()
  {
    return this.device.handle();
  }
}
