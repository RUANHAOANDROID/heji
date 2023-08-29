package pkg

import "net"

func GetOutboundIP() (net.IP, error) {
	conn, err := net.Dial("udp", "114.114.114.114:80")
	defer conn.Close()
	localAddr := conn.LocalAddr().(*net.UDPAddr)
	return localAddr.IP, err
}
