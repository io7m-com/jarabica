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


package com.io7m.jarabica.api;

import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;

/**
 * An abstract, empty implementation of the graph listener interface.
 *
 * @param <V> The type of vertices
 * @param <E> The type of edges
 */

public abstract class JAAbstractGraphListener<V, E>
  implements GraphListener<V, E>
{
  @Override
  public void edgeWeightUpdated(
    final GraphEdgeChangeEvent<V, E> e)
  {

  }

  @Override
  public void edgeAdded(
    final GraphEdgeChangeEvent<V, E> e)
  {

  }

  @Override
  public void edgeRemoved(
    final GraphEdgeChangeEvent<V, E> e)
  {

  }

  @Override
  public void vertexAdded(
    final GraphVertexChangeEvent<V> e)
  {

  }

  @Override
  public void vertexRemoved(
    final GraphVertexChangeEvent<V> e)
  {

  }
}
