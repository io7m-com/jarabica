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

package com.io7m.jarabica.lwjgl.internal.efx;

import com.io7m.jarabica.api.JACallException;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JASourceType;
import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoParameters;
import com.io7m.jarabica.extensions.efx.JAEFXEffectEchoType;
import com.io7m.jarabica.extensions.efx.JAEFXEffectType;
import com.io7m.jarabica.extensions.efx.JAEFXFilterHighPassParameters;
import com.io7m.jarabica.extensions.efx.JAEFXFilterHighPassType;
import com.io7m.jarabica.extensions.efx.JAEFXFilterLowPassParameters;
import com.io7m.jarabica.extensions.efx.JAEFXFilterLowPassType;
import com.io7m.jarabica.extensions.efx.JAEFXFilterType;
import com.io7m.jarabica.extensions.efx.JAEFXGraphEdgeType;
import com.io7m.jarabica.extensions.efx.JAEFXGraphEdgeType.JAEFXEffectOnSlot;
import com.io7m.jarabica.extensions.efx.JAEFXGraphEdgeType.JAEFXSourceDirectToFilter;
import com.io7m.jarabica.extensions.efx.JAEFXGraphNodeType;
import com.io7m.jarabica.extensions.efx.JAEFXSourceNode;
import com.io7m.jarabica.extensions.efx.JAEFXType;
import com.io7m.jarabica.extensions.efx.JAEXFEffectsSlotType;
import com.io7m.jarabica.lwjgl.internal.JALContext;
import com.io7m.jarabica.lwjgl.internal.JALErrorChecker;
import com.io7m.jarabica.lwjgl.internal.JALExtension;
import com.io7m.jarabica.lwjgl.internal.JALSource;
import com.io7m.jarabica.lwjgl.internal.JALStrings;
import org.jgrapht.Graph;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC10;

import java.util.Objects;
import java.util.Optional;

import static com.io7m.jarabica.extensions.efx.JAEFXGraphEdgeType.JAEFXSourceDirectToEffectsSlot;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;
import static org.lwjgl.openal.EXTEfx.AL_AUXILIARY_SEND_FILTER;
import static org.lwjgl.openal.EXTEfx.AL_DIRECT_FILTER;
import static org.lwjgl.openal.EXTEfx.AL_EFFECTSLOT_EFFECT;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_ECHO;
import static org.lwjgl.openal.EXTEfx.AL_EFFECT_TYPE;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_HIGHPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_LOWPASS;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_NULL;
import static org.lwjgl.openal.EXTEfx.AL_FILTER_TYPE;
import static org.lwjgl.openal.EXTEfx.alAuxiliaryEffectSloti;
import static org.lwjgl.openal.EXTEfx.alEffecti;
import static org.lwjgl.openal.EXTEfx.alFilteri;
import static org.lwjgl.openal.EXTEfx.alGenAuxiliaryEffectSlots;
import static org.lwjgl.openal.EXTEfx.alGenEffects;
import static org.lwjgl.openal.EXTEfx.alGenFilters;

/**
 * The EFX extension.
 */

public final class JALExtensionEFXContext extends JALExtension implements
  JAEFXType
{
  private final JALContext context;
  private final JALErrorChecker errorChecker;
  private final JALStrings strings;
  private final DirectedAcyclicGraph<JAEFXGraphNodeType, JAEFXGraphEdgeType> signalGraph;
  private final AsUnmodifiableGraph<JAEFXGraphNodeType, JAEFXGraphEdgeType> signalGraphRead;

  /**
   * The EFX extension.
   *
   * @param inContext      The context
   * @param inErrorChecker An error checker
   * @param inStrings      A provider of strings
   */

  public JALExtensionEFXContext(
    final JALContext inContext,
    final JALErrorChecker inErrorChecker,
    final JALStrings inStrings)
  {
    this.context =
      Objects.requireNonNull(inContext, "context");
    this.errorChecker =
      Objects.requireNonNull(inErrorChecker, "errorChecker");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.signalGraph =
      new DirectedAcyclicGraph<>(JAEFXGraphEdgeType.class);
    this.signalGraphRead =
      new AsUnmodifiableGraph<>(this.signalGraph);
  }

  /**
   * @return The underlying context
   */

  public JALContext context()
  {
    return this.context;
  }

  @Override
  public int maxAuxiliarySends()
    throws JAException
  {
    this.context.check();
    return ALC10.alcGetInteger(
      this.context.deviceHandle(),
      ALC_MAX_AUXILIARY_SENDS
    );
  }

  @Override
  public JAEFXEffectEchoType createEffectEcho(
    final JAEFXEffectEchoParameters parameters)
    throws JAException
  {
    this.context.check();

    final var effect = alGenEffects();
    this.errorChecker.checkErrors("alGenEffects");
    alEffecti(effect, AL_EFFECT_TYPE, AL_EFFECT_ECHO);
    this.errorChecker.checkErrors("alEffecti");
    final var echo = new JALEFXEcho(this, parameters, effect);
    this.signalGraph.addVertex(echo);
    echo.setParameters(parameters);
    return echo;
  }

  @Override
  public JAEFXFilterLowPassType createFilterLowPass(
    final JAEFXFilterLowPassParameters parameters)
    throws JAException
  {
    Objects.requireNonNull(parameters, "parameters");

    this.context.check();

    final var filter = alGenFilters();
    this.errorChecker.checkErrors("alGenFilters");
    alFilteri(filter, AL_FILTER_TYPE, AL_FILTER_LOWPASS);
    this.errorChecker.checkErrors("alFilteri");
    final var newFilter =
      new JALEFXFilterLowPass(this, parameters, filter);

    this.signalGraph.addVertex(newFilter);
    newFilter.setParameters(parameters);
    return newFilter;
  }

  @Override
  public JAEFXFilterHighPassType createFilterHighPass(
    final JAEFXFilterHighPassParameters parameters)
    throws JAException
  {
    Objects.requireNonNull(parameters, "parameters");

    this.context.check();

    final var filter = alGenFilters();
    this.errorChecker.checkErrors("alGenFilters");
    alFilteri(filter, AL_FILTER_TYPE, AL_FILTER_HIGHPASS);
    this.errorChecker.checkErrors("alFilteri");
    final var newFilter =
      new JALEFXFilterHighPass(this, parameters, filter);

    this.signalGraph.addVertex(newFilter);
    newFilter.setParameters(parameters);
    return newFilter;
  }

  @Override
  public Optional<JAEFXFilterType<?>> attachSourceDirectOutputToFilter(
    final JASourceType source,
    final JAEFXFilterType<?> filter)
    throws JAException
  {
    Objects.requireNonNull(filter, "filter");
    Objects.requireNonNull(source, "source");

    this.context.check();

    final var sourceNode =
      new JAEFXSourceNode(source);

    this.signalGraph.addVertex(sourceNode);
    this.signalGraph.addVertex(filter);

    final var edges =
      this.signalGraph.outgoingEdgesOf(sourceNode);

    Optional<JAEFXFilterType<?>> existing = Optional.empty();
    for (final var edge : edges) {
      if (edge instanceof JAEFXSourceDirectToFilter edgeFilter) {
        existing = Optional.of(edgeFilter.filter());
      }
    }

    AL10.alSourcei(
      (int) sourceNode.source().handle(),
      AL_DIRECT_FILTER,
      (int) filter.handle()
    );
    this.errorChecker.checkErrors("alSourcei");

    this.signalGraph.addEdge(
      sourceNode,
      filter,
      new JAEFXSourceDirectToFilter(sourceNode, filter)
    );

    existing.ifPresent(oldFilter -> {
      this.signalGraph.removeEdge(sourceNode, oldFilter);
    });
    return existing;
  }

  @Override
  public void attachSourceDirectToEffectsSlot(
    final JASourceType source,
    final JAEXFEffectsSlotType slot)
    throws JAException
  {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(slot, "slot");

    this.context.check();

    final var sourceNode = new JAEFXSourceNode(source);
    this.signalGraph.addVertex(sourceNode);
    this.signalGraph.addVertex(slot);

    AL11.alSource3i(
      (int) source.handle(),
      AL_AUXILIARY_SEND_FILTER,
      (int) slot.handle(),
      0,
      0
    );
    this.errorChecker.checkErrors("alSource3i");

    this.signalGraph.addEdge(
      sourceNode,
      slot,
      new JAEFXSourceDirectToEffectsSlot(sourceNode, slot)
    );
  }

  @Override
  public Optional<JAEFXFilterType<?>> detachSourceDirectOutputFromFilter(
    final JASourceType source)
    throws JAException
  {
    Objects.requireNonNull(source, "source");

    final var sourceNode = new JAEFXSourceNode(source);
    this.context.check();

    final var edges =
      this.signalGraph.outgoingEdgesOf(sourceNode);

    for (final var edge : edges) {
      if (edge instanceof JAEFXSourceDirectToFilter edgeFilter) {
        final var filter = edgeFilter.filter();
        AL10.alSourcei(
          (int) sourceNode.source().handle(),
          AL_DIRECT_FILTER,
          AL_FILTER_NULL);
        this.errorChecker.checkErrors("alSourcei");
        this.signalGraph.removeEdge(sourceNode, filter);
        return Optional.of(filter);
      }
    }

    return Optional.empty();
  }

  @Override
  public JAEXFEffectsSlotType createEffectsSlot()
    throws JAException
  {
    this.context.check();

    final var slot = alGenAuxiliaryEffectSlots();
    this.errorChecker.checkErrors("alGenAuxiliaryEffectSlots");
    final var effectsSlot = new JALEFXEffectsSlot(this, slot);
    this.signalGraph.addVertex(effectsSlot);
    return effectsSlot;
  }

  @Override
  public Optional<JAEFXEffectType<?>> attachEffectToEffectsSlot(
    final JAEFXEffectType<?> effect,
    final JAEXFEffectsSlotType effectsSlot)
    throws JAException
  {
    Objects.requireNonNull(effect, "effect");
    Objects.requireNonNull(effectsSlot, "effectsSlot");

    this.context.check();

    final var edges =
      this.signalGraph.outgoingEdgesOf(effectsSlot);

    Optional<JAEFXEffectType<?>> existingEffect = Optional.empty();
    for (final var edge : edges) {
      if (edge instanceof JAEFXEffectOnSlot onSlot) {
        if (Objects.equals(onSlot.effect(), effect)) {
          return Optional.empty();
        }
        existingEffect = Optional.of(onSlot.effect());
        break;
      }
    }

    alAuxiliaryEffectSloti(
      (int) effectsSlot.handle(),
      AL_EFFECTSLOT_EFFECT,
      (int) effect.handle()
    );
    this.errorChecker.checkErrors("alAuxiliaryEffectSloti");

    this.signalGraph.addVertex(effect);
    this.signalGraph.addVertex(effectsSlot);
    this.signalGraph.addEdge(
      effectsSlot,
      effect,
      new JAEFXEffectOnSlot(effect, effectsSlot)
    );

    existingEffect.ifPresent(oldEffect -> {
      this.signalGraph.removeEdge(
        oldEffect, effectsSlot
      );
    });
    return existingEffect;
  }

  @Override
  public Graph<JAEFXGraphNodeType, JAEFXGraphEdgeType> signalGraph()
  {
    return this.signalGraphRead;
  }

  void filterParametersUpdated(
    final JAEFXFilterType<?> filter)
    throws JAException
  {
    for (final var edge : this.signalGraph.incomingEdgesOf(filter)) {
      if (edge instanceof JAEFXSourceDirectToFilter toFilter) {
        AL10.alSourcei(
          (int) toFilter.source().source().handle(),
          AL_DIRECT_FILTER,
          (int) filter.handle()
        );
        this.errorChecker.checkErrors("alSourcei");
      }
    }
  }

  void effectParametersUpdated(
    final JAEFXEffectType<?> effect)
    throws JACallException
  {
    for (final var edge : this.signalGraph.incomingEdgesOf(effect)) {
      if (edge instanceof JAEFXEffectOnSlot onSlot) {
        alAuxiliaryEffectSloti(
          (int) onSlot.slot().handle(),
          AL_EFFECTSLOT_EFFECT,
          (int) effect.handle()
        );
        this.errorChecker.checkErrors("alAuxiliaryEffectSloti");
      }
    }
  }

  void effectsSlotDeleted(
    final JALEFXEffectsSlot effectsSlot)
  {
    this.signalGraph.removeVertex(effectsSlot);
  }

  void onFilterDeleted(
    final JAEFXFilterType<?> filter)
  {
    this.signalGraph.removeVertex(filter);
  }

  @Override
  protected void onSourceDeleted(
    final JALSource source)
  {
    this.signalGraph.removeVertex(new JAEFXSourceNode(source));
  }

  void onEffectDeleted(
    final JAEFXEffectType<?> effect)
  {
    this.signalGraph.removeVertex(effect);
  }
}
