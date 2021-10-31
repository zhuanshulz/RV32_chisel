.file	"crt.s"

	.text
	
	.global _start
	
	.extern main

_start:
	li	sp, 0x7f050000
	j _call_main

_finish:
    li x3, 0x7f030000
    addi x5, x0, 0xff
    sb x5, 0(x3)
	li a7, 93
	ecall
    beq x0, x0, _finish

_call_main:
	call main
	j _finish


