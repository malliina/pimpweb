package com.malliina.generator

import org.slf4j.LoggerFactory

import scala.sys.process.{Process, ProcessLogger}

object IO {
  private val log = LoggerFactory.getLogger(getClass)

  def run(cmd: String): ExitValue = {
    val logger = ProcessLogger(out => log.info(out), err => log.error(err))
    val process = Process(cmd).run(logger)
    // blocks
    ExitValue(process.exitValue())
  }
}
