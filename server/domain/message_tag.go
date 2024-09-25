// Code generated by gtag. DO NOT EDIT.
// See: https://github.com/wolfogre/gtag

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types Message -tags bson .
package domain

import (
	"reflect"
	"strings"
)

var (
	valueOfMessage = Message{}
	typeOfMessage  = reflect.TypeOf(valueOfMessage)

	_                   = valueOfMessage.ID
	fieldOfMessageID, _ = typeOfMessage.FieldByName("ID")
	tagOfMessageID      = fieldOfMessageID.Tag

	_                     = valueOfMessage.Type
	fieldOfMessageType, _ = typeOfMessage.FieldByName("Type")
	tagOfMessageType      = fieldOfMessageType.Tag

	_                          = valueOfMessage.Timestamp
	fieldOfMessageTimestamp, _ = typeOfMessage.FieldByName("Timestamp")
	tagOfMessageTimestamp      = fieldOfMessageTimestamp.Tag

	_                         = valueOfMessage.SenderId
	fieldOfMessageSenderId, _ = typeOfMessage.FieldByName("SenderId")
	tagOfMessageSenderId      = fieldOfMessageSenderId.Tag

	_                            = valueOfMessage.ReceiverIds
	fieldOfMessageReceiverIds, _ = typeOfMessage.FieldByName("ReceiverIds")
	tagOfMessageReceiverIds      = fieldOfMessageReceiverIds.Tag

	_                        = valueOfMessage.Content
	fieldOfMessageContent, _ = typeOfMessage.FieldByName("Content")
	tagOfMessageContent      = fieldOfMessageContent.Tag
)

// MessageTags indicate tags of type Message
type MessageTags struct {
	ID          string // `bson:"_id,omitempty" json:"_id"`
	Type        string // `bson:"type" json:"type"`
	Timestamp   string // `bson:"timestamp" json:"timestamp"`
	SenderId    string // `bson:"sender_id" json:"sender_id"`
	ReceiverIds string // `bson:"receiver_ids" json:"receiver_ids"`
	Content     string // `bson:"content" json:"content"`
}

// Values return all tags of Message as slice
func (t *MessageTags) Values() []string {
	return []string{
		t.ID,
		t.Type,
		t.Timestamp,
		t.SenderId,
		t.ReceiverIds,
		t.Content,
	}
}

// Tags return specified tags of Message
func (*Message) Tags(tag string, convert ...func(string) string) MessageTags {
	conv := func(in string) string { return strings.TrimSpace(strings.Split(in, ",")[0]) }
	if len(convert) > 0 {
		conv = convert[0]
	}
	if conv == nil {
		conv = func(in string) string { return in }
	}
	return MessageTags{
		ID:          conv(tagOfMessageID.Get(tag)),
		Type:        conv(tagOfMessageType.Get(tag)),
		Timestamp:   conv(tagOfMessageTimestamp.Get(tag)),
		SenderId:    conv(tagOfMessageSenderId.Get(tag)),
		ReceiverIds: conv(tagOfMessageReceiverIds.Get(tag)),
		Content:     conv(tagOfMessageContent.Get(tag)),
	}
}

// TagsBson is alias of Tags("bson")
func (*Message) TagsBson() MessageTags {
	var v *Message
	return v.Tags("bson")
}