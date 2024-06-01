jarabica
===

[![Maven Central](https://img.shields.io/maven-central/v/com.io7m.jarabica/com.io7m.jarabica.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.io7m.jarabica%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/com.io7m.jarabica/com.io7m.jarabica?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/com/io7m/jarabica/)
[![Codecov](https://img.shields.io/codecov/c/github/io7m-com/jarabica.svg?style=flat-square)](https://codecov.io/gh/io7m-com/jarabica)
![Java Version](https://img.shields.io/badge/21-java?label=java&color=e6c35c)

![com.io7m.jarabica](./src/site/resources/jarabica.jpg?raw=true)

| JVM | Platform | Status |
|-----|----------|--------|
| OpenJDK (Temurin) Current | Linux | [![Build (OpenJDK (Temurin) Current, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jarabica/main.linux.temurin.current.yml)](https://www.github.com/io7m-com/jarabica/actions?query=workflow%3Amain.linux.temurin.current)|
| OpenJDK (Temurin) LTS | Linux | [![Build (OpenJDK (Temurin) LTS, Linux)](https://img.shields.io/github/actions/workflow/status/io7m-com/jarabica/main.linux.temurin.lts.yml)](https://www.github.com/io7m-com/jarabica/actions?query=workflow%3Amain.linux.temurin.lts)|
| OpenJDK (Temurin) Current | Windows | [![Build (OpenJDK (Temurin) Current, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jarabica/main.windows.temurin.current.yml)](https://www.github.com/io7m-com/jarabica/actions?query=workflow%3Amain.windows.temurin.current)|
| OpenJDK (Temurin) LTS | Windows | [![Build (OpenJDK (Temurin) LTS, Windows)](https://img.shields.io/github/actions/workflow/status/io7m-com/jarabica/main.windows.temurin.lts.yml)](https://www.github.com/io7m-com/jarabica/actions?query=workflow%3Amain.windows.temurin.lts)|

## jarabica

The `jarabica` package attempts to provide a type-safe, mildly object-oriented
frontend to the [OpenAL](https://www.openal.org/) API. Currently, it uses the
high-quality [LWJGL](https://www.lwjgl.org/) bindings internally, and wraps
them with a thin layer of short-lived immutable types for safety and efficiency.
It strongly separates the API and implementation to allow for easy unit testing
and mocking of code that calls OpenAL.

## Features

* Type-safe [OpenAL](https://www.openal.org/) frontend.
* Strong separation of API and implementation to allow for switching to
  different bindings at compile-time.
* Strongly-typed interfaces with a heavy emphasis on immutable value types.
* Fully documented (JavaDoc).
* Example code included.
* High coverage test suite.
* [OSGi-ready](https://www.osgi.org/)
* [JPMS-ready](https://en.wikipedia.org/wiki/Java_Platform_Module_System)
* ISC license.

## Usage

The `jarabica` package works in the same manner as the OpenAL API, but with
adjustments to make the API feel more like a Java API. Create a _device factory_
from which to create devices. The device factory implementation chosen
essentially decides which underlying OpenAL bindings will be used; currently,
the only real implementation is based on LWJGL.

```
val devices = new JALWDeviceFactory();
```

Enumerate the available audio devices:

```
List<JADeviceDescription> deviceDescriptions = devices.enumerateDevices();
```

Inspect the list of returned devices, and select the one that is most
appropriate to your application. Use the description of the device to
open the device:

```
try (JADeviceType device = devices.openDevice(deviceDescriptions.get(0))) {
  ...
}
```

With the open device, it's necessary to create a _context_.

```
JAContextType context = device.createContext();
```

The context value follows the usual OpenAL thread-safety rules; a context
must be _made current_ on the current thread before any operations can be
performed using it. Creating a context automatically makes it current on
the calling thread.

The context value can then be used to create OpenAL _sources_ and _buffers_
in a manner familiar to anyone experienced with OpenAL.

```
try (var source = context.createSource()) {
  try (var buffer = context.createBuffer()) {
    ...
  }
}
```

## Example Application

A [demo application](com.io7m.jarabica.demo) is included that demonstrates
how to use the API correctly, and also demonstrates numerous extensions such
as the ubiquitous EFX extension.

