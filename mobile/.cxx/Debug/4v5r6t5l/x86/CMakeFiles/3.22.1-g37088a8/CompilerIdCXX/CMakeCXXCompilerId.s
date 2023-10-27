	.text
	.file	"CMakeCXXCompilerId.cpp"
	.globl	main                            # -- Begin function main
	.p2align	4, 0x90
	.type	main,@function
main:                                   # @main
# %bb.0:
	pushl	%esi
	calll	.L0$pb
.L0$pb:
	popl	%eax
.Ltmp0:
	addl	$_GLOBAL_OFFSET_TABLE_+(.Ltmp0-.L0$pb), %eax
	movl	8(%esp), %ecx
	movl	info_compiler@GOTOFF(%eax), %edx
	movsbl	(%edx,%ecx), %edx
	movl	info_platform@GOTOFF(%eax), %esi
	movsbl	(%esi,%ecx), %esi
	addl	%edx, %esi
	movsbl	_ZL12info_version@GOTOFF(%eax,%ecx), %edx
	addl	%esi, %edx
	movl	info_language_standard_default@GOTOFF(%eax), %esi
	movsbl	(%esi,%ecx), %esi
	addl	%edx, %esi
	movl	info_language_extensions_default@GOTOFF(%eax), %eax
	movsbl	(%eax,%ecx), %eax
	addl	%esi, %eax
	popl	%esi
	retl
.Lfunc_end0:
	.size	main, .Lfunc_end0-main
                                        # -- End function
	.type	.L.str,@object                  # @.str
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"INFO:compiler[Clang]"
	.size	.L.str, 21

	.type	info_compiler,@object           # @info_compiler
	.data
	.globl	info_compiler
	.p2align	2
info_compiler:
	.long	.L.str
	.size	info_compiler, 4

	.type	.L.str.1,@object                # @.str.1
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.1:
	.asciz	"INFO:platform[Linux]"
	.size	.L.str.1, 21

	.type	info_platform,@object           # @info_platform
	.data
	.globl	info_platform
	.p2align	2
info_platform:
	.long	.L.str.1
	.size	info_platform, 4

	.type	.L.str.2,@object                # @.str.2
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.2:
	.asciz	"INFO:arch[]"
	.size	.L.str.2, 12

	.type	info_arch,@object               # @info_arch
	.data
	.globl	info_arch
	.p2align	2
info_arch:
	.long	.L.str.2
	.size	info_arch, 4

	.type	.L.str.3,@object                # @.str.3
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.3:
	.asciz	"INFO:standard_default[17]"
	.size	.L.str.3, 26

	.type	info_language_standard_default,@object # @info_language_standard_default
	.data
	.globl	info_language_standard_default
	.p2align	2
info_language_standard_default:
	.long	.L.str.3
	.size	info_language_standard_default, 4

	.type	.L.str.4,@object                # @.str.4
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str.4:
	.asciz	"INFO:extensions_default[OFF]"
	.size	.L.str.4, 29

	.type	info_language_extensions_default,@object # @info_language_extensions_default
	.data
	.globl	info_language_extensions_default
	.p2align	2
info_language_extensions_default:
	.long	.L.str.4
	.size	info_language_extensions_default, 4

	.type	_ZL12info_version,@object       # @_ZL12info_version
	.section	.rodata.str1.1,"aMS",@progbits,1
_ZL12info_version:
	.asciz	"INFO:compiler_version[00000012.00000000.00000008]"
	.size	_ZL12info_version, 50

	.ident	"Android (7714059, based on r416183c1) clang version 12.0.8 (https://android.googlesource.com/toolchain/llvm-project c935d99d7cf2016289302412d708641d52d2f7ee)"
	.section	".note.GNU-stack","",@progbits
