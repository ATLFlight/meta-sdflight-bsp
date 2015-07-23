bin_PROGRAMS = make_ext4fs
make_ext4fs_SOURCES = make_ext4fs_main.c \
	make_ext4fs.c \
	ext4fixup.c \
	ext4_utils.c \
	allocate.c \
	contents.c \
	extent.c \
	indirect.c \
	uuid.c \
	sha1.c \
	wipe.c \
	crc16.c
make_ext4fs_CFLAGS = -Iinclude -DHOST
make_ext4fs_LDADD = -lsparse -lz
