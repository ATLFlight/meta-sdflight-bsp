lib_LIBRARIES = libsparse.a
sparseincdir = $(includedir)/sparse
sparseinc_HEADERS = include/sparse/sparse.h
libsparse_a_SOURCES = backed_block.c \
	output_file.c \
	sparse.c \
	sparse_crc32.c \
	sparse_err.c \
	sparse_read.c
libsparse_a_CFLAGS = -Iinclude

bin_PROGRAMS = simg2img

simg2img_SOURCES = simg2img.c \
	sparse_crc32.c
simg2img_CFLAGS = -Iinclude
simg2img_LDADD = libsparse.a -lz
