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


package com.io7m.jarabica.demo;

import com.io7m.jarabica.api.JAAbstractGraphListener;
import com.io7m.jarabica.api.JABufferFormat;
import com.io7m.jarabica.api.JABufferType;
import com.io7m.jarabica.api.JAContextType;
import com.io7m.jarabica.api.JADeviceType;
import com.io7m.jarabica.api.JAException;
import com.io7m.jarabica.api.JASourceBufferLink;
import com.io7m.jarabica.api.JASourceOrBufferType;
import com.io7m.jarabica.api.JASourceType;
import com.io7m.jarabica.extensions.efx.JAEFXEffectEAXReverbParameters;
import com.io7m.jarabica.extensions.efx.JAEFXEffectEAXReverbType;
import com.io7m.jarabica.extensions.efx.JAEFXType;
import com.io7m.jarabica.extensions.efx.JAEXFEffectsSlotType;
import com.io7m.jarabica.lwjgl.JALWDeviceFactory;
import com.io7m.jtensors.core.unparameterized.vectors.Vector3D;
import com.io7m.jtensors.core.unparameterized.vectors.Vectors3D;
import com.io7m.jwheatsheaf.api.JWFileChooserAction;
import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import com.io7m.jwheatsheaf.api.JWFileChoosersType;
import com.io7m.jwheatsheaf.ui.JWFileChoosers;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import org.jgrapht.Graph;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The main controller.
 */

public final class MainController implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(MainController.class);

  private static final Font FONT =
    Font.font("Monospaced Bold", 12.0);

  private static final double DISTANCE_SCALE_FACTOR = 20.0;

  private final JALWDeviceFactory devices;
  private final JWFileChoosersType choosers;
  private final HashMap<JASourceType, SourceShapeState> sourceShapes;

  @FXML private Pane listenerPane;
  @FXML private Ellipse listener;
  @FXML private Label listenerText;
  @FXML private Pane sourceControls;
  @FXML private TextField sourcePosition;
  @FXML private TextField sourceVelocity;
  @FXML private Slider sourcePitch;
  @FXML private Slider sourceGain;
  @FXML private TextField sourceId;
  @FXML private Slider reverbDensity;
  @FXML private Slider reverbDiffusion;
  @FXML private Slider reverbGain;
  @FXML private Slider reverbGainHF;
  @FXML private Slider reverbGainLF;
  @FXML private Slider reverbDecay;
  @FXML private Slider reverbDecayHF;
  @FXML private Slider reverbDecayLF;
  @FXML private Slider reverbReflectionsGain;
  @FXML private Slider reverbReflectionsDelay;
  @FXML private Slider reverbLateGain;
  @FXML private Slider reverbLateDelay;
  @FXML private Slider reverbEchoTime;
  @FXML private Slider reverbEchoDepth;
  @FXML private Slider reverbModulationTime;
  @FXML private Slider reverbModulationDepth;
  @FXML private Slider reverbAirAbsorption;

  private JADeviceType device;
  private JAContextType context;
  private JAEFXType efx;
  private Graph<JASourceOrBufferType, JASourceBufferLink> sourceGraph;
  private JAEXFEffectsSlotType mainEffectSlot;
  private SimpleObjectProperty<JAEFXEffectEAXReverbParameters> reverbEAXEffectParameters;
  private JAEFXEffectEAXReverbType reverbEAXEffect;
  private SimpleObjectProperty<JASourceType> sourceSelected;

  /**
   * The main controller.
   */

  public MainController()
  {
    this.devices = new JALWDeviceFactory();
    this.choosers = JWFileChoosers.create();
    this.sourceShapes = new HashMap<>();
  }

  private static JABufferFormat formatFor(
    final AudioFormat format)
  {
    return switch (format.getChannels()) {
      case 1 -> switch (format.getSampleSizeInBits()) {
        case 8 -> JABufferFormat.AUDIO_8_BIT_MONO;
        case 16 -> JABufferFormat.AUDIO_16_BIT_MONO;
        default -> throw new UnsupportedOperationException();
      };
      case 2 -> switch (format.getSampleSizeInBits()) {
        case 8 -> JABufferFormat.AUDIO_8_BIT_STEREO;
        case 16 -> JABufferFormat.AUDIO_16_BIT_STEREO;
        default -> throw new UnsupportedOperationException();
      };
      default -> throw new UnsupportedOperationException();
    };
  }

  @Override
  public void initialize(
    final URL location,
    final ResourceBundle resources)
  {
    final var descriptions =
      this.devices.enumerateDevices();
    if (descriptions.isEmpty()) {
      throw new UnsupportedOperationException();
    }

    try {
      this.device =
        this.devices.openDevice(descriptions.get(0));
      this.context =
        this.device.createContext();
      this.efx =
        this.context.extension(JAEFXType.class)
          .orElseThrow();

      this.sourceGraph =
        this.context.sourceBufferGraph();
      this.sourceSelected =
        new SimpleObjectProperty<>();

      this.mainEffectSlot =
        this.efx.createEffectsSlot();

      this.reverbEAXEffectParameters = new SimpleObjectProperty<>();
      this.reverbEAXEffectParameters.set(
        new JAEFXEffectEAXReverbParameters(
          1.0,
          1.0,
          0.32,
          0.89,
          0.0,
          1.49,
          0.83,
          1.0,
          0.05,
          0.007,
          1.26,
          0.011,
          0.25,
          0.0,
          0.25,
          0.0,
          0.994,
          5000.0,
          250.0,
          0.0,
          true
        )
      );

      this.reverbEAXEffect =
        this.efx.createEffectEAXReverb(this.reverbEAXEffectParameters.get());

      this.efx.attachEffectToEffectsSlot(
        this.reverbEAXEffect,
        this.mainEffectSlot);

      this.context.addSourceBufferGraphListener(
        new SourceBufferGraphListener());

    } catch (final JAException e) {
      throw new IllegalStateException(e);
    }

    this.listenerText.setText("");

    this.listenerPane.widthProperty()
      .addListener((o, old, newValue) -> this.onListenerPaneSizeChanged());
    this.listenerPane.heightProperty()
      .addListener((o, old, newValue) -> this.onListenerPaneSizeChanged());

    this.configureReverbSliders();

    this.reverbEAXEffectParameters.addListener((observable, oldValue, newValue) -> {
      try {
        this.reverbEAXEffect.setParameters(newValue);
      } catch (final JAException e) {
        LOG.error("AL: ", e);
      }
    });

    this.sourceSelected.addListener((observable, oldValue, newValue) -> {
      this.onSourceSelectionChanged(oldValue, newValue);
    });

    this.sourcePitch.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onSourcePitchChanged(newValue.doubleValue());
      });
    this.sourceGain.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onSourceGainChanged(newValue.doubleValue());
      });
  }

  private void configureReverbSliders()
  {
    this.reverbDensity.setValue(
      this.reverbEAXEffectParameters.get()
        .density()
    );
    this.reverbDiffusion.setValue(
      this.reverbEAXEffectParameters.get()
        .diffusion()
    );
    this.reverbGain.setValue(
      this.reverbEAXEffectParameters.get()
        .gain()
    );
    this.reverbGainHF.setValue(
      this.reverbEAXEffectParameters.get()
        .gainHF()
    );
    this.reverbGainLF.setValue(
      this.reverbEAXEffectParameters.get()
        .gainLF()
    );
    this.reverbDecay.setValue(
      this.reverbEAXEffectParameters.get()
        .decaySeconds()
    );
    this.reverbDecayHF.setValue(
      this.reverbEAXEffectParameters.get()
        .decayHFRatio()
    );
    this.reverbDecayLF.setValue(
      this.reverbEAXEffectParameters.get()
        .decayLFRatio()
    );
    this.reverbReflectionsGain.setValue(
      this.reverbEAXEffectParameters.get()
        .reflectionsGain()
    );
    this.reverbReflectionsDelay.setValue(
      this.reverbEAXEffectParameters.get()
        .reflectionsDelaySeconds()
    );
    this.reverbLateGain.setValue(
      this.reverbEAXEffectParameters.get()
        .lateReverbGain()
    );
    this.reverbLateDelay.setValue(
      this.reverbEAXEffectParameters.get()
        .lateReverbDelaySeconds()
    );
    this.reverbEchoTime.setValue(
      this.reverbEAXEffectParameters.get()
        .echoTime()
    );
    this.reverbEchoTime.setValue(
      this.reverbEAXEffectParameters.get()
        .echoDepth()
    );
    this.reverbModulationTime.setValue(
      this.reverbEAXEffectParameters.get()
        .modulationTime()
    );
    this.reverbModulationDepth.setValue(
      this.reverbEAXEffectParameters.get()
        .modulationDepth()
    );
    this.reverbAirAbsorption.setValue(
      this.reverbEAXEffectParameters.get()
        .airAbsorptionHFGain()
    );

    this.reverbDensity.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withDensity(newValue.doubleValue())
        );
      });

    this.reverbDiffusion.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withDiffusion(newValue.doubleValue())
        );
      });

    this.reverbGain.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withGain(newValue.doubleValue())
        );
      });

    this.reverbGainHF.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withGainHF(newValue.doubleValue())
        );
      });

    this.reverbGainLF.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withGainLF(newValue.doubleValue())
        );
      });

    this.reverbDecay.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withDecaySeconds(newValue.doubleValue())
        );
      });

    this.reverbDecayHF.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withDecayHFRatio(newValue.doubleValue())
        );
      });

    this.reverbDecayLF.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withDecayLFRatio(newValue.doubleValue())
        );
      });

    this.reverbReflectionsGain.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withReflectionsGain(newValue.doubleValue())
        );
      });

    this.reverbReflectionsDelay.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withReflectionsDelaySeconds(newValue.doubleValue())
        );
      });

    this.reverbLateGain.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withLateReverbGain(newValue.doubleValue())
        );
      });

    this.reverbLateDelay.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withLateReverbDelaySeconds(newValue.doubleValue())
        );
      });

    this.reverbEchoTime.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withEchoTime(newValue.doubleValue())
        );
      });

    this.reverbEchoDepth.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withEchoDepth(newValue.doubleValue())
        );
      });

    this.reverbModulationTime.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withModulationTime(newValue.doubleValue())
        );
      });

    this.reverbModulationDepth.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withModulationDepth(newValue.doubleValue())
        );
      });

    this.reverbAirAbsorption.valueProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.reverbEAXEffectParameters.set(
          this.reverbEAXEffectParameters.get()
            .withAirAbsorptionHFGain(newValue.doubleValue())
        );
      });
  }

  private void onSourceGainChanged(
    final double gain)
  {
    final var source = this.sourceSelected.get();
    if (source == null) {
      return;
    }

    try {
      source.setGain(gain);
    } catch (final JAException e) {
      LOG.error("AL: ", e);
    }
  }

  private void onSourcePitchChanged(
    final double pitch)
  {
    final var source = this.sourceSelected.get();
    if (source == null) {
      return;
    }

    try {
      source.setPitch(pitch);
    } catch (final JAException e) {
      LOG.error("AL: ", e);
    }
  }

  private void onSourceSelectionChanged(
    final JASourceType oldSource,
    final JASourceType newSource)
  {
    if (oldSource != null) {
      final var shape =
        this.sourceShapes.get(oldSource);

      if (shape != null) {
        shape.shape.setStroke(Color.BLACK);
      }
    }

    if (newSource == null) {
      this.sourceId.setText("");
      this.sourceControls.setDisable(true);
      this.sourcePosition.setText("");
      this.sourceVelocity.setText("");
      this.sourcePitch.setValue(1.0);
      this.sourceGain.setValue(1.0);
      return;
    }

    final var shape = this.sourceShapes.get(newSource);
    if (shape != null) {
      shape.shape.setStroke(Color.CYAN);
    }

    this.sourceControls.setDisable(false);

    try {
      this.sourceId.setText(String.valueOf(newSource.handle()));
      final var position = newSource.position();
      this.sourcePosition.setText(String.format(
        "%.2f %.2f",
        position.x(),
        position.z()));
      final var velocity = newSource.velocity();
      this.sourceVelocity.setText(String.format(
        "%.2f %.2f",
        velocity.x(),
        velocity.z()));
      this.sourcePitch.setValue(newSource.pitch());
      this.sourceGain.setValue(newSource.gain());
    } catch (final JAException e) {
      LOG.error("AL: ", e);
    }
  }

  private void onSourceDeleted(
    final JASourceType source)
  {
    final var shape = this.sourceShapes.get(source);
    this.listenerPane.getChildren()
      .remove(shape.shape);
    this.listenerPane.getChildren()
      .remove(shape.label);
  }

  private void onSourceCreated(
    final JASourceType source)
  {
    try {
      final var shape = new Ellipse(8.0, 8.0);
      shape.setFill(
        Color.rgb(
          (source.hashCode() >> 16 & 0xff),
          (source.hashCode() >> 8 & 0xff),
          (source.hashCode() & 0xff)
        )
      );
      shape.setStroke(Color.BLACK);

      final var label = new Label();
      label.setTextFill(Color.WHITE);
      label.setFont(FONT);

      final var shapeState =
        new SourceShapeState(
          source,
          shape,
          label,
          new AtomicReference<>(source.position())
        );

      this.sourceShapes.put(source, shapeState);

      shape.onMouseDraggedProperty()
        .set(event -> this.onSourceDragged(event, shapeState));
      shape.onMouseDragReleasedProperty()
        .set(event -> this.onSourceDragReleased(event, shapeState));
      shape.onMouseReleasedProperty()
        .set(event -> this.onSourceMouseReleased(event, shapeState));

      this.listenerPane.getChildren()
        .add(shape);
      this.listenerPane.getChildren()
        .add(label);

      final var position = this.alWorldToLocal(source.position());
      shape.setLayoutX(position.getX());
      shape.setLayoutY(position.getY());

      this.sourceSelected.set(source);
    } catch (final JAException e) {
      LOG.error("AL: ", e);
    }
  }

  private void onSourceMouseReleased(
    final MouseEvent event,
    final SourceShapeState state)
  {
    if (event.getButton() == MouseButton.PRIMARY) {
      this.sourceSelected.set(state.source);
      return;
    }

    if (event.getButton() == MouseButton.SECONDARY) {
      if (Objects.equals(this.sourceSelected.get(), state.source)) {
        this.sourceSelected.set(null);
      }

      try {
        state.source.stop();
        state.source.detachBuffer();
        state.source.close();
      } catch (final JAException e) {
        LOG.error("AL: ", e);
      }

      final var nodes =
        Set.copyOf(this.sourceGraph.vertexSet());

      for (final var node : nodes) {
        if (node instanceof JABufferType buffer) {
          if (this.sourceGraph.outDegreeOf(buffer) == 0) {
            try {
              buffer.close();
            } catch (final JAException e) {
              LOG.error("AL: ", e);
            }
          }
        }
      }
    }
  }

  private void onSourceDragReleased(
    final MouseDragEvent event,
    final SourceShapeState state)
  {
    try {
      this.sourceSelected.set(state.source);
      state.source.setVelocity(Vectors3D.zero());
    } catch (final JAException e) {
      LOG.error("AL: ", e);
    }
  }

  private void onSourceDragged(
    final MouseEvent event,
    final SourceShapeState state)
  {
    this.sourceSelected.set(state.source);

    final var eventSceneX =
      event.getSceneX();
    final var eventSceneY =
      event.getSceneY();
    final var panePosition =
      this.listenerPane.sceneToLocal(eventSceneX, eventSceneY);

    final var alPositionLast =
      state.positionLast().get();
    final var alPosition =
      this.localToALWorld(panePosition);
    final var diff =
      Vectors3D.scale(Vectors3D.subtract(alPosition, alPositionLast), 1.0);

    state.shape.setLayoutX(panePosition.getX());
    state.shape.setLayoutY(panePosition.getY());
    state.label.setLayoutX(panePosition.getX() + 16);
    state.label.setLayoutY(panePosition.getY() - 8);
    state.label.setText(String.format(
      "[%.2f, %.2f]",
      alPosition.x(),
      alPosition.z()));

    this.sourceSelected.set(state.source);

    try {
      state.source.setPosition(alPosition);
      state.source.setVelocity(diff);
    } catch (final JAException e) {
      LOG.error("AL: ", e);
    }
  }

  /**
   * Convert a pane-local position to OpenAL world coordinates.
   *
   * @param position The position
   *
   * @return The OpenAL position
   */

  private Vector3D localToALWorld(
    final Point2D position)
  {
    final var centerX =
      this.listenerPane.getWidth() / 2.0;
    final var centerY =
      this.listenerPane.getHeight() / 2.0;

    final var centerRelative =
      new Point2D(
        position.getX() - centerX,
        centerY - position.getY()
      );

    final var centerScaled =
      new Point2D(
        centerRelative.getX() / DISTANCE_SCALE_FACTOR,
        centerRelative.getY() / DISTANCE_SCALE_FACTOR
      );

    return Vector3D.of(
      centerScaled.getX(),
      0.0,
      centerScaled.getY()
    );
  }

  /**
   * Convert OpenAL world coordinates to a pane-local position.
   *
   * @param position The OpenAL position
   *
   * @return The position
   */

  private Point2D alWorldToLocal(
    final Vector3D position)
  {
    final var x = position.x() * DISTANCE_SCALE_FACTOR;
    final var y = position.z() * DISTANCE_SCALE_FACTOR;

    final var centerX =
      this.listenerPane.getWidth() / 2.0;
    final var centerY =
      this.listenerPane.getHeight() / 2.0;

    return new Point2D(
      centerX + x,
      centerY + y
    );
  }

  @FXML
  private void onListenerMouseDragged(
    final MouseEvent event)
    throws JAException
  {
    final var eventSceneX =
      event.getSceneX();
    final var eventSceneY =
      event.getSceneY();

    var panePosition =
      this.listenerPane.sceneToLocal(eventSceneX, eventSceneY);

    panePosition =
      new Point2D(
        Math.min(
          this.listenerPane.getWidth() - 16.0,
          Math.max(16.0, panePosition.getX())),
        Math.min(
          this.listenerPane.getHeight() - 16.0,
          Math.max(16.0, panePosition.getY()))
      );

    final var alPosition =
      this.localToALWorld(panePosition);

    this.context.listener()
      .setPosition(alPosition);

    final var position =
      this.context.listener()
        .position();

    this.listener.setLayoutX(panePosition.getX());
    this.listener.setLayoutY(panePosition.getY());
    this.listenerText.setText(
      String.format("[%.2f, %.2f]", position.x(), position.z())
    );
    this.listenerText.setLayoutX(panePosition.getX() + 16);
    this.listenerText.setLayoutY(panePosition.getY() - 8);
  }

  private void onListenerPaneSizeChanged()
  {
    try {
      final var centerX =
        this.listenerPane.getWidth() / 2.0;
      final var centerY =
        this.listenerPane.getHeight() / 2.0;

      final var position =
        this.context.listener()
          .position();

      this.listener.setLayoutX(centerX + position.x());
      this.listener.setLayoutY(centerY + position.z());

      this.listenerPane.setClip(
        new Rectangle(
          this.listenerPane.getWidth(),
          this.listenerPane.getHeight()
        )
      );
    } catch (final JAException e) {
      throw new IllegalStateException(e);
    }
  }

  @FXML
  private void onCreateSourceSelected()
    throws IOException, UnsupportedAudioFileException, JAException
  {
    final var chooser =
      this.choosers.create(
        JWFileChooserConfiguration.builder()
          .setAction(JWFileChooserAction.OPEN_EXISTING_SINGLE)
          .build()
      );

    final var file = chooser.showAndWait();
    if (file.isEmpty()) {
      return;
    }

    try (var stream =
           AudioSystem.getAudioInputStream(file.get(0).toFile())) {
      final var format =
        stream.getFormat();
      final var data =
        stream.readAllBytes();
      final var dataBuffer =
        ByteBuffer.allocateDirect(data.length);
      dataBuffer.put(data);
      dataBuffer.rewind();

      final var buffer =
        this.context.createBuffer();
      buffer.setData(
        formatFor(format),
        (int) format.getSampleRate(),
        dataBuffer
      );

      final var source = this.context.createSource();
      this.efx.attachSourceDirectToEffectsSlot(source, this.mainEffectSlot);

      source.setPosition(0.0, 0.0, 0.0);
      source.setBuffer(buffer);
      source.setGain(0.5);
      source.setLooping(true);
      source.play();
    }
  }

  @FXML
  private void onQuit()
  {
    Platform.exit();
  }

  record SourceShapeState(
    JASourceType source,
    Ellipse shape,
    Label label,
    AtomicReference<Vector3D> positionLast)
  {

  }

  private final class SourceBufferGraphListener
    extends JAAbstractGraphListener<JASourceOrBufferType, JASourceBufferLink>
  {
    SourceBufferGraphListener()
    {

    }

    @Override
    public void vertexAdded(
      final GraphVertexChangeEvent<JASourceOrBufferType> e)
    {
      final var v = e.getVertex();
      if (v instanceof JASourceType source) {
        MainController.this.onSourceCreated(source);
      }
    }

    @Override
    public void vertexRemoved(
      final GraphVertexChangeEvent<JASourceOrBufferType> e)
    {
      final var v = e.getVertex();
      if (v instanceof JASourceType source) {
        MainController.this.onSourceDeleted(source);
      }
    }
  }
}
