package com.fastcode.example.domain.extended.authorization.users;

import com.fastcode.example.domain.core.authorization.users.IUsersRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

@JaversSpringDataAuditable
@Repository("usersRepositoryExtended")
public interface IUsersRepositoryExtended extends IUsersRepository {
    //Add your custom code here
}
