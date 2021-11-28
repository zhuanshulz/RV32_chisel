package system

import chisel3._
import chisel3.util._

class fifo(val size : Int, val width : Int) extends Module {
  val io = IO(new Bundle {
    val dataIn = Input(UInt(width.W))
    val dataOut = Output(UInt(width.W))
    val writeFlag = Input(Bool())
    val readFlag = Input(Bool())
    val full = Output(Bool())
    val empty = Output(Bool())
  })

  val count = RegInit(0.U((log2Ceil(size)+1).W))
  val mem = Mem(size, UInt(width.W))
  val wPointer = RegInit(0.U((log2Ceil(size)).W))
  val rPointer = RegInit(0.U((log2Ceil(size)).W))
  val dataOut = RegInit(0.U(width.W))

  def indexAdd(index : UInt) : UInt = {
      Mux(index === (size - 1).U, 0.U, index + 1.U)
  }

  dataOut := mem(rPointer)
  when(io.writeFlag === true.B && io.readFlag === true.B) {
    when(count === 0.U) { dataOut := io.dataIn }
    .otherwise {
      rPointer := indexAdd(rPointer)
      mem(wPointer) := io.dataIn
      wPointer := indexAdd(wPointer)
    } 
  } .elsewhen (io.writeFlag === true.B && io.readFlag === false.B) {
    when(count < size.U) {
      mem(wPointer) := io.dataIn
      wPointer := indexAdd(wPointer)
      count := count + 1.U
    }
  } .elsewhen (io.writeFlag === false.B && io.readFlag === true.B) {
    when(count > 0.U) {
      rPointer := indexAdd(rPointer)
      count := count - 1.U
    } 
  }

  io.dataOut := dataOut
  io.full := (size.U === count)
  io.empty := (count === 0.U)
}
