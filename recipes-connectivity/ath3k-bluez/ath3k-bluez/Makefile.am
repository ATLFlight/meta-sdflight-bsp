bin_PROGRAMS = ath3k_hciattach

ath3k_hciattach_SOURCES = \
   lib/bluetooth.c \
   lib/hci.c \
   lib/sdp.c \
   lib/uuid.c \
   tools/hciattach_ath3k.c \
   tools/hciattach.c \
   tools/hciattach_intel.c \
   tools/hciattach_qualcomm.c \
   tools/hciattach_st.c \
   tools/hciattach_tialt.c \
   tools/hciattach_ti.c

ath3k_hciattach_CFLAGS = -static -g -Wall -Wundef
ath3k_hciattach_CFLAGS += -Wstrict-prototypes -Wno-trigraphs

ath3k_hciattach_CXXFLAGS = -fpermissive
ath3k_hciattach_CPPFLAGS = -I $(srcdir) -I $(srcdir)/lib/

# ath3k-hciattach_CPPFLAGS +=

ath3k_hciattach_LDFLAGS = -lrt -lpthread

