// Code generated by gtag. DO NOT EDIT.
// See: https://github.com/wolfogre/gtag

//go:generate go run github.com/wolfogre/gtag/cmd/gtag -types Category -tags bson .
package domain

import (
	"reflect"
	"strings"
)

var (
	valueOfCategory = Category{}
	typeOfCategory  = reflect.TypeOf(valueOfCategory)

	_                    = valueOfCategory.ID
	fieldOfCategoryID, _ = typeOfCategory.FieldByName("ID")
	tagOfCategoryID      = fieldOfCategoryID.Tag

	_                        = valueOfCategory.BookID
	fieldOfCategoryBookID, _ = typeOfCategory.FieldByName("BookID")
	tagOfCategoryBookID      = fieldOfCategoryBookID.Tag

	_                      = valueOfCategory.Type
	fieldOfCategoryType, _ = typeOfCategory.FieldByName("Type")
	tagOfCategoryType      = fieldOfCategoryType.Tag

	_                       = valueOfCategory.Level
	fieldOfCategoryLevel, _ = typeOfCategory.FieldByName("Level")
	tagOfCategoryLevel      = fieldOfCategoryLevel.Tag
)

// CategoryTags indicate tags of type Category
type CategoryTags struct {
	ID     string // `bson:"_id"`
	BookID string // `bson:"book_id"`
	Type   string // `bson:"type"`
	Level  string // `bson:"level"`
}

// Values return all tags of Category as slice
func (t *CategoryTags) Values() []string {
	return []string{
		t.ID,
		t.BookID,
		t.Type,
		t.Level,
	}
}

// Tags return specified tags of Category
func (*Category) Tags(tag string, convert ...func(string) string) CategoryTags {
	conv := func(in string) string { return strings.TrimSpace(strings.Split(in, ",")[0]) }
	if len(convert) > 0 {
		conv = convert[0]
	}
	if conv == nil {
		conv = func(in string) string { return in }
	}
	return CategoryTags{
		ID:     conv(tagOfCategoryID.Get(tag)),
		BookID: conv(tagOfCategoryBookID.Get(tag)),
		Type:   conv(tagOfCategoryType.Get(tag)),
		Level:  conv(tagOfCategoryLevel.Get(tag)),
	}
}

// TagsBson is alias of Tags("bson")
func (*Category) TagsBson() CategoryTags {
	var v *Category
	return v.Tags("bson")
}