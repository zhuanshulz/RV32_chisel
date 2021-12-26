package eh1

import chisel3._
import chisel3.util._
import scala .io. Source
import chisel3.util.experimental.loadMemoryFromFile
// import chisel3.experimental.{annotate, ChiselAnnotation}
// import firrtl.annotations.MemorySynthInit

class MemoryPort (val addrWidth: Int, val dataWidth:Int) extends Bundle{
    val readEn      = Input(UInt(2.W))
    val readAddr    = Input(UInt(addrWidth.W))
    val readData    = Output(UInt(dataWidth.W))
    val readAddr_o  = Output(UInt(addrWidth.W))
    val writeAddr   = Input(UInt(addrWidth.W))
    val writeData   = Input(UInt(dataWidth.W))
    val writeEn     = Input(UInt(2.W))
}


class CCM(
    bankNum: Int = 1,
    addrWidth: Int = 32,
    memoryFile: String = ""
) extends Module{

    val io = IO(new Bundle{
        val iccm = new MemoryPort(addrWidth, bankNum * 8)
        val dccm = new MemoryPort(addrWidth, bankNum * 8)
        val stall_i = Input(Bool())
    })

    val mem_0 = SyncReadMem(8192, UInt(8.W))
    val mem_1 = SyncReadMem(8192, UInt(8.W))
    val mem_2 = SyncReadMem(8192, UInt(8.W))
    val mem_3 = SyncReadMem(8192, UInt(8.W))
    
    val mem_4 = SyncReadMem(4096, UInt(8.W))
    val mem_5 = SyncReadMem(4096, UInt(8.W))
    val mem_6 = SyncReadMem(4096, UInt(8.W))
    val mem_7 = SyncReadMem(4096, UInt(8.W))

    if (memoryFile != ""){
        loadMemoryFromFile(mem_0, memoryFile + "program_iccm_1.hex")
        loadMemoryFromFile(mem_4, memoryFile + "data_dccm_1.hex")
        loadMemoryFromFile(mem_1, memoryFile + "program_iccm_2.hex")
        loadMemoryFromFile(mem_5, memoryFile + "data_dccm_2.hex")
        loadMemoryFromFile(mem_2, memoryFile + "program_iccm_3.hex")
        loadMemoryFromFile(mem_6, memoryFile + "data_dccm_3.hex")
        loadMemoryFromFile(mem_3, memoryFile + "program_iccm_4.hex")
        loadMemoryFromFile(mem_7, memoryFile + "data_dccm_4.hex")
    }

    val readAddr_reg = RegNext(io.iccm.readAddr)
    val actual_read_addr = Wire(UInt(32.W))

    val byte0 = mem_0.read(actual_read_addr >> 2.U)
    val byte1 = mem_1.read(actual_read_addr >> 2.U)
    val byte2 = mem_2.read(actual_read_addr >> 2.U)
    val byte3 = mem_3.read(actual_read_addr >> 2.U)

    val byte4 = mem_4.read(io.dccm.readAddr >> 2.U)
    val byte5 = mem_5.read(io.dccm.readAddr >> 2.U)
    val byte6 = mem_6.read(io.dccm.readAddr >> 2.U)
    val byte7 = mem_7.read(io.dccm.readAddr >> 2.U)

    io.iccm.readData := Cat(byte3, byte2, byte1, byte0)
    io.dccm.readData := Cat(byte7, byte6, byte5, byte4)
    io.iccm.readAddr_o := RegNext(actual_read_addr)
    io.dccm.readAddr_o := RegNext(io.dccm.readAddr)

    when(io.stall_i){
        actual_read_addr := readAddr_reg
        readAddr_reg := actual_read_addr
    } .otherwise{
        actual_read_addr := io.iccm.readAddr
    }

// 01 byte
// 10 half
// 11 word
    when(io.iccm.writeEn === "b11".U){
        mem_0.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
        mem_1.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(15,8))
        mem_2.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(23,16))
        mem_3.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(31,24))
    } .elsewhen(io.iccm.writeEn === "b10".U){
        when(io.iccm.writeAddr(1)){
            mem_2.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
            mem_3.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(15,8))
        } .otherwise{
            mem_0.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
            mem_1.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(15,8))
        }
    } .elsewhen(io.iccm.writeEn === "b01".U){
        switch(io.iccm.writeAddr(1,0)){
            is(0.U) {
                mem_0.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
            }
            is(1.U) {
                mem_1.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
            }
            is(2.U) {
                mem_2.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
            }
            is(3.U) {
                mem_3.write(io.iccm.writeAddr >> 2.U, io.iccm.writeData(7,0))
            }
        }
    }


    when((io.dccm.writeEn.orR) && (io.dccm.writeAddr === "h7f030000".U)){
        when(io.dccm.writeData(7,0) === "hff".U){
            stop()
        } .otherwise{
            printf("%c", io.dccm.writeData(7,0))
        }
    } .elsewhen(io.dccm.writeEn === "b11".U){
        mem_4.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
        mem_5.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(15,8))
        mem_6.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(23,16))
        mem_7.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(31,24))
    } .elsewhen(io.dccm.writeEn === "b10".U){
        when(io.dccm.writeAddr(1)){
            mem_6.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
            mem_7.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(15,8))
        } .otherwise{
            mem_4.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
            mem_5.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(15,8))
        }
    } .elsewhen(io.dccm.writeEn === "b01".U){
        switch(io.dccm.writeAddr(1,0)){
            is(0.U) {
                mem_4.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
            }
            is(1.U) {
                mem_5.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
            }
            is(2.U) {
                mem_6.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
            }
            is(3.U) {
                mem_7.write(io.dccm.writeAddr >> 2.U, io.dccm.writeData(7,0))
            }
        }
    }
}


//

// loadMemoryFromFile in a file with same mem name can only be use once, although it was override!!
