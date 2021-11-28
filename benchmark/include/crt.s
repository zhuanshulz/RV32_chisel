.file	"crt.s"

	.text
	
	.global _start
	
	.extern main

_start:
	li	sp, 0x0000fff0
	li x3, 0x7f030000
    addi x5, x0, 0x00
    sb x5, 0(x3)
	j _call_main

_finish:
    li x3, 0x7f030000
    addi x5, x0, 0xff
    sb x5, 0(x3)
    beq x0, x0, _finish

_call_main:
	call main
	j _finish


