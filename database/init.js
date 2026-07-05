use TaskFlow;

db.createCollection("Users", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Users",
            "properties": {
                "_id": {
                    "bsonType": "objectId"
                },
                "name": {
                    "bsonType": "string"
                },
                "email": {
                    "bsonType": "string"
                },
                "password": {
                    "bsonType": "string"
                },
                "mobilePhone": {
                    "bsonType": "string"
                }
            },
            "additionalProperties": false,
            "required": [
                "name",
                "email",
                "password",
                "mobilePhone"
            ]
        }
    },
    "validationLevel": "strict"
});

db.createCollection("Projects", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Projects",
            "properties": {
                "_id": {
                    "bsonType": "objectId"
                },
                "name": {
                    "bsonType": "string"
                },
                "description": {
                    "bsonType": "string"
                },
                "ownerId": {
                    "bsonType": "string"
                },
                "status": {
                    "bsonType": "string",
                    "enum": [
                        "ACTIVE",
                        "ARCHIVED"
                    ]
                },
                "members": {
                    "bsonType": "array",
                    "additionalItems": true,
                    "items": {
                        "bsonType": "object",
                        "properties": {
                            "userId": {
                                "bsonType": "string"
                            },
                            "role": {
                                "bsonType": "string",
                                "enum": [
                                    "OWNER",
                                    "ADMIN",
                                    "MEMBER"
                                ]
                            }
                        },
                        "additionalProperties": false,
                        "required": [
                            "userId",
                            "role"
                        ]
                    }
                }
            },
            "additionalProperties": false,
            "required": [
                "name",
                "description",
                "ownerId",
                "status",
                "members"
            ]
        }
    },
    "validationLevel": "strict",
    "validationAction": "error"
});

db.createCollection("Tasks", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Tasks",
            "properties": {
                "_id": {
                    "bsonType": "objectId"
                },
                "projectId": {
                    "bsonType": "string"
                },
                "title": {
                    "bsonType": "string"
                },
                "description": {
                    "bsonType": "string"
                },
                "createdBy": {
                    "bsonType": "string"
                },
                "assignedTo": {
                    "bsonType": "string"
                },
                "status": {
                    "bsonType": "string",
                    "enum": [
                        "TO_DO",
                        "IN_PROGRESS",
                        "BLOCKED",
                        "DONE"
                    ]
                },
                "priority": {
                    "bsonType": "string",
                    "enum": [
                        "LOW",
                        "MEDIUM",
                        "HIGH"
                    ]
                },
                "dueDate": {
                    "bsonType": "date"
                },
                "createdAt": {
                    "bsonType": "date"
                }
            },
            "additionalProperties": false,
            "required": [
                "projectId",
                "title",
                "description",
                "createdBy",
                "status",
                "priority",
                "dueDate",
                "createdAt"
            ]
        }
    },
    "validationLevel": "strict",
    "validationAction": "error"
});

db.createCollection("Comments", {
    "capped": false,
    "validator": {
        "$jsonSchema": {
            "bsonType": "object",
            "title": "Comments",
            "properties": {
                "_id": {
                    "bsonType": "objectId"
                },
                "taskId": {
                    "bsonType": "string"
                },
                "authorId": {
                    "bsonType": "string"
                },
                "content": {
                    "bsonType": "string"
                },
                "createdAt": {
                    "bsonType": "date"
                }
            },
            "additionalProperties": false,
            "required": [
                "taskId",
                "authorId",
                "content",
                "createdAt"
            ]
        }
    },
    "validationLevel": "strict",
    "validationAction": "error"
});
