
OUTPUT_ARCH( "riscv" )
ENTRY(_start)

SECTIONS
{
    . = 0x00040000;
  .data .  : { *(.*data) *(.rodata*) *(.srodata*) }
  .bss : { *(.bss) }

  . = 0x0;
  .text .  : { *(.text) }
 _end = .;
}
