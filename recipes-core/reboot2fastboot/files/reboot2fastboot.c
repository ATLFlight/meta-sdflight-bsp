/*****************************************************************************
* FILE: reboot2fastboot.c
* DESCRIPTION: Reboot the device in bootloader/fastboot mode
*
* Copyright (c) 2013, Qualcomm Technologies Inc. All rights reserved.
*****************************************************************************/
#include <linux/reboot.h>
#include <stdio.h>
#include <errno.h>

/*****************************************************************************
* Reboot device and wait for fastboot processing
* Invokes reboot systemcall with parameter "bootloader"
*****************************************************************************/
int main(void)
{
	int ret = -1;
	printf("Rebooting to fastboot mode..\n");
	sleep(1);
	ret = __rfastboot(LINUX_REBOOT_MAGIC1, LINUX_REBOOT_MAGIC2,
               			LINUX_REBOOT_CMD_RESTART2, "bootloader");

	if(ret)
		printf("ERROR: Reboot failed errno(%d)\n", errno);

	return ret;
}

/*****************************************************************************
* Callback routine if reboot fails.
* Set errno
*****************************************************************************/
int __cb_reboot_failed(int n)
{
    errno = n;
    return -1;
}

