// SPDX-License-Identifier: Apache-2.0
// Copyright 2019 Western Digital Corporation or its affiliates.
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
// http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
#include <stdlib.h>
#include <iostream>
#include <utility>
#include <string>
#include "VEH1SimTop.h"
#include "verilated_vcd_c.h"


// /*
vluint64_t main_time = 0;

double sc_time_stamp () { 
 return main_time;
}
// */

//int main(int argc, char* argv[]) {
int main(int argc, char** argv) {

  std::cout << "\nStart of sim\n" << std::endl;
  Verilated::commandArgs(argc, argv);

  VEH1SimTop* tb = new VEH1SimTop;
  uint32_t clkCnt = 0;

  // init trace dump
  Verilated::traceEverOn(true);
  VerilatedVcdC* tfp = new VerilatedVcdC;
  tb->trace (tfp, 24);
  tfp->open ("sim.vcd");
  
  // Simulate
  for(auto i=0; (!Verilated::gotFinish()); ++i){
    clkCnt++;
    if(i<10)  {
       tb->reset  = 1;
    } else {
       tb->reset  = 0;
    }

    for (auto clk=0; clk<2; clk++) {
      tfp->dump (2*i+clk);
      tb->clock = !tb->clock;
      tb->eval();
    }

    // if(tb->io_rd_final_rd_wb_en){
    //     std::cout << tb->io_rd_final_rd_wb_en << std::endl;
    //     printf("rd_wb_data : %h \n", uint32_t(tb->io_rd_final_rd_wb_data));
    // }


    if (Verilated::gotFinish()) {
    // if (tb->finished) {
      tfp->close();
      break;
    }

    // if(clkCnt == 10000){
    //   tfp->close();
    //   break;
    // }

  }

  // for(auto i=0; i<100; ++i){
  //   clkCnt++;
  //   for (auto clk=0; clk<2; clk++) {
  //     tfp->dump (2*i+clk);
  //     tb->core_clk = !tb->core_clk;
  //     tb->eval();
  //   }
  // }
 
  std::cout << "\nEnd of sim" << std::endl;
  exit(EXIT_SUCCESS);

}
