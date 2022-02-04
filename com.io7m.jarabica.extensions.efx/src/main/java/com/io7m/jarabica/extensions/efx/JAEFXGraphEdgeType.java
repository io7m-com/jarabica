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

package com.io7m.jarabica.extensions.efx;

import java.util.Objects;

/**
 * An edge in the signal graph.
 */

public sealed interface JAEFXGraphEdgeType
{
  /**
   * @return The source node
   */

  JAEFXGraphNodeType source();

  /**
   * @return The target node
   */

  JAEFXGraphNodeType target();

  /**
   * A source node connected directly to a filter.
   *
   * @param filter The filter
   * @param source The source
   */

  record JAEFXSourceDirectToFilter(
    JAEFXSourceNode source,
    JAEFXFilterType<?> filter
  ) implements JAEFXGraphEdgeType
  {
    /**
     * A source node connected directly to a filter.
     */

    public JAEFXSourceDirectToFilter
    {
      Objects.requireNonNull(source, "source");
      Objects.requireNonNull(filter, "filter");
    }

    @Override
    public JAEFXGraphNodeType target()
    {
      return this.filter;
    }
  }

  /**
   * A source node connected directly to an effects slot.
   *
   * @param slot   The slot
   * @param source The source
   */

  record JAEFXSourceDirectToEffectsSlot(
    JAEFXSourceNode source,
    JAEXFEffectsSlotType slot
  ) implements JAEFXGraphEdgeType
  {
    /**
     * A source node connected directly to an effects slot.
     */

    public JAEFXSourceDirectToEffectsSlot
    {
      Objects.requireNonNull(source, "source");
      Objects.requireNonNull(slot, "slot");
    }

    @Override
    public JAEFXGraphNodeType target()
    {
      return this.slot;
    }
  }

  /**
   * An effect connected to an effects slot.
   *
   * @param effect The effect
   * @param slot   The slot
   */

  record JAEFXEffectOnSlot(
    JAEFXEffectType<?> effect,
    JAEXFEffectsSlotType slot
  ) implements JAEFXGraphEdgeType
  {
    /**
     * An effect connected to an effects slot.
     */

    public JAEFXEffectOnSlot
    {
      Objects.requireNonNull(effect, "effect");
      Objects.requireNonNull(slot, "slot");
    }

    @Override
    public JAEFXGraphNodeType source()
    {
      return this.slot;
    }

    @Override
    public JAEFXGraphNodeType target()
    {
      return this.effect;
    }
  }
}
