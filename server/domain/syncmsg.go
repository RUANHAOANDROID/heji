package domain

const (
	CollSyncMessages = "sync_messages"
)

// SyncMessages 同步消息
type SyncMessages struct {
	ID   string
	Type int
}
