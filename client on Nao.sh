gst-launch-0.10 autoaudiosrc ! "audio/x-raw-int", channels=1, width=16, depth=16, signed=TRUE, rate=16000, endianness=1234 ! audioconvert ! vorbisenc ! oggmux max-delay=50 max-page-delay=50 ! tcpclientsink host=192.168.1.108 port=3000

# Ohne Naoqi
