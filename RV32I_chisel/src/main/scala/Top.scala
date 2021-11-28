/*
 * This code is a minimal hardware described in Chisel.
 * 
 * Blinking LED: the FPGA version of Hello World
 */
package top

import chisel3._
import eh1._
import system._
import chisel3.stage.{ChiselStage, ChiselGeneratorAnnotation}

class Top extends Module {

  val io = IO(new Bundle{})
  val eh1_i0 = Module(new EH1SimTop)

}

/**
 * An object extending App to generate the Verilog code.
 */
object TopMain extends App {

  (new chisel3.stage.ChiselStage).execute(args, Seq(ChiselGeneratorAnnotation(() => new EH1SimTop)))

}
