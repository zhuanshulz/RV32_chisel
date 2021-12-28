module BindsTo_0_EH1SimTop(
  input         clock,
  input         reset,
//   output        io_exe_result_valid,
//   output        io_exe_result_and,
//   output        io_exe_result_andi,
//   output        io_exe_result_or,
//   output        io_exe_result_ori,
//   output        io_exe_result_xor,
//   output        io_exe_result_xori,
//   output        io_exe_result_sll,
//   output        io_exe_result_slli,
//   output        io_exe_result_srl,
//   output        io_exe_result_srli,
//   output        io_exe_result_sra,
//   output        io_exe_result_srai,
//   output        io_exe_result_beq,
//   output        io_exe_result_bne,
//   output        io_exe_result_blt,
//   output        io_exe_result_bltu,
//   output        io_exe_result_bge,
//   output        io_exe_result_bgeu,
//   output        io_exe_result_add,
//   output        io_exe_result_addi,
//   output        io_exe_result_sub,
//   output        io_exe_result_slt,
//   output        io_exe_result_sltu,
//   output        io_exe_result_slti,
//   output        io_exe_result_sltiu,
//   output        io_exe_result_jal,
//   output        io_exe_result_jalr,
//   output        io_exe_result_auipc,
//   output        io_exe_result_lui,
//   output [11:0] io_exe_result_imm12_itype,
//   output [11:0] io_exe_result_imm12_btype,
//   output [19:0] io_exe_result_imm20_utype,
//   output [19:0] io_exe_result_imm20_jtype,
//   output [4:0]  io_exe_result_shamt,
//   output [31:0] io_exe_result_rs1,
//   output        io_exe_result_rs1_en,
//   output        io_exe_result_rs1_valid,
//   output [31:0] io_exe_result_rs2,
//   output        io_exe_result_rs2_en,
//   output        io_exe_result_rs2_valid,
//   output [4:0]  io_exe_result_rd,
//   output        io_exe_result_rd_en,
//   output [31:0] io_exe_result_pc,
//   output        io_exe_result_jump,
//   output [31:0] io_exe_result_jump_addr,
//   output        io_exe_result_finish,
//   output [31:0] io_exe_result_result,
//   output        io_exe_result_predict_jump,
//   output [31:0] io_exe_result_predict_target,
//   output        io_lsu_result_sw,
//   output        io_lsu_result_sh,
//   output        io_lsu_result_sb,
//   output        io_lsu_result_lhu,
//   output        io_lsu_result_lbu,
//   output        io_lsu_result_lw,
//   output        io_lsu_result_lh,
//   output        io_lsu_result_lb,
//   output [11:0] io_lsu_result_imm12_l,
//   output [11:0] io_lsu_result_imm12_s,
//   output [31:0] io_lsu_result_rs1,
//   output [31:0] io_lsu_result_rs2,
//   output [4:0]  io_lsu_result_rd,
//   output        io_lsu_result_rd_en,
//   output [31:0] io_lsu_result_load_data,
//   output [31:0] io_lsu_result_addr,
//   output        io_lsu_result_finish,
//   output        io_lsu_result_valid,
  output [4:0]  io_rd_final_rd_wb,
  output        io_rd_final_rd_wb_en,
  output [31:0] io_rd_final_rd_wb_data
);
int trace_file;

initial begin
    trace_file = $fopen("trace.txt","w");
end

always @(posedge clock) begin
    if(io_rd_final_rd_wb_en)begin
        if(io_rd_final_rd_wb != 0)
            $fdisplay(trace_file, "%x  %x", io_rd_final_rd_wb, io_rd_final_rd_wb_data);
    end
end

endmodule

bind EH1SimTop BindsTo_0_EH1SimTop BindsTo_0_EH1SimTop_Inst(.*);