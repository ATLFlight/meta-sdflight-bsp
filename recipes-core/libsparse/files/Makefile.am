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
libsparse_a_LDFLAGS = -lz