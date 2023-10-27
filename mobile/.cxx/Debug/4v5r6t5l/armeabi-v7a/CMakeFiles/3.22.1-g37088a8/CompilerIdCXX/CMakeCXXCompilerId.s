	.text
	.syntax unified
	.eabi_attribute	67, "2.09"	@ Tag_conformance
	.eabi_attribute	6, 10	@ Tag_CPU_arch
	.eabi_attribute	7, 65	@ Tag_CPU_arch_profile
	.eabi_attribute	8, 1	@ Tag_ARM_ISA_use
	.eabi_attribute	9, 2	@ Tag_THUMB_ISA_use
	.fpu	neon
	.eabi_attribute	34, 1	@ Tag_CPU_unaligned_access
	.eabi_attribute	15, 1	@ Tag_ABI_PCS_RW_data
	.eabi_attribute	16, 1	@ Tag_ABI_PCS_RO_data
	.eabi_attribute	17, 2	@ Tag_ABI_PCS_GOT_use
	.eabi_attribute	20, 1	@ Tag_ABI_FP_denormal
	.eabi_attribute	21, 0	@ Tag_ABI_FP_exceptions
	.eabi_attribute	23, 3	@ Tag_ABI_FP_number_model
	.eabi_attribute	24, 1	@ Tag_ABI_align_needed
	.eabi_attribute	25, 1	@ Tag_ABI_align_preserved
	.eabi_attribute	38, 1	@ Tag_ABI_FP_16bit_format
	.eabi_attribute	18, 4	@ Tag_ABI_PCS_wchar_t
	.eabi_attribute	26, 2	@ Tag_ABI_enum_size
	.eabi_attribute	14, 0	@ Tag_ABI_PCS_R9_use
	.file	"CMakeCXXCompilerId.cpp"
	.globl	main                            @ -- Begin function main
	.p2align	2
	.type	main,%function
	.code	32                              @ @main
main:
	.fnstart
@ %bb.0:
	.save	{r4, r10, r11, lr}
	push	{r4, r10, r11, lr}
	.setfp	r11, sp, #8
	add	r11, sp, #8
	ldr	r1, .LCPI0_0
	ldr	r12, .LCPI0_1
.LPC0_0:
	add	r1, pc, r1
.LPC0_1:
	add	r12, pc, r12
	ldmib	r1, {r3, r4, lr}
	ldr	r1, .LCPI0_2
	ldrb	r2, [r12, r0]
.LPC0_2:
	ldr	r1, [pc, r1]
	ldrb	r3, [r3, r0]
	ldrb	r4, [r4, r0]
	ldrb	r1, [r1, r0]
	ldrb	r0, [lr, r0]
	add	r1, r3, r1
	add	r1, r1, r2
	add	r1, r1, r4
	add	r0, r1, r0
	pop	{r4, r10, r11, pc}
	.p2align	2
@ %bb.1:
.LCPI0_0:
	.long	.L_MergedGlobals-(.LPC0_0+8)
.LCPI0_1:
	.long	_ZL12info_version-(.LPC0_1+8)
.LCPI0_2:
	.long	.L_MergedGlobals-(.LPC0_2+8)
.Lfunc_end0:
	.size	main, .Lfunc_end0-main
	.cantunwind
	.fnend
                                        @ -- End function
	.type	.L.str,%object                  @ @.str
	.section	.rodata.str1.1,"aMS",%progbits,1
.L.str:
	.asciz	"INFO:compiler[Clang]"
	.size	.L.str, 21

	.type	.L.str.1,%object                @ @.str.1
.L.str.1:
	.asciz	"INFO:platform[Linux]"
	.size	.L.str.1, 21

	.type	.L.str.2,%object                @ @.str.2
.L.str.2:
	.asciz	"INFO:arch[]"
	.size	.L.str.2, 12

	.type	info_arch,%object               @ @info_arch
	.data
	.globl	info_arch
	.p2align	2
info_arch:
	.long	.L.str.2
	.size	info_arch, 4

	.type	.L.str.3,%object                @ @.str.3
	.section	.rodata.str1.1,"aMS",%progbits,1
.L.str.3:
	.asciz	"INFO:standard_default[17]"
	.size	.L.str.3, 26

	.type	.L.str.4,%object                @ @.str.4
.L.str.4:
	.asciz	"INFO:extensions_default[OFF]"
	.size	.L.str.4, 29

	.type	_ZL12info_version,%object       @ @_ZL12info_version
_ZL12info_version:
	.asciz	"INFO:compiler_version[00000012.00000000.00000008]"
	.size	_ZL12info_version, 50

	.type	.L_MergedGlobals,%object        @ @_MergedGlobals
	.data
	.p2align	2
.L_MergedGlobals:
	.long	.L.str
	.long	.L.str.1
	.long	.L.str.3
	.long	.L.str.4
	.size	.L_MergedGlobals, 16

	.globl	info_compiler
.set info_compiler, .L_MergedGlobals
	.size	info_compiler, 4
	.globl	info_platform
.set info_platform, .L_MergedGlobals+4
	.size	info_platform, 4
	.globl	info_language_standard_default
.set info_language_standard_default, .L_MergedGlobals+8
	.size	info_language_standard_default, 4
	.globl	info_language_extensions_default
.set info_language_extensions_default, .L_MergedGlobals+12
	.size	info_language_extensions_default, 4
	.ident	"Android (7714059, based on r416183c1) clang version 12.0.8 (https://android.googlesource.com/toolchain/llvm-project c935d99d7cf2016289302412d708641d52d2f7ee)"
	.section	".note.GNU-stack","",%progbits
