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

import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JAExtensionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.SortedSet;

/**
 * A registry of extensions.
 */

public final class JALExtensionRegistry
{
  private final Map<Class<? extends JAExtensionType>, JALExtensionFactoryType> factories;
  private final SortedSet<String> supported;

  private JALExtensionRegistry(
    final Map<Class<? extends JAExtensionType>, JALExtensionFactoryType> factoryMap,
    final SortedSet<String> inSupported)
  {
    this.factories = Map.copyOf(factoryMap);
    this.supported = Objects.requireNonNull(inSupported, "supported");
  }

  /**
   * Create a new registry by loading extension factories from {@link
   * ServiceLoader}.
   *
   * @param supported The supported extensions
   *
   * @return A registry
   */

  public static JALExtensionRegistry createFromServiceLoader(
    final SortedSet<String> supported)
  {
    Objects.requireNonNull(supported, "supported");

    final var iterator =
      ServiceLoader.load(JALExtensionFactoryType.class)
        .iterator();

    final var factories =
      new ArrayList<JALExtensionFactoryType>(32);
    while (iterator.hasNext()) {
      factories.add(iterator.next());
    }

    return create(supported, factories);
  }

  /**
   * Create a new registry using the given extension factories.
   *
   * @param supported The supported extensions
   * @param factories The extension factories
   *
   * @return A registry
   */

  public static JALExtensionRegistry create(
    final SortedSet<String> supported,
    final List<JALExtensionFactoryType> factories)
  {
    Objects.requireNonNull(supported, "supported");
    Objects.requireNonNull(factories, "factories");

    final var factoryMap =
      new HashMap<Class<? extends JAExtensionType>, JALExtensionFactoryType>(
        factories.size());

    for (final var factory : factories) {
      factoryMap.put(factory.extensionClass(), factory);
    }

    return new JALExtensionRegistry(factoryMap, supported);
  }

  /**
   * Create an extension.
   *
   * @param context The context
   * @param clazz   The extension class
   * @param <T>     The extension type
   *
   * @return The extension
   *
   * @throws JAException On errors
   */

  public <T extends JAExtensionType> Optional<T> extension(
    final JALContext context,
    final Class<T> clazz)
    throws JAException
  {
    Objects.requireNonNull(context, "context");
    Objects.requireNonNull(clazz, "clazz");

    final var factory = this.factories.get(clazz);
    if (factory != null) {
      if (factory.isSupported(this.supported)) {
        return Optional.of((T) factory.create(context));
      }
    }
    return Optional.empty();
  }
}
