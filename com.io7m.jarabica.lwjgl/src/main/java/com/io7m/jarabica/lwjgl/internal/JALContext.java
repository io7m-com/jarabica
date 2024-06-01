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
import com.io7m.jarabica.api.JASourceBufferLink;
import com.io7m.jarabica.api.JASourceOrBufferType;
import com.io7m.jarabica.api.JASourceType;
import org.jgrapht.Graph;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.VertexSetListener;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * The basic context type.
 */

public final class JALContext extends JALHandle implements JAContextType
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
  private final JALListener listener;
  private final JALExtensionRegistry extensions;
  private final HashSet<JALExtension> extensionsCreated;
  private final DefaultListenableGraph<JASourceOrBufferType, JASourceBufferLink> sourcesToBuffers;
  private final AsUnmodifiableGraph<JASourceOrBufferType, JASourceBufferLink> sourcesToBuffersRead;

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
    super("context", inContextHandle, inStrings);

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
    this.extensionsCreated =
      new HashSet<JALExtension>();
    this.sourcesToBuffers =
      new DefaultListenableGraph<>(
        new DirectedAcyclicGraph<>(JASourceBufferLink.class));
    this.sourcesToBuffersRead =
      new AsUnmodifiableGraph<>(this.sourcesToBuffers);

    this.listener =
      new JALListener(this, this.stack, this.strings, this.errorChecker);
  }

  Set<JASourceBufferLink> sourcesUsingBuffer(
    final JALBuffer buffer)
  {
    return this.sourcesToBuffers.outgoingEdgesOf(buffer);
  }

  @Override
  protected Logger logger()
  {
    return LOG;
  }

  @Override
  protected void closeActual()
  {
    final var current = ALC10.alcGetCurrentContext();
    if (current == this.contextHandle) {
      ALC10.alcMakeContextCurrent(0L);
    }

    this.device.contextDelete(this);
  }

  @Override
  public String toString()
  {
    return new StringBuilder(64)
      .append("[JALContext ")
      .append(this.handleString())
      .append("]")
      .toString();
  }

  @Override
  public <T extends JAExtensionContextType> Optional<T> extension(
    final Class<T> clazz)
    throws JAException
  {
    this.checkNotClosed();

    final var ext =
      this.extensions.extension(this, clazz);
    ext.ifPresent(actual -> {
      if (actual instanceof JALExtension e) {
        this.extensionsCreated.add(e);
      }
    });
    return ext;
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

    this.sourcesToBuffers.addVertex(source);
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

    this.sourcesToBuffers.addVertex(buffer);
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

  @Override
  public Graph<JASourceOrBufferType, JASourceBufferLink> sourceBufferGraph()
    throws JAException
  {
    this.check();
    return this.sourcesToBuffersRead;
  }

  @Override
  public void addSourceBufferGraphListener(
    final GraphListener<JASourceOrBufferType, JASourceBufferLink> l)
  {
    this.sourcesToBuffers.addGraphListener(
      Objects.requireNonNull(l, "l"));
  }

  @Override
  public void addSourceBufferVertexSetListener(
    final VertexSetListener<JASourceOrBufferType> l)
  {
    this.sourcesToBuffers.addVertexSetListener(
      Objects.requireNonNull(l, "l"));
  }

  @Override
  public void removeSourceBufferGraphListener(
    final GraphListener<JASourceOrBufferType, JASourceBufferLink> l)
  {
    this.sourcesToBuffers.removeGraphListener(
      Objects.requireNonNull(l, "l"));
  }

  @Override
  public void removeSourceBufferVertexSetListener(
    final VertexSetListener<JASourceOrBufferType> l)
  {
    this.sourcesToBuffers.removeVertexSetListener(
      Objects.requireNonNull(l, "l"));
  }

  /**
   * Check that this context is current and not closed.
   *
   * @throws JAException On errors
   */

  public void check()
    throws JAException
  {
    this.checkNotClosed();
    this.checkCurrent(this, this);
  }

  /**
   * Check that the right context is current for the given object.
   *
   * @param object        The object
   * @param objectContext The object's context
   *
   * @throws JAException On errors
   */

  public void checkCurrent(
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

  /**
   * @return The handle to the underlying device
   */

  public long deviceHandle()
  {
    return this.device.handle();
  }

  /**
   * @return The context's error checker
   */

  public JALErrorChecker errorChecker()
  {
    return this.errorChecker;
  }

  /**
   * @return The context's strings
   */

  public JALStrings strings()
  {
    return this.strings;
  }

  void onSourceDeleted(
    final JALSource source)
  {
    for (final var e : this.extensionsCreated) {
      e.onSourceDeleted(source);
    }
    this.sourcesToBuffers.removeVertex(source);
  }

  void onSourceSetBuffer(
    final JALSource source,
    final JALBuffer buffer)
  {
    this.onSourceUnsetBuffer(source);

    if (LOG.isTraceEnabled()) {
      LOG.trace("source set buffer: {} -> {}", source, buffer);
    }

    this.sourcesToBuffers.addEdge(
      buffer,
      source,
      new JASourceBufferLink(buffer, source));
  }

  void onSourceUnsetBuffer(
    final JALSource source)
  {
    final var edges =
      Set.copyOf(this.sourcesToBuffers.incomingEdgesOf(source));

    for (final var edge : edges) {
      if (LOG.isTraceEnabled()) {
        LOG.trace("source unset buffer: {} -> {}", source, edge.buffer());
      }
      this.sourcesToBuffers.removeEdge(edge);
    }
  }

  Optional<JABufferType> onSourceWantBuffer(
    final JALSource source)
  {
    final var edges =
      this.sourcesToBuffers.incomingEdgesOf(source);

    for (final var edge : edges) {
      return Optional.of(edge.buffer());
    }
    return Optional.empty();
  }

  void onBufferDeleted(
    final JALBuffer buffer)
  {
    this.sourcesToBuffers.removeVertex(buffer);
  }
}
