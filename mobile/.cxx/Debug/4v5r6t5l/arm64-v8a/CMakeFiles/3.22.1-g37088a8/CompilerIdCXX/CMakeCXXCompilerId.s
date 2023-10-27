	.text
	.file	"CMakeCXXCompilerId.cpp"
	.globl	main                            // -- Begin function main
	.p2align	2
	.type	main,@function
main:                                   // @main
	.cfi_startproc
// %bb.0:
	adrp	x8, info_compiler
	adrp	x10, info_platform
	ldr	x8, [x8, :lo12:info_compiler]
	ldr	x10, [x10, :lo12:info_platform]
	adrp	x11, info_language_standard_default
	adrp	x12, info_language_extensions_default
	ldr	x11, [x11, :lo12:info_language_standard_default]
                                        // kill: def $w0 killed $w0 def $x0
	sxtw	x9, w0
	ldr	x12, [x12, :lo12:info_language_extensions_default]
	adrp	x13, _ZL12info_version
	ldrb	w8, [x8, x9]
	ldrb	w10, [x10, x9]
	add	x13, x13, :lo12:_ZL12info_version
	ldrb	w13, [x13, x9]
	ldrb	w11, [x11, x9]
	ldrb	w9, [x12, x9]
	add	w8, w10, w8
	add	w8, w8, w13
	add	w8, w8, w11
	add	w0, w8, w9
	ret
.Lfunc_end0:
	.size	main, .Lfunc_end0-main
	.cfi_endproc
                                        // -- End function
	.type	.L.str,@object                  // @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"INFO:compiler[Clang]"
	.size	.L.str, 21

	.type	info_compiler,@object           // @info_compiler
	.data
	.globl	info_compiler
	.p2align	3
info_compiler:
	.xword	.L.str
	.size	info_compiler, 8

	.type	.L.str.1,@object                // @.str.1
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.1:
	.asciz	"INFO:platform[Linux]"
	.size	.L.str.1, 21

	.type	info_platform,@object           // @info_platform
	.data
	.globl	info_platform
	.p2align	3
info_platform:
	.xword	.L.str.1
	.size	info_platform, 8

	.type	.L.str.2,@object                // @.str.2
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.2:
	.asciz	"INFO:arch[]"
	.size	.L.str.2, 12

	.type	info_arch,@object               // @info_arch
	.data
	.globl	info_arch
	.p2align	3
info_arch:
	.xword	.L.str.2
	.size	info_arch, 8

	.type	.L.str.3,@object                // @.str.3
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.3:
	.asciz	"INFO:standard_default[17]"
	.size	.L.str.3, 26

	.type	info_language_standard_default,@object // @info_language_standard_default
	.data
	.globl	info_language_standard_default
	.p2align	3
info_language_standard_default:
	.xword	.L.str.3
	.size	info_language_standard_default, 8

	.type	.L.str.4,@object                // @.str.4
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.4:
	.asciz	"INFO:extensions_default[OFF]"
	.size	.L.str.4, 29

	.type	info_language_extensions_default,@object // @info_language_extensions_default
	.data
	.globl	info_language_extensions_default
	.p2align	3
info_language_extensions_default:
	.xword	.L.str.4
	.size	info_language_extensions_default, 8

	.type	_ZL12info_version,@object       // @_ZL12info_version
	.section	.rodata.str1.1,"aMS",@progbits,1
_ZL12info_version:
	.asciz	"INFO:compiler_version[00000012.00000000.00000008]"
	.size	_ZL12info_version, 50

	.ident	"Android (7714059, based on r416183c1) clang version 12.0.8 (https://android.googlesource.com/toolchain/llvm-project c935d99d7cf2016289302412d708641d52d2f7ee)"
	.section	".note.GNU-stack","",@progbits
