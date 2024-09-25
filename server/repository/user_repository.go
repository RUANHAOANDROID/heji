package repository

import (
	"context"
	"errors"
	"go.mongodb.org/mongo-driver/bson"
	"heji-server/domain"
	"heji-server/mongo"
)

var user domain.User

// userRepository 结构体实现了 domain.UserRepository
type userRepository struct {
	database   mongo.Database
	collection string
}

// Register 注册一个
func (u *userRepository) Register(c context.Context, user *domain.User) error {
	result, err := u.GetByTel(c, user.Tel)
	if err == nil && result.Tel == user.Tel {
		return errors.New("用户已存在")
	}
	coll := u.database.Collection(u.collection)
	_, err = coll.InsertOne(c, user)
	if err != nil {
		return err
	}
	return nil
}

func (u *userRepository) GetByTel(c context.Context, tel string) (domain.User, error) {
	coll := u.database.Collection(u.collection)
	tags := user.TagsBson()
	filter := bson.M{tags.Tel: tel}
	var result domain.User
	err := coll.FindOne(c, filter).Decode(&result)
	return result, err
}

// NewUserRepository 初始化用户仓库
func NewUserRepository(db mongo.Database, collection string) domain.UserRepository {
	return &userRepository{
		database:   db,
		collection: collection,
	}
}
