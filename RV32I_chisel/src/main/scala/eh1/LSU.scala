package eh1

import chisel3._
import chisel3.util._

class Addr_Com extends Module {
    val io = IO(new Bundle{
        val lsu  = Flipped(new LsuPort())
        val got_addr = new LsuPort()
    })
    val got_addr_reg = RegNext(io.lsu)

    when(io.lsu.valid) {    
        when(io.lsu.sw || io.lsu.sh || io.lsu.sb ) {    // store
            got_addr_reg.addr   := (io.lsu.rs1.asSInt + io.lsu.imm12_s.asSInt).asUInt
        } .otherwise {  // load
            got_addr_reg.addr   := (io.lsu.rs1.asSInt + io.lsu.imm12_l.asSInt).asUInt
        }
    }

    io.got_addr <> got_addr_reg
}

class Load_Store(
    bankNum: Int = 1,
    addrWidth: Int = 32,
) extends Module{
    val io = IO(new Bundle{
        val got_addr = Flipped(new LsuPort())
        val lsu_result = new LsuPort()
        val dccm_port = Flipped(new MemoryPort(addrWidth, bankNum * 32))
    })
    val wr_en = Wire(UInt(2.W))
        wr_en := 0.U
        when(io.got_addr.valid){
        when(io.got_addr.sw){
            wr_en := "b11".U
        }
        when(io.got_addr.sh){
            wr_en := "b10".U
        }
        when(io.got_addr.sb){
            wr_en := "b01".U
        }
    }
    val readAddr = io.got_addr.addr
    val writeAddr = io.got_addr.addr
    val writeData = io.got_addr.rs2
    val readEn    = io.got_addr.rd_en

    io.dccm_port.readEn := readEn
    io.dccm_port.readAddr := readAddr
    io.dccm_port.writeAddr := writeAddr
    io.dccm_port.writeData := writeData
    io.dccm_port.writeEn := wr_en

    val got_addr_reg = RegNext(io.got_addr)
    io.lsu_result <> got_addr_reg

    io.lsu_result.load_data := io.dccm_port.readData

    when(got_addr_reg.lhu){
        when(io.dccm_port.readAddr_o(1)){
            io.lsu_result.load_data:= Cat(0.U(16.W), io.dccm_port.readData(31,16))
        } .otherwise{
            io.lsu_result.load_data:= Cat(0.U(16.W), io.dccm_port.readData(15,0))
        }
    }
    when(got_addr_reg.lh){
        when(io.dccm_port.readAddr_o(1)){
            io.lsu_result.load_data:= Cat(Fill(16, io.dccm_port.readData(31)), io.dccm_port.readData(31,16))
        } .otherwise{
            io.lsu_result.load_data:= Cat(Fill(16, io.dccm_port.readData(15)), io.dccm_port.readData(15,0))
        }
    }

    when(got_addr_reg.lbu){
        when(io.dccm_port.readAddr_o(1,0) === "b11".U){
            io.lsu_result.load_data:= Cat(0.U(24.W), io.dccm_port.readData(31,24))
        } .elsewhen(io.dccm_port.readAddr_o(1,0) === "b10".U){
            io.lsu_result.load_data:= Cat(0.U(24.W), io.dccm_port.readData(23,16))
        } .elsewhen(io.dccm_port.readAddr_o(1,0) === "b01".U){
            io.lsu_result.load_data:= Cat(0.U(24.W), io.dccm_port.readData(15,8))
        } .elsewhen(io.dccm_port.readAddr_o(1,0) === "b00".U){
            io.lsu_result.load_data:= Cat(0.U(24.W), io.dccm_port.readData(7,0))
        }
    }
    when(got_addr_reg.lb){
        when(io.dccm_port.readAddr_o(1,0) === "b11".U){
            io.lsu_result.load_data:= Cat(Fill(24, io.dccm_port.readData(31)), io.dccm_port.readData(31,24))
        } .elsewhen(io.dccm_port.readAddr_o(1,0) === "b10".U){
            io.lsu_result.load_data:= Cat(Fill(24, io.dccm_port.readData(23)), io.dccm_port.readData(23,16))
        } .elsewhen(io.dccm_port.readAddr_o(1,0) === "b01".U){
            io.lsu_result.load_data:= Cat(Fill(24, io.dccm_port.readData(15)), io.dccm_port.readData(15,8))
        } .elsewhen(io.dccm_port.readAddr_o(1,0) === "b00".U){
            io.lsu_result.load_data:= Cat(Fill(24, io.dccm_port.readData(7)), io.dccm_port.readData(7,0))
        }
    }
}

class LSU(
    bankNum: Int = 4,
    addrWidth: Int = 32,
)  extends Module{
    val io = IO(new Bundle{
        val lsu         = Flipped(new LsuPort())
        val lsu_result  = new LsuPort()
        val dccm_port   = Flipped(new MemoryPort(addrWidth, bankNum * 8))
        val flush_i     = new FlushPort()
    })

    val addr_com = Module(new Addr_Com())
    val load_store = Module(new Load_Store(  bankNum = 4,  addrWidth = 32) )


    addr_com.io.lsu <> io.lsu
    addr_com.io.got_addr <> load_store.io.got_addr
    io.dccm_port <> load_store.io.dccm_port
    io.lsu_result <> load_store.io.lsu_result

    when(io.flush_i.flush){
        addr_com.io.lsu.valid := 0.B
        load_store.io.got_addr.valid := 0.B
        io.dccm_port.writeEn := 0.U
        io.lsu_result.valid := 0.B
    }
}