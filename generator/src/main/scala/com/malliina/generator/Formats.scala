package com.malliina.generator

import io.circe.{Codec, Decoder, Encoder}

import java.nio.file.{Path, Paths}

object Formats:
  given pathFormat: Codec[Path] = Codec.from(
    Decoder.decodeString.map(s => Paths.get(s)),
    Encoder.encodeString.contramap(p => p.toAbsolutePath.toString)
  )
