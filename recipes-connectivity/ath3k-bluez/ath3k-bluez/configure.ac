AC_PREREQ(2.68)

AC_INIT([ath3k-bluez],[1.0.0])

AC_CONFIG_SRCDIR([tools/hciattach.c])
AC_CONFIG_HEADER([config.h])
AM_INIT_AUTOMAKE

# Checks for programs.
AC_PROG_CC
AC_PROG_CXX
AC_PROG_LIBTOOL

# Checks for libraries
#AC_CHECK_LIB([pthread], [pthread_create])
#AC_CHECK_LIB([rt], [rt_create], [], {
#   echo "Error! You need to have rt lib."
#   exit -1
#   ])

AC_ARG_WITH([common_includes],
      AC_HELP_STRING([--with-common-includes=@<:@dir@:>@],
         [Specify the location of the common headers]),
      [common_incdir=$withval],
      with_common_includes=no)

if test "x$with_common_includes" != "xno"; then
   CFLAGS="${CFLAGS} -I${common_incdir}"
fi

AC_CONFIG_FILES([Makefile])
AC_OUTPUT
