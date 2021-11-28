package eh1

import chisel3._
import chisel3.util._

trait HasResetVector {
  val resetVector = Settings.getLong("ResetVector")
}

class InstrPort extends Bundle{
    val instr   = Output(UInt(32.W))
    val pc      = Output(UInt(32.W))
    val valid   = Output(Bool())
    val predict_jump    = Output(Bool())
    val predict_target = Output(Bool())
}

class FlushPort extends Bundle{
    val flush = Input(Bool())
    val flush_path = Input(UInt(32.W))
}

class IFU(
    bankNum: Int = 1,
    addrWidth: Int = 32,
) extends Module with HasResetVector with CoreParameter{
    val io = IO(new Bundle{
        val stall_i = Input(Bool())
        val flush_i = new FlushPort()
        val iccm_i  = Flipped(new MemoryPort(addrWidth, bankNum * 8))
        val bpFlush = Output(Bool())
        val instr   = new InstrPort
    })

    io.bpFlush := "b11".U
    io.iccm_i.readAddr := 0.U
    io.iccm_i.writeAddr := 0.U
    io.iccm_i.writeData := 0.U
    io.iccm_i.writeEn := 0.U
    io.iccm_i.readEn  := "b11".U

    // pc
    val pc = RegInit(resetVector.U(AddrBits.W))
    // next pc
    val npc = Wire(UInt(AddrBits.W))
    val lpc = RegNext(pc)
    val valid_reg = RegInit(0.U(1.W))
    valid_reg := 1.U
    when(io.flush_i.flush){
        pc := io.flush_i.flush_path
        valid_reg := 0.B
    } .elsewhen(io.stall_i){
        pc := pc
    } .otherwise{
        pc := npc
    }

    npc := pc + 4.U

    io.iccm_i.readAddr := pc
    io.instr.instr := io.iccm_i.readData
    io.instr.valid := valid_reg
    io.instr.pc    := io.iccm_i.readAddr_o
    io.instr.predict_jump := 0.B
    io.instr.predict_target := 0.U



}