package com.malliina.generator

import org.slf4j.LoggerFactory

import scala.sys.process.{Process, ProcessLogger}

object IO {
  private val log = LoggerFactory.getLogger(getClass)
  val isWindows = sys.props("os.name").toLowerCase().contains("win")
  val cmdPrefix = if (isWindows) "cmd /c " else ""

  def run(cmd: String): ExitValue = {
    log.info(s"Running '$cmd'...")
    val logger = ProcessLogger(out => log.info(out), err => log.error(err))
    val process = Process(s"$cmdPrefix$cmd").run(logger)
    // blocks
    ExitValue(process.exitValue())
  }
}
