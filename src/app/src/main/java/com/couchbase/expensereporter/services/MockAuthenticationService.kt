package com.couchbase.expensereporter.services

import com.couchbase.expensereporter.models.User

class MockAuthenticationService
    : AuthenticationService {
    private var _user: User? = null
    private var _mockUsers = HashMap<String, User>()

    override fun getCurrentUser(): User {
        return _user?: User("", "", "")
    }

    override fun authenticatedUser(username: String, password: String): Boolean {
        return if (_mockUsers.containsKey(username)){
            val user = _mockUsers[username]
            if (user?.password == password){
                _user = user
                true
            } else {
                false
            }
        } else {
            _user = User(username = username, password = password, department = "Engineering")
            return true
        }
    }

    override fun logout() {
        _user = null
    }

    init {
        //create mock users for testing the application
        //in a real app this would be provided by some kind of OAuth2 Service, etc
        _mockUsers["demo@example.com"] = User("demo@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo1@example.com"] = User("demo1@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo2@example.com"] = User("demo2@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo3@example.com"] = User("demo3@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo4@example.com"] = User("demo4@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo5@example.com"] = User("demo5@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo6@example.com"] = User("demo6@example.com", "P@ssw0rd12", "Engineering")
        _mockUsers["demo7@example.com"] = User("demo7@example.com", "P@ssw0rd12", "Human Resources")
        _mockUsers["demo8@example.com"] = User("demo8@example.com", "P@ssw0rd12", "Human Resources")
        _mockUsers["demo9@example.com"] = User("demo9@example.com", "P@ssw0rd12", "Human Resources")
        _mockUsers["demo10@example.com"] = User("demo10@example.com", "P@ssw0rd12", "Sales")
        _mockUsers["demo11@example.com"] = User("demo11@example.com", "P@ssw0rd12", "Sales")
        _mockUsers["demo12@example.com"] = User("demo12@example.com", "P@ssw0rd12", "Sales")
        _mockUsers["demo13@example.com"] = User("demo13@example.com", "P@ssw0rd12", "Sales")
        _mockUsers["demo14@example.com"] = User("demo14@example.com", "P@ssw0rd12", "Sales")
        _mockUsers["demo15@example.com"] = User("demo15@example.com", "P@ssw0rd12", "Sales")
    }
}