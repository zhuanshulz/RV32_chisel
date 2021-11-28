package eh1

import chisel3._
import chisel3.util._


class ByPassPort extends Bundle{
    val rs1_en = Input(Bool())
    val rs1    = Input(UInt(32.W))
    val rs2_en = Input(Bool())
    val rs2    = Input(UInt(32.W))
}

class ExePipe extends Module{
    val io = IO(new Bundle{
        val alu_in = Flipped(new AluPort())
        val bypass = new ByPassPort()
        val alu_out = new AluPort()
    })
    val alu_new = Wire(new AluPort)
    alu_new := io.alu_in
    when(io.bypass.rs1_en){
        alu_new.rs1_valid := 1.B
        alu_new.rs1       := io.bypass.rs1
    } .elsewhen(io.bypass.rs2_en){
        alu_new.rs2_valid := 1.B
        alu_new.rs2       := io.bypass.rs2
    }
    val alu_reg = RegNext(alu_new)
    io.alu_out <> alu_reg
}


class Alu extends Module{
    val io = IO(new Bundle{
        val alu_in = Flipped(new AluPort())
        val bypass = new ByPassPort()
        val alu_out = new AluPort()
    })
    val alu_new = Wire(new AluPort)
    alu_new := io.alu_in

    when(io.bypass.rs1_en){
        alu_new.rs1_valid := 1.B
        alu_new.rs1       := io.bypass.rs1
    } .elsewhen(io.bypass.rs2_en){
        alu_new.rs2_valid := 1.B
        alu_new.rs2       := io.bypass.rs2
    }

    val ready = (alu_new.valid && 
                (~(alu_new.rs1_en & (~ alu_new.rs1_valid))) &&
                (~(alu_new.rs2_en & (~ alu_new.rs2_valid))))

    when(ready){
        when(alu_new.and){
            alu_new.result := alu_new.rs1 & alu_new.rs2
            alu_new.jump := 0.B
        }
        . elsewhen(alu_new.andi){
            alu_new.result := alu_new.rs1 & (Cat(Fill(20, alu_new.imm12_itype(11)), alu_new.imm12_itype))
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.or){
            alu_new.result := alu_new.rs1 | alu_new.rs2
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.ori){
            alu_new.result := alu_new.rs1 | (Cat(Fill(20, alu_new.imm12_itype(11)), alu_new.imm12_itype))
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.xor){
            alu_new.result := alu_new.rs1 ^ alu_new.rs2
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.xori){
            alu_new.result := alu_new.rs1 ^ (Cat(Fill(20, alu_new.imm12_itype(11)), alu_new.imm12_itype))
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.sll){
            alu_new.result := alu_new.rs1 << alu_new.rs2(4,0)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.slli){
            alu_new.result := alu_new.rs1 << alu_new.shamt
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.srl){
            alu_new.result := alu_new.rs1 >> alu_new.rs2(4,0)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.srli){
            alu_new.result := alu_new.rs1 >> alu_new.shamt
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.sra){
            alu_new.result := (alu_new.rs1.asSInt >> alu_new.rs2(4,0)).asUInt
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.srai){
            alu_new.result := (alu_new.rs1.asSInt >> alu_new.shamt).asUInt
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.beq){
            alu_new.jump := alu_new.rs1 === alu_new.rs2
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm12_btype.asSInt * 2.S)).asUInt
        }
        .elsewhen(alu_new.bne){
            alu_new.jump := alu_new.rs1 =/= alu_new.rs2
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm12_btype.asSInt * 2.S)).asUInt
        }
        .elsewhen(alu_new.blt){
            alu_new.jump := alu_new.rs1.asSInt < alu_new.rs2.asSInt
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm12_btype.asSInt * 2.S)).asUInt
        }
        .elsewhen(alu_new.bltu){
            alu_new.jump := alu_new.rs1 < alu_new.rs2
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm12_btype.asSInt * 2.S)).asUInt
        }
        .elsewhen(alu_new.bge){
            alu_new.jump := alu_new.rs1.asSInt >= alu_new.rs2.asSInt
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm12_btype.asSInt * 2.S)).asUInt
        }
        .elsewhen(alu_new.bgeu){
            alu_new.jump := alu_new.rs1 >= alu_new.rs2
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm12_btype.asSInt * 2.S)).asUInt
        }
        .elsewhen(alu_new.add){
            alu_new.result := alu_new.rs1 + alu_new.rs2
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.addi){
            alu_new.result := (alu_new.rs1.asSInt + alu_new.imm12_itype.asSInt).asUInt
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.sub){
            alu_new.result := alu_new.rs1 - alu_new.rs2
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.slt){
            alu_new.result := Mux(alu_new.rs1.asSInt < alu_new.rs2.asSInt, 1.U, 0.U)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.sltu){
            alu_new.result := Mux(alu_new.rs1 < alu_new.rs2, 1.U, 0.U)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.slti){
            alu_new.result := Mux(alu_new.rs1.asSInt < alu_new.imm12_itype.asSInt, 1.U, 0.U)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.sltiu){
            alu_new.result := Mux(alu_new.rs1 < alu_new.imm12_itype, 1.U, 0.U)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.jal){
            alu_new.result := alu_new.pc + 4.U
            alu_new.jump := 1.B
            alu_new.jump_addr := (alu_new.pc.asSInt + (alu_new.imm20_jtype.asSInt * 2.S) ).asUInt
        }
        .elsewhen(alu_new.jalr){
            alu_new.result := alu_new.pc + 4.U
            alu_new.jump := 1.B
            alu_new.jump_addr := (alu_new.rs1.asSInt + alu_new.imm12_itype.asSInt ).asUInt
        }
        .elsewhen(alu_new.auipc){
            alu_new.result := alu_new.pc + (alu_new.imm20_utype << 12)
            alu_new.jump := 0.B
        }
        .elsewhen(alu_new.lui){
            alu_new.result := (alu_new.imm20_utype << 12)
            alu_new.jump := 0.B
        }
    }

    val alu_reg = RegNext(alu_new)
    io.alu_out <> alu_reg
}

class EXE extends Module{
    val io = IO(new Bundle{
        val alu  = Flipped(new AluPort())
        val bypass_d1 = new ByPassPort()
        val bypass_d2 = new ByPassPort()
        val alu_out = new AluPort()
        val flush_o = Flipped(new FlushPort())
    })
// bypass 的逻辑控制全部交由 dec模块控制。
    val alu_d1 = Module(new Alu)
    val alu_d4 = Module(new Alu)

    alu_d1.io.alu_in <> io.alu
    alu_d1.io.bypass <> io.bypass_d1
    alu_d4.io.alu_in <> alu_d1.io.alu_out
    alu_d4.io.bypass <> io.bypass_d2
    io.alu_out <> alu_d4.io.alu_out
    
    io.flush_o.flush := 0.B
    io.flush_o.flush_path := 0.U

    when(io.alu_out.valid && io.alu_out.jump){  // actual taken
        when((!io.alu_out.predict_jump) || (io.alu_out.jump_addr =/= io.alu_out.predict_target)){
            io.flush_o.flush := 1.B
            io.flush_o.flush_path  := io.alu_out.jump_addr
        }
    } .elsewhen(io.alu_out.valid && (! io.alu_out.jump)){// actual not taken
        when(io.alu_out.predict_jump){
            io.flush_o.flush := 1.B
            io.flush_o.flush_path := io.alu_out.pc + 4.U
        }
    } .otherwise{
        io.flush_o.flush := 0.B
        io.flush_o.flush_path := 0.U
    }

    when(io.flush_o.flush){
        alu_d1.io.alu_in.valid := 0.B
        alu_d4.io.alu_in.valid := 0.B
    }
}