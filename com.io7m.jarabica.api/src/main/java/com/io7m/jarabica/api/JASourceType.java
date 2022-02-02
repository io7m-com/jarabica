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

import com.io7m.jtensors.core.parameterized.vectors.PVector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;

import java.util.Optional;

/**
 * The type of sources.
 */

public interface JASourceType extends JAHandleType
{
  /**
   * @return {@code true} if the source is playing
   *
   * @throws JAException On errors
   */

  default boolean isPlaying()
    throws JAException
  {
    return this.state() == JASourceState.SOURCE_STATE_PLAYING;
  }

  /**
   * @return The source's position
   *
   * @throws JAException On errors
   */

  Vector3D position()
    throws JAException;

  /**
   * Set the source's position.
   *
   * @param x The X axis component
   * @param y The Y axis component
   * @param z The Z axis component
   *
   * @throws JAException On errors
   */

  void setPosition(
    double x,
    double y,
    double z)
    throws JAException;

  /**
   * Set the source's position.
   *
   * @param position The position vector
   *
   * @throws JAException On errors
   */

  default void setPosition(
    final Vector3D position)
    throws JAException
  {
    this.setPosition(position.x(), position.y(), position.z());
  }

  /**
   * Set the source's position.
   *
   * @param position The position vector
   *
   * @throws JAException On errors
   */

  default void setPosition(
    final PVector3D<?> position)
    throws JAException
  {
    this.setPosition(position.x(), position.y(), position.z());
  }

  /**
   * @return The source's velocity
   *
   * @throws JAException On errors
   */

  Vector3D velocity()
    throws JAException;

  /**
   * Set the source's velocity.
   *
   * @param x The X axis component
   * @param y The Y axis component
   * @param z The Z axis component
   *
   * @throws JAException On errors
   */

  void setVelocity(
    double x,
    double y,
    double z)
    throws JAException;

  /**
   * Set the source's velocity.
   *
   * @param velocity The velocity vector
   *
   * @throws JAException On errors
   */

  default void setVelocity(
    final Vector3D velocity)
    throws JAException
  {
    this.setVelocity(velocity.x(), velocity.y(), velocity.z());
  }

  /**
   * Set the source's velocity.
   *
   * @param velocity The velocity vector
   *
   * @throws JAException On errors
   */

  default void setVelocity(
    final PVector3D<?> velocity)
    throws JAException
  {
    this.setVelocity(velocity.x(), velocity.y(), velocity.z());
  }

  /**
   * Play any attached buffer from this source.
   *
   * @throws JAException On errors
   */

  void play()
    throws JAException;

  /**
   * Pause playback of any attached buffer.
   *
   * @throws JAException On errors
   */

  void pause()
    throws JAException;

  /**
   * Rewind playback of any attached buffer.
   *
   * @throws JAException On errors
   */

  void rewind()
    throws JAException;

  /**
   * Stop playback of any attached buffer.
   *
   * @throws JAException On errors
   */

  void stop()
    throws JAException;

  /**
   * Attach the given buffer to this source.
   *
   * @param buffer The buffer
   *
   * @throws JAException On errors
   */

  void setBuffer(JABufferType buffer)
    throws JAException;

  /**
   * Detach any buffer attached to the source.
   *
   * @throws JAException On errors
   */

  void detachBuffer()
    throws JAException;

  /**
   * @return The buffer attached to the source, if any
   *
   * @throws JAException On errors
   */

  Optional<JABufferType> buffer()
    throws JAException;

  /**
   * @return The current state of the source
   *
   * @throws JAException On errors
   */

  JASourceState state()
    throws JAException;
}

