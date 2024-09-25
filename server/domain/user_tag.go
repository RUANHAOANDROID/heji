// Code generated by gtag. DO NOT EDIT.
// See: https://github.com/wolfogre/gtag

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types User -tags bson .
package domain

import (
	"reflect"
	"strings"
)

var (
	valueOfUser = User{}
	typeOfUser  = reflect.TypeOf(valueOfUser)

	_                = valueOfUser.ID
	fieldOfUserID, _ = typeOfUser.FieldByName("ID")
	tagOfUserID      = fieldOfUserID.Tag

	_                  = valueOfUser.Name
	fieldOfUserName, _ = typeOfUser.FieldByName("Name")
	tagOfUserName      = fieldOfUserName.Tag

	_                 = valueOfUser.Tel
	fieldOfUserTel, _ = typeOfUser.FieldByName("Tel")
	tagOfUserTel      = fieldOfUserTel.Tag

	_                      = valueOfUser.Password
	fieldOfUserPassword, _ = typeOfUser.FieldByName("Password")
	tagOfUserPassword      = fieldOfUserPassword.Tag

	_                      = valueOfUser.ImageUrl
	fieldOfUserImageUrl, _ = typeOfUser.FieldByName("ImageUrl")
	tagOfUserImageUrl      = fieldOfUserImageUrl.Tag
)

// UserTags indicate tags of type User
type UserTags struct {
	ID       string // `bson:"_id,omitempty" json:"id"`
	Name     string // `bson:"name" json:"name"`
	Tel      string // `bson:"tel" json:"tel"`
	Password string // `bson:"password" json:"password"`
	ImageUrl string // `bson:"image_url" json:"image_url"`
}

// Values return all tags of User as slice
func (t *UserTags) Values() []string {
	return []string{
		t.ID,
		t.Name,
		t.Tel,
		t.Password,
		t.ImageUrl,
	}
}

// Tags return specified tags of User
func (*User) Tags(tag string, convert ...func(string) string) UserTags {
	conv := func(in string) string { return strings.TrimSpace(strings.Split(in, ",")[0]) }
	if len(convert) > 0 {
		conv = convert[0]
	}
	if conv == nil {
		conv = func(in string) string { return in }
	}
	return UserTags{
		ID:       conv(tagOfUserID.Get(tag)),
		Name:     conv(tagOfUserName.Get(tag)),
		Tel:      conv(tagOfUserTel.Get(tag)),
		Password: conv(tagOfUserPassword.Get(tag)),
		ImageUrl: conv(tagOfUserImageUrl.Get(tag)),
	}
}

// TagsBson is alias of Tags("bson")
func (*User) TagsBson() UserTags {
	var v *User
	return v.Tags("bson")
}
