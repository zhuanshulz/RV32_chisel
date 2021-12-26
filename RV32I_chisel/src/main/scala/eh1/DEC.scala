package eh1

import chisel3._
import chisel3.util._
import scala.io.Source

class AluPort extends Bundle{
    val valid       = Output(Bool())
    val and         = Output(Bool())
    val andi        = Output(Bool())
    val or          = Output(Bool())
    val ori          = Output(Bool())
    val xor         = Output(Bool())
    val xori         = Output(Bool())
    val sll         = Output(Bool())
    val slli         = Output(Bool())
    val srl         = Output(Bool())
    val srli        = Output(Bool())
    val sra         = Output(Bool())
    val srai        = Output(Bool())
    val beq         = Output(Bool())
    val bne         = Output(Bool())
    val blt         = Output(Bool())
    val bltu         = Output(Bool())
    val bge         = Output(Bool())
    val bgeu         = Output(Bool())
    val add         = Output(Bool())
    val addi        = Output(Bool())
    val sub         = Output(Bool())
    val slt         = Output(Bool())
    val sltu         = Output(Bool())
    val slti        = Output(Bool())
    val sltiu       = Output(Bool())
    val jal         = Output(Bool())
    val jalr        = Output(Bool())
    val auipc       = Output(Bool())
    val lui         = Output(Bool())
    
    val imm12_itype    = Output(UInt(12.W))
    val imm12_btype    = Output(UInt(12.W))
    val imm20_utype    = Output(UInt(20.W))
    val imm20_jtype    = Output(UInt(20.W))
    val shamt          = Output(UInt(5.W))

    val rs1         = Output(UInt(32.W))
    val rs1_en      = Output(Bool())
    val rs1_valid   = Output(Bool())
    val rs2         = Output(UInt(32.W))
    val rs2_en      = Output(Bool())
    val rs2_valid   = Output(Bool())
    val rd          = Output(UInt(5.W))
    val rd_en       = Output(Bool())

    val pc          = Output(UInt(32.W))

    val jump        = Output(Bool())
    val jump_addr   = Output(UInt(32.W))

    val finish      = Output(Bool())
    val result      = Output(UInt(32.W))

    val predict_jump = Output(Bool())
    val predict_target = Output(UInt(32.W))
}

class LsuPort extends Bundle{
    val sw          = Output(Bool())
    val sh          = Output(Bool())
    val sb          = Output(Bool())
    val lhu         = Output(Bool())
    val lbu         = Output(Bool())
    val lw          = Output(Bool())
    val lh          = Output(Bool())
    val lb          = Output(Bool())

    val imm12_l     = Output(UInt(12.W))
    val imm12_s     = Output(UInt(12.W))

    val rs1         = Output(UInt(32.W))
    val rs2         = Output(UInt(32.W))
    val rd          = Output(UInt(5.W))
    val rd_en       = Output(Bool())
    val load_data   = Output(UInt(32.W))

    val addr        = Output(UInt(32.W))

    val finish      = Output(Bool())
    val valid       = Output(Bool())
}

class RfReadPort extends Bundle{
    val rs_en   = Input(Bool())
    val rs      = Input(UInt(5.W))
    val rs_out  = Output(UInt(32.W))
}
class RfWritePort extends Bundle{
    val rd_en = Input(Bool())
    val rd    = Input(UInt(5.W))
    val rd_in = Input(UInt(32.W))
}

class RegFiles extends Module{
    val io = IO(new Bundle{
        val rs1 = new RfReadPort
        val rs2 = new RfReadPort
        val rd1  = new RfWritePort
        val rd2  = new RfWritePort
    })

    val registerFile = Reg(Vec(32,(UInt(32.W))))

    when(io.rd1.rd_en){
        when(io.rd1.rd =/= 0.U){
            registerFile(io.rd1.rd) := io.rd1.rd_in
        }
    }
    when(io.rd2.rd_en){
        when(io.rd2.rd =/= 0.U){
            registerFile(io.rd2.rd) := io.rd2.rd_in
        }
    }

    io.rs1.rs_out := Mux(io.rs1.rs_en, registerFile(io.rs1.rs), 0.U)
    io.rs2.rs_out := Mux(io.rs2.rs_en, registerFile(io.rs2.rs), 0.U)

    when(io.rs1.rs_en && io.rd1.rd_en && (io.rs1.rs === io.rd1.rd)){
        io.rs1.rs_out := io.rd1.rd_in
    } .elsewhen(io.rs1.rs_en && io.rd2.rd_en && (io.rs1.rs === io.rd2.rd)){
        io.rs1.rs_out := io.rd2.rd_in
    }

    when(io.rs2.rs_en && io.rd1.rd_en && (io.rs2.rs === io.rd1.rd)){
        io.rs2.rs_out := io.rd1.rd_in
    } .elsewhen(io.rs2.rs_en && io.rd2.rd_en && (io.rs2.rs === io.rd2.rd)){
        io.rs2.rs_out := io.rd2.rd_in
    }
}

class Decode extends Module{
    val io = IO(new Bundle{
        val instr   = Flipped(new InstrPort)
        val aluport  = new AluPort()
        val lsuport  = new LsuPort()
    })
    val opcode = io.instr.instr(6,0)
    val rd     = io.instr.instr(11,7)
    val func3  = io.instr.instr(14,12)
    val rs1    = io.instr.instr(19,15)
    val rs2    = io.instr.instr(24,20)
    val func7  = io.instr.instr(31,25)
    io.aluport.pc           := io.instr.pc
    val imm12_itype = io.instr.instr(31,20)
    val imm12_stype = Cat(func7, rd)
    val imm20_utype = Cat(func7, rs2, rs1, func3)
    val imm12_btype = Cat(io.instr.instr(31), io.instr.instr(7), io.instr.instr(30,25), io.instr.instr(11,8))
    val imm20_jtype = Cat(io.instr.instr(31), io.instr.instr(19,12), io.instr.instr(20), io.instr.instr(30,21))
    val shamt = rs2

    io.aluport.rs1      := rs1
    io.aluport.rs1_en   := 0.B
    io.aluport.rs1_valid := 0.B
    io.aluport.rs2      := rs2
    io.aluport.rs2_en   := 0.B
    io.aluport.rs2_valid := 0.B
    io.aluport.rd       := rd
    io.aluport.rd_en    := 0.B

    io.aluport.result       := 0.U
    io.aluport.jump         := 0.B
    io.aluport.jump_addr    := 0.U
    io.aluport.finish       := 0.B
    io.aluport.predict_jump := io.instr.predict_jump
    io.aluport.predict_target := io.instr.predict_target

    val sw          = (opcode === "b0100011".U) && (func3 === "b010".U)
    val sh          = (opcode === "b0100011".U) && (func3 === "b001".U)
    val sb          = (opcode === "b0100011".U) && (func3 === "b000".U)
    val lhu         = (opcode === "b0000011".U) && (func3 === "b101".U)
    val lbu         = (opcode === "b0000011".U) && (func3 === "b100".U)
    val lw          = (opcode === "b0000011".U) && (func3 === "b010".U)
    val lh          = (opcode === "b0000011".U) && (func3 === "b001".U)
    val lb          = (opcode === "b0000011".U) && (func3 === "b000".U)

    val lsu_valid = (sw || sh || sb || lhu || lbu || lw || lh ||lb)

    io.aluport.valid       := io.instr.valid && (!lsu_valid )
    io.aluport.and         := (opcode === "b0110011".U) && (func3 === "b111".U) && (func7 === "b0000000".U)
    io.aluport.or          := (opcode === "b0110011".U) && (func3 === "b110".U) && (func7 === "b0000000".U)
    io.aluport.sra         := (opcode === "b0110011".U) && (func3 === "b101".U) && (func7 === "b0100000".U)
    io.aluport.srl         := (opcode === "b0110011".U) && (func3 === "b101".U) && (func7 === "b0000000".U)
    io.aluport.xor         := (opcode === "b0110011".U) && (func3 === "b100".U) && (func7 === "b0000000".U)
    io.aluport.sltu        := (opcode === "b0110011".U) && (func3 === "b011".U) && (func7 === "b0000000".U)
    io.aluport.slt         := (opcode === "b0110011".U) && (func3 === "b010".U) && (func7 === "b0000000".U)
    io.aluport.sll         := (opcode === "b0110011".U) && (func3 === "b001".U) && (func7 === "b0000000".U)
    io.aluport.sub         := (opcode === "b0110011".U) && (func3 === "b000".U) && (func7 === "b0100000".U)
    io.aluport.add         := (opcode === "b0110011".U) && (func3 === "b000".U) && (func7 === "b0000000".U)
    io.aluport.srai        := (opcode === "b0010011".U) && (func3 === "b101".U) && (func7 === "b0100000".U)
    io.aluport.srli        := (opcode === "b0010011".U) && (func3 === "b101".U) && (func7 === "b0000000".U)
    io.aluport.slli        := (opcode === "b0010011".U) && (func3 === "b001".U) && (func7 === "b0000000".U)
    io.aluport.andi        := (opcode === "b0010011".U) && (func3 === "b111".U)
    io.aluport.ori         := (opcode === "b0010011".U) && (func3 === "b110".U)
    io.aluport.xori        := (opcode === "b0010011".U) && (func3 === "b100".U)
    io.aluport.sltiu       := (opcode === "b0010011".U) && (func3 === "b011".U)
    io.aluport.slti        := (opcode === "b0010011".U) && (func3 === "b010".U)
    io.aluport.addi        := (opcode === "b0010011".U) && (func3 === "b000".U)
    io.aluport.bgeu        := (opcode === "b1100011".U) && (func3 === "b111".U)
    io.aluport.bltu        := (opcode === "b1100011".U) && (func3 === "b110".U)
    io.aluport.bge         := (opcode === "b1100011".U) && (func3 === "b101".U)
    io.aluport.blt         := (opcode === "b1100011".U) && (func3 === "b100".U)
    io.aluport.bne         := (opcode === "b1100011".U) && (func3 === "b001".U)
    io.aluport.beq         := (opcode === "b1100011".U) && (func3 === "b000".U)
    io.aluport.jalr        := (opcode === "b1100111".U) && (func3 === "b000".U)
    io.aluport.jal         := (opcode === "b1101111".U)
    io.aluport.auipc       := (opcode === "b0010111".U)
    io.aluport.lui         := (opcode === "b0110111".U)

    io.aluport.imm12_itype := imm12_itype
    io.aluport.imm12_btype := imm12_btype
    io.aluport.imm20_utype := imm20_utype
    io.aluport.imm20_jtype := imm20_jtype
    io.aluport.shamt       := shamt

    io.lsuport.sw     := sw 
    io.lsuport.sh     := sh 
    io.lsuport.sb     := sb 
    io.lsuport.lhu    := lhu
    io.lsuport.lbu    := lbu
    io.lsuport.lw     := lw 
    io.lsuport.lh     := lh 
    io.lsuport.lb     := lb 

    io.lsuport.imm12_l      := imm12_itype
    io.lsuport.imm12_s      := imm12_stype

    io.lsuport.rs1      := 0.U
    io.lsuport.rs2      := 0.U
    io.lsuport.rd       := rd

    io.lsuport.rd_en    := 0.B
    io.lsuport.load_data := 0.U
    io.lsuport.addr      := 0.U
    io.lsuport.finish    := 0.U
    io.lsuport.valid     :=  io.instr.valid && lsu_valid
}

class rd_entry extends Bundle{
    val valid = Output(Bool())
    val rd    = Output(UInt(5.W))
}

class Rd_Final_Port extends Bundle{
    val rd_wb   =  Output(UInt(5.W))
    val rd_wb_en = Output(Bool())
    val rd_wb_data  = Output(UInt(32.W))
}

// finish decode and issue 
class DEC extends Module{
    val io = IO(new Bundle{
        val instr       = Flipped(new InstrPort())
        val alu         = new AluPort()
        val lsu         = new LsuPort()
        val alu_back    = Flipped(new AluPort())
        val lsu_back    = Flipped(new LsuPort())
        val flush_i     = new FlushPort()
        val stall_o     = Output(Bool())
        val bypass_d0o  = Flipped(new ByPassPort())
        val bypass_d1o  = Flipped(new ByPassPort())
        val rd_final    = new Rd_Final_Port()
    })

    val decode = Module(new Decode)

    val rf = Module(new RegFiles)

    val alu_reg = Reg(new AluPort)
    val lsu_reg = RegNext(decode.io.lsuport)

    alu_reg := decode.io.aluport

    val rs1_en = !(decode.io.aluport.lui || decode.io.aluport.auipc || decode.io.aluport.jal)
    val rs2_en = decode.io.aluport.beq || decode.io.aluport.bne || decode.io.aluport.blt || decode.io.aluport.bge  || decode.io.aluport.bltu || decode.io.aluport.bgeu || decode.io.lsuport.sb || decode.io.lsuport.sh   || decode.io.lsuport.sw || decode.io.aluport.add || decode.io.aluport.sub || decode.io.aluport.sll                      || decode.io.aluport.slt || decode.io.aluport.sltu || decode.io.aluport.xor || decode.io.aluport.srl                    || decode.io.aluport.srl || decode.io.aluport.sra ||decode.io.aluport.or || decode.io.aluport.and
    val rd_en = !(decode.io.aluport.beq || decode.io.aluport.bne || decode.io.aluport.blt || decode.io.aluport.bge   || decode.io.aluport.bltu || decode.io.aluport.bgeu || decode.io.lsuport.sb || decode.io.lsuport.sh   || decode.io.lsuport.sw )

    val rd_alu_d0  = Wire(new rd_entry())
    val rd_lsu_d0  = Wire(new rd_entry())
    val rs1_alu_d0  = Wire(new rd_entry())
    val rs2_alu_d0  = Wire(new rd_entry())
    rd_alu_d0.valid := rd_en && io.instr.valid
    rs1_alu_d0.valid := rs1_en && io.instr.valid
    rs2_alu_d0.valid := rs2_en && io.instr.valid
    rd_lsu_d0.valid := rd_en && (!io.instr.valid)
    rd_alu_d0.rd    := decode.io.aluport.rd
    rd_lsu_d0.rd    := decode.io.aluport.rd
    rs1_alu_d0.rd   := decode.io.aluport.rs1
    rs2_alu_d0.rd   := decode.io.aluport.rs2

    val rd_fifo_alu = Reg(Vec(2, (new rd_entry)))
    val rd_fifo_lsu = Reg(Vec(2, (new rd_entry)))
    val rs1_fifo_alu = Reg(Vec(2, (new rd_entry)))
    val rs2_fifo_alu = Reg(Vec(2, (new rd_entry)))

    rd_fifo_alu(0) := rd_alu_d0
    rd_fifo_alu(1) := rd_fifo_alu(0)
    rs1_fifo_alu(0) := rs1_alu_d0
    rs1_fifo_alu(1) := rs1_fifo_alu(0)

    rd_fifo_lsu(0)  := rd_lsu_d0
    rd_fifo_lsu(1)  := rd_fifo_lsu(0)
    rs2_fifo_alu(0) := rs2_alu_d0
    rs2_fifo_alu(1) := rs2_fifo_alu(0)

    val lsu_stall = Wire(Bool())
    val lsu_stall_d1 = RegNext(lsu_stall)
    lsu_stall := ((decode.io.lsuport.valid) && 
                        (( rs1_en && (decode.io.aluport.rs1 =/= 0.U)
                            && ((rd_fifo_alu(0).valid && (decode.io.aluport.rs1 === rd_fifo_alu(0).rd))
                            || (rd_fifo_alu(1).valid && (decode.io.aluport.rs1 === rd_fifo_alu(1).rd))
                            || (rd_fifo_lsu(0).valid && (decode.io.aluport.rs1 === rd_fifo_lsu(0).rd))
                            || (rd_fifo_lsu(1).valid && (decode.io.aluport.rs1 === rd_fifo_lsu(1).rd)))
                        )||( rs2_en && (decode.io.aluport.rs2 =/= 0.U)
                            && ((rd_fifo_alu(0).valid && (decode.io.aluport.rs2 === rd_fifo_alu(0).rd))
                            || (rd_fifo_alu(1).valid && (decode.io.aluport.rs2 === rd_fifo_alu(1).rd))
                            || (rd_fifo_lsu(0).valid && (decode.io.aluport.rs2 === rd_fifo_lsu(0).rd))
                            || (rd_fifo_lsu(1).valid && (decode.io.aluport.rs2 === rd_fifo_lsu(1).rd)))
                        ))
                    )

    val rd_wb   =  Wire(UInt(5.W))
    val rd_wb_en = Wire(Bool())
    val rd_wb_data  = Wire(UInt(32.W))

    io.rd_final.rd_wb := rd_wb
    io.rd_final.rd_wb_en := rd_wb_en
    io.rd_final.rd_wb_data := rd_wb_data

    when(io.alu_back.valid && io.alu_back.rd_en){
        rd_wb_en := 1.B
        rd_wb    := io.alu_back.rd
        rd_wb_data  := io.alu_back.result
    } .elsewhen(io.lsu_back.valid && io.lsu_back.rd_en){
        rd_wb_en := 1.B
        rd_wb    := io.lsu_back.rd
        rd_wb_data  := io.lsu_back.load_data
    } .otherwise {
        rd_wb_en := 0.B
        rd_wb    := 0.U
        rd_wb_data  := 0.U
    }

    // bypass logic
    val bypass_0 = Wire(new ByPassPort())
    bypass_0.rs1_en := 0.B
    bypass_0.rs1 := 0.U
    bypass_0.rs2_en := 0.B
    bypass_0.rs2 := 0.U
    io.bypass_d0o := bypass_0
    io.bypass_d1o := bypass_0
    when(rd_wb_en){
        when( (rd_wb =/= 0.U) && (rd_wb === rs1_fifo_alu(1).rd) && rs1_fifo_alu(1).valid){
            io.bypass_d1o.rs1_en := 1.B
            io.bypass_d1o.rs1    := rd_wb_data
        } 
        when( (rd_wb =/= 0.U) && (rd_wb === rs2_fifo_alu(1).rd) && rs2_fifo_alu(1).valid){
            io.bypass_d1o.rs2_en := 1.B
            io.bypass_d1o.rs2    := rd_wb_data
        } 
        when(  (rd_wb =/= 0.U) && (rd_wb ===  rd_fifo_alu(1).rd) && rd_fifo_alu(1).valid){

        } 
        
        when(  (rd_wb =/= 0.U) && (rd_wb === rs1_fifo_alu(0).rd) && rs1_fifo_alu(0).valid){
            io.bypass_d0o.rs1_en := 1.B
            io.bypass_d0o.rs1    := rd_wb_data
        } 
        when(  (rd_wb =/= 0.U) && (rd_wb === rs2_fifo_alu(0).rd) && rs2_fifo_alu(0).valid){
            io.bypass_d0o.rs2_en := 1.B
            io.bypass_d0o.rs2    := rd_wb_data
        }
    }

    io.stall_o := lsu_stall

    rf.io.rs1.rs_en := rs1_en
    rf.io.rs1.rs := decode.io.aluport.rs1

    rf.io.rs2.rs_en := rs2_en
    rf.io.rs2.rs := decode.io.aluport.rs2

    rf.io.rd1.rd_en := io.alu_back.rd_en && io.alu_back.valid
    rf.io.rd1.rd := io.alu_back.rd
    rf.io.rd1.rd_in := io.alu_back.result
    rf.io.rd2.rd_en := io.lsu_back.rd_en && io.lsu_back.valid
    rf.io.rd2.rd := io.lsu_back.rd
    rf.io.rd2.rd_in := io.lsu_back.load_data

    val rs1 = rf.io.rs1.rs_out
    val rs2 = rf.io.rs2.rs_out

    val instr_reg = RegNext(decode.io.instr)

    alu_reg.rs1 := rs1
    alu_reg.rs2 := rs2
    alu_reg.rd_en  := rd_en
    lsu_reg.rs1 := rs1
    lsu_reg.rs2 := rs2
    lsu_reg.rd  := decode.io.aluport.rd
    lsu_reg.rd_en := rd_en

    io.alu <> alu_reg
    io.lsu <> lsu_reg

    decode.io.instr <> io.instr

    when(io.flush_i.flush){
        decode.io.instr.valid := 0.B
    }
    when(lsu_stall){
        // decode.io.instr := instr_reg
        alu_reg.valid := 0.B
        lsu_reg.valid := 0.B
        rd_alu_d0.valid  :=  0.B
        rs1_alu_d0.valid := 0.B
        rs2_alu_d0.valid := 0.B
        rd_lsu_d0.valid  :=  0.B
    }
}

