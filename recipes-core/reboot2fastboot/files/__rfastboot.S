@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@ FILE: __rfastboot.S 
@ DESCRIPTION: Invoke linux reboot system call with custom params 
@
@ Copyright (c) 2013, Qualcomm Technologies Inc. All rights reserved.
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

#include <asm/unistd.h>

.text
.align 2
.global __rfastboot
.type __rfastboot #function 

__rfastboot:
	.fnstart	
    stmfd   sp!, {r4, r7}
    ldr     r7, =__NR_reboot           @ Linux reboot system call number
    svc     0x00                       @ Supervisor mode interrupt 
    ldmfd   sp!, {r4, r7}
    movs    r0, r0
    bxpl    lr
    b       __cb_reboot_failed         @ system call failuer callback
	.fnend
	.size __rfastboot,  .-__rfastboot
	
