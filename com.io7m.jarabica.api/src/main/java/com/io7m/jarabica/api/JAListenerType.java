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

import java.util.Objects;

/**
 * Access to the "listener".
 */

public interface JAListenerType
{
  /**
   * @return The current listener position
   *
   * @throws JAException On errors
   */

  Vector3D position()
    throws JAException;

  /**
   * Set the listener's position.
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
   * Set the listener's position.
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
   * Set the listener's position.
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
   * @return The current listener velocity
   *
   * @throws JAException On errors
   */

  Vector3D velocity()
    throws JAException;

  /**
   * Set the listener's velocity.
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
   * Set the listener's velocity.
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
   * Set the listener's velocity.
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
   * @return The current listener orientation
   *
   * @throws JAException On errors
   */

  Orientation orientation()
    throws JAException;

  /**
   * Set the listener's orientation expressed as a pair of linearly independent
   * "forward" and "up" vectors.
   *
   * @param forwardX The X component of the forward vector
   * @param forwardY The Y component of the forward vector
   * @param forwardZ The Z component of the forward vector
   * @param upX      The X component of the up vector
   * @param upY      The Y component of the up vector
   * @param upZ      The Z component of the up vector
   *
   * @throws JAException On errors
   */

  void setOrientation(
    double forwardX,
    double forwardY,
    double forwardZ,
    double upX,
    double upY,
    double upZ)
    throws JAException;

  /**
   * Set the listener's orientation expressed as a pair of linearly independent
   * "forward" and "up" vectors.
   *
   * @param forward The forward vector
   * @param up      The up vector
   *
   * @throws JAException On errors
   */

  default void setOrientation(
    final Vector3D forward,
    final Vector3D up)
    throws JAException
  {
    this.setOrientation(
      forward.x(),
      forward.y(),
      forward.z(),
      up.x(),
      up.y(),
      up.z()
    );
  }

  /**
   * Set the listener's orientation expressed as a pair of linearly independent
   * "forward" and "up" vectors.
   *
   * @param forward The forward vector
   * @param up      The up vector
   *
   * @throws JAException On errors
   */

  default void setOrientation(
    final PVector3D<?> forward,
    final PVector3D<?> up)
    throws JAException
  {
    this.setOrientation(
      forward.x(),
      forward.y(),
      forward.z(),
      up.x(),
      up.y(),
      up.z()
    );
  }

  /**
   * An orientation expressed as a pair of linearly independent "forward" and
   * "up" vectors.
   *
   * @param forward The vector pointing towards a point on a plane
   * @param up      The vector pointing upwards
   */

  record Orientation(
    Vector3D forward,
    Vector3D up)
  {
    /**
     * An orientation expressed as a pair of linearly independent "forward" and
     * "up" vectors.
     */

    public Orientation
    {
      Objects.requireNonNull(forward, "forward");
      Objects.requireNonNull(up, "up");
    }
  }
}
