	.section	__TEXT,__text,regular,pure_instructions
	.build_version macos, 13, 0
	.globl	_main                           ; -- Begin function main
	.p2align	2
_main:                                  ; @main
	.cfi_startproc
; %bb.0:
                                        ; kill: def $w0 killed $w0 def $x0
Lloh0:
	adrp	x8, _info_compiler@PAGE
Lloh1:
	ldr	x8, [x8, _info_compiler@PAGEOFF]
	sxtw	x9, w0
	ldrsb	w8, [x8, x9]
Lloh2:
	adrp	x10, _info_platform@PAGE
Lloh3:
	ldr	x10, [x10, _info_platform@PAGEOFF]
	ldrsb	w10, [x10, x9]
Lloh4:
	adrp	x11, _info_arch@PAGE
Lloh5:
	ldr	x11, [x11, _info_arch@PAGEOFF]
	ldrsb	w11, [x11, x9]
	add	w8, w10, w8
	add	w8, w8, w11
Lloh6:
	adrp	x10, __ZL12info_version@PAGE
Lloh7:
	add	x10, x10, __ZL12info_version@PAGEOFF
	ldrsb	w10, [x10, x9]
Lloh8:
	adrp	x11, _info_language_standard_default@PAGE
Lloh9:
	ldr	x11, [x11, _info_language_standard_default@PAGEOFF]
	ldrsb	w11, [x11, x9]
	add	w8, w8, w10
	add	w8, w8, w11
Lloh10:
	adrp	x10, _info_language_extensions_default@PAGE
Lloh11:
	ldr	x10, [x10, _info_language_extensions_default@PAGEOFF]
	ldrsb	w9, [x10, x9]
	add	w0, w8, w9
	ret
	.loh AdrpLdr	Lloh10, Lloh11
	.loh AdrpLdr	Lloh8, Lloh9
	.loh AdrpAdd	Lloh6, Lloh7
	.loh AdrpLdr	Lloh4, Lloh5
	.loh AdrpLdr	Lloh2, Lloh3
	.loh AdrpLdr	Lloh0, Lloh1
	.cfi_endproc
                                        ; -- End function
	.section	__TEXT,__cstring,cstring_literals
l_.str:                                 ; @.str
	.asciz	"INFO:compiler[AppleClang]"

	.section	__DATA,__data
	.globl	_info_compiler                  ; @info_compiler
	.p2align	3
_info_compiler:
	.quad	l_.str

	.section	__TEXT,__cstring,cstring_literals
l_.str.1:                               ; @.str.1
	.asciz	"INFO:platform[Darwin]"

	.section	__DATA,__data
	.globl	_info_platform                  ; @info_platform
	.p2align	3
_info_platform:
	.quad	l_.str.1

	.section	__TEXT,__cstring,cstring_literals
l_.str.2:                               ; @.str.2
	.asciz	"INFO:arch[]"

	.section	__DATA,__data
	.globl	_info_arch                      ; @info_arch
	.p2align	3
_info_arch:
	.quad	l_.str.2

	.section	__TEXT,__cstring,cstring_literals
l_.str.3:                               ; @.str.3
	.asciz	"INFO:standard_default[98]"

	.section	__DATA,__data
	.globl	_info_language_standard_default ; @info_language_standard_default
	.p2align	3
_info_language_standard_default:
	.quad	l_.str.3

	.section	__TEXT,__cstring,cstring_literals
l_.str.4:                               ; @.str.4
	.asciz	"INFO:extensions_default[ON]"

	.section	__DATA,__data
	.globl	_info_language_extensions_default ; @info_language_extensions_default
	.p2align	3
_info_language_extensions_default:
	.quad	l_.str.4

	.section	__TEXT,__cstring,cstring_literals
__ZL12info_version:                     ; @_ZL12info_version
	.asciz	"INFO:compiler_version[00000014.00000000.00000003.14030022]"

.subsections_via_symbols
