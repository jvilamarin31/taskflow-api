package com.taskflow.repositories;

import com.taskflow.models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IUserRepository extends MongoRepository<UserModel, String> {


}
