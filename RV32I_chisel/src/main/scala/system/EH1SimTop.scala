package system

import chisel3._
import eh1._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class EH1SimTop extends Module{
    val io = IO(new Bundle{
        val exe_result = new AluPort()
        val lsu_result = new LsuPort()
        val rd_final = new Rd_Final_Port()
    })

    val ifu = Module(new IFU(    bankNum = 4,   addrWidth = 32 ))
    val ccm = Module(new CCM(    bankNum = 4,   addrWidth = 32,  memoryFile = "/home/wxz/data/RV32_chisel/benchmark/sim_hex/"))
    val dec = Module(new DEC)
    val exe = Module(new EXE)
    val lsu = Module(new LSU)

    ifu.io.iccm_i <> ccm.io.iccm
    ifu.io.flush_i <> exe.io.flush_o
    ifu.io.stall_i := dec.io.stall_o
    ccm.io.iccm.readAddr := ifu.io.iccm_i.readAddr
    ccm.io.stall_i := dec.io.stall_o
    dec.io.instr <> ifu.io.instr
    exe.io.alu <> dec.io.alu
    exe.io.alu_out <> dec.io.alu_back
    lsu.io.lsu <> dec.io.lsu
    lsu.io.dccm_port <> ccm.io.dccm
    lsu.io.flush_i <> exe.io.flush_o
    dec.io.lsu_back <> lsu.io.lsu_result
    dec.io.flush_i <> exe.io.flush_o

    exe.io.bypass_d1 := dec.io.bypass_d0o
    exe.io.bypass_d2 := dec.io.bypass_d1o

    io.exe_result <> exe.io.alu_out
    io.lsu_result <> lsu.io.lsu_result
    io.rd_final  <> dec.io.rd_final
}
