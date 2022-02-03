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
import com.io7m.jarabica.api.JAContextType;
import com.io7m.jarabica.api.JADeviceException;
import com.io7m.jarabica.api.JADeviceType;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAMisuseException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An open device.
 */

public final class JALDevice implements JADeviceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(JALDevice.class);

  private final JALStrings strings;
  private final JALErrorChecker errorChecker;
  private final long handle;
  private final AtomicBoolean closed;
  private final MemoryStack stack;
  private final HashMap<Long, JALContext> contexts;
  private JALContext contextCurrent;
  private SortedSet<String> extensions;

  /**
   * An open device.
   *
   * @param inErrorChecker An error checker
   * @param inStrings      A string provider
   * @param inHandle       The device handle
   */

  public JALDevice(
    final JALStrings inStrings,
    final JALErrorChecker inErrorChecker,
    final long inHandle)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "errorChecker");
    this.handle = inHandle;
    this.closed =
      new AtomicBoolean(false);
    this.stack =
      MemoryStack.create()
        .push();

    this.contexts = new HashMap<Long, JALContext>();
    this.contextCurrent = null;
  }

  @Override
  public void close()
    throws JAException
  {
    if (this.closed.compareAndSet(false, true)) {
      this.stack.close();

      final var ok = ALC10.alcCloseDevice(this.handle);
      if (!ok) {
        throw new JADeviceException(
          this.strings.format("errorDeviceClose"));
      }

      if (LOG.isTraceEnabled()) {
        LOG.trace("closed device: {}", this);
      }
    }
  }

  @Override
  public boolean isClosed()
  {
    return this.closed.get();
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALDevice 0x")
      .append(Long.toUnsignedString(this.handle, 16))
      .append("]")
      .toString();
  }

  @Override
  public JAContextType createContext()
    throws JAException
  {
    this.check();

    return this.contextCreate();
  }

  @Override
  public SortedSet<String> extensions()
    throws JAException
  {
    this.check();

    if (this.extensions == null) {
      final var text =
        ALC10.alcGetString(this.handle, ALC10.ALC_EXTENSIONS);
      this.extensions =
        Collections.unmodifiableSortedSet(
          new TreeSet<>(List.of(text.split("\s+")))
        );
    }

    return this.extensions;
  }

  @Override
  public int versionMajor()
    throws JAException
  {
    this.check();
    return ALC10.alcGetInteger(this.handle, ALC10.ALC_MAJOR_VERSION);
  }

  @Override
  public int versionMinor()
    throws JAException
  {
    this.check();
    return ALC10.alcGetInteger(this.handle, ALC10.ALC_MINOR_VERSION);
  }

  private JALContext contextCreate()
    throws JADeviceException, JACallException
  {
    final var contextHandle =
      ALC10.alcCreateContext(this.handle, (IntBuffer) null);

    if (contextHandle == 0L) {
      throw new JADeviceException(
        this.strings.format(
          "errorContextCreate",
          Long.toUnsignedString(this.handle, 16))
      );
    }

    ALC10.alcMakeContextCurrent(contextHandle);

    final var alcCapabilities =
      ALC.createCapabilities(this.handle);
    final var alCapabilities =
      AL.createCapabilities(alcCapabilities);

    final var context =
      new JALContext(
        this,
        this.stack,
        this.strings,
        this.errorChecker,
        contextHandle,
        alcCapabilities,
        alCapabilities
      );

    this.errorChecker.checkErrors("alcMakeContextCurrent");

    if (LOG.isTraceEnabled()) {
      LOG.trace("created context: {}", context);
    }

    this.contexts.put(Long.valueOf(contextHandle), context);
    this.contextCurrent = context;
    return context;
  }

  private void check()
    throws JAMisuseException
  {
    if (this.closed.get()) {
      throw new JAMisuseException(
        this.strings.format("errorClosed", this));
    }
  }

  JALContext contextCurrent()
  {
    return this.contextCurrent;
  }

  void contextDelete(
    final JALContext context)
  {
    final var newHandle = context.handle();
    ALC10.alcDestroyContext(newHandle);
    this.contexts.remove(Long.valueOf(newHandle));

    if (this.contextCurrent != null) {
      if (this.contextCurrent.handle() == newHandle) {
        this.contextCurrent = null;
      }
    }
  }
}
