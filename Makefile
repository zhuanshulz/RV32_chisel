# 设置为你的工作路径
export RV_ROOT=/home/wxz/data/RV32_chisel

# Allow tool override
SWERV_CONFIG = ${RV_ROOT}/configs/swerv.config
IRUN = irun
VCS = vcs
VERILATOR = verilator
GCC_PREFIX = riscv32-unknown-elf
GCC_TOOL_PATH = /opt/riscv32i
GCC_VERSION =10.2.0
GCC_FLAGS = -march=rv32i -O3

TOP = TopMain
BUILD_DIR = ${RV_ROOT}/build
TOP_V = $(BUILD_DIR)/$(TOP).v
SCALA_FILE = $(shell find ./RV32I_chisel/src/main/scala -name '*.scala')
TEST_FILE = $(shell find ./RV32I_chisel/src/test/scala -name '*.scala')

SIM_TOP = EH1SimTop
CFLAGS += "-std=c++11 -DVL_DEBUG"
# Optimization for better performance; alternative is nothing for slower runtime (faster compiles)
# -O2 for faster runtime (slower compiles), or -O for balance.
VERILATOR_MAKE_FLAGS = OPT_FAST=""
# Define test name，must in TEST_LIST
ifeq ($(strip $(TEST)),)
	TEST = CoreMark
endif

TEST_LIST = HelloWorld Dhrystone Float CoreMark Bubble

ifeq ($(strip $(TEST_DIR)),)
	TEST_DIR = ${RV_ROOT}/benchmark
endif

src = $(wildcard $(TEST_DIR)/$(TEST)/*.c)
obj = $(patsubst %.c, %.o, $(src))

%.o: %.c
	$(GCC_TOOL_PATH)/bin/$(GCC_PREFIX)-gcc $(GCC_FLAGS) -I$(TEST_DIR)/include/ -I$(TEST_DIR)/$(TEST) -c -o $@ $<

$(TEST_DIR)/sim_hex/program_iccm_1.hex: $(TEST_DIR)/sim_hex/main.elf
	$(GCC_TOOL_PATH)/bin/$(GCC_PREFIX)-objdump -D $(TEST_DIR)/sim_hex/main.elf -M on-aliases,numeric > $(TEST_DIR)/sim_hex/main.dump
	$(GCC_TOOL_PATH)/bin/$(GCC_PREFIX)-objcopy -O verilog --only-section ".data*" --only-section ".rodata*" --only-section ".srodata*" $(TEST_DIR)/sim_hex/main.elf $(TEST_DIR)/sim_hex/data.hex
	$(GCC_TOOL_PATH)/bin/$(GCC_PREFIX)-objcopy -O verilog --only-section ".text*" --set-start=0x0 $(TEST_DIR)/sim_hex/main.elf $(TEST_DIR)/sim_hex/program.hex
	python $(TEST_DIR)/sim_hex/transform_data.py
	python $(TEST_DIR)/sim_hex/transform_program.py

$(TEST_DIR)/sim_hex/main.elf: $(obj)
	$(GCC_TOOL_PATH)/bin/$(GCC_PREFIX)-gcc $(GCC_FLAGS) -c $(TEST_DIR)/include/crt.s -o $(TEST_DIR)/sim_hex/crt.o
	$(GCC_TOOL_PATH)/bin/$(GCC_PREFIX)-ld -m elf32lriscv --discard-none -o $@ $(TEST_DIR)/sim_hex/crt.o $(obj) -L$(GCC_TOOL_PATH)/lib/gcc/$(GCC_PREFIX)/$(GCC_VERSION)/ \
		-L$(GCC_TOOL_PATH)/$(GCC_PREFIX)/lib/ -lm -lc -lgcc -lgcov -T$(TEST_DIR)/include/link.ld -static

build_benchmark: $(TEST_DIR)/sim_hex/program_iccm_1.hex

clean_benchmark: 
		rm -rf $(TEST_DIR)/$(TEST)/*.o $(TEST_DIR)/sim_hex/*.hex $(TEST_DIR)/sim_hex/*.dump $(TEST_DIR)/sim_hex/*.elf $(TEST_DIR)/sim_hex/*.o

$(TOP_V): $(SCALA_FILE) $(TEST_DIR)/sim_hex/program_iccm_1.hex
	mkdir -p $(@D)
	mill RV32I_chisel.runMain top.$(TOP) -td $(@D) --output-file $(@F)

clean: clean_benchmark build_benchmark
	rm -rf ./build

mill:$(TOP_V)

all:clean $(TOP_V) verilator-run 

pre_compiled:$(TOP_V)
	cp $(RV_ROOT)/benchmark/precompiled/$(TEST)/* $(RV_ROOT)/benchmark/sim_hex/
	$(VERILATOR) '-UASSERT_ON' --cc -CFLAGS ${CFLAGS} -I$(abspath $(BUILD_DIR))  -I${RV_ROOT}/debug $(BUILD_DIR)/*.v ${RV_ROOT}/debug/*.v\
		-Werror-PINMISSING -Werror-IMPLICIT -Wno-LITENDIAN -Wno-BLKANDNBLK -Wno-CMPCONST -Wno-CASEINCOMPLETE -Wno-UNOPTFLAT -Wno-WIDTH \
		--top-module $(SIM_TOP)  -exe test_tb.cpp --trace --autoflush --prof-cfuncs
	$(MAKE) -C obj_dir/ -f V$(SIM_TOP).mk $(VERILATOR_MAKE_FLAGS) CPP_FLAGS=-DVL_DEBUG
	./obj_dir/V$(SIM_TOP)

verilator-run:
	$(VERILATOR) '-UASSERT_ON' --cc -CFLAGS ${CFLAGS} -I$(abspath $(BUILD_DIR))  -I${RV_ROOT}/debug $(BUILD_DIR)/*.v ${RV_ROOT}/debug/*.v\
		-Werror-PINMISSING -Werror-IMPLICIT -Wno-LITENDIAN -Wno-BLKANDNBLK -Wno-CMPCONST -Wno-CASEINCOMPLETE -Wno-UNOPTFLAT -Wno-WIDTH \
		--top-module $(SIM_TOP)  -exe test_tb.cpp --trace --autoflush --prof-cfuncs
	$(MAKE) -C obj_dir/ -f V$(SIM_TOP).mk $(VERILATOR_MAKE_FLAGS) CPP_FLAGS=-DVL_DEBUG
	./obj_dir/V$(SIM_TOP)